package com.ziro.bullet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.ziro.bullet.R;

public class ReelTutorialPopup {
    private CardView ok_btn;
    private TextView close;
    private Context context;
    private Dialog dialog;
    private View.OnClickListener clickListener;


    // CONSTRUCTORS
    public ReelTutorialPopup(Context context) {
        this.context = context;
    }

    // SHOW DIALOG METHOD
    public void showDialog() {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reel_tutorial_popup);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        initViews(dialog);

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    // SHOW DIALOG METHOD
    public void showDialog(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reel_tutorial_popup);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        initViews(dialog);

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void initViews(Dialog view) {
        ok_btn = view.findViewById(R.id.ok_btn);
        close = view.findViewById(R.id.close);

        ok_btn.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onClick(ok_btn);
            dialog.dismiss();
        });

        close.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onClick(close);
            dialog.dismiss();
        });

    }


    private void showToast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
