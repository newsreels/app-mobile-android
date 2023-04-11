package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.model.CategorizedChannelsData;

import java.util.ArrayList;

public interface UserChannelCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(ArrayList<Source> channels);

    void successData(ArrayList<CategorizedChannelsData> channels);

    void channelSelected();
}
