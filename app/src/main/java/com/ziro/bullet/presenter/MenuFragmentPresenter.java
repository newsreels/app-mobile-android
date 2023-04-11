package com.ziro.bullet.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.ApiResponseInterface;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragmentPresenter {

    private Context context;
    private Activity activity;
    private PrefConfig mPrefs;

    public MenuFragmentPresenter(Activity activity) {
        this.activity = activity;
        this.mPrefs = new PrefConfig(activity);
    }

    public void acceptGuidelines(ApiResponseInterface apiResponseInterface) {
        if (!InternetCheckHelper.isConnected()) {
            return;
        }
        if (!TextUtils.isEmpty(mPrefs.getAccessToken())) {
            Call<ResponseBody> call = ApiClient.getInstance(activity).getApi()
                    .acceptGuidelines("Bearer " + mPrefs.getAccessToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    Log.e("FCM", "FCM token to server onResponse : " + response.toString());
                    if (apiResponseInterface != null && response != null) {
                        if (response.code() == 200) {
                            apiResponseInterface._success();
                        } else {
                            apiResponseInterface._other(response.code());
                        }
                    } else {
                        apiResponseInterface._other(0);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    Log.e("FCM", "FCM token to server Failure : " + t.getMessage());
                    if (apiResponseInterface != null)
                        apiResponseInterface._other(0);
                }
            });
        }
    }
}
