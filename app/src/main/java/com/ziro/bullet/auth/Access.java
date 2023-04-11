package com.ziro.bullet.auth;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Access {
    protected static OAuthResponse refreshAccessToken(String token, OAuthClient oAuth2Client) throws IOException {
        FormBody.Builder formBodyBuilder = new FormBody.Builder()
                .add(Constants.POST_REFRESH_TOKEN, token);

        Utils.postAddIfValid(formBodyBuilder, oAuth2Client.getFieldsAsMap());

        final Request request = new Request.Builder()
                .url(oAuth2Client.getUrl())
                .addHeader("x-user-language", oAuth2Client.getLangauge() != null ? oAuth2Client.getLangauge() : "")
                .post(formBodyBuilder.build())
                .build();


        return refreshTokenFromResponse(oAuth2Client, request);
    }

    private static OAuthResponse refreshTokenFromResponse(final OAuthClient oAuth2Client,
                                                          final Request request) throws IOException {
        return getTokenFromResponse(oAuth2Client, oAuth2Client.getOkHttpClient(),
                request, new AuthState(AuthState.REFRESH_TOKEN));

    }

    protected static OAuthResponse getToken(OAuthClient oAuth2Client) throws IOException {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Utils.postAddIfValid(formBodyBuilder, oAuth2Client.getFieldsAsMap());
        Utils.postAddIfValid(formBodyBuilder, oAuth2Client.getParameters());

        return getAccessToken(oAuth2Client, formBodyBuilder);
    }

    private static OAuthResponse getAccessToken(OAuthClient oAuth2Client, FormBody.Builder formBodyBuilder)
            throws IOException {
        final Request request = new Request.Builder()
                .addHeader("x-user-language", oAuth2Client.getLangauge() != null ? oAuth2Client.getLangauge() : "")
                .url(oAuth2Client.getUrl())
                .post(formBodyBuilder.build())
                .build();

        return getTokenFromResponse(oAuth2Client, request);
    }

    private static OAuthResponse getTokenFromResponse(final OAuthClient oAuth2Client,
                                                      final Request request) throws IOException {
        return getTokenFromResponse(oAuth2Client, oAuth2Client.getOkHttpClient(),
                request, new AuthState(AuthState.ACCESS_TOKEN));
    }

    private static OAuthResponse getTokenFromResponse(final OAuthClient oAuth2Client,
                                                      final OkHttpClient okHttpClient,
                                                      final Request request,
                                                      final AuthState authState) throws IOException {
        Response response = okHttpClient.newBuilder().authenticator(Utils.getAuthenticator(oAuth2Client, authState))
                .build().newCall(request).execute();

        return new OAuthResponse(response);
    }
}
