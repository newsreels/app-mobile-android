package com.ziro.bullet.mediapicker.engine;


import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.listener.OnResultCallbackListener;

public interface PictureSelectorEngine {

    /**
     * Create ImageLoad Engine
     *
     * @return
     */
    ImageEngine createEngine();

    /**
     * Create Result Listener
     *
     * @return
     */
    OnResultCallbackListener<LocalMedia> getResultCallbackListener();
}
