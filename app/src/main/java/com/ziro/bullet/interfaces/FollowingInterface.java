package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.model.Tabs.DataItem;

public interface FollowingInterface {
    void loaderShow(boolean flag);

    void error(String error, int load);

    void onTopicFollowSuccess(DataItem dataItem, int position);

    void onFollowedTopicsSuccess(TopicsModel response);

    void onFollowedChannelsSuccess(SourceModel response);

    void onFollowedLocationSuccess(LocationModel response);

    void onFollowedAuthorsSuccess(AuthorSearchResponse response);

}
