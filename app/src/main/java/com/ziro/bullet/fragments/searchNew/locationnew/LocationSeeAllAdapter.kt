package com.ziro.bullet.fragments.searchNew.locationnew

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.data.models.location.Location
import com.ziro.bullet.data.models.sources.Source


class LocationSeeAllAdapter(
    private var context: Activity,
    private var searchLocationInterface: SearchLocationInterface?
) : RecyclerView.Adapter<LocationSeeAllAdapter.ChannelViewHolder?>() {
    private var source = mutableListOf<Location>()
    private var holder: ChannelViewHolder? = null
    var ivSelected: ImageView? = null
    var linearlayoutc: LinearLayout? = null
    private var image: ImageView? = null

    inner class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tv_channel_title: TextView

        init {
            image = itemView.findViewById(R.id.iv_channel_icon)
            tv_channel_title = itemView.findViewById(R.id.tv_channel_title)
            ivSelected = itemView.findViewById(R.id.iv_image)
            linearlayoutc = itemView.findViewById(R.id.linearlayoutc)
        }


        fun onBind(position: Int, location: Location?) {

            image?.let {
                Glide.with(it.context)
                    .load(location?.image)
                    .placeholder(R.drawable.img_place_holder)
                    .into(itemView.findViewById(R.id.iv_channel_icon))
            }
            itemView.findViewById<TextView>(R.id.tv_channel_title).text = location?.name
//            itemView.findViewById<TextView>(R.id.tv_pos).text = position.toString()

            linearlayoutc?.setOnClickListener {
                searchLocationInterface?.onItemClick(position, location)
            }

            if (source[position].isFavorite) {
                Glide.with(ivSelected!!.context)
                    .load(R.drawable.ic_black_tick_background)
                    .placeholder(R.drawable.ic_black_tick_background)
                    .into(ivSelected!!)
//            DrawableCompat.setTint(ivSelected!!.drawable, Color.BLACK)
            } else {
                Glide.with(ivSelected!!.context)
                    .load(R.drawable.ic_plus_background)
                    .placeholder(R.drawable.ic_plus_background)
                    .into(ivSelected!!)
            }

            ivSelected?.setOnClickListener {
                if (location!!.isFavorite) {
                    location.isFavorite = false
                    searchLocationInterface?.onItemLocationUnfollowed2(
                        location,
                        position
                    )
                } else {
                    location.isFavorite = true
                    searchLocationInterface?.onItemLocationFollowed2(location, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ChannelViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.trending_channels_item_new, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        this.holder = holder
        holder.setIsRecyclable(false)
        holder.onBind(position, source[position])
    }

    override fun getItemCount(): Int {
        return source.size
    }

    fun updateChannelsData(source: ArrayList<Location>) {
        this.source = source
        notifyDataSetChanged()
    }

    fun updateChannelFollowPosition2(position: Int, channel: Source?) {
//        if (channel != null) {
//            this.source[position] = channel
//        }
        notifyItemChanged(position)
    }

}