package com.ziro.bullet.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.ziro.bullet.R;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.auth.OAuthResponse;
import com.ziro.bullet.auth.TokenGenerator;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.fragments.ForgotPasswordSettingPopup;
import com.ziro.bullet.fragments.MessagePopup;
import com.ziro.bullet.interfaces.PasswordInterface;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.CustomTypefaceSpan;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeAccountInfoActivity extends BaseActivity implements PasswordInterface {

    private static final String TAG = ChangeAccountInfoActivity.class.getSimpleName();
    private static final String TYPE_EMAIL = "change_email";
    private static final String TYPE_PASSWORD = "change_pwd";

    private RelativeLayout mIvBack;
    private ImageView eye1, eye2, eye3;
    private TextView mTvTitle, forgot_password;
    private TextView mBtnSave;
    private LinearLayout mLlEmail, mLlPwd;
    private EditText mEtChangeEmailNewEmail, mEtChangeEmailCurrPwd;
    private EditText mEtChangePwdCurrPwd, mEtChangePwdNewPwd, mEtChangePwdConfirmPwd;
    private ImageView mLoader;
    private RelativeLayout mRlprogress;

    private PrefConfig mPrefConfig;
    private String type;
    private boolean isPassword1Visible = false;
    private boolean isPassword2Visible = false;
    private boolean isPassword3Visible = false;
    private ForgotPasswordSettingPopup popup = null;
    private PictureLoadingDialog loadingDialog;


    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkAppModeColor(this, false);
        //checkTheme();
        Utils.showKeyboard(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account_info);
        mPrefConfig = new PrefConfig(this);
        bindViews();
        init();
    }

    private void checkTheme() {
        if (mPrefConfig.getAppTheme() != null && mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
            //if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            //}
        } else {
            //if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            //}
        }
    }

    private void init() {
        type = getIntent().getStringExtra("type");
        if (type != null && type.equals(TYPE_EMAIL)) {
            mLlEmail.setVisibility(View.VISIBLE);
            mLlPwd.setVisibility(View.GONE);
            mEtChangeEmailNewEmail.requestFocus();
            mTvTitle.setText(getString(R.string.change_email));
            forgot_password.setVisibility(View.GONE);
        } else {
            forgot_password.setVisibility(View.VISIBLE);
            mLlEmail.setVisibility(View.GONE);
            mLlPwd.setVisibility(View.VISIBLE);
            mEtChangePwdCurrPwd.requestFocus();
            mTvTitle.setText(getString(R.string.change_password));
            popup = new ForgotPasswordSettingPopup(ChangeAccountInfoActivity.this, this);
        }
        mIvBack.setOnClickListener(view -> onBackPressed());
        mBtnSave.setOnClickListener(view -> saveCredentials());
        forgot_password.setOnClickListener(v -> {
            Utils.hideKeyboard(ChangeAccountInfoActivity.this, forgot_password);
            popup.showDialog(mPrefConfig.getUserEmail());
        });

        mEtChangeEmailNewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newEmail = mEtChangeEmailNewEmail.getText().toString().trim();
                String curPass = mEtChangeEmailCurrPwd.getText().toString().trim();
                if (newEmail.length() > 0
                        && Utils.isValidEmail(newEmail)
                        && curPass.length() > 3) {

                    DrawableCompat.setTint(mBtnSave.getBackground(), ContextCompat.getColor(ChangeAccountInfoActivity.this, R.color.theme_color_1));
                } else {
                    DrawableCompat.setTint(mBtnSave.getBackground(), ContextCompat.getColor(ChangeAccountInfoActivity.this, R.color.btnUnSelectedBg));
                }
            }
        });

        mEtChangeEmailCurrPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newEmail = mEtChangeEmailNewEmail.getText().toString().trim();
                String curPass = mEtChangeEmailCurrPwd.getText().toString().trim();
                if (newEmail.length() > 0
                        && Utils.isValidEmail(newEmail)
                        && curPass.length() > 3) {

                    DrawableCompat.setTint(mBtnSave.getBackground(), ContextCompat.getColor(ChangeAccountInfoActivity.this, R.color.theme_color_1));
                } else {
                    DrawableCompat.setTint(mBtnSave.getBackground(), ContextCompat.getColor(ChangeAccountInfoActivity.this, R.color.btnUnSelectedBg));
                }
            }
        });

        mEtChangePwdConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String oldPass = mEtChangePwdCurrPwd.getText().toString().trim();
                String newPass = mEtChangePwdNewPwd.getText().toString().trim();
                String confirmPass = mEtChangePwdConfirmPwd.getText().toString().trim();
                if (!TextUtils.isEmpty(oldPass)
                        && !TextUtils.isEmpty(newPass)
                        && confirmPass.length() > 3) {

                    DrawableCompat.setTint(mBtnSave.getBackground(), ContextCompat.getColor(ChangeAccountInfoActivity.this, R.color.theme_color_1));
                } else {
                    DrawableCompat.setTint(mBtnSave.getBackground(), ContextCompat.getColor(ChangeAccountInfoActivity.this, R.color.btnUnSelectedBg));
                }
            }
        });


    }

    private void saveCredentials() {
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(this, "" + getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }
        if (type != null && type.equals(TYPE_EMAIL)) {
            String email = Objects.requireNonNull(mEtChangeEmailNewEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(mEtChangeEmailCurrPwd.getText()).toString().trim();
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                if (!email.equals(mPrefConfig.getUserEmail())) {
                    changeEmail(email, password);
                } else {
                    Utils.showPopupMessageWithCloseButton(this, 2000, getString(R.string.use_different_email), true);
                }
            }
        } else {
            String oldPass = Objects.requireNonNull(mEtChangePwdCurrPwd.getText()).toString().trim();
            String newPass = Objects.requireNonNull(mEtChangePwdNewPwd.getText()).toString().trim();
            String newPassConfirm = Objects.requireNonNull(mEtChangePwdConfirmPwd.getText()).toString().trim();
            if (!TextUtils.isEmpty(oldPass) && !TextUtils.isEmpty(newPass))
                if (newPassConfirm.equals(newPass)) {
                    if (!oldPass.equals(newPass))
                        changePassword(oldPass, newPass);
                    else
                        Utils.showPopupMessageWithCloseButton(this, 2000, getString(R.string.use_different_password), true);
                } else {
                    Utils.showPopupMessageWithCloseButton(this, 2000, getString(R.string.password_mismatch), true);
                }
        }
    }

    private void bindViews() {
        forgot_password = findViewById(R.id.forgot_password);
        mIvBack = findViewById(R.id.ivBack);
        mBtnSave = findViewById(R.id.btnSave);
        mLlEmail = findViewById(R.id.ll_email);
        mLlPwd = findViewById(R.id.ll_pwd);
        mTvTitle = findViewById(R.id.headerText);

        mEtChangeEmailNewEmail = findViewById(R.id.email);
        mEtChangeEmailCurrPwd = findViewById(R.id.password);

        View currPwd = findViewById(R.id.current_pwd);
        View newPwd = findViewById(R.id.new_pwd);
        View ConfirmNewPwd = findViewById(R.id.confirm_new_pwd);

        mEtChangePwdCurrPwd = currPwd.findViewById(R.id.password);
        mEtChangePwdNewPwd = newPwd.findViewById(R.id.password);
        mEtChangePwdConfirmPwd = ConfirmNewPwd.findViewById(R.id.password);

        eye1 = currPwd.findViewById(R.id.eye);
        eye2 = newPwd.findViewById(R.id.eye);
        eye3 = ConfirmNewPwd.findViewById(R.id.eye);

        mLoader = findViewById(R.id.loader);
        mRlprogress = findViewById(R.id.rlProgress);

//        Glide.with(this)
//                .load(Utils.getLoaderForTheme(mPrefConfig.getAppTheme()))
//                .into(mLoader);

        mEtChangePwdCurrPwd.setHint(getString(R.string.enter_current_password));

        String s = getString(R.string.new_password_limit);
        SpannableString ss1 = new SpannableString(s);

        int textSize1 = getResources().getDimensionPixelSize(R.dimen._13sdp);
        int textSize2 = getResources().getDimensionPixelSize(R.dimen._8sdp);

        String text1 = getString(R.string.enter_new_password);
        String text2 = getString(R.string.character_min);

        Typeface fontMedium = ResourcesCompat.getFont(this, R.font.muli_semi_bold);
        Typeface fontRoman = ResourcesCompat.getFont(this, R.font.muli_regular);

        SpannableString span1 = new SpannableString(text1);
//        span1.setSpan(new AbsoluteSizeSpan(textSize1), 0, text1.length(), SPAN_INCLUSIVE_INCLUSIVE);
        span1.setSpan(new CustomTypefaceSpan("", fontMedium, textSize1), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(text2);
//        span2.setSpan(new AbsoluteSizeSpan(textSize2), 0, text2.length(), SPAN_INCLUSIVE_INCLUSIVE);
        span2.setSpan(new CustomTypefaceSpan("", fontRoman, textSize2), 0, text2.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);


        CharSequence finalText = TextUtils.concat(span1, " ", span2);

        mEtChangePwdNewPwd.setHint(finalText);
        mEtChangePwdConfirmPwd.setHint(getString(R.string.enter_confirm_new_password));


//        mEtChangePwdCurrPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._13sdp));
//        mEtChangePwdNewPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._13sdp));
//        mEtChangePwdConfirmPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._13sdp));

//        mEtChangePwdCurrPwd = findViewById(R.id.etChangePwdCurrPwd);
//        mEtChangePwdNewPwd = findViewById(R.id.etChangePwdNewPwd);
//        mEtChangePwdConfirmPwd = findViewById(R.id.etChangePwdConfirmPwd);

        mEtChangeEmailNewEmail.setHint(getString(R.string.enter_new_email_address));
        mEtChangeEmailCurrPwd.setHint(getString(R.string.enter_current_password));

        eye1.setOnClickListener(v -> {
            if (!isPassword1Visible) {
                isPassword1Visible = true;
                mEtChangePwdCurrPwd.setTransformationMethod(null);
            } else {
                isPassword1Visible = false;
                mEtChangePwdCurrPwd.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
        eye2.setOnClickListener(v -> {
            if (!isPassword2Visible) {
                isPassword2Visible = true;
                mEtChangePwdNewPwd.setTransformationMethod(null);
            } else {
                isPassword2Visible = false;
                mEtChangePwdNewPwd.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
        eye3.setOnClickListener(v -> {
            if (!isPassword3Visible) {
                isPassword3Visible = true;
                mEtChangePwdConfirmPwd.setTransformationMethod(null);
            } else {
                isPassword3Visible = false;
                mEtChangePwdConfirmPwd.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawableCompat.setTint(mBtnSave.getBackground(), ContextCompat.getColor(ChangeAccountInfoActivity.this, R.color.btnUnSelectedBg));
        Utils.hideKeyboard(this, mIvBack);
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void changeEmail(String email, String password) {
        showLoadingView(true);
        mRlprogress.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = OAuthAPIClient
                .getInstance()
                .getApi()
                .changeEmail("Bearer " + mPrefConfig.getAccessToken(), email, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: ");
                showLoadingView(false);
//                onBackPressed();
                if (response.isSuccessful()) {
                    mPrefConfig.setUserEmail(email);
                    updateToken();
                } else {
                    mRlprogress.setVisibility(View.GONE);
                    try {
                        if (response.errorBody() != null) {
                            String s = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(s);
                            String msg = jsonObject.getString("message");
                            Utils.showPopupMessageWithCloseButton(ChangeAccountInfoActivity.this, 2000, msg, true);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Utils.showPopupMessageWithCloseButton(ChangeAccountInfoActivity.this, 2000, getString(R.string.server_error), true);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                showLoadingView(false);
                Log.d(TAG, "onResponse: ");
                mRlprogress.setVisibility(View.GONE);
                getString(R.string.server_error);
            }
        });
    }

    private void updateToken() {


        if (mPrefConfig != null) {
            new Thread(() -> {
                // call send message here
                try {
                    OAuthResponse oAuthResponse = new TokenGenerator().refreshToken(mPrefConfig.getRefreshToken(), mPrefConfig.isLanguagePushedToServer());
                    if (oAuthResponse.isSuccessful()) {
                        try{
                        runOnUiThread(() -> {
                            mRlprogress.setVisibility(View.GONE);
                            mPrefConfig.setAccessToken(oAuthResponse.getAccessToken());
                            mPrefConfig.setRefreshToken(oAuthResponse.getRefreshToken());
                            Utils.showPopupMessageWithCloseButton(ChangeAccountInfoActivity.this, 3000, getString(R.string.changed), false);
                            finish();
                        });}catch (Exception e){
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            runOnUiThread(() -> mRlprogress.setVisibility(View.GONE));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }

    public void forgotPassword(String email) {

        Log.d("CALLXSXS", "===================================");
        Log.e("CALLXSXS", "email : " + email);

        try {
            if (!InternetCheckHelper.isConnected()) {
                showToast(getString(R.string.internet_error));
                return;
            } else {
                showLoadingView(true);
                mRlprogress.setVisibility(View.VISIBLE);
                Call<ResponseBody> call = OAuthAPIClient
                        .getInstance()
                        .getApi()
                        .forgotPassword(email);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        Log.e("CALLXSXS", "code : " + response.code());
                        mRlprogress.setVisibility(View.GONE);
                        showLoadingView(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                resetSuccess("");
                            }
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    String s = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(s);
                                    String msg = jsonObject.getString("message");
                                    showToast(msg);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                                showToast(getString(R.string.server_error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        showLoadingView(false);
                        showToast(getString(R.string.server_error));
                        mRlprogress.setVisibility(View.GONE);
                    }
                });
            }
        } catch (Exception t) {
            showToast(t.getMessage());
        }
    }

    private void showToast(String string) {
        Utils.showPopupMessageWithCloseButton(ChangeAccountInfoActivity.this, 2000, string, true);
    }

    private void changePassword(String oldPassword, String newPassword) {
        showLoadingView(true);
        Call<ResponseBody> call = OAuthAPIClient
                .getInstance()
                .getApi()
                .changePassword("Bearer " + mPrefConfig.getAccessToken(), oldPassword, newPassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: ");
                showLoadingView(false);
//                onBackPressed();
                if (response.isSuccessful()) {
                    Utils.showPopupMessageWithCloseButton(ChangeAccountInfoActivity.this, 3000, getString(R.string.changed), false);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String s = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(s);
                            String msg = jsonObject.getString("message");
                            showToast(msg);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        showToast(getString(R.string.password_should_be_minimum_8));
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                showLoadingView(false);
                Log.d(TAG, "onResponse: ");
                Toast.makeText(ChangeAccountInfoActivity.this, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void loaderShow(boolean flag) {
        showLoadingView(flag);
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void error404(String error) {

    }

    @Override
    public void success(boolean flag) {

    }

    @Override
    public void reset() {
        forgotPassword(mPrefConfig.getUserEmail());
    }

    @Override
    public void resetSuccess(String str) {
        MessagePopup popup = new MessagePopup(this, mPrefConfig.getUserEmail(), getString(R.string.password_reset),
                getString(R.string.a_reset_password_link_has_been_sent_to) + mPrefConfig.getUserEmail(), getString(R.string.ok));
        popup.showDialog(v -> {
//            Intent intent = new Intent(ChangeAccountInfoActivity.this, OTPVerificationActivity.class);
//            intent.putExtra("email", mPrefConfig.getUserEmail());
//            intent.putExtra("forgot", true);
//            intent.putExtra("check", "forgot_password_from_settings");
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
//            finishAfterTransition();
//            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
    }

    private void showLoadingView(boolean isShow) {
        if (isFinishing()) {
            return;
        }
        if (isShow) {
            if (loadingDialog == null) {
                loadingDialog = new PictureLoadingDialog(this);
            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null
                    && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}
