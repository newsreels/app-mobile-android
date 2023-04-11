package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareBottomSheetPresenter {

    private Activity activity;
    private PrefConfig mPrefConfig;

    public ShareBottomSheetPresenter(Activity activity) {
        this.activity = activity;
        this.mPrefConfig = new PrefConfig(activity);
    }

    public void share_msg(String id, ShareInfoInterface listener) {
        try {
            if (!InternetCheckHelper.isConnected()) {
                if (listener != null) {
                    listener.error("");
                }
            } else {
                Call<ResponseBody> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .share_msg("Bearer " + mPrefConfig.getAccessToken(), id);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                ShareInfo shareInfo = null;
                                try {
                                    shareInfo = new Gson().fromJson(response.body().string(), ShareInfo.class);
                                    Log.e("RESP", "" + shareInfo.getShare_message());
                                    if (listener != null) {
                                        listener.response(shareInfo);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    if (listener != null) {
                                        listener.error("");
                                    }
                                }

                            }
                        } else if (response.code() == 400) {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    JSONObject errors = jsonObject.getJSONObject("errors");
                                    String msg = errors.getString("message");
                                    Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                                    if (listener != null) {
                                        listener.error(msg);
                                    }
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                if (listener != null) {
                                    listener.error("");
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.error("");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        if (listener != null) {
                            listener.error("");
                        }
                    }
                });
            }
        } catch (Exception t) {
            if (listener != null) {
                listener.error("");
            }
        }
    }
}
