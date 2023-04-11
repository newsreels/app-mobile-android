package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.AudioObject;

public interface DetailsActivityInterface {
    void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio);

    void pause();

    void resume();
}
