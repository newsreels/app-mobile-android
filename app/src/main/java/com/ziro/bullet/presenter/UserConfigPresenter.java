package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.config.Narration;
import com.ziro.bullet.data.models.config.Terms;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.data.models.userInfo.UserInfoModel;
import com.ziro.bullet.interfaces.AccountLinkCallback;
import com.ziro.bullet.interfaces.UserConfigCallback;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConfigPresenter {

    private Activity activity;
    private UserConfigCallback userConfigCallback;
    private PrefConfig mPrefconfig;

    public UserConfigPresenter(Activity activity, UserConfigCallback callback) {
        this.activity = activity;
        this.userConfigCallback = callback;
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void getUserConfig(boolean useOldToken) {
        try {
            activity.runOnUiThread(() ->
                    userConfigCallback.loaderShow(true)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!InternetCheckHelper.isConnected()) {
            try {
                activity.runOnUiThread(() -> {
                            userConfigCallback.error(activity.getString(R.string.internet_error));
//                        userConfigCallback.onUserConfigSuccess(mPrefconfig.getUserConfig());
                            userConfigCallback.loaderShow(false);
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Call<UserConfigModel> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .userConfig("Bearer " + (useOldToken ? mPrefconfig.getOldAccessToken() : mPrefconfig.getAccessToken()));

            call.enqueue(new Callback<UserConfigModel>() {
                @Override
                public void onResponse(Call<UserConfigModel> call, Response<UserConfigModel> response) {
                    try {
                        activity.runOnUiThread(() ->
                                userConfigCallback.loaderShow(false)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            mPrefconfig.setUserConfig(response.body());
                            mPrefconfig.setWalletUrl(response.body().getWallet());
                            if (response.body().getHomePreference() != null) {
                                mPrefconfig.setMenuViewMode(response.body().getHomePreference().getViewMode());
                                mPrefconfig.setMenuNarration(response.body().getHomePreference().getNarration());
                                Narration narration = mPrefconfig.getMenuNarration();
                                narration.setMuted(false); // by default mute that's why set it to false always
                                mPrefconfig.setMenuNarration(narration);
                                mPrefconfig.setTapIntro(response.body().getHomePreference().isTutorial_done());
                                mPrefconfig.setReaderMode(response.body().getHomePreference().isReaderMode());
                                mPrefconfig.setBulletsAutoPlay(response.body().getHomePreference().isDataSaver());
                                mPrefconfig.setReelsAutoPlay(response.body().getHomePreference().isReels_autoplay());
//                                mPrefconfig.setVideoAutoPlay(response.body().getHomePreference().isVideos_autoplay());
                            }
                            mPrefconfig.setHomeImage(response.body().getmStatic());
                            mPrefconfig.setEditions(response.body().getEditions());
                            mPrefconfig.setRegions(response.body().getRegionsList());
                            mPrefconfig.setAds(response.body().getAds());
                            mPrefconfig.setRatingInterval(response.body().getRating().getInterval());
                            mPrefconfig.setLongRatingInterval(response.body().getRating().getInterval());
                            User user = response.body().getUser();
                            if (user != null) {
                                if (!TextUtils.isEmpty(user.getEmail())) {
                                    mPrefconfig.setUserEmail(user.getEmail());
                                }
                                mPrefconfig.setUsername(user.getUsername());
                                mPrefconfig.setHasPassword(user.getHasPassword());
                                mPrefconfig.setUserProfile(user);
                                mPrefconfig.setGuestUser(!user.isGuestValid());
                            }
                            Terms terms = response.body().getTerms();
                            if (terms != null) {
                                mPrefconfig.setGuidelines(terms.getCommunity());
                            }
                            mPrefconfig.setUserChannels(response.body().getChannels());
                            Map<Float, Float> tempMap = new HashMap<>();
                            if (response.body().getHomePreference() != null
                                    && response.body().getHomePreference().getNarration() != null) {
                                tempMap.put(1.0f, response.body().getHomePreference().getNarration().getSpeedRate().getSpeed1x());
                                tempMap.put(1.5f, response.body().getHomePreference().getNarration().getSpeedRate().getSpeed1_point5x());
                                tempMap.put(2.0f, response.body().getHomePreference().getNarration().getSpeedRate().getSpeed2x());
                                tempMap.put(0.75f, response.body().getHomePreference().getNarration().getSpeedRate().getSpeed_point75x());
                                tempMap.put(0.5f, response.body().getHomePreference().getNarration().getSpeedRate().getSpeed_point5x());

                                Constants.READING_SPEED_RATES = tempMap;

                                switch (response.body().getHomePreference().getNarration().getReadingSpeed()) {
                                    case "1.0x":
                                        Constants.reading_speed = 1.0f;
                                        break;
                                    case "0.5x":
                                        Constants.reading_speed = 0.5f;
                                        break;
                                    case "0.75x":
                                        Constants.reading_speed = 0.75f;
                                        break;
                                    case "1.5x":
                                        Constants.reading_speed = 1.5f;
                                        break;
                                    case "2.0x":
                                        Constants.reading_speed = 2.0f;
                                        break;
                                }
                            }
                            try {
                                activity.runOnUiThread(() -> userConfigCallback.onUserConfigSuccess(response.body()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                try {
                                    activity.runOnUiThread(() -> userConfigCallback.error("" + msg));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException | IOException e) {
                            try {
                                activity.runOnUiThread(() -> {
                                    userConfigCallback.error("");
                                    userConfigCallback.loaderShow(false);
                                    showToast(activity.getString(R.string.internet_error));
                                });
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserConfigModel> call, Throwable t) {
                    try {
                        activity.runOnUiThread(() -> {
                            userConfigCallback.loaderShow(false);
                            userConfigCallback.error("");
                            showToast(activity.getString(R.string.server_error));
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    public void getUserInfo() {
        Call<UserInfoModel> call = ApiClient
                .getInstance(activity)
                .getApi()
                .userInfo("Bearer " + mPrefconfig.getAccessToken());

        call.enqueue(new Callback<UserInfoModel>() {
            @Override
            public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
                if (response.code() == 200) {
                    UserInfoModel user = response.body();
                    if (user != null && user.getUser() != null && !TextUtils.isEmpty(user.getUser().getEmail())) {
                        mPrefconfig.setUserEmail(user.getUser().getEmail());
                        mPrefconfig.setHasPassword(user.getUser().getHasPassword());
                    }
                    userConfigCallback.onUserConfigSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<UserInfoModel> call, Throwable t) {

            }
        });
    }

    private void swapToken() {
        String newToken = mPrefconfig.getAccessToken();
        String oldToken = mPrefconfig.getOldAccessToken();
        boolean tokenSwapped = mPrefconfig.getTokenSwapped();

        if (oldToken != null && !tokenSwapped) {
            mPrefconfig.setAccessToken(oldToken);
            mPrefconfig.setOldAccessToken(newToken);
            mPrefconfig.setTokenSwapped(true);
        }
    }

    public void linkAccount(AccountLinkCallback accountLinkCallback) {
        String oldToken = mPrefconfig.getAccessToken();
//        String oldToken =  mPrefconfig.getOldAccessToken();
        swapToken();

        accountLinkCallback.loaderShow(true);
        Call<ResponseBody> call = OAuthAPIClient
                .getInstance()
                .getApi()
                .linkAccount("Bearer " + mPrefconfig.getAccessToken(), oldToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                Log.e("linkAccount @@@@", "onResponse: ");
                accountLinkCallback.loaderShow(false);
                if (response.code() == 200) {
                    accountLinkCallback.onLinkSuccess(true);
                } else if (response.code() == 409) {
                    try {
                        if (response.errorBody() != null) {
                            String s = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(s);
                            String msg = jsonObject.getString("error");
                            accountLinkCallback.error(msg);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            String s = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(s);
                            String msg = jsonObject.getString("message");
                            accountLinkCallback.error(msg);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.e("linkAccount @@@@", "onResponse: ");
                accountLinkCallback.loaderShow(false);
                accountLinkCallback.error("");
            }
        });

    }

    private void showToast(String string) {
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
    }
}
