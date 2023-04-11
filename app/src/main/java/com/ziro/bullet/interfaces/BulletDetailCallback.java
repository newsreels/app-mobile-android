package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.TYPE;

public interface BulletDetailCallback {
    void onChannelItemClicked(TYPE type, String id, String name, boolean favorite);
}
