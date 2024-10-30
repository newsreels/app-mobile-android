package com.newsreels.app.interfaces;

import android.content.DialogInterface;

import com.newsreels.app.data.models.ShareInfo;
import com.newsreels.app.model.articles.Article;

public interface AdapterCallback {
    int getArticlePosition();

    void showShareBottomSheet(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener);

    void onItemClick(int position, boolean setCurrentView);
}