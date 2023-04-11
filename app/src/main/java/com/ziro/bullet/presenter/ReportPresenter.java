package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.interfaces.ReportBottomSheetListener;
import com.ziro.bullet.model.Report.ReportResponse;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.data.PrefConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportPresenter {

    private Activity context;
    private ReportBottomSheetListener interfacee;
    private PrefConfig mPrefconfig;

    public ReportPresenter(Activity context, ReportBottomSheetListener interfacee) {
        this.context = context;
        this.interfacee = interfacee;
        this.mPrefconfig = new PrefConfig(context);
    }

    public void getConcerns(String article_id, String type) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true);
                Call<ReportResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .getConcerns("Bearer " + mPrefconfig.getAccessToken(), type, article_id);
                call.enqueue(new Callback<ReportResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ReportResponse> call, @NotNull Response<ReportResponse> response) {
                        interfacee.loaderShow(false);
                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getConcerns());
                            }
                        } else if (response.code() == 400) {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    JSONObject errors = jsonObject.getJSONObject("errors");
                                    String msg = errors.getString("message");
                                    Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ReportResponse> call, @NotNull Throwable t) {
                        interfacee.error(t.getMessage());
                        interfacee.loaderShow(false);
                    }
                });
            }
        } catch (Exception t) {
            interfacee.error(t.getMessage());
            interfacee.loaderShow(false);
        }
    }

    public void sendReport(ArrayList<String> concerns, String article_id, String type) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true);
                Call<ResponseBody> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .sendReport("Bearer " + mPrefconfig.getAccessToken(), type, article_id, concerns);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        interfacee.loaderShow(false);
                        if (response.code() == 200) {
                            interfacee.success();
                        } else if (response.code() == 400) {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    JSONObject errors = jsonObject.getJSONObject("errors");
                                    String msg = errors.getString("message");
                                    Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        interfacee.error(t.getMessage());
                        interfacee.loaderShow(false);
                    }
                });
            }
        } catch (Exception t) {
            interfacee.error(t.getMessage());
            interfacee.loaderShow(false);
        }
    }

}
