package com.ziro.bullet.model;

public class AudioObject {
    private String id;
    private String text;
    private String url;
    private int index;
    private long duration;

    public AudioObject(){}

    public AudioObject(String id, String text, String url, int index, long duration) {
        this.duration = duration;
        this.id = id;
        this.text = text;
        this.index = index;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
