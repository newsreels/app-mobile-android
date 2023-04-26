package com.ziro.bullet.fragments.searchNew.locationnew

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.ziro.bullet.R
import com.ziro.bullet.activities.*
import com.ziro.bullet.adapters.feed.FeedAdapter
import com.ziro.bullet.adapters.searchhistory.SearchHistoryAdapter
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.TYPE
import com.ziro.bullet.data.models.AuthorListResponse
import com.ziro.bullet.data.models.NewFeed.HomeResponse
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.fragments.Search.SearchReelsFragment
import com.ziro.bullet.fragments.searchNew.SearchResultFragment
import com.ziro.bullet.fragments.searchNew.presenter.SearchResultPresenter
import com.ziro.bullet.fragments.test.ReelInnerActivity
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.AudioObject
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articlenew.ArticleBase
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.articles.ArticleResponse
import com.ziro.bullet.model.discoverNew.*
import com.ziro.bullet.model.discoverNew.liveScore.LiveScoreApiResponse
import com.ziro.bullet.model.discoverNew.liveScore.SportEvent
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.trading.icons.TradingIconsResponse
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import com.ziro.bullet.model.places.PlacesOrderBase
import com.ziro.bullet.model.places.Search
import com.ziro.bullet.model.searchhistory.History
import com.ziro.bullet.model.searchhistorydelete.DeleteHistory
import com.ziro.bullet.presenter.NewDiscoverPresenter
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.SpeedyLinearLayoutManager
import com.ziro.bullet.utills.Utils.prefConfig
import kotlinx.android.synthetic.main.discover_new_content.*
import kotlinx.android.synthetic.main.discover_new_content.ll_no_results
import kotlinx.android.synthetic.main.fragment_places.*
import java.util.*


class PlacesListFragmentNew : Fragment(), DiscoverResponseInterface,
    DiscoverChildInterface, AdapterItemCallback {

    companion object {
        private var placesListFragmentNew: PlacesListFragmentNew? = null

        private var goHome: GoHome? = null
        fun getInstance(bundle: Bundle, goHome: GoHome): PlacesListFragmentNew {
            if (placesListFragmentNew == null)
                placesListFragmentNew = PlacesListFragmentNew()
            placesListFragmentNew!!.arguments = bundle
            this.goHome = goHome
            return placesListFragmentNew!!
        }
    }

    private var mCurrentType = ""
    private var mPage = ""
    private var adFailedStatus: Boolean = false
    private val placesOrderArray = mutableListOf<Search>()
    private var searchHistoryAvailable: Boolean = false
    private var placesListAdapterNew: PlacesAdapterNew? = null
    lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private var cardLinearLayoutManager: SpeedyLinearLayoutManager? = null
    private lateinit var discoverPresenter: NewDiscoverPresenter
    var searchHisData: List<History>? = null
    var searchtake10: List<History>? = null
    var searchquery: String? = ""
    private var currentPage = ""
    private val contentArrayList = ArrayList<Article>()
    private var mArticlesAdapter: FeedAdapter? = null
    private var count = 0
    val bundle1 = Bundle()
    private var handleBackPress = false
    private var isLastPage = false
    private var isLoading = false
    private lateinit var searchResultPresenter: SearchResultPresenter
    private val discoverTopicsType = mutableListOf<String>()
    val bundle = Bundle()
    private var mArticlePosition = 0
    var onGotoChannelListener: OnGotoChannelListener = object : OnGotoChannelListener {
        override fun onItemClicked(type: TYPE, id: String, name: String, favorite: Boolean) {
            Log.e("@@@", "ITEM CLICKED")

            //Resetting the audio data to avoid old article speech while loading new data
            Constants.articleId = ""
            Constants.speech = ""
            Constants.url = ""
            goHome!!.sendAudioEvent("stop_destroy")
            if (type != null && type == TYPE.MANAGE) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
                Constants.canAudioPlay = true
                val intent = Intent(context, ChannelDetailsActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                intent.putExtra("name", name)
                intent.putExtra("favorite", favorite)
                intent.putExtra("article_id", Constants.articleId)
                intent.putExtra("position", mArticlePosition)
                startActivityForResult(intent, Constants.CommentsRequestCode)
            }
        }

        override fun onItemClicked(
            type: TYPE,
            id: String,
            name: String,
            favorite: Boolean,
            article: Article,
            position: Int
        ) {
        }

        override fun onArticleSelected(article: Article) {}
    }
    private val shareToMainInterface: ShareToMainInterface = object : ShareToMainInterface {
        override fun removeItem(id: String, position: Int) {}
        override fun onItemClicked(type: TYPE, id: String, name: String, favorite: Boolean) {

            //Resetting the audio data to avoid old article speech while loading new data
            Constants.articleId = ""
            Constants.speech = ""
            Constants.url = ""
            goHome!!.sendAudioEvent("stop_destroy")
            // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);
            if (type != null && type == TYPE.MANAGE) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
                Constants.canAudioPlay = true
                val intent = Intent(context, ChannelDetailsActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                intent.putExtra("name", name)
                intent.putExtra("favorite", favorite)
                intent.putExtra("article_id", Constants.articleId)
                intent.putExtra("position", mArticlePosition)
                startActivityForResult(intent, Constants.CommentsRequestCode)
                //                startActivity(intent);
            }
        }

        override fun unarchived() {}
    }
    private val swipeListener: TempCategorySwipeListener = object : TempCategorySwipeListener {
        override fun swipe(enable: Boolean) {}
        override fun muteIcon(isShow: Boolean) {}
        override fun onFavoriteChanged(fav: Boolean) {}
        override fun selectTab(id: String) {}
        override fun selectTabOrDetailsPage(
            id: String,
            name: String,
            type: TYPE,
            footerType: String
        ) {
        }
    }
    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (goHome != null) {
                goHome?.scrollDown()
            }

            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStackImmediate()
                return
            }
            isEnabled = false

            requireActivity().onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }


    fun init() {
        if (placesListAdapterNew == null || rvPlacesnew.adapter == null) {
            bundle1.putString("query", "")

            placesListAdapterNew = PlacesAdapterNew(requireActivity() as AppCompatActivity)
            discoverPresenter = NewDiscoverPresenter(requireActivity(), this)

            rvPlacesnew.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            placesListAdapterNew!!.setHasStableIds(true)
            placesListAdapterNew!!.setParametersForArticles(
                object : CommentClick {
                    override fun commentClick(position: Int, id: String) {
                        val intent = Intent(activity, CommentsActivity::class.java)
                        intent.putExtra("article_id", id)
                        intent.putExtra("position", position)
                        startActivityForResult(intent, Constants.CommentsRequestCode)
                    }

                    override fun onDetailClick(position: Int, article: Article) {
                        val intent = Intent(context, BulletDetailActivity::class.java)
                        intent.putExtra("article", Gson().toJson(article))
                        //                intent.putExtra("type", type);
                        intent.putExtra("position", position)
                        startActivityForResult(intent, Constants.CommentsRequestCode)
                    }

                    override fun onNewDetailClick(
                        position: Int,
                        article: Article?,
                        articlelist: MutableList<Article>?
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun fullscreen(
                        position: Int,
                        article: Article,
                        duration: Long,
                        mode: String,
                        isManual: Boolean
                    ) {
                        val intent = Intent(context, VideoFullScreenActivity::class.java)
                        intent.putExtra("url", article.link)
                        intent.putExtra("mode", mode)
                        intent.putExtra("position", position)
                        intent.putExtra("duration", duration)
                        startActivityForResult(intent, Constants.VideoDurationRequestCode)
                    }
                },
                false,
                activity as AppCompatActivity?,
                "AuthorArticles",
                true,
                object : DetailsActivityInterface {
                    override fun playAudio(
                        audioCallback: AudioCallback,
                        fragTag: String,
                        audio: AudioObject
                    ) {
                        if (goHome != null) {
                            goHome!!.sendAudioToTempHome(audioCallback, fragTag, "", audio)
                        }
                    }

                    override fun pause() {

                    }

                    override fun resume() {

                    }
                },
                goHome,
                shareToMainInterface,
                swipeListener,
                onGotoChannelListener,
                { show: Boolean -> },
                {

                },
                lifecycle
            )
            rvPlacesnew.adapter = placesListAdapterNew
//            rvPlacesnew.setItemViewCacheSize(3)

            placesListAdapterNew!!.addChildListener(this)
            discoverPresenter.getPlacesOrder()

        }
    }

    private fun init2() {
        prefConfig = PrefConfig(context)

        back_img.setOnClickListener { requireActivity().onBackPressed() }

        mArticlesAdapter = FeedAdapter(
            object : CommentClick {
                override fun commentClick(position: Int, id: String) {
                    val intent = Intent(activity, CommentsActivity::class.java)
                    intent.putExtra("article_id", id)
                    intent.putExtra("position", position)
                    startActivityForResult(intent, Constants.CommentsRequestCode)
                }

                override fun onDetailClick(position: Int, article: Article) {
                    val intent = Intent(context, BulletDetailActivity::class.java)
                    intent.putExtra("article", Gson().toJson(article))
                    //                intent.putExtra("type", type);
                    intent.putExtra("position", position)
                    startActivityForResult(intent, Constants.CommentsRequestCode)
                }

                override fun onNewDetailClick(
                    position: Int,
                    article: Article?,
                    articlelist: MutableList<Article>?
                ) {
                    TODO("Not yet implemented")
                }

                override fun fullscreen(
                    position: Int,
                    article: Article,
                    duration: Long,
                    mode: String,
                    isManual: Boolean
                ) {
                    val intent = Intent(context, VideoFullScreenActivity::class.java)
                    intent.putExtra("url", article.link)
                    intent.putExtra("mode", mode)
                    intent.putExtra("position", position)
                    intent.putExtra("duration", duration)
                    startActivityForResult(intent, Constants.VideoDurationRequestCode)
                }
            },
            false,
            activity as AppCompatActivity?,
            contentArrayList,
            "AuthorArticles",
            true,
            object : DetailsActivityInterface {
                override fun playAudio(
                    audioCallback: AudioCallback,
                    fragTag: String,
                    audio: AudioObject
                ) {
                    goHome?.sendAudioToTempHome(
                        audioCallback,
                        fragTag,
                        "",
                        audio
                    )
                }

                override fun pause() {}
                override fun resume() {}
            },
            goHome,
            object : ShareToMainInterface {
                override fun removeItem(id: String, position: Int) {}
                override fun onItemClicked(
                    type: TYPE,
                    id: String,
                    name: String,
                    favorite: Boolean
                ) {
                }

                override fun unarchived() {}
            },
            null,
            object : CommunityCallback {
                override fun authors(response: AuthorListResponse) {}
                override fun reels(response: ReelResponse) {}
                override fun loaderShow(flag: Boolean) {}
                override fun error(error: String) {}
                override fun error404(error: String) {}
                override fun success(response: ArticleResponse, offlineData: Boolean) {}
                override fun successArticle(article: Article) {}
                override fun homeSuccess(homeResponse: HomeResponse, currentPage: String) {}
                override fun nextPosition(position: Int) {}
                override fun nextPositionNoScroll(position: Int, shouldNotify: Boolean) {}
                override fun onItemHeightMeasured(height: Int) {}
            },
            object : OnGotoChannelListener {
                override fun onItemClicked(
                    type: TYPE,
                    id: String,
                    name: String,
                    favorite: Boolean
                ) {
                }

                override fun onItemClicked(
                    type: TYPE,
                    id: String,
                    name: String,
                    favorite: Boolean,
                    article: Article,
                    position: Int
                ) {
                }

                override fun onArticleSelected(article: Article) {}
            },
            { },
            {
                removeAdItem()
                adFailedStatus = true
            },
            lifecycle
        )
        cardLinearLayoutManager = SpeedyLinearLayoutManager(context)
        rvPlacesnew.setLayoutManager(cardLinearLayoutManager)
        rvPlacesnew.setOnFlingListener(null)
        rvPlacesnew.setAdapter(mArticlesAdapter)
        val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(
            ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        )
        rvPlacesnew.addItemDecoration(dividerItemDecoration)

//        mRecyclerView.setCacheManager(mArticlesAdapter);
//        mRecyclerView.setPlayerSelector(selector);
    }

    private fun navigatetsearch() {
        ll5.visibility = View.GONE
        ll3.visibility = View.GONE
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(
            R.id.framel,
            SearchResultFragment.getInstance(bundle, goHome!!),
            "navsearch"
        )
        transaction.addToBackStack("navsearch")
        transaction.commit()
    }

    private fun removeAdItem() {
        if (contentArrayList != null && contentArrayList.size > 0) {
            for (i in contentArrayList.indices) {
                if (contentArrayList[i] != null && !TextUtils.isEmpty(contentArrayList[i].type)) {
                    if (contentArrayList[i].type == "FB_Ad" || contentArrayList[i].type == "G_Ad") {
                        contentArrayList.removeAt(i)
                        if (mArticlesAdapter != null) mArticlesAdapter!!.notifyItemRemoved(i)
                        removeAdItem()
                        return
                    }
                }
            }
        }
    }

    fun setStatusBarColor() {
        if (activity != null) {
            val window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.white)
        }

    }

    fun isDiscoverVisible(): Boolean {
        return mCurrentType == getString(R.string.relevant)
    }

    override fun getChannelFollow(
        response: FollowResponse?,
        position: Int,
        channel: DiscoverNewChannels?
    ) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            placesListAdapterNew!!.updateChannelFollowPosition(position, channel)
        }
    }

    override fun getChannelsunFollow(
        response: FollowResponse?,
        position: Int,
        channel: DiscoverNewChannels?
    ) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            placesListAdapterNew!!.updateChannelFollowPosition(position, channel)
        }
    }

    override fun loadingData(isLoading: Boolean) {
//        if (isLoading) {
//            Utils.loadSkeletonLoader(discover_new_switcher, isLoading)
//        } else if (!isLoading && placesListAdapterNew!!.getDiscoverItems().isNotEmpty()) {
//            Utils.loadSkeletonLoader(discover_new_switcher, isLoading)
//        } else {
//            shimmer_view_container.visibility = View.GONE
//            discover_items.visibility = View.GONE
//            ll_no_results.visibility = View.VISIBLE
//        }
    }

    override fun error(error: String, topic: String) {

    }

    override fun getDiscoverTopics(response: DiscoverNewResponse?) {

    }

    override fun getPlacesOrder(response: PlacesOrderBase?) {
        placesOrderArray.clear()
        if (response != null && !response.search.isNullOrEmpty()) {
            response.search.forEach {
                when (it.type) {
                    "REELS" -> {
                        placesOrderArray.add(it)
                        discoverPresenter.getPlacesReels("india", mPage)
                    }
                    "ARTICLE" -> {
                        placesOrderArray.add(it)
                        discoverPresenter.placesArticlesContext("india", mPage, "", false)
                    }
                }
            }
            placesListAdapterNew!!.updatePlaceOrder(placesOrderArray)
        } else {
            rvPlacesnew.visibility = View.GONE
            ll_no_results.visibility = View.VISIBLE
        }
    }


    override fun getDiscoverReels(reelsResponse: List<ReelsItem>?) {
        if (!reelsResponse.isNullOrEmpty()) {
            placesListAdapterNew!!.updateReelsData(reelsResponse)
        }
    }

    override fun refreshHistory() {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSearchArticleSuccess(response: ArticleBase?, isPagination: Boolean) {
        isLoading = false
        if (response != null) {
//            cardview.setVisibility(View.VISIBLE)
//            search_skeleton.setVisibility(View.GONE)

            currentPage = response.meta.next

            if (TextUtils.isEmpty(currentPage)) {
                isLastPage = true
//                    contentArrayList.clear();
            }
            if (!isPagination) {
                contentArrayList.clear()
            }
//            contentArrayList.addAll(response.getArticles());


            //            contentArrayList.addAll(response.getArticles());
            if (!response.articles.isNullOrEmpty()) {
                for (position in response.articles.indices) {
                    val article: Article = response.articles.get(position)
                    if (prefConfig != null && prefConfig.getAds() != null && prefConfig.getAds()
                            .isEnabled()
                    ) {
                        var interval = 10
                        if (prefConfig.getAds().getInterval() != 0) {
                            interval = prefConfig.getAds().getInterval()
                        }
                        val newCount: Int = contentArrayList.size
                        if (newCount != 0 && newCount % interval == 0 && !adFailedStatus) {
                            Log.e("ADS", "AD Added")
                            val adArticle1 = Article()
                            if (!TextUtils.isEmpty(
                                    prefConfig.getAds().getType()
                                ) && prefConfig.getAds().getType()
                                    .equals("facebook", ignoreCase = true)
                            ) {
                                adArticle1.type = "FB_Ad"
                            } else {
                                adArticle1.type = "G_Ad"
                            }
                            contentArrayList.add(adArticle1)
                        }
                    }
                    contentArrayList.add(article)
                }

                mArticlesAdapter?.notifyDataSetChanged()
            }
            if (contentArrayList.size <= 0) {
//                cardview.setVisibility(View.GONE)
                ll_no_results.visibility = View.VISIBLE
//                search_skeleton.setVisibility(View.GONE)
            } else {
                ll_no_results.visibility = View.GONE
            }

//            mArticlesAdapter?.notifyDataSetChanged()
        }
    }


    override fun getSearchHistory(searchHistory: List<History>?) {

    }

    override fun deleteHistory(deletehistory: DeleteHistory?) {


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun clearHistory(clearhistory: FollowResponse?) {

        if (clearhistory?.message?.lowercase(Locale.ROOT).equals("success")) {
            discoverPresenter.getSearchHistory()
//            searchHistoryAdapter?.notifyDataSetChanged()

        }
    }

    override fun getDiscoverTrendingTopics(topicsResponse: List<DiscoverNewTopic>?) {

    }

    override fun getDiscoverTrendingNews(trendingNews: List<Article>?) {
        if (trendingNews != null) {
            placesListAdapterNew!!.updateTrendingNews(trendingNews)
        }
    }

    override fun getDiscoverTrendingChannels(trendingChannels: List<DiscoverNewChannels>?) {

    }

    override fun getDiscoverLiveChannels(liveChannels: List<DiscoverNewLiveChannels>?) {

    }

    override fun getWeatherForecast(weatherForecastResponse: WeatherForecastResponse?) {

    }

    override fun getTradingItemsList(tradingIconsResponse: TradingIconsResponse?) {

    }

    override fun getCryptoPrices(cryptoForexApiResponse: CryptoForexApiResponse?) {

    }

    override fun getForexPrices(cryptoForexApiResponse: CryptoForexApiResponse?) {

    }

    override fun getLiveScore(category: String, liveScoreApiResponse: LiveScoreApiResponse?) {

    }

    override fun getTopicsFollow(body: FollowResponse?, position: Int, topic: DiscoverNewTopic?) {

    }

    override fun loadItem(discoverNew: DiscoverNew) {

    }

    override fun searchChildOnClick(reelsList: List<ReelsItem>) {
        navigateReelsList(reelsList)
    }

    override fun searchChildSecondOnClick(
        reelsItem: ReelsItem,
        reelsList: List<ReelsItem>,
        position: Int
    ) {
        val intent = Intent(context, ReelInnerActivity::class.java)
        if (reelsItem.author != null && reelsItem.author.isNotEmpty()) {
            intent.putExtra(
                ReelInnerActivity.REEL_F_AUTHOR_ID,
                reelsItem.author[0].id
            )
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, "userContext")
        }
        if (reelsItem.source != null) {
            intent.putExtra(
                ReelInnerActivity.REEL_F_SOURCE_ID,
                reelsItem.source.id
            )
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, "userContext")
        }
        intent.putExtra(ReelInnerActivity.REEL_F_MODE, "public")
        intent.putExtra(ReelInnerActivity.REEL_POSITION, position)
        intent.putExtra(ReelInnerActivity.REEL_F_PAGE, "")//page
        intent.putParcelableArrayListExtra(
            ReelInnerActivity.REEL_F_DATALIST,
            reelsList as ArrayList<ReelsItem>?
        )
        requireContext().startActivity(intent)
    }

    override fun searchChildOnTopicClick(trendingTopics: List<DiscoverNewTopic>) {

    }

    override fun searchChildTopicSecondOnClick(trendingTopic: DiscoverNewTopic) {

    }

    override fun onTopicFollowed(topic: DiscoverNewTopic?, position: Int) {

    }

    override fun onTopicUnfollowed(topic: DiscoverNewTopic?, position: Int) {

    }

    override fun searchChildOnChannelClick(trendingChannels: List<DiscoverNewChannels>) {


    }

    override fun searchChildChannelSecondOnClick(discoverNewChannels: DiscoverNewChannels) {


    }

    override fun onItemChannelFollowed(channel: DiscoverNewChannels?, position: Int) {

    }

    override fun discoverLiveChannelClick(liveChannels: DiscoverNewLiveChannels) {

    }

    override fun onArticleSeeAll() {

    }

    override fun onCryptoSeeAllClick(cryptoForexApiResponse: CryptoForexApiResponse?) {

    }


    override fun onForexSeeAllClick(cryptoForexApiResponse: CryptoForexApiResponse?) {
    }


    override fun searchChildWeatherSecondOnClick(forecast: WeatherForecastResponse) {

    }

    override fun updateSports(category: String) {

    }

    override fun moveSportsDetailPage(sportCategory: String, liveEvent: SportEvent) {
    }


    override fun onItemChannelUnfollowed(channel: DiscoverNewChannels?, position: Int) {

    }


    private fun navigateReelsList(reelsList: List<ReelsItem>) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(
            R.id.framel,
            SearchReelsFragment.getInstance(bundle1, goHome),
            "searchreel"
        )
        transaction.addToBackStack("searchreel")
        transaction.commit()
    }

    override fun onItemClick(
        position: Int,
        item: History
    ) {


    }

    override fun onItemClick(position: Int) {

    }

    override fun onItemCancelClick(position: Int, item: History?) {

    }

}