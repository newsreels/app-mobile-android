package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.Reel.ReelResponse;

public interface StudioCallback {
    void loaderShow(boolean flag);

    void error(String error);


    void success(ReelResponse body);
}
