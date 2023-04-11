package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.LikeInterface;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikePresenter {

    private Activity activity;
    private PrefConfig mPrefconfig;

    public LikePresenter(Activity activity) {
        this.activity = activity;
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void like(String article_id, LikeInterface likeInterface, boolean like) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        Log.e("CALLXSXS", "article_id : " + article_id);
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");

        try {
            if (!InternetCheckHelper.isConnected()) {
                if (likeInterface != null) {
                    likeInterface.failure();
                }
                return;
            } else {
                Map<String,String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID,article_id);
                AnalyticsEvents.INSTANCE.logEvent(activity,
                        params,
                        Events.FEED_LIKE);
                Call<ResponseBody> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .likeUnlikeArticle("Bearer " + mPrefconfig.getAccessToken(), article_id, like);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        Log.e("likeArticle", "RES: " + response.body());
                        Log.e("likeArticle", "old like: " + like);
                        if (response.code() == 200) {
                            if (likeInterface != null) {
                                likeInterface.success(like);
                            }
                        } else {
                            if (likeInterface != null) {
                                likeInterface.failure();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        Log.e("likeArticle", "RES: " + t.getMessage());
                        if (likeInterface != null) {
                            likeInterface.failure();
                        }
                    }
                });
            }
        } catch (Exception t) {
            Log.e("likeArticle", "RES: " + t.getMessage());
            if (likeInterface != null) {
                likeInterface.failure();
            }
        }
    }

    public void event(String id) {
        Map<String,String> params = new HashMap<>();
        params.put(Events.KEYS.REEL_ID,id);
        AnalyticsEvents.INSTANCE.logEventWithAPI(activity,
                params,
                Events.REEL_VIEW);
//        if (!InternetCheckHelper.isConnected()) {
//            return;
//        }
//        if (!TextUtils.isEmpty(mPrefconfig.getAccessToken())) {
//            Call<ResponseBody> call = ApiClient.getInstance(activity).getApi()
//                    .event("Bearer " + mPrefconfig.getAccessToken(), id);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
//
//                }
//
//                @Override
//                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
//
//                }
//            });
//        }
    }
}
