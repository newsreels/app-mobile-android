package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.EditionInterface;
import com.ziro.bullet.model.Edition.ResponseEdition;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditionPresenter {
    private Activity activity;
    private EditionInterface mCallback;
    private PrefConfig mPrefs;

    public EditionPresenter(Activity activity, EditionInterface mCallback) {
        this.activity = activity;
        this.mCallback = mCallback;
        this.mPrefs = new PrefConfig(activity);
    }

//    public void getEdition(String searchStr, String next, String parent, int position) {
//        if (!InternetCheckHelper.isConnected()) {
//            mCallback.error(activity.getString(R.string.internet_error));
//            mCallback.loaderShow(false);
//        } else {
//            mCallback.loaderShow(true);
//            Call<ResponseEdition> call = ApiClient
//                    .getInstance(activity)
//                    .getApi()
//                    .getEditions("Bearer " + mPrefs.getAccessToken(), parent);
//            call.enqueue(new Callback<ResponseEdition>() {
//                @Override
//                public void onResponse(@NotNull Call<ResponseEdition> call, @NotNull Response<ResponseEdition> response) {
//                    mCallback.loaderShow(false);
//                    if (response.code() == 200) {
//                        Log.e("HOMELIFE", "userTopics : here");
//                        mCallback.success(response.body(), parent, position);
//                    } else {
//                        mCallback.error(response.message());
//                    }
//                }
//
//                @Override
//                public void onFailure(@NotNull Call<ResponseEdition> call, @NotNull Throwable t) {
//                    mCallback.loaderShow(false);
//                    mCallback.error(t.getMessage());
//                }
//            });
//        }
//    }

    public void getEdition(String searchStr, String next) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<ResponseEdition> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getEditions("Bearer " + mPrefs.getAccessToken(), searchStr, next);
            call.enqueue(new Callback<ResponseEdition>() {
                @Override
                public void onResponse(@NotNull Call<ResponseEdition> call, @NotNull Response<ResponseEdition> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        Log.e("HOMELIFE", "userTopics : here");
                        mCallback.success(response.body());
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseEdition> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }
    }

    public void followEdition(int position, String id, boolean isFollow) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .followEdition("Bearer " + mPrefs.getAccessToken(), id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        Log.e("HOMELIFE", "userTopics : here");
                        mCallback.successFollowUnfollow(position, id, isFollow);
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }
    }

    public void unFollowEdition(int position, String id, boolean isFollow) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .unfollowEdition("Bearer " + mPrefs.getAccessToken(), id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        Log.e("HOMELIFE", "userTopics : here");
                        mCallback.successFollowUnfollow(position, id, isFollow);
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }
    }


    public void updateEditions(ArrayList<String> followList, ArrayList<String> unfollowList, boolean force) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            return;
        } else {
            mCallback.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .updateEditions("Bearer " + mPrefs.getAccessToken(), followList, unfollowList, force);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    mCallback.loaderShow(false);
                    if (response.body() != null) {
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response.code() == 200) {
                            mCallback.onUpdateSuccess();
                        } else {
                            mCallback.error(response.message());
                        }
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }

    }

    public void getSelectedEditions() {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
        } else {
            mCallback.loaderShow(true);
            Call<ResponseEdition> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getSelectedEditions("Bearer " + mPrefs.getAccessToken());
            call.enqueue(new Callback<ResponseEdition>() {
                @Override
                public void onResponse(@NotNull Call<ResponseEdition> call, @NotNull Response<ResponseEdition> response) {
                    mCallback.loaderShow(false);
                    if (response.body() != null) {
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response.code() == 200) {
                            mCallback.successFollowed(response.body());
                        } else {
                            mCallback.error(response.message());
                        }
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseEdition> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }

    }
}
