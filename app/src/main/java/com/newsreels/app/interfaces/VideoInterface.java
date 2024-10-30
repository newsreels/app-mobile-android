package com.newsreels.app.interfaces;

import com.newsreels.app.model.Reel.ReelResponse;

public interface VideoInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(ReelResponse reelResponse, boolean reload);

    void nextVideo(int position);
}
