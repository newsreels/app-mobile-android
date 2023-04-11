package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ReelActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

public class ReelCardViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener {

    private static final String TAG = "discoverreel";
    private final TextView text1;
    private final TextView text2;
    private final ImageView thumbnail;
    private final ImageView source_icon;
    private final TextView source_name;
    private final ImageView play_image;
    private final CardView card;
    public PlayerView playerView;
    private Context mContext;
    private boolean isMute = false;
    private PrefConfig mPrefConfig;
    private CardView cardMain;
    private ImageView ivSpeaker;
    @Nullable
    private Uri mediaUri;
    private ExoPlayerViewHelper helper;
    private ReelsItem video;
//    private ProgressBar progressBar;

    public ReelCardViewHolder(@NonNull View itemView, Activity context) {
        super(itemView);

        this.mContext = context;
        mPrefConfig = new PrefConfig(context);
        source_icon = itemView.findViewById(R.id.source_icon);
        source_name = itemView.findViewById(R.id.source_name);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        play_image = itemView.findViewById(R.id.play_image);
        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
        card = itemView.findViewById(R.id.card);
        playerView = itemView.findViewById(R.id.video_player);
        cardMain = itemView.findViewById(R.id.cardMain);
        ivSpeaker = itemView.findViewById(R.id.speaker);
//        progressBar = itemView.findViewById(R.id.progressBar);
    }

    public void bind(int position, ArrayList<ReelsItem> videoList) {
        video = videoList.get(position);
        if (video != null) {
            if (!TextUtils.isEmpty(video.getMedia()))
                mediaUri = Uri.parse(video.getMedia());

            if (!TextUtils.isEmpty(video.getImage())) {
                Picasso.get()
                        .load(video.getImage())
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(thumbnail);
            } else {
                    thumbnail.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            if (!TextUtils.isEmpty(video.getSourceImageToDisplay())) {
                Picasso.get()
                        .load(video.getSourceImageToDisplay())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(source_icon);
            }

            if (!TextUtils.isEmpty(video.getSourceNameToDisplay())) {
                source_name.setText(video.getSourceNameToDisplay());
            }

            playerView.setOnClickListener(v -> {
                if(playerView.isControllerVisible()) {
                    Intent intent = new Intent(mContext, ReelInnerActivity.class);
                    intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, video.getContext());
                    intent.putExtra(ReelInnerActivity.REEL_F_MODE, "");
                    intent.putExtra(ReelInnerActivity.REEL_POSITION, position);
                    intent.putParcelableArrayListExtra(ReelActivity.REEL_DATALIST, videoList);
                    mContext.startActivity(intent);
                }
            });

            thumbnail.setEnabled(false);
//            progressBar.setVisibility(View.GONE);

            thumbnail.setOnClickListener(v -> {
                playVideo();
            });

            ivSpeaker.setOnClickListener(v -> {
                try {
                    if (!Constants.ReelMute) {
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.REEL_ID,video.getId());
                        params.put(Events.KEYS.STATUS,"0");
                        AnalyticsEvents.INSTANCE.logEvent(mContext,
                                params,
                                Events.MUTE_REELS_DISCOVER);
                        Constants.ReelMute = true;
                        helper.setVolume(0f);
                    } else {
                        Constants.ReelMute = false;
                        helper.setVolume(1f);
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.REEL_ID,video.getId());
                        params.put(Events.KEYS.STATUS,"1");
                        AnalyticsEvents.INSTANCE.logEvent(mContext,
                                params,
                                Events.MUTE_REELS_DISCOVER);
                    }
                    updateMuteButtons();
                } catch (Exception ignore) {
                }
            });

            updateMuteButtons();

            playerView.setControllerVisibilityListener(visibility -> showFlagAndAudioBtn(visibility == View.VISIBLE));
        }
    }

    protected void showFlagAndAudioBtn(boolean show) {
        ivSpeaker.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void updateMuteButtons() {
        if (!Constants.ReelMute) {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_unmute_black);
        } else {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_mute_black);
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
            }
            helper.initialize(container, playbackInfo);
        }
    }

    @Override
    public void play() {
        if (helper != null) {
            helper.play();
            if (mPrefConfig.isReelsAutoPlay()) {
                thumbnail.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.VISIBLE);
                play_image.setVisibility(View.GONE);
            } else {
                thumbnail.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
                play_image.setVisibility(View.VISIBLE);
            }

            if (Constants.ReelMute) {
                if (helper != null)
                    helper.setVolume(0);
            } else {
                if (helper != null)
                    helper.setVolume(1);
            }

            if (video.getMediaMeta() != null) {
                if (video.getMediaMeta().getWidth() > video.getMediaMeta().getHeight()) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                } else {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                }
            } else {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            }

            playerView.getPlayer().setPlayWhenReady(mPrefConfig.isReelsAutoPlay());
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
        boolean b = ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
        if (b){
            Map<String,String> params = new HashMap<>();
            params.put(Events.KEYS.REEL_ID,video.getId());
            AnalyticsEvents.INSTANCE.logEvent(mContext,
                    params,
                    Events.DISCOVER_REEL_WATCH);
        }
        return b;
    }

    @Override
    public int getPlayerOrder() {
        Log.d(TAG, "getPlayerOrder: ");
        return getAbsoluteAdapterPosition();
    }

    @Override
    public void onFirstFrameRendered() {
    }

    @Override
    public void onBuffering() {
    }

    @Override
    public void onPlaying() {
        thumbnail.setVisibility(View.GONE);
        play_image.setVisibility(View.GONE);
//        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPaused() {
        thumbnail.setVisibility(View.VISIBLE);
        play_image.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompleted() {
        thumbnail.setVisibility(View.VISIBLE);
        if (playerView != null && playerView.getPlayer() != null) {
            playerView.getPlayer().seekTo(0);
            playerView.getPlayer().setPlayWhenReady(false);
        }
        if (video != null && !TextUtils.isEmpty(video.getId())) {
            Map<String,String> params = new HashMap<>();
            params.put(Events.KEYS.ARTICLE_ID,video.getId());
            AnalyticsEvents.INSTANCE.logEventWithAPI(mContext,
                    params,
                    Events.REEL_COMPLETE);
        }
    }

    private void playVideo() {
//        progressBar.setVisibility(View.VISIBLE);
        play_image.setVisibility(View.GONE);
        if (helper != null) {
            helper.play();
            if (Constants.ReelMute) {
                if (helper != null)
                    helper.setVolume(0);
            } else {
                if (helper != null)
                    helper.setVolume(1);
            }
        }
    }


    public void disablePlayClick() {
        if (thumbnail != null)
            thumbnail.setEnabled(false);
    }

    public void enablePlayClick() {
        if (thumbnail != null)
            thumbnail.setEnabled(true);
    }
}
