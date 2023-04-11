package com.ziro.bullet.data;

import com.ziro.bullet.model.articles.Bullet;

import java.util.ArrayList;

public class PostArticleParams {

    private ArrayList<Bullet> bullets;
    private String title;
    private String link;
    private String image;
    private String category_id;
    private String source;
    private String id;
    private String video;
    private String youtube_id;
    private String headline;
    private String description;
    private String media;
    private String type;
    private String scheduled_at;
    private String status;

    public PostArticleParams(){}

    public PostArticleParams(ArrayList<Bullet> bullets, String title, String link, String image, String category_id, String source, String id, String type) {
        //IMAGE ARTICLE
        this.bullets = bullets;
        this.title = title;
        this.link = link;
        this.image = image;
        this.category_id = category_id;
        this.source = source;
        this.id = id;
        this.type = type;
    }

    public PostArticleParams(String title, String category_id, String source, String id, String video, String type) {
        //VIDEO ARTICLE
        this.title = title;
        this.category_id = category_id;
        this.source = source;
        this.id = id;
        this.video = video;
        this.type = type;
    }

    public PostArticleParams(String source, String id, String youtube_id, String headline, String type) {
        //YOUTUBE ARTICLE
        this.source = source;
        this.id = id;
        this.youtube_id = youtube_id;
        this.headline = headline;
        this.type = type;
    }

    public PostArticleParams(String id, String description, String media, String type) {
        //FOR REELS
        this.type = type;
        this.id = id;
        this.description = description;
        this.media = media;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(ArrayList<Bullet> bullets) {
        this.bullets = bullets;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getSchedule() {
        return scheduled_at;
    }

    public void setSchedule(String schedule) {
        this.scheduled_at = schedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
