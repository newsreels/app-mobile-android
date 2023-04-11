package com.ziro.bullet.fragments.searchNew.locationnew

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.adapters.discover_new.DiscoverLiveChannelsViewHolder
import com.ziro.bullet.adapters.discover_new.DiscoverTrendingChannelsViewHolder
import com.ziro.bullet.adapters.discover_new.TrendingNewsViewHolder
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewLiveChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.discoverNew.liveScore.Stage
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import com.ziro.bullet.model.places.Search


private const val PLACES_REELS = 0
private const val PLACES_NEWS = 1
private const val OTHER_TRENDS = 2


class PlacesAdapterNew(private val context: AppCompatActivity?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var placeItems = listOf<Search>()
    private var reelsList = listOf<ReelsItem>()
    private var liveChannelsList = listOf<DiscoverNewLiveChannels>()
    private var trendingChannels = listOf<DiscoverNewChannels>()
    private var trendingTopics = listOf<DiscoverNewTopic>()
    private var trendingNews = listOf<Article>()
    private var weatherForecastResponse: WeatherForecastResponse? = null
    private var cryptoPrices: CryptoForexApiResponse? = null
    private var forexPrices: CryptoForexApiResponse? = null
    private var sportsStagesList: List<Stage>? = null
    private lateinit var discoverChildInterface: DiscoverChildInterface
    private lateinit var discoverTrendingChannelsViewHolder: DiscoverTrendingChannelsViewHolder

    private var isPostArticle = false
    private var adFailedListener: AdFailedListener? = null
    private var shareToMainInterface: ShareToMainInterface? = null
    private var swipeListener: TempCategorySwipeListener? = null
    private var gotoChannelListener: OnGotoChannelListener? = null
    private var detailsActivityInterface: DetailsActivityInterface? = null
    private var goHomeMainActivity: GoHome? = null
    private var showOptionsLoaderCallback: ShowOptionsLoaderCallback? = null
    private var mPrefConfig: PrefConfig? = null
    private var isGotoFollowShow = false
    private var type: String? = null
    private var commentClick: CommentClick? = null
    private var lifecycle: Lifecycle? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            PLACES_REELS -> PlacesReelsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_trending_reels_item, parent, false),
                context
            )

            PLACES_NEWS -> TrendingNewsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_top_news_item, parent, false)
            )

            else -> DiscoverLiveChannelsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.discover_live_channels, parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlacesReelsViewHolder -> holder.onBind(
                position,
                placeItems[position],
                reelsList,
                discoverChildInterface
            )
            is TrendingNewsViewHolder -> {
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
                    discoverChildInterface
                )
                holder.onBind(
                    trendingNews,
                    context!!
                )
            }
        }
    }

    override fun getItemId(position: Int): Long {
        val discoverItem = placeItems[position]
        return when (discoverItem.type) {
            "REELS" -> PLACES_REELS.toLong()
            "ARTICLE" -> PLACES_NEWS.toLong()
            "YOUTUBE_LIVE" -> OTHER_TRENDS.toLong()
            else -> OTHER_TRENDS.toLong()
        }
    }

    override fun getItemCount(): Int {
        return placeItems.size
    }

    override fun getItemViewType(position: Int): Int {
        if (placeItems.isEmpty())
            return 0
        val discoverItem = placeItems[position]
        return when (discoverItem.type) {
            "REELS" -> {
                REELS_POSITION = position
                PLACES_REELS
            }
            "YOUTUBE_LIVE" -> {
                OTHER_POSITION = position
                OTHER_TRENDS
            }
            "ARTICLE" -> {
                ARTICLE_POSITION = position
                PLACES_NEWS
            }
            else -> {
                OTHER_POSITION = position
                OTHER_TRENDS
            }
        }
    }

    fun updatePlaceOrder(discoverItems: List<Search>) {
        this.placeItems = discoverItems
        notifyItemChanged(0)
    }

    fun getDiscoverItems(): List<Search> = this.placeItems

    fun updateReelsData(reelsList: List<ReelsItem>) {
        this.reelsList = reelsList
        notifyItemChanged(REELS_POSITION)
    }

    fun addChildListener(discoverChildInterface: DiscoverChildInterface) {
        this.discoverChildInterface = discoverChildInterface
    }

    fun updateTrendingNews(trendingNews: List<Article>) {
        this.trendingNews = trendingNews
        notifyItemChanged(ARTICLE_POSITION)
    }


    fun updateChannelFollowPosition(position: Int, channel: DiscoverNewChannels?) {
        discoverTrendingChannelsViewHolder.updateChannelPositionin(position, channel)
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
        lifecycle: Lifecycle?
    ) {
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
        mPrefConfig = PrefConfig(context)
    }

    companion object {
        private var REELS_POSITION = 0
        private var ARTICLE_POSITION = 0
        private var OTHER_POSITION = 0
    }

}