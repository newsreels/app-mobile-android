package com.ziro.bullet.model.discoverNew.sportsteam

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lu(
    val Ps: List<P>,
    val Tnb: Int
) : Parcelable