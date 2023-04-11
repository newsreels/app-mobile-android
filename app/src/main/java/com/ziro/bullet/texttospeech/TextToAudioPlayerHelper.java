package com.ziro.bullet.texttospeech;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.utills.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TextToAudioPlayerHelper {

    ///////////////////////////////////////////////////////
    private final static String TAG = "AudioFocus";
    ///////////////////////////////////////////////////////
    boolean isPlaying = false;
    AudioCallback audioCallback;
    AudioObject audio;
    AudioManager.OnAudioFocusChangeListener afChangeListener;
    private Context context;
    private MediaPlayer mp;
    private AudioManager audioManager;
    private int lenght = -1;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = focusChange -> {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.e(TAG, "AUDIOFOCUS_GAIN");
                //restart/resume your sound
                unmute();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.e(TAG, "AUDIOFOCUS_LOSS");
                //Loss of audio focus for a long time
                //Stop playing the sound
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                //Loss of audio focus for a short time
                //Pause playing the sound
                mute();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                //Loss of audio focus for a short time.
                //But one can duck. Lower the volume of playing the sound
                break;
            default:
        }
    };
    ///////////////////////////////////////////////////////

    public TextToAudioPlayerHelper(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        mp = new MediaPlayer();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }


    private boolean requestAudioFocusForMyApp(final Context context) {
        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d(TAG, "Audio focus received");
            return true;
        } else {
            Log.d(TAG, "Audio focus NOT received");
            return false;
        }
    }

    void releaseAudioFocusForMyApp(final Context context) {
        audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }

    // MEDIA PLAYER //
    public void isPlayingMediaPlayer(AudioObject audio, AudioCallback audioCallback) {
        Log.d(TAG, "isPlayingMediaPlayer: ");
        this.audioCallback = audioCallback;
        init();
        if (audio != null && mp != null) {
            try {
                Log.e("isAudioLoaded", "==============================");
                Log.e("isAudioLoaded", "AUDIO : " + audio.getUrl());
                mp.setDataSource(audio.getUrl());
                mp.prepareAsync();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(Constants.READING_SPEED_RATES.get(Constants.reading_speed)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.e("isAudioLoaded", " prepareAsync called");
                Log.e("isAudioLoaded", " canAudioPlay : " + Constants.canAudioPlay);
                mp.setOnErrorListener((mp, what, extra) -> {
                    Log.d("isAudioLoaded", "onError() called with: mp = [" + mp + "], what = [" + what + "], extra = [" + extra + "]");
                    // in case of no audio file
                    isPlaying = false;
                    if (audioCallback != null)
                        audioCallback.isAudioLoaded(true);
                    return false;
                });

                mp.setOnPreparedListener(mp -> {
                    Log.e("isAudioLoaded", "text : " + audio.getText());
                    Log.e("isAudioLoaded", "canAudioPlay : " + Constants.canAudioPlay);
                    if (mp != null) {
                        if (Constants.canAudioPlay) {
                            TextToAudioPlayerHelper.this.playMediaPlayer();
                            isPlaying = true;
                            if (audioCallback != null)
                                audioCallback.isAudioLoaded(true);

                        } else {
                            isPlaying = false;
                            if (audioCallback != null)
                                audioCallback.isAudioLoaded(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("isAudioLoaded", "-w-> " + e.getMessage());
                // in case of no audio file
                isPlaying = false;
                if (audioCallback != null)
                    audioCallback.isAudioLoaded(true);
            }
        }
    }

    public void playMediaPlayer() {
        if (mp != null) {
            boolean gotFocus = requestAudioFocusForMyApp(context);
            if (gotFocus) {
                //play audio.
                mp.start();
            }
        }
    }

    public void pauseMediaPlayer() {
        if (mp != null) {
            mp.pause();
            lenght = mp.getCurrentPosition();
        }
    }

    public void resumeMediaPlayer() {
        if (mp != null) {
            if (lenght > -1) {
                mp.seekTo(lenght);
            }
            boolean gotFocus = requestAudioFocusForMyApp(context);
            if (gotFocus) {
                mp.start();
            }
        }
    }

    public void stopMediaPlayer() {
        if (mp != null) {
            mp.stop();
        }
    }

    public void destroyMediaPlayer() {
        if (mp != null) {
            releaseAudioFocusForMyApp(context);
            mp.release();
            mp = null;
        }
    }

    ///////////////////////////////////////////////////////

    // SOUND POOL //
    public void isPlaying(AudioObject audio, AudioCallback audioCallback) {
        this.audioCallback = audioCallback;
        this.audio = audio;
        isPlayingMediaPlayer(audio, audioCallback);
    }

    public boolean isSpeaking() {
        if (mp != null) {
            return mp.isPlaying();
        } else {
            return false;
        }
    }

    public void mute() {
        if (!isPlaying) {
            //If audio is loading and user press mute, then for start next bullet need to call this
            if (audioCallback != null)
                audioCallback.isAudioLoaded(true);
        }
        if (mp != null) {
            mp.setVolume(0, 0);
        }
    }

    public void unmute() {
        if (mp != null) {
            mp.setVolume(0, 1);
        }
    }

    public void stop() {
        stopMediaPlayer();
        isPlaying = false;
    }

    public void pause() {
        pauseMediaPlayer();
        isPlaying = false;
    }

    public void resume() {
        isPlaying = true;
        resumeMediaPlayer();
    }

    public void destroy() {
        destroyMediaPlayer();
        isPlaying = false;
    }
}
