package com.newsreels.app.model.places

import com.google.gson.annotations.SerializedName

data class PlacesOrderBase (

	@SerializedName("search") val search : List<Search>
)