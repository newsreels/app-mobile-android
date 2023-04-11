package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.FollowResponse;
import com.ziro.bullet.model.Menu.Category;
import com.ziro.bullet.model.Menu.CategoryResponse;


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
