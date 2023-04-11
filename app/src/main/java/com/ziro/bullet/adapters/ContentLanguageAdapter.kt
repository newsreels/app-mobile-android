package com.ziro.bullet.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.data.dataclass.ContentLanguage
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.item_content_langauge.view.*
import java.util.*

class ContentLanguageAdapter(
    private val context: Context, private val languageList: ArrayList<ContentLanguage>
) :
    RecyclerView.Adapter<ContentLanguageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_content_langauge, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, languageList[position])
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, lang: ContentLanguage) {
            itemView.title.setText(lang.name)
            itemView.text.setText(lang.sample)
            Glide.with(context)
                .load(lang.image)
                .circleCrop()
                .into(itemView.image)

            if (lang.favorite) {
                itemView.imageView5.progress = 1f
            } else {
                itemView.imageView5.progress = 0f
            }

            itemView.setOnClickListener {
                lang.favorite = !lang.favorite
                Utils.followAnimation(itemView.imageView5, 500)
            }
        }
    }
}