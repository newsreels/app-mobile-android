package com.ziro.bullet.adapters.feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.makeramen.roundedimageview.RoundedImageView;
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
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.fragments.CategoryFragment;
import com.ziro.bullet.fragments.CommentPopup;

import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.LikeInterface;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.SelectedChannel;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.storyMaker.HorizontalStoriesProgressView;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class YoutubeViewHolder extends RecyclerView.ViewHolder {

    private static final int MIN_DISTANCE = 150;
    private static final String TAG = "youtubePlayer";
    private final LikePresenter presenter;
    public HorizontalStoriesProgressView storiesProgressView;
    public TextView comment_count;
    public RelativeLayout statusView;
    public TextView like_count;
    public ImageView like_icon;
    public long youtubeDuration = 0;
    private float Y_BUFFER = 10;
    private float preX = 0f;
    private float preY = 0f;
    private RoundedImageView profile;
    private ImageView odd_imageBack;
    private ImageView odd_image;
    private TextView source_name;
    private TextView authorName;
    private ImageView separatorDot;
    private TextView tvTime;
    private ImageView dots;
    public LinearLayout share;
    private RecyclerView desc_list;
    private ProgressBar progressAudio;
    private FrameLayout youtubeMain;
    private YouTubePlayerView youTubePlayerView;
    private ImageView play;
    private ImageView fullscreen;
    private TextView playDuration;
    private ImageView equilizer;
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
    private RelativeLayout commentMain;
    private ImageView comment_icon;
    private RelativeLayout likeMain;
    private CommentPopup commentPopup;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private Article bindArticle;
    private boolean isPostArticle = false;
    private String type;
    private CommentClick mCommentClick;
    private ImageView statusIcon;
    private ImageView ivSpeaker;
    private LottieAnimationView statusAnimation;
    private TextView text1, text2;
    private boolean isCommunity;
    private Lifecycle lifecycle;
    public CategoryFragment categoryFragment;
    public ImageView follow;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private RelativeLayout rootCard;

    public YoutubeViewHolder(boolean isCommunity, CommentClick mCommentClick, String type, boolean isPostArticle, @NonNull View itemView, AppCompatActivity context,
                             AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity,
                             ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener,
                             NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener,
                             DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback,
                             Lifecycle lifecycle) {
        super(itemView);
        this.presenter = new LikePresenter(context);
        this.followUnfollowPresenter = new FollowUnfollowPresenter(context);
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
        rootCard = itemView.findViewById(R.id.rootCard);
        follow = itemView.findViewById(R.id.follow);
        play = itemView.findViewById(R.id.play);
        playDuration = itemView.findViewById(R.id.play_duration);
        youtubeMain = itemView.findViewById(R.id.youtubeMain);
        equilizer = itemView.findViewById(R.id.equilizer);
        youTubePlayerView = itemView.findViewById(R.id.youtube_view);
        progressAudio = itemView.findViewById(R.id.progressAudio);
        odd_image = itemView.findViewById(R.id.odd_image);
        odd_imageBack = itemView.findViewById(R.id.odd_imageBack);
        profile = itemView.findViewById(R.id.profile);
        desc_list = itemView.findViewById(R.id.desc_list);
        tvTime = itemView.findViewById(R.id.time);
        source_name = itemView.findViewById(R.id.source_name);
        authorName = itemView.findViewById(R.id.author_name);
        separatorDot = itemView.findViewById(R.id.separator_dot);
        dots = itemView.findViewById(R.id.dotImg);
        share = itemView.findViewById(R.id.share);
        storiesProgressView = itemView.findViewById(R.id.stories);
        comment_icon = itemView.findViewById(R.id.comment_icon);
        comment_count = itemView.findViewById(R.id.comment_count);
        commentMain = itemView.findViewById(R.id.commentMain);
        like_icon = itemView.findViewById(R.id.like_icon);
        like_count = itemView.findViewById(R.id.like_count);
        likeMain = itemView.findViewById(R.id.likeMain);
        statusView = itemView.findViewById(R.id.statusView);
        statusIcon = itemView.findViewById(R.id.statusIcon);
        statusAnimation = itemView.findViewById(R.id.statusAnimation);
        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
        ivSpeaker = itemView.findViewById(R.id.speaker);
        fullscreen = itemView.findViewById(R.id.fullscreen);
        statusAnimation.setAnimation(Utils.getProcessingForTheme(mPrefConfig.getAppTheme()));

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
                if (playerState == PlayerConstants.PlayerState.ENDED && !isPostArticle) {
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, bindArticle.getId());
                    AnalyticsEvents.INSTANCE.logEventWithAPI(context,
                            params,
                            Events.ARTICLE_VIDEO_COMPLETE);
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
            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {
                youtubeDuration = (long) v;
                if ((categoryFragment != null && !categoryFragment.checkVisibility())) {
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

    public void showFullScreenVideo(int mOrientation, int pos) {
        if (mCommentClick != null)
            mCommentClick.fullscreen(pos, bindArticle, youtubeDuration, "youtube", false);
    }

    public void checkArticleStatus(Article article) {
        if (!TextUtils.isEmpty(article.getStatus())) {
            switch (article.getStatus()) {
                case Constants.ARTICLE_PROCESSING:
                    statusView.setVisibility(View.VISIBLE);
                    statusAnimation.setVisibility(View.VISIBLE);
                    statusIcon.setVisibility(View.GONE);
                    text1.setText(context.getString(R.string.processing));
                    text2.setVisibility(View.GONE);
                    break;
                case Constants.ARTICLE_SCHEDULED:
                    statusView.setVisibility(View.VISIBLE);
                    text1.setText(context.getString(R.string.scheduled_on));
                    text2.setVisibility(View.VISIBLE);
                    statusAnimation.setVisibility(View.GONE);
                    statusIcon.setVisibility(View.VISIBLE);
                    Date publishDate = Utils.getDate(article.getPublishTime());
                    boolean sameYear = Utils.isSameYear(publishDate);
                    String pattern = "";
                    if (sameYear) {
                        pattern = "HH:mm a - MMM d";
                    } else {
                        pattern = "HH:mm a - MMM d yyyy";
                    }
                    String time = Utils.getCustomDate(article.getPublishTime(), pattern);
                    if (!TextUtils.isEmpty(time)) {
                        text2.setText(time);
                    }
                    break;
                default:
                    statusView.setVisibility(View.GONE);
            }
        } else {
            statusView.setVisibility(View.GONE);
        }
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
        authorName.setTextColor(context.getResources().getColor(R.color.large_time));
    }

    public void forReelBottomSheet() {
        updateMuteButtons();
        source_name.setTextColor(context.getResources().getColor(R.color.black));
        authorName.setTextColor(context.getResources().getColor(R.color.grey_33));
    }

    public void bind(int position, Article article) {
        if (article != null) {
            rootCard.setBackgroundColor(context.getResources().getColor(R.color.card_bg));

            bindArticle = article;
            checkArticleStatus(article);
            youtubeLink = article.getLink();
            shouldLoad = true;
            storiesProgressView.pause();
            storiesProgressView.setStoriesCount(1);
            storiesProgressView.getLayoutParams().height = 0;
            storiesProgressView.requestLayout();
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
                if (!TextUtils.isEmpty(article.getSourceImageToDisplay())) {
                    Picasso.get()
                            .load(article.getSourceImageToDisplay())
                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .transform(new CropCircleTransformation())
                            .into(profile);
                }

                if (!article.getAuthorNameToDisplay().equals("")) {
                    authorName.setVisibility(View.VISIBLE);
                    separatorDot.setVisibility(View.VISIBLE);
                    authorName.setText(context.getString(R.string.by) + " " + article.getAuthorNameToDisplay());
                } else {
                    authorName.setVisibility(View.GONE);
                    separatorDot.setVisibility(View.GONE);
                    authorName.setText(context.getString(R.string.by) + " " + article.getSourceNameToDisplay());
                }

                if (article.getSource() != null) {
                    if (article.getSource().isFavorite()) {
                        follow.setVisibility(View.GONE);
                    } else {
                        follow.setVisibility(View.VISIBLE);
                    }
                    follow.setOnClickListener(view -> followUnfollowPresenter.followSource(article.getSource().getId(), position, (position1, flag) -> {
                        if (flag) follow.setVisibility(View.GONE);
                        else follow.setVisibility(View.VISIBLE);
                    }));
                }

                commentMain.setOnClickListener(v -> {
                    if (mCommentClick == null) return;
                    mCommentClick.commentClick(position, article.getId());
//                Intent intent = new Intent(context, CommentsActivity.class);
//                intent.putExtra("article_id", article.getId());
//                context.startActivityForResult(intent, Constants.CommentsRequestCode);
                });

                if (article.getInfo() != null) {
                    comment_count.setText("" + article.getInfo().getComment_count());
                    like_count.setText("" + article.getInfo().getLike_count());

                    if (article.getInfo().isLiked()) {
                        like_icon.setImageResource(R.drawable.ic_reel_like_active);
                        like_icon.setColorFilter(ContextCompat.getColor(context, R.color.theme_color_1), android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {
                        like_icon.setImageResource(R.drawable.ic_reel_like_inactive);
                        like_icon.setColorFilter(ContextCompat.getColor(context, R.color.greyad), android.graphics.PorterDuff.Mode.SRC_IN);
                    }

                    likeMain.setOnClickListener(v -> {
                        if (!article.isSelected())
                            if (categoryCallback != null) {
                                categoryCallback.nextPositionNoScroll(position, true);
                            }
                        likeMain.setEnabled(false);
                        presenter.like(article.getId(), new LikeInterface() {
                            @Override
                            public void success(boolean like) {
                                if (context == null) return;
                                likeMain.setEnabled(true);
                                article.getInfo().setLiked(like);
                                int counter = article.getInfo().getLike_count();
                                if (like) {
                                    counter++;
                                } else {
                                    if (counter > 0) {
                                        counter--;
                                    } else {
                                        counter = 0;
                                    }
                                }
                                article.getInfo().setLike_count(counter);
                                like_count.setText("" + counter);

                                if (article.getInfo().isLiked()) {
                                    like_icon.setImageResource(R.drawable.ic_reel_like_active);
                                    like_icon.setColorFilter(ContextCompat.getColor(context, R.color.theme_color_1), android.graphics.PorterDuff.Mode.SRC_IN);
                                } else {
                                    like_icon.setImageResource(R.drawable.ic_reel_like_inactive);
                                    like_icon.setColorFilter(ContextCompat.getColor(context, R.color.greyad), android.graphics.PorterDuff.Mode.SRC_IN);
                                }
                            }

                            @Override
                            public void failure() {
                                likeMain.setEnabled(true);
                            }
                        }, !article.getInfo().isLiked());
                    });
                }

                share.setOnClickListener(view -> {
                    if (article == null) return;
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.SHARE_CLICK);
//                    bulletPause();
                    if (shareBottomSheetPresenter != null) {
                        showOptionsLoaderCallback.showLoader(true);
                        shareBottomSheetPresenter.share_msg(article.getId(), new ShareInfoInterface() {
                            @Override
                            public void response(ShareInfo shareInfo) {
                                showOptionsLoaderCallback.showLoader(false);

                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                if (shareInfo != null) {
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfo.getShare_message());
                                }
                                sendIntent.setType("text/plain");
                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                context.startActivity(shareIntent);
                            }

                            @Override
                            public void error(String error) {
                                showOptionsLoaderCallback.showLoader(false);
                            }
                        });
                    }
                });
                dots.setOnClickListener(v -> {
                    if (!article.isSelected())
                        if (categoryCallback != null) {
                            categoryCallback.nextPositionNoScroll(position, false);
                        }
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.SHARE_CLICK);
                    bulletPause();

                    if (shareBottomSheetPresenter != null) {
                        showOptionsLoaderCallback.showLoader(true);
                        shareBottomSheetPresenter.share_msg(article.getId(), new ShareInfoInterface() {
                            @Override
                            public void response(ShareInfo shareInfo) {
                                showOptionsLoaderCallback.showLoader(false);
                                adapterCallback.showShareBottomSheet(shareInfo, article, dialog -> bulletResume());
                            }

                            @Override
                            public void error(String error) {
                                showOptionsLoaderCallback.showLoader(false);
                                adapterCallback.showShareBottomSheet(null, article, dialog -> bulletResume());
                            }
                        });
                    }
                });
            } else {
                // for new post article
                String name = null, image = null;
                if (mPrefConfig != null) {
                    if (mPrefConfig.selectedChannel() != null && !TextUtils.isEmpty(mPrefConfig.selectedChannel().getName()) &&
                            !mPrefConfig.selectedChannel().getName().equalsIgnoreCase(context.getString(R.string.my_profile))) {
                        SelectedChannel channel = mPrefConfig.selectedChannel();
                        name = channel.getName();
                        image = channel.getImage();
                        authorName.setVisibility(View.VISIBLE);
                        separatorDot.setVisibility(View.VISIBLE);
                        if (mPrefConfig.isUserObject() != null) {
                            authorName.setText(mPrefConfig.isUserObject().getFirst_name() + " " + mPrefConfig.isUserObject().getLast_name());
                        }
                    } else {
                        authorName.setVisibility(View.GONE);
                        separatorDot.setVisibility(View.GONE);
                        if (mPrefConfig.isUserObject() != null) {
                            image = mPrefConfig.isUserObject().getProfile_image();
                            name = mPrefConfig.isUserObject().getFirst_name() + " " + mPrefConfig.isUserObject().getLast_name();
                        }
                    }
                    source_name.setText(name);
                    if (!TextUtils.isEmpty(image)) {
                        Picasso.get()
                                .load(image)
                                .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                                .transform(new CropCircleTransformation())
                                .into(profile);
                    } else {
                        profile.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                    }
                }
                tvTime.setText(R.string.today);
                like_count.setText("0");
                comment_count.setText("0");
            }

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
                            Map<String, String> params = new HashMap<>();
                            params.put(Events.KEYS.ARTICLE_ID, article.getId());
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
                            Map<String, String> params = new HashMap<>();
                            params.put(Events.KEYS.ARTICLE_ID, article.getId());
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    params,
                                    Events.UNMUTE);
                        }
                    }
                    updateMuteButtons();
                } catch (Exception ignore) {
                }
            });

            profile.setOnClickListener(v -> {
                if (!article.isSelected())
                    if (categoryCallback != null) {
                        categoryCallback.nextPositionNoScroll(position, false);
                    }
                detailsPage(article);
            });
            source_name.setOnClickListener(v -> {
                if (!article.isSelected())
                    if (categoryCallback != null) {
                        categoryCallback.nextPositionNoScroll(position, false);
                    }
                detailsPage(article);
            });
            authorName.setOnClickListener(v -> {
                if (!article.isSelected())
                    if (categoryCallback != null) {
                        categoryCallback.nextPositionNoScroll(position, false);
                    }
                if (!TextUtils.isEmpty(type) && !type.equalsIgnoreCase("MY_ARTICLES")) {
                    if (article.getAuthor().get(0) != null) {
                        Utils.openAuthor(context, article.getAuthor().get(0));
                    }
                }
            });
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
                            if (!InternetCheckHelper.isConnected()) {
                                Toast.makeText(context, context.getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                                return;
                            }
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
                                                if ((categoryFragment == null || categoryFragment.checkVisibility())) {
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
                                                if ((categoryFragment != null && !categoryFragment.checkVisibility())) {
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
                desc_list.setLayoutManager(manager);
//                                desc_list.setHasFixedSize(true);
                desc_list.setAdapter(bulletsAdapter);
                desc_list.setOnFlingListener(null);
                PagerSnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(desc_list);
                if (!isPostArticle) {
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
                }

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

                    if (gotoChannelListener != null) {
                        gotoChannelListener.onArticleSelected(article);
                    }

                    if (goHomeMainActivity != null)
                        goHomeMainActivity.sendAudioEvent("stop_destroy");
                    storiesProgressView.setStoriesListener(null);
                    storiesProgressView.destroy();
                    storiesProgressView.setVisibility(View.INVISIBLE);
                    //commentMain.setVisibility(View.VISIBLE);
                    //likeMain.setVisibility(View.VISIBLE);
                    progressAudio.setVisibility(View.GONE);
                    equilizer.setVisibility(View.GONE);

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

                    equilizer.setImageResource(R.drawable.static_equilizer);
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
                    storiesProgressView.setStoriesListener(null);
                    storiesProgressView.destroy();
                    storiesProgressView.setVisibility(View.INVISIBLE);
                    //commentMain.setVisibility(View.INVISIBLE);
                    //likeMain.setVisibility(View.INVISIBLE);
                    desc_list.scrollToPosition(article.getLastPosition());
                    progressAudio.setVisibility(View.GONE);
                    equilizer.setVisibility(View.GONE);
                    equilizer.setImageResource(R.drawable.static_equilizer);
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
            storiesProgressView.resume();
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
            storiesProgressView.resume();
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
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("resume");
        }
    }

    public void bulletPause() {
        if (context != null) {
            storiesProgressView.pause();
            if (youTubePlayer != null) {
                youTubePlayer.pause();
//                youTubePlayer.mute();
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
