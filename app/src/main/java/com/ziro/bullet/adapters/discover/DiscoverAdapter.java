package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.CommunityFeed.SuggestedAuthorsHolder;
import com.ziro.bullet.adapters.CommunityFeed.SuggestedReelsHolder;
import com.ziro.bullet.adapters.NewFeed.PlacesViewHolder;
import com.ziro.bullet.adapters.NewFeed.SuggestedArticlesHolder;
import com.ziro.bullet.adapters.relevant.ChannelsViewHolder;
import com.ziro.bullet.adapters.relevant.CircleTopicsViewHolder;
import com.ziro.bullet.fragments.SearchModifiedFragment;
import com.ziro.bullet.fragments.searchNew.searchviewholder.TopicsNewViewHolder;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DiscoverCallback;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;

import java.util.ArrayList;


public class DiscoverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_REEL_LARGE = 0;
    private static final int TYPE_TOPICS_LARGE = 1;
    private static final int TYPE_YOUTUBE = 2;
    private static final int TYPE_VIDEO = 3;
    private static final int TYPE_HORIZONTAL_ARTICLES_COUNTERS = 4;
    private static final int TYPE_ARTICLE_IMAGE = 5;
    private static final int TYPE_PLACES = 6;
    private static final int TYPE_HORIZONTAL_ARTICLES = 7;
    private static final int TYPE_CHANNELS = 8;
    private static final int TYPE_AUTHORS = 9;
    private static final int TYPE_REEL_SMALL = 10;
    private static final int TYPE_HORIZONTAL_VIDEOS = 11;
    private static final int TYPE_TOPICS_SMALL = 12;

    private ArrayList<DiscoverItem> items;
    private Activity context;
    private AdapterCallback adapterCallback;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private CommentClick commentClick;
    private Callback callback;
    private DiscoverCallback discoverCallback;
    private SearchModifiedFragment.OnFragmentInteractionListener listener;

    public DiscoverAdapter(CommentClick commentClick, Activity context, ArrayList<DiscoverItem> items,
                           AdapterCallback adapterCallback, ShowOptionsLoaderCallback showOptionsLoaderCallback,
                           Callback callback, DiscoverCallback discoverCallback, SearchModifiedFragment.OnFragmentInteractionListener listener) {
        this.listener = listener;
        this.commentClick = commentClick;
        this.discoverCallback = discoverCallback;
        this.adapterCallback = adapterCallback;
        this.context = context;
        this.items = items;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.callback = callback;
    }

    public void updateData(ArrayList<DiscoverItem> data) {
        for (DiscoverItem discoverLocal : items) {
            for (DiscoverItem discoverApi : data) {

            }
        }
        items.addAll(data);
        notifyDataSetChanged();
    }

    public void addItem(int position, DiscoverItem data) {
        items.add(position, data);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public ArrayList<DiscoverItem> getItems() {
        return items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case TYPE_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_video_card, parent, false);
                VideoCardViewHolder holder = new VideoCardViewHolder(view, context);
//            if (article.getData() != null && article.getData().getVideo() != null && article.getData().getVideo().getMediaMeta() != null) {
//                MediaMeta mediaMeta = article.getData().getVideo().getMediaMeta();
////                resizeVideoView(holder, mediaMeta.getHeight(), mediaMeta.getWidth());
//            }
                return holder;
            case TYPE_YOUTUBE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_youtube_card, parent, false);
                YoutubeCardViewHolder youtubeCardViewHolder = new YoutubeCardViewHolder(view, context);
//            if (article.getData() != null && article.getData().getVideo() != null && article.getData().getVideo().getMediaMeta() != null) {
//                MediaMeta mediaMeta = article.getData().getVideo().getMediaMeta();
//                resizeVideoView(holder, mediaMeta.getHeight(), mediaMeta.getWidth());
//            }
                return youtubeCardViewHolder;
            case TYPE_REEL_LARGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_video_item, parent, false);
                return new DiscoverReelViewHolder(view, context);
            case TYPE_REEL_SMALL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new SuggestedReelsHolder(view, context, true,false);
            case TYPE_CHANNELS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new ChannelsViewHolder(view, true);
            case TYPE_TOPICS_LARGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new TopicsNewViewHolder(view, context, true, true);
            case TYPE_TOPICS_SMALL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new CircleTopicsViewHolder(view, context, true);
            case TYPE_AUTHORS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new SuggestedAuthorsHolder(view, context);
            case TYPE_HORIZONTAL_VIDEOS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_video_item, parent, false);
                DiscoverVideoViewHolder discoverVideoViewHolder = new DiscoverVideoViewHolder(view, context);
                ViewTreeObserver viewTreeObserver = discoverVideoViewHolder.itemView.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            discoverVideoViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = discoverVideoViewHolder.itemView.getHeight();
                            if (discoverCallback != null)
                                discoverCallback.onHeightChange(viewHeight);
                        }
                    });
                }
                return new DiscoverVideoViewHolder(view, context);
            case TYPE_HORIZONTAL_ARTICLES:
            case TYPE_HORIZONTAL_ARTICLES_COUNTERS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggested_articles, parent, false);
                return new SuggestedArticlesHolder(view, context, false, null, null);
            case TYPE_PLACES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
                return new PlacesViewHolder(view, context, true, listener);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_big_article_card, parent, false);
                return new ArticleCardViewHolder(view, context, adapterCallback);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DiscoverItem discover = items.get(position);
        if (holder instanceof VideoCardViewHolder) {
            ((VideoCardViewHolder) holder).bind(position, discover);
        } else if (holder instanceof YoutubeCardViewHolder) {
            ((YoutubeCardViewHolder) holder).bind(position, discover);
        } else if (holder instanceof DiscoverReelViewHolder) {
            ((DiscoverReelViewHolder) holder).bind(true, discover.getTitle(), discover.getData().getReels());
        } else if (holder instanceof SuggestedReelsHolder) {
            ((SuggestedReelsHolder) holder).bind(discover.getTitle(), discover.getData().getReels(),
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
        } else if (holder instanceof ChannelsViewHolder) {
            ((ChannelsViewHolder) holder).bind(discover.getTitle(), context, discover.getData().getSources(),
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
        } else if (holder instanceof CircleTopicsViewHolder) {
            if (discover != null) {
                ((CircleTopicsViewHolder) holder).bind(discover.getTitle(), discover.getData().getTopics(), new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                }, 0, 0);
            }


        } else if (holder instanceof SuggestedAuthorsHolder) {
            ((SuggestedAuthorsHolder) holder).bind(discover.getTitle(), discover.getData().getAuthors());
        } else if (holder instanceof DiscoverVideoViewHolder) {
            ((DiscoverVideoViewHolder) holder).bind(true, discover.getTitle(), discover.getData().getArticles());
        } else if (holder instanceof SuggestedArticlesHolder) {
            ((SuggestedArticlesHolder) holder).bind(discover.getTitle(), discover.getData().getArticles(), discover.getData().isTop(),
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
        } else if (holder instanceof ArticleCardViewHolder) {
            ((ArticleCardViewHolder) holder).bind(discover.getTitle(), discover);
        } else if (holder instanceof PlacesViewHolder) {
            ((PlacesViewHolder) holder).bind(discover.getTitle(), discover.getData().getLocations());
        }
    }

    @Override
    public int getItemViewType(int position) {
        DiscoverItem discoverItem = items.get(position);
        if (discoverItem != null && discoverItem.getData() != null && !TextUtils.isEmpty(discoverItem.getType())) {
            switch (discoverItem.getType()) {
                case "REELS":
                    if (discoverItem.getData().isLarge()) {
                        return TYPE_REEL_LARGE;
                    } else {
                        return TYPE_REEL_SMALL;
                    }
                case "TOPICS":
                    if (discoverItem.getData().isLarge()) {
                        return TYPE_TOPICS_LARGE;
                    } else {
                        return TYPE_TOPICS_SMALL;
                    }
                case "ARTICLE":
                    if (discoverItem.getData().getArticle() != null && !TextUtils.isEmpty(discoverItem.getData().getArticle().getType())) {
                        switch (discoverItem.getData().getArticle().getType()) {
                            case "IMAGE":
                                return TYPE_ARTICLE_IMAGE;
                            case "VIDEO":
                                return TYPE_VIDEO;
                            case "YOUTUBE":
                                return TYPE_YOUTUBE;
                        }
                    }
                case "ARTICLES":
                    if (discoverItem.getData().isTop()) {
                        return TYPE_HORIZONTAL_ARTICLES_COUNTERS;
                    } else {
                        return TYPE_HORIZONTAL_ARTICLES;
                    }
                case "PLACES":
                    return TYPE_PLACES;
                case "CHANNELS":
                    return TYPE_CHANNELS;
                case "AUTHORS":
                    return TYPE_AUTHORS;
                case "ARTICLE_VIDEOS":
                    return TYPE_HORIZONTAL_VIDEOS;
                default:
                    return TYPE_ARTICLE_IMAGE;
            }
        } else {
            return TYPE_ARTICLE_IMAGE;
        }
    }

    private void resizeVideoView(VideoCardViewHolder videoViewHolder, double mVideoHeight, double mVideoWidth) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int cardWidth = displayMetrics.widthPixels - (2 * context.getResources().getDimensionPixelSize(R.dimen._15sdp));
        int screenHeight = displayMetrics.heightPixels - (context.getResources().getDimensionPixelSize(R.dimen._150sdp));
        int cardHeight = (int) ((mVideoHeight * cardWidth) / mVideoWidth);
        if (cardHeight > screenHeight) {
            cardHeight = screenHeight;
        }
        if (!videoViewHolder.isHeightSet && cardHeight > 0 && videoViewHolder.videoCard.getHeight() != cardHeight) {
            videoViewHolder.videoCard.getLayoutParams().height = cardHeight;
            videoViewHolder.videoCard.requestLayout();
            videoViewHolder.isHeightSet = true;
//            videoViewHolder.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        } else
            return items.size();
    }

}
