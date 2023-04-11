package com.ziro.bullet.onboarding.ui.categories.item_model

import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.ziro.bullet.R
import com.ziro.bullet.common.utils.extensions.loadImage
import kotlinx.android.synthetic.main.item_onboarding_category.view.*


@EpoxyModelClass(
    layout = R.layout.item_onboarding_category
)
abstract class OnboardingCategoriesItemModel :
    EpoxyModelWithHolder<OnboardingCategoriesItemModel.Holder>() {

    @EpoxyAttribute
    var imageUrl: String? = null

    @EpoxyAttribute
    var name: String? = null

    @EpoxyAttribute
    var itemSelected: Boolean = false

    @EpoxyAttribute
    var onItemClick: (() -> Unit)? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder) {
            view.tvLabel.text = name
            view.iv_cat_image.loadImage(imageUrl ?: "")
            val background =
                if (itemSelected) R.drawable.onboarding_selected_topic_bg else R.drawable.onboarding_topics_bg
            val color = if (itemSelected) R.color.white else R.color.black
            view.tvLabel.setTextColor(ContextCompat.getColor(view.context, color))
            view.card.background = ContextCompat.getDrawable(
                view.context, background
            )
            view.setOnClickListener {
                onItemClick?.invoke()
            }
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var view: View
        override fun bindView(itemView: View) {
            view = itemView
        }
    }
}