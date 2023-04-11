package com.ziro.bullet.interfaces;

import android.view.View;

public interface ViewItemClickListener {

    int TYPE_RELEVANT_TOPIC_SEE_ALL = 1;
    int TYPE_RELEVANT_CHANNEL_SEE_ALL = 2;
    int TYPE_RELEVANT_PLACE_SEE_ALL = 3;
    int TYPE_RELEVANT_AUTHOR_SEE_ALL = 4;
    int TYPE_RELEVANT_AUTHOR_ITEM = 5;

    void itemClickedData(View view, int type, Object data);
}