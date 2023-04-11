package com.ziro.bullet.onboarding.ui.categories

import android.app.Activity
import com.ziro.bullet.common.base.UiState
import com.ziro.bullet.data.dataclass.OnBoardingModel
import com.ziro.bullet.data.dataclass.SaveOnBoardingModel
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.onboarding.data.repository.OnBoardingRepoImp
import com.ziro.bullet.onboarding.domain.repository.OnBoardingRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnBoardingPresenter(private val listener: Listener) {

    lateinit var repo: OnBoardingRepo

    fun initPresenter(){
        repo = OnBoardingRepoImp(listener.activity)
    }

    fun getOnBoardingCollection(){
        listener.onBoardingCollections(UiState.Loading)
        repo.getOnBoardingCollection(object : Callback<OnBoardingModel> {
            override fun onResponse(
                call: Call<OnBoardingModel>,
                response: Response<OnBoardingModel>
            ) {
                listener.onBoardingCollections(UiState.Success(response.body()))
            }
            override fun onFailure(call: Call<OnBoardingModel>, t: Throwable) {
                listener.onBoardingCollections(UiState.Error(t))
            }
        })
    }

    fun saveCategories(categories: SaveOnBoardingModel) {
        listener.onCategoriesSaved(UiState.Loading)
        repo.saveOnBoarding(categories, object: Callback<OnBoardingModel>{
            override fun onResponse(
                call: Call<OnBoardingModel>,
                response: Response<OnBoardingModel>
            ) {
                listener.onCategoriesSaved(UiState.Success())
            }

            override fun onFailure(call: Call<OnBoardingModel>, t: Throwable) {
                listener.onCategoriesSaved(UiState.Error(t))
            }
        })
    }

    fun loadReels() {
        listener.onReelsResult(UiState.Loading)
        repo.loadReels(object: Callback<ReelResponse>{
            override fun onResponse(call: Call<ReelResponse>, response: Response<ReelResponse>) {
                listener.onReelsResult(UiState.Success(response.body()))
            }
            override fun onFailure(call: Call<ReelResponse>, t: Throwable) {
                listener.onReelsResult(UiState.Error(t))
            }
        })
    }

    interface Listener{
        val activity: Activity
        fun onBoardingCollections(state: UiState<OnBoardingModel>)
        fun onCategoriesSaved(state: UiState<Unit>)
        fun onReelsResult(state: UiState<ReelResponse>)
    }
}