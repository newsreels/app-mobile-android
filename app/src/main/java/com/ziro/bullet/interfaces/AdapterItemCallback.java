package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.searchhistory.History;

public interface AdapterItemCallback {
    void onItemClick(int position, History item);

    void onItemClick(int position);

  void onItemCancelClick(int position,History item);
}