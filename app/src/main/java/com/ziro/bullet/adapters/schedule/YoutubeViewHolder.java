package com.ziro.bullet.adapters.schedule;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.CardBulletsAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnScheduleCallback;
import com.ziro.bullet.interfaces.ScheduleTimerFinishListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.storyMaker.HorizontalStoriesProgressView;
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

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class YoutubeViewHolder extends RecyclerView.ViewHolder {
    private static final int MIN_DISTANCE = 150;
    private static final String TAG = "scheduleyoutubePlayer";
    public HorizontalStoriesProgressView storiesProgressView;
    private float Y_BUFFER = 10;
    private float preX = 0f;
    private float preY = 0f;
    private RoundedImageView profile;
    private ImageView odd_imageBack;
    private ImageView odd_image;
    private TextView source_name;
    private RecyclerView desc_list;

    private ProgressBar progressAudio;
    private CardView youtubeMain;
    private YouTubePlayerView youTubePlayerView;
    private ImageView play;
    private TextView playDuration;
    private float x1, x2;
    private NewsCallback categoryCallback;
    private DetailsActivityInterface listener;
    private AdapterCallback adapterCallback;
    private PrefConfig mPrefConfig;
    private Activity context;
    private YouTubePlayer youTubePlayer;
    private CardBulletsAdapter bulletsAdapterMain;
    private Handler handler = new Handler();
    private boolean swipeNextEnabled = true;
    private Runnable tapRunnable = () -> swipeNextEnabled = true;
    private boolean isPlaying = false;
    private boolean shouldLoad = true;
    private String youtubeLink = "";
    private Article bindArticle;
    private String mType;
    private TextView tvTimer;
    private LinearLayout llPost, llEdit, llDelete, llTimer;
    private OnScheduleCallback onScheduleCallback;
    private CountDownTimer timer;
    private ScheduledListAdapter.TYPE type;
    private TextView authorName;
    private ScheduleTimerFinishListener timerFinishListener;
    private ImageView ivSpeaker;
    private ImageView fullscreen;
    private long youtubeDuration = 0;
    private boolean isCommunity;

    public YoutubeViewHolder(boolean isCommunity, ScheduleTimerFinishListener timerFinishListener, @NotNull View itemView, PrefConfig config, Activity context,
                             AdapterCallback adapterCallback, NewsCallback categoryCallback,
                             OnScheduleCallback onScheduleCallback, ScheduledListAdapter.TYPE type) {
        super(itemView);
        this.mType = mType;
        this.type = type;
        this.isCommunity = isCommunity;
        this.timerFinishListener = timerFinishListener;

        this.adapterCallback = adapterCallback;
        this.context = context;
        this.categoryCallback = categoryCallback;
        this.onScheduleCallback = onScheduleCallback;

        mPrefConfig = config;

        play = itemView.findViewById(R.id.play);
        playDuration = itemView.findViewById(R.id.play_duration);
        youtubeMain = itemView.findViewById(R.id.youtubeMain);
        youTubePlayerView = itemView.findViewById(R.id.youtube_view);
        progressAudio = itemView.findViewById(R.id.progressAudio);
        odd_image = itemView.findViewById(R.id.odd_image);
        odd_imageBack = itemView.findViewById(R.id.odd_imageBack);
        profile = itemView.findViewById(R.id.profile);
        desc_list = itemView.findViewById(R.id.desc_list);
        source_name = itemView.findViewById(R.id.source_name);
        storiesProgressView = itemView.findViewById(R.id.stories);
        tvTimer = itemView.findViewById(R.id.tvTimer);
        llPost = itemView.findViewById(R.id.llPost);
        llEdit = itemView.findViewById(R.id.llEdit);
        llDelete = itemView.findViewById(R.id.llDelete);
        llTimer = itemView.findViewById(R.id.llTimer);
        authorName = itemView.findViewById(R.id.author_name);
        ivSpeaker = itemView.findViewById(R.id.speaker);
        fullscreen = itemView.findViewById(R.id.fullscreen);

        if (context instanceof AppCompatActivity)
            ((AppCompatActivity) context).getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {
                Log.d(TAG, "onVideoLoadedFraction: v = " + v);
                Log.d(TAG, "onVideoLoadedFraction: shouldLoad = " + shouldLoad);
                //causing issues in feed auto pause
//                if (!shouldLoad) {
//                    bulletPause();
//                }
            }

            @Override
            public void onVideoId(@NotNull YouTubePlayer youTubePlayer, @NotNull String s) {
            }

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float v) {
                Log.d(TAG, "onVideoDuration() called with: youTubePlayer = [" + youTubePlayer + "], v = [" + v + "]");
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {
                if (playerState == PlayerConstants.PlayerState.ENDED) {
                    nextArticle();
                }

                if (playerState == PlayerConstants.PlayerState.PLAYING && !shouldLoad) {
                    bulletPause();
                }

                if (playerState == PlayerConstants.PlayerState.PLAYING) {
                    if (Constants.muted) {
                        youTubePlayer.mute();
                    } else {
                        youTubePlayer.unMute();
                    }
                    updateMuteButtons();
                    showFlagAndAudioBtn(false);
                } else {
                    showFlagAndAudioBtn(true);
                }

                Log.d(TAG, "onStateChange: " + playerState);
            }

            @Override
            public void onPlaybackRateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {
                Log.d(TAG, "onError() called with: youTubePlayer = [" + youTubePlayer + "], playerError = [" + playerError + "]");
            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {
            }

            @Override
            public void onReady(@NotNull YouTubePlayer player) {
                if (getAbsoluteAdapterPosition() == 0 || youTubePlayer == null) {

                    Log.d(TAG, "onReady: link = " + bindArticle.getLink());
                    if (mPrefConfig.isVideoAutoPlay()) {
                        if (bindArticle != null) {
                            if (bindArticle.isSelected() && shouldLoad) {
                                player.loadVideo(bindArticle.getLink(), 0);
                            } else
                                player.cueVideo(bindArticle.getLink(), 0);
                        }
                    } else {
                        player.cueVideo(bindArticle.getLink(), 0);
                    }
                } else {
                    player.cueVideo(bindArticle.getLink(), 0);
                }
                youTubePlayer = player;
                if (Constants.muted) {
                    youTubePlayer.mute();
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.MUTE_VIDEO_FEED);
                } else {
                    youTubePlayer.unMute();
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.UNMUTE);
                }
                if (bindArticle != null) {
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setType(MessageEvent.TYPE_COUNT_API_CALL);
                    messageEvent.setStringData(bindArticle.getId());

                    EventBus.getDefault().post(messageEvent);
                }
            }
        });
    }

    public void destroy() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void bind(int position, Article article) {
        if (article != null) {
            bindArticle = article;
            youtubeLink = article.getLink();
//            Log.d(TAG, "bind: " + shouldLoad);
            shouldLoad = true;
//            Log.d(TAG, "bind: " + shouldLoad);
            storiesProgressView.pause();
            storiesProgressView.setStoriesCount(1);
            storiesProgressView.getLayoutParams().height = 0;
            storiesProgressView.requestLayout();
            odd_imageBack.setVisibility(View.VISIBLE);
            odd_image.setVisibility(View.VISIBLE);
            youtubeMain.setVisibility(View.GONE);
            playDuration.setVisibility(View.GONE);

            if (article.isSelected()) {
                ivSpeaker.setVisibility(View.VISIBLE);
                fullscreen.setVisibility(View.VISIBLE);
            } else {
                ivSpeaker.setVisibility(View.GONE);
                fullscreen.setVisibility(View.GONE);
            }
            updateMuteButtons();

            if (article.getBullets() != null && article.getBullets().size() > 0) {

                playDuration.setVisibility(View.VISIBLE);
                playDuration.setText(article.getBullets().get(0).getDurationString());

                if (!TextUtils.isEmpty(article.getBullets().get(0).getImage())) {

                    if (!TextUtils.isEmpty(article.getImage())) {
                        Picasso.get()
                                .load(article.getImage())
                                .transform(new jp.wasabeef.picasso.transformations.BlurTransformation(context, 25, 1))
                                .into(odd_imageBack);

                        Picasso.get()
                                .load(article.getImage())
                                .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                                .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                                .into(odd_image);
                    }

                } else {
                    odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }
            } else {
                odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

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

            fullscreen.setOnClickListener(v -> {
                article.setLastPosition(position);
                Utils.openFullScreen(context, article, "youtube", youtubeDuration);
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
                bulletPause();
                if (onScheduleCallback != null)
                    onScheduleCallback.onDelete(position);
            });

            ivSpeaker.setOnClickListener(v -> {
                if (!article.isSelected())
                    if (categoryCallback!=null)
                    categoryCallback.nextPositionNoScroll(position, true);
                try {
                    if (!Constants.muted) {
                        if (youTubePlayer != null) {
                            Constants.muted = true;
                            Constants.ReelMute = true;
                            youTubePlayer.mute();
                            //helper.setVolume(0f);
                            Map<String,String> params = new HashMap<>();
                            params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    params,
                                    Events.MUTE_VIDEO_FEED);
                        }
                    } else {
                        if (youTubePlayer != null) {
                            Constants.muted = false;
                            Constants.ReelMute = false;
                            youTubePlayer.unMute();
                            //helper.setVolume(1f);
                            Map<String,String> params = new HashMap<>();
                            params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    params,
                                    Events.UNMUTE);
                        }
                    }
                    updateMuteButtons();
                } catch (Exception ignore) {
                }
            });


            final int[] mPosition = {0};
            if (article.getBullets() != null && article.getBullets().size() > 0) {
                CardBulletsAdapter bulletsAdapter = new CardBulletsAdapter(isCommunity, null, mType, false, (Activity) context, article.getBullets(), new ListAdapterListener() {
                    @Override
                    public void verticalScrollList(boolean isEnable) {
                        if (isEnable) {
                            bulletResume();
                        } else {
                            bulletPause();
                        }
                    }

                    @Override
                    public void nextArticle(int position) {
                    }

                    @Override
                    public void prevArticle(int position) {
                    }

                    @Override
                    public void clickArticle(int position) {
                        if (article.isSelected()) {
                            play.setVisibility(View.GONE);
                            odd_imageBack.setVisibility(View.GONE);
                            odd_image.setVisibility(View.GONE);
                            youtubeMain.setVisibility(View.VISIBLE);
                            playDuration.setVisibility(View.GONE);

                            if (!isPlaying) {
                                if (youTubePlayer != null) {
                                    youTubePlayer.loadVideo(article.getLink(), 0);
                                    if (Constants.muted) {
                                        youTubePlayer.mute();
                                        Map<String,String> params = new HashMap<>();
                                        params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                                        AnalyticsEvents.INSTANCE.logEvent(context,
                                                params,
                                                Events.MUTE_VIDEO_FEED);
                                    } else {
                                        youTubePlayer.unMute();
                                        Map<String,String> params = new HashMap<>();
                                        params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                                        AnalyticsEvents.INSTANCE.logEvent(context,
                                                params,
                                                Events.UNMUTE);
                                    }
                                    youTubePlayer.play();
                                    isPlaying = true;
                                } else {
                                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {

                                        @Override
                                        public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float loadedFraction) {
                                            super.onVideoLoadedFraction(youTubePlayer, loadedFraction);
                                            youtubeDuration = (long) loadedFraction;
                                        }

                                        @Override
                                        public void onReady(@NotNull YouTubePlayer initializedYouTubePlayer) {
                                            super.onReady(initializedYouTubePlayer);
                                            youTubePlayer = initializedYouTubePlayer;
                                            youTubePlayer.loadVideo(article.getLink(), 0);
                                            if (Constants.muted) {
                                                youTubePlayer.mute();
                                                Map<String,String> params = new HashMap<>();
                                                params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                                                AnalyticsEvents.INSTANCE.logEvent(context,
                                                        params,
                                                        Events.MUTE_VIDEO_FEED);
                                            } else {
                                                youTubePlayer.unMute();
                                                Map<String,String> params = new HashMap<>();
                                                params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                                                AnalyticsEvents.INSTANCE.logEvent(context,
                                                        params,
                                                        Events.UNMUTE);
                                            }
                                            youTubePlayer.play();
                                            isPlaying = true;

                                            Log.e("articleview", "youtube");
                                        }
                                    });
                                }
                            } else {
                                if (youTubePlayer != null)
                                    youTubePlayer.pause();
                                isPlaying = false;
                            }
                        }
                    }
                }, article, position);
                LinearLayoutManager manager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                desc_list.setLayoutManager(manager);
//                                desc_list.setHasFixedSize(true);
                desc_list.setAdapter(bulletsAdapter);
                desc_list.setOnFlingListener(null);
                PagerSnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(desc_list);
                desc_list.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                x1 = e.getX();
                                desc_list.getParent().requestDisallowInterceptTouchEvent(true);
                                break;
                            case MotionEvent.ACTION_UP:
                                if (bulletsAdapterMain != null) {
                                    x2 = e.getX();
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (Math.abs(e.getX() - preX) > Math.abs(e.getY() - preY)) {
                                    desc_list.getParent().requestDisallowInterceptTouchEvent(true);
                                } else if (Math.abs(e.getY() - preY) > Y_BUFFER) {
                                    desc_list.getParent().requestDisallowInterceptTouchEvent(false);
                                }
                                break;
                        }
                        preX = e.getX();
                        preY = e.getY();
                        return false;
                    }

                    @Override
                    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                });


                desc_list.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                desc_list.requestLayout();
                RecyclerView.OnScrollListener scrollListener;
                desc_list.addOnScrollListener(scrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (mPosition[0] != -1) {
                                article.setLastPosition(mPosition[0]);
                                bulletsAdapter.setCurrentPosition(mPosition[0]);
//                            if (!TextUtils.isEmpty(article.getFragTag()) && article.isSelected() && article.getFragTag().equalsIgnoreCase("id_" + Constants.visiblePage)) {
//                                Constants.auto_scroll = false;
//                                playAudio( bulletsAdapter, article);
//                            } else {
//                                resizeBullet( bulletsAdapter, article);
//                            }
                            }
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        mPosition[0] = snapHelper.findTargetSnapPosition(manager, dx, dy);
//                    if (dx > 0) {
//                        direction = "rtl";
//                    } else {
//                        direction = "ltr";
//                    }
                    }
                });

                if (article.isSelected()) {
                    bulletsAdapterMain = bulletsAdapter;
                    storiesProgressView.setStoriesListener(null);
                    storiesProgressView.destroy();
                    storiesProgressView.setVisibility(View.INVISIBLE);
                    //commentMain.setVisibility(View.VISIBLE);
                    //likeMain.setVisibility(View.VISIBLE);
                    progressAudio.setVisibility(View.GONE);

                    if (mPrefConfig.isVideoAutoPlay()) {
                        play.setVisibility(View.GONE);
                        odd_imageBack.setVisibility(View.GONE);
                        odd_image.setVisibility(View.GONE);
                        playDuration.setVisibility(View.GONE);
                        youtubeMain.setVisibility(View.VISIBLE);
                    } else {
                        play.setVisibility(View.GONE);
                        odd_imageBack.setVisibility(View.VISIBLE);
                        odd_image.setVisibility(View.VISIBLE);
                        playDuration.setVisibility(View.VISIBLE);
                        youtubeMain.setVisibility(View.GONE);
                    }

                    if (youTubePlayer != null) {
                        if (mPrefConfig.isVideoAutoPlay() && shouldLoad) {
                            if (bindArticle != null) {
                                if (bindArticle.isSelected() && shouldLoad) {
                                    isPlaying = true;
                                    youTubePlayer.loadVideo(bindArticle.getLink(), 0);
                                } else
                                    youTubePlayer.cueVideo(bindArticle.getLink(), 0);
                            }
                        } else {
                            youTubePlayer.cueVideo(bindArticle.getLink(), 0);
                        }
                        if (Constants.muted) {
                            youTubePlayer.mute();
                            Map<String,String> params = new HashMap<>();
                            params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    params,
                                    Events.MUTE_VIDEO_FEED);
                        } else {
                            youTubePlayer.unMute();
                            Map<String,String> params = new HashMap<>();
                            params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    params,
                                    Events.UNMUTE);
                        }


//                        if (mPrefConfig.isVideoAutoPlay() && shouldLoad) {
//                            isPlaying = true;
//                            youTubePlayer.play();
//                        youTubePlayer.unMute();
//                        } else {
//                            youTubePlayer.pause();
//                        youTubePlayer.mute();
//                        }


//                    if (mPrefConfig.isDataSavedMode()) {
//                        youTubePlayer.loadVideo(youtubeLink, 0);
//                    } else {
//                        youTubePlayer.cueVideo(youtubeLink, 0);
////                        youTubePlayer.mute();
//                    }


//                    youTubePlayer.loadVideo(article.getLink(), 0);
//                    if (mPrefConfig.isDataSavedMode()) {
//                        youTubePlayer.pause();
//                        youTubePlayer.mute();
//                    }
                    }
                } else {
                    storiesProgressView.setStoriesListener(null);
                    storiesProgressView.destroy();
                    storiesProgressView.setVisibility(View.INVISIBLE);
                    //commentMain.setVisibility(View.INVISIBLE);
                    //likeMain.setVisibility(View.INVISIBLE);
                    desc_list.scrollToPosition(article.getLastPosition());
                    progressAudio.setVisibility(View.GONE);
                    play.setVisibility(View.GONE);
                    if (youTubePlayer != null) {
                        youTubePlayer.pause();
//                    youTubePlayer.mute();
                    }
                    isPlaying = false;
                }
            }
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
        desc_list.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        desc_list.requestLayout();
        if (categoryCallback != null) {
            int pos = adapterCallback.getArticlePosition();
            pos++;
            categoryCallback.nextPosition(pos);
        }
        if (youTubePlayer != null) {
            youTubePlayer.pause();
//            youTubePlayer.mute();
        }
        isPlaying = false;
    }

    public void bulletResume() {
        if (context != null) {
            storiesProgressView.resume();
            if (isPlaying) {
                if (youTubePlayer != null) {
                    youTubePlayer.play();
                    if (Constants.muted) {
                        youTubePlayer.mute();
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.MUTE_VIDEO_FEED);
                    } else {
                        youTubePlayer.unMute();
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.ARTICLE_ID,bindArticle.getId());
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.UNMUTE);
                    }
                }
            }
            isPlaying = false;
        }
    }

    public void bulletPause() {
        if (context != null) {
            Log.d("youtubePlayer", "bulletPause: 1 shouldLoad " + shouldLoad);
            Log.d("youtubePlayer", "bulletPause: 1 youTubePlayer " + youTubePlayer);
            storiesProgressView.pause();
            if (youTubePlayer != null) {
                youTubePlayer.pause();
//                youTubePlayer.mute();
            }
            isPlaying = false;
            shouldLoad = false;
            Log.d("youtubePlayer", "bulletPause: 1 shouldLoad " + shouldLoad);
        }
    }

    protected void showFlagAndAudioBtn(boolean show) {
        ivSpeaker.setVisibility(show ? View.VISIBLE : View.GONE);
        fullscreen.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
