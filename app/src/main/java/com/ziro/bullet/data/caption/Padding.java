package com.ziro.bullet.data.caption;

import com.google.gson.annotations.SerializedName;

public class Padding{

	@SerializedName("top")
	private int top;

	@SerializedName("left")
	private int left;

	@SerializedName("bottom")
	private int bottom;

	@SerializedName("right")
	private int right;

	public void setTop(int top){
		this.top = top;
	}

	public int getTop(){
		return top;
	}

	public void setLeft(int left){
		this.left = left;
	}

	public int getLeft(){
		return left;
	}

	public void setBottom(int bottom){
		this.bottom = bottom;
	}

	public int getBottom(){
		return bottom;
	}

	public void setRight(int right){
		this.right = right;
	}

	public int getRight(){
		return right;
	}
}