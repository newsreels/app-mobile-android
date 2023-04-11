package com.ziro.bullet.mediapicker.gallery.process

import android.content.Context
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.video.trimmer.interfaces.OnProgressVideoListener
import com.video.trimmer.interfaces.OnRangeSeekBarListener
import com.video.trimmer.interfaces.OnTrimVideoListener
import com.video.trimmer.interfaces.OnVideoListener
import com.ziro.bullet.R
import com.ziro.bullet.mediapicker.utils.BackgroundExecutor
import com.ziro.bullet.mediapicker.utils.UiThreadExecutor
import kotlinx.android.synthetic.main.view_trimmer_reel.view.*
import java.io.File
import java.lang.ref.WeakReference
import java.util.*


class VideoTrimmerReel @JvmOverloads constructor(context: Context) : FrameLayout(context) {

    private var scaled: Boolean = false

    private lateinit var mSrc: Uri
    private var mFinalPath: String? = null

    private var mAspectRatio: String? = "default"

    private var mMaxDuration: Int = -1
    private var mMinDuration: Int = -1
    private var mListeners: ArrayList<OnProgressVideoListener> = ArrayList()

    private var mOnTrimVideoListener: OnTrimVideoListener? = null
    private var mOnVideoListener: OnVideoListener? = null

    private var mDuration = 0f
    private var mTimeVideo = 0f
    private var mStartPosition = 0f

    private var mEndPosition = 0f
    private var mResetSeekBar = true
    private val mMessageHandler = MessageHandler(this)

    private var reqWidth: Int = 0
    private var reqHeight: Int = 0
    private var videoProportion: Float = 0F
    private var screenProportion: Float = 0F
    private var isReel: Boolean = false

    private var destinationPath: String
        get() {
            if (mFinalPath == null) {
                val folder = Environment.getExternalStorageDirectory()
                mFinalPath = folder.path + File.separator
            }
            return mFinalPath ?: ""
        }
        set(finalPath) {
            mFinalPath = finalPath
        }

    init {
        init(context)
    }


    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_trimmer_reel, this, true)
        setUpListeners()
        setUpMargins()
    }

    private fun setUpListeners() {
        mListeners = ArrayList()
        mListeners.add(object : OnProgressVideoListener {
            override fun updateProgress(time: Float, max: Float, scale: Float) {
                updateVideoProgress(time)
            }
        })

        val gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    onClickVideoPlayPause()
                    return true
                }
            })

        video_loader.setOnErrorListener { _, what, _ ->
            mOnTrimVideoListener?.onError("Something went wrong reason : $what")
            false
        }

        video_loader.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        handlerTop.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                onPlayerIndicatorSeekChanged(progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                onPlayerIndicatorSeekStart()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                onPlayerIndicatorSeekStop(seekBar)
            }
        })

        timeLineBar.addOnRangeSeekBarListener(object : OnRangeSeekBarListener {
            override fun onCreate(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {

            }

            override fun onSeek(
                rangeSeekBarView: RangeSeekBarView,
                index: Int,
                value: Float,
                left: Float,
                right: Float
            ) {
                handlerTop.visibility = View.INVISIBLE
                onSeekThumbs(index, value)
                if (left.toInt() == 0) {
                    bg_view.right = right.toInt()
//                    timeFrame.left = bg_view.left
//                    timeFrame.right = bg_view.right
                } else {
                    bg_view.left = left.toInt()
//                    timeFrame.left = bg_view.left
//                    timeFrame.right = bg_view.right
                }
            }

            override fun onSeekStart(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
            }

            override fun onSeekStop(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
                onStopSeekThumbs()
            }
        })



        layout_surface_view.setOnClickListener {
            layout_surface_view.background =
                ContextCompat.getDrawable(context, R.drawable.bg_round_scale)
            if (!scaled) {
                tvScale.visibility = VISIBLE
                video_loader.scaleX = videoProportion / screenProportion
                video_loader.scaleY = videoProportion / screenProportion
                scaled = true
            } else {
                video_loader.scaleX = 1f
                video_loader.scaleY = 1f
                tvScale.visibility = VISIBLE
                scaled = false
            }

            Handler().postDelayed({
                layout_surface_view.background = null
                tvScale.visibility = GONE
                if (!scaled) {
                    ivScale.setImageResource(R.drawable.icon_scale_unselected)
                } else {
                    ivScale.setImageResource(R.drawable.icon_scale_selected)
                }
            }, 500)
        }

        video_loader.setOnPreparedListener { mp -> onVideoPrepared(mp) }
        video_loader.setOnCompletionListener { onVideoCompleted() }
    }

    private fun onPlayerIndicatorSeekChanged(progress: Int, fromUser: Boolean) {
        val duration = (mDuration * progress / 1000L)
        if (fromUser) {
            if (duration < mStartPosition) setProgressBarPosition(mStartPosition)
            else if (duration > mEndPosition) setProgressBarPosition(mEndPosition)
        }
    }

    private fun onPlayerIndicatorSeekStart() {
        mMessageHandler.removeMessages(SHOW_PROGRESS)
        video_loader.pause()
        icon_video_play.visibility = View.VISIBLE
        notifyProgressUpdate(false)
    }

    private fun onPlayerIndicatorSeekStop(seekBar: SeekBar) {
        mMessageHandler.removeMessages(SHOW_PROGRESS)
        video_loader.pause()
        icon_video_play.visibility = View.VISIBLE

        val duration = (mDuration * seekBar.progress / 1000L).toInt()
        video_loader.seekTo(duration)
        notifyProgressUpdate(false)
    }

    private fun setProgressBarPosition(position: Float) {
        if (mDuration > 0) handlerTop.progress = (1000L * position / mDuration).toInt()
    }

    private fun setUpMargins() {
        val marge = timeLineBar.thumbs[0].widthBitmap
        val lp = timeLineView.layoutParams as LayoutParams
        lp.setMargins(marge, 0, marge, 0)
        timeLineView.layoutParams = lp
    }

    fun onSaveClicked() {
        if (scaled)
            mAspectRatio = "scaled"
        mOnTrimVideoListener?.onTrimStarted(mStartPosition, mEndPosition, mAspectRatio)
        icon_video_play.visibility = View.VISIBLE
        video_loader.pause()
    }

    private fun onClickVideoPlayPause() {
        if (video_loader.isPlaying) {
            icon_video_play.visibility = View.VISIBLE
            mMessageHandler.removeMessages(SHOW_PROGRESS)
            video_loader.pause()
        } else {
            icon_video_play.visibility = View.GONE
            if (mResetSeekBar) {
                mResetSeekBar = false
                video_loader.seekTo(mStartPosition.toInt())
            }
            mMessageHandler.sendEmptyMessage(SHOW_PROGRESS)
            video_loader.start()
        }
    }

    fun onCancelClicked() {
        video_loader.stopPlayback()
        mOnTrimVideoListener?.cancelAction()
    }

    private fun onVideoPrepared(mp: MediaPlayer) {
//        mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)

        val videoWidth = mp.videoWidth
        val videoHeight = mp.videoHeight
        Log.d("TAG", "onVideoPrepared: videoWidth = " + videoWidth)
        Log.d("TAG", "onVideoPrepared: videoHeight = " + videoHeight)
        videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
        val screenWidth = container.width
        val screenHeight = container.height
        screenProportion = screenWidth.toFloat() / screenHeight.toFloat()
        val lp = video_loader.layoutParams

//        Log.d("TAG", "onVideoPrepared: screenHeight = " + screenHeight)
//        Log.d("TAG", "onVideoPrepared: screenWidth = " + screenWidth)

        if (videoWidth > videoHeight) {
            //width high
            reqHeight = (screenWidth.toFloat() / videoProportion).toInt()
            lp.height = screenHeight
            lp.width = (screenWidth.toFloat() * videoProportion).toInt()


            Log.d("TAG", "onVideoPrepared: lp.width  = " + lp.width)
            Log.d("TAG", "onVideoPrepared: lp.height  = " + lp.height)
        } else {
            //height high
            reqWidth = (videoProportion * screenHeight.toFloat()).toInt()
            lp.width = (videoProportion * screenHeight.toFloat()).toInt()
            lp.height = screenHeight

            layout_surface_view.visibility = GONE

            Log.d("TAG", "onVideoPrepared: lp.width  = " + lp.width)
            Log.d("TAG", "onVideoPrepared: lp.height  = " + lp.height)
        }


        video_loader.layoutParams = lp

        icon_video_play.visibility = View.VISIBLE

//        if (!isReel)
//            changeHeightOnAspectRatio(0.5625)

        mDuration = video_loader.duration.toFloat()
        setSeekBarPosition()

        setTimeFrames()

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(mSrc.toString())

        val hahawidth =
            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
        val hahaheight =
            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))

        val haharotation =
            Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION))
        retriever.release()

        if (haharotation == 90) {
            mOnVideoListener?.onVideoPrepared(hahaheight, hahawidth)
        } else
            mOnVideoListener?.onVideoPrepared(hahawidth, hahaheight)
    }


    private fun changeHeightOnAspectRatio(ratio: Double) {
//        val cropParams = crop_frame.layoutParams
//
//        if (videoProportion > screenProportion) {
//            //width high
//            cropParams.width = (reqHeight / ratio).toInt()
//            cropParams.height = reqHeight
//        } else {
//            //height high
//            cropParams.width = reqWidth
//            cropParams.height = (reqWidth * ratio).toInt()
//        }
//
//        val layoutTransition = layout_surface_view.layoutTransition
//        layoutTransition.setDuration(200) // Change duration
//        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
//
//        crop_frame.visibility = VISIBLE
//        crop_frame.layoutParams = cropParams
//        crop_frame_top.visibility = VISIBLE
//        crop_frame_start.visibility = VISIBLE
//        crop_frame_bottom.visibility = VISIBLE
//        crop_frame_end.visibility = VISIBLE
    }

    private fun setSeekBarPosition() {
        when {
            mDuration >= mMaxDuration && mMaxDuration != -1 -> {
                mStartPosition = 0f
                mEndPosition = mMaxDuration.toFloat()
                timeLineBar.setThumbValue(0, (mStartPosition * 100 / mDuration))
                timeLineBar.setThumbValue(1, (mEndPosition * 100 / mDuration))
                try {
                    bg_view.right =
                        (timeLineBar.thumbs.get(1).pos + timeLineBar.thumbs.get(1).widthBitmap).toInt()
                } catch (ignore: Exception) {
                }

            }
            mDuration <= mMinDuration && mMinDuration != -1 -> {
                mStartPosition = mDuration / 2 - mMinDuration / 2
                mEndPosition = mDuration / 2 + mMinDuration / 2
                timeLineBar.setThumbValue(0, (mStartPosition * 100 / mDuration))
                timeLineBar.setThumbValue(1, (mEndPosition * 100 / mDuration))
            }
            else -> {
                mStartPosition = 0f
                mEndPosition = mDuration
            }
        }
        video_loader.seekTo(mStartPosition.toInt())
        mTimeVideo = mDuration
        timeLineBar.initMaxWidth()
    }

    private fun setTimeFrames() {
        textTimeSelection.text =
            String.format("%s - %s", stringForTime(mStartPosition), stringForTime(mEndPosition))
    }

    fun stringForTime(timeMs: Float): String {
        val totalSeconds = (timeMs / 1000).toInt()
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        val mFormatter = Formatter()
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    private fun onSeekThumbs(index: Int, value: Float) {
        when (index) {
            Thumb.LEFT -> {
                mStartPosition = (mDuration * value / 100L)
                video_loader.seekTo(mStartPosition.toInt())
            }
            Thumb.RIGHT -> {
                mEndPosition = (mDuration * value / 100L)
            }
        }
        setTimeFrames()
        mTimeVideo = mEndPosition - mStartPosition
    }

    private fun onStopSeekThumbs() {
        mMessageHandler.removeMessages(SHOW_PROGRESS)
        video_loader.pause()
        icon_video_play.visibility = View.VISIBLE
    }

    private fun onVideoCompleted() {
        video_loader.seekTo(mStartPosition.toInt())
    }

    private fun notifyProgressUpdate(all: Boolean) {
        if (mDuration == 0f) return
        val position = video_loader.currentPosition
        if (all) {
            for (item in mListeners) {
                item.updateProgress(position.toFloat(), mDuration, (position * 100 / mDuration))
            }
        } else {
            mListeners[0].updateProgress(
                position.toFloat(),
                mDuration,
                (position * 100 / mDuration)
            )
        }
    }

    private fun updateVideoProgress(time: Float) {
        if (video_loader == null) return
        if (time <= mStartPosition && time <= mEndPosition) handlerTop.visibility = View.INVISIBLE
        else handlerTop.visibility = View.VISIBLE
        if (time >= mEndPosition) {
            mMessageHandler.removeMessages(SHOW_PROGRESS)
            video_loader.pause()
            icon_video_play.visibility = View.VISIBLE
            mResetSeekBar = true
            return
        }
        setProgressBarPosition(time)
    }

    fun setVideoInformationVisibility(visible: Boolean): VideoTrimmerReel {
        timeFrame.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    fun setOnTrimVideoListener(onTrimVideoListener: OnTrimVideoListener): VideoTrimmerReel {
        mOnTrimVideoListener = onTrimVideoListener
        return this
    }

    fun setOnVideoListener(onVideoListener: OnVideoListener): VideoTrimmerReel {
        mOnVideoListener = onVideoListener
        return this
    }

    fun destroy() {
        BackgroundExecutor.cancelAll("", true)
        UiThreadExecutor.cancelAll("")
    }

    fun setMaxDuration(maxDuration: Int): VideoTrimmerReel {
        mMaxDuration = maxDuration
        return this
    }

    fun setMinDuration(minDuration: Int): VideoTrimmerReel {
        mMinDuration = minDuration
        return this
    }

//    fun setDestinationPath(path: String): VideoTrimmer {
//        destinationPath = path
//        return this
//    }

    fun setVideoURI(videoURI: Uri, width: Int, height: Int): VideoTrimmerReel {
        mSrc = videoURI
        video_loader.setVideoURI(mSrc)
        video_loader.requestFocus()
        timeLineView.setVideo(mSrc)
        return this
    }

    fun isReel(flag: Boolean): VideoTrimmerReel {
        isReel = flag
        return this
    }

    fun setTextTimeSelectionTypeface(tf: Typeface?): VideoTrimmerReel {
        if (tf != null) textTimeSelection.typeface = tf
        return this
    }

    private class MessageHandler internal constructor(view: VideoTrimmerReel) : Handler() {
        private val mView: WeakReference<VideoTrimmerReel> = WeakReference(view)
        override fun handleMessage(msg: Message) {
            val view = mView.get()
            if (view == null || view.video_loader == null) return
            view.notifyProgressUpdate(true)
            if (view.video_loader.isPlaying) sendEmptyMessageDelayed(0, 10)
        }
    }

    companion object {
        private const val MIN_TIME_FRAME = 1000
        private const val SHOW_PROGRESS = 2
    }
}