package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.VideoInterface;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReelPresenter {

    private Activity activity;
    private PrefConfig mPrefs;
    private VideoInterface videoInterface;
    //    private DbHandler cacheManager;
    private String TAG = "ReelPresenter";
    private String reel = "reels";

    public ReelPresenter(Activity activity, VideoInterface videoInterface) {
        this.activity = activity;
        this.videoInterface = videoInterface;
        this.mPrefs = new PrefConfig(activity);
//        this.cacheManager = new DbHandler(activity);
    }

    public void getVideos(String type, String context, String page, boolean reload, boolean isPagination, String hashtag) {
        if (!InternetCheckHelper.isConnected()) {
            videoInterface.error(activity.getString(R.string.internet_error));
            videoInterface.loaderShow(true);
            Toast.makeText(activity, "" + activity.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        } else {
            if (!TextUtils.isEmpty(mPrefs.getAccessToken())) {
//                videoInterface.loaderShow(!isPagination);
                Call<ReelResponse> call = ApiClient.getInstance(activity).getApi()
                        .newsReel("Bearer " + mPrefs.getAccessToken(), context, hashtag, page, type, BuildConfig.DEBUG);
                call.enqueue(new Callback<ReelResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ReelResponse> call, @NotNull Response<ReelResponse> response) {
                        videoInterface.loaderShow(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                videoInterface.success(response.body(), reload);
                            }
                        } else {
                            videoInterface.error(response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ReelResponse> call, @NotNull Throwable t) {
                        videoInterface.loaderShow(false);
                        videoInterface.error(t.getMessage());
                    }
                });
            }
        }
    }

    public void getReelsHome(String type, String context, String page, boolean reload, boolean showShimmer, boolean isPagination, String hashtag) {
//        Log.d(TAG, "getReelsHome: Token:: " + mPrefs.getAccessToken());
//        videoInterface.loaderShow(showShimmer);
        if (!InternetCheckHelper.isConnected()) {
            videoInterface.loaderShow(false);
            Toast.makeText(activity, "" + activity.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        } else {
            if (!TextUtils.isEmpty(mPrefs.getAccessToken())) {
//                if (showShimmer) {
//                    videoInterface.loaderShow(!isPagination);
//                }
                Call<ReelResponse> call = ApiClient.getInstance(activity).getApi()
                        .newsReel("Bearer " + mPrefs.getAccessToken(), context, hashtag, page, type, BuildConfig.DEBUG);
                call.enqueue(new Callback<ReelResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ReelResponse> call, @NotNull Response<ReelResponse> response) {
                        videoInterface.loaderShow(false);
                        if (response.isSuccessful()) {
                            ReelResponse body = response.body();
                            if (body != null) {
//                                if (TextUtils.isEmpty(page)) {
//                                    DbHandler dbHandler = new DbHandler(activity);
//                                    dbHandler.insertReelList("ReelsList", new Gson().toJson(body));
//                                }
                                videoInterface.success(body, reload);
                            }
                        } else {
                            videoInterface.error(response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ReelResponse> call, @NotNull Throwable t) {
                        videoInterface.loaderShow(false);
                        videoInterface.error(t.getMessage());
                    }
                });
            }
        }
    }

    public void searchReels(String type, String context, String query, String page, boolean reload, boolean showShimmer, boolean isPagination, String hashtag) {
        Log.d(TAG, "getReelsHome: Token:: " + mPrefs.getAccessToken());
        videoInterface.loaderShow(showShimmer);
        if (!InternetCheckHelper.isConnected()) {
            videoInterface.loaderShow(false);
            Toast.makeText(activity, "" + activity.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        } else {
            if (!TextUtils.isEmpty(mPrefs.getAccessToken())) {
//                if (showShimmer) {
//                    videoInterface.loaderShow(!isPagination);
//                }
                Call<ReelResponse> call = ApiClient.getInstance(activity).getApi()
                        .searchReels("Bearer " + mPrefs.getAccessToken(), context, hashtag, query, page, type, BuildConfig.DEBUG);
                call.enqueue(new Callback<ReelResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ReelResponse> call, @NotNull Response<ReelResponse> response) {
                        videoInterface.loaderShow(false);
                        if (response.isSuccessful()) {
                            ReelResponse body = response.body();
                            if (body != null) {
                                videoInterface.success(body, reload);
                            }
                        } else {
                            videoInterface.error(response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ReelResponse> call, @NotNull Throwable t) {
                        videoInterface.loaderShow(false);
                        videoInterface.error(t.getMessage());
                    }
                });
            }
        }
    }

    public void event(String id) {
        Map<String, String> params = new HashMap<>();
        params.put(Events.KEYS.ARTICLE_ID, id);
        AnalyticsEvents.INSTANCE.logEventWithAPI(activity,
                params,
                Events.REEL_VIEW);
    }
}
