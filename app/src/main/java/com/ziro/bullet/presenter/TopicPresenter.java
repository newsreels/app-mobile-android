package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.TopicInterface;
import com.ziro.bullet.model.FollowResponse;
import com.ziro.bullet.model.Menu.Category;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicPresenter {

    private Activity activity;
    private TopicInterface topicInterface;
    private PrefConfig mPrefconfig;

    public TopicPresenter(Activity activity, TopicInterface topicInterface) {
        this.activity = activity;
        this.topicInterface = topicInterface;
        this.mPrefconfig = new PrefConfig(activity);

    }

    public void topics(String page, boolean isPagination) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        Log.e("CALLXSXS", "page : " + page);
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        try {
            if (!InternetCheckHelper.isConnected()) {
                topicInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                topicInterface.loaderShow(true);
                Call<CategoryResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .getCategory("Bearer " + mPrefconfig.getAccessToken(), page);
                call.enqueue(new Callback<CategoryResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CategoryResponse> call, @NotNull Response<CategoryResponse> response) {
                        topicInterface.loaderShow(false);
                        if (response.body() != null) {
                            Log.e("RESPONSE", "RES: " + response.body());
                            topicInterface.success(response.body(), isPagination);
                        } else {
                            topicInterface.error(response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CategoryResponse> call, @NotNull Throwable t) {
                        topicInterface.loaderShow(false);
                        topicInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            topicInterface.error(t.getMessage());
        }
    }

    public void addTopics(ArrayList<String> topics) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
//        try {
//            if (!InternetCheckHelper.isConnected()) {
//                topicInterface.error(activity.getString(R.string.internet_error));
//                return;
//            } else {
//                Call<ResponseBody> call = ApiClient
//                        .getInstance(activity)
//                        .getApi()
//                        .addTopics("Bearer " + mPrefconfig.getAccessToken(), topics);
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
//                        topicInterface.loaderShow(false);
//                        if (response.body() != null) {
//                            Log.e("RESPONSE", "RES: " + response.body());
//                            if (response.code() == 200) {
//                                topicInterface.addSuccess(0);
//                            } else {
//                                topicInterface.error(response.message());
//                            }
//                        } else {
//                            topicInterface.error(response.message());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
//                        topicInterface.loaderShow(false);
//                        topicInterface.error(t.getMessage());
//                    }
//                });
//            }
//        } catch (Exception t) {
//            topicInterface.error(t.getMessage());
//        }
    }

    public void deleteTopics(int position, String id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                topicInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                Call<ResponseBody> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .unfollowTopic("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        topicInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response.isSuccessful()) {
                            topicInterface.deleteSuccess(position);
                        } else {
                            topicInterface.error(response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        topicInterface.loaderShow(false);
                        topicInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            topicInterface.error(t.getMessage());
        }
    }

    public void getTopicFollowPresenter(String id, int position, Category topic) {
        try {
            if (!InternetCheckHelper.isConnected()) {
                topicInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                topicInterface.loaderShow(true);
                Call<FollowResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .addTopicnew("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<FollowResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<FollowResponse> call, @NotNull Response<FollowResponse> response) {
                        topicInterface.loaderShow(false);
                    topicInterface.getTopicsFollow(response.body(), position, topic);
                    }

                    @Override
                    public void onFailure(@NotNull Call<FollowResponse> call, @NotNull Throwable t) {
                        topicInterface.loaderShow(false);
                        topicInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            topicInterface.error(t.getMessage());
            topicInterface.loaderShow(false);
        }
    }

    public void getTopicUnfollowPresenter(String id, int position, Category topic) {
        try {
            if (!InternetCheckHelper.isConnected()) {
                topicInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                topicInterface.loaderShow(true);
                Call<FollowResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .unfollowTopicNew("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<FollowResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<FollowResponse> call, @NotNull Response<FollowResponse> response) {
                        topicInterface.loaderShow(false);
                        topicInterface.getTopicsFollow(response.body(), position, topic);
//                        topicInterface.searchSuccess(response.body(), isPagination);
                    }

                    @Override
                    public void onFailure(@NotNull Call<FollowResponse> call, @NotNull Throwable t) {
                        topicInterface.loaderShow(false);
                        topicInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            topicInterface.error(t.getMessage());
            topicInterface.loaderShow(false);
        }
    }

    public void searchTopics(String query, String page, boolean isPagination) {
        topicInterface.loaderShow(true);
        try {
            if (!InternetCheckHelper.isConnected()) {
                topicInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                Call<CategoryResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .searchTopics("Bearer " + mPrefconfig.getAccessToken(), query, page);
                call.enqueue(new Callback<CategoryResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CategoryResponse> call, @NotNull Response<CategoryResponse> response) {
                        topicInterface.loaderShow(false);
                        topicInterface.searchSuccess(response.body(), isPagination);
                    }

                    @Override
                    public void onFailure(@NotNull Call<CategoryResponse> call, @NotNull Throwable t) {
                        topicInterface.loaderShow(false);
                        topicInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            topicInterface.error(t.getMessage());
            topicInterface.loaderShow(false);
        }
    }

    public void updateTopics(ArrayList<String> followList, ArrayList<String> unfollowList) {
        if (!InternetCheckHelper.isConnected()) {
            topicInterface.error(activity.getString(R.string.internet_error));
            return;
        } else {
            topicInterface.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .updateTopics("Bearer " + mPrefconfig.getAccessToken(), followList, unfollowList);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    topicInterface.loaderShow(false);
                    if (response.body() != null) {
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response.code() == 200) {
                            topicInterface.addSuccess(0);
                        } else {
                            topicInterface.error(response.message());
                        }
                    } else {
                        topicInterface.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    topicInterface.loaderShow(false);
                    topicInterface.error(t.getMessage());
                }
            });
        }
    }
}
