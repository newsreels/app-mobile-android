package com.ziro.bullet.model.searchresultnew

import com.google.gson.annotations.SerializedName
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewReels
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.searchResult.*
import com.ziro.bullet.data.models.location.Location

data class SearchresultData (

	@SerializedName("title") val title : String,
	@SerializedName("type") val type : String,
	@SerializedName("topics") val topics : List<DiscoverNewTopic>,
	@SerializedName("sources") val sources : List<DiscoverNewChannels>,
	@SerializedName("locations") val locations : List<Location>,
	@SerializedName("articles") val articles : List<Article>,
	@SerializedName("authors") val authors : List<Authors>,
	@SerializedName("reels") val reels : List<ReelsItem>,
	@SerializedName("search") val search : Search
)