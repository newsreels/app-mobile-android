package com.ziro.bullet.presenter;

import static com.ziro.bullet.utills.Utils.getOutputMediaFile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.ziro.bullet.R;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.BaseModel;
import com.ziro.bullet.interfaces.ApiCallbacks;
import com.ziro.bullet.interfaces.ProfileApiCallback;
import com.ziro.bullet.model.Profile.UpdateResponse;
import com.ziro.bullet.model.Profile.UsernameCheckResponse;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePresenter {

    private Activity activity;
    private ProfileApiCallback profileApiCallback;
    private PrefConfig mPrefs;

    public ProfilePresenter(Activity activity, ProfileApiCallback mainInterface) {
        this.activity = activity;
        this.profileApiCallback = mainInterface;
        this.mPrefs = new PrefConfig(activity);
    }

    private MultipartBody.Part getBodyPartData(File file, String fieldName) {
        MultipartBody.Part result = null;
        if (file != null && !TextUtils.isEmpty(file.getPath())) {
            File file_ = getOutputMediaFile();
            OutputStream stream;
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                stream = new FileOutputStream(getOutputMediaFile());
                Utils.resize(bitmap, 600, 600).compress(Bitmap.CompressFormat.JPEG, 80, stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (file_ != null) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file_);
                result = MultipartBody.Part.createFormData(fieldName, file.getName(), requestFile);
            }
        }
        return result;
    }

    public void updateProfile(String name, File profilePic,
                              File coverPic, boolean fromLogin, String username) {

        Log.e("postArticle", "+++++++++++++++++++++++++++++++++++++++");
        Log.e("postArticle", "name : " + name);

        String accessToken = mPrefs.getAccessToken();
        String oldAccessToken = mPrefs.getOldAccessToken();
        if (fromLogin && !TextUtils.isEmpty(oldAccessToken)) {
            accessToken = oldAccessToken;
        }

        RequestBody usernameRequestBody = null;
        if (!TextUtils.isEmpty(username)) {
            usernameRequestBody = RequestBody.create(okhttp3.MultipartBody.FORM, username);
        }

        RequestBody nameRequestBody = null;
        if (name != null) {
            nameRequestBody = RequestBody.create(okhttp3.MultipartBody.FORM, name);
        }
        RequestBody mobRequestBody = RequestBody.create(okhttp3.MultipartBody.FORM, "");

        String img = null;
        MultipartBody.Part profilePicPart = null;
        if (profilePic != null) {
            profilePicPart = getBodyPartData(profilePic, "profile_image");
            img = "profile_image]";
        }
        MultipartBody.Part coverPicPart = null;
        if (coverPic != null) {
            coverPicPart = getBodyPartData(coverPic, "cover_image");
            img = "cover_image";
        }

        if (!InternetCheckHelper.isConnected()) {
            profileApiCallback.error(activity.getString(R.string.internet_error), null);
            profileApiCallback.loaderShow(false);
        } else {
            profileApiCallback.loaderShow(true);
            Call<UpdateResponse> call = OAuthAPIClient
                    .getInstance()
                    .getApi()
                    .updateProfile(
                            "Bearer " + accessToken,
                            usernameRequestBody,
                            nameRequestBody,
                            mobRequestBody,
                            profilePicPart,
                            coverPicPart
                    );
            String finalImg = img;
            call.enqueue(new Callback<UpdateResponse>() {
                @Override
                public void onResponse(@NotNull Call<UpdateResponse> call, @NotNull Response<UpdateResponse> response) {
                    profileApiCallback.loaderShow(false);
                    if (response.isSuccessful()) {
                        Log.e("HOMELIFE", "userTopics : here");
                        if (response.body() != null && response.body().getUser() != null) {
                            mPrefs.setUserProfile(response.body().getUser());
                            mPrefs.setUsername(response.body().getUser().getUsername());
                        }
                        profileApiCallback.success();
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                profileApiCallback.error(msg, finalImg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            profileApiCallback.error(activity.getString(R.string.server_error), finalImg);
                        }

                    }
                }

                @Override
                public void onFailure(@NotNull Call<UpdateResponse> call, @NotNull Throwable t) {
                    profileApiCallback.loaderShow(false);
                    profileApiCallback.error(t.getMessage(), finalImg);
                }
            });
        }
    }

    public void checkUsername(String username, ApiCallbacks apiCallbacks) {
        if (!InternetCheckHelper.isConnected()) {
            Utils.showPopupMessageWithCloseButton(activity, 2000, activity.getString(R.string.network_error), true);
            return;
        }
        apiCallbacks.loaderShow(true);
        Call<UsernameCheckResponse> call = OAuthAPIClient
                .getInstance()
                .getApi()
                .checkUsername(username);
        call.enqueue(new Callback<UsernameCheckResponse>() {
            @Override
            public void onResponse(@NotNull Call<UsernameCheckResponse> call, @NotNull Response<UsernameCheckResponse> response) {
                apiCallbacks.loaderShow(false);
                if (response.body() != null) {
                    apiCallbacks.success(response.body().isValid());
                } else {
                    apiCallbacks.success(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<UsernameCheckResponse> call, @NotNull Throwable t) {
                if (!call.isCanceled()) {
                    apiCallbacks.loaderShow(false);
                    apiCallbacks.error("Error");
                }
            }
        });
    }

    public void deleteAccount(String token, ApiCallbacks apiCallbacks) {
        if (!InternetCheckHelper.isConnected()) {
            Utils.showPopupMessageWithCloseButton(activity, 2000, activity.getString(R.string.network_error), true);
            return;
        }
        apiCallbacks.loaderShow(true);
        Call<BaseModel> call = OAuthAPIClient
                .getInstance()
                .getApi()
                .deleteAccount(token);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                apiCallbacks.loaderShow(false);
                if (response.body() != null) {
                    apiCallbacks.success(response.body());
                } else {
                    apiCallbacks.success(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {
                if (!call.isCanceled()) {
                    apiCallbacks.loaderShow(false);
                    apiCallbacks.error("Error");
                }
            }
        });
    }
}
