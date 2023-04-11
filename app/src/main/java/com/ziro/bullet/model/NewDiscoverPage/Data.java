package com.ziro.bullet.model.NewDiscoverPage;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.articles.SingleTopic;
import com.ziro.bullet.model.Reel.ReelsItem;

import java.util.ArrayList;
import java.util.List;

public class Data{

	@SerializedName("article")
//	private Article article;
	private com.ziro.bullet.model.articles.Article article;

	@SerializedName("large")
	private boolean large;

	@SerializedName("top")
	private boolean top;

	@SerializedName("image")
	private String image;

	@SerializedName("icons")
	private List<Icon> icons = null;

	@SerializedName("context")
	private String context;

	@SerializedName("articles")
	private ArrayList<com.ziro.bullet.model.articles.Article> articles;

	@SerializedName("reel")
	private Reel reel;

	@SerializedName("video")
	private com.ziro.bullet.model.articles.Article video;

	@SerializedName("reels")
	private ArrayList<ReelsItem> reels;

	@SerializedName("sources")
	private ArrayList<Source> sources;

	@SerializedName("locations")
	private ArrayList<Location> locations;

	@SerializedName("topics")
	private ArrayList<Topics> topics;

	@SerializedName("topic")
	private SingleTopic topic;

	@SerializedName("authors")
	private ArrayList<Author> authors;

	public ArrayList<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(ArrayList<Author> authors) {
		this.authors = authors;
	}

	public ArrayList<ReelsItem> getReels() {
		return reels;
	}

	public void setReels(ArrayList<ReelsItem> reels) {
		this.reels = reels;
	}

	public ArrayList<Source> getSources() {
		return sources;
	}

	public void setSources(ArrayList<Source> sources) {
		this.sources = sources;
	}

	public ArrayList<Location> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<Location> locations) {
		this.locations = locations;
	}

	public ArrayList<Topics> getTopics() {
		return topics;
	}

	public void setTopics(ArrayList<Topics> topics) {
		this.topics = topics;
	}

	public void setArticle(com.ziro.bullet.model.articles.Article article){
		this.article = article;
	}

	public com.ziro.bullet.model.articles.Article getArticle(){
		return article;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setContext(String context){
		this.context = context;
	}

	public String getContext(){
		return context;
	}

	public void setArticles(ArrayList<com.ziro.bullet.model.articles.Article> articles){
		this.articles = articles;
	}

	public ArrayList<com.ziro.bullet.model.articles.Article> getArticles(){
		return articles;
	}

	public void setReel(Reel reel){
		this.reel = reel;
	}

	public Reel getReel(){
		return reel;
	}

	public void setVideo(com.ziro.bullet.model.articles.Article video){
		this.video = video;
	}

	public com.ziro.bullet.model.articles.Article getVideo(){
		return article;
	}

	public List<Icon> getIcons() {
		return icons;
	}

	public void setIcons(List<Icon> icons) {
		this.icons = icons;
	}

	public boolean isLarge() {
		return large;
	}

	public void setLarge(boolean large) {
		this.large = large;
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public SingleTopic getTopic() {
		return topic;
	}

	public void setTopic(SingleTopic topic) {
		this.topic = topic;
	}
}