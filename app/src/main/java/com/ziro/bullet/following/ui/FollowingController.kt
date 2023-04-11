package com.ziro.bullet.following.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyController
import com.ziro.bullet.R
import com.ziro.bullet.common.base.UiState
import com.ziro.bullet.common.utils.extensions.loadImage
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.data.models.topics.TopicsModel
import com.ziro.bullet.following.ui.item_model.*
import kotlinx.android.synthetic.main.item_following_content.view.*

class FollowingController(private val listener: Listener) : EpoxyController() {

    var followingLocation: UiState<LocationModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }
    var followingChannels: UiState<SourceModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }
    var followingTopics: UiState<TopicsModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var suggestedLocation: UiState<LocationModel>? = null
        set(value) {
            field = value
            value?.isEmpty = if (value is UiState.Success) value.data?.locations?.isEmpty() == true
            else false
            requestModelBuild()
        }
    var suggestedChannels: UiState<SourceModel>? = null
        set(value) {
            field = value
            value?.isEmpty = if (value is UiState.Success) value.data?.sources?.isEmpty() == true
            else false
            requestModelBuild()
        }
    var suggestedTopics: UiState<TopicsModel>? = null
        set(value) {
            field = value
            value?.isEmpty = if (value is UiState.Success) value.data?.topics?.isEmpty() == true
            else false
            requestModelBuild()
        }

    var selectedBullet: SelectedContent? = SelectedContent.TOPICS
        set(value) {
            field = value
            requestModelBuild()
        }

    var isTopicVisible: Boolean = true
        set(value) {
            field = value
            requestModelBuild()
        }

    var isChannelVisible: Boolean = true
        set(value) {
            field = value
            requestModelBuild()
        }

    var isLocationVisible: Boolean = true
        set(value) {
            field = value
            requestModelBuild()
        }

    var isNotTopicsEmpty:Boolean = false
    var isNotLocationEmpty:Boolean = false
    var isNotChannelEmpty:Boolean = false
//    val isListEmpty: Boolean
//        get() = (((followingLocation as UiState.Success<LocationModel>).data == null || (followingLocation as UiState.Success<LocationModel>).data?.locations.isNullOrEmpty())
//                && ((followingTopics as UiState.Success<TopicsModel>).data == null || (followingTopics as UiState.Success<TopicsModel>).data?.topics.isNullOrEmpty())
//                && ((followingChannels as UiState.Success<SourceModel>).data == null || (followingChannels as UiState.Success<SourceModel>).data?.sources.isNullOrEmpty()))

    override fun buildModels() {

//        editButtonItem {
//            id(EditButtonItemModel::class.simpleName)
//            onEditClick {
//                this@FollowingController.listener.onEditClick()
//            }
//        }

        var context = listener.getContext()
//        var st = context?.resources?.getString(R.string.following)
//        headerItem {
//            id(HeaderItemModel::class.simpleName)
//            var following = context?.getString(R.string.following)
////            header()
//            header(following)
//        }

        context?.let { setupFollowingTopics(it) }

        context?.let { setupFollowingChannels(it) }

        context?.let { setupFollowingLocations(it) }

        if (suggestedChannels?.isEmpty == false ||
            suggestedLocation?.isEmpty == false ||
            suggestedTopics?.isEmpty == false
        ) {
            headerItem {
                id(HeaderItemModel::class.simpleName)
                var suggested = context?.getString(R.string.suggested)
                header(suggested)
            }
        }
        context?.let { setupSuggestedTopics(it) }

        context?.let { setupSuggestedChannels(it) }

        context?.let { setupSuggestedLocations(it) }


    }

    private fun setupSuggestedLocations(context: Context) {
        when (suggestedLocation) {
            is UiState.Success -> {
                val sources =
                    (suggestedLocation as UiState.Success<LocationModel>).data?.locations ?: return
                if (sources.isEmpty()) return
                val listView = mutableListOf<View>()
                sources.forEach {
                    val inflater = LayoutInflater.from(listener.viewContext)
                    val view = inflater.inflate(R.layout.item_following_content, null, false)
                    with(view) {
                        contentName.text = it.name ?: ""
                        contentImage.loadImage(it.icon ?: "")
                    }
                    listView.add(view)
                }
                expandableContentItem {
                    id(SelectedContent.SUGGESTED_LOCATION.name)
                    titleContent(context.getString(R.string.places))
                    listItems(listView.toList())
//                    contentItemGone(this@FollowingController.selectedBullet != SelectedContent.SUGGESTED_LOCATION)
                    onExpanderClick {
                        if (this@FollowingController.selectedBullet == SelectedContent.SUGGESTED_LOCATION) {
                            this@FollowingController.selectedBullet = null
                        } else {
                            this@FollowingController.selectedBullet =
                                SelectedContent.SUGGESTED_LOCATION
                        }
                    }
                }
            }
        }
    }

    private fun setupSuggestedChannels(context: Context) {
        when (suggestedChannels) {
            is UiState.Success -> {
                val sources =
                    (suggestedChannels as UiState.Success<SourceModel>).data?.sources ?: return
                if (sources.isEmpty()) return
                val listView = mutableListOf<View>()
                sources.forEach {
                    val inflater = LayoutInflater.from(listener.viewContext)
                    val view = inflater.inflate(R.layout.item_following_content, null, false)
                    with(view) {
                        contentName.text = it.name ?: ""
                        contentImage.loadImage(it.icon ?: "")
                    }
                    listView.add(view)
                }
                expandableContentItem {
                    id(SelectedContent.SUGGESTED_CHANNELS.name)
                    titleContent(context?.getString(R.string.channels))
                    listItems(listView.toList())
//                    contentItemGone(this@FollowingController.selectedBullet != SelectedContent.SUGGESTED_CHANNELS)
                    onExpanderClick {
                        if (this@FollowingController.selectedBullet == SelectedContent.SUGGESTED_CHANNELS) {
                            this@FollowingController.selectedBullet = null
                        } else this@FollowingController.selectedBullet =
                            SelectedContent.SUGGESTED_CHANNELS
                    }
                }
            }
        }
    }

    private fun setupSuggestedTopics(context: Context) {
        when (suggestedTopics) {
            is UiState.Success -> {
                val sources =
                    (suggestedTopics as UiState.Success<TopicsModel>).data?.topics ?: return
                if (sources.isEmpty()) return
//                if(sources.isNotEmpty()) isNotTopicsEmpty = true
                val listView = mutableListOf<View>()
                sources.forEach {
                    val inflater = LayoutInflater.from(listener.viewContext)
                    val view = inflater.inflate(R.layout.item_following_content, null, false)
                    with(view) {
                        contentName.text = it.name ?: ""
                        contentImage.loadImage(it.icon ?: "")
                    }
                    listView.add(view)
                }
                expandableContentItem {
                    id(SelectedContent.SUGGESTED_TOPICS.name)
                    titleContent(context?.getString(R.string.topics))
                    listItems(listView.toList())
//                    contentItemGone(this@FollowingController.selectedBullet != SelectedContent.SUGGESTED_TOPICS)
                    onExpanderClick {
                        if (this@FollowingController.selectedBullet == SelectedContent.SUGGESTED_TOPICS) {
                            this@FollowingController.selectedBullet = null
                        } else this@FollowingController.selectedBullet =
                            SelectedContent.SUGGESTED_TOPICS
                    }
                }
            }
        }
    }

    private fun setupFollowingLocations(context: Context) {
        when (followingLocation) {
            is UiState.Success -> {
                val sources =
                    (followingLocation as UiState.Success<LocationModel>).data?.locations ?: return
                if (sources.isEmpty()) return
                if(sources.isNotEmpty()) isNotLocationEmpty = true
                val listView = mutableListOf<View>()
                sources.forEachIndexed { index, location ->
                    val inflater = LayoutInflater.from(listener.viewContext)
                    val view = inflater.inflate(R.layout.item_following_content, null, false)
                    with(view) {
                        contentName.text = location.name ?: ""
                        contentImage.loadImage(location.icon ?: location.image)
                        imgFollow.setOnClickListener {
                            listener.onUnFollow(location, index)
                            imgFollow.visibility = View.INVISIBLE
                            following_progress.visibility = View.VISIBLE
                        }
                        popup_bg.setOnClickListener {
                            listener.onItemClick(location)
                        }
                        if (location.isFavorite) {
                            imgFollow.setImageDrawable(
                                ContextCompat.getDrawable(
                                    listener.viewContext,
                                    R.drawable.ic_black_tick_background
                                )
                            )
                        } else {
                            imgFollow.setImageDrawable(
                                ContextCompat.getDrawable(
                                    listener.viewContext,
                                    R.drawable.ic_follow_item
                                )
                            )
                        }
                    }
                    listView.add(view)
                }
                expandableContentItem {
                    id(SelectedContent.LOCATION.name)
//                    titleContent(SelectedContent.LOCATION.title)
                    titleContent(context.getString(R.string.places))
                    listItems(listView.toList())
                    contentItemGone(!this@FollowingController.isLocationVisible)
                    onExpanderClick {
                        this@FollowingController.isLocationVisible =
                            !this@FollowingController.isLocationVisible
//                        if (this@FollowingController.selectedBullet == SelectedContent.LOCATION) {
//                            this@FollowingController.selectedBullet = null
//                        } else this@FollowingController.selectedBullet = SelectedContent.LOCATION
                    }
                }
            }
        }
    }

    private fun setupFollowingTopics(context: Context) {
        when (followingTopics) {
            is UiState.Success -> {
                val sources =
                    (followingTopics as UiState.Success<TopicsModel>).data?.topics ?: return
                if (sources.isEmpty()) return
                if(sources.isNotEmpty()) isNotTopicsEmpty = true

                val listView = mutableListOf<View>()
                sources.forEachIndexed { index, topic ->
                    val inflater = LayoutInflater.from(listener.viewContext)
                    val view = inflater.inflate(R.layout.item_following_content, null, false)
                    if (topic.isFavorite) {
                        view.findViewById<ImageView>(R.id.imgFollow).setImageDrawable(
                            ContextCompat.getDrawable(
                                listener.viewContext,
                                R.drawable.ic_black_tick_background
                            )
                        )
                    } else {
                        view.findViewById<ImageView>(R.id.imgFollow).setImageDrawable(
                            ContextCompat.getDrawable(
                                listener.viewContext,
                                R.drawable.ic_follow_item
                            )
                        )
                    }
                    with(view) {
                        contentName.text = topic.name ?: ""
                        contentImage.loadImage(topic.icon ?: "")
                        imgFollow.setOnClickListener {
                            listener.onUnFollow(topic, index)
                            imgFollow.visibility = View.INVISIBLE
                            following_progress.visibility = View.VISIBLE
                        }
                        popup_bg.setOnClickListener {
                            listener.onItemClick(topic)
                        }
                        if (index == sources.size - 1) {
                            divider_view.visibility = View.GONE
                        }
                        if (topic.isFavorite) {
                            imgFollow.setImageDrawable(
                                ContextCompat.getDrawable(
                                    listener.viewContext,
                                    R.drawable.ic_black_tick_background
                                )
                            )
                        } else {
                            imgFollow.setImageDrawable(
                                ContextCompat.getDrawable(
                                    listener.viewContext,
                                    R.drawable.ic_follow_item
                                )
                            )
                        }
                    }
                    listView.add(view)
                }
//                var context= listener.getContext()
                expandableContentItem {
                    id(SelectedContent.TOPICS.name)
                    titleContent(context?.getString(R.string.topics))
//                    titleContent(SelectedContent.TOPICS.title)
                    listItems(listView.toList())
                    contentItemGone(!this@FollowingController.isTopicVisible)
                    onExpanderClick {
                        this@FollowingController.isTopicVisible =
                            !this@FollowingController.isTopicVisible
//                        if (this@FollowingController.selectedBullet == SelectedContent.TOPICS) {
//                            this@FollowingController.selectedBullet = null
//                        } else this@FollowingController.selectedBullet = SelectedContent.TOPICS
                    }
                }
            }
        }
    }

    private fun setupFollowingChannels(context: Context) {
        when (followingChannels) {
            is UiState.Success -> {
                val sources =
                    (followingChannels as UiState.Success<SourceModel>).data?.sources ?: return
                if (sources.isEmpty()) return
                if(sources.isNotEmpty()) isNotChannelEmpty = true
                val listView = mutableListOf<View>()
                sources.forEachIndexed { index, source ->
                    val inflater = LayoutInflater.from(listener.viewContext)
                    val view = inflater.inflate(R.layout.item_following_content, null, false)
                    with(view) {
                        contentName.text = source.name ?: ""
                        contentImage.loadImage(source.icon ?: "")
                        imgFollow.setOnClickListener {
                            listener.onUnFollow(source, index)
                            imgFollow.visibility = View.INVISIBLE
                            following_progress.visibility = View.VISIBLE
                        }

                        popup_bg.setOnClickListener {
                            listener.onItemClick(source)
                        }

                        if (index == sources.size - 1) {
                            divider_view.visibility = View.GONE
                        }
                        if (source.isFavorite) {
                            imgFollow.setImageDrawable(
                                ContextCompat.getDrawable(
                                    listener.viewContext,
                                    R.drawable.ic_black_tick_background
                                )
                            )
                        } else {
                            imgFollow.setImageDrawable(
                                ContextCompat.getDrawable(
                                    listener.viewContext,
                                    R.drawable.ic_follow_item
                                )
                            )
                        }
                    }
                    listView.add(view)
                }
                expandableContentItem {
                    id(SelectedContent.CHANNELS.name)
//                    titleContent(SelectedContent.CHANNELS.title)title
                    titleContent(context?.getString(R.string.channels))
                    listItems(listView.toList())
                    contentItemGone(!this@FollowingController.isChannelVisible)
                    onExpanderClick {
                        this@FollowingController.isChannelVisible =
                            !this@FollowingController.isChannelVisible
//                        if (this@FollowingController.selectedBullet == SelectedContent.CHANNELS) {
//                            this@FollowingController.selectedBullet = null
//                        } else this@FollowingController.selectedBullet = SelectedContent.CHANNELS
                    }
                }
            }
        }
    }

    interface Listener {
        val viewContext: Context
        fun onEditClick()

        fun onUnFollow(item: Any, position: Int)

        fun onItemClick(item: Any)

        fun getContext(): Context?
    }

    enum class SelectedContent(val title: String) {
        TOPICS("Topics"),
        LOCATION("Locations"),
        CHANNELS("Channels"),
        SUGGESTED_TOPICS("Topics"),
        SUGGESTED_LOCATION("Locations"),
        SUGGESTED_CHANNELS("Channels"),
    }

}