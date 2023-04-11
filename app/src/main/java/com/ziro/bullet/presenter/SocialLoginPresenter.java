package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.auth.OAuthResponse;
import com.ziro.bullet.auth.OAuthResponseCallback;
import com.ziro.bullet.auth.TokenGenerator;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.BaseModel;
import com.ziro.bullet.interfaces.PasswordInterface;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialLoginPresenter {
    private static final String TAG = SocialLoginPresenter.class.getSimpleName();
    private static final List<String> FACEBOOK_PERMISSIONS = Arrays.asList("public_profile", "email");
    private static final String AUTH0_SCOPE = "openid email profile offline_access";
    private static final String GOOGLE_OAUTH2 = "google-oauth2";
    private static final String FACEBOOK_SUBJECT_TOKEN_TYPE = "http://auth0.com/oauth/token-type/facebook-info-session-access-token";
    private Activity activity;
    private PasswordInterface passwordInterface;
    private PrefConfig mPrefconfig;

    private TokenGenerator tokenGenerator;

    public SocialLoginPresenter(Activity activity, PasswordInterface passwordInterface) {
        this.activity = activity;
        this.passwordInterface = passwordInterface;
        this.mPrefconfig = new PrefConfig(activity);
        tokenGenerator = new TokenGenerator();
    }


    public void facebookLogin(@NonNull final AccessToken accessToken) {
        passwordInterface.loaderShow(true);
        final String token = accessToken.getToken();

        String subject_token_type = "facebook_access_token";

        tokenGenerator.socialExchangeToken(token, subject_token_type, mPrefconfig.isLanguagePushedToServer(), new OAuthResponseCallback() {
            @Override
            public void onResponse(OAuthResponse response) {
                Log.d(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    mPrefconfig.setAccessToken(response.getAccessToken());
                    mPrefconfig.setRefreshToken(response.getRefreshToken());
                    passwordInterface.success(true);
                } else {
                    passwordInterface.loaderShow(false);
                    if (response.getOAuthError() != null)
                        passwordInterface.error("" + response.getOAuthError().getErrorDescription());
                    else {
                        passwordInterface.error("");
                    }
                }
            }
        });
    }

    public void googleLogin(String token) {
        passwordInterface.loaderShow(true);

        Log.d(TAG, "googleLogin: ");
        if (InternetCheckHelper.isConnected()) {

            String subject_token_type = "google_access_token";

            tokenGenerator.socialExchangeToken(token, subject_token_type, mPrefconfig.isLanguagePushedToServer(), response -> {
                Log.d(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    mPrefconfig.setAccessToken(response.getAccessToken());
                    mPrefconfig.setRefreshToken(response.getRefreshToken());
                    passwordInterface.success(true);
                } else {
                    passwordInterface.loaderShow(false);
                    if (response.getOAuthError() != null)
                        passwordInterface.error("" + response.getOAuthError().getErrorDescription());
                    else {
                        passwordInterface.error("");
                    }
                }
            });
        }
    }

    public void skipLogin(String deviceId) {
//        Log.d(TAG, "skipLogin: deviceId = " + deviceId);
//        passwordInterface.loaderShow(true);

        tokenGenerator.authenticateGuest(deviceId + "a", mPrefconfig.getPrefLocale(), response -> {
            if (response.isSuccessful()) {
                mPrefconfig.setAccessToken(response.getAccessToken());
                mPrefconfig.setRefreshToken(response.getRefreshToken());
                passwordInterface.success(true);
            } else {
//                passwordInterface.loaderShow(false);
                if (response.getOAuthError() != null && !TextUtils.isEmpty(response.getOAuthError().getErrorDescription()))
                    passwordInterface.error("" + response.getOAuthError().getErrorDescription());
                else {
                    passwordInterface.error404("");
                }
            }
        });
    }

    public void selectRegion(String id, PrefConfig prefConfig) {
        if (InternetCheckHelper.isConnected()) {
            Call<BaseModel> call = ApiClient
                    .getInstance()
                    .getApi()
                    .updateUserRegion("Bearer " + prefConfig.getAccessToken(), id);
            call.enqueue(new Callback<BaseModel>() {
                @Override
                public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
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
                public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {

                }
            });
        }
    }

    public void selectLanguage(String id) {
        if (InternetCheckHelper.isConnected()) {
            Call<BaseModel> call = ApiClient
                    .getInstance()
                    .getApi()
                    .updateUserLanguage("Bearer " + mPrefconfig.getAccessToken(), id, Constants.PRIMARY_LANGUAGE);
            call.enqueue(new Callback<BaseModel>() {
                @Override
                public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                    if (response.code() == 200) {
//                        new Thread(() -> {
//                            try {
//                                OAuthResponse oAuthResponse = new TokenGenerator().refreshToken(mPrefconfig.getRefreshToken(), mPrefconfig.isLanguagePushedToServer());
//                                if (oAuthResponse.isSuccessful()) {
//                                    mPrefconfig.setAccessToken(oAuthResponse.getAccessToken());
//                                    mPrefconfig.setRefreshToken(oAuthResponse.getRefreshToken());
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }).start();
//                        mPrefconfig.setLanguageForServer(null);
                    }
                }
                @Override
                public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {

                }
            });
        }
    }

    private interface SimpleCallback<T> {
        void onResult(@NonNull T result);

        void onError(@NonNull Throwable cause);
    }
}
