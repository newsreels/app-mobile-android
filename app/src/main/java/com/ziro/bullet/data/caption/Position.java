package com.ziro.bullet.data.caption;

import com.google.gson.annotations.SerializedName;

public class Position{

	@SerializedName("x")
	private int X;

	@SerializedName("y")
	private int Y;

	public void setX(int X){
		this.X = X;
	}

	public int getX(){
		return X;
	}

	public void setY(int Y){
		this.Y = Y;
	}

	public int getY(){
		return Y;
	}
}