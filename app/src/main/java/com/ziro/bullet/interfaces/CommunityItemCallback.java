package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.articles.Article;

public interface CommunityItemCallback {
    void onItemClick(String option, Article article);
}