package com.ziro.bullet.background

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val VIDEO_RATIO_DEFAULT = "default"
const val VIDEO_RATIO_SCALED = "scaled"
const val VIDEO_RATIO_11 = "11"
const val VIDEO_RATIO_169 = "169"
const val VIDEO_RATIO_34 = "34"

@Parcelize
data class UploadInfo(
    var id: String,
    var type: String,
    var source: String = "",
    var videoInfo: VideoInfo,
    var videoStatus: String,
    var key: String,
    var error: String
) : Parcelable {
    constructor(
        id: String,
        type: String,
        source: String = "",
        videoInfo: VideoInfo,
        videoStatus: String
    ) : this(
        id, type, source, videoInfo, videoStatus, "",""
    )

}

@Parcelize
data class VideoInfo(
    var path: String,
    var width: Int,
    var height: Int,
    var startTime: Long,
    var endTime: Long,
    var videoRatio: String
) : Parcelable


data class BackgroundEvent(
    var type: Int,
    var progress: Int,
    var data: String,
    var id: String,
    var error: String
) {
    constructor(type: Int, id: String) : this(
        type, 0, "", id,""
    )

    constructor(type: Int, id: String, error: String) : this(
        type, 0, "", id,error
    )
}
