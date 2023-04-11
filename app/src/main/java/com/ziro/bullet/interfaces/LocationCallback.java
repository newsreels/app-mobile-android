package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.location.LocationModel;

public interface LocationCallback {

    void loaderShow(boolean flag);

    void error(String error);


    void success(LocationModel response);

    void addSuccess(int position);

    void searchSuccess(LocationModel body);
}
