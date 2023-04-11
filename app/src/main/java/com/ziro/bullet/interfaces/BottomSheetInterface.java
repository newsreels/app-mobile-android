package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.Tabs.DataItem;

import java.util.ArrayList;

public interface BottomSheetInterface {
    void onForYouSelect();

    void onFollowingSelect();

    void onHomeTab(DataItem item);

    void onSearch();

    void onSequenceUpdated(ArrayList<DataItem> mCategoriesList);

    void updateTabs(ArrayList<DataItem> mCategoriesList);

    void dialogDismissListener();
}
