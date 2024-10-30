package com.newsreels.app.fragments.searchNew.locationnew

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newsreels.app.R
import com.newsreels.app.adapters.discover_new.DiscoverTrendingReelsAdapter
import com.newsreels.app.fragments.DiscoverChildInterface
import com.newsreels.app.mediapicker.gallery.SpacingItemDecoration
import com.newsreels.app.mediapicker.utils.ScreenUtils
import com.newsreels.app.model.Reel.ReelsItem
import com.newsreels.app.model.places.Search

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