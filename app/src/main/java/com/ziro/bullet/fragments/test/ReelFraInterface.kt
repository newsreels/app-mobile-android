package com.ziro.bullet.fragments.test

import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.model.Reel.ReelsItem

interface ReelFraInterface {
    fun loadingReelInfo(isLoading: Boolean)
    fun errorr(error: String, topic: String)

    fun followChannelClick(reelsItem:ReelsItem)
    fun likeicon(reelsItem:ReelsItem)
    fun commentsclick(reelsItem:ReelsItem)
    fun share(reelsItem:ReelsItem)
    fun dotsCkickOpen(reelsItem:ReelsItem)
    fun openReelviewmore(reelsItem:ReelsItem)
    fun doubleClickLike(reelsItem:ReelsItem)
    fun userIconClick(reelsItem:ReelsItem)
    fun nextReelVideo(pos:Int)
    fun updateView();


//    fun onItemClick(position: Int, item: Source?)
}