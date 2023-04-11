package com.ziro.bullet.mediapicker.engine;

import android.content.Context;

public interface CacheResourcesEngine {
    /**
     * Get the cache path
     *
     * @param context
     * @param url
     */
    String onCachePath(Context context, String url);
}
