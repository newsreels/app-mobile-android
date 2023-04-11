package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.interfaces.LocationCallback;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.location.LocationModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationPresenter {

    private Activity activity;
    private LocationCallback locationCallback;
    private PrefConfig mPrefconfig;

    public LocationPresenter(Activity activity, LocationCallback locationCallback) {
        this.activity = activity;
        this.locationCallback = locationCallback;
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void getAllLocations(String query, String page) {
        if (!InternetCheckHelper.isConnected()) {
            locationCallback.error(activity.getString(R.string.internet_error));
        } else {
            locationCallback.loaderShow(true);
            Call<LocationModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getLocations("Bearer " + mPrefconfig.getAccessToken(), query, page);
            call.enqueue(new Callback<LocationModel>() {
                @Override
                public void onResponse(@NotNull Call<LocationModel> call, @NotNull Response<LocationModel> response) {
                    locationCallback.loaderShow(false);
                    locationCallback.success(response.body());
                }

                @Override
                public void onFailure(@NotNull Call<LocationModel> call, @NotNull Throwable t) {
                    locationCallback.loaderShow(false);
                    locationCallback.error(t.getMessage());
                }
            });
        }
    }

    public void updateLocation(ArrayList<String> followList, ArrayList<String> unfollowList){
        if (!InternetCheckHelper.isConnected()) {
            locationCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .updateLocations("Bearer " + mPrefconfig.getAccessToken(), followList,unfollowList);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    locationCallback.loaderShow(false);
                    Log.e("RESPONSE", "RES: " + response.body());
                    if (response.code() == 200) {
                        locationCallback.addSuccess(0);
                    } else if (response.code() == 400) {
                        locationCallback.error(response.message());
                    } else {
                        locationCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    locationCallback.loaderShow(false);
                    locationCallback.error(t.getMessage());
                }
            });
        }
    }
}
