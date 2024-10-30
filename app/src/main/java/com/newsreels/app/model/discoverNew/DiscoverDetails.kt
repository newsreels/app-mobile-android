package com.newsreels.app.model.discoverNew

import com.google.gson.annotations.SerializedName
import com.newsreels.app.model.Reel.ReelsItem
import com.newsreels.app.model.articles.Article

data class DiscoverDetails(
    val large: Boolean,
    val topics: List<DiscoverNewTopic>,
    val reels: List<ReelsItem>,
    @SerializedName("sources") val trendingChannels: List<DiscoverNewChannels>,
    @SerializedName("channels") val liveChannels: List<DiscoverNewLiveChannels>,
    @SerializedName("locations") val locations: List<WeatherLocation>,
    @SerializedName("articles") val trendingNews: List<Article>
)