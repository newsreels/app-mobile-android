package com.newsreels.app.adapters.relevant.callbacks;


import com.newsreels.app.data.models.topics.Topics;

public interface TopicsFollowingCallback {

    void onItemFollowed(Topics topic);

    void onItemUnfollowed(Topics topic);

    void onItemClicked(Topics topic);
}
