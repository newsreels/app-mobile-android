package com.newsreels.app.interfaces;

import com.newsreels.app.model.AudioObject;

public interface GoHome {
    void home();

    void sendAudioToTempHome(AudioCallback audioCallback, String fragTag, String status, AudioObject audio);

    void scrollUp();

    void scrollDown();

    void sendAudioEvent(String event);
}
