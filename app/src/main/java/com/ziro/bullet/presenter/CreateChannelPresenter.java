package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.APIResources.ProgressRequestBody;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.CreateChannelCallback;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateChannelPresenter implements ProgressRequestBody.UploadCallbacks {

    private Activity activity;
    private CreateChannelCallback mCreateChannelCallback;
    private PrefConfig mPrefs;

    public CreateChannelPresenter(Activity activity, CreateChannelCallback mCreateChannelCallback) {
        this.activity = activity;
        this.mCreateChannelCallback = mCreateChannelCallback;
        this.mPrefs = new PrefConfig(activity);
    }

    public void createChannel(String name, String description, String icon) {
        if (!InternetCheckHelper.isConnected()) {
            mCreateChannelCallback.error(activity.getString(R.string.internet_error));
            mCreateChannelCallback.loaderShow(false);
        } else {
            mCreateChannelCallback.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance()
                    .getApi()
                    .createChannel("Bearer " + mPrefs.getAccessToken(), name, "", description, icon, "");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    mCreateChannelCallback.loaderShow(false);
                    if (response.code() == 200) {
                        mCreateChannelCallback.validName(true);
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                mCreateChannelCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            mCreateChannelCallback.error(activity.getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    mCreateChannelCallback.loaderShow(false);
                    mCreateChannelCallback.error(t.getMessage());
                }
            });
        }
    }

    public void checkValidName(String name) {
        if (!InternetCheckHelper.isConnected()) {
            mCreateChannelCallback.error(activity.getString(R.string.internet_error));
            mCreateChannelCallback.loaderShow(false);
        } else {
            mCreateChannelCallback.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance()
                    .getApi()
                    .checkChannelNameExist("Bearer " + mPrefs.getAccessToken(), name);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    mCreateChannelCallback.loaderShow(false);
                    if (response.isSuccessful()) {
                        try {
                            if (response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                boolean valid = jsonObject.getBoolean("valid");
                                mCreateChannelCallback.validName(valid);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                mCreateChannelCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            mCreateChannelCallback.error(activity.getString(R.string.server_error));
                        }

                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    mCreateChannelCallback.loaderShow(false);
                    mCreateChannelCallback.error(t.getMessage());
                }
            });
        }
    }

    public void uploadImageVideo(File file, String api, int requestType) {

        Log.e("postArticle", "+++++++++++++++++++++++++++++++++++++++");
        Log.e("postArticle", "file : " + file);
        Log.e("postArticle", "api : " + api);

        MultipartBody.Part image_ = null;
        if (file != null && !TextUtils.isEmpty(file.getPath())) {
            RequestBody requestFile = null;
            switch (api) {
                case "images":
                    requestFile = new ProgressRequestBody(file, "image/jpeg", this);
                    if (requestFile != null) {
                        image_ = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                    }
                    break;
                case "videos":
                    requestFile = new ProgressRequestBody(file, "video/mp4", this);
                    if (requestFile != null) {
                        image_ = MultipartBody.Part.createFormData("video", file.getName(), requestFile);
                    }
                    break;
            }

        } else {
            mCreateChannelCallback.error(api + " empty!");
            return;
        }

        if (!InternetCheckHelper.isConnected()) {
            mCreateChannelCallback.error(activity.getString(R.string.internet_error));
            mCreateChannelCallback.loaderShow(false);
        } else {
            mCreateChannelCallback.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .uploadImageVideo("Bearer " + mPrefs.getAccessToken(), api, image_);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    mCreateChannelCallback.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject != null) {
                                    switch (api) {
                                        case "images":
                                            mCreateChannelCallback.mediaUploaded(jsonObject.getString("results"), api, requestType);
                                            break;
                                        case "videos":
                                            mCreateChannelCallback.mediaUploaded(jsonObject.getString("key"), api, requestType);
                                            break;
                                    }
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                mCreateChannelCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            mCreateChannelCallback.error(activity.getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    mCreateChannelCallback.loaderShow(false);
                    mCreateChannelCallback.error(t.getMessage());
                }
            });
        }
    }

    @Override
    public void onProgressUpdate(int percentage) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }
}
