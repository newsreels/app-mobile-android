package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.articles.Article;

public interface CommentClick {
    void commentClick(int position, String id);

    void onDetailClick(int position, Article article);

    void fullscreen(int position, Article article, long duration, String mode, boolean isManual);
}
