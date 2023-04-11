package com.ziro.bullet.following.ui.item_model

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.ziro.bullet.R
import kotlinx.android.synthetic.main.item_header.view.*


@EpoxyModelClass(
    layout = R.layout.item_header
)
abstract class HeaderItemModel: EpoxyModelWithHolder<HeaderItemModel.Holder>() {

    @EpoxyAttribute
    lateinit var header: String;

    override fun bind(holder: Holder) {
        super.bind(holder)
        //can be commented
        holder.view.textViewHeader.text = header
    }

    class Holder: EpoxyHolder() {
        lateinit var view: View
        override fun bindView(itemView: View) {
            view = itemView
        }
    }
}