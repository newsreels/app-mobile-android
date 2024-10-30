package com.newsreels.app.following.domain.repository

import com.newsreels.app.data.models.location.LocationModel
import com.newsreels.app.data.models.sources.SourceModel
import com.newsreels.app.data.models.topics.TopicsModel
import com.newsreels.app.model.FollowResponse
import retrofit2.Callback

interface FollowingRepo {

    fun getFollowingTopics(page: String, callback: Callback<TopicsModel>)

    fun getFollowingLocations(page: String, callback: Callback<LocationModel>)

    fun getFollowingChannels(page: String, callback: Callback<SourceModel>)

    fun getSuggestedTopics(callback: Callback<TopicsModel>)

    fun getSuggestedChannels(hasReels: Boolean, callback: Callback<SourceModel>)

    fun getSuggestedLocations(callback: Callback<LocationModel>)

    fun followChannel(id: String, callback: Callback<FollowResponse>)

    fun unFollowChannel(id: String, callback: Callback<FollowResponse>)

    fun followTopics(id: String, callback: Callback<FollowResponse>)

    fun unFollowTopic(id: String, callback: Callback<FollowResponse>)

    fun followLocation(id: String, callback: Callback<FollowResponse>)

    fun unFollowLocation(id: String, callback: Callback<FollowResponse>)

}