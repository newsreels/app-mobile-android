package com.ziro.bullet.adapters.discover_new

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.DiscoverNew
import com.ziro.bullet.model.discoverNew.DiscoverNewLiveChannels

class DiscoverLiveChannelsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(
        liveChannelsList: List<DiscoverNewLiveChannels>,
        discoverChildInterface: DiscoverChildInterface
    ) {
        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = "Live Channels"
        }

        if (liveChannelsList.isNotEmpty()) {
            itemView.findViewById<LinearLayout>(R.id.ll_live_shimmer).visibility = View.GONE
            val liveChannelsAdapter = DiscoverLiveChannelsAdapter(discoverChildInterface)
            itemView.findViewById<RecyclerView>(R.id.rv_live_channels).apply {
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = liveChannelsAdapter
                visibility = View.VISIBLE
            }
            liveChannelsAdapter.updateLiveChannels(liveChannelsList)

        }
    }
}