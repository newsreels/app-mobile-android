package com.ziro.bullet.adapters.discover_new

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.bottomSheet.ShareBottomSheet
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.ShareInfo
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.discoverNew.DiscoverNew
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewLiveChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.discoverNew.liveScore.Stage
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse


private const val TRENDING_TOPICS = 0
private const val CRYPTO = 1
private const val SPORTS_SCORE = 2
private const val WEATHER_UPDATE = 3
private const val TRENDING_REELS = 4
private const val TRENDING_NEWS = 5
private const val TRENDING_CHANNELS = 6
private const val OTHER_TRENDS = 7
private const val FOREX = 8
private const val STOCKS = 9

class DiscoverAdapterNew(val context: AppCompatActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterCallback {

    private var discoverItems = listOf<DiscoverNew>()
    private var reelsList = listOf<ReelsItem>()
    private var liveChannelsList = listOf<DiscoverNewLiveChannels>()
    private var trendingChannels = listOf<DiscoverNewChannels>()
    private var trendingTopics = mutableListOf<DiscoverNewTopic>()
    private var trendingNews = listOf<Article>()
    private var weatherForecastResponse: WeatherForecastResponse? = null
    private var cryptoPrices: CryptoForexApiResponse? = null
    private var forexPrices: CryptoForexApiResponse? = null
    private var sportsStagesList: List<Stage>? = null
    var category: String = ""

    private lateinit var discoverChildInterface: DiscoverChildInterface
    private lateinit var discoverTrendingChannelsViewHolder: DiscoverTrendingChannelsViewHolder
    private lateinit var discoverTopicsViewHolder: DiscoverTrendingTopicsViewHolder

    private var isPostArticle = false
    private var adFailedListener: AdFailedListener? = null
    lateinit var swcontext: Context
    private var shareToMainInterface: ShareToMainInterface? = null
    private var swipeListener: TempCategorySwipeListener? = null
    private var gotoChannelListener: OnGotoChannelListener? = null
    private var detailsActivityInterface: DetailsActivityInterface? = null
    private var goHomeMainActivity: GoHome? = null
    private var showOptionsLoaderCallback: ShowOptionsLoaderCallback? = null
    private var listener: DetailsActivityInterface? = null
    private var mPrefConfig: PrefConfig? = null
    private var isGotoFollowShow = false
    private var type: String? = null
    private var commentClick: CommentClick? = null
    private var lifecycle: Lifecycle? = null
    private var shareBottomSheet: ShareBottomSheet? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TRENDING_TOPICS -> {
                discoverTopicsViewHolder = DiscoverTrendingTopicsViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.discover_list_items, parent, false
                    )
                )
                discoverTopicsViewHolder
            }
            CRYPTO -> CryptoViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_list_items, parent, false)
            )

            FOREX -> ForexViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_list_items, parent, false)
            )

            SPORTS_SCORE -> SportsScoreViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.sports_score_item, parent, false)
            )

            WEATHER_UPDATE -> WeatherUpdateViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_module_weather, parent, false)
            )

            TRENDING_REELS -> DiscoverTrendingReelsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_trending_reels_item, parent, false),
                context
            )

            TRENDING_NEWS -> TrendingNewsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_top_news_item, parent, false)
            )

            TRENDING_CHANNELS -> DiscoverTrendingChannelsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.discover_trending_channels_view, parent, false)
            )

//            6 -> OtherTrendsViewHolder(
//                LayoutInflater.from(parent.context).inflate(
//                    R.layout.discover_horizontal_list_item, parent, false
//                )
//            )

            else -> DiscoverLiveChannelsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.discover_live_channels, parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiscoverTrendingTopicsViewHolder -> holder.onBind(
                trendingTopics,
                discoverChildInterface
            )
            is CryptoViewHolder -> {
                holder.onBind(
                    holder.itemView.context.getString(R.string.crypto_prices),
                    cryptoPrices,
                    discoverChildInterface
                )
            }
            is ForexViewHolder -> {
                holder.onBind(
                    holder.itemView.context.getString(R.string.forex_prices),
                    forexPrices,
                    discoverChildInterface
                )
            }
            is SportsScoreViewHolder -> holder.onBind(
                category, swcontext,
                sportsStagesList,
                discoverChildInterface
            )
            is WeatherUpdateViewHolder -> holder.onBind(
                swcontext,
                weatherForecastResponse,
                discoverChildInterface
            )
            is DiscoverTrendingReelsViewHolder -> holder.onBind(
                position,
                discoverItems[position],
                reelsList,
                discoverChildInterface,
                showOptionsLoaderCallback!!,
                this,
                listener!!
            )
            is DiscoverTrendingChannelsViewHolder -> {
                discoverTrendingChannelsViewHolder = holder
                holder.onBind(
                    position,
                    discoverItems[position],
                    trendingChannels,
                    discoverChildInterface
                )
            }

            is DiscoverLiveChannelsViewHolder -> holder.onBind(
                liveChannelsList,
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
        val discoverItem = discoverItems[position]
        return when (discoverItem.type) {
            "TOPICS" -> TRENDING_TOPICS.toLong()
            "FINANCE" -> CRYPTO.toLong()
            "SPORTS" -> SPORTS_SCORE.toLong()
            "WEATHER" -> WEATHER_UPDATE.toLong()
            "REELS" -> TRENDING_REELS.toLong()
            "ARTICLE" -> TRENDING_NEWS.toLong()
            "STOCK" -> STOCKS.toLong()
            "CURRENCY" -> FOREX.toLong()
            "CHANNELS" -> TRENDING_CHANNELS.toLong()
            "YOUTUBE_LIVE" -> OTHER_TRENDS.toLong()
            else -> OTHER_TRENDS.toLong()
        }
    }

    override fun getItemCount(): Int {
        return discoverItems.size
    }

    override fun getItemViewType(position: Int): Int {
        if (discoverItems.isEmpty())
            return 0
        val discoverItem = discoverItems[position]
        return when (discoverItem.type) {
            "TOPICS" -> {
                TRENDING_TOPICS_POSITION = position
                TRENDING_TOPICS
            }
            "FINANCE" -> {
                CRYPTO_POSITION = position
                CRYPTO
            }
            "SPORTS" -> {
                SPORTS_SCORE_POSITION = position
                SPORTS_SCORE
            }
            "WEATHER" -> {
                WEATHER_UPDATE_POSITION = position
                WEATHER_UPDATE
            }
            "REELS" -> {
                TRENDING_REELS_POSITION = position
                TRENDING_REELS
            }
            "CHANNELS" -> {
                TRENDING_CHANNELS_POSITION = position
                TRENDING_CHANNELS
            }
            "YOUTUBE_LIVE" -> {
                OTHER_TRENDS_POSITION = position
                OTHER_TRENDS
            }
            "ARTICLE" -> {
                TRENDING_NEWS_POSITION = position
                TRENDING_NEWS
            }
//            "STOCK" -> {
//                STOCKS_POSITION = position
//                STOCKS
//            }
            "CURRENCY" -> {
                FOREX_POSITION = position
                FOREX
            }
            else -> {
                OTHER_TRENDS_POSITION = position
                OTHER_TRENDS
            }
        }
    }

    fun updateDiscoverItems(discoverItems: List<DiscoverNew>) {
        this.discoverItems = discoverItems
        notifyDataSetChanged()
    }

    fun getDiscoverItems(): List<DiscoverNew> = this.discoverItems

    fun updateTopicsData(trendingTopicsList: List<DiscoverNewTopic>) {
        this.trendingTopics = trendingTopicsList as MutableList
        notifyItemChanged(TRENDING_TOPICS_POSITION)
    }

    fun updateReelsData(reelsList: List<ReelsItem>) {
        this.reelsList = reelsList
        notifyItemChanged(TRENDING_REELS_POSITION)
    }

    fun updateChannelsData(channelList: List<DiscoverNewChannels>) {
        this.trendingChannels = channelList
        notifyItemChanged(TRENDING_CHANNELS_POSITION)
    }

    fun updateLiveChannelsData(liveChannelsList: List<DiscoverNewLiveChannels>) {
        this.liveChannelsList = liveChannelsList
        notifyItemChanged(OTHER_TRENDS_POSITION)
    }

    fun updateWeatherForecast(weatherForecastResponse: WeatherForecastResponse) {
        this.weatherForecastResponse = weatherForecastResponse
        notifyItemChanged(WEATHER_UPDATE_POSITION)
    }

    fun updateCryptoPrices(cryptoForexApiResponse: CryptoForexApiResponse) {
        this.cryptoPrices = cryptoForexApiResponse
        notifyItemChanged(CRYPTO_POSITION)
    }

    fun updateForexPrices(cryptoForexApiResponse: CryptoForexApiResponse) {
        this.forexPrices = cryptoForexApiResponse
        notifyItemChanged(FOREX_POSITION)
    }

    fun addChildListener(discoverChildInterface: DiscoverChildInterface) {
        this.discoverChildInterface = discoverChildInterface
    }

    fun updateLiveScore(category: String, stages: List<Stage>) {
        this.category = category
        this.sportsStagesList = stages
        notifyItemChanged(SPORTS_SCORE_POSITION)
    }

    fun updateTrendingNews(trendingNews: List<Article>) {
        this.trendingNews = trendingNews
        notifyItemChanged(TRENDING_NEWS_POSITION)
    }

    fun updateChannelFollowPosition(position: Int, channel: DiscoverNewChannels?) {
        discoverTrendingChannelsViewHolder.updateChannelPositionin(position, channel)
    }

    fun getContextF(context: Context) {
        this.swcontext = context
    }

    override fun getArticlePosition(): Int {
        return 0
    }

    override fun showShareBottomSheet(
        shareInfo: ShareInfo?,
        article: Article?,
        onDismissListener: DialogInterface.OnDismissListener?
    ) {
        showBottomSheetDialog(shareInfo, article, onDismissListener)
    }

    override fun onItemClick(position: Int, setCurrentView: Boolean) {

    }

    private fun showBottomSheetDialog(
        shareInfo: ShareInfo?,
        article: Article?,
        onDismissListener: DialogInterface.OnDismissListener?
    ) {
        if (shareBottomSheet == null) {
            shareBottomSheet = ShareBottomSheet(context, shareToMainInterface, true, "ARTICLES")
        }
        shareBottomSheet?.show(article, onDismissListener, shareInfo)
    }

    fun dismissBottomSheet() {
        if (shareBottomSheet != null) {
            shareBottomSheet?.hide()
        }
    }

    fun addShareBottomSheetCallbacks(
        showOptionsLoaderCallback: ShowOptionsLoaderCallback,
        listener: DetailsActivityInterface
    ) {
        this.showOptionsLoaderCallback = showOptionsLoaderCallback
        this.listener = listener
    }

    fun setParametersForArticles(
        mCommentClick: CommentClick,
        isPostArticle: Boolean,
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

    fun updateTrendingTopicItem(position: Int, topic: DiscoverNewTopic?) {
        trendingTopics.removeAt(position)
        trendingTopics.add(position, topic!!)
        discoverTopicsViewHolder.updateTopicsList(trendingTopics, position)
    }

    companion object {
        private var TRENDING_TOPICS_POSITION = 0
        private var CRYPTO_POSITION = 0
        var SPORTS_SCORE_POSITION = 0
        var WEATHER_UPDATE_POSITION = 0
        private var TRENDING_REELS_POSITION = 0
        private var TRENDING_NEWS_POSITION = 0
        private var TRENDING_CHANNELS_POSITION = 0
        private var OTHER_TRENDS_POSITION = 0
        private var FOREX_POSITION = 0
        private var STOCKS_POSITION = 0
    }

}