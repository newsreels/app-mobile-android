package com.ziro.bullet.adapters.CommunityFeed;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.feed.AdViewHolder;
import com.ziro.bullet.adapters.feed.FacebookAdViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.adapters.feed.ViewHolder;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.CommunityItemCallback;
import com.ziro.bullet.interfaces.DeleteCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ReelViewMoreCallback;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.MediaMeta;
import com.ziro.bullet.viewHolder.LastItemViewHolder;

import java.util.List;

import im.ene.toro.CacheManager;


public class CommunityFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterCallback, CacheManager, DeleteCallback {
    private static final int TYPE_CARD = 0;
    private static final int TYPE_LIST = 1;
    private static final int TYPE_YOUTUBE = 2;
    private static final int TYPE_VIDEO = 3;
    private static final int TYPE_AD_GOOGLE = 4;
    private static final int TYPE_AD_FACEBOOK = 5;
    private static final int TYPE_NEW_POST = 6;
    private static final int TYPE_SUGGESTED_REELS = 7;
    private static final int TYPE_SUGGESTED_AUTHORS = 8;
    private static final int TYPE_LAST_ITEM = 9;
    private static final int TYPE_TITLE = 10;
    private static final int TYPE_NO_POST = 11;

    private List<Article> items;
    private AppCompatActivity context;
    private ShareToMainInterface shareToMainInterface;
    private TempCategorySwipeListener swipeListener;
    private NewsCallback categoryCallback;
    private OnGotoChannelListener gotoChannelListener;
    private DetailsActivityInterface detailsActivityInterface;
    private GoHome goHomeMainActivity;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private boolean isWhiteOnly;
    private PrefConfig mPrefConfig;
    private boolean isPostArticle = false;
    private int currentArticlePosition = 0;
    private boolean isGotoFollowShow;
    private String type;
    private ShareBottomSheet shareBottomSheet;
    private CommentClick mCommentClick;
    private CommunityItemCallback mCommunityItemCallback;
    private AdFailedListener adFailedListener;
    private ReelViewMoreCallback viewMoreCallback;
    private Lifecycle lifecycle;

    public CommunityFeedAdapter(CommentClick mCommentClick, boolean isPostArticle, AppCompatActivity context, List<Article> items, String type, boolean isGotoFollowShow, DetailsActivityInterface detailsActivityInterface,
                                GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback,
                                OnGotoChannelListener gotoChannelListener, ShowOptionsLoaderCallback showOptionsLoaderCallback, CommunityItemCallback mCommunityItemCallback,
                                AdFailedListener adFailedListener, boolean isWhiteOnly, ReelViewMoreCallback viewMoreCallback, Lifecycle lifecycle) {
        this.mCommunityItemCallback = mCommunityItemCallback;
        this.context = context;
        this.viewMoreCallback = viewMoreCallback;
        this.isWhiteOnly = isWhiteOnly;
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_large_card_community, parent, false);
                ArticleLargeCardHolder largeCardViewHolder = new ArticleLargeCardHolder(isWhiteOnly,this, mCommentClick, type, isPostArticle, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
                LinearLayout rootCard = view.findViewById(R.id.rootCard);
                if (isWhiteOnly) {
                    view.findViewById(R.id.flag).setVisibility(View.GONE);
                    rootCard.setBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    rootCard.setBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
                }
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_small_card, parent, false);
                SmallCardHolder smallCardViewHolder = new SmallCardHolder(isWhiteOnly,this, mCommentClick, type, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
                ConstraintLayout rootCard1 = view.findViewById(R.id.cardData);
                if (isWhiteOnly) {
                    View viewFlag = view.findViewById(R.id.flag);
                    if(viewFlag != null) {
                        viewFlag.setVisibility(View.GONE);
                    }
                    rootCard1.setBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    rootCard1.setBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
                }
                ViewTreeObserver vtoSmall = smallCardViewHolder.itemView.getViewTreeObserver();
                if (vtoSmall.isAlive()) {
                    vtoSmall.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            smallCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = smallCardViewHolder.itemView.getHeight();
                            smallCardViewHolder.statusView.getLayoutParams().height = viewHeight;
                            smallCardViewHolder.statusView.requestLayout();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return smallCardViewHolder;
            case TYPE_YOUTUBE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_card_community, parent, false);
                YoutubeCardHolder youtubeViewHolder = new YoutubeCardHolder(isWhiteOnly,this, mCommentClick, type, isPostArticle, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback,lifecycle);
                CardView rootCard2 = view.findViewById(R.id.rootCard);
                if (isWhiteOnly) {
                    view.findViewById(R.id.flag).setVisibility(View.GONE);
                    rootCard2.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    rootCard2.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
                }
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_card_community, parent, false);
                VideoCardHolder videoViewHolder = new VideoCardHolder(isWhiteOnly,this, mCommentClick, type, isPostArticle, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
                CardView rootCard3 = view.findViewById(R.id.rootCard);
                if (isWhiteOnly) {
                    view.findViewById(R.id.flag).setVisibility(View.GONE);
                    rootCard3.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    rootCard3.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
                }
                return videoViewHolder;
            case TYPE_AD_GOOGLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_google_ads, parent, false);
                CardView rootCard4 = view.findViewById(R.id.rootCard);
                if (isWhiteOnly) {
                    rootCard4.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    rootCard4.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
                }
                return new AdViewHolder(isWhiteOnly, view, context, mPrefConfig, adFailedListener);
            case TYPE_AD_FACEBOOK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_fb_ads, parent, false);
                CardView rootCard5 = view.findViewById(R.id.rootCard);
                if (isWhiteOnly) {
                    rootCard5.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    rootCard5.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
                }
                return new FacebookAdViewHolder(isWhiteOnly, view, context, mPrefConfig, adFailedListener);
            case TYPE_NEW_POST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_post_card, parent, false);
                return new NewPostViewHolder(view, context, mCommunityItemCallback);
            case TYPE_SUGGESTED_REELS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                if (isWhiteOnly) {
                    view.findViewById(R.id.title).setVisibility(View.GONE);
                    TextView title2 = view.findViewById(R.id.title);
                    title2.setTextColor(context.getResources().getColor(R.color.black));
                }
                return new SuggestedReelsHolder(view, context,false,false);
            case TYPE_SUGGESTED_AUTHORS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new SuggestedAuthorsHolder(view, context);
            case TYPE_NO_POST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_no_post, parent, false);
                return new ViewHolderHeader(view);
            case TYPE_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_view, parent, false);
                RelativeLayout rootCard7 = view.findViewById(R.id.relatedMain);
                if (isWhiteOnly) {
                    TextView related_title = view.findViewById(R.id.related_title);
                    rootCard7.setBackgroundColor(context.getResources().getColor(R.color.white));
                    related_title.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    rootCard7.setBackgroundColor(context.getResources().getColor(R.color.home_bg));
                }
                return new ViewHolder(view);
            case TYPE_LAST_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_last_item, parent, false);
                return new LastItemViewHolder(view, context, mCommunityItemCallback);
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
//        videoViewHolder.cardMain.getLayoutParams().width = cardWidth;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        View viewGroup = holder.itemView;
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewGroup.getLayoutParams();
        if (position == 0) {
            params.setMargins(params.getMarginStart(), (int) context.getResources().getDimension(R.dimen._10sdp), params.getMarginEnd(), params.bottomMargin);
        }
        viewGroup.setLayoutParams(params);

        Article article = items.get(position);

        if (holder instanceof ArticleLargeCardHolder) {
            ((ArticleLargeCardHolder) holder).bind(position, article);
        } else if (holder instanceof SmallCardHolder) {
            ((SmallCardHolder) holder).bind(position, article);
        } else if (holder instanceof YoutubeCardHolder) {
            ((YoutubeCardHolder) holder).bind(position, article);
        } else if (holder instanceof VideoCardHolder) {
            ((VideoCardHolder) holder).bind(position, article);
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
        } else if (holder instanceof NewPostViewHolder) {
            ((NewPostViewHolder) holder).bind(article);
        } else if (holder instanceof SuggestedAuthorsHolder) {
            ((SuggestedAuthorsHolder) holder).bind(article.getTitle(), article.getAuthor());
        } else if (holder instanceof SuggestedReelsHolder) {
            ((SuggestedReelsHolder) holder).bind(article.getTitle(), article.getReels(),
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
        } else if (holder instanceof LastItemViewHolder) {
            ((LastItemViewHolder) holder).listener();
        } else if (holder instanceof ViewHolder) {
            if (isWhiteOnly) {
                if (((ViewHolder) holder).related_title != null && !TextUtils.isEmpty(article.getTitle())) {
                    ((ViewHolder) holder).related_title.setText(article.getTitle());
                }
            }
            ((ViewHolder) holder).bind("");
        } else if (holder instanceof ViewHolderHeader) {
//            if (isWhiteOnly) {
//                if (((ViewHolderHeader) holder).viewArticle != null && context != null) {
//                    if (!TextUtils.isEmpty(article.getLink())) {
//                        ((ViewHolderHeader) holder).viewArticle.setText(context.getResources().getString(R.string.view_original_article));
//                        ((ViewHolderHeader) holder).viewArticle.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(context, WebViewActivity.class);
//                                intent.putExtra("title", context.getResources().getString(R.string.view_original_article));
//                                intent.putExtra("url", article.getLink());
//                                context.startActivity(intent);
//                            }
//                        });
//                    } else {
//                        ((ViewHolderHeader) holder).viewArticle.setText("");
//                    }
//                }
//            }
            ((ViewHolderHeader) holder).bind(context);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Article article = null;
        if (items.size() > position && position >= 0) {
            article = items.get(position);
        }
        if (article != null) {
            if (article.getType().equalsIgnoreCase("EXTENDED")) {
                return TYPE_CARD;
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
            } else if (article.getType().equalsIgnoreCase("new")) {
                return TYPE_NEW_POST;
            } else if (article.getType().equalsIgnoreCase("suggested_reels")) {
                return TYPE_SUGGESTED_REELS;
            } else if (article.getType().equalsIgnoreCase("suggested_authors")) {
                return TYPE_SUGGESTED_AUTHORS;
            } else if (article.getType().equalsIgnoreCase("last_item")) {
                return TYPE_LAST_ITEM;
            } else if (article.getType().equalsIgnoreCase("title")) {
                return TYPE_TITLE;
            } else if (article.getType().equalsIgnoreCase("no_post")) {
                return TYPE_NO_POST;
            } else {
                return TYPE_CARD;
            }
        }
        return TYPE_CARD;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(currentArticlePosition);
        if (holder instanceof YoutubeCardHolder) {
            ((YoutubeCardHolder) holder).release();
        }
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

    @Override
    public void deleteItem(int position) {
        remove(position);
        notifyItemChanged(position);
    }
}
