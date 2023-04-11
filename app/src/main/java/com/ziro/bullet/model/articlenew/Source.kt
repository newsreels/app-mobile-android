package com.ziro.bullet.model.articlenew
import com.google.gson.annotations.SerializedName

data class Source (

	@SerializedName("id") val id : String,
	@SerializedName("context") val context : String,
	@SerializedName("name") val name : String,
	@SerializedName("link") val link : String,
	@SerializedName("icon") val icon : String,
	@SerializedName("name_image") val name_image : String
)