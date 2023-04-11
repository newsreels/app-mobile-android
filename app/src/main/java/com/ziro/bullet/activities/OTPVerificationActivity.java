package com.ziro.bullet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.SignInInterface;
import com.ziro.bullet.presenter.OTPVerificationPresenter;
import com.ziro.bullet.utills.ChatEditText;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.SearchLayout;
import com.ziro.bullet.utills.URLSpanNoUnderline;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

public class OTPVerificationActivity extends BaseActivity implements SignInInterface {

    private RelativeLayout back;
    private TextView help;
    private TextView emailTv;
    private TextView resend;
    private TextView button_text;
    private CardView button;
    private ChatEditText code1, code2, code3, code4, code5, code6;
    private String email = "";
    private OTPVerificationPresenter presenter;
    private String check = "";
    private RelativeLayout progress;
    private PrefConfig prefConfig;
    private boolean forgot = false;
    private LinearLayout terms_n_condition;
    private ImageView terms_check;
    private TextView terms;
    private LinearLayout errorMain;
    private TextView errorTv;
    private boolean isChecked = false;
    private boolean isPopUpLogin;
    private boolean finishToLogin;

    @Override
    protected void onResume() {
        super.onResume();
        Utils.showKeyboard(this);
        SearchLayout.setSearchActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() != null && getIntent().hasExtra("forgot")){
            forgot = getIntent().getBooleanExtra("forgot", false);
            if(!forgot) {
                new Components().blackStatusBar(this, "black");
                BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
        setContentView(R.layout.activity_otp_verification);

        if(getIntent() != null && getIntent().hasExtra("isPopUpLogin")){
            isPopUpLogin = getIntent().getBooleanExtra("isPopUpLogin", false);
        }
        if(getIntent() != null && getIntent().hasExtra("finishToLogin")){
            finishToLogin = getIntent().getBooleanExtra("finishToLogin", false);
        }

        bindView();
        setListener();
        init();
        setData();
    }

    private void bindView() {
        progress = findViewById(R.id.progress);
        button = findViewById(R.id.button);
        button_text = findViewById(R.id.button_text);
        back = findViewById(R.id.back);
        help = findViewById(R.id.help);
        emailTv = findViewById(R.id.email);
        resend = findViewById(R.id.resend);
        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);
        terms_n_condition = findViewById(R.id.terms_n_condition);
        errorTv = findViewById(R.id.error);
        errorMain = findViewById(R.id.errorMain);
        terms_check = findViewById(R.id.terms_check);
        terms = findViewById(R.id.terms);
        button.setBackground(getResources().getDrawable(R.drawable.shape_with_stroke));
    }

    private void setListener() {
        back.setOnClickListener(v -> onBackPressed());
        help.setOnClickListener(v -> {
            Utils.hideKeyboard(this, emailTv);
            Intent intent = new Intent(this, HelpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
        button.setOnClickListener(v -> {
            if (presenter != null) {
                if (!TextUtils.isEmpty(getCode())) {
//                    if (!forgot) {
//                        if (!isChecked) {
//                            error(getString(R.string.accept_terms_and_conditions_to_continue));
//                            return;
//                        }
//                    }
                    presenter.codeValid(email, getCode(), forgot);
                }
            }
        });
        resend.setOnClickListener(v -> {
            if (presenter != null) {
                if (!TextUtils.isEmpty(email)) {
                    presenter.resendCode(email);
                }
            }
        });

        terms_check.setOnClickListener(v -> {
            if (!isChecked) {
                isChecked = true;
                terms_check.setImageResource(R.drawable.ic_terms_check_password);
            } else {
                isChecked = false;
                terms_check.setImageResource(R.drawable.ic_terms_uncheck_password);
            }
        });

        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
                if (s.length() == 1) {
                    code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
                if (s.length() == 1) {
                    code3.requestFocus();
                } else {
                    code1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code2.setKeyImeChangeListener((keyCode, event) -> {
            if (TextUtils.isEmpty(code2.getText())) {
                code1.setText("");
                code1.requestFocus();
                clearError();
            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
                if (s.length() == 1) {
                    code4.requestFocus();
                } else {
                    code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code3.setKeyImeChangeListener((keyCode, event) -> {
            if (TextUtils.isEmpty(code3.getText())) {
                code2.setText("");
                code2.requestFocus();
                clearError();
            }
        });
        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
                if (s.length() == 1) {
                    code5.requestFocus();
                } else {
                    code3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code4.setKeyImeChangeListener((keyCode, event) -> {
            if (TextUtils.isEmpty(code4.getText())) {
                code3.setText("");
                code3.requestFocus();
                clearError();
            }
        });
        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
                if (s.length() == 1) {
                    code6.requestFocus();
                } else {
                    code4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code5.setKeyImeChangeListener((keyCode, event) -> {
            if (TextUtils.isEmpty(code5.getText())) {
                code4.setText("");
                clearError();
                code4.requestFocus();
            }
        });
        code6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
                if (s.length() == 1) {

                } else {
                    code5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code6.setKeyImeChangeListener((keyCode, event) -> {
            if (TextUtils.isEmpty(code6.getText())) {
                code5.setText("");
                clearError();
                code5.requestFocus();
            }
        });
        code1.requestFocus();
    }

    private void clearError() {
        errorMain.setVisibility(View.INVISIBLE);
        errorTv.setText("");
    }

    private void setData() {
        button_text.setText(getString(R.string.next));
        if (getIntent() != null) {
            email = getIntent().getStringExtra("email");
            check = getIntent().getStringExtra("check");
            forgot = getIntent().getBooleanExtra("forgot", false);
//            if (!TextUtils.isEmpty(check) && !check.equalsIgnoreCase("forgot_password") && !check.equalsIgnoreCase("forgot_password_from_settings")) {
//                terms_n_condition.setVisibility(View.VISIBLE);
//                String bodyData = getString(R.string.checkbox_terms);
//                try {
//
//                    SpannableStringBuilder spannableString = new SpannableStringBuilder(bodyData);
//                    spannableString.setSpan(new ClickableSpan() {
//                        @Override
//                        public void onClick(View v) {
//                            Utils.hideKeyboard(OTPVerificationActivity.this, terms);
//                            Utils.openWebView(OTPVerificationActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions));
//                        }
//                    }, 39, 55, 0);
//                    spannableString.setSpan(new URLSpanNoUnderline(getString(R.string.terms_conditions)), 39, 55, 0);
//                    spannableString.setSpan(
//                            new ForegroundColorSpan(getResources().getColor(R.color.white)),
//                            39, 55,
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    spannableString.setSpan(new ClickableSpan() {
//                        @Override
//                        public void onClick(View v) {
//                            Utils.hideKeyboard(OTPVerificationActivity.this, terms);
//                            Utils.openWebView(OTPVerificationActivity.this, "https://www.newsinbullets.app/privacy?header=false", getString(R.string.privacy_policy));
//                        }
//                    }, 96, 111, 0);
//                    spannableString.setSpan(new URLSpanNoUnderline(getString(R.string.privacy_policy)), 96, 111, 0);
//                    spannableString.setSpan(
//                            new ForegroundColorSpan(getResources().getColor(R.color.white)),
//                            96, 111,
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    terms.setText(spannableString);
//                    terms.setMovementMethod(LinkMovementMethod.getInstance());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    terms.setText(bodyData);
//                    terms.setOnClickListener(v -> Utils.openWebView(OTPVerificationActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions)));
//                }
//            }
            emailTv.setText(email);
        }
    }

    private void init() {
        prefConfig = new PrefConfig(this);
        presenter = new OTPVerificationPresenter(this, this);
    }

    private void isEnableClicks(boolean enable) {
        back.setEnabled(enable);
        help.setEnabled(enable);
        code1.setEnabled(enable);
        code2.setEnabled(enable);
        code3.setEnabled(enable);
        code4.setEnabled(enable);
        code5.setEnabled(enable);
        code6.setEnabled(enable);
        button.setEnabled(enable);
        resend.setEnabled(enable);
    }

    @Override
    public void onBackPressed() {
        Utils.hideKeyboard(this, emailTv);
        finishAfterTransition();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @NotNull
    private String getCode() {
        if (TextUtils.isEmpty(code1.getText()) ||
                TextUtils.isEmpty(code2.getText()) ||
                TextUtils.isEmpty(code3.getText()) ||
                TextUtils.isEmpty(code4.getText()) ||
                TextUtils.isEmpty(code5.getText()) ||
                TextUtils.isEmpty(code6.getText())) {
            Toast.makeText(this, getString(R.string.otp_6_digit), Toast.LENGTH_SHORT).show();
            return null;
        }
        return String.valueOf(code1.getText()) +
                code2.getText() +
                code3.getText() +
                code4.getText() +
                code5.getText() +
                code6.getText();
    }

    @Override
    public void loaderShow(boolean flag) {
        isEnableClicks(!flag);
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        Utils.showKeyboard(this);
        errorMain.setVisibility(View.VISIBLE);
        errorTv.setText(error);
    }

    @Override
    public void error404(String error) {
        Utils.showKeyboard(this);
        errorMain.setVisibility(View.VISIBLE);
        errorTv.setText(error);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 919 || resultCode == 919) {
            finish();
        }else if(requestCode == LoginPopupActivity.LOGIN_WITH_EMAIL){
            if(resultCode == Activity.RESULT_OK){
                boolean result;
                if (data != null) {
                    result = data.getBooleanExtra("result", false);
                    if (result) {
                        Log.e("####", OTPVerificationActivity.class.getName()+" "+String.valueOf(true));
                        passDataToPreviousActivity();
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }else if(requestCode == FirstActivity.FINISH_TO_LOGIN){
            if (resultCode == Activity.RESULT_OK) {
                boolean result;
                if (data != null) {
                    result = data.getBooleanExtra("finishToLogin", false);
                    if (result) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("finishToLogin", true);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            }
        }
    }

    private void passDataToPreviousActivity(){
        if(isPopUpLogin) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", true);
            setResult(Activity.RESULT_OK, returnIntent);
            onBackPressed();
        }
    }

    @Override
    public void success(boolean flag) {
        if (flag) {
            Intent intent = new Intent(OTPVerificationActivity.this, PasswordActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("check", check);
            intent.putExtra("code", getCode());
            intent.putExtra("forgot", forgot);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if(isPopUpLogin){
                intent.putExtra("isPopUpLogin", true);
                startActivityForResult(intent, LoginPopupActivity.LOGIN_WITH_EMAIL);
            }else {
                if(finishToLogin){
                    intent.putExtra("finishToLogin", true);
                    startActivityForResult(intent, FirstActivity.FINISH_TO_LOGIN);
                }else {
                    startActivityForResult(intent, 919);
                }
            }
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }
}
