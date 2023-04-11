package com.ziro.bullet.model.articles;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MediaMeta implements Parcelable {
    @SerializedName("duration")
    private double duration;
    @SerializedName("height")
    private double height;
    @SerializedName("width")
    private double width;
    @SerializedName("type")
    private String type;

    public double getDuration() {
        return duration;
    }

    public String getDurationString(){
        int durationSec = (int) (duration / 1000);
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

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public MediaMeta() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.duration);
        dest.writeDouble(this.height);
        dest.writeDouble(this.width);
        dest.writeString(this.type);
    }

    public void readFromParcel(Parcel source) {
        this.duration = source.readDouble();
        this.height = source.readDouble();
        this.width = source.readDouble();
        this.type = source.readString();
    }

    protected MediaMeta(Parcel in) {
        this.duration = in.readDouble();
        this.height = in.readDouble();
        this.width = in.readDouble();
        this.type = in.readString();
    }

    public static final Creator<MediaMeta> CREATOR = new Creator<MediaMeta>() {
        @Override
        public MediaMeta createFromParcel(Parcel source) {
            return new MediaMeta(source);
        }

        @Override
        public MediaMeta[] newArray(int size) {
            return new MediaMeta[size];
        }
    };
}
