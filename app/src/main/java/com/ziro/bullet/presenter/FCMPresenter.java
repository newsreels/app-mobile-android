package com.ziro.bullet.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.data.PrefConfig;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMPresenter {

    private Context context;
    private Activity activity;
    private PrefConfig mPrefs;

    public FCMPresenter(Activity activity) {
        this.activity = activity;
        this.mPrefs = new PrefConfig(activity);
    }

    public FCMPresenter(Context context) {
        this.context = context;
        this.mPrefs = new PrefConfig(context);
    }

    public void sentTokenToServer(PrefConfig prefConfig) {
        if (!InternetCheckHelper.isConnected()) {
            return;
        }
        Log.e("FCM", "++++++++++++++++++++++++++++++++++");
        Log.e("FCM", "FCM token To Server : " + prefConfig.getFirebaseToken());
        Log.e("FCM", "ACCESS token To Server : " + prefConfig.getAccessToken());
        if(!TextUtils.isEmpty(prefConfig.getAccessToken()) && !TextUtils.isEmpty(prefConfig.getFirebaseToken())) {
            Call<ResponseBody> call = ApiClient.getInstance(activity).getApi()
                    .sendTokenToServer("Bearer " + prefConfig.getAccessToken(), prefConfig.getFirebaseToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    Log.e("FCM", "FCM token to server onResponse : " + response.toString());
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    Log.e("FCM", "FCM token to server Failure : " + t.getMessage());
                }
            });
        }
    }
}
