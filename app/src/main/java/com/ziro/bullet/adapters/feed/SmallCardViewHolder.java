package com.ziro.bullet.adapters.feed;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.adapters.ListBulletsAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.fragments.CommentPopup;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.HandleFlag;
import com.ziro.bullet.interfaces.LikeInterface;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.storyMaker.HorizontalStoriesProgressView;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SmallCardViewHolder extends RecyclerView.ViewHolder {
    private static final int MIN_DISTANCE = 150;
    private final float Y_BUFFER = 10;
    public HorizontalStoriesProgressView storiesProgressView;
    public TextView comment_count;
    public RelativeLayout statusView;
    public TextView like_count;
    public ImageView like_icon;
    private float preX = 0f;
    private float preY = 0f;
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
    private Activity context;
    private String direction = "";
    private TextView source_name;
    private TextView tvTime;
    private TextView dummyBulletForSize;
    //    private RelativeLayout imageSection;
    private ConstraintLayout cardData;
    private RelativeLayout container;
    private RecyclerView desc_list;
    private RelativeLayout leftArc, line;
    private ProgressBar progressAudio;
    // private ImageView signal;
    private ImageView speaker;
    private ImageView ivDots;
    private RelativeLayout commentMain;
    private ImageView comment_icon;
    private RelativeLayout likeMain;
    private ListBulletsAdapter bulletsAdapterMain;
    private RecyclerView.OnScrollListener scrollListener;
    private LikePresenter presenter;
    private CommentClick mCommentClick;
    private Handler handler = new Handler();
    private boolean swipeNextEnabled = true;
    private Runnable tapRunnable = () -> swipeNextEnabled = true;
    private CommentPopup commentPopup;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private String type;
    private ImageView statusIcon;
    private LottieAnimationView statusAnimation;
    private TextView text1, text2;
    private ImageView sourceImage;
    private LinearLayout main;
    private ImageView separatorDot;
    private ImageView equilizer;
    private View viewFill;
    private TextView authorName;
    private ImageView bulletImage;
    private CardView bullet_image_m;
    private ImageView play_image;
    private HandleFlag handleFlag;
    private boolean isCommunity;

    public SmallCardViewHolder(boolean isCommunity, CommentClick mCommentClick, String type, View itemView, Activity context, AdapterCallback adapterCallback,
                               PrefConfig config, GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface,
                               TempCategorySwipeListener swipeListener, NewsCallback categoryCallback,
                               OnGotoChannelListener gotoChannelListener,
                               DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(itemView);
        this.presenter = new LikePresenter(context);
        this.shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
        this.isCommunity = isCommunity;
        this.type = type;
        this.mCommentClick = mCommentClick;
        this.swipeListener = swipeListener;
        this.adapterCallback = adapterCallback;
        this.context = context;
        this.goHomeMainActivity = goHomeMainActivity;
        this.shareToMainInterface = shareToMainInterface;
        this.gotoChannelListener = gotoChannelListener;
        this.categoryCallback = categoryCallback;
        this.listener = detailsActivityInterface;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        mPrefConfig = config;

        dummyBulletForSize = itemView.findViewById(R.id.dummyBulletForSize);
        speaker = itemView.findViewById(R.id.speaker);
        progressAudio = itemView.findViewById(R.id.progressAudio);
        leftArc = itemView.findViewById(R.id.leftArc);
//        rightArc = itemView.findViewById(R.id.rightArc);
        desc_list = itemView.findViewById(R.id.desc_list);
        cardData = itemView.findViewById(R.id.cardData);
        tvTime = itemView.findViewById(R.id.time);
        source_name = itemView.findViewById(R.id.source_name);
        storiesProgressView = itemView.findViewById(R.id.stories);
        ivDots = itemView.findViewById(R.id.dotImg);
        comment_icon = itemView.findViewById(R.id.comment_icon);
        comment_count = itemView.findViewById(R.id.comment_count);
        commentMain = itemView.findViewById(R.id.commentMain);
        like_icon = itemView.findViewById(R.id.like_icon);
        like_count = itemView.findViewById(R.id.like_count);
        likeMain = itemView.findViewById(R.id.likeMain);
        container = itemView.findViewById(R.id.container);
        statusView = itemView.findViewById(R.id.statusView);
        statusIcon = itemView.findViewById(R.id.statusIcon);
        statusAnimation = itemView.findViewById(R.id.statusAnimation);
        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
        sourceImage = itemView.findViewById(R.id.source_image);
        main = itemView.findViewById(R.id.main);
        separatorDot = itemView.findViewById(R.id.separator_dot);
        authorName = itemView.findViewById(R.id.author_name);
        bulletImage = itemView.findViewById(R.id.bullet_image);
        bullet_image_m = itemView.findViewById(R.id.bullet_image_m);
        play_image = itemView.findViewById(R.id.play_image);
        equilizer = itemView.findViewById(R.id.equilizer);
        viewFill = itemView.findViewById(R.id.view_fill);
        line = itemView.findViewById(R.id.line);

        statusAnimation.setAnimation(Utils.getProcessingForTheme(mPrefConfig.getAppTheme()));
    }

    public void setFlagListener(HandleFlag handleFlag) {
        this.handleFlag = handleFlag;
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

    public void invalidate() {
        if (bulletsAdapterMain != null)
            bulletsAdapterMain.notifyDataSetChanged();
        source_name.setTextColor(context.getResources().getColor(R.color.large_source_name));
        authorName.setTextColor(context.getResources().getColor(R.color.large_time));
    }

    public void forReelBottomSheet() {
        updateMuteButtons();
        if (bulletsAdapterMain != null)
            bulletsAdapterMain.forReel();
        source_name.setTextColor(context.getResources().getColor(R.color.black));
        authorName.setTextColor(context.getResources().getColor(R.color.grey_33));
    }

    public void bind(int position, Article article) {
        if (article != null) {
            checkArticleStatus(article);
            cardData.setVisibility(View.VISIBLE);
//            cardData.setBackgroundColor(context.getResources().getColor(R.color.card_bg));
            commentMain.setOnClickListener(v -> {
                if (mCommentClick == null) return;
                mCommentClick.commentClick(position, article.getId());
            });

            if (mPrefConfig.isReaderMode()) {
//                line.setVisibility(View.VISIBLE);
                bullet_image_m.setVisibility(View.GONE);
                bulletImage.setVisibility(View.GONE);
                main.setVisibility(View.GONE);
            } else {
//                line.setVisibility(View.GONE);
                bullet_image_m.setVisibility(View.VISIBLE);
                bulletImage.setVisibility(View.VISIBLE);
                main.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(article.getImage())) {
                    // bulletImage
                    Glide.with(BulletApp.getInstance())
                            .load(article.getImage())
                            .override(Constants.targetWidth, Constants.targetHeight)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .thumbnail(0.5f)
//                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .into(bulletImage);
                } else {
                    bulletImage.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }
                if (!TextUtils.isEmpty(article.getType()) && play_image != null) {
                    if (article.getType().equalsIgnoreCase("video") || article.getType().equalsIgnoreCase("youtube")) {
                        play_image.setVisibility(View.VISIBLE);
                    } else {
                        play_image.setVisibility(View.GONE);
                    }
                }

                if (article.getSourceImageToDisplay() != null && !article.getSourceImageToDisplay().equals("")) {
                    Glide.with(BulletApp.getInstance())
                            .load(article.getSourceImageToDisplay())
                            .override(Constants.targetWidth, Constants.targetHeight)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(0.5f)
//                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                            .transform(new CircleCrop())
                            .into(sourceImage);
                } else {
                    Glide.with(BulletApp.getInstance())
                            .load(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .override(Constants.targetWidth, Constants.targetHeight)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(0.5f)
//                            .transform(new CircleCrop())
                            .into(sourceImage);
                }
            }

            if (!article.getAuthorNameToDisplay().equals("")) {
                authorName.setVisibility(View.VISIBLE);
                authorName.setText(context.getString(R.string.by) + " " + article.getAuthorNameToDisplay());

            } else {
                authorName.setVisibility(View.GONE);
                authorName.setText(context.getString(R.string.by) + " " + article.getSourceNameToDisplay());
            }

            if (article.getSourceNameToDisplay().isEmpty()) {
                separatorDot.setVisibility(View.GONE);
            } else separatorDot.setVisibility(View.VISIBLE);
            source_name.setText(article.getSourceNameToDisplay());

            if (article.getInfo() != null) {
                comment_count.setText("" + article.getInfo().getComment_count());
                like_count.setText("" + article.getInfo().getLike_count());

                if (article.getInfo().isLiked()) {
                    like_count.setTextColor(ContextCompat.getColor(context, R.color.like_heart_filled));
                    like_icon.setImageResource(R.drawable.ic_like_heart_filled);
                } else {
                    like_count.setTextColor(ContextCompat.getColor(context, R.color.like_disable_text_color));
                    like_icon.setImageResource(R.drawable.ic_like_heart);
                }

                likeMain.setOnClickListener(v -> {
//                    if (!article.isSelected())
//                        categoryCallback.nextPositionNoScroll(position, true);
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
                                like_count.setTextColor(ContextCompat.getColor(context, R.color.like_heart_filled));
                                like_icon.setImageResource(R.drawable.ic_like_heart_filled);
                            } else {
                                like_count.setTextColor(ContextCompat.getColor(context, R.color.like_disable_text_color));
                                like_icon.setImageResource(R.drawable.ic_like_heart);
                            }
                        }

                        @Override
                        public void failure() {
                            likeMain.setEnabled(true);
                        }
                    }, !article.getInfo().isLiked());
                });
            }

            String time = Utils.getTimeAgo(Utils.getDate(article.getPublishTime()), context);
            if (!TextUtils.isEmpty(time)) {
                tvTime.setText(time);
            }

            cardData.setOnClickListener(v -> {
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID, article.getId());
                AnalyticsEvents.INSTANCE.logEvent(context,
                        params,
                        Events.ARTICLE_OPEN);
                if (mCommentClick == null) return;
                mCommentClick.onDetailClick(position, article);
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
            sourceImage.setOnClickListener(v -> {
                detailsPage(article);
            });


            final int[] mPosition = {0};

            if (article.getBullets() != null && article.getBullets().size() > 0) {
                ListBulletsAdapter bulletsAdapter = new ListBulletsAdapter(isCommunity, type, new ListAdapterListener() {
                    @Override
                    public void verticalScrollList(boolean isEnable) {
                        if (swipeListener != null) {
                            swipeListener.swipe(isEnable);
                        }
                    }

                    @Override
                    public void nextArticle(int position) {

                    }

                    @Override
                    public void prevArticle(int position) {

                    }

                    @Override
                    public void clickArticle(int bulletPosition) {
                        Map<String, String> params = new HashMap<>();
                        params.put(Events.KEYS.ARTICLE_ID, article.getId());
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.ARTICLE_OPEN);
                        if (mCommentClick == null) return;
                        mCommentClick.onDetailClick(position, article);
                    }
                }, context, article.getBullets(), article.getType().equalsIgnoreCase("YOUTUBE"), article);

                /*bulletsAdapter.setCallback(new ListBulletsAdapter.BulletCallback() {
                    @Override
                    public void onItemClicked() {
                        if (mCommentClick != null)
                            mCommentClick.onDetailClick(position, article);
                    }

                    @Override
                    public void onPause() {
                        storiesProgressView.pause();
                        if (goHomeMainActivity != null)
                            goHomeMainActivity.sendAudioEvent("pause");
                    }

                    @Override
                    public void onResume() {
                        storiesProgressView.resume();
                        // making it again to auto scroll
                        if (storiesProgressView.getCurrent() == 0) {
                            Constants.auto_scroll = true;
                        }
                        if (goHomeMainActivity != null)
                            goHomeMainActivity.sendAudioEvent("resume");
                    }

                    @Override
                    public void onMove() {
                        if (article.isSelected()) {
                            Constants.auto_scroll = false;
                        }
                    }

                    @Override
                    public void onBottomSheetOpen() {
                    }

                    @Override
                    public void onYoutubePlay() {

                    }

                    @Override
                    public void moveTolastPosition() {

                    }

                    @Override
                    public void bulletPosition(int position) {

                    }

                    @Override
                    public void onLeft(MotionEvent event) {
                        if (bulletsAdapter.getCurrentPosition() == 0) {
                            if (position == adapterCallback.getArticlePosition()) {
                                prevArticle(position);
                            }
                        } else {
                            if (article.isSelected()) {
                                Constants.auto_scroll = false;
                            }
                            fadeInOut(leftArc);
                            prevBullet(bulletsAdapter, desc_list);
                        }
                    }

                    @Override
                    public void onRight(MotionEvent event) {
                        if (article.isSelected()) {
                            Constants.auto_scroll = false;
                        }
                        fadeInOut(rightArc);
                        if (bulletsAdapter.getCurrentPosition() < (article.getBullets().size() - 1)) {
                            nextBullet(bulletsAdapter, desc_list);
                        } else {
                            if (position == adapterCallback.getArticlePosition()) {
                                nextArticle(position);
                            }
                        }
                    }
                });*/

                LinearLayoutManager manager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                desc_list.setLayoutManager(manager);
                desc_list.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                desc_list.requestLayout();
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
                                    float deltaX = x2 - x1;
                                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                                        if (swipeNextEnabled) {
                                            swipeNextEnabled = false;
                                            handler.postDelayed(tapRunnable, 200);
                                            if (context != null) {
                                                AnalyticsEvents.INSTANCE.logEvent(context,
                                                        Events.ARTICLE_SWIPE);
                                            }
                                        }
//                                        //Utils.doHaptic(mPrefConfig);
//                                        if ((!Utils.isRTL() && x2 > x1) || (Utils.isRTL() && !(x2 > x1))) {
//                                            if (bulletsAdapterMain.getCurrentPosition() == 0 && position == adapterCallback.getArticlePosition()) {
//                                                if (swipeNextEnabled) {
//                                                    swipeNextEnabled = false;
//                                                    handler.postDelayed(tapRunnable, 200);
//                                                    prevArticle(position);
//                                                    if (scrollListener != null)
//                                                        desc_list.removeOnScrollListener(scrollListener);
//                                                }
//
//                                            }
//                                        } else {
//                                            if (bulletsAdapterMain.getCurrentPosition() == (bulletsAdapterMain.getItemCount() - 1) && position == adapterCallback.getArticlePosition()) {
//                                                if (swipeNextEnabled) {
//                                                    swipeNextEnabled = false;
//                                                    handler.postDelayed(tapRunnable, 200);
//                                                    nextArticle(position);
//                                                    if (scrollListener != null)
//                                                        desc_list.removeOnScrollListener(scrollListener);
//                                                }
//                                            }
//                                        }
                                    }
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

                speaker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Constants.mute_disable) {
                            Utils.showSnacky(speaker, context.getString(R.string.speech_not_available));
                            return;
                        }
                        if (!Constants.muted) {
                            Constants.muted = true;
                            Constants.ReelMute = true;
                            if (goHomeMainActivity != null)
                                goHomeMainActivity.sendAudioEvent("mute");
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    Events.MUTE_VIDEO_FEED);
                        } else {
                            Constants.muted = false;
                            Constants.ReelMute = false;
                            if (goHomeMainActivity != null) {
                                goHomeMainActivity.sendAudioEvent("unmute");
                                goHomeMainActivity.sendAudioEvent("play");
                            }
                            playAudio(mPosition[0], bulletsAdapter, article);
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    Events.UNMUTE);
                        }
                        updateMuteButtons();
                    }
                });

                ivDots.setOnClickListener(v -> {

                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.SHARE_CLICK);
                    if (listener != null) {
                        listener.pause();
                    }
                    if (goHomeMainActivity != null)
                        goHomeMainActivity.sendAudioEvent("pause");

                    if (shareBottomSheetPresenter != null) {
                        showOptionsLoaderCallback.showLoader(true);
                        shareBottomSheetPresenter.share_msg(article.getId(), new ShareInfoInterface() {
                            @Override
                            public void response(ShareInfo shareInfo) {
                                showOptionsLoaderCallback.showLoader(false);
                                adapterCallback.showShareBottomSheet(shareInfo, article, new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        if (listener != null) {
                                            listener.resume();
                                        }
                                        if (goHomeMainActivity != null)
                                            goHomeMainActivity.sendAudioEvent("resume");
                                    }
                                });
                            }

                            @Override
                            public void error(String error) {
                                showOptionsLoaderCallback.showLoader(false);
                                adapterCallback.showShareBottomSheet(null, article, dialog -> {
                                    if (listener != null) {
                                        listener.resume();
                                    }
                                    if (goHomeMainActivity != null)
                                        goHomeMainActivity.sendAudioEvent("resume");
                                });
                            }
                        });
                    }
                });

                desc_list.addOnScrollListener(scrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (mPosition[0] != -1) {
                                article.setLastPosition(mPosition[0]);
                                bulletsAdapter.setCurrentPosition(mPosition[0]);

                                float val = -1;
                                if (mPosition[0] == 0) {
                                    if (handleFlag != null) {
                                        handleFlag.flag(true);
                                    }
                                    val = Utils.getHeadlineDimens(mPrefConfig, context);
                                    dummyBulletForSize.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                                } else {
                                    if (handleFlag != null) {
                                        handleFlag.flag(false);
                                    }
                                    val = Utils.getBulletDimens(mPrefConfig, context);
                                    dummyBulletForSize.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                                }
                                if (val != -1) {
                                    dummyBulletForSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
                                }

                                if (article.isSelected() &&
                                        (article.getType().equalsIgnoreCase("EXTENDED") ||
                                                article.getType().equalsIgnoreCase("SIMPLE"))
                                ) {
                                    if (mPosition[0] == 0) {
                                        Constants.auto_scroll = true;
                                    } else {
                                        Constants.auto_scroll = false;
                                    }
                                    Log.e("####", "@ " + Constants.auto_scroll);
                                    playAudio(mPosition[0], bulletsAdapter, article);
                                } else {
                                    if (categoryCallback != null)
                                        categoryCallback.nextPositionNoScroll(position, false);

                                    resizeBullet(bulletsAdapter, article);
                                }
                            }
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        mPosition[0] = snapHelper.findTargetSnapPosition(manager, dx, dy);
                        if (dx > 0) {
                            direction = "rtl";
                        } else {
                            direction = "ltr";
                        }
                    }
                });

                bulletsAdapterMain = bulletsAdapter;

                if (article.isSelected() && (
                        article.getType().equalsIgnoreCase("EXTENDED") ||
                                article.getType().equalsIgnoreCase("SIMPLE")
                )) {

                    equilizer.setVisibility(View.INVISIBLE);
                    viewFill.setVisibility(View.GONE);


                    speaker.setVisibility(View.VISIBLE);
                    imageCase(position, bulletsAdapter, article);
                    Constants.mute_disable = article.isMute();
                    if (Constants.mute_disable) {
                        if (goHomeMainActivity != null)
                            goHomeMainActivity.sendAudioEvent("mute");
                    } else {
                        if (!Constants.muted) {
                            if (goHomeMainActivity != null)
                                goHomeMainActivity.sendAudioEvent("unmute");
                        } else {
                            if (goHomeMainActivity != null)
                                goHomeMainActivity.sendAudioEvent("mute");
                        }
                    }
                    updateMuteButtons();


                    if (gotoChannelListener != null) {
                        gotoChannelListener.onArticleSelected(article);
                    }

                    if (bulletsAdapter.getCurrentPosition() == 0) {

                        MessageEvent messageEvent = new MessageEvent();
                        messageEvent.setType(MessageEvent.TYPE_COUNT_API_CALL);
                        messageEvent.setStringData(article.getId());

                        EventBus.getDefault().post(messageEvent);
                    }

                } else {
                    equilizer.setVisibility(View.GONE);
                    viewFill.setVisibility(View.INVISIBLE);

                    storiesProgressView.setStoriesListener(null);
                    storiesProgressView.destroy();
//                    storiesProgressView.setVisibility(View.GONE);
                    // likeMain.setVisibility(View.INVISIBLE);
                    // commentMain.setVisibility(View.INVISIBLE);
                    desc_list.scrollToPosition(article.getLastPosition());
                    progressAudio.setVisibility(View.GONE);
                    // signal.setVisibility(View.VISIBLE);
                    speaker.setVisibility(View.GONE);
//                                        if(holder.imageSection.getVisibility() == View.GONE) {
//                                            holder.imageSection.setVisibility(View.VISIBLE);
//                                        }
                }
            }
        }
    }


    public void unselect(Article article) {
        storiesProgressView.setStoriesListener(null);
        storiesProgressView.destroy();
//        storiesProgressView.setVisibility(View.INVISIBLE);
        // likeMain.setVisibility(View.INVISIBLE);
        // commentMain.setVisibility(View.INVISIBLE);
        desc_list.scrollToPosition(article.getLastPosition());
        progressAudio.setVisibility(View.GONE);
        // signal.setVisibility(View.VISIBLE);
        speaker.setVisibility(View.GONE);
        equilizer.setVisibility(View.GONE);
        viewFill.setVisibility(View.INVISIBLE);
    }

    public void resizeBullet(ListBulletsAdapter bulletsAdapter, Article content) {
//        Log.e("resizeBullet", "------------------------------------");
//        Log.e("resizeBullet", "--> " + content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData());
//        Log.e("resizeBullet", "-position-> " + bulletsAdapter.getCurrentPosition());
        dummyBulletForSize.setText(content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData());
        dummyBulletForSize.post(() -> {
            int measureHeight = dummyBulletForSize.getMeasuredHeight();

            Log.e("resizeBullet", "------------------------------------");
            Log.e("resizeBullet", "- Dummy Text Height -> " + measureHeight);
            Log.e("resizeBullet", "- Bullet List Height -> " + desc_list.getMeasuredHeight());
            Log.e("resizeBullet", "- Condition -> " + (desc_list.getMeasuredHeight() < measureHeight));

            if (desc_list.getMeasuredHeight() <= measureHeight) {
//                setHeight(container, measureHeight);
                setHeight(desc_list, measureHeight);
                bulletsAdapter.setHeight(measureHeight);
//                bulletsAdapter.notifyItemChanged(bulletsAdapter.getCurrentPosition());
//                dummyBulletForSize.setText("");
            }
        });
    }

    public void setHeight(View view, int height) {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), height);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = val;
            view.setLayoutParams(layoutParams);
        });
        anim.setDuration(200);
        anim.start();
    }


    public void imageCase(int position, ListBulletsAdapter bulletsAdapter, Article content) {
        if (position > -1 && bulletsAdapter != null && content != null) {

            Constants.ProgressBarDrawable = R.drawable.bullet_selector_green;
            Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_green_2;

            storiesProgressView.setStoriesCount(content.getBullets().size());
            storiesProgressView.setProgressWidth(14, false);
            storiesProgressView.setStoriesListener(new HorizontalStoriesProgressView.StoriesListener() {
                @Override
                public void onFirst() {

                }

                @Override
                public void onNext() {
                    if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                        //GOTO NEXT BULLET
                        if (mPrefConfig.isBulletsAutoPlay())
                            nextBullet(bulletsAdapter, desc_list);

                    } else {
                        //Auto Swipe
                        if (Constants.auto_scroll) {
//                            Utils.logEvent(context, "list_next_auto");
//                            //GOTO NEXT ARTICLE
//                            if (mPrefConfig.isBulletsAutoPlay())
//                                nextArticle(position);
                        } else {
                            //GOTO NEXT BULLET
                            if (mPrefConfig.isBulletsAutoPlay())
                                nextBullet(bulletsAdapter, desc_list);
                        }
                    }
                }

                @Override
                public void onPrev() {
                }

                @Override
                public void onComplete() {
                    //Auto Swipe
//                    if (Constants.auto_scroll) {
//                        Utils.logEvent(context, "list_next_auto");
//                    } else {
//                        Utils.logEvent(context, "list_next_scroll");
//                    }
//                    if (mPrefConfig.isBulletsAutoPlay())
//                        nextArticle(position);
                }
            });


            Animation progressAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in_progress);
//            storiesProgressView.setVisibility(View.VISIBLE);
            // likeMain.setVisibility(View.VISIBLE);
            // commentMain.setVisibility(View.VISIBLE);
            storiesProgressView.startAnimation(progressAnimation);
            progressAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    scrollListener.onScrollStateChanged(desc_list, RecyclerView.SCROLL_STATE_IDLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private void nextArticle(int position) {
        Log.d("TAG", "nextArticle: position=  = = = " + position);
        dummyBulletForSize.setText("");
        desc_list.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        desc_list.requestLayout();
        Constants.auto_scroll = true;
        if (categoryCallback != null) {
            int pos = position;
            pos++;
            categoryCallback.nextPosition(pos);
        }
    }

    private void prevArticle(int position) {
        dummyBulletForSize.setText("");
        desc_list.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        desc_list.requestLayout();
        Constants.auto_scroll = true;
        if (categoryCallback != null) {
            int pos = position;
            pos--;
            if (pos > -1)
                categoryCallback.nextPosition(pos);
        }
    }

    public void playAudio(int mPosition, ListBulletsAdapter bulletsAdapter, Article content) {
        if (mPosition > -1 && bulletsAdapter != null && content != null) {

            AnalyticsEvents.INSTANCE.logEvent(context,
                    Events.ARTICLE_SWIPE);

            bulletsAdapter.setCurrentPosition(mPosition);
            if (bulletsAdapter.getCurrentPosition() < content.getBullets().size()) {
                resizeBullet(bulletsAdapter, content);
                long duration = 0;
                if (content.getBullets().get(bulletsAdapter.getCurrentPosition()).getDuration() == 0 && !TextUtils.isEmpty(content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData()))
                    duration = content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData().length() * 100;
                else {
                    duration = (long) (content.getBullets().get(bulletsAdapter.getCurrentPosition()).getDuration() / Constants.READING_SPEED_RATES.get(Constants.reading_speed));
                }
                duration = duration + 1500;
                if (Constants.muted || content.isMute()) {

                    progressAudio.setVisibility(View.GONE);
                    storiesProgressView.setStoryDuration(duration);
                    if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                        if (mPrefConfig.isBulletsAutoPlay()) {
                            storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                        } else {
                            storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                        }
                    } else {
                        if (mPrefConfig.isBulletsAutoPlay()) {
                            storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                        } else {
                            storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                        }
                    }

                    long finalDuration1 = duration;
                    listener.playAudio(
                            new AudioCallback() {
                                @Override
                                public void isAudioLoaded(boolean isLoaded) {
                                    Log.e("isAudioLoaded", "isLoaded = " + isLoaded);
                                    if (isLoaded) {
                                        progressAudio.setVisibility(View.INVISIBLE);
                                        storiesProgressView.setStoryDuration(finalDuration1);
                                        if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                                            if (mPrefConfig.isBulletsAutoPlay()) {
                                                storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                                            } else {
                                                storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                                            }
                                        } else {
                                            if (mPrefConfig.isBulletsAutoPlay()) {
                                                storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                                            } else {
                                                storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                                            }
                                        }
                                    } else {
                                        progressAudio.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void isAudioComplete(boolean isCompleted) {

                                }
                            }, content.getFragTag(), new AudioObject(content.getId(),
                                    content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData(),
                                    "",
                                    bulletsAdapter.getCurrentPosition(), duration));

                } else {

                    progressAudio.setVisibility(View.VISIBLE);
                    long finalDuration = duration;
                    listener.playAudio(new AudioCallback() {
                        @Override
                        public void isAudioLoaded(boolean isLoaded) {
                            Log.e("isAudioLoaded", "isLoaded = " + isLoaded);
                            if (isLoaded) {
                                progressAudio.setVisibility(View.INVISIBLE);
                                storiesProgressView.setStoryDuration(finalDuration);

                                storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());

//                                if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
//                                    storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
//                                } else {
//                                    storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
//                                }
                            } else {
                                progressAudio.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void isAudioComplete(boolean isCompleted) {

                        }
                    }, content.getFragTag(), new AudioObject(content.getId(),
                            content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData(),
                            content.getBullets().get(bulletsAdapter.getCurrentPosition()).getAudio(),
                            bulletsAdapter.getCurrentPosition(), duration));
                }
            }
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

    private void nextBullet(ListBulletsAdapter bulletsAdapter, RecyclerView view) {
        if (bulletsAdapter != null && view != null) {
            int pos = bulletsAdapter.getCurrentPosition();
            pos++;
            view.smoothScrollToPosition(pos);
            bulletsAdapter.setCurrentPosition(pos);
        }
    }

    private void prevBullet(ListBulletsAdapter bulletsAdapter, RecyclerView view) {
        if (bulletsAdapter != null && view != null) {
            if (bulletsAdapter.getCurrentPosition() == 0) return;
            int pos = bulletsAdapter.getCurrentPosition();
            view.smoothScrollToPosition(--pos);
            bulletsAdapter.setCurrentPosition(pos);
        }
    }

    private void fadeInOut(View view) {
        crossFadeAnimation(view, view, 300);
    }

    private void crossFadeAnimation(final View fadeInTarget, final View fadeOutTarget, long duration) {
        AnimatorSet mAnimationSet = new AnimatorSet();
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fadeOutTarget, View.ALPHA, 1f, 0f);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fadeOutTarget.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        fadeOut.setInterpolator(new LinearInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(fadeInTarget, View.ALPHA, 0f, 1f);
        fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                fadeInTarget.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        fadeIn.setInterpolator(new LinearInterpolator());
        mAnimationSet.setDuration(duration);
        mAnimationSet.playTogether(fadeOut, fadeIn);
        mAnimationSet.start();
    }

    public void updateMuteButtons() {
        if (!Constants.mute_disable) {
            speaker.setAlpha(1f);
            if (!Constants.muted) {
                speaker.setImageResource(R.drawable.ic_speaker_unmute);
            } else {
                speaker.setImageResource(R.drawable.ic_speaker_mute);
            }
        } else {
            speaker.setVisibility(View.GONE);
        }
    }

    public void selectUnselectedItem(int position, Article article) {
        if (position > -1 && bulletsAdapterMain != null && article != null) {
            equilizer.setVisibility(View.INVISIBLE);
            viewFill.setVisibility(View.GONE);

            Constants.ProgressBarDrawable = R.drawable.bullet_selector_green;
            Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_green_2;

            storiesProgressView.setStoriesCount(article.getBullets().size());
            storiesProgressView.setProgressWidth(14, false);
            storiesProgressView.setStoriesListener(new HorizontalStoriesProgressView.StoriesListener() {
                @Override
                public void onFirst() {

                }

                @Override
                public void onNext() {
                    if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                        //GOTO NEXT BULLET
                        if (mPrefConfig.isBulletsAutoPlay())
                            nextBullet(bulletsAdapterMain, desc_list);
                    } else {
                        //Auto Swipe
                        if (Constants.auto_scroll) {
//                            Utils.logEvent(context, "list_next_auto");
//                            //GOTO NEXT ARTICLE
//                            if (mPrefConfig.isBulletsAutoPlay())
//                                nextArticle(position);
                        } else {
                            //GOTO NEXT BULLET
                            if (mPrefConfig.isBulletsAutoPlay())
                                nextBullet(bulletsAdapterMain, desc_list);
                        }
                    }
                }

                @Override
                public void onPrev() {
                }

                @Override
                public void onComplete() {
                    //Auto Swipe
//                    if (Constants.auto_scroll) {
//                        Utils.logEvent(context, "list_next_auto");
//                    } else {
//                        Utils.logEvent(context, "list_next_scroll");
//                    }
//                    if (mPrefConfig.isBulletsAutoPlay())
//                        nextArticle(position);
                }
            });


            Animation progressAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in_progress);
//            storiesProgressView.setVisibility(View.VISIBLE);
            // likeMain.setVisibility(View.VISIBLE);
            // commentMain.setVisibility(View.VISIBLE);
            storiesProgressView.startAnimation(progressAnimation);
            progressAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    scrollListener.onScrollStateChanged(desc_list, RecyclerView.SCROLL_STATE_IDLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        Constants.mute_disable = article.isMute();
        if (Constants.mute_disable) {
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("mute");
        } else {
            if (!Constants.muted) {
                if (goHomeMainActivity != null)
                    goHomeMainActivity.sendAudioEvent("unmute");
            } else {
                if (goHomeMainActivity != null)
                    goHomeMainActivity.sendAudioEvent("mute");
            }
        }
        updateMuteButtons();


        if (gotoChannelListener != null) {
            gotoChannelListener.onArticleSelected(article);
        }
    }
}
