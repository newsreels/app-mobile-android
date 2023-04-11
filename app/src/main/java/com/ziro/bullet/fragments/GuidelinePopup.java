package com.ziro.bullet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ziro.bullet.R;
import com.ziro.bullet.interfaces.ReportConcernDialog;
import com.ziro.bullet.utills.Utils;

public class GuidelinePopup {
    private  RelativeLayout continuee;
    private RelativeLayout main;
    private Context context;
    private Dialog dialog;
    private ReportConcernDialog listener;
    private TextView community_guideline;


    // CONSTRUCTORS
    public GuidelinePopup(Context context, ReportConcernDialog listener) {
        this.context = context;
        this.listener = listener;
    }


    // SHOW DIALOG METHOD
    public void showDialog() {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.guideline_popup);
        dialog.setCancelable(true);
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
        main = view.findViewById(R.id.main);
        continuee = view.findViewById(R.id.continuee);
        community_guideline = view.findViewById(R.id.community_guideline);
        continuee.setOnClickListener(v -> {
            if (listener != null) {
                listener.isPositive(true, null);
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        main.setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        community_guideline.setOnClickListener(v -> {
            Utils.openWebView(context, "https://www.newsinbullets.app/community-guidelines?header=false", context.getString(R.string.community_guideline_));
            if (dialog != null) {
                dialog.dismiss();
            }
        });
    }


    private void showToast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
