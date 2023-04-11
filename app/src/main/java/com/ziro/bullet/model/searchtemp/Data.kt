package com.ziro.bullet.model.searchtemp

import com.ziro.bullet.model.discoverNew.DiscoverNewReels
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic

data class Data(
    val reels: List<DiscoverNewReels>,
    val topics: List<DiscoverNewTopic>,
    var `data`: List<DataX>,
    val title: String,
    val type: String
)