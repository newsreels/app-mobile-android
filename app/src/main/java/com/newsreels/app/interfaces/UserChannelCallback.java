package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.sources.Source;
import com.newsreels.app.model.CategorizedChannelsData;

import java.util.ArrayList;

public interface UserChannelCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(ArrayList<Source> channels);

    void successData(ArrayList<CategorizedChannelsData> channels);

    void channelSelected();
}
