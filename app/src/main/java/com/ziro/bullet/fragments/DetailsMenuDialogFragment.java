package com.ziro.bullet.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;


public class DetailsMenuDialogFragment extends DialogFragment {

    private PrefConfig prefConfig;

    public DetailsMenuDialogFragment(Context context) {
        prefConfig = new PrefConfig(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            Window window = getDialog().getWindow();
            window.setGravity(Gravity.TOP | Gravity.END);
            WindowManager.LayoutParams wmlp = window.getAttributes();
            wmlp.y = 50;
        }
        return inflater.inflate(R.layout.fragment_details_menu, container);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView location = view.findViewById(R.id.location);
        TextView language = view.findViewById(R.id.language);
        ImageView ivClose = view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> {
            if (getDialog() != null)
                getDialog().dismiss();
        });
        if (prefConfig != null) {
            if (!TextUtils.isEmpty(prefConfig.getSrcLang())) {
                language.setText(prefConfig.getSrcLang());
            } else {
                language.setText("");
            }
            if (!TextUtils.isEmpty(prefConfig.getSrcLoc())) {
                location.setText(prefConfig.getSrcLoc());
            } else {
                location.setText("");
            }
        }
    }
}
