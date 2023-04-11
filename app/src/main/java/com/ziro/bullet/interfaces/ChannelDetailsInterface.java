package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.sources.Source;

public interface ChannelDetailsInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void success(Source source);

    void update(String id, String image, String cover);
}
