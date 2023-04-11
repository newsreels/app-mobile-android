package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;

import retrofit2.Response;

public interface SearchInterface {
    void loaderShow(boolean flag);

    void error(String error, int load);

    void onFollowedTopicsSuccess(TopicsModel response);

    void onFollowedChannelsSuccess(SourceModel response);

    void onFollowedLocationSuccess(LocationModel response);

    void onSuggestedTopicsSuccess(TopicsModel response);

    void onSuggestedChannelsSuccess(SourceModel response);

    void onSuggestedLocationSuccess(LocationModel response);

    void onSearchTopicsSuccess(TopicsModel response);

    void onSearchChannelsSuccess(SourceModel response);

    void onSearchLocationSuccess(LocationModel response);

    void onDiscoverSuccess(Response<NewDiscoverResponse> response);

    void onSearchArticleSuccess(ArticleResponse response,Boolean isPagination);

    void onRelevantSuccess(RelevantResponse response);

    void onRelevantArticlesSuccess(ArticleResponse response);

    void onSearchAuthorsSuccess(AuthorSearchResponse response);

    void onFollowedAuthorsSuccess(AuthorSearchResponse response);

    void onSuggestedAuthorsSuccess(AuthorSearchResponse response);
}
