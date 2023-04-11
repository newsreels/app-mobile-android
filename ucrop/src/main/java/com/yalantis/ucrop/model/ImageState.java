package com.yalantis.ucrop.model;

import android.graphics.RectF;

/**
 * Created by Oleksii Shliama [https://github.com/shliama] on 6/21/16.
 */
public class ImageState {

    private RectF mCropRect;
    private RectF mCurrentImageRect;

    private float mCurrentScale, mCurrentAngle;

    private float mFlipVertical, mFlipHorizontal;

    public ImageState(RectF cropRect, RectF currentImageRect, float currentScale, float currentAngle) {
        mCropRect = cropRect;
        mCurrentImageRect = currentImageRect;
        mCurrentScale = currentScale;
        mCurrentAngle = currentAngle;
        mFlipVertical = 1;
        mFlipHorizontal = 1;
    }

    public ImageState(RectF cropRect, RectF currentImageRect,
                      float currentScale, float currentAngle,
                      float flipVertical, float flipHorizontal
    ) {
        mCropRect = cropRect;
        mCurrentImageRect = currentImageRect;
        mCurrentScale = currentScale;
        mCurrentAngle = currentAngle;
        mFlipVertical = flipVertical;
        mFlipHorizontal = flipHorizontal;
    }

    public RectF getCropRect() {
        return mCropRect;
    }

    public RectF getCurrentImageRect() {
        return mCurrentImageRect;
    }

    public float getCurrentScale() {
        return mCurrentScale;
    }

    public float getCurrentAngle() {
        return mCurrentAngle;
    }

    public float getmFlipVertical() {
        return mFlipVertical;
    }

    public float getmFlipHorizontal() {
        return mFlipHorizontal;
    }
}
