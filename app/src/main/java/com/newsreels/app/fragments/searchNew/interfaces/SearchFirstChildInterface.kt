package com.newsreels.app.fragments.searchNew.interfaces

import com.newsreels.app.data.models.location.Location
import com.newsreels.app.model.Reel.ReelsItem
import com.newsreels.app.model.articles.Article
import com.newsreels.app.model.discoverNew.DiscoverNewChannels
import com.newsreels.app.model.discoverNew.DiscoverNewTopic


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