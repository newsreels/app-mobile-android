package com.ziro.bullet.mediapicker.gallery.process;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.ziro.bullet.R;


public class InstagramLoadingDialog extends Dialog {

    private InstagramLoadingView mContentView;

    public InstagramLoadingDialog(Context context) {
        super(context, R.style.Picture_Theme_AlertDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setWindowAnimations(R.style.PictureThemeDialogWindowStyle);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentView = new InstagramLoadingView(getContext());
        setContentView(mContentView);
    }

    public void updateProgress(double progress) {
        if (mContentView != null) {
            mContentView.updateProgress(progress);
        }
    }
}