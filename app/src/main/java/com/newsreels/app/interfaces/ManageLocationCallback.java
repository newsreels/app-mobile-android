package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.location.LocationModel;

public interface ManageLocationCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(LocationModel response);
}
