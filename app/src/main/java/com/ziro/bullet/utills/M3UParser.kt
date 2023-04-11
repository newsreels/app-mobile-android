package com.ziro.bullet.utills

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.ziro.bullet.model.Reel.ReelsItem
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

class M3UParser(
    private val context: Context
) {
    private var resUrl: String? = null
    private var parsedURL: Array<String>? = null
    private var videoFiles: List<String>? = null
    private var videoCacheListener: VideoCacheListener? = null
    private var reelsItem: ReelsItem? = null
    private var reelPosition: Int = -1

    private var videoName: String? = null
    private var hlsFile: File? = null
    private var videoPath: String? = null
    private var connectivityManager: ConnectivityManager?
//    private var trafficSpeedMeasurer: TrafficSpeedMeasurer?

    private var activity: Context? = null
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            GlobalScope.launch(Dispatchers.Main)
            { videoCacheListener?.videoCached(null, reelPosition) }
        }

    init {
        connectivityManager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        trafficSpeedMeasurer = TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL)
//        trafficSpeedMeasurer?.registerListener(object : ITrafficSpeedListener {
//            override fun onTrafficSpeedMeasured(upStream: Double, downStream: Double) {
//                Log.d(
//                    "DEBUG_SPEED",
//                    "onTrafficSpeedMeasured: DownSpeed :: ${
//                        TrafficUtils.parseSpeed(
//                            downStream,
//                            true
//                        )
//                    }"
//                )
//            }
//        })
//        trafficSpeedMeasurer?.startMeasuring()/
    }

    fun updateFilePrefs(
        videoNamePrefix: String,
        hlsFile: File,
        videoPath: String
    ) {
        this.resUrl = null
        this.parsedURL = null
        this.videoFiles = null
        this.videoName = null
        this.hlsFile = null
        this.videoPath = null
        this.reelPosition = -1
        this.reelsItem = null
        this.videoName = videoNamePrefix
        this.hlsFile = hlsFile
        this.videoPath = videoPath
        Log.d(
            "M3U8_TAG",
            "updateFilePrefs: videoname:: $videoNamePrefix \nHls:: ${hlsFile.absoluteFile}"
        )
//        getNetworkSpeed()
    }

    private fun getNetworkSpeed() {
        if (connectivityManager == null) {
            return
        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        val wifiConnected: NetworkInfo? =
            connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileDataConnected: NetworkInfo? =
            connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiConnected != null && wifiConnected.isConnected) {
            Log.d("DEBUG_TAG", "getNetworkSpeed: wifi connected")
            RES = RES_720
        } else if (mobileDataConnected != null && mobileDataConnected.isConnected) {
            Log.d("DEBUG_TAG", "getNetworkSpeed: Mobile Data connected")
            RES = RES_480
        }
//        } else {
//            val networkInfo =
//                connectivityManager!!.getNetworkCapabilities(connectivityManager!!.activeNetwork)
//            if (networkInfo != null) {
//                val downSpeed = (networkInfo.linkDownstreamBandwidthKbps) / 1000
//                Log.d("DEBUG_TAG", "getNetworkSpeed: $downSpeed")
//                RES = if (downSpeed <= 5) {
//                    RES_240
//                } else {
//                    RES_720
//                }
//            }
//        }
    }

    fun addVideoCacheListener(videoCacheListener: VideoCacheListener) {
        this.videoCacheListener = videoCacheListener
    }

    fun cacheVideo(reelsItem: ReelsItem, position: Int) {
        this.reelPosition = position
        this.reelsItem = reelsItem
//        videoCacheListener!!.videoCached(reelsItem, position)
//        return
        GlobalScope.launch(coroutineExceptionHandler) {
            withContext(Dispatchers.IO) {
                resUrl = getFilesFromURL(URL(reelsItem.media).openStream())
            }
            withContext(Dispatchers.IO) {
                parsedURL = parseURL(URL(resUrl).openStream())
                videoFiles = getVideoFiles(URL(resUrl).openStream())
            }

            withContext(Dispatchers.Main) {
                if (!videoFiles.isNullOrEmpty()) {
                    downloadFile(videoFiles!![0], videoName!!)
                }
            }
        }
    }

    private fun downloadFile(url: String, fileName: String) {

        Log.d("M3U8_TAG", "downloadFile: videoname:: $fileName")
        PRDownloader
            .download(url, videoPath, fileName)
            .build()
            .setOnProgressListener {
//                Log.d("DEBUG_TAG", "downloadFile: $reelPosition Progress::>> $it")
            }
            .start(object : OnDownloadListener {
                override fun onError(error: Error?) {
                    videoCacheListener?.videoCached(reelsItem, reelPosition)
                    Log.d(
                        "DEBUG_TAG",
                        "onError: File Download Error::>> ${error!!.connectionException.printStackTrace()} "
                    )
                    Log.d(
                        "DEBUG_TAG",
                        "onError: Download Completed $reelPosition _ ${reelsItem!!.description} _ Not Cached"
                    )
                }

                override fun onDownloadComplete() {
                    var position = -1
                    parsedURL?.forEachIndexed { index, s ->
                        if (s == url) {
                            position = index
                        }
                    }
                    if (position >= 0) {
                        Log.d("M3U8_TAG", "onDownloadComplete: videoname:: $fileName \n")
                        parsedURL!![position] = "$videoPath/$fileName"
                    }
                    GlobalScope.launch(coroutineExceptionHandler) {
                        withContext(Dispatchers.IO) {
                            hlsFile?.createNewFile()
                            createHLSFile(
                                parsedURL!!,
                                hlsFile?.absolutePath!!
                            )
                        }

                        withContext(Dispatchers.Main) {

                            val file = File(videoPath, fileName)
                            if (file.exists()) {
                                Log.d(
                                    "DEBUG_TAG",
                                    "onDownloadComplete: Download Completed $reelPosition _ ${reelsItem!!.description} _ cached :: Size_ ${(file.length() / 1024f) / 1024f}"
                                )
                            }
                            reelsItem?.isCached = true
                            reelsItem?.media = hlsFile?.absolutePath
                            videoCacheListener?.videoCached(reelsItem!!, reelPosition)
                        }
                    }
                }

            })
    }

    private fun convertStreamToString(`is`: InputStream): String {
        return try {
            Scanner(`is`).useDelimiter("\\A").next()
        } catch (e: NoSuchElementException) {
            ""
        }
    }

    private fun parseURL(inputStream: InputStream): Array<String> {
        val stream = convertStreamToString(inputStream)
        return stream.split("\n").toTypedArray()
    }

    private fun getFilesFromURL(inputStream: InputStream): String {
        var fileUrl = ""
        val stream = convertStreamToString(inputStream)
        val linesArray = stream.split("\n").toTypedArray()
        for (line in linesArray) {
            if (line.startsWith(EXT_URL) && line.contains(RES)) {
                fileUrl = line
                break
            }
        } 
        if (fileUrl.isNullOrEmpty()) {
            RES = RES_240
            for (line in linesArray) {
                if (line.startsWith(EXT_URL) && line.contains(RES)) {
                    fileUrl = line
                    break
                }
            }
        }
        try {
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fileUrl
    }

    private fun getVideoFiles(inputStream: InputStream): List<String> {
        val videoFilesLink: MutableList<String> = ArrayList()
        val stream = convertStreamToString(inputStream)
        val linesArray = stream.split("\n").toTypedArray()
        for (line in linesArray) {
            if (line.startsWith(EXT_URL)) {
                videoFilesLink.add(line)
            }
        }
        try {
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return videoFilesLink
    }

    fun createHLSFile(context: Context?, inputStream: InputStream, outputPath: String?) {
        try {
            val stream = convertStreamToString(inputStream)
            val outputStreamWriter = FileOutputStream(outputPath)
            outputStreamWriter.write(stream.toByteArray())
            outputStreamWriter.close()
            //            Toast.makeText(context, "File Created", Toast.LENGTH_SHORT).show();
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    private fun createHLSFile(parsedURL: Array<String>, absolutePath: String) {
        try {
            val lineBreak = System.lineSeparator()
            val fileOutputStream = FileOutputStream(File(absolutePath))
            for (item in parsedURL) {
                fileOutputStream.write(item.toByteArray())
                fileOutputStream.write(lineBreak.toByteArray())
            }
            fileOutputStream.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    interface VideoCacheListener {
        fun videoCached(reelsItem: ReelsItem?, position: Int)
    }

    companion object {
        private const val EXT_URL = "https://"
        private var RES = "720p"
        private const val RES_720 = "720p"
        private const val RES_480 = "480p"
        private const val RES_240 = "240"
    }
}