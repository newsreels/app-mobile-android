package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.TCPInterface;
import com.ziro.bullet.model.TCP.TCPResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TCPPresenter {

    private Activity activity;
    private PrefConfig mPrefconfig;
    private TCPInterface mTCPInterface;

    public TCPPresenter(Activity activity, TCPInterface mTCPInterface) {
        this.activity = activity;
        this.mTCPInterface = mTCPInterface;
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void tcpData(String context, String type, String page) {

        String path = null;
        if (type.equalsIgnoreCase("IMAGE_TOPICS")) {
            path = "topics";
        } else if (type.equalsIgnoreCase("IMAGE_CHANNELS")) {
            path = "sources";
        } else if (type.equalsIgnoreCase("IMAGE_PLACES")) {
            path = "locations";
        }

        try {
            if (!InternetCheckHelper.isConnected()) {
                mTCPInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                mTCPInterface.loaderShow(true);
                Call<TCPResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .getDataTCP("Bearer " + mPrefconfig.getAccessToken(), path, context, page);
                call.enqueue(new Callback<TCPResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<TCPResponse> call, @NotNull Response<TCPResponse> response) {
                        mTCPInterface.loaderShow(false);
                        if (response.body() != null) {
                            if (TextUtils.isEmpty(page))
                                mTCPInterface.onTCPSuccess(type, response.body());
                            else
                                mTCPInterface.onTCPSuccessPagination(type, response.body());
                        } else {
                            mTCPInterface.error(response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<TCPResponse> call, @NotNull Throwable t) {
                        mTCPInterface.loaderShow(false);
                        mTCPInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            mTCPInterface.error(t.getMessage());
        }
    }
}
