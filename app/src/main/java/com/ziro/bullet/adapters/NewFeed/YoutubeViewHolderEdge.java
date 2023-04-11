package com.ziro.bullet.adapters.NewFeed;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.adapters.CardBulletsAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.fragments.CategoryFragment;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.SelectedChannel;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class YoutubeViewHolderEdge extends RecyclerView.ViewHolder {

    private static final int MIN_DISTANCE = 150;
    private static final String TAG = "youtubePlayer";
    private final LikePresenter presenter;
    public long youtubeDuration = 0;
    private float Y_BUFFER = 10;
    private float preX = 0f;
    private float preY = 0f;
    private ImageView odd_imageBack;
    private ImageView odd_image;
    private TextView source_name;
    private TextView tvTime;
    private ProgressBar progressAudio;
    private CardView youtubeMain;
    private YouTubePlayerView youTubePlayerView;
    private ImageView play;
    private ImageView fullscreen;
    private TextView playDuration;
    private TextView article_title;
    private float x1, x2;
    private ShareToMainInterface shareToMainInterface;
    private TempCategorySwipeListener swipeListener;
    private GoHome goHomeMainActivity;
    private OnGotoChannelListener gotoChannelListener;
    private NewsCallback categoryCallback;
    private DetailsActivityInterface listener;
    private AdapterCallback adapterCallback;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private PrefConfig mPrefConfig;
    private AppCompatActivity context;
    private YouTubePlayer youTubePlayer;
    private CardBulletsAdapter bulletsAdapterMain;
    private Handler handler = new Handler();
    private boolean swipeNextEnabled = true;
    private Runnable tapRunnable = () -> swipeNextEnabled = true;
    private boolean isPlaying = false;
    private boolean shouldLoad = true;
    private String youtubeLink = "";
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private Article bindArticle;
    private boolean isPostArticle = false;
    private String type;
    private CommentClick mCommentClick;
    private ImageView statusIcon;
    private ImageView ivSpeaker;
    private TextView text1, text2;
    private boolean isCommunity;
    private Lifecycle lifecycle;
    public CategoryFragment categoryFragment;

    public YoutubeViewHolderEdge(boolean isCommunity, CommentClick mCommentClick, String type, boolean isPostArticle, @NonNull View itemView, AppCompatActivity context,
                                 AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity,
                                 ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener,
                                 NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener,
                                 DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback,
                                 Lifecycle lifecycle) {
        super(itemView);
        this.presenter = new LikePresenter(context);
        this.shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
        this.swipeListener = swipeListener;
        this.mCommentClick = mCommentClick;
        this.type = type;
        this.isCommunity = isCommunity;
        this.adapterCallback = adapterCallback;
        this.context = context;
        this.isPostArticle = isPostArticle;
        this.goHomeMainActivity = goHomeMainActivity;
        this.shareToMainInterface = shareToMainInterface;
        this.gotoChannelListener = gotoChannelListener;
        this.categoryCallback = categoryCallback;
        this.listener = detailsActivityInterface;
        this.lifecycle = lifecycle;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;

        mPrefConfig = config;
        article_title = itemView.findViewById(R.id.article_title);
        play = itemView.findViewById(R.id.play);
        playDuration = itemView.findViewById(R.id.play_duration);
        youtubeMain = itemView.findViewById(R.id.youtubeMain);
        youTubePlayerView = itemView.findViewById(R.id.youtube_view);
        progressAudio = itemView.findViewById(R.id.progressAudio);
        odd_image = itemView.findViewById(R.id.odd_image);
        odd_imageBack = itemView.findViewById(R.id.odd_imageBack);
        tvTime = itemView.findViewById(R.id.time);
        source_name = itemView.findViewById(R.id.source_name);
        statusIcon = itemView.findViewById(R.id.statusIcon);
        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
        ivSpeaker = itemView.findViewById(R.id.speaker);
        fullscreen = itemView.findViewById(R.id.fullscreen);

        if (context instanceof AppCompatActivity)
            ((AppCompatActivity) context).getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {
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
                youtubeDuration = (long) v;
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {
//                if (playerState == PlayerConstants.PlayerState.ENDED && !isPostArticle) {
//                    Utils.logEvent(context, bindArticle.getId(), Constants.Events.ARTICLE_VIDEO_COMPLETE);
//                    nextArticle();
//                }

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
            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {
                youtubeDuration = (long) v;
                if((categoryFragment != null && !categoryFragment.checkVisibility()) ){
                    youTubePlayer.pause();
                }
            }

            @Override
            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {
            }

            @Override
            public void onReady(@NotNull YouTubePlayer player) {
                if (Constants.muted) {
                    player.mute();
                } else {
                    player.unMute();
                }
                if (getAbsoluteAdapterPosition() == 0 || youTubePlayer == null) {

                    Log.d(TAG, "onReady: link = " + bindArticle.getLink());
                    if (mPrefConfig.isVideoAutoPlay()) {
                        if (bindArticle != null) {
                            if (bindArticle.isSelected() && shouldLoad && (categoryFragment == null || categoryFragment.checkVisibility())) {
                                YouTubePlayerUtils.loadOrCueVideo(
                                        player,
                                        lifecycle,
                                        bindArticle.getLink(),
                                        youtubeDuration
                                );
                                //player.loadVideo(bindArticle.getLink(), youtubeDuration);
                            } else
                                player.cueVideo(bindArticle.getLink(), youtubeDuration);
                        }
                    } else {
                        player.cueVideo(bindArticle.getLink(), youtubeDuration);
                    }
                } else {
                    player.cueVideo(bindArticle.getLink(), youtubeDuration);
                }
                if (bindArticle != null) {
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setType(MessageEvent.TYPE_COUNT_API_CALL);
                    messageEvent.setStringData(bindArticle.getId());

                    EventBus.getDefault().post(messageEvent);
                }
                youTubePlayer = player;
            }
        });
    }


    public void seekTo(long duration) {
        youtubeDuration = duration;
        if (youTubePlayer != null) {
            youTubePlayer.seekTo(duration);
        }
    }

    public void release() {
        shouldLoad = false;
        if (youTubePlayer != null) {
            youTubePlayer.pause();
        }
        if (youTubePlayerView != null)
            youTubePlayerView.release();
        youTubePlayer = null;
        youTubePlayerView = null;
    }

    public void invalidate() {
        updateMuteButtons();
        fullscreen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fullscreen_icon));
        if (bulletsAdapterMain != null)
            bulletsAdapterMain.notifyDataSetChanged();
        source_name.setTextColor(context.getResources().getColor(R.color.large_source_name));
    }

    public void forReelBottomSheet() {
        updateMuteButtons();
        source_name.setTextColor(context.getResources().getColor(R.color.black));
    }

    public void bind(int position, Article article) {
        if (article != null) {
            bindArticle = article;
            youtubeLink = article.getLink();
            shouldLoad = true;
            odd_imageBack.setVisibility(View.VISIBLE);
            odd_image.setVisibility(View.VISIBLE);
            youtubeMain.setVisibility(View.GONE);

            if (article.isSelected()) {
//                ivSpeaker.setVisibility(View.VISIBLE);
//                fullscreen.setVisibility(View.VISIBLE);
            } else {
                ivSpeaker.setVisibility(View.GONE);
                fullscreen.setVisibility(View.GONE);
            }
            updateMuteButtons();

            if (article.getBullets() != null && article.getBullets().size() > 0) {

                if (article.getBullets().get(0).getDuration() == 0) {
                    playDuration.setText("Live");
                    youTubePlayerView.getPlayerUiController().enableLiveVideoUi(true);
                } else {
                    playDuration.setText(article.getBullets().get(0).getDurationString());
                    youTubePlayerView.getPlayerUiController().enableLiveVideoUi(false);
                }
                playDuration.setVisibility(View.VISIBLE);


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

                } else {
                    odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }
            } else {
                youTubePlayerView.getPlayerUiController().enableLiveVideoUi(false);
                odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            if (!isPostArticle) {

                source_name.setText(article.getSourceNameToDisplay());
//                if (!TextUtils.isEmpty(article.getSourceImageToDisplay())) {
//                    Picasso.get()
//                            .load(article.getSourceImageToDisplay())
//                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                            .transform(new CropCircleTransformation())
//                            .into(profile);
//                }


            } else {
                // for new post article
                String name = null, image = null;
                if (mPrefConfig != null) {
                    if (mPrefConfig.selectedChannel() != null && !TextUtils.isEmpty(mPrefConfig.selectedChannel().getName()) &&
                            !mPrefConfig.selectedChannel().getName().equalsIgnoreCase(context.getString(R.string.my_profile))) {
                        SelectedChannel channel = mPrefConfig.selectedChannel();
                        name = channel.getName();
                        image = channel.getImage();
                    } else {
                        if (mPrefConfig.isUserObject() != null) {
                            image = mPrefConfig.isUserObject().getProfile_image();
                            name = mPrefConfig.isUserObject().getFirst_name() + " " + mPrefConfig.isUserObject().getLast_name();
                        }
                    }
                    source_name.setText(name);
//                    if (!TextUtils.isEmpty(image)) {
//                        Picasso.get()
//                                .load(image)
//                                .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                                .transform(new CropCircleTransformation())
//                                .into(profile);
//                    } else {
//                        profile.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
//                    }
                }
                tvTime.setText(R.string.today);
            }

            article_title.setText(article.getTitle());

            fullscreen.setOnClickListener(v -> {
                if (mCommentClick != null)
                    mCommentClick.fullscreen(position, article, youtubeDuration, "youtube", true);
            });

            ivSpeaker.setOnClickListener(v -> {
                if (!article.isSelected())
                    if (categoryCallback != null) {
                        categoryCallback.nextPositionNoScroll(position, true);
                    }
                try {
                    if (!Constants.muted) {
                        if (youTubePlayer != null) {
                            Constants.muted = true;
                            Constants.ReelMute = true;
                            youTubePlayer.mute();
                            //helper.setVolume(0f);
                            Map<String,String> params = new HashMap<>();
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
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    params,
                                    Events.UNMUTE);
                        }
                    }
                    updateMuteButtons();
                } catch (Exception ignore) {
                }
            });

//            source_name.setOnClickListener(v -> {
//                if (!article.isSelected())
//                    if (categoryCallback != null) {
//                        categoryCallback.nextPositionNoScroll(position, false);
//                    }
//                detailsPage(article);
//            });
            tvTime.setOnClickListener(v -> {
                if (!article.isSelected())
                    if (categoryCallback != null) {
                        categoryCallback.nextPositionNoScroll(position, false);
                    }
                detailsPage(article);
            });

            String time = Utils.getTimeAgo(Utils.getDate(article.getPublishTime()), context);
            if (!TextUtils.isEmpty(time)) {
                tvTime.setText(time);
            }


            final int[] mPosition = {0};
            if (article.getBullets() != null && article.getBullets().size() > 0) {
                CardBulletsAdapter bulletsAdapter = new CardBulletsAdapter(isCommunity, mCommentClick, type, isPostArticle, (Activity) context, article.getBullets(), new ListAdapterListener() {
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
                    public void clickArticle(int i) {
                        if (!isPostArticle) {
                            //Center Click
                            if (article.isSelected()) {
                                play.setVisibility(View.GONE);
                                odd_imageBack.setVisibility(View.GONE);
                                odd_image.setVisibility(View.GONE);
                                playDuration.setVisibility(View.GONE);
                                youtubeMain.setVisibility(View.VISIBLE);

                                if (!isPlaying) {
                                    if (swipeListener != null)
                                        swipeListener.swipe(true);
                                    if (youTubePlayer != null && (categoryFragment == null || categoryFragment.checkVisibility())) {
                                        YouTubePlayerUtils.loadOrCueVideo(
                                                youTubePlayer,
                                                lifecycle,
                                                article.getLink(),
                                                youtubeDuration
                                        );
                                        // youTubePlayer.loadVideo(article.getLink(), youtubeDuration);
                                        if (Constants.muted) {
                                            youTubePlayer.mute();
                                        } else {
                                            youTubePlayer.unMute();
                                        }
                                        youTubePlayer.play();
                                        isPlaying = true;
                                    } else {
                                        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                            @Override
                                            public void onReady(@NotNull YouTubePlayer initializedYouTubePlayer) {
                                                super.onReady(initializedYouTubePlayer);
                                                youTubePlayer = initializedYouTubePlayer;
                                                //youTubePlayer.loadVideo(article.getLink(), youtubeDuration);
                                                if((categoryFragment == null || categoryFragment.checkVisibility())) {
                                                    YouTubePlayerUtils.loadOrCueVideo(
                                                            youTubePlayer,
                                                            lifecycle,
                                                            article.getLink(),
                                                            youtubeDuration
                                                    );
                                                }
                                                if (Constants.muted) {
                                                    youTubePlayer.mute();
                                                } else {
                                                    youTubePlayer.unMute();
                                                }
                                                youTubePlayer.play();
                                                isPlaying = true;
                                                Log.e("articleview", "youtube");
                                            }

                                            @Override
                                            public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
                                                super.onCurrentSecond(youTubePlayer, second);
                                                if((categoryFragment != null && !categoryFragment.checkVisibility()) ){
                                                    youTubePlayer.pause();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    if (swipeListener != null)
                                        swipeListener.swipe(false);
                                    if (youTubePlayer != null)
                                        youTubePlayer.pause();
                                    isPlaying = false;
                                }
                            } else {
                                if (categoryCallback != null)
                                    categoryCallback.nextPositionNoScroll(position, true);
                            }
                        }
                    }
                }, article, position);
                LinearLayoutManager manager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);


                if (article.isSelected()) {
                    bulletsAdapterMain = bulletsAdapter;

                    if (gotoChannelListener != null) {
                        gotoChannelListener.onArticleSelected(article);
                    }

                    if (goHomeMainActivity != null)
                        goHomeMainActivity.sendAudioEvent("stop_destroy");

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
                                if (bindArticle.isSelected() && shouldLoad && (categoryFragment == null || categoryFragment.checkVisibility())) {
                                    isPlaying = true;
                                    YouTubePlayerUtils.loadOrCueVideo(
                                            youTubePlayer,
                                            lifecycle,
                                            bindArticle.getLink(),
                                            0
                                    );
                                    //youTubePlayer.loadVideo(bindArticle.getLink(), youtubeDuration);
                                } else
                                    youTubePlayer.cueVideo(bindArticle.getLink(), youtubeDuration);
                            }
                        } else {
                            youTubePlayer.cueVideo(bindArticle.getLink(), youtubeDuration);
                        }
                    }
                } else {
                    youtubeDuration = 0;
                    progressAudio.setVisibility(View.GONE);
                    play.setVisibility(View.GONE);
                    if (youTubePlayer != null) {
                        youTubePlayer.pause();
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
        if (categoryCallback != null) {
            int pos = adapterCallback.getArticlePosition();
            pos++;
            if (categoryCallback != null) {
                categoryCallback.nextPosition(pos);
            }
        }
        if (youTubePlayer != null) {
            youTubePlayer.pause();
//            youTubePlayer.mute();
        }
        isPlaying = false;
    }


    public void bulletResume() {
        if (context != null) {
            if (isPlaying) {
                if (youTubePlayer != null) {
                    if (Constants.muted) {
                        youTubePlayer.mute();
                    } else {
                        youTubePlayer.unMute();
                    }
                    if (youtubeDuration > 0)
                        youTubePlayer.seekTo(youtubeDuration);
                    youTubePlayer.play();
                }
            }
            shouldLoad = true;
            isPlaying = false;
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("resume");
        }
    }

    public void youtubeResume() {
        shouldLoad = true;
        if (context != null) {
            if (youTubePlayer != null) {
                if (Constants.muted) {
                    youTubePlayer.mute();
                } else {
                    youTubePlayer.unMute();
                }
                if (youtubeDuration > 0)
                    youTubePlayer.seekTo(youtubeDuration);

                play.setVisibility(View.GONE);
                odd_imageBack.setVisibility(View.GONE);
                odd_image.setVisibility(View.GONE);
                playDuration.setVisibility(View.GONE);
                youtubeMain.setVisibility(View.VISIBLE);
                youTubePlayer.play();
            }
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("resume");
        }
    }

    public void bulletPause() {
        if (context != null) {
            if (youTubePlayer != null) {
                youTubePlayer.pause();
            }
            isPlaying = false;
            shouldLoad = false;
            Log.d("youtubePlayer", "bulletPause: 1 shouldLoad " + shouldLoad);
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("pause");
        }
    }

    private void detailsPage(Article content) {
        if (context instanceof ChannelDetailsActivity) {
            return;
        }
        if (content != null) {
            if (gotoChannelListener != null && content.getSource() != null) {
                if (mPrefConfig != null) {
                    mPrefConfig.setSrcLang(content.getSource().getLanguage());
                    mPrefConfig.setSrcLoc(content.getSource().getCategory());
                }
                gotoChannelListener.onItemClicked(TYPE.SOURCE, content.getSource().getId(), content.getSource().getName(), content.getSource().isFavorite());
            } else if (content.getAuthor() != null && content.getAuthor().size() > 0) {
                if (TextUtils.isEmpty(type) || !type.equalsIgnoreCase("MY_ARTICLES")) {
                    if (content.getAuthor().get(0) != null) {
                        Utils.openAuthor(context, content.getAuthor().get(0));
                    }
                }
            }
        }
    }

    protected void showFlagAndAudioBtn(boolean show) {
        ivSpeaker.setVisibility(show ? View.VISIBLE : View.GONE);
        if (mCommentClick == null) {
            fullscreen.setVisibility(View.GONE);
        } else {
            fullscreen.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
