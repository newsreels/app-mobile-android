package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.auth.OAuthResponse;
import com.ziro.bullet.auth.OAuthResponseCallback;
import com.ziro.bullet.auth.TokenGenerator;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.PasswordInterface;
import com.ziro.bullet.model.language.LanguageResponse;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordPresenter {
    private static final String TAG = PasswordPresenter.class.getSimpleName();

    private Activity activity;
    private PasswordInterface passwordInterface;
    private PrefConfig mPrefconfig;
    private TokenGenerator mTokenGenerator;

    public PasswordPresenter(Activity activity, PasswordInterface passwordInterface) {
        this.activity = activity;
        this.passwordInterface = passwordInterface;
        this.mTokenGenerator = new TokenGenerator();
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void selectRegion(String id, PrefConfig prefConfig) {
        if (InternetCheckHelper.isConnected()) {
            Call<LanguageResponse> call = OAuthAPIClient
                    .getInstance()
                    .getApi()
                    .updateUserRegion("Bearer " + prefConfig.getAccessToken(), id);
            call.enqueue(new Callback<LanguageResponse>() {
                @Override
                public void onResponse(@NotNull Call<LanguageResponse> call, @NotNull Response<LanguageResponse> response) {
                    if (response.code() == 200) {
//                        new Thread(() -> {
//                            // call send message here
//                            try {
//                                OAuthResponse oAuthResponse = new TokenGenerator().refreshToken(prefConfig.getRefreshToken(),mPrefconfig.isLanguagePushedToServer());
//                                if (oAuthResponse.isSuccessful()) {
//                                    prefConfig.setAccessToken(oAuthResponse.getAccessToken());
//                                    prefConfig.setRefreshToken(oAuthResponse.getRefreshToken());
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }).start();
//                        prefConfig.setLanguageForServer(null);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LanguageResponse> call, @NotNull Throwable t) {

                }
            });
        }
    }

    public void selectLanguage(String id, PrefConfig prefConfig) {
        if (InternetCheckHelper.isConnected()) {
            Call<LanguageResponse> call = OAuthAPIClient
                    .getInstance()
                    .getApi()
                    .updateLanguage("Bearer " + prefConfig.getAccessToken(), id, Constants.PRIMARY_LANGUAGE);
            call.enqueue(new Callback<LanguageResponse>() {
                @Override
                public void onResponse(@NotNull Call<LanguageResponse> call, @NotNull Response<LanguageResponse> response) {
                    if (response.code() == 200) {
//                        new Thread(() -> {
//                            // call send message here
//                            try {
//                                OAuthResponse oAuthResponse = new TokenGenerator().refreshToken(prefConfig.getRefreshToken(),mPrefconfig.isLanguagePushedToServer());
//                                if (oAuthResponse.isSuccessful()) {
//                                    prefConfig.setAccessToken(oAuthResponse.getAccessToken());
//                                    prefConfig.setRefreshToken(oAuthResponse.getRefreshToken());
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }).start();
//                        prefConfig.setLanguageForServer(null);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LanguageResponse> call, @NotNull Throwable t) {

                }
            });
        }
    }

    public void normalLogin(String uname, String pwd) {
        passwordInterface.loaderShow(true);

        if (!InternetCheckHelper.isConnected()) {
            passwordInterface.error(activity.getString(R.string.internet_error));
            passwordInterface.loaderShow(false);
        } else {
            mTokenGenerator.login(uname, pwd, mPrefconfig.isLanguagePushedToServer(), response -> {
                if (activity != null) {
                    try {
                    activity.runOnUiThread(() -> {
                        passwordInterface.loaderShow(false);
                        if (response.isSuccessful()) {
                            AnalyticsEvents.INSTANCE.logEvent(activity,
                                    Events.SIGNUP_EMAIL);
                            mPrefconfig.setAccessToken(response.getAccessToken());
                            mPrefconfig.setRefreshToken(response.getRefreshToken());
                            mPrefconfig.setUserEmail(uname);
                            passwordInterface.success(true);
                        } else {
                            passwordInterface.error("" + response.getOAuthError().getErrorDescription());
                        }
                    });
                }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Authenticate email and password with server and store token locally.a
     */
    public void login(String mEmail, String mPassword) {
        if (!TextUtils.isEmpty(mEmail) && !TextUtils.isEmpty(mPassword)) {
            mEmail = mEmail.trim();
            mPassword = mPassword.trim();

            passwordInterface.loaderShow(true);
            if (!InternetCheckHelper.isConnected()) {
                passwordInterface.error(activity.getString(R.string.internet_error));
                passwordInterface.loaderShow(false);
                return;
            } else {
                Log.d("CALLXSXS", "================ LOGIN ===================");
                Log.e("CALLXSXS", "mEmail : " + mEmail);
                Log.e("CALLXSXS", "mPassword : " + mPassword);

                String finalMEmail = mEmail;
                mTokenGenerator.login(mEmail, mPassword, mPrefconfig.isLanguagePushedToServer(), new OAuthResponseCallback() {
                    @Override
                    public void onResponse(OAuthResponse response) {
                        passwordInterface.loaderShow(false);
                        if (response.isSuccessful()) {
                            mPrefconfig.setAccessToken(response.getAccessToken());
                            mPrefconfig.setRefreshToken(response.getRefreshToken());
                            mPrefconfig.setUserEmail(finalMEmail);
                            passwordInterface.success(true);
                        } else {
                            passwordInterface.error("Please enter the correct password");
                        }
                    }
                });
            }
        }

    }

    public void register(String mEmail, String mPassword, String code, boolean forgot, boolean termsandcondition) {

        Log.d("CALLXSXS", "================ REG ===================");
        Log.e("CALLXSXS", "mEmail : " + mEmail);
        Log.e("CALLXSXS", "mPassword : " + mPassword);

        try {
            passwordInterface.loaderShow(true);
            if (!InternetCheckHelper.isConnected()) {
                passwordInterface.error(activity.getString(R.string.internet_error));
                passwordInterface.loaderShow(false);
            } else {
                Call<ResponseBody> call = OAuthAPIClient
                        .getInstance()
                        .getApi()
                        .register(mEmail, mPassword, code, forgot, termsandcondition);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        passwordInterface.loaderShow(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                normalLogin(mEmail, mPassword);
                            }
                        } else {
                            if (response.code() == 404) {
                                passwordInterface.error404(response.message());
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        String s = response.errorBody().string();
                                        JSONObject jsonObject = new JSONObject(s);
                                        String msg = jsonObject.getString("message");
                                        passwordInterface.error(msg);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        passwordInterface.loaderShow(false);
                        passwordInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            passwordInterface.error(t.getMessage());
        }
    }

    public void register(String mEmail, String mPassword, boolean termsandcondition) {

        Log.d("CALLXSXS", "================ REG ===================");
        Log.e("CALLXSXS", "mEmail : " + mEmail);
        Log.e("CALLXSXS", "mPassword : " + mPassword);

        try {
            passwordInterface.loaderShow(true);
            if (!InternetCheckHelper.isConnected()) {
                passwordInterface.error(activity.getString(R.string.internet_error));
                passwordInterface.loaderShow(false);
            } else {
                Call<ResponseBody> call = OAuthAPIClient
                        .getInstance()
                        .getApi()
                        .registerUser(mEmail, mPassword, termsandcondition);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        passwordInterface.loaderShow(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
//                                normalLogin(mEmail, mPassword);
                                passwordInterface.success(true);
                            }
                        } else {
                            if (response.code() == 404) {
                                passwordInterface.error404(response.message());
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        String s = response.errorBody().string();
                                        JSONObject jsonObject = new JSONObject(s);
                                        String msg = jsonObject.getString("message");
                                        passwordInterface.error(msg);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        passwordInterface.loaderShow(false);
                        passwordInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            passwordInterface.error(t.getMessage());
        }
    }

    public void forgotPassword(String email) {

        Log.d("CALLXSXS", "===================================");
        Log.e("CALLXSXS", "email : " + email);

        try {
            if (!InternetCheckHelper.isConnected()) {
                showToast(activity.getString(R.string.internet_error));
            } else {
                Call<ResponseBody> responseBodyCall = OAuthAPIClient
                        .getInstance()
                        .getApi()
                        .forgotPassword(email);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("CALLXSXS", "code : " + response.code());
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    passwordInterface.resetSuccess("A reset password link has been sent to " + email);
                                }
                                break;
                            case 404:
                                showToast(response.message());
                                break;
                            default:
                                showToast(response.message());

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showToast(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            showToast(t.getMessage());
        }
    }

    private void showToast(String string) {
//        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
        Utils.showPopupMessageWithCloseButton(activity, 3000, string, true);
    }
}
