package com.ziro.bullet.data.models.suggestions;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SuggestionsModel {
    @SerializedName("result")
    private ArrayList<Suggestions> suggestions;

    public ArrayList<Suggestions> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(ArrayList<Suggestions> suggestions) {
        this.suggestions = suggestions;
    }
}
