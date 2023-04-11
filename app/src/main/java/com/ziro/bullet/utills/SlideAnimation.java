package com.ziro.bullet.utills;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SlideAnimation extends Animation {

    int mFromWidth;
    int mToWidth;
    View mView;

    public SlideAnimation(View view, int mFromWidth, int mToWidth) {
        this.mView = view;
        this.mFromWidth = mFromWidth;
        this.mToWidth = mToWidth;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        int newHeight;

        if (mView.getHeight() != mToWidth) {
            newHeight = (int) (mFromWidth + ((mToWidth - mFromWidth) * interpolatedTime));
            mView.getLayoutParams().width = newHeight;
            mView.requestLayout();
        }
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}