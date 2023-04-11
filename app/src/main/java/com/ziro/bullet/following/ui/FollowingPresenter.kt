package com.ziro.bullet.following.ui

import android.app.Activity
import com.ziro.bullet.common.base.UiState
import com.ziro.bullet.common.exceptions.NoInternetException
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.data.models.topics.Topics
import com.ziro.bullet.data.models.topics.TopicsModel
import com.ziro.bullet.following.data.repository.FollowingRepoImp
import com.ziro.bullet.following.domain.repository.FollowingRepo
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.utills.InternetCheckHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FollowingPresenter(
    private val listener: Listener
) {

    lateinit var repo: FollowingRepo

    fun initPresenter() {
        repo = FollowingRepoImp(listener.activity)
    }

    fun getFollowingLocations(page: String) {
        if (!InternetCheckHelper.isConnected()) {
            listener.onFollowingLocation(UiState.Error(NoInternetException()))
        } else {
            repo.getFollowingLocations(page, object : Callback<LocationModel> {
                override fun onResponse(
                    call: Call<LocationModel>,
                    response: Response<LocationModel>
                ) {
                    listener.onFollowingLocation(UiState.Success(response.body()))
                }

                override fun onFailure(call: Call<LocationModel>, t: Throwable) {
                    listener.onFollowingLocation(UiState.Error(t))
                }
            })
        }
    }

    fun unFollowChannel(id: String, channel: Source?, position: Int) {
        if (!InternetCheckHelper.isConnected()) {
            listener.followChannel(UiState.Error(NoInternetException()), channel, position)
        } else {
            repo.unFollowChannel(id, object : Callback<FollowResponse> {
                override fun onResponse(
                    call: Call<FollowResponse>,
                    response: Response<FollowResponse>
                ) {
                    if (response.isSuccessful) {
                        listener.followChannel(UiState.Success(response.body()), channel, position)
                    } else {
                        listener.followChannel(
                            UiState.Error(Throwable("Api Error")),
                            channel,
                            position
                        )
                    }
                }

                override fun onFailure(call: Call<FollowResponse>, t: Throwable) {
                    listener.followChannel(UiState.Error(t), channel, position)
                }
            })
        }
    }

    fun unFollowTopic(id: String, topic: Topics?, position: Int) {
        if (!InternetCheckHelper.isConnected()) {
            listener.followTopic(UiState.Error(NoInternetException()), topic, position)
        } else {
            repo.unFollowTopic(id, object : Callback<FollowResponse> {
                override fun onResponse(
                    call: Call<FollowResponse>,
                    response: Response<FollowResponse>
                ) {
                    if (response.isSuccessful) {
                        listener.followTopic(UiState.Success(response.body()), topic, position)
                    } else {
                        listener.followTopic(
                            UiState.Error(Throwable("Api Error")),
                            topic,
                            position
                        )
                    }
                }

                override fun onFailure(call: Call<FollowResponse>, t: Throwable) {
                    listener.followTopic(UiState.Error(t), topic, position)
                }
            })
        }
    }

    fun unFollowPlaces(id: String, location: Location?, position: Int) {
        if (!InternetCheckHelper.isConnected()) {
            listener.followLocation(UiState.Error(NoInternetException()), location, position)
        } else {
            repo.unFollowLocation(id, object : Callback<FollowResponse> {
                override fun onResponse(
                    call: Call<FollowResponse>,
                    response: Response<FollowResponse>
                ) {
                    if (response.isSuccessful) {
                        listener.followLocation(
                            UiState.Success(response.body()),
                            location,
                            position
                        )
                    } else {
                        listener.followLocation(
                            UiState.Error(Throwable("Api Error")),
                            location,
                            position
                        )
                    }
                }

                override fun onFailure(call: Call<FollowResponse>, t: Throwable) {
                    listener.followLocation(UiState.Error(t), location, position)
                }
            })
        }
    }

//        fun followPlaces(id: String, position: Int, channel: Location?) {
//            searchResultsViewInterface?.loadingData(true)
//            if (!InternetCheckHelper.isConnected()) {
//                searchResultsViewInterface?.error(
//                    activity!!.getString(R.string.internet_error),
//                    SearchResultPresenter.DISCOVER_TOPIC
//                )
//            } else {
//                val call = ApiClient
//                    .getInstance(activity)
//                    .api
//                    .followLocationNew("Bearer " + mPrefs.accessToken, id)
//                call.enqueue(object : Callback<FollowResponse?> {
//                    override fun onResponse(
//                        call: Call<FollowResponse?>,
//                        response: Response<FollowResponse?>
//                    ) {
//                        searchResultsViewInterface?.loadingData(false)
//                        searchResultsViewInterface?.getLocationFollow2(
//                            response.body(),
//                            position,
//                            channel
//                        )
//                    }
//
//                    override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                        searchResultsViewInterface?.loadingData(false)
//                        searchResultsViewInterface?.error(
//                            activity!!.getString(R.string.server_error),
//                            SearchResultPresenter.DISCOVER_TOPIC
//                        )
//                    }
//                })
//            }
//        }

    fun getFollowingTopics(page: String) {
        if (!InternetCheckHelper.isConnected()) {
            listener.onFollowingTopics(UiState.Error(NoInternetException()))
        } else {
            repo.getFollowingTopics(page, object : Callback<TopicsModel> {
                override fun onResponse(
                    call: Call<TopicsModel>,
                    response: Response<TopicsModel>
                ) {
                    listener.onFollowingTopics(UiState.Success(response.body()))
                }

                override fun onFailure(call: Call<TopicsModel>, t: Throwable) {
                    listener.onFollowingTopics(UiState.Error(t))
                }
            })
        }
    }

    fun getFollowingChannels(page: String) {
        if (!InternetCheckHelper.isConnected()) {
            listener.onFollowingChannels(UiState.Error(NoInternetException()))
        } else {
            repo.getFollowingChannels(page, object : Callback<SourceModel> {
                override fun onResponse(
                    call: Call<SourceModel>,
                    response: Response<SourceModel>
                ) {
                    if (response.isSuccessful) {

                        listener.onFollowingChannels(UiState.Success(response.body()))
                    } else {
                        listener.onFollowingChannels(UiState.Error(null))
                    }
                }

                override fun onFailure(call: Call<SourceModel>, t: Throwable) {
                    listener.onFollowingChannels(UiState.Error(t))
                }
            })
        }
    }


    fun getSuggestedTopics() {
        if (!InternetCheckHelper.isConnected()) {
            listener.onSuggestedTopics(UiState.Error(NoInternetException()))
        } else {
            repo.getSuggestedTopics(object : Callback<TopicsModel> {
                override fun onResponse(
                    call: Call<TopicsModel>,
                    response: Response<TopicsModel>
                ) {
                    listener.onSuggestedTopics(UiState.Success(response.body()))
                }

                override fun onFailure(call: Call<TopicsModel>, t: Throwable) {
                    listener.onSuggestedTopics(UiState.Error(t))
                }
            })
        }
    }

    fun getSuggestedChannels(hasReels: Boolean) {
        if (!InternetCheckHelper.isConnected()) {
            listener.onSuggestedChannels(UiState.Error(NoInternetException()))
        } else {
            repo.getSuggestedChannels(hasReels, object : Callback<SourceModel> {
                override fun onResponse(
                    call: Call<SourceModel>,
                    response: Response<SourceModel>
                ) {
                    listener.onSuggestedChannels(UiState.Success(response.body()))
                }

                override fun onFailure(call: Call<SourceModel>, t: Throwable) {
                    listener.onSuggestedChannels(UiState.Error(t))
                }
            })
        }
    }

    fun getSuggestedLocations() {
        if (!InternetCheckHelper.isConnected()) {
            listener.onSuggestedLocation(UiState.Error(NoInternetException()))
        } else {
            repo.getSuggestedLocations(object : Callback<LocationModel> {
                override fun onResponse(
                    call: Call<LocationModel>,
                    response: Response<LocationModel>
                ) {
                    listener.onSuggestedLocation(UiState.Success(response.body()))
                }

                override fun onFailure(call: Call<LocationModel>, t: Throwable) {
                    listener.onSuggestedLocation(UiState.Error(t))
                }
            })
        }
    }

    fun followChannel(id: String, item: Source, position: Int) {
        if (!InternetCheckHelper.isConnected()) {
            listener.followChannel(UiState.Error(NoInternetException()), item, position)
        } else {
            repo.followChannel(id, object : Callback<FollowResponse> {
                override fun onResponse(
                    call: Call<FollowResponse>,
                    response: Response<FollowResponse>
                ) {
                    listener.followChannel(UiState.Success(response.body()), item, position)
                }

                override fun onFailure(call: Call<FollowResponse>, t: Throwable) {
                    listener.followChannel(UiState.Error(t), item, position)
                }
            })
        }
    }

    fun followTopic(id: String, item: Topics, position: Int) {
        if (!InternetCheckHelper.isConnected()) {
            listener.followTopic(UiState.Error(NoInternetException()), item, position)
        } else {
            repo.followTopics(id, object : Callback<FollowResponse> {
                override fun onResponse(
                    call: Call<FollowResponse>,
                    response: Response<FollowResponse>
                ) {
                    listener.followTopic(UiState.Success(response.body()), item, position)
                }

                override fun onFailure(call: Call<FollowResponse>, t: Throwable) {
                    listener.followTopic(UiState.Error(t), item, position)
                }
            })
        }

    }

    fun followPlaces(id: String, item: Location, position: Int) {
        if (!InternetCheckHelper.isConnected()) {
            listener.followLocation(UiState.Error(NoInternetException()), item, position)
        } else {
            repo.followLocation(id, object : Callback<FollowResponse> {
                override fun onResponse(
                    call: Call<FollowResponse>,
                    response: Response<FollowResponse>
                ) {
                    listener.followLocation(UiState.Success(response.body()), item, position)
                }

                override fun onFailure(call: Call<FollowResponse>, t: Throwable) {
                    listener.followLocation(UiState.Error(t), item, position)
                }
            })
        }
    }

    interface Listener {
        val activity: Activity
        fun onFollowingTopics(state: UiState<TopicsModel>)
        fun onFollowingChannels(state: UiState<SourceModel>)
        fun onFollowingLocation(state: UiState<LocationModel>)
        fun onSuggestedTopics(state: UiState<TopicsModel>)
        fun onSuggestedChannels(state: UiState<SourceModel>)
        fun onSuggestedLocation(state: UiState<LocationModel>)

        fun followChannel(state: UiState<FollowResponse>, channel: Source?, position: Int)
        fun followTopic(state: UiState<FollowResponse>, topic: Topics?, position: Int)
        fun followLocation(state: UiState<FollowResponse>, location: Location?, position: Int)
    }
}