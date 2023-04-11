package com.ziro.bullet.model.discoverNew.football

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SeriesInfo(
    val aggScoreTeam1: Int,
    val aggScoreTeam2: Int,
    val currentLeg: Int,
    val totalLegs: Int
) : Parcelable