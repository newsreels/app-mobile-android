package com.ziro.bullet.presenter;

import android.app.Activity;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.ApiCallbacks;
import com.ziro.bullet.model.followers.FollowersListResponse;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowersPresenter {

    private Activity activity;
    private ApiCallbacks apiCallbacks;
    private PrefConfig mPrefs;

    public FollowersPresenter(Activity activity, ApiCallbacks apiCallbacks) {
        this.activity = activity;
        this.apiCallbacks = apiCallbacks;
        this.mPrefs = new PrefConfig(activity);
    }

    public void getFollowers(String source, String query, String page) {
        if (!InternetCheckHelper.isConnected()) {
            apiCallbacks.error(activity.getString(R.string.internet_error));
            Constants.isApiCalling = false;
        } else {
            apiCallbacks.loaderShow(true);
            Constants.isApiCalling = true;

            Call<FollowersListResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getFollowers("Bearer " + mPrefs.getAccessToken(), source, query, page);
            call.enqueue(new Callback<FollowersListResponse>() {
                @Override
                public void onResponse(@NotNull Call<FollowersListResponse> call, @NotNull Response<FollowersListResponse> response) {
                    apiCallbacks.loaderShow(false);
                    if (response.isSuccessful()) {
                        apiCallbacks.success(response.body());
                    }else{
                        apiCallbacks.error("");
                    }
                    Constants.isApiCalling = false;
                }

                @Override
                public void onFailure(@NotNull Call<FollowersListResponse> call, @NotNull Throwable t) {
                    if(!call.isCanceled()) {
                        apiCallbacks.loaderShow(false);
                    }
                    apiCallbacks.error(t.getMessage());
                    Constants.isApiCalling = false;
                }
            });
        }
    }
}
