package com.ziro.bullet.fragments.searchNew.interfaces

import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.model.Menu.CategoryResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewReels
import com.ziro.bullet.model.discoverNew.DiscoverNewResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.searchresultnew.SearchresultdataBase
import com.ziro.bullet.model.topicnew.TopicResponse
import com.ziro.bullet.model.topicresponsenew.TopicResponseBase
import okhttp3.ResponseBody

interface SearchResultsViewInterface {

    fun loadingData(isLoading: Boolean)

    fun error(error: String, topic: String)

    fun getSearchTopics(response: DiscoverNewResponse?)

    fun getSearchResult(response: SearchresultdataBase?)

    fun getSearchReels(reelsResponse: List<DiscoverNewReels>?)

    fun searchChannelSuccess(response: SourceModel?, isPagination: Boolean)
    fun searchLocationSuccess(response: LocationModel?, isPagination: Boolean)

    fun getTopicsFollow(response: FollowResponse?, position: Int,topic: DiscoverNewTopic?)
    fun getTopicsunFollow(response: FollowResponse?, position: Int,topic: DiscoverNewTopic?)

    fun getChannelFollow(response: FollowResponse?, position: Int,topic: DiscoverNewChannels?)
    fun getChannelsunFollow(response: FollowResponse?, position: Int,topic: DiscoverNewChannels?)

    fun getChannelFollow2(response: FollowResponse?, position: Int,topic: Source?)
    fun getChannelsunFollow2(response: FollowResponse?, position: Int,topic: Source?)

    fun getLocationFollow2(response: FollowResponse?, position: Int,topic: Location?)
    fun getLocationunFollow2(response: FollowResponse?, position: Int,topic: Location?)

}