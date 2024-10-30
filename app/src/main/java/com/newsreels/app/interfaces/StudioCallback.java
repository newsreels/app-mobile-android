package com.newsreels.app.interfaces;

import com.newsreels.app.model.Reel.ReelResponse;

public interface StudioCallback {
    void loaderShow(boolean flag);

    void error(String error);


    void success(ReelResponse body);
}
