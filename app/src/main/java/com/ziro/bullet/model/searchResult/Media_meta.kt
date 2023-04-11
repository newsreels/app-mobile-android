package com.ziro.bullet.model.searchResult

import com.google.gson.annotations.SerializedName
data class Media_meta (

	@SerializedName("duration") val duration : Int,
	@SerializedName("height") val height : Int,
	@SerializedName("width") val width : Int
)