package com.newsreels.app.presenter;

import android.app.Activity;

import com.newsreels.app.APIResources.ApiClient;
import com.newsreels.app.R;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.interfaces.AuthorApiCallback;
import com.newsreels.app.model.AuthorResponse;
import com.newsreels.app.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorPresenter {

    private Activity activity;
    private AuthorApiCallback profileApiCallback;
    private PrefConfig mPrefs;

    public AuthorPresenter(Activity activity, AuthorApiCallback mainInterface) {
        this.activity = activity;
        this.profileApiCallback = mainInterface;
        this.mPrefs = new PrefConfig(activity);
    }

    public void getAuthor(String authorID) {

        if (!InternetCheckHelper.isConnected()) {
            profileApiCallback.error(activity.getString(R.string.internet_error));
            profileApiCallback.loaderShow(false);
        } else {
            profileApiCallback.loaderShow(true);
            Call<AuthorResponse> call = ApiClient
                    .getInstance()
                    .getApi()
                    .getAuthor("Bearer " + mPrefs.getAccessToken(), authorID);
            call.enqueue(new Callback<AuthorResponse>() {
                @Override
                public void onResponse(@NotNull Call<AuthorResponse> call, @NotNull Response<AuthorResponse> response) {
                    profileApiCallback.loaderShow(false);
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getAuthor() != null) {
                            profileApiCallback.success(response.body().getAuthor());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = jsonObject.getString("message");
                                profileApiCallback.error(msg);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            profileApiCallback.error(activity.getString(R.string.server_error));
                        }

                    }
                }

                @Override
                public void onFailure(@NotNull Call<AuthorResponse> call, @NotNull Throwable t) {
                    profileApiCallback.loaderShow(false);
                    profileApiCallback.error(t.getMessage());
                }
            });
        }
    }
}
