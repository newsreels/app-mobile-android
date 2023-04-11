package com.ziro.bullet.data.models.config;

import com.google.gson.annotations.SerializedName;

public class Ads {
    @SerializedName("enabled")
    private boolean enabled;

    @SerializedName("ad_unit_key")
    private String ad_unit_key;

    @SerializedName("type")
    private String type;

    @SerializedName("interval")
    private int interval;

    @SerializedName("admob")
    private AdData admob;

    @SerializedName("facebook")
    private AdData facebook;

    public AdData getAdmob() {
        return admob;
    }

    public void setAdmob(AdData admob) {
        this.admob = admob;
    }

    public AdData getFacebook() {
        return facebook;
    }

    public void setFacebook(AdData facebook) {
        this.facebook = facebook;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

//    public String getKey() {
//        return ad_unit_key;
//    }

    public void setKey(String key) {
        this.ad_unit_key = key;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }


    public static class AdData {
        @SerializedName("feed")
        private String feed;
        @SerializedName("reel")
        private String reel;

        public AdData(String feed, String reel) {
            this.feed = feed;
            this.reel = reel;
        }

        public String getFeed() {
            return feed;
        }

        public void setFeed(String feed) {
            this.feed = feed;
        }

        public String getReel() {
            return reel;
        }

        public void setReel(String reel) {
            this.reel = reel;
        }
    }
}
