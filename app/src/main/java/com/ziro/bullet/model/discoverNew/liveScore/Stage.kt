package com.ziro.bullet.model.discoverNew.liveScore

data class Stage(
    val Ccd: String,
    val Ccdiso: String,
    val Chi: Int,
    val Cid: String,
    val Cnm: String,
    val Csnm: String,
    //football
    val CompD: String,
    val CompId: String,
    val CompN: String,
    val CompST: String,
    //
    val Events: List<SportEvent>,
    val Scd: String,
    val Scu: Int,
    val Sdn: String,
    val Sds: String,
    val Shi: Int,
    val Sid: String,
    val Snm: String
)