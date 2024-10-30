package com.newsreels.app.presenter;

import android.app.Activity;

import com.newsreels.app.APIResources.ApiClient;
import com.newsreels.app.R;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.model.articles.ArticleResponse;
import com.newsreels.app.utills.InternetCheckHelper;

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
