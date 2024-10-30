package com.newsreels.app.fragments.searchNew.locationnew

import com.newsreels.app.data.models.location.Location
import com.newsreels.app.data.models.sources.Source

interface SearchLocationInterface {

    fun onItemClick(position: Int, item: Location?)
    fun onItemClickFollow(position: Int, item: Location?)


    //channel
    fun onItemLocationFollowed2(channel: Location?, position: Int)
    fun onItemLocationUnfollowed2(channel: Location?, position: Int)

}