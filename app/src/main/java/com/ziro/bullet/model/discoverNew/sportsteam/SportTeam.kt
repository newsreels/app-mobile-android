package com.ziro.bullet.model.discoverNew.sportsteam

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SportTeam(
    val Eid: String,
    val Lu: List<Lu>
) : Parcelable