package com.ziro.bullet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Utils;

public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.Picture_Theme_AlertDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setWindowAnimations(R.style.PictureThemeDialogWindowStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

        if (getContext() != null) {
            PrefConfig prefConfig = new PrefConfig(getContext());

            ImageView loader_gif = findViewById(R.id.loader);
            Glide.with(getContext())
                    .load(Utils.getLoaderForTheme(prefConfig.getAppTheme()))
                    .into(loader_gif);
        }
    }
}

