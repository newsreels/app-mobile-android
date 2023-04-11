package com.ziro.bullet.following.data.repository

import android.app.Activity
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.data.models.topics.TopicsModel
import com.ziro.bullet.following.domain.repository.FollowingRepo
import com.ziro.bullet.model.FollowResponse
import retrofit2.Callback

class FollowingRepoImp(private val activity: Activity) :
    FollowingRepo {

    var apiClient: ApiClient = ApiClient.getInstance(activity)
    var prefs = PrefConfig(activity.baseContext)

    override fun getFollowingTopics(page: String, callback: Callback<TopicsModel>) {
        val call = apiClient.api.getFollowingTopics("Bearer " + prefs.accessToken, page)
        call.enqueue(callback)
    }

    override fun getFollowingLocations(page: String, callback: Callback<LocationModel>) {
        val call = apiClient.api.getFollowedLocation("Bearer " + prefs.accessToken, page)
        call.enqueue(callback)
    }

    override fun getFollowingChannels(page: String, callback: Callback<SourceModel>) {
        val call = apiClient.api.getFollowingSources("Bearer " + prefs.accessToken, page)
        call.enqueue(callback)
    }

    override fun getSuggestedTopics(callback: Callback<TopicsModel>) {
        val call = apiClient.api.getSuggestedTopics("Bearer " + prefs.accessToken, false)
        call.enqueue(callback)
    }

    override fun getSuggestedChannels(hasReels: Boolean, callback: Callback<SourceModel>) {
        val call = apiClient.api.getSuggestedChannels("Bearer " + prefs.accessToken, hasReels)
        call.enqueue(callback)
    }

    override fun getSuggestedLocations(callback: Callback<LocationModel>) {
        val call = apiClient.api.getSuggestedLocations("Bearer " + prefs.accessToken)
        call.enqueue(callback)
    }

    override fun followChannel(id: String, callback: Callback<FollowResponse>) {
        val call = apiClient.api.followSources("Bearer " + prefs.accessToken, id)
        call.enqueue(callback)
    }

    override fun unFollowChannel(id: String, callback: Callback<FollowResponse>) {
        val call = apiClient.api.unfollowSourcesNew("Bearer " + prefs.accessToken, id)
        call.enqueue(callback)
    }

    override fun followTopics(id: String, callback: Callback<FollowResponse>) {
        val call = apiClient.api.addTopicnew("Bearer " + prefs.accessToken, id)
        call.enqueue(callback)
    }

    override fun unFollowTopic(id: String, callback: Callback<FollowResponse>) {
        val call = apiClient.api.unfollowTopicNew("Bearer " + prefs.accessToken, id)
        call.enqueue(callback)
    }

    override fun followLocation(id: String, callback: Callback<FollowResponse>) {
        val call = apiClient.api.followLocationNew("Bearer " + prefs.accessToken, id)
        call.enqueue(callback)
    }

    override fun unFollowLocation(id: String, callback: Callback<FollowResponse>) {
        val call = apiClient.api.unfollowLocationNew("Bearer " + prefs.accessToken, id)
        call.enqueue(callback)
    }


}