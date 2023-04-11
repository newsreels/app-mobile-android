package com.ziro.bullet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ziro.bullet.interfaces.PasswordInterface;
import com.ziro.bullet.R;

public class ForgotPasswordSettingPopup {
    RelativeLayout send;
    TextView content;
    TextView cancel;
    private Context context;
    private Dialog dialog;
    private PasswordInterface anInterface;


    // CONSTRUCTORS
    public ForgotPasswordSettingPopup(Context context, PasswordInterface anInterface) {
        this.context = context;
        this.anInterface = anInterface;
    }


    // SHOW DIALOG METHOD
    public void showDialog(String email) {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forgot_password_setting_popup);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        initViews(dialog);
        setContent(email);

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void setContent(String email) {
        content.setText(context.getResources().getString(R.string.we_will_send_an_email_to) + email + context.getResources().getString(R.string.with_a_link_to_reset_your_password));
    }

    private void initViews(Dialog view) {
        content = view.findViewById(R.id.content);
        send = view.findViewById(R.id.send);
        cancel = view.findViewById(R.id.cancel);

        // CLOSE BUTTONS CLICKS
        send.setOnClickListener(v ->
                {
                    send.setEnabled(false);
                    anInterface.reset();
                    dialog.dismiss();
                }
        );
        cancel.setOnClickListener(v -> dialog.dismiss());
    }

}
