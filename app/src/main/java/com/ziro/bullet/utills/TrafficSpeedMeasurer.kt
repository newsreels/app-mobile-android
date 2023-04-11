package com.ziro.bullet.utills

import android.net.TrafficStats
import android.os.*
import java.util.*

interface ITrafficSpeedListener {
    fun onTrafficSpeedMeasured(upStream: Double, downStream: Double)
}

class TrafficSpeedMeasurer(private val mTrafficType: TrafficType) {

    private var mTrafficSpeedListener: ITrafficSpeedListener? = null
    private val mHandler: SamplingHandler
    private var mLastTimeReading: Long = 0
    private var mPreviousUpStream: Long = -1
    private var mPreviousDownStream: Long = -1

    fun registerListener(iTrafficSpeedListener: ITrafficSpeedListener?) {
        mTrafficSpeedListener = iTrafficSpeedListener
    }

    fun removeListener() {
        mTrafficSpeedListener = null
    }

    fun startMeasuring() {
        mHandler.startSamplingThread()
        mLastTimeReading = SystemClock.elapsedRealtime()
    }

    fun stopMeasuring() {
        mHandler.stopSamplingThread()
        finalReadTrafficStats()
    }

    private fun readTrafficStats() {
        val newBytesUpStream =
            (if (mTrafficType == TrafficType.MOBILE) TrafficStats.getMobileTxBytes() else TrafficStats.getTotalTxBytes()) * 1024
        val newBytesDownStream =
            (if (mTrafficType == TrafficType.MOBILE) TrafficStats.getMobileRxBytes() else TrafficStats.getTotalRxBytes()) * 1024
        val byteDiffUpStream = newBytesUpStream - mPreviousUpStream
        val byteDiffDownStream = newBytesDownStream - mPreviousDownStream
        synchronized(this) {
            val currentTime = SystemClock.elapsedRealtime()
            var bandwidthUpStream = 0.0
            var bandwidthDownStream = 0.0
            if (mPreviousUpStream >= 0) {
                bandwidthUpStream = byteDiffUpStream * 1.0 / (currentTime - mLastTimeReading)
            }
            if (mPreviousDownStream >= 0) {
                bandwidthDownStream = byteDiffDownStream * 1.0 / (currentTime - mLastTimeReading)
            }
            if (mTrafficSpeedListener != null) {
                mTrafficSpeedListener?.onTrafficSpeedMeasured(
                    bandwidthUpStream,
                    bandwidthDownStream
                )
            }
            mLastTimeReading = currentTime
        }
        mPreviousDownStream = newBytesDownStream
        mPreviousUpStream = newBytesUpStream
    }

    private fun finalReadTrafficStats() {
        readTrafficStats()
        mPreviousUpStream = -1
        mPreviousDownStream = -1
    }

    inner class SamplingHandler constructor(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_START -> {
                    readTrafficStats()
                    sendEmptyMessageDelayed(MSG_START, SAMPLE_TIME)
                }
                else -> throw IllegalArgumentException("Unknown what=" + msg.what)
            }
        }

        fun startSamplingThread() {
            sendEmptyMessage(MSG_START)
        }

        fun stopSamplingThread() {
            removeMessages(MSG_START)
        }


    }

    companion object {
        private const val SAMPLE_TIME: Long = 1000
        private const val MSG_START = 1
    }

    enum class TrafficType {
        MOBILE, ALL
    }

    init {
        val thread = HandlerThread("ParseThread")
        thread.start()
        mHandler = SamplingHandler(thread.looper)
    }
}

object TrafficUtils {
    private const val B: Long = 1
    private val KB: Long = B * 1024
    private val MB: Long = KB * 1024
    private val GB: Long = MB * 1024

    fun parseSpeed(bytes: Double, inBits: Boolean): String {
        var value = if (inBits) bytes * 8 else bytes
        value = if (value == 0.0) 3650.0 else value
        return if (value < KB) {
            java.lang.String.format(
                Locale.getDefault(),
                "%.0f " + (if (inBits) "b" else "B") + "/s",
                value
            )

        } else if (value < MB) {
            java.lang.String.format(
                Locale.getDefault(),
                "%.0f K" + (if (inBits) "b" else "B") + "/s",
                value / KB
            )
        } else if (value < GB) {
            java.lang.String.format(
                Locale.getDefault(),
                "%.0f M" + (if (inBits) "b" else "B") + "/s",
                value / MB
            )
        } else {
            java.lang.String.format(
                Locale.getDefault(),
                "%.0f G" + (if (inBits) "b" else "B") + "/s",
                value / GB
            )
        }
    }
}