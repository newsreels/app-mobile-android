package com.ziro.bullet.adapters.relevant;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.relevant.callbacks.SourceFollowingCallback;
import com.ziro.bullet.adapters.relevant.callbacks.TopicsFollowingCallback;
import com.ziro.bullet.data.PrefConfig;

import com.ziro.bullet.data.models.relevant.Location;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class FirstViewHolder extends RecyclerView.ViewHolder {

    final TextView text1;
    final TextView text2;
    final ImageView image;
    final ImageView imageBack;
    final CardView card;
    final LinearLayout unfollow;
    final LinearLayout follow;
    final RelativeLayout buttons;

    public FirstViewHolder(@NonNull View itemView) {
        super(itemView);

        unfollow = itemView.findViewById(R.id.unfollow);
        follow = itemView.findViewById(R.id.follow);
        text1 = itemView.findViewById(R.id.text1);
        card = itemView.findViewById(R.id.card);
        text2 = itemView.findViewById(R.id.text2);
        image = itemView.findViewById(R.id.image);
        imageBack = itemView.findViewById(R.id.imageBack);
        buttons = itemView.findViewById(R.id.buttons);
    }

    public void bind(ArrayList<Topics> topicArrayList,
                     ArrayList<Location> locationArrayList,
                     ArrayList<Source> sourceArrayList,
                     ArrayList<Author> authorSearchArrayList,
                     TopicsFollowingCallback followingCallback,
                     SourceFollowingCallback followingCallback1,
                     SearchRelevantMainAdapter.FollowingCallback followingCallback2,
                     View.OnClickListener topicFollowClick,
                     View.OnClickListener sourceFollowClick,
                     View.OnClickListener locationFollowClick,
                     View.OnClickListener authorFollowClick, PrefConfig mPrefConfig
                     ) {

        image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
        if (topicArrayList != null && topicArrayList.size() >= 1) {
            Topics topic = topicArrayList.get(0);

            if (!TextUtils.isEmpty(topic.getIcon())) {
                Picasso.get().load(topic.getIcon())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(image);
            } else {
                image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            text1.setText(topic.getName());

//            if(!topic.isFavorite()) {
//                unfollow.setVisibility(View.VISIBLE);
//                follow.setVisibility(View.GONE);
//            }else{
//                unfollow.setVisibility(View.GONE);
//                follow.setVisibility(View.VISIBLE);
//            }

            itemView.setOnClickListener(v -> {
                if (followingCallback != null) {
//                    followingCallback.onItemClicked(topic);
                }
            });

            buttons.setOnClickListener(v -> {
                topicFollowClick.onClick(follow);
                if (follow.getVisibility() == View.VISIBLE) {
                    unfollow.setVisibility(View.VISIBLE);
                    follow.setVisibility(View.GONE);
                    if (followingCallback != null) {
//                        followingCallback.onItemFollowed(topic);
                    }
                } else {
                    unfollow.setVisibility(View.GONE);
                    follow.setVisibility(View.VISIBLE);
                    if (followingCallback != null) {
//                        followingCallback.onItemUnfollowed(topic);
                    }
                }
            });
        }
        if (locationArrayList != null && locationArrayList.size() >= 1) {
            Location location = locationArrayList.get(0);

            if (!TextUtils.isEmpty(location.getIcon())) {
                Picasso.get().load(location.getIcon())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(image);
            } else {
                image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            text1.setText(location.getName());

            if(!location.isFavorite()) {
                unfollow.setVisibility(View.VISIBLE);
                follow.setVisibility(View.GONE);
            }else{
                unfollow.setVisibility(View.GONE);
                follow.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> {
                if (followingCallback2 != null) {
                    followingCallback2.onItemClicked(location);
                }
            });

            buttons.setOnClickListener(v -> {
                locationFollowClick.onClick(follow);
                if (follow.getVisibility() == View.VISIBLE) {
                    unfollow.setVisibility(View.VISIBLE);
                    follow.setVisibility(View.GONE);
                    if (followingCallback2 != null) {
                        followingCallback2.onItemFollowed(location);
                    }
                } else {
                    unfollow.setVisibility(View.GONE);
                    follow.setVisibility(View.VISIBLE);
                    if (followingCallback2 != null) {
                        followingCallback2.onItemUnfollowed(location);
                    }
                }
            });
        }
        if (sourceArrayList != null && sourceArrayList.size() >= 1) {
            Source source = sourceArrayList.get(0);
            if (!TextUtils.isEmpty(source.getIcon())) {
                Picasso.get().load(source.getIcon())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(image);
            } else {
                image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }
            text1.setText(source.getName());

//            unfollow.setVisibility(View.VISIBLE);
//            follow.setVisibility(View.GONE);

            if(!source.isFavorite()) {
                unfollow.setVisibility(View.VISIBLE);
                follow.setVisibility(View.GONE);
            }else{
                unfollow.setVisibility(View.GONE);
                follow.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> {
                if (followingCallback1 != null) {
//                    followingCallback1.onItemClicked(source);
                }
            });

            buttons.setOnClickListener(v -> {
                sourceFollowClick.onClick(follow);
                if (follow.getVisibility() == View.VISIBLE) {
                    unfollow.setVisibility(View.VISIBLE);
                    follow.setVisibility(View.GONE);
                    if (followingCallback1 != null) {
//                        followingCallback1.onItemFollowed(source);
                    }
                } else {
                    unfollow.setVisibility(View.GONE);
                    follow.setVisibility(View.VISIBLE);
                    if (followingCallback1 != null) {
//                        followingCallback1.onItemUnfollowed(source);
                    }
                }
            });
        }
        if (authorSearchArrayList != null && authorSearchArrayList.size() >= 1) {
            Author authorSearch = authorSearchArrayList.get(0);
            if (!TextUtils.isEmpty(authorSearch.getProfile_image())) {
                Picasso.get().load(authorSearch.getProfile_image())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(image);
            } else {
                image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }
            text1.setText(String.format("%s %s", authorSearch.getFirst_name(), authorSearch.getLast_name()));

//            if(!source.isFavorite()) {
//                unfollow.setVisibility(View.VISIBLE);
//                follow.setVisibility(View.GONE);
//            }else{
//                unfollow.setVisibility(View.GONE);
//                follow.setVisibility(View.VISIBLE);
//            }
//
//            itemView.setOnClickListener(v -> {
//                if (followingCallback1 != null) {
//                    followingCallback1.onItemClicked(source);
//                }
//            });
//
//            buttons.setOnClickListener(v -> {
//                sourceFollowClick.onClick(follow);
//                if (follow.getVisibility() == View.VISIBLE) {
//                    unfollow.setVisibility(View.VISIBLE);
//                    follow.setVisibility(View.GONE);
//                    if (followingCallback1 != null) {
//                        followingCallback1.onItemFollowed(source);
//                    }
//                } else {
//                    unfollow.setVisibility(View.GONE);
//                    follow.setVisibility(View.VISIBLE);
//                    if (followingCallback1 != null) {
//                        followingCallback1.onItemUnfollowed(source);
//                    }
//                }
//            });
        }


    }
}
