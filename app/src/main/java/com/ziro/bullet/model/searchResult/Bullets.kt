package com.ziro.bullet.model.searchResult

import com.google.gson.annotations.SerializedName

data class Bullets (

	@SerializedName("data") val data : String,
	@SerializedName("audio") val audio : String,
	@SerializedName("duration") val duration : Int,
	@SerializedName("image") val image : String
)