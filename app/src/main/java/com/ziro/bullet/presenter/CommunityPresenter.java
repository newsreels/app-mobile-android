package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.CommentsCounterInterface;
import com.ziro.bullet.interfaces.CommunityCallback;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Info;
import com.ziro.bullet.model.comment.CommentCounterResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityPresenter {

    private Activity activity;
    private CommunityCallback communityCallback;
    private PrefConfig mPrefs;
    private DbHandler cacheManager;

    public CommunityPresenter(Activity activity, CommunityCallback communityCallback) {
        this.activity = activity;
        this.communityCallback = communityCallback;
        this.mPrefs = new PrefConfig(activity);
        this.cacheManager = new DbHandler(activity);
    }

    public void getCommunityFeed(String page, boolean pagination) {

        if (!InternetCheckHelper.isConnected()) {
//            communityCallback.error(activity.getString(R.string.internet_error));
            communityCallback.loaderShow(false);
        } else {
            if (!pagination) {
                communityCallback.loaderShow(true);
            }
            Call<ArticleResponse> call = ApiClient
                    .getInstance()
                    .getApi()
                    .communityFeed("Bearer " + mPrefs.getAccessToken(), page);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                    communityCallback.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (cacheManager != null && TextUtils.isEmpty(page)) {
                                cacheManager.insertCommunityData("community", new Gson().toJson(response.body()));
                            }
                            communityCallback.success(response.body(), false);
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                communityCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            communityCallback.error(activity.getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                    communityCallback.loaderShow(false);
                    communityCallback.error(t.getMessage());
                }
            });
        }
    }

    public void counters(String id, CommentsCounterInterface commentsCounterInterface) {
        if (!InternetCheckHelper.isConnected()) {

        } else {
            Call<CommentCounterResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .commentCounter("Bearer " + mPrefs.getAccessToken(), id);
            call.enqueue(new Callback<CommentCounterResponse>() {
                @Override
                public void onResponse(@NotNull Call<CommentCounterResponse> call, @NotNull Response<CommentCounterResponse> response) {
                    if (response != null) {
                        Log.e("CALLXSXS", "code : " + response.code());
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Info info = response.body().getInfo();
                                if (info != null) {
                                    if (commentsCounterInterface != null)
                                        commentsCounterInterface.counter(info);
                                }
                            }
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = jsonObject.getString("message");
                                    if (communityCallback != null)
                                        communityCallback.error(msg);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                if (communityCallback != null && activity != null)
                                    communityCallback.error(activity.getString(R.string.server_error));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<CommentCounterResponse> call, @NotNull Throwable t) {
                    if (communityCallback != null && t != null)
                        communityCallback.error(t.getMessage());
                }
            });
        }
    }
    public void loadLocationArticle(String page, String id, boolean readerMode) {
        if (!InternetCheckHelper.isConnected()) {
            communityCallback.error(activity.getString(R.string.internet_error));
            communityCallback.loaderShow(false);
        } else {
            communityCallback.loaderShow(true);
            Call<ArticleResponse> call = ApiClient
                    .getInstance()
                    .getApi()
                    .relateLocation("Bearer " + mPrefs.getAccessToken(), id, readerMode, page);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                    communityCallback.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            communityCallback.success(response.body(), false);
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                communityCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            communityCallback.error(activity.getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                    communityCallback.loaderShow(false);
                    communityCallback.error(t.getMessage());
                }
            });
        }
    }
    public void loadRelatedArticle(String page, String id, boolean readerMode) {
        if (!InternetCheckHelper.isConnected()) {
            communityCallback.error(activity.getString(R.string.internet_error));
            communityCallback.loaderShow(false);
        } else {
            communityCallback.loaderShow(true);
            Call<ArticleResponse> call = ApiClient
                    .getInstance()
                    .getApi()
                    .relatedArticles("Bearer " + mPrefs.getAccessToken(), id, readerMode, page);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                    communityCallback.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            communityCallback.success(response.body(), false);
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                communityCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            communityCallback.error(activity.getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                    communityCallback.loaderShow(false);
                    communityCallback.error(t.getMessage());
                }
            });
        }
    }
}
