package com.ziro.bullet.adapters.discover_new

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels
import com.ziro.bullet.utills.Constants

class TrendingChannelsAdapter :
    RecyclerView.Adapter<TrendingChannelsAdapter.TrendingChannelsViewHolder>() {

    private var trendingChannelsList = mutableListOf<DiscoverNewChannels>()
    private lateinit var searchFirstChildInterface: SearchFirstChildInterface
    private lateinit var discoverChildInterface: DiscoverChildInterface

    var isDiscoverpage: Boolean = false
    var postapped = -1
    var tickDone: Boolean = false
    var firsttime: Boolean = true
    var refresh: Boolean = true
    var cancelDone: Boolean = true

    inner class TrendingChannelsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(discoverNewChannels: DiscoverNewChannels, sel_position: Int) {
            var imgPlus: ImageView? = null
            var linearlayoutc: LinearLayout? = null
            var mRunnable: Runnable? = null
            var nRunnable: Runnable? = null
            var mHandler: Handler = Handler()
            val updateHandler = Handler(Looper.getMainLooper())
            val updateRunnable = Runnable {
                if (refresh) {
                    if (discoverNewChannels.favorite) {
                        Glide.with(imgPlus!!.context)
                            .load(R.drawable.ic_black_tick_background)
                            .placeholder(R.drawable.ic_black_tick_background)
                            .into(imgPlus!!)

                    } else {
                        imgPlus?.context?.let {
                            Glide.with(it)
                                .load(R.drawable.ic_plus_background)
                                .placeholder(R.drawable.ic_plus_background)
                                .into(imgPlus!!)
                        }
                    }
                }
            }

            updateHandler.removeCallbacks(updateRunnable)
            updateHandler.postDelayed(updateRunnable, 1000)

            Glide.with(itemView.context)
                .load(discoverNewChannels.icon)
                .placeholder(R.color.grey_n)
                .into(itemView.findViewById(R.id.iv_channel_icon))

            itemView.findViewById<TextView>(R.id.tv_channel_title).text = discoverNewChannels.name

            imgPlus = itemView.findViewById(R.id.imgPlus)
            linearlayoutc = itemView.findViewById(R.id.linearlayoutc)

            mRunnable = Runnable {
                refresh = true
                itemView.findViewById<TextView>(R.id.tv_follow)?.visibility = View.GONE
                itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                    .setBackgroundResource(0)
                Glide.with(imgPlus!!.context)
                    .load(R.drawable.ic_plus_background)
                    .placeholder(R.drawable.ic_plus_background)
                    .into(imgPlus!!)

            }

            nRunnable = Runnable {
                refresh = true
                itemView.findViewById<TextView>(R.id.tv_follow)?.visibility = View.GONE
                itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                    .setBackgroundResource(0)
                Glide.with(imgPlus!!.context)
                    .load(R.drawable.ic_black_tick_background)
                    .placeholder(R.drawable.ic_black_tick_background)
                    .into(imgPlus!!)

            }

            itemView.setOnClickListener {

                if (isDiscoverpage) {
                    if (discoverChildInterface != null) {
                        discoverChildInterface.searchChildChannelSecondOnClick(
                            trendingChannelsList[position]
                        )
                    }
                } else {
                    if (searchFirstChildInterface != null) {
                        searchFirstChildInterface.searchChildChannelSecondOnClick(
                            trendingChannelsList[position]
                        )
                    }
                }

            }
            //crosscheck after api response for onclick position
            if (sel_position == postapped) {
                //onclick done and after api response cross checking if value set properly
                //t == t
                if (discoverNewChannels.favorite) {
                    if (discoverNewChannels.favorite == tickDone) {
                        //do ntn as it is chnaged
                    } else {
                        Glide.with(imgPlus!!.context)
                            .load(R.drawable.ic_black_tick_background)
                            .placeholder(R.drawable.ic_black_tick_background)
                            .into(imgPlus)
                    }
                }

                if (!discoverNewChannels.favorite) {
                    if (discoverNewChannels.favorite == cancelDone) {
                        //do ntn as + set
                    } else {
                        // will not go to this case
                        Glide.with(imgPlus!!.context)
                            .load(R.drawable.ic_plus_background)
                            .placeholder(R.drawable.ic_plus_background)
                            .into(imgPlus!!)
                    }
                }

            }

            fun unfollow(view: View) {
                refresh = false

                cancelDone = false
                Glide.with(itemView.findViewById<ImageView>(R.id.imgPlus).context)
                    .load(R.drawable.ic_cross_red)
                    .placeholder(R.drawable.ic_cross_red)
                    .into(imgPlus)
                itemView.findViewById<TextView>(R.id.tv_follow).visibility = View.VISIBLE
                itemView.findViewById<TextView>(R.id.tv_follow).text = "unfollowed"
                itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                    .setBackgroundResource(R.drawable.bg_red_border)
                mHandler.removeCallbacks(mRunnable)
                mHandler.postDelayed(mRunnable, 1000)

                updateHandler.removeCallbacks(updateRunnable)
                updateHandler.postDelayed(updateRunnable, 1200)
                itemView.findViewById<ImageView>(R.id.imgPlus)?.postDelayed({
                    view.startAnimation(
                        AnimationUtils.loadAnimation(
                            view.context,
                            R.anim.rotate_anti_clock
                        )
                    )
                }, 1000)
            }

            fun follow() {
                tickDone = true
                refresh = false
                Glide.with(itemView.findViewById<ImageView>(R.id.imgPlus).context)
                    .load(R.drawable.ic_red_tick_background)
                    .placeholder(R.drawable.ic_red_tick_background)
                    .into(imgPlus)
                itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                    .setBackgroundResource(R.drawable.ic_rec)
                itemView.findViewById<TextView>(R.id.tv_follow).visibility = View.VISIBLE
                itemView.findViewById<TextView>(R.id.tv_follow).text = "followed"
                mHandler.removeCallbacks(nRunnable)
                mHandler.postDelayed(nRunnable, 1000)
                updateHandler.removeCallbacks(updateRunnable)
                updateHandler.postDelayed(updateRunnable, 1200)
            }

            imgPlus?.setOnClickListener {
                refresh = false

                postapped = sel_position
                if (!isDiscoverpage) {
                    if (discoverNewChannels.favorite) {
                        unfollow(it)
                        discoverNewChannels.favorite = false
                        searchFirstChildInterface.onItemChannleUnfollowed(
                            discoverNewChannels,
                            position
                        )
                    } else {
                        follow()
                        discoverNewChannels.favorite = true
                        searchFirstChildInterface.onItemChannelFollowed(
                            discoverNewChannels,
                            position
                        )
                    }
                } else {
                    if (discoverNewChannels.favorite) {
                        unfollow(it)

                        discoverNewChannels.favorite = false
                        discoverChildInterface.onItemChannelUnfollowed(
                            discoverNewChannels,
                            position
                        )
                    } else {
                        follow()
                        discoverNewChannels.favorite = true
                        discoverChildInterface.onItemChannelFollowed(discoverNewChannels, position)
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingChannelsViewHolder {
        return TrendingChannelsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.trending_channels_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrendingChannelsViewHolder, position: Int) {

        holder.onBind(trendingChannelsList[position], position)

    }

    fun addChildListener(
        searchFirstChildInterface: SearchFirstChildInterface,
        isDiscoverpage: Boolean
    ) {
        this.searchFirstChildInterface = searchFirstChildInterface
        this.isDiscoverpage = isDiscoverpage
    }

    fun addChildListenerDiscover(
        discoverChildInterface: DiscoverChildInterface,
        isDiscoverpage: Boolean
    ) {
        this.discoverChildInterface = discoverChildInterface
        this.isDiscoverpage = isDiscoverpage
    }

    override fun getItemCount(): Int {
        return trendingChannelsList.size
    }

    fun updateTrendingChannels(trendingChannelsList: List<DiscoverNewChannels>) {
        this.trendingChannelsList.clear()
        this.trendingChannelsList.addAll(trendingChannelsList)
        notifyDataSetChanged()
    }

    fun updateSingleChannel(channel: DiscoverNewChannels, position: Int) {
        trendingChannelsList.removeAt(position)
        trendingChannelsList.add(position, channel)
        notifyItemChanged(position, channel)
    }

    fun updateChannelStatus() {
        if (trendingChannelsList.isNotEmpty()) {
            var position = -1
            trendingChannelsList.forEachIndexed { index, trendingChannel ->
                if (trendingChannel.id == Constants.sourceStatusChanged) {
                    position = index
                    return@forEachIndexed
                }
            }
            if (position >= 0) {
                val trendingChannel = trendingChannelsList[position]
                if (Constants.isSourceDataChange) {
                    trendingChannel.favorite = Constants.followStatus.toBoolean()
                }
                trendingChannelsList.removeAt(position)
                trendingChannelsList.add(position, trendingChannel)
                notifyItemChanged(position, trendingChannel)
            }
        }
    }
}
