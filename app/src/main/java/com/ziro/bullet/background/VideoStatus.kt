package com.ziro.bullet.background

import com.ziro.bullet.CacheData.DbHandler

public class VideoStatus {
    companion object {
        const val PROCESSING = "PROCESSING"
        const val DRAFT = "DRAFT"
        const val UPLOADING = "UPLOADING"
        const val UPLOAD_DONE = "UPLOAD_DONE"
        const val PUBLISHED = "PUBLISH"

        const val ERROR = "ERROR"
        const val TRANSCODING_ERROR = "TRANSCODING_ERROR"
        const val INTERNET_ERROR = "INTERNET_ERROR"
        const val API_ERROR = "API_ERROR"

        const val READY_TO_PUBLISH_YES = "READY_TO_PUBLISH_YES"
        const val READY_TO_PUBLISH_NO = "READY_TO_PUBLISH_NO"
    }
}
