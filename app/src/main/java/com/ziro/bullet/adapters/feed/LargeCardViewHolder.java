package com.ziro.bullet.adapters.feed;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.adapters.CardBulletsAdapter;
import com.ziro.bullet.adapters.ListBulletsAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.data.models.config.Static;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.LikeInterface;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.SelectedChannel;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
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

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class LargeCardViewHolder extends RecyclerView.ViewHolder {

    private static final int MIN_DISTANCE = 150;
    private final float Y_BUFFER = 10;
    public HorizontalStoriesProgressView storiesProgressView;
    public TextView comment_count;
    public TextView like_count;
    public ImageView like_icon;
    public TextView authorName;
    public ImageView separatorDot;
    public ImageView follow;
    public RelativeLayout statusView;
    public LinearLayout share;
    public LinearLayout root;
    public ConstraintLayout clArticle;
    private float preX = 0f;
    private float preY = 0f;
    private float x1, x2;
    private RoundedImageView ivProfile;
    private ImageView ivArticleImage, ivArticleBackImage, ivDots, ivSpeaker, ivLeftArc, ivRightArc, ivEquilizer;
    private TextView tvTime, tvSourceName, tvDummyBulletForSize;
    private RecyclerView rvDescList;
    private ProgressBar pbProgressAudio;
    private RelativeLayout commentMain;
    private ImageView comment_icon;
    private RelativeLayout likeMain;
    private ShareToMainInterface shareToMainInterface;
    private TempCategorySwipeListener swipeListener;
    private GoHome goHomeMainActivity;
    private OnGotoChannelListener gotoChannelListener;
    private NewsCallback categoryCallback;
    private DetailsActivityInterface detailsActivityInterface;
    private AdapterCallback adapterCallback;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private PrefConfig mPrefConfig;
    private Activity mContext;
    private String direction = "";
    private ListBulletsAdapter bulletsAdapterMain;
    private LikePresenter presenter;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private Handler handler = new Handler();
    private boolean swipeNextEnabled = true;
    private Runnable tapRunnable = () -> swipeNextEnabled = true;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private boolean isPostArticle = false;
    private String type = "";
    private CommentClick mCommentClick;
    private ImageView statusIcon;
    private LottieAnimationView statusAnimation;
    private TextView text1, text2, bulletHeading;
    private boolean isCommunity;
    private RecyclerView.OnScrollListener scrollListener;


    public LargeCardViewHolder(boolean isCommunity, CommentClick mCommentClick, String type, boolean isPostArticle, @NonNull View itemView, Activity context, AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity,
                               ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback,
                               OnGotoChannelListener gotoChannelListener, DetailsActivityInterface detailsActivityInterface,
                               ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(itemView);
        this.presenter = new LikePresenter(context);
        this.followUnfollowPresenter = new FollowUnfollowPresenter(context);
        this.shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
        this.type = type;
        this.isCommunity = isCommunity;
        this.mCommentClick = mCommentClick;
        this.isPostArticle = isPostArticle;
        this.swipeListener = swipeListener;
        this.adapterCallback = adapterCallback;
        this.mContext = context;
        this.goHomeMainActivity = goHomeMainActivity;
        this.shareToMainInterface = shareToMainInterface;
        this.gotoChannelListener = gotoChannelListener;
        this.categoryCallback = categoryCallback;
        this.detailsActivityInterface = detailsActivityInterface;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;

        mPrefConfig = config;

        root = itemView.findViewById(R.id.root);
        clArticle = itemView.findViewById(R.id.cl_article);
        share = itemView.findViewById(R.id.share);
        ivSpeaker = itemView.findViewById(R.id.speaker);
        tvDummyBulletForSize = itemView.findViewById(R.id.dummyBulletForSize);
        ivEquilizer = itemView.findViewById(R.id.equilizer);
        pbProgressAudio = itemView.findViewById(R.id.progressAudio);
        ivLeftArc = itemView.findViewById(R.id.leftArc);
        ivRightArc = itemView.findViewById(R.id.rightArc);
        ivArticleImage = itemView.findViewById(R.id.odd_image);
        ivArticleBackImage = itemView.findViewById(R.id.odd_imageBack);
        ivProfile = itemView.findViewById(R.id.profile);
        rvDescList = itemView.findViewById(R.id.desc_list);
        tvTime = itemView.findViewById(R.id.time);
        tvSourceName = itemView.findViewById(R.id.source_name);
        ivDots = itemView.findViewById(R.id.dotImg);
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
        authorName = itemView.findViewById(R.id.author_name);
        separatorDot = itemView.findViewById(R.id.separator_dot);
        follow = itemView.findViewById(R.id.follow);
        statusAnimation.setAnimation(Utils.getProcessingForTheme(mPrefConfig.getAppTheme()));
        bulletHeading = itemView.findViewById(R.id.bulletHeading);
    }

    public void checkArticleStatus(Article article) {
        if (!TextUtils.isEmpty(article.getStatus())) {
            switch (article.getStatus()) {
                case Constants.ARTICLE_PROCESSING:
                    statusView.setVisibility(View.VISIBLE);
                    statusAnimation.setVisibility(View.VISIBLE);
                    statusIcon.setVisibility(View.GONE);
                    text1.setText(mContext.getString(R.string.processing));
                    text2.setVisibility(View.GONE);
                    break;
                case Constants.ARTICLE_SCHEDULED:
                    statusView.setVisibility(View.VISIBLE);
                    text1.setText(mContext.getString(R.string.scheduled_on));
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

    public void changeImage(String img) {
        if (!TextUtils.isEmpty(img)) {
            if (!isPostArticle) {
                Picasso.get()
                        .load(img)
                        .transform(new BlurTransformation(mContext, 25, 1))
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .into(ivArticleBackImage);

                Glide.with(BulletApp.getInstance())
                        .load(img)
//                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .override(Constants.targetWidth, Constants.targetHeight)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.5f)
//                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                        .transform(new CircleCrop())
                        .into(ivArticleImage);
            } else {

                Glide.with(BulletApp.getInstance())
                        .load(img)
                        .apply(RequestOptions.bitmapTransform(new com.ziro.bullet.utills.BlurTransformation(50, 10)))
                        .override(Constants.targetWidth, Constants.targetHeight)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.5f)
                        .into(ivArticleBackImage);

                Glide.with(BulletApp.getInstance())
                        .load(img)
//                        .listener(new RequestListener<Drawable>() {
//                            @Override
//                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                ivArticleImage.setImageResource(R.drawable.img_place_holder);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                return false;
//                            }
//
//                        })
//                        .error(R.drawable.img_place_holder)
//                        .placeholder(R.drawable.img_place_holder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(Constants.targetWidth, Constants.targetHeight)
                        .thumbnail(0.5f)
//                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                        .transform(new CircleCrop())
                        .into(ivArticleImage);
            }
        } else {
            ivArticleImage.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
        }
    }

    public void invalidate() {
        updateMuteButtons();
        if (bulletsAdapterMain != null)
            bulletsAdapterMain.notifyDataSetChanged();
        tvSourceName.setTextColor(mContext.getResources().getColor(R.color.large_source_name));
        authorName.setTextColor(mContext.getResources().getColor(R.color.large_time));
    }

    public void forReelBottomSheet() {
        updateMuteButtons();
        tvSourceName.setTextColor(mContext.getResources().getColor(R.color.black));
        authorName.setTextColor(mContext.getResources().getColor(R.color.grey_33));
    }

    public void bind(int position, Article article) {
        Log.d("largecard", "bind: pos = " + position);
        if (article != null) {

            checkArticleStatus(article);
            if (article.getBullets() != null && article.getBullets().size() > 0) {
                changeImage(article.getBullets().get(0).getImage());
            } else {
                ivArticleImage.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }
            if (article.getBullets() != null && bulletHeading != null) {
                if (article.getBullets().get(0).getData() != null)
                    bulletHeading.setText(article.getBullets().get(0).getData());
            }
            itemView.setOnClickListener(v -> {
                if (!isPostArticle) {
                    mCommentClick.onDetailClick(position, article);
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.ARTICLE_OPEN);
                }
            });
            if (!isPostArticle) {
                if (article.getSourceNameToDisplay().isEmpty()) {
                    separatorDot.setVisibility(View.GONE);
                } else {
                    separatorDot.setVisibility(View.VISIBLE);
                }
                tvSourceName.setText(article.getSourceNameToDisplay());
                if (article.getSourceImageToDisplay() != null && !article.getSourceImageToDisplay().equals("")) {
                    Picasso.get()
                            .load(article.getSourceImageToDisplay())
                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .transform(new CropCircleTransformation())
                            .into(ivProfile);
                } else {
                    Picasso.get()
                            .load(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .transform(new CropCircleTransformation())
                            .into(ivProfile);
                }

                if (article.getSource() != null) {
                    if (article.getSource().isFavorite()) {
                        follow.setVisibility(View.GONE);
                    } else {
                        follow.setVisibility(View.VISIBLE);
                    }
                    follow.setOnClickListener(view -> followUnfollowPresenter.followSource(article.getSource().getId(), position, (position1, flag) -> {
                        if (flag) {
                            follow.setVisibility(View.GONE);
                            article.getSource().setFavorite(true);
                        } else follow.setVisibility(View.VISIBLE);
                    }));
                }

                if (!article.getAuthorNameToDisplay().equals("")) {
                    authorName.setVisibility(View.VISIBLE);
                    authorName.setText(mContext.getString(R.string.by) + " " + article.getAuthorNameToDisplay());
                } else {
                    authorName.setVisibility(View.GONE);
                    authorName.setText(mContext.getString(R.string.by) + " " + article.getSourceNameToDisplay());
                }


                commentMain.setOnClickListener(v -> {
                    if (mCommentClick == null) return;
                    mCommentClick.commentClick(position, article.getId());
                });


                if (article.getInfo() != null) {

                    comment_count.setText("" + article.getInfo().getComment_count());
                    like_count.setText("" + article.getInfo().getLike_count());

                    if (article.getInfo().isLiked()) {
                        like_icon.setImageResource(R.drawable.ic_like_active);
//                        like_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.theme_color_1), android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {
                        like_icon.setImageResource(R.drawable.ic_like_inactive);
//                        like_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.bottom_nav_inactive), android.graphics.PorterDuff.Mode.SRC_IN);
                    }

                    likeMain.setOnClickListener(v -> {
                        likeMain.setEnabled(false);
                        Log.e("likeArticle", "like : " + article.getInfo().isLiked());
                        presenter.like(article.getId(), new LikeInterface() {
                            @Override
                            public void success(boolean like) {
                                if (mContext == null) return;
                                likeMain.setEnabled(true);
                                Log.e("likeArticle", "new like: " + like);
                                article.getInfo().setLiked(like);
                                Log.e("likeArticle", "object like : " + article.getInfo().isLiked());
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
                                    like_icon.setImageResource(R.drawable.ic_like_active);
//                                    like_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.theme_color_1), android.graphics.PorterDuff.Mode.SRC_IN);
                                } else {
                                    like_icon.setImageResource(R.drawable.ic_like_inactive);
//                                    like_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.bottom_nav_inactive), android.graphics.PorterDuff.Mode.SRC_IN);
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
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
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
                                mContext.startActivity(shareIntent);
                            }

                            @Override
                            public void error(String error) {
                                showOptionsLoaderCallback.showLoader(false);
                            }
                        });
                    }
                });
                ivDots.setOnClickListener(v -> {
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
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
                ivProfile.setOnClickListener(v -> {
                    detailsPage(article, position);
                });
                tvSourceName.setOnClickListener(v -> {
                    detailsPage(article, position);
                });
                authorName.setOnClickListener(v -> {
//                    if (TextUtils.isEmpty(type) || !type.equalsIgnoreCase("MY_ARTICLES")) {
//                        if (article.getAuthor().get(0) != null) {
//                            Utils.openAuthor(mContext, article.getAuthor().get(0));
//                        }
//                    }
                    detailsPage(article, position);
                });
                tvTime.setOnClickListener(v -> {
                    detailsPage(article, position);
                });
            } else {
                String name = null, image = null;
                if (mPrefConfig != null) {
                    if (mPrefConfig.selectedChannel() != null && !TextUtils.isEmpty(mPrefConfig.selectedChannel().getName()) && !mPrefConfig.selectedChannel().getName().equalsIgnoreCase(mContext.getString(R.string.my_profile))) {
                        SelectedChannel channel = mPrefConfig.selectedChannel();
                        name = channel.getName();
                        image = channel.getImage();
                        authorName.setVisibility(View.VISIBLE);
//                        separatorDot.setVisibility(View.VISIBLE);
                        if (mPrefConfig.isUserObject() != null) {
                            authorName.setText(mPrefConfig.isUserObject().getFirst_name() + " " + mPrefConfig.isUserObject().getLast_name());
                        }
                    } else {
                        authorName.setVisibility(View.GONE);
//                        separatorDot.setVisibility(View.GONE);
                        if (mPrefConfig.isUserObject() != null) {
                            image = mPrefConfig.isUserObject().getProfile_image();
                            name = mPrefConfig.isUserObject().getFirst_name() + " " + mPrefConfig.isUserObject().getLast_name();
                        }
                    }
                    tvSourceName.setText(name);
                    if (!TextUtils.isEmpty(image)) {
                        Picasso.get()
                                .load(image)
                                .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                                .transform(new CropCircleTransformation())
                                .into(ivProfile);
                    } else {
                        ivProfile.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                    }
                }
                // for new post article
                Constants.muted = true;
                tvTime.setText(R.string.today);
                like_count.setText("0");
                comment_count.setText("0");
            }

            String time = Utils.getTimeAgo(Utils.getDate(article.getPublishTime()), mContext);
            if (!TextUtils.isEmpty(time)) {
                tvTime.setText(time);
            }

            if (mPrefConfig != null) {
                Static mStatic = mPrefConfig.getHomeImage();
                if (mStatic != null && mStatic.getHomeImage() != null) {
                    if (!TextUtils.isEmpty(mStatic.getHomeImage().getLeft())) {
                        Picasso.get()
                                .load(mStatic.getHomeImage().getLeft())
                                .into(ivLeftArc);
                    } else {
                        ivLeftArc.setImageResource(R.drawable.tap_left);
                    }
                    if (!TextUtils.isEmpty(mStatic.getHomeImage().getRight())) {
                        Picasso.get()
                                .load(mStatic.getHomeImage().getRight())
                                .into(ivRightArc);
                    } else {
                        ivRightArc.setImageResource(R.drawable.tap_right);
                    }
                } else {
                    ivRightArc.setImageResource(R.drawable.tap_right);
                    ivLeftArc.setImageResource(R.drawable.tap_left);
                }
            }


//            CardBulletsAdapter cardBulletsAdapter = new CardBulletsAdapter(isCommunity, mCommentClick, type, isPostArticle, mContext, article.getBullets(), new ListAdapterListener() {
//                @Override
//                public void verticalScrollList(boolean isEnable) {
//                    if (swipeListener != null) {
//                        swipeListener.swipe(isEnable);
//                    }
//                    if (isEnable) {
//                        bulletResume();
//                    } else {
//                        bulletPause();
//                    }
//                }
//
//                @Override
//                public void nextArticle(int position) {
////                    nextBullet(bulletsAdapterMain, true, article);
//                }
//
//                @Override
//                public void prevArticle(int position) {
////                    prevBullet(bulletsAdapterMain, true, article);
//                }
//
//                @Override
//                public void clickArticle(int bPosition) {
//                    if (!isPostArticle) {
//                        Map<String, String> params = new HashMap<>();
//                        params.put(Events.KEYS.ARTICLE_ID, article.getId());
//                        AnalyticsEvents.INSTANCE.logEvent(mContext,
//                                params,
//                                Events.ARTICLE_OPEN);
//                        mCommentClick.onDetailClick(position, article);
//                    }
//                }
//            }, article, position);

            ListBulletsAdapter cardBulletsAdapter = new ListBulletsAdapter(isCommunity, type, new ListAdapterListener() {
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
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.ARTICLE_OPEN);
                    if (mCommentClick == null) return;
                    mCommentClick.onDetailClick(position, article);
                }
            }, mContext, article.getBullets(), article.getType().equalsIgnoreCase("YOUTUBE"), article);

            clArticle.setOnClickListener(v -> {
                if (!isPostArticle) {
                    mCommentClick.onDetailClick(position, article);
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.ARTICLE_OPEN);
                }
            });
            ivArticleImage.setOnClickListener(v -> {
                if (!isPostArticle) {
                    mCommentClick.onDetailClick(position, article);
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.ARTICLE_OPEN);
                }
            });

            if (article.getBullets() != null && article.getBullets().size() > 0) {
                LinearLayoutManager manager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
                rvDescList.setLayoutManager(manager);
                rvDescList.setAdapter(cardBulletsAdapter);
                rvDescList.setOnFlingListener(null);
                PagerSnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(rvDescList);

                rvDescList.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                rvDescList.requestLayout();
                if (!isPostArticle) {
                    rvDescList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                        @Override
                        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                            switch (e.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    x1 = e.getX();
                                    rvDescList.getParent().requestDisallowInterceptTouchEvent(true);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    if (bulletsAdapterMain != null) {
                                        x2 = e.getX();
                                        float deltaX = x2 - x1;
                                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                                            if (swipeNextEnabled) {
                                                swipeNextEnabled = false;
                                                handler.postDelayed(tapRunnable, 200);
                                                if (mContext != null) {
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put(Events.KEYS.ARTICLE_ID, article.getId());
                                                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                                                            params,
                                                            Events.ARTICLE_SWIPE);
                                                }
                                            }

                                            //Utils.doHaptic(mPrefConfig);
//                                            if ((!Utils.isRTL() && x2 > x1) || (Utils.isRTL() && !(x2 > x1))) {
//                                                if (article.isSelected() && bulletsAdapterMain.getCurrentPosition() == 0 && position == adapterCallback.getArticlePosition()) {
//                                                    if (swipeNextEnabled) {
//                                                        swipeNextEnabled = false;
//                                                        handler.postDelayed(tapRunnable, 200);
//                                                        prevArticle();
//                                                        if (scrollListener != null)
//                                                            rvDescList.removeOnScrollListener(scrollListener);
//                                                    }
//
//                                                }
//                                            } else {
//                                                if (article.isSelected() && bulletsAdapterMain.getCurrentPosition() == (bulletsAdapterMain.getItemCount() - 1) && position == adapterCallback.getArticlePosition()) {
//                                                    handler.removeCallbacks(tapRunnable);
//
//                                                    if (swipeNextEnabled) {
//                                                        swipeNextEnabled = false;
//                                                        handler.postDelayed(tapRunnable, 200);
//                                                        nextArticle();
//                                                        if (scrollListener != null)
//                                                            rvDescList.removeOnScrollListener(scrollListener);
//                                                    }
//                                                }
//                                            }
                                        }
                                    }
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    if (Math.abs(e.getX() - preX) > Math.abs(e.getY() - preY)) {
                                        rvDescList.getParent().requestDisallowInterceptTouchEvent(true);
                                    } else if (Math.abs(e.getY() - preY) > Y_BUFFER) {
                                        rvDescList.getParent().requestDisallowInterceptTouchEvent(false);
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

                final int[] mPosition = {0};
                rvDescList.addOnScrollListener(scrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (mPosition[0] != -1) {
                                article.setLastPosition(mPosition[0]);
                                cardBulletsAdapter.setCurrentPosition(mPosition[0]);

                                if (mPosition[0] < article.getBullets().size()) {
                                    Bullet bullet = article.getBullets().get(mPosition[0]);
                                    if (bullet != null) {
                                        changeImage(bullet.getImage());
                                    }
                                }

                                float val = -1;
                                if (mPosition[0] == 0) {
                                    val = Utils.getHeadlineDimens(mPrefConfig, mContext);
                                } else {
                                    val = Utils.getBulletDimens(mPrefConfig, mContext);
                                }
                                if (val != -1) {
                                    tvDummyBulletForSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
                                }

                                if (article.isSelected()) {
                                    Constants.auto_scroll = false;
                                    playAudio(cardBulletsAdapter, article);
                                } else {
                                    Log.d("dsadasdasd", "onScrollStateChanged: " + mPosition[0]);
                                    if (categoryCallback != null)
                                        categoryCallback.nextPositionNoScroll(position, true);
                                    //Utils.doHaptic(mPrefConfig);
                                    resizeBullet(cardBulletsAdapter, article);
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


                ivSpeaker.setOnClickListener(v -> {
                    if (Constants.mute_disable) {
                        Utils.showSnacky(ivSpeaker, mContext.getString(R.string.speech_not_available));
                        return;
                    }
                    if (!Constants.muted) {
                        Constants.muted = true;
                        Constants.ReelMute = true;
                        if (goHomeMainActivity != null)
                            goHomeMainActivity.sendAudioEvent("mute");
                    } else {
                        Constants.muted = false;
                        Constants.ReelMute = false;
                        if (goHomeMainActivity != null) {
                            goHomeMainActivity.sendAudioEvent("unmute");
                            goHomeMainActivity.sendAudioEvent("play");
                        }
                        playAudio(cardBulletsAdapter, article);
                    }
                    updateMuteButtons();
                });
            }

            bulletsAdapterMain = cardBulletsAdapter;

            if (article.isSelected()) {
                storiesProgressView.clearStory();

                if (gotoChannelListener != null) {
                    gotoChannelListener.onArticleSelected(article);
                }

                if (swipeListener != null) swipeListener.swipe(false);
                if (!isPostArticle) {
                    ivSpeaker.setVisibility(View.VISIBLE);
                    ivEquilizer.setVisibility(View.VISIBLE);
                } else {
                    ivEquilizer.setVisibility(View.GONE);
                }
                imageCase(position, cardBulletsAdapter, article);

                Constants.mute_disable = article.isMute();
                if (Constants.mute_disable) {
                    if (goHomeMainActivity != null) goHomeMainActivity.sendAudioEvent("mute");
                } else {
                    if (!Constants.muted) {
                        if (goHomeMainActivity != null) goHomeMainActivity.sendAudioEvent("unmute");
                    } else {
                        if (goHomeMainActivity != null) goHomeMainActivity.sendAudioEvent("mute");
                    }
                }
                updateMuteButtons();

                if (cardBulletsAdapter.getCurrentPosition() == 0) {

                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setType(MessageEvent.TYPE_COUNT_API_CALL);
                    messageEvent.setStringData(article.getId());

                    EventBus.getDefault().post(messageEvent);

                    playAudio(cardBulletsAdapter, article);
                }
            } else {
                storiesProgressView.setStoriesListener(null);
                storiesProgressView.destroy();
//                storiesProgressView.setVisibility(View.GONE);
                //commentMain.setVisibility(View.INVISIBLE);
                //likeMain.setVisibility(View.INVISIBLE);
                ivSpeaker.setVisibility(View.GONE);
                ivEquilizer.setVisibility(View.GONE);
                ivEquilizer.setImageResource(R.drawable.static_equilizer);
                pbProgressAudio.setVisibility(View.GONE);
                rvDescList.scrollToPosition(article.getLastPosition());
            }
        }
    }


    private void detailsPage(Article content, int position) {
        if (mContext instanceof ChannelDetailsActivity) {
            return;
        }
        if (content != null) {
            if (gotoChannelListener != null && content.getSource() != null) {
                if (mPrefConfig != null) {
                    mPrefConfig.setSrcLang(content.getSource().getLanguage());
                    mPrefConfig.setSrcLoc(content.getSource().getCategory());
                }
                gotoChannelListener.onItemClicked(TYPE.SOURCE, content.getSource().getId(),
                        content.getSource().getName(), content.getSource().isFavorite(), content, position);
            } else if (content.getAuthor() != null && content.getAuthor().size() > 0) {
                if (TextUtils.isEmpty(type) || !type.equalsIgnoreCase("MY_ARTICLES")) {
                    if (content.getAuthor().get(0) != null) {
                        Utils.openAuthor(mContext, content.getAuthor().get(0));
                    }
                }
            }
        }
    }

    public void unselect(Article article) {
        storiesProgressView.setStoriesListener(null);
        storiesProgressView.destroy();
//        storiesProgressView.setVisibility(View.GONE);
        //commentMain.setVisibility(View.INVISIBLE);
        //likeMain.setVisibility(View.INVISIBLE);
        ivSpeaker.setVisibility(View.GONE);
        ivEquilizer.setVisibility(View.GONE);
        ivEquilizer.setImageResource(R.drawable.static_equilizer);
        pbProgressAudio.setVisibility(View.GONE);
        rvDescList.scrollToPosition(article.getLastPosition());
    }

    public void selectUnselectedItem(int position, Article content) {
        if (position > -1 && content != null) {

            Constants.ProgressBarDrawable = R.drawable.bullet_selector_green;
            Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_green_2;

            if (content.getBullets() != null) {
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
                                nextBullet(bulletsAdapterMain, false, content);
                        } else {
                            //Auto Swipe
                            if (Constants.auto_scroll) {
//                                Utils.logEvent(mContext, "list_next_auto");
//                                //GOTO NEXT ARTICLE
//                                if (mPrefConfig.isBulletsAutoPlay())
//                                    if (categoryCallback != null) {
//                                        int pos = position;
//                                        pos++;
//                                        categoryCallback.nextPosition(pos);
//                                    }
                            } else {
                                //GOTO NEXT BULLET
                                if (mPrefConfig.isBulletsAutoPlay())
                                    nextBullet(bulletsAdapterMain, false, content);
                            }
                        }
                    }

                    @Override
                    public void onPrev() {
                    }

                    @Override
                    public void onComplete() {
                        //Auto Swipe
//                        if (Constants.auto_scroll) {
//                            Utils.logEvent(mContext, "list_next_auto");
//                        } else {
//                            Utils.logEvent(mContext, "list_next_scroll");
//                        }
//                        Constants.auto_scroll = true;
//                        if (mPrefConfig.isBulletsAutoPlay())
//                            if (categoryCallback != null) {
//                                int pos = position;
//                                pos++;
//                                categoryCallback.nextPosition(pos);
//                            }
                    }
                });
                storiesProgressView.selectStory(1);
            }

//            storiesProgressView.setVisibility(View.VISIBLE);
//            commentMain.setVisibility(View.VISIBLE);
//            likeMain.setVisibility(View.VISIBLE);


            if (gotoChannelListener != null) {
                gotoChannelListener.onArticleSelected(content);
            }

            Constants.mute_disable = content.isMute();
            if (Constants.mute_disable) {
                if (goHomeMainActivity != null) goHomeMainActivity.sendAudioEvent("mute");
            } else {
                if (!Constants.muted) {
                    if (goHomeMainActivity != null) goHomeMainActivity.sendAudioEvent("unmute");
                } else {
                    if (goHomeMainActivity != null) goHomeMainActivity.sendAudioEvent("mute");
                }
            }

            if (swipeListener != null) swipeListener.swipe(false);

            playAudio(bulletsAdapterMain, content);

            if (!isPostArticle) {
                ivSpeaker.setVisibility(View.VISIBLE);
                ivEquilizer.setVisibility(View.VISIBLE);
            } else {
                ivEquilizer.setVisibility(View.GONE);
            }

            updateMuteButtons();
        }
    }

    public void updateMuteButtons() {
        if (!Constants.mute_disable) {
            ivSpeaker.setAlpha(1f);
            if (!Constants.muted) {
                ivSpeaker.setImageResource(R.drawable.ic_speaker_unmute);
                Glide.with(BulletApp.getInstance()).load(R.raw.equlizer).into(ivEquilizer);
            } else {
                ivSpeaker.setImageResource(R.drawable.ic_speaker_mute);
                ivEquilizer.setImageResource(R.drawable.static_equilizer);
            }
        } else {
            ivSpeaker.setVisibility(View.GONE);
            ivEquilizer.setVisibility(View.GONE);
        }
    }


    private void nextArticle() {
        Log.e("kjandn", "nextArticle");
        Constants.auto_scroll = true;
        tvDummyBulletForSize.setText("");
        rvDescList.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        rvDescList.requestLayout();
        if (categoryCallback != null) {
            int pos = adapterCallback.getArticlePosition();
            pos++;
            categoryCallback.nextPosition(pos);
        }
    }

    private void prevArticle() {
        Constants.auto_scroll = true;
        tvDummyBulletForSize.setText("");
        rvDescList.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        rvDescList.requestLayout();
        Log.e("kjandn", "prevArticle");
        if (categoryCallback != null) {
            int pos = adapterCallback.getArticlePosition();
            pos--;
            categoryCallback.nextPosition(pos);
        }
    }


    public void bulletResume() {
        if (mContext != null) {
            if (detailsActivityInterface != null) {
                detailsActivityInterface.resume();
            }
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("resume");
        }
    }

    public void bulletPause() {
        if (mContext != null) {
            if (detailsActivityInterface != null) {
                detailsActivityInterface.pause();
            }
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("pause");
        }
    }

    public void imageCase(int position, ListBulletsAdapter bulletsAdapter, Article content) {
        if (position > -1 && bulletsAdapter != null && content != null) {

            Constants.ProgressBarDrawable = R.drawable.bullet_selector_green;
            Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_green_2;

            if (content.getBullets() != null) {
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
                                nextBullet(bulletsAdapter, false, content);
                        } else {
                            //Auto Swipe
                            if (Constants.auto_scroll) {
//                                Utils.logEvent(mContext, "list_next_auto");
//                                //GOTO NEXT ARTICLE
//                                if (mPrefConfig.isBulletsAutoPlay())
//                                    if (categoryCallback != null) {
//                                        int pos = position;
//                                        pos++;
//                                        categoryCallback.nextPosition(pos);
//                                    }
                            } else {
                                //GOTO NEXT BULLET
                                if (mPrefConfig.isBulletsAutoPlay())
                                    nextBullet(bulletsAdapter, false, content);
                            }
                        }
                    }

                    @Override
                    public void onPrev() {
                    }

                    @Override
                    public void onComplete() {
                        //Auto Swipe
//                        if (Constants.auto_scroll) {
//                            Utils.logEvent(mContext, "list_next_auto");
//                        } else {
//                            Utils.logEvent(mContext, "list_next_scroll");
//                        }
//                        Constants.auto_scroll = true;
//                        if (mPrefConfig.isBulletsAutoPlay())
//                            if (categoryCallback != null) {
//                                int pos = position;
//                                pos++;
//                                categoryCallback.nextPosition(pos);
//                            }
                    }
                });
            }

//            storiesProgressView.setVisibility(View.VISIBLE);
        }
    }

    public void playAudio(ListBulletsAdapter bulletsAdapter, Article content) {
        if (bulletsAdapter != null && content != null) {
            if (content.getBullets() != null) {
                if (bulletsAdapter.getCurrentPosition() < content.getBullets().size()) {
                    resizeBullet(bulletsAdapter, content);
                    long duration = 0;

                    if (content.getBullets() != null && content.getBullets().size() > 0) {
                        Bullet bullet = content.getBullets().get(bulletsAdapter.getCurrentPosition());
                        if (bullet != null) {
                            if (!TextUtils.isEmpty(bullet.getData()) && bullet.getDuration() == 0) {
                                duration = bullet.getData().length() * 100;
                            } else {
                                duration = (long) ((long) bullet.getDuration() / Constants.READING_SPEED_RATES.get(Constants.reading_speed));
                            }
                        }
                    }
                    duration = duration + 1500;

                    if (Constants.muted || content.isMute()) {

                        pbProgressAudio.setVisibility(View.GONE);
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
                        detailsActivityInterface.playAudio(
                                new AudioCallback() {
                                    @Override
                                    public void isAudioLoaded(boolean isLoaded) {
                                        Log.e("isAudioLoaded", "isLoaded = " + isLoaded);
                                        pbProgressAudio.setVisibility(View.GONE);
                                        if (isLoaded) {
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
                                        }
                                    }

                                    @Override
                                    public void isAudioComplete(boolean isCompleted) {

                                    }
                                }, content.getFragTag(), new AudioObject(content.getId(),
                                        content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData(),
                                        content.getBullets().get(bulletsAdapter.getCurrentPosition()).getAudio(),
                                        bulletsAdapter.getCurrentPosition(), duration));

                    } else {
                        Log.d("audiotest", "main card adapter : play audio");
                        pbProgressAudio.setVisibility(View.VISIBLE);
                        long finalDuration = duration;

                        detailsActivityInterface.playAudio(new AudioCallback() {
                            @Override
                            public void isAudioLoaded(boolean isLoaded) {
                                Log.e("isAudioLoaded", "isLoaded = " + isLoaded);
                                pbProgressAudio.setVisibility(View.GONE);
                                if (isLoaded) {
                                    storiesProgressView.setStoryDuration(finalDuration);
                                    storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
//                                    if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
//                                        storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
//                                    } else {
//                                        storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
//                                    }
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
    }

    public void resizeBullet(ListBulletsAdapter bulletsAdapter, Article content) {
        Log.e("resizeBullet", "------------------------------------");
        Log.e("resizeBullet", "--> " + content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData());
        Log.e("resizeBullet", "-position-> " + bulletsAdapter.getCurrentPosition());
        tvDummyBulletForSize.setText(content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData());
        tvDummyBulletForSize.post(() -> {
            if (rvDescList.getMeasuredHeight() < tvDummyBulletForSize.getMeasuredHeight()) {
                setHeight(rvDescList, tvDummyBulletForSize.getMeasuredHeight());
                bulletsAdapter.setHeight(tvDummyBulletForSize.getMeasuredHeight());
                tvDummyBulletForSize.setText("");
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

    private void nextBullet(ListBulletsAdapter bulletsAdapter, boolean tapEffect, Article article) {
        Log.e("kjandn", "nextBullet");
        if (tapEffect) {
            if (article.isSelected()) {
                Constants.auto_scroll = false;
            }
            if (Utils.isRTL()) {
                crossFadeAnimation(ivLeftArc, ivLeftArc, 300);
            } else {
                crossFadeAnimation(ivRightArc, ivRightArc, 300);
            }
        }
        if (bulletsAdapter != null) {
            int pos = bulletsAdapter.getCurrentPosition();
            if (pos == bulletsAdapter.getItemCount() - 1) {
                nextArticle();
            } else {
                pos++;
                rvDescList.smoothScrollToPosition(pos);
                bulletsAdapter.setCurrentPosition(pos);
            }
        }
    }

    private void prevBullet(CardBulletsAdapter bulletsAdapter, boolean tapEffect, Article article) {
        Log.e("kjandn", "prevBullet");
        if (tapEffect) {
            if (article.isSelected()) {
                Constants.auto_scroll = false;
            }
            if (Utils.isRTL()) {
                crossFadeAnimation(ivRightArc, ivRightArc, 300);
            } else {
                crossFadeAnimation(ivLeftArc, ivLeftArc, 300);
            }
        }
        if (bulletsAdapter != null) {
            if (bulletsAdapter.getCurrentPosition() == 0) {
                prevArticle();
            } else {
                int pos = bulletsAdapter.getCurrentPosition();
                rvDescList.smoothScrollToPosition(--pos);
                bulletsAdapter.setCurrentPosition(pos);
            }
        }
    }

    private void crossFadeAnimation(final View fadeInTarget, final View fadeOutTarget, long duration) {
        AnimatorSet mAnimationSet = new AnimatorSet();
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fadeOutTarget, View.ALPHA, 1f, 0f);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Utils.doHaptic(mPrefConfig);
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

    public void recycle() {
        Glide.with(mContext).clear(ivArticleBackImage);
        Glide.with(mContext).clear(ivArticleImage);
    }
}
