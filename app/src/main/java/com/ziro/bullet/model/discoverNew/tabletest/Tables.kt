package com.ziro.bullet.model.discoverNew.tabletest

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tables(

    @SerializedName("LTT") var LTT: Int? = null,
    @SerializedName("team") var team: ArrayList<Team> = arrayListOf(),
    @SerializedName("phrX") var phrX: ArrayList<String> = arrayListOf()

) : Parcelable