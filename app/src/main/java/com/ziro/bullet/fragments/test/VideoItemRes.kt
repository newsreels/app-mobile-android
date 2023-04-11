package com.ziro.bullet.fragments.test

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.ziro.bullet.model.Reel.ReelsItem

class VideoItemRes(
    var exoPlayerVar: SimpleExoPlayer,
    var exoPosition: Int,
    var reelsItem: ReelsItem
)