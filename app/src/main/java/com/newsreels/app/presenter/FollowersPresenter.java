package com.newsreels.app.presenter;

import android.app.Activity;
import com.newsreels.app.APIResources.ApiClient;
import com.newsreels.app.R;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.interfaces.ApiCallbacks;
import com.newsreels.app.model.followers.FollowersListResponse;
import com.newsreels.app.utills.Constants;
import com.newsreels.app.utills.InternetCheckHelper;

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
