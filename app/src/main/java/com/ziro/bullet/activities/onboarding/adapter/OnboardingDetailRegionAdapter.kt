package com.ziro.bullet.activities.onboarding.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.data.dataclass.Region
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.item_language.view.*

class OnboardingDetailRegionAdapter(
    var context: Context,
    var list: ArrayList<Region>
) : RecyclerView.Adapter<OnboardingDetailRegionAdapter.ViewHolder>() {
    var onItemClick: ((Region) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, item: Region, onItemClick: ((Region) -> Unit)?) {
            itemView.tvLabel.setText(item.name)
//            itemView.tvText.visibility = View.GONE

//            Glide.with(context)
//                .load(item.flag)
//                .circleCrop()
//                .centerCrop()
//                .into(itemView.flag)

//            if (item.favorite) {
//                itemView.radio1.setImageResource(R.drawable.ic_checkbox_active)
//            } else {
//                itemView.radio1.setImageResource(R.drawable.ic_checkbox_inactive)
//            }
//
//            itemView.item.setOnClickListener {
//                item.favorite = !item.favorite
//                if (item.favorite) {
//                    itemView.radio1.setImageResource(R.drawable.ic_checkbox_active)
//                } else {
//                    itemView.radio1.setImageResource(R.drawable.ic_checkbox_inactive)
//                }
//                onItemClick?.invoke(item)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_on_bord_language, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, list[position],onItemClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
