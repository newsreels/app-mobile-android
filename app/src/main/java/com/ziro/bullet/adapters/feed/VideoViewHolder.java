package com.ziro.bullet.adapters.feed;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.Helper.PopUpClass;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.VideoPreviewActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.LikeInterface;
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
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class VideoViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, ToroPlayer.EventListener, ToroPlayer.OnErrorListener {
    private static final String TAG = "VideoViewHolder";
    public FrameLayout videoMain;
    public PlayerView playerView;
    public FrameLayout image_card;
    public RelativeLayout cardMain;
    public TextView comment_count;
    public TextView like_count;
    public ImageView like_icon;
    public boolean isHeightSet = false;
    public boolean isPostArticle = false;
    public RelativeLayout statusView;
    private float Y_BUFFER = 10;
    private float preX = 0f;
    private float preY = 0f;
    private RoundedImageView profile;
    private ImageView odd_imageBack;
    private ImageView odd_image;
    private ImageView ivSpeaker;
    private ImageView play_image;
    private ImageView fullscreen;
    private TextView source_name;
    private TextView authorName;
    private ImageView separatorDot;
    private TextView tvTime;
    private ImageView dots;
    private ShareToMainInterface shareToMainInterface;
    private TempCategorySwipeListener swipeListener;
    private GoHome goHomeMainActivity;
    private OnGotoChannelListener gotoChannelListener;
    private NewsCallback categoryCallback;
    private DetailsActivityInterface detailsActivityInterface;
    private AdapterCallback adapterCallback;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private PrefConfig mPrefConfig;
    private Activity context;
    private RelativeLayout commentMain;
    private ImageView comment_icon;
    private RelativeLayout likeMain;
    private TextView bulletHeadline;
    private TextView playDuration;
    private ExoPlayerViewHelper helper;
    @Nullable
    private Uri mediaUri;
    private LikePresenter presenter;
    private Article currentPlayingArticle;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private String type;
    private CommentClick mCommentClick;
    private ImageView statusIcon;
    private LottieAnimationView statusAnimation;
    private TextView text1, text2;
    private ProgressBar progressBar;
    private PopUpClass popUpClass;
    private ImageView equilizer;
    private boolean isManual;
    public ImageView ivFollow;
    private FollowUnfollowPresenter followUnfollowPresenter;
    //    private RelativeLayout rootCard;
    private LinearLayout share;


    public VideoViewHolder(CommentClick mCommentClick, String type, boolean isPostArticle, @NonNull View itemView, Activity context,
                           AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity,
                           ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener,
                           NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener,
                           DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(itemView);
        this.popUpClass = new PopUpClass();
        this.presenter = new LikePresenter(context);
        this.followUnfollowPresenter = new FollowUnfollowPresenter(context);
        this.shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
        this.type = type;
        this.mCommentClick = mCommentClick;
        this.swipeListener = swipeListener;
        this.adapterCallback = adapterCallback;
        this.context = context;
        this.isPostArticle = isPostArticle;
        this.goHomeMainActivity = goHomeMainActivity;
        this.shareToMainInterface = shareToMainInterface;
        this.gotoChannelListener = gotoChannelListener;
        this.categoryCallback = categoryCallback;
        this.detailsActivityInterface = detailsActivityInterface;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;

        mPrefConfig = config;

        share = itemView.findViewById(R.id.share);
//        rootCard = itemView.findViewById(R.id.rootCard);
        cardMain = itemView.findViewById(R.id.cardMain);
        ivFollow = itemView.findViewById(R.id.follow);
        bulletHeadline = itemView.findViewById(R.id.bulletHeadline);
        image_card = itemView.findViewById(R.id.image_card);
        odd_image = itemView.findViewById(R.id.odd_image);
        odd_imageBack = itemView.findViewById(R.id.odd_imageBack);
        profile = itemView.findViewById(R.id.profile);
        videoMain = itemView.findViewById(R.id.videoMain);
        tvTime = itemView.findViewById(R.id.time);
        source_name = itemView.findViewById(R.id.source_name);
        authorName = itemView.findViewById(R.id.author_name);
        separatorDot = itemView.findViewById(R.id.separator_dot);
        dots = itemView.findViewById(R.id.dotImg);
        playerView = itemView.findViewById(R.id.video_player);
        ivSpeaker = itemView.findViewById(R.id.speaker);
        fullscreen = itemView.findViewById(R.id.fullscreen);
        comment_icon = itemView.findViewById(R.id.comment_icon);
        comment_count = itemView.findViewById(R.id.comment_count);
        commentMain = itemView.findViewById(R.id.commentMain);
        like_icon = itemView.findViewById(R.id.like_icon);
        like_count = itemView.findViewById(R.id.like_count);
        likeMain = itemView.findViewById(R.id.likeMain);
        playDuration = itemView.findViewById(R.id.play_duration);
        statusView = itemView.findViewById(R.id.statusView);
        statusIcon = itemView.findViewById(R.id.statusIcon);
        statusAnimation = itemView.findViewById(R.id.statusAnimation);
        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
        play_image = itemView.findViewById(R.id.play_image);
        progressBar = itemView.findViewById(R.id.progressBar);
        equilizer = itemView.findViewById(R.id.equilizer);
        statusAnimation.setAnimation(Utils.getProcessingForTheme(mPrefConfig.getAppTheme()));

        popUpClass.setOnDismissListener(() -> updateMuteButtons());
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

    private void resizeVideoView(double mVideoHeight, double mVideoWidth) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int cardWidth = displayMetrics.widthPixels - (2 * context.getResources().getDimensionPixelSize(R.dimen._15sdp));

        int screenHeight;
        if (TextUtils.isEmpty(type))
            screenHeight = displayMetrics.heightPixels - (context.getResources().getDimensionPixelSize(R.dimen._150sdp));
        else
            screenHeight = displayMetrics.heightPixels - (context.getResources().getDimensionPixelSize(R.dimen._220sdp));
        int cardHeight = (int) ((mVideoHeight * cardWidth) / mVideoWidth);
        if (cardHeight > screenHeight) {
            cardHeight = screenHeight;
        }
        if (!isHeightSet && cardHeight > 0 && cardMain.getHeight() < cardHeight) {
            cardMain.getLayoutParams().height = cardHeight;
            cardMain.requestLayout();
            statusView.getLayoutParams().height = cardHeight + (context.getResources().getDimensionPixelSize(R.dimen._100sdp));
            statusView.requestLayout();
            isHeightSet = true;
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
    }

    public void seekTo(long duration) {
        if (playerView != null && playerView.getPlayer() != null) {
            playerView.getPlayer().seekTo(duration);
        }
    }

    public void invalidate() {
        updateMuteButtons();
        fullscreen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fullscreen_icon));
        bulletHeadline.setTextColor(context.getResources().getColor(R.color.bullet_text));
        source_name.setTextColor(context.getResources().getColor(R.color.large_source_name));
        authorName.setTextColor(context.getResources().getColor(R.color.large_time));
    }

    public void forReelBottomSheet() {
        updateMuteButtons();
        fullscreen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fullscreen_icon));
        bulletHeadline.setTextColor(context.getResources().getColor(R.color.black));
        source_name.setTextColor(context.getResources().getColor(R.color.black));
        authorName.setTextColor(context.getResources().getColor(R.color.grey_33));
    }

    public void bind(int position, Article article) {

        if (article != null) {
//            rootCard.setBackgroundColor(context.getResources().getColor(R.color.card_bg));
            currentPlayingArticle = article;
            checkArticleStatus(article);
            playDuration.setVisibility(View.GONE);

            bulletHeadline.setTextDirection(Utils.getLanguageDirectionForView(article.getLanguageCode()));

            if (article.getMediaMeta() != null) {
                playDuration.setVisibility(View.VISIBLE);
                playDuration.setText(article.getMediaMeta().getDurationString());
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
                if (article.getSource() != null && ivFollow != null) {
                    if (article.getSource().isFavorite()) {
                        ivFollow.setVisibility(View.GONE);
                    } else {
                        ivFollow.setVisibility(View.VISIBLE);
                    }
                    ivFollow.setOnClickListener(view -> followUnfollowPresenter.followSource(article.getSource().getId(), position, (position1, flag) -> {
                        if (flag) ivFollow.setVisibility(View.GONE);
                        else ivFollow.setVisibility(View.VISIBLE);
                    }));
                }
                commentMain.setOnClickListener(v -> {
                    if (mCommentClick == null) return;
                    mCommentClick.commentClick(position, article.getId());
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
                        like_icon.setEnabled(false);
                        presenter.like(article.getId(), new LikeInterface() {
                            @Override
                            public void success(boolean like) {
                                if (context == null) return;
                                like_icon.setEnabled(true);
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
                                like_icon.setEnabled(true);
                            }
                        }, !article.getInfo().isLiked());
                    });
                }
                if (share != null)
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
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            Events.SHARE_CLICK);
                    bulletPause();

                    if (shareBottomSheetPresenter != null) {
                        showOptionsLoaderCallback.showLoader(true);
                        shareBottomSheetPresenter.share_msg(article.getId(), new ShareInfoInterface() {
                            @Override
                            public void response(ShareInfo shareInfo) {
                                showOptionsLoaderCallback.showLoader(false);
                                adapterCallback.showShareBottomSheet(shareInfo, article, dialog -> {
                                    bulletResume();
                                });
                            }

                            @Override
                            public void error(String error) {
                                showOptionsLoaderCallback.showLoader(false);
                                adapterCallback.showShareBottomSheet(null, article, dialog -> {
                                    bulletResume();
                                });
                            }
                        });
                    }
                });
                profile.setOnClickListener(v -> {
                    detailsPage(article);
                });
                source_name.setOnClickListener(v -> {
                    detailsPage(article);
                });
                authorName.setOnClickListener(v -> {
                    if (TextUtils.isEmpty(type) || !type.equalsIgnoreCase("MY_ARTICLES")) {
                        if (article.getAuthor().get(0) != null) {
                            Utils.openAuthor(context, article.getAuthor().get(0));
                        }
                    }
                });
                tvTime.setOnClickListener(v -> {
                    detailsPage(article);
                });

                bulletHeadline.setOnClickListener(v -> {
                    if (!article.isSelected())
                        if (categoryCallback != null)
                            categoryCallback.nextPositionNoScroll(position, false);
                    if (mCommentClick != null)
                        mCommentClick.onDetailClick(position, article);
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

            image_card.setOnClickListener(v -> {
                if (isPostArticle) {
                    if (article.getVideoInfo() != null) {
                        Intent intent = new Intent(context, VideoPreviewActivity.class);
                        intent.putExtra("video_info", article.getVideoInfo());
                        context.startActivity(intent);
                    }
                } else {
                    if (!InternetCheckHelper.isConnected()) {
                        Toast.makeText(context, context.getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!article.isSelected()) {
                        if (adapterCallback != null)
                            adapterCallback.onItemClick(position, false);
                    }
                    if (!isPlaying()) {
                        playVideo();
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    play_image.setVisibility(View.GONE);
                    playDuration.setVisibility(View.GONE);
//                ivSpeaker.setVisibility(View.VISIBLE);
                    updateMuteButtons();
                }
            });

            ivSpeaker.setOnClickListener(v -> {
                try {
                    if (!Constants.muted) {
                        Constants.muted = true;
//                        Constants.ReelMute = true;
                        helper.setVolume(0f);
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                Events.MUTE_VIDEO_FEED);
                    } else {
                        Constants.muted = false;
//                        Constants.ReelMute = false;
                        helper.setVolume(1f);
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                Events.UNMUTE);
                    }
                    updateMuteButtons();
                } catch (Exception ignore) {
                }
            });

            fullscreen.setOnClickListener(v -> {
                showFullScreenVideo(0, true);

                //Toast.makeText(context, "Show popup here", Toast.LENGTH_SHORT).show();
                // mCommentClick.fullscreen(position, article, playerView.getPlayer().getContentPosition(), "video");
            });

            String time = Utils.getTimeAgo(Utils.getDate(article.getPublishTime()), context);
            if (!TextUtils.isEmpty(time)) {
                tvTime.setText(time);
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

            if (!isPostArticle) {
                if (!TextUtils.isEmpty(article.getImage())) {
                    loadThumbnail(article.getImage());
                } else if (!TextUtils.isEmpty(article.getLink())) {
                    loadGlide(article.getLink());
                } else {
                    odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }
            } else {
                if (!TextUtils.isEmpty(article.getLink())) {
                    loadGlide(article.getLink());
                } else if (!TextUtils.isEmpty(article.getImage())) {
                    loadThumbnail(article.getImage());
                } else {
                    odd_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }
            }

            if (!TextUtils.isEmpty(article.getTitle())) {
                bulletHeadline.setText(article.getTitle());
            }

            if (!isPostArticle)
                mediaUri = Uri.parse(article.getLink());

            if (article.isSelected()) {

                if (gotoChannelListener != null) {
                    gotoChannelListener.onArticleSelected(article);
                }

                if (goHomeMainActivity != null)
                    goHomeMainActivity.sendAudioEvent("stop_destroy");
//                ivSpeaker.setVisibility(View.VISIBLE);

                if (!isPostArticle) {
                    if (mPrefConfig.isVideoAutoPlay()) {
                        progressBar.setVisibility(View.VISIBLE);
                        play_image.setVisibility(View.GONE);
                        image_card.setVisibility(View.VISIBLE);
                        playDuration.setVisibility(View.GONE);
                    } else {
                        play_image.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        image_card.setVisibility(View.VISIBLE);
                        playDuration.setVisibility(View.VISIBLE);
                    }
                } else {
                    play_image.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    image_card.setVisibility(View.VISIBLE);
                    playDuration.setVisibility(View.GONE);
                }
                updateMuteButtons();
            } else {

                if (helper != null)
                    helper.pause();
//                ivSpeaker.setVisibility(View.GONE);
                image_card.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                playDuration.setVisibility(View.VISIBLE);
                play_image.setVisibility(View.VISIBLE);
            }
        }

        playerView.setControllerVisibilityListener(visibility -> showFlagAndAudioBtn(visibility == View.VISIBLE));
    }

    public void showFullScreenVideo(int mOrientation, boolean isManual) {
        if (fullscreen != null) {
            if (playerView != null && playerView.getPlayer() != null
                    && (playerView.getPlayer().getPlaybackState() != Player.STATE_IDLE && playerView.getPlayer().getPlaybackState() != Player.STATE_ENDED)) {
                if (!popUpClass.isShowing()) {
                    this.isManual = isManual;
                    popUpClass.showPopupWindow(fullscreen, playerView, context, mOrientation);
                }
            }
        }
    }

    public boolean isManual() {
        return isManual;
    }

    public PopUpClass getPopUpClass() {
        return popUpClass;
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
        if (!Constants.muted) {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_unmute);
        } else {
            ivSpeaker.setImageResource(R.drawable.ic_speaker_mute);
        }
        equilizerControl();
    }

    private void equilizerControl() {
        if (equilizer != null) {
            if (!Constants.muted && isPlaying()) {
                Glide.with(BulletApp.getInstance()).load(R.raw.equlizer).into(equilizer);
            } else {
                equilizer.setImageResource(R.drawable.static_equilizer);
            }
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
//            playVideo();
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("resume");
        }
    }

    public void bulletPause() {
        if (context != null) {
            if (detailsActivityInterface != null) {
                detailsActivityInterface.pause();
            }
//            pause();
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("pause");
        }
    }

    private void detailsPage(Article content) {
        if (context instanceof ChannelDetailsActivity) {
            return;
        }
        if (content != null) {
//            if (isGotoFollowShow && !type.equalsIgnoreCase("ARCHIVE")) {
            if (gotoChannelListener != null && content.getSource() != null) {
                if (mPrefConfig != null) {
                    mPrefConfig.setSrcLang(content.getSource().getLanguage());
                    mPrefConfig.setSrcLoc(content.getSource().getCategory());
                }
                gotoChannelListener.onItemClicked(TYPE.SOURCE, content.getSource().getId(), content.getSource().getName(), content.getSource().isFavorite());
//                }
            } else if (content.getAuthor() != null && content.getAuthor().size() > 0) {
                if (TextUtils.isEmpty(type) || !type.equalsIgnoreCase("MY_ARTICLES")) {
                    if (content.getAuthor().get(0) != null) {
                        Utils.openAuthor(context, content.getAuthor().get(0));
                    }
                }
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
        equilizerControl();
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
        image_card.setVisibility(View.GONE);
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
        equilizerControl();
    }

    @Override
    public void onPaused() {
        equilizerControl();
        Log.d(TAG, "onPaused: ");

        if (currentPlayingArticle != null && playerView != null && playerView.getPlayer() != null) {
            if (currentPlayingArticle.isSelected() &&
                    playerView.getPlayer().getCurrentPosition() > 1000) {
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID, currentPlayingArticle.getId());
                params.put(Events.KEYS.DURATION, String.valueOf(playerView.getPlayer().getCurrentPosition()));
                AnalyticsEvents.INSTANCE.logEventWithAPI(context,
                        params,
                        Events.ARTICLE_DURATION);
                AnalyticsEvents.INSTANCE.logEventWithAPI(context,
                        params,
                        Events.FOLLOW_SOURCE);
            }
        }
    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "onCompleted: ");
//        Utils.logEvent(context, currentPlayingArticle.getId(), Constants.Events.ARTICLE_VIDEO_COMPLETE);
//        nextArticle();

        if (playerView != null && playerView.getPlayer() != null) {
            playerView.getPlayer().seekTo(0);
            playerView.getPlayer().setPlayWhenReady(false);
        }
        progressBar.setVisibility(View.GONE);
        play_image.setVisibility(View.VISIBLE);
        image_card.setVisibility(View.VISIBLE);
        playDuration.setVisibility(View.VISIBLE);
        equilizerControl();
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
