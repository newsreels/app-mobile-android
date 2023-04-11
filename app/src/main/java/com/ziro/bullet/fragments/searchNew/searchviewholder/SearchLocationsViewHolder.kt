package com.ziro.bullet.fragments.searchNew.searchviewholder

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.fragments.searchNew.searchchildadapter.SearchLocationAdapter
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.model.discoverNew.DiscoverNewChannels

class SearchLocationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val searchLocationAdapter = SearchLocationAdapter()
    private lateinit var rvLocations: RecyclerView
    fun onBind(
        position: Int,
        locationlist: List<Location>,
        searchFirstChildInterface: SearchFirstChildInterface
    ) {
//        itemView.findViewById<LinearLayout>(R.id.ll_topics_shimmer).visibility = View.GONE
        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = context.resources.getString(R.string.places)
        }
//        itemView.findViewById<TextView>(R.id.tv_more).apply {
//            text = "See More"
//        }
        if(locationlist.size >= 5){
            itemView.findViewById<TextView>(R.id.tv_more).apply {
                text = context.resources.getString(R.string.see_more)
            }
        }else{
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.GONE
        }


//        fun String.toColor(): Int = Color.parseColor(this)
//        itemView.findViewById<TextView>(R.id.tv_more).setTextColor("#EB5165".toColor())

        itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
            searchFirstChildInterface.searchChildOnPlacesClick(locationlist)
        }

        if (locationlist.isNotEmpty()) {


            searchLocationAdapter.updateTopics(locationlist)
            if (!::rvLocations.isInitialized) {
                rvLocations = itemView.findViewById(R.id.rv_location)
                searchLocationAdapter.addChildListener(searchFirstChildInterface)
                itemView.findViewById<RecyclerView>(R.id.rv_location).apply {
                    visibility = View.VISIBLE
                    layoutManager =
                        LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                    val itemDecorator =  DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider));
                    rvLocations.addItemDecoration(itemDecorator)

                    adapter = searchLocationAdapter
                }
            }
        }
    }

//    fun updateTopicPositionin(position:Int,topic: DiscoverNewTopic?) {
//        if (topic != null) {
//            searchLocationAdapter.updateSingleTopic(topic,position)
//        }
//    }

    fun updateChannelPositionin(position: Int, channel: Location?) {
        if (channel != null) {
            searchLocationAdapter.updateSingleChannel(channel, position)
        }
    }
}