package com.newsreels.app.model;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.sources.Source;

public class ChannelDetails {

    @SerializedName("channel")
    private Source channel;

    public Source getChannel() {
        return channel;
    }

    public void setChannel(Source channel) {
        this.channel = channel;
    }
}
