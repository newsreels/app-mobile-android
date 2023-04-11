package com.ziro.bullet.following.ui.item_model

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.ziro.bullet.R
import com.ziro.bullet.common.utils.extensions.isGone
import kotlinx.android.synthetic.main.item_expandable_layout.view.*


@EpoxyModelClass(
    layout = R.layout.item_expandable_layout
)
abstract class ExpandableContentItemModel :
    EpoxyModelWithHolder<ExpandableContentItemModel.Holder>() {

    @EpoxyAttribute
    lateinit var titleContent: String

    @EpoxyAttribute
    var listItems: List<View>? = emptyList()

    @EpoxyAttribute
    var onExpanderClick: (() -> Unit)? = null

    @EpoxyAttribute
    var contentItemGone: Boolean = false

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder.view) {
            contentTitle.text = titleContent

            contentContainer.removeAllViews()
            contentContainer.invalidate()
            listItems?.forEach {
                try {
                    contentContainer.addView(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            rightArrow.setOnClickListener {
                onExpanderClick?.invoke()
            }
            ll_content.isGone(contentItemGone)
            rightArrow.rotation = if (contentItemGone) 0F else 90F
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var view: View
        override fun bindView(itemView: View) {
            view = itemView
        }
    }
}