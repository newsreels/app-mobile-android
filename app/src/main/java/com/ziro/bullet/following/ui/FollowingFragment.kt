package com.ziro.bullet.following.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.ziro.bullet.R
import com.ziro.bullet.activities.ChannelDetailsActivity
import com.ziro.bullet.activities.ChannelPostActivity
import com.ziro.bullet.activities.PlacesActivity
import com.ziro.bullet.common.base.BaseFragment
import com.ziro.bullet.common.base.UiState
import com.ziro.bullet.data.TYPE
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.data.models.topics.Topics
import com.ziro.bullet.data.models.topics.TopicsModel
import com.ziro.bullet.interfaces.GoHome
import com.ziro.bullet.model.FollowResponse
import com.ziro.bullet.utills.Constants
import kotlinx.android.synthetic.main.fragment_following2.*

class FollowingFragment : BaseFragment(R.layout.fragment_following2),
    FollowingPresenter.Listener, FollowingController.Listener {
    var search_skeleton: ShimmerFrameLayout? = null
    var epoxyRecyclerView: EpoxyRecyclerView? = null

    companion object {
        var goHome: GoHome? = null
        fun getInstance(goHome: GoHome): FollowingFragment {
            this.goHome = goHome
            return FollowingFragment()
        }
    }

    lateinit var presenter: FollowingPresenter

    private var apiCount = 0
    private val TOTAL_API_CALLS = 2

    override val activity: Activity
        get() = requireActivity()

    private val controller by lazy {
        FollowingController(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onFollowingTopics(state: UiState<TopicsModel>) {
        if (search_skeleton?.visibility == View.VISIBLE) {
            search_skeleton?.visibility = View.GONE
            epoxyRecyclerView?.visibility = View.VISIBLE
        }
        controller.followingTopics = state
//        val topics =
//            (controller.followingTopics as UiState.Success<TopicsModel>).data?.topics ?: return


        val topics: ArrayList<Topics>? = when (state) {
            is UiState.Success ->
                (controller.followingTopics as UiState.Success<TopicsModel>).data?.topics
            is UiState.Error -> null
            else -> null
        }

        if (topics != null) {
            controller.isNotTopicsEmpty = topics.size >= 1
        }

        apiCount += 1
        if (apiCount >= TOTAL_API_CALLS) {
            if (controller.isNotTopicsEmpty || controller.isNotChannelEmpty || controller.isNotLocationEmpty) {
                cl_no_data?.visibility = View.GONE
            } else {
                cl_no_data?.visibility = View.VISIBLE
            }
        }
    }

    override fun onFollowingChannels(state: UiState<SourceModel>) {

        if (search_skeleton?.visibility == View.VISIBLE) {
            search_skeleton?.visibility = View.GONE
            epoxyRecyclerView?.visibility = View.VISIBLE
        }
        controller.followingChannels = state

        val sources: ArrayList<Source>? = when (state) {
            is UiState.Success ->
                (controller.followingChannels as UiState.Success<SourceModel>).data?.sources

            is UiState.Error -> null
            else -> null
        }

        if (sources != null) {
            controller.isNotChannelEmpty = sources.size >= 1
        }

        apiCount += 1
        if (apiCount >= TOTAL_API_CALLS) {
            if (controller.isNotTopicsEmpty || controller.isNotChannelEmpty || controller.isNotLocationEmpty) {
                cl_no_data?.visibility = View.GONE
            } else {
                cl_no_data?.visibility = View.VISIBLE
            }
        }
    }

    override fun onFollowingLocation(state: UiState<LocationModel>) {
        if (search_skeleton?.visibility == View.VISIBLE) {
            search_skeleton?.let {
                it.visibility = View.GONE
            }
            epoxyRecyclerView?.visibility = View.VISIBLE
        }
        controller.followingLocation = state

        val location: ArrayList<Location>? = when (state) {
            is UiState.Success ->
                (controller.followingLocation as UiState.Success<LocationModel>).data?.locations

            is UiState.Error -> null
            else -> null
        }

        if (location != null) {
            controller.isNotLocationEmpty = location.size >= 1
        }

        apiCount += 1
        if (apiCount >= TOTAL_API_CALLS) {
            if (controller.isNotTopicsEmpty || controller.isNotChannelEmpty || controller.isNotLocationEmpty) {
                cl_no_data?.visibility = View.GONE
            } else {
                cl_no_data?.visibility = View.VISIBLE
            }
        }
    }

    override fun onSuggestedTopics(state: UiState<TopicsModel>) {
        controller.suggestedTopics = state
    }

    override fun onSuggestedChannels(state: UiState<SourceModel>) {
        controller.suggestedChannels = state
    }

    override fun onSuggestedLocation(state: UiState<LocationModel>) {
        controller.suggestedLocation = state
    }

    override fun followChannel(state: UiState<FollowResponse>, channel: Source?, position: Int) {
        when (state) {
            is UiState.Success -> {
                val sourcesModel =
                    (controller.followingChannels as UiState.Success<SourceModel>).data
                if (sourcesModel != null && position < sourcesModel.sources.size) {
                    sourcesModel.sources[position].isFavorite = channel?.isFavorite == true
                    controller.followingChannels = UiState.Success(sourcesModel)
                    //updating the follow list on unfollow scenario
                    presenter.getFollowingChannels("")

                }
            }
            is UiState.Error -> {
                Toast.makeText(requireContext(), "Something went wrong..", Toast.LENGTH_SHORT)
                    .show()
                controller.requestModelBuild()
            }
        }
    }

    override fun followTopic(state: UiState<FollowResponse>, topic: Topics?, position: Int) {
        when (state) {
            is UiState.Success -> {
                val topicsModel =
                    (controller.followingTopics as UiState.Success<TopicsModel>).data
                if (topicsModel != null && position < topicsModel.topics.size) {
                    topicsModel.topics[position].isFavorite = topic!!.isFavorite
                    controller.followingTopics = UiState.Success(topicsModel)
                    presenter.getFollowingTopics("")

                }
            }
            is UiState.Error -> {
                Toast.makeText(requireContext(), "Something went wrong..", Toast.LENGTH_SHORT)
                    .show()
                controller.requestModelBuild()
            }
        }
    }

    override fun followLocation(
        state: UiState<FollowResponse>,
        location: Location?,
        position: Int
    ) {
        when (state) {
            is UiState.Success -> {
                val locationModel =
                    (controller.followingLocation as UiState.Success<LocationModel>).data
                if (locationModel != null && position < locationModel.locations.size) {
                    locationModel.locations[position].isFavorite = location!!.isFavorite
                    controller.followingLocation = UiState.Success(locationModel)
                    presenter.getFollowingLocations("")
                }
            }
            is UiState.Error -> {
                Toast.makeText(requireContext(), "Something went wrong..", Toast.LENGTH_SHORT)
                    .show()
                controller.requestModelBuild()
            }
        }
    }

    override val viewContext: Context
        get() = requireContext()

    override fun onEditClick() {

    }

    override fun getContext(): FragmentActivity? {
        return getActivity()
    }

    override fun onUnFollow(item: Any, position: Int) {
        if (item is Source) {
            if (item.isFavorite) {
                item.isFavorite = false
                presenter.unFollowChannel(item.id, item, position)
            } else {
                item.isFavorite = true
                presenter.followChannel(item.id, item, position)
            }
        } else if (item is Topics) {
            if (item.isFavorite) {
                item.isFavorite = false
                presenter.unFollowTopic(item.id, item, position)
            } else {
                item.isFavorite = true
                presenter.followTopic(item.id, item, position)
            }
        } else if (item is Location) {
            if (item.isFavorite) {
                item.isFavorite = false
                presenter.unFollowPlaces(item.id, item, position)
            } else {
                item.isFavorite = true
                presenter.followPlaces(item.id, item, position)
            }
        }
    }

    override fun onItemClick(item: Any) {
        if (item is Source) {
            val intent = Intent(context, ChannelDetailsActivity::class.java)
            intent.putExtra("id", item.id)
            startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST)
        } else if (item is Topics) {
            val intent = Intent(context, ChannelPostActivity::class.java)
            intent.putExtra("type", TYPE.TOPIC)
            intent.putExtra("id", item.id)
            intent.putExtra("context", item.context)
            intent.putExtra("name", item.name)
            intent.putExtra("favorite", item.isFavorite)
            requireContext().startActivity(intent)
        } else if (item is Location) {
            startActivity(Intent(requireActivity(), PlacesActivity::class.java).also {
                it.putExtra("location", item)
            })
        }
    }

    override fun setupDependencies() {
        presenter = FollowingPresenter(this)
        presenter.initPresenter()
    }

    fun reload() {

        apiCount = 0
        if (::presenter.isInitialized) {
//            presenter.getFollowingLocations("")
            presenter.getFollowingTopics("")
            presenter.getFollowingChannels("")
//            presenter.getSuggestedChannels(false)
//            presenter.getSuggestedTopics()
//            presenter.getSuggestedLocations()
        }
    }

    override fun setupObservers() {
        search_skeleton = view?.findViewById(R.id.shimmer_view_containerfo)
        epoxyRecyclerView = view?.findViewById(R.id.recyclerViewExpo)
    }

    override fun setupView() {
        epoxyRecyclerView?.layoutManager = getLinearLayoutManger(LinearLayoutManager.VERTICAL)
        epoxyRecyclerView?.setController(controller)
    }

    override fun onResume() {
        super.onResume()
        if (Constants.topicsStatusChanged != null && controller.followingTopics != null) {
            var position = -1
            val topicsModel =
                (controller.followingTopics as UiState.Success<TopicsModel>).data
            topicsModel?.topics?.forEachIndexed { index, topics ->
                if (topics.id == Constants.topicsStatusChanged) {
                    position = index
                    return@forEachIndexed
                }
            }
            if (Constants.followStatus != null) {
                if (position > -1) {
                    topicsModel?.topics?.get(position)?.isFavorite =
                        Constants.followStatus.toBoolean()
                    Constants.topicsStatusChanged = null
                    Constants.followStatus = null
                    controller.followingTopics = UiState.Success(topicsModel)
                }
            }
        }
        if (Constants.sourceStatusChanged != null && controller.followingChannels != null) {
            var position = -1
            val sourcesModel =
                (controller.followingChannels as UiState.Success<SourceModel>).data
            sourcesModel?.sources?.forEachIndexed { index, source ->
                if (source.id == Constants.sourceStatusChanged) {
                    position = index
                    return@forEachIndexed
                }
            }
            if (Constants.followStatus != null) {
                if (position > -1) {
                    sourcesModel?.sources?.get(position)?.isFavorite =
                        Constants.followStatus.toBoolean()
                    Constants.sourceStatusChanged = null
                    Constants.followStatus = null
                    controller.followingChannels = UiState.Success(sourcesModel)
                }
            }
        }
        if (Constants.locationStatusChanged != null && controller.followingLocation != null) {
            var position = -1
            val locationModel =
                (controller.followingLocation as UiState.Success<LocationModel>).data
            locationModel?.locations?.forEachIndexed { index, location ->
                if (location.id == Constants.locationStatusChanged) {
                    position = index
                    return@forEachIndexed
                }
            }
            if (Constants.followStatus != null) {
                if (position > -1) {
                    locationModel?.locations?.get(position)?.isFavorite =
                        Constants.followStatus.toBoolean()
                    Constants.locationStatusChanged = null
                    Constants.followStatus = null
                    controller.followingLocation = UiState.Success(locationModel)
                }
            }
        }
    }

}