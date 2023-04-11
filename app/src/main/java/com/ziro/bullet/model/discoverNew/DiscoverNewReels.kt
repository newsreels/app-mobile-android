package com.ziro.bullet.model.discoverNew

import com.ziro.bullet.model.articles.Author

data class DiscoverNewReels(
    val authors: List<Author>,
    val context: String,
    val description: String,
    val id: String,
    val image: String,
    val info: Info,
    val language: String,
    val link: String,
    val media: String,
    val media_landscape: String,
    val media_meta: MediaMeta,
    val publish_time: String,
    val source: Source
)