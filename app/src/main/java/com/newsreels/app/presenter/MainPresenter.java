package com.newsreels.app.presenter;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.newsreels.app.APIResources.ApiClient;
import com.newsreels.app.R;
import com.newsreels.app.analytics.AnalyticsEvents;
import com.newsreels.app.analytics.Events;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.data.models.sources.Info;
import com.newsreels.app.data.models.topics.TopicsModel;
import com.newsreels.app.interfaces.MainInterface;
import com.newsreels.app.interfaces.MenuViewCallback;
import com.newsreels.app.utills.InternetCheckHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter {

    private Activity activity;
    private MainInterface mainInterface;
    private PrefConfig mPrefs;

    public MainPresenter(Activity activity, MainInterface mainInterface) {
        this.activity = activity;
        this.mainInterface = mainInterface;
        this.mPrefs = new PrefConfig(activity);
    }

    public void relatedToTopics(String id) {
        if (mainInterface == null) return;
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            mainInterface.loaderShow(true);
            Call<TopicsModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .relatedToTopics("Bearer " + mPrefs.getAccessToken(), id);
            call.enqueue(new Callback<TopicsModel>() {
                @Override
                public void onResponse(Call<TopicsModel> call, Response<TopicsModel> response) {
                    mainInterface.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            mainInterface.UserTopicSuccess(response.body().getTopics());
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                mainInterface.error(msg, -1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            mainInterface.error(msg, -1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TopicsModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getMessage(), -1);
                }
            });
        }
    }

    public void relatedToSources(String id) {
        if (mainInterface == null) return;
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            mainInterface.loaderShow(true);
            Call<Info> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .relatedToSources("Bearer " + mPrefs.getAccessToken(), id);
            call.enqueue(new Callback<Info>() {
                @Override
                public void onResponse(Call<Info> call, Response<Info> response) {
                    mainInterface.loaderShow(false);
                    switch (response.code()) {
                        case 200: {
                            if (response.body() != null) {
                                Log.e("Resdfd", "response : " + new Gson().toJson(response.body()));
                                mainInterface.UserInfoSuccess(response.body().getInfo());
                            } else {
                                mainInterface.error(response.message(), -1);
                            }
                        }
                        break;
                        default:
                            mainInterface.error(response.message(), -1);
                    }
                }

                @Override
                public void onFailure(Call<Info> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getMessage(), -1);
                }
            });
        }
    }

    public void updateConfig(String view_mode, boolean narration_enabled, String narration_mode, float reading_speed, boolean auto_scroll, boolean data_saver, boolean reader_mode, boolean videos_autoplay, boolean reels_autoplay, MenuViewCallback listener) {
        if (mainInterface == null) return;
        if (!InternetCheckHelper.isConnected()) {
            listener.success(false);
        } else {
            mainInterface.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .updateConfig("Bearer " + mPrefs.getAccessToken(), view_mode, narration_enabled, narration_mode, reading_speed, auto_scroll, data_saver, reader_mode, videos_autoplay, reels_autoplay);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mainInterface.loaderShow(false);
                    if (response.code() == 200) {
                        listener.success(true);
                    } else {
                        listener.success(false);
                        mainInterface.error(response.message(), -1);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    listener.success(false);
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getMessage(), -1);
                }
            });
        }
    }

    public void articleViewCountUpdate(String articleId) {
        AnalyticsEvents.INSTANCE.articleViewEvent(activity, articleId);
        Map<String, String> params = new HashMap<>();
        params.put(Events.KEYS.ARTICLE_ID, articleId);
        AnalyticsEvents.INSTANCE.logEventWithAPI(activity,
                params,
                Events.ARTICLE_VIEW);

//        Call<ResponseBody> call = ApiClient
//                .getInstance(activity)
//                .getApi()
//                .articleViewCount("Bearer " + mPrefs.getAccessToken(), articleId);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
//
//            }
//        });
    }

}
