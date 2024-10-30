package com.newsreels.app.data.models;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.model.articles.Author;

import java.util.ArrayList;

public class AuthorListResponse {

    @SerializedName("authors")
    private ArrayList<Author> authors;

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }
}
