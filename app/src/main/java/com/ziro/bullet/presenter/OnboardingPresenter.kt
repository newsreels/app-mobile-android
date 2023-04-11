package com.ziro.bullet.presenter

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.BuildConfig
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.activities.onboarding.ApiCallback
import com.ziro.bullet.analytics.AnalyticsEvents.logEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.dataclass.ContentLanguageResponse
import com.ziro.bullet.data.dataclass.OnBoardingModel
import com.ziro.bullet.data.dataclass.RegionResponse
import com.ziro.bullet.data.dataclass.SaveOnBoardingModel
import com.ziro.bullet.data.models.topics.TopicsModel
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.InternetCheckHelper
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnboardingPresenter(
    private val context: Context,
    private val callback: ApiCallback
) {
    private var mPrefConfig: PrefConfig = PrefConfig(context)

    fun saveOnboarding(saveOnBoardingModel: SaveOnBoardingModel) =
        if (!InternetCheckHelper.isConnected()) {
            callback.onError(context.getString(R.string.internet_error))
        } else {
            callback.onLoading(true)
            val call =
                ApiClient.getInstance(context as Activity?).api.saveOnboarding(
                    "Bearer " + mPrefConfig.accessToken,
                    saveOnBoardingModel
                )

            try {
                val paramsTopics = HashMap<String, String>()
                val paramsRegions = HashMap<String, String>()
                val paramsLanguage = HashMap<String, String>()
                saveOnBoardingModel.topics.toString().also { paramsTopics[Events.KEYS.ID] = it }
                saveOnBoardingModel.regions.toString().also { paramsRegions[Events.KEYS.ID] = it }
                saveOnBoardingModel.languageList.toString()
                    .also { paramsLanguage[Events.KEYS.ID] = it }
                logEvent(
                    context,
                    paramsTopics,
                    Events.REG_SELECT_TOPIC
                )
                logEvent(
                    context,
                    paramsRegions,
                    Events.REG_SELECT_PLACES
                )
                logEvent(
                    context,
                    paramsLanguage,
                    Events.REG_SELECT_LANG_CONTENT
                )
            } catch (e: Exception) {

            }
            call.enqueue(object : Callback<OnBoardingModel> {
                override fun onResponse(
                    call: Call<OnBoardingModel>,
                    response: Response<OnBoardingModel>
                ) {
                    callback.onLoading(false)
                    if (response.isSuccessful) {
                        loadReels()
//                        callback.onSuccess()
                    } else {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        val message = jsonObject.getString("message")
                        callback.onError(message)
                    }
                }

                override fun onFailure(call: Call<OnBoardingModel>, t: Throwable) {
                    callback.onLoading(false)
                    callback.onError(context.getString(R.string.server_error))
                }

            })
        }

    fun getOnboardingCollection() {
        if (!InternetCheckHelper.isConnected()) {
            callback.onError(context.getString(R.string.internet_error))
        } else {
            callback.onLoading(true)
            val call =
                ApiClient.getInstance(context as Activity?).api.getOnboarding("Bearer " + mPrefConfig.accessToken)
            call.enqueue(object : Callback<OnBoardingModel> {
                override fun onResponse(
                    call: Call<OnBoardingModel>,
                    response: Response<OnBoardingModel>
                ) {
                    callback.onLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { callback.onSuccess(it) }
                    } else {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        val message = jsonObject.getString("message")
                        callback.onError(message)
                    }
                }

                override fun onFailure(call: Call<OnBoardingModel>, t: Throwable) {
                    callback.onLoading(false)
                    callback.onError(context.getString(R.string.server_error))
                }

            })
        }
    }

    fun getContentLanguages(query: String, page: String) {
        if (!InternetCheckHelper.isConnected()) {
            callback.onError(context.getString(R.string.internet_error))
        } else {
            callback.onLoading(true)
            val call =
                ApiClient.getInstance(context as Activity?).api.getContentLanguages(
                    "Bearer " + mPrefConfig.accessToken,
                    query,
                    page
                )
            call.enqueue(object : Callback<ContentLanguageResponse> {
                override fun onResponse(
                    call: Call<ContentLanguageResponse>,
                    response: Response<ContentLanguageResponse>
                ) {
                    callback.onLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { callback.onSuccess(it) }
                    } else {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        val message = jsonObject.getString("message")
                        callback.onError(message)
                    }
                }

                override fun onFailure(call: Call<ContentLanguageResponse>, t: Throwable) {
                    callback.onLoading(false)
                    callback.onError(context.getString(R.string.server_error))
                }

            })
        }
    }

    fun getRegions(query: String, page: String) {
        if (!InternetCheckHelper.isConnected()) {
            callback.onError(context.getString(R.string.internet_error))
        } else {
            callback.onLoading(true)
            val call =
                ApiClient.getInstance(context as Activity?).api.getRegions(
                    "Bearer " + mPrefConfig.accessToken,
                    query,
                    page
                )
            call.enqueue(object : Callback<RegionResponse> {
                override fun onResponse(
                    call: Call<RegionResponse>,
                    response: Response<RegionResponse>
                ) {
                    callback.onLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { callback.onSuccess(it) }
                    } else {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        val message = jsonObject.getString("message")
                        callback.onError(message)
                    }
                }

                override fun onFailure(call: Call<RegionResponse>, t: Throwable) {
                    callback.onLoading(false)
                    callback.onError(context.getString(R.string.server_error))
                }

            })
        }
    }

    fun getTopics(query: String, page: String) {
        if (!InternetCheckHelper.isConnected()) {
            callback.onError(context.getString(R.string.internet_error))
        } else {
            callback.onLoading(true)
            val call =
                ApiClient.getInstance(context as Activity?).api.getTopics(
                    "Bearer " + mPrefConfig.accessToken,
                    query,
                    page
                )
            call.enqueue(object : Callback<TopicsModel> {
                override fun onResponse(
                    call: Call<TopicsModel>,
                    response: Response<TopicsModel>
                ) {
                    callback.onLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { callback.onSuccess(it) }
                    } else {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        val message = jsonObject.getString("message")
                        callback.onError(message)
                    }
                }

                override fun onFailure(call: Call<TopicsModel>, t: Throwable) {
                    callback.onLoading(false)
                    callback.onError(context.getString(R.string.server_error))
                }

            })
        }
    }

    fun updateContentLanguages(followedList: List<String>) {
        if (!InternetCheckHelper.isConnected()) {
            callback.onError(context.getString(R.string.internet_error))
        } else {
            callback.onLoading(true)
            val call =
                ApiClient.getInstance(context as Activity?).api.updateContentLanguages(
                    "Bearer " + mPrefConfig.accessToken, true, followedList
                )
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    callback.onLoading(false)
                    if (response.isSuccessful) {
                        response.body()?.let { callback.onSuccess() }
                    } else {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        val message = jsonObject.getString("message")
                        callback.onError(message)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callback.onLoading(false)
                    callback.onError(context.getString(R.string.server_error))
                }

            })
        }
    }

    fun loadReels() {
        callback.onLoading(true)
        val call =
            ApiClient.getInstance(context as Activity?).api.newsReel(
                "Bearer " + mPrefConfig.accessToken,
                "",
                "",
                "",
                Constants.REELS_FOR_YOU,
                BuildConfig.DEBUG
            )

        call.enqueue(object : Callback<ReelResponse> {
            override fun onResponse(
                call: Call<ReelResponse>,
                response: Response<ReelResponse>
            ) {
                callback.onLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let {
                        val dbHandler = DbHandler(context)
                        dbHandler.insertReelList("ReelsList", Gson().toJson(it))
                    }
                }
                callback.onSuccess()
            }

            override fun onFailure(call: Call<ReelResponse>, t: Throwable) {
                callback.onLoading(false)
            }

        })
    }
}