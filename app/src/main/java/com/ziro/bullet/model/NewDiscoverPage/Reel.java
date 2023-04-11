package com.ziro.bullet.model.NewDiscoverPage;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.model.articles.MediaMeta;

public class Reel{

	@SerializedName("media_meta")
	private MediaMeta mediaMeta;

	@SerializedName("publish_time")
	private String publishTime;

	@SerializedName("description")
	private String description;

	@SerializedName("id")
	private String id;

	@SerializedName("media")
	private String media;

	@SerializedName("source")
	private Source source;

	@SerializedName("info")
	private Info info;

	@SerializedName("image")
	private String image;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public MediaMeta getMediaMeta() {
		return mediaMeta;
	}

	public void setMediaMeta(MediaMeta mediaMeta) {
		this.mediaMeta = mediaMeta;
	}

	public void setPublishTime(String publishTime){
		this.publishTime = publishTime;
	}

	public String getPublishTime(){
		return publishTime;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setMedia(String media){
		this.media = media;
	}

	public String getMedia(){
		return media;
	}

	public void setSource(Source source){
		this.source = source;
	}

	public Source getSource(){
		return source;
	}

	public void setInfo(Info info){
		this.info = info;
	}

	public Info getInfo(){
		return info;
	}
}