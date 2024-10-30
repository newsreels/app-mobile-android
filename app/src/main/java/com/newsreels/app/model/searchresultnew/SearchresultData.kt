package com.newsreels.app.model.searchresultnew

import com.google.gson.annotations.SerializedName
import com.newsreels.app.model.Reel.ReelsItem
import com.newsreels.app.model.articles.Article
import com.newsreels.app.model.discoverNew.DiscoverNewChannels
import com.newsreels.app.model.discoverNew.DiscoverNewReels
import com.newsreels.app.model.discoverNew.DiscoverNewTopic
import com.newsreels.app.model.searchResult.*
import com.newsreels.app.data.models.location.Location

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