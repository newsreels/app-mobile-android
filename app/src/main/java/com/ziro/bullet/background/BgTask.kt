package com.ziro.bullet.background

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.sink.DataSink
import com.otaliastudios.transcoder.sink.DefaultDataSink
import com.otaliastudios.transcoder.source.ClipDataSource
import com.otaliastudios.transcoder.source.FilePathDataSource
import com.otaliastudios.transcoder.source.UriDataSource
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.APIResources.ProgressRequestBody
import com.ziro.bullet.BuildConfig
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.analytics.AnalyticsEvents.logEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.data.PostArticleParams
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.SingleArticle
import com.ziro.bullet.mediapicker.config.PictureMimeType
import com.ziro.bullet.mediapicker.gallery.process.MyExactResizer
import com.ziro.bullet.mediapicker.utils.DateUtils
import com.ziro.bullet.utills.Constants.ARTICLE_DRAFT
import com.ziro.bullet.utills.Constants.ARTICLE_PUBLISHED
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


class BgTask : Runnable, TranscoderListener, ProgressRequestBody.UploadCallbacks {
    companion object {
        private val TAG = "BgTask"
    }

    protected lateinit var context: Context
    private var mTranscodeFuture: Future<Void>? = null

    var shouldContinue = true
    lateinit var uploadInfo: UploadInfo
    lateinit var broadcastEmitter: BroadcastEmitter
    lateinit var dbHandler: DbHandler

    fun init(
        context: Context,
        info: UploadInfo,
        broadcastEmitter: BroadcastEmitter,
        dbHandler: DbHandler
    ): BgTask {
        this.context = context
        this.uploadInfo = info
        this.broadcastEmitter = broadcastEmitter
        this.dbHandler = dbHandler
        return this
    }

    fun cancel() {
        shouldContinue = false
    }

    fun setId(id: String) {
        uploadInfo.id = id
    }

    override fun run() {
//        uploadInfo = dbHandler.task
        Log.d(TAG, "run: start status====== ${uploadInfo.videoStatus}")
//        while (shouldContinue) {
        when (uploadInfo.videoStatus) {
            VideoStatus.PROCESSING -> processVideo()
            VideoStatus.UPLOADING -> uploadVideo()
            VideoStatus.UPLOAD_DONE -> {
                val taskReadyToPublish = dbHandler.isTaskReadyToPublish(uploadInfo.id)
                if (taskReadyToPublish)
                    publishVideo(uploadInfo.key)
                else
                    broadcastEmitter.stopService(uploadInfo.id)
            }
            else -> {
                broadcastEmitter.stopService(uploadInfo.id)
            }
        }
    }

    private fun processVideo() {
        Log.d(TAG, "processVideo: prcessvideo ")
        val transcodeOutputFile: File
        transcodeOutputFile = try {
            val outputDir =
                File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "TrimVideos")
            outputDir.mkdir()
            File.createTempFile(DateUtils.getCreateFileName("trim_"), ".mp4", outputDir)
        } catch (e: IOException) {
            return
        }

        val videoStrategy = DefaultVideoStrategy.Builder()
            .addResizer(
                MyExactResizer(
                    uploadInfo.videoInfo.width,
                    uploadInfo.videoInfo.height
                )
            ) //16: 9
            .build()

        val sink: DataSink = DefaultDataSink(transcodeOutputFile.getAbsolutePath())
        val builder = Transcoder.into(sink)
        if (PictureMimeType.isContent(uploadInfo.videoInfo.path)) {
            builder.addDataSource(
                ClipDataSource(
                    UriDataSource(context, Uri.parse(uploadInfo.videoInfo.path)),
                    uploadInfo.videoInfo.startTime,
                    uploadInfo.videoInfo.endTime
                )
            )
        } else {
            builder.addDataSource(
                ClipDataSource(
                    FilePathDataSource(uploadInfo.videoInfo.path),
                    uploadInfo.videoInfo.startTime,
                    uploadInfo.videoInfo.endTime
                )
            )
        }

        uploadInfo.videoInfo.path = transcodeOutputFile.getAbsolutePath()

        mTranscodeFuture = builder.setListener(this)
            .setVideoTrackStrategy(videoStrategy)
            .transcode()

        shouldContinue = false
        Log.d(TAG, "upload: task running")
    }

    override fun onTranscodeProgress(progress: Double) {
        broadcastEmitter.onStart(uploadInfo.id)
//        Log.d(TAG, "onTranscodeProgress: progress = " + progress)
    }

    override fun onTranscodeCompleted(successCode: Int) {
        Log.d(TAG, "onTranscodeCompleted: ")
        broadcastEmitter.onProcessingCompleted(uploadInfo.id)
        uploadVideo()
    }

    override fun onTranscodeCanceled() {
        Log.d(TAG, "onTranscodeCanceled: ")
        broadcastEmitter.onError(uploadInfo.id, VideoStatus.TRANSCODING_ERROR)
    }

    override fun onTranscodeFailed(exception: Throwable) {
        Log.d(TAG, "onTranscodeFailed: ")
        broadcastEmitter.onError(uploadInfo.id, VideoStatus.TRANSCODING_ERROR)
    }

    private fun uploadVideo() {
        dbHandler.setTaskStatus(uploadInfo.id, VideoStatus.UPLOADING)
        Log.d(TAG, "uploadVideo start: ")

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "video",
                File(uploadInfo.videoInfo.path).getName(),
                ProgressRequestBody(File(uploadInfo.videoInfo.path), "video/mp4", this)
            )
            .build()


        val request = Request.Builder()
            .url(BuildConfig.BASE_URL + "media/videos")
            .header("Authorization", "Bearer " + PrefConfig(context).getAccessToken())
            .post(body)
            .build()

        val builder = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val request = chain.request().newBuilder()
                        .header("x-app-platform", "android")
                        .header("x-app-version", BuildConfig.VERSION_CODE.toString())
                        .header("api-version", BuildConfig.API_VERSION)
                        .build()
                    return chain.proceed(request)
                }
            })


//        if (BuildConfig.DEBUG) {
//            val interceptor = HttpLoggingInterceptor()
//            interceptor.level = HttpLoggingInterceptor.Level.BODY
//            builder.addInterceptor(interceptor)
//        }

        val client = builder.build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, error: IOException) {
                Log.d(TAG, "uploadVideo error: localizedMessage " + error.localizedMessage)
                broadcastEmitter.onError(uploadInfo.id, VideoStatus.INTERNET_ERROR)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                Log.d(TAG, "uploadVideo success: ")
                if (response.isSuccessful) {
                    try {
                        val jsonObject = JSONObject(response.body!!.string())
                        val key = jsonObject.getString("key")
                        dbHandler.addTaskKey(uploadInfo.id, key)
                        val taskReadyToPublish = dbHandler.isTaskReadyToPublish(uploadInfo.id)
                        broadcastEmitter.onUploadCompleted(uploadInfo.id)
                        if (taskReadyToPublish) {
                            dbHandler.setTaskStatus(uploadInfo.id, VideoStatus.UPLOAD_DONE)
                            publishVideo(key)
                        } else {
                            draftVideo(key)
                        }
                        response.close()
                    } catch (exce: Exception) {
                        Log.d(TAG, "uploadVideo Exception: " + exce)
                        response.close()
                        broadcastEmitter.onError(uploadInfo.id, VideoStatus.API_ERROR)
                    }
                } else {
                    Log.d(
                        TAG,
                        "uploadVideo errorBody: " + (response.body?.string()
                            ?: "errorbody null")
                    )
                    broadcastEmitter.onError(uploadInfo.id, VideoStatus.API_ERROR)
                }

            }
        })


//        val request: Request = Builder()
//            .url("/path/to/your/upload")
//            .headers(Headers.of("headers"))
//            .post(multipart)
//            .build()

//        val call = ApiClient
//            .getInstance()
//            .api
//            .uploadImageVideo("Bearer " + PrefConfig(context).getAccessToken(), "videos", multipart)

//        call.enqueue(object : Callback<ResponseBody?> {
//            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
//                Log.d(TAG, "uploadVideo success: ")
//                if (response.isSuccessful) {
//                    try {
//                        val jsonObject = JSONObject(response.body()!!.string())
//                        val key = jsonObject.getString("key")
//                        dbHandler.addTaskKey(uploadInfo.id, key)
//                        val taskReadyToPublish = dbHandler.isTaskReadyToPublish(uploadInfo.id)
//                        broadcastEmitter.onUploadCompleted(uploadInfo.id)
//                        if (taskReadyToPublish) {
//                            dbHandler.setTaskStatus(uploadInfo.id, VideoStatus.UPLOAD_DONE)
//                            publishVideo(key)
//                        } else {
//                            draftVideo(key)
//                        }
//                    } catch (exce: Exception) {
//                        Log.d(TAG, "uploadVideo Exception: " + exce)
//                        broadcastEmitter.onError(uploadInfo.id, VideoStatus.API_ERROR)
//                    }
//                } else {
//                    Log.d(
//                        TAG,
//                        "uploadVideo errorBody: " + (response.errorBody()?.string()
//                            ?: "errorbody null")
//                    )
//                    broadcastEmitter.onError(uploadInfo.id, VideoStatus.API_ERROR)
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
//                val bolll = t is IOException
//                Log.d(TAG, "uploadVideo error: localizedMessage " + bolll)
//
//                Log.d(TAG, "uploadVideo error: localizedMessage " + t.localizedMessage)
//                broadcastEmitter.onError(uploadInfo.id, VideoStatus.INTERNET_ERROR)
//            }
//        })
    }

    private fun publishVideo(key: String) {
        Log.d(TAG, "publishVideo start: ")
        val postArticleParams = PostArticleParams()
        postArticleParams.id = uploadInfo.id
        if (!uploadInfo.type.equals("reels")) {
            postArticleParams.video = key
            val params: MutableMap<String, String> = HashMap()
            params[Events.KEYS.ID] = uploadInfo.id
            params[Events.KEYS.DURATION] =
                (uploadInfo.videoInfo.endTime - uploadInfo.videoInfo.startTime).toString()
            logEvent(
                context,
                params,
                Events.UPLOAD_NEWSREELS_DURATION
            )
        } else {
            postArticleParams.media = key
            val params: MutableMap<String, String> = HashMap()
            params[Events.KEYS.ID] = uploadInfo.id
            params[Events.KEYS.DURATION] =
                (uploadInfo.videoInfo.endTime - uploadInfo.videoInfo.startTime).toString()
            logEvent(
                context,
                params,
                Events.UPLOAD_MEDIA_DURATION
            )
        }
        postArticleParams.status = ARTICLE_PUBLISHED
        postArticleParams.source = uploadInfo.source
        postArticleParams.type = uploadInfo.type
        val call: Call<SingleArticle>?
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            Gson().toJson(postArticleParams)
        )

        broadcastEmitter.onPublishStarted(uploadInfo.id)

        call = if (uploadInfo.type.equals("reels")) {
            ApiClient
                .getInstance()
                .api
                .createReels("Bearer " + PrefConfig(context).getAccessToken(), body)
        } else {
            ApiClient
                .getInstance()
                .api
                .createArticle(
                    "Bearer " + PrefConfig(context).getAccessToken(),
                    postArticleParams.getType(),
                    body
                )
        }

        call.enqueue(object : Callback<SingleArticle?> {
            override fun onResponse(
                call: Call<SingleArticle?>,
                response: Response<SingleArticle?>
            ) {
                dbHandler.setTaskStatus(uploadInfo.id, VideoStatus.PUBLISHED)
                Log.d(TAG, "publishVideo success: ")
                if (response.isSuccessful) {
                    if (!uploadInfo.type.equals("reels")) {
                        val params: MutableMap<String, String> = HashMap()
                        params[Events.KEYS.ID] = uploadInfo.id
                        logEvent(
                            context,
                            params,
                            Events.UPLOAD_NEWSREELS_DONE
                        )
                    } else {
                        val params: MutableMap<String, String> = HashMap()
                        params[Events.KEYS.ID] = uploadInfo.id
                        logEvent(
                            context,
                            params,
                            Events.UPLOAD_MEDIA_DONE
                        )
                    }
                    broadcastEmitter.onPublish(uploadInfo.id)
                } else {
                    Log.d(
                        TAG,
                        "publishVideo errorBody: " + (response.errorBody()?.string()
                            ?: "errorbody null")
                    )
                    broadcastEmitter.onError(uploadInfo.id, VideoStatus.API_ERROR)
                }
            }

            override fun onFailure(call: Call<SingleArticle?>, t: Throwable) {
                Log.d(TAG, "publishVideo error: localizedMessage" + t.localizedMessage)
                broadcastEmitter.onError(uploadInfo.id, VideoStatus.INTERNET_ERROR)
            }

        })
    }


    private fun draftVideo(key: String) {
        Log.d(TAG, "draft start: ")
        val postArticleParams = PostArticleParams()
        postArticleParams.id = uploadInfo.id
        if (!uploadInfo.type.equals("reels"))
            postArticleParams.video = key
        else
            postArticleParams.media = key
        postArticleParams.status = ARTICLE_DRAFT
        postArticleParams.source = uploadInfo.source
        postArticleParams.type = uploadInfo.type
        val call: Call<SingleArticle>?
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            Gson().toJson(postArticleParams)
        )

        call = if (uploadInfo.type.equals("reels")) {
            ApiClient
                .getInstance()
                .api
                .createReels("Bearer " + PrefConfig(context).getAccessToken(), body)
        } else {
            ApiClient
                .getInstance()
                .api
                .createArticle(
                    "Bearer " + PrefConfig(context).getAccessToken(),
                    postArticleParams.getType(),
                    body
                )
        }

        call.enqueue(object : Callback<SingleArticle?> {
            override fun onResponse(
                call: Call<SingleArticle?>,
                response: Response<SingleArticle?>
            ) {
                dbHandler.setTaskStatus(uploadInfo.id, VideoStatus.PUBLISHED)
                Log.d(TAG, "draft success: ")
                if (response.isSuccessful) {
                    dbHandler.setTaskStatus(uploadInfo.id, VideoStatus.UPLOAD_DONE)
                    val taskReadyToPublish = dbHandler.isTaskReadyToPublish(uploadInfo.id)
                    if (taskReadyToPublish)
                        publishVideo(key)
                    else
                        broadcastEmitter.stopService(uploadInfo.id)
                } else {
                    Log.d(
                        TAG,
                        "draft errorBody: " + (response.errorBody()?.string()
                            ?: "errorbody null")
                    )
                    broadcastEmitter.onError(uploadInfo.id, VideoStatus.API_ERROR)
                }
            }

            override fun onFailure(call: Call<SingleArticle?>, t: Throwable) {
                Log.d(TAG, "draft error: localizedMessage" + t.localizedMessage)
                broadcastEmitter.onError(uploadInfo.id, VideoStatus.INTERNET_ERROR)
            }

        })
    }

    override fun onProgressUpdate(percentage: Int) {
        Log.d(TAG, "onProgressUpdate: " + percentage)
        broadcastEmitter.onUploadProgress(percentage, uploadInfo.id)
    }

    override fun onError() {
        broadcastEmitter.onError(uploadInfo.id, VideoStatus.INTERNET_ERROR)
    }

    override fun onFinish() {

    }
}