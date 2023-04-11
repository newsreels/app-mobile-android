package com.ziro.bullet.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

public class OAuthClient {
    private final String clientId;
    private final String clientSecret;
    private final String url;
    private final String langauge;
    private final OkHttpClient okHttpClient;

    private String scope;
    private String grantType;

    private String username;
    private String password;

    private String token;
    private String tokenType;
    private String deviceId;

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    private Map<String, String> parameters;

    private OAuthClient(OAuthClient.Builder builder) {
        this.token = builder.token;
        this.tokenType = builder.tokenType;
        this.username = builder.username;
        this.password = builder.password;
        this.deviceId = builder.deviceId;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.url = builder.url;
        this.scope = builder.scope;
        this.grantType = builder.grantType;
        this.okHttpClient = builder.okHttpClient;
        this.parameters = builder.parameters;
        this.langauge = builder.langauge;
    }

    protected String getScope() {
        return scope;
    }

    protected String getGrantType() {
        return grantType;
    }

    protected String getClientId() {
        return clientId;
    }

    protected String getClientSecret() {
        return clientSecret;
    }

    protected String getUrl() {
        return url;
    }

    protected String getUsername() {
        return username;
    }

    public String getDeviceId() {
        return deviceId;
    }

    protected String getPassword() {
        return password;
    }

    protected String getLangauge() {
        return langauge;
    }

    public OAuthResponse refreshAccessToken(String refreshToken) throws IOException {
        if (this.grantType == null)
            this.grantType = Constants.GRANT_TYPE_REFRESH;
        return Access.refreshAccessToken(refreshToken, this);

    }

    public void refreshAccessToken(final String refreshToken, final OAuthResponseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OAuthResponse response;
                try {
                    response = refreshAccessToken(refreshToken);
                    callback.onResponse(response);
                } catch (Exception e) {
                    response = new OAuthResponse(e);
                    callback.onResponse(response);
                }
            }
        }).start();
    }

    public OAuthResponse requestAccessToken() throws IOException {
        if (this.grantType == null)
            this.grantType = Constants.GRANT_TYPE_PASSWORD;
        return Access.getToken(this);
    }

    public void requestAccessToken(final OAuthResponseCallback callback) {
        new Thread(() -> {
            OAuthResponse response;
            try {
                response = requestAccessToken();
                callback.onResponse(response);
            } catch (Exception e) {
                response = new OAuthResponse(e);
                callback.onResponse(response);
            }
        }).start();
    }

    protected OkHttpClient getOkHttpClient() {
        if (this.okHttpClient == null) {
            return new OkHttpClient();
        } else {
            return this.okHttpClient;
        }
    }

    protected Map<String, String> getParameters() {
        if (parameters == null) {
            return new HashMap<>();
        } else {
            return parameters;
        }
    }

    protected Map<String, String> getFieldsAsMap() {
        Map<String, String> oAuthParams = new HashMap<>();
        oAuthParams.put(Constants.POST_CLIENT_ID, getClientId());
        oAuthParams.put(Constants.POST_CLIENT_SECRET, getClientSecret());
        oAuthParams.put(Constants.POST_GRANT_TYPE, getGrantType());
        oAuthParams.put(Constants.POST_SCOPE, getScope());
        oAuthParams.put(Constants.POST_USERNAME, getUsername());
        oAuthParams.put(Constants.POST_DEVICE_CODE, getDeviceId());
        oAuthParams.put(Constants.POST_SUBJECT_TOKEN_TYPE, getTokenType());
        oAuthParams.put(Constants.POST_SUBJECT_TOKEN, getToken());
        oAuthParams.put(Constants.POST_PASSWORD, getPassword());
        return oAuthParams;
    }

    public static class Builder {
        private final String clientId;
        private final String clientSecret;
        private final String url;
        private String langauge;

        private String scope;
        private String grantType;

        private String username;
        private String password;
        private String deviceId;

        private String token;
        private String tokenType;

        private OkHttpClient okHttpClient;

        private Map<String, String> parameters;

        public Builder(String username, String password, String clientId, String clientSecret,
                       String url) {
            this.username = username;
            this.password = password;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.url = url;
            this.okHttpClient = null;
        }

        public Builder(String token, String grantType, String tokenType, String clientId, String clientSecret,
                       String url) {
            this.token = token;
            this.grantType = grantType;
            this.tokenType = tokenType;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.url = url;
            this.okHttpClient = null;
        }

        public Builder(String clientId, String clientSecret, String url) {
            this.username = null;
            this.password = null;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.url = url;
            this.okHttpClient = null;
        }

        public OAuthClient.Builder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public OAuthClient.Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public OAuthClient.Builder username(String username) {
            this.username = username;
            return this;
        }

        public OAuthClient.Builder password(String password) {
            this.password = password;
            return this;
        }

        public OAuthClient.Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public OAuthClient.Builder langauge(String langauge) {
            this.langauge = langauge;
            return this;
        }

        public OAuthClient.Builder okHttpClient(OkHttpClient client) {
            this.okHttpClient = client;
            return this;
        }

        public OAuthClient.Builder parameters(Map<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public OAuthClient build() {
            return new OAuthClient(this);
        }

    }

}
