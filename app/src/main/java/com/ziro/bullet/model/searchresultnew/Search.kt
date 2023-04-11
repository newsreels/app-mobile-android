package com.ziro.bullet.model.searchresultnew

import com.google.gson.annotations.SerializedName

data class Search (

	@SerializedName("id") val id : String,
	@SerializedName("search") val search : String
)