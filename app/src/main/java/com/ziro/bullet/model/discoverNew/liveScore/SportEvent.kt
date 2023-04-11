package com.ziro.bullet.model.discoverNew.liveScore

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ziro.bullet.model.discoverNew.football.Pids
import com.ziro.bullet.model.discoverNew.football.SeriesInfo
import com.ziro.bullet.model.discoverNew.football.Sids
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SportEvent(
    //football
    val Awt: Int,
    val Edf: Long,
    val Eds: Long,
    val Ern: Int,
    val Ewt: Int,
    val Pid: Int,
//    val Pids: Pids,
//    val Sids: Sids,
//    val Tr1: String,
    val Tr1ET: String? = "",
    val Tr1OR: String? = "",
    val Tr2: String? = "",
    val Tr2ET: String? = "",
    val Tr2OR: String? = "",
    val Trh1: String? = "",
    val Trh2: String? = "",
    val Trp1: String? = "",
    val Trp2: String? = "",
//    val seriesInfo: SeriesInfo,
    //
    val ComX: Int,
    val ECo: String,
    val EO: Int,
    val Eact: Int,
    val Ebat: Int,
    val Ecov: Int,
    val Ehid: Int,
    val Eid: String,
    val Epr: Int,
    val Eps: String,
    val EpsL: String,
    val ErnInf: String?,
    val Esd: Long,
    val Ese: Long,
    val Esid: Int,
    val Et: Int,
    val EtTx: String,
    val Exd: Int,
    val IncsX: Int,
    val LuC: Int,
    val LuUT: Long,
    val LuX: Int,
    val SDFowX: Int,
    val SDInnX: Int,
    val Spid: Int,
    val StatX: Int,
    val Stg: Stg?,
    val SubsX: Int,
    @SerializedName("T1") val team1: List<Team>,
    @SerializedName("T2") val team2: List<Team>,
    val TCho: Int,
    val TPa: Int,
    val Tr1C1: Int,
    val Tr1C2: Int?,
    val Tr1CO1: Double,
    val Tr1CO2: Double?,
    val Tr1CW1: Int,
    val Tr1CW2: Int,
    val Tr2C1: Int,
    val Tr2C2: Int?,
    val Tr2CO1: Double,
    val Tr2CO2: Double?,
    val Tr2CW1: Int,
    val Tr2CW2: Int
) : Parcelable