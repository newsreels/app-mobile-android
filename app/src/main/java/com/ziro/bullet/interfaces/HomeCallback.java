package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.home.HomeModel;
import com.ziro.bullet.model.Menu.CategoryResponse;

public interface HomeCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(HomeModel response);

    void searchSuccess(CategoryResponse body, boolean isPagination);
}
