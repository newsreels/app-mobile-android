package com.newsreels.app.interfaces;

import com.newsreels.app.model.AudioObject;

public interface DetailsActivityInterface {
    void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio);

    void pause();

    void resume();
}
