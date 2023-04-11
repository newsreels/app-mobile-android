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


public class RepliesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_REPLY = 1;
    private Activity mContext;
    private PrefConfig prefConfig;
    private ReplyCallback mCallback;
    private ArrayList<Comment> mItems = new ArrayList<>();
    private int viewType = 0;
    private String article_id;
    private int parentPosition;

    public RepliesAdapter(ArrayList<Comment> mItems, Activity context, int viewType, String article_id, int parentPosition) {
        this.parentPosition = parentPosition;
        this.viewType = viewType;
        this.mItems = mItems;
        this.mContext = context;
        this.article_id = article_id;
        this.prefConfig = new PrefConfig(context);
    }

    public void setListener(ReplyCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_item, parent, false);
        return new ReplyViewHolder(view, mContext, article_id);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mItems != null && mItems.size() > 0) {
            Comment item = mItems.get(position);
            if (item != null) {
                ((ReplyViewHolder) holder).bind(position, item, viewType, mCallback, parentPosition);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_REPLY;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setParentId(String parentId) {

    }

    public interface ReplyCallback {
        void onReply(int layer, int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter);

        void onViewMore(int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter);

        void onPagination(int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter);

        void onWriteReply(int layer, int position, String parentName, String childName, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter);
    }
}
