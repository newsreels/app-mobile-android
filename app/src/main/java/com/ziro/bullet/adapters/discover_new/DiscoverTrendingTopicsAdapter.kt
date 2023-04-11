package com.ziro.bullet.adapters.discover_new

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic

class DiscoverTrendingTopicsAdapter :
    RecyclerView.Adapter<DiscoverTrendingTopicsAdapter.TrendingTopicsViewHolder>() {
    private lateinit var discoverChildInterface: DiscoverChildInterface
    private var trendingTopicList = listOf<DiscoverNewTopic>()

    inner class TrendingTopicsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(trendingTopic: DiscoverNewTopic) {

            Glide.with(itemView.context)
                .load(trendingTopic.image)
                .placeholder(R.drawable.bg_btn_round_grey)
                .error(R.drawable.bg_btn_round_grey)
                .into(itemView.findViewById(R.id.iv_suggested_topic_icon))

            itemView.findViewById<TextView>(R.id.tv_suggested_topics_title).text =
                trendingTopic.name

            if (trendingTopic.favorite!!) {
                itemView.findViewById<ImageView>(R.id.iv_follow_topic).setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.discover_unfollow
                    )
                )
            } else {
                itemView.findViewById<ImageView>(R.id.iv_follow_topic).setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.ic_plus
                    )
                )
            }

            DrawableCompat.setTint(
                itemView.findViewById<ImageView>(R.id.iv_follow_topic).drawable,
                Color.BLACK
            )

            itemView.findViewById<ImageView>(R.id.iv_follow_topic).setOnClickListener {
                if (trendingTopic.favorite!!) {
                    trendingTopic.favorite = false
                    discoverChildInterface.onTopicUnfollowed(trendingTopic, position)
                } else {
                    trendingTopic.favorite = true
                    discoverChildInterface.onTopicFollowed(trendingTopic, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingTopicsViewHolder {
        return TrendingTopicsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.trending_topic_item_new, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrendingTopicsViewHolder, position: Int) {
        holder.onBind(trendingTopicList[position])

        holder.itemView.setOnClickListener {
            discoverChildInterface.searchChildTopicSecondOnClick(trendingTopicList[position])
        }
    }

    override fun getItemCount(): Int {
        return trendingTopicList.size
    }

    fun updateTopics(trendingTopicList: List<DiscoverNewTopic>, position: Int) {
        this.trendingTopicList = trendingTopicList
        notifyItemChanged(position)
    }

    fun addChildListener(discoverChildInterface: DiscoverChildInterface) {
        this.discoverChildInterface = discoverChildInterface
    }
}