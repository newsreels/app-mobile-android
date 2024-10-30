package com.newsreels.app.interfaces;

import com.newsreels.app.model.FollowResponse;
import com.newsreels.app.model.Menu.Category;
import com.newsreels.app.model.Menu.CategoryResponse;


public interface TopicInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(CategoryResponse response, boolean isPagination);

    void searchSuccess(CategoryResponse response, boolean isPagination);

    void addSuccess(int position);

    void deleteSuccess(int position);

    void getTopicsFollow(FollowResponse response, int position, Category topic);

}
