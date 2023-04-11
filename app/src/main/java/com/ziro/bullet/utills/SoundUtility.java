package com.ziro.bullet.utills;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.File;

public class SoundUtility {
    /**
     * @param context
     * @param soundFile sound file object (can be one of: Integer(resource id), String(file path), or File)
     * @return duration of given sound file in millis (0 if failed)
     */
    public static long getDurationOfSound(Context context, Object soundFile) {
        int millis = 0;
        MediaPlayer mp = new MediaPlayer();
        try {
            Class<? extends Object> currentArgClass = soundFile.getClass();
            if (currentArgClass == Integer.class) {
                AssetFileDescriptor afd = context.getResources().openRawResourceFd((Integer) soundFile);
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            } else if (currentArgClass == String.class) {
                mp.setDataSource((String) soundFile);
            } else if (currentArgClass == File.class) {
                mp.setDataSource(((File) soundFile).getAbsolutePath());
            }
            mp.prepare();
            millis = mp.getDuration();
        } catch (Exception e) {
            //  Logger.e(e.toString());
        } finally {
            mp.release();
            mp = null;
        }
        return millis;
    }
}