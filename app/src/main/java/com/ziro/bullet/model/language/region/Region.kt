package com.ziro.bullet.model.language.region

data class Region(
    val city: String,
    val context: String,
    val country: String,
    var favorite: Boolean,
    val flag: String,
    val id: String,
    val image: String,
    val name: String
)