package com.ziro.bullet.model.channelnew

data class Source(
    val category: String,
    val context: String,
    val description: String,
    val favorite: Boolean,
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