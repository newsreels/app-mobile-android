package com.video.trimmer.interfaces

import android.net.Uri

interface OnTrimVideoListener {
    fun onTrimStarted(mStartPosition: Float, mEndPosition: Float, mAspectRatio: String?)
    fun getResult(uri: Uri)
    fun cancelAction()
    fun onError(message: String)
}
