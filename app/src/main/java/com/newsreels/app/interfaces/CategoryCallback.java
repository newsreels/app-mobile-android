package com.newsreels.app.interfaces;

import com.newsreels.app.model.Tabs.DataItem;

public interface CategoryCallback {
    void onTabClick(DataItem tab);

    void onItemFollowClick(DataItem dataItem, int position);
}
