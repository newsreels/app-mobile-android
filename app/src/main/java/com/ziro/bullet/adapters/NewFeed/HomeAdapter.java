package com.ziro.bullet.adapters.NewFeed;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.CommunityFeed.SuggestedReelsHolder;
import com.ziro.bullet.adapters.NewFeed.newHomeArticle.NewHomeArticlesViewHolder;
import com.ziro.bullet.adapters.feed.AdViewHolder;
import com.ziro.bullet.adapters.feed.FacebookAdViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.fragments.CategoryFragment;
import com.ziro.bullet.fragments.Reels.ReelsPageInterface;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.CommunityItemCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.articles.Article;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.CacheManager;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterCallback, CacheManager {

    //    private static final int TYPE_CARD = 0;
    private static final int TYPE_LIST = 1;
    //    private static final int TYPE_YOUTUBE = 2;
//    private static final int TYPE_VIDEO = 3;
//    private static final int TYPE_HEADER = 5;
    private static final int TYPE_TITLE = 6;
    //    private static final int TYPE_FOOTER = 7;
//    private static final int TYPE_EDGE_TO_EDGE_IMAGE = 8;
//    private static final int TYPE_EDGE_TO_EDGE_VIDEO = 9;
//    private static final int TYPE_EDGE_TO_EDGE_YOUTUBE = 10;
//    private static final int TYPE_HORIZONTAL_ARTICLES = 11;
    private static final int TYPE_SUGGESTED_REELS = 12;
    private static final int TYPE_SUGGESTED_CHANNELS = 13;
    private static final int TYPE_AD_GOOGLE = 14;
    private static final int TYPE_AD_FACEBOOK = 15;
    private static final int TYPE_SUGGESTED_TOPICS = 16;
    //    private static final int TYPE_NEW_POST = 17;
//    private static final int TYPE_HORIZONTAL_VIDEOS = 18;
//    private static final int TYPE_REEL_LARGE = 19;
    private static final int TYPE_SEPARATOR_LINE = 20;

    private final boolean isPostArticle;
    private final AdFailedListener adFailedListener;
    // updating from outside
    public CategoryFragment categoryFragment;
    public boolean isDark;
    SuggestedReelsHolder suggestedReelsHolder;
    private List<Article> items;
    private AppCompatActivity context;
    private ShareToMainInterface shareToMainInterface;
    private ReelsPageInterface reelsPageInterface;
    private TempCategorySwipeListener swipeListener;
    private NewsCallback categoryCallback;
    private OnGotoChannelListener gotoChannelListener;
    private DetailsActivityInterface detailsActivityInterface;
    private GoHome goHomeMainActivity;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private PrefConfig mPrefConfig;
    private int currentArticlePosition = 0;
    private boolean isGotoFollowShow;
    private String type;
    private ShareBottomSheet shareBottomSheet;
    private CommentClick commentClick;
    private Lifecycle lifecycle;
    private CommunityItemCallback callback;

    public HomeAdapter(boolean isDark, CommentClick mCommentClick, boolean isPostArticle, AppCompatActivity context, ArrayList<Article> items, String type,
                       boolean isGotoFollowShow, DetailsActivityInterface detailsActivityInterface,
                       GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback,
                       OnGotoChannelListener gotoChannelListener, ShowOptionsLoaderCallback showOptionsLoaderCallback,
                       AdFailedListener adFailedListener, Lifecycle lifecycle, CommunityItemCallback callback) {
        this.isDark = isDark;
        this.callback = callback;
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
        this.commentClick = mCommentClick;
        this.isPostArticle = isPostArticle;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.adFailedListener = adFailedListener;
        this.lifecycle = lifecycle;
        mPrefConfig = new PrefConfig(context);
    }

//    public HomeAdapter(List<Article> items, PrefConfig prefConfig, AppCompatActivity context) {
//        this.items = items;
//        this.isPostArticle = false;
//        this.adFailedListener = null;
//        mPrefConfig = prefConfig;
//        this.context = context;
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
//            case TYPE_EDGE_TO_EDGE_IMAGE:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edge_to_egde_view, parent, false);
//                return new EdgeToEdgeImage(commentClick, view, context, categoryCallback, this, gotoChannelListener, goHomeMainActivity, lifecycle, type);
//            case TYPE_EDGE_TO_EDGE_VIDEO:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_card_edge_to_edge, parent, false);
//                return new EdgeToEdgeVideo(commentClick, type, false, view, context, this, mPrefConfig, goHomeMainActivity,
//                        shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
//            case TYPE_EDGE_TO_EDGE_YOUTUBE:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_card_edge_to_edge, parent, false);
//                return new EdgeToEdgeYoutube(false, commentClick, type, false, view, context, this, mPrefConfig,
//                        goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback, lifecycle);
//            case TYPE_CARD:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_large_card_article, parent, false);
//                LargeCardViewHolder largeCardViewHolder = new LargeCardViewHolder(
//                        false,
//                        commentClick,
//                        type,
//                        false,
//                        view,
//                        context,
//                        this,
//                        mPrefConfig,
//                        goHomeMainActivity,
//                        shareToMainInterface,
//                        swipeListener,
//                        categoryCallback,
//                        gotoChannelListener,
//                        detailsActivityInterface,
//                        showOptionsLoaderCallback
//                );
//
//                ViewTreeObserver viewTreeObserver = largeCardViewHolder.itemView.getViewTreeObserver();
//                if (viewTreeObserver.isAlive()) {
//                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            largeCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            int viewHeight = largeCardViewHolder.itemView.getHeight();
////                            largeCardViewHolder.statusView.getLayoutParams().height = viewHeight;
////                            largeCardViewHolder.statusView.requestLayout();
//                            if (categoryCallback != null)
//                                categoryCallback.onItemHeightMeasured(viewHeight);
//                        }
//                    });
//                }
//                return largeCardViewHolder;
            case TYPE_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item_new, parent, false);
                //                SmallCardViewHolder smallCardViewHolder = new SmallCardViewHolder(
//                        false, commentClick, type, view, context, this, mPrefConfig,
//                        goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener,
//                        detailsActivityInterface, showOptionsLoaderCallback);
//                ViewTreeObserver vtoSmall = smallCardViewHolder.itemView.getViewTreeObserver();
//                if (vtoSmall.isAlive()) {
//                    vtoSmall.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            smallCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            int viewHeight = smallCardViewHolder.itemView.getHeight();
////                            smallCardViewHolder.statusView.getLayoutParams().height = viewHeight;
////                            smallCardViewHolder.statusView.requestLayout();
//                            if (categoryCallback != null)
//                                categoryCallback.onItemHeightMeasured(viewHeight);
//                        }
//                    });
//                }
                return new NewHomeArticlesViewHolder(context, view, commentClick, showOptionsLoaderCallback, this, detailsActivityInterface);
//            case TYPE_YOUTUBE:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_card, parent, false);
//                YoutubeViewHolder youtubeViewHolder = new YoutubeViewHolder(false, commentClick, type, false, view, context, this,
//                        mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback, lifecycle);
//                youtubeViewHolder.categoryFragment = categoryFragment;
//                ViewTreeObserver vtoYtube = youtubeViewHolder.itemView.getViewTreeObserver();
//                if (vtoYtube.isAlive()) {
//                    vtoYtube.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            youtubeViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            int viewHeight = youtubeViewHolder.itemView.getHeight();
////                            youtubeViewHolder.statusView.getLayoutParams().height = viewHeight;
////                            youtubeViewHolder.statusView.requestLayout();
//                            if (categoryCallback != null)
//                                categoryCallback.onItemHeightMeasured(viewHeight);
//                        }
//                    });
//                }
//                return youtubeViewHolder;
//            case TYPE_VIDEO:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_card, parent, false);
//                return new VideoViewHolder(commentClick, type, false, view, context, this, mPrefConfig, goHomeMainActivity,
//                        shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
//            case TYPE_HEADER:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_you_header, parent, false);
//                return new HeaderViewHolder(view);
            case TYPE_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_you_title, parent, false);
                return new TitleViewHolder(view);
            case TYPE_SEPARATOR_LINE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_seperator_line, parent, false);
                return new LineHolder(view);
//            case TYPE_FOOTER:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_you_footer, parent, false);
//                return new FooterViewHolder(view, isDark);
//            case TYPE_HORIZONTAL_ARTICLES:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggested_articles, parent, false);
//                return new SuggestedArticlesHolder(view, context, true, gotoChannelListener, swipeListener);
            case TYPE_SUGGESTED_REELS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                SuggestedReelsHolder holder = new SuggestedReelsHolder(view, context, false, false);
                holder.addShareListener(showOptionsLoaderCallback, this, detailsActivityInterface);
                return holder;
            case TYPE_SUGGESTED_CHANNELS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new SuggestedChannelHolder(view, context, false);
            case TYPE_SUGGESTED_TOPICS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new TopicsViewHolder(view, context, false, false);
//            case TYPE_SUGGESTED_TOPICS:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_suggested_item, parent, false);
//                return new NewTopicsViewHolder(view, context);
            case TYPE_AD_GOOGLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ad, parent, false);
                return new AdViewHolder(true, view, context, mPrefConfig, adFailedListener);
            case TYPE_AD_FACEBOOK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_fb_ad, parent, false);
                return new FacebookAdViewHolder(true, view, context, mPrefConfig, adFailedListener);
//            case TYPE_NEW_POST:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_post_card, parent, false);
//                return new NewPostViewHolder(view, context, callback);
//            case TYPE_REEL_LARGE:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_video_item, parent, false);
//                return new DiscoverReelViewHolder(view, context);
//            case TYPE_HORIZONTAL_VIDEOS:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_video_item, parent, false);
//                DiscoverVideoViewHolder discoverVideoViewHolder = new DiscoverVideoViewHolder(view, context);
//                ViewTreeObserver viewTreeObserver2 = discoverVideoViewHolder.itemView.getViewTreeObserver();
//                if (viewTreeObserver2.isAlive()) {
//                    viewTreeObserver2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            discoverVideoViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                            int viewHeight = discoverVideoViewHolder.itemView.getHeight();
//                            if (categoryCallback != null)
//                                categoryCallback.onItemHeightMeasured(viewHeight);
//                        }
//                    });
//                }
//                return new DiscoverVideoViewHolder(view, context);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        int position = holder.getAbsoluteAdapterPosition();

        Article article = items.get(position);

        /*if (holder instanceof LargeCardViewHolder) {
            ((LargeCardViewHolder) holder).bind(position, article);
        } else if (holder instanceof SmallCardViewHolder) {
            ((SmallCardViewHolder) holder).bind(position, article);
        } else*/
        if (holder instanceof NewHomeArticlesViewHolder) {
            ((NewHomeArticlesViewHolder) holder).onBind(article, position,items);
        } /*else if (holder instanceof EdgeToEdgeImage) {
            ((EdgeToEdgeImage) holder).bind(article);
        } else if (holder instanceof EdgeToEdgeYoutube) {
            ((EdgeToEdgeYoutube) holder).bind(position, article);
        } else if (holder instanceof EdgeToEdgeVideo) {
            ((EdgeToEdgeVideo) holder).bind(position, article);
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
        } */ else if (holder instanceof AdViewHolder) {
            ((AdViewHolder) holder).bind();
        } else if (holder instanceof FacebookAdViewHolder) {
            ((FacebookAdViewHolder) holder).bind();
        } /*else if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(article);
        }  else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).bind(article);
        }*/ else if (holder instanceof TitleViewHolder) {
            ((TitleViewHolder) holder).bind(article);
        } else if (holder instanceof LineHolder) {
            ((LineHolder) holder).bind(article);
        } /*else if (holder instanceof SuggestedArticlesHolder) {
            if (article != null) {
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
                                        article.setPosState(firstVisiblePosition);
                                        article.setOffsetState(Offset);
                                    }
                                }
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        }, article.getPosState(), article.getOffsetState());
            }
        }*/ else if (holder instanceof TopicsViewHolder) {
            if (article != null) {
                ((TopicsViewHolder) holder).bind(article.getTitle(), article.getTopics(), new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        int firstVisiblePosition = ((TopicsViewHolder) holder).layoutManager.findFirstVisibleItemPosition();
                        View firstItemView = ((TopicsViewHolder) holder).layoutManager.findViewByPosition(firstVisiblePosition);
                        if (firstItemView != null) {
                            float Offset = firstItemView.getLeft();

                            if (items.size() > position) {
                                article.setPosState(firstVisiblePosition);
                                article.setOffsetState(Offset);
                            }
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                }, article.getPosState(), article.getOffsetState());
            }
        } else if (holder instanceof SuggestedReelsHolder) {
            suggestedReelsHolder = (SuggestedReelsHolder) holder;
            suggestedReelsHolder.addinteface(reelsPageInterface);
            if (article != null) {
                ((SuggestedReelsHolder) holder).bind(article.getTitle(), article.getReels(),
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);

                                int firstVisiblePosition = ((SuggestedReelsHolder) holder).layoutManager.findFirstVisibleItemPosition();
                                View firstItemView = ((SuggestedReelsHolder) holder).layoutManager.findViewByPosition(firstVisiblePosition);
                                if (firstItemView != null) {
                                    float Offset = firstItemView.getLeft();

                                    if (items.size() > position) {
                                        article.setPosState(firstVisiblePosition);
                                        article.setOffsetState(Offset);
                                    }
                                }
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        }, article.getPosState(), article.getOffsetState());
            }
        } else if (holder instanceof SuggestedChannelHolder) {
            if (article != null) {
                ((SuggestedChannelHolder) holder).bind(article.getTitle(), article.getChannels(),
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);

                                int firstVisiblePosition = ((SuggestedChannelHolder) holder).layoutManager.findFirstVisibleItemPosition();
                                View firstItemView = ((SuggestedChannelHolder) holder).layoutManager.findViewByPosition(firstVisiblePosition);
                                if (firstItemView != null) {
                                    float Offset = firstItemView.getLeft();

                                    if (items.size() > position) {
                                        article.setPosState(firstVisiblePosition);
                                        article.setOffsetState(Offset);
                                    }
                                }
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        }, article.getPosState(), article.getOffsetState());
            }
        } /*else if (holder instanceof NewPostViewHolder) {
            ((NewPostViewHolder) holder).bind(article);
        } else if (holder instanceof DiscoverReelViewHolder) {
            if (article != null) {
                ((DiscoverReelViewHolder) holder).bind(article.isSelected(), article.getTitle(), article.getReels());
            }
        } else if (holder instanceof DiscoverVideoViewHolder) {
            if (article != null) {
                ((DiscoverVideoViewHolder) holder).bind(article.isSelected(), article.getTitle(), article.getArticles());
            }
        } */ else if (holder instanceof NewTopicsViewHolder) {
            if (article != null) {
                ((NewTopicsViewHolder) holder).bind(article.getTitle(), article.getTopics());
            }
        }
    }

    private void resizeVideoView(VideoViewHolder videoViewHolder, double mVideoHeight, double mVideoWidth) {
//        Log.d("shineeee", "resizeVideoView() called with:  mVideoHeight = [" + mVideoHeight + "], mVideoWidth = [" + mVideoWidth + "]");
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
//        videoViewHolder.cardMain.getLayoutParams().width = cardWidth;
        // videoViewHolder.cardMain.requestLayout();
        videoViewHolder.statusView.getLayoutParams().height = cardHeight + (context.getResources().getDimensionPixelSize(R.dimen._100sdp));
        //videoViewHolder.statusView.getLayoutParams().width = cardWidth;
        videoViewHolder.statusView.requestLayout();
        //videoViewHolder.isHeightSet = true;
//        Log.d("shineeee", "resizeVideoView() called with:  cardHeight = [" + cardHeight + "], cardWidth = [" + cardWidth + "]");
        videoViewHolder.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        videoViewHolder.playerView.requestLayout();
        //}
    }

    @Override
    public int getItemViewType(int position) {
        Article article = items.get(position);
        if (article.getType() != null) {
            if (article.getType().equalsIgnoreCase("EXTENDED")) {
                return TYPE_LIST;
            } else if (article.getType().equalsIgnoreCase("SIMPLE")) {
                return TYPE_LIST;
            } /*else if (article.getType().equalsIgnoreCase("YOUTUBE")) {
                return TYPE_YOUTUBE;
            } else if (article.getType().equalsIgnoreCase("VIDEO")) {
                return TYPE_VIDEO;
            } else if (article.getType().equalsIgnoreCase("HORIZONTAL_ARTICLES")) {
                return TYPE_HORIZONTAL_ARTICLES;
            }*/ else if (article.getType().equalsIgnoreCase("REELS")) {
                return TYPE_SUGGESTED_REELS;
            } else if (article.getType().equalsIgnoreCase("CHANNELS")) {
                return TYPE_SUGGESTED_CHANNELS;
            } else if (article.getType().equalsIgnoreCase("TOPICS")) {
                return TYPE_SUGGESTED_TOPICS;
            } /*else if (article.getType().equalsIgnoreCase("FIRST_YOUTUBE")) {
                return TYPE_EDGE_TO_EDGE_YOUTUBE;
            } else if (article.getType().equalsIgnoreCase("FIRST_VIDEO")) {
                return TYPE_EDGE_TO_EDGE_VIDEO;
            } else if (article.getType().equalsIgnoreCase("FIRST_IMAGE")) {
                return TYPE_EDGE_TO_EDGE_IMAGE;
            }*/ else if (article.getType().equalsIgnoreCase("G_Ad")) {
                return TYPE_AD_GOOGLE;
            } else if (article.getType().equalsIgnoreCase("FB_Ad")) {
                return TYPE_AD_FACEBOOK;
            } /*else if (article.getType().equalsIgnoreCase("NEW_POST")) {
                return TYPE_NEW_POST;
            } else if (article.getType().equalsIgnoreCase("LARGE_REEL")) {
                return TYPE_REEL_LARGE;
            } else if (article.getType().equalsIgnoreCase("ARTICLE_VIDEOS")) {
                return TYPE_HORIZONTAL_VIDEOS;
            }*/ else if (article.getType().equalsIgnoreCase("LINE")) {
                return TYPE_SEPARATOR_LINE;
            } else if (article.getType().equalsIgnoreCase("TITLE")) {
                Log.d("HOME_ADAPTER_TAG", "getItemViewType: Title type matched");
                return TYPE_TITLE;
            } else {
                return TYPE_LIST;
            }
        } else /*if (!TextUtils.isEmpty(article.getTabTitle()) || !TextUtils.isEmpty(article.getFooter().getTitle())) {
            Log.d("HOME_ADAPTER_TAG", "getItemViewType: Tab Title Matched or Footer Matched :: " + article.getTabTitle());
            return TYPE_TITLE;
        } else*/ {
            /*if (!TextUtils.isEmpty(article.getHeader())) {
                return TYPE_HEADER;
            } else if (!TextUtils.isEmpty(article.getTabTitle()) || !TextUtils.isEmpty(article.getFooter().getTitle())) {
                Log.d("HOME_ADAPTER_TAG", "getItemViewType: Tab Title Matched or Footer Matched :: " + article.getTabTitle());
                return TYPE_TITLE;
            }*/
//            else if (!TextUtils.isEmpty(article.getFooter().getTitle())) {
//                return TYPE_FOOTER;
//            }
            return TYPE_LIST;
        }
    }

    @Override
    public long getItemId(int position) {
        Article article = items.get(position);
        return position;
        /*if (article.getType() != null) {
            if (article.getType().equalsIgnoreCase("EXTENDED")) {
                return TYPE_LIST;
            } else if (article.getType().equalsIgnoreCase("SIMPLE")) {
                return TYPE_LIST;
            } *//*else if (article.getType().equalsIgnoreCase("YOUTUBE")) {
                return TYPE_YOUTUBE;
            } else if (article.getType().equalsIgnoreCase("VIDEO")) {
                return TYPE_VIDEO;
            } else if (article.getType().equalsIgnoreCase("HORIZONTAL_ARTICLES")) {
                return TYPE_HORIZONTAL_ARTICLES;
            } *//*else if (article.getType().equalsIgnoreCase("REELS")) {
                return TYPE_SUGGESTED_REELS;
            } else if (article.getType().equalsIgnoreCase("CHANNELS")) {
                return TYPE_SUGGESTED_CHANNELS;
            } else if (article.getType().equalsIgnoreCase("TOPICS")) {
                return TYPE_SUGGESTED_TOPICS;
            } *//*else if (article.getType().equalsIgnoreCase("FIRST_YOUTUBE")) {
                return TYPE_EDGE_TO_EDGE_YOUTUBE;
            } else if (article.getType().equalsIgnoreCase("FIRST_VIDEO")) {
                return TYPE_EDGE_TO_EDGE_VIDEO;
            } else if (article.getType().equalsIgnoreCase("FIRST_IMAGE")) {
                return TYPE_EDGE_TO_EDGE_IMAGE;
            }*//* else if (article.getType().equalsIgnoreCase("G_Ad")) {
                return TYPE_AD_GOOGLE;
            } else if (article.getType().equalsIgnoreCase("FB_Ad")) {
                return TYPE_AD_FACEBOOK;
            } *//*else if (article.getType().equalsIgnoreCase("NEW_POST")) {
                return TYPE_NEW_POST;
            } else if (article.getType().equalsIgnoreCase("LARGE_REEL")) {
                return TYPE_REEL_LARGE;
            } else if (article.getType().equalsIgnoreCase("ARTICLE_VIDEOS")) {
                return TYPE_HORIZONTAL_VIDEOS;
            }*//* else if (article.getType().equalsIgnoreCase("LINE")) {
                return TYPE_SEPARATOR_LINE;
            } else if (article.getType().equalsIgnoreCase("TITLE")) {
                return TYPE_TITLE;
            } else {
                return TYPE_LIST;
            }
        } else {
           *//* if (!TextUtils.isEmpty(article.getHeader())) {
                return TYPE_HEADER;
            } else if (!TextUtils.isEmpty(article.getTabTitle()) || !TextUtils.isEmpty(article.getFooter().getTitle())) {
                return TYPE_TITLE;
            }*//*
//            else if (!TextUtils.isEmpty(article.getFooter().getTitle())) {
//                return TYPE_FOOTER;
//            }
            return TYPE_LIST;
        }*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setCurrentArticlePosition(int position) {
        this.currentArticlePosition = position;
    }

    private void showBottomSheetDialog(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet(context, shareToMainInterface, true, "ARTICLES");
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

    public void addChildListener(ReelsPageInterface reelsPageInterface) {
        this.reelsPageInterface = reelsPageInterface;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }

        public void bind(Article article) {
            tvHeader.setText(String.format("%s", article.getHeader()));
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public boolean isDark;
        private TextView categoryName;
        private TextView categoryTag;
        private RelativeLayout footerBtn;

        public FooterViewHolder(@NonNull View itemView, boolean isDark) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryTag = itemView.findViewById(R.id.category_tag);
            footerBtn = itemView.findViewById(R.id.footer_btn);
            this.isDark = isDark;
        }

        public void bind(Article article) {
            categoryName.setText(String.format("%s", article.getFooter().getTitle()));
            categoryTag.setText(String.format("%s ", article.getFooter().getPrefix()));

            if ((!TextUtils.isEmpty(article.getFooterType()) && article.getFooterType().equals("LARGE_REEL")) || isDark) {
                footerBtn.setBackgroundColor(context.getResources().getColor(R.color.black));
                categoryTag.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                footerBtn.findViewById(R.id.footer_btn).setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                categoryTag.setTextColor(context.getResources().getColor(R.color.title_bar_title));
            }

            footerBtn.setOnClickListener(v -> {
                if (swipeListener != null && !TextUtils.isEmpty(article.getTabId())) {
                    swipeListener.selectTabOrDetailsPage(article.getTabId(), article.getFooter() != null ? article.getFooter().getTitle() : "", TYPE.FEED, article.getFooterType());
                }
            });
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout llContainer;
        private TextView textView, tvSubHeader;
        private ImageView arrow;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            llContainer = itemView.findViewById(R.id.ll_category_header);
            textView = itemView.findViewById(R.id.tvTitle);
            arrow = itemView.findViewById(R.id.arrow);
            tvSubHeader = itemView.findViewById(R.id.tvSubHeader);
        }

        public void bind(Article article) {
            if (article != null) {

                textView.setTextColor(context.getResources().getColor(R.color.primaryRed));
                tvSubHeader.setTextColor(context.getResources().getColor(R.color.grey_light));
                Log.e("tttt", "bind: " + article.getTabTitle());

                textView.setText(String.format("%s", article.getTabTitle()));
                if (!TextUtils.isEmpty(article.getSubHeader()))
                    tvSubHeader.setText(String.format("%s", article.getSubHeader()));
//                if (article.getFooter() != null && !TextUtils.isEmpty(article.getFooter().getTitle())) {
//                    llContainer.setOnClickListener(v -> {
//                        if (swipeListener != null && !TextUtils.isEmpty(article.getTabId())) {
//                            swipeListener.selectTabOrDetailsPage(article.getTabId(), article.getFooter() != null ? article.getFooter().getTitle() : "", TYPE.FEED, article.getFooterType());
//                        }
//                    });
//                }
            }
        }
    }

    class LineHolder extends RecyclerView.ViewHolder {

        RelativeLayout liner;

        public LineHolder(@NonNull View itemView) {
            super(itemView);
            liner = itemView.findViewById(R.id.liner);
        }

        public void bind(Article article) {
            liner.setBackgroundColor(context.getResources().getColor(R.color.articlebg));
        }
    }
}
