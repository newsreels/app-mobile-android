package com.ziro.bullet.adapters.foryou;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.CommunityFeed.SuggestedReelsHolder;
import com.ziro.bullet.adapters.NewFeed.SuggestedArticlesHolder;
import com.ziro.bullet.adapters.NewFeed.SuggestedChannelHolder;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.adapters.feed.YoutubeViewHolder;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.MediaMeta;

import java.util.List;

import im.ene.toro.CacheManager;

public class ForYouAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterCallback, CacheManager {

    private static final int TYPE_CARD = 0;
    private static final int TYPE_LIST = 1;
    private static final int TYPE_YOUTUBE = 2;
    private static final int TYPE_VIDEO = 3;

    private static final int TYPE_HEADER = 5;
    private static final int TYPE_TITLE = 6;
    private static final int TYPE_FOOTER = 7;

//    private static final int TYPE_LAST_SMALL = 8;
//    private static final int TYPE_LAST_LARGE = 9;
//    private static final int TYPE_LAST_YOUTUBE = 10;
//    private static final int TYPE_LAST_VIDEO = 11;

    private static final int TYPE_EDGE_TO_EDGE_IMAGE = 12;
    private static final int TYPE_EDGE_TO_EDGE_VIDEO = 13;
    private static final int TYPE_EDGE_TO_EDGE_YOUTUBE = 14;
    private static final int TYPE_HORIZONTAL_ARTICLES = 15;
    private static final int TYPE_SUGGESTED_REELS = 16;
    private static final int TYPE_SUGGESTED_CHANNELS = 17;


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
    private int currentArticlePosition = 0;
    private boolean isGotoFollowShow;
    private String type;
    private ShareBottomSheet shareBottomSheet;
    private CommentClick commentClick;
    private Lifecycle lifecycle;


    public ForYouAdapter(CommentClick commentClick, AppCompatActivity context, List<Article> items, String type, boolean isGotoFollowShow, DetailsActivityInterface detailsActivityInterface,
                         GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback,
                         OnGotoChannelListener gotoChannelListener, ShowOptionsLoaderCallback showOptionsLoaderCallback, Lifecycle lifecycle) {
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
        this.commentClick = commentClick;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.lifecycle = lifecycle;
        mPrefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_EDGE_TO_EDGE_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edge_to_egde_view, parent, false);
                return new EdgeToEdgeImage(commentClick, view, context, categoryCallback, this, gotoChannelListener, goHomeMainActivity, lifecycle, type);
            case TYPE_EDGE_TO_EDGE_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_card_edge_to_edge, parent, false);
                return new EdgeToEdgeVideo(commentClick, type, false, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
            case TYPE_EDGE_TO_EDGE_YOUTUBE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_card_edge_to_edge, parent, false);
                return new EdgeToEdgeYoutube(false, commentClick, type, false, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback, lifecycle);
            case TYPE_CARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_large_card_for_you, parent, false);
                LargeCardViewHolder largeCardViewHolder = new LargeCardViewHolder(false, commentClick, type, false, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);

                ViewTreeObserver viewTreeObserver = largeCardViewHolder.itemView.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            largeCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = largeCardViewHolder.itemView.getHeight();
//                            largeCardViewHolder.statusView.getLayoutParams().height = viewHeight;
//                            largeCardViewHolder.statusView.requestLayout();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return largeCardViewHolder;
            case TYPE_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_small_card_for_you, parent, false);
                SmallCardViewHolder smallCardViewHolder = new SmallCardViewHolder(false, commentClick, type, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);

                ViewTreeObserver vtoSmall = smallCardViewHolder.itemView.getViewTreeObserver();
                if (vtoSmall.isAlive()) {
                    vtoSmall.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            smallCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = smallCardViewHolder.itemView.getHeight();
//                            smallCardViewHolder.statusView.getLayoutParams().height = viewHeight;
//                            smallCardViewHolder.statusView.requestLayout();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return smallCardViewHolder;
            case TYPE_YOUTUBE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_card_for_you, parent, false);
                YoutubeViewHolder youtubeViewHolder = new YoutubeViewHolder(false, commentClick, type, false, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback, lifecycle);

                ViewTreeObserver vtoYtube = youtubeViewHolder.itemView.getViewTreeObserver();
                if (vtoYtube.isAlive()) {
                    vtoYtube.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            youtubeViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = youtubeViewHolder.itemView.getHeight();
//                            youtubeViewHolder.statusView.getLayoutParams().height = viewHeight;
//                            youtubeViewHolder.statusView.requestLayout();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return youtubeViewHolder;
            case TYPE_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_card_for_you, parent, false);
                return new VideoViewHolder(commentClick, type, false, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_you_header, parent, false);
                return new HeaderViewHolder(view);
            case TYPE_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_you_title, parent, false);
                return new TitleViewHolder(view);
            case TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_you_footer, parent, false);
                return new FooterViewHolder(view);
            case TYPE_HORIZONTAL_ARTICLES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggested_articles, parent, false);
                return new SuggestedArticlesHolder(view, context, true, gotoChannelListener, swipeListener);
            case TYPE_SUGGESTED_REELS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new SuggestedReelsHolder(view, context, false,false);
            case TYPE_SUGGESTED_CHANNELS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new SuggestedChannelHolder(view, context,false);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        View viewGroup = holder.itemView;
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewGroup.getLayoutParams();
        if (position == 0) {
            params.setMargins(params.getMarginStart(), (int) context.getResources().getDimension(R.dimen._10sdp), params.getMarginEnd(), 0);
        } else {
            params.setMargins(params.getMarginStart(), 0, params.getMarginEnd(), 0);
        }
        viewGroup.setLayoutParams(params);

        Article article = items.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_EDGE_TO_EDGE_IMAGE:
                ((EdgeToEdgeImage) holder).bind(article);
                break;
            case TYPE_EDGE_TO_EDGE_YOUTUBE:
                ((EdgeToEdgeYoutube) holder).bind(position, article);
                break;
            case TYPE_EDGE_TO_EDGE_VIDEO:
                ((EdgeToEdgeVideo) holder).bind(position, article);
//                try {
//                    if (article != null) {
//                        MediaMeta mediaMeta = article.getMediaMeta();
//                        Log.e("@#@#@#", "" + article.getId());
//                        if (mediaMeta != null) {
//                            resizeVideoView(((EdgeToEdgeVideo) holder), mediaMeta.getHeight(), mediaMeta.getWidth());
//                        } else {
//                            Log.e("@#@#@#", "failed");
//                        }
//                    } else {
//                        Log.e("@#@#@#", "failed");
//                    }
//                } catch (Exception e) {
//                    Log.e("@#@#@#", "failed");
//                    e.printStackTrace();
//                }
                break;
            case TYPE_CARD:
                ((LargeCardViewHolder) holder).bind(position, article);
                break;
            case TYPE_LIST:
                ((SmallCardViewHolder) holder).bind(position, article);
                break;
            case TYPE_YOUTUBE:
                ((YoutubeViewHolder) holder).bind(position, article);
                break;
            case TYPE_VIDEO:
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
                break;
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(article);
                break;
            case TYPE_TITLE:
                ((TitleViewHolder) holder).bind(article);
                break;
            case TYPE_FOOTER:
                ((FooterViewHolder) holder).bind(article);
                break;
            case TYPE_HORIZONTAL_ARTICLES:
                ((SuggestedArticlesHolder) holder).bind(items.get(position).getTitle(), items, false,
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        }, 0, 0);
                break;
            case TYPE_SUGGESTED_REELS:
                ((SuggestedReelsHolder) holder).bind(items.get(position).getTitle(), null,
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        },0,0);
                break;
            case TYPE_SUGGESTED_CHANNELS:
                ((SuggestedChannelHolder) holder).bind(items.get(position).getTitle(), null,
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                            }

                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        },0, 0);
                break;
            default:
                ((LargeCardViewHolder) holder).bind(position, article);
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
        videoViewHolder.cardMain.getLayoutParams().width = cardWidth;
        // videoViewHolder.cardMain.requestLayout();
        videoViewHolder.statusView.getLayoutParams().height = cardHeight + (context.getResources().getDimensionPixelSize(R.dimen._100sdp));
        //videoViewHolder.statusView.getLayoutParams().width = cardWidth;
        videoViewHolder.statusView.requestLayout();
        //videoViewHolder.isHeightSet = true;
        Log.e("@#@#@#", "QQQQQQ = " + cardWidth + " X " + cardHeight);
        videoViewHolder.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        videoViewHolder.playerView.requestLayout();
        //}
    }

    @Override
    public int getItemViewType(int position) {
        Article article = items.get(position);
//        if (article.isForYouLastItem() && article.getType() != null) {
////            if (article.getType().equalsIgnoreCase("EXTENDED")) {
////                return TYPE_LAST_LARGE;
////            } else if (article.getType().equalsIgnoreCase("SIMPLE")) {
////                return TYPE_LAST_SMALL;
////            } else if (article.getType().equalsIgnoreCase("YOUTUBE")) {
////                return TYPE_EDGE_TO_EDGE_YOUTUBE;
//////                return TYPE_LAST_YOUTUBE;
////            } else if (article.getType().equalsIgnoreCase("VIDEO")) {
////                return TYPE_LAST_VIDEO;
////            } else {
////                return TYPE_LAST_SMALL;
////            }
//        } else
        if (article.getType() != null) {
            if (article.getType().equalsIgnoreCase("EXTENDED")) {
                return TYPE_CARD;
            } else if (article.getType().equalsIgnoreCase("SIMPLE")) {
                return TYPE_LIST;
            } else if (article.getType().equalsIgnoreCase("YOUTUBE")) {
                return TYPE_YOUTUBE;
            } else if (article.getType().equalsIgnoreCase("VIDEO")) {
                return TYPE_VIDEO;
            } else if (article.getType().equalsIgnoreCase("HORIZONTAL_ARTICLES")) {
                return TYPE_HORIZONTAL_ARTICLES;
            } else if (article.getType().equalsIgnoreCase("HORIZONTAL_REELS")) {
                return TYPE_SUGGESTED_REELS;
            } else if (article.getType().equalsIgnoreCase("HORIZONTAL_CHANNELS")) {
                return TYPE_SUGGESTED_CHANNELS;
            } else if (article.getType().equalsIgnoreCase("FIRST_YOUTUBE")) {
                return TYPE_EDGE_TO_EDGE_YOUTUBE;
            } else if (article.getType().equalsIgnoreCase("FIRST_VIDEO")) {
                return TYPE_EDGE_TO_EDGE_VIDEO;
            } else if (article.getType().equalsIgnoreCase("FIRST_IMAGE")) {
                return TYPE_EDGE_TO_EDGE_IMAGE;
            } else {
                return TYPE_CARD;
            }
        } else {
            if (!TextUtils.isEmpty(article.getHeader())) {
                return TYPE_HEADER;
            } else if (!TextUtils.isEmpty(article.getTabTitle())) {
                return TYPE_TITLE;
            } else if (article.getFooter() != null) {
                return TYPE_FOOTER;
            }
            return TYPE_CARD;
        }
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
            shareBottomSheet = new ShareBottomSheet(context, shareToMainInterface, isGotoFollowShow, type);
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
        private TextView categoryName;
        private TextView categoryTag;
        private ConstraintLayout footerBtn;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryTag = itemView.findViewById(R.id.category_tag);
            footerBtn = itemView.findViewById(R.id.footer_btn);
        }

        public void bind(Article article) {
            categoryName.setText(String.format("%s", article.getForYouTitle()));
            categoryTag.setText(String.format("%s ", categoryName.getContext().getString(R.string.more)));

            footerBtn.setOnClickListener(v -> {
                if (swipeListener != null && article != null && !TextUtils.isEmpty(article.getTabId()))
                    swipeListener.selectTab(article.getTabId());
            });
        }
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llContainer;
        private TextView textView;
//        private ImageView icon;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            llContainer = itemView.findViewById(R.id.ll_category_header);
            textView = itemView.findViewById(R.id.tvTitle);
//            icon = itemView.findViewById(R.id.icon);
        }

        public void bind(Article article) {
            textView.setText(String.format("%s", article.getTabTitle()));
//            if (article.getTabIcon() != null && !article.getTabIcon().equals("")) {
//                Picasso.get()
//                        .load(article.getTabIcon())
//                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                        .into(icon);
//                icon.setVisibility(View.VISIBLE);
//            } else {
//                icon.setVisibility(View.GONE);
//            }

//            llContainer.setOnClickListener(v -> {
//                if (swipeListener != null && article != null && !TextUtils.isEmpty(article.getTabId()))
//                    swipeListener.selectTab(article.getTabId());
//            });
        }
    }
}
