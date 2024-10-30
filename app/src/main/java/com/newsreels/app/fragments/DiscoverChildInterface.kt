package com.newsreels.app.fragments

import com.newsreels.app.model.Reel.ReelsItem
import com.newsreels.app.model.discoverNew.DiscoverNew
import com.newsreels.app.model.discoverNew.DiscoverNewChannels
import com.newsreels.app.model.discoverNew.DiscoverNewLiveChannels
import com.newsreels.app.model.discoverNew.DiscoverNewTopic
import com.newsreels.app.model.discoverNew.liveScore.SportEvent
import com.newsreels.app.model.discoverNew.trading.CryptoForexApiResponse
import com.newsreels.app.model.discoverNew.trading.Ticker
import com.newsreels.app.model.discoverNew.weather.WeatherForecastResponse

interface DiscoverChildInterface {
    fun loadItem(discoverItem: DiscoverNew)

    //Reel
    fun searchChildOnClick(reelsList: List<ReelsItem>)
    fun searchChildSecondOnClick(reelsItem: ReelsItem, reelsList: List<ReelsItem>, position: Int)

    //Topic
    fun searchChildOnTopicClick(trendingTopics: List<DiscoverNewTopic>)
    fun searchChildTopicSecondOnClick(trendingTopic: DiscoverNewTopic)
    fun onTopicFollowed(topic: DiscoverNewTopic?, position: Int)
    fun onTopicUnfollowed(topic: DiscoverNewTopic?, position: Int)

    //Channel
    fun searchChildOnChannelClick(trendingChannels: List<DiscoverNewChannels>)
    fun searchChildChannelSecondOnClick(discoverNewChannels: DiscoverNewChannels)


    //channel
    fun onItemChannelFollowed(channel: DiscoverNewChannels?, position: Int)
    fun onItemChannelUnfollowed(channel: DiscoverNewChannels?, position: Int)
    fun discoverLiveChannelClick(liveChannels: DiscoverNewLiveChannels)


    //Article
    fun onArticleSeeAll()

    //crypto
    fun onCryptoSeeAllClick(cryptoForexApiResponse: CryptoForexApiResponse?)
    fun onForexSeeAllClick(cryptoForexApiResponse: CryptoForexApiResponse?)


    //weather
    fun searchChildWeatherSecondOnClick(forecast: WeatherForecastResponse)

    //sports
    fun updateSports(category:String)
    fun moveSportsDetailPage(sportCategory: String,liveEvent: SportEvent) //get the eid from this
}