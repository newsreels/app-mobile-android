package com.ziro.bullet.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.ziro.bullet.R;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.interfaces.ApiCallbacks;
import com.ziro.bullet.interfaces.ProfileApiCallback;
import com.ziro.bullet.onboarding.ui.categories.OnBoardingBottomSheet;
import com.ziro.bullet.presenter.ProfilePresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

public class ProfileNameActivity extends BaseActivity {

    private RelativeLayout back;
    private EditText first_name;
    //    private ChatEditText last_name;
    private EditText username;
    private TextView countFn;
    private RelativeLayout button_color;
    private RelativeLayout progressMain;
    //    private TextView countLn;
    private TextView count_username;
    private TextView button_text;
    private ImageView icon;
    private ProgressBar progress;
    private CardView cvContinue;
    private boolean isPopUpLogin;
    private boolean isUsernameValid;
    private String validUsername;
    private boolean finishToLogin;

    private ProfilePresenter profilePresenter;

    private boolean onboard = false;
    private ProfileApiCallback profileApiCallback = new ProfileApiCallback() {
        @Override
        public void loaderShow(boolean flag) {
            if (flag) progressMain.setVisibility(View.VISIBLE);
            else progressMain.setVisibility(View.GONE);
        }

        @Override
        public void error(String error, String img) {

        }

        @Override
        public void success() {
            if (isPopUpLogin) {
                passDataToPreviousActivity();
            } else {
                OnBoardingBottomSheet onBoardingBottomSheet = new OnBoardingBottomSheet();
                onBoardingBottomSheet.updateFragManager(getSupportFragmentManager());
                onBoardingBottomSheet.show();
//                updateWidget();
//                Intent intent = null;
//                if (onboard) {
//                    intent = new Intent(ProfileNameActivity.this, MainActivityNew.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                } else {
//                    intent = new Intent(ProfileNameActivity.this, OnBoardingActivity.class);
//                    intent.putExtra("main", "main");
//                }
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        }
    };
    private String TAG = "TAGG";

    @Override
    protected void onResume() {
        super.onResume();
//        Utils.showKeyboard(this);
//        SearchLayout.setSearchActivity(this);
    }

    @Override
    public void onBackPressed() {
        if (finishToLogin) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("finishToLogin", true);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("finishToLogin", false);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            super.onBackPressed();
        }
//        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Components().blackStatusBar(this, "black");
//        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_profile_name);

        profilePresenter = new ProfilePresenter(this, profileApiCallback);

        if (getIntent() != null && getIntent().hasExtra("isPopUpLogin")) {
            isPopUpLogin = getIntent().getBooleanExtra("isPopUpLogin", false);
        }
        if (getIntent() != null && getIntent().hasExtra("finishToLogin")) {
            finishToLogin = getIntent().getBooleanExtra("finishToLogin", false);
        }
        if (getIntent() != null && getIntent().hasExtra("onboard")) {
            onboard = getIntent().getBooleanExtra("onboard", false);
        }

        bindView();
        setListener();
        setData();
    }

    private void passDataToPreviousActivity() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void setData() {
        if (getIntent() != null) {
            String firstName = getIntent().getStringExtra("first_name");
            String lastName = getIntent().getStringExtra("last_name");
            if (!TextUtils.isEmpty(firstName)) {
                first_name.setText(firstName + " " + lastName);
            }
        }
//        username.requestFocus();
    }

    private void setListener() {
        back.setOnClickListener(v -> onBackPressed());
        cvContinue.setOnClickListener(v -> {
            if (TextUtils.isEmpty(first_name.getText().toString().trim()) || first_name.getText().toString().trim().length() < 3) {
                Utils.showPopupMessageWithCloseButton(
                        this,
                        2000,
                        getString(R.string.profile_name_error),
                        true
                );
                return;
            }

//            if (!isUsernameValid) {
//                Utils.showPopupMessageWithCloseButton(
//                        this,
//                        2000,
//                        getString(R.string.username_not_valid),
//                        true
//                );
//                return;
//            }

            if (profilePresenter != null)
                profilePresenter.updateProfile(first_name.getText().toString().trim(), null, null, true, validUsername);

        });

        first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkButton();
                if (s != null) {
                    countFn.setText(s.length() + "/25");
                    if (s.length() == 0) {
                        first_name.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkButton();
                if (s != null) {
                    count_username.setText(s.length() + "/25");
//                    if (s.length() == 0) {
//                        username.requestFocus();
//                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                OAuthAPIClient.cancelAll();
                isUsernameValid = false;
                String usernameCopy = s.toString();
                if (usernameCopy.length() > 2) {
                    profilePresenter.checkUsername(usernameCopy, new ApiCallbacks() {
                        @Override
                        public void loaderShow(boolean flag) {
                            if (flag) {
                                setUsernameLoading();
                            }
                        }

                        @Override
                        public void error(String error) {
                            Toast.makeText(ProfileNameActivity.this, error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void error404(String error) {

                        }

                        @Override
                        public void success(Object response) {
                            if (response instanceof Boolean) {
                                if ((Boolean) response) {
                                    validUsername = usernameCopy;
                                    isUsernameValid = true;
                                    setUsernameValidIcon();
                                } else {
                                    isUsernameValid = false;
                                    setUsernameInvalidIcon();
                                }
                            } else {
                                isUsernameValid = false;
                                setUsernameInvalidIcon();
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkButton() {
        if (first_name.getText().length() >= 3) {
            cvContinue.setEnabled(true);
            button_color.setBackgroundColor(
                    (ContextCompat.getColor(
                            this,
                            R.color.theme_color_1
                    ))
            );
            button_text.setTextColor(
                    (ContextCompat.getColor(
                            this,
                            R.color.white
                    ))
            );
        } else {
            cvContinue.setEnabled(true);
            button_color.setBackgroundColor(
                    (ContextCompat.getColor(
                            this,
                            R.color.disable_btn
                    ))
            );
            button_text.setTextColor(
                    (ContextCompat.getColor(
                            this,
                            R.color.edittextHint
                    ))
            );
        }
    }

    private void setUsernameValidIcon() {
        progress.setVisibility(View.INVISIBLE);
        icon.setImageResource(R.drawable.ic_username_check);
        icon.setVisibility(View.VISIBLE);
        cvContinue.setEnabled(true);
        button_color.setBackgroundColor(
                (ContextCompat.getColor(
                        this,
                        R.color.theme_color_1
                ))
        );
        button_text.setTextColor(
                (ContextCompat.getColor(
                        this,
                        R.color.white
                ))
        );
    }

    private void setUsernameInvalidIcon() {
        progress.setVisibility(View.INVISIBLE);
        icon.setImageResource(R.drawable.ic_username_cross);
        icon.setVisibility(View.VISIBLE);
        cvContinue.setEnabled(true);
        button_color.setBackgroundColor(
                (ContextCompat.getColor(
                        this,
                        R.color.disable_btn
                ))
        );
        button_text.setTextColor(
                (ContextCompat.getColor(
                        this,
                        R.color.edittextHint
                ))
        );
    }

    private void setUsernameLoading() {
        progress.setVisibility(View.VISIBLE);
        icon.setImageResource(R.drawable.ic_username_cross);
        icon.setVisibility(View.GONE);
        cvContinue.setEnabled(true);
        button_color.setBackgroundColor(
                (ContextCompat.getColor(
                        this,
                        R.color.disable_btn
                ))
        );
        button_text.setTextColor(
                (ContextCompat.getColor(
                        this,
                        R.color.edittextHint
                ))
        );
    }

    private void bindView() {
        back = findViewById(R.id.back);
        button_color = findViewById(R.id.button_color);
        first_name = findViewById(R.id.first_name);

        username = findViewById(R.id.username);
        icon = findViewById(R.id.icon);
        progressMain = findViewById(R.id.progress);
        progress = findViewById(R.id.progressSmall);
        countFn = findViewById(R.id.countFn);

        count_username = findViewById(R.id.count_username);
        cvContinue = findViewById(R.id.button);
        button_text = findViewById(R.id.button_text);
        button_text.setText(getString(R.string.continuee));
//        button.setBackground(getResources().getDrawable(R.drawable.shape_with_stroke));
        cvContinue.setEnabled(true);
        button_color.setBackgroundColor(
                (ContextCompat.getColor(
                        this,
                        R.color.disable_btn
                ))
        );
        button_text.setTextColor(
                (ContextCompat.getColor(
                        this,
                        R.color.edittextHint
                ))
        );
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
                        Log.e("####", ProfileNameActivity.class.getName() + " " + String.valueOf(true));
                        isPopUpLogin = true;
                        passDataToPreviousActivity();
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
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
}