package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.TYPE;

public interface ShareToMainInterface {
    void removeItem(String id,int position);

    void onItemClicked(TYPE type, String id, String name, boolean favorite);

    void unarchived();
}
