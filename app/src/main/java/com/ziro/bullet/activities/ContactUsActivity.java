package com.ziro.bullet.activities;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.fragments.SuccessPopup;
import com.ziro.bullet.interfaces.ContactUsInterface;
import com.ziro.bullet.presenter.ContactUsPresenter;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.data.PrefConfig;

public class ContactUsActivity extends BaseActivity implements ContactUsInterface {
    private RelativeLayout header_back;
    private TextView mesg;
    private EditText email;
    private EditText name;
    private EditText msg;
    private RelativeLayout progress;
    private ImageView loader;
    private RelativeLayout sendEmail;
    private PrefConfig prefConfig;
    private ContactUsPresenter contactUsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsEvents.INSTANCE.logEvent(this,
                Events.HELP_CENTER);
        Utils.checkAppModeColor(this, false);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setContentView(R.layout.activity_contact_us);
        prefConfig = new PrefConfig(this);
        contactUsPresenter = new ContactUsPresenter(this, this);
        bindView();
        clickListener();
        setData();

//        Glide.with(this).load(Utils.getLoaderForTheme(prefConfig.getAppTheme())).into(loader);
    }

    private void setData() {
        SpannableString spannableString = new SpannableString(getString(R.string.drop_msg));
        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.primaryRed)),
                0, 17,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mesg.setText(spannableString);
        if (prefConfig.isUserObject()!=null) {
            if (!TextUtils.isEmpty(prefConfig.isUserObject().getFirst_name())) {
                String fullName = prefConfig.isUserObject().getFirst_name();
                if (!TextUtils.isEmpty(prefConfig.isUserObject().getLast_name())) {
                    fullName = fullName + " " + prefConfig.isUserObject().getLast_name();
                }
                name.setText(fullName);
            }
            if (!TextUtils.isEmpty(prefConfig.isUserObject().getEmail())) {
                email.setText("" + prefConfig.isUserObject().getEmail());
            }
        }
        if (getIntent() != null) {
            String msgTxt = getIntent().getStringExtra("msg");
            msg.setText(msgTxt);
        }
    }

    private void clickListener() {
        header_back.setOnClickListener(view -> {
            onBackPressed();
        });
        sendEmail.setOnClickListener(view -> {
            if (TextUtils.isEmpty(name.getText())) {
                Toast.makeText(this, "" + getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email.getText())) {
                Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Utils.isValidEmail(email.getText().toString().trim())) {
                Toast.makeText(this, "" + getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(msg.getText())) {
                Toast.makeText(this, "" + getString(R.string.enter_message), Toast.LENGTH_SHORT).show();
                return;
            }
            if (contactUsPresenter != null) {
                Utils.hideKeyboard(this, email);
                contactUsPresenter.contactUs(
                        email.getText().toString().trim(),
                        name.getText().toString(),
                        msg.getText().toString());
            }
        });
    }

    private void bindView() {
        progress = findViewById(R.id.progress);
        loader = findViewById(R.id.loader);
        msg = findViewById(R.id.msg);
        name = findViewById(R.id.name);
        sendEmail = findViewById(R.id.sendEmail);
        email = findViewById(R.id.email);
        mesg = findViewById(R.id.mesg);
        header_back = findViewById(R.id.header_back);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void error404(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(String msg) {
        SuccessPopup successPopup = new SuccessPopup(this, (flag, msg1) -> onBackPressed());
        successPopup.showDialog();
    }
}
