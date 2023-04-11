package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.auth.TokenGenerator;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.push.Push;
import com.ziro.bullet.data.models.push.PushResponse;
import com.ziro.bullet.interfaces.PushNotificationInterface;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotificationPresenter {

    private Activity activity;
    private PushNotificationInterface mPushNotificationInterface;
    private PrefConfig mPrefconfig;
    private TokenGenerator mTokenGenerator;

    public PushNotificationPresenter(Activity activity, PushNotificationInterface mPushNotificationInterface) {
        this.activity = activity;
        this.mPushNotificationInterface = mPushNotificationInterface;
        this.mTokenGenerator = new TokenGenerator();
        this.mPrefconfig = new PrefConfig(activity);
    }

    /**
     * Update Push Option
     */
    public void pushConfig(boolean breaking, boolean personalized, String freq, String startTime, String endTime) {

        Log.d("CALLXSXS", "================ REG ===================");
        Log.e("CALLXSXS", "breaking : " + breaking);
        Log.e("CALLXSXS", "personalized : " + personalized);

        try {
            mPushNotificationInterface.loaderShow(true);
            if (!InternetCheckHelper.isConnected()) {
                mPushNotificationInterface.error(activity.getString(R.string.internet_error));
                mPushNotificationInterface.loaderShow(false);
                return;
            } else {
                Call<PushResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .push("Bearer " + mPrefconfig.getAccessToken(), breaking, personalized, freq, startTime, endTime);
                call.enqueue(new Callback<PushResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<PushResponse> call, @NotNull Response<PushResponse> response) {
                        mPushNotificationInterface.loaderShow(false);
                        Log.e("CALLXSXS", "code : " + response.code());
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    Push push = response.body().getPush();
                                    mPrefconfig.saveNotificationPref(push);
                                    mPushNotificationInterface.SuccessFirst(true);
//                                    Utils.showPopupMessageWithCloseButton(activity, 3000, "Notification setting updated!", false);
                                }
                                break;
                            case 404:
                                mPushNotificationInterface.error404(response.message());
                                break;
                            default:
                                try {
                                    if (response.errorBody() != null) {
                                        String s = response.errorBody().string();
                                        JSONObject jsonObject = new JSONObject(s);
                                        String msg = jsonObject.getString("message");
                                        mPushNotificationInterface.error(msg);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }

                        }

                    }

                    @Override
                    public void onFailure(@NotNull Call<PushResponse> call, @NotNull Throwable t) {
                        mPushNotificationInterface.loaderShow(false);
                        mPushNotificationInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            mPushNotificationInterface.error(t.getMessage());
        }
    }

    public void onBoardingPushConfig(boolean breaking, boolean personalized, String freq) {

        Log.d("CALLXSXS", "================ REG ===================");
        Log.e("CALLXSXS", "breaking : " + breaking);
        Log.e("CALLXSXS", "personalized : " + personalized);

        try {
            mPushNotificationInterface.loaderShow(true);
            if (!InternetCheckHelper.isConnected()) {
                mPushNotificationInterface.error(activity.getString(R.string.internet_error));
                mPushNotificationInterface.loaderShow(false);
                return;
            } else {
                Call<PushResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .onBoardingPush("Bearer " + mPrefconfig.getAccessToken(), breaking, personalized, freq);
                call.enqueue(new Callback<PushResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<PushResponse> call, @NotNull Response<PushResponse> response) {
                        mPushNotificationInterface.loaderShow(false);
                        Log.e("CALLXSXS", "code : " + response.code());
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    Push push = response.body().getPush();
                                    mPrefconfig.saveNotificationPref(push);
                                    mPushNotificationInterface.SuccessFirst(true);
//                                    Utils.showPopupMessageWithCloseButton(activity, 3000, "Notification setting updated!", false);
                                }
                                break;
                            case 404:
                                mPushNotificationInterface.error404(response.message());
                                break;
                            default:
                                try {
                                    if (response.errorBody() != null) {
                                        String s = response.errorBody().string();
                                        JSONObject jsonObject = new JSONObject(s);
                                        String msg = jsonObject.getString("message");
                                        mPushNotificationInterface.error(msg);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }

                        }

                    }

                    @Override
                    public void onFailure(@NotNull Call<PushResponse> call, @NotNull Throwable t) {
                        mPushNotificationInterface.loaderShow(false);
                        mPushNotificationInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            mPushNotificationInterface.error(t.getMessage());
        }
    }

    public void pushConfig() {
        Log.e("CALLXSXS", "TOKEN : " + mPrefconfig.getAccessToken());
        try {
            mPushNotificationInterface.loaderShow(true);
            if (!InternetCheckHelper.isConnected()) {
                mPushNotificationInterface.error(activity.getString(R.string.internet_error));
                mPushNotificationInterface.loaderShow(false);
                return;
            } else {
                Call<PushResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .pushConfig("Bearer " + mPrefconfig.getAccessToken());
                call.enqueue(new Callback<PushResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<PushResponse> call, @NotNull Response<PushResponse> response) {
                        mPushNotificationInterface.loaderShow(false);
                        Log.e("CALLXSXS", "code : " + response.code());
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    Push model = response.body().getPush();
                                    if (model != null) {
                                        mPrefconfig.saveNotificationPref(model);
                                        mPushNotificationInterface.success(model);
                                    }
                                }
                                break;
                            case 404:
                                mPushNotificationInterface.error404(response.message());
                                break;
                            default:
                                try {
                                    if (response.errorBody() != null) {
                                        String s = response.errorBody().string();
                                        JSONObject jsonObject = new JSONObject(s);
                                        String msg = jsonObject.getString("message");
                                        mPushNotificationInterface.error(msg);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }

                        }

                    }

                    @Override
                    public void onFailure(@NotNull Call<PushResponse> call, @NotNull Throwable t) {
                        mPushNotificationInterface.loaderShow(false);
                        mPushNotificationInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            mPushNotificationInterface.error(t.getMessage());
        }
    }


    private void showToast(String string) {
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
    }
}
