package com.ziro.bullet.background

import android.util.Log
import org.greenrobot.eventbus.EventBus

class BroadcastEmitter() : TaskObserver {
    companion object {
        const val BACKGROUND_STATUS = "BACKGROUND_STATUS"
        const val TYPE_BACKGROUND_PROCESS = 7

        const val BG_PROCESS_START = "start"
        const val BG_PROCESSING_COMPLETED = "processing_completed"
        const val BG_UPLOAD_PROGRESS = "upload_progress"
        const val BG_UPLOAD_COMPLETED = "upload_completed"
        const val BG_PUBLISHING = "publishing"
        const val BG_PUBLISHED = "published"
        const val BG_ERROR = "error"
        const val BG_STOP = "stop_service"

    }

    private fun send(id: String, data: String) {
        val messageEvent = BackgroundEvent(TYPE_BACKGROUND_PROCESS, id)
        messageEvent.data = data
        EventBus.getDefault().post(messageEvent)
    }

    private fun sendProgress(id: String, percentage: Int) {
        val messageEvent = BackgroundEvent(TYPE_BACKGROUND_PROCESS, id)
        messageEvent.data = BG_UPLOAD_PROGRESS
        messageEvent.progress = percentage
        EventBus.getDefault().post(messageEvent)
    }

    private fun sendError(id: String, error: String) {
        val messageEvent = BackgroundEvent(TYPE_BACKGROUND_PROCESS, id,error)
        messageEvent.data = BG_ERROR
        EventBus.getDefault().post(messageEvent)
    }

    override fun onStart(id: String) {
        send(id,BG_PROCESS_START)
    }

    override fun onProcessingCompleted(id: String) {
        send(id,BG_PROCESSING_COMPLETED)
    }

    override fun onUploadProgress(percentage: Int, id: String) {
        sendProgress(id,percentage)
    }

    override fun onUploadCompleted(id: String) {
        send(id,BG_UPLOAD_COMPLETED)
    }

    override fun onPublishStarted(id: String) {
        send(id,BG_PUBLISHING)
    }

    override fun onPublish(id: String) {
        send(id, BG_PUBLISHED)
    }

    override fun onError(id: String,error: String) {
        sendError(id,error)
    }

    override fun stopService(id: String) {
        Log.d("TAG", "stopService: ")
        send(id,BG_STOP)
    }
}