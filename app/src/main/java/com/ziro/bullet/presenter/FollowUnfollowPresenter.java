package com.ziro.bullet.presenter;

import android.app.Activity;
import android.widget.Toast;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.model.ShareBottomSheetResponse;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowUnfollowPresenter {

    private Activity activity;
    private PrefConfig mPrefconfig;
    private DbHandler cacheManager;

    public FollowUnfollowPresenter(Activity activity) {
        this.activity = activity;
        this.mPrefconfig = new PrefConfig(activity);
        cacheManager = new DbHandler(activity);
    }

    public void unFollowSource(String id, int position, FollowUnFollowApiCallback mCallback) {
        Call<ShareBottomSheetResponse> call = ApiClient
                .getInstance(activity)
                .getApi()
                .unfollowSource("Bearer " + mPrefconfig.getAccessToken(), id);
        call.enqueue(new Callback<ShareBottomSheetResponse>() {
            @Override
            public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                if (response.code() == 200) {
                    clear();
                    if (mCallback != null) mCallback.onResponse(position, true);
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    if (mCallback != null) mCallback.onResponse(position, false);
                } else {
                    if (mCallback != null) mCallback.onResponse(position, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                Toast.makeText(activity, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onResponse(position, false);
            }
        });
    }

    public void followSource(String id, int position, FollowUnFollowApiCallback mCallback) {
        Call<ResponseBody> call = ApiClient
                .getInstance(activity)
                .getApi()
                .followSource("Bearer " + mPrefconfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    clear();
                    if (mCallback != null) mCallback.onResponse(position, true);
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    if (mCallback != null) mCallback.onResponse(position, false);
                } else {
                    if (mCallback != null) mCallback.onResponse(position, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(activity, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onResponse(position, false);
            }
        });
    }

    public void followTopic(String id, int position, FollowUnFollowApiCallback mCallback) {
        Call<ResponseBody> call = ApiClient
                .getInstance(activity)
                .getApi()
                .addTopic("Bearer " + mPrefconfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    clear();
                    if (mCallback != null) mCallback.onResponse(position, true);
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    if (mCallback != null) mCallback.onResponse(position, false);
                } else {
                    if (mCallback != null) mCallback.onResponse(position, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(activity, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onResponse(position, false);
            }
        });
    }

    public void unFollowTopic(String id, int position, FollowUnFollowApiCallback mCallback) {
        Call<ResponseBody> call = ApiClient
                .getInstance(activity)
                .getApi()
                .unfollowTopic("Bearer " + mPrefconfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    clear();
                    if (mCallback != null) mCallback.onResponse(position, true);
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    if (mCallback != null) mCallback.onResponse(position, false);
                } else {
                    if (mCallback != null) mCallback.onResponse(position, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(activity, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onResponse(position, false);
            }
        });
    }

    public void followLocation(String id, int position, FollowUnFollowApiCallback mCallback) {
        Call<ResponseBody> call = ApiClient
                .getInstance(activity)
                .getApi()
                .followLocation("Bearer " + mPrefconfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    clear();
                    if (mCallback != null) mCallback.onResponse(position, true);
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    if (mCallback != null) mCallback.onResponse(position, false);
                } else {
                    if (mCallback != null) mCallback.onResponse(position, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(activity, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onResponse(position, false);
            }
        });
    }

    public void unFollowLocation(String id, int position, FollowUnFollowApiCallback mCallback) {
        Call<ResponseBody> call = ApiClient
                .getInstance(activity)
                .getApi()
                .unfollowLocation("Bearer " + mPrefconfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    clear();
                    if (mCallback != null) mCallback.onResponse(position, true);
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    if (mCallback != null) mCallback.onResponse(position, false);
                } else {
                    if (mCallback != null) mCallback.onResponse(position, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(activity, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onResponse(position, false);
            }
        });
    }

    public void followAuthor(String id, int position, FollowUnFollowApiCallback mCallback) {
        Call<ResponseBody> call = ApiClient
                .getInstance(activity)
                .getApi()
                .followAuthor("Bearer " + mPrefconfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    clear();
                    if (mCallback != null) mCallback.onResponse(position, true);
                    try {
                        if (response.body() != null) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String msg = jsonObject.getString("message");
//                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    if (mCallback != null) mCallback.onResponse(position, false);
                } else {
                    if (mCallback != null) mCallback.onResponse(position, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(activity, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onResponse(position, false);
            }
        });
    }

    public void unFollowAuthor(String id, int position, FollowUnFollowApiCallback mCallback) {
        Call<ResponseBody> call = ApiClient
                .getInstance(activity)
                .getApi()
                .unFollowAuthor("Bearer " + mPrefconfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    clear();
                    if (mCallback != null) mCallback.onResponse(position, true);
                    try {
                        if (response.body() != null) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String msg = jsonObject.getString("message");
//                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    if (mCallback != null) mCallback.onResponse(position, false);
                } else {
                    if (mCallback != null) mCallback.onResponse(position, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(activity, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (mCallback != null) mCallback.onResponse(position, false);
            }
        });
    }

    private void clear() {
        if (cacheManager != null) {
            Constants.homeDataUpdate = true;
            Constants.menuDataUpdate = true;
            Constants.reelDataUpdate = true;
            if (mPrefconfig != null)
                mPrefconfig.setAppStateHomeTabs("");
            cacheManager.clearDb();
        }
    }

    public interface FollowUnFollowApiCallback {
        void onResponse(int position, boolean flag);
    }
}
