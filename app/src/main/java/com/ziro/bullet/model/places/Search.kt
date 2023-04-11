package com.ziro.bullet.model.places

import com.google.gson.annotations.SerializedName
data class Search (

	@SerializedName("title") val title : String,
	@SerializedName("type") val type : String,
	@SerializedName("context") val context : String
)