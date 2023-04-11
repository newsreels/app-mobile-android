package com.ziro.bullet.interfaces;


import com.ziro.bullet.data.caption.CaptionResponse;

public interface CaptionApiCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(CaptionResponse response);
}
