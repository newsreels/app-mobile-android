package com.ziro.bullet.presenter;

import android.app.Activity;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.interfaces.SearchInterface;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPresenter {

    private Activity activity;
    private SearchInterface mainInterface;
    private PrefConfig mPrefs;
    private DbHandler cacheManager;

    public SearchPresenter(Activity activity, SearchInterface mainInterface) {
        this.activity = activity;
        this.mainInterface = mainInterface;
        this.mPrefs = new PrefConfig(activity);
        this.cacheManager = new DbHandler(activity);
    }

    public void getFollowingTopics(String page) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<TopicsModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getFollowingTopics("Bearer " + mPrefs.getAccessToken(), page);
            call.enqueue(new Callback<TopicsModel>() {
                @Override
                public void onResponse(Call<TopicsModel> call, Response<TopicsModel> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onFollowedTopicsSuccess(response.body());
                }

                @Override
                public void onFailure(Call<TopicsModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getFollowingChannels(String page) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<SourceModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getFollowingSources("Bearer " + mPrefs.getAccessToken(), page);
            call.enqueue(new Callback<SourceModel>() {
                @Override
                public void onResponse(Call<SourceModel> call, Response<SourceModel> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onFollowedChannelsSuccess(response.body());
                }

                @Override
                public void onFailure(Call<SourceModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getFollowingLocations(String page) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<LocationModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getFollowedLocation("Bearer " + mPrefs.getAccessToken(), page);
            call.enqueue(new Callback<LocationModel>() {
                @Override
                public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onFollowedLocationSuccess(response.body());
                }

                @Override
                public void onFailure(Call<LocationModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getSuggestedTopics() {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<TopicsModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getSuggestedTopics("Bearer " + mPrefs.getAccessToken(), false);
            call.enqueue(new Callback<TopicsModel>() {
                @Override
                public void onResponse(Call<TopicsModel> call, Response<TopicsModel> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSuggestedTopicsSuccess(response.body());
                }

                @Override
                public void onFailure(Call<TopicsModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getSuggestedChannels(boolean hasReels) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<SourceModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getSuggestedChannels("Bearer " + mPrefs.getAccessToken(), hasReels);
            call.enqueue(new Callback<SourceModel>() {
                @Override
                public void onResponse(Call<SourceModel> call, Response<SourceModel> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSuggestedChannelsSuccess(response.body());
                }

                @Override
                public void onFailure(Call<SourceModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getSuggestedLocations() {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<LocationModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getSuggestedLocations("Bearer " + mPrefs.getAccessToken());
            call.enqueue(new Callback<LocationModel>() {
                @Override
                public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSuggestedLocationSuccess(response.body());
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
            Call<TopicsModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .searchPageTopics("Bearer " + mPrefs.getAccessToken(), query, page);
            call.enqueue(new Callback<TopicsModel>() {
                @Override
                public void onResponse(Call<TopicsModel> call, Response<TopicsModel> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSearchTopicsSuccess(response.body());
                }

                @Override
                public void onFailure(Call<TopicsModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
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

    public void searchLocations(String query, String page) {
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
                    mainInterface.onSearchLocationSuccess(response.body());
                }

                @Override
                public void onFailure(Call<LocationModel> call, Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void searchAuthors(String query, String page) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<AuthorSearchResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .searchAuthors("Bearer " + mPrefs.getAccessToken(), query, page);
            call.enqueue(new Callback<AuthorSearchResponse>() {
                @Override
                public void onResponse(@NotNull Call<AuthorSearchResponse> call, @NotNull Response<AuthorSearchResponse> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSearchAuthorsSuccess(response.body());
                }

                @Override
                public void onFailure(@NotNull Call<AuthorSearchResponse> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getFollowingAuthors(String page) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<AuthorSearchResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getFollowingAuthors("Bearer " + mPrefs.getAccessToken(), page);
            call.enqueue(new Callback<AuthorSearchResponse>() {
                @Override
                public void onResponse(@NotNull Call<AuthorSearchResponse> call, @NotNull Response<AuthorSearchResponse> response) {
                    mainInterface.loaderShow(false);
                    if (response.isSuccessful())
                        mainInterface.onFollowedAuthorsSuccess(response.body());
                    else
                        mainInterface.error(activity.getString(R.string.server_error), -1);
                }

                @Override
                public void onFailure(@NotNull Call<AuthorSearchResponse> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getSuggestedAuthors(boolean hasReels) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            Call<AuthorSearchResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getSuggestedAuthors("Bearer " + mPrefs.getAccessToken(), hasReels);
            call.enqueue(new Callback<AuthorSearchResponse>() {
                @Override
                public void onResponse(@NotNull Call<AuthorSearchResponse> call, @NotNull Response<AuthorSearchResponse> response) {
                    mainInterface.loaderShow(false);
                    if (response.isSuccessful())
                        mainInterface.onSuggestedAuthorsSuccess(response.body());
                    else
                        mainInterface.error(activity.getString(R.string.server_error), -1);
                }

                @Override
                public void onFailure(@NotNull Call<AuthorSearchResponse> call, @NotNull Throwable t) {
                    mainInterface.loaderShow(false);
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void getDirection(boolean shimmerShow, String page, String theme) {
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        } else {
            mainInterface.loaderShow(shimmerShow);
            Call<NewDiscoverResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getDiscover("Bearer " + mPrefs.getAccessToken(), theme, page);
            call.enqueue(new Callback<NewDiscoverResponse>() {
                @Override
                public void onResponse(@NotNull Call<NewDiscoverResponse> call, @NotNull Response<NewDiscoverResponse> response) {
                    mainInterface.loaderShow(false);
//                    Headers headers = response.headers();
//                    String lastModified = headers.get("Last-Modified");
//                    Log.e("###@@@", lastModified != null ? lastModified : "");
//                    if (cacheManager != null && TextUtils.isEmpty(page)) {
//                        cacheManager.insertDiscoverData("discover", new Gson().toJson(response.body()), lastModified);
//                    }
                    mainInterface.onDiscoverSuccess(response);
//                    Log.e("discover", " response = " + new Gson().toJson(response.body()) + "");
                }

                @Override
                public void onFailure(@NotNull Call<NewDiscoverResponse> call, @NotNull Throwable t) {
                    if (!call.isCanceled()) {
                        mainInterface.loaderShow(false);
                    }
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            });
        }
    }

    public void searchArticles(String query, String page) {
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
                    .searchArticles("Bearer " + mPrefs.getAccessToken(), query, readerMode, page);
            call.enqueue(new Callback<ArticleResponse>() {
                @Override
                public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                    mainInterface.loaderShow(false);
                    mainInterface.onSearchArticleSuccess(response.body(),true);
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
}
