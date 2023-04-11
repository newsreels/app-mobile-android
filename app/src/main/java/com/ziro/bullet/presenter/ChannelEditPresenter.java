package com.ziro.bullet.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.channels.UpdateChannelResponse;
import com.ziro.bullet.interfaces.ChannelApiCallback;
import com.ziro.bullet.interfaces.ProfileApiCallback;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ziro.bullet.utills.Utils.getOutputMediaFile;

public class ChannelEditPresenter {

    private Activity activity;
    private ChannelApiCallback channelApiCallback;
    private PrefConfig mPrefs;

    public ChannelEditPresenter(Activity activity, ChannelApiCallback channelApiCallback) {
        this.activity = activity;
        this.channelApiCallback = channelApiCallback;
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

    public void updateChannelProfile(String channelId, String description, String profilePic, String coverPic, String coverPicPort, boolean editDesc) {


        String img = null;
        if (profilePic != null) {
            img = "icon";
        }
        if (coverPic != null) {
            img = "cover";
        }

        if (!InternetCheckHelper.isConnected()) {
            channelApiCallback.error(activity.getString(R.string.internet_error), null);
            channelApiCallback.loaderShow(false);
        } else {
            channelApiCallback.loaderShow(true);
            Call<UpdateChannelResponse> call = null;
            if (editDesc) {
                call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .updateChannelProfile(
                                "Bearer " + mPrefs.getAccessToken(),
                                channelId,
                                description,
                                profilePic,
                                coverPic
                        );
            } else {
                call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .updateChannelProfile2(
                                "Bearer " + mPrefs.getAccessToken(),
                                channelId,
                                profilePic,
                                coverPic,
                                coverPicPort
                        );
            }
            String finalImg = img;
            call.enqueue(new Callback<UpdateChannelResponse>() {
                @Override
                public void onResponse(@NotNull Call<UpdateChannelResponse> call, @NotNull Response<UpdateChannelResponse> response) {
                    if (response.code() == 200) {
                        channelApiCallback.success(response.body());
                    } else {
                        channelApiCallback.error(response.message(), finalImg);
                    }
                    channelApiCallback.loaderShow(false);
                }

                @Override
                public void onFailure(@NotNull Call<UpdateChannelResponse> call, @NotNull Throwable t) {
                    channelApiCallback.loaderShow(false);
                    channelApiCallback.error(t.getMessage(), finalImg);
                }
            });
        }
    }
}
