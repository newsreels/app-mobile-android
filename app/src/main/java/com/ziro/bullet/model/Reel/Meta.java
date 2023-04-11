package com.ziro.bullet.model.Reel;

import com.google.gson.annotations.SerializedName;

public class Meta{

	@SerializedName("next")
	private String next;

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}
}