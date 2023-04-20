package com.ziro.bullet.fragments.test

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.ziro.bullet.R
import com.ziro.bullet.fragments.VideoInnerFragment.VideoPlayerConfig
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.utills.DoubleClickHandler.DoubleClick
import com.ziro.bullet.utills.DoubleClickHandler.DoubleClickListener

class VideoTestAdapter(
    private var context: Context,
    private var videoPreparedListener: OnVideoPreparedInterface,
) : RecyclerView.Adapter<VideoTestAdapter.VideoViewHolder?>() {
    var videos = listOf<ReelsItem>()
    private var holder: VideoTestAdapter.VideoViewHolder? = null
    private lateinit var videoItem: ReelsItem
    private var defaultBandwidthMeter: DefaultBandwidthMeter? = null
    private var dataSourceFactory: DataSource.Factory? = null
    lateinit var exoPlayer: SimpleExoPlayer
    lateinit var mediaSource: MediaSource

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoTestAdapter.VideoViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_video_test, parent, false)
        return VideoViewHolder(view, context, videoPreparedListener)
    }

    override fun onBindViewHolder(holder: VideoTestAdapter.VideoViewHolder, position: Int) {
        this.holder = holder
        if (videos[position] == null) {
            holder.llEndOfReels?.visibility = View.VISIBLE
            holder.playerView?.visibility = View.GONE
            return
        }
        holder.llEndOfReels?.visibility = View.GONE
        holder.playerView?.visibility = View.VISIBLE
        videoItem = videos[position]

//        holder.tvTitle?.text = "testing"

        if (videoItem.source.isFavorite) {
            holder.followtxt?.text = context.getString(R.string.following)
            videoItem.source.isFavorite = true
        } else {
            videoItem.source.isFavorite = false
            holder.followtxt?.text = context.getString(R.string.follow)
        }


        if (!videoItem.media.isNullOrEmpty())
            holder.setVideoPath(videoItem.media, holder.absoluteAdapterPosition, videoItem);

    }

    override fun getItemCount(): Int {
        return videos.size
    }

    inner class VideoViewHolder(
        var itemView: View?,
        var context: Context,
        var videoPreparedListener: OnVideoPreparedInterface
    ) : RecyclerView.ViewHolder(itemView!!) {
        var tvTitle: TextView? = null
        var followtxt: TextView? = null
        var playerView: PlayerView? = null
        var llEndOfReels: LinearLayout? = null

        init {
            tvTitle = itemView.findViewById(R.id.tv_item_title)
            playerView = itemView.findViewById(R.id.video_view)
            llEndOfReels = itemView.findViewById(R.id.ll_end_of_reel_msg)
        }


        fun setVideoPath(streamUrl: String, absposition: Int, videoItem: ReelsItem) {
//
//            var replaceString = NetworkSpeedUtils.deviceRam(videoItem.media, context)
//
//            val loadControl: LoadControl =
//                DefaultLoadControl.Builder().setAllocator(DefaultAllocator(true, 16))
//                    .setBufferDurationsMs(
//                        VideoPlayerConfig.MIN_BUFFER_DURATION,
//                        VideoPlayerConfig.MAX_BUFFER_DURATION,
//                        VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
//                        VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER
//                    )
//                    .setTargetBufferBytes(-1).setPrioritizeTimeOverSizeThresholds(true)
//                    .createDefaultLoadControl()
//
//            if (replaceString.endsWith(".m3u8")) {
//                val adaptiveTrackSelection = AdaptiveTrackSelection.Factory()
//
//                val rendererFactory = DefaultRenderersFactory(context)
//                rendererFactory.setEnableDecoderFallback(true)
//
//                exoPlayer = ExoPlayerFactory.newSimpleInstance(
//                    context,
//                    rendererFactory,
//                    DefaultTrackSelector(adaptiveTrackSelection),
//                    loadControl
//                )
//
//                defaultBandwidthMeter = DefaultBandwidthMeter()
//
//                dataSourceFactory = DefaultDataSourceFactory(
//                    context,
//                    Util.getUserAgent(context, "nib"),
//                    defaultBandwidthMeter
//                )
//
//                val uri = Uri.parse(replaceString)
//
//                mediaSource = HlsMediaSource.Factory(dataSourceFactory)
//                    .setAllowChunklessPreparation(true)
//                    .createMediaSource(uri)
//                itemView.findViewById<PlayerView>(R.id.video_view).player = exoPlayer
//
//                exoPlayer.seekTo(0)
//                exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
//
//                exoPlayer.prepare(mediaSource)
//
//                playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
//
//                if (absposition == 0) {
//                    exoPlayer.playWhenReady = true
//                }
////                itemView.setOnClickListener {
////
////                    videoPreparedListener.openReelviewmore()
////                }
////
////
////                itemView.setOnLongClickListener {
////                    videoPreparedListener.doubleClickLike()
////                    true
////                }
//
//
//                itemView.setOnClickListener(DoubleClick(object : DoubleClickListener {
//                    override fun onSingleClick(view: View) {
//                        videoPreparedListener.openReelviewmore()
//                    }
//
//                    override fun onDoubleClick(view: View) {
//                        videoPreparedListener.doubleClickLike()
//                    }
//                }))
//
//                videoPreparedListener.onVideoFun(VideoItemRes(exoPlayer, absposition, videoItem))
//
//
////            text2.setOnTouchListener((view, motionEvent) -> {
////                constraintL2.setVisibility(View.GONE);
////                return gestureDetectorCompat.onTouchEvent(motionEvent);
////                // return false;
////            });
////            text2.setOnTouchListener(new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(View v, MotionEvent event) {
////                    return false;
////                }
////            });
//
//            }
//
        }
    }

    fun updateReelData(videoItems: List<ReelsItem>) {
        this.videos = videoItems as MutableList<ReelsItem>
        notifyDataSetChanged()
    }

    interface OnVideoPreparedInterface {
        fun onVideoFun(exoPlayerItem: VideoItemRes)
        fun openReelviewmore()
        fun doubleClickLike()
    }
}