package com.ziro.bullet.presenter;

import android.app.Activity;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchedulePresenter {
    private Activity activity;
    private PrefConfig mPrefs;
    private ScheduledCallback callback;

    public SchedulePresenter(Activity activity) {
        this.activity = activity;
        this.callback = (ScheduledCallback) activity;
        this.mPrefs = new PrefConfig(activity);
    }

    public void getScheduledPosts(String source, String nextPage) {
        if (callback == null)
            return;
        if (!InternetCheckHelper.isConnected()) {
            callback.error(activity.getString(R.string.internet_error));
        } else {
            callback.loaderShow(true);
            Call<ArticleResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getScheduledPosts("Bearer " + mPrefs.getAccessToken(), source, nextPage);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                    callback.loaderShow(false);
                    if (response.isSuccessful()) {
                        callback.success(response.body());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                    callback.loaderShow(false);
                    callback.error(t.getLocalizedMessage());
                }
            });
        }
    }

    public interface ScheduledCallback {
        void loaderShow(boolean flag);

        void error(String error);

        void success(ArticleResponse body);
    }
}
