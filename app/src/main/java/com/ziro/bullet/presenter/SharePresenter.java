package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.ApiResponseInterface;
import com.ziro.bullet.interfaces.ShareInterface;
import com.ziro.bullet.model.ShareBottomSheetResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharePresenter {

    private Activity context;
    private ShareInterface interfacee;
    private PrefConfig mPrefconfig;
    private String type;

    public SharePresenter(Activity context, ShareInterface interfacee, String type) {
        this.context = context;
        this.type = type;
        this.interfacee = interfacee;
        this.mPrefconfig = new PrefConfig(context);

    }

    public void archive(String id, boolean isArchive) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "archive");
                Call<ResponseBody> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .archive("Bearer " + mPrefconfig.getAccessToken(), id, !isArchive);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        interfacee.loaderShow(false, "archive");
                        if (response.code() == 200) {
                            if (response != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    String msg = jsonObject.getString("message");
                                    if (!isArchive) {
                                        interfacee.success(msg, "archive", id);
                                    } else {
                                        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("ARCHIVE")) {
                                            interfacee.success(msg, "archive", id);
                                        } else {
                                            interfacee.success(msg, "remove", id);
                                        }
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }

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
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        interfacee.error(t.getMessage());
                        interfacee.loaderShow(false, "archive");
                    }
                });
            }
        } catch (Exception t) {
            interfacee.error(t.getMessage());
            interfacee.loaderShow(false, "archive");
        }
    }

    public void share_msg(String id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                Call<ResponseBody> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .share_msg("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    String msg = jsonObject.getString("share_message");
                                    boolean source_blocked = jsonObject.getBoolean("source_blocked");
                                    boolean source_followed = jsonObject.getBoolean("source_followed");
                                    interfacee.success(msg, "share", id);
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }


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
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.error(t.getMessage());
        }
    }

    public void block(String id, String article_id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "block");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .blockSource("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "block");
                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "block", article_id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "block");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "block");
            interfacee.error(t.getMessage());
        }
    }

    public void unblock(String id, String article_id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "unblock");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .unblock("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "unblock");
//                        Log.e("RESDSD", "=============================");
//                        Log.e("RESDSD", new Gson().toJson(response));
//                        Log.e("RESDSD", "CODE : " + response.code());
//                        Log.e("RESDSD", "MSG : " + (response.message()));
//                        Log.e("RESDSD", "BODY : " + (response.body()));

                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "unblock", article_id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "unblock");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "unblock");
            interfacee.error(t.getMessage());
        }
    }

    public void follow(String id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "follow");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .follow("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "follow");
//                        Log.e("RESDSD", "=============================");
//                        Log.e("RESDSD", new Gson().toJson(response));
//                        Log.e("RESDSD", "CODE : " + response.code());
//                        Log.e("RESDSD", "MSG : " + (response.message()));
//                        Log.e("RESDSD", "BODY : " + (response.body()));

                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success("Success", "follow", id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "follow");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "follow");
            interfacee.error(t.getMessage());
        }
    }

    public void unfollow(String id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "unfollow");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .unfollowSource("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "unfollow");
//                        Log.e("RESDSD", "=============================");
//                        Log.e("RESDSD", new Gson().toJson(response));
//                        Log.e("RESDSD", "CODE : " + response.code());
//                        Log.e("RESDSD", "MSG : " + (response.message()));
//                        Log.e("RESDSD", "BODY : " + (response.body()));

                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "unfollow", id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "unfollow");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "unfollow");
            interfacee.error(t.getMessage());
        }
    }

    public void report(String id, String msg) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "report");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .report("Bearer " + mPrefconfig.getAccessToken(), "articles", id, msg);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "report");
//                        Log.e("RESDSD", "=============================");
//                        Log.e("RESDSD", new Gson().toJson(response));
//                        Log.e("RESDSD", "CODE : " + response.code());
//                        Log.e("RESDSD", "MSG : " + (response.message()));
//                        Log.e("RESDSD", "BODY : " + (response.body()));

                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "report", id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "report");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "report");
            interfacee.error(t.getMessage());
        }
    }

    public void more(String id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "more");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .suggestMore("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "more");
//                        Log.e("RESDSD", "=============================");
//                        Log.e("RESDSD", new Gson().toJson(response));
//                        Log.e("RESDSD", "CODE : " + response.code());
//                        Log.e("RESDSD", "MSG : " + (response.message()));
//                        Log.e("RESDSD", "BODY : " + (response.body()));

                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "more", id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "more");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "more");
            interfacee.error(t.getMessage());
        }
    }

    public void less(String id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "less");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .suggestLess("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "less");
//                        Log.e("RESDSD", "=============================");
//                        Log.e("RESDSD", new Gson().toJson(response));
//                        Log.e("RESDSD", "CODE : " + response.code());
//                        Log.e("RESDSD", "MSG : " + (response.message()));
//                        Log.e("RESDSD", "BODY : " + (response.body()));

                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "less", id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "less");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "less");
            interfacee.error(t.getMessage());
        }
    }

    public void unpublishArticle(String id, ApiResponseInterface responseInterface) {

        if (!TextUtils.isEmpty(id)) {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                interfacee.loaderShow(false, "del");
            } else {
                interfacee.loaderShow(true, "del");
                Call<ResponseBody> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .publishArticle("Bearer " + mPrefconfig.getAccessToken(), id, "UNPUBLISHED");

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        interfacee.loaderShow(false, "del");
                        if (response.code() == 200) {
                            Log.e("HOMELIFE", "userTopics : here");
                            responseInterface._success();
                        } else {
                            responseInterface._other(response.code());
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = jsonObject.getString("message");
                                    interfacee.error(msg);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                if (context != null)
                                    interfacee.error(context.getString(R.string.server_error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "del");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        }
    }

    public void changeArticleStatus(String id, String status, SchedulePresenterCallback responseInterface) {
        if (!TextUtils.isEmpty(id)) {
            if (!InternetCheckHelper.isConnected()) {
                responseInterface.error(context.getString(R.string.internet_error));
                responseInterface.loading(false);
            } else {
                responseInterface.loading(true);
                Call<ResponseBody> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .publishArticle("Bearer " + mPrefconfig.getAccessToken(), id, status);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        responseInterface.loading(false);
                        if (response.isSuccessful()) {
                            responseInterface.success();
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = jsonObject.getString("message");
                                    responseInterface.error(msg);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        responseInterface.loading(false);
                        responseInterface.error(t.getMessage());
                    }
                });
            }
        }
    }

    public void blockAuthor(String id, String article_id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "block");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .blockAuthor("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "block");
                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "block", article_id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "block");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "block");
            interfacee.error(t.getMessage());
        }
    }

    public void unblockAuthor(String id, String article_id) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "unblock");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .unblockAuthor("Bearer " + mPrefconfig.getAccessToken(), id);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "unblock");
                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "unblock", article_id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "unblock");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "unblock");
            interfacee.error(t.getMessage());
        }
    }

    public void blockAuthor(String authorId) {

        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "block");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .blockAuthor("Bearer " + mPrefconfig.getAccessToken(), authorId);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "block");
                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "block", authorId);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "block");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "block");
            interfacee.error(t.getMessage());
        }
    }

    public void reportAuthor(String id, String msg) {

        try {
            if (!InternetCheckHelper.isConnected()) {
                interfacee.error(context.getString(R.string.internet_error));
                return;
            } else {
                interfacee.loaderShow(true, "report");
                Call<ShareBottomSheetResponse> call = ApiClient
                        .getInstance(context)
                        .getApi()
                        .report("Bearer " + mPrefconfig.getAccessToken(), "sources", id, msg);
                call.enqueue(new Callback<ShareBottomSheetResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                        interfacee.loaderShow(false, "report");
                        if (response.code() == 200) {
                            if (response != null && response.body() != null) {
                                interfacee.success(response.body().getMessage(), "report", id);
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
                    public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                        interfacee.loaderShow(false, "report");
                        interfacee.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            interfacee.loaderShow(false, "report");
            interfacee.error(t.getMessage());
        }
    }

    public interface SchedulePresenterCallback {
        void loading(boolean flag);

        void success();

        void error(String error);
    }

}
