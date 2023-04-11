package com.ziro.bullet.model;

public class FullScreenVideo {

    public String url;
    public String type;
    public long duration;

    public FullScreenVideo(String url, String type, long duration) {
        this.url = url;
        this.type = type;
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
