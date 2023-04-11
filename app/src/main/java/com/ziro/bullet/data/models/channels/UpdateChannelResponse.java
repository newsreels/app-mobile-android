package com.ziro.bullet.data.models.channels;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.sources.Source;

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
