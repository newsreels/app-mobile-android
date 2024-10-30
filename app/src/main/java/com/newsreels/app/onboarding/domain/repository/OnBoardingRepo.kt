package com.newsreels.app.onboarding.domain.repository

import com.newsreels.app.data.dataclass.ContentLanguageResponse
import com.newsreels.app.data.dataclass.OnBoardingModel
import com.newsreels.app.data.dataclass.RegionResponse
import com.newsreels.app.data.dataclass.SaveOnBoardingModel
import com.newsreels.app.data.models.topics.TopicsModel
import com.newsreels.app.model.Reel.ReelResponse
import okhttp3.ResponseBody
import retrofit2.Callback

interface OnBoardingRepo {

    fun saveOnBoarding(saveOnBoardingModel: SaveOnBoardingModel, callback: Callback<OnBoardingModel>)

    fun getOnBoardingCollection(callback: Callback<OnBoardingModel>)

    fun getContentLanguages(query: String, page: String, callback: Callback<ContentLanguageResponse>)

    fun getRegions(query: String, page: String, callback: Callback<RegionResponse>)

    fun getTopics(query: String, page: String, callback: Callback<TopicsModel>)

    fun updateContentLanguages(followedList: List<String>, callback: Callback<ResponseBody>)

    fun loadReels(callback : Callback<ReelResponse>)
}