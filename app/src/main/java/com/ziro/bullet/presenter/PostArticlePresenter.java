package com.ziro.bullet.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.APIResources.ProgressRequestBody;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PostArticleParams;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.SingleArticle;
import com.ziro.bullet.data.models.postarticle.CurrentLocations;
import com.ziro.bullet.data.models.postarticle.CurrentTags;
import com.ziro.bullet.data.models.postarticle.LocationReplace;
import com.ziro.bullet.data.models.postarticle.LocationResponse;
import com.ziro.bullet.data.models.postarticle.TagsReplace;
import com.ziro.bullet.data.models.postarticle.TagsResponse;
import com.ziro.bullet.interfaces.PostArticleCallback;
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
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ziro.bullet.utills.Utils.getOutputMediaFile;

public class PostArticlePresenter implements ProgressRequestBody.UploadCallbacks {
    private Activity activity;
    private PostArticleCallback homeCallback;
    private PrefConfig mPrefs;

    public PostArticlePresenter(Activity activity, PostArticleCallback mainInterface) {
        this.activity = activity;
        this.homeCallback = mainInterface;
        this.mPrefs = new PrefConfig(activity);
    }

    public void updateArticleLanguage(String articleId, String languageCode) {
        homeCallback.loaderShow(false);
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .updateArticleLanguage("Bearer " + mPrefs.getAccessToken(), articleId, languageCode);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    homeCallback.loaderShow(false);
                    if (response.body() != null) {
                        homeCallback.success(response.body());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void removePostLocation(String articleId, String tagId) {
        homeCallback.loaderShow(false);
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<LocationResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .removePostLocation("Bearer " + mPrefs.getAccessToken(), articleId, tagId);
            call.enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(@NotNull Call<LocationResponse> call, @NotNull Response<LocationResponse> response) {
                    homeCallback.loaderShow(false);
                    //homeCallback.success(response.body().getTopics().get(0).getId());
                }

                @Override
                public void onFailure(@NotNull Call<LocationResponse> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void addPostLocation(String articleId, String tags, String tempTagId) {
        homeCallback.loaderShow(false);
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<LocationResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .addPostLocation("Bearer " + mPrefs.getAccessToken(), articleId, tags);
            call.enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(@NotNull Call<LocationResponse> call, @NotNull Response<LocationResponse> response) {
                    homeCallback.loaderShow(false);
                    if (response.body() != null && response.body().getLocations().size() > 0) {
                        LocationReplace locationReplace = new LocationReplace(tempTagId, response.body().getLocations().get(0));
                        homeCallback.success(locationReplace);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LocationResponse> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void getSuggestedLocation(String articleId, String query) {
        homeCallback.loaderShow(false);
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<LocationResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getArticleSuggestedLocation("Bearer " + mPrefs.getAccessToken(), articleId, query);
            call.enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(@NotNull Call<LocationResponse> call, @NotNull Response<LocationResponse> response) {
                    homeCallback.loaderShow(false);
                    homeCallback.success(response.body());
                }

                @Override
                public void onFailure(@NotNull Call<LocationResponse> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void getLocationList(String articleId) {
        homeCallback.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<LocationResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getLocationList("Bearer " + mPrefs.getAccessToken(), articleId);
            call.enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(@NotNull Call<LocationResponse> call, @NotNull Response<LocationResponse> response) {
                    homeCallback.loaderShow(false);
                    if (response.body() != null) {
                        CurrentLocations currentLocations = new CurrentLocations();
                        currentLocations.setLocations(response.body().getLocations());
                        homeCallback.success(currentLocations);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LocationResponse> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void getTagList(String articleId) {
        homeCallback.loaderShow(true);
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<TagsResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getTagList("Bearer " + mPrefs.getAccessToken(), articleId);
            call.enqueue(new Callback<TagsResponse>() {
                @Override
                public void onResponse(@NotNull Call<TagsResponse> call, @NotNull Response<TagsResponse> response) {
                    homeCallback.loaderShow(false);
                    if (response.body() != null) {
                        CurrentTags currentTags = new CurrentTags();
                        currentTags.setTopics(response.body().getTopics());
                        homeCallback.success(currentTags);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<TagsResponse> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void removePostTags(String articleId, String tagId) {
        homeCallback.loaderShow(false);
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<TagsResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .removePostTags("Bearer " + mPrefs.getAccessToken(), articleId, tagId);
            call.enqueue(new Callback<TagsResponse>() {
                @Override
                public void onResponse(@NotNull Call<TagsResponse> call, @NotNull Response<TagsResponse> response) {
                    homeCallback.loaderShow(false);
                    //homeCallback.success(response.body().getTopics().get(0).getId());
                }

                @Override
                public void onFailure(@NotNull Call<TagsResponse> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void postArticle(String title, String source, String link, String publisheddate, File file, ArrayList<String> bullet) {

        Log.e("postArticle", "+++++++++++++++++++++++++++++++++++++++");
        Log.e("postArticle", "title : " + title);
        Log.e("postArticle", "source : " + source);
        Log.e("postArticle", "link : " + link);
        Log.e("postArticle", "publisheddate : " + publisheddate);
        Log.e("postArticle", "file : " + file);
        Log.e("postArticle", "bullet : " + bullet.size());

        RequestBody title_ = RequestBody.create(MediaType.parse("string"), title);
        RequestBody source_ = RequestBody.create(MediaType.parse("string"), source);
        RequestBody link_ = RequestBody.create(MediaType.parse("string"), link);
        RequestBody publisheddate_ = RequestBody.create(MediaType.parse("string"), publisheddate);
        MultipartBody.Part image_ = null;

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

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file_);
            if (requestFile != null) {
                image_ = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            }
        }

        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
            homeCallback.loaderShow(false);
        } else {
            homeCallback.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .createArticle("Bearer " + mPrefs.getAccessToken(), title_, source_, link_, publisheddate_, image_, bullet);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    homeCallback.loaderShow(false);
                    if (response.isSuccessful()) {
                        Log.e("HOMELIFE", "userTopics : here");
                        homeCallback.success(response.body());
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                homeCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            homeCallback.error(activity.getString(R.string.server_error));
                        }

                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void getSuggestedTags(String articleId, String query) {
        homeCallback.loaderShow(false);
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<TagsResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getSuggestedTags("Bearer " + mPrefs.getAccessToken(), articleId, query);
            call.enqueue(new Callback<TagsResponse>() {
                @Override
                public void onResponse(@NotNull Call<TagsResponse> call, @NotNull Response<TagsResponse> response) {
                    homeCallback.loaderShow(false);
                    homeCallback.success(response.body());
                }

                @Override
                public void onFailure(@NotNull Call<TagsResponse> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void addPostTags(String articleId, String tags, String tempTagId) {
        homeCallback.loaderShow(false);
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
        } else {
            Call<TagsResponse> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .addPostTags("Bearer " + mPrefs.getAccessToken(), articleId, tags);
            call.enqueue(new Callback<TagsResponse>() {
                @Override
                public void onResponse(@NotNull Call<TagsResponse> call, @NotNull Response<TagsResponse> response) {
                    homeCallback.loaderShow(false);
                    if (response.body() != null && response.body().getTopics().size() > 0) {
                        TagsReplace tagsReplace = new TagsReplace(tempTagId, response.body().getTopics().get(0));
                        homeCallback.success(tagsReplace);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<TagsResponse> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void uploadImageVideo(File file, String api) {

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


//                    requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
//                    if (requestFile != null) {
//                        image_ = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//                    }
                    break;
                case "videos":

                    requestFile = new ProgressRequestBody(file, "video/mp4", this);
                    if (requestFile != null) {
                        image_ = MultipartBody.Part.createFormData("video", file.getName(), requestFile);
                    }


//                    requestFile = RequestBody.create(MediaType.parse("video/mp4"), file);
//                    if (requestFile != null) {
//                        image_ = MultipartBody.Part.createFormData("video", file.getName(), requestFile);
//                    }
                    break;
            }

        } else {
            homeCallback.error(api + " empty!");
            return;
        }

        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
            homeCallback.loaderShow(false);
        } else {
            homeCallback.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .uploadImageVideo("Bearer " + mPrefs.getAccessToken(), api, image_);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject != null) {
                                    switch (api) {
                                        case "images":
                                            homeCallback.uploadSuccess(jsonObject.getString("results"), api);
                                            break;
                                        case "videos":
                                            homeCallback.uploadSuccess(jsonObject.getString("key"), api);
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
                                homeCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            homeCallback.error(activity.getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void createArticle(PostArticleParams object, boolean isReel) {

        if (object != null) {
            if (!InternetCheckHelper.isConnected()) {
                homeCallback.error(activity.getString(R.string.internet_error));
                homeCallback.loaderShow(false);
            } else {
                homeCallback.loaderShow(true);
                Log.e("HOMELIFE", "userTopics : " + new Gson().toJson(object));
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(object));
                Call<SingleArticle> call = null;
                if (isReel) {
                    call = ApiClient
                            .getInstance(activity)
                            .getApi()
                            .createReels("Bearer " + mPrefs.getAccessToken(), body);
                } else {
                    call = ApiClient
                            .getInstance(activity)
                            .getApi()
                            .createArticle("Bearer " + mPrefs.getAccessToken(), object.getType(), body);
                }

                call.enqueue(new Callback<SingleArticle>() {
                    @Override
                    public void onResponse(@NotNull Call<SingleArticle> call, @NotNull Response<SingleArticle> response) {
                        homeCallback.loaderShow(false);
                        if (response.isSuccessful()) {
                            Log.e("HOMELIFE", "userTopics : here");
                            if (isReel) {
                                homeCallback.createSuccess(response.body().getReels(), object.getType());
                            } else {
                                homeCallback.createSuccess(response.body().getArticle(), object.getType());
                            }
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = jsonObject.getString("message");
                                    homeCallback.error(msg);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                homeCallback.error(activity.getString(R.string.server_error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<SingleArticle> call, @NotNull Throwable t) {
                        homeCallback.loaderShow(false);
                        homeCallback.error(t.getMessage());
                    }
                });
            }
        }
    }

    public void publishArticle(String id, String mode) {

        if (!TextUtils.isEmpty(id)) {
            if (!InternetCheckHelper.isConnected()) {
                homeCallback.error(activity.getString(R.string.internet_error));
                homeCallback.loaderShow(false);
            } else {
                homeCallback.loaderShow(true);
                Call<ResponseBody> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .publishArticle("Bearer " + mPrefs.getAccessToken(), id, mode);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        homeCallback.loaderShow(false);
                        if (response.isSuccessful()) {
                            Log.e("HOMELIFE", "userTopics : here");
                            homeCallback.success(null);
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = jsonObject.getString("message");
                                    homeCallback.error(msg);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                homeCallback.error(activity.getString(R.string.server_error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        homeCallback.loaderShow(false);
                        homeCallback.error(t.getMessage());
                    }
                });
            }
        }
    }

    public void validateMedia(long size, float duration, String type, String media) {
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.error(activity.getString(R.string.internet_error));
            homeCallback.loaderShow(false);
        } else {
            homeCallback.loaderShow(true);
            Call<ResponseBody> call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .mediaValidate("Bearer " + mPrefs.getAccessToken(), size, duration, type, media);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject != null) {
                                    boolean flag = jsonObject.getBoolean("success");
                                    if (flag) {
                                        homeCallback.proceedToUpload();
                                    } else {
                                        homeCallback.loaderShow(false);
                                        homeCallback.error(response.message());
                                    }
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            homeCallback.loaderShow(false);
                        }
                    } else {
                        homeCallback.loaderShow(false);
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                homeCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            homeCallback.error(activity.getString(R.string.server_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    @Override
    public void onProgressUpdate(int percentage) {
        if (homeCallback != null)
            homeCallback.onProgressUpdate(percentage);
    }

    @Override
    public void onError() {
        if (homeCallback != null && activity != null)
            homeCallback.error(activity.getString(R.string.upload_failed));
    }

    @Override
    public void onFinish() {

    }
}
