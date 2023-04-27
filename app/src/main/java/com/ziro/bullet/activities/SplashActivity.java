package com.ziro.bullet.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.data.models.push.Push;
import com.ziro.bullet.interfaces.PasswordInterface;
import com.ziro.bullet.interfaces.PushNotificationInterface;
import com.ziro.bullet.interfaces.UserConfigCallback;
import com.ziro.bullet.presenter.FCMPresenter;
import com.ziro.bullet.presenter.PushNotificationPresenter;
import com.ziro.bullet.presenter.SocialLoginPresenter;
import com.ziro.bullet.presenter.UserConfigPresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SplashActivity extends BaseActivity implements PasswordInterface, UserConfigCallback, PushNotificationInterface {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private PrefConfig prefConfig;
    private ImageView splashImg;
    private Intent intentStored;
    private boolean delayCompleted;
    private ProgressBar progress;
    private SocialLoginPresenter mSocialLoginPresenter;
    private UserConfigPresenter configPresenter;
    private FCMPresenter fcmPresenter;
    private PushNotificationPresenter pushpresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Components().transparentStatusBar(this, "black");
        setContentView(R.layout.splash);

        splashImg = findViewById(R.id.splash_img);
        progress = findViewById(R.id.progress_barn);

        mSocialLoginPresenter = new SocialLoginPresenter(this, this);
        configPresenter = new UserConfigPresenter(this, this);
        fcmPresenter = new FCMPresenter(this);
        pushpresenter = new PushNotificationPresenter(this, this);
        prefConfig = new PrefConfig(this);
        AnalyticsEvents.INSTANCE.logEvent(this, Events.APP_OPEN);
        InternetCheckHelper.getInstance().startObservingConnection(getApplicationContext());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            delayCompleted = true;
            openIntent(intentStored);
        }, 3000);
    }

    private void openIntent(Intent intent) {
        if (delayCompleted && intent != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finishAfterTransition();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
//        return;
        if (!hasFocus) {
            return;
        }

        Constants.HomeSelectedFragment = Constants.BOTTOM_TAB_HOME;
        if (!openArticleFromNotification()) {
            getDynamicLinkData();
        }
        super.onWindowFocusChanged(true);
    }

    private void getDynamicLinkData() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                // Get deep link from result (may be null if no link is found)
                Uri deepLink = null;
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.getLink();
                    Log.d(TAG, "onSuccess: deepLink" + deepLink);
                    Log.d(TAG, "onSuccess: deepLink" + pendingDynamicLinkData.getExtensions());
                    if (deepLink != null) {
                        Log.d(TAG, "onSuccess: deepLink id = " + deepLink.getQueryParameter("id"));
                        Log.d(TAG, "onSuccess: deepLink qry = " + deepLink.getQuery());
                        Log.d(TAG, "onSuccess: deepLink qry prmtr names= " + deepLink.getQueryParameterNames());
                        Log.d(TAG, "onSuccess: deepLink qry prmtr names= " + deepLink.getLastPathSegment());
                    }
                }

                Intent intent;
                if (TextUtils.isEmpty(prefConfig.getAccessToken())) {
                    skipLogin();
//                            intent = new Intent(SplashActivity.this, WelcomeActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivityNew.class);
//                            intent.putExtra("source_id", getIntent().getExtras().getString("source_id"));
//                            intent.putExtra("source_name", getIntent().getExtras().getString("source_name"));
                    if (deepLink != null && !TextUtils.isEmpty(deepLink.getLastPathSegment()) && deepLink.getLastPathSegment().contains("articles")) {
                        intent.putExtra("article_id", deepLink.getQueryParameter("id"));
                        Map<String, String> params = new HashMap<>();
                        params.put(Events.KEYS.REEL_ID, deepLink.getQueryParameter("id"));
                        AnalyticsEvents.INSTANCE.logEvent(SplashActivity.this, params, Events.SHARE_OPEN_ARTICLE);
                    } else if (deepLink != null && !TextUtils.isEmpty(deepLink.getLastPathSegment()) && deepLink.getLastPathSegment().contains("reel")) {
                        intent.putExtra("reel_context", deepLink.getQueryParameter("context"));
                        Map<String, String> params = new HashMap<>();
                        params.put(Events.KEYS.REEL_ID, deepLink.getQueryParameter("context"));
                        AnalyticsEvents.INSTANCE.logEvent(SplashActivity.this, params, Events.SHARE_OPEN_REEL);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    clearIntent();
                    intentStored = intent;
                    openIntent(intentStored);
                }
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        finishAfterTransition();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.w(TAG, "getDynamicLink:onFailure", e);
                Intent intent;
                if (TextUtils.isEmpty(prefConfig.getAccessToken())) {
                    skipLogin();
//                            intent = new Intent(SplashActivity.this, WelcomeActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivityNew.class);
                    clearIntent();
                    intentStored = intent;
                    openIntent(intentStored);
                }
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        finishAfterTransition();
            }
        });
    }

    private void skipLogin() {
        progress.setVisibility(View.VISIBLE);
        mSocialLoginPresenter.skipLogin(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    private boolean openArticleFromNotification() {
        if (!TextUtils.isEmpty(prefConfig.getAccessToken()) && getIntent().getExtras() != null)
            if (getIntent().getExtras().containsKey("type")) {
                if (Objects.equals(getIntent().getExtras().getString("type"), "widget_topic")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivityNew.class);
                    intent.putExtra("topic_id", getIntent().getExtras().getString("topic_id"));
                    intent.putExtra("topic_name", getIntent().getExtras().getString("topic_name"));

                    intent.putExtra("source_id", getIntent().getExtras().getString("source_id"));
                    intent.putExtra("source_name", getIntent().getExtras().getString("source_name"));
//                    intent.putExtra("article_id", getIntent().getExtras().getString("article_id"));
                    clearIntent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentStored = intent;
                    openIntent(intentStored);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    finishAfterTransition();
                } else if (Objects.equals(getIntent().getExtras().getString("type"), "widget_article")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivityNew.class);
//                    intent.putExtra("topic_id", getIntent().getExtras().getString("topic_id"));
//                    intent.putExtra("topic_name", getIntent().getExtras().getString("topic_name"));

                    intent.putExtra("source_id", getIntent().getExtras().getString("source_id"));
                    intent.putExtra("source_name", getIntent().getExtras().getString("source_name"));
                    intent.putExtra("article_id", getIntent().getExtras().getString("article_id"));
                    clearIntent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentStored = intent;
                    openIntent(intentStored);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    finishAfterTransition();
                } else if (Objects.equals(getIntent().getExtras().getString("type"), "article.new")) {
                    AnalyticsEvents.INSTANCE.logEvent(this, Events.NOTIFICATION_OPEN_ARTICLE);
                    Intent intent = new Intent(SplashActivity.this, MainActivityNew.class);
                    intent.putExtra("source_id", getIntent().getExtras().getString("source_id"));
                    intent.putExtra("source_name", getIntent().getExtras().getString("source_name"));
                    intent.putExtra("article_id", getIntent().getExtras().getString("article_id"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    clearIntent();
                    intentStored = intent;
                    openIntent(intentStored);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    finishAfterTransition();
                } else if (Objects.equals(getIntent().getExtras().getString("type"), "reel.new")) {
                    AnalyticsEvents.INSTANCE.logEvent(this, Events.NOTIFICATION_OPEN_REEL);
                    Intent intent = new Intent(SplashActivity.this, MainActivityNew.class);
                    intent.putExtra("source_id", getIntent().getExtras().getString("source_id"));
                    intent.putExtra("source_name", getIntent().getExtras().getString("source_name"));
                    intent.putExtra("reel_id", getIntent().getExtras().getString("article_id"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    clearIntent();
                    intentStored = intent;
                    openIntent(intentStored);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    finishAfterTransition();
                } else {
                    AnalyticsEvents.INSTANCE.logEvent(this, Events.NOTIFICATION_OPEN);
                    Intent intent = new Intent(SplashActivity.this, MainActivityNew.class);
                    intent.putExtra("topic_id", getIntent().getExtras().getString("topic_id"));
                    intent.putExtra("topic_name", getIntent().getExtras().getString("topic_name"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    clearIntent();
                    intentStored = intent;
                    openIntent(intentStored);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    finishAfterTransition();
                }
                return true;
            }
        return false;
    }

    private void clearIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) getIntent().getExtras().clear();
    }

    @Override
    public void loaderShow(boolean flag) {
//        progress.setVisibility(View.VISIBLE);

    }

    @Override
    public void error(String error) {
//        progress.setVisibility(View.GONE);
        runOnUiThread(() -> Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void error404(String error) {
//        progress.setVisibility(View.GONE);
        runOnUiThread(() -> Toast.makeText(this, this.getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void success(Push push) {
        //add notification
        Log.e(TAG, "success: notification enalbled");
    }

    @Override
    public void SuccessFirst(boolean flag) {

    }

    @Override
    public void onUserConfigSuccess(UserConfigModel userConfigModel) {

        if (!this.isFinishing()) try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        task.getException().printStackTrace();
                    } else {
                        String token = task.getResult();
                        prefConfig.setFirebaseToken(token);
                        fcmPresenter.sentTokenToServer(prefConfig);
                        OneSignal.setExternalUserId(token);
                    }
                    Locale deviceLocale = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        deviceLocale = Resources.getSystem().getConfiguration().getLocales().get(0);
                    } else {
                        deviceLocale = Resources.getSystem().getConfiguration().locale;
                    }
                    mSocialLoginPresenter.selectLanguage(deviceLocale.getLanguage());

                    Intent intent = new Intent(SplashActivity.this, MainActivityNew.class);
                    clearIntent();
                    intentStored = intent;
                    openIntent(intentStored);
                    pushpresenter.onBoardingPushConfig(true, false, "1h");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void success(boolean flag) {
//        progress.setVisibility(View.GONE);

        if (flag) {
            configPresenter.getUserConfig(false);
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void resetSuccess(String str) {

    }
}