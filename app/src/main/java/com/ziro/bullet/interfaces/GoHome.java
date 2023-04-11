package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.AudioObject;

public interface GoHome {
    void home();

    void sendAudioToTempHome(AudioCallback audioCallback, String fragTag, String status, AudioObject audio);

    void scrollUp();

    void scrollDown();

    void sendAudioEvent(String event);
}
