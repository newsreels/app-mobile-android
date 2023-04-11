package com.ziro.bullet.model.searchhistory

import com.google.gson.annotations.SerializedName
data class History (

	@SerializedName("id") val id : String,
	@SerializedName("user_id") val user_id : String,
	@SerializedName("search") val search : String
)