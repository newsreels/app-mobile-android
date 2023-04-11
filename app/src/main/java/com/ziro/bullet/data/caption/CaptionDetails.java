package com.ziro.bullet.data.caption;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CaptionDetails {

    @SerializedName("sentence")
    private String sentence;

    @SerializedName("forced")
    private boolean forced;

    @SerializedName("landscape")
    private boolean landscape;

    @SerializedName("corner_radius")
    private int corner_radius;

    @SerializedName("y_direction")
    private String yDirection;

    @SerializedName("padding")
    private Padding padding;

    @SerializedName("margin")
    private Margin margin;

    @SerializedName("rotation")
    private int rotation;

    @SerializedName("words")
    private List<WordsItem> words;

    @SerializedName("wrapping")
    private boolean wrapping;

    @SerializedName("text_background")
    private String textBackground;

    @SerializedName("image_background")
    private String imageBackground;

    @SerializedName("marquee")
    private boolean marquee;

    @SerializedName("animation")
    private String animation;

    @SerializedName("animation_duration")
    private int animation_duration;

    @SerializedName("duration")
    private Duration duration;

    @SerializedName("is_clickable")
    private boolean isClickable;

    @SerializedName("action")
    private String action;

    @SerializedName("action_data")
    private ActionData action_data;

    @SerializedName("position")
    private Position position;

    @SerializedName("alignment")
    private String alignment;

    private boolean shown;
    private int id;

    public ActionData getAction_data() {
        return action_data;
    }

    public void setAction_data(ActionData action_data) {
        this.action_data = action_data;
    }

    public boolean isLandscape() {
        return landscape;
    }

    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
    }

    public int getCorner_radius() {
        return corner_radius;
    }

    public void setCorner_radius(int corner_radius) {
        this.corner_radius = corner_radius;
    }

    public boolean isMarquee() {
        return marquee;
    }

    public void setMarquee(boolean marquee) {
        this.marquee = marquee;
    }

    public String getImageBackground() {
        return imageBackground;
    }

    public void setImageBackground(String imageBackground) {
        this.imageBackground = imageBackground;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public int getAnimation_duration() {
        return animation_duration;
    }

    public void setAnimation_duration(int animation_duration) {
        this.animation_duration = animation_duration;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public void setYDirection(String yDirection) {
        this.yDirection = yDirection;
    }

    public String getYDirection() {
        return yDirection;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    public Padding getPadding() {
        return padding;
    }

    public void setMargin(Margin margin) {
        this.margin = margin;
    }

    public Margin getMargin() {
        return margin;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }

    public void setWords(List<WordsItem> words) {
        this.words = words;
    }

    public List<WordsItem> getWords() {
        return words;
    }

    public void setWrapping(boolean wrapping) {
        this.wrapping = wrapping;
    }

    public boolean isWrapping() {
        return wrapping;
    }

    public void setTextBackground(String textBackground) {
        this.textBackground = textBackground;
    }

    public String getTextBackground() {
        return textBackground;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getAnimation() {
        return animation;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setIsClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    public boolean isIsClickable() {
        return isClickable;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getAlignment() {
        return alignment;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}