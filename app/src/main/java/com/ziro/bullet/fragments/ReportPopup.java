package com.ziro.bullet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ziro.bullet.interfaces.ReportConcernDialog;
import com.ziro.bullet.R;

public class ReportPopup {
    RelativeLayout cancel;
    RelativeLayout reportbtn;
    TextView title;
    TextView report_edittext;
    private Context context;
    private Dialog dialog;
    private ReportConcernDialog listener;


    // CONSTRUCTORS
    public ReportPopup(Context context, ReportConcernDialog listener) {
        this.context = context;
        this.listener = listener;
    }


    // SHOW DIALOG METHOD
    public void showDialog() {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.report_popup);
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
        title = view.findViewById(R.id.title);
        report_edittext = view.findViewById(R.id.report_edittext);
        cancel = view.findViewById(R.id.cancel);
        reportbtn = view.findViewById(R.id.reportbtn);
        cancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.isPositive(false, null);
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        reportbtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(report_edittext.getText().toString())) {
                if (listener != null) {
                    listener.isPositive(true, report_edittext.getText().toString());
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            } else {
                showToast(context.getString(R.string.enter_your_reason_first));
            }
        });
    }


    private void showToast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
