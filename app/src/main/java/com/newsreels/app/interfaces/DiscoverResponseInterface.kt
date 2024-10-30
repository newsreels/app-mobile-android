package com.newsreels.app.interfaces

import com.newsreels.app.model.FollowResponse
import com.newsreels.app.model.Reel.ReelsItem
import com.newsreels.app.model.articlenew.ArticleBase
import com.newsreels.app.model.articles.Article
import com.newsreels.app.model.discoverNew.DiscoverNewChannels
import com.newsreels.app.model.discoverNew.DiscoverNewLiveChannels
import com.newsreels.app.model.discoverNew.DiscoverNewResponse
import com.newsreels.app.model.discoverNew.DiscoverNewTopic
import com.newsreels.app.model.discoverNew.liveScore.LiveScoreApiResponse
import com.newsreels.app.model.discoverNew.trading.CryptoForexApiResponse
import com.newsreels.app.model.discoverNew.trading.icons.TradingIconsResponse
import com.newsreels.app.model.discoverNew.weather.WeatherForecastResponse
import com.newsreels.app.model.places.PlacesOrderBase
import com.newsreels.app.model.searchhistory.History
import com.newsreels.app.model.searchhistorydelete.DeleteHistory

interface DiscoverResponseInterface {
    fun getChannelFollow(response: FollowResponse?, position: Int, topic: DiscoverNewChannels?)
    fun getChannelsunFollow(response: FollowResponse?, position: Int, topic: DiscoverNewChannels?)
    fun loadingData(isLoading: Boolean)

    fun error(error: String, topic: String)

    fun getDiscoverTopics(response: DiscoverNewResponse?)
    fun getPlacesOrder(response: PlacesOrderBase?)

    fun getDiscoverReels(reelsResponse: List<ReelsItem>?)

    fun refreshHistory()
    fun onSearchArticleSuccess(response: ArticleBase?, pagination: Boolean)

    fun getSearchHistory(reelsResponse: List<History>?)

    fun deleteHistory(deletehistory: DeleteHistory?)
    fun clearHistory(clearhistory: FollowResponse?)

    fun getDiscoverTrendingTopics(topicsResponse: List<DiscoverNewTopic>?)

    fun getDiscoverTrendingNews(trendingNews: List<Article>?)

    fun getDiscoverTrendingChannels(trendingChannels: List<DiscoverNewChannels>?)

    fun getDiscoverLiveChannels(liveChannels: List<DiscoverNewLiveChannels>?)

    fun getWeatherForecast(weatherForecastResponse: WeatherForecastResponse?)

    fun getTradingItemsList(tradingIconsResponse: TradingIconsResponse?)

    fun getCryptoPrices(cryptoForexApiResponse: CryptoForexApiResponse?)

    fun getForexPrices(cryptoForexApiResponse: CryptoForexApiResponse?)

    fun getLiveScore(category: String, liveScoreApiResponse: LiveScoreApiResponse?)

    fun getTopicsFollow(body: FollowResponse?, position: Int, topic: DiscoverNewTopic?)

}