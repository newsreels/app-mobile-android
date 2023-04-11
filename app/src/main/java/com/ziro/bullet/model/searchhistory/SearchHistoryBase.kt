package com.ziro.bullet.model.searchhistory

import com.google.gson.annotations.SerializedName
data class SearchHistoryBase (

	@SerializedName("history") val history : List<History>
)