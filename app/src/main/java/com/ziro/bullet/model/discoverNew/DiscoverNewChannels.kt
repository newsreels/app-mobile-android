package com.ziro.bullet.model.discoverNew

data class DiscoverNewChannels(
    val category: String,
    val context: String,
    val description: String,
    var favorite: Boolean,
    val follower_count: Int,
    val icon: String,
    val id: String,
    val image: String,
    val language: String,
    val link: String,
    val name: String,
    val name_image: String,
    val verified: Boolean
)