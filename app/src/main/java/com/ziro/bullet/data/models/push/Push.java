
package com.ziro.bullet.data.models.push;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Push {

    @SerializedName("breaking")
    private boolean mBreaking;

    @SerializedName("personalized")
    private boolean mPersonalized;

    @SerializedName("frequency")
    private String frequency;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    public boolean getBreaking() {
        return mBreaking;
    }

    public void setBreaking(boolean breaking) {
        mBreaking = breaking;
    }

    public boolean getPersonalized() {
        return mPersonalized;
    }

    public void setPersonalized(boolean personalized) {
        mPersonalized = personalized;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
