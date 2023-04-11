package com.ziro.bullet.fragments.searchNew

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.fragments.searchNew.searchviewholder.*
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.searchresultnew.SearchresultData


private const val REELS = 0
private const val ARTICLE = 1
private const val SOURCES = 2
private const val TOPICS = 3
private const val PLACES = 4
private const val AUTHORS = 5
private const val OTHER_TRENDS = 6

class SearchableAdapterNew() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var searchItems = listOf<SearchresultData>()
    private lateinit var searchFirstChildInterface: SearchFirstChildInterface
    private lateinit var searchTopicsViewHolder: SearchTopicsViewHolder
    private lateinit var searchSourcesViewHolder: SearchSourcesViewHolder
    private lateinit var searchArticlesItemViewHolder: SearchArticlesItemViewHolder
    private lateinit var searchLocationsViewHolder: SearchLocationsViewHolder

    private var isPostArticle = false
    private var adFailedListener: AdFailedListener? = null
    private var shareToMainInterface: ShareToMainInterface? = null
    private var swipeListener: TempCategorySwipeListener? = null
    private var gotoChannelListener: OnGotoChannelListener? = null
    private var detailsActivityInterface: DetailsActivityInterface? = null
    private var goHomeMainActivity: GoHome? = null
    private var showOptionsLoaderCallback: ShowOptionsLoaderCallback? = null
    private var adapterCallback: AdapterCallback? = null
    private var mPrefConfig: PrefConfig? = null
    private var isGotoFollowShow = false
    private var type: String? = null
    private var commentClick: CommentClick? = null
    private var lifecycle: Lifecycle? = null
    private var context: AppCompatActivity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            REELS -> SearchReelsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_trending_reels_item, parent, false),
                context
            )

            ARTICLE -> SearchArticlesItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.discover_trending_channels_view, parent, false
                )
            )

            SOURCES ->
                SearchSourcesViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.discover_trending_channels_view, parent, false)
                )

            TOPICS -> SearchTopicsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_trending_channels_view, parent, false)
            )

            PLACES -> SearchLocationsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_location_view, parent, false)
            )
            else -> SearchExtraViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.author_layout_view, parent, false)
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            REELS -> {
                if (holder is SearchReelsViewHolder) {
                    holder.onBind(
                        position,
                        searchItems[position].reels,
                        searchFirstChildInterface,
                        showOptionsLoaderCallback!!,
                        adapterCallback!!,
                        detailsActivityInterface!!
                    )
                }
            }

            ARTICLE -> {
                if (holder is SearchArticlesItemViewHolder) {
                    searchArticlesItemViewHolder = holder
                    holder.setParameters(
                        commentClick!!,
                        isPostArticle,
                        context,
                        type,
                        isGotoFollowShow,
                        detailsActivityInterface,
                        goHomeMainActivity,
                        shareToMainInterface,
                        swipeListener,
                        gotoChannelListener,
                        showOptionsLoaderCallback,
                        adFailedListener,
                        lifecycle,
                        adapterCallback
                    )
                    holder.onBind(
                        position,
                        searchItems[position].articles,
                        searchFirstChildInterface
                    )
                }
            }

            SOURCES -> {
                if (holder is SearchSourcesViewHolder) {
                    searchSourcesViewHolder = holder
                    holder.onBind(
                        position,
                        searchItems[position].sources,
                        searchFirstChildInterface
                    )
                }
            }
            TOPICS -> {
                if (holder is SearchTopicsViewHolder) {
                    searchTopicsViewHolder = holder
                    holder.onBind(position, searchItems[position].topics, searchFirstChildInterface)
                }
            }

            PLACES -> {
                if (holder is SearchLocationsViewHolder) {
                    searchLocationsViewHolder = holder
                    holder.onBind(
                        position,
                        searchItems[position].locations,
                        searchFirstChildInterface
                    )
                }
            }

            AUTHORS -> {
                if (holder is SearchExtraViewHolder) {
//                    searchTopicsViewHolder = holder
                    holder.onBind(
                        position,
                        searchItems[position].authors,
                        searchFirstChildInterface
                    )
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return searchItems.size
    }

    override fun getItemViewType(position: Int): Int {

        if (searchItems.isEmpty())
            return 0

        val searchItem = searchItems[position]
        return if (searchItem.type == "REELS" && !searchItem.reels.isNullOrEmpty()) {
            return REELS
        } else if (searchItem.type == "TOPICS" && !searchItem.topics.isNullOrEmpty()) {
            return TOPICS
        } else if (searchItem.type == "ARTICLE" && !searchItem.articles.isNullOrEmpty()) {
            return ARTICLE
        } else if (searchItem.type == "CHANNELS" && !searchItem.sources.isNullOrEmpty()) {
            return SOURCES
        } else if (searchItem.type == "PLACES" && !searchItem.locations.isNullOrEmpty()) {
            return PLACES
        } else if (searchItem.type == "AUTHORS" && !searchItem.authors.isNullOrEmpty()) {
            return AUTHORS
        } else return OTHER_TRENDS
//        when (searchItem.type) {
//            "REELS" -> {
//                if (searchItem.reels.isNotEmpty())
//                    return REELS
//            }
//
//            "TOPICS" -> if(searchItem.topics.isNotEmpty())
//                return TOPICS
//
//            "ARTICLE" -> if(searchItem.articles.isNotEmpty())
//                return ARTICLE
//
//            "CHANNELS" -> if(searchItem.sources.isNotEmpty())
//                return SOURCES
//
//            "PLACES" -> if(!searchItem.locations.isEmpty())
//                return PLACES
//
//            "AUTHORS" -> if(searchItem.authors.isNotEmpty())
//                return AUTHORS
//
//            else -> OTHER_TRENDS
//        }
    }

    override fun getItemId(position: Int): Long {
        val searchItem = searchItems[position]
        return when (searchItem.type) {
            "REELS" -> REELS.toLong()
            "TOPICS" -> TOPICS.toLong()
            "ARTICLE" -> ARTICLE.toLong()
            "CHANNELS" -> SOURCES.toLong()
            "PLACES" -> PLACES.toLong()
            else -> OTHER_TRENDS.toLong()
        }
    }

    fun addChildListener(searchFirstChildInterface: SearchFirstChildInterface) {
        this.searchFirstChildInterface = searchFirstChildInterface
    }

    fun updateDiscoverItems(discoverItems: List<SearchresultData>) {
        this.searchItems = discoverItems
        notifyDataSetChanged()
    }

    fun updateTopicPosition(position: Int, topic: DiscoverNewTopic?) {
        searchTopicsViewHolder.updateTopicPositionin(position, topic)
    }

    fun updateChannelFollowPosition(position: Int, channel: DiscoverNewChannels?) {
        searchSourcesViewHolder.updateChannelPositionin(position, channel)
    }

    fun updateChannelStatus() {
        if (::searchSourcesViewHolder.isInitialized) {
            searchSourcesViewHolder.updateChannelStatus()
        }
    }

    fun updateLocationFollowPosition(position: Int, channel: Location?) {
        searchLocationsViewHolder.updateChannelPositionin(position, channel)
    }

    fun setParametersForArticles(
        mCommentClick: CommentClick,
        isPostArticle: Boolean,
        context: AppCompatActivity?,
        type: String?,
        isGotoFollowShow: Boolean,
        detailsActivityInterface: DetailsActivityInterface?,
        goHomeMainActivity: GoHome?,
        shareToMainInterface: ShareToMainInterface?,
        swipeListener: TempCategorySwipeListener?,
        gotoChannelListener: OnGotoChannelListener?,
        showOptionsLoaderCallback: ShowOptionsLoaderCallback?,
        adFailedListener: AdFailedListener?,
        lifecycle: Lifecycle?,
        adapterCallback: AdapterCallback
    ) {
        this.context = context
        this.swipeListener = swipeListener
        this.goHomeMainActivity = goHomeMainActivity
        this.shareToMainInterface = shareToMainInterface
        this.gotoChannelListener = gotoChannelListener
        this.detailsActivityInterface = detailsActivityInterface
        this.isGotoFollowShow = isGotoFollowShow
        this.type = type
        this.commentClick = mCommentClick
        this.isPostArticle = isPostArticle
        this.showOptionsLoaderCallback = showOptionsLoaderCallback
        this.adFailedListener = adFailedListener
        this.lifecycle = lifecycle
        this.adapterCallback = adapterCallback
        mPrefConfig = PrefConfig(context)
    }

    fun updateTopicStatus() {
        if (::searchTopicsViewHolder.isInitialized) {
            searchTopicsViewHolder.updateTopicStatus()
        }
    }
}