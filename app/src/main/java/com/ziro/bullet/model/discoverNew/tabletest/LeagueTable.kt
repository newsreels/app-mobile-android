package com.ziro.bullet.model.discoverNew.tabletest

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LeagueTable(

    @SerializedName("L") var L: ArrayList<L> = arrayListOf()

) : Parcelable