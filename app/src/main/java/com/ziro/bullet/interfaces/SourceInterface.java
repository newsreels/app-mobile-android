package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.sources.CategoryModel;
import com.ziro.bullet.data.models.sources.SourceModel;

public interface SourceInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(SourceModel response, boolean setAdapter);

    void successLocalPaginationResult(CategoryModel response);
    void successInternationalPaginationResult(SourceModel response);

    void addSuccess(int position);
    void deleteSuccess(int position);

    void searchSuccess(SourceModel body);
}
