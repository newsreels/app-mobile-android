package com.ziro.bullet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.ziro.bullet.R;

public class ReportSentPopup {

    private Context context;
    private Dialog dialog;

    // CONSTRUCTORS
    public ReportSentPopup(Context context) {
        this.context = context;
    }

    // SHOW DIALOG METHOD
    public void showDialog(DialogInterface.OnDismissListener onDismissListener) {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.report_sent_popup);
        dialog.setOnDismissListener(onDismissListener);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        if (!dialog.isShowing()) {
            dialog.show();
            new Handler().postDelayed(() -> dialog.dismiss(), 3000);
        }
    }
}
