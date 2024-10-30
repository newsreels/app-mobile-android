package com.newsreels.app.interfaces;

import com.newsreels.app.data.TYPE;

public interface BulletDetailCallback {
    void onChannelItemClicked(TYPE type, String id, String name, boolean favorite);
}
