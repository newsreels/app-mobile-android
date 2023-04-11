package com.ziro.bullet.activities.changereels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.activities.HashTagDetailsActivity
import com.ziro.bullet.data.TYPE
import com.ziro.bullet.data.models.topics.Topics
import com.ziro.bullet.fragments.test.ReelInnerActivity
import com.ziro.bullet.presenter.FollowUnfollowPresenter
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.item_topics.view.title
import kotlinx.android.synthetic.main.item_topics_feed.view.*


class ReelTopicsAdapter(
    private val context: Context,
    private val list: ArrayList<Topics>,
    private val fromReels: Boolean
) : RecyclerView.Adapter<ReelTopicsAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, topic: Topics, fromReels: Boolean) {
            val presenter = FollowUnfollowPresenter(context as Activity?)

            itemView.title.text = "#" + topic.name
//            Glide.with(context)
//                    .load(topic.image)
//                    .centerCrop()
//                    .into(itemView.image)

            if (topic.isFavorite) {
                itemView.selected.progress = 1f
            } else {
                itemView.selected.progress = 0f
            }

//            if (topic.color != null)
//                itemView.container.setCardBackgroundColor(
//                        ColorStateList.valueOf(
//                                Color.parseColor(
//                                        topic.color
//                                )
//                        )
//                )

            itemView.setOnClickListener {
                if (fromReels) {
                    val intent = Intent(context, ReelInnerActivity::class.java)
                    intent.apply {
                        putExtra(ReelInnerActivity.REEL_F_HASHTAG, topic.name)
                        putExtra(ReelInnerActivity.REEL_F_TITLE, topic.name)
                    }
                    context.startActivity(intent)
                } else {
//                    val intent = Intent(context, ChannelPostActivity::class.java)
                    val intent = Intent(context, HashTagDetailsActivity::class.java)
                    intent.putExtra("type", TYPE.TOPIC)
                    intent.putExtra("mContext", topic.context)
                    intent.putExtra("id", topic.id)
                    intent.putExtra("name", topic.name)
                    intent.putExtra("favorite", topic.isFavorite)
                    context.startActivity(intent)
                }
            }

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
        holder.bind(context, list[position], fromReels)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}