package com.ziro.bullet.adapters.FullScreenVideo;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.PlayerView;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.interfaces.DurationCallback;
import com.ziro.bullet.model.FullScreenVideo;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.HashMap;
import java.util.Map;

import im.ene.toro.ToroPlayer;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

public class VideoViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener, ToroPlayer.OnErrorListener {
    private static final String TAG = "VideoViewHolder";
    private final RelativeLayout buttonPanel;
    public PlayerView playerView;
    private ExoPlayerViewHelper helper;
    @Nullable
    private Uri mediaUri;
    private ImageView ivSpeaker;
    private ImageView smallScreen;
    private Activity context;
    private FullScreenVideo article;
    private DurationCallback callback;

    public VideoViewHolder(@NonNull View itemView, Activity context, DurationCallback callback) {
        super(itemView);
        this.callback = callback;
        this.context = context;
        playerView = itemView.findViewById(R.id.video_player);
        buttonPanel = itemView.findViewById(R.id.buttonPanel);
        ivSpeaker = itemView.findViewById(R.id.speaker);
        smallScreen = itemView.findViewById(R.id.smallScreen);
    }

    public void bind(int position, FullScreenVideo article) {
        this.article = article;
        if (article != null) {
            mediaUri = Uri.parse(article.getUrl());
            updateMuteButtons();
            ivSpeaker.setOnClickListener(v -> {
                try {
                    if (!Constants.muted) {
                        Constants.muted = true;
                        helper.setVolume(0f);
                        Map<String,String> params = new HashMap<>();
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.MUTE_VIDEO_FEED);
                    } else {
                        Constants.muted = false;
                        helper.setVolume(1f);
                        Map<String,String> params = new HashMap<>();
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.UNMUTE);
                    }
                    updateMuteButtons();
                } catch (Exception ignore) {
                }
            });
            smallScreen.setOnClickListener(v -> {
                if (callback != null)
                    callback.getVideoDuration(getDuration());
            });
        }
        playerView.setControllerVisibilityListener(visibility -> showFlagAndAudioBtn(visibility == View.VISIBLE));
    }

    public long getDuration() {
        if (playerView != null && playerView.getPlayer() != null)
            return playerView.getPlayer().getContentPosition();
        else
            return 0L;
    }

    public void updateMuteButtons() {
        if (!Constants.muted) {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_unmute);
        } else {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_mute);
        }
    }

    @NonNull
    @Override
    public View getPlayerView() {
        return this.playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
        if (mediaUri != null) {
            if (helper == null) {
                helper = new ExoPlayerViewHelper(this, mediaUri);
                helper.addPlayerEventListener(this);
                helper.addErrorListener(this);
            }
            helper.initialize(container, playbackInfo);
        }
    }

    @Override
    public void play() {
        if (helper != null) {
            helper.play();
            if (Constants.muted) {
                helper.setVolume(0f);
            } else {
                helper.setVolume(1f);
            }
            if (article != null && playerView != null && playerView.getPlayer() != null) {
                playerView.getPlayer().seekTo(article.duration);
                //in the lib videos will autoplay by default when loaded, to block this following line is added
                playerView.getPlayer().setPlayWhenReady(true);
            }
        }
    }

    private void playVideo() {
        if (helper != null) {
            helper.play();
            if (Constants.muted) {
                helper.setVolume(0f);
            } else {
                helper.setVolume(1f);
            }
        }
    }

    @Override
    public void pause() {
        if (helper != null) helper.pause();
    }

    @Override
    public boolean isPlaying() {
        return helper != null && helper.isPlaying();
    }

    @Override
    public void release() {
        if (helper != null) {
            helper.removePlayerEventListener(this);
            helper.release();
            helper = null;
        }
    }

    @Override
    public boolean wantsToPlay() {
        return true;
    }

    @Override
    public int getPlayerOrder() {
        return getAbsoluteAdapterPosition();
    }

    @Override
    public void onFirstFrameRendered() {
        Log.d(TAG, "onFirstFrameRendered: ");
    }

    @Override
    public void onBuffering() {
        Log.d(TAG, "onBuffering: ");
    }

    @Override
    public void onPlaying() {
        Log.d(TAG, "onPlaying: ");

    }

    @Override
    public void onPaused() {
        Log.d(TAG, "onPaused: ");
    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted: ");

    }

    @Override
    public void onError(Exception error) {
        Log.d(TAG, "onError: " + error);
        error.printStackTrace();
    }

    protected void showFlagAndAudioBtn(boolean show) {
        buttonPanel.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
