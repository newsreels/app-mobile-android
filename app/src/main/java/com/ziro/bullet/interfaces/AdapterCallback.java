package com.ziro.bullet.interfaces;

import android.content.DialogInterface;

import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.model.articles.Article;

public interface AdapterCallback {
    int getArticlePosition();

    void showShareBottomSheet(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener);

    void onItemClick(int position, boolean setCurrentView);
}