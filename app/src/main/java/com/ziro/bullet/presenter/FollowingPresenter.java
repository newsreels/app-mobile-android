package com.ziro.bullet.presenter;

import android.app.Activity;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.interfaces.FollowingInterface;
import com.ziro.bullet.model.Tabs.DataItem;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingPresenter {

    private Activity activity;
    private FollowingInterface mainInterface;
    private PrefConfig mPrefs;
    private DbHandler cacheManager;

    public FollowingPresenter(Activity activity, FollowingInterface mainInterface) {
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

    public void followTopic(String id, DataItem dataItem, int position) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.loaderShow(false);
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        }
        Call<ResponseBody> call = ApiClient.getInstance(activity).getApi().addTopic("Bearer " + mPrefs.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mainInterface.loaderShow(false);
                if (response.isSuccessful()) {
                    mainInterface.onTopicFollowSuccess(dataItem, position);
                } else {
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainInterface.loaderShow(false);
                mainInterface.error(activity.getString(R.string.server_error), -1);
            }
        });
    }

    public void unFollowTopic(String id, DataItem dataItem, int position) {
        mainInterface.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            mainInterface.loaderShow(false);
            mainInterface.error(activity.getString(R.string.internet_error), -1);
        }
        Call<ResponseBody> call = ApiClient.getInstance(activity).getApi().unfollowTopic("Bearer " + mPrefs.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mainInterface.loaderShow(false);
                if (response.isSuccessful()) {
                    mainInterface.onTopicFollowSuccess(dataItem, position);
                } else {
                    mainInterface.error(activity.getString(R.string.server_error), -1);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainInterface.loaderShow(false);
                mainInterface.error(activity.getString(R.string.server_error), -1);
            }
        });
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

}
