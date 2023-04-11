package com.ziro.bullet.adapters.NewFeed

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.activities.ChannelPostActivity
import com.ziro.bullet.activities.HashTagDetailsActivity
import com.ziro.bullet.data.TYPE
import com.ziro.bullet.data.models.topics.Topics
import com.ziro.bullet.presenter.FollowUnfollowPresenter
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.item_topics.view.container
import kotlinx.android.synthetic.main.item_topics.view.image
import kotlinx.android.synthetic.main.item_topics.view.ivSelected
import kotlinx.android.synthetic.main.item_topics.view.title
import kotlinx.android.synthetic.main.item_topics_feed.view.*
import java.util.*

class TopicsAdapter(
    private val context: Context,
    private val list: ArrayList<Topics>,
) : RecyclerView.Adapter<TopicsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, topic: Topics) {
            val presenter = FollowUnfollowPresenter(context as Activity?)

            itemView.title.text = "#"+topic.name
//            Glide.with(context)
//                .load(topic.image)
//                .centerCrop()
//                .into(itemView.image)

            if (topic.isFavorite) {
                itemView.selected.progress = 1f
            } else {
                itemView.selected.progress = 0f
            }

            itemView.setOnClickListener {
                val intent = Intent(context, ChannelPostActivity::class.java)
                //shifa test
//                val intent = Intent(context, HashTagDetailsActivity::class.java)
                intent.apply {
                    intent.putExtra("type", TYPE.TOPIC)
                    intent.putExtra("id", topic.id)
                    intent.putExtra("mContext", topic.context)
                    intent.putExtra("name", topic.name)
                    intent.putExtra("favorite", topic.isFavorite)
                }
                context.startActivity(intent)
            }

//            if (!topic.color.isEmpty())
//                itemView.container.setCardBackgroundColor(
//                        ColorStateList.valueOf(
//                                Color.parseColor(
//                                        topic.color
//                                )
//                        )
//                )

            itemView.selected.setOnClickListener {
                Utils.followAnimation(itemView.selected, 500)
                if (topic.isFavorite) {
                    topic.isFavorite = false
                    presenter.unFollowTopic(
                            topic.id,
                            absoluteAdapterPosition
                    ) { position, flag ->
                        run {
                            if (!flag)
                                topic.isFavorite = true
                        }
                    }
                } else {
                    topic.isFavorite = true
                    presenter.followTopic(
                            topic.id,
                            absoluteAdapterPosition
                    ) { position, flag ->
                        run {
                            if (!flag)
                                topic.isFavorite = false
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_topics_feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}