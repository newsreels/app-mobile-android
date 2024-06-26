package com.ziro.bullet.mediapicker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ziro.bullet.R;


public class PictureCustomDialog extends Dialog {

    public PictureCustomDialog(Context context, int layout) {
        super(context, R.style.Picture_Theme_Dialog);
        setContentView(layout);
        initParams();
    }

    public PictureCustomDialog(Context context, View layout) {
        super(context, R.style.Picture_Theme_Dialog);
        setContentView(layout);
        initParams();
    }

    private void initParams() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
}
