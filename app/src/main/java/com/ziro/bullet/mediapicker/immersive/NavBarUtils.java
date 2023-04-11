package com.ziro.bullet.mediapicker.immersive;

import android.app.Activity;
import android.os.Build;
import android.view.Window;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class NavBarUtils {

    public static void setNavBarColor(@NonNull final Activity activity, @ColorInt final int color) {
        setNavBarColor(activity.getWindow(), color);
    }

    public static void setNavBarColor(@NonNull final Window window, @ColorInt final int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(color);
        }
    }
}
