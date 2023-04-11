package com.ziro.bullet.mediapicker.entity;

import java.util.List;


public class MediaData {

    /**
     * Is there more
     */
    public boolean isHasNextMore;

    /**
     * data
     */
    public List<LocalMedia> data;


    public MediaData() {
        super();
    }

    public MediaData(boolean isHasNextMore, List<LocalMedia> data) {
        super();
        this.isHasNextMore = isHasNextMore;
        this.data = data;
    }
}
