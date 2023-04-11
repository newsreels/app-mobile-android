package com.ziro.bullet.model.searchResult

import com.google.gson.annotations.SerializedName

data class Topics (

	@SerializedName("id") val id : String,
	@SerializedName("context") val context : String,
	@SerializedName("name") val name : String,
	@SerializedName("icon") val icon : String,
	@SerializedName("image") val image : String,
	@SerializedName("color") val color : String
)