package com.ziro.bullet.onboarding.domain.repository

import com.ziro.bullet.data.dataclass.ContentLanguageResponse
import com.ziro.bullet.data.dataclass.OnBoardingModel
import com.ziro.bullet.data.dataclass.RegionResponse
import com.ziro.bullet.data.dataclass.SaveOnBoardingModel
import com.ziro.bullet.data.models.topics.TopicsModel
import com.ziro.bullet.model.Reel.ReelResponse
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