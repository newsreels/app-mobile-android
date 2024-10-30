package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.sources.Source;

public interface ChannelDetailsInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void success(Source source);

    void update(String id, String image, String cover);
}
