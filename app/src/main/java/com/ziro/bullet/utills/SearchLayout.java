package com.ziro.bullet.utills;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class SearchLayout extends RelativeLayout {

    private static final String TAG = "SearchLayout";

    private static Activity mSearchActivity;;

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchLayout(Context context) {
        super(context);
    }

    public static void setSearchActivity(Activity searchActivity) {
        mSearchActivity = searchActivity;
    }

    /**
     * Overrides the handling of the back key to move back to the 
     * previous sources or dismiss the search dialog, instead of 
     * dismissing the input method.
     */
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEventPreIme(" + event + ")");
        if (mSearchActivity != null && 
                    event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            KeyEvent.DispatcherState state = getKeyDispatcherState();
            if (state != null) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    state.startTracking(event, this);
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP
                        && !event.isCanceled() && state.isTracking(event)) {
                    mSearchActivity.onBackPressed();
                    return true;
                }
            }
        }

        return super.dispatchKeyEventPreIme(event);
    }
}