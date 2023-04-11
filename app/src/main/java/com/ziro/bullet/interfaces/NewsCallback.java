package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;

public interface NewsCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(ArticleResponse response, boolean offlineData);

    void successArticle(Article article);

    void homeSuccess(HomeResponse homeResponse, String currentPage);

    void nextPosition(int position);

    void nextPositionNoScroll(int position, boolean shouldNotify);

    /**
     * This height is used to set padding in the articles list at the bottom. So that the last item will be scrolled to top
     * @param height item height
     */
    void onItemHeightMeasured(int height);
}
