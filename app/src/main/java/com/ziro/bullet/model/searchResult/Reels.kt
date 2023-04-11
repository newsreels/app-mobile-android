package com.ziro.bullet.model.searchResult

import com.google.gson.annotations.SerializedName

data class Reels (

	@SerializedName("id") val id : String,
	@SerializedName("context") val context : String,
	@SerializedName("description") val description : String,
	@SerializedName("image") val image : String,
	@SerializedName("link") val link : String,
	@SerializedName("media") val media : String,
	@SerializedName("media_landscape") val media_landscape : String,
	@SerializedName("media_meta") val media_meta : Media_meta,
	@SerializedName("publish_time") val publish_time : String,
	@SerializedName("source") val source : Source,
	@SerializedName("info") val info : Info,
	@SerializedName("authors") val authors : List<String>,
	@SerializedName("language") val language : String
)