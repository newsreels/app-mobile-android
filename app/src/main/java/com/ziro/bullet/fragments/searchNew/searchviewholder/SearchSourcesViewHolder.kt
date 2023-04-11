package com.ziro.bullet.fragments.searchNew.searchviewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.adapters.discover_new.TrendingChannelsAdapter
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels

class SearchSourcesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val trendingChannelsAdapter = TrendingChannelsAdapter()
    private lateinit var rvTrendingChannel: RecyclerView

    fun onBind(
        position: Int,
        trendingChannels: List<DiscoverNewChannels>,
        searchFirstChildInterface: SearchFirstChildInterface
    ) {
        itemView.findViewById<LinearLayout>(R.id.ll_trending_channel_shimmer).visibility = View.GONE
        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = context.resources.getString(R.string.channels)
        }
        if (trendingChannels.size >= 5) {
            itemView.findViewById<TextView>(R.id.tv_more).apply {
                text = context.resources.getString(R.string.see_more)
            }
        } else {
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.GONE
        }


//        fun String.toColor(): Int = Color.parseColor(this)
//        itemView.findViewById<TextView>(R.id.tv_more).setTextColor("#EB5165".toColor())

        itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
            searchFirstChildInterface.searchChildOnChannelClick(trendingChannels)
        }


        trendingChannelsAdapter.addChildListener(searchFirstChildInterface, false)

        rvTrendingChannel = itemView.findViewById(R.id.rv_trending_channels)
        itemView.findViewById<RecyclerView>(R.id.rv_trending_channels).apply {
            visibility = View.VISIBLE
            layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            adapter = trendingChannelsAdapter
            val itemDecorator =
                DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider));
            rvTrendingChannel.addItemDecoration(itemDecorator)
        }


        trendingChannelsAdapter.updateTrendingChannels(trendingChannels)
    }

    fun updateChannelPositionin(position: Int, channel: DiscoverNewChannels?) {
        if (channel != null) {
            trendingChannelsAdapter.updateSingleChannel(channel, position)
        }
    }

    fun updateChannelStatus() {
        trendingChannelsAdapter.updateChannelStatus()
    }


}