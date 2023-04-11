package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.NotificationPresenterCallback;
import com.ziro.bullet.model.notification.GeneralNotificationResponse;
import com.ziro.bullet.model.notification.NewsNotificationResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationPresenter {
    private Activity context;
    private PrefConfig mPrefconfig;
    private NotificationPresenterCallback callback;

    public NotificationPresenter(Activity context, NotificationPresenterCallback callback) {
        this.context = context;
        this.callback = callback;
        mPrefconfig = new PrefConfig(context);
    }

    public void getGeneralNotifications(String page) {
        if (!InternetCheckHelper.isConnected()) {
            callback.error(context.getString(R.string.internet_error));
            return;
        }
        if (!TextUtils.isEmpty(mPrefconfig.getAccessToken())) {
            callback.loaderShow(true);
            Call<GeneralNotificationResponse> call = ApiClient.getInstance(context).getApi()
                    .getGeneralNotifications("Bearer " + mPrefconfig.getAccessToken(), page);
            call.enqueue(new Callback<GeneralNotificationResponse>() {
                @Override
                public void onResponse(@NotNull Call<GeneralNotificationResponse> call, @NotNull Response<GeneralNotificationResponse> response) {
                    callback.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            callback.success(response.body());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                callback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            callback.error(context.getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<GeneralNotificationResponse> call, @NotNull Throwable t) {
                    callback.loaderShow(false);
                    callback.error(context.getString(R.string.server_error));
                }
            });
        }
    }


    public void getArticleNotifications(String page) {
        if (!InternetCheckHelper.isConnected()) {
            callback.error(context.getString(R.string.internet_error));
            return;
        }
        if (!TextUtils.isEmpty(mPrefconfig.getAccessToken())) {
            callback.loaderShow(true);
            Call<NewsNotificationResponse> call = ApiClient.getInstance(context).getApi()
                    .getNewsNotifications("Bearer " + mPrefconfig.getAccessToken(), page);
            call.enqueue(new Callback<NewsNotificationResponse>() {
                @Override
                public void onResponse(@NotNull Call<NewsNotificationResponse> call, @NotNull Response<NewsNotificationResponse> response) {
                    callback.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            callback.success(response.body());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                callback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            callback.error(context.getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<NewsNotificationResponse> call, @NotNull Throwable t) {
                    callback.loaderShow(false);
                    callback.error(context.getString(R.string.server_error));
                }
            });
        }
    }

    public void readNotification(String id) {
        if (!TextUtils.isEmpty(mPrefconfig.getAccessToken())) {
            Call<ResponseBody> call = ApiClient.getInstance(context).getApi()
                    .readNotification("Bearer " + mPrefconfig.getAccessToken(), id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

                }
            });
        }
    }
}
