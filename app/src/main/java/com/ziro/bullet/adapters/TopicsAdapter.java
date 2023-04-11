package com.ziro.bullet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.model.Menu.Category;

import java.util.ArrayList;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {

    private ArrayList<Category> topicsArrayList;
    private LayoutInflater mInflater;
    private ItemClickListener itemClickListener;
    private Context mContext;

    public TopicsAdapter(Context context, ArrayList<Category> topicsArrayList) {
        this.mInflater = LayoutInflater.from(context);
        this.topicsArrayList = topicsArrayList;
        this.mContext = context;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.search_topics_new_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category item = topicsArrayList.get(position);
        holder.topic.setText(topicsArrayList.get(position).getName());

        if (item.isFavorite()) {
            holder.follow.setImageResource(R.drawable.ic_unfollow_cat);
        } else {
            holder.follow.setImageResource(R.drawable.ic_follow_cat);
        }

        holder.itemView.setOnClickListener(v -> itemClickListener.onItemTopicClick(position, topicsArrayList.get(position)));

        holder.follow.setOnClickListener(v -> {
            if (itemClickListener != null) {
//                itemClickListener.onItemTopicClick(position, topicsArrayList.get(position));
                if (item.isFavorite()) {
                    item.setFavorite(false);
                    itemClickListener.onItemTopicUnfollowed2(position, item);
                } else {
                    item.setFavorite(true);
                    itemClickListener.onItemTopicFollowed2(position, item);
                }
            }
        });

        if (position == getItemCount() - 1) {
            if (itemClickListener != null) {
                itemClickListener.isLastItem(true);
            }
        } else {
            if (itemClickListener != null) {
                itemClickListener.isLastItem(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return topicsArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout card;
        TextView topic;
        ImageView follow;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            follow = itemView.findViewById(R.id.iv_cat_image);
            topic = itemView.findViewById(R.id.tvLabel);
        }
    }

    public ArrayList<Category> getSelected() {
        ArrayList<Category> list = new ArrayList<>();
        if (topicsArrayList != null && topicsArrayList.size() > 0) {
            for (Category item : topicsArrayList) {
                if (item.isFavorite()) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, boolean isFav);

        void isLastItem(boolean flag);

        void onItemTopicClick(int position, Category item);

        void onItemTopicFollowed2(int position, Category item);

        void onItemTopicUnfollowed2(int position, Category item);
    }
}
