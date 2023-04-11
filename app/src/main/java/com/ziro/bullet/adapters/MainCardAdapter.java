package com.ziro.bullet.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.data.models.config.Static;

import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.storyMaker.HorizontalStoriesProgressView;
import com.ziro.bullet.storyMaker.StoriesProgressView;
import com.ziro.bullet.utills.BlurTransformation;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ShareToMainInterface {

    static final int MIN_DISTANCE = 150;
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;
    private static GoHome goHomeMainActivity;
    float Y_BUFFER = 10;
    float preX = 0f;
    float preY = 0f;
    private float x1, x2;
    private int layout;
    private Activity context;
    private List<Article> items;
    private int viewType = 0;
    private CardBulletsAdapter bulletsAdapterMain;
    private DetailsActivityInterface listener;
    private boolean isGotoFollowShow;
    private OnGotoChannelListener gotoChannelListener;
    private ShareBottomSheet shareBottomSheet;
    private PrefConfig mPrefConfig;
    private NewsCallback categoryCallback;
    private String direction = "";
    private String type;
    private HorizontalStoriesProgressView.StoriesListener listenerProgress;
    private Animation animFade, animFadeOut;
    private NativeAd mNativeAd;
    private ShareToMainInterface shareToMainInterface;
    private TempCategorySwipeListener swipeListener;
    private int currentArticlePosition = 0;
    private boolean isPlaying = false;
    private float opacity = 0.5f;
    private Handler handler = new Handler();
    private boolean swipeNextEnabled = true;
    private Runnable tapRunnable = () -> swipeNextEnabled = true;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;

    public MainCardAdapter(GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener, int layout, Activity context, List<Article> arrayList, boolean isGotoFollowShow, String type) {
        this.swipeListener = swipeListener;
        this.goHomeMainActivity = goHomeMainActivity;
        this.shareToMainInterface = shareToMainInterface;
        this.layout = layout;
        this.context = context;
        this.type = type;
        this.gotoChannelListener = gotoChannelListener;
        this.items = arrayList;
        this.isGotoFollowShow = isGotoFollowShow;
        this.categoryCallback = categoryCallback;
        this.mPrefConfig = new PrefConfig(context);
        this.shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view = null;
        if (type == CONTENT_TYPE) {
            view = LayoutInflater.from(context).inflate(layout, parent, false);
            ViewHolder holder = new ViewHolder(view);

            ViewTreeObserver viewTreeObserver = holder.itemView.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        holder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewHeight = holder.itemView.getHeight();
                        if (categoryCallback != null)
                            categoryCallback.onItemHeightMeasured(viewHeight);
                    }
                });
            }
            return holder;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.admob, parent, false);
            return new ADViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < items.size() && !TextUtils.isEmpty(items.get(position).getTitle()) && items.get(position).getTitle().equalsIgnoreCase("G_Ad")) {
            return AD_TYPE;
        } else
            return CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {
        if (items != null && items.size() > 0) {
            return items.size();
        } else {
            return 0;
        }
    }

    public void setCurrentArticlePosition(int position) {
        this.currentArticlePosition = position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder1, final int position) {
        int viewType = getItemViewType(position);
        if (items != null && items.size() > 0) {
            Article content = items.get(position);
            switch (viewType) {
                case CONTENT_TYPE:
                    if (content != null) {
                        if (holder1 instanceof ViewHolder) {
                            ViewHolder holder = (ViewHolder) holder1;

                            if (content.getType().equalsIgnoreCase("YOUTUBE")) {

                                holder.storiesProgressView.pause();
                                holder.storiesProgressView.setStoriesCount(1);
                                holder.storiesProgressView.getLayoutParams().height = 0;
                                holder.storiesProgressView.requestLayout();
                                holder.odd_imageBack.setVisibility(View.VISIBLE);
                                holder.odd_image.setVisibility(View.VISIBLE);
                                holder.youtubeMain.setVisibility(View.GONE);

                            } else {

                                holder.storiesProgressView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                                holder.storiesProgressView.requestLayout();

                                holder.play.setVisibility(View.GONE);
                                holder.odd_imageBack.setVisibility(View.VISIBLE);
                                holder.odd_image.setVisibility(View.VISIBLE);
                                holder.youtubeMain.setVisibility(View.GONE);
                            }

                            loadImage(content, holder);

                            if (mPrefConfig != null) {
                                Static mStatic = mPrefConfig.getHomeImage();
                                if (mStatic != null && mStatic.getHomeImage() != null) {
                                    opacity = mStatic.getHomeImage().getOpacity();
                                    if (!TextUtils.isEmpty(mStatic.getHomeImage().getLeft())) {
                                        Picasso.get()
                                                .load(mStatic.getHomeImage().getLeft())
                                                .into(holder.leftArc);
                                    } else {
                                        holder.leftArc.setImageResource(R.drawable.tap_left);
                                    }
                                    if (!TextUtils.isEmpty(mStatic.getHomeImage().getRight())) {
                                        Picasso.get()
                                                .load(mStatic.getHomeImage().getRight())
                                                .into(holder.rightArc);
                                    } else {
                                        holder.rightArc.setImageResource(R.drawable.tap_right);
                                    }
                                } else {
                                    holder.rightArc.setImageResource(R.drawable.tap_right);
                                    holder.leftArc.setImageResource(R.drawable.tap_left);
                                }
                            }

                            String time = Utils.getTimeAgo(Utils.getDate(content.getPublishTime()), context);
                            if (!TextUtils.isEmpty(time)) {
                                holder.time.setText(time);
                            }
                            if (content.getSource() != null) {
                                holder.source_name.setText(content.getSource().getName());
                                if (!TextUtils.isEmpty(content.getSource().getIcon())) {
                                    Picasso.get()
                                            .load(content.getSource().getIcon())
                                            .transform(new CropCircleTransformation())
                                            .into(holder.profile);
                                }
                            }

                            holder.dots.setOnClickListener(v -> {
                                Map<String, String> params = new HashMap<>();
                                params.put(Events.KEYS.ARTICLE_ID, content.getId());
                                AnalyticsEvents.INSTANCE.logEvent(context,
                                        params,
                                        Events.SHARE_CLICK);
                                bulletPause(holder);
                                if (shareBottomSheetPresenter != null)
                                    shareBottomSheetPresenter.share_msg(content.getId(), new ShareInfoInterface() {
                                        @Override
                                        public void response(ShareInfo shareInfo) {
                                            showBottomSheetDialog(shareInfo, position, content, dialog -> bulletResume(holder));
                                        }

                                        @Override
                                        public void error(String error) {

                                        }
                                    });
                            });
                            holder.profile.setOnClickListener(v -> {
                                detailsPage(content);
                            });
                            holder.source_name.setOnClickListener(v -> {
                                detailsPage(content);
                            });
                            holder.time.setOnClickListener(v -> {
                                detailsPage(content);
                            });

                            final int[] mPosition = {0};
                            if (content.getBullets() != null && content.getBullets().size() > 0) {
                                CardBulletsAdapter bulletsAdapter = new CardBulletsAdapter(false, null, type, false, context, content.getBullets(), new ListAdapterListener() {
                                    @Override
                                    public void verticalScrollList(boolean isEnable) {
                                        if (isEnable) {
                                            bulletResume(holder);
                                        } else {
                                            bulletPause(holder);
                                        }
                                    }

                                    @Override
                                    public void nextArticle(int position) {
                                        nextBullet(bulletsAdapterMain, holder, true, content);
                                    }

                                    @Override
                                    public void prevArticle(int position) {
                                        prevBullet(bulletsAdapterMain, holder, true, content);
                                    }

                                    @Override
                                    public void clickArticle(int position) {
                                        //Center Click
                                        if (content.getType().equalsIgnoreCase("YOUTUBE")) {
                                            if (content.isSelected()) {
                                                holder.play.setVisibility(View.GONE);
                                                holder.odd_imageBack.setVisibility(View.GONE);
                                                holder.odd_image.setVisibility(View.GONE);
                                                holder.youtubeMain.setVisibility(View.VISIBLE);

                                                if (!isPlaying) {
                                                    if (swipeListener != null)
                                                        swipeListener.swipe(true);
                                                    if (holder.youTubePlayer != null) {
                                                        holder.youTubePlayer.loadVideo(content.getLink(), 0);
                                                        holder.youTubePlayer.unMute();
                                                        holder.youTubePlayer.play();
                                                        isPlaying = true;
                                                    } else {
                                                        holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                                            @Override
                                                            public void onReady(@NotNull YouTubePlayer initializedYouTubePlayer) {
                                                                super.onReady(initializedYouTubePlayer);
                                                                holder.youTubePlayer = initializedYouTubePlayer;
                                                                holder.youTubePlayer.loadVideo(content.getLink(), 0);
                                                                holder.youTubePlayer.unMute();
                                                                holder.youTubePlayer.play();
                                                                isPlaying = true;
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    if (swipeListener != null)
                                                        swipeListener.swipe(false);
                                                    if (holder.youTubePlayer != null)
                                                        holder.youTubePlayer.pause();
                                                    isPlaying = false;
                                                }
                                            }
                                        } else if (!content.getTitle().equalsIgnoreCase("G_Ad")) {
                                            Map<String, String> params = new HashMap<>();
                                            params.put(Events.KEYS.ARTICLE_ID, content.getId());
                                            AnalyticsEvents.INSTANCE.logEvent(context,
                                                    params,
                                                    Events.ARTICLE_OPEN);
//                                            Intent intent = new Intent(context, WebViewActivity.class);
//                                            intent.putExtra("title", content.getSource().getName());
//                                            intent.putExtra("url", content.getLink());
//                                            context.startActivity(intent);
                                            Utils.openDetailView(context, content, type);
                                        }
                                    }
                                }, content, position);
                                LinearLayoutManager manager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                                holder.desc_list.setLayoutManager(manager);
//                                holder.desc_list.setHasFixedSize(true);
                                holder.desc_list.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                                holder.desc_list.setAdapter(bulletsAdapter);
                                holder.desc_list.setOnFlingListener(null);
                                PagerSnapHelper snapHelper = new PagerSnapHelper();
                                snapHelper.attachToRecyclerView(holder.desc_list);

                                holder.desc_list.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                                    @Override
                                    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                                        switch (e.getAction()) {
                                            case MotionEvent.ACTION_DOWN:
                                                x1 = e.getX();
                                                holder.desc_list.getParent().requestDisallowInterceptTouchEvent(true);
                                                break;
                                            case MotionEvent.ACTION_UP:
                                                if (bulletsAdapterMain != null) {
                                                    x2 = e.getX();
                                                    float deltaX = x2 - x1;
                                                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                                                        if (x2 > x1) {
                                                            if (content.isSelected() && bulletsAdapterMain.getCurrentPosition() == 0 && position == currentArticlePosition) {
                                                                if (swipeNextEnabled) {
                                                                    swipeNextEnabled = false;
                                                                    handler.postDelayed(tapRunnable, 200);
                                                                    prevArticle(holder);
                                                                }
                                                            }
                                                        } else {
                                                            if (content.isSelected() && bulletsAdapterMain.getCurrentPosition() == (bulletsAdapterMain.getItemCount() - 1) && position == currentArticlePosition) {
                                                                handler.removeCallbacks(tapRunnable);

                                                                if (swipeNextEnabled) {
                                                                    swipeNextEnabled = false;
                                                                    handler.postDelayed(tapRunnable, 200);
                                                                    nextArticle(holder);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            case MotionEvent.ACTION_MOVE:
                                                if (Math.abs(e.getX() - preX) > Math.abs(e.getY() - preY)) {
                                                    holder.desc_list.getParent().requestDisallowInterceptTouchEvent(true);
                                                } else if (Math.abs(e.getY() - preY) > Y_BUFFER) {
                                                    holder.desc_list.getParent().requestDisallowInterceptTouchEvent(false);
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

                                holder.desc_list.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                holder.desc_list.requestLayout();
                                RecyclerView.OnScrollListener scrollListener;
                                holder.desc_list.addOnScrollListener(scrollListener = new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                            if (mPosition[0] != -1) {
                                                content.setLastPosition(mPosition[0]);
                                                bulletsAdapter.setCurrentPosition(mPosition[0]);
                                                if (!TextUtils.isEmpty(content.getFragTag()) && content.isSelected() && content.getFragTag().equalsIgnoreCase("id_" + Constants.visiblePage)) {
                                                    Constants.auto_scroll = false;
                                                    playAudio(holder, bulletsAdapter, content);
                                                } else {
                                                    resizeBullet(holder, bulletsAdapter, content);
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


                                holder.speaker.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Constants.mute_disable) {
                                            Utils.showSnacky(holder.speaker, context.getString(R.string.speech_not_available));
                                            return;
                                        }
                                        if (!Constants.muted) {
                                            Constants.muted = true;
                                            Constants.ReelMute = true;
                                            if (goHomeMainActivity != null)
                                                goHomeMainActivity.sendAudioEvent("mute");
                                            Map<String, String> params = new HashMap<>();
                                            params.put(Events.KEYS.ARTICLE_ID, content.getId());
                                            AnalyticsEvents.INSTANCE.logEvent(context,
                                                    params,
                                                    Events.MUTE_VIDEO_FEED);
                                        } else {
                                            Constants.muted = false;
                                            Constants.ReelMute = false;
                                            if (goHomeMainActivity != null) {
                                                goHomeMainActivity.sendAudioEvent("unmute");
                                                goHomeMainActivity.sendAudioEvent("play");
                                            }
                                            playAudio(holder, bulletsAdapter, content);
                                            Map<String, String> params = new HashMap<>();
                                            params.put(Events.KEYS.ARTICLE_ID, content.getId());
                                            AnalyticsEvents.INSTANCE.logEvent(context,
                                                    params,
                                                    Events.UNMUTE);
                                        }
                                        updateMuteButtons(holder);
                                    }
                                });


                                if (!TextUtils.isEmpty(content.getFragTag()) && content.isSelected() && content.getFragTag().equalsIgnoreCase("id_" + Constants.visiblePage)) {
                                    bulletsAdapterMain = bulletsAdapter;

                                    if (gotoChannelListener != null) {
                                        gotoChannelListener.onArticleSelected(content);
                                    }

                                    if (content.getType().equalsIgnoreCase("IMAGE")) {
                                        if (swipeListener != null) swipeListener.swipe(false);
                                        holder.speaker.setVisibility(View.VISIBLE);
                                        holder.equilizer.setVisibility(View.VISIBLE);
                                        holder.play.setVisibility(View.GONE);
                                        imageCase(position, holder, bulletsAdapter, content);

                                        Constants.mute_disable = content.isMute();
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
                                        updateMuteButtons(holder);

                                        if (bulletsAdapter.getCurrentPosition() == 0) {
                                            playAudio(holder, bulletsAdapter, content);
                                        }
                                    } else {
                                        Log.d("audiotest", " cardadas : stop_destroy");
                                        if (goHomeMainActivity != null)
                                            goHomeMainActivity.sendAudioEvent("stop_destroy");
                                        holder.storiesProgressView.setStoriesListener(null);
                                        holder.storiesProgressView.destroy();
                                        holder.storiesProgressView.setVisibility(View.INVISIBLE);
                                        holder.progressAudio.setVisibility(View.GONE);
                                        holder.speaker.setVisibility(View.GONE);
                                        holder.equilizer.setVisibility(View.GONE);
                                        holder.play.setVisibility(View.VISIBLE);
                                        holder.equilizer.setImageResource(R.drawable.static_equilizer);
                                        if (holder.youTubePlayer != null) {
                                            holder.youTubePlayer.loadVideo(content.getLink(), 0);
                                            holder.youTubePlayer.pause();
                                            holder.youTubePlayer.mute();
                                        }
                                    }

                                } else {
                                    holder.storiesProgressView.setStoriesListener(null);
                                    holder.storiesProgressView.destroy();
                                    holder.storiesProgressView.setVisibility(View.INVISIBLE);
                                    holder.desc_list.scrollToPosition(content.getLastPosition());
                                    holder.progressAudio.setVisibility(View.GONE);
                                    holder.equilizer.setVisibility(View.GONE);
                                    holder.equilizer.setImageResource(R.drawable.static_equilizer);
                                    holder.speaker.setVisibility(View.GONE);
                                    holder.play.setVisibility(View.GONE);
                                    if (holder.youTubePlayer != null) {
                                        holder.youTubePlayer.pause();
                                        holder.youTubePlayer.mute();
                                    }
                                    isPlaying = false;
                                }
                            }
                        }
                    }
                    break;
                case AD_TYPE:
                    Log.d("audiotest", "afasdasdas : stop_destroy");
                    if (goHomeMainActivity != null)
                        goHomeMainActivity.sendAudioEvent("stop_destroy");
                    break;
            }
        }
    }

    public void updateMuteButtons(ViewHolder holder) {
        if (!Constants.mute_disable) {
            holder.speaker.setAlpha(1f);
            if (!Constants.muted) {
                holder.speaker.setImageResource(R.drawable.ic_speaker_unmute);
                Glide.with(BulletApp.getInstance()).load(R.raw.equlizer).into(holder.equilizer);
            } else {
                holder.speaker.setImageResource(R.drawable.ic_speaker_mute);
                holder.equilizer.setImageResource(R.drawable.static_equilizer);
            }
        } else {
            holder.speaker.setAlpha(0.5f);
        }
    }


    private void nextArticle(ViewHolder holder) {
        Log.e("kjandn", "nextArticle");
        Constants.auto_scroll = true;
        holder.dummyBulletForSize.setText("");
        holder.desc_list.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.desc_list.requestLayout();
        if (categoryCallback != null) {
            int pos = currentArticlePosition;
            pos++;
            Log.d("TAG", "prevArticle:  position to = " + pos);
            categoryCallback.nextPosition(pos);
        }
        if (holder.youTubePlayer != null) {
            holder.youTubePlayer.pause();
            holder.youTubePlayer.mute();
        }
        isPlaying = false;
    }

    private void prevArticle(ViewHolder holder) {
        Constants.auto_scroll = true;
        holder.dummyBulletForSize.setText("");
        holder.desc_list.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.desc_list.requestLayout();
        Log.e("kjandn", "prevArticle");
        if (categoryCallback != null) {
            int pos = currentArticlePosition;
            pos--;
            Log.d("TAG", "prevArticle:  position to = " + pos);
            categoryCallback.nextPosition(pos);
        }
        if (holder.youTubePlayer != null) {
            holder.youTubePlayer.pause();
            holder.youTubePlayer.mute();
        }
        isPlaying = false;
    }


    public void bulletResume(ViewHolder holder) {
        if (holder != null && context != null) {
            holder.storiesProgressView.resume();
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("resume");
        }
    }

    public void bulletPause(ViewHolder holder) {
        if (holder != null && context != null) {
            holder.storiesProgressView.pause();
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("pause");
        }
    }

    private void detailsPage(Article content) {
        if (content != null) {
            if (isGotoFollowShow && !type.equalsIgnoreCase("ARCHIVE")) {
                if (gotoChannelListener != null && content.getSource() != null) {
                    if (mPrefConfig != null) {
                        mPrefConfig.setSrcLang(content.getSource().getLanguage());
                        mPrefConfig.setSrcLoc(content.getSource().getCategory());
                    }
                    gotoChannelListener.onItemClicked(TYPE.SOURCE, content.getSource().getId(), content.getSource().getName(), content.getSource().isFavorite());
                } else if (content.getAuthor() != null && content.getAuthor().size() > 0) {
                    if (content.getAuthor().get(0) != null) {
                        Utils.openAuthor(context, content.getAuthor().get(0));
                    }
                }
            }
        }
    }

    public void loadImage(Article currentArticle, ViewHolder holder) {
        if (currentArticle.getBullets() != null && currentArticle.getBullets().size() > 0) {
            if (!TextUtils.isEmpty(currentArticle.getBullets().get(0).getImage())) {
                Glide.with(BulletApp.getInstance())
                        .load(currentArticle.getBullets().get(0).getImage())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .dontAnimate()
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                        .override(Constants.targetWidth, Constants.targetHeight)
                        .into(holder.odd_imageBack);

                Glide.with(BulletApp.getInstance())
                        .load(currentArticle.getBullets().get(0).getImage())
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.odd_image.setImageResource(R.drawable.img_place_holder);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }

                        })
                        .error(R.drawable.img_place_holder)
                        .placeholder(R.drawable.img_place_holder)
                        .override(Constants.targetWidth, Constants.targetHeight)
                        .into(holder.odd_image);

            } else {
                holder.odd_image.setImageResource(R.drawable.img_place_holder);
            }
        } else {
            holder.odd_image.setImageResource(R.drawable.img_place_holder);
        }
    }

    public void imageCase(int position, ViewHolder holder, CardBulletsAdapter bulletsAdapter, Article content) {
        if (position > -1 && holder != null && bulletsAdapter != null && content != null) {

            Constants.ProgressBarDrawable = R.drawable.bullet_selector_green;
            Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_green_2;

            holder.storiesProgressView.setStoriesCount(content.getBullets().size());
            holder.storiesProgressView.setStoriesListener(listenerProgress = new HorizontalStoriesProgressView.StoriesListener() {
                @Override
                public void onFirst() {

                }

                @Override
                public void onNext() {
                    if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                        //GOTO NEXT BULLET
                        nextBullet(bulletsAdapter, holder, false, content);
                    } else {
                        //Auto Swipe
                        if (Constants.auto_scroll) {
//                            Utils.logEvent(context, "list_next_auto");
//                            //GOTO NEXT ARTICLE
//                            if (categoryCallback != null) {
//                                int pos = position;
//                                pos++;
//                                categoryCallback.nextPosition(pos);
//                            }
                        } else {
                            //GOTO NEXT BULLET
                            nextBullet(bulletsAdapter, holder, false, content);
                        }
                    }
                }

                @Override
                public void onPrev() {
                }

                @Override
                public void onComplete() {
                    //Auto Swipe
                    if (Constants.auto_scroll) {
                        Utils.logEvent(context, "list_next_auto");
                    } else {
                        Utils.logEvent(context, "list_next_scroll");
                    }
                    Constants.auto_scroll = true;
                    if (categoryCallback != null) {
                        int pos = position;
                        pos++;
                        categoryCallback.nextPosition(pos);
                    }
                }
            });

            holder.storiesProgressView.setVisibility(View.VISIBLE);
        }
    }

    public void playAudio(ViewHolder holder, CardBulletsAdapter bulletsAdapter, Article content) {
        if (holder != null && bulletsAdapter != null && content != null) {
            if (layout == R.layout.collapse_card_item) {

            } else {
                if (!TextUtils.isEmpty(direction)) {
                    switch (direction) {
                        case "rtl":
                        case "ltr":
                            Map<String, String> params = new HashMap<>();
                            params.put(Events.KEYS.ARTICLE_ID, content.getId());
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    params,
                                    Events.ARTICLE_SWIPE);
                            break;
                    }
                }
            }

            if (bulletsAdapter.getCurrentPosition() < content.getBullets().size()) {
                resizeBullet(holder, bulletsAdapter, content);
                long duration = 0;
                if (content.getBullets().get(bulletsAdapter.getCurrentPosition()).getDuration() == 0)
                    duration = content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData().length() * 100;
                else {
                    duration = (long) (content.getBullets().get(bulletsAdapter.getCurrentPosition()).getDuration() / Constants.READING_SPEED_RATES.get(Constants.reading_speed));
                }
                duration = duration + 1500;

                if (Constants.muted || content.isMute()) {

                    holder.progressAudio.setVisibility(View.GONE);
                    holder.storiesProgressView.setStoryDuration(duration);
                    if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                        if (mPrefConfig.isBulletsAutoPlay()) {
                            holder.storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                        } else {
                            holder.storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                        }
                    } else {
                        if (mPrefConfig.isBulletsAutoPlay()) {
                            holder.storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                        } else {
                            holder.storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                        }
                    }

                    long finalDuration1 = duration;
                    listener.playAudio(
                            new AudioCallback() {
                                @Override
                                public void isAudioLoaded(boolean isLoaded) {
                                    Log.e("isAudioLoaded", "isLoaded = " + isLoaded);
                                    holder.progressAudio.setVisibility(View.GONE);
                                    if (isLoaded) {
                                        holder.storiesProgressView.setStoryDuration(finalDuration1);
                                        if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                                            if (mPrefConfig.isBulletsAutoPlay()) {
                                                holder.storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                                            } else {
                                                holder.storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                                            }
                                        } else {
                                            if (mPrefConfig.isBulletsAutoPlay()) {
                                                holder.storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                                            } else {
                                                holder.storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
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
                    holder.progressAudio.setVisibility(View.VISIBLE);
                    long finalDuration = duration;
                    listener.playAudio(new AudioCallback() {
                        @Override
                        public void isAudioLoaded(boolean isLoaded) {
                            Log.e("isAudioLoaded", "isLoaded = " + isLoaded);
                            holder.progressAudio.setVisibility(View.GONE);
                            if (isLoaded) {
                                holder.storiesProgressView.setStoryDuration(finalDuration);
                                if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                                    if (mPrefConfig.isBulletsAutoPlay()) {
                                        holder.storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                                    } else {
                                        holder.storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                                    }
                                } else {
                                    if (mPrefConfig.isBulletsAutoPlay()) {
                                        holder.storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                                    } else {
                                        holder.storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
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
                }
            }
        }
    }

    public void resizeBullet(ViewHolder holder, CardBulletsAdapter bulletsAdapter, Article content) {
        Log.e("resizeBullet", "------------------------------------");
        Log.e("resizeBullet", "--> " + content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData());
        Log.e("resizeBullet", "-position-> " + bulletsAdapter.getCurrentPosition());
        holder.dummyBulletForSize.setText(content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData());
        holder.dummyBulletForSize.post(new Runnable() {
            @Override
            public void run() {
                if (holder.desc_list.getMeasuredHeight() < holder.dummyBulletForSize.getMeasuredHeight()) {
                    setHeight(holder.desc_list, holder.dummyBulletForSize.getMeasuredHeight());
                    bulletsAdapter.setHeight(holder.dummyBulletForSize.getMeasuredHeight());
                    holder.dummyBulletForSize.setText("");
                }
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

    private void nextBullet(CardBulletsAdapter bulletsAdapter, ViewHolder holder, boolean tapEffect, Article article) {
        Log.e("kjandn", "nextBullet");
        if (tapEffect) {
            if (article.isSelected()) {
                Constants.auto_scroll = false;
            }
            crossFadeAnimation(holder.rightArc, holder.rightArc, 300);
        }
        if (bulletsAdapter != null && holder != null) {
            int pos = bulletsAdapter.getCurrentPosition();
            if (pos == bulletsAdapter.getItemCount() - 1) {
                nextArticle(holder);
            } else {
                pos++;
                holder.desc_list.smoothScrollToPosition(pos);
                bulletsAdapter.setCurrentPosition(pos);
            }
        }
    }

    private void prevBullet(CardBulletsAdapter bulletsAdapter, ViewHolder holder, boolean tapEffect, Article article) {
        Log.e("kjandn", "prevBullet");
        if (tapEffect) {
            if (article.isSelected()) {
                Constants.auto_scroll = false;
            }
            crossFadeAnimation(holder.leftArc, holder.leftArc, 300);
        }
        if (bulletsAdapter != null) {
            if (bulletsAdapter.getCurrentPosition() == 0) {
                prevArticle(holder);
            } else {
                int pos = bulletsAdapter.getCurrentPosition();
                holder.desc_list.smoothScrollToPosition(--pos);
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

    private int getSnapPosition(RecyclerView recyclerView, SnapHelper snapHelper) {

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null)
            return RecyclerView.NO_POSITION;

        View snapView = snapHelper.findSnapView(layoutManager);
        if (snapView == null)
            return RecyclerView.NO_POSITION;

        return layoutManager.getPosition(snapView);
    }

    private void LTR_Slide(View view) {
        if (view == null) return;
        animFade = AnimationUtils.loadAnimation(context, R.anim.left_to_right);
        animFade.setDuration(120);
        animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out_fast);
        animFade.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                // when fadeout animation ends, fade in your second image
                view.startAnimation(animFadeOut);
                view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(animFade);
    }

    private void RTL_Slide(View view) {
        if (view == null) return;
        animFade = AnimationUtils.loadAnimation(context, R.anim.rtl);
        animFade.setDuration(120);
        animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out_fast);
        animFade.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                // when fadeout animation ends, fade in your second image
                view.startAnimation(animFadeOut);
                view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(animFade);
    }

    public void setListener(DetailsActivityInterface listener) {
        this.listener = listener;
    }

    @Override
    public void removeItem(String id, int position) {
        if (items != null && items.size() > 0) {
            if (!TextUtils.isEmpty(items.get(position).getId()) && !TextUtils.isEmpty(id)) {
                if (items.get(position).getId().equalsIgnoreCase(id)) {
                    boolean isSelected = items.get(position).isSelected();
                    items.remove(position);
                    notifyDataSetChanged();
                    if (position < items.size()) {
                        if (isSelected)
                            items.get(position).setSelected(true);
                    }
                    notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void unarchived() {

    }

    @Override
    public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
        Log.e("TabSelected", "here type --> " + type);
        if (gotoChannelListener != null) {
            gotoChannelListener.onItemClicked(type, id, name, favorite);
        }
    }

    private void showBottomSheetDialog(ShareInfo shareInfo, int position, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet(context, shareToMainInterface, isGotoFollowShow, type);
        }
        shareBottomSheet.show(article, onDismissListener, shareInfo);
    }

    public void dismissBottomSheet() {
        if (shareBottomSheet != null) {
            shareBottomSheet.hide();
        }
    }

    private void refreshAd(ADViewHolder holder1) {
        Log.e("refreshAd", "refreshAd : ");
        if (holder1 != null) {
            AdLoader.Builder builder = null;
            if (BuildConfig.DEBUG)
                builder = new AdLoader.Builder(context, Constants.ADMOB_AD_UNIT_ID);
            else {
                if (mPrefConfig.getAds() != null && mPrefConfig.getAds().getAdmob() != null)
                    builder = new AdLoader.Builder(context, mPrefConfig.getAds().getAdmob().getFeed());
            }

            if (builder != null) {

                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        if (mNativeAd != null) {
                            nativeAd.destroy();
                        }
                        mNativeAd = nativeAd;
                        NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_expand_card, null, false);
                        populateUnifiedNativeAdView(nativeAd, adView, holder1);
                        holder1.fl_adplaceholder.removeAllViews();
                        holder1.fl_adplaceholder.addView(adView);
                    }
                });

                VideoOptions videoOptions = new VideoOptions.Builder()
                        .setStartMuted(Constants.muted)
                        .build();

                NativeAdOptions adOptions = new NativeAdOptions.Builder()
                        .setVideoOptions(videoOptions)
                        .build();

                builder.withNativeAdOptions(adOptions);

                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.d("TAG", "onAdLoaded() called");
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d("TAG", "onAdFailedToLoad() called with: loadAdError = [" + loadAdError + "]");
                    }
                }).build();

                adLoader.loadAd(new AdRequest.Builder().build());
            }
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView, ADViewHolder adHolder) {

        if (adHolder != null) {
            ADViewHolder holder1 = (ADViewHolder) adHolder;

            // Set the media view. Media content will be automatically populated in the media view once
            // adView.setNativeAd() is called.
            MediaView mediaView = adView.findViewById(R.id.ad_media);
            adView.setMediaView(mediaView);

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
            TextView ad_txt = adView.findViewById(R.id.ad_txt);

            // The headline is guaranteed to be in every UnifiedNativeAd.
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.GONE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

//            if (nativeAd.getIcon() == null) {
//                adView.getIconView().setVisibility(View.GONE);
//            } else {
//                ((ImageView) adView.getIconView()).setImageDrawable(
//                        nativeAd.getIcon().getDrawable());
//                adView.getIconView().setVisibility(View.GONE);
//            }

//            if (nativeAd.getPrice() == null) {
//                adView.getPriceView().setVisibility(View.INVISIBLE);
//            } else {
//                adView.getPriceView().setVisibility(View.VISIBLE);
//                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//            }
//
//            if (nativeAd.getStore() == null) {
//                adView.getStoreView().setVisibility(View.INVISIBLE);
//            } else {
//                adView.getStoreView().setVisibility(View.VISIBLE);
//                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//            }

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
                ad_txt.setText("Advertisement");
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
                ad_txt.setText("AD");
            }

//            if (nativeAd.getAdvertiser() == null) {
//                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
//            } else {
//                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
//                adView.getAdvertiserView().setVisibility(View.VISIBLE);
//            }

            ImageView bck = adView.findViewById(R.id.ad_imageBack);
            if (nativeAd.getImages() != null && nativeAd.getImages().size() > 0) {
                if (nativeAd.getImages().get(0).getUri() != null) {
                    Picasso.get()
                            .load(nativeAd.getImages().get(0).getUri())
                            .transform(new jp.wasabeef.picasso.transformations.BlurTransformation(context, 25, 3))
                            .into(bck);
                }
            }
            adView.setNativeAd(nativeAd);
            try {
                VideoController vc = nativeAd.getMediaContent().getVideoController();
                if (holder1 != null) {
                    if (vc.hasVideoContent()) {
                        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                            @Override
                            public void onVideoEnd() {
                                super.onVideoEnd();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ADViewHolder extends RecyclerView.ViewHolder {
        //AD-MOB START
        StoriesProgressView progress;
        FrameLayout fl_adplaceholder;
        //AD-MOB END

        public ADViewHolder(View itemView) {
            super(itemView);

            refreshAd(this);
            //AD-MOB START
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
            progress = itemView.findViewById(R.id.progress);
            //AD-MOB END
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public HorizontalStoriesProgressView storiesProgressView;
        FrameLayout fl_adplaceholder;
        RelativeLayout ad_mob;
        RelativeLayout button;
        RoundedImageView profile;
        ImageView odd_imageBack;
        ImageView odd_image;
        TextView source_name;
        TextView time;
        ImageView dots, image2;
        ImageView speaker;
        RelativeLayout leftRightPanel;
        RecyclerView desc_list;
        ImageView leftArc, rightArc;
        ProgressBar progressAudio;
        CardView youtubeMain;
        YouTubePlayerView youTubePlayerView;
        RelativeLayout rlDummy;
        ImageView play;
        ImageView equilizer;
        TextView dummyBulletForSize;
        private YouTubePlayer youTubePlayer;


        public ViewHolder(View itemView) {
            super(itemView);
            speaker = itemView.findViewById(R.id.speaker);
            dummyBulletForSize = itemView.findViewById(R.id.dummyBulletForSize);
            leftRightPanel = itemView.findViewById(R.id.leftRightPanel);
            play = itemView.findViewById(R.id.play);
            youtubeMain = itemView.findViewById(R.id.youtubeMain);
            equilizer = itemView.findViewById(R.id.equilizer);
            youTubePlayerView = itemView.findViewById(R.id.youtube_view);
            button = itemView.findViewById(R.id.button);
            progressAudio = itemView.findViewById(R.id.progressAudio);
            leftArc = itemView.findViewById(R.id.leftArc);
            rightArc = itemView.findViewById(R.id.rightArc);
            odd_image = itemView.findViewById(R.id.odd_image);
            odd_imageBack = itemView.findViewById(R.id.odd_imageBack);
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
            ad_mob = itemView.findViewById(R.id.ad_mob);
            profile = itemView.findViewById(R.id.profile);
            image2 = itemView.findViewById(R.id.image2);
            desc_list = itemView.findViewById(R.id.desc_list);
            time = itemView.findViewById(R.id.time);
            source_name = itemView.findViewById(R.id.source_name);
            dots = itemView.findViewById(R.id.dotImg);
            storiesProgressView = itemView.findViewById(R.id.stories);
            rlDummy = itemView.findViewById(R.id.dummy);

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NotNull YouTubePlayer initializedYouTubePlayer) {
                    super.onReady(initializedYouTubePlayer);
                    youTubePlayer = initializedYouTubePlayer;
                }
            });
        }
    }
}
