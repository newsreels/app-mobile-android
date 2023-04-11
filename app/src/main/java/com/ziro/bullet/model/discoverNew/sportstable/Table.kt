package com.ziro.bullet.model.discoverNew.sportstable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Table(
    val LTT: Int,
//    val phrX: @RawValue List<Any>,
    val team: List<Team>
) : Parcelable