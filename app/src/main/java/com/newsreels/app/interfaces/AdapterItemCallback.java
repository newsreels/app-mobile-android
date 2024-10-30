package com.newsreels.app.interfaces;

import com.newsreels.app.model.searchhistory.History;

public interface AdapterItemCallback {
    void onItemClick(int position, History item);

    void onItemClick(int position);

  void onItemCancelClick(int position,History item);
}