package com.ziro.bullet.fragments.searchNew.searchchildadapter

import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.model.discoverNew.DiscoverNewTopic
import com.ziro.bullet.utills.Constants

class SearchTopicsAdapter :
    RecyclerView.Adapter<SearchTopicsAdapter.SearchTopicsViewHolder>() {

    private var trendingTopicList = mutableListOf<DiscoverNewTopic>()
    private lateinit var topic: DiscoverNewTopic
    private lateinit var searchFirstChildInterface: SearchFirstChildInterface
    var ivSelected: ImageView? = null
    var tv_follow: TextView? = null
    var linearlayoutc: LinearLayout? = null
    var postapped = -1
    var tickDone: Boolean = false
    var firsttime: Boolean = true
    var refresh: Boolean = true
    var cancelDone: Boolean = true

    var mHandler: Handler = Handler(Looper.getMainLooper())
    val updateHandler = Handler(Looper.getMainLooper())

    inner class SearchTopicsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun onBind(trendingTopic: DiscoverNewTopic) {

            if (trendingTopic.favorite!!) {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_black_tick_background)
                    .placeholder(R.drawable.ic_black_tick_background)
                    .into(itemView.findViewById(R.id.imgFollow))
            } else {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_plus_background)
                    .placeholder(R.drawable.ic_plus_background)
                    .into(itemView.findViewById(R.id.imgFollow))
            }


            Glide.with(itemView.context)
                .load(trendingTopic.image)
                .placeholder(R.color.grey_n)
                .into(itemView.findViewById(R.id.iv_topic_icon))

            itemView.findViewById<TextView>(R.id.tv_topic_title).text = trendingTopic.name
            ivSelected = itemView.findViewById(R.id.imgFollow)
            tv_follow = itemView.findViewById(R.id.tv_follow)
            linearlayoutc = itemView.findViewById(R.id.linearlayoutc)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTopicsViewHolder {
        return SearchTopicsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_topics_tab_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchTopicsViewHolder, position: Int) {
        holder.onBind(trendingTopicList[position])

        val nRunnable = Runnable {
            refresh = true
            holder.itemView.findViewById<TextView>(R.id.tv_follow)?.visibility = View.GONE
            holder.itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                .setBackgroundResource(0)
            Glide.with(holder.itemView.findViewById<ImageView>(R.id.imgFollow).context)
                .load(R.drawable.ic_black_tick_background)
                .placeholder(R.drawable.ic_black_tick_background)
                .into(holder.itemView.findViewById<ImageView>(R.id.imgFollow))

        }
        val updateRunnable = Runnable {
            if (refresh) {
                if (trendingTopicList[position].favorite == true) {
                    Glide.with(ivSelected!!.context)
                        .load(R.drawable.ic_black_tick_background)
                        .placeholder(R.drawable.ic_black_tick_background)
                        .into(ivSelected!!)
//                DrawableCompat.setTint(ivSelected!!.drawable, Color.BLACK)
                } else {
                    Glide.with(ivSelected!!.context)
                        .load(R.drawable.ic_plus_background)
                        .placeholder(R.drawable.ic_plus_background)
                        .into(ivSelected!!)
                }
            }
        }
        val mRunnable = Runnable {
            refresh = true
            holder.itemView.findViewById<TextView>(R.id.tv_follow)?.visibility = View.GONE
            holder.itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                .setBackgroundResource(0)
            Glide.with(holder.itemView.findViewById<ImageView>(R.id.imgFollow).context)
                .load(R.drawable.ic_plus_background)
                .placeholder(R.drawable.ic_plus_background)
                .into(holder.itemView.findViewById<ImageView>(R.id.imgFollow))


        }

        if (trendingTopicList.isNotEmpty()) {
            val topics: DiscoverNewTopic = trendingTopicList[position]

            updateHandler.removeCallbacks(updateRunnable)
            updateHandler.postDelayed(updateRunnable, 1200)
            holder.itemView.setOnClickListener {
                searchFirstChildInterface.searchChildTopicSecondOnClick(trendingTopicList[position])
            }

            fun unfollow() {
                refresh = false
                cancelDone = false
                holder.itemView.findViewById<ImageView>(R.id.imgFollow)?.let {
                    Glide.with(it.context)
                        .load(R.drawable.ic_cross_red)
                        .placeholder(R.drawable.ic_cross_red)
                        .into(holder.itemView.findViewById<ImageView>(R.id.imgFollow))
                }

                holder.itemView.findViewById<TextView>(R.id.tv_follow)?.visibility = View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.tv_follow)?.text = "unfollowed"
                holder.itemView?.findViewById<ConstraintLayout>(R.id.cons_follow)
                    ?.setBackgroundResource(R.drawable.bg_red_border)

                mRunnable.let { mHandler.removeCallbacks(it) }
                mRunnable.let { mHandler.postDelayed(it, 1000) }

                updateRunnable.let { updateHandler.removeCallbacks(it) }
                updateRunnable.let { updateHandler.postDelayed(it, 1200) }
                var rotateAntiClock =
                    AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rotate_anti_clock)

                holder.itemView.findViewById<ImageView>(R.id.imgFollow)?.postDelayed({
                    holder.itemView.findViewById<ImageView>(R.id.imgFollow)
                        ?.startAnimation(rotateAntiClock)
                }, 1000)


//
//                rotateAntiClock.setAnimationListener(object :
//                    Animation.AnimationListener {
//                    override fun onAnimationStart(animation: Animation?) {}
//                    override fun onAnimationRepeat(animation: Animation?) {}
//                    override fun onAnimationEnd(animation: Animation?) {
//                        holder.itemView?.let {
//                            Glide.with(holder.itemView.findViewById<ImageView>(R.id.iv_follow_channel).context)
//                                .load(R.drawable.ic_plus_background)
//                                .placeholder(R.drawable.ic_plus_background)
//                                .into(it.findViewById<ImageView>(R.id.iv_follow_channel))
//                        }
//                    }
//                })
//
//                view?.findViewById<ImageView>(R.id.iv_follow_channel)
//                    ?.startAnimation(rotateAntiClock)
            }

            fun follow() {
                tickDone = true
                refresh = false
                Glide.with(holder.itemView.findViewById<ImageView>(R.id.imgFollow).context)
                    .load(R.drawable.ic_red_tick_background)
                    .placeholder(R.drawable.ic_red_tick_background)
                    .into(holder.itemView.findViewById<ImageView>(R.id.imgFollow))

                holder.itemView.findViewById<ConstraintLayout>(R.id.cons_follow)
                    .setBackgroundResource(R.drawable.ic_rec)


                holder.itemView.findViewById<TextView>(R.id.tv_follow).visibility = View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.tv_follow).text = "followed"

                mHandler.removeCallbacks(nRunnable)
                mHandler.postDelayed(nRunnable, 1000)
                updateHandler.removeCallbacks(updateRunnable)
                updateHandler.postDelayed(updateRunnable, 1200)
            }

            holder.itemView.findViewById<ImageView>(R.id.imgFollow).setOnClickListener {
//                Utils.followAnimation(ivSelected!!, 500);
                if (topics.favorite == true) {
                    unfollow()
                    topics.favorite = false
                    searchFirstChildInterface.onItemUnfollowed(
                        topics,
                        position
                    )
                } else {
                    follow()
                    topics.favorite = true
                    searchFirstChildInterface.onItemFollowed(topics, position)
                }
            }

        }
    }


    override fun getItemCount(): Int {
        return trendingTopicList.size
    }

    fun updateTopics(trendingTopicList: List<DiscoverNewTopic>) {
        this.trendingTopicList.clear()
        this.trendingTopicList.addAll(trendingTopicList)
        notifyDataSetChanged()
    }

    fun updateSingleTopic(topic: DiscoverNewTopic, position: Int) {
        this.topic = topic
        notifyItemChanged(position, topic)
    }

    fun addChildListener(searchFirstChildInterface: SearchFirstChildInterface) {
        this.searchFirstChildInterface = searchFirstChildInterface
    }

    fun updateTopicStatus() {
        Log.d("Search_Topic_TAG", "updateTopicStatus: ")
        if (trendingTopicList.isNotEmpty()) {
            var position = -1
            trendingTopicList.forEachIndexed { index, trendingTopic ->
                if (trendingTopic.id == Constants.topicsStatusChanged) {
                    position = index
                    return@forEachIndexed
                }
            }
            if (position >= 0) {
                val trendingChannel = trendingTopicList[position]
                if (Constants.isTopicDataChange) {
                    trendingChannel.favorite = Constants.followStatus.toBoolean()
                }
                trendingTopicList.removeAt(position)
                trendingTopicList.add(position, trendingChannel)
                notifyItemChanged(position, trendingChannel)
//                Constants.isTopicDataChange = false
//                Constants.topicsStatusChanged = null
//                Constants.followStatus = null
//                Constants.itemPosition = -1
            }
        }
    }
}