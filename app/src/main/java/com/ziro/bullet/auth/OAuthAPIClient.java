package com.ziro.bullet.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ziro.bullet.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthAPIClient {
    public static Retrofit retrofit;
    public static OAuthAPIClient Instance;
    private static OkHttpClient client;

    /**
     * GET API CLIENT
     */
    private OAuthAPIClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS);

        Gson gson = new GsonBuilder()
                .create();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.addInterceptor(interceptor);
        }

        client = builder.build();


        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.AUTH_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }


    /**
     * CREATE API INSTANCE WITHOUT CONTEXT
     * only for widgets
     */
    public static synchronized OAuthAPIClient getInstance() {
        Instance = new OAuthAPIClient();
        return Instance;
    }

    public static void cancelAll() {
        if (client != null) {
            client.dispatcher().cancelAll();
        }
    }

    public OAuthApiInterface getApi() {
        return retrofit.create(OAuthApiInterface.class);
    }
}
