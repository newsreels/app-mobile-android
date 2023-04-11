package com.ziro.bullet.adapters.comment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.comment.Comment;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_COMMENT = 0;

    private Activity mContext;
    private PrefConfig prefConfig;
    private CommentCallback mCallback;
    private ArrayList<Comment> mItems = new ArrayList<>();
    private int ViewType = 0;
    private String article_id;

    public CommentsAdapter(ArrayList<Comment> mItems, Activity context, int ViewType, CommentCallback callback, String article_id) {
        this.ViewType = ViewType;
        this.article_id = article_id;
        this.mCallback = callback;
        this.mItems = mItems;
        this.mContext = context;
        this.prefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view, mContext, article_id,mItems, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mItems != null && mItems.size() > 0) {
            Comment item = mItems.get(position);
            if (item != null) {
                ((CommentViewHolder) holder).bind(position, item, ViewType, mCallback);
            }
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        return TYPE_COMMENT;
//    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface CommentCallback {
        void onParentReply(int position, Comment comment, String type, CommentsAdapter adapter, ArrayList<Comment> comments);
        void onChildReply(int position, Comment comment, String type, RepliesAdapter adapter, ArrayList<Comment> childItems);
        void onPagination(int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter);
        void updateParentId(String name, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter);
    }
}
