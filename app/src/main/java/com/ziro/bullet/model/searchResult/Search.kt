package com.ziro.bullet.model.searchResult

import com.google.gson.annotations.SerializedName
data class Search (

	@SerializedName("id") val id : String,
	@SerializedName("search") val search : String
)