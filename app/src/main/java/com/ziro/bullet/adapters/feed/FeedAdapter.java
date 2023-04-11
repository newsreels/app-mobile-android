package com.ziro.bullet.adapters.feed;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.NewFeed.newHomeArticle.NewHomeArticlesViewHolder;
import com.ziro.bullet.adapters.schedule.ReelViewHolder;
import com.ziro.bullet.adapters.schedule.ScheduledListAdapter;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.MediaMeta;

import java.util.List;

import im.ene.toro.CacheManager;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterCallback, CacheManager {
    private static final int TYPE_CARD = 0;
    private static final int TYPE_LIST = 1;
    private static final int TYPE_YOUTUBE = 2;
    private static final int TYPE_VIDEO = 3;
    private static final int TYPE_AD_GOOGLE = 4;
    private static final int TYPE_AD_FACEBOOK = 5;
    private static final int TYPE_REEL = 6;

    private List<Article> items;
    private AppCompatActivity context;

    private ShareToMainInterface shareToMainInterface;
    private TempCategorySwipeListener swipeListener;
    private NewsCallback categoryCallback;
    private OnGotoChannelListener gotoChannelListener;
    private DetailsActivityInterface detailsActivityInterface;
    private GoHome goHomeMainActivity;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;

    private PrefConfig mPrefConfig;
    private boolean isPostArticle = false;
    private int currentArticlePosition = 0;
    private boolean isGotoFollowShow;
    private String type;
    private ShareBottomSheet shareBottomSheet;
    private CommentClick mCommentClick;
    private AdFailedListener adFailedListener;
    private Lifecycle lifecycle;

    // updating from outside
//    public TempCategoryFragment tempCategoryFragment;

    public FeedAdapter(CommentClick mCommentClick, boolean isPostArticle, AppCompatActivity context, List<Article> items, String type, boolean isGotoFollowShow, DetailsActivityInterface detailsActivityInterface,
                       GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback,
                       OnGotoChannelListener gotoChannelListener, ShowOptionsLoaderCallback showOptionsLoaderCallback,
                       AdFailedListener adFailedListener, Lifecycle lifecycle) {
        this.context = context;
        this.items = items;
        this.swipeListener = swipeListener;
        this.goHomeMainActivity = goHomeMainActivity;
        this.shareToMainInterface = shareToMainInterface;
        this.gotoChannelListener = gotoChannelListener;
        this.categoryCallback = categoryCallback;
        this.detailsActivityInterface = detailsActivityInterface;
        this.isGotoFollowShow = isGotoFollowShow;
        this.type = type;
        this.mCommentClick = mCommentClick;
        this.isPostArticle = isPostArticle;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.adFailedListener = adFailedListener;
        this.lifecycle = lifecycle;
        mPrefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_large_card_article, parent, false);
                LargeCardViewHolder largeCardViewHolder = new LargeCardViewHolder(
                        false,
                        mCommentClick,
                        type,
                        isPostArticle,
                        view,
                        context,
                        this,
                        mPrefConfig,
                        goHomeMainActivity,
                        shareToMainInterface,
                        swipeListener,
                        categoryCallback,
                        gotoChannelListener,
                        detailsActivityInterface,
                        showOptionsLoaderCallback
                );

                ViewTreeObserver viewTreeObserver = largeCardViewHolder.itemView.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            largeCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = largeCardViewHolder.itemView.getHeight();
                            largeCardViewHolder.statusView.getLayoutParams().height = viewHeight;
                            largeCardViewHolder.statusView.requestLayout();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return largeCardViewHolder;
            case TYPE_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item_new, parent, false);
                NewHomeArticlesViewHolder homeArticlesViewHolder = new NewHomeArticlesViewHolder(context, view, mCommentClick, showOptionsLoaderCallback, this, detailsActivityInterface);
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_small_card, parent, false);
//                SmallCardViewHolder smallCardViewHolder = new SmallCardViewHolder(false, mCommentClick, type, view, context,
//                        this, mPrefConfig, goHomeMainActivity,
//                        shareToMainInterface, swipeListener, categoryCallback,
//                        gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
//
//                ViewTreeObserver vtoSmall = smallCardViewHolder.itemView.getViewTreeObserver();
//                if (vtoSmall.isAlive()) {
//                    vtoSmall.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            smallCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            int viewHeight = smallCardViewHolder.itemView.getHeight();
//                            smallCardViewHolder.statusView.getLayoutParams().height = viewHeight;
//                            smallCardViewHolder.statusView.requestLayout();
//                            if (categoryCallback != null)
//                                categoryCallback.onItemHeightMeasured(viewHeight);
//                        }
//                    });
//                }
                return homeArticlesViewHolder;
            case TYPE_YOUTUBE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_card, parent, false);
                YoutubeViewHolder youtubeViewHolder = new YoutubeViewHolder(false, mCommentClick, type, isPostArticle, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback, lifecycle);
//                youtubeViewHolder.tempCategoryFragment = tempCategoryFragment;
                ViewTreeObserver vtoYtube = youtubeViewHolder.itemView.getViewTreeObserver();
                if (vtoYtube.isAlive()) {
                    vtoYtube.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            youtubeViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = youtubeViewHolder.itemView.getHeight();
                            youtubeViewHolder.statusView.getLayoutParams().height = viewHeight;
                            youtubeViewHolder.statusView.requestLayout();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return youtubeViewHolder;

            case TYPE_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_card, parent, false);
                VideoViewHolder videoViewHolder = new VideoViewHolder(mCommentClick, type, isPostArticle, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);

                ViewTreeObserver vtoVideo = videoViewHolder.itemView.getViewTreeObserver();
                if (vtoVideo.isAlive()) {
                    vtoVideo.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            videoViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = videoViewHolder.itemView.getHeight();
//                            videoViewHolder.statusView.getLayoutParams().height = viewHeight;
//                            videoViewHolder.statusView.requestLayout();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }

//                try {
//                    videoViewHolder.itemView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (videoViewHolder.getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
//                                Article article = items.get(videoViewHolder.getAbsoluteAdapterPosition());
//                                if (article != null) {
//                                    MediaMeta mediaMeta = article.getMediaMeta();
//                                    if (mediaMeta != null) {
//                                        resizeVideoView(videoViewHolder, mediaMeta.getHeight(), mediaMeta.getWidth());
//                                    }
//                                }
//                            }
//                        }
//                    });

//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                return videoViewHolder;
            case TYPE_AD_GOOGLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ad, parent, false);
                return new AdViewHolder(false, view, context, mPrefConfig, adFailedListener);
            case TYPE_AD_FACEBOOK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_fb_ad, parent, false);
                return new FacebookAdViewHolder(false, view, context, mPrefConfig, adFailedListener);
            case TYPE_REEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_reel_card, parent, false);
                ReelViewHolder reelViewHolder = new ReelViewHolder(null, view, mPrefConfig, context, this, categoryCallback, detailsActivityInterface, null, ScheduledListAdapter.TYPE.PREVIEW);
                ViewTreeObserver viewTreeObserverReel = reelViewHolder.itemView.getViewTreeObserver();
                if (viewTreeObserverReel.isAlive()) {
                    viewTreeObserverReel.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            reelViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = reelViewHolder.itemView.getHeight();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return reelViewHolder;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    private void resizeVideoView(VideoViewHolder videoViewHolder, double mVideoHeight, double mVideoWidth) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int cardWidth = displayMetrics.widthPixels - (2 * context.getResources().getDimensionPixelSize(R.dimen._15sdp));

        int screenHeight;
        if (TextUtils.isEmpty(type))
            screenHeight = displayMetrics.heightPixels - (context.getResources().getDimensionPixelSize(R.dimen._150sdp));
        else
            screenHeight = displayMetrics.heightPixels - (context.getResources().getDimensionPixelSize(R.dimen._220sdp));


        Log.e("@#@#@#", screenHeight + "");
        int cardHeight = (int) ((mVideoHeight * cardWidth) / mVideoWidth);
        if (cardHeight > screenHeight) {
            cardHeight = screenHeight;
        }
        //if (!videoViewHolder.isHeightSet && cardHeight > 0 && videoViewHolder.cardMain.getHeight() < cardHeight) {
        videoViewHolder.cardMain.getLayoutParams().height = cardHeight + context.getResources().getDimensionPixelSize(R.dimen._8sdp);
//            videoViewHolder.cardMain.getLayoutParams().width = cardWidth;
        // videoViewHolder.cardMain.requestLayout();
        videoViewHolder.statusView.getLayoutParams().height = cardHeight + (context.getResources().getDimensionPixelSize(R.dimen._100sdp));
        //videoViewHolder.statusView.getLayoutParams().width = cardWidth;
        videoViewHolder.statusView.requestLayout();
        //videoViewHolder.isHeightSet = true;
        Log.e("@#@#@#", "QQQQQQ = " + cardWidth + " X " + cardHeight);
        videoViewHolder.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        videoViewHolder.playerView.requestLayout();
        //}
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        View viewGroup = holder.itemView;
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewGroup.getLayoutParams();
        if (position == 0) {
            params.setMargins(params.getMarginStart(), (int) context.getResources().getDimension(R.dimen._10sdp), params.getMarginEnd(), params.bottomMargin);
        }
        viewGroup.setLayoutParams(params);

        Article article = items.get(position);

        if (holder instanceof LargeCardViewHolder) {
            ((LargeCardViewHolder) holder).bind(position, article);
        } else if (holder instanceof SmallCardViewHolder) {
            ((SmallCardViewHolder) holder).bind(position, article);
        } else if (holder instanceof NewHomeArticlesViewHolder) {
            ((NewHomeArticlesViewHolder) holder).onBind(article, position);
        } else if (holder instanceof YoutubeViewHolder) {
            ((YoutubeViewHolder) holder).bind(position, article);
        } else if (holder instanceof ReelViewHolder) {
            ((ReelViewHolder) holder).bind(position, article);
        } else if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).bind(position, article);
            try {
                if (article != null) {
                    MediaMeta mediaMeta = article.getMediaMeta();
                    Log.e("@#@#@#", "" + article.getId());
                    if (mediaMeta != null
                            && mediaMeta.getHeight() > 0 &&
                            mediaMeta.getWidth() > 0
                    ) {
                        resizeVideoView(((VideoViewHolder) holder), mediaMeta.getHeight(), mediaMeta.getWidth());
                    } else {
                        Log.e("@#@#@#", "failed");
                    }
                } else {
                    Log.e("@#@#@#", "failed");
                }
            } catch (Exception e) {
                Log.e("@#@#@#", "failed");
                e.printStackTrace();
            }
        } else if (holder instanceof AdViewHolder) {
            ((AdViewHolder) holder).bind();
        } else if (holder instanceof FacebookAdViewHolder) {
            ((FacebookAdViewHolder) holder).bind();
        }


//        if (article.getType().equalsIgnoreCase("EXTENDED")) {
//            ((LargeCardViewHolder) holder).bind(position, article);
//        } else if (article.getType().equalsIgnoreCase("SIMPLE")) {
//            ((SmallCardViewHolder) holder).bind(position, article);
//        } else if (article.getType().equalsIgnoreCase("YOUTUBE")) {
//            ((YoutubeViewHolder) holder).bind(position, article);
//        } else if (article.getType().equalsIgnoreCase("VIDEO")) {
//            ((VideoViewHolder) holder).bind(position, article);
//        } else if (article.getType().equalsIgnoreCase("G_Ad")) {
//            ((AdViewHolder) holder).bind();
//        } else {
//            ((LargeCardViewHolder) holder).bind(position, article);
//        }
    }

    @Override
    public int getItemViewType(int position) {
        Article article = null;
        if (items.size() > position && position >= 0) {
            article = items.get(position);
        }
        if (article != null && !TextUtils.isEmpty(article.getType())) {
            if (article.getType().equalsIgnoreCase("EXTENDED")) {
                return TYPE_LIST;
            } else if (article.getType().equalsIgnoreCase("SIMPLE")) {
                return TYPE_LIST;
            } else if (article.getType().equalsIgnoreCase("YOUTUBE")) {
                return TYPE_YOUTUBE;
            } else if (article.getType().equalsIgnoreCase("VIDEO")) {
                return TYPE_VIDEO;
            } else if (article.getType().equalsIgnoreCase("G_Ad")) {
                return TYPE_AD_GOOGLE;
            } else if (article.getType().equalsIgnoreCase("FB_Ad")) {
                return TYPE_AD_FACEBOOK;
            } else if (article.getType().equalsIgnoreCase("REEL")) {
                return TYPE_REEL;
            } else {
                return TYPE_LIST;
            }
        }
        return TYPE_LIST;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(currentArticlePosition);
        if (holder instanceof YoutubeViewHolder) {
            ((YoutubeViewHolder) holder).release();
        }
    }

    public void setCurrentArticlePosition(int position) {
        this.currentArticlePosition = position;
    }

    private void showBottomSheetDialog(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet(context, shareToMainInterface, isGotoFollowShow, "ARTICLES");
        }
        shareBottomSheet.show(article, onDismissListener, shareInfo);
    }

    public void dismissBottomSheet() {
        if (shareBottomSheet != null) {
            shareBottomSheet.hide();
        }
    }

    @Override
    public int getArticlePosition() {
        return currentArticlePosition;
    }


    @Override
    public void showShareBottomSheet(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
//        if (shareInfo == null && !TextUtils.isEmpty(type) && type.equals("MY_ARTICLES")) {
//            showBottomSheetDialog(shareInfo, article, onDismissListener);
//        } else
        showBottomSheetDialog(shareInfo, article, onDismissListener);
    }

    @Override
    public void onItemClick(int position, boolean setCurrentView) {
        Log.e("onItemClick", "onItemClick : " + position);
        if (position > -1) {
            int temp = position;

            for (int i = 4; i >= 0; i--) {
                //previous position article
                temp--;
                resetArticle(temp, false, setCurrentView);
            }
            temp = position;
            //current position article
            resetArticle(temp, true, setCurrentView);

            //next position article
            temp++;
            resetArticle(temp, false, setCurrentView);
        }
    }

    private void resetArticle(int position, boolean flag, boolean youtube) {
        if (position > -1 && items != null && position < items.size()) {
            items.get(position).setYoutubePlaying(flag);
            items.get(position).setSelected(flag);
            if (!flag)
                notifyItemChanged(position);
        }
    }

    @Nullable
    @Override
    public Object getKeyForOrder(int order) {
        return null;
    }

    @Nullable
    @Override
    public Integer getOrderForKey(@NonNull Object key) {
        return null;
    }

    public void remove(int position) {
        if (items != null && position < items.size()) {
            items.remove(position);
        }
    }
}
