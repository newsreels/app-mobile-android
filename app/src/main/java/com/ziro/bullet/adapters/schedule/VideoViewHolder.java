package com.ziro.bullet.adapters.schedule;

import android.app.Activity;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
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
import org.jetbrains.annotations.NotNull;

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
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class VideoViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener, ToroPlayer.OnErrorListener {

    private static final String TAG = "ScheduleVideoViewHolder";
    public CardView videoMain;
    public PlayerView playerView;
    public CardView image_card;
    public RelativeLayout cardMain;
    public boolean isHeightSet = false;
    CountDownTimer timer;
    private float Y_BUFFER = 10;
    private float preX = 0f;
    private float preY = 0f;
    private RoundedImageView profile;
    private TextView authorName;
    private ImageView odd_imageBack;
    private ImageView odd_image;
    private ImageView ivSpeaker;
    private ImageView fullscreen;
    private TextView source_name;
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

    public VideoViewHolder(ScheduleTimerFinishListener timerFinishListener, @NotNull View itemView, PrefConfig config, Activity context,
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

        cardMain = itemView.findViewById(R.id.cardMain);
        bulletHeadline = itemView.findViewById(R.id.bulletHeadline);
        image_card = itemView.findViewById(R.id.image_card);
        odd_image = itemView.findViewById(R.id.odd_image);
        odd_imageBack = itemView.findViewById(R.id.odd_imageBack);
        profile = itemView.findViewById(R.id.profile);
        videoMain = itemView.findViewById(R.id.videoMain);
        source_name = itemView.findViewById(R.id.source_name);
        playerView = itemView.findViewById(R.id.video_player);
        ivSpeaker = itemView.findViewById(R.id.speaker);
        fullscreen = itemView.findViewById(R.id.fullscreen);
        playDuration = itemView.findViewById(R.id.play_duration);
        tvTimer = itemView.findViewById(R.id.tvTimer);
        llPost = itemView.findViewById(R.id.llPost);
        llEdit = itemView.findViewById(R.id.llEdit);
        llDelete = itemView.findViewById(R.id.llDelete);
        llTimer = itemView.findViewById(R.id.llTimer);
        authorName = itemView.findViewById(R.id.author_name);
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
                if (article.getMediaMeta() != null) {
                    playDuration.setVisibility(View.VISIBLE);
                    playDuration.setText(article.getMediaMeta().getDurationString());
                }
                if (!TextUtils.isEmpty(article.getImage())) {
                    loadThumbnail(article.getBullets().get(0).getImage());
                } else if (!TextUtils.isEmpty(article.getLink())) {
                    loadVideoThumbnail(article.getLink());
                } else {
                    odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }
            } else {
                odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            image_card.setOnClickListener(v -> {
                if (!article.isSelected()) {
                    if (adapterCallback != null)
                        adapterCallback.onItemClick(position, false);
                }
                if (!isPlaying()) {
                    playVideo();
                }
                image_card.setVisibility(View.GONE);
                playDuration.setVisibility(View.GONE);
//                ivSpeaker.setVisibility(View.VISIBLE);
                fullscreen.setVisibility(View.VISIBLE);
                updateMuteButtons();
            });

            fullscreen.setOnClickListener(v -> {
                article.setLastPosition(position);
                Utils.openFullScreen(context, article, "video", playerView.getPlayer().getContentPosition());
            });

            ivSpeaker.setOnClickListener(v -> {
                try {
                    if (!Constants.muted) {
                        Constants.muted = true;
                        Constants.ReelMute = true;
                        helper.setVolume(0f);
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.ARTICLE_ID,article.getId());
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.MUTE_VIDEO_FEED);
                    } else {
                        Constants.muted = false;
                        Constants.ReelMute = false;
                        helper.setVolume(1f);
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.ARTICLE_ID,article.getId());
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.MUTE_VIDEO_FEED);
                    }
                    updateMuteButtons();
                } catch (Exception ignore) {
                }
            });

            llPost.setOnClickListener(v -> {
                article.setSelected(false);
                if (onScheduleCallback != null)
                    onScheduleCallback.onPost(position);
            });
            llEdit.setOnClickListener(v -> {
                if (onScheduleCallback != null)
                    onScheduleCallback.onEdit(position);
            });
            llDelete.setOnClickListener(v -> {
                article.setSelected(false);
                if (onScheduleCallback != null) {
                    onScheduleCallback.onDelete(position);
                }
            });

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
            } else {
                llTimer.setVisibility(View.GONE);
                llPost.setVisibility(View.GONE);
            }

            source_name.setText(article.getSourceNameToDisplay());

            if (!TextUtils.isEmpty(article.getSourceImageToDisplay())) {
                Picasso.get()
                        .load(article.getSourceImageToDisplay())
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .transform(new CropCircleTransformation())
                        .into(profile);
            } else {
                Picasso.get()
                        .load(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .transform(new CropCircleTransformation())
                        .into(profile);
            }

            if (!article.getAuthorNameToDisplay().equals("")) {
                authorName.setVisibility(View.VISIBLE);
                authorName.setText(article.getAuthorNameToDisplay());
            } else {
                authorName.setVisibility(View.GONE);
            }

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


            if (article.isSelected()) {
//                ivSpeaker.setVisibility(View.VISIBLE);
//                fullscreen.setVisibility(View.VISIBLE);

                if (mPrefConfig.isVideoAutoPlay()) {
                    image_card.setVisibility(View.GONE);
                    playDuration.setVisibility(View.GONE);
                } else {
                    image_card.setVisibility(View.VISIBLE);
                    playDuration.setVisibility(View.VISIBLE);
                }
                updateMuteButtons();
            } else {
                if (helper != null)
                    helper.pause();
//                ivSpeaker.setVisibility(View.GONE);
//                fullscreen.setVisibility(View.GONE);
                image_card.setVisibility(View.VISIBLE);
                playDuration.setVisibility(View.VISIBLE);
            }
        }

        playerView.setControllerVisibilityListener(visibility -> showFlagAndAudioBtn(visibility == View.VISIBLE));
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

    public void updateMuteButtons() {
        if (!Constants.muted) {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_unmute);
        } else {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_mute);
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
            if (playerView != null && playerView.getPlayer() != null) {
                playerView.getPlayer().setPlayWhenReady(mPrefConfig.isVideoAutoPlay());
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
        if (currentPlayingArticle != null) {
            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setType(MessageEvent.TYPE_COUNT_API_CALL);
            messageEvent.setStringData(currentPlayingArticle.getId());

            EventBus.getDefault().post(messageEvent);
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

    protected void showFlagAndAudioBtn(boolean show) {
        ivSpeaker.setVisibility(show ? View.VISIBLE : View.GONE);
        fullscreen.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
