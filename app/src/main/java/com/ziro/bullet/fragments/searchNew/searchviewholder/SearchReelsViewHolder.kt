package com.ziro.bullet.fragments.searchNew.searchviewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.adapters.discover_new.DiscoverTrendingReelsAdapter
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.interfaces.AdapterCallback
import com.ziro.bullet.interfaces.DetailsActivityInterface
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback
import com.ziro.bullet.model.Reel.ReelsItem

class SearchReelsViewHolder(itemView: View, context: AppCompatActivity?) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var rvTrendingReels: RecyclerView
    private val trendingReelsAdapter = DiscoverTrendingReelsAdapter(context)


    fun onBind(
        position: Int,
        reelsList: List<ReelsItem>,
        searchFirstChildInterface: SearchFirstChildInterface,
        showOptionsLoaderCallback: ShowOptionsLoaderCallback,
        adapterCallback: AdapterCallback,
        listener: DetailsActivityInterface
    ) {

        if (reelsList.isNullOrEmpty()) {
            itemView.findViewById<LinearLayout>(R.id.ll_reels_shimmer).visibility = View.GONE
            itemView.findViewById<LinearLayout>(R.id.tv_title).visibility = View.GONE
            itemView.findViewById<LinearLayout>(R.id.tv_more).visibility = View.GONE
            itemView.findViewById<LinearLayout>(R.id.rv_trending_reels).visibility = View.GONE
        } else {
            trendingReelsAdapter.addChildListener(searchFirstChildInterface, false)
            trendingReelsAdapter.addShareBottomSheetCallback(
                showOptionsLoaderCallback,
                adapterCallback,
                listener
            )
            itemView.findViewById<TextView>(R.id.tv_title).apply {
                text = context.resources.getString(R.string.reels)
            }
            if (reelsList.size >= 5) {
                itemView.findViewById<TextView>(R.id.tv_more).visibility = View.VISIBLE
            } else {
                itemView.findViewById<TextView>(R.id.tv_more).visibility = View.GONE
            }

            itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
                searchFirstChildInterface.searchChildOnClick(reelsList)
            }

            if (reelsList.isNotEmpty()) {
                if (!::rvTrendingReels.isInitialized) {
                    rvTrendingReels = itemView.findViewById(R.id.rv_trending_reels)
                    itemView.findViewById<RecyclerView>(R.id.rv_trending_reels).apply {
                        visibility = View.VISIBLE
                        layoutManager =
                            LinearLayoutManager(
                                itemView.context,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        adapter = trendingReelsAdapter
                    }
                }
                itemView.findViewById<LinearLayout>(R.id.ll_reels_shimmer).visibility = View.GONE
                trendingReelsAdapter.updateTrendingReels(reelsList)

            }
        }
    }
}