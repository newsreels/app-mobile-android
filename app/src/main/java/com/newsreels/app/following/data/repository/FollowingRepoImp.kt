package com.newsreels.app.following.data.repository

import android.app.Activity
import com.newsreels.app.APIResources.ApiClient
import com.newsreels.app.data.PrefConfig
import com.newsreels.app.data.models.location.LocationModel
import com.newsreels.app.data.models.sources.SourceModel
import com.newsreels.app.data.models.topics.TopicsModel
import com.newsreels.app.following.domain.repository.FollowingRepo
import com.newsreels.app.model.FollowResponse
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