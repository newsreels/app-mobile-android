package com.ziro.bullet.presenter;

import android.app.Activity;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.UserChannelCallback;
import com.ziro.bullet.model.CategorizedChannels;
import com.ziro.bullet.model.UserChannels;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserChannelPresenter {

    private Activity activity;
    private UserChannelCallback userChannelCallback;
    private PrefConfig mPrefs;

    public UserChannelPresenter(Activity activity, UserChannelCallback userChannelCallback) {
        this.activity = activity;
        this.userChannelCallback = userChannelCallback;
        this.mPrefs = new PrefConfig(activity);
    }

    public void getUserChannels(String page) {
        if (!InternetCheckHelper.isConnected()) {
            if (userChannelCallback != null) {
                userChannelCallback.error(activity.getString(R.string.internet_error));
                userChannelCallback.loaderShow(false);
            }
        } else {
            if (userChannelCallback != null) {
                userChannelCallback.loaderShow(true);
            }
            Call<UserChannels> call = ApiClient
                    .getInstance()
                    .getApi()
                    .getChannels("Bearer " + mPrefs.getAccessToken(),page);
            call.enqueue(new Callback<UserChannels>() {
                @Override
                public void onResponse(@NotNull Call<UserChannels> call, @NotNull Response<UserChannels> response) {
                    if (userChannelCallback != null) {
                        userChannelCallback.loaderShow(false);
                    }
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (userChannelCallback != null) {
                                userChannelCallback.success(response.body().getChannels());
                            }
                            if (response.body().getChannels() != null) {
                                mPrefs.setUserChannels(response.body().getChannels());
                            }
                        }
                    } else {
                        if (userChannelCallback != null) {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = jsonObject.getString("message");
                                    userChannelCallback.error(msg);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                userChannelCallback.error(activity.getString(R.string.server_error));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<UserChannels> call, @NotNull Throwable t) {
                    if (userChannelCallback != null) {
                        userChannelCallback.loaderShow(false);
                        userChannelCallback.error(t.getMessage());
                    }
                }
            });
        }
    }

    public void getCategorizedChannels() {
        if (!InternetCheckHelper.isConnected()) {
            if (userChannelCallback != null) {
                userChannelCallback.error(activity.getString(R.string.internet_error));
                userChannelCallback.loaderShow(false);
            }
        } else {
            if (userChannelCallback != null) {
                userChannelCallback.loaderShow(true);
            }
            Call<CategorizedChannels> call = ApiClient
                    .getInstance()
                    .getApi()
                    .getChannelsCategorized("Bearer " + mPrefs.getAccessToken());
            call.enqueue(new Callback<CategorizedChannels>() {
                @Override
                public void onResponse(@NotNull Call<CategorizedChannels> call, @NotNull Response<CategorizedChannels> response) {
                    if (userChannelCallback != null) {
                        userChannelCallback.loaderShow(false);
                    }
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getChannelsData() != null) {
                            if (userChannelCallback != null) {
                                userChannelCallback.successData(response.body().getChannelsData());
                            }
                        }
                    } else {
                        if (userChannelCallback != null) {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = jsonObject.getString("message");
                                    userChannelCallback.error(msg);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                userChannelCallback.error(activity.getString(R.string.server_error));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<CategorizedChannels> call, @NotNull Throwable t) {
                    if (userChannelCallback != null) {
                        userChannelCallback.loaderShow(false);
                        userChannelCallback.error(t.getMessage());
                    }
                }
            });
        }
    }

    public void updateChannel(String articleID, String sourceID) {
        if (!InternetCheckHelper.isConnected()) {
            if (userChannelCallback != null) {
                userChannelCallback.error(activity.getString(R.string.internet_error));
                userChannelCallback.loaderShow(false);
            }
        } else {
            if (userChannelCallback != null) {
                userChannelCallback.loaderShow(true);
            }
            Call<ResponseBody> call = ApiClient
                    .getInstance()
                    .getApi()
                    .updateChannelForArticle("Bearer " + mPrefs.getAccessToken(), articleID, sourceID);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (userChannelCallback != null) {
                        userChannelCallback.loaderShow(false);
                    }
                    if (response.isSuccessful()) {
                        if (userChannelCallback != null) {
                            userChannelCallback.channelSelected();
                        }
                    } else {
                        if (userChannelCallback != null) {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = jsonObject.getString("message");
                                    userChannelCallback.error(msg);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                userChannelCallback.error(activity.getString(R.string.server_error));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    if (userChannelCallback != null) {
                        userChannelCallback.loaderShow(false);
                        userChannelCallback.error(t.getMessage());
                    }
                }
            });
        }
    }
}
