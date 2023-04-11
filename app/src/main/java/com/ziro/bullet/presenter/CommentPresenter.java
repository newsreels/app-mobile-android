package com.ziro.bullet.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.comment.RepliesAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.CommentInterface;
import com.ziro.bullet.model.comment.Comment;
import com.ziro.bullet.model.comment.CommentResponse;
import com.ziro.bullet.model.comment.User;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentPresenter {

    private Activity activity;
    private CommentInterface commentInterface;
    private PrefConfig mPrefconfig;

    public CommentPresenter(Activity activity, CommentInterface commentInterface) {
        this.activity = activity;
        this.commentInterface = commentInterface;
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void comments(String article_id, String parent_id, String page, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        Log.e("CALLXSXS", "article_id : " + article_id);
        Log.e("CALLXSXS", "parent_id : " + parent_id);
        Log.e("CALLXSXS", "page : " + page);
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");

        try {
            if (!InternetCheckHelper.isConnected()) {
                commentInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                commentInterface.loaderShow(true);
                Call<CommentResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .comments("Bearer " + mPrefconfig.getAccessToken(), article_id, parent_id, page);
                call.enqueue(new Callback<CommentResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommentResponse> call, @NotNull Response<CommentResponse> response) {
                        Log.e("RESPONSE", "RES: " + response.body());
                        commentInterface.loaderShow(false);
                        if (response.body() != null) {
                            if (childItemsAdapter != null && adapter != null) {
                                childItemsAdapter.clear();
                                if (response.body().getmComment() != null && response.body().getmComment().size() > 0) {
                                    childItemsAdapter.addAll(response.body().getmComment());
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommentResponse> call, @NotNull Throwable t) {
                        Log.e("RESPONSE", "RES: " + t.getMessage());
                        commentInterface.loaderShow(false);
                    }
                });
            }
        } catch (Exception t) {
            Log.e("RESPONSE", "RES: " + t.getMessage());
            commentInterface.loaderShow(false);
        }
    }

    public void comments(String article_id, String parent_id, String page, boolean refresh) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        Log.e("CALLXSXS", "article_id : " + article_id);
        Log.e("CALLXSXS", "parent_id : " + parent_id);
        Log.e("CALLXSXS", "page : " + page);
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");

        try {
            if (!InternetCheckHelper.isConnected()) {
                commentInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                commentInterface.loaderShow(true);
                Call<CommentResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .comments("Bearer " + mPrefconfig.getAccessToken(), article_id, parent_id, page);
                call.enqueue(new Callback<CommentResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommentResponse> call, @NotNull Response<CommentResponse> response) {
                        commentInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response != null) {
                            Log.e("RESPONSE", "RES: " + response.body());
                            commentInterface.success(response.body(), refresh);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommentResponse> call, @NotNull Throwable t) {
                        commentInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + t.getMessage());
                        if (t != null && !TextUtils.isEmpty(t.getMessage())) {
                            commentInterface.error(t.getMessage());
                        }
                    }
                });
            }
        } catch (Exception t) {
            Log.e("RESPONSE", "RES: " + t.getMessage());
            if (t != null && !TextUtils.isEmpty(t.getMessage())) {
                commentInterface.error(t.getMessage());
            }
        }
    }

    public void commentPagination(String article_id, String parent_id, String page) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        Log.e("CALLXSXS", "article_id : " + article_id);
        Log.e("CALLXSXS", "parent_id : " + parent_id);
        Log.e("CALLXSXS", "page : " + page);
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");

        try {
            if (!InternetCheckHelper.isConnected()) {
                commentInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                commentInterface.loaderShow(true);
                Call<CommentResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .comments("Bearer " + mPrefconfig.getAccessToken(), article_id, parent_id, page);
                call.enqueue(new Callback<CommentResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommentResponse> call, @NotNull Response<CommentResponse> response) {
                        commentInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response != null) {
                            Log.e("RESPONSE", "RES: " + response.body());
                            commentInterface.successPagination(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommentResponse> call, @NotNull Throwable t) {
                        commentInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + t.getMessage());
                        if (t != null && !TextUtils.isEmpty(t.getMessage())) {
                            commentInterface.error(t.getMessage());
                        }
                    }
                });
            }
        } catch (Exception t) {
            Log.e("RESPONSE", "RES: " + t.getMessage());
            if (t != null && !TextUtils.isEmpty(t.getMessage())) {
                commentInterface.error(t.getMessage());
            }
        }
    }

    public void createComment(String article_id, String parent_id, String comment) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        Log.e("CALLXSXS", "article_id : " + article_id);
        Log.e("CALLXSXS", "parent_id : " + parent_id);
        Log.e("CALLXSXS", "comment : " + comment);
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");

        try {
            if (!InternetCheckHelper.isConnected()) {
                commentInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {
                commentInterface.loaderShow(true);
                Call<CommentResponse> call = ApiClient
                        .getInstance(activity)
                        .getApi()
                        .createComment("Bearer " + mPrefconfig.getAccessToken(), article_id, parent_id, comment);
                call.enqueue(new Callback<CommentResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommentResponse> call, @NotNull Response<CommentResponse> response) {
                        commentInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response.body() != null) {
                            Log.e("RESPONSE", "RES: " + response.body());
                            if (response.body().getmComment() != null && response.body().getmComment().size() > 0)
                                commentInterface.success(response.body().getmComment().get(0));
                        } else {
//                            if (!TextUtils.isEmpty(response.message())) {
                                commentInterface.error("Error");
//                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommentResponse> call, @NotNull Throwable t) {
                        commentInterface.loaderShow(false);
                        Log.e("RESPONSE", "RES: " + t.getMessage());
                        if (t != null && !TextUtils.isEmpty(t.getMessage())) {
                            commentInterface.error(t.getMessage());
                        }
                    }
                });
            }
        } catch (Exception t) {
            Log.e("RESPONSE", "RES: " + t.getMessage());
            if (t != null && !TextUtils.isEmpty(t.getMessage())) {
                commentInterface.error(t.getMessage());
            }
        }
    }

}
