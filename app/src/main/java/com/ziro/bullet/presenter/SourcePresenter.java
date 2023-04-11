package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.interfaces.SourceInterface;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.CategoryPaginationModel;
import com.ziro.bullet.data.models.sources.SourceModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SourcePresenter {

    private Activity activity;
    private SourceInterface sourceInterface;
    private PrefConfig mPrefconfig;

    public SourcePresenter(Activity activity, SourceInterface sourceInterface) {
        this.activity = activity;
        this.sourceInterface = sourceInterface;
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void AllSources(String page,boolean setAdapter) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                sourceInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                sourceInterface.loaderShow(true);
                Call<SourceModel> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .getSources("Bearer " + mPrefconfig.getAccessToken(), page);
                call.enqueue(new Callback<SourceModel>() {
                    @Override
                    public void onResponse(@NotNull Call<SourceModel> call, @NotNull Response<SourceModel> response) {
                        Log.d("TAG", "onResponse: ");
                        sourceInterface.loaderShow(false);
                        sourceInterface.success(response.body(), setAdapter);
                    }

                    @Override
                    public void onFailure(@NotNull Call<SourceModel> call, @NotNull Throwable t) {
                        Log.d("TAG", "onResponse: ");
                        sourceInterface.loaderShow(false);
                        if (t != null)
                            sourceInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            sourceInterface.error(t.getMessage());
            sourceInterface.loaderShow(false);
        }
    }

    public void sourcesInternational(String page) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "page : " + page);
        try {
            if (!InternetCheckHelper.isConnected()) {
                sourceInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                sourceInterface.loaderShow(true);
                Call<SourceModel> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .getSources("Bearer " + mPrefconfig.getAccessToken(), page);
                call.enqueue(new Callback<SourceModel>() {
                    @Override
                    public void onResponse(@NotNull Call<SourceModel> call, @NotNull Response<SourceModel> response) {
                        sourceInterface.loaderShow(false);
                        if (response != null && response.body() != null && response.body().getSources() != null) {
                            sourceInterface.successInternationalPaginationResult(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<SourceModel> call, @NotNull Throwable t) {
                        Log.d("CALLXSXS", "onResponse: ");
                        sourceInterface.loaderShow(false);
                        if (t != null)
                            sourceInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            sourceInterface.error(t.getMessage());
            sourceInterface.loaderShow(false);
        }
    }

    public void sourcesLocal(int page) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "page : " + page);
        try {
            if (!InternetCheckHelper.isConnected()) {
                sourceInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                sourceInterface.loaderShow(true);
                Call<CategoryPaginationModel> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .sourcesLocal("Bearer " + mPrefconfig.getAccessToken(), page);
                call.enqueue(new Callback<CategoryPaginationModel>() {
                    @Override
                    public void onResponse(@NotNull Call<CategoryPaginationModel> call, @NotNull Response<CategoryPaginationModel> response) {
//                        Log.e("CALLXSXS", "response : " + new Gson().toJson(response));
                        sourceInterface.loaderShow(false);
                        if (response != null && response.body() != null && response.body().getCategoryModel() != null) {
                            sourceInterface.successLocalPaginationResult(response.body().getCategoryModel());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CategoryPaginationModel> call, @NotNull Throwable t) {
                        Log.d("TAG", "onResponse: ");
                        sourceInterface.loaderShow(false);
                        if (t != null)
                            sourceInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            sourceInterface.error(t.getMessage());
        }
    }

    public void addSources(ArrayList<String> topics) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                sourceInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                Call<ResponseBody> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .followSource("Bearer " + mPrefconfig.getAccessToken(), topics);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        sourceInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response.code() == 200) {
                            sourceInterface.addSuccess(0);
                        } else if (response.code() == 400) {
                            sourceInterface.error("Minimum 3 sources required");
                        } else {
                            sourceInterface.error(response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        sourceInterface.loaderShow(false);
                        sourceInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            sourceInterface.error(t.getMessage());
        }
    }

    public void searchSources(String data, String page) {
        Log.e("LASTPAGE", "Call with page  = " + page);

        sourceInterface.loaderShow(true);
        try {
            if (!InternetCheckHelper.isConnected()) {
                sourceInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                Call<SourceModel> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .searchSources("Bearer " + mPrefconfig.getAccessToken(), data);
                call.enqueue(new Callback<SourceModel>() {
                    @Override
                    public void onResponse(@NotNull Call<SourceModel> call, @NotNull Response<SourceModel> response) {
                        sourceInterface.loaderShow(false);
                        sourceInterface.searchSuccess(response.body());
                    }

                    @Override
                    public void onFailure(@NotNull Call<SourceModel> call, @NotNull Throwable t) {
                        sourceInterface.loaderShow(false);
                        sourceInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            sourceInterface.error(t.getMessage());
            sourceInterface.loaderShow(false);
        }
    }


    public void updateSource(ArrayList<String> followList, ArrayList<String> unfollowList){
        if (!InternetCheckHelper.isConnected()) {
            sourceInterface.error(activity.getString(R.string.internet_error));
            return;
        } else {
            sourceInterface.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .updateSources("Bearer " + mPrefconfig.getAccessToken(), followList,unfollowList);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    sourceInterface.loaderShow(false);
                    Log.e("RESPONSE", "RES: " + response.body());
                    if (response.code() == 200) {
                        sourceInterface.addSuccess(0);
                    } else if (response.code() == 400) {
                        sourceInterface.error(response.message());
                    } else {
                        sourceInterface.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    sourceInterface.loaderShow(false);
                    sourceInterface.error(t.getMessage());
                }
            });
        }
    }
}
