package com.ziro.bullet.fragments.searchNew.channelpackage

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.activities.ChannelDetailsActivity
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.fragments.searchNew.interfaces.SearchResultsViewInterface
import com.ziro.bullet.fragments.searchNew.presenter.SearchResultPresenter
import com.ziro.bullet.interfaces.GoHome
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.model.discoverNew.DiscoverNewReels
import com.ziro.bullet.model.discoverNew.DiscoverNewResponse
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.model.searchresultnew.SearchresultdataBase
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.PaginationListener
import com.ziro.bullet.utills.Utils
import java.util.*


class SearchChannelFragment : Fragment(), SearchChannelInterface, SearchResultsViewInterface {
    private lateinit var searchResultPresenter: SearchResultPresenter
    var recyclerView1: RecyclerView? = null
    var cardview: CardView? = null
    var mSearchChar: String = ""
    private var ll_no_results: LinearLayout? = null
    private val mPage = ""
    private var back_img: ImageView? = null
    var search_skeleton: LinearLayout? = null
    private var etSearchChannels: EditText? = null
    private var ivClearText: ImageView? = null
    private var currentPage = PaginationListener.PAGE_START

    private val channel = ArrayList<Source>()

    var topics: List<Source>? = null
    private var manager: LinearLayoutManager? = null
    var tv_search2: TextView? = null
    lateinit var searchChannelAdapter: SearchChannelAdapter

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
        private var searchChannelFragment: SearchChannelFragment? = null

        lateinit var searchResultItem: SearchresultdataBase
        private var goHome: GoHome? = null
        private var isLoad: Boolean = false
        private var isLast: Boolean = false

        fun getInstance(bundle1: Bundle, goHome: GoHome?): SearchChannelFragment {
            if (searchChannelFragment == null)
                searchChannelFragment = SearchChannelFragment()
            searchChannelFragment!!.arguments = bundle1
            this.goHome = goHome
            isLoad = false
            isLast = false
            return searchChannelFragment!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_search_channel, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView1 = view.findViewById(R.id.rv_topic)
        cardview = view.findViewById(R.id.cardview)
        tv_search2 = view.findViewById(R.id.tv_search2)
        ll_no_results = view.findViewById(R.id.ll_no_results)
        back_img = view.findViewById(R.id.back_img)
        search_skeleton = view.findViewById(R.id.search_skeleton)
        etSearchChannels = view.findViewById(R.id.et_searchtext)
        ivClearText = view.findViewById(R.id.iv_clear_text)


        init()
        setPagination()
    }

    override fun onResume() {
        super.onResume()
        if (goHome != null) goHome!!.scrollUp()
        if (Constants.sourceStatusChanged != null && ::searchChannelAdapter.isInitialized) {
            searchChannelAdapter.updateChannelStatus()
        }
    }

    fun init() {
        //hide bottom nav
        if (goHome != null) goHome!!.scrollUp()

        searchResultPresenter = SearchResultPresenter(requireActivity(), this)

        val bundle1 = arguments
        if (bundle1!!.getString("query") != null) {
            mSearchChar = bundle1.getString("query").toString()
        }

        etSearchChannels?.setText(mSearchChar)

        back_img!!.setOnClickListener {
            Utils.hideKeyboard(requireActivity(), it)
            requireActivity().onBackPressed()
        }

        if (!::searchChannelAdapter.isInitialized) {
            searchChannelAdapter = SearchChannelAdapter(requireActivity(), this)
            searchResultPresenter = SearchResultPresenter(requireActivity(), this)
        }
        if (recyclerView1?.adapter == null) {
            recyclerView1?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val itemDecorator = DividerItemDecorator(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.divider
                )
            });
            recyclerView1?.addItemDecoration(itemDecorator)
            recyclerView1?.adapter = searchChannelAdapter
            recyclerView1?.layoutManager!!.scrollToPosition(0)
        }
        searchResultPresenter.getSearchChannels(mSearchChar, mPage, false)

        etSearchChannels?.setText(mSearchChar)

        etSearchChannels!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val text = charSequence.toString().trim { it <= ' ' }
                if (!TextUtils.isEmpty(text)) {
                    ivClearText!!.visibility = View.VISIBLE
                } else {
                    ivClearText!!.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        etSearchChannels!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = etSearchChannels!!.text.toString().trim { it <= ' ' }
                if (!TextUtils.isEmpty(searchQuery)) {
                    mSearchChar = searchQuery
                    searchResultPresenter.getSearchChannels(mSearchChar, mPage, false)
                    Utils.hideKeyboard(requireActivity(), etSearchChannels)
                }
                return@OnEditorActionListener true
            }
            false
        })

        ivClearText!!.setOnClickListener { v: View? ->
            etSearchChannels!!.setText("")
            Utils.hideKeyboard(requireActivity(), etSearchChannels)
        }
    }

    private fun setPagination() {
//        manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        manager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//        recyclerView1?.addItemDecoration(SpacesItemDecoration(2))
        recyclerView1?.addOnScrollListener(object : PaginationListener(manager!!) {
            override fun loadMoreItems() {
                isLoad = true
                searchResultPresenter.getSearchChannels(mSearchChar, currentPage, true)
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


    }

    override fun getSearchTopics(response: DiscoverNewResponse?) {

    }

    override fun getSearchResult(response: SearchresultdataBase?) {

    }

    override fun getSearchReels(reelsResponse: List<DiscoverNewReels>?) {

    }

    override fun searchChannelSuccess(response: SourceModel?, isPagination: Boolean) {
        isLoad = false
        if (response != null) {
            search_skeleton?.visibility = View.GONE

            if (response.getMeta() != null) {
                currentPage = response.getMeta().next
            }
            if (TextUtils.isEmpty(currentPage)) {
                isLast = true
            }
            if (!isPagination) {
                channel.clear()
            }
            channel.addAll(response.sources)
//            searchChannelAdapter.notifyDataSetChanged()

            if (channel.size <= 0) {
                search_skeleton?.visibility = View.GONE
                cardview?.visibility = View.GONE
                ll_no_results?.visibility = View.VISIBLE
            } else {
                ll_no_results?.visibility = View.GONE
            }

            searchChannelAdapter.updateChannelsData(channel as List<Source>)

            if (!::searchChannelAdapter.isInitialized) {
                searchChannelAdapter = SearchChannelAdapter(
                    requireActivity(),
                    this
                )
                recyclerView1?.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                recyclerView1?.adapter = searchChannelAdapter

            }
//            setPagination()
        }
    }

    override fun searchLocationSuccess(response: LocationModel?, isPagination: Boolean) {
        TODO("Not yet implemented")
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
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            Constants.sourceStatusChanged = channel?.id
            Constants.isSourceDataChange = true
            Constants.followStatus = true.toString()
            searchChannelAdapter.notifyItemChanged(position)
        }
    }

    override fun getChannelsunFollow2(response: FollowResponse?, position: Int, channel: Source?) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            Constants.sourceStatusChanged = channel?.id
            Constants.isSourceDataChange = true
            Constants.followStatus = false.toString()
            searchChannelAdapter.notifyItemChanged(position)
        }
    }

    override fun getLocationFollow2(response: FollowResponse?, position: Int, topic: Location?) {

    }

    override fun getLocationunFollow2(response: FollowResponse?, position: Int, topic: Location?) {

    }


    private fun navigateChanel(source: Source?) {

        var intent = Intent(context, ChannelDetailsActivity::class.java)
        intent.putExtra("id", source?.id)
        startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST)
    }

    override fun onItemClick(position: Int, item: Source?) {
        Log.e("CHANNEL-fragment", "onBindViewHolder: $position ${item?.name}")
//        Toast.makeText(requireContext(), "channel $position  ${item?.name}  ${item?.id}", Toast.LENGTH_SHORT).show()
        navigateChanel(item)
    }

    override fun onItemClickFollow(position: Int, item: Source?) {

    }

    override fun onItemChannelFollowed2(channel: Source?, position: Int) {
        channel?.id?.let { searchResultPresenter.getChannelsFollow2(it, position, channel) }
    }

    override fun onItemChannleUnfollowed2(channel: Source?, position: Int) {
        channel?.id?.let { searchResultPresenter.getChannelUnfollow2(it, position, channel) }
    }


}
