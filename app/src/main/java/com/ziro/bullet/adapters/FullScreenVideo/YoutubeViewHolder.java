package com.ziro.bullet.adapters.FullScreenVideo;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.interfaces.DurationCallback;
import com.ziro.bullet.model.FullScreenVideo;
import com.ziro.bullet.utills.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class YoutubeViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "youtubePlayer";
    private Activity context;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;
    private RelativeLayout buttonPanel;
    private ImageView ivSpeaker;
    private ImageView smallScreen;
    private DurationCallback callback;
    public long mDuration = 0;

    public YoutubeViewHolder(@NonNull View itemView, Activity context, DurationCallback callback) {
        super(itemView);
        this.callback = callback;
        this.context = context;
        youTubePlayerView = itemView.findViewById(R.id.youtube);
        buttonPanel = itemView.findViewById(R.id.buttonPanel);
        ivSpeaker = itemView.findViewById(R.id.speaker);
        smallScreen = itemView.findViewById(R.id.smallScreen);
    }

    public void release() {
        if (youTubePlayerView != null)
            youTubePlayerView.release();
        youTubePlayer = null;
        youTubePlayerView = null;
    }

    public long getDuration() {
        return mDuration;
    }

    public void bind(int position, FullScreenVideo video) {
        Log.d(TAG, "bind() called with: position = [" + position + "], article = [" + video.url + "]");
        if (video != null) {
            updateMuteButtons();
            ivSpeaker.setOnClickListener(v -> {
                try {
                    if (!Constants.muted) {
                        Constants.muted = true;
                        youTubePlayer.mute();
                        Map<String,String> params = new HashMap<>();
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.MUTE_VIDEO_FEED);
                    } else {
                        Constants.muted = false;
                        youTubePlayer.unMute();
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

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {

                @Override
                public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float duration) {
                    super.onVideoDuration(youTubePlayer, duration);
                    mDuration = (long) duration;
                }

                @Override
                public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float second) {
                    super.onCurrentSecond(youTubePlayer, second);
                    mDuration = (long) second;
                }

                @Override
                public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {
                    if (playerState == PlayerConstants.PlayerState.PLAYING) {
                        if (Constants.muted) {
                            youTubePlayer.mute();
                        } else {
                            youTubePlayer.unMute();
                        }
                        buttonPanel.setVisibility(View.GONE);
                    } else {
                        buttonPanel.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "onStateChange: " + playerState);
                }

                @Override
                public void onReady(@NotNull YouTubePlayer initializedYouTubePlayer) {
                    super.onReady(initializedYouTubePlayer);
                    youTubePlayer = initializedYouTubePlayer;
                    youTubePlayer.loadVideo(video.getUrl(), video.duration);
                    if (Constants.muted) {
                        youTubePlayer.mute();
                    } else {
                        youTubePlayer.unMute();
                    }
                    youTubePlayer.play();
                }
            });
        }
    }


    public void updateMuteButtons() {
        if (!Constants.muted) {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_unmute);
        } else {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_mute);
        }
    }
}
