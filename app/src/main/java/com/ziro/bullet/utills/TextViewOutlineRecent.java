package com.ziro.bullet.utills;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.ziro.bullet.R;

public class TextViewOutlineRecent extends AppCompatTextView {

    // constants
    private static final int DEFAULT_OUTLINE_SIZE = 0;
    private static final int DEFAULT_OUTLINE_COLOR = Color.TRANSPARENT;

    // data
    private int mOutlineSize;
    private int mOutlineColor;
    private int mTextColor;
    private float mShadowRadius;
    private float mShadowDx;
    private float mShadowDy;
    private int mShadowColor;

    public TextViewOutlineRecent(Context context) {
        this(context, null);
    }

    public TextViewOutlineRecent(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attrs) {
        mOutlineSize = DEFAULT_OUTLINE_SIZE;
        mOutlineColor = DEFAULT_OUTLINE_COLOR;
        mTextColor = getCurrentTextColor();
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TextViewOutlineRecent);
            if (a.hasValue(R.styleable.TextViewOutlineRecent_outlineSize)) {
                mOutlineSize = (int) a.getDimension(R.styleable.TextViewOutlineRecent_outlineSize, DEFAULT_OUTLINE_SIZE);
            }
            if (a.hasValue(R.styleable.TextViewOutlineRecent_outlineColor)) {
                mOutlineColor = a.getColor(R.styleable.TextViewOutlineRecent_outlineColor, DEFAULT_OUTLINE_COLOR);
            }
            if (a.hasValue(R.styleable.TextViewOutlineRecent_android_shadowRadius)
                    || a.hasValue(R.styleable.TextViewOutlineRecent_android_shadowDx)
                    || a.hasValue(R.styleable.TextViewOutlineRecent_android_shadowDy)
                    || a.hasValue(R.styleable.TextViewOutlineRecent_android_shadowColor)) {
                mShadowRadius = a.getFloat(R.styleable.TextViewOutlineRecent_android_shadowRadius, 0);
                mShadowDx = a.getFloat(R.styleable.TextViewOutlineRecent_android_shadowDx, 0);
                mShadowDy = a.getFloat(R.styleable.TextViewOutlineRecent_android_shadowDy, 0);
                mShadowColor = a.getColor(R.styleable.TextViewOutlineRecent_android_shadowColor, Color.TRANSPARENT);
            }

            a.recycle();
        }
    }

    private void setPaintToOutline() {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mOutlineSize);
        super.setTextColor(mOutlineColor);
        super.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor);
    }

    private void setPaintToRegular() {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        super.setTextColor(mTextColor);
        super.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setPaintToOutline();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        mTextColor = color;
    }

    @Override
    public void setShadowLayer(float radius, float dx, float dy, int color) {
        super.setShadowLayer(radius, dx, dy, color);
        mShadowRadius = radius;
        mShadowDx = dx;
        mShadowDy = dy;
        mShadowColor = color;
    }

    public void setOutlineSize(int size) {
        mOutlineSize = size;
    }

    public void setOutlineColor(int color) {
        mOutlineColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setPaintToOutline();
        super.onDraw(canvas);
        setPaintToRegular();
        super.onDraw(canvas);
    }

}
