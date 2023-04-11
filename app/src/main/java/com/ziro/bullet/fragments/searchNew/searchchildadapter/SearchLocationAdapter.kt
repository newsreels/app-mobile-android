package com.ziro.bullet.fragments.searchNew.searchchildadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.data.models.location.Location

class SearchLocationAdapter :
    RecyclerView.Adapter<SearchLocationAdapter.SearchLocationsViewHolder>() {

    private var locationList = listOf<Location>()
    private lateinit var location: Location
    private lateinit var searchFirstChildInterface: SearchFirstChildInterface
    var ivSelected: ImageView? = null
    var linearlayoutc: LinearLayout? = null

    inner class SearchLocationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun onBind(location: Location) {

            Glide.with(itemView.context)
                .load(location.image)
                .placeholder(R.drawable.img_place_holder)
                .into(itemView.findViewById(R.id.iv_topic_icon))

            itemView.findViewById<TextView>(R.id.tv_topic_title).text = location.name
            ivSelected = itemView.findViewById(R.id.imgPlus)
            linearlayoutc = itemView.findViewById(R.id.linearlayoutc)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLocationsViewHolder {
        return SearchLocationsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_locations_tab_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchLocationsViewHolder, position: Int) {
        holder.onBind(locationList[position])

        if (locationList != null && locationList.size > 0) {
            var loc: Location = locationList.get(position)

            linearlayoutc?.setOnClickListener {
                searchFirstChildInterface.searchChildLocationSecondOnClick(locationList[position])
            }


            if (loc.isFavorite) {
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
            ivSelected?.setOnClickListener {
//                Utils.followAnimation(ivSelected!!, 500);
                if (loc.isFavorite) {
                    loc.isFavorite = false
                    searchFirstChildInterface.onItemUnfollowedLoc(
                        loc,
                        position
                    )
                } else {
                    loc.isFavorite = true
                    searchFirstChildInterface.onItemFollowedLoc(loc, position)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }


    fun updateTopics(trendingTopicList: List<Location>) {
        this.locationList = trendingTopicList
        notifyDataSetChanged()
    }

    fun addChildListener(searchFirstChildInterface: SearchFirstChildInterface) {
        this.searchFirstChildInterface = searchFirstChildInterface
    }

    fun updateSingleChannel(channel: Location, position: Int) {
//        this.channel = channel
        notifyItemChanged(position, channel)
    }


}



//    fun updateSingleTopic(topic: DiscoverNewTopic, position: Int) {
//        this.topic = topic
//        notifyItemChanged(position, topic)
//    }

