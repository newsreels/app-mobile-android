package com.ziro.bullet.data.caption;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CaptionResponse {

    @SerializedName("captions")
    private ArrayList<CaptionDetails> captions;

    public ArrayList<CaptionDetails> getCaptions() {
        return captions;
    }

    public void setCaptions(ArrayList<CaptionDetails> captions) {
        this.captions = captions;
    }
}
