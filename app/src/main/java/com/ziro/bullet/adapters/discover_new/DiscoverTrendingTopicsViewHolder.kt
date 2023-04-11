package com.ziro.bullet.adapters.discover_new

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic

class DiscoverTrendingTopicsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var trendingTopicsAdapter: DiscoverTrendingTopicsAdapter

    fun onBind(
        trendingTopics: List<DiscoverNewTopic>,
        discoverChildInterface: DiscoverChildInterface
    ) {
        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = context.getString(R.string.trending_topics)
        }

        if (trendingTopics.size >= 5) {
            itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
                itemView.findViewById<TextView>(R.id.tv_more).visibility = View.VISIBLE
                discoverChildInterface.searchChildOnTopicClick(trendingTopics)
            }
        } else {
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.GONE
        }


        if (trendingTopics.isNotEmpty()) {
            itemView.findViewById<LinearLayout>(R.id.ll_topics_shimmer).visibility = View.GONE

            trendingTopicsAdapter = DiscoverTrendingTopicsAdapter()
            trendingTopicsAdapter.updateTopics(trendingTopics, position)
            trendingTopicsAdapter.addChildListener(discoverChildInterface)

            itemView.findViewById<RecyclerView>(R.id.rv_trending_topics).apply {
//                layoutManager =
//                    GridLayoutManager(itemView.context, 2, GridLayoutManager.HORIZONTAL, false)
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
                adapter = trendingTopicsAdapter
                visibility = View.VISIBLE
            }
        }
    }

    fun updateTopicsList(trendingTopicsList: List<DiscoverNewTopic>, position: Int) {
        trendingTopicsAdapter.updateTopics(trendingTopicsList, position)
    }
}