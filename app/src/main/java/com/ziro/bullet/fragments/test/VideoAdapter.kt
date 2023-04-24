package com.ziro.bullet.fragments.test

import android.app.Activity
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.SystemClock
import com.google.android.exoplayer2.util.Util
import com.ziro.bullet.R
import com.ziro.bullet.analytics.AnalyticsEvents.logEventWithAPI
import com.ziro.bullet.analytics.AnalyticsEvents.reelDurationEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.fragments.VideoInnerFragment
import com.ziro.bullet.interfaces.LikeInterface
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.DoubleClickHandler.DoubleClick
import com.ziro.bullet.utills.DoubleClickHandler.DoubleClickListener
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Locale

class VideoAdapter(
    private val viewPager2: ViewPager2,
    private val mContext: Context,
    private var reelFraInterface: ReelFraInterface?,
    private var presenter: ReelsNewPresenter?,

    ) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private var mVideoList = ArrayList<ReelsItem>()
    private var currentPlaybackPosition = 0
    private var curAdapPosition = 0
    private var isSeekBarBeingTouched = false
    var handlerseek = Handler()
    private val handlernew = Handler()
    private var startPosition: Long = 0
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
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_video_test, parent, false)
        return VideoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val reelsItem = mVideoList[position]
        curreelItem = reelsItem


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
                defaultBandwidthMeter =
                    DefaultBandwidthMeter.Builder(mContext).setInitialBitrateEstimate(1400000L)
                        .build()

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
            if (viewholder.sourceName?.equals(reelsItem.source.name) == true) {
                if (reelsItem.source.isFavorite) {
                    viewholder.followtxt?.setText(R.string.following)
                } else {
                    viewholder.followtxt?.setText(R.string.follow)
                }
            }
            if (Constants.ReelsVolume) {
                viewholder.speaker.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_volume
                    )
                )
            } else {
                viewholder.speaker.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.ic_volmute
                    )
                )
            }
        }
        if (reelsItem != null) {
            holder.bind(reelsItem, mContext, position, exoPlayer)
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
        Constants.mViewRecycled = true;
        if (holder in viewHolderArray) {
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
        currentPlaybackState =
            prevViewHolder?.playerView?.player?.playbackState ?: Player.STATE_IDLE
        if (prevViewHolder != null) {
            reelAnalyticsApical(prevViewHolder)
        }
    }

    private fun reelAnalyticsApical(prevViewHolder: VideoViewHolder) {
        //Analytics REEL DURATION
        val params: MutableMap<String, String> = HashMap()
        params[Events.KEYS.REEL_ID] = mVideoList[prevPostion].id
        val endPosition = prevViewHolder.playerView.player?.currentPosition
        val elapsedTime: Long? = endPosition?.minus(startPosition)
        params[Events.KEYS.DURATION] = elapsedTime.toString()
        reelDurationEvent(mContext, params, Events.REEL_DURATION, mVideoList[prevPostion].id)

        //Analytics REEL VIEW
        val params1: MutableMap<String, String> = HashMap()
        params1[Events.KEYS.ARTICLE_ID] = mVideoList[prevPostion].id
        params1[Events.KEYS.DURATION] = elapsedTime.toString()
        logEventWithAPI(mContext, params, Events.REEL_VIEW)

    }

    fun resumePlayback(curPosition: Int) {
//avoid the same player play again after viewrecycled
//        if(curAdapPosition!=curPosition || curAdapPosition ==0) {
        this.curAdapPosition = curPosition;

        val recyclerView = viewPager2.getChildAt(0) as? RecyclerView
        val curViewHolder =
            recyclerView?.findViewHolderForAdapterPosition(curPosition) as? VideoViewHolder
        curViewHolder?.playerView?.player?.playWhenReady = true
        curViewHolder?.playerView?.player?.seekTo(0)
//        }

    }

    fun pauseCurPlayback(curPosition: Int) {
        this.curAdapPosition = curPosition;
        val recyclerView = viewPager2.getChildAt(0) as? RecyclerView
        val curViewHolder =
            recyclerView?.findViewHolderForAdapterPosition(curPosition) as? VideoViewHolder
        curViewHolder?.playerView?.player?.playWhenReady = false
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
        lateinit var exoPlayerVh: ExoPlayer
        var container_parent: ConstraintLayout = itemView.findViewById(R.id.container_parent)
        val btn_comment: ImageView
        val img_share: ImageView
        val imgSelect: ImageView
        val imgLike: ImageView
        val img_pause: ImageView
        val speaker: ImageView
        val sourceName: TextView?
        val followtxt: TextView?
        val like_text: TextView?
        val comment_text: TextView?
        val textViewCurrentTime: TextView?
        val textViewTotalTime: TextView?
        val ll_channel: LinearLayout?
        val ll_right: LinearLayout?
        val linear_bottom: LinearLayout?
        val tvDesc: TextView?
        val rl_seek: RelativeLayout? = itemView.findViewById(R.id.rl_seek)
        val cons_header: ConstraintLayout
        val progbarada: ProgressBar
        val heart: ImageView
        var seekBar: SeekBar = itemView.findViewById(R.id.seek_bar)
        var drawable: Drawable? = null
        var runnablenew = Runnable {
            if (!isSeekBarBeingTouched) {
                container_parent.visibility = View.VISIBLE
                rl_seek?.visibility = View.GONE
                seekBar.progressDrawable =
                    ContextCompat.getDrawable(mContext, R.drawable.custom_seekbar_progress)
                seekBar.thumb =
                    ContextCompat.getDrawable(mContext, R.drawable.custom_seekbar_thumb_normal)

            }
        }
        private val runnableseek: Runnable = object : Runnable {
            override fun run() {
                val positionseek: Int = exoPlayerVh.currentPosition.toInt() / 1000
                seekBar.progress = positionseek
                if (isSeekBarBeingTouched) {
                    container_parent.visibility = View.GONE
                    rl_seek?.visibility = View.VISIBLE
                    seekBar.progressDrawable =
                        ContextCompat.getDrawable(mContext, R.drawable.custom_seekbar_progress_big)
                    seekBar.thumb =
                        ContextCompat.getDrawable(mContext, R.drawable.custom_seekbar_thumb_pressed)
                } else {
                    handlernew.postDelayed(runnablenew, 5000) // Change back to original after 5 sec
                }
                handlerseek.postDelayed(this, 100)
            }
        }
        private var avd: AnimatedVectorDrawableCompat? = null
        private var avd2: AnimatedVectorDrawable? = null

        init {
            playerView = itemView.findViewById(R.id.video_view)
            img_pause = itemView.findViewById(R.id.img_pause)
            textViewCurrentTime = itemView.findViewById(R.id.textViewCurrentTime)
            textViewTotalTime = itemView.findViewById(R.id.textViewTotalTime)
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
            heart = itemView.findViewById(R.id.imgHeart)
        }

        fun bind(
            reelsItem: ReelsItem,
            mContext: Context,
            position: Int,
            exoPlayerVh: ExoPlayer
        ) {
            this@VideoViewHolder.exoPlayerVh = exoPlayerVh
            audioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            linear_bottom?.isClickable = false
            ll_right?.isClickable = false
            cons_header.isClickable = false

            if (reelsItem != null) {
                if (!TextUtils.isEmpty(reelsItem.image)) {
                    thumbnail.visibility = View.VISIBLE
                    Glide.with(mContext).load(reelsItem.image).into(thumbnail);
                } else {
                    thumbnail.visibility = View.INVISIBLE
                }
                if (Constants.ReelsVolume) {
                    audioManager?.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    speaker.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_volume
                        )
                    )
                } else {
                    speaker.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.ic_volmute
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
                        reelFraInterface?.doubleClickLike(reelsItem)
                        if (reelsItem != null) {
                            drawable = heart.drawable
                            if (mContext != null) {
                                val drawable: Drawable =
                                    ContextCompat.getDrawable(
                                        mContext,
                                        R.drawable.ic_heartborderred
                                    )!!
                                imgLike.setImageDrawable(null)
                                imgLike.setImageDrawable(drawable)
                            }
                            heart.visibility = View.VISIBLE
                            heart.alpha = 1f
                            if (drawable is AnimatedVectorDrawableCompat) {
                                avd = drawable as AnimatedVectorDrawableCompat
                                avd?.start()
                            } else if (drawable is AnimatedVectorDrawable) {
                                avd2 = drawable as AnimatedVectorDrawable
                                avd2?.start()
                            }
                            val counter = intArrayOf(-1)
                            if (!reelsItem.info.isLiked) {
                                counter[0] = reelsItem.info.like_count
                                counter[0]++
                                like_text?.text = "" + counter[0]
                                val drawable: Drawable? =
                                    ContextCompat.getDrawable(
                                        mContext,
                                        R.drawable.ic_heartborderred
                                    )
                                imgLike.setImageDrawable(null)
                                imgLike.setImageDrawable(drawable)
                                reelsItem.info.isLiked = true
                                like_text?.visibility =
                                    if (reelsItem.info.like_count + 1 > 0) View.VISIBLE else View.VISIBLE

                                presenter?.like(reelsItem.id, object : LikeInterface {
                                    override fun success(like: Boolean) {
                                        if (mContext == null || (mContext as? Activity)?.isFinishing == true) {
                                            return
                                        }
                                        imgLike.isEnabled = true
                                        reelsItem.info.isLiked = like
                                        reelsItem.info.like_count = counter[0]
                                        like_text?.visibility =
                                            if (reelsItem.info.like_count > 0) View.VISIBLE else View.VISIBLE

                                        if (reelsItem.info.isLiked) {
                                            val drawable: Drawable =
                                                ContextCompat.getDrawable(
                                                    mContext,
                                                    R.drawable.ic_heartborderred
                                                )!!
                                            imgLike.setImageDrawable(null)
                                            imgLike.setImageDrawable(drawable)
                                            DrawableCompat.setTint(
                                                imgLike.drawable,
                                                ContextCompat.getColor(
                                                    mContext,
                                                    R.color.theme_color_1
                                                )
                                            )
                                        } else {
                                            val drawable: Drawable? =
                                                ContextCompat.getDrawable(
                                                    mContext,
                                                    R.drawable.ic_heartborderwhite
                                                )
                                            imgLike.setImageDrawable(drawable)
                                        }
                                    }

                                    override fun failure() {
                                        if (mContext == null || (mContext as? Activity)?.isFinishing == true) {
                                            return
                                        }
                                        imgLike.isEnabled = true
                                        if (counter[0] > 0) {
                                            counter[0]--
                                        } else {
                                            counter[0] = 0
                                        }
//                                        DrawableCompat.setTint(
//                                            imgLike.drawable,
//                                            ContextCompat.getColor(mContext, R.color.white)
//                                        )
                                        //                                like_text.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                                like_icon.setImageResource(R.drawable.ic_reel_like_inactive);
                                        reelsItem.info.isLiked = false
                                    }
                                }, true)
                            }

                        }
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
                            ContextCompat.getDrawable(mContext, R.drawable.ic_heartborderred)!!
                        imgLike.setImageDrawable(null)
                        imgLike.setImageDrawable(drawable)
                        DrawableCompat.setTint(
                            imgLike.drawable,
                            ContextCompat.getColor(mContext, R.color.theme_color_1)
                        )
                    } else {
                        val drawable: Drawable =
                            ContextCompat.getDrawable(mContext, R.drawable.ic_heartborderwhite)!!
                        imgLike.setImageDrawable(drawable)
                    }
                }

                img_pause.setOnClickListener {
                    if (exoPlayerVh.playWhenReady) {
                        isSeekBarBeingTouched = true
                        exoPlayerVh.playWhenReady = false
                        img_pause.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.playbtn
                            )
                        )
                    } else {
                        isSeekBarBeingTouched = false
                        exoPlayerVh.playWhenReady = true
                        img_pause.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.pausebtn
                            )
                        )
                    }
                }
                imgSelect.setOnClickListener {
                    reelFraInterface?.dotsCkickOpen(reelsItem)
                }
                speaker.setOnClickListener {
                    if (Constants.ReelsVolume) {
                        audioManager?.setStreamMute(AudioManager.STREAM_MUSIC, true);
                        speaker.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.ic_volmute
                            )
                        )
                        Constants.ReelsVolume = false
                    } else {
                        audioManager?.setStreamMute(AudioManager.STREAM_MUSIC, false);
                        speaker.setImageDrawable(
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.ic_volume
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
                            ContextCompat.getDrawable(mContext, R.drawable.ic_heartborderred)!!
                        imgLike.setImageDrawable(null)
                        imgLike.setImageDrawable(drawable)
                        DrawableCompat.setTint(
                            imgLike.drawable,
                            ContextCompat.getColor(mContext, R.color.theme_color_1)
                        )
                        like_text?.text = " ${(reelsItem.info.like_count + 1).toString()}"
                        like_text?.visibility =
                            if (reelsItem.info.like_count + 1 > 0) View.VISIBLE else View.GONE
                    } else {
                        val drawable: Drawable =
                            ContextCompat.getDrawable(mContext, R.drawable.ic_heartborder)!!
                        imgLike.setImageDrawable(drawable)
                        like_text?.text = " ${(reelsItem.info.like_count - 1).toString()}"
                        like_text?.visibility =
                            if (reelsItem.info.like_count - 1 > 0) View.VISIBLE else View.GONE
                    }
                    presenter?.like(reelsItem.id, object : LikeInterface {
                        override fun success(like: Boolean) {
                            if (mContext == null) return
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
                    viewHolderArray.forEach { viewholder ->
                        if (viewholder.sourceName?.text?.equals(reelsItem.source.name) == true) {
                            if (reelsItem.source.isFavorite) {
                                viewholder.followtxt?.setText(R.string.following)
                            } else {
                                viewholder.followtxt?.setText(R.string.follow)
                            }
                        }
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
                    Glide.with(itemView.context)
                        .load(reelsItem.sourceImageToDisplay)
                        .into(userpic)
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
                            progbarada.visibility = View.VISIBLE
                        }

                        Player.STATE_READY -> {
                            progbarada.visibility = View.GONE
                            thumbnail.visibility = View.INVISIBLE
                            reelFraInterface?.updateView()

                            handlerseek.removeCallbacks(runnableseek)
                            seekBar.max = exoPlayerVh.duration.toInt() / 1000
                            val positionseek: Int = exoPlayerVh.currentPosition.toInt() / 1000
                            seekBar.progress = positionseek
                            handlerseek.post(runnableseek)
                            isSeekBarBeingTouched = false

                        }

                        Player.STATE_ENDED -> {
                            reelFraInterface?.nextReelVideo(position)
                        }
                    }
                }
            })
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (exoPlayerVh != null && fromUser) {
                        exoPlayerVh.seekTo(progress * 1000L)
                    }
                    if (exoPlayerVh != null) {
                        val currentTime: Long = exoPlayerVh.currentPosition
                        val totalTime: Long = exoPlayerVh.duration
                        textViewCurrentTime?.text = formatTime(currentTime)
                        textViewTotalTime?.text = formatTime(totalTime)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {
//                container_parent.setVisibility(View.VISIBLE);
//                rl_seek.setVisibility(View.GONE);
                }
            })

            seekBar.setOnTouchListener { v, event ->

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isSeekBarBeingTouched = true
                        container_parent.visibility = View.GONE
                        rl_seek!!.visibility = View.VISIBLE
                        seekBar.progressDrawable =
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.custom_seekbar_progress_big
                            )
                        seekBar.thumb =
                            ContextCompat.getDrawable(
                                mContext,
                                R.drawable.custom_seekbar_thumb_pressed
                            )
                        handlernew.removeCallbacks(runnablenew)
                    }

                    MotionEvent.ACTION_UP -> {
                        isSeekBarBeingTouched = false
                        handlernew.postDelayed(
                            runnablenew,
                            5000
                        ) // Change back to original after 5 sec
//                        if (!isSeekBarBeingTouched && event.eventTime - event.downTime < ViewConfiguration.getTapTimeout()) {
//                            v.performClick()
//                        }
                    }
                }
                false
            }
        }
    }

}

private fun formatTime(millis: Long): String? {
    val seconds = (millis / 1000).toInt() % 60
    val minutes = (millis / (1000 * 60) % 60).toInt()
    val hours = (millis / (1000 * 60 * 60) % 24).toInt()
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

class SlowPageTransformer(private val viewPager: ViewPager2) : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
//        val absPos = abs(position)
//        val swipeSpeed = 0.1f // Adjust this value to control the scroll speed
//        val height = page.height
//        page.translationY = absPos * swipeSpeed * height
    }
}





