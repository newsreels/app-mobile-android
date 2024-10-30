package com.newsreels.app.presenter;

import android.app.Activity;
import android.util.Log;

import com.newsreels.app.R;
import com.newsreels.app.auth.OAuthAPIClient;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.interfaces.SignInInterface;
import com.newsreels.app.model.SigninResponse;
import com.newsreels.app.utills.InternetCheckHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInPresenter {

    private Activity activity;
    private SignInInterface signInInterface;
    private PrefConfig mPrefconfig;

    public SignInPresenter(Activity activity, SignInInterface signInInterface) {
        this.activity = activity;
        this.signInInterface = signInInterface;
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void checkEmail(String email) {
        try {
            signInInterface.loaderShow(true);

            if (!InternetCheckHelper.isConnected()) {
                signInInterface.error(activity.getString(R.string.internet_error));
                signInInterface.loaderShow(false);
            } else {
                Call<SigninResponse> signinResponseCall = OAuthAPIClient
                        .getInstance()
                        .getApi()
                        .checkEmail(email);
                signinResponseCall.enqueue(new Callback<SigninResponse>() {
                    @Override
                    public void onResponse(Call<SigninResponse> call, Response<SigninResponse> response) {
                        signInInterface.loaderShow(false);
                        Log.e("CALLXSXS", "code : " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            signInInterface.success(response.body().getExist());
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    String s = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(s);
                                    String msg = jsonObject.getString("message");
                                    signInInterface.error(msg);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SigninResponse> call, Throwable t) {
                        signInInterface.loaderShow(false);
                        signInInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            signInInterface.loaderShow(false);
            signInInterface.error(t.getMessage());
        }
    }


}
