package com.ziro.bullet.model.discoverNew.liveScore

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Team(
    val Gd: Int,
    val ID: String,
    val Nm: String? = null,
    val CoNm: String? = null,
    val Img: String?,
    val tbd: Int
) : Parcelable