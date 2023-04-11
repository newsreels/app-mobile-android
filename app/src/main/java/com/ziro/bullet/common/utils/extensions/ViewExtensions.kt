package com.ziro.bullet.common.utils.extensions

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ziro.bullet.R

fun View.isGone(isGone: Boolean) {
    this.visibility = if (isGone) View.GONE else View.VISIBLE
}

fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .centerCrop()
        .placeholder(R.drawable.error_image)
        .into(this)
}