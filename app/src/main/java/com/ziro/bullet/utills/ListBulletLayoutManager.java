package com.ziro.bullet.utills;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ListBulletLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public ListBulletLayoutManager(Context context) {
        super(context);

    }

    public ListBulletLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }

    @Override
    public boolean canScrollHorizontally() {
        return isScrollEnabled && super.canScrollHorizontally();
    }
}