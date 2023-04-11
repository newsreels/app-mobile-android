package com.ziro.bullet.mediapicker.engine;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class ImageCacheUtils {
    public static File getCacheFileTo4x(Context context, String url) {
        try {
            return Glide.with(context).downloadOnly().load(url).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getCacheFileTo3x(Context context, String url) {
        try {
            return Glide.with(context).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
