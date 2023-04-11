package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.Reel.ReelResponse;

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
