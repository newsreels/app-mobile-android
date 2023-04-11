package com.ziro.bullet.model.discoverNew.tabletest

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Team(

    @SerializedName("rnk") var rnk: Int? = null,
    @SerializedName("Tid") var Tid: String? = null,
    @SerializedName("win") var win: Int? = 0,
    @SerializedName("winn") var winn: String? = null,
    @SerializedName("wreg") var wreg: Int? = null,
    @SerializedName("wot") var wot: Int? = null,
    @SerializedName("wap") var wap: Int? = null,
    @SerializedName("Tnm") var Tnm: String? = null,
    @SerializedName("lst") var lst: Int? = 0,
    @SerializedName("lstn") var lstn: String? = null,
    @SerializedName("lreg") var lreg: Int? = null,
    @SerializedName("lot") var lot: Int? = null,
    @SerializedName("lap") var lap: Int? = null,
    @SerializedName("drw") var drw: Int? = null,
    @SerializedName("drwn") var drwn: String? = null,
    @SerializedName("gf") var gf: Int? = null,
    @SerializedName("ga") var ga: Int? = null,
    @SerializedName("gd") var gd: Int? = null,
    @SerializedName("pf") var pf: Int? = null,
    @SerializedName("pa") var pa: Int? = null,
    @SerializedName("ptsn") var ptsn: String? = null,
    @SerializedName("Ipr") var Ipr: Int? = null,
    @SerializedName("pts") var pts: Int? = 0,
    @SerializedName("pld") var pld: Int? = 0,
    @SerializedName("nr") var nr: String? = "0",
    @SerializedName("nrr") var nrr: String? = "0",
    @SerializedName("bab") var bab: String? = null,
    @SerializedName("bob") var bob: String? = null,
    @SerializedName("td") var td: String? = "0"

) : Parcelable