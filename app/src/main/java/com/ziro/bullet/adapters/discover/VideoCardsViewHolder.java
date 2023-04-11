package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class VideoCardsViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener {

    private static final String TAG = "VideoCardsViewHolder";
    private final TextView headline;
    private final TextView timeTv;
    private final TextView source_name;
    private final TextView authorName;
    private final ImageView dot;
    private final ImageView thumbnail;
    private final RoundedImageView source_image;
    private final ImageView play_image;
    private final CardView card;
    private ImageView ivSpeaker;
    private Context mContext;
    private PlayerView playerView;
    private PrefConfig mPrefConfig;
    private CardView cardMain;
    private LinearLayout llRoot;
    private Article article;

    @Nullable
    private Uri mediaUri;
    private ExoPlayerViewHelper helper;
    private int baseAdapterPosition = -1;
//    private ProgressBar progressBar;

    public VideoCardsViewHolder(@NonNull View itemView, Activity context,int baseAdapterPosition) {
        super(itemView);

        this.baseAdapterPosition = baseAdapterPosition;
        this.mContext = context;
        mPrefConfig = new PrefConfig(context);
        source_image = itemView.findViewById(R.id.source_image);
        authorName = itemView.findViewById(R.id.authorName);
        dot = itemView.findViewById(R.id.dot);
        source_name = itemView.findViewById(R.id.source_name);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        play_image = itemView.findViewById(R.id.play_image);
        headline = itemView.findViewById(R.id.headline);
        timeTv = itemView.findViewById(R.id.time);
        card = itemView.findViewById(R.id.card);
        playerView = itemView.findViewById(R.id.video_player);
        cardMain = itemView.findViewById(R.id.cardMain);
        llRoot = itemView.findViewById(R.id.llRoot);
        ivSpeaker = itemView.findViewById(R.id.speaker);
//        progressBar = itemView.findViewById(R.id.progressBar);
    }

    public void bind(int position, Article video) {

        if (video != null) {
            this.article = video;
            if (!TextUtils.isEmpty(video.getLink()))
                mediaUri = Uri.parse(video.getLink());

            if (video.getSourceImageToDisplay() != null && !video.getSourceImageToDisplay().equals("")) {
                Picasso.get()
                        .load(video.getSourceImageToDisplay())
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .transform(new CropCircleTransformation())
                        .into(source_image);
            } else {
                Picasso.get()
                        .load(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .transform(new CropCircleTransformation())
                        .into(source_image);
            }
            if (!TextUtils.isEmpty(video.getSourceNameToDisplay())) {
                source_name.setText(video.getSourceNameToDisplay());

                if (!TextUtils.isEmpty(video.getAuthorNameToDisplay())) {
                    dot.setVisibility(View.VISIBLE);
                    authorName.setVisibility(View.VISIBLE);
                    authorName.setText(video.getAuthorNameToDisplay());
                } else {
                    dot.setVisibility(View.GONE);
                    authorName.setVisibility(View.GONE);
                }

            } else {
                dot.setVisibility(View.GONE);
                authorName.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(video.getAuthorNameToDisplay())) {
                    source_name.setText(video.getAuthorNameToDisplay());
                }
            }

            if (!TextUtils.isEmpty(video.getPublishTime())) {
                String time = Utils.getTimeAgo(Utils.getDate(video.getPublishTime()), mContext);
                if (!TextUtils.isEmpty(time)) {
                    timeTv.setText(time);
                }
            }

//            progressBar.setVisibility(View.GONE);

            headline.setText(video.getTitle());

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

            updateMuteButtons();

            playerView.setOnClickListener(v -> {
                if(playerView.isControllerVisible()) {
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID,article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.DISCOVER_VIDEOS_OPEN);

                    Intent intent = new Intent(mContext, BulletDetailActivity.class);
                    intent.putExtra("articleId", video.getId());
                    mContext.startActivity(intent);
                }
            });

            llRoot.setOnClickListener(v -> {
                Map<String,String> params = new HashMap<>();
                params.put(Events.KEYS.REEL_ID,article.getId());
                AnalyticsEvents.INSTANCE.logEvent(mContext,
                        params,
                        Events.DISCOVER_VIDEOS_OPEN);
                Intent intent = new Intent(mContext, BulletDetailActivity.class);
                intent.putExtra("articleId", video.getId());
                mContext.startActivity(intent);
            });

            thumbnail.setEnabled(false);
            thumbnail.setOnClickListener(v -> {

                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.TYPE_VIDEO_PLAYING);
                messageEvent.setIntData(baseAdapterPosition);

                EventBus.getDefault().post(messageEvent);

                playVideo();
            });

            ivSpeaker.setOnClickListener(v -> {
                try {
                    if (!Constants.muted) {
                        Constants.muted = true;
//                        Constants.ReelMute = true;
                        helper.setVolume(0f);
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.ARTICLE_ID,article.getId());
                        AnalyticsEvents.INSTANCE.logEvent(mContext,
                                params,
                                Events.MUTE_VIDEO_DISCOVER);
                    } else {
                        Constants.muted = false;
//                        Constants.ReelMute = false;
                        helper.setVolume(1f);
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.ARTICLE_ID,article.getId());
                        AnalyticsEvents.INSTANCE.logEvent(mContext,
                                params,
                                Events.UNMUTE);
                    }
                    updateMuteButtons();
                } catch (Exception ignore) {
                }
            });

            playerView.setControllerVisibilityListener(visibility -> showFlagAndAudioBtn(visibility == View.VISIBLE));

        }
    }

    protected void showFlagAndAudioBtn(boolean show) {
        ivSpeaker.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void updateMuteButtons() {
        if (!Constants.muted) {
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
        Log.d(TAG, "initialize() called with: mediaUri = [" + mediaUri + "]");
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
        Log.d(TAG, "play: ");
        if (helper != null) {
            helper.play();
            if (mPrefConfig.isVideoAutoPlay()) {
                thumbnail.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.VISIBLE);
                play_image.setVisibility(View.GONE);
            } else {
                thumbnail.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
                play_image.setVisibility(View.VISIBLE);
            }

            if (Constants.muted) {
                if (helper != null)
                    helper.setVolume(0);
            } else {
                if (helper != null)
                    helper.setVolume(1);
            }
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            playerView.getPlayer().setPlayWhenReady(mPrefConfig.isVideoAutoPlay());
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
            params.put(Events.KEYS.REEL_ID,article.getId());
            AnalyticsEvents.INSTANCE.logEvent(mContext,
                    params,
                    Events.DISCOVER_VIDEOS_WATCH);
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
        Log.d(TAG, "onFirstFrameRendered: ");
    }

    @Override
    public void onBuffering() {
        Log.d(TAG, "onBuffering: ");
    }

    @Override
    public void onPlaying() {
        thumbnail.setVisibility(View.GONE);
        play_image.setVisibility(View.GONE);
//        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPaused() {
        Log.d(TAG, "onPaused: ");
        thumbnail.setVisibility(View.VISIBLE);
        play_image.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted: ");
        thumbnail.setVisibility(View.VISIBLE);
        if (playerView != null && playerView.getPlayer() != null) {
            playerView.getPlayer().seekTo(0);
            playerView.getPlayer().setPlayWhenReady(false);
        }
        if (article != null && !TextUtils.isEmpty(article.getId())) {
            Map<String,String> params = new HashMap<>();
            params.put(Events.KEYS.ARTICLE_ID,article.getId());
            AnalyticsEvents.INSTANCE.logEventWithAPI(mContext,
                    params,
                    Events.ARTICLE_VIDEO_COMPLETE);
        }
    }

    private void playVideo() {
//        progressBar.setVisibility(View.VISIBLE);
        play_image.setVisibility(View.GONE);

        if (helper != null) {
            helper.play();
            if (Constants.muted) {
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
