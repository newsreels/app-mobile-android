package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.interfaces.CommentsCounterInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.ForYou;
import com.ziro.bullet.model.articles.Info;
import com.ziro.bullet.model.comment.CommentCounterResponse;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsPresenter {

    private Activity activity;
    private NewsCallback mainInterface;
    private PrefConfig mPrefs;
    private DbHandler cacheManager;
    private String TAG = "#@#@#@#@";

    public NewsPresenter(Activity activity, NewsCallback mainInterface) {
        this.activity = activity;
        this.mainInterface = mainInterface;
        this.mPrefs = new PrefConfig(activity);
        this.cacheManager = new DbHandler(activity);
    }

    public void loadSingleArticle(String id) {
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error));
            Constants.isApiCalling = false;
        } else {
            mainInterface.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .viewArticle("Bearer " + mPrefs.getAccessToken(), id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    mainInterface.loaderShow(false);
                    Log.e("CALLXSXS", "code : " + response.code());
                    if (response.code() == 200) {
                        try {
                            String responseString = null;
                            if (response.body() != null) {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                JSONObject articleObject = jsonObject.getJSONObject("article");
                                Article article = new Gson().fromJson(articleObject.toString(), Article.class);
                                mainInterface.successArticle(article);
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Constants.isApiCalling = false;
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error("");
                    Constants.isApiCalling = false;
                }
            });
        }
    }

    public void loadRelatedArticle(String id, String nextPage) {
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error));
            Constants.isApiCalling = false;
        } else {
            mainInterface.loaderShow(true);
            Constants.isApiCalling = true;

            Call<ArticleResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .relatedArticles("Bearer " + mPrefs.getAccessToken(), id, mPrefs.isReaderMode(), nextPage);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                    mainInterface.loaderShow(false);
                    Log.e("CALLXSXS", "code : " + response.code());
                    if (response.isSuccessful()) {
                        mainInterface.success(response.body(), false);
                    } else {
                        mainInterface.error("");
                    }
                    Constants.isApiCalling = false;
                }

                @Override
                public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getMessage());
                    Constants.isApiCalling = false;
                }
            });
        }
    }

    public void updateNews(String contextId, boolean isReaderMode, String page) {
        Log.e("#@#@#@#@", "contextId : " + contextId);

        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error));
            Constants.isApiCalling = false;
            return;
        } else {
            //during pagination dont show loader
            if (TextUtils.isEmpty(page))
                mainInterface.loaderShow(true);
            Call<ArticleResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getPaginatedNews("Bearer " + mPrefs.getAccessToken(), isReaderMode, page, contextId);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                    mainInterface.loaderShow(false);
                    Log.e("CALLXSXS", "code : " + response.code());
                    switch (response.code()) {
                        case 200:
                            if (response.body() != null) {
                                mainInterface.success(response.body(), false);
                            }
                            break;
                        case 404:
                            mainInterface.error404(response.message());
                            break;
                        default:
                            mainInterface.error(response.message());

                    }
                    Constants.isApiCalling = false;
                }

                @Override
                public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getMessage());
                    Constants.isApiCalling = false;
                }
            });
        }
    }

    public void updatedArticles(String contextId, boolean isReaderMode, String page, Boolean getReels) {
        Log.e("#@#@#@#@", "contextId : " + contextId);
        Log.e("###@@@", "11111111");

//        String json = Utils.getJsonFromAssets(activity, "sample_caption");
//        HomeResponse response = new Gson().fromJson(json, HomeResponse.class);
//        mainInterface.homeSuccess(response);

        long apiStartTime = System.currentTimeMillis();
        Log.e("@@##@@##", "Api started");


        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error));
            Constants.isApiCalling = false;
            return;
        } else {
            //during pagination dont show loader
            if (TextUtils.isEmpty(page))
                mainInterface.loaderShow(true);
            Call<HomeResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getUpdatedArticles("Bearer " + mPrefs.getAccessToken(), isReaderMode, page, contextId, String.valueOf(true));
            call.enqueue(new Callback<HomeResponse>() {
                @Override
                public void onResponse(@NotNull Call<HomeResponse> call, @NotNull Response<HomeResponse> response) {
                    mainInterface.loaderShow(false);
                    Log.e("CALLXSXS", "code : " + response.code());
                    switch (response.code()) {
                        case 200:
                            if (response.body() != null) {
                                Headers headers = response.headers();
                                String lastModified = headers.get("Last-Modified");
                                Log.e("###@@@", lastModified != null ? lastModified : "");
                                if (cacheManager != null && TextUtils.isEmpty(page)) {
                                    Log.e("##contextId@@", "contextId : " + contextId);
                                    cacheManager.UpdateHomeRecord(contextId, new Gson().toJson(response.body()), lastModified);
                                }
                                long apiEndTime = System.currentTimeMillis() - apiStartTime;
                                long apiEndTimeSec = apiEndTime / 1000;
                                Log.e("@@##@@##", "Api end - " + apiEndTime + " " + apiEndTimeSec);
                                mainInterface.homeSuccess(response.body(), page);
                            }
                            break;
                        case 404:
                            mainInterface.error404(response.message());
                            break;
                        default:
                            mainInterface.error(response.message());

                    }
                    Constants.isApiCalling = false;
                }

                @Override
                public void onFailure(@NotNull Call<HomeResponse> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getMessage());
                    Constants.isApiCalling = false;
                }
            });
        }
    }

    public void archiveArticles(String page) {

        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error));
            Constants.isApiCalling = false;
            return;
        } else {
            mainInterface.loaderShow(true);
            Call<ArticleResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getArchiveArticles("Bearer " + mPrefs.getAccessToken(), page);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                    mainInterface.loaderShow(false);
                    Log.e("CALLXSXS", "code : " + response.code());
                    switch (response.code()) {
                        case 200:
                            if (response.body() != null) {
                                mainInterface.success(response.body(), false);
                            }
                            break;
                        case 404:
                            mainInterface.error404(response.message());
                            break;
                        default:
                            mainInterface.error(response.message());

                    }
                    Constants.isApiCalling = false;
                }

                @Override
                public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getMessage());
                    Constants.isApiCalling = false;
                }
            });
        }
    }

    public void getArticlesForYou(String page) {
        Log.e("#@#@#@#@", "updateNews : " + page);

        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error));
            Constants.isApiCalling = false;
        } else {
            if (TextUtils.isEmpty(page))
                mainInterface.loaderShow(true);
            Call<ForYou> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getForYouArticles("Bearer " + mPrefs.getAccessToken(), mPrefs.isReaderMode(), page);
            call.enqueue(new Callback<ForYou>() {
                @Override
                public void onResponse(@NotNull Call<ForYou> call, @NotNull Response<ForYou> response) {
                    mainInterface.loaderShow(false);
                    switch (response.code()) {
                        case 200:
                            if (response.body() != null) {
//                                if (cacheManager != null && TextUtils.isEmpty(page)) {
//                                    cacheManager.UpdateForYouRecord(new Gson().toJson(response.body()));
//                                }
//                                mainInterface.successForYou(response.body(), false);
                            }
                            break;
                        case 404:
                            mainInterface.error404(response.message());
                            break;
                        default:
                            mainInterface.error(response.message());

                    }
                    Constants.isApiCalling = false;
                }

                @Override
                public void onFailure(@NotNull Call<ForYou> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getMessage());
                    Constants.isApiCalling = false;
                }
            });
        }
    }

    public void loadMyArticles(String status, String source, String nextPage) {
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error));
        } else {
            mainInterface.loaderShow(true);
            Call<ArticleResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getMyArticles("Bearer " + mPrefs.getAccessToken(), source, status, nextPage);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                    mainInterface.loaderShow(false);
                    if (response.isSuccessful()) {
                        mainInterface.success(response.body(), false);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getLocalizedMessage());
                }
            });
        }
    }

    public void loadDraftedPosts(String source, String nextPage) {
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error));
        } else {
            mainInterface.loaderShow(true);
            Call<ArticleResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getDraftedPosts("Bearer " + mPrefs.getAccessToken(), source, nextPage);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                    mainInterface.loaderShow(false);
                    if (response.isSuccessful()) {
                        mainInterface.success(response.body(), false);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(t.getLocalizedMessage());
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
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<CommentCounterResponse> call, @NotNull Throwable t) {

                }
            });
        }
    }
}
