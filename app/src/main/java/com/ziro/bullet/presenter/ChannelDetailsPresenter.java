package com.ziro.bullet.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.ChannelDetailsInterface;
import com.ziro.bullet.interfaces.CreateChannelCallback;
import com.ziro.bullet.model.ChannelDetails;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ziro.bullet.utills.Utils.getOutputMediaFile;

public class ChannelDetailsPresenter implements CreateChannelCallback {

    private Activity activity;
    private ChannelDetailsInterface channelDetails;
    private PrefConfig mPrefs;
    private CreateChannelPresenter presenter;

    public ChannelDetailsPresenter(Activity activity, ChannelDetailsInterface channelDetails) {
        this.activity = activity;
        this.channelDetails = channelDetails;
        this.mPrefs = new PrefConfig(activity);
        this.presenter = new CreateChannelPresenter(activity,this);
    }

    public void getDetails(String channelID) {

        if (!InternetCheckHelper.isConnected()) {
            channelDetails.error(activity.getString(R.string.internet_error));
            channelDetails.loaderShow(false);
        } else {
            channelDetails.loaderShow(true);
            Call<ChannelDetails> call = ApiClient
                    .getInstance()
                    .getApi()
                    .getChannelDetails("Bearer " + mPrefs.getAccessToken(), channelID);
            call.enqueue(new Callback<ChannelDetails>() {
                @Override
                public void onResponse(@NotNull Call<ChannelDetails> call, @NotNull Response<ChannelDetails> response) {
                    channelDetails.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getChannel() != null) {
                            channelDetails.success(response.body().getChannel());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                channelDetails.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            channelDetails.error(activity.getString(R.string.server_error));
                        }

                    }
                }

                @Override
                public void onFailure(@NotNull Call<ChannelDetails> call, @NotNull Throwable t) {
                    channelDetails.loaderShow(false);
                    channelDetails.error(t.getMessage());
                }
            });
        }
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

    private String sourceId;
    private String imageType;

    public void uploadMedia(String sourceId, File file, String imageType) {
        this.sourceId = sourceId;
        this.imageType = imageType;
        if (presenter != null) {
            presenter.uploadImageVideo(file, "images", 0);
        }
    }

    public void updateChannel(String channelID, String icon, String cover) {

        if (!InternetCheckHelper.isConnected()) {
            channelDetails.error(activity.getString(R.string.internet_error));
            channelDetails.loaderShow(false);
        } else {
            channelDetails.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance()
                    .getApi()
                    .updateChannel("Bearer " + mPrefs.getAccessToken(), channelID, icon, cover);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    channelDetails.loaderShow(false);
                    if (response.code() == 200) {
                        channelDetails.update(channelID,icon,cover);
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                channelDetails.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            channelDetails.error(activity.getString(R.string.server_error));
                        }

                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    channelDetails.loaderShow(false);
                    channelDetails.error(t.getMessage());
                }
            });
        }
    }

    @Override
    public void loaderShow(boolean flag) {

    }

    @Override
    public void error(String error) {

    }

    @Override
    public void mediaUploaded(String url, String type, int request) {
        if (!TextUtils.isEmpty(sourceId) && !TextUtils.isEmpty(url) && !TextUtils.isEmpty(imageType)) {
            switch (imageType) {
                case "icon":
                    updateChannel(sourceId, url, null);
                    break;
                case "cover":
                    updateChannel(sourceId,null , url);
                    break;
            }
        }
    }

    @Override
    public void validName(boolean isValid) {

    }
}
