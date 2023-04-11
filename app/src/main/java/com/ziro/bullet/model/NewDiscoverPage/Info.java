package com.ziro.bullet.model.NewDiscoverPage;

import com.google.gson.annotations.SerializedName;

public class Info{

	@SerializedName("comment_count")
	private int commentCount;

	@SerializedName("like_count")
	private int likeCount;

	@SerializedName("view_count")
	private String viewCount;

	@SerializedName("is_liked")
	private boolean isLiked;

	public void setCommentCount(int commentCount){
		this.commentCount = commentCount;
	}

	public int getCommentCount(){
		return commentCount;
	}

	public void setLikeCount(int likeCount){
		this.likeCount = likeCount;
	}

	public int getLikeCount(){
		return likeCount;
	}

	public void setViewCount(String viewCount){
		this.viewCount = viewCount;
	}

	public String getViewCount(){
		return viewCount;
	}

	public void setIsLiked(boolean isLiked){
		this.isLiked = isLiked;
	}

	public boolean isIsLiked(){
		return isLiked;
	}
}