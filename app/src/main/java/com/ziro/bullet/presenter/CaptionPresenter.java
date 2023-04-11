package com.ziro.bullet.presenter;

import android.app.Activity;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.caption.CaptionResponse;
import com.ziro.bullet.interfaces.AuthorApiCallback;
import com.ziro.bullet.interfaces.CaptionApiCallback;
import com.ziro.bullet.model.AuthorResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaptionPresenter {

    private Activity activity;
    private CaptionApiCallback mCallback;
    private PrefConfig mPrefs;

    public CaptionPresenter(Activity activity, CaptionApiCallback mainInterface) {
        this.activity = activity;
        this.mCallback = mainInterface;
        this.mPrefs = new PrefConfig(activity);
    }

    public void getCaptions(String reelId) {

        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<CaptionResponse> call = ApiClient
                    .getInstance()
                    .getApi()
                    .getReelCaptions("Bearer " + mPrefs.getAccessToken(), reelId);
            call.enqueue(new Callback<CaptionResponse>() {
                @Override
                public void onResponse(@NotNull Call<CaptionResponse> call, @NotNull Response<CaptionResponse> response) {
                    mCallback.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            mCallback.success(response.body());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                mCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            mCallback.error(activity.getString(R.string.server_error));
                        }

                    }
                }

                @Override
                public void onFailure(@NotNull Call<CaptionResponse> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }
    }
}
