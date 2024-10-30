package com.newsreels.app.fragments.searchNew.interfaces

import com.newsreels.app.data.models.location.Location
import com.newsreels.app.data.models.location.LocationModel
import com.newsreels.app.data.models.sources.Source
import com.newsreels.app.data.models.sources.SourceModel
import com.newsreels.app.model.FollowResponse
import com.newsreels.app.model.Menu.CategoryResponse
import com.newsreels.app.model.discoverNew.DiscoverNewChannels
import com.newsreels.app.model.discoverNew.DiscoverNewReels
import com.newsreels.app.model.discoverNew.DiscoverNewResponse
import com.newsreels.app.model.discoverNew.DiscoverNewTopic
import com.newsreels.app.model.searchresultnew.SearchresultdataBase
import com.newsreels.app.model.topicnew.TopicResponse
import com.newsreels.app.model.topicresponsenew.TopicResponseBase
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