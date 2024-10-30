package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.location.LocationModel;

public interface LocationCallback {

    void loaderShow(boolean flag);

    void error(String error);


    void success(LocationModel response);

    void addSuccess(int position);

    void searchSuccess(LocationModel body);
}
