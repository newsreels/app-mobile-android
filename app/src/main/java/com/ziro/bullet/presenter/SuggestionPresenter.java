package com.ziro.bullet.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.interfaces.SuggestionCallback;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.data.PrefConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ziro.bullet.utills.Utils.getOutputMediaFile;

public class SuggestionPresenter {

    private Activity activity;
    private SuggestionCallback listener;
    private PrefConfig mPrefconfig;

    public SuggestionPresenter(Activity activity, SuggestionCallback callback) {
        this.activity = activity;
        this.listener = callback;
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void submitSuggestion(String email, String msgs, ArrayList<File> files) {

        if (!InternetCheckHelper.isConnected()) {
            listener.error(activity.getString(R.string.internet_error));
        } else {
            listener.loaderShow(true);

            RequestBody email_ = RequestBody.create(MediaType.parse("string"), email);
            RequestBody msgs_ = RequestBody.create(MediaType.parse("string"), msgs);

            Call<ResponseBody> call;
            if (files != null && files.size() > 0) {
                ArrayList<MultipartBody.Part> imagePart = new ArrayList<>();
                for (int i = 0; i < files.size(); i++) {
                    if (files.get(i) != null) {
                        if (!TextUtils.isEmpty(files.get(i).getPath())) {

                            //compress image before uploading
                            File file = getOutputMediaFile();
                            OutputStream stream;
                            try {
                                Bitmap bitmap = BitmapFactory.decodeFile(files.get(i).getPath());
                                stream = new FileOutputStream(getOutputMediaFile());
                                Utils.resize(bitmap, 600, 600).compress(Bitmap.CompressFormat.JPEG, 80, stream);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                            if (requestFile != null) {
                                imagePart.add(MultipartBody.Part.createFormData("file", file.getName(), requestFile));
                            }
                        }
                    }
                }
                call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .sendSuggestion("Bearer " + mPrefconfig.getAccessToken(), email_, msgs_, imagePart);
            } else {
                call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .sendSuggestion("Bearer " + mPrefconfig.getAccessToken(), email_, msgs_);
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    listener.loaderShow(false);
                    if (response.code() == 200) {
                        listener.onSuccess();
                    } else {
//                        if (response.errorBody() != null) {
//                                String s = response.errorBody().toString();
//                                JSONObject jsonObject = new JSONObject(s);
//                                String msg = jsonObject.getString("message");
                            listener.error("error");
//                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    listener.loaderShow(false);
                    listener.error(t.getMessage());
                }
            });
        }
    }
}
