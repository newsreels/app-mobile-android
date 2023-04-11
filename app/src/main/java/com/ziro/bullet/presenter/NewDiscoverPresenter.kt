package com.ziro.bullet.presenter

import android.app.Activity
import android.util.Log
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.BuildConfig
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.fragments.searchNew.presenter.SearchResultPresenter
import com.ziro.bullet.interfaces.DiscoverResponseInterface
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.model.articlenew.ArticleBase
import com.ziro.bullet.model.discoverNew.DiscoverDetailsResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.discoverNew.liveScore.LiveScoreApiResponse
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.trading.icons.TradingIconsResponse
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import com.ziro.bullet.model.places.PlacesOrderBase
import com.ziro.bullet.model.searchhistory.SearchHistoryBase
import com.ziro.bullet.model.searchhistorydelete.DeleteHistory
import com.ziro.bullet.utills.InternetCheckHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class NewDiscoverPresenter constructor(
    private val activity: Activity? = null,
    private val discoverResponseInterface: DiscoverResponseInterface? = null
) {

    companion object {
        const val DISCOVER_TOPIC = "DiscoverTopics"
        const val DISCOVER_TRENDING_REELS = "TrendingReels"
        const val DISCOVER_TRENDING_TOPICS = "TrendingTopics"
        const val DISCOVER_TRENDING_CHANNELS = "TrendingChannels"
        const val DISCOVER_TRENDING_ARTICLES = "TrendingArticles"
        const val DISCOVER_TRENDING_LIVE_CHANNELS = "TrendingLiveChannels"
        const val SEARCH_HISTORY = "SearchHistory"
        const val WEATHER_FORECAST = "WeatherForecast"
        const val TRADING_ITEMS_LIST = "TradingItemsList"
        const val CRYPTO = "CRYPTO"
        const val FOREX = "FOREX"
        const val LIVE_SCORE = "LiveScore"
    }

    private val headerPrefix = "Bearer "
    private val mPrefs: PrefConfig = PrefConfig(activity)
    private val cacheManager: DbHandler = DbHandler(activity)

    var sportCategory:String = "cricket"

    open fun placesArticlesContext(
        query: String?,
        page: String?,
        context: String,
        pagination: Boolean
    ) {

//        discoverResponseInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface?.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            var readerMode = false
            if (mPrefs != null) readerMode = mPrefs.isReaderMode
            val call = ApiClient
                .getInstance(activity)
                .api
                .searchArticlesContext(
                    "Bearer " + mPrefs.accessToken,
                    "india",
                    "TE9DQVRJT04qKioqKkluZGlh",
                    true,
                    page
                )

            call.enqueue(object : Callback<ArticleBase?> {
                override fun onResponse(
                    call: Call<ArticleBase?>,
                    response: Response<ArticleBase?>
                ) {
//                    discoverResponseInterface?.loadingData(false)
                    discoverResponseInterface?.onSearchArticleSuccess(response.body(), pagination)
                }

                override fun onFailure(call: Call<ArticleBase?>, t: Throwable) {
                    discoverResponseInterface?.loadingData(false)
                    discoverResponseInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getPlacesOrder() {
//        discoverResponseInterface!!.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface?.error(
                activity!!.getString(R.string.internet_error),
                "Error"
            )
        } else {
            val call =
                ApiClient.Instance.api.getPlacesOrder(
                    "$headerPrefix ${mPrefs.accessToken}"
                )

            call.enqueue(object : Callback<PlacesOrderBase?> {
                override fun onResponse(
                    call: Call<PlacesOrderBase?>,
                    response: Response<PlacesOrderBase?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface?.getPlacesOrder(response.body())
//                        discoverResponseInterface.loadingData(false)
                    } else {
                        discoverResponseInterface?.error(
                            activity!!.getString(R.string.server_error),
                            DISCOVER_TOPIC
                        )
//                        discoverResponseInterface.loadingData(false)
                    }
                }

                override fun onFailure(call: Call<PlacesOrderBase?>, t: Throwable) {
                    discoverResponseInterface?.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TOPIC
                    )
//                    discoverResponseInterface.loadingData(false)
                }
            })
        }
    }

    fun getDiscoverTopics() {
        discoverResponseInterface!!.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TOPIC
            )
        } else {
            val call =
                ApiClient.Instance.api.getNewDiscoverTopics(
                    "$headerPrefix ${mPrefs.accessToken}",
//                    "v5"
                )

            call.enqueue(object : Callback<DiscoverNewResponse?> {
                override fun onResponse(
                    call: Call<DiscoverNewResponse?>,
                    response: Response<DiscoverNewResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface.getDiscoverTopics(response.body())
                        discoverResponseInterface.loadingData(false)
                    } else {
                        discoverResponseInterface.error(
                            activity!!.getString(R.string.server_error),
                            DISCOVER_TOPIC
                        )
                        discoverResponseInterface.loadingData(false)
                    }
                }

                override fun onFailure(call: Call<DiscoverNewResponse?>, t: Throwable) {
                    activity?.getString(R.string.server_error)?.let {
                        discoverResponseInterface.error(
                            it,
                            DISCOVER_TOPIC
                        )
                    }
                    discoverResponseInterface.loadingData(false)
                }
            })
        }
    }

    fun getChannelsFollowPresenter(id: String, position: Int, channel: DiscoverNewChannels?) {
//        discoverResponseInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface?.error(
                activity!!.getString(R.string.internet_error),
                SearchResultPresenter.DISCOVER_TOPIC
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
//                    discoverResponseInterface?.loadingData(false)
                    discoverResponseInterface?.getChannelFollow(response.body(), position, channel)
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                    discoverResponseInterface?.loadingData(false)
                    discoverResponseInterface?.error(
                        activity!!.getString(R.string.server_error),
                        SearchResultPresenter.DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getChannelUnfollowPresenter(id: String, position: Int, channel: DiscoverNewChannels?) {
//        discoverResponseInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface?.error(
                activity!!.getString(R.string.internet_error),
                SearchResultPresenter.DISCOVER_TOPIC
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
//                    discoverResponseInterface?.loadingData(false)
                    discoverResponseInterface?.getChannelsunFollow(
                        response.body(),
                        position,
                        channel
                    )
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                    discoverResponseInterface?.loadingData(false)
                    discoverResponseInterface?.error(
                        activity!!.getString(R.string.server_error),
                        SearchResultPresenter.DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getSearchHistory() {
//        discoverResponseInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                SEARCH_HISTORY
            )
        } else {
            val call =
                ApiClient.Instance.api.getSearchHistory(
                    "$headerPrefix${mPrefs.accessToken}"
                )

            call.enqueue(object : Callback<SearchHistoryBase?> {
                override fun onResponse(
                    call: Call<SearchHistoryBase?>,
                    response: Response<SearchHistoryBase?>
                ) {
                    if (response.isSuccessful) {
//                        discoverResponseInterface?.loadingData(false)
                        discoverResponseInterface!!.getSearchHistory(response.body()!!.history)

                    } else {
//                        discoverResponseInterface?.loadingData(false)
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            SEARCH_HISTORY
                        )
                    }
                }

                override fun onFailure(call: Call<SearchHistoryBase?>, t: Throwable) {
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        SEARCH_HISTORY
                    )
                }
            })
        }
    }

    fun clearHistory() {
//        discoverResponseInterface!!.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TRENDING_REELS
            )
        } else {
            val call =
                ApiClient.Instance.api.clearHistory(
                    "$headerPrefix${mPrefs.accessToken}"
                )

            call.enqueue(object : Callback<FollowResponse?> {
                override fun onResponse(
                    call: Call<FollowResponse?>,
                    response: Response<FollowResponse?>
                ) {
                    if (response.isSuccessful) {
//                        discoverResponseInterface.loadingData(false)
                        discoverResponseInterface!!.clearHistory(response.body()!!)
//                        discoverResponseInterface!!.refreshHistory()

                    } else {
//                        discoverResponseInterface.loadingData(false)
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            DISCOVER_TRENDING_REELS
                        )
                    }
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
//                    discoverResponseInterface.loadingData(false)
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TRENDING_REELS
                    )
                }
            })
        }
    }

    fun deleteHistory(id: String) {
//        discoverResponseInterface!!.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TRENDING_REELS
            )
        } else {
            val call =
                ApiClient.Instance.api.deleteHistory(
                    "$headerPrefix${mPrefs.accessToken}", id
                )

            call.enqueue(object : Callback<DeleteHistory?> {
                override fun onResponse(
                    call: Call<DeleteHistory?>,
                    response: Response<DeleteHistory?>
                ) {
                    if (response.isSuccessful) {
//                        discoverResponseInterface.loadingData(false)
                        discoverResponseInterface!!.deleteHistory(response.body()!!)
                        discoverResponseInterface!!.refreshHistory()

                    } else {
//                        discoverResponseInterface.loadingData(false)
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            DISCOVER_TRENDING_REELS
                        )
                    }
                }

                override fun onFailure(call: Call<DeleteHistory?>, t: Throwable) {
//                    discoverResponseInterface.loadingData(false)
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TRENDING_REELS
                    )
                }
            })
        }
    }

    open fun getPlacesReels(query: String, page: String?) {
//        discoverResponseInterface?.loadingData(true)
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface?.error(
                activity!!.getString(R.string.internet_error),
                "places reels error"
            )
        } else {
            val call = ApiClient
                .getInstance(activity)
                .api
                .searchReelsContext(
                    "Bearer " + mPrefs.accessToken,
                    "india",
                    "TE9DQVRJT04qKioqKkluZGlh",
                    page
                )
            call.enqueue(object : Callback<ReelResponse?> {
                override fun onResponse(
                    call: Call<ReelResponse?>,
                    response: Response<ReelResponse?>
                ) {
                    discoverResponseInterface?.loadingData(false)
//                    discoverResponseInterface.onReelSuccess(response.body())
                }

                override fun onFailure(call: Call<ReelResponse?>, t: Throwable) {
                    discoverResponseInterface?.loadingData(false)
                    discoverResponseInterface?.error(
                        activity!!.getString(R.string.server_error),
                        "reels error"
                    )
                }
            })
        }
    }

    fun getDiscoverReels(context: String) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TRENDING_REELS
            )
        } else {
            val call =
                ApiClient.Instance.api.getNewDiscoverDetails(
                    "$headerPrefix${mPrefs.accessToken}",
                    context
                )

            call.enqueue(object : Callback<DiscoverDetailsResponse?> {
                override fun onResponse(
                    call: Call<DiscoverDetailsResponse?>,
                    response: Response<DiscoverDetailsResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface!!.getDiscoverReels(response.body()!!.discover.reels)
                    } else {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            DISCOVER_TRENDING_REELS
                        )
                    }
                }

                override fun onFailure(call: Call<DiscoverDetailsResponse?>, t: Throwable) {
//                    discoverResponseInterface.loadingData(false)
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TRENDING_REELS
                    )
                }
            })
        }
    }


    fun getDiscoverTrendingTopics(context: String) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TRENDING_TOPICS
            )
        } else {
            val call =
                ApiClient.Instance.api.getNewDiscoverDetails(
                    "$headerPrefix${mPrefs.accessToken}",
                    context
                )

            call.enqueue(object : Callback<DiscoverDetailsResponse?> {
                override fun onResponse(
                    call: Call<DiscoverDetailsResponse?>,
                    response: Response<DiscoverDetailsResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface!!.getDiscoverTrendingTopics(response.body()!!.discover.topics)
                    } else {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            DISCOVER_TRENDING_TOPICS
                        )
                    }
                }

                override fun onFailure(call: Call<DiscoverDetailsResponse?>, t: Throwable) {
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TRENDING_TOPICS
                    )
                }
            })
        }
    }

    fun getDiscoverTrendingNews(context: String) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TRENDING_ARTICLES
            )
        } else {
            val call =
                ApiClient.Instance.api.getNewDiscoverDetails(
                    "$headerPrefix${mPrefs.accessToken}",
                    context
                )

            call.enqueue(object : Callback<DiscoverDetailsResponse?> {
                override fun onResponse(
                    call: Call<DiscoverDetailsResponse?>,
                    response: Response<DiscoverDetailsResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface!!.getDiscoverTrendingNews(response.body()!!.discover.trendingNews)
                    } else {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            DISCOVER_TRENDING_ARTICLES
                        )
                    }
                }

                override fun onFailure(call: Call<DiscoverDetailsResponse?>, t: Throwable) {
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TRENDING_ARTICLES
                    )
                }
            })
        }
    }

    fun getDiscoverTrendingChannels(context: String) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TRENDING_CHANNELS
            )
        } else {
            val call =
                ApiClient.Instance.api.getNewDiscoverDetails(
                    "$headerPrefix${mPrefs.accessToken}",
                    context
                )

            call.enqueue(object : Callback<DiscoverDetailsResponse?> {
                override fun onResponse(
                    call: Call<DiscoverDetailsResponse?>,
                    response: Response<DiscoverDetailsResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface!!.getDiscoverTrendingChannels(response.body()!!.discover.trendingChannels)
                    } else {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            DISCOVER_TRENDING_CHANNELS
                        )
                    }
                }

                override fun onFailure(call: Call<DiscoverDetailsResponse?>, t: Throwable) {
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TRENDING_CHANNELS
                    )
                }
            })
        }
    }

    fun getDiscoverLiveChannels(context: String) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TRENDING_LIVE_CHANNELS
            )
        } else {
            val call =
                ApiClient.Instance.api.getNewDiscoverDetails(
                    "$headerPrefix${mPrefs.accessToken}",
                    context
                )

            call.enqueue(object : Callback<DiscoverDetailsResponse?> {
                override fun onResponse(
                    call: Call<DiscoverDetailsResponse?>,
                    response: Response<DiscoverDetailsResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface!!.getDiscoverLiveChannels(response.body()!!.discover.liveChannels)
                    } else {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            DISCOVER_TRENDING_LIVE_CHANNELS
                        )
                    }
                }

                override fun onFailure(call: Call<DiscoverDetailsResponse?>, t: Throwable) {
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        DISCOVER_TRENDING_LIVE_CHANNELS
                    )
                }
            })
        }
    }

    fun getWeatherLocation(context: String) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                DISCOVER_TRENDING_LIVE_CHANNELS
            )
        } else {
            val call =
                ApiClient.Instance.api.getNewDiscoverDetails(
                    "$headerPrefix${mPrefs.accessToken}",
//                    "v5",
                    context
                )

            call.enqueue(object : Callback<DiscoverDetailsResponse?> {
                override fun onResponse(
                    call: Call<DiscoverDetailsResponse?>,
                    response: Response<DiscoverDetailsResponse?>
                ) {
                    if (response.isSuccessful) {
                        getWeatherForecast(
                            response.body()!!.discover.locations[1].address,
                            "5",
                            "yes"
                        )
                    } else {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            WEATHER_FORECAST
                        )
                    }
                }

                override fun onFailure(call: Call<DiscoverDetailsResponse?>, t: Throwable) {
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        WEATHER_FORECAST
                    )
                }
            })
        }
    }

    fun getWeatherForecast(
        location: String,
        daysCount: String,
        airQualityIndex: String
    ) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                WEATHER_FORECAST
            )
        } else {
            val call = ApiClient.Instance.api.getWeatherForecast(
                BuildConfig.WEATHER_URL,
                BuildConfig.WEATHER_API_TOKEN,
                location,
                daysCount,
                airQualityIndex
            )

            call.enqueue(object : Callback<WeatherForecastResponse?> {
                override fun onResponse(
                    call: Call<WeatherForecastResponse?>,
                    response: Response<WeatherForecastResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface!!.getWeatherForecast(response.body())
                    } else {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            WEATHER_FORECAST
                        )
                    }
                }

                override fun onFailure(call: Call<WeatherForecastResponse?>, t: Throwable) {
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        WEATHER_FORECAST
                    )
                }
            })
        }
    }

    fun getTradingItemsList() {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                TRADING_ITEMS_LIST
            )
        } else {
            val call = ApiClient.getInstance().api.getTradingItemsList(mPrefs.accessToken)
            call.enqueue(object : Callback<TradingIconsResponse?> {
                override fun onResponse(
                    call: Call<TradingIconsResponse?>,
                    response: Response<TradingIconsResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface!!.getTradingItemsList(response.body())
                    } else {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            TRADING_ITEMS_LIST
                        )
                    }
                }

                override fun onFailure(call: Call<TradingIconsResponse?>, t: Throwable) {

                    Log.e("ERROR_TAG", "onFailure: ${t.localizedMessage}")
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        TRADING_ITEMS_LIST
                    )
                }
            })
        }
    }

    fun getCryptoPrices(url: String, header: String) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                CRYPTO
            )
        } else {
            val call = ApiClient.getInstance().api.getCryptoPrices(url, header)
            call.enqueue(object : Callback<CryptoForexApiResponse?> {
                override fun onResponse(
                    call: Call<CryptoForexApiResponse?>,
                    response: Response<CryptoForexApiResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface!!.getCryptoPrices(response.body())
                    } else {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            CRYPTO
                        )
                    }
                }

                override fun onFailure(call: Call<CryptoForexApiResponse?>, t: Throwable) {

                    Log.e("ERROR_TAG", "onFailure: ${t.localizedMessage}")
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        CRYPTO
                    )
                }
            })
        }
    }

    fun getForexPrices(url: String, header: String) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                FOREX
            )
        } else {
            ApiClient.getInstance().api.getForexPrices(url, header).apply {
                enqueue(object : Callback<CryptoForexApiResponse?> {
                    override fun onResponse(
                        call: Call<CryptoForexApiResponse?>,
                        response: Response<CryptoForexApiResponse?>
                    ) {
                        if (response.isSuccessful) {
                            discoverResponseInterface!!.getForexPrices(response.body())
                        } else {
                            discoverResponseInterface!!.error(
                                activity!!.getString(R.string.server_error),
                                FOREX
                            )
                        }
                    }

                    override fun onFailure(call: Call<CryptoForexApiResponse?>, t: Throwable) {
                        discoverResponseInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            FOREX
                        )
                    }
                })
            }
        }
    }


    fun getLiveScore(category:String,date: String) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface!!.error(
                activity!!.getString(R.string.internet_error),
                LIVE_SCORE
            )
        } else {
            val call = ApiClient.Instance.api.getLiveScore(
                "https://livescore6.p.rapidapi.com/matches/v2/list-by-date",
                "6bf4026ecbmshc32c080f12c325dp1ca5f5jsn2ebfe99fd6b3",
                "livescore6.p.rapidapi.com",
                category,
                date,
                "4"
            )

            call.enqueue(object : Callback<LiveScoreApiResponse?> {
                override fun onResponse(
                    call: Call<LiveScoreApiResponse?>,
                    response: Response<LiveScoreApiResponse?>
                ) {
                    if (response.isSuccessful) {
                        discoverResponseInterface?.getLiveScore(category ,response.body())
                    } else {
                        discoverResponseInterface?.error(
                            activity!!.getString(R.string.server_error),
                            LIVE_SCORE
                        )
                    }
                }

                override fun onFailure(call: Call<LiveScoreApiResponse?>, t: Throwable) {
                    discoverResponseInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        LIVE_SCORE
                    )
                }
            })
        }
    }

    fun getTopicsFollowPresenter(id: String, position: Int, topic: DiscoverNewTopic?) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface?.error(
                activity!!.getString(R.string.internet_error),
                SearchResultPresenter.DISCOVER_TOPIC
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
                    discoverResponseInterface?.getTopicsFollow(response.body(), position, topic)
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
                    discoverResponseInterface?.error(
                        activity!!.getString(R.string.server_error),
                        SearchResultPresenter.DISCOVER_TOPIC
                    )
                }
            })
        }
    }

    fun getTopicsUnfollowPresenter(id: String, position: Int, topic: DiscoverNewTopic?) {
        if (!InternetCheckHelper.isConnected()) {
            discoverResponseInterface?.error(
                activity!!.getString(R.string.internet_error),
                SearchResultPresenter.DISCOVER_TOPIC
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
                    discoverResponseInterface?.getTopicsFollow(response.body(), position, topic)
                }

                override fun onFailure(call: Call<FollowResponse?>, t: Throwable) {
                    discoverResponseInterface?.error(
                        activity!!.getString(R.string.server_error),
                        SearchResultPresenter.DISCOVER_TOPIC
                    )
                }
            })
        }
    }
}