package com.newsreels.app.data.caption;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.sources.Source;
import com.newsreels.app.data.models.topics.Topics;
import com.newsreels.app.model.articles.Author;

public class ActionData {

    @SerializedName("topic")
    private Topics topics;

    @SerializedName("source")
    private Source source;

    @SerializedName("author")
    private Author author;


    public Topics getTopics() {
        return topics;
    }

    public void setTopics(Topics topics) {
        this.topics = topics;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
