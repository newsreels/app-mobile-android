package com.ziro.bullet.fragments.searchNew.interfaces

import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic


interface SearchFirstChildInterface {
    //reels
    fun searchChildOnClick(reelsList: List<ReelsItem>)
    fun searchChildSecondOnClick(reelsItem: ReelsItem, reelsList: List<ReelsItem>, position: Int)

    //article

    fun searchChildOnArticleClick(trendingTopics: List<Article>)
    fun searchChildArticleSecondOnClick(trendingTopics: Article)

    //channel
    fun searchChildOnChannelClick(trendingChannels: List<DiscoverNewChannels>)
    fun searchChildChannelSecondOnClick(discoverNewChannels: DiscoverNewChannels)

    //topics
    fun searchChildOnTopicClick(trendingTopics: List<DiscoverNewTopic>)
    fun searchChildTopicSecondOnClick(trendingTopic: DiscoverNewTopic)

    fun onItemFollowed(topic: DiscoverNewTopic?, position: Int)

    fun onItemUnfollowed(topic: DiscoverNewTopic?, position: Int)

    //channel
    fun onItemChannelFollowed(channel: DiscoverNewChannels?, position: Int)

    fun onItemChannleUnfollowed(channel: DiscoverNewChannels?, position: Int)


    fun searchChildLocationSecondOnClick(location: Location)

    //location
    fun searchChildOnPlacesClick(locationlist: List<Location>)
    fun onItemFollowedLoc(topic: Location?, position: Int)

    fun onItemUnfollowedLoc(topic: Location?, position: Int)


}