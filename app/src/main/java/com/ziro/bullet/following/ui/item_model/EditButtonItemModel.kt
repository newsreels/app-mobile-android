package com.ziro.bullet.following.ui.item_model

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.ziro.bullet.R
import kotlinx.android.synthetic.main.item_button_edit.view.*
import kotlinx.android.synthetic.main.item_header.view.*


@EpoxyModelClass(
    layout = R.layout.item_button_edit
)
abstract class EditButtonItemModel: EpoxyModelWithHolder<EditButtonItemModel.Holder>() {

    @EpoxyAttribute
    var onEditClick: (()->Unit)? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.editButton.setOnClickListener {
            onEditClick?.invoke()
        }
    }

    class Holder: EpoxyHolder() {
        lateinit var view: View
        override fun bindView(itemView: View) {
            view = itemView
        }
    }
}