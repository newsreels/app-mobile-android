package com.ziro.bullet.model.NewDiscoverPage;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.model.articles.MediaMeta;

public class Video{

	@SerializedName("media_meta")
	private MediaMeta mediaMeta;

	@SerializedName("image")
	private String image;

	@SerializedName("publish_time")
	private String publishTime;

	@SerializedName("topics")
	private List<Object> topics;

	@SerializedName("link")
	private String link;

	@SerializedName("mute")
	private boolean mute;

	@SerializedName("id")
	private String id;

	@SerializedName("source")
	private Source source;

	@SerializedName("title")
	private String title;

	@SerializedName("type")
	private String type;

	@SerializedName("bullets")
	private List<BulletsItem> bullets;

	@SerializedName("info")
	private Info info;

	public MediaMeta getMediaMeta() {
		return mediaMeta;
	}

	public void setMediaMeta(MediaMeta mediaMeta) {
		this.mediaMeta = mediaMeta;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setPublishTime(String publishTime){
		this.publishTime = publishTime;
	}

	public String getPublishTime(){
		return publishTime;
	}

	public void setTopics(List<Object> topics){
		this.topics = topics;
	}

	public List<Object> getTopics(){
		return topics;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setMute(boolean mute){
		this.mute = mute;
	}

	public boolean isMute(){
		return mute;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setSource(Source source){
		this.source = source;
	}

	public Source getSource(){
		return source;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setBullets(List<BulletsItem> bullets){
		this.bullets = bullets;
	}

	public List<BulletsItem> getBullets(){
		return bullets;
	}

	public void setInfo(Info info){
		this.info = info;
	}

	public Info getInfo(){
		return info;
	}
}