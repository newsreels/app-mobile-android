package com.ziro.bullet.model.searchResult

import com.google.gson.annotations.SerializedName
import com.ziro.bullet.model.discoverNew.DiscoverNewReels

data class SearchResultBase (
	@SerializedName("article_context") val article_context : String,
	@SerializedName("article_page") val article_page : Article_page,
	@SerializedName("articles") val articles : List<Articles>,
	@SerializedName("topics") val topics : List<Topics>,
	@SerializedName("sources") val sources : List<Sources>,
	@SerializedName("locations") val locations : String,
	@SerializedName("authors") val authors : List<Authors>,
	@SerializedName("reels") val reels : List<DiscoverNewReels>,
	@SerializedName("search") val search : Search
)