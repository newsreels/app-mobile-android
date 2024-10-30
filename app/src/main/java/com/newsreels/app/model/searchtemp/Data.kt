package com.newsreels.app.model.searchtemp

import com.newsreels.app.model.discoverNew.DiscoverNewReels
import com.newsreels.app.model.discoverNew.DiscoverNewTopic

data class Data(
    val reels: List<DiscoverNewReels>,
    val topics: List<DiscoverNewTopic>,
    var `data`: List<DataX>,
    val title: String,
    val type: String
)