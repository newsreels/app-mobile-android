package com.ziro.bullet.fragments.searchNew.channelpackage

import com.ziro.bullet.data.models.sources.Source

interface SearchChannelInterface {

    fun onItemClick(position: Int, item: Source?)
    fun onItemClickFollow(position: Int, item: Source?)


    //channel
    fun onItemChannelFollowed2(channel: Source?, position: Int)
    fun onItemChannleUnfollowed2(channel: Source?, position: Int)

}