package com.ziro.bullet.interfaces;


public interface ChannelApiCallback {

    void loaderShow(boolean flag);

    void error(String error, String img);

    void success(Object data);
}
