package com.newsreels.app.fragments.searchNew.searchviewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.newsreels.app.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.newsreels.app.fragments.searchNew.searchchildadapter.SearchLocationAdapter
import com.newsreels.app.model.searchResult.Authors

class SearchExtraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val searchLocationAdapter = SearchLocationAdapter()
    private lateinit var rvLocations: RecyclerView
    fun onBind(
        position: Int,
        trendingTopics: List<Authors>,
        searchFirstChildInterface: SearchFirstChildInterface
    ) {
//        itemView.findViewById<LinearLayout>(R.id.ll_trending_channel_shimmer).visibility = View.GONE
//        itemView.findViewById<LinearLayout>(R.id.tv_title).visibility = View.GONE
//        itemView.findViewById<LinearLayout>(R.id.tv_more).visibility = View.GONE
//        itemView.findViewById<LinearLayout>(R.id.rv_location).visibility = View.GONE


    }
}