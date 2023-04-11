package com.ziro.bullet.model.discoverNew.sportsteam

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class P(
    val Fn: String,
    val Ln: String,
    val Pid: String,
    val Pos: Int,
    val PosA: Int,
    val Snm: String
) : Parcelable