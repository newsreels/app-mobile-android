package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.location.LocationModel;

public interface ManageLocationCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(LocationModel response);
}
