package com.ziro.bullet.adapters.NewFeed.newHomeArticle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.CommunityFeed.SuggestedReelsHolder;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.fragments.CategoryFragment;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.model.articles.Article;

import java.util.ArrayList;
import java.util.List;

public class HomeArticlesAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int ARTICLES_ITEM = 0;
    private static int REELS_ITEM = 1;
    private static int SUGGESTED_TOPICS_ITEM = 2;
    private static int SUGGESTED_CHANNELS_ITEM = 3;
    private static int AD_ITEM = 4;

    private AdFailedListener adFailedListener;
    public CategoryFragment categoryFragment;
    private List<Article> items;
    private AppCompatActivity context;
    private ShareToMainInterface shareToMainInterface;
    private NewsCallback categoryCallback;
    private OnGotoChannelListener gotoChannelListener;
    private DetailsActivityInterface detailsActivityInterface;
    private GoHome goHomeMainActivity;
    private CommentClick commentClick;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private PrefConfig mPrefConfig;
    private int currentArticlePosition = 0;
    private ShareBottomSheet shareBottomSheet;

    public HomeArticlesAdapterNew(AppCompatActivity context, ArrayList<Article> items, CommentClick commentClick,
                                  DetailsActivityInterface detailsActivityInterface,
                                  GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, NewsCallback categoryCallback,
                                  OnGotoChannelListener gotoChannelListener, ShowOptionsLoaderCallback showOptionsLoaderCallback,
                                  AdFailedListener adFailedListener) {

        this.context = context;
        this.items = items;
        this.commentClick = commentClick;
        this.goHomeMainActivity = goHomeMainActivity;
        this.shareToMainInterface = shareToMainInterface;
        this.gotoChannelListener = gotoChannelListener;
        this.categoryCallback = categoryCallback;
        this.detailsActivityInterface = detailsActivityInterface;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.adFailedListener = adFailedListener;
        mPrefConfig = new PrefConfig(context);
    }

    public HomeArticlesAdapterNew(AppCompatActivity context, List<Article> items, CommentClick commentClick) {
        this.context = context;
        this.items = items;
        this.commentClick = commentClick;
        mPrefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ARTICLES_ITEM) {
            View articleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item_new, parent, false);
            return new NewHomeArticlesViewHolder(articleView);
        } else if (viewType == REELS_ITEM) {
            View reelsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_suggested_item, parent, false);
            return new SuggestedReelsHolder(reelsView, context, false, false);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Article article = items.get(position);
        if (holder instanceof NewHomeArticlesViewHolder) {
            ((NewHomeArticlesViewHolder) holder).onBind(article, position,items);
        } else if (holder instanceof SuggestedReelsHolder) {
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
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Article article = items.get(position);
        if (article.getType() != null) {
            if (article.getType().equalsIgnoreCase("EXTENDED") ||
                    article.getType().equalsIgnoreCase("SIMPLE")) {
                return ARTICLES_ITEM;
            } else if (article.getType().equalsIgnoreCase("REELS")) {
                return REELS_ITEM;
            } else if (article.getType().equalsIgnoreCase("TOPICS")) {
                return SUGGESTED_TOPICS_ITEM;
            } else if (article.getType().equalsIgnoreCase("CHANNELS")) {
                return SUGGESTED_CHANNELS_ITEM;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
