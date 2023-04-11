package com.ziro.bullet.interfaces;

public interface ApiCallbacks {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(Object response);

}
