package com.ziro.bullet.utills;

public class MessageEvent {
    public static final int TYPE_UPDATE_ARTICLE = 3;
    public static final int TYPE_COUNT_API_CALL = 1;
//    public static final int TYPE_SHOW_DETAIL_VIEW = 2;
    public static final int TYPE_PROFILE_CHANGED = 4;
    public static final int TYPE_CHANNEL_PROFILE_CHANGED = 5;
    public static final int TYPE_USER_LOGGED_IN = 6;
    public static final int TYPE_MUTE_REELS_SCROLLING = 7;
    public static final int TYPE_VIDEO_PLAYING = 7;

    private int intData;
    private String stringData;
    private Object objectData;
    private int type;

    public int getIntData() {
        return intData;
    }

    public void setIntData(int intData) {
        this.intData = intData;
    }

    public String getStringData() {
        return stringData;
    }

    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    public Object getObjectData() {
        return objectData;
    }

    public void setObjectData(Object objectData) {
        this.objectData = objectData;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
