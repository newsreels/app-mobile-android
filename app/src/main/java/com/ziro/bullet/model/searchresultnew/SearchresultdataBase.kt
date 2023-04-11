package com.ziro.bullet.model.searchresultnew

import com.google.gson.annotations.SerializedName


data class SearchresultdataBase (
	@SerializedName("search") val search : Search,
	@SerializedName("data") val data : List<SearchresultData>
)