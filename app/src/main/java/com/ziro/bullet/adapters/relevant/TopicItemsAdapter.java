package com.ziro.bullet.adapters.relevant;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.relevant.callbacks.TopicsFollowingCallback;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class TopicItemsAdapter extends RecyclerView.Adapter<TopicItemsAdapter.ViewHolder> {

    private final ArrayList<Topics> topicsArrayList;
    private final TopicsFollowingCallback followingCallback1;
    private PrefConfig mPrefConfig;
    private Context context;

    public TopicItemsAdapter(Context context, ArrayList<Topics> topicsArrayList, TopicsFollowingCallback followingCallback1) {
        this.topicsArrayList = topicsArrayList;
        this.followingCallback1 = followingCallback1;
        this.context = context;
        mPrefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public TopicItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topics_feed, parent, false);
        return new TopicItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicItemsAdapter.ViewHolder holder, int position) {
        if (topicsArrayList != null && topicsArrayList.size() > 0) {
            Topics topics = topicsArrayList.get(position);

            holder.title.setText("#"+topics.getName());
//            if (!TextUtils.isEmpty(topics.getImage())) {
//                Picasso.get().load(topics.getImage()).error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                        .into(holder.topicImage);
//            } else {
//                holder.topicImage.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
//            }

            if (topics.isFavorite()) {
                holder.ivSelected.setProgress(1f);
            } else {
                holder.ivSelected.setProgress(0f);
            }

            holder.ivSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.followAnimation(holder.ivSelected, 500);
                    if (topics.isFavorite()) {
                        followingCallback1.onItemUnfollowed(topics);
                        topics.setFavorite(false);
                    } else {
                        followingCallback1.onItemFollowed(topics);
                        topics.setFavorite(true);
                    }
                }
            });

            holder.itemView.setOnClickListener(v -> {
                if (followingCallback1 != null) {
                     followingCallback1.onItemClicked(topics);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return topicsArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

//        private final ImageView topicImage;
        private final TextView title;
        private final LottieAnimationView ivSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            topicImage = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            ivSelected = itemView.findViewById(R.id.selected);
        }
    }
}
