package com.newsreels.app.presenter;

import android.app.Activity;

import com.newsreels.app.APIResources.ApiClient;
import com.newsreels.app.BuildConfig;
import com.newsreels.app.R;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.interfaces.StudioCallback;
import com.newsreels.app.model.Reel.ReelResponse;
import com.newsreels.app.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudioPresenter {
    private Activity activity;
    private PrefConfig mPrefs;
    private StudioCallback callback;

    public StudioPresenter(Activity activity, StudioCallback mainInterface) {
        this.activity = activity;
        this.callback = mainInterface;
        this.mPrefs = new PrefConfig(activity);
    }

    public void loadMyReels(String source,String nextPage) {
        if (!InternetCheckHelper.isConnected()) {
            callback.error(activity.getString(R.string.internet_error));
        } else {
            callback.loaderShow(true);
            Call<ReelResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getMyReels("Bearer " + mPrefs.getAccessToken(), source, nextPage);
            call.enqueue(new Callback<ReelResponse>() {
                @Override
                public void onResponse(@NotNull Call<ReelResponse> call, @NotNull Response<ReelResponse> response) {
                    callback.loaderShow(false);
                    if (response.isSuccessful()) {
                        callback.success(response.body());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ReelResponse> call, @NotNull Throwable t) {
                    callback.loaderShow(false);
                    callback.error(t.getLocalizedMessage());
                }
            });
        }
    }

    public void getReelsHome(String type, String context, String query,String page, boolean reload, boolean showShimmer, boolean isPagination, String hashtag) {
        String query_up = query;
        if (!InternetCheckHelper.isConnected()) {
            callback.error(activity.getString(R.string.internet_error));
        } else {
            callback.loaderShow(true);
            Call<ReelResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
//                    .searchReels("Bearer " + mPrefs.getAccessToken(), query_up, page);
                    .searchReels("Bearer " + mPrefs.getAccessToken(), context, hashtag,query_up, page, type, BuildConfig.DEBUG);
            call.enqueue(new Callback<ReelResponse>() {
                @Override
                public void onResponse(Call<ReelResponse> call, Response<ReelResponse> response) {
                    callback.loaderShow(false);
                    if (response.isSuccessful()) {
                        callback.success(response.body());
                    }
                }

                @Override
                public void onFailure(Call<ReelResponse> call, Throwable t) {
                    callback.loaderShow(false);
                    callback.error(t.getLocalizedMessage());
                }
            });
        }
    }



    public void loadSavedReels(String nextPage) {
        if (!InternetCheckHelper.isConnected()) {
            callback.error(activity.getString(R.string.internet_error));
        } else {
            callback.loaderShow(true);
            Call<ReelResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .newsReelArchive("Bearer " + mPrefs.getAccessToken(), nextPage);
            call.enqueue(new Callback<ReelResponse>() {
                @Override
                public void onResponse(@NotNull Call<ReelResponse> call, @NotNull Response<ReelResponse> response) {
                    callback.loaderShow(false);
                    if (response.isSuccessful()) {
                        callback.success(response.body());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ReelResponse> call, @NotNull Throwable t) {
                    callback.loaderShow(false);
                    callback.error(t.getLocalizedMessage());
                }
            });
        }
    }
}
