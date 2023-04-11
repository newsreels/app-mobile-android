package com.ziro.bullet.fragments.searchNew.presenter

import android.app.Activity
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.fragments.searchNew.interfaces.SearchResultsViewInterface
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.searchresultnew.SearchresultdataBase
import com.ziro.bullet.presenter.NewDiscoverPresenter
import com.ziro.bullet.utills.InternetCheckHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class SearchResultPresenter constructor(
    private val activity: Activity? = null,
    private val searchResultsViewInterface: SearchResultsViewInterface? = null
) {

    companion object {
        const val DISCOVER_TOPIC = "DiscoverTopics"
        const val SEARCH_REELS = "Reels"
        const val DISCOVER_TRENDING_TOPICS = "TrendingTopics"
        const val DISCOVER_TRENDING_CHANNELS = "TrendingChannels"
        const val DISCOVER_TRENDING_ARTICLES = "TrendingArticles"
        const val DISCOVER_TRENDING_LIVE_CHANNELS = "TrendingLiveChannels"
        const val WEATHER_FORECAST = "WeatherForecast"
    }


    private val headerPrefix = "Bearer "
    private val mPrefs: PrefConfig = PrefConfig(activity)
    private val cacheManager: DbHandler = DbHandler(activity)

    open fun getAllLocations(query: String?, page: String?,isPagination: Boolean) {
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
//            searchResultsViewInterface?.loadingData(true)
            val call = ApiClient
                .getInstance(activity)
                .api
                .getLocations("Bearer " + mPrefs.accessToken, query, page)
            call.enqueue(object : Callback<LocationModel?> {
                override fun onResponse(
                    call: Call<LocationModel?>,
                    response: Response<LocationModel?>
                ) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.searchLocationSuccess(response.body(),isPagination)
                }

                override fun onFailure(call: Call<LocationModel?>, t: Throwable) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        "Location"
                    )                }
            })
        }
    }


    fun getSearchChannels(query: String, page: String, isPagination: Boolean) {
//        searchResultsViewInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .searchPageSources("Bearer " + mPrefs.accessToken, query, page)
            call.enqueue(object : Callback<SourceModel?> {
                override fun onResponse(
                    call: Call<SourceModel?>,
                    response: Response<SourceModel?>
                ) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.searchChannelSuccess(response.body(), isPagination)
                }

                override fun onFailure(call: Call<SourceModel?>, t: Throwable) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getTopicsFollowPresenter(id: String, position: Int, topic: DiscoverNewTopic?) {
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .addTopicnew("Bearer " + mPrefs.accessToken, id)
            call.enqueue(object : Callback<FollowResponse?> {
                override fun onResponse(
                    call: Call<FollowResponse?>,
                    response: Response<FollowResponse?>
                ) {
                    searchResultsViewInterface?.getTopicsFollow(response.body(), position, topic)
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getTopicsUnfollowPresenter(id: String, position: Int, topic: DiscoverNewTopic?) {
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .unfollowTopicNew("Bearer " + mPrefs.accessToken, id)
            call.enqueue(object : Callback<FollowResponse?> {
                override fun onResponse(
                    call: Call<FollowResponse?>,
                    response: Response<FollowResponse?>
                ) {
                    searchResultsViewInterface?.getTopicsFollow(response.body(), position, topic)
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }
    fun getLocationUnfollow2(id: String, position: Int, channel: Location?) {
//        searchResultsViewInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .unfollowLocationNew("Bearer " + mPrefs.accessToken, id)
            call.enqueue(object : Callback<FollowResponse?> {
                override fun onResponse(
                    call: Call<FollowResponse?>,
                    response: Response<FollowResponse?>
                ) {
//                    searchResultsViewInterface?.loadingData(false)

                    searchResultsViewInterface?.getLocationunFollow2(
                        response.body(),
                        position,
                        channel
                    )
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }
    fun getChannelUnfollow2(id: String, position: Int, channel: Source?) {
//        searchResultsViewInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .unfollowSourcesNew("Bearer " + mPrefs.accessToken, id)
            call.enqueue(object : Callback<FollowResponse?> {
                override fun onResponse(
                    call: Call<FollowResponse?>,
                    response: Response<FollowResponse?>
                ) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.getChannelsunFollow2(
                        response.body(),
                        position,
                        channel
                    )
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getChannelUnfollowPresenter(id: String, position: Int, channel: DiscoverNewChannels?) {
//        searchResultsViewInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .unfollowSourcesNew("Bearer " + mPrefs.accessToken, id)
            call.enqueue(object : Callback<FollowResponse?> {
                override fun onResponse(
                    call: Call<FollowResponse?>,
                    response: Response<FollowResponse?>
                ) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.getChannelsunFollow(
                        response.body(),
                        position,
                        channel
                    )
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getChannelsFollow2(id: String, position: Int, channel: Source?) {
//        searchResultsViewInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .followSources("Bearer " + mPrefs.accessToken, id)
            call.enqueue(object : Callback<FollowResponse?> {
                override fun onResponse(
                    call: Call<FollowResponse?>,
                    response: Response<FollowResponse?>
                ) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.getChannelFollow2(
                        response.body(),
                        position,
                        channel
                    )
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getLocationFollow2(id: String, position: Int, channel: Location?) {
//        searchResultsViewInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .followLocationNew("Bearer " + mPrefs.accessToken, id)
            call.enqueue(object : Callback<FollowResponse?> {
                override fun onResponse(
                    call: Call<FollowResponse?>,
                    response: Response<FollowResponse?>
                ) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.getLocationFollow2(
                        response.body(),
                        position,
                        channel
                    )
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }
    fun getChannelsFollowPresenter(id: String, position: Int, channel: DiscoverNewChannels?) {
//        searchResultsViewInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .followSources("Bearer " + mPrefs.accessToken, id)
            call.enqueue(object : Callback<FollowResponse?> {
                override fun onResponse(
                    call: Call<FollowResponse?>,
                    response: Response<FollowResponse?>
                ) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.getChannelFollow(response.body(), position, channel)
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                    searchResultsViewInterface?.loadingData(false)
                    searchResultsViewInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getSearchResults(query: String?) {
//        val response = Gson().fromJson(test, SearchresultdataBase::class.java)
//        searchResultsViewInterface!!.getSearchResult(response)
        searchResultsViewInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            searchResultsViewInterface!!.error(
                activity!!.getString(R.string.internet_error),
                NewDiscoverPresenter.DISCOVER_TRENDING_TOPICS
            )
        } else {
            val call =
                ApiClient.Instance.api.getSearchResults(
                    "$headerPrefix${mPrefs.accessToken}", "$query"
                )

            call.enqueue(object : Callback<SearchresultdataBase?> {
                override fun onResponse(
                    call: Call<SearchresultdataBase?>,
                    response: Response<SearchresultdataBase?>
                ) {
                    searchResultsViewInterface?.loadingData(false)
                    if (response.isSuccessful) {
                        searchResultsViewInterface!!.getSearchResult(response.body()!!)
//                       searchResultsViewInterface!!.refreshHistory()

                    } else {
                        searchResultsViewInterface?.loadingData(false)
                        searchResultsViewInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            NewDiscoverPresenter.DISCOVER_TRENDING_TOPICS
                        )
                    }
                }

                override fun onFailure(call: Call<SearchresultdataBase?>, t: Throwable) {
                    searchResultsViewInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        NewDiscoverPresenter.DISCOVER_TRENDING_TOPICS
                    )
                }
            })
        }
    }


}

