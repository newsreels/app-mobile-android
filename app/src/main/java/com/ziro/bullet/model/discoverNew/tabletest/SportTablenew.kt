package com.ziro.bullet.model.discoverNew.tabletest

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SportTablenew(

    @SerializedName("Sid") var Sid: String? = null,
    @SerializedName("Snm") var Snm: String? = null,
    @SerializedName("Scd") var Scd: String? = null,
    @SerializedName("Cid") var Cid: String? = null,
    @SerializedName("Cnm") var Cnm: String? = null,
    @SerializedName("Csnm") var Csnm: String? = null,
    @SerializedName("Ccd") var Ccd: String? = null,
    @SerializedName("Ccdiso") var Ccdiso: String? = null,
    @SerializedName("Scu") var Scu: Int? = null,
    @SerializedName("Chi") var Chi: Int? = null,
    @SerializedName("Shi") var Shi: Int? = null,
    @SerializedName("Sdn") var Sdn: String? = null,
    @SerializedName("LeagueTable") var LeagueTable: LeagueTable? = LeagueTable()

) : Parcelable