package com.ziro.bullet.auth;

import com.ziro.bullet.BuildConfig;

import java.io.IOException;


/**
 * Created by shine_joseph on 26/05/20.
 */
public class TokenGenerator {
    public void login(String email, String password, String language, OAuthResponseCallback callback) {
        OAuthClient.Builder builder = new OAuthClient.Builder(email, password, BuildConfig.AUTH_CLIENT_ID, BuildConfig.AUTH_CLIENT_SECRET, BuildConfig.AUTH_ACCOUNT_URL);
        builder.langauge(language);
        OAuthClient client = builder.build();
        client.requestAccessToken(callback);
    }

    public OAuthResponse refreshToken(String token, String language) throws IOException {
        OAuthClient.Builder builder = new OAuthClient.Builder(BuildConfig.AUTH_CLIENT_ID, BuildConfig.AUTH_CLIENT_SECRET, BuildConfig.AUTH_ACCOUNT_URL);
        builder.langauge(language);
        OAuthClient client = builder.build();
        return client.refreshAccessToken(token);
    }


    public void socialExchangeToken(String token, String tokenType, String language, OAuthResponseCallback callback) {
        String grant_type = "token_exchange";
        OAuthClient.Builder builder = new OAuthClient.Builder(token, grant_type, tokenType, BuildConfig.AUTH_CLIENT_ID, BuildConfig.AUTH_CLIENT_SECRET, BuildConfig.AUTH_ACCOUNT_URL);
        builder.langauge(language);
        OAuthClient client = builder.build();
        client.requestAccessToken(callback);
    }

    public void authenticateGuest(String deviceId, String language, OAuthResponseCallback callback) {
        String grant_type = "device_code";
        OAuthClient.Builder builder = new OAuthClient.Builder(BuildConfig.AUTH_CLIENT_ID, BuildConfig.AUTH_CLIENT_SECRET, BuildConfig.AUTH_ACCOUNT_URL);

        builder.deviceId(deviceId);
        builder.grantType(grant_type);
        builder.langauge(language);

        OAuthClient client = builder.build();
        client.requestAccessToken(callback);
    }
}
