package com.ziro.bullet.data.caption;

import com.google.gson.annotations.SerializedName;

public class WordsItem {

    @SerializedName("animation_repeat")
    private int animationRepeat;

    @SerializedName("delay")
    private int delay;

    @SerializedName("animation_duration")
    private int animationDuration;

    @SerializedName("shadow")
    private Shadow shadow;

    @SerializedName("underline")
    private boolean underline;

    @SerializedName("highlight_color")
    private String highlightColor;

    @SerializedName("word")
    private String word;

    @SerializedName("font")
    private Font font;

    public void setAnimationRepeat(int animationRepeat) {
        this.animationRepeat = animationRepeat;
    }

    public int getAnimationRepeat() {
        return animationRepeat;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }

    public String getHighlightColor() {
        return highlightColor;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public Shadow getShadow() {
        return shadow;
    }

    public void setShadow(Shadow shadow) {
        this.shadow = shadow;
    }
}