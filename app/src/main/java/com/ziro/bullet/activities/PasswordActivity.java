package com.ziro.bullet.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.onboarding.OnBoardingActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.fragments.ForgotPasswordPopup;
import com.ziro.bullet.fragments.MessagePopup;
import com.ziro.bullet.interfaces.AccountLinkCallback;
import com.ziro.bullet.interfaces.PasswordInterface;
import com.ziro.bullet.interfaces.UserConfigCallback;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.presenter.FCMPresenter;
import com.ziro.bullet.presenter.PasswordPresenter;
import com.ziro.bullet.presenter.UserConfigPresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.SearchLayout;
import com.ziro.bullet.utills.URLSpanNoUnderline;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends BaseActivity implements PasswordInterface, UserConfigCallback {
    private static final String TAG = PasswordActivity.class.getSimpleName();
    private ProgressBar progress;
    private TextView next_text;
    private ImageView eye;
    private ImageView icon;
    private Animation[] aniRotate = null;
    private EditText password;
    private CardView next;
    private LinearLayout errorMain;
    private TextView forgot_password;
    private TextView error;
    private TextView for_;
    private TextView label;
    //    private ImageView dot2;
    private TextView validation_password;
    private LinearLayout terms_n_condition;
    private ImageView terms_check;
    private TextView terms;
    private PrefConfig mPrefConfig;
    private String mEmail;
    private String check;
    private TextView checkPassword;
    private PasswordPresenter presenter;
    private UserConfigPresenter configPresenter;
    private FCMPresenter fcmPresenter;
    private ForgotPasswordPopup popup;
    private boolean isPasswordVisible = false;
    private boolean isChecked = false;
    private boolean isLoading = false;
    private RelativeLayout tint;
    private boolean forgot = false;
    private String code = "";

    private boolean isPopUpLogin;
    private boolean finishToLogin;

    private AccountLinkCallback accountLinkCallback = new AccountLinkCallback() {
        @Override
        public void loaderShow(boolean flag) {
            if (flag) {
                progress.setVisibility(View.VISIBLE);
            } else {
                progress.setVisibility(View.GONE);
            }
        }

        @Override
        public void error(String errorT) {
            try{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    next.setEnabled(true);
                    next.setClickable(true);
                    errorMain.setVisibility(View.VISIBLE);
                    error.setText(errorT);
                }
            });}catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void error404(String errorT) {
            try{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    next.setEnabled(true);
                    next.setClickable(true);
                    errorMain.setVisibility(View.VISIBLE);
                    error.setText(errorT);
                }
            });}catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLinkSuccess(boolean status) {
            if (status) {
                if (check.equalsIgnoreCase("signup")) {
                    Intent intent = new Intent(PasswordActivity.this, ProfileNameActivity.class);
                    intent.putExtra("check", check);
                    intent.putExtra("forgot", forgot);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    if (isPopUpLogin) {
                        intent.putExtra("isPopUpLogin", true);
                        startActivityForResult(intent, LoginPopupActivity.LOGIN_WITH_EMAIL);
                    } else {
                        startActivity(intent);
                    }
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    configPresenter.getUserConfig(false);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null && getIntent().hasExtra("forgot")) {
            forgot = getIntent().getBooleanExtra("forgot", false);
            if (!forgot) {
                // new Components().blackStatusBar(this, "black");
                BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
        new Components().blackStatusBar(this, "black");
        setContentView(R.layout.activity_password);

        if (getIntent() != null && getIntent().hasExtra("isPopUpLogin")) {
            isPopUpLogin = getIntent().getBooleanExtra("isPopUpLogin", false);
        }
        if (getIntent() != null && getIntent().hasExtra("finishToLogin")) {
            finishToLogin = getIntent().getBooleanExtra("finishToLogin", false);
        }

        presenter = new PasswordPresenter(this, this);
        configPresenter = new UserConfigPresenter(this, this);
        fcmPresenter = new FCMPresenter(this);

        popup = new ForgotPasswordPopup(PasswordActivity.this, this);

        bindView();
        init();
        listener();
        checkData();
    }

    private void passDataToPreviousActivity() {
        if (isPopUpLogin) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", true);
            setResult(Activity.RESULT_OK, returnIntent);
            onBackPressed();
        }
    }

    private void init() {
        aniRotate = new Animation[]{
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise),
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out),
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in)};
        mPrefConfig = new PrefConfig(this);

    }

    private void startProgress() {
        tint.setVisibility(View.VISIBLE);
        icon.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        progress.startAnimation(aniRotate[2]);
    }

    private void stopProgress() {
        tint.setVisibility(View.GONE);
        progress.startAnimation(aniRotate[1]);
        progress.setVisibility(View.INVISIBLE);
        icon.setVisibility(View.VISIBLE);
    }

    private void checkData() {
        if (getIntent() != null) {
            mEmail = getIntent().getStringExtra("email");
            check = getIntent().getStringExtra("check");
            forgot = getIntent().getBooleanExtra("forgot", false);
            code = getIntent().getStringExtra("code");
            if (!TextUtils.isEmpty(check)) {
                switch (check) {
                    case "signin":
                        for_.setText(getString(R.string.for__) + " " + mEmail);
                        next_text.setText(getString(R.string.sign_in));
                        terms_n_condition.setVisibility(View.GONE);
                        validation_password.setVisibility(View.GONE);
                        forgot_password.setVisibility(View.VISIBLE);
                        label.setText(getString(R.string.whats_your_password));
                        eye.setVisibility(View.VISIBLE);
                        checkPassword.setVisibility(View.GONE);
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AnalyticsEvents.INSTANCE.logEvent(PasswordActivity.this,
                                        Events.SIGNIN_CLICK);
                                errorMain.setVisibility(View.INVISIBLE);
                                error.setText("");
                                Log.e("CALLXSXS", "EMAIL : " + mEmail);
                                Log.e("CALLXSXS", "password : " + password.getText().toString());
                                next.setEnabled(false);
                                next.setClickable(false);
                                presenter.normalLogin(mEmail, password.getText().toString());

//                                presenter.login(mEmail, password.getText().toString());
                            }
                        });

                        break;
                    case "signup":
                        for_.setText("");
                        next_text.setText(getString(R.string.sign_up));
                        forgot_password.setVisibility(View.GONE);
                        terms_n_condition.setVisibility(View.VISIBLE);
                        validation_password.setVisibility(View.GONE);
                        label.setText(getString(R.string.choose_your_password));
                        eye.setVisibility(View.GONE);
                        checkPassword.setVisibility(View.VISIBLE);

                        String bodyData = getString(R.string.checkbox_terms);
                        try {

                            SpannableStringBuilder spannableString = new SpannableStringBuilder(bodyData);
                            spannableString.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View v) {
                                    Utils.hideKeyboard(PasswordActivity.this, terms);
                                    Utils.openWebView(PasswordActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions));
                                }
                            }, 39, 55, 0);
                            spannableString.setSpan(new URLSpanNoUnderline(getString(R.string.terms_conditions)), 39, 55, 0);
                            spannableString.setSpan(
                                    new ForegroundColorSpan(getResources().getColor(R.color.white)),
                                    39, 55,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View v) {
                                    Utils.hideKeyboard(PasswordActivity.this, terms);
                                    Utils.openWebView(PasswordActivity.this, "https://www.newsinbullets.app/privacy?header=false", getString(R.string.privacy_policy));
                                }
                            }, 96, 111, 0);
                            spannableString.setSpan(new URLSpanNoUnderline(getString(R.string.privacy_policy)), 96, 111, 0);
                            spannableString.setSpan(
                                    new ForegroundColorSpan(getResources().getColor(R.color.white)),
                                    96, 111,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            terms.setText(spannableString);
                            terms.setMovementMethod(LinkMovementMethod.getInstance());

                        } catch (Exception e) {
                            e.printStackTrace();
                            terms.setText(bodyData);
                            terms.setOnClickListener(v -> Utils.openWebView(PasswordActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions)));
                        }

                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isChecked) {
                                    if (password.getText().length() < 8) {
                                        errorMain.setVisibility(View.VISIBLE);
                                        error(getString(R.string.password_must_be_at_least_8_characters));
                                        return;
                                    }
                                    AnalyticsEvents.INSTANCE.logEvent(PasswordActivity.this,
                                            Events.SIGNUP_CLICK);
                                    errorMain.setVisibility(View.INVISIBLE);
                                    error.setText("");
                                    Log.e("CALLXSXS", "EMAIL : " + mEmail);
                                    Log.e("CALLXSXS", "password : " + password.getText().toString());
                                    next.setEnabled(false);
                                    next.setClickable(false);
                                    presenter.register(mEmail.trim(), password.getText().toString().trim(), code, forgot, isChecked);
                                } else {
                                    error(getString(R.string.accept_terms_and_conditions_to_continue));
                                }
                            }
                        });
                        break;
                    case "forgot_password":
                    case "forgot_password_from_settings":
                        for_.setText("");
                        next_text.setText(getString(R.string.continuee));
                        forgot_password.setVisibility(View.GONE);
                        terms_n_condition.setVisibility(View.GONE);
                        validation_password.setVisibility(View.GONE);
                        label.setText(getString(R.string.choose_your_password));
                        eye.setVisibility(View.GONE);
                        checkPassword.setVisibility(View.VISIBLE);

                        String bodyData2 = getString(R.string.checkbox_terms);
                        try {

                            SpannableStringBuilder spannableString = new SpannableStringBuilder(bodyData2);
                            spannableString.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View v) {
                                    Utils.hideKeyboard(PasswordActivity.this, terms);
                                    Utils.openWebView(PasswordActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions));
                                }
                            }, 39, 55, 0);
                            spannableString.setSpan(new URLSpanNoUnderline(getString(R.string.terms_conditions)), 39, 55, 0);
                            spannableString.setSpan(
                                    new ForegroundColorSpan(getResources().getColor(R.color.white)),
                                    39, 55,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View v) {
                                    Utils.hideKeyboard(PasswordActivity.this, terms);
                                    Utils.openWebView(PasswordActivity.this, "https://www.newsinbullets.app/privacy?header=false", getString(R.string.privacy_policy));
                                }
                            }, 96, 111, 0);
                            spannableString.setSpan(new URLSpanNoUnderline(getString(R.string.privacy_policy)), 96, 111, 0);
                            spannableString.setSpan(
                                    new ForegroundColorSpan(getResources().getColor(R.color.white)),
                                    96, 111,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            terms.setText(spannableString);
                            terms.setMovementMethod(LinkMovementMethod.getInstance());

                        } catch (Exception e) {
                            e.printStackTrace();
                            terms.setText(bodyData2);
                            terms.setOnClickListener(v -> Utils.openWebView(PasswordActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions)));
                        }

                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                if (isChecked) {
                                if (password.getText().length() < 8) {
                                    errorMain.setVisibility(View.VISIBLE);
                                    error(getString(R.string.password_must_be_at_least_8_characters));
                                    return;
                                }
                                AnalyticsEvents.INSTANCE.logEvent(PasswordActivity.this,
                                        Events.SIGNUP_CLICK);
                                errorMain.setVisibility(View.INVISIBLE);
                                error.setText("");
                                Log.e("CALLXSXS", "EMAIL : " + mEmail);
                                Log.e("CALLXSXS", "password : " + password.getText().toString());
                                next.setEnabled(false);
                                next.setClickable(false);
                                presenter.register(mEmail.trim(), password.getText().toString().trim(), code, forgot, isChecked);
//                                } else {
//                                    error(getString(R.string.accept_terms_and_conditions_to_continue));
//                                }
                            }
                        });
                        break;
                    default:
                }
            }
        }
    }

    private void listener() {
        findViewById(R.id.back).setOnClickListener(v -> {
            if (!isLoading)
                onBackPressed();
        });
        findViewById(R.id.help).setOnClickListener(v -> {
            Utils.hideKeyboard(this, password);
//            Utils.openHelpView(this, BuildConfig.HelpUrl, "Help");
            Intent intent = new Intent(this, HelpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
        eye.setOnClickListener(v -> {
            if (!isPasswordVisible) {
                isPasswordVisible = true;
                password.setTransformationMethod(null);
                eye.setImageResource(R.drawable.ic_look_enabled);
            } else {
                isPasswordVisible = false;
                password.setTransformationMethod(new PasswordTransformationMethod());
                eye.setImageResource(R.drawable.ic_look_disable);
            }
            password.setSelection(password.getText().length());
        });

        terms_n_condition.setOnClickListener(v -> {
            if (!isChecked) {
                isChecked = true;
                terms_check.setImageResource(R.drawable.ic_terms_check_password);
            } else {
                isChecked = false;
                terms_check.setImageResource(R.drawable.ic_terms_uncheck_password);
            }
        });

        forgot_password.setOnClickListener(v -> {
            Utils.hideKeyboard(PasswordActivity.this, password);
            popup.showDialog();
        });

//        final ColorStateList green = ColorStateList.valueOf(getResources().getColor(R.color.play_btn));
//        final ColorStateList grey = ColorStateList.valueOf(getResources().getColor(R.color.grey));
//        password.setBackgroundTintList(green);
        next.setBackground(getResources().getDrawable(R.drawable.shape_with_stroke));
        password.setTextColor(getResources().getColor(R.color.white));
        password.requestFocus();
        next.setEnabled(false);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                errorMain.setVisibility(View.INVISIBLE);
                error.setText("");
                if (s.length() == 0) {
                    checkPassword.setText("");
                    next.setEnabled(false);
                    if (check.equalsIgnoreCase("signin")) {
                        password.setTextColor(getResources().getColor(R.color.white));
//                        icon.setColorFilter(getResources().getColor(R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                } else {
                    if (!TextUtils.isEmpty(password.getText()) && password.getText().length() >= 1) {

                        if (check.equalsIgnoreCase("signup") || check.equalsIgnoreCase("forgot_password")) {
                            if (password.getText().length() < 8) {
                                errorMain.setVisibility(View.VISIBLE);
                                error.setText(getString(R.string.password_must_be_at_least_8_characters));
                            } else {
                                errorMain.setVisibility(View.INVISIBLE);
                                error.setText("");
                            }
                            String check = Utils.checkString(password.getText().toString());
                            String txt = "";
                            if (!TextUtils.isEmpty(check)) {
                                switch (check) {
                                    case "Weak":
                                        txt = getString(R.string.weak);
                                        checkPassword.setTextColor(getResources().getColor(R.color.yellow));
                                        break;
                                    case "Strong":
                                        txt = getString(R.string.strong);
                                        checkPassword.setTextColor(getResources().getColor(R.color.green2));
                                        break;
                                    case "Medium":
                                        txt = getString(R.string.medium);
                                        checkPassword.setTextColor(getResources().getColor(R.color.orange));
                                        break;
                                }
                                checkPassword.setText(txt + "");
                            }
                        }
                        next.setEnabled(true);
//                        if (check.equalsIgnoreCase("signin")) {
//                            password.setTextColor(getResources().getColor(R.color.play_btn));
//                            icon.setColorFilter(getResources().getColor(R.color.play_btn), android.graphics.PorterDuff.Mode.SRC_IN);
//                        }
                    } else {
                        next.setEnabled(false);
                        if (check.equalsIgnoreCase("signin")) {
                            password.setTextColor(getResources().getColor(R.color.white));
//                            icon.setColorFilter(getResources().getColor(R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.showKeyboard(this);
        SearchLayout.setSearchActivity(this);
    }

    private void bindView() {
        eye = findViewById(R.id.eye);
        checkPassword = findViewById(R.id.checkPassword);
        next_text = findViewById(R.id.button_text);
        icon = findViewById(R.id.icon);
        progress = findViewById(R.id.progress);
        error = findViewById(R.id.error);
        errorMain = findViewById(R.id.errorMain);
        terms_check = findViewById(R.id.terms_check);
        terms = findViewById(R.id.terms);
        terms_n_condition = findViewById(R.id.terms_n_condition);
        forgot_password = findViewById(R.id.forgot_password);
        validation_password = findViewById(R.id.validation_password);
//        dot2 = findViewById(R.id.dot2);
        label = findViewById(R.id.whats_ur_password);
        for_ = findViewById(R.id.for_);
        password = findViewById(R.id.password);
        next = findViewById(R.id.button);
        tint = findViewById(R.id.tint);

        Utils.setCursorColor(password, getResources().getColor(R.color.login_btn_border));
//        password.setHint(getString(R.string.your_password));
        eye.setVisibility(View.VISIBLE);
        checkPassword.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void loaderShow(boolean flag) {
        isLoading = flag;
        try{
        runOnUiThread(() -> {
            if (flag) {
                startProgress();
            } else {
                stopProgress();
            }
        });}catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void error(String err) {
        try{
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                next.setEnabled(true);
                next.setClickable(true);
                errorMain.setVisibility(View.VISIBLE);
                error.setText(err);
            }
        });}catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void error404(String err) {
        try{
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                next.setEnabled(true);
                next.setClickable(true);
                errorMain.setVisibility(View.VISIBLE);
                error.setText(err);
            }
        });}catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUserConfigSuccess(UserConfigModel userConfigModel) {

        if (!TextUtils.isEmpty(check)) {
            if (check.equalsIgnoreCase("forgot_password")) {
                Intent intent = new Intent(PasswordActivity.this, MainActivityNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Utils.hideKeyboard(this, password);
                intent.putExtra("check", check);
                intent.putExtra("forgot", forgot);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                if (!isPopUpLogin) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    passDataToPreviousActivity();
                }
            } else {
                try {

                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();

                            Log.d(TAG, "onComplete: fcm  == " + token);

                            mPrefConfig.setFirebaseToken(token);
                            fcmPresenter.sentTokenToServer(mPrefConfig);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(mPrefConfig.isLanguagePushedToServer())) {
                    presenter.selectRegion(mPrefConfig.getSelectedRegion(), mPrefConfig);
                    presenter.selectLanguage(mPrefConfig.isLanguagePushedToServer(), mPrefConfig);
                }

                Utils.hideKeyboard(PasswordActivity.this, password);


                //check profile setup is complete
                if (userConfigModel.getUser() != null
                        && userConfigModel.getUser().isSetup()
                ) {
                    if (userConfigModel.isOnboarded()) {
                        loadReels();
                    } else {
                        Intent intent = new Intent(PasswordActivity.this, OnBoardingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        if (!isPopUpLogin) {
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            passDataToPreviousActivity();
                        }
                    }
                } else {

                    //is not guest user
                    if (userConfigModel.getUser() != null
                            && userConfigModel.getUser().isGuestValid()
                    ) {
                        Intent intent1 = new Intent(PasswordActivity.this, ProfileNameActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        finish();
                        startActivity(intent1);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        if (userConfigModel.isOnboarded()) {
                            loadReels();
                        } else {
                            Intent intent = new Intent(PasswordActivity.this, OnBoardingActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            if (!isPopUpLogin) {
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                passDataToPreviousActivity();
                            }
                        }
                    }
                }
            }
        }
    }

    private void gotoHome() {
        loaderShow(false);
        Intent intent = new Intent(PasswordActivity.this, MainActivityNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        updateWidget();

        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        if (!isPopUpLogin) {
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else {
            passDataToPreviousActivity();
        }
    }

    private void loadReels() {
        loaderShow(true);
        Call<ReelResponse> call = ApiClient.getInstance(this)
                .getApi()
                .newsReel(
                        "Bearer " + mPrefConfig.getAccessToken(), "", "", "", Constants.REELS_FOR_YOU, BuildConfig.DEBUG
                );

        call.enqueue(new Callback<ReelResponse>() {
            @Override
            public void onResponse(Call<ReelResponse> call, Response<ReelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DbHandler dbHandler = new DbHandler(PasswordActivity.this);
                    dbHandler.insertReelList("ReelsList", new Gson().toJson(response.body()));
                }
                gotoHome();
            }

            @Override
            public void onFailure(Call<ReelResponse> call, Throwable t) {
                gotoHome();
            }
        });
    }

    private void updateWidget() {
        try {
            if (AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, CollectionWidget.class)).length > 0) {
                Intent updateWidget = new Intent(this, CollectionWidget.class);
                updateWidget.setAction("update_widget");
                PendingIntent pending = PendingIntent.getBroadcast(this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                pending.send();
            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void success(boolean flag) {
        if (flag) {
            Intent intent = null;
            if (!TextUtils.isEmpty(check)) {
                if (check.equalsIgnoreCase("forgot_password") || check.equalsIgnoreCase("signin")) {
                    if (check.equalsIgnoreCase("signin") && isPopUpLogin) {
                        if (mPrefConfig.getNewUserFlag()) {
                            configPresenter.linkAccount(accountLinkCallback);
                        } else {
                            if (check.equalsIgnoreCase("signup")) {
                                Intent intent1 = new Intent(PasswordActivity.this, ProfileNameActivity.class);
                                intent1.putExtra("check", check);
                                intent1.putExtra("forgot", forgot);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                if (isPopUpLogin) {
                                    intent1.putExtra("isPopUpLogin", true);
                                    startActivityForResult(intent1, LoginPopupActivity.LOGIN_WITH_EMAIL);
                                } else {
                                    if (finishToLogin) {
                                        intent1.putExtra("finishToLogin", true);
                                        startActivityForResult(intent1, FirstActivity.FINISH_TO_LOGIN);
                                    } else {
                                        startActivity(intent1);
                                    }
                                }
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                configPresenter.getUserConfig(false);
                            }
                        }
                    } else {
                        configPresenter.getUserConfig(false);
                    }
                } else if (check.equalsIgnoreCase("forgot_password_from_settings")) {
                    Utils.hideKeyboard(this, password);
                    setResult(919);
                    finish();
                } else {
                    if (isPopUpLogin) {
                        if (mPrefConfig.getNewUserFlag()) {
                            configPresenter.linkAccount(accountLinkCallback);
                        } else {
                            if (check.equalsIgnoreCase("signup")) {
                                Intent intent2 = new Intent(PasswordActivity.this, ProfileNameActivity.class);
                                intent2.putExtra("check", check);
                                intent2.putExtra("forgot", forgot);
                                intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                if (isPopUpLogin) {
                                    intent2.putExtra("isPopUpLogin", true);
                                    startActivityForResult(intent2, LoginPopupActivity.LOGIN_WITH_EMAIL);
                                } else {
                                    if (finishToLogin) {
                                        intent2.putExtra("finishToLogin", true);
                                        startActivityForResult(intent2, FirstActivity.FINISH_TO_LOGIN);
                                    } else {
                                        startActivity(intent2);
                                    }
                                }
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                configPresenter.getUserConfig(false);
                            }
                        }
                    } else {
                        intent = new Intent(PasswordActivity.this, ProfileNameActivity.class);
                        intent.putExtra("check", check);
                        intent.putExtra("forgot", forgot);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                        if (isPopUpLogin) {
                            intent.putExtra("isPopUpLogin", true);
                            startActivityForResult(intent, LoginPopupActivity.LOGIN_WITH_EMAIL);
                        } else {
                            if (finishToLogin) {
                                intent.putExtra("finishToLogin", true);
                                startActivityForResult(intent, FirstActivity.FINISH_TO_LOGIN);
                            } else {
                                startActivity(intent);
                            }
                        }
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }
            }
        }
    }

    @Override
    public void reset() {
        forgot = true;
        presenter.forgotPassword(mEmail);
    }

    @Override
    public void resetSuccess(String str) {
        MessagePopup popup = new MessagePopup(this, mEmail, getString(R.string.password_reset),
                getString(R.string.a_reset_password_link_has_been_sent_to) + mEmail, getString(R.string.ok));
        popup.showDialog(v -> {
            Intent intent = new Intent(PasswordActivity.this, OTPVerificationActivity.class);
            intent.putExtra("email", mEmail);
            intent.putExtra("forgot", forgot);
            intent.putExtra("check", "forgot_password");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if (isPopUpLogin) {
                intent.putExtra("isPopUpLogin", true);
                startActivityForResult(intent, LoginPopupActivity.LOGIN_WITH_EMAIL);
            } else {
                startActivity(intent);
            }
            finishAfterTransition();
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginPopupActivity.LOGIN_WITH_EMAIL) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result;
                if (data != null) {
                    result = data.getBooleanExtra("result", false);
                    if (result) {
                        Log.e("####", PasswordActivity.class.getName() + " " + String.valueOf(true));
                        isPopUpLogin = true;
                        passDataToPreviousActivity();
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        } else if (requestCode == FirstActivity.FINISH_TO_LOGIN) {
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
