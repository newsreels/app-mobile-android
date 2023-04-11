package com.ziro.bullet.model.places

import com.google.gson.annotations.SerializedName

data class PlacesOrderBase (

	@SerializedName("search") val search : List<Search>
)