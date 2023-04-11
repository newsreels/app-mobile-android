package com.ziro.bullet.adapters.discover_new

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.DiscoverNewLiveChannels

class DiscoverLiveChannelsAdapter(val discoverChildInterface: DiscoverChildInterface?) :
    RecyclerView.Adapter<DiscoverLiveChannelsAdapter.LiveChannelsViewHolder>() {

    private var liveChannelsList = listOf<DiscoverNewLiveChannels>()

    inner class LiveChannelsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(liveChannel: DiscoverNewLiveChannels) {
            itemView.setOnClickListener {
                discoverChildInterface?.discoverLiveChannelClick(liveChannel)
            }
            Glide.with(itemView.context)
                .load(liveChannel.image)
                .placeholder(R.drawable.img_place_holder)
                .into(itemView.findViewById(R.id.iv_channel_icon))

            itemView.findViewById<TextView>(R.id.liveChannelName).text = liveChannel.title
            itemView.findViewById<TextView>(R.id.liveChannelDescription).text =
                liveChannel.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveChannelsViewHolder {
        return LiveChannelsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_module_live, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LiveChannelsViewHolder, position: Int) {
        holder.onBind(liveChannelsList[position])
    }

    override fun getItemCount(): Int {
        return liveChannelsList.size
    }

    fun updateLiveChannels(liveChannelsList: List<DiscoverNewLiveChannels>) {
        this.liveChannelsList = liveChannelsList
        notifyDataSetChanged()
    }
}