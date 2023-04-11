package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.BaseModel;
import com.ziro.bullet.data.models.CategorySequenceModel;
import com.ziro.bullet.data.models.home.HomeModel;
import com.ziro.bullet.interfaces.HomeCallback;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter {
    private Activity activity;
    private HomeCallback homeCallback;
    private PrefConfig mPrefs;
    public String errorInternet = "internet";
    private Call<HomeModel> call;
    private DbHandler cacheManager;

    public HomePresenter(Activity activity, HomeCallback mainInterface) {
        this.activity = activity;
        this.homeCallback = mainInterface;
        this.mPrefs = new PrefConfig(activity);
        this.cacheManager = new DbHandler(activity);
    }

    public void getHome(String type) {
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.loaderShow(false);
            homeCallback.error(errorInternet);
        } else {
            homeCallback.loaderShow(true);
            call = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .getHomeData("Bearer " + mPrefs.getAccessToken(), type, mPrefs.isReaderMode());
            call.enqueue(new Callback<HomeModel>() {
                @Override
                public void onResponse(@NotNull Call<HomeModel> call, @NotNull Response<HomeModel> response) {
                    homeCallback.loaderShow(false);
                    if (response.code() == 200) {
                        if (response != null) {
                            Log.e("HOMELIFE", "data : " + new Gson().toJson(response.body()));
                            homeCallback.success(response.body());
                        } else {
                            homeCallback.error(response.message());
                        }
                    } else {
                        homeCallback.error(response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<HomeModel> call, @NotNull Throwable t) {
                    homeCallback.loaderShow(false);
                    homeCallback.error(t.getMessage());
                }
            });
        }
    }

    public void searchTopics(String query, String page, boolean isPagination) {
        if (!InternetCheckHelper.isConnected()) {
            homeCallback.loaderShow(false);
            homeCallback.error(errorInternet);
            return;
        }
        homeCallback.loaderShow(true);
        Call<CategoryResponse> call = ApiClient
                .getInstance(activity)
                .getApi()
                .searchTopics("Bearer " + mPrefs.getAccessToken(), query, page);
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NotNull Call<CategoryResponse> call, @NotNull Response<CategoryResponse> response) {
                homeCallback.loaderShow(false);
                homeCallback.searchSuccess(response.body(), isPagination);
            }

            @Override
            public void onFailure(@NotNull Call<CategoryResponse> call, @NotNull Throwable t) {
                homeCallback.loaderShow(false);
                homeCallback.error(t.getMessage());
            }
        });
    }

    public void updateArticleCatSequence(List<String> catIds, String type) {
        if (!InternetCheckHelper.isConnected()) {
//            homeCallback.loaderShow(false);
            homeCallback.error(errorInternet);
        } else {
//            homeCallback.loaderShow(true);

            Map<String, List<String>> catIdsMap = new HashMap<>();
            catIdsMap.put("ids", catIds);
//            catIdsMap.put("types", type);
            CategorySequenceModel categorySequenceModel = new CategorySequenceModel(type, catIds);
            Call<BaseModel> updateSequenceCall = ApiClient
                    .getInstance(activity)
                    .getApi()
                    .updateArticleCatSequence("Bearer " + mPrefs.getAccessToken(), categorySequenceModel);
            updateSequenceCall.enqueue(new Callback<BaseModel>() {
                @Override
                public void onResponse(Call<BaseModel> call, Response<BaseModel> response) {
                    if (response.isSuccessful()) {
                        Log.d("DEBUG_TAG", "onResponse: Sequence Updated");
                    } else {
                        Log.d("DEBUG_TAG", "onResponse: Error updating sequence");
                    }
                }

                @Override
                public void onFailure(Call<BaseModel> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("DEBUG_TAG", "onResponse: Error updating sequence");

                }
            });
        }
    }

    public String getGreetings() {
        try {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH", Locale.ROOT);
            String time = dateFormat.format(date);
            int hour = Integer.parseInt(time);

            if (hour >= 0 && hour <= 11) {
                return activity.getString(R.string.good_morning);
            } else if (hour >= 12 && hour <= 17) {
                return activity.getString(R.string.good_afternoon);
            } else return activity.getString(R.string.good_evening);
        } catch (Exception e) {
            return activity.getString(R.string.good_morning);
        }
    }

    public String getDay() {

//        String dateStr = "04/05/2010";
//
//        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
//        Date dateObj = curFormater.parse(dateStr);
//        SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");

        try {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd");
            String formattedDate = simpleDateFormat.format(c);
            return formattedDate;
        } catch (Exception e) {
            return "";
        }
    }

    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }
}
