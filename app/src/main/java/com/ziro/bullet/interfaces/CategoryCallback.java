package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.Tabs.DataItem;

public interface CategoryCallback {
    void onTabClick(DataItem tab);

    void onItemFollowClick(DataItem dataItem, int position);
}
