package com.ziro.bullet.utills;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FitYCropXImageView extends ImageView {
    private final RectF drawableRect = new RectF(0, 0, 0, 0);
    private final RectF viewRect = new RectF(0, 0, 0, 0);
    private final Matrix m = new Matrix();
    boolean done = false;

    @SuppressWarnings("UnusedDeclaration")
    public FitYCropXImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }
    @SuppressWarnings("UnusedDeclaration")
    public FitYCropXImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
    }
    @SuppressWarnings("UnusedDeclaration")
    public FitYCropXImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (done) {
            return;//Already fixed drawable scale
        }
        final Drawable d = getDrawable();
        if (d == null) {
            return;//No drawable to correct for
        }
        int viewHeight = getMeasuredHeight();
        int viewWidth = getMeasuredWidth();
        int drawableWidth = d.getIntrinsicWidth();
        int drawableHeight = d.getIntrinsicHeight();
        drawableRect.set(0, 0, drawableWidth, drawableHeight);//Represents the original image
        //Compute the left and right bounds for the scaled image
        float viewHalfWidth = viewWidth / 2;
        float scale = (float) viewHeight / (float) drawableHeight;
        float scaledWidth = drawableWidth * scale;
        float scaledHalfWidth = scaledWidth / 2;
        viewRect.set(viewHalfWidth - scaledHalfWidth, 0, viewHalfWidth + scaledHalfWidth, viewHeight);

        m.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER /* This constant doesn't matter? */);
        setImageMatrix(m);

        done = true;

        requestLayout();
    }
}