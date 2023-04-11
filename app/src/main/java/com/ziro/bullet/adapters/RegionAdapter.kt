package com.ziro.bullet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.model.language.region.Region
import kotlinx.android.synthetic.main.region_item.view.*

class RegionAdapter(private val favoriteChangeListener: FavoriteChangeListener) :
    RecyclerView.Adapter<RegionAdapter.ViewHolder>() {

    private var regionList = listOf<Region>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.region_item, parent, false)
        return ViewHolder(view, favoriteChangeListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(regionList[position], position)
    }

    override fun getItemCount(): Int {
        return regionList.size
    }

    class ViewHolder(itemView: View, private val favoriteChangeListener: FavoriteChangeListener?) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(region: Region, position: Int) {
            itemView.tv_region.text = region.name

            if (region.favorite) {
                itemView.iv_selected_tick.visibility = View.VISIBLE
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.selected_lang_bg
                    )
                )
            } else {
                itemView.iv_selected_tick.visibility = View.INVISIBLE
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            }

            itemView.setOnClickListener {
                if (!region.favorite) {
                    region.favorite = !region.favorite
                    favoriteChangeListener?.onRegionSelection(region, position)
                }
            }
        }
    }

    fun updateRegion(regionList: List<Region>) {
        this.regionList = regionList
        notifyDataSetChanged()
    }

    interface FavoriteChangeListener {
        fun onRegionSelection(region: Region, position: Int)
    }
}