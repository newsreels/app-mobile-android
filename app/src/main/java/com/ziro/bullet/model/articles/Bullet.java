
package com.ziro.bullet.model.articles;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


@SuppressWarnings("unused")
public class Bullet implements Parcelable {

    @SerializedName("audio")
    private String audio;

    @SerializedName("duration")
    private int duration;

    @SerializedName("number")
    private int number;

    @SerializedName("data")
    private String mData;

    @SerializedName("image")
    private String image;

    private String hint;
    private int bulletTextViewHeight;
    private boolean isYoutube;
    private boolean isSelected;

    public Bullet() {}

    public Bullet(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public Bullet(String mData, String image, String hint) {
        this.mData = mData;
        this.image = image;
        this.hint = hint;
    }

    public int getBulletTextViewHeight() {
        return bulletTextViewHeight;
    }

    public void setBulletTextViewHeight(int bulletTextViewHeight) {
        this.bulletTextViewHeight = bulletTextViewHeight;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isYoutube() {
        return isYoutube;
    }

    public void setYoutube(boolean youtube) {
        isYoutube = youtube;
    }

    private ArrayList<Integer> audioList = new ArrayList<>();

    public ArrayList<Integer> getAudioList() {
        return audioList;
    }

    public void setAudioList(ArrayList<Integer> audioList) {
        this.audioList = audioList;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getData() {
        return mData;
    }

    public void setData(String mData) {
        this.mData = mData;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getmData() {
        return mData;
    }

    public void setmData(String mData) {
        this.mData = mData;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.audio);
        dest.writeInt(this.duration);
        dest.writeInt(this.number);
        dest.writeString(this.mData);
        dest.writeString(this.image);
        dest.writeString(this.hint);
        dest.writeInt(this.bulletTextViewHeight);
        dest.writeByte(this.isYoutube ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeList(this.audioList);
    }

    public void readFromParcel(Parcel source) {
        this.audio = source.readString();
        this.duration = source.readInt();
        this.number = source.readInt();
        this.mData = source.readString();
        this.image = source.readString();
        this.hint = source.readString();
        this.bulletTextViewHeight = source.readInt();
        this.isYoutube = source.readByte() != 0;
        this.isSelected = source.readByte() != 0;
        this.audioList = new ArrayList<Integer>();
        source.readList(this.audioList, Integer.class.getClassLoader());
    }

    protected Bullet(Parcel in) {
        this.audio = in.readString();
        this.duration = in.readInt();
        this.number = in.readInt();
        this.mData = in.readString();
        this.image = in.readString();
        this.hint = in.readString();
        this.bulletTextViewHeight = in.readInt();
        this.isYoutube = in.readByte() != 0;
        this.isSelected = in.readByte() != 0;
        this.audioList = new ArrayList<Integer>();
        in.readList(this.audioList, Integer.class.getClassLoader());
    }

    public static final Creator<Bullet> CREATOR = new Creator<Bullet>() {
        @Override
        public Bullet createFromParcel(Parcel source) {
            return new Bullet(source);
        }

        @Override
        public Bullet[] newArray(int size) {
            return new Bullet[size];
        }
    };
}
