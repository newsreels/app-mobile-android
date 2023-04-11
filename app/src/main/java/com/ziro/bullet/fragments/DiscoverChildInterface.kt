package com.ziro.bullet.fragments

import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.discoverNew.DiscoverNew
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewLiveChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.discoverNew.liveScore.SportEvent
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.trading.Ticker
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse

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