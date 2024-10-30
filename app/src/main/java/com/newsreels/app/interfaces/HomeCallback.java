package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.home.HomeModel;
import com.newsreels.app.model.Menu.CategoryResponse;

public interface HomeCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(HomeModel response);

    void searchSuccess(CategoryResponse body, boolean isPagination);
}
