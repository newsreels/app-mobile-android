package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.Reel.ReelResponse;

public interface VideoInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(ReelResponse reelResponse, boolean reload);

    void nextVideo(int position);
}
