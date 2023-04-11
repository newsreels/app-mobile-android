package com.ziro.bullet.onboarding.ui.categories.item_model

import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.ziro.bullet.R
import kotlinx.android.synthetic.main.item_onboarding_header.view.*

@EpoxyModelClass(
    layout = R.layout.item_onboarding_header
)
abstract class OnboardingHeaderItemModel :
    EpoxyModelWithHolder<OnboardingHeaderItemModel.Holder>() {

    @EpoxyAttribute
    var headerTitle: String? = ""

    @EpoxyAttribute
    var headerSubTitle: String? = ""


    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder.view) {
            val param = this.layoutParams
            if (param != null && param is StaggeredGridLayoutManager.LayoutParams) {
                param.isFullSpan = true
            }
            subHeader.text = headerSubTitle
            header.text = headerTitle
        }
    }

    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        return 2
    }

    class Holder : EpoxyHolder() {
        lateinit var view: View
        override fun bindView(itemView: View) {
            view = itemView
        }
    }
}