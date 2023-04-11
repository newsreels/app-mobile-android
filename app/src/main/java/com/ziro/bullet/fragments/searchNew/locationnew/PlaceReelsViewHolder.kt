package com.ziro.bullet.fragments.searchNew.locationnew

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.adapters.discover_new.DiscoverTrendingReelsAdapter
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.mediapicker.gallery.SpacingItemDecoration
import com.ziro.bullet.mediapicker.utils.ScreenUtils
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.discoverNew.DiscoverNew

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