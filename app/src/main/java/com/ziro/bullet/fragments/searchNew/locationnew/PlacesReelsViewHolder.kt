package com.ziro.bullet.fragments.searchNew.locationnew

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.adapters.discover_new.DiscoverTrendingReelsAdapter
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.mediapicker.gallery.SpacingItemDecoration
import com.ziro.bullet.mediapicker.utils.ScreenUtils
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.places.Search

class PlacesReelsViewHolder(itemView: View, context: AppCompatActivity?) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var rvTrendingReels: RecyclerView
    private val trendingReelsAdapter = DiscoverTrendingReelsAdapter(context)
    fun onBind(
        position: Int,
        search: Search,
        reelsList: List<ReelsItem>,
        discoverChildInterface: DiscoverChildInterface
    ) {
        trendingReelsAdapter.addChildListenerDiscover(discoverChildInterface, true)
        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = "Trending Reels"
        }

        if (reelsList.size >= 5) {
            itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
                discoverChildInterface.searchChildOnClick(reelsList)
            }
        } else {
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.GONE
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