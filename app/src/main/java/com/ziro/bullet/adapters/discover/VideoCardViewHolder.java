package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.HashMap;
import java.util.Map;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class VideoCardViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener {

    //    private final TextView text1;
    private final TextView text2;
    private final ImageView speaker;
    private final ImageView imageBack;
    private final ImageView image;
    private final ImageView play_image;
    //    private final TextView playDuration;
    private final RelativeLayout thumbnail;
    private final RelativeLayout data;
    private final RoundedImageView source_image;
    private final TextView source_name;
    private final TextView time;
    private final TextView headline;
    private final LinearLayout card;
    public CardView videoCard;
    public PlayerView playerView;
    public boolean isHeightSet = false;
    private Activity mContext;
    private ExoPlayerViewHelper helper;
    @Nullable
    private Uri mediaUri;
    private PrefConfig mPrefConfig;
    private Article article;

    public VideoCardViewHolder(@NonNull View itemView, Activity context) {
        super(itemView);

        this.mContext = context;
        mPrefConfig = new PrefConfig(context);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        data = itemView.findViewById(R.id.data);
        source_image = itemView.findViewById(R.id.source_image);
        source_name = itemView.findViewById(R.id.source_name);
        time = itemView.findViewById(R.id.time);
        headline = itemView.findViewById(R.id.headline);
        imageBack = itemView.findViewById(R.id.imageBack);
        image = itemView.findViewById(R.id.image);
        play_image = itemView.findViewById(R.id.play_image);
        videoCard = itemView.findViewById(R.id.videoCard);
        text2 = itemView.findViewById(R.id.text2);
        playerView = itemView.findViewById(R.id.video_player);
        speaker = itemView.findViewById(R.id.speaker);
//        playDuration = itemView.findViewById(R.id.play_duration);
        card = itemView.findViewById(R.id.card);
//        transformationLayout = itemView.findViewById(R.id.transformationLayout);
    }

    public void bind(int position, DiscoverItem item) {

//        if (mContext != null) {
////            transformationLayout.setBackground(mContext.getResources().getDrawable(R.drawable.discover_card_shape));
//            text2.setTextColor(mContext.getResources().getColor(R.color.discover_card_title));
//        }

        if (item != null && item.getData() != null) {
            if (!TextUtils.isEmpty(item.getTitle())) {
                text2.setText(item.getTitle());
            }
            Article video = item.getData().getVideo();
            if (video != null) {
                article = video;
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
                if (!video.getAuthorNameToDisplay().equals("")) {
                    source_name.setText(video.getAuthorNameToDisplay());
                } else {
                    source_name.setText(video.getSourceNameToDisplay());
                }

                headline.setText(video.getTitle());

                if (!TextUtils.isEmpty(video.getLink()))
                    mediaUri = Uri.parse(video.getLink());

                if (!TextUtils.isEmpty(video.getImage())) {

                    Picasso.get()
                            .load(video.getImage())
                            .into(image);

                    Picasso.get()
                            .load(video.getImage())
                            .transform(new BlurTransformation(mContext, 25, 3))
                            .into(imageBack);
                }

//                if (video.getMediaMeta() != null) {
//                    playDuration.setText(video.getMediaMeta().getDurationString());
//                    playDuration.setVisibility(View.GONE);
//                } else {
//                    playDuration.setVisibility(View.GONE);
//                }
            }

//            if (Constants.muted) {
//                speaker.setImageResource(R.drawable.ic_speaker_mute);
//                if (helper != null)
//                    helper.setVolume(0);
//            } else {
//                speaker.setImageResource(R.drawable.ic_speaker_unmute);
//                if (helper != null)
//                    helper.setVolume(1);
//            }
//            speaker.setVisibility(View.VISIBLE);
//            speaker.setOnClickListener(v -> {
//                try {
//                    if (!Constants.muted) {
//                        Constants.muted = true;
//                        Constants.ReelMute = true;
//                        speaker.setImageResource(R.drawable.ic_speaker_mute);
//                        if (helper != null)
//                            helper.setVolume(0);
//                    } else {
//                        Constants.muted = false;
//                        Constants.ReelMute = false;
//                        speaker.setImageResource(R.drawable.ic_speaker_unmute);
//                        if (helper != null)
//                            helper.setVolume(1);
//                    }
//                } catch (Exception ignore) {
//
//                }
//            });

            thumbnail.setOnClickListener(v -> {
                playVideo();
                thumbnail.setVisibility(View.GONE);
                data.setVisibility(View.GONE);

//                if (item != null && mContext != null) {
//                    Intent intent = new Intent(mContext, BulletDetailActivity.class);
//                    intent.putExtra("article", new Gson().toJson(item.getData().getVideo()));
//                    //intent.putExtra("position", position);
//                    mContext.startActivity(intent);
//                }
            });


        }

        card.setOnClickListener(v -> {
//            if (item != null && mContext != null) {
//                Intent intent = new Intent(mContext, BulletDetailActivity.class);
//                intent.putExtra("article", new Gson().toJson(item.getData().getVideo()));
//                //intent.putExtra("position", position);
//                mContext.startActivity(intent);
//            }
        });

        playerView.setOnClickListener(v -> {
            if (item != null && mContext != null) {
                Intent intent = new Intent(mContext, BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(item.getData().getVideo()));
                //intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
//
        playerView.setControllerVisibilityListener(visibility -> showFlagAndAudioBtn(visibility == View.VISIBLE));
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

            if (mPrefConfig.isVideoAutoPlay()) {
                thumbnail.setVisibility(View.GONE);
            } else {
                thumbnail.setVisibility(View.VISIBLE);
                data.setVisibility(View.VISIBLE);
            }

            if (Constants.muted) {
                speaker.setImageResource(R.drawable.ic_speaker_mute);
                if (helper != null)
                    helper.setVolume(0);
            } else {
                speaker.setImageResource(R.drawable.ic_speaker_unmute);
                if (helper != null)
                    helper.setVolume(1);
            }
//            if (mPrefConfig.isAutoPlayMode()) {
//                speaker.setVisibility(View.GONE);
//            } else {
//                speaker.setVisibility(View.VISIBLE);
//            }
            speaker.setVisibility(View.GONE);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            //in the lib videos will autoplay by default when loaded, to block this following line is added //mPrefConfig.isAutoPlayMode()
            playerView.getPlayer().setPlayWhenReady(mPrefConfig.isVideoAutoPlay());
        }
    }

    private void playVideo() {
        if (helper != null) {
            helper.play();
            if (Constants.muted) {
                if (helper != null)
                    helper.setVolume(0);
            } else {
                if (helper != null)
                    helper.setVolume(1);
            }
            speaker.setVisibility(View.GONE);
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
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
    }

    @Override
    public int getPlayerOrder() {
        return getAbsoluteAdapterPosition();
    }

    @Override
    public void onFirstFrameRendered() {
        Log.d("TAG", "onFirstFrameRendered: ");
    }

    @Override
    public void onBuffering() {
        Log.d("TAG", "onBuffering: ");
    }

    @Override
    public void onPlaying() {
        thumbnail.setVisibility(View.GONE);
        data.setVisibility(View.GONE);
        Log.d("TAG", "onPlaying: ");
    }

    @Override
    public void onPaused() {
        Log.d("TAG", "onPaused: ");
    }

    @Override
    public void onCompleted() {
        thumbnail.setVisibility(View.VISIBLE);
        if (playerView != null && playerView.getPlayer() != null) {
            playerView.getPlayer().seekTo(0);
            playerView.getPlayer().setPlayWhenReady(false);
        }
        Log.d("TAG", "onCompleted: ");
        if (article != null && !TextUtils.isEmpty(article.getId())) {
            Map<String,String> params = new HashMap<>();
            params.put(Events.KEYS.ARTICLE_ID,article.getId());
            params.put(Events.KEYS.DURATION, String.valueOf(playerView.getPlayer().getCurrentPosition()));
            AnalyticsEvents.INSTANCE.logEventWithAPI(mContext,
                    params,
                    Events.ARTICLE_DURATION);
            AnalyticsEvents.INSTANCE.logEventWithAPI(mContext,
                    params,
                    Events.ARTICLE_VIDEO_COMPLETE);
        }
    }

    protected void showFlagAndAudioBtn(boolean show) {
        speaker.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
