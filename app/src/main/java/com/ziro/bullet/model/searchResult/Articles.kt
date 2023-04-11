package com.ziro.bullet.model.searchResult

import com.google.gson.annotations.SerializedName
data class Articles (

	@SerializedName("id") val id : String,
	@SerializedName("title") val title : String,
	@SerializedName("image") val image : String,
	@SerializedName("link") val link : String,
	@SerializedName("original_link") val original_link : String,
	@SerializedName("publish_time") val publish_time : String,
	@SerializedName("source") val source : Source,
	@SerializedName("topics") val topics : String,
	@SerializedName("bullets") val bullets : List<Bullets>,
	@SerializedName("mute") val mute : Boolean,
	@SerializedName("type") val type : String,
	@SerializedName("info") val info : Info,
	@SerializedName("language") val language : String,
	@SerializedName("authors") val authors : List<String>
)