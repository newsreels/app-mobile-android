package com.ziro.bullet.model.searchresultnew

import com.google.gson.annotations.SerializedName
data class Locations (

	@SerializedName("id") val id : String,
	@SerializedName("context") val context : String,
	@SerializedName("name") val name : String,
	@SerializedName("image") val image : String,
	@SerializedName("flag") val flag : String,
	@SerializedName("city") val city : String,
	@SerializedName("country") val country : String,
	@SerializedName("favorite") val favorite : Boolean
)