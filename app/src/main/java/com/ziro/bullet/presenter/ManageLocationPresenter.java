package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.interfaces.ManageLocationCallback;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.location.LocationModel;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageLocationPresenter {
    private Activity activity;
    private ManageLocationCallback homeCallback;
    private PrefConfig mPrefs;

    public ManageLocationPresenter(Activity activity, ManageLocationCallback mainInterface) {
        this.activity = activity;
        this.homeCallback = mainInterface;
        this.mPrefs = new PrefConfig(activity);
    }

    public void getLocations(String page) {
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
            homeCallback.loaderShow(false);
        } else {
            homeCallback.loaderShow(true);
            Call<LocationModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getFollowedLocation("Bearer " + mPrefs.getAccessToken(), page);
            call.enqueue(new Callback<LocationModel>() {
                @Override
                public void onResponse(@NotNull Call<LocationModel> call, @NotNull Response<LocationModel> response) {
                    homeCallback.loaderShow(false);
                    if (response.code() == 200) {
                        Log.e("HOMELIFE", "userTopics : here");
                        homeCallback.success(response.body());
                    } else {
                        homeCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LocationModel> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }
}
