package com.ziro.bullet.interfaces;


public interface ProfileApiCallback {
    void loaderShow(boolean flag);

    void error(String error, String img);

    void success();
}
