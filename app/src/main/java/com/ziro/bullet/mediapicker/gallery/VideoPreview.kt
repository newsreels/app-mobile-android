package com.ziro.bullet.mediapicker.gallery

import android.animation.LayoutTransition
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.video.trimmer.interfaces.OnProgressVideoListener
import com.ziro.bullet.R
import com.ziro.bullet.background.*
import com.ziro.bullet.mediapicker.utils.BackgroundExecutor
import com.ziro.bullet.mediapicker.utils.UiThreadExecutor
import kotlinx.android.synthetic.main.view_video_preview.view.*
import java.util.*

class VideoPreview @JvmOverloads constructor(context: Context) : FrameLayout(context) {
    private lateinit var mSrc: Uri

    private var mMaxDuration: Int = -1
    private var mMinDuration: Int = -1
    private var mListeners: ArrayList<OnProgressVideoListener> = ArrayList()

    private lateinit var videoRatio: String
    private var mTimeVideo = 0f
    private var mStartPosition = 0f

    private var mEndPosition = 0f
    private var mResetSeekBar = true

    private var reqWidth: Int = 0
    private var reqHeight: Int = 0
    private var videoProportion: Float = 0F
    private var screenProportion: Float = 0F
    private var isReel: Boolean = false

    init {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_video_preview, this, true)
        setUpListeners()
    }

    private fun setUpListeners() {

        val gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    onClickVideoPlayPause()
                    return true
                }
            })

        video_loader.setOnErrorListener { _, what, _ ->
            false
        }

        video_loader.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        video_loader.setOnPreparedListener { mp -> onVideoPrepared(mp) }
        video_loader.setOnCompletionListener { onVideoCompleted() }
    }


    private fun onClickVideoPlayPause() {
        if (video_loader.isPlaying) {
            icon_video_play.visibility = View.VISIBLE
            video_loader.pause()
        } else {
            icon_video_play.visibility = View.GONE
            if (mResetSeekBar) {
                mResetSeekBar = false
                video_loader.seekTo(mStartPosition.toInt())
            }
            video_loader.start()
        }
    }

    private fun onVideoPrepared(mp: MediaPlayer) {


        val videoWidth = mp.videoWidth
        val videoHeight = mp.videoHeight
        videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
        val screenWidth = layout_surface_view.width
        val screenHeight = layout_surface_view.height
        screenProportion = screenWidth.toFloat() / screenHeight.toFloat()

        val lp = video_loader.layoutParams

        if (videoProportion > screenProportion) {
            //width high
            reqHeight = (screenWidth.toFloat() / videoProportion).toInt()
            lp.height = screenHeight
            lp.width = (screenWidth.toFloat() * videoProportion).toInt()
        } else {
            //height high
            reqWidth = (videoProportion * screenHeight.toFloat()).toInt()
            lp.width = (videoProportion * screenHeight.toFloat()).toInt()
            lp.height = screenHeight
        }

        video_loader.layoutParams = lp

        when (videoRatio) {
            VIDEO_RATIO_DEFAULT -> {
                video_loader.scaleX = 1f
                video_loader.scaleY = 1f
            }
            VIDEO_RATIO_SCALED -> {
                video_loader.scaleX = videoProportion / screenProportion
                video_loader.scaleY = videoProportion / screenProportion
            }
            VIDEO_RATIO_11 -> {
                changeHeightOnAspectRatio(1.0)
            }
            VIDEO_RATIO_169 -> {
                changeHeightOnAspectRatio(0.5625)
            }
            VIDEO_RATIO_34 -> {
                changeHeightOnAspectRatio(1.3333)
            }
        }
        video_loader.start()
        icon_video_play.visibility = View.GONE
    }

    private fun changeHeightOnAspectRatio(ratio: Double) {
        val cropParams = crop_frame.layoutParams

        if (videoProportion > screenProportion) {
            //width high
            cropParams.width = (reqHeight / ratio).toInt()
            cropParams.height = reqHeight
        } else {
            //height high
            cropParams.width = reqWidth
            cropParams.height = (reqWidth * ratio).toInt()
        }

        val layoutTransition = layout_surface_view.layoutTransition
        layoutTransition.setDuration(200) // Change duration
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        crop_frame.visibility = VISIBLE
        crop_frame.layoutParams = cropParams
        crop_frame_top.visibility = VISIBLE
        crop_frame_start.visibility = VISIBLE
        crop_frame_bottom.visibility = VISIBLE
        crop_frame_end.visibility = VISIBLE
    }


    private fun onVideoCompleted() {
        video_loader.seekTo(mStartPosition.toInt())
    }


    fun destroy() {
        BackgroundExecutor.cancelAll("", true)
        UiThreadExecutor.cancelAll("")
    }

    fun setMaxDuration(maxDuration: Int): VideoPreview {
        return this
    }

    fun setMinDuration(minDuration: Int): VideoPreview {
        return this
    }

//    fun setDestinationPath(path: String): VideoPreview {
//        destinationPath = path
//        return this
//    }

    fun setVideoRatio(ratio: String): VideoPreview {
        videoRatio = ratio

        return this
    }

    fun setVideoURI(videoURI: Uri): VideoPreview {
        mSrc = videoURI
        Log.d("TAGss", "setVideoURI: $mSrc")

        video_loader.setVideoURI(mSrc)
        video_loader.requestFocus()
        return this
    }

    fun isReel(flag: Boolean): VideoPreview {
        isReel = flag
        return this
    }


    companion object {
        private const val MIN_TIME_FRAME = 1000
        private const val SHOW_PROGRESS = 2
    }
}