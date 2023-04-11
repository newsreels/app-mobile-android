package com.ziro.bullet.fragments.searchNew

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.ziro.bullet.R
import com.ziro.bullet.activities.*
import com.ziro.bullet.adapters.searchhistory.SearchHistoryAdapter
import com.ziro.bullet.bottomSheet.ShareBottomSheet
import com.ziro.bullet.data.TYPE
import com.ziro.bullet.data.models.ShareInfo
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.fragments.Search.SearchArticlesFragment
import com.ziro.bullet.fragments.Search.SearchReelsFragment
import com.ziro.bullet.fragments.searchNew.channelpackage.SearchChannelFragment
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.fragments.searchNew.interfaces.SearchResultsViewInterface
import com.ziro.bullet.fragments.searchNew.locationnew.LocationSeeAllFragment
import com.ziro.bullet.fragments.searchNew.locationnew.PlacesListFragmentNew3
import com.ziro.bullet.fragments.searchNew.presenter.SearchResultPresenter
import com.ziro.bullet.fragments.test.ReelInnerActivity
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.AudioObject
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articlenew.ArticleBase
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.discoverNew.*
import com.ziro.bullet.model.discoverNew.liveScore.LiveScoreApiResponse
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.trading.icons.TradingIconsResponse
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import com.ziro.bullet.model.places.PlacesOrderBase
import com.ziro.bullet.model.searchhistory.History
import com.ziro.bullet.model.searchhistorydelete.DeleteHistory
import com.ziro.bullet.model.searchresultnew.SearchresultData
import com.ziro.bullet.model.searchresultnew.SearchresultdataBase
import com.ziro.bullet.presenter.NewDiscoverPresenter
import com.ziro.bullet.utills.Constants
import java.util.*


class SearchResultFragment : Fragment(), SearchResultsViewInterface, SearchFirstChildInterface,
    DiscoverResponseInterface, AdapterItemCallback {
    private lateinit var searchResultPresenter: SearchResultPresenter
    private lateinit var searchResultAdapterNew: SearchableAdapterNew
    var searchquery: String? = ""
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private var searchHistoryAvailable: Boolean = false
    var rv_search: RecyclerView? = null
    var rvSearchHistory: RecyclerView? = null
    var ll3: RelativeLayout? = null
    var ll_no_results: LinearLayout? = null
    var search_skeleton: LinearLayout? = null
    private var ed_searchtab: EditText? = null
    private var tvcancel: TextView? = null
    private var clear: TextView? = null
    private var iccross: ImageView? = null
    val bundle1 = Bundle()
    val bundle = Bundle()
    private var searchistoryitems: List<History>? = null
    private var searchResultData: MutableList<SearchresultData>? = null
    private val searchResultsTopics = mutableListOf<DiscoverNewTopic>()
    var searchtake10: List<History>? = null
    private lateinit var discoverPresenter: NewDiscoverPresenter
    private var mArticlePosition = 0
    private var shareBottomSheet: ShareBottomSheet? = null

    var onGotoChannelListener: OnGotoChannelListener = object : OnGotoChannelListener {
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
                Log.e("expandCard", "newInstance 3")
                val intent = Intent(context, ChannelDetailsActivity::class.java)
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                intent.putExtra("name", name)
                intent.putExtra("favorite", favorite)
                intent.putExtra("article_id", Constants.articleId)
                intent.putExtra("position", mArticlePosition)
                startActivityForResult(intent, Constants.CommentsRequestCode)
                //                startActivity(intent);
                // finish();
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
            Log.e("@@@", "ITEM CLICKED")

            //Resetting the audio data to avoid old article speech while loading new data
            Constants.articleId = ""
            Constants.speech = ""
            Constants.url = ""
            Log.d("audiotest", " onitemclick : stop_destroy")
            goHome!!.sendAudioEvent("stop_destroy")
            // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);
            if (type != null && type == TYPE.MANAGE) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
                Constants.canAudioPlay = true
                Log.e("expandCard", "newInstance 3")
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

            if (rvSearchHistory?.visibility == View.VISIBLE) {
                ll3?.visibility = View.GONE
                rvSearchHistory?.visibility = View.GONE
//                ed_searchtab?.text?.clear()
                ed_searchtab?.clearFocus()
                rv_search?.visibility = View.VISIBLE
                val imm =
                    activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.windowToken, 0)

            } else {
                if (childFragmentManager.backStackEntryCount > 0) {
                    Log.d("Search_Topic_TAG", "handleOnBackPressed: Search Result Fragment")
                    updateListData()
                    childFragmentManager.popBackStackImmediate()
                    return
                }
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    companion object {
        private var searchresultsNew: SearchResultFragment? = null

        //        lateinit var searchResultItem: SearchresultdataBase
        private var goHome: GoHome? = null

        fun getInstance(bundle: Bundle, goHome: GoHome): SearchResultFragment {
//            if (searchresultsNew == null)
            searchresultsNew = SearchResultFragment()
            searchresultsNew!!.arguments = bundle
            this.goHome = goHome
            return searchresultsNew!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_search_result_new, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //make nav bar visible
//        goHome?.scrollDown()

        //make nav bar gone
        if (goHome != null) goHome!!.scrollUp()

        //RV 1
        rv_search = view.findViewById(R.id.rv_search)
        ed_searchtab = view.findViewById(R.id.ed_searchtab)
        tvcancel = view.findViewById(R.id.tvcancel)
        clear = view.findViewById(R.id.clear)
        iccross = view.findViewById(R.id.imgCross)
        rvSearchHistory = view.findViewById(R.id.rvSearchHistory)
        ll3 = view.findViewById(R.id.ll3)
        ll_no_results = view.findViewById(R.id.ll_no_results)
        search_skeleton = view.findViewById(R.id.search_skeleton)

        searchResultAdapterNew = SearchableAdapterNew()
        searchResultAdapterNew.setParametersForArticles(
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
            lifecycle, object : AdapterCallback {
                override fun getArticlePosition(): Int {
                    return 0
                }

                override fun showShareBottomSheet(
                    shareInfo: ShareInfo?,
                    article: Article?,
                    onDismissListener: DialogInterface.OnDismissListener?
                ) {
                    if (shareInfo == null || article == null)
                        return
                    showBottomSheetDialog(shareInfo, article, onDismissListener!!)
                }

                override fun onItemClick(position: Int, setCurrentView: Boolean) {
                }

            }
        )
        rv_search?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        searchResultAdapterNew.setHasStableIds(true)
        rv_search?.adapter = searchResultAdapterNew
        if (goHome != null) goHome!!.scrollUp()
        rv_search?.setItemViewCacheSize(3)
        // RV 2
        if (!::searchHistoryAdapter.isInitialized) {
            searchHistoryAdapter = SearchHistoryAdapter(requireActivity(), this)
            searchResultPresenter = SearchResultPresenter(requireActivity(), this)
        }
        if (rvSearchHistory?.adapter == null) {
            rvSearchHistory?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvSearchHistory?.adapter = searchHistoryAdapter
//            val itemDecorator =
//                DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
//            rvSearchDetails?.addItemDecoration(itemDecorator)
            rvSearchHistory?.layoutManager!!.scrollToPosition(0)
        }

//        searchHistoryAdapter = SearchHistoryAdapter(requireActivity(), this, null)
//        rvSearchDetails?.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        if (!searchHistoryAdapter?.hasObservers()!!) {
//            searchHistoryAdapter?.setHasStableIds(true)
//        }
//////        searchHistoryAdapter!!.setHasStableIds(true)
//        rvSearchDetails?.adapter = searchHistoryAdapter
//        rvSearchDetails?.setItemViewCacheSize(3)

        //hide the keypad
//        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
//        hideKeyboard()

        init()
    }

    override fun onResume() {
        super.onResume()
        Log.d("Search_Topic_TAG", "onResume: Search Resumed")
        updateListData()
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    fun init() {
        ed_searchtab?.text?.clear()
        ed_searchtab?.clearFocus()
        searchResultPresenter = SearchResultPresenter(requireActivity(), this)
        discoverPresenter = NewDiscoverPresenter(requireActivity(), this)

        val bundle = arguments
        if (bundle!!.getString("query") != null) {
            searchquery = bundle.getString("query")
            ed_searchtab?.setText(searchquery)
        }
        searchResultPresenter.getSearchResults(searchquery)
        clear?.setOnClickListener {
            discoverPresenter.clearHistory()
        }

        iccross?.setOnClickListener {
            ed_searchtab?.text?.clear()
        }

        if (!::searchResultAdapterNew.isInitialized) {
            searchResultAdapterNew = SearchableAdapterNew()
            searchResultPresenter = SearchResultPresenter(requireActivity(), this)
            rv_search?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            searchResultAdapterNew.setHasStableIds(true)
            rv_search?.adapter = searchResultAdapterNew
            rv_search?.setItemViewCacheSize(3)
        }

        searchResultAdapterNew.addChildListener(this)
//        activity?.let { searchresultAdapterNew.updatelaocation(it) }

        discoverPresenter.getSearchHistory()

        ed_searchtab?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchHistoryAvailable && !searchistoryitems.isNullOrEmpty()) {
                rvSearchHistory?.visibility = View.VISIBLE
                ll3?.visibility = View.VISIBLE
                rv_search?.visibility = View.GONE
            } else {
                if (!searchResultData.isNullOrEmpty()) {
                    rvSearchHistory?.visibility = View.GONE
                    ll3?.visibility = View.GONE
                    rv_search?.visibility = View.VISIBLE
                }
            }
        }

        ed_searchtab?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true) {
                    searchquery = s.toString()
                }

                if (!searchistoryitems.isNullOrEmpty()) {
                    rvSearchHistory?.visibility = View.VISIBLE
                    ll3?.visibility = View.VISIBLE
                    rv_search?.visibility = View.GONE
                } else {
                    rvSearchHistory?.visibility = View.GONE
                    ll3?.visibility = View.GONE
                    rv_search?.visibility = View.VISIBLE
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!searchistoryitems.isNullOrEmpty()) {
                    rvSearchHistory?.visibility = View.VISIBLE
                    ll3?.visibility = View.VISIBLE
                    rv_search?.visibility = View.GONE
                } else {
                    rvSearchHistory?.visibility = View.GONE
                    ll3?.visibility = View.GONE
                    rv_search?.visibility = View.VISIBLE
                }


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!searchistoryitems.isNullOrEmpty()) {
                    rvSearchHistory?.visibility = View.VISIBLE
                    ll3?.visibility = View.VISIBLE
                    rv_search?.visibility = View.GONE
                } else {
                    rvSearchHistory?.visibility = View.GONE
                    ll3?.visibility = View.GONE
                    rv_search?.visibility = View.VISIBLE
                }
            }
        })
        ed_searchtab?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!ed_searchtab?.text.isNullOrEmpty()) {
                    searchViewChanges(searchquery!!)
                    searchResultPresenter.getSearchResults(searchquery)
                } else {
                    hideKeyboard()
                }
            }
            false
        })

        tvcancel?.setOnClickListener {
            if (rvSearchHistory?.visibility == View.VISIBLE) {
                ll3?.visibility = View.GONE
                rvSearchHistory?.visibility = View.GONE
                ed_searchtab?.text?.clear()
                ed_searchtab?.clearFocus()
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)

            } else {
                requireActivity().onBackPressed()
            }
        }
    }

    fun updateListData() {
        if (Constants.sourceStatusChanged != null && ::searchResultAdapterNew.isInitialized) {
            searchResultAdapterNew.updateChannelStatus()
            if (childFragmentManager.backStackEntryCount == 0) {
                Constants.isSourceDataChange = false
                Constants.sourceStatusChanged = null
                Constants.followStatus = null
                Constants.itemPosition = -1
            }
        }
        if (Constants.topicsStatusChanged != null && ::searchResultAdapterNew.isInitialized) {
            searchResultAdapterNew.updateTopicStatus()
        }
    }

    private fun showBottomSheetDialog(
        shareInfo: ShareInfo,
        article: Article,
        onDismissListener: DialogInterface.OnDismissListener
    ) {
        if (shareBottomSheet == null) {
            shareBottomSheet =
                ShareBottomSheet(requireActivity(), shareToMainInterface, true, "ARTICLES")
        }
        shareBottomSheet?.show(article, onDismissListener, shareInfo)
    }

    private fun navigateTopic(trendingTopic: DiscoverNewTopic) {
//        val intent = Intent(context, HashTagDetailsActivity::class.java)
        val intent = Intent(context, ChannelPostActivity::class.java)
        intent.putExtra("type", TYPE.TOPIC)
        intent.putExtra("id", trendingTopic.id)
        intent.putExtra("context", trendingTopic.context)
        intent.putExtra("name", trendingTopic.name)
        intent.putExtra("favorite", trendingTopic.favorite)
        requireContext().startActivity(intent)
    }

    private fun navigateLocationSeeAll(locationlist: List<Location>) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        bundle1.putString("query", searchquery)

        val oldFragment = manager.findFragmentByTag("navlocseeall")
        if (oldFragment != null) {
            manager.beginTransaction().remove(oldFragment).commit()
        }
        transaction.add(
            R.id.frame2,
            LocationSeeAllFragment.getInstance(bundle1, goHome!!),
            "navlocseeall"
        )
        transaction.addToBackStack("navlocseeall")
        transaction.commit()
    }

    private fun navigateLocation(location: Location) {

        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        bundle1.putString("query", searchquery)
        bundle1.putString("locationId", location.id)
        bundle1.putString("locationName", location.name)

        val oldFragment = manager.findFragmentByTag("navlocation")
        if (oldFragment != null) {
            manager.beginTransaction().remove(oldFragment).commit()
        }
        transaction.add(
            R.id.frame2,
            PlacesListFragmentNew3.getInstancenew(bundle1, goHome!!),
            "navlocation"
        )
        transaction.addToBackStack("navlocation")
        transaction.commit()
    }

    //Articles
    private fun navigateArticle(article: Article) {
        val intent = Intent(context, BulletDetailActivity::class.java)
        intent.putExtra("article", Gson().toJson(article))
        intent.putExtra("type", "")
        intent.putExtra("position", 0)
        startActivityForResult(intent, Constants.CommentsRequestCode)
//        val manager: FragmentManager = childFragmentManager
//        backCallback.isEnabled = true
//        val transaction: FragmentTransaction = manager.beginTransaction()
//        transaction.add(R.id.frame2, BulletDetailFragment.newInstance(article,"",false), "frame2")
//        transaction.addToBackStack("frame2")
//        transaction.commit()
    }

    private fun navigateArticleList(trendingTopics: List<Article>) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        bundle1.putString("query", searchquery)
        transaction.add(
            R.id.frame2,
            SearchArticlesFragment.getInstance(bundle1, goHome),
            "articlelist"
        )
        transaction.addToBackStack("articlelist")
        transaction.commit()
    }

    //Channel
    private fun navigateChannel(trendingChannels: List<DiscoverNewChannels>) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        bundle1.putString("query", searchquery)

        val oldFragment = manager.findFragmentByTag("searchchannel")
        if (oldFragment != null) {
            manager.beginTransaction().remove(oldFragment).commit()
        }
        val oldFragment2 = manager.findFragmentByTag("searchchannelist")
        if (oldFragment2 != null) {
            manager.beginTransaction().remove(oldFragment2).commit()
        }
        transaction.add(
            R.id.frame2,
            SearchChannelFragment.getInstance(bundle1, goHome!!),
            "searchchannelist"
        )
        transaction.addToBackStack("searchchannelist")
        transaction.commit()
    }


    private fun navigateReelsList(reelsList: List<ReelsItem>) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        bundle1.putString("query", searchquery)
        bundle1.putBoolean("reelsapi", true)

        transaction.add(R.id.frame2, SearchReelsFragment.getInstance(bundle1, goHome!!), "reellist")
        transaction.addToBackStack("reellist")
        transaction.commit()
    }

    private fun navigateTopicsAll(trendingTopics: List<DiscoverNewTopic>) {
        val intent = Intent(context, TopicsActivity::class.java)
        intent.putExtra("query", searchquery)
        requireContext().startActivity(intent)
    }

    private fun navigateChanel(discoverNewChannels: DiscoverNewChannels) {
        var intent = Intent(context, ChannelDetailsActivity::class.java)
        intent.putExtra("id", discoverNewChannels.id)
        startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST)

        //Taken from Reels-->VideoInnerFragment.java --->icon.onclicklistner
//        val intent: Intent
//        if (discoverNewChannels != null) {
//            intent = Intent(context, ChannelDetailsActivity::class.java)
//            intent.putExtra("id", discoverNewChannels.id)
//        } else {
//            val user = PrefConfig(context).isUserObject
//            intent = if (user != null && !TextUtils.isEmpty(user.id) && user.id.equals(
//                    discoverNewChannels.getAuthor().get(0).getId(), ignoreCase = true
//                )
//            ) {
//                Intent(context, ProfileActivity::class.java)
//            } else {
//                Intent(context, AuthorActivity::class.java)
//            }
//            intent.putExtra("authorID", reelsItem.getAuthor().get(0).getId())
//            intent.putExtra("authorContext", reelsItem.getAuthor().get(0).getContext())
//        }
//        startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST)


    }

    override fun loadingData(isLoading: Boolean) {
        if (isLoading) {
            rv_search?.visibility = View.GONE
            search_skeleton?.visibility = View.VISIBLE
        } else {
            rv_search?.visibility = View.VISIBLE
            search_skeleton?.visibility = View.GONE
//            discover_items.visibility = View.GONE
//            ll_no_results?.visibility = View.VISIBLE
        }
    }


    override fun error(error: String, topic: String) {

    }

    override fun getDiscoverTopics(response: DiscoverNewResponse?) {
    }

    override fun getPlacesOrder(response: PlacesOrderBase?) {
    }

    override fun getDiscoverReels(reelsResponse: List<ReelsItem>?) {

    }

    override fun refreshHistory() {

    }

    override fun onSearchArticleSuccess(response: ArticleBase?, pagination: Boolean) {

    }


    override fun getDiscoverTrendingNews(trendingNews: List<Article>?) {

    }

    override fun getSearchTopics(response: DiscoverNewResponse?) {

    }

    override fun getSearchResult(searchResult: SearchresultdataBase?) {
//searchResult.search == se
        searchResultData?.clear()
        discoverPresenter.getSearchHistory()

        if (!(searchResult?.data.isNullOrEmpty())) {
            searchResultData = searchResult?.data as MutableList<SearchresultData>?
            ll_no_results?.visibility = View.GONE
            search_skeleton?.visibility = View.GONE
            rv_search?.visibility = View.VISIBLE
            searchResultAdapterNew.updateDiscoverItems(searchResultData!!)

        } else {
            searchResultData = searchResult?.data as MutableList<SearchresultData>?
            rv_search?.visibility = View.GONE
            search_skeleton?.visibility = View.GONE
            ll_no_results?.visibility = View.VISIBLE
//            searchHistoryAvailable = false
        }

    }


    override fun getTopicsFollow(
        searchResult: FollowResponse?,
        position: Int,
        topic: DiscoverNewTopic?
    ) {
        if (searchResult?.message?.lowercase(Locale.ROOT).equals("success")) {
            searchResultAdapterNew.updateTopicPosition(position, topic)
        }


    }

    override fun getTopicsunFollow(
        response: FollowResponse?,
        position: Int,
        topic: DiscoverNewTopic?
    ) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            searchResultAdapterNew.updateTopicPosition(position, topic)
        }

    }

    override fun getChannelFollow(
        response: FollowResponse?,
        position: Int,
        channel: DiscoverNewChannels?
    ) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            searchResultAdapterNew.updateChannelFollowPosition(position, channel)
        }
    }

    override fun getChannelsunFollow(
        response: FollowResponse?,
        position: Int,
        channel: DiscoverNewChannels?
    ) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            searchResultAdapterNew.updateChannelFollowPosition(position, channel)
        }
    }

    override fun getChannelFollow2(response: FollowResponse?, position: Int, topic: Source?) {

    }

    override fun getChannelsunFollow2(response: FollowResponse?, position: Int, topic: Source?) {

    }

    override fun getLocationFollow2(response: FollowResponse?, position: Int, location: Location?) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
//            searchresultAdapterNew.notifyDataSetChanged()
            searchResultAdapterNew.updateLocationFollowPosition(position, location)
//            searchChannelAdapter.notifyItemChanged(position)
//            searchChannelAdapter.updateChannelFollowPosition2(position, channel)
        }
    }

    override fun getLocationunFollow2(
        response: FollowResponse?,
        position: Int,
        location: Location?
    ) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            searchResultAdapterNew.updateLocationFollowPosition(position, location)
//            searchresultAdapterNew.notifyDataSetChanged()
//            searchChannelAdapter.notifyItemChanged(position)
//            searchChannelAdapter.updateChannelFollowPosition2(position, channel)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun getSearchHistory(searchHistory: List<History>?) {

        if (!searchHistory.isNullOrEmpty()) {
            searchHistoryAvailable = true
            searchistoryitems = searchHistory

            if (searchHistory.size > 10) {
                searchtake10 = searchHistory.take(10)
                searchHistoryAdapter.updateDiscoverItems(searchtake10 as ArrayList<History>?)
            } else {
                searchHistoryAdapter.updateDiscoverItems(searchHistory as ArrayList<History>?)
            }

        } else {
            searchHistoryAvailable = false
            searchistoryitems = searchHistory
            ll3?.visibility = View.GONE
            rvSearchHistory?.visibility = View.GONE
            rv_search?.visibility = View.VISIBLE
//            searchHistoryAdapter.notifyDataSetChanged()
        }
    }

    override fun deleteHistory(deletehistory: DeleteHistory?) {

    }

    override fun clearHistory(clearhistory: FollowResponse?) {

        if (clearhistory?.message?.lowercase(Locale.ROOT).equals("success")) {
            discoverPresenter.getSearchHistory()
//            searchHistoryAdapter?.notifyDataSetChanged()

        }
    }

    private fun searchViewChanges(searchquery: String) {
//        ed_searchtab?.text?.clear()
        ed_searchtab?.setText(searchquery)
        ed_searchtab?.clearFocus()
        rvSearchHistory?.visibility = View.GONE
        ll3?.visibility = View.GONE
        rv_search?.visibility = View.VISIBLE
        hideKeyboard()
//        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }
//    fun Activity.hideKeyboard() {
//        hideKeyboard(currentFocus ?: View(this))
//    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun getDiscoverTrendingTopics(topicsResponse: List<DiscoverNewTopic>?) {

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

    override fun getSearchReels(reelsResponse: List<DiscoverNewReels>?) {
        if (!reelsResponse.isNullOrEmpty()) {
//            searchresultAdapterNew.updateReelsData(reelsResponse)
        }
    }

    override fun searchChannelSuccess(response: SourceModel?, isPagination: Boolean) {

    }

    override fun searchLocationSuccess(response: LocationModel?, isPagination: Boolean) {

    }


    override fun onItemClick(position: Int, item: History?) {
        searchquery = item?.search.toString()



        searchViewChanges(searchquery!!)

        searchResultPresenter.getSearchResults(searchquery)

    }

    override fun onItemClick(position: Int) {

    }

    override fun onItemCancelClick(position: Int, item: History?) {
    }

    override fun searchChildOnClick(reelsList: List<ReelsItem>) {
        navigateReelsList(reelsList)
    }

    fun navigateReels(
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

        intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, reelsItem.context)
        intent.putExtra(ReelInnerActivity.REEL_F_TITLE, searchquery)
//        intent.putExtra(ReelInnerFragment.API_CALL, true)
        intent.putExtra(ReelInnerActivity.REEL_POSITION, 0)
        intent.putExtra(ReelInnerActivity.REEL_F_PAGE, "")//page

        intent.putParcelableArrayListExtra(
            ReelInnerActivity.REEL_F_DATALIST,
            reelsList as ArrayList<ReelsItem>?
        )
        requireContext().startActivity(intent)
    }

    override fun searchChildSecondOnClick(
        reelsItem: ReelsItem,
        reelsList: List<ReelsItem>,
        position: Int
    ) {
        navigateReels(reelsItem, reelsList, position);

    }


    override fun searchChildOnArticleClick(trendingTopics: List<Article>) {
        navigateArticleList(trendingTopics)
    }

    override fun searchChildArticleSecondOnClick(article: Article) {

        if (article.bullets != null) {
            navigateArticle(article)

        }


    }

    override fun searchChildOnChannelClick(trendingChannels: List<DiscoverNewChannels>) {
        navigateChannel(trendingChannels)

    }

    override fun searchChildChannelSecondOnClick(discoverNewChannels: DiscoverNewChannels) {
        navigateChanel(discoverNewChannels)

    }

    override fun searchChildOnTopicClick(trendingTopics: List<DiscoverNewTopic>) {

        navigateTopicsAll(trendingTopics)


    }


    override fun searchChildTopicSecondOnClick(trendingTopic: DiscoverNewTopic) {
        navigateTopic(trendingTopic)

    }

    override fun onItemFollowed(topic: DiscoverNewTopic?, position: Int) {

        topic?.id?.let { searchResultPresenter.getTopicsFollowPresenter(it, position, topic) }


    }

    override fun onItemUnfollowed(topic: DiscoverNewTopic?, position: Int) {
        topic?.id?.let { searchResultPresenter.getTopicsUnfollowPresenter(it, position, topic) }
    }

    override fun onItemChannelFollowed(channel: DiscoverNewChannels?, position: Int) {
//details list page
        channel?.id?.let { searchResultPresenter.getChannelsFollowPresenter(it, position, channel) }
    }

    override fun onItemChannleUnfollowed(channel: DiscoverNewChannels?, position: Int) {
        //details list page
        channel?.id?.let {
            searchResultPresenter.getChannelUnfollowPresenter(
                it,
                position,
                channel
            )
        }
    }

    override fun searchChildLocationSecondOnClick(location: Location) {
        navigateLocation(location)
//        val intent = Intent(context, HashTagDetailsActivity::class.java)
//        intent.putExtra("type", TYPE.LOCATION)
//        intent.putExtra("id", location.id)
//        intent.putExtra("mContext", location.context)
//        intent.putExtra("name", location.name)
//        intent.putExtra("favorite", location.isFavorite)
//        context!!.startActivity(intent)
    }

    override fun searchChildOnPlacesClick(locationlist: List<Location>) {
        navigateLocationSeeAll(locationlist)
    }

    override fun onItemFollowedLoc(location: Location?, position: Int) {
        location?.id?.let { searchResultPresenter.getLocationFollow2(it, position, location) }
    }

    override fun onItemUnfollowedLoc(location: Location?, position: Int) {
        location?.id?.let { searchResultPresenter.getLocationUnfollow2(it, position, location) }
    }


}