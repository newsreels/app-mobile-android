package com.newsreels.app.interfaces;


import com.newsreels.app.data.caption.CaptionResponse;

public interface CaptionApiCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(CaptionResponse response);
}
