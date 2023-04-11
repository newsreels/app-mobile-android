package com.ziro.bullet.model.discoverNew.sportstable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Team(
    val Ipr: Int,
    val Tid: String,
    val Tnm: String,
    val bab: String,
    val bob: String,
    val drw: Int,
    val drwn: String,
    val ga: Int,
    val gd: Int,
    val gf: Int,
    val lap: Int,
    val lot: Int,
    val lreg: Int,
    val lst: Int,
    val lstn: String,
    val nr: String,
    val nrr: String,
    val pa: Int,
    val pf: Int,
    val pld: Int,
    val pts: Int,
    val ptsn: String,
    val rnk: Int,
    val td: String,
    val wap: Int,
    val win: Int,
    val winn: String,
    val wot: Int,
    val wreg: Int
) : Parcelable