package com.newsreels.app.fragments.test

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.newsreels.app.model.Reel.ReelsItem

class VideoItemRes(
    var exoPlayerVar: SimpleExoPlayer,
    var exoPosition: Int,
    var reelsItem: ReelsItem
)