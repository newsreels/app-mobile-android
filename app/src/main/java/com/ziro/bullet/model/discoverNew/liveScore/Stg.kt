package com.ziro.bullet.model.discoverNew.liveScore

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stg(
    val Ccd: String,
    val Ccdiso: String,
    val Chi: Int,
    val Cid: String,
    val Cnm: String,
    val Csnm: String,
    val Scd: String,
    val Scu: Int,
    val Sdn: String,
    val Sds: String?,
    val Shi: Int,
    val Sid: String,
    val Snm: String
) : Parcelable