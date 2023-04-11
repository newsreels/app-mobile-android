package com.ziro.bullet.model.searchResult

import com.google.gson.annotations.SerializedName
data class Authors (

	@SerializedName("id") val id : String,
	@SerializedName("context") val context : String,
	@SerializedName("username") val username : String,
	@SerializedName("first_name") val first_name : String,
	@SerializedName("last_name") val last_name : String,
	@SerializedName("profile_image") val profile_image : String,
	@SerializedName("cover_image") val cover_image : String,
	@SerializedName("language") val language : String,
	@SerializedName("follower_count") val follower_count : Int,
	@SerializedName("post_count") val post_count : Int,
	@SerializedName("favorite") val favorite : Boolean,
	@SerializedName("verified") val verified : Boolean
)