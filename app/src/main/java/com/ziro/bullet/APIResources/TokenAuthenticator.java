package com.ziro.bullet.APIResources;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.ziro.bullet.BulletApp;
import com.ziro.bullet.activities.SplashActivity;
import com.ziro.bullet.auth.OAuthResponse;
import com.ziro.bullet.auth.TokenGenerator;
import com.ziro.bullet.data.PrefConfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by shine_joseph on 27/05/20.
 * <p>
 * * this class will be called when any api returns 401(unauthenticated).
 * * Here we will try to renew token with 0Auth2 and if it still fails, then we can forcefully logout the user.
 */
public class TokenAuthenticator implements Authenticator {
    private static final String TAG = TokenAuthenticator.class.getSimpleName();
    private TokenGenerator mTokenGenerator;
    private PrefConfig mPrefConfig;
    private Activity activity;


    public TokenAuthenticator(Activity activity) {
        this.activity = activity;
        mTokenGenerator = new TokenGenerator();
        mPrefConfig = new PrefConfig(BulletApp.getInstance());
    }

    public TokenAuthenticator() {
        mTokenGenerator = new TokenGenerator();
        mPrefConfig = new PrefConfig(BulletApp.getInstance());
    }

    @Override
    public Request authenticate(Route route, @NotNull Response response) {
        if (mPrefConfig != null) {
            try {
                OAuthResponse oAuthResponse = mTokenGenerator.refreshToken(mPrefConfig.getRefreshToken(), mPrefConfig.isLanguagePushedToServer());
                if (oAuthResponse.isSuccessful()) {
                    if (oAuthResponse.getAccessToken() != null)
                        mPrefConfig.setAccessToken(oAuthResponse.getAccessToken());
                    if (oAuthResponse.getRefreshToken() != null)
                        mPrefConfig.setRefreshToken(oAuthResponse.getRefreshToken());
                    return response.request().newBuilder()
                            .header("Authorization", "Bearer " + mPrefConfig.getAccessToken())
                            .build();
                } else {
                    if (!TextUtils.isEmpty(mPrefConfig.getUserEmail())) { // just adding this check to call logout one time only.
                        logout();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private void logout() {
        if (activity == null) {
            return;
        }
        mPrefConfig.clear();
        mPrefConfig.setFirstTimeLaunch(false);
        ApiClient.cancelAll();

        Intent intent = new Intent(activity, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}
