package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.discover.ArticleSkeletonViewHolder;
import com.ziro.bullet.adapters.discover.ListSmallCardViewHolder;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.viewHolder.DiscoverHeaderViewHolder;

import java.util.ArrayList;

public class CustomDiscoverListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADER = 2;
    private ArrayList<Article> mData;
    private LayoutInflater mInflater;
    private CustomDiscoverListAdapter.ItemClickListener mClickListener;
    private Activity mContext;
    private PrefConfig mPrefConfig;
    private AdapterCallback adapterCallback;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private DiscoverItem mCoverData;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private CommentClick commentClick;

    public CustomDiscoverListAdapter(CommentClick commentClick, Activity context, ArrayList<Article> data, AdapterCallback adapterCallback, DiscoverItem mCoverData, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        this.mInflater = LayoutInflater.from(context);
        this.commentClick = commentClick;
        this.mCoverData = mCoverData;
        this.mData = data;
        this.mContext = context;
        this.adapterCallback = adapterCallback;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.mPrefConfig = new PrefConfig(context);
        shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_HEADER:
                view = mInflater.inflate(R.layout.discover_list_header, parent, false);
                return new DiscoverHeaderViewHolder(view, mContext);
            case TYPE_LOADER:
                view = mInflater.inflate(R.layout.skeleton_discover_article_list, parent, false);
                return new ArticleSkeletonViewHolder(view);
            default:
                view = mInflater.inflate(R.layout.discover_item_small_card, parent, false);
                return new ListSmallCardViewHolder(commentClick, view, mContext, adapterCallback, shareBottomSheetPresenter, showOptionsLoaderCallback);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Article articlesItem = mData.get(position);
        if (articlesItem != null) {
            switch (holder.getItemViewType()) {
                case TYPE_HEADER:
                    ((DiscoverHeaderViewHolder) holder).bind(position, mCoverData);
                    break;
                case TYPE_LOADER:
                    ((ArticleSkeletonViewHolder) holder).bind(position);
                    break;
                default:
                    ((ListSmallCardViewHolder) holder).bind(position, articlesItem);
                    ((ListSmallCardViewHolder) holder).getLine().setVisibility(View.VISIBLE);
                    ((ListSmallCardViewHolder) holder).getDivider().setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        Article article = mData.get(position);
        if (article != null &&  !TextUtils.isEmpty(article.getType())) {
            if (article.getType().equalsIgnoreCase("HEADER")) {
                return TYPE_HEADER;
            } else if (article.getType().equalsIgnoreCase("LOADER")) {
                return TYPE_LOADER;
            } else {
                return TYPE_ITEM;
            }
        } else {
            return TYPE_ITEM;
        }
    }

    public void setClickListener(CustomDiscoverListAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, boolean isFav);

        void isFavorite(View view, int position);

        void isLastItem(boolean flag);
    }
}
