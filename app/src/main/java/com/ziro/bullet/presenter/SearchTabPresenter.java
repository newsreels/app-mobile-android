package com.ziro.bullet.presenter;

import android.app.Activity;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.interfaces.SearchTabsInterface;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTabPresenter {

    private Activity activity;
    private SearchTabsInterface mainInterface;
    private PrefConfig mPrefs;
    private DbHandler cacheManager;

    public SearchTabPresenter(Activity activity, SearchTabsInterface mainInterface) {
        this.activity = activity;
        this.mainInterface = mainInterface;
        this.mPrefs = new PrefConfig(activity);
        this.cacheManager = new DbHandler(activity);
    }

    public void searchChannels(String query, String page) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<SourceModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .searchPageSources("Bearer " + mPrefs.getAccessToken(), query, page);
            call.enqueue(new Callback<SourceModel>() {
                @Override
                public void onResponse(Call<SourceModel> call, Response<SourceModel> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSearchChannelsSuccess(response.body());
                }

                @Override
                public void onFailure(Call<SourceModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void searchPlaces(String query, String page) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<LocationModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .searchLocations("Bearer " + mPrefs.getAccessToken(), query, page);
            call.enqueue(new Callback<LocationModel>() {
                @Override
                public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSearchPlacesSuccess(response.body());
                }

                @Override
                public void onFailure(Call<LocationModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void searchTopics(String query, String page) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<CategoryResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .searchTopics("Bearer " + mPrefs.getAccessToken(), query, page);
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSearchTopicsSuccess(response.body());
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void searchArticles(String query, String page, Boolean pagination) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            boolean readerMode = false;
            if (mPrefs != null)
                readerMode = mPrefs.isReaderMode();
            Call<ArticleResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .searchArticles("Bearer " + mPrefs.getAccessToken(), query, false, page);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSearchArticleSuccess(response.body(), pagination);
                }

                @Override
                public void onFailure(Call<ArticleResponse> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getRelevantArticles(String context, String page) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            boolean readerMode = false;
            if (mPrefs != null)
                readerMode = mPrefs.isReaderMode();
            Call<ArticleResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getPaginatedNews("Bearer " + mPrefs.getAccessToken(), readerMode, page, context);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onRelevantArticlesSuccess(response.body());
                }

                @Override
                public void onFailure(Call<ArticleResponse> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getRelevant(String query) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<RelevantResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getRelevant("Bearer " + mPrefs.getAccessToken(), query, mPrefs.isReaderMode());
            call.enqueue(new Callback<RelevantResponse>() {
                @Override
                public void onResponse(Call<RelevantResponse> call, Response<RelevantResponse> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onRelevantSuccess(response.body());
                }

                @Override
                public void onFailure(Call<RelevantResponse> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getReelsHome(String type, String context, String query, String page, boolean reload, boolean showShimmer, boolean isPagination, String hashtag) {
        String query_up = query;
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<ReelResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
//                    .searchReels("Bearer " + mPrefs.getAccessToken(), query_up, page);
                    .searchReels("Bearer " + mPrefs.getAccessToken(), context, hashtag, query_up, page, type, BuildConfig.DEBUG);
            call.enqueue(new Callback<ReelResponse>() {
                @Override
                public void onResponse(Call<ReelResponse> call, Response<ReelResponse> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onReelSuccess(response.body());
                }

                @Override
                public void onFailure(Call<ReelResponse> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }


}
