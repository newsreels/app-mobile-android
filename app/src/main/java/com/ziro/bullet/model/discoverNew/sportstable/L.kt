package com.ziro.bullet.model.discoverNew.sportstable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class L(
    val Tables: List<Table>
) : Parcelable