package com.ziro.bullet.model.language;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LanguageResponse{

    @SerializedName("languages")
    private List<LanguagesItem> languages;

    @SerializedName("mta")
    private Meta meta;

    @SerializedName("meta")
    private String metaString;

    public void setLanguages(List<LanguagesItem> languages){
        this.languages = languages;
    }

    public List<LanguagesItem> getLanguages(){
        return languages;
    }

    public void setMeta(Meta meta){
        this.meta = meta;
    }

    public Meta getMeta(){
        return meta;
    }
}