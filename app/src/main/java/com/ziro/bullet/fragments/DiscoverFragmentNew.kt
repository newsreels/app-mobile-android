package com.ziro.bullet.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView.OnEditorActionListener
import android.widget.ViewSwitcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ziro.bullet.BuildConfig
import com.ziro.bullet.R
import com.ziro.bullet.activities.*
import com.ziro.bullet.adapters.discover_new.DiscoverAdapterNew
import com.ziro.bullet.adapters.searchhistory.SearchHistoryAdapter
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.TYPE
import com.ziro.bullet.fragments.Search.SearchArticlesFragment
import com.ziro.bullet.fragments.Search.SearchReelsFragment
import com.ziro.bullet.fragments.discoverdetail.crypto.CryptoSeeAllFragment
import com.ziro.bullet.fragments.discoverdetail.forex.ForexSeeAllFragment
import com.ziro.bullet.fragments.discoverdetail.weather.WeatherActivity
import com.ziro.bullet.fragments.searchNew.SearchResultFragment
import com.ziro.bullet.fragments.searchNew.channelpackage.SearchChannelFragment
import com.ziro.bullet.fragments.searchNew.sportsdetail.SportsDetailActivity
import com.ziro.bullet.fragments.test.ReelInnerActivity
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog
import com.ziro.bullet.model.AudioObject
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articlenew.ArticleBase
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.discoverNew.*
import com.ziro.bullet.model.discoverNew.liveScore.LiveScoreApiResponse
import com.ziro.bullet.model.discoverNew.liveScore.SportEvent
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.trading.Ticker
import com.ziro.bullet.model.discoverNew.trading.icons.TradingIconsResponse
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import com.ziro.bullet.model.places.PlacesOrderBase
import com.ziro.bullet.model.searchhistory.History
import com.ziro.bullet.model.searchhistorydelete.DeleteHistory
import com.ziro.bullet.presenter.NewDiscoverPresenter
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.discover_new_content.*
import kotlinx.android.synthetic.main.fragment_discover_new.*
import kotlinx.android.synthetic.main.skeleton_discovery.*
import java.text.SimpleDateFormat
import java.util.*


open class DiscoverFragmentNew : Fragment(), DiscoverResponseInterface,
    DiscoverChildInterface, AdapterItemCallback {

    companion object {
        private var discoverFragmentNew: DiscoverFragmentNew? = null

        private var goHome: GoHome? = null
        fun getInstance(goHome: GoHome): DiscoverFragmentNew {
//            if (discoverFragmentNew == null)
            discoverFragmentNew = DiscoverFragmentNew()
            this.goHome = goHome
            return discoverFragmentNew!!
        }
    }

    var ll_no_results: LinearLayout? = null
    var discoverNewSwitcher: ViewSwitcher? = null
    var isbackpress: Boolean = false
    var isfer: Boolean = false
    private var mCurrentType = ""
    var disquery: String? = ""
    private var detailsActivityInterface: DetailsActivityInterface? = null
    private var mLoadingDialog: PictureLoadingDialog? = null
    var countryWeather: String = ""
    private var prefConfig: PrefConfig? = null
    private var forexPrices: CryptoForexApiResponse? = null
    private var cryptoPrices: CryptoForexApiResponse? = null
    private val discoverResponseInterface: DiscoverResponseInterface? = this
    private val discoverTopicsArray = mutableListOf<DiscoverNew>()
    private val discoverTrendingTopics = mutableListOf<DiscoverNewTopic>()
    private val discoverTrendingChannels = mutableListOf<DiscoverNewChannels>()
    private var weatherForecastResponseNew: WeatherForecastResponse? = null
    private var tradingIconsResponse: TradingIconsResponse? = null
    private var searchHistoryAvailable: Boolean = false
    private var discoverAdapterNew: DiscoverAdapterNew? = null
    lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var discoverPresenter: NewDiscoverPresenter
    private var gettingIcons = false

    private var timerTask: TimerTask? = null
    private var timer: Timer? = null

    var searchHisData: List<History>? = null
    var searchtake10: List<History>? = null
    private var count = 0
    val bundle = Bundle()
    val bundle1 = Bundle()
    val bundle2 = Bundle()
    private var mArticlePosition = 0
    var onGotoChannelListener: OnGotoChannelListener = object : OnGotoChannelListener {
        override fun onItemClicked(type: TYPE, id: String, name: String, favorite: Boolean) {
            Log.e("@@@", "ITEM CLICKED")

            //Resetting the audio data to avoid old article speech while loading new data
            Constants.articleId = ""
            Constants.speech = ""
            Constants.url = ""
            goHome?.sendAudioEvent("stop_destroy")
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
            goHome?.sendAudioEvent("stop_destroy")
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
            //for clear history and add again
            discoverPresenter = NewDiscoverPresenter(requireActivity(), discoverResponseInterface)
            discoverPresenter.getSearchHistory()
            if (ll5?.visibility == View.VISIBLE || ll3?.visibility == View.VISIBLE) {
                cancelGoBack()
            } else {
                if (goHome != null && isVisible) {
                    goHome?.scrollDown()
                }
                if (childFragmentManager.backStackEntryCount > 0) {
                    updateDataList()
                    childFragmentManager.popBackStackImmediate()
                    return
                }
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ll_no_results = view.findViewById(R.id.ll_no_results)
        discoverNewSwitcher = view.findViewById(R.id.discoverNewSwitcher)

    }

    override fun onResume() {
        super.onResume()
        Log.d("Search_Topic_TAG", "onResume: Discover Resumed")
        weatherForecastResponseNew?.isfernite = prefConfig!!.weaTemp

        discoverAdapterNew?.notifyItemChanged(DiscoverAdapterNew.WEATHER_UPDATE_POSITION)

        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)

        updateDataList()

    }

    fun updateDataList() {
        if (Constants.topicsStatusChanged != null && isVisible) {

            if (discoverTopicsArray.isNotEmpty()) {
                var position = -1
                discoverTrendingTopics.forEachIndexed { index, discoverNewTopic ->
                    if (discoverNewTopic.id == Constants.topicsStatusChanged) {
                        position = index
                        return@forEachIndexed
                    }
                }
                if (position >= 0) {
                    val discoverTopic = discoverTrendingTopics[position]
                    if (Constants.isTopicDataChange) {
                        discoverTopic.favorite = Constants.followStatus.toBoolean()
                    }
                    discoverAdapterNew?.updateTrendingTopicItem(position, discoverTopic)
                    if (childFragmentManager.backStackEntryCount == 0) {
                        Constants.isTopicDataChange = false
                        Constants.topicsStatusChanged = null
                        Constants.followStatus = null
                    }
                }
            }
        }

        if (Constants.sourceStatusChanged != null && isVisible) {
            if (discoverTrendingChannels.isNotEmpty()) {
                var position = -1
                discoverTrendingChannels.forEachIndexed { index, trendingChannel ->
                    if (trendingChannel.id == Constants.sourceStatusChanged) {
                        position = index
                        return@forEachIndexed
                    }
                }
                if (position >= 0) {
                    val trendingChannel = discoverTrendingChannels[position]
                    if (Constants.isSourceDataChange) {
                        trendingChannel.favorite = Constants.followStatus.toBoolean()
                    }
                    discoverAdapterNew?.updateChannelFollowPosition(position, trendingChannel)
                    if (childFragmentManager.backStackEntryCount == 0) {
                        Constants.isSourceDataChange = false
                        Constants.sourceStatusChanged = null
                        Constants.followStatus = null
                        Constants.itemPosition = -1
                    }
                }
            }
        }
    }

    fun cancelGoBack() {
        imgCross.visibility = View.GONE
        tvcancel.visibility = View.GONE
        ll5.visibility = View.GONE
        ll3.visibility = View.GONE
        //tv_search.visibility = View.VISIBLE
        discoverNewSwitcher?.visibility = View.VISIBLE
        ed_searchtext.setText("")
        ed_searchtext.clearFocus()
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)

    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setWeathtemp() {
        if (prefConfig != null) {
            weatherForecastResponseNew?.isfernite = prefConfig!!.weaTemp
        }
    }

    fun init() {
        if (discoverAdapterNew == null) {

            prefConfig = PrefConfig(context)

            if (discoverAdapterNew == null || discover_items.adapter == null) {
                bundle2.putString("query", "")
                bundle2.putBoolean("reelsapi", false)
                bundle1.putString("query", "")

                discoverAdapterNew = DiscoverAdapterNew(requireActivity() as AppCompatActivity)

                discoverAdapterNew?.addShareBottomSheetCallbacks({
                    if (it) {
                        showProgressDialog()
                    } else {
                        dismissProgressDialog()
                    }
                }, object : DetailsActivityInterface {
                    override fun playAudio(
                        audioCallback: AudioCallback?,
                        fragTag: String?,
                        audio: AudioObject?
                    ) {

                    }

                    override fun pause() {

                    }

                    override fun resume() {

                    }


                })

                discoverPresenter = NewDiscoverPresenter(requireActivity(), this)
                discover_items.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                discoverAdapterNew?.setHasStableIds(true)
                discoverAdapterNew?.getContextF(requireActivity())
                discoverAdapterNew?.setParametersForArticles(
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
                    "AuthorArticles",
                    true,
                    object : DetailsActivityInterface {
                        override fun playAudio(
                            audioCallback: AudioCallback,
                            fragTag: String,
                            audio: AudioObject
                        ) {
                            if (goHome != null) {
                                goHome?.sendAudioToTempHome(audioCallback, fragTag, "", audio)
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
                discover_items.adapter = discoverAdapterNew
                discover_items.setItemViewCacheSize(9)

                if (!::searchHistoryAdapter.isInitialized) {
                    searchHistoryAdapter = SearchHistoryAdapter(requireActivity(), this)
                    discoverPresenter = NewDiscoverPresenter(requireActivity(), this)
                }
                if (rvSearchhistory?.adapter == null) {
                    rvSearchhistory?.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    rvSearchhistory?.adapter = searchHistoryAdapter
                    rvSearchhistory?.layoutManager!!.scrollToPosition(0)
                }

                discoverAdapterNew!!.addChildListener(this)
                discoverPresenter.getDiscoverTopics()
                discoverPresenter.getSearchHistory()

                tvcancel.setOnClickListener {
                    cancelGoBack()
                }
                ed_searchtext.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (!searchHisData.isNullOrEmpty()) {
                            ll5.visibility = View.VISIBLE
                            ll3.visibility = View.VISIBLE
                            discoverNewSwitcher?.visibility = View.GONE
                        } else {
                            ll5.visibility = View.GONE
                            ll3.visibility = View.GONE
                            discoverNewSwitcher?.visibility = View.VISIBLE
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        if (!searchHisData.isNullOrEmpty()) {
                            ll5.visibility = View.VISIBLE
                            ll3.visibility = View.VISIBLE
                            discoverNewSwitcher?.visibility = View.GONE
                        } else {
                            ll5.visibility = View.GONE
                            ll3.visibility = View.GONE
                            discoverNewSwitcher?.visibility = View.VISIBLE
                        }

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (!searchHisData.isNullOrEmpty()) {
                            ll5.visibility = View.VISIBLE
                            ll3.visibility = View.VISIBLE
                            discoverNewSwitcher?.visibility = View.GONE
                        } else {
                            ll5.visibility = View.GONE
                            ll3.visibility = View.GONE
                            discoverNewSwitcher?.visibility = View.VISIBLE
                        }

                    }
                })

                ed_searchtext.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && searchHistoryAvailable) {
                        discoverPresenter.getSearchHistory()
                        tvcancel.visibility = View.VISIBLE
                        imgCross.visibility = View.VISIBLE
                        ll5.visibility = View.VISIBLE
                        ll3.visibility = View.VISIBLE
                        //tv_search.visibility = View.GONE
//                        viewgray.visibility = View.GONE
                        discoverNewSwitcher?.visibility = View.GONE
                    } else {
                        imgCross.visibility = View.GONE
                        tvcancel.visibility = View.GONE
                        ll5.visibility = View.GONE
                        ll3.visibility = View.GONE
                        //tv_search.visibility = View.VISIBLE
                        discoverNewSwitcher?.visibility = View.VISIBLE
                    }
                }


                imgCross.setOnClickListener {
                    ed_searchtext?.text?.clear()
                }


                clear.setOnClickListener {
                    discoverPresenter.clearHistory()
                }
                ed_searchtext.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        disquery = ed_searchtext.text.toString().trim()
                        if (!disquery.isNullOrEmpty()) {
                            navigateSearch()
                        }

                    }
                    false
                })
            } else {
                updateDiscoverPage()
            }
        }
    }

    private fun updateDiscoverPage() {
        discoverTopicsArray.forEach {
            when (it.type) {
                "TOPICS" -> {
                    discoverPresenter.getDiscoverTrendingTopics(it.context)
                }
                "REELS" -> {
                    discoverPresenter.getDiscoverReels(it.context)
                }
                "CHANNELS" -> {
                    discoverPresenter.getDiscoverTrendingChannels(
                        it.context
                    )
                }
//                    "YOUTUBE_LIVE" -> {
//                        discoverTopicsArray.add(it)
//                        discoverPresenter.getDiscoverLiveChannels(
//                            it.context
//                        )
//                    }
                "WEATHER" -> {
                    if (it.country != null) {
                        discoverPresenter.getWeatherForecast(
                            it.country,
                            "5",
                            "yes"
                        )
                    }
                }
                "FINANCE" -> {
                    discoverPresenter.getCryptoPrices(
                        BuildConfig.CRYPTO_URL,
                        BuildConfig.POLYGON_TOKEN
                    )
                }
                "SPORTS" -> {
                    val sdf = SimpleDateFormat("yyyyMMdd Z", Locale.getDefault())
                    val calendar = Calendar.getInstance()
                    val date = sdf.format(calendar.time)
                    discoverPresenter.getLiveScore(discoverPresenter.sportCategory, date)
                }
                "ARTICLE" -> {
                    discoverPresenter.getDiscoverTrendingNews(
                        it.context
                    )
                }
                "STOCK" -> {

                }
                "CURRENCY" -> {
                    if (!gettingIcons) {
                        gettingIcons = true
                        discoverPresenter.getTradingItemsList()
                    }

                }
            }
        }
        setWeathtemp()
    }

    private fun showProgressDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = PictureLoadingDialog(requireContext())
            }
            if (mLoadingDialog!!.isShowing) {
                mLoadingDialog?.dismiss()
            }
            mLoadingDialog?.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * dismiss dialog
     */
    protected fun dismissProgressDialog() {
        try {
            if (mLoadingDialog != null
                && mLoadingDialog!!.isShowing
            ) {
                mLoadingDialog?.dismiss()
            }
        } catch (e: java.lang.Exception) {
            mLoadingDialog = null
            e.printStackTrace()
        }
    }


    private fun navigateSearch() {
        ll5.visibility = View.GONE
        ll3.visibility = View.GONE
        val manager: FragmentManager = childFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        backCallback.isEnabled = true
        bundle.putString("query", disquery)

        val oldFragment = manager.findFragmentByTag("navsearch")
        if (oldFragment != null) {
            manager.beginTransaction().remove(oldFragment).commit()
        }

        goHome?.let { SearchResultFragment.getInstance(bundle, it) }?.let {
            transaction.add(
                R.id.framel, it, "navsearch"
            )
        }
        transaction.addToBackStack("navsearch")
        transaction.commit()
        ed_searchtext.setText("")
        ed_searchtext.clearFocus()
    }

    fun selectFirst() {
        Log.e("DiscoverFrag_TAG", "selectFirst: ")
        ed_searchtext.text.clear()
        ed_searchtext.clearFocus()
        ll_no_results?.visibility = View.GONE
        ll3.visibility = View.GONE
        ll5.visibility = View.GONE

        if (discoverAdapterNew != null) {
            discoverAdapterNew?.notifyDataSetChanged()
        }

        if (discoverTopicsArray != null) {
            discoverTopicsArray.forEach {
                when (it.type) {
                    "TOPICS" -> {
                        discoverPresenter.getDiscoverTrendingTopics(it.context)
                    }
                    "CHANNELS" -> {
                        discoverPresenter.getDiscoverTrendingChannels(
                            it.context
                        )
                    }
                }
            }
        }

    }

    fun reload() {
        ed_searchtext.text.clear()
        ed_searchtext.clearFocus()
        ll_no_results?.visibility = View.GONE
        discoverNewSwitcher?.visibility = View.VISIBLE
        Utils.loadSkeletonLoader(discoverNewSwitcher, true)
        ll3.visibility = View.GONE
        ll5.visibility = View.GONE
        if (::discoverPresenter.isInitialized) {
            discoverPresenter.getDiscoverTopics()
            discoverPresenter.getSearchHistory()
        }
    }

//    fun focus() {
//
//    }
//
//    fun clearText() {
//
//    }

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
            discoverAdapterNew!!.updateChannelFollowPosition(position, channel)
        }
    }

    override fun getChannelsunFollow(
        response: FollowResponse?,
        position: Int,
        channel: DiscoverNewChannels?
    ) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            discoverAdapterNew!!.updateChannelFollowPosition(position, channel)
        }
    }

    override fun loadingData(isLoading: Boolean) {
        if (isLoading) {
            Utils.loadSkeletonLoader(discoverNewSwitcher, isLoading)
        } else if (!isLoading && discoverAdapterNew!!.getDiscoverItems().isNotEmpty()) {
            Utils.loadSkeletonLoader(discoverNewSwitcher, isLoading)
        } else {
            shimmer_view_container?.let {
                it.visibility = View.GONE
            }
            discover_items?.let {
                it.visibility = View.GONE
            }
            ll_no_results?.let {
                it.visibility = View.VISIBLE
            }
        }
    }

    override fun error(error: String, topic: String) {
        if (topic == NewDiscoverPresenter.DISCOVER_TOPIC) {
            ll_no_results?.visibility = View.VISIBLE
            discoverNewSwitcher?.visibility = View.GONE
        }
        if (topic == NewDiscoverPresenter.DISCOVER_TRENDING_TOPICS) {
            var position = -1
            discoverTopicsArray.forEachIndexed { index, discoverNew ->
                if (discoverNew.type == "TOPICS")
                    position = index
            }
            if (position >= 0) {
                discoverTopicsArray.removeAt(position)
                discoverAdapterNew?.updateDiscoverItems(discoverTopicsArray)
            }
        }
        if (topic == NewDiscoverPresenter.DISCOVER_TRENDING_REELS) {
            var position = -1
            discoverTopicsArray.forEachIndexed { index, discoverNew ->
                if (discoverNew.type == "REELS")
                    position = index
            }
            if (position >= 0) {
                discoverTopicsArray.removeAt(position)
                discoverAdapterNew?.updateDiscoverItems(discoverTopicsArray)
            }
        }
        if (topic == NewDiscoverPresenter.DISCOVER_TRENDING_CHANNELS) {
            var position = -1
            discoverTopicsArray.forEachIndexed { index, discoverNew ->
                if (discoverNew.type == "CHANNELS")
                    position = index
            }
            if (position >= 0) {
                discoverTopicsArray.removeAt(position)
                discoverAdapterNew?.updateDiscoverItems(discoverTopicsArray)
            }
        }
        if (topic == NewDiscoverPresenter.DISCOVER_TRENDING_LIVE_CHANNELS) {
            var position = -1
            discoverTopicsArray.forEachIndexed { index, discoverNew ->
                if (discoverNew.type == "YOUTUBE_LIVE")
                    position = index
            }
            if (position >= 0) {
                discoverTopicsArray.removeAt(position)
                discoverAdapterNew?.updateDiscoverItems(discoverTopicsArray)
            }
        }
        if (topic == NewDiscoverPresenter.WEATHER_FORECAST) {
            var position = -1
            discoverTopicsArray.forEachIndexed { index, discoverNew ->
                if (discoverNew.type == "WEATHER")
                    position = index
            }
            if (position >= 0) {
                discoverTopicsArray.removeAt(position)
                discoverAdapterNew?.updateDiscoverItems(discoverTopicsArray)
            }
        }
        if (topic == NewDiscoverPresenter.CRYPTO) {
            var position = -1
            discoverTopicsArray.forEachIndexed { index, discoverNew ->
                if (discoverNew.type == "FINANCE")
                    position = index
            }
            if (position >= 0) {
                discoverTopicsArray.removeAt(position)
                discoverAdapterNew?.updateDiscoverItems(discoverTopicsArray)
            }
        }
        if (topic == NewDiscoverPresenter.FOREX) {
            var position = -1
            discoverTopicsArray.forEachIndexed { index, discoverNew ->
                if (discoverNew.type == "CURRENCY")
                    position = index
            }
            if (position >= 0) {
                discoverTopicsArray.removeAt(position)
                discoverAdapterNew?.updateDiscoverItems(discoverTopicsArray)
            }
        }
        if (topic == NewDiscoverPresenter.LIVE_SCORE) {
            var position = -1
            discoverTopicsArray.forEachIndexed { index, discoverNew ->
                if (discoverNew.type == "SPORTS")
                    position = index
            }
            if (position >= 0) {
                discoverTopicsArray.removeAt(position)
                discoverAdapterNew?.updateDiscoverItems(discoverTopicsArray)
            }
        }
        if (topic == NewDiscoverPresenter.DISCOVER_TRENDING_ARTICLES) {
            var position = -1
            discoverTopicsArray.forEachIndexed { index, discoverNew ->
                if (discoverNew.type == "ARTICLE")
                    position = index
            }
            if (position >= 0) {
                discoverTopicsArray.removeAt(position)
                discoverAdapterNew?.updateDiscoverItems(discoverTopicsArray)
            }
        }
    }

    override fun getDiscoverTopics(response: DiscoverNewResponse?) {
        discoverTopicsArray.clear()
        if (response != null && !response.discover.isNullOrEmpty()) {
            if (view != null) {
                if (ll5.visibility == View.GONE) {
                    ll_no_results?.visibility = View.GONE
                    discoverNewSwitcher?.visibility = View.VISIBLE
                }
            }
            var articlesIndex = -1
            var reelsIndex = -1
            response.discover.forEachIndexed { index, it ->
                when (it.type) {
                    "TOPICS" -> {
                        discoverTopicsArray.add(it)
                        discoverPresenter.getDiscoverTrendingTopics(it.context)
                    }
                    "REELS" -> {
                        reelsIndex = index
                        discoverTopicsArray.add(it)
                        discoverPresenter.getDiscoverReels(it.context)
                    }
                    "CHANNELS" -> {
                        discoverTopicsArray.add(it)
                        discoverPresenter.getDiscoverTrendingChannels(
                            it.context
                        )
                    }
//                    "YOUTUBE_LIVE" -> {
//                        discoverTopicsArray.add(it)
//                        discoverPresenter.getDiscoverLiveChannels(
//                            it.context
//                        )
//                    }
                    "WEATHER" -> {
                        if (it.country != null) {
                            countryWeather = it.country
                            discoverTopicsArray.add(it)
                            discoverPresenter.getWeatherForecast(
                                it.country,
                                "5",
                                "yes"
                            )
                        }
                    }
//                    "FINANCE" -> {
//                        discoverTopicsArray.add(it)
//                        if (tradingIconsResponse != null) {
//                            discoverPresenter.getCryptoPrices(
//                                BuildConfig.CRYPTO_URL,
//                                BuildConfig.POLYGON_TOKEN
//                            )
//                        }
//                    }
                    "SPORTS" -> {
                        discoverTopicsArray.add(it)
                        val sdf = SimpleDateFormat("yyyyMMdd Z", Locale.getDefault())
                        val calendar = Calendar.getInstance()
                        val date = sdf.format(calendar.time)
                        discoverPresenter.getLiveScore(discoverPresenter.sportCategory, date)
                    }
                    "ARTICLE" -> {
                        articlesIndex = index
                        discoverTopicsArray.add(it)
                        discoverPresenter.getDiscoverTrendingNews(
                            it.context
                        )
                    }
//                    "STOCK" -> {
//
//                    }
//                    "CURRENCY" -> {
//                        discoverTopicsArray.add(it)
//                        if (tradingIconsResponse == null) {
//                            if (!gettingIcons) {
//                                gettingIcons = true
//                                discoverPresenter.getTradingItemsList()
//                            }
//                        } else {
//                            discoverPresenter.getForexPrices(
//                                BuildConfig.FOREX_URL,
//                                BuildConfig.POLYGON_TOKEN
//                            )
//                        }
//                    }
                }
            }
            try {
                Collections.swap(discoverTopicsArray, reelsIndex, 0)
                Collections.swap(discoverTopicsArray, articlesIndex, 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            discoverAdapterNew!!.updateDiscoverItems(discoverTopicsArray)
        } else {
            discoverNewSwitcher?.visibility = View.GONE
            ll_no_results?.visibility = View.VISIBLE
        }
    }

    private fun startTime() {
        if (timer == null) {
            timer = Timer()
        }
        if (timerTask == null) {
            timerTask = object : TimerTask() {
                override fun run() {
                    try {
                        requireActivity().runOnUiThread {
                            if (::discoverPresenter.isInitialized) {
                                discoverPresenter.getForexPrices(
                                    BuildConfig.FOREX_URL,
                                    BuildConfig.POLYGON_TOKEN
                                )
                                val sdf = SimpleDateFormat("yyyyMMdd Z", Locale.getDefault())
                                val calendar = Calendar.getInstance()
                                val date = sdf.format(calendar.time)
                                discoverPresenter.getLiveScore(
                                    discoverPresenter.sportCategory,
                                    date
                                )
                                discoverPresenter.getCryptoPrices(
                                    BuildConfig.CRYPTO_URL,
                                    BuildConfig.POLYGON_TOKEN
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            timer!!.schedule(
                timerTask,
                Constants.DISCOVER_REFRESH_DELAY,
                Constants.DISCOVER_REFRESH_DELAY
            )
        }
    }

    private fun stopTimer() {
        if (timer != null && timerTask != null) {
            timerTask?.cancel()
            timer?.cancel()
            timer?.purge()
            timer = null
            timerTask = null
        }
    }

    override fun getPlacesOrder(response: PlacesOrderBase?) {
    }

    override fun getLiveScore(category: String, liveScoreApiResponse: LiveScoreApiResponse?) {
        if (liveScoreApiResponse != null) {
            discoverAdapterNew?.updateLiveScore(category, liveScoreApiResponse.Stages)
//            discoverAdapterNew?.notifyItemChanged(2)
            discoverAdapterNew?.notifyItemChanged(DiscoverAdapterNew.SPORTS_SCORE_POSITION)
        }
    }

    override fun getDiscoverReels(reelsResponse: List<ReelsItem>?) {
        if (!reelsResponse.isNullOrEmpty()) {
            discoverAdapterNew!!.updateReelsData(reelsResponse)
        }
    }

    override fun refreshHistory() {
//        discoverPresenter.getSearchHistory()
    }

    override fun onSearchArticleSuccess(response: ArticleBase?, pagination: Boolean) {
    }


    override fun getSearchHistory(searchHistory: List<History>?) {

        if (view != null) {
            if (!searchHistory.isNullOrEmpty()) {
                searchHistoryAvailable = true
                searchHisData = searchHistory
                //            ll5.visibility = View.VISIBLE
                //            ll3.visibility = View.VISIBLE

                if (::searchHistoryAdapter.isInitialized) {
                    if (searchHistory.size > 10) {
                        searchtake10 = searchHistory.take(10)
                        searchHistoryAdapter?.updateDiscoverItems(searchtake10 as ArrayList<History>?)
                    } else {
                        searchHistoryAdapter?.updateDiscoverItems(searchHistory as ArrayList<History>?)
                    }
                }

            } else {
                searchHistoryAvailable = false
                searchHisData = searchHistory
                ll3.visibility = View.GONE
                ll5.visibility = View.GONE
                discoverNewSwitcher?.visibility = View.VISIBLE
            }
        }
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
        if (!topicsResponse.isNullOrEmpty()) {
            this.discoverTrendingTopics.clear()
            this.discoverTrendingTopics.addAll(topicsResponse)
            discoverAdapterNew!!.updateTopicsData(topicsResponse)
        }
    }

    override fun getDiscoverTrendingNews(trendingNews: List<Article>?) {
        if (trendingNews != null) {
            discoverAdapterNew!!.updateTrendingNews(trendingNews)
        }
    }

    override fun getDiscoverTrendingChannels(trendingChannels: List<DiscoverNewChannels>?) {
        if (!trendingChannels.isNullOrEmpty()) {
            this.discoverTrendingChannels.clear()
            this.discoverTrendingChannels.addAll(trendingChannels)
            discoverAdapterNew!!.updateChannelsData(trendingChannels)
        }
    }

    override fun getDiscoverLiveChannels(liveChannels: List<DiscoverNewLiveChannels>?) {
        if (!liveChannels.isNullOrEmpty()) {
            discoverAdapterNew!!.updateLiveChannelsData(liveChannels)
        }
    }

    override fun getWeatherForecast(weatherForecastResponse: WeatherForecastResponse?) {
        if (weatherForecastResponse != null) {
            weatherForecastResponseNew = weatherForecastResponse
            discoverAdapterNew!!.updateWeatherForecast(weatherForecastResponse)
        }
    }

    override fun getTradingItemsList(tradingIconsResponse: TradingIconsResponse?) {
        if (tradingIconsResponse != null) {
            this.tradingIconsResponse = tradingIconsResponse
            discoverPresenter.getCryptoPrices(
                BuildConfig.CRYPTO_URL,
                BuildConfig.POLYGON_TOKEN
            )
            discoverPresenter.getForexPrices(
                BuildConfig.FOREX_URL,
                BuildConfig.POLYGON_TOKEN
            )
        }
    }

    override fun getCryptoPrices(cryptoForexApiResponse: CryptoForexApiResponse?) {
        if (cryptoForexApiResponse != null) {
            var tickerList = mutableListOf<Ticker>()
            tradingIconsResponse?.crypto?.forEach {
                kotlin.run {
                    cryptoForexApiResponse.tickers?.forEach { ticker ->
                        val tickerName = ticker.ticker?.substring(startIndex = 2)
                        if (tickerName.equals("${it.Code}USD", true)) {
                            ticker.ticker = it.Code
                            ticker.icon = it.Image
                            ticker.tickerName = it.Name
                            tickerList.add(ticker)
                            return@run
                        }
                    }
                }
            }
            if (tickerList.isNotEmpty()) {
                cryptoForexApiResponse.tickers = tickerList

                cryptoPrices = cryptoForexApiResponse

                discoverAdapterNew!!.updateCryptoPrices(cryptoForexApiResponse)
            }
        }
    }

    override fun getForexPrices(cryptoForexApiResponse: CryptoForexApiResponse?) {
        if (cryptoForexApiResponse != null) {
            var tickerList = mutableListOf<Ticker>()
            tradingIconsResponse?.currency?.forEach {
                kotlin.run {
                    cryptoForexApiResponse.tickers?.forEach { ticker ->
                        val tickerName = ticker.ticker?.substring(startIndex = 2)
                        if (tickerName.equals("${it.From}${it.To}", true)) {
                            var iconFrom = ""
                            var iconTo = ""
                            var fromName = ""
                            var toName = ""
                            tradingIconsResponse?.forex?.forEach { forex ->
                                if (it.From.equals(forex.Code, true)) {
                                    iconFrom = forex.Image
                                    fromName = forex.Name
                                }
                                if (it.To.equals(forex.Code, true)) {
                                    iconTo = forex.Image
                                    toName = forex.Name
                                }

                            }
                            ticker.ticker = "${it.From}/${it.To}"
                            ticker.iconFrom = iconFrom
                            ticker.iconTo = iconTo
                            ticker.fromName = fromName
                            ticker.toName = toName
                            tickerList.add(ticker)
                            return@run
                        } /*else if (tickerName.equals("${it.To}${it.From}", true)) {
                            var iconFrom = ""
                            var iconTo = ""
                            var fromName = ""
                            var toName = ""
                            tradingIconsResponse?.forex?.forEach { forex ->
                                if (it.From.equals(forex.Code, true)) {
                                    iconFrom = forex.Image
                                    fromName = forex.Name
                                }
                                if (it.To.equals(forex.Code, true)) {
                                    iconTo = forex.Image
                                    toName = forex.Name
                                }
                            }
                            ticker.ticker = "${it.To}/${it.From}"
                            ticker.iconFrom = iconTo
                            ticker.iconTo = iconFrom
                            ticker.fromName = toName
                            ticker.toName = fromName
                            tickerList.add(ticker)
                            return@run
                        }*/
                    }
                }
            }
            if (tickerList.isNotEmpty()) {
                cryptoForexApiResponse.tickers = tickerList

                forexPrices = cryptoForexApiResponse
//                discoverAdapterNew!!.updateCryptoPrices(cryptoForexApiResponse)
                discoverAdapterNew!!.updateForexPrices(cryptoForexApiResponse)
            }
        }
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
            intent.putExtra(ReelInnerActivity.REEL_F_AUTHOR_ID, reelsItem.author[0].id)
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, "userContext")
        }
        if (reelsItem.source != null) {
            intent.putExtra(ReelInnerActivity.REEL_F_SOURCE_ID, reelsItem.source.id)
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, "userContext")
        }
        intent.putExtra(ReelInnerActivity.REEL_F_MODE, "public")
        intent.putExtra(ReelInnerActivity.REEL_F_TITLE, "Trending Reels")
        intent.putExtra(ReelInnerActivity.REEL_POSITION, 0)
        intent.putExtra("f", position)
        intent.putExtra(ReelInnerActivity.REEL_F_PAGE, "")//page
        intent.putParcelableArrayListExtra(
            ReelInnerActivity.REEL_F_DATALIST,
            reelsList as ArrayList<ReelsItem>?
        )
        requireContext().startActivity(intent)
    }

    override fun searchChildOnTopicClick(trendingTopics: List<DiscoverNewTopic>) {
        navigateTopicsAll(trendingTopics)
    }

    override fun searchChildTopicSecondOnClick(trendingTopic: DiscoverNewTopic) {
        navigateTopic(trendingTopic)
    }

    override fun onTopicFollowed(topic: DiscoverNewTopic?, position: Int) {
        discoverPresenter.getTopicsFollowPresenter(topic!!.id, position, topic)
    }

    override fun onTopicUnfollowed(topic: DiscoverNewTopic?, position: Int) {
        discoverPresenter.getTopicsUnfollowPresenter(topic!!.id, position, topic)
    }

    override fun getTopicsFollow(body: FollowResponse?, position: Int, topic: DiscoverNewTopic?) {
        discoverAdapterNew?.updateTrendingTopicItem(position, topic)
    }

    override fun searchChildOnChannelClick(trendingChannels: List<DiscoverNewChannels>) {
        navigateChannel(trendingChannels)
    }

    override fun searchChildChannelSecondOnClick(discoverNewChannels: DiscoverNewChannels) {
        navigateChanel(discoverNewChannels)
    }

    override fun onItemChannelFollowed(channel: DiscoverNewChannels?, position: Int) {
        channel?.id?.let { discoverPresenter.getChannelsFollowPresenter(it, position, channel) }
    }

    override fun discoverLiveChannelClick(liveChannels: DiscoverNewLiveChannels) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(liveChannels.link)
        startActivity(intent)
    }

    override fun onArticleSeeAll() {
        navigateArticleList()
    }

    override fun onCryptoSeeAllClick(cryptoForexApiResponse: CryptoForexApiResponse?) {
        navigateCrypto(cryptoForexApiResponse)
    }

    override fun onForexSeeAllClick(cryptoForexApiResponse: CryptoForexApiResponse?) {
        navigateForex(cryptoForexApiResponse)
    }

    override fun searchChildWeatherSecondOnClick(forecast: WeatherForecastResponse) {
        navigateWeather(forecast)
    }

    override fun updateSports(category: String) {
        discoverPresenter.sportCategory = category
        val sdf = SimpleDateFormat("yyyyMMdd Z", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val date = sdf.format(calendar.time)
        discoverPresenter.getLiveScore(category, date)
    }

    override fun moveSportsDetailPage(categorySelected: String, liveEvent: SportEvent) {
        backCallback.isEnabled = true
        if (categorySelected == "cricket") {
            val intent = Intent(context, SportsDetailActivity::class.java)
            intent.putExtra("sportsDeatil", liveEvent)
            requireContext().startActivity(intent)
        } /*else if (categorySelected == "soccer") {

        }*/


    }

    private fun navigateArticleList() {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        bundle1.putString("query", "")
        transaction.add(
            R.id.framel,
            SearchArticlesFragment.getInstance(bundle1, goHome),
            "articlelist"
        )
        transaction.addToBackStack("articlelist")
        transaction.commit()
    }

    override fun onItemChannelUnfollowed(channel: DiscoverNewChannels?, position: Int) {
        channel?.id?.let {
            discoverPresenter.getChannelUnfollowPresenter(
                it,
                position,
                channel
            )
        }
    }

    fun navigateChanel(discoverNewChannels: DiscoverNewChannels) {

        var intent = Intent(context, ChannelDetailsActivity::class.java)
        intent.putExtra("id", discoverNewChannels.id)
        startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST)

    }

    //Channel
    private fun navigateChannel(trendingChannels: List<DiscoverNewChannels>) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(
            R.id.framel,
            SearchChannelFragment.getInstance(bundle1, goHome),
            "searchchannel"
        )
        transaction.addToBackStack("searchchannel")
        transaction.commit()
    }

    private fun navigateCrypto(cryptoApiResponse: CryptoForexApiResponse?) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        if (cryptoApiResponse != null) {
            bundle1.putParcelable("CryptoResponse", cryptoApiResponse)
        }

        val oldFragment = manager.findFragmentByTag("cryptopage")
        if (oldFragment != null) {
            manager.beginTransaction().remove(oldFragment).commit()
        }
        transaction.add(
            R.id.framel,
            CryptoSeeAllFragment.getInstance(bundle1, goHome),
            "cryptopage"
        )
        transaction.addToBackStack("cryptopage")
        transaction.commit()
    }


    private fun navigateForex(forexApiResponse: CryptoForexApiResponse?) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()

        if (forexPrices != null) {
            bundle1.putParcelable("ForexResponse", forexApiResponse)
        }

        transaction.add(
            R.id.framel,
            ForexSeeAllFragment.getInstance(bundle1, goHome),
            "forexpage"
        )
        transaction.addToBackStack("forexpage")
        transaction.commit()

    }

    private fun navigateWeather(forecast: WeatherForecastResponse) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true

        val intent = Intent(context, WeatherActivity::class.java)
        intent.putExtra("weatherInfo", forecast)
        requireContext().startActivity(intent)
    }

    fun navigateTopic(trendingTopic: DiscoverNewTopic) {
        val intent = Intent(context, ChannelPostActivity::class.java)
        intent.putExtra("type", TYPE.TOPIC)
        intent.putExtra("id", trendingTopic.id)
        intent.putExtra("context", trendingTopic.context)
        intent.putExtra("name", trendingTopic.name)
        intent.putExtra("favorite", trendingTopic.favorite)
        requireContext().startActivity(intent)
    }

    private fun navigateTopicsAll(trendingTopics: List<DiscoverNewTopic>) {
        val intent = Intent(context, TopicsActivity::class.java)
        intent.putExtra("query", "")
        requireContext().startActivity(intent)
    }

    private fun navigateReelsList(reelsList: List<ReelsItem>) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(
            R.id.framel,
            SearchReelsFragment.getInstance(bundle2, goHome),
            "searchreel"
        )
        transaction.addToBackStack("searchreel")
        transaction.commit()
    }

    override fun onItemClick(
        position: Int,
        item: History
    ) {
        disquery = item.search.trim()
        bundle.putString("query", disquery)
        navigateSearch()

    }

    override fun onItemClick(position: Int) {

    }

    override fun onItemCancelClick(position: Int, item: History?) {

        if (item != null) {
//            discoverPresenter.deleteHistory(item.id)
        }
    }

}