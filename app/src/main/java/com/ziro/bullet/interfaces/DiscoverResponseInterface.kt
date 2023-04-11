package com.ziro.bullet.interfaces

import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articlenew.ArticleBase
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewLiveChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.discoverNew.liveScore.LiveScoreApiResponse
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.trading.icons.TradingIconsResponse
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import com.ziro.bullet.model.places.PlacesOrderBase
import com.ziro.bullet.model.searchhistory.History
import com.ziro.bullet.model.searchhistorydelete.DeleteHistory

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