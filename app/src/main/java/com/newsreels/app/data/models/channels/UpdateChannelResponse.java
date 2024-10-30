package com.newsreels.app.data.models.channels;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.sources.Source;

public class UpdateChannelResponse {

    @SerializedName("channel")
    private Source channel;

    public Source getChannel() {
        return channel;
    }

    public void setChannel(Source channel) {
        this.channel = channel;
    }
}
