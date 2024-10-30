package com.newsreels.app.interfaces;

import com.newsreels.app.model.Tabs.DataItem;

public interface MenuInterface {
    void selectTab(DataItem item);
    void selectOption(String option);
}
