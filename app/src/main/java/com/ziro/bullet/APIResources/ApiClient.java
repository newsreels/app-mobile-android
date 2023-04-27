package com.ziro.bullet.APIResources;


/**
 * This is Retrofit API Client to setup Retrofit
 */


import android.app.Activity;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.activities.ForceUpdateActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import com.facebook.stetho.okhttp3.StethoInterceptor;


public class ApiClient {

    // RETROFIT OBJECT
    public static ApiClient Instance;
    public static Retrofit retrofit;
    private static OkHttpClient client;
    private boolean force_update = false;

    /**
     * GET API CLIENT
     */
    private ApiClient(Activity activity) {


        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .authenticator(new TokenAuthenticator(activity))
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(chain -> {
                    Request request = chain.request().
                            newBuilder()
                            .addHeader("x-app-platform", "android")
                            .addHeader("x-app-version", String.valueOf(BuildConfig.VERSION_NAME))
                            .addHeader("api-version", BuildConfig.API_VERSION)
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    if (response.code() == 412) {
                        Intent startActivity = new Intent();
                        startActivity.setClass(BulletApp.getInstance(), ForceUpdateActivity.class);
                        startActivity.setAction(ForceUpdateActivity.class.getName());
                        startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        BulletApp.getInstance().startActivity(startActivity);
                    }
                    if (!response.isSuccessful()){
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.URL,request.url().toString());
                        params.put(Events.KEYS.CODE, String.valueOf(response.code()));
                        AnalyticsEvents.INSTANCE.logEvent(activity,
                                params,
                                Events.API_FAILURE);
                    }
                    return response;
                });


        Gson gson = new GsonBuilder()
                .create();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        client = builder.build();

//        client.dispatcher().cancelAll();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }


    /**
     * GET API CLIENT
     */
    private ApiClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .authenticator(new TokenAuthenticator())
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(chain -> {
                    Request request = chain.request().
                            newBuilder()
                            .addHeader("x-app-platform", "android")
                            .addHeader("x-app-version", String.valueOf(BuildConfig.VERSION_NAME))
                            .addHeader("api-version", BuildConfig.API_VERSION)
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    if (response.code() == 412) {
                        Intent startActivity = new Intent();
                        startActivity.setClass(BulletApp.getInstance(), ForceUpdateActivity.class);
                        startActivity.setAction(ForceUpdateActivity.class.getName());
                        startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        BulletApp.getInstance().startActivity(startActivity);
                    }
                    return response;
                });

        Gson gson = new GsonBuilder()
                .create();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.addInterceptor(interceptor);
        }

        client = builder.build();

//        client.dispatcher().cancelAll();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }


    /**
     * CREATE API INSTANCE
     * will not catch unauthenticated error
     */
    public static synchronized ApiClient getInstance(Activity activity) {
        if (Instance == null) {
            Instance = new ApiClient(activity);
        }
        return Instance;
    }

    /**
     * CREATE API INSTANCE WITHOUT CONTEXT
     * only for widgets
     */
    public static synchronized ApiClient getInstance() {
        Instance = new ApiClient();
        return Instance;
    }

    public static void cancelAll() {
        if (client != null) {
            client.dispatcher().cancelAll();
        }
    }

    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }

}
