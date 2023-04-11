package com.ziro.bullet.fragments;

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

public class MessagePopup {
    RelativeLayout btn;
    TextView title;
    TextView content;
    TextView btnText;
    private Context context;
    private Dialog dialog;
    private String email;
    private String titleTxt;
    private String contentTxt;
    private String btnTxt;
    private View.OnClickListener clickListener;


    // CONSTRUCTORS
    public MessagePopup(Context context, String email, String title, String content, String btnText) {
        this.context = context;
        this.email = email;
        this.titleTxt = title;
        this.contentTxt = content;
        this.btnTxt = btnText;
    }


    // SHOW DIALOG METHOD
    public void showDialog() {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.msg_popup);
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
        dialog.setContentView(R.layout.msg_popup);
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
        content = view.findViewById(R.id.content);
        btnText = view.findViewById(R.id.btnText);
        btn = view.findViewById(R.id.btn);

        btn.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onClick(btn);
            dialog.dismiss();
        });


        title.setText(titleTxt);
        content.setText(contentTxt);
        btnText.setText(btnTxt);
    }


    private void showToast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

}
