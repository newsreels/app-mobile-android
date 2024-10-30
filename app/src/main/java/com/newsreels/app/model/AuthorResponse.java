package com.newsreels.app.model;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.model.articles.Author;

public class AuthorResponse {

    @SerializedName("author")
    private Author author;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
