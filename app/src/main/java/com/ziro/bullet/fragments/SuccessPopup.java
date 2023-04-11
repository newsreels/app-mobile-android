package com.ziro.bullet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ziro.bullet.R;
import com.ziro.bullet.interfaces.ReportConcernDialog;

public class SuccessPopup {
    RelativeLayout continuee;
    private Context context;
    private Dialog dialog;
    private ReportConcernDialog listener;


    // CONSTRUCTORS
    public SuccessPopup(Context context, ReportConcernDialog listener) {
        this.context = context;
        this.listener = listener;
    }


    // SHOW DIALOG METHOD
    public void showDialog() {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.success_popup);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        initViews(dialog);

        if (!((Activity) context).isFinishing())
            if (!dialog.isShowing()) {
                dialog.show();
            }
    }

    private void initViews(Dialog view) {
        ImageView gif = view.findViewById(R.id.gif);
        try {
            Glide.with(context)
                    .load(R.raw.succcess)
                    .into(gif);
        } catch (Exception e) {
            e.printStackTrace();
        }
        continuee = view.findViewById(R.id.continuee);
        continuee.setOnClickListener(v -> {
            if (listener != null) {
                listener.isPositive(false, null);
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        });
    }


    private void showToast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
