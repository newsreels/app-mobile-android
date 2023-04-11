package com.ziro.bullet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.ziro.bullet.R;

public class LoadingProgressDialog extends Dialog {
    TextView mTvPercentage;
    CircularProgressIndicator mCircularProgressIndicator;

    public LoadingProgressDialog(@NonNull Context context) {
        super(context, R.style.Picture_Theme_AlertDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setWindowAnimations(R.style.PictureThemeDialogWindowStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_progress);
        mTvPercentage = findViewById(R.id.tvPercentage);
        mCircularProgressIndicator = findViewById(R.id.progress_circular);
    }

    public void updateProgress(int progress) {
        if (mTvPercentage != null) {
            mTvPercentage.setText("" + progress + " %");
        }

        if (mCircularProgressIndicator != null)
            mCircularProgressIndicator.setProgress(progress);
    }
}
