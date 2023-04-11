package com.ziro.bullet.fragments.searchNew.searchviewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.fragments.searchNew.searchchildadapter.SearchTopicsAdapter
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic

class SearchTopicsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val searchTopicsAdapter = SearchTopicsAdapter()
    private lateinit var rvTrendingTopics: RecyclerView
    fun onBind(
        position: Int,
        trendingTopics: List<DiscoverNewTopic>,
        searchFirstChildInterface: SearchFirstChildInterface
    ) {
        itemView.findViewById<LinearLayout>(R.id.ll_trending_channel_shimmer).visibility = View.GONE
        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = context.resources.getString(R.string.topics)
        }
//        if(trendingTopics.size >= 5){
        itemView.findViewById<TextView>(R.id.tv_more).apply {
            text = context.resources.getString(R.string.see_more)
        }
        itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
            searchFirstChildInterface.searchChildOnTopicClick(trendingTopics)
        }

        if (trendingTopics.isNotEmpty()) {
            searchTopicsAdapter.updateTopics(trendingTopics)
            if (!::rvTrendingTopics.isInitialized) {
                rvTrendingTopics = itemView.findViewById(R.id.rv_trending_channels)
                searchTopicsAdapter.addChildListener(searchFirstChildInterface)
                itemView.findViewById<RecyclerView>(R.id.rv_trending_channels).apply {
                    visibility = View.VISIBLE
                    layoutManager =
                        LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                    val itemDecorator = DividerItemDecorator(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.divider
                        )
                    )
                    addItemDecoration(itemDecorator)

                    adapter = searchTopicsAdapter
                }
            }
        }
    }

    fun updateTopicPositionin(position: Int, topic: DiscoverNewTopic?) {
        if (topic != null) {
            searchTopicsAdapter.updateSingleTopic(topic, position)
        }
    }

    fun updateTopicStatus() {
        searchTopicsAdapter.updateTopicStatus()
    }


}