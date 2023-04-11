package com.ziro.bullet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.model.language.region.Region
import kotlinx.android.synthetic.main.region_item.view.*

class RegionDialogAdapter(private val favoriteChangeListener: RegionAdapter.FavoriteChangeListener) :
    RecyclerView.Adapter<RegionDialogAdapter.RegionDialogViewHolder>() {
    private var regionList = listOf<Region>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionDialogViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.region_spinner_item, parent, false)
        return RegionDialogViewHolder(view, favoriteChangeListener)
    }

    override fun onBindViewHolder(holder: RegionDialogViewHolder, position: Int) {
        holder.bind(regionList[position], position)
    }

    override fun getItemCount(): Int {
        return regionList.size
    }

    class RegionDialogViewHolder(
        itemView: View,
        private val favoriteChangeListener: RegionAdapter.FavoriteChangeListener?
    ) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(region: Region, position: Int) {
            itemView.tv_region.text = region.name

            if (region.favorite) {
                itemView.iv_selected_tick.visibility = View.VISIBLE
//                itemView.setBackgroundColor(
//                    ContextCompat.getColor(
//                        itemView.context,
//                        R.color.selected_lang_bg
//                    )
//                )
            } else {
                itemView.iv_selected_tick.visibility = View.INVISIBLE
//                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            }

            itemView.setOnClickListener {
                favoriteChangeListener?.onRegionSelection(region, position)
            }
        }
    }

    fun updateRegion(regionList: List<Region>) {
        this.regionList = regionList
        notifyDataSetChanged()
    }

}