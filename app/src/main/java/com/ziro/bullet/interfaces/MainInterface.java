package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;

import java.util.ArrayList;

public interface MainInterface {
    void loaderShow(boolean flag);

    void error(String error, int load);

    void error404(String error);

    void success(ArticleResponse response, int load, int category, String topic);

    void success(CategoryResponse response);

    void UserTopicSuccess(ArrayList<Topics> response);

    void UserInfoSuccess(Info info);

    void UserSourceSuccess(SourceModel response);
}
