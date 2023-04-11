package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.ContactUsInterface;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsPresenter {

    private Activity activity;
    private ContactUsInterface contactInterface;
    private PrefConfig mPrefconfig;

    public ContactUsPresenter(Activity activity, ContactUsInterface contactInterface) {
        this.activity = activity;
        this.contactInterface = contactInterface;
        this.mPrefconfig = new PrefConfig(activity);

    }

    public void contactUs(String email, String name, String msg) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        Log.e("CALLXSXS", "email : " + email);
        Log.e("CALLXSXS", "name : " + name);
        Log.e("CALLXSXS", "msg : " + msg);
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        try {
            if (!InternetCheckHelper.isConnected()) {
                contactInterface.error(activity.getString(R.string.internet_error));
            } else {
                contactInterface.loaderShow(true);
                Call<ResponseBody> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .contactUs("Bearer " + mPrefconfig.getAccessToken(), email, name, msg);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        contactInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response.body() != null) {
                            Log.e("RESPONSE", "RES: " + response.body());
                            contactInterface.success(response.message());
                        } else {
                            if (!TextUtils.isEmpty(response.message())) {
                                contactInterface.error(response.message());
                            } else {
                                contactInterface.error(activity.getString(R.string.server_error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        contactInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + t.getMessage());

                        contactInterface.error(activity.getString(R.string.server_error));

                    }
                });
            }
        } catch (Exception t) {
            Log.e("RESPONSE", "RES: " + t.getMessage());
            if (!TextUtils.isEmpty(t.getMessage())) {
                contactInterface.error(t.getMessage());
            } else {
                contactInterface.error(activity.getString(R.string.server_error));
            }
        }
    }

}
