package com.ziro.bullet.adapters.schedule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ui.PlayerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.VideoPreviewActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnScheduleCallback;
import com.ziro.bullet.interfaces.ScheduleTimerFinishListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.utills.BlurTransformation;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

public class ReelViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener, ToroPlayer.OnErrorListener {

    private static final String TAG = "ReelViewHolder";
    private final LinearLayout buttonPanel;
    private final ImageView ivSpeaker;
    public CardView videoMain;
    public PlayerView playerView;
    public FrameLayout image_card;
    public RelativeLayout cardMain;
    public boolean isHeightSet = false;
    CountDownTimer timer;
    private RoundedImageView profile;
    private ImageView odd_imageBack;
    private ImageView odd_image;
    private TextView source_name;
    private TextView user_name;
    private NewsCallback categoryCallback;
    private DetailsActivityInterface detailsActivityInterface;
    private AdapterCallback adapterCallback;
    private PrefConfig mPrefConfig;
    private Activity context;
    private TextView bulletHeadline;
    private TextView playDuration;
    private TextView tvTimer;
    private LinearLayout llPost, llEdit, llDelete, llTimer;
    private ExoPlayerViewHelper helper;
    @Nullable
    private Uri mediaUri;
    private Article currentPlayingArticle;
    private OnScheduleCallback onScheduleCallback;
    private ScheduledListAdapter.TYPE type;
    private ScheduleTimerFinishListener timerFinishListener;

    public ReelViewHolder(ScheduleTimerFinishListener timerFinishListener, @NonNull View itemView, PrefConfig config, Activity context,
                          AdapterCallback adapterCallback, NewsCallback categoryCallback, DetailsActivityInterface detailsActivityInterface,
                          OnScheduleCallback onScheduleCallback, ScheduledListAdapter.TYPE type) {
        super(itemView);
        this.timerFinishListener = timerFinishListener;
        this.adapterCallback = adapterCallback;
        this.context = context;
        this.categoryCallback = categoryCallback;
        this.detailsActivityInterface = detailsActivityInterface;
        this.onScheduleCallback = onScheduleCallback;
        this.type = type;

        mPrefConfig = config;

        buttonPanel = itemView.findViewById(R.id.buttonPanel);
        ivSpeaker = itemView.findViewById(R.id.speaker);
        cardMain = itemView.findViewById(R.id.cardMain);
        user_name = itemView.findViewById(R.id.user_name);
        bulletHeadline = itemView.findViewById(R.id.bulletHeadline);
        image_card = itemView.findViewById(R.id.image_card);
        odd_image = itemView.findViewById(R.id.odd_image);
        odd_imageBack = itemView.findViewById(R.id.odd_imageBack);
        profile = itemView.findViewById(R.id.profile);
        videoMain = itemView.findViewById(R.id.videoMain);
        source_name = itemView.findViewById(R.id.source_name);
        playerView = itemView.findViewById(R.id.video_player);
        playDuration = itemView.findViewById(R.id.play_duration);
        tvTimer = itemView.findViewById(R.id.tvTimer);
        llPost = itemView.findViewById(R.id.llPost);
        llEdit = itemView.findViewById(R.id.llEdit);
        llDelete = itemView.findViewById(R.id.llDelete);
        llTimer = itemView.findViewById(R.id.llTimer);
    }

    public void destroy() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void bind(int position, Article article) {

        if (article != null) {
            currentPlayingArticle = article;
            playDuration.setVisibility(View.GONE);

            bulletHeadline.setTextDirection(Utils.getLanguageDirectionForView(article.getLanguageCode()));

            if (article.getBullets() != null && article.getBullets().size() > 0) {
                playDuration.setVisibility(View.VISIBLE);
                if (article.getMediaMeta() != null && !TextUtils.isEmpty(article.getMediaMeta().getDurationString()))
                    playDuration.setText(article.getMediaMeta().getDurationString());
                if (!TextUtils.isEmpty(article.getImage())) {
                    loadThumbnail(article.getBullets().get(0).getImage());
                } else if (!TextUtils.isEmpty(article.getLink())) {
                    loadGlide(article.getLink());
                } else {
                    odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }
            } else {
                odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            image_card.setOnClickListener(v -> {
                if (article.getVideoInfo() != null) {
                    Intent intent = new Intent(context, VideoPreviewActivity.class);
                    intent.putExtra("video_info", article.getVideoInfo());
                    context.startActivity(intent);
                } else {
                    if (!article.isSelected()) {
                        if (adapterCallback != null)
                            adapterCallback.onItemClick(position, false);
                    }
                    if (!isPlaying()) {
                        playVideo();
                    }
                    image_card.setVisibility(View.GONE);
                }
            });

            llPost.setOnClickListener(v -> {
                if (onScheduleCallback != null)
                    onScheduleCallback.onPost(position);
            });
            llEdit.setOnClickListener(v -> {
                if (onScheduleCallback != null)
                    onScheduleCallback.onEdit(position);
            });
            llDelete.setOnClickListener(v -> {
                article.setSelected(false);
                if (onScheduleCallback != null)
                    onScheduleCallback.onDelete(position);
            });

            ivSpeaker.setOnClickListener(v -> {
                if (helper == null) return;
                if (!Constants.muted) {
                    Constants.muted = true;
                    Constants.ReelMute = true;
                    helper.setVolume(0);
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID,article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.MUTE_REELS);
                } else {
                    Constants.muted = false;
                    Constants.ReelMute = false;
                    helper.setVolume(1);
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID,article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.UNMUTE);
                }
                updateMuteButtons();
            });

            updateMuteButtons();

            if (article.getSourceImageToDisplay() != null && !article.getSourceImageToDisplay().equals("")) {
                Picasso.get()
                        .load(article.getSourceImageToDisplay())
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(profile);
            } else {
                Picasso.get()
                        .load(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(profile);
            }
            if (!article.getAuthorNameToDisplay().equals("")) {
                user_name.setVisibility(View.VISIBLE);
                user_name.setText(article.getAuthorNameToDisplay());
            } else {
                user_name.setVisibility(View.GONE);
            }
            source_name.setText(article.getSourceNameToDisplay());

            float val = -1;
            if (position == 0) {
                val = Utils.getHeadlineDimens(mPrefConfig, context);
            } else {
                val = Utils.getBulletDimens(mPrefConfig, context);
            }
            if (val != -1) {
                bulletHeadline.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
            }
            if (article.getBullets() != null && article.getBullets().size() > 0) {
                bulletHeadline.setText(article.getBullets().get(0).getData());
            }

            mediaUri = Uri.parse(article.getLink());

            if (type == ScheduledListAdapter.TYPE.SCHEDULE) {
                llTimer.setVisibility(View.VISIBLE);
                //Very important code for some problems
                if (timer != null) {
                    timer.cancel();
                }

                Date now = new Date();
                long millis = 0;
                Date publishDate = Utils.getDate(article.getPublishTime());
                if (publishDate != null)
                    millis = Objects.requireNonNull(publishDate).getTime() - now.getTime();

                long day = TimeUnit.MILLISECONDS.toDays(millis);
                if (day > 0) {
                    boolean sameYear = Utils.isSameYear(publishDate);
                    String pattern = "";
                    if (sameYear) {
                        pattern = "MMM dd, hh:mm aaa";
                    } else {
                        pattern = "yyyy MMM dd, hh:mm aaa";
                    }
                    String localizedFormattedDate = Utils.getCustomDate(article.getPublishTime(), pattern);
                    tvTimer.setText(String.format(context.getString(R.string.will_be_posted_on) + " %s", localizedFormattedDate));
                } else {
                    timer = new CountDownTimer(millis, 900) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.DAYS.toMillis(day);

                            long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);

                            long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);

                            long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                            if (day > 0) {
                                tvTimer.setText(String.format(context.getString(R.string.will_be_posted_in) + " %dd %dh %dm %ds", day, hour, minute, second));
                            } else if (hour > 0) {
                                tvTimer.setText(String.format(context.getString(R.string.will_be_posted_in) + " %dh %dm %ds", hour, minute, second));
                            } else if (minute > 0) {
                                tvTimer.setText(String.format(context.getString(R.string.will_be_posted_in) + " %dm %ds", minute, second));
                            } else if (second > 0) {
                                tvTimer.setText(String.format(context.getString(R.string.will_be_posted_in) + " %ds", second));
                            }
                        }

                        @Override
                        public void onFinish() {
                            tvTimer.setText(String.format(context.getString(R.string.will_be_posted_now)));
                            if (timerFinishListener != null)
                                timerFinishListener.removeArticle(position);
                        }
                    }.start();
                }
            } else if (type == ScheduledListAdapter.TYPE.PREVIEW) {
                llTimer.setVisibility(View.GONE);
                llPost.setVisibility(View.GONE);
                buttonPanel.setVisibility(View.GONE);
            } else {
                llTimer.setVisibility(View.GONE);
                llPost.setVisibility(View.GONE);
            }

            if (article.isSelected()) {

                if (mPrefConfig.isVideoAutoPlay())
                    image_card.setVisibility(View.GONE);
                else
                    image_card.setVisibility(View.VISIBLE);
            } else {
                if (helper != null)
                    helper.pause();
                image_card.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadGlide(String imageLink) {
        if (!TextUtils.isEmpty(imageLink)) {
            Glide.with(context)
                    .load(imageLink)
                    .apply(RequestOptions.bitmapTransform(new com.ziro.bullet.utills.BlurTransformation(50, 10)))
                    .override(Constants.targetWidth, Constants.targetHeight)
                    .into(odd_imageBack);

            Glide.with(context)
                    .load(imageLink)
                    .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                    .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                    .into(odd_image);
        }
    }

    public void updateMuteButtons() {
        ivSpeaker.setAlpha(1f);
        if (!Constants.muted) {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_unmute);
        } else {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_mute);
        }
    }


    private void loadVideoThumbnail(String link) {
        Glide.with(context)
                .load(link)
                .into(odd_image);

        Glide.with(context)
                .load(link)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                .into(odd_imageBack);
    }


    private void loadThumbnail(String imageLink) {
        if (!TextUtils.isEmpty(imageLink)) {

            Picasso.get()
                    .load(imageLink)
                    .transform(new jp.wasabeef.picasso.transformations.BlurTransformation(context, 25, 1))
                    .into(odd_imageBack);

            Picasso.get()
                    .load(imageLink)
                    .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                    .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                    .into(odd_image);
        }
    }

    private void nextArticle() {
        Log.e("kjandn", "nextArticle");
        Constants.auto_scroll = true;
        if (categoryCallback != null) {
            int pos = adapterCallback.getArticlePosition();
            pos++;
            categoryCallback.nextPosition(pos);
        }
    }

    private void prevArticle() {
        Constants.auto_scroll = true;
        Log.e("kjandn", "prevArticle");
        if (categoryCallback != null) {
            int pos = adapterCallback.getArticlePosition();
            pos--;
            categoryCallback.nextPosition(pos);
        }
    }

    public void bulletResume() {
        if (context != null) {
            if (detailsActivityInterface != null) {
                detailsActivityInterface.resume();
            }
        }
    }

    public void bulletPause() {
        if (context != null) {
            if (detailsActivityInterface != null) {
                detailsActivityInterface.pause();
            }
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

            //in the lib videos will autoplay by default when loaded, to block this following line is added
            playerView.getPlayer().setPlayWhenReady(mPrefConfig.isVideoAutoPlay());
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

            if (currentPlayingArticle != null) {
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.TYPE_COUNT_API_CALL);
                messageEvent.setStringData(currentPlayingArticle.getId());

                EventBus.getDefault().post(messageEvent);
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
        return currentPlayingArticle.isSelected() && ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.75;
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
        if (!currentPlayingArticle.isSelected()) {
            if (adapterCallback != null)
                adapterCallback.onItemClick(getAbsoluteAdapterPosition(), false);
        }
    }

    @Override
    public void onPaused() {
        Log.d(TAG, "onPaused: ");
    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted: ");
        nextArticle();
    }

    public void recycle() {
//        Picasso.get().clear(odd_imageBack);
//        Picasso.get().clear(odd_image);
    }

    @Override
    public void onError(Exception error) {
        Log.d(TAG, "onError: " + error);
        error.printStackTrace();
    }
}
