package com.ziro.bullet.fragments.discoverdetail.forex

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.BuildConfig
import com.ziro.bullet.R
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.fragments.discoverdetail.crypto.CryptoPresenter
import com.ziro.bullet.fragments.discoverdetail.crypto.CryptoSeeAllInterface
import com.ziro.bullet.fragments.searchNew.interfaces.SearchResultsViewInterface
import com.ziro.bullet.interfaces.GoHome
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewReels
import com.ziro.bullet.model.discoverNew.DiscoverNewResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.trading.Ticker
import com.ziro.bullet.model.discoverNew.trading.icons.TradingIconsResponse
import com.ziro.bullet.model.searchresultnew.SearchresultdataBase
import com.ziro.bullet.utills.PaginationListener


class ForexSeeAllFragment : Fragment(), CryptoSeeAllInterface, SearchResultsViewInterface {
    private lateinit var cryptoPresenter: CryptoPresenter
    var recyclerView1: RecyclerView? = null
    var cardview: CardView? = null
    var forexApiResponse: CryptoForexApiResponse?? = null
    var searchquery: String = ""
    private var ll_no_results: LinearLayout? = null
    private var back_img: ImageView? = null
    var search_skeleton: LinearLayout? = null
    private var currentPage = PaginationListener.PAGE_START
    private var tradingIconsResponse: TradingIconsResponse? = null

    private val tickerArray = ArrayList<Ticker>()
    private var gettingIcons = false
    var topics: List<Source>? = null
    private var manager: LinearLayoutManager? = null
    var tv_search2: TextView? = null
    lateinit var forexAdapter: ForexAdapter

    //    private var searchTopicAdapter:searchTopicAdapter? = null
    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (goHome != null) goHome!!.scrollUp()
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStackImmediate()
                return
            }
            isEnabled = false

            requireActivity().onBackPressed()
        }
    }

    companion object {
        private var forexSeeAllFragment: ForexSeeAllFragment? = null
        private var goHome: GoHome? = null
        private var isLoad: Boolean = false
        private var isLast: Boolean = false

        fun getInstance(bundle1: Bundle, goHome: GoHome?): ForexSeeAllFragment {
            if (forexSeeAllFragment == null)
                forexSeeAllFragment = ForexSeeAllFragment()
            forexSeeAllFragment!!.arguments = bundle1
            this.goHome = goHome
            isLoad = false
            isLast = false
            return forexSeeAllFragment!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_forex, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView1 = view.findViewById(R.id.rv_topic)
        cardview = view.findViewById(R.id.cardview)
        tv_search2 = view.findViewById(R.id.tv_search2)
        ll_no_results = view.findViewById(R.id.ll_no_results)
        back_img = view.findViewById(R.id.back_img)
        search_skeleton = view.findViewById(R.id.search_skeleton)



        init()
//        setPagination()
    }


    override fun onResume() {
        super.onResume()
        if (goHome != null) goHome!!.scrollUp()

    }

    fun init() {
        //hide bottom nav
        if (goHome != null) goHome!!.scrollUp()

        cryptoPresenter = CryptoPresenter(requireActivity(), this)

        val bundle1 = arguments
        forexApiResponse = bundle1?.getParcelable("ForexResponse")

        back_img!!.setOnClickListener { requireActivity().onBackPressed() }

        if (!::forexAdapter.isInitialized) {
            forexAdapter = ForexAdapter(requireActivity(), this)
            cryptoPresenter = CryptoPresenter(requireActivity(), this)
        }
        if (recyclerView1?.adapter == null) {
            recyclerView1?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val itemDecorator = DividerItemDecorator(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.divider_light
                )
            });
            recyclerView1?.addItemDecoration(itemDecorator)
            recyclerView1?.adapter = forexAdapter
            recyclerView1?.layoutManager!!.scrollToPosition(0)
        }
//        cryptoPresenter.getForexPrices(
//            BuildConfig.FOREX_URL,
//            BuildConfig.POLYGON_TOKEN
//        )


        if (forexApiResponse != null) {
            setLayout()
        } else {
            if (!gettingIcons) {
                gettingIcons = true
                cryptoPresenter.getTradingItemsList()
            }
        }

    }

    private fun setPagination() {
        manager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView1?.addOnScrollListener(object : PaginationListener(manager!!) {
            override fun loadMoreItems() {
                isLoad = true
                cryptoPresenter.getCryptoPrices(
                    BuildConfig.CRYPTO_URL,
                    BuildConfig.POLYGON_TOKEN
                )
            }

            override fun isLastPage(): Boolean {
                return isLast
            }

            override fun isLoading(): Boolean {
                return isLoad
            }

            override fun onScroll(position: Int) {
//                Utils.hideKeyboard(TopicsActivity.this, recyclerView);
            }

            override fun onScrolling(recyclerView: RecyclerView, newState: Int) {}
        })
        recyclerView1?.layoutManager = manager
//        searchChannelAdapter.updateChannelsData(channel)
    }

    override fun loadingData(isLoading: Boolean) {

    }

    override fun error(error: String, topic: String) {
        search_skeleton?.visibility = View.GONE
        ll_no_results?.visibility = View.VISIBLE


    }

    override fun getSearchTopics(response: DiscoverNewResponse?) {

    }

    override fun getSearchResult(response: SearchresultdataBase?) {

    }

    override fun getSearchReels(reelsResponse: List<DiscoverNewReels>?) {

    }

    override fun searchChannelSuccess(response: SourceModel?, isPagination: Boolean) {

    }

    override fun searchLocationSuccess(response: LocationModel?, isPagination: Boolean) {

    }

    override fun getTopicsFollow(
        response: FollowResponse?,
        position: Int,
        topic: DiscoverNewTopic?
    ) {

    }

    override fun getTopicsunFollow(
        response: FollowResponse?,
        position: Int,
        topic: DiscoverNewTopic?
    ) {

    }

    override fun getChannelFollow(
        response: FollowResponse?,
        position: Int,
        topic: DiscoverNewChannels?
    ) {

    }

    override fun getChannelsunFollow(
        response: FollowResponse?,
        position: Int,
        topic: DiscoverNewChannels?
    ) {

    }

    override fun getChannelFollow2(response: FollowResponse?, position: Int, channel: Source?) {

    }

    override fun getChannelsunFollow2(response: FollowResponse?, position: Int, channel: Source?) {

    }

    override fun getLocationFollow2(response: FollowResponse?, position: Int, topic: Location?) {

    }

    override fun getLocationunFollow2(response: FollowResponse?, position: Int, topic: Location?) {

    }


    override fun onItemClick(position: Int, item: Ticker?) {

    }

    override fun onItemClickFollow(position: Int, item: Ticker?) {

    }

    override fun getCryptoPrices(cryptoForexApiResponse: CryptoForexApiResponse?) {

    }


    override fun getTradingItemsList(tradingIconsResponse: TradingIconsResponse?) {
        if (tradingIconsResponse != null) {
            this.tradingIconsResponse = tradingIconsResponse
//            cryptoPresenter.getCryptoPrices(
//                BuildConfig.CRYPTO_URL,
//                BuildConfig.POLYGON_TOKEN
//            )
            cryptoPresenter.getForexPrices(
                BuildConfig.FOREX_URL,
                BuildConfig.POLYGON_TOKEN
            )
        }
    }

    override fun getForexPrices(
        cryptoForexApiResponse: CryptoForexApiResponse?

    ) {
        if (cryptoForexApiResponse != null) {
            search_skeleton?.visibility = View.GONE
            var tickerList = mutableListOf<Ticker>()
            tradingIconsResponse?.currency?.forEach {
                kotlin.run {
                    cryptoForexApiResponse.tickers?.forEach { ticker ->
                        val tickerName = ticker.ticker?.substring(startIndex = 2)
                        if (tickerName.equals("${it.From}${it.To}", true)) {
                            var iconFrom = ""
                            var iconTo = ""
                            tradingIconsResponse?.forex?.forEach { forex ->
                                if (it.From.equals(forex.Code, true))
                                    iconFrom = forex.Image
                                if (it.To.equals(forex.Code, true))
                                    iconTo = forex.Image

                            }
                            ticker.ticker = "${it.From}/${it.To}"
                            ticker.iconFrom = iconFrom
                            ticker.iconTo = iconTo
                            tickerList.add(ticker)
                            return@run
                        }
                    }
                }
            }


            if (tickerList.isNotEmpty()) {
                cryptoForexApiResponse.tickers = tickerList
//                discoverAdapterNew!!.updateCryptoPrices(cryptoForexApiResponse)
                forexAdapter!!.updateForexData(cryptoForexApiResponse.tickers as ArrayList<Ticker>)
            } else {
                search_skeleton?.visibility = View.GONE
                recyclerView1?.visibility = View.GONE
                ll_no_results?.visibility = View.VISIBLE
            }
        }
    }

    fun setLayout() {
        if (forexApiResponse != null) {
            search_skeleton?.visibility = View.GONE
            recyclerView1?.visibility = View.VISIBLE
            forexAdapter!!.updateForexData(forexApiResponse?.tickers as ArrayList<Ticker>)
        } else {
            search_skeleton?.visibility = View.GONE
            recyclerView1?.visibility = View.GONE
            ll_no_results?.visibility = View.VISIBLE
        }
    }

    override fun onItemChannelFollowed2(channel: Ticker?, position: Int) {
//        channel?.id?.let { searchResultPresenter.getChannelsFollow2(it, position, channel) }
    }

    override fun onItemChannleUnfollowed2(channel: Ticker?, position: Int) {
//        channel?.id?.let { searchResultPresenter.getChannelUnfollow2(it, position, channel) }
    }


}
