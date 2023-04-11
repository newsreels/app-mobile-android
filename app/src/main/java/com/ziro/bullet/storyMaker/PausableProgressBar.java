package com.ziro.bullet.storyMaker;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ziro.bullet.R;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.ResizeHeightAnimation;

final class PausableProgressBar extends FrameLayout {

    /***
     * progress
     */
    private static final int DEFAULT_PROGRESS_DURATION = 2000;

    private View frontProgressView;
    private View maxProgressView;
    private View back_progress;
    private FrameLayout bullet;

    private PausableScaleAnimation animation;
    private long duration = DEFAULT_PROGRESS_DURATION;
    private Callback callback;

    public PausableProgressBar(Context context) {
        this(context, null);
    }

    public PausableProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PausableProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.pausable_progress, this);
        bullet = findViewById(R.id.bullet);
        back_progress = findViewById(R.id.back_progress_1);
        frontProgressView = findViewById(R.id.front_progress_1);
        maxProgressView = findViewById(R.id.max_progress_1); // work around
//        frontProgressView.setBackgroundColor(Color.parseColor(Constants.ProgressColor));
//        frontProgressView.setBackground(getContext().getResources().getDrawable(R.drawable.bullet_selector));
        frontProgressView.setBackgroundResource(Constants.ProgressBarDrawable);
        back_progress.setBackgroundResource(Constants.ProgressBarBackDrawable);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setHeight(int height, boolean animate) {
        ResizeHeightAnimation anim = new ResizeHeightAnimation(bullet, height);
        if (animate) {
            anim.setDuration(Constants.mProgressAnimationSpeed);
        } else {
            anim.setDuration(0);
        }
        Log.d("TAG", "setHeight: start");
        bullet.startAnimation(anim);
    }

    public void setCallback(@NonNull Callback callback) {
        this.callback = callback;
    }

    void setMax() {
        finishProgress(true);
    }

    void setMin() {
        finishProgress(false);
    }

    void setMinWithoutCallback() {
//        maxProgressView.setBackgroundResource(R.color.progress_secondary);
        maxProgressView.setBackgroundResource(R.drawable.bullet_selector_2);
        maxProgressView.setVisibility(INVISIBLE);
        frontProgressView.setVisibility(INVISIBLE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
        }
    }

    void setMaxWithoutCallback() {
//        maxProgressView.setBackgroundResource(R.color.progress_secondary);
//        maxProgressView.setBackgroundResource(R.drawable.bullet_selector);
        maxProgressView.setBackgroundResource(Constants.ProgressBarDrawable);
        maxProgressView.setVisibility(VISIBLE);
        frontProgressView.setVisibility(INVISIBLE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
        }
    }

    void setMaxWithoutCallbackFill() {
//        maxProgressView.setBackgroundResource(R.color.progress_secondary);
//        maxProgressView.setBackgroundResource(R.drawable.bullet_selector);
        maxProgressView.setBackgroundResource(Constants.ProgressBarDrawable);
        maxProgressView.setVisibility(VISIBLE);
        frontProgressView.setVisibility(VISIBLE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
        }
    }

    private void finishProgress(boolean isMax) {
        if (isMax) {
//            maxProgressView.setBackgroundResource(R.color.progress_secondary);
            maxProgressView.setBackgroundResource(Constants.ProgressBarBackDrawable);
        }
        maxProgressView.setVisibility(isMax ? VISIBLE : GONE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
            if (callback != null) {
                callback.onFinishProgress();
            }
        }
    }

    public void reverseProgress(){
        setHeight(Constants.mProgressCollapseSize, true);
    }

    public void reverseProgressPrev(){
        maxProgressView.setBackgroundResource(R.drawable.bullet_selector_2);
        setHeight(Constants.mProgressCollapseSize, true);
    }

    public void startProgress() {
        maxProgressView.setVisibility(GONE);

        animation = new PausableScaleAnimation(1, 1, 0, 1, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                frontProgressView.setVisibility(View.VISIBLE);
                if (callback != null) callback.onStartProgress();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (callback != null) callback.onFinishProgress();
            }
        });
        animation.setFillAfter(true);
        Log.d("TAG", "startProgress: start");
        frontProgressView.startAnimation(animation);
    }

    public void pauseProgress() {
        if (animation != null) {
            animation.pause();
        }
    }

    public void resetProgress() {
        if (animation != null) {
            animation.reset();
            animation.cancel();
        }
    }

    public void resumeProgress() {
        if (animation != null) {
            animation.resume();
        }
    }

    void clear() {
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
            animation = null;
        }
    }

    interface Callback {
        void onStartProgress();

        void onFinishProgress();
    }

    private class PausableScaleAnimation extends ScaleAnimation {

        private long mElapsedAtPause = 0;
        private boolean mPaused = false;

        PausableScaleAnimation(float fromX, float toX, float fromY,
                               float toY, int pivotXType, float pivotXValue, int pivotYType,
                               float pivotYValue) {
            super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
                    pivotYValue);
        }

        @Override
        public boolean getTransformation(long currentTime, Transformation outTransformation, float scale) {
            if (mPaused && mElapsedAtPause == 0) {
                mElapsedAtPause = currentTime - getStartTime();
            }
            if (mPaused) {
                setStartTime(currentTime - mElapsedAtPause);
            }
            return super.getTransformation(currentTime, outTransformation, scale);
        }

        /***
         * pause animation  ==
         */
        void pause() {
            if (mPaused) return;
            mElapsedAtPause = 0;
            mPaused = true;
        }

        /***
         * resume animation
         */
        void resume() {
            mPaused = false;
        }
    }
}