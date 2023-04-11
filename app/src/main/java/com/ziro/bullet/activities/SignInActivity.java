package com.ziro.bullet.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.SignInInterface;
import com.ziro.bullet.presenter.OTPVerificationPresenter;
import com.ziro.bullet.presenter.SignInPresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.SearchLayout;
import com.ziro.bullet.utills.Utils;

public class SignInActivity extends BaseActivity implements SignInInterface {

    private ProgressBar progress;
    private Animation[] aniRotate = null;
    private TextView error;
    private TextView next_text;
    private EditText email;
    private LinearLayout errorMain;
    private CardView next;
    private ImageView arrowIcon;
    private RelativeLayout root;
    private ImageView icon;
    private String check;
    private SignInPresenter presenter;
    private int defaultBtnWidth = 0;
    private PrefConfig prefConfig;
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
        new Components().blackStatusBar(this, "black");
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_sign_in);
        prefConfig = new PrefConfig(this);
        presenter = new SignInPresenter(this, this);

        if(getIntent() != null && getIntent().hasExtra("isPopUpLogin")){
            isPopUpLogin = getIntent().getBooleanExtra("isPopUpLogin", false);
        }

        if(getIntent() != null && getIntent().hasExtra("finishToLogin")){
            finishToLogin = getIntent().getBooleanExtra("finishToLogin", false);
        }

        bindview();
        listener();
        checkData();
        Utils.saveSystemThemeAsDefault(this, prefConfig);
    }

    private void passDataToPreviousActivity(){
        if(isPopUpLogin) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", true);
            setResult(Activity.RESULT_OK, returnIntent);
            onBackPressed();
        }
    }

    private void checkData() {
        aniRotate = new Animation[]{
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise),
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out),
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in)};
        if (getIntent() != null) {
            check = getIntent().getStringExtra("check");
        }
    }

    private void listener() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.help).setOnClickListener(v -> {
            Utils.hideKeyboard(this, email);
//            Utils.openHelpView(this, BuildConfig.HelpUrl, "Help");
            Intent intent = new Intent(this, HelpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email.getText()) && Utils.isValidEmail(email.getText().toString())) {
                    Log.e("CALLXSXS", "state: " + prefConfig.getLoginState());
                    switch (prefConfig.getLoginState()) {
                        case 1:
                            presenter.checkEmail(email.getText().toString());
                            break;
                        case 2:
                            check = "signup";
                            Intent intent = new Intent(SignInActivity.this, PasswordActivity.class);
                            intent.putExtra("email", email.getText().toString());
                            intent.putExtra("check", check);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            if (isPopUpLogin) {
                                intent.putExtra("isPopUpLogin", true);
                                startActivityForResult(intent, LoginPopupActivity.LOGIN_WITH_EMAIL);
                            } else {
                                if(finishToLogin){
                                    intent.putExtra("finishToLogin", true);
                                    startActivityForResult(intent, FirstActivity.FINISH_TO_LOGIN);
                                }else {
                                    startActivity(intent);
                                }
                            }
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            break;
                        case 3:
                            check = "signin";
                            Intent intent2 = new Intent(SignInActivity.this, PasswordActivity.class);
                            intent2.putExtra("email", email.getText().toString());
                            intent2.putExtra("check", check);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            if (isPopUpLogin) {
                                intent2.putExtra("isPopUpLogin", true);
                                startActivityForResult(intent2, LoginPopupActivity.LOGIN_WITH_EMAIL);
                            } else {
                                startActivity(intent2);
                            }
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            break;
                    }
                } else {
                    Toast.makeText(SignInActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ColorStateList green = ColorStateList.valueOf(getResources().getColor(R.color.login_btn_border));
        final ColorStateList grey = ColorStateList.valueOf(getResources().getColor(R.color.grey));
        email.setBackgroundTintList(green);
        next.setBackground(getResources().getDrawable(R.drawable.shape_with_stroke));
        email.requestFocus();
        next.setEnabled(true);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorMain.setVisibility(View.INVISIBLE);
                arrowIcon.setVisibility(View.GONE);
                error.setText("");
                next_text.setText(getString(R.string.next));
                next.setBackground(getResources().getDrawable(R.drawable.shape_with_stroke));

                if (next.getLayoutParams().width > defaultBtnWidth) {
                    next.getLayoutParams().width = defaultBtnWidth;
                    next.requestLayout();
                }

                if (s.length() == 0) {
//                    next.setEnabled(false);
                    email.setTextColor(getResources().getColor(R.color.white));
//                    icon.setColorFilter(getResources().getColor(R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    prefConfig.setPrefLoginState(0);
                } else {
                    if (!TextUtils.isEmpty(email.getText()) && Utils.isValidEmail(email.getText().toString())) {
//                        next.setEnabled(true);
                        email.setTextColor(green);
//                        icon.setColorFilter(getResources().getColor(R.color.play_btn), android.graphics.PorterDuff.Mode.SRC_IN);
                        prefConfig.setPrefLoginState(1);
                    } else {
//                        icon.setColorFilter(getResources().getColor(R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                        email.setTextColor(getResources().getColor(R.color.white));
//                        next.setEnabled(false);
                        prefConfig.setPrefLoginState(0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void startProgress() {
        icon.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        progress.startAnimation(aniRotate[2]);
    }

    private void stopProgress() {
        progress.startAnimation(aniRotate[1]);
        progress.setVisibility(View.INVISIBLE);
        icon.setVisibility(View.VISIBLE);
    }

    private void bindview() {
        arrowIcon = findViewById(R.id.arrowIcon);
        icon = findViewById(R.id.icon);
        progress = findViewById(R.id.progress);
        root = findViewById(R.id.root);
        next_text = findViewById(R.id.button_text);
        errorMain = findViewById(R.id.errorMain);
        email = findViewById(R.id.email);
        next = findViewById(R.id.button);
        error = findViewById(R.id.error);
        defaultBtnWidth = next.getLayoutParams().width;
        Utils.setCursorColor(email, getResources().getColor(R.color.read_more));
        next_text.setText(getString(R.string.next));
        email.setHint(getString(R.string.your_email));
    }

    @Override
    public void onBackPressed() {
        Utils.hideKeyboard(this, email);
        finishAfterTransition();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            startProgress();
        } else {
            stopProgress();
        }
    }

    @Override
    public void error(String err) {
        Log.e("CALLXSXS", "err: " + new Gson().toJson(err));
        errorMain.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(err)) {
            error.setText(getString(R.string.server_error));
        } else
            error.setText(err);
    }

    @Override
    public void error404(String err) {
        Log.e("CALLXSXS", "err: 2 : " + new Gson().toJson(err));
        errorMain.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(err)) {
            error.setText(getString(R.string.server_error));
        } else
            error.setText(err);
    }

    @Override
    public void success(boolean flag) {
        Log.e("CALLXSXS", "flag:12: " + flag);

        prefConfig.setNewUserFlag(!flag);
        if (flag) {
            //goto enter password
            Intent intent = new Intent(SignInActivity.this, PasswordActivity.class);
            intent.putExtra("email", email.getText().toString());
            intent.putExtra("check", "signin");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if (isPopUpLogin) {
                intent.putExtra("isPopUpLogin", true);
                startActivityForResult(intent, LoginPopupActivity.LOGIN_WITH_EMAIL);
            } else {
                startActivity(intent);
            }
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else {
            //goto create password
            Intent intent = new Intent(SignInActivity.this, OTPVerificationActivity.class);
            intent.putExtra("email", email.getText().toString());
            intent.putExtra("check", "signup");
            intent.putExtra("forgot", false);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if (isPopUpLogin) {
                intent.putExtra("isPopUpLogin", true);
                startActivityForResult(intent, LoginPopupActivity.LOGIN_WITH_EMAIL);
            } else {
                if(finishToLogin) {
                    intent.putExtra("finishToLogin", true);
                    startActivityForResult(intent, FirstActivity.FINISH_TO_LOGIN);
                }else {
                    startActivity(intent);
                }
            }
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LoginPopupActivity.LOGIN_WITH_EMAIL){
            if(resultCode == Activity.RESULT_OK){
                boolean result;
                if (data != null) {
                    result = data.getBooleanExtra("result", false);
                    if (result) {
                        Log.e("####", String.valueOf(true));
                        isPopUpLogin = true;
                        passDataToPreviousActivity();
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        } else if(requestCode == FirstActivity.FINISH_TO_LOGIN){
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
}
