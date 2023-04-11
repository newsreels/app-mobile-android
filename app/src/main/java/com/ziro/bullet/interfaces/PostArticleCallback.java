package com.ziro.bullet.interfaces;


import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.Reel.ReelsItem;

public interface PostArticleCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(Object responseBody);

    void successDelete();

    void createSuccess(Article responseBody, String type);

    void createSuccess(ReelsItem responseBody, String type);

    void uploadSuccess(String url, String type);

    void proceedToUpload();

    void onProgressUpdate(int percentage);
}
