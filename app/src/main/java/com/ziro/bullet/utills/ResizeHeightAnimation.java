package com.ziro.bullet.utills;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeHeightAnimation extends Animation {
    private int mHeight;
    private int mStartHeight;
    private View mView;

    public ResizeHeightAnimation(View view, int height) {
        mView = view;
        mHeight = height;
        mStartHeight = view.getHeight();
    }
    
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        mView.getLayoutParams().height = mStartHeight + (int) ((mHeight - mStartHeight) * interpolatedTime);
        mView.requestLayout();
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