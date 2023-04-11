package com.ziro.bullet.adapters.relevant.callbacks;


import com.ziro.bullet.data.models.topics.Topics;

public interface TopicsFollowingCallback {

    void onItemFollowed(Topics topic);

    void onItemUnfollowed(Topics topic);

    void onItemClicked(Topics topic);
}
