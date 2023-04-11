package com.ziro.bullet.adapters.discover_new

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.interfaces.AdapterCallback
import com.ziro.bullet.interfaces.DetailsActivityInterface
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.discoverNew.DiscoverNew

class DiscoverTrendingReelsViewHolder(itemView: View, val context: AppCompatActivity) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var rvTrendingReels: RecyclerView
    private val trendingReelsAdapter = DiscoverTrendingReelsAdapter(context)


    fun onBind(
        position: Int,
        discoverItem: DiscoverNew,
        reelsList: List<ReelsItem>,
        discoverChildInterface: DiscoverChildInterface,
        showOptionsLoaderCallback: ShowOptionsLoaderCallback,
        adapterCallback: AdapterCallback,
        listener: DetailsActivityInterface
    ) {
        trendingReelsAdapter.addChildListenerDiscover(discoverChildInterface, true)
        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = context.getString(R.string.trending_reels)
        }

        if (reelsList.size >= 5) {
            itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
                discoverChildInterface.searchChildOnClick(reelsList)
            }
        } else {
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.GONE
        }
        itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
            discoverChildInterface.searchChildOnClick(reelsList)
        }

        trendingReelsAdapter.addShareBottomSheetCallback(
            showOptionsLoaderCallback,
            adapterCallback,
            listener
        )

        if (reelsList.isNotEmpty()) {

            if (!::rvTrendingReels.isInitialized) {
                rvTrendingReels = itemView.findViewById(R.id.rv_trending_reels)
                itemView.findViewById<RecyclerView>(R.id.rv_trending_reels).apply {
                    layoutManager =
                        LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = trendingReelsAdapter
                    visibility = View.VISIBLE
                }
            }
            itemView.findViewById<LinearLayout>(R.id.ll_reels_shimmer).visibility = View.GONE
            trendingReelsAdapter.updateTrendingReels(reelsList)
        }
    }
}