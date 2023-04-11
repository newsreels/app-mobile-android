package com.ziro.bullet.presenter;

import android.app.Activity;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.auth.OAuthResponse;
import com.ziro.bullet.auth.TokenGenerator;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.BaseModel;
import com.ziro.bullet.interfaces.LanguageInterface;
import com.ziro.bullet.model.language.LanguageResponse;
import com.ziro.bullet.model.language.region.RegionApiResponse;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguagePresenter {
    private Activity activity;
    private LanguageInterface mCallback;
    private PrefConfig mPrefs;

    public LanguagePresenter(Activity activity, LanguageInterface mCallback) {
        this.activity = activity;
        this.mCallback = mCallback;
        this.mPrefs = new PrefConfig(activity);
    }

    public void getRegions() {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<RegionApiResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getRegions("Bearer " + mPrefs.getAccessToken());
            call.enqueue(new Callback<RegionApiResponse>() {
                @Override
                public void onResponse(@NotNull Call<RegionApiResponse> call, @NotNull Response<RegionApiResponse> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        mCallback.success(response.body());
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RegionApiResponse> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }
    }

    public void getPublicRegions() {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<RegionApiResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getPublicRegions();
            call.enqueue(new Callback<RegionApiResponse>() {
                @Override
                public void onResponse(@NotNull Call<RegionApiResponse> call, @NotNull Response<RegionApiResponse> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        mCallback.success(response.body());
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RegionApiResponse> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }
    }

    public void getLanguages(String regionId) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<LanguageResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getLanguageWithRegion("Bearer " + mPrefs.getAccessToken(), regionId);
            call.enqueue(new Callback<LanguageResponse>() {
                @Override
                public void onResponse(@NotNull Call<LanguageResponse> call, @NotNull Response<LanguageResponse> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        mCallback.success(response.body());
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LanguageResponse> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

    public void getLanguagesWithoutRegion(String regionId) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<LanguageResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getLanguageWithoutRegion("Bearer " + mPrefs.getAccessToken());
            call.enqueue(new Callback<LanguageResponse>() {
                @Override
                public void onResponse(@NotNull Call<LanguageResponse> call, @NotNull Response<LanguageResponse> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        mCallback.success(response.body());
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LanguageResponse> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

    public void getPublicLanguages(String regionId) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<LanguageResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getPublicLanguageWithRegion(regionId);
            call.enqueue(new Callback<LanguageResponse>() {
                @Override
                public void onResponse(@NotNull Call<LanguageResponse> call, @NotNull Response<LanguageResponse> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        mCallback.success(response.body());
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LanguageResponse> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

    public void updateRegion(String regionId) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<BaseModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .updateUserRegion("Bearer " + mPrefs.getAccessToken(), regionId);
            call.enqueue(new Callback<BaseModel>() {
                @Override
                public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        mCallback.success(response.body());
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }
    }

    public void updateLanguage(String langId, String tag) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<BaseModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .updateUserLanguage("Bearer " + mPrefs.getAccessToken(), langId, tag);
            call.enqueue(new Callback<BaseModel>() {
                @Override
                public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                    mCallback.loaderShow(false);
                    if (response.code() == 200) {
                        mCallback.success(response.body());
                    } else {
                        mCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }

    public void selectLanguage(String id, int position, String code) {
        if (!InternetCheckHelper.isConnected()) {
            mCallback.error(activity.getString(R.string.internet_error));
            mCallback.loaderShow(false);
        } else {
            mCallback.loaderShow(true);
            Call<LanguageResponse> call = OAuthAPIClient
                    .getInstance()
                    .getApi()
                    .updateLanguage("Bearer " + mPrefs.getAccessToken(), id, Constants.PRIMARY_LANGUAGE);
            call.enqueue(new Callback<LanguageResponse>() {
                @Override
                public void onResponse(@NotNull Call<LanguageResponse> call, @NotNull Response<LanguageResponse> response) {

                    if (response.code() == 200) {
                        new Thread(() -> {
                            // call send message here
                            try {
                                OAuthResponse oAuthResponse = new TokenGenerator().refreshToken(mPrefs.getRefreshToken(), mPrefs.isLanguagePushedToServer());
                                if (oAuthResponse.isSuccessful()) {
                                    mPrefs.setAccessToken(oAuthResponse.getAccessToken());
                                    mPrefs.setRefreshToken(oAuthResponse.getRefreshToken());
                                }
                                try {
                                    activity.runOnUiThread(() -> {
                                        mCallback.loaderShow(false);
//                                    mCallback.successLanguageSelection(id, position, code);
                                    });
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            } catch (Exception e1) {
                                try {
                                    activity.runOnUiThread(() -> {
                                        mCallback.loaderShow(false);
                                        if (activity != null)
                                            mCallback.error(activity.getString(R.string.server_error));
                                    });
                                }catch (Exception e2){
                                    e2.printStackTrace();
                                }
                                e1.printStackTrace();
                            }
                        }).start();

                    } else {
                        mCallback.loaderShow(false);
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                if (mCallback != null) {
                                    mCallback.error(msg);
                                }
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            if (mCallback != null) {
                                if (activity != null)
                                    mCallback.error(activity.getString(R.string.server_error));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LanguageResponse> call, @NotNull Throwable t) {
                    mCallback.loaderShow(false);
                    mCallback.error(t.getMessage());
                }
            });
        }
    }
}
