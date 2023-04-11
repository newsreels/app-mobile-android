package com.ziro.bullet.fragments.searchNew.channelpackage

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.utills.Constants


class SearchChannelAdapter(
    private var context: Activity,
    private var searchChannelInterface: SearchChannelInterface?
) : RecyclerView.Adapter<SearchChannelAdapter.ChannelViewHolder?>() {
    private var sourcesList = mutableListOf<Source>()
    private var holder: ChannelViewHolder? = null
    private lateinit var channel: Source

    var tickDone: Boolean = false
    var rotate: Boolean = false
    var firsttime: Boolean = true
    var refresh: Boolean = true
    var cancelDone: Boolean = true
    var postapped = -1


    inner class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(sel_position: Int, channel: Source?) {

            var ivImage: ImageView? = null
            var rotate_anti_clock =
                AnimationUtils.loadAnimation(itemView.context, R.anim.rotate_anti_clock)
            var followHandler = Handler(Looper.getMainLooper())
            val updateHandler = Handler(Looper.getMainLooper())
            val updateRunnable = Runnable {

                if (channel != null) {
                    if (channel.isFavorite) {
                        Glide.with(itemView.findViewById<ImageView>(R.id.iv_image).context)
                            .load(R.drawable.ic_black_tick_background)
                            .placeholder(R.drawable.ic_black_tick_background)
                            .into(itemView.findViewById(R.id.iv_image))
//                        if (itemView.findViewById<ImageView>(R.id.iv_image).animation != null) {
//                            itemView.findViewById<ImageView>(R.id.iv_image).animation.cancel()
//                            itemView.findViewById<ImageView>(R.id.iv_image).clearAnimation()
//                        }

                    } else {
                        Glide.with(itemView.findViewById<ImageView>(R.id.iv_image).context)
                            .load(R.drawable.ic_plus_background)
                            .placeholder(R.drawable.ic_plus_background)
                            .into(itemView.findViewById(R.id.iv_image))
                        if (sel_position == postapped) {
                            if (rotate) {
                                rotate = false
                                itemView.findViewById<ImageView>(R.id.iv_image)
                                    .startAnimation(rotate_anti_clock)
                            }
                        }
                    }
                }
            }
            val unfollowRunnable = Runnable {
                refresh = true
                cancelDone = true
                rotate = true
                itemView.findViewById<TextView>(R.id.tv_followseeall)?.visibility = View.GONE
                itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                    .setBackgroundResource(0)
                Glide.with(itemView.findViewById<ImageView>(R.id.iv_image).context)
                    .load(R.drawable.ic_plus_background)
                    .placeholder(R.drawable.ic_plus_background)
                    .into(itemView.findViewById(R.id.iv_image))
            }

            val nRunnable = Runnable {
                refresh = true
                tickDone = false
                rotate = false
                itemView.findViewById<TextView>(R.id.tv_followseeall)?.visibility = View.GONE
                itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                    .setBackgroundResource(0)
                Glide.with(itemView.findViewById<ImageView>(R.id.iv_image).context)
                    .load(R.drawable.ic_black_tick_background)
                    .placeholder(R.drawable.ic_black_tick_background)
                    .into(itemView.findViewById(R.id.iv_image))
            }

            ivImage = itemView.findViewById(R.id.iv_image)
//            image = itemView.findViewById(R.id.iv_channel_icon)
//            linearlayoutc = itemView.findViewById(R.id.linearlayoutc)
//            tvFollowSeeall = itemView.findViewById(R.id.tv_followseeall)

            Glide.with(itemView.context)
                .load(channel?.image)
                .placeholder(R.color.grey_n)
                .into(itemView.findViewById(R.id.iv_channel_icon))


            itemView.findViewById<TextView>(R.id.tv_channel_title).text = channel?.name

            itemView.setOnClickListener {
                searchChannelInterface?.onItemClick(position, channel)
            }
            updateHandler.removeCallbacks(updateRunnable)
            updateHandler.postDelayed(updateRunnable, 1)


            fun unfollow(view: View) {
                refresh = false

                cancelDone = false
                Glide.with(itemView.findViewById<ImageView>(R.id.iv_image).context)
                    .load(R.drawable.ic_cross_red)
                    .placeholder(R.drawable.ic_cross_red)
                    .into(itemView.findViewById(R.id.iv_image))
                itemView.findViewById<TextView>(R.id.tv_followseeall).visibility = View.VISIBLE
                itemView.findViewById<TextView>(R.id.tv_followseeall).text = "unfollowed"
//                view.findViewById<TextView>(R.id.tv_followseeall).visibility = View.VISIBLE
//                view.findViewById<TextView>(R.id.tv_followseeall).text = "unfollowed"
                itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                    .setBackgroundResource(R.drawable.bg_red_border)
                followHandler.removeCallbacks(unfollowRunnable)
                followHandler.postDelayed(unfollowRunnable, 1000)

                updateHandler.removeCallbacks(updateRunnable)
                updateHandler.postDelayed(updateRunnable, 1200)
//                itemView.findViewById<ImageView>(R.id.iv_image)?.postDelayed({
//                    view.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            view.context,
//                            R.anim.rotate_anti_clock
//                        )
//                    )
//                }, 1000)
//                itemView.findViewById<ImageView>(R.id.iv_image)
//                    .startAnimation(rotate_anti_clock)
//
//                rotate_anti_clock.setAnimationListener(object :
//                    Animation.AnimationListener {
//                    override fun onAnimationStart(animation: Animation?) {}
//                    override fun onAnimationRepeat(animation: Animation?) {}
//                    override fun onAnimationEnd(animation: Animation?) {
//                        Glide.with(itemView.findViewById<ImageView>(R.id.iv_image).context)
//                            .load(R.drawable.ic_plus_background)
//                            .placeholder(R.drawable.ic_plus_background)
//                            .into(itemView.findViewById(R.id.iv_image))
//                    }
//                })


            }

            fun follow() {
                tickDone = true
                refresh = false
                Glide.with(itemView.findViewById<ImageView>(R.id.iv_image).context)
                    .load(R.drawable.ic_red_tick_background)
                    .placeholder(R.drawable.ic_red_tick_background)
                    .into(itemView.findViewById(R.id.iv_image))

                itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                    .setBackgroundResource(R.drawable.ic_rec)
                itemView.findViewById<TextView>(R.id.tv_followseeall).visibility = View.VISIBLE
                itemView.findViewById<TextView>(R.id.tv_followseeall).text = "followed"
                followHandler.removeCallbacks(nRunnable)
                followHandler.postDelayed(nRunnable, 1000)
                updateHandler.removeCallbacks(updateRunnable)
                updateHandler.postDelayed(updateRunnable, 1200)
            }

            ivImage?.setOnClickListener {
                postapped = sel_position
                refresh = false
                if (channel!!.isFavorite) {
                    rotate = true
                    unfollow(it)
                    channel.isFavorite = false
                    searchChannelInterface?.onItemChannleUnfollowed2(
                        channel,
                        position
                    )
                } else {
                    follow()
                    channel.isFavorite = true
                    searchChannelInterface?.onItemChannelFollowed2(channel, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ChannelViewHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.trending_channels_item_new, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
//        this.holder = holder
//        holder.setIsRecyclable(false)
        holder.onBind(position, sourcesList[position])
    }

    override fun getItemCount(): Int {
        return sourcesList.size
    }

    fun updateChannelsData(source: List<Source>) {
        this.sourcesList.clear()
        this.sourcesList.addAll(source)
        notifyDataSetChanged()
    }

    fun updateChannelStatus() {
        if (sourcesList.isNotEmpty()) {
            var position = -1
            sourcesList.forEachIndexed { index, trendingChannel ->
                if (trendingChannel.id == Constants.sourceStatusChanged) {
                    position = index
                    return@forEachIndexed
                }
            }
            if (position >= 0) {
                val trendingChannel = sourcesList[position]
                if (Constants.isSourceDataChange) {
                    trendingChannel.isFavorite = Constants.followStatus.toBoolean()
                }
                sourcesList.removeAt(position)
                sourcesList.add(position, trendingChannel)
                notifyItemChanged(position, trendingChannel)
                Constants.isSourceDataChange = false
                Constants.sourceStatusChanged = null
                Constants.followStatus = null
                Constants.itemPosition = -1
            }
        }
    }

}