package com.ziro.bullet.fragments.test

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.SystemClock
import com.google.android.exoplayer2.util.Util
import com.ziro.bullet.R
import com.ziro.bullet.analytics.AnalyticsEvents.reelDurationEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.fragments.VideoInnerFragment
import com.ziro.bullet.interfaces.LikeInterface
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.DoubleClickHandler.DoubleClick
import com.ziro.bullet.utills.DoubleClickHandler.DoubleClickListener
import de.hdodenhof.circleimageview.CircleImageView


class VideoAdapter(
    private val viewPager2: ViewPager2,
    private val mContext: Context,
    private var reelFraInterface: ReelFraInterface?,
    private var presenter: ReelsNewPresenter?,

    ) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private var mVideoList = ArrayList<ReelsItem>()
    private var currentPlaybackPosition = 0
    private var curAdapPosition = 0
    var startPosition: Long = 0
    private var prevPostion = 0
    private var currentPlaybackState = Player.STATE_IDLE
    private var defaultBandwidthMeter: BandwidthMeter? = null
    private var dataSourceFactory: DataSource.Factory? = null
    lateinit var mediaSource: HlsMediaSource.Factory
    private var viewHolderArray = ArrayList<VideoViewHolder>()
    lateinit var exoPlayer: ExoPlayer
    private var audioManager: AudioManager? = null
    private lateinit var curreelItem: ReelsItem
//    val reelsItem = mVideoList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_video_test, parent, false)
        return VideoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        Log.d("VideoAdapterBind_TAG", "onBindViewHolder: $position")
        val reelsItem = mVideoList[position]
        curreelItem = reelsItem
        if (reelsItem != null) {
            if (!TextUtils.isEmpty(reelsItem.image)) {
                holder.thumbnail.visibility = View.VISIBLE
                Glide.with(mContext).load(reelsItem.getImage()).into(holder.thumbnail);
            } else {
                holder.thumbnail.visibility = View.INVISIBLE
            }
        }

        if (reelsItem != null) {
            val loadControl: LoadControl =
                DefaultLoadControl.Builder().setAllocator(DefaultAllocator(true, 16))
                    .setBufferDurationsMs(
                        VideoInnerFragment.VideoPlayerConfig.MIN_BUFFER_DURATION,
                        VideoInnerFragment.VideoPlayerConfig.MAX_BUFFER_DURATION,
                        VideoInnerFragment.VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                        VideoInnerFragment.VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER
                    ).setTargetBufferBytes(C.LENGTH_UNSET).setPrioritizeTimeOverSizeThresholds(true)
                    .build()

            val adaptiveTrackSelection = AdaptiveTrackSelection.Factory()

            val defaultTrackSelector = DefaultTrackSelector(mContext, adaptiveTrackSelection).also {
//                it.setParameters(
//                    it.buildUponParameters()
//                        .setMaxVideoSize(1280, 720)
//                        .setMinVideoSize(640, 480)
////                        .setForceLowestBitrate(true)
//                        .setAllowMultipleAdaptiveSelections(false)
//                )
            }

            val rendererFactory = DefaultRenderersFactory(mContext)
            rendererFactory.setEnableDecoderFallback(true)

//            exoPlayer = SimpleExoPlayer.newSimpleInstance(
//                mContext,
//                rendererFactory,
//                DefaultTrackSelector(adaptiveTrackSelection),
//                loadControl
//            )
            if (reelsItem.media.endsWith(".m3u8")) {
                defaultBandwidthMeter = DefaultBandwidthMeter.Builder(mContext).setInitialBitrateEstimate(1400000L).build()

                dataSourceFactory = DefaultDataSourceFactory(
                    mContext,
                    Util.getUserAgent(mContext, "nib")
                )
                mediaSource =
                    HlsMediaSource.Factory(dataSourceFactory!!).setAllowChunklessPreparation(false)

                exoPlayer = ExoPlayer.Builder(
                    mContext,
                    rendererFactory,
                    mediaSource,
                    defaultTrackSelector,
                    loadControl,
                    defaultBandwidthMeter!!,
                    DefaultAnalyticsCollector(SystemClock.DEFAULT)
                ).build()

//                exoPlayer = ExoPlayer.Builder(
//                    mContext,
//                    rendererFactory,
//                    mediaSource,
//                    defaultTrackSelector,
//                    loadControl,
//                    defaultBandwidthMeter!!,
//                    this
//                ).build()
                val mediaItem = MediaItem.fromUri(reelsItem.media)
                holder.playerView.player = exoPlayer
                holder.playerView.requestFocus()
                viewHolderArray.add(holder)
                exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                holder.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
            }
        } else {
            Log.e("today", "placeholder ")
        }
        viewHolderArray.forEach { viewholder ->
            //add a check if speaker clicked then to this check - pending
//            if (viewholder.sourceName?.equals(reelsItem.source.name) == true) {
//                if (reelsItem.source.isFavorite) {
//                    viewholder.followtxt?.setText(R.string.following)
//                } else {
//                    viewholder.followtxt?.setText(R.string.follow)
//                }
//            }
            if (Constants.ReelsVolume) {
                viewholder.speaker.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext, R.drawable.ic_volume
                    )
                )
            } else {
                viewholder.speaker.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext, R.drawable.ic_volmute
                    )
                )
            }
        }
        if (reelsItem != null) {
            holder.bind(reelsItem, mContext, position)
        }


    }

    fun setVideoList(videoList: ArrayList<ReelsItem>) {
        mVideoList = videoList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mVideoList.size
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        Log.e("video", "onViewRecycled: ")
        Constants.mViewRecycled = true;
        if (holder in viewHolderArray) {
            Log.e("TAGfv", "onViewRecycled:$holder ")
            viewHolderArray.remove(holder)
            holder.playerView.player?.playWhenReady = false
            holder.playerView.player?.release()
            holder.playerView.player = null
        }
        super.onViewRecycled(holder)

    }

    fun pausePrevPlayback(prevPostion: Int) {
        this.prevPostion = prevPostion
        val recyclerView = viewPager2.getChildAt(0) as? RecyclerView
        val prevViewHolder =
            recyclerView?.findViewHolderForAdapterPosition(prevPostion) as? VideoViewHolder
        prevViewHolder?.playerView?.player?.playWhenReady = false
        prevViewHolder?.playerView?.player?.seekTo(0)
        currentPlaybackState =
            prevViewHolder?.playerView?.player?.playbackState ?: Player.STATE_IDLE
        if (prevViewHolder != null) {
            reelAnalyticsApical(prevViewHolder)
        }
    }

    fun reelAnalyticsApical(prevViewHolder: VideoViewHolder) {
        val params: MutableMap<String, String> = HashMap()
        params[Events.KEYS.REEL_ID] = mVideoList[prevPostion].id
        val endPosition = prevViewHolder?.playerView?.player?.currentPosition
        val elapsedTime: Long? = endPosition?.minus(startPosition)
        params[Events.KEYS.DURATION] = elapsedTime.toString()
        reelDurationEvent(mContext, params, Events.REEL_DURATION, mVideoList[prevPostion].id)
        Log.e(
            "ReelFragment.TAG", "reelAnalyticsApical" + mVideoList[prevPostion].description + params
        )

    }

    fun resumePlayback(curPosition: Int) {
        Log.e("todayr", "resumePlayback: ")
//avoid the same player play again after viewrecycled
//        if(curAdapPosition!=curPosition || curAdapPosition ==0) {
        this.curAdapPosition = curPosition;

        val recyclerView = viewPager2.getChildAt(0) as? RecyclerView
        val curViewHolder =
            recyclerView?.findViewHolderForAdapterPosition(curPosition) as? VideoViewHolder
        curViewHolder?.playerView?.player?.playWhenReady = true
//        }

    }

    fun pauseCurPlayback(curPosition: Int) {
        this.curAdapPosition = curPosition;
        val recyclerView = viewPager2.getChildAt(0) as? RecyclerView
        val curViewHolder =
            recyclerView?.findViewHolderForAdapterPosition(curPosition) as? VideoViewHolder
        curViewHolder?.playerView?.player?.playWhenReady = false
        curViewHolder?.playerView?.player?.seekTo(0)
    }

    fun releasePlayers() {
        viewHolderArray.forEach { holder ->
            Log.e("TAGf", "releasePlayers: $holder")
            holder?.playerView?.player?.playWhenReady = false
            holder?.playerView?.player?.release()
            holder?.playerView?.player = null
        }
    }


    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerView: PlayerView
        val userpic: CircleImageView
        val thumbnail: ImageView
        val btn_comment: ImageView
        val img_share: ImageView
        val imgSelect: ImageView
        val imgLike: ImageView
        val speaker: ImageView
        val sourceName: TextView?
        val followtxt: TextView?
        val like_text: TextView?
        val comment_text: TextView?
        val ll_channel: LinearLayout?
        val ll_right: LinearLayout?
        val linear_bottom: LinearLayout?
        val tvDesc: TextView?
        val cons_header: ConstraintLayout
        val progbarada: ProgressBar

        init {
            playerView = itemView.findViewById(R.id.video_view)
            userpic = itemView.findViewById(R.id.user_pic)
            thumbnail = itemView.findViewById(R.id.thumbnail)
            progbarada = itemView.findViewById(R.id.progbarada)
            ll_channel = itemView.findViewById(R.id.ll_channel)
            linear_bottom = itemView.findViewById(R.id.linear_bottom)
            ll_right = itemView.findViewById(R.id.ll_right)
            imgSelect = itemView.findViewById(R.id.imgSelect)
            speaker = itemView.findViewById(R.id.speakervol)
            img_share = itemView.findViewById(R.id.img_share)
            btn_comment = itemView.findViewById(R.id.btn_comment)
            imgLike = itemView.findViewById(R.id.imgLike)
            like_text = itemView.findViewById(R.id.like_text)
            comment_text = itemView.findViewById(R.id.comment_text)
            sourceName = itemView.findViewById(R.id.sourcenamev)
            followtxt = itemView.findViewById(R.id.user_follow_icon)
            tvDesc = itemView.findViewById(R.id.tv_desc)
            cons_header = itemView.findViewById(R.id.cons_header)

        }

        fun bind(reelsItem: ReelsItem, mcontext: Context, position: Int) {
            audioManager = mcontext.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            linear_bottom?.isClickable = false
            ll_right?.isClickable = false
            cons_header.isClickable = false

            if (reelsItem != null) {
                if (Constants.ReelsVolume) {
                    audioManager?.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    speaker.setImageDrawable(
                        ContextCompat.getDrawable(
                            mcontext, R.drawable.ic_volume
                        )
                    )
                } else {
                    speaker.setImageDrawable(
                        ContextCompat.getDrawable(
                            mcontext, R.drawable.ic_volmute
                        )
                    )
                    audioManager?.setStreamMute(AudioManager.STREAM_MUSIC, true);
                }

                if (reelsItem.source.name != null) {
                    sourceName?.text = reelsItem.source.name.toString()
                }
                if (reelsItem.source.isFavorite) {
                    followtxt?.setText(R.string.following)
                } else {
                    followtxt?.setText(R.string.follow)
                }
                ll_channel?.setOnClickListener {
                    reelFraInterface?.userIconClick(reelsItem)
                }
                itemView.setOnClickListener(DoubleClick(object : DoubleClickListener {
                    override fun onSingleClick(view: View) {
                        reelFraInterface?.openReelviewmore(reelsItem)
                    }

                    override fun onDoubleClick(view: View) {
//                        reelFraInterface?.doubleClickLike(reelsItem)
                    }
                }))
                if (reelsItem?.info != null) {
                    comment_text?.text = "" + reelsItem.info.comment_count
                    like_text?.text = "" + reelsItem.info.like_count
                    comment_text?.visibility =
                        if (reelsItem.info.comment_count > 0) View.VISIBLE else View.GONE
                    like_text?.visibility =
                        if (reelsItem.info.like_count > 0) View.VISIBLE else View.GONE
                    if (reelsItem.info.isLiked) {
                        val drawable: Drawable =
                            ContextCompat.getDrawable(mcontext, R.drawable.ic_heartborderred)!!
                        imgLike.setImageDrawable(null)
                        imgLike.setImageDrawable(drawable)
                        DrawableCompat.setTint(
                            imgLike.drawable,
                            ContextCompat.getColor(mcontext, R.color.theme_color_1)
                        )
                    } else {
                        val drawable: Drawable =
                            ContextCompat.getDrawable(mcontext, R.drawable.ic_heartborderwhite)!!
                        imgLike.setImageDrawable(drawable)
                    }
                }
                imgSelect.setOnClickListener {
                    Log.e("TAG", "bind:imgSelect ")
                    reelFraInterface?.dotsCkickOpen(reelsItem)
                }
                speaker.setOnClickListener {
                    if (Constants.ReelsVolume) {
                        audioManager?.setStreamMute(AudioManager.STREAM_MUSIC, true);
                        speaker.setImageDrawable(
                            ContextCompat.getDrawable(
                                mcontext, R.drawable.ic_volmute
                            )
                        )
                        Constants.ReelsVolume = false
                    } else {
                        audioManager?.setStreamMute(AudioManager.STREAM_MUSIC, false);
                        speaker.setImageDrawable(
                            ContextCompat.getDrawable(
                                mcontext, R.drawable.ic_volume
                            )
                        )
                        Constants.ReelsVolume = true
                    }
                }
                tvDesc?.text = reelsItem.description
                tvDesc?.maxLines = 1
                tvDesc?.ellipsize = TextUtils.TruncateAt.END

                btn_comment.setOnClickListener {
                    reelFraInterface?.commentsclick(reelsItem)
                }
                img_share.setOnClickListener {
                    reelFraInterface?.share(reelsItem)
                }

                imgLike.setOnClickListener(View.OnClickListener { v: View? ->

//                    imgLike.isEnabled = false
                    val flag = reelsItem.info.isLiked
                    if (!flag) {
                        val drawable: Drawable =
                            ContextCompat.getDrawable(mcontext, R.drawable.ic_heartborderred)!!
                        imgLike.setImageDrawable(null)
                        imgLike.setImageDrawable(drawable)
                        DrawableCompat.setTint(
                            imgLike.drawable,
                            ContextCompat.getColor(mcontext, R.color.theme_color_1)
                        )
                        like_text?.text = " ${(reelsItem.info.like_count + 1).toString()}"
                        like_text?.visibility =
                            if (reelsItem.info.like_count + 1 > 0) View.VISIBLE else View.GONE
                    } else {
                        val drawable: Drawable =
                            ContextCompat.getDrawable(mcontext, R.drawable.ic_heartborder)!!
                        imgLike.setImageDrawable(drawable)
                        like_text?.text = " ${(reelsItem.info.like_count - 1).toString()}"
                        like_text?.visibility =
                            if (reelsItem.info.like_count - 1 > 0) View.VISIBLE else View.GONE
                    }
                    presenter?.like(reelsItem.id, object : LikeInterface {
                        override fun success(like: Boolean) {
                            if (mcontext == null) return
//                            imgLike.setEnabled(true)
                            reelsItem.info.isLiked = like
                            var counter = reelsItem.info.like_count
                            if (like) {
                                counter++
                            } else {
                                if (counter > 0) {
                                    counter--
                                } else {
                                    counter = 0
                                }
                            }
                            reelsItem.info.like_count = counter
                            like_text?.text = "" + counter
                        }

                        override fun failure() {
//                            imgLike.setEnabled(true)
                        }
                    }, !reelsItem.info.isLiked)

                    reelFraInterface?.likeicon(reelsItem)
                })
                followtxt?.setOnClickListener {
                    if (reelsItem.source.isFavorite) {
                        followtxt?.setText(R.string.follow)
                    } else {
                        followtxt?.setText(R.string.following)
                    }
                    reelFraInterface?.followChannelClick(reelsItem)
                }
//                tvDesc?.movementMethod = ScrollingMovementMethod()
                tvDesc?.setOnClickListener(View.OnClickListener {
                    if (tvDesc.maxLines == 1) {
                        tvDesc.maxLines = 7
                        tvDesc.ellipsize = null
                        //                    scrollView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    } else {
                        tvDesc.maxLines = 1
                        tvDesc.ellipsize = TextUtils.TruncateAt.END
                    }
                })


                if (reelsItem.sourceImageToDisplay != null && reelsItem.sourceImageToDisplay !== "") {
                    Glide.with(itemView.context).load(reelsItem.sourceImageToDisplay).into(userpic)
                }
            }
            exoPlayer.addListener(object : Player.Listener {

                override fun onTracksChanged(tracks: Tracks) {
                    super.onTracksChanged(tracks)
                    tracks.groups.forEachIndexed { index, group ->
                        if (group.isSelected) {
                            for (i in 0 until group.length) {
//                                Log.d(
//                                    "VideoAdapter_TAG",
//                                    "onTrackChanged: " + group.getTrackFormat(i).width + "x" + group.getTrackFormat(
//                                        i
//                                    ).height
//                                )
                                if (group.isTrackSelected(i) && group.getTrackFormat(i).width != -1) {
                                    Log.d(
                                        "VideoAdapter_TAG",
                                        "onTrackChanged->$adapterPosition->: Selected" + group.getTrackFormat(
                                            i
                                        ).width + "x" + group.getTrackFormat(
                                            i
                                        ).height
                                    )
                                }
                            }
                        }
                    }
                }
//                override fun onTracksChanged(
//                    trackGroups: TrackGroupArray,
//                    trackSelections: TrackSelectionArray
//                ) {
//
//                    try {
//                        val trackSelectionArray = trackSelections
//                        if (trackSelectionArray.length > 0) {
//                            val rendererIndex =
//                                0 // index of the renderer for which you want to get the resolution
//
//                            for (i in 0 until trackSelectionArray.length) {
//                                val track = trackSelectionArray[i]
//                                for (j in 0 until track!!.length()) {
//                                    val trackFormat = track.getFormat(j)
//                                    Log.d(
//                                        "VideoAdapter_TAG",
//                                        "onTrackChanged: " + trackFormat.width + "x" + trackFormat.height
//                                    )
//                                }
//                            }
//                        }
//                    } catch (e: Exception) {
//                        Log.d(
//                            "VideoAdapter_TAG",
//                            "onTrackChanged: ${e.localizedMessage}"
//                        )
//                    }
//                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                        }
                        Player.STATE_BUFFERING -> {
//                            Log.d(
//                                "VideoAdapterBind_TAG",
//                                "onPlayerStateChanged: Buffering $adapterPosition"
//                            )
                            progbarada.visibility = View.VISIBLE
                        }
                        Player.STATE_READY -> {
//                            Log.d(
//                                "VideoAdapterBind_TAG",
//                                "onPlayerStateChanged: PlayerReady $adapterPosition"
//                            )
                            progbarada.visibility = View.GONE
                            thumbnail.visibility = View.INVISIBLE
                            reelFraInterface?.updateView()

                        }
                        Player.STATE_ENDED -> {
                            Log.d(
                                "VideoAdapterBind_TAG",
                                "onPlayerStateChanged: moving to next video $position"
                            )
                            reelFraInterface?.nextReelVideo(position)
                        }
                    }
                }
            })

        }
    }
}





