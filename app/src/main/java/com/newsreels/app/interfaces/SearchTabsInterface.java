package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.location.LocationModel;
import com.newsreels.app.data.models.relevant.RelevantResponse;
import com.newsreels.app.data.models.sources.SourceModel;
import com.newsreels.app.model.Reel.ReelsItem;
import com.newsreels.app.model.articles.ArticleResponse;
import com.newsreels.app.model.Menu.CategoryResponse;
import com.newsreels.app.model.Reel.ReelResponse;

import java.util.List;

public interface SearchTabsInterface {

    void loaderShow(boolean flag);

    void error(String error, int load);

    void onSearchArticleSuccess(ArticleResponse response,Boolean isPagination);

    void onRelevantArticlesSuccess(ArticleResponse response);

    void onRelevantSuccess(RelevantResponse response);

    void onSearchChannelsSuccess(SourceModel response);

    void onSearchPlacesSuccess(LocationModel response);

    void onSearchTopicsSuccess(CategoryResponse response);

    void onReelSuccess(ReelResponse response);

    void searchChildSecondOnClick(ReelsItem response, List<ReelsItem> reelsList, int position, String page);


}
