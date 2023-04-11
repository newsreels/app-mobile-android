package com.ziro.bullet.model.discoverNew.sportstable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SportsTable(
    val Ccd: String,
    val Ccdiso: String,
    val Chi: Int,
    val Cid: String,
    val Cnm: String,
    val Csnm: String,
    val LeagueTable: LeagueTable,
    val Scd: String,
    val Scu: Int,
    val Sdn: String,
    val Shi: Int,
    val Sid: String,
    val Snm: String
) : Parcelable