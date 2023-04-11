package com.ziro.bullet.fragments.searchNew.locationnew

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
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
import com.ziro.bullet.utills.PaginationListener
import java.util.*


class LocationSeeAllFragment : Fragment(), SearchLocationInterface, SearchResultsViewInterface {
    private lateinit var searchResultPresenter: SearchResultPresenter
    var recyclerView1: RecyclerView? = null
    var cardview: CardView? = null
    var searchquery: String = ""
    private var llNoResults: LinearLayout? = null
    private val mPage = ""
    val bundle1 = Bundle()
    private var back_img: ImageView? = null
    var searchSkeleton: LinearLayout? = null
    private var currentPage = PaginationListener.PAGE_START

    private val location = ArrayList<Location>()

    var topics: List<Source>? = null
    private var manager: LinearLayoutManager? = null
    var tv_search2: TextView? = null
    lateinit var locationSeeAllAdapter: LocationSeeAllAdapter

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
        private var locationSeeAllFragment: LocationSeeAllFragment? = null

        lateinit var searchResultItem: SearchresultdataBase
        private var goHome: GoHome? = null
        private var isLoad: Boolean = false
        private var isLast: Boolean = false

        fun getInstance(bundle1: Bundle,goHome: GoHome): LocationSeeAllFragment {
            if (locationSeeAllFragment == null)
                locationSeeAllFragment = LocationSeeAllFragment()
            locationSeeAllFragment!!.arguments = bundle1
            this.goHome = goHome
            isLoad = false
            isLast = false
            return locationSeeAllFragment!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_search_locationl, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView1 = view.findViewById(R.id.rv_topic)
        cardview = view.findViewById(R.id.cardview)
        tv_search2 = view.findViewById(R.id.tv_search2)
        llNoResults = view.findViewById(R.id.ll_no_results)
        back_img = view.findViewById(R.id.back_img)
        searchSkeleton = view.findViewById(R.id.search_skeleton)



        init()
        setPagination()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        if (goHome != null) goHome!!.scrollUp()

    }

    fun init() {
        //hide bottom nav
        if (goHome != null) goHome!!.scrollUp()

        searchResultPresenter = SearchResultPresenter(requireActivity(), this)

        val bundle1 = arguments
        if (bundle1!!.getString("query") != null) {
            searchquery = bundle1.getString("query").toString()
        }

//        tv_search2?.text = searchquery.toString()

        back_img!!.setOnClickListener { activity!!.onBackPressed() }

        if (!::locationSeeAllAdapter.isInitialized) {
            locationSeeAllAdapter = LocationSeeAllAdapter(requireActivity(), this)
            searchResultPresenter = SearchResultPresenter(requireActivity(), this)
        }
        if (recyclerView1?.adapter == null) {
            recyclerView1?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val itemDecorator =  DividerItemDecorator(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.divider
                )
            });
            recyclerView1?.addItemDecoration(itemDecorator)
            recyclerView1?.adapter = locationSeeAllAdapter
            recyclerView1?.layoutManager!!.scrollToPosition(0)
        }
        searchResultPresenter.getAllLocations(searchquery, mPage, false)
    }

    private fun setPagination() {
//        manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        manager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//        recyclerView1?.addItemDecoration(SpacesItemDecoration(2))
        recyclerView1?.addOnScrollListener(object : PaginationListener(manager!!) {
            override fun loadMoreItems() {
                isLoad = true
                searchResultPresenter.getAllLocations(searchquery, currentPage, true)
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
        searchSkeleton?.visibility  = View.GONE


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
        isLoad = false
        if (response != null) {
            searchSkeleton?.visibility = View.GONE

            if (response.getMeta() != null) {
                currentPage = response.getMeta().next
            }
            if (TextUtils.isEmpty(currentPage)) {
                isLast = true
            }
            if (!isPagination) {
                location.clear()
            }
            location.addAll(response.locations)
//            searchChannelAdapter.notifyDataSetChanged()

            if (location.size <= 0) {
                searchSkeleton?.visibility = View.GONE
                cardview?.visibility = View.GONE
                llNoResults?.visibility = View.VISIBLE
            } else {
                llNoResults?.visibility = View.GONE
            }

            locationSeeAllAdapter.updateChannelsData(location)

            if (!::locationSeeAllAdapter.isInitialized) {
                locationSeeAllAdapter = LocationSeeAllAdapter(
                    requireActivity(),
                    this
                )
                recyclerView1?.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                recyclerView1?.adapter = locationSeeAllAdapter

            }
//            setPagination()
        }
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
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            locationSeeAllAdapter.notifyDataSetChanged()
//            searchChannelAdapter.notifyItemChanged(position)
//            searchChannelAdapter.updateChannelFollowPosition2(position, channel)
        }
    }

    override fun getLocationunFollow2(response: FollowResponse?, position: Int, topic: Location?) {
        if (response?.message?.lowercase(Locale.ROOT).equals("success")) {
            locationSeeAllAdapter.notifyDataSetChanged()
//            searchChannelAdapter.notifyItemChanged(position)
//            searchChannelAdapter.updateChannelFollowPosition2(position, channel)
        }
    }


    private fun navigateLocation(location: Location?) {
        val manager: FragmentManager = childFragmentManager
        backCallback.isEnabled = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        bundle1.putString("query", searchquery)
        bundle1.putString("locationId", location?.id)
        bundle1.putString("locationName", location?.name)

        val oldFragment = manager.findFragmentByTag("navlocation")
        if (oldFragment != null) {
            manager.beginTransaction().remove(oldFragment).commit()
        }
        transaction.add(R.id.frame2, PlacesListFragmentNew3.getInstancenew(bundle1, goHome!!), "navlocation")
        transaction.addToBackStack("navlocation")
        transaction.commit()
    }

    override fun onItemClick(position: Int, item: Location?) {

//        Toast.makeText(requireContext(), "channel $position  ${item?.name}  ${item?.id}", Toast.LENGTH_SHORT).show()
        navigateLocation(item)
    }

    override fun onItemClickFollow(position: Int, item: Location?) {

    }

    override fun onItemLocationFollowed2(location: Location?, position: Int) {
        location?.id?.let { searchResultPresenter.getLocationFollow2(it, position, location) }
    }

    override fun onItemLocationUnfollowed2(location: Location?, position: Int) {
        location?.id?.let { searchResultPresenter.getLocationUnfollow2(it, position, location) }
    }



}
