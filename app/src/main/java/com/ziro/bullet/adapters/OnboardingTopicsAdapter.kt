package com.ziro.bullet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.data.models.topics.Topics
import kotlinx.android.synthetic.main.new_topic_item.view.*

class OnboardingTopicsAdapter(
    private val context: Context, private val list: ArrayList<Topics>
) : RecyclerView.Adapter<OnboardingTopicsAdapter.ViewHolder>() {

    var favoriteChangeListener: FavoriteChangeListener? = null
    var onItemClick: ((Topics) -> Unit)? = null

    class ViewHolder(itemView: View, private val favoriteChangeListener: FavoriteChangeListener?) :
        RecyclerView.ViewHolder(itemView) {
        lateinit var itemColor:LinearLayout
        lateinit var name:TextView


        init {
            itemColor = itemView.findViewById(R.id.player_name)
            name = itemView.findViewById(R.id.name)
        }

        fun bind(context: Context, topic: Topics, onItemClick: ((Topics) -> Unit)?) {
            itemView.name.text = topic.name

            if (topic.isFavorite) {
                itemColor.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                name.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
                itemView.cross.visibility = View.VISIBLE
            } else {
                itemColor.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.theme_color_1
                    )
                )
                name.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                itemView.cross.visibility = View.GONE
            }

            itemView.setOnClickListener {
                topic.isFavorite = !topic.isFavorite
                favoriteChangeListener?.onChange()
                onItemClick?.invoke(topic)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.new_topic_item, parent, false)
        return ViewHolder(view, favoriteChangeListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, list[position], onItemClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface FavoriteChangeListener {
    fun onChange()
}