package com.ziro.bullet.model.discoverNew

data class DiscoverNewTopic(
    val context: String,
    var favorite: Boolean? = false,
    val icon: String,
    val id: String,
    val image: String,
    val name: String
)