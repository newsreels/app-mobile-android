package com.ziro.bullet.mediapicker.immersive;

import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: 沉浸式相关
 */

public class ImmersiveManage {


    public static void immersiveAboveAPI23(AppCompatActivity baseActivity, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            immersiveAboveAPI23(baseActivity, false, false, statusBarColor, navigationBarColor, isDarkStatusBarIcon);
        }
    }


    public static void immersiveAboveAPI23(AppCompatActivity baseActivity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar, int statusBarColor, int navigationBarColor, boolean isDarkStatusBarIcon) {
        try {
            Window window = baseActivity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                window.setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isMarginStatusBar && isMarginNavigationBar) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    LightStatusBarUtils.setLightStatusBar(baseActivity, isMarginStatusBar
                            , isMarginNavigationBar
                            , statusBarColor == Color.TRANSPARENT
                            , isDarkStatusBarIcon);

                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else if (!isMarginStatusBar && !isMarginNavigationBar) {
                    window.requestFeature(Window.FEATURE_NO_TITLE);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                    LightStatusBarUtils.setLightStatusBar(baseActivity, isMarginStatusBar
                            , isMarginNavigationBar
                            , statusBarColor == Color.TRANSPARENT
                            , isDarkStatusBarIcon);

                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


                } else if (!isMarginStatusBar && isMarginNavigationBar) {
                    window.requestFeature(Window.FEATURE_NO_TITLE);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    LightStatusBarUtils.setLightStatusBar(baseActivity, isMarginStatusBar
                            , isMarginNavigationBar
                            , statusBarColor == Color.TRANSPARENT
                            , isDarkStatusBarIcon);

                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


                } else {
                    return;
                }

                window.setStatusBarColor(statusBarColor);
                window.setNavigationBarColor(navigationBarColor);

            }
        } catch (Exception e) {
        }
    }
}
