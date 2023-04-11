package com.ziro.bullet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.EditProfileActivity;

public class CommentPopup {
    private RelativeLayout continuee;
    private RelativeLayout root;
    private TextView desc;
    private Context context;
    private Dialog dialog;


    // CONSTRUCTORS
    public CommentPopup(Context context) {
        this.context = context;
    }


    // SHOW DIALOG METHOD
    public void showDialog(String str) {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.comment_popup);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        initViews(dialog);

        if (!TextUtils.isEmpty(str))
            desc.setText(str);

        if (!((Activity) context).isFinishing())
            if (!dialog.isShowing()) {
                dialog.show();
            }
    }

    private void initViews(Dialog view) {
        desc = view.findViewById(R.id.desc);
        continuee = view.findViewById(R.id.continuee);
        root = view.findViewById(R.id.root);
        root.setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        continuee.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EditProfileActivity.class));
            if (dialog != null) {
                dialog.dismiss();
            }
        });
    }

    private void showToast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
