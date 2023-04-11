package com.ziro.bullet.model.NewDiscoverPage;

import com.google.gson.annotations.SerializedName;

public class BulletsItem{

	@SerializedName("duration")
	private int duration;

	@SerializedName("image")
	private String image;

	@SerializedName("data")
	private String data;

	@SerializedName("audio")
	private String audio;

	public void setDuration(int duration){
		this.duration = duration;
	}

	public int getDuration(){
		return duration;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setData(String data){
		this.data = data;
	}

	public String getData(){
		return data;
	}

	public void setAudio(String audio){
		this.audio = audio;
	}

	public String getAudio(){
		return audio;
	}


	public String getDurationString(){
		int durationSec = duration / 1000;
		int sec = durationSec % 60;
		int min = durationSec/60;
		int hour = min/60;
		min = min % 60;
		if(hour == 0){
			return fillZero(min)+":"+fillZero(sec);
		}else{
			return fillZero(hour)+":"+fillZero(min)+":"+fillZero(sec);
		}
	}

	private String fillZero(int num){
		if(num < 10){
			return "0"+num;
		}
		return String.valueOf(num);
	}
}