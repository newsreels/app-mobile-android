package com.ziro.bullet.data.models.config;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.search.Registration;

public class HomePreference {
    @SerializedName("view_mode")
    private String viewMode;

    @SerializedName("auto_scroll")
    private boolean auto_scroll;

    @SerializedName("videos_autoplay")
    private boolean videos_autoplay;

    @SerializedName("reels_autoplay")
    private boolean reels_autoplay;

    @SerializedName("tutorial_done")
    private boolean tutorial_done;

    @SerializedName("bullets_autoplay")
    private boolean data_saver;

    @SerializedName("reader_mode")
    private boolean reader_mode;

    @SerializedName("narration")
    private Narration narration;

    public boolean isVideos_autoplay() {
        return videos_autoplay;
    }

    public void setVideos_autoplay(boolean videos_autoplay) {
        this.videos_autoplay = videos_autoplay;
    }

    public boolean isReels_autoplay() {
        return reels_autoplay;
    }

    public void setReels_autoplay(boolean reels_autoplay) {
        this.reels_autoplay = reels_autoplay;
    }

    public boolean isData_saver() {
        return data_saver;
    }

    public void setData_saver(boolean data_saver) {
        this.data_saver = data_saver;
    }

    public String getViewMode() {
        return viewMode;
    }

    public void setViewMode(String viewMode) {
        this.viewMode = viewMode;
    }

    public boolean isAuto_scroll() {
        return auto_scroll;
    }

    public void setAuto_scroll(boolean auto_scroll) {
        this.auto_scroll = auto_scroll;
    }

    public Narration getNarration() {
        return narration;
    }

    public void setNarration(Narration narration) {
        this.narration = narration;
    }

    public boolean isTutorial_done() {
        return tutorial_done;
    }

    public void setTutorial_done(boolean tutorial_done) {
        this.tutorial_done = tutorial_done;
    }

    public boolean isDataSaver() {
        return data_saver;
    }

    public void setDataSaver(boolean data_saver) {
        this.data_saver = data_saver;
    }

    public boolean isReaderMode() {
        return reader_mode;
    }

    public void setReaderMode(boolean reader_mode) {
        this.reader_mode = reader_mode;
    }
}
