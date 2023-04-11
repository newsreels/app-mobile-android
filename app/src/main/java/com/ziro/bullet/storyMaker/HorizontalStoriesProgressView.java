package com.ziro.bullet.storyMaker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ziro.bullet.R;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;
import java.util.List;

public class HorizontalStoriesProgressView extends LinearLayout {

    private static final String TAG = StoriesProgressView.class.getSimpleName();

    private final LayoutParams PROGRESS_BAR_LAYOUT_PARAM = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private final LayoutParams SPACE_LAYOUT_PARAM = new LayoutParams(20, LayoutParams.WRAP_CONTENT);
    private final List<HorizontalPausableProgressBar> progressBars = new ArrayList<>();
    boolean isComplete;
    private int storiesCount = -1;
    /**
     * pointer of running animation
     */
    private int current = -1;
    private StoriesListener storiesListener;
    private boolean isSkipStart;
    private boolean isReverseStart;

    public HorizontalStoriesProgressView(Context context) {
        this(context, null);
    }

    public HorizontalStoriesProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HorizontalStoriesProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalStoriesProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView);
        storiesCount = typedArray.getInt(R.styleable.StoriesProgressView_progressCount, 0);
        Constants.mProgressCollapseSize = context.getResources().getDimensionPixelSize(R.dimen.progress_bar_height);
        typedArray.recycle();
        bindViews();
    }

    private void bindViews() {
        progressBars.clear();
        removeAllViews();

        for (int i = 0; i < storiesCount; i++) {
            final HorizontalPausableProgressBar p = createProgressBar();
            progressBars.add(p);
            addView(p);
            if ((i + 1) < storiesCount) {
                addView(createSpace());
            }
        }
    }

    private HorizontalPausableProgressBar createProgressBar() {
        HorizontalPausableProgressBar p = new HorizontalPausableProgressBar(getContext());
        p.setLayoutParams(PROGRESS_BAR_LAYOUT_PARAM);
        return p;
    }

    private View createSpace() {
        View v = new View(getContext());
        v.setLayoutParams(SPACE_LAYOUT_PARAM);
        return v;
    }

    /**
     * Set story count and create views
     *
     * @param storiesCount story count
     */
    public void setStoriesCount(int storiesCount) {
        this.storiesCount = storiesCount;
        bindViews();
    }

    /**
     * Set storiesListener
     *
     * @param storiesListener StoriesListener
     */
    public void setStoriesListener(StoriesListener storiesListener) {
        this.storiesListener = storiesListener;
    }

    /**
     * Skip current story
     */
    public void skip() {
        if (isSkipStart || isReverseStart) return;
        if (isComplete) return;
        if (current < 0) return;
        HorizontalPausableProgressBar p = progressBars.get(current);
        isSkipStart = true;
        p.setMax();
    }

    /**
     * Reverse current story
     */
    public void reverse() {
        if (isSkipStart || isReverseStart) return;
        if (isComplete) return;
        if (current < 0) return;
        HorizontalPausableProgressBar p = progressBars.get(current);
        isReverseStart = true;
        p.setMin();
    }

    /**
     * Set Progress Width
     *
     * @param width
     */
    public void setProgressWidth(int width, boolean animate) {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setWidth(width, animate);
        }
    }

    /**
     * Set a story's duration
     *
     * @param duration millisecond
     */
    public void setStoryDuration(long duration) {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(duration);
            progressBars.get(i).setCallback(callback(i));
        }
    }

    public void setStoryDuration(long duration, int index) {
        if (index < progressBars.size()) {
            progressBars.get(index).setDuration(duration);
            progressBars.get(index).setCallback(callback(index));
        }
    }

    /**
     * Set stories count and each story duration
     *
     * @param durations milli
     */
    public void setStoriesCountWithDurations(@NonNull long[] durations) {
        storiesCount = durations.length;
        bindViews();
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(durations[i]);
            progressBars.get(i).setCallback(callback(i));
        }
    }

    private HorizontalPausableProgressBar.Callback callback(final int index) {
        return new HorizontalPausableProgressBar.Callback() {
            @Override
            public void onStartProgress() {
                Log.e("Animation Callback", String.valueOf(index));
//                Log.d(TAG, "==================================");
//                Log.d(TAG, "onStartProgress: " + index);
////                if (current == -1) {
//                    if (storiesListener != null) storiesListener.onFirst();
//                }
                current = index;

                for (int i = 0; i < progressBars.size(); i++) {
                    if (i == current) {
                        progressBars.get(i).setWidth(Constants.mProgressExpandSize, true);
                    } else {
                        progressBars.get(i).setWidth(Constants.mProgressCollapseSize, true);
                    }
                }

                new Handler().postDelayed(() -> {
                    if (current - 1 >= 0) {
                        for (int i = 0; i < current; i++) {
                            if (i < progressBars.size()) {
                                progressBars.get(i).setMaxWithoutCallback();
                            }
                        }
                    }
                }, Constants.mProgressAnimationSpeed);

            }

            @Override
            public void onFinishProgress() {
                Log.d(TAG, "onFinishProgress: " + index);
                // progressBars.get(index).setWidth(Constants.mProgressCollapseSize, true);
                if (isReverseStart) {
                    if (storiesListener != null) storiesListener.onPrev();
                    if (0 <= (current - 1)) {
                        HorizontalPausableProgressBar p = progressBars.get(current - 1);
                        p.setMinWithoutCallback();
//                        progressBars.get(--current).startProgress();
                    } else {
//                        progressBars.get(current).startProgress();
                    }
                    isReverseStart = false;
                    return;
                }
                int next = current + 1;
                if (next <= (progressBars.size() - 1)) {
                    if (storiesListener != null) storiesListener.onNext();
//                    progressBars.get(next).startProgress();
                } else {
                    isComplete = true;
                    if (storiesListener != null) storiesListener.onComplete();
                }
                isSkipStart = false;
            }
        };
    }

    /**
     * Start progress animation
     */
    public void startStories() {
        Log.d("selectStory", "================================================");
        Log.e("selectStory", "startStories : 0 static");
//        Log.e("itemCard", "storiesCount : " + storiesCount);
//        Log.e("itemCard", "progressBars.size() : " + progressBars.size());
        Log.d("selectStory", "================================================");
        if (progressBars.size() > 0)
            progressBars.get(0).startProgress();
    }

    public void restartLast() {
        if (current < 0 || current >= progressBars.size())
            return;
        startStories(current);
    }

    /**
     * Start progress animation from specific progress
     */
    public void startStories(int from) {

        Log.d("selectStory", "================================================");
        Log.e("selectStory", "startStories : " + from);
        Log.d("selectStory", "================================================");
        if (storiesCount > 0 && from < storiesCount) {

            for (int i = 0; i < storiesCount; i++) {
                progressBars.get(i).resumeProgress();
                progressBars.get(i).setMinWithoutCallback();
                progressBars.get(i).setWidth(Constants.mProgressCollapseSize, false);
                progressBars.get(i).setCallback(null);
            }

            for (int i = 0; i < from; i++) {
                progressBars.get(i).setMaxWithoutCallback();
                progressBars.get(i).setWidth(Constants.mProgressCollapseSize, false);
                progressBars.get(i).setCallback(null);
            }

            int curr = current;

            if (current > -1 && current < storiesCount) {
                progressBars.get(current).setMinWithoutCallback();
                if (current < from)
                    progressBars.get(current).reverseProgress(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (curr < from) {
                                progressBars.get(curr).setMaxWithoutCallback();
                            } else
                                progressBars.get(curr).setMinWithoutCallback();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                else
                    progressBars.get(current).reverseProgressPrev(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (curr < from) {
                                progressBars.get(curr).setMaxWithoutCallback();
                            } else
                                progressBars.get(curr).setMinWithoutCallback();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
//                progressBars.get(from - 1).setWidth(Constants.mProgressCollapseSize, false);
                progressBars.get(current).setCallback(null);
//                }
            }
            progressBars.get(from).startProgress();
            progressBars.get(from).setCallback(callback(from));
        }
    }

    /**
     * Start progress animation from specific progress
     */
    public void selectStory(int from) {
        Log.e("selectStory", "selectStory : " + from);
        if (from < progressBars.size()) {
            for (int i = 0; i < storiesCount; i++) {
                progressBars.get(i).resumeProgress();
                progressBars.get(i).setMinWithoutCallback();
                progressBars.get(i).setWidth(Constants.mProgressCollapseSize, false);
                progressBars.get(i).setCallback(null);
            }

            for (int i = 0; i < from; i++) {
                progressBars.get(i).setMaxWithoutCallback();
                progressBars.get(i).setWidth(Constants.mProgressCollapseSize, false);
                progressBars.get(i).setCallback(null);
            }

            if (current > -1 && current < storiesCount) {

                int curr = current;
                progressBars.get(current).setMinWithoutCallback();
                if (current < from)
                    progressBars.get(current).reverseProgress(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (curr < from) {
                                progressBars.get(curr).setMaxWithoutCallback();
                            } else
                                progressBars.get(curr).setMinWithoutCallback();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                else
                    progressBars.get(current).reverseProgressPrev(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (curr < from) {
                                progressBars.get(curr).setMaxWithoutCallback();
                            } else
                                progressBars.get(curr).setMinWithoutCallback();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
//                progressBars.get(from - 1).setWidth(Constants.mProgressCollapseSize, false);
                progressBars.get(current).setCallback(null);
//                }
            }

            progressBars.get(from).setWidth(Constants.mProgressExpandSize, false);
            progressBars.get(from).setMaxWithoutCallbackFill();
            progressBars.get(from).setCallback(callback(from));
        }
    }

    public void clearStory() {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setMinWithoutCallback();
            progressBars.get(i).clear();
        }
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    public void destroy() {
        for (HorizontalPausableProgressBar p : progressBars) {
            p.clear();
        }
    }

    /**
     * Pause story
     */
    public void pause() {
        if (current < 0 || current >= progressBars.size())
            return;
        progressBars.get(current).pauseProgress();
    }

    /**
     * Resume story
     */
    public void resume() {
        if (current < 0 || current >= progressBars.size())
            return;
        progressBars.get(current).resumeProgress();
    }

    public int getCurrent() {
        return current;
    }

    public interface StoriesListener {
        void onFirst();

        void onNext();

        void onPrev();

        void onComplete();
    }
}