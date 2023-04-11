package com.ziro.bullet.onboarding.data.repository

import android.app.Activity
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.BuildConfig
import com.ziro.bullet.analytics.AnalyticsEvents
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.dataclass.ContentLanguageResponse
import com.ziro.bullet.data.dataclass.OnBoardingModel
import com.ziro.bullet.data.dataclass.RegionResponse
import com.ziro.bullet.data.dataclass.SaveOnBoardingModel
import com.ziro.bullet.data.models.topics.TopicsModel
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.onboarding.domain.repository.OnBoardingRepo
import com.ziro.bullet.utills.Constants
import okhttp3.ResponseBody
import retrofit2.Callback

class OnBoardingRepoImp(private val activity: Activity) : OnBoardingRepo {

    var apiClient: ApiClient = ApiClient.getInstance(activity)
    var prefs = PrefConfig(activity.baseContext)

    override fun saveOnBoarding(
        saveOnBoardingModel: SaveOnBoardingModel,
        callback: Callback<OnBoardingModel>
    ) {
        val call = apiClient.api.saveOnboarding("Bearer " + prefs.accessToken, saveOnBoardingModel)

        try {
            val paramsTopics = HashMap<String, String>()
            val paramsRegions = HashMap<String, String>()
            val paramsLanguage = HashMap<String, String>()
            saveOnBoardingModel.topics.toString().also { paramsTopics[Events.KEYS.ID] = it }
            saveOnBoardingModel.regions.toString().also { paramsRegions[Events.KEYS.ID] = it }
            saveOnBoardingModel.languageList.toString().also { paramsLanguage[Events.KEYS.ID] = it }
            AnalyticsEvents.logEvent(
                activity.baseContext,
                paramsTopics,
                Events.REG_SELECT_TOPIC
            )
            AnalyticsEvents.logEvent(
                activity.baseContext,
                paramsRegions,
                Events.REG_SELECT_PLACES
            )
            AnalyticsEvents.logEvent(
                activity.baseContext,
                paramsLanguage,
                Events.REG_SELECT_LANG_CONTENT
            )
        } catch (e: Exception) {
        }

        call.enqueue(callback)
    }

    override fun getOnBoardingCollection(callback: Callback<OnBoardingModel>) {
        val call = apiClient.api.getOnboarding("Bearer " + prefs.accessToken)
        call.enqueue(callback)
    }

    override fun getContentLanguages(
        query: String,
        page: String,
        callback: Callback<ContentLanguageResponse>
    ) {
        val call = apiClient.api.getContentLanguages("Bearer " + prefs.accessToken, query, page)
        call.enqueue(callback)
    }

    override fun getRegions(query: String, page: String, callback: Callback<RegionResponse>) {
        val call = apiClient.api.getRegions("Bearer " + prefs.accessToken, query, page)
        call.enqueue(callback)
    }

    override fun getTopics(query: String, page: String, callback: Callback<TopicsModel>) {
        val call = apiClient.api.getTopics("Bearer " + prefs.accessToken, query, page)
        call.enqueue(callback)
    }

    override fun updateContentLanguages(
        followedList: List<String>,
        callback: Callback<ResponseBody>
    ) {
        val call = apiClient.api.updateContentLanguages(
            "Bearer " + prefs.accessToken,
            true,
            followedList
        )
        call.enqueue(callback)
    }

    override fun loadReels(callback: Callback<ReelResponse>) {
        val call = apiClient.api.newsReel(
            "Bearer " + prefs.accessToken, "", "", "", Constants.REELS_FOR_YOU, BuildConfig.DEBUG
        )
        call.enqueue(callback)
    }


}