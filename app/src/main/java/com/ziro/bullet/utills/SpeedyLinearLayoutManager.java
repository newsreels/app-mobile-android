package com.ziro.bullet.utills;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class SpeedyLinearLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;
    private static final float MILLISECONDS_PER_INCH = 155f; //default is 25f (bigger = slower)

//    public SpeedyLinearLayoutManager(Context context) {
//        super(context);
//    }
//
//    public SpeedyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
//        super(context, orientation, reverseLayout);
//    }

    public SpeedyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

//    @Override
//    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
//        try {
//            RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
//
//            smoothScroller.setTargetPosition(position);
//            startSmoothScroll(smoothScroller);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }

//    private class TopSnappedSmoothScroller extends LinearSmoothScroller {
//        public TopSnappedSmoothScroller(Context context) {
//            super(context);
//
//        }
//
//        @Override
//        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
////            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
//            return super.calculateSpeedPerPixel(displayMetrics) * 5;
//        }
//
//        @Override
//        public PointF computeScrollVectorForPosition(int targetPosition) {
//            return SpeedyLinearLayoutManager.this
//                    .computeScrollVectorForPosition(targetPosition);
//        }
//
//        @Override
//        protected int getVerticalSnapPreference() {
//            return SNAP_TO_START;
//        }
//    }

    public SpeedyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SpeedyLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private class CenterSmoothScroller extends LinearSmoothScroller {

        CenterSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return 0.5f; //pass as per your requirement
        }
    }
}