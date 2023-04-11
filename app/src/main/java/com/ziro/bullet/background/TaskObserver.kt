package com.ziro.bullet.background

interface TaskObserver {
    fun onStart(id:String)

    fun onProcessingCompleted(id:String)

    fun onUploadProgress(percentage: Int,id:String)

    fun onUploadCompleted(id:String)

    fun onPublishStarted(id:String)

    fun onPublish(id:String)

    fun onError(id:String,error:String)

    fun stopService(id:String)
}