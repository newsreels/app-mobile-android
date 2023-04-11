package com.ziro.bullet.interfaces;

public interface CreateChannelCallback {
    void loaderShow(boolean flag);
    void error(String error);
    void mediaUploaded(String url, String type, int request);
    void validName(boolean isValid);
}