package com.newsreels.app.interfaces;

import com.newsreels.app.model.articles.Article;

public interface CommunityItemCallback {
    void onItemClick(String option, Article article);
}