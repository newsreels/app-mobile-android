package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.TYPE;

public interface TempCategorySwipeListener {
    void swipe(boolean enable);

    void muteIcon(boolean isShow);

    void onFavoriteChanged(boolean fav);

    void selectTab(String id);

    void selectTabOrDetailsPage(String id, String name, TYPE type, String footerType);
}
