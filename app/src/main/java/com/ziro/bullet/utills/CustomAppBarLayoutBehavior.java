package com.ziro.bullet.utills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class CustomAppBarLayoutBehavior extends AppBarLayout.Behavior {

    private boolean shouldScroll = false;

    public CustomAppBarLayoutBehavior() {
        super();
    }

    public CustomAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes, int type) {
        return shouldScroll;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        if(shouldScroll){
            return super.onTouchEvent(parent, child, ev);
        }else{
            return false;
        }
    }

    public void setScrollBehavior(boolean shouldScroll){
        this.shouldScroll = shouldScroll;
    }

    public boolean isShouldScroll(){
        return shouldScroll;
    }
}