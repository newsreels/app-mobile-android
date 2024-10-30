package com.newsreels.app.presenter;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.newsreels.app.APIResources.ApiClient;
import com.newsreels.app.adapters.ManageLocationAdapter;
import com.newsreels.app.data.models.location.Location;
import com.newsreels.app.interfaces.HomeCallback;
import com.newsreels.app.model.ShareBottomSheetResponse;
import com.newsreels.app.data.PrefConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeaderPresenter {

    private Activity mContext;
    private PrefConfig mPrefConfig;
    private HomeCallback callback;

    public HeaderPresenter(Activity activity, HomeCallback callback) {
        this.mContext = activity;
        this.callback = callback;
        this.mPrefConfig = new PrefConfig(activity);
    }

    public void unfollowSource(String id) {
        if (callback != null) {
            callback.loaderShow(true);
        }
        Call<ShareBottomSheetResponse> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unfollowSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ShareBottomSheetResponse>() {
            @Override
            public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (response.code() == 200) {
                    if (callback != null) {
                        callback.success(null);
                    }
                } else {
                    if (callback != null) {
                        callback.error(null);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (callback != null) {
                    callback.error(null);
                }
            }
        });
    }

    public void followSource(String id) {
        if (callback != null) {
            callback.loaderShow(true);
        }
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .followSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (response.code() == 200) {
                    if (callback != null) {
                        callback.success(null);
                    }
                } else {
                    if (callback != null) {
                        callback.error(null);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (callback != null) {
                    callback.error(null);
                }
            }
        });
    }

    public void unfollowTopic(String id) {
        if (callback != null) {
            callback.loaderShow(true);
        }
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unfollowTopic("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (response.code() == 200) {
                    if (callback != null) {
                        callback.success(null);
                    }

                } else {
                    if (callback != null) {
                        callback.error(null);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (callback != null) {
                    callback.error(null);
                }
            }
        });
    }

    public void followTopic(String id) {
        if (callback != null) {
            callback.loaderShow(true);
        }
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .addTopic("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (response.code() == 200) {
                    if (callback != null) {
                        callback.success(null);
                    }
                } else {
                    if (callback != null) {
                        callback.error(null);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (callback != null) {
                    callback.error(null);
                }
            }
        });
    }


    public void unfollowLocation(String id) {
        if (callback != null) {
            callback.loaderShow(true);
        }
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unfollowLocation("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (response.code() == 200) {
                    if (callback != null) {
                        callback.success(null);
                    }
                } else {
                    if (callback != null) {
                        callback.error(null);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (callback != null) {
                    callback.error(null);
                }
            }
        });
    }

    public void followLocation(String id) {
        if (callback != null) {
            callback.loaderShow(true);
        }
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .followLocation("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (response.code() == 200) {
                    if (callback != null) {
                        callback.success(null);
                    }
                } else {
                    if (callback != null) {
                        callback.error(null);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                if (callback != null) {
                    callback.loaderShow(false);
                }
                if (callback != null) {
                    callback.error(null);
                }
            }
        });
    }
}
