package com.newsreels.app.mediapicker.engine;


import com.newsreels.app.mediapicker.entity.LocalMedia;
import com.newsreels.app.mediapicker.listener.OnResultCallbackListener;

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
