package com.newsreels.app.fragments.searchNew.locationnew

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newsreels.app.R
import com.newsreels.app.adapters.discover_new.DiscoverTrendingReelsAdapter
import com.newsreels.app.fragments.DiscoverChildInterface
import com.newsreels.app.mediapicker.gallery.SpacingItemDecoration
import com.newsreels.app.mediapicker.utils.ScreenUtils
import com.newsreels.app.model.Reel.ReelsItem
import com.newsreels.app.model.discoverNew.DiscoverNew

class PlaceReelsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var rvTrendingReels: RecyclerView
    val trendingReelsAdapter = DiscoverTrendingReelsAdapter()
    fun onBind(
        position: Int,
        discoverItem: DiscoverNew,
        reelsList: List<ReelsItem>,
        discoverChildInterface: DiscoverChildInterface
    ) {
        trendingReelsAdapter.addChildListenerDiscover(discoverChildInterface,true)
        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = "Trending Reels"
        }
        itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
            discoverChildInterface.searchChildOnClick(reelsList)
        }

        if (reelsList.isNotEmpty()) {

            if (!::rvTrendingReels.isInitialized) {
                rvTrendingReels = itemView.findViewById(R.id.rv_trending_reels)
                itemView.findViewById<RecyclerView>(R.id.rv_trending_reels).apply {
                    layoutManager =
                        GridLayoutManager(itemView.context, 3, GridLayoutManager.VERTICAL, false)
                    addItemDecoration(
                        SpacingItemDecoration(
                            3,
                            ScreenUtils.dip2px(context, 6f), false
                        )
                    )
                    adapter = trendingReelsAdapter
                    visibility = View.VISIBLE
                }
            }
            itemView.findViewById<LinearLayout>(R.id.ll_reels_shimmer).visibility = View.GONE
            trendingReelsAdapter.updateTrendingReels(reelsList)
        }
    }
}