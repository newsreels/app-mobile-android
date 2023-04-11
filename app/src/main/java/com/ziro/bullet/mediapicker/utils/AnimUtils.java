package com.ziro.bullet.mediapicker.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class AnimUtils {
    private final static int DURATION = 450;

    public static void zoom(View view, boolean isZoomAnim) {
        if (isZoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.12f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.12f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }

    public static void disZoom(View view, boolean isZoomAnim) {
        if (isZoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1.12f, 1f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1.12f, 1f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }

    /**
     * 箭头旋转动画
     *
     * @param arrow
     * @param flag
     */
    public static void rotateArrow(ImageView arrow, boolean flag) {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = flag ? 180f : 180f;
        float toDegrees = flag ? 360f : 360f;
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees,
                pivotX, pivotY);
        animation.setDuration(350);
        arrow.startAnimation(animation);
    }
}
