package com.ziro.bullet.adapters.discover_new

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.model.discoverNew.DiscoverNew
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels

class DiscoverTrendingChannelsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var rvTrendingChannel: RecyclerView
    val trendingChannelsAdapter = TrendingChannelsAdapter()
    fun onBind(
        position: Int,
        discoverItem: DiscoverNew,
        trendingChannels: List<DiscoverNewChannels>,
        discoverChildInterface: DiscoverChildInterface
    ) {

        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = context.resources.getString(R.string.trending_channel)
        }

        if (trendingChannels.size >= 5) {
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.VISIBLE
        } else {
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.GONE
        }

        itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
            discoverChildInterface.searchChildOnChannelClick(trendingChannels)
        }

        if (trendingChannels.isNotEmpty()) {

            itemView.findViewById<LinearLayout>(R.id.ll_trending_channel_shimmer).visibility =
                View.GONE

            trendingChannelsAdapter.addChildListenerDiscover(discoverChildInterface, true)
            rvTrendingChannel = itemView.findViewById(R.id.rv_trending_channels)
            itemView.findViewById<RecyclerView>(R.id.rv_trending_channels).apply {
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                adapter = trendingChannelsAdapter

                rvTrendingChannel.addItemDecoration(
                    DividerItemDecorator(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.divider
                        )
                    )
                )



                visibility = View.VISIBLE
            }


//            itemView.findViewById<RecyclerView>(R.id.rv_trending_channels).apply {
//                layoutManager =
//                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
//
//                adapter = trendingChannelsAdapter
//                visibility = View.VISIBLE
//            }


            trendingChannelsAdapter.updateTrendingChannels(trendingChannels)

        }
    }

    fun updateChannelPositionin(position: Int, channel: DiscoverNewChannels?) {
        if (channel != null) {
            trendingChannelsAdapter.updateSingleChannel(channel, position)
        }
    }
}