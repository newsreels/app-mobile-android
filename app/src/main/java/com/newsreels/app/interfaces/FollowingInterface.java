package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.author.AuthorSearchResponse;
import com.newsreels.app.data.models.location.LocationModel;
import com.newsreels.app.data.models.sources.SourceModel;
import com.newsreels.app.data.models.topics.TopicsModel;
import com.newsreels.app.model.Tabs.DataItem;

public interface FollowingInterface {
    void loaderShow(boolean flag);

    void error(String error, int load);

    void onTopicFollowSuccess(DataItem dataItem, int position);

    void onFollowedTopicsSuccess(TopicsModel response);

    void onFollowedChannelsSuccess(SourceModel response);

    void onFollowedLocationSuccess(LocationModel response);

    void onFollowedAuthorsSuccess(AuthorSearchResponse response);

}
