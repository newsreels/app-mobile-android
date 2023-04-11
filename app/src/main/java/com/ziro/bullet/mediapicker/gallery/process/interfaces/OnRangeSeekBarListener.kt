package com.video.trimmer.interfaces

import com.ziro.bullet.mediapicker.gallery.process.RangeSeekBarView

interface OnRangeSeekBarListener {
    fun onCreate(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float)
    fun onSeek(
        rangeSeekBarView: RangeSeekBarView,
        index: Int,
        value: Float,
        left: Float,
        right: Float
    )
    fun onSeekStart(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float)
    fun onSeekStop(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float)
}