package com.newsreels.app.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsreels.app.R;
import com.newsreels.app.adapters.discover.ArticleSkeletonViewHolder;
import com.newsreels.app.adapters.discover.ImageTCPCardViewHolder;
import com.newsreels.app.model.NewDiscoverPage.DiscoverItem;
import com.newsreels.app.model.TCP.TCP;
import com.newsreels.app.presenter.FollowUnfollowPresenter;
import com.newsreels.app.viewHolder.DiscoverHeaderViewHolder;

import java.util.ArrayList;

public class CustomDiscoverTCPListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADER = 2;
    private ArrayList<TCP> mData;
    private LayoutInflater mInflater;
    private Activity mContext;
    private DiscoverItem mCoverData;
    private FollowUnfollowPresenter presenter;
    private String type;

    public CustomDiscoverTCPListAdapter(String type, Activity context, ArrayList<TCP> data, DiscoverItem mCoverData) {
        this.mInflater = LayoutInflater.from(context);
        this.type = type;
        this.mData = data;
        this.mContext = context;
        this.mCoverData = mCoverData;
        presenter = new FollowUnfollowPresenter(context);
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
                view = mInflater.inflate(R.layout.skeleton_discover_tcp_list, parent, false);
                return new ArticleSkeletonViewHolder(view);
            default:
                view = mInflater.inflate(R.layout.discover_item_tcp, parent, false);
                return new ImageTCPCardViewHolder(view, mContext);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TCP tcp = mData.get(position);
        if (tcp != null) {
            switch (holder.getItemViewType()) {
                case TYPE_HEADER:
                    ((DiscoverHeaderViewHolder) holder).bind(position, mCoverData);
                    break;
                case TYPE_LOADER:
                    ((ArticleSkeletonViewHolder) holder).bind(position);
                    break;
                default:
                    ((ImageTCPCardViewHolder) holder).bind(type, tcp, presenter);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        TCP tcp = mData.get(position);
        if (tcp != null &&  !TextUtils.isEmpty(tcp.getType())) {
            if (tcp.getType().equalsIgnoreCase("HEADER")) {
                return TYPE_HEADER;
            } else if (tcp.getType().equalsIgnoreCase("LOADER")) {
                return TYPE_LOADER;
            } else {
                return TYPE_ITEM;
            }
        } else {
            return TYPE_ITEM;
        }
    }
}
