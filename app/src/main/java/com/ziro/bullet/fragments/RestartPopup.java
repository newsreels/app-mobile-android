package com.ziro.bullet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ziro.bullet.interfaces.ReportConcernDialog;
import com.ziro.bullet.R;

public class RestartPopup {
    private TextView cancel;
    private TextView blockBtn;
    private TextView title_1, title_2;
    private Context context;
    private Dialog dialog;
    private ReportConcernDialog listener;

    // CONSTRUCTORS
    public RestartPopup(Context context, ReportConcernDialog listener) {
        this.context = context;
        this.listener = listener;
    }

    // SHOW DIALOG METHOD
    public void showDialog() {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.block_popup);
        dialog.setCancelable(true);
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
        title_1 = view.findViewById(R.id.title_1);
        title_2 = view.findViewById(R.id.title_2);
        title_2 = view.findViewById(R.id.title_2);
        cancel = view.findViewById(R.id.cancel);
        blockBtn = view.findViewById(R.id.block);
        TextView msg = view.findViewById(R.id.msg);

        title_2.setVisibility(View.INVISIBLE);
        title_1.setVisibility(View.GONE);
        msg.setText(context.getString(R.string.restart_aap_label));
        blockBtn.setText(context.getString(R.string.yes));
        cancel.setText(context.getString(R.string.no));

        cancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.isPositive(false, null);
            }
            if (dialog != null) {
                dialog.dismiss();
            }
        });


        blockBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.isPositive(true, "");
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
