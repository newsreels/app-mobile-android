package com.ziro.bullet.model.articlenew
import com.google.gson.annotations.SerializedName
data class Info (

	@SerializedName("view_count") val view_count : Int,
	@SerializedName("views") val views : Int,
	@SerializedName("like_count") val like_count : Int,
	@SerializedName("comment_count") val comment_count : Int,
	@SerializedName("is_liked") val is_liked : Boolean
)