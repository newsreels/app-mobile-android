package com.ziro.bullet.model.searchResult

import com.google.gson.annotations.SerializedName
data class Sources (
	@SerializedName("id") val id : String,
	@SerializedName("context") val context : String,
	@SerializedName("name") val name : String,
	@SerializedName("description") val description : String,
	@SerializedName("link") val link : String,
	@SerializedName("icon") val icon : String,
	@SerializedName("image") val image : String,
	@SerializedName("name_image") val name_image : String,
	@SerializedName("language") val language : String,
	@SerializedName("category") val category : String,
	@SerializedName("follower_count") val follower_count : Int,
	@SerializedName("favorite") val favorite : Boolean,
	@SerializedName("verified") val verified : Boolean
)