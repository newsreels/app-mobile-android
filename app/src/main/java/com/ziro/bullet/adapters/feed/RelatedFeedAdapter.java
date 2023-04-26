package com.ziro.bullet.adapters.feed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
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

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.NewFeed.SuggestedArticlesHolder;
import com.ziro.bullet.adapters.NewFeed.newHomeArticle.NewHomeArticlesViewHolder;
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

import java.util.List;

import im.ene.toro.CacheManager;


public class RelatedFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterCallback, CacheManager {

    private static final int TYPE_CARD = 0;
    private static final int TYPE_LIST = 1;
    private static final int TYPE_YOUTUBE = 2;
    private static final int TYPE_VIDEO = 3;
    private static final int TYPE_AD_GOOGLE = 4;
    private static final int TYPE_AD_FACEBOOK = 5;
    private static final int TYPE_DETAIL_VIEW = 6;
    private static final int TYPE_LOADER_VIEW = 7;
    private static final int TYPE_TITLE_VIEW = 8;
    private static final int TYPE_HORIZONTAL_ARTICLES = 9;

    private List<Article> items;
    private List<Article> articleList;
    private AppCompatActivity context;
    private Article currentArticle;

    private ShareToMainInterface shareToMainInterface;
    private TempCategorySwipeListener swipeListener;
    private NewsCallback categoryCallback;
    private OnGotoChannelListener gotoChannelListener;
    private DetailsActivityInterface detailsActivityInterface;
    private GoHome goHomeMainActivity;

    private PrefConfig mPrefConfig;

    private int currentArticlePosition = 0;
    private boolean isGotoFollowShow;
    private String type;
    private ShareBottomSheet shareBottomSheet;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private CommentClick commentClick;
    private AdFailedListener adFailedListener;
    private boolean showLoader;
    private Lifecycle lifecycle;

    private boolean isDetailViewSelected;

    public RelatedFeedAdapter(CommentClick commentClick, AppCompatActivity context, List<Article> items, String type, boolean isGotoFollowShow, DetailsActivityInterface detailsActivityInterface,
                              GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback,
                              OnGotoChannelListener gotoChannelListener, ShowOptionsLoaderCallback showOptionsLoaderCallback, AdFailedListener adFailedListener,
                              Lifecycle lifecycle) {
        this.context = context;
        this.items = items;
        this.swipeListener = swipeListener;
        this.goHomeMainActivity = goHomeMainActivity;
        this.shareToMainInterface = shareToMainInterface;
        this.gotoChannelListener = gotoChannelListener;
        this.categoryCallback = categoryCallback;
        this.detailsActivityInterface = detailsActivityInterface;
        this.isGotoFollowShow = isGotoFollowShow;
        this.commentClick = commentClick;
        this.type = type;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.adFailedListener = adFailedListener;
        this.lifecycle = lifecycle;

        isDetailViewSelected = true;

        mPrefConfig = new PrefConfig(context);
    }

    public void setCurrentArticle(Article currentArticle) {
        this.currentArticle = currentArticle;
        notifyDataSetChanged();
    }

    public void setDetailViewSelected(boolean detailViewSelected) {
        isDetailViewSelected = detailViewSelected;
    }

    public void setShowLoader(boolean showLoader) {
        this.showLoader = showLoader;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_large_card_article, parent, false);
                LargeCardViewHolder largeCardViewHolder = new LargeCardViewHolder(false, commentClick, type, false, view,
                        (Activity) context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback,
                        gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);

                ViewTreeObserver viewTreeObserver = largeCardViewHolder.itemView.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            largeCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = largeCardViewHolder.itemView.getHeight();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return largeCardViewHolder;
            case TYPE_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item_new, parent, false);
                NewHomeArticlesViewHolder homeArticlesViewHolder = new NewHomeArticlesViewHolder(context, view, commentClick, showOptionsLoaderCallback, this, detailsActivityInterface);
//                SmallCardViewHolder smallCardViewHolder = new SmallCardViewHolder(false, commentClick, type, view,
//                        (Activity) context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener,
//                        categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
//
//                ViewTreeObserver vtoSmall = smallCardViewHolder.itemView.getViewTreeObserver();
//                if (vtoSmall.isAlive()) {
//                    vtoSmall.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            smallCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            int viewHeight = smallCardViewHolder.itemView.getHeight();
//                            if (categoryCallback != null)
//                                categoryCallback.onItemHeightMeasured(viewHeight);
//                        }
//                    });
//                }
                return homeArticlesViewHolder;
            case TYPE_YOUTUBE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_card, parent, false);
                YoutubeViewHolder youtubeViewHolder = new YoutubeViewHolder(false, commentClick, type, false, view,
                        context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback,
                        gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback, lifecycle);

                ViewTreeObserver vtoYtube = youtubeViewHolder.itemView.getViewTreeObserver();
                if (vtoYtube.isAlive()) {
                    vtoYtube.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            youtubeViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = youtubeViewHolder.itemView.getHeight();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return youtubeViewHolder;

            case TYPE_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_card, parent, false);
                VideoViewHolder videoViewHolder = new VideoViewHolder(commentClick, type, false, view, context,
                        this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback,
                        gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);

                ViewTreeObserver vtoVideo = videoViewHolder.itemView.getViewTreeObserver();
                if (vtoVideo.isAlive()) {
                    vtoVideo.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            videoViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = videoViewHolder.itemView.getHeight();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return videoViewHolder;
            case TYPE_AD_GOOGLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ad, parent, false);
                return new AdViewHolder(false, view, context, mPrefConfig, adFailedListener);
            case TYPE_AD_FACEBOOK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_fb_ad, parent, false);
                return new FacebookAdViewHolder(false, view, context, mPrefConfig, adFailedListener);
            case TYPE_DETAIL_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_detail_holder, parent, false);
                return new DetailViewHolder(view, context, type);
            case TYPE_LOADER_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_loader_view, parent, false);
                return new LoaderViewHolder(view, context, mPrefConfig);
            case TYPE_TITLE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_title, parent, false);
                return new ViewHolder(view);
            case TYPE_HORIZONTAL_ARTICLES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggested_articles, parent, false);
                return new SuggestedArticlesHolder(view, context, true, gotoChannelListener, swipeListener);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Article article = null;
        if (items.size() > position && position >= 0) {
            article = items.get(position);
        }

        if (holder instanceof LargeCardViewHolder) {
            ((LargeCardViewHolder) holder).bind(position, article);
        } else if (holder instanceof SmallCardViewHolder) {
            ((SmallCardViewHolder) holder).bind(position, article);
        } else if (holder instanceof NewHomeArticlesViewHolder) {
            ((NewHomeArticlesViewHolder) holder).onBind(article, position, articleList);
        } else if (holder instanceof YoutubeViewHolder) {
            ((YoutubeViewHolder) holder).bind(position, article);
        } else if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).bind(position, article);
        } else if (holder instanceof AdViewHolder) {
            ((AdViewHolder) holder).bind();
        } else if (holder instanceof FacebookAdViewHolder) {
            ((FacebookAdViewHolder) holder).bind();
        } else if (holder instanceof DetailViewHolder) {
            ((DetailViewHolder) holder).bind(0, currentArticle, isDetailViewSelected);
        } else if (holder instanceof ViewHolder) {
            if (article != null)
                ((ViewHolder) holder).bind(article.getTabTitle());
        } else if (holder instanceof SuggestedArticlesHolder) {
            if (article != null)
                ((SuggestedArticlesHolder) holder).bind(article.getTitle(), article.getArticles(), false,
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);

                                int firstVisiblePosition = ((SuggestedArticlesHolder) holder).layoutManager.findFirstVisibleItemPosition();
                                View firstItemView = ((SuggestedArticlesHolder) holder).layoutManager.findViewByPosition(firstVisiblePosition);
                                if (firstItemView != null) {
                                    float Offset = firstItemView.getLeft();

                                    if (items.size() > position) {
                                        items.get(position).setPosState(firstVisiblePosition);
                                        items.get(position).setOffsetState(Offset);
                                    }
                                }
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        }, items.get(position).getPosState(), items.get(position).getOffsetState());
        } else if (holder instanceof LoaderViewHolder) {
            ((LoaderViewHolder) holder).bind();
        }

//        switch (holder.getItemViewType()) {
//            case TYPE_CARD:
//
//                break;
//            case TYPE_LIST:
//                (() holder).bind();
//                break;
//            case TYPE_YOUTUBE:
//                (() holder).bind(position, article);
//                break;
//            case TYPE_VIDEO:
//                (() holder).bind(position, article);
//                break;
//            case TYPE_AD_GOOGLE:
//                (() holder).bind();
//                break;
//            case TYPE_AD_FACEBOOK:
//                (() holder).bind();
//                break;
//            case TYPE_DETAIL_VIEW:
//
//                break;
//            case TYPE_TITLE_VIEW:
//
//                break;
//            case TYPE_HORIZONTAL_ARTICLES:
//                ((ViewHolder) holder).bind();
//                break;
//            default:
//
//        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (holder instanceof DetailViewHolder) {
            DetailViewHolder detailViewHolder = (DetailViewHolder) holder;
            detailViewHolder.pause();
        }
    }

    @Override
    public int getItemViewType(int position) {
        Article article = null;
        if (items.size() > position && position >= 0) {
            article = items.get(position);
        }
        if (article != null && article.getType().equalsIgnoreCase("EXTENDED")) {
            return TYPE_LIST;
        } else if (article != null && article.getType().equalsIgnoreCase("SIMPLE")) {
            return TYPE_LIST;
        } else if (article != null && article.getType().equalsIgnoreCase("YOUTUBE")) {
            return TYPE_YOUTUBE;
        } else if (article != null && article.getType().equalsIgnoreCase("VIDEO")) {
            return TYPE_VIDEO;
        } else if (article != null && article.getType().equalsIgnoreCase("G_Ad")) {
            return TYPE_AD_GOOGLE;
        } else if (article != null && article.getType().equalsIgnoreCase("FB_Ad")) {
            return TYPE_AD_FACEBOOK;
        } else if (article != null && article.getType().equalsIgnoreCase("DETAILS")) {
            return TYPE_DETAIL_VIEW;
        } else if (article != null && article.getType().equalsIgnoreCase("TITLE")) {
            return TYPE_TITLE_VIEW;
        } else if (article != null && article.getType().equalsIgnoreCase("GROUP_ARTICLES")) {
            return TYPE_HORIZONTAL_ARTICLES;
        } else {
            return TYPE_LOADER_VIEW;
        }
    }

    @Override
    public long getItemId(int position) {
        Article article = null;
        if (items.size() > position && position >= 0) {
            article = items.get(position);
        }
        if (article != null && article.getType().equalsIgnoreCase("EXTENDED")) {
            return TYPE_LIST;
        } else if (article != null && article.getType().equalsIgnoreCase("SIMPLE")) {
            return TYPE_LIST;
        } else if (article != null && article.getType().equalsIgnoreCase("YOUTUBE")) {
            return TYPE_YOUTUBE;
        } else if (article != null && article.getType().equalsIgnoreCase("VIDEO")) {
            return TYPE_VIDEO;
        } else if (article != null && article.getType().equalsIgnoreCase("G_Ad")) {
            return TYPE_AD_GOOGLE;
        } else if (article != null && article.getType().equalsIgnoreCase("FB_Ad")) {
            return TYPE_AD_FACEBOOK;
        } else if (article != null && article.getType().equalsIgnoreCase("DETAILS")) {
            return TYPE_DETAIL_VIEW;
        } else if (article != null && article.getType().equalsIgnoreCase("TITLE")) {
            return TYPE_TITLE_VIEW;
        } else if (article != null && article.getType().equalsIgnoreCase("GROUP_ARTICLES")) {
            return TYPE_HORIZONTAL_ARTICLES;
        } else {
            return TYPE_LOADER_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        if (showLoader && currentArticle != null) {
            return items.size() + 1;
        }
        return items.size();
    }

    public void setCurrentArticlePosition(int position) {
        this.currentArticlePosition = position;
    }

    private void showBottomSheetDialog(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet((Activity) context, shareToMainInterface, isGotoFollowShow, type);
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
        showBottomSheetDialog(shareInfo, article, onDismissListener);
    }

    @Override
    public void onItemClick(int position, boolean setCurrentView) {
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
}
