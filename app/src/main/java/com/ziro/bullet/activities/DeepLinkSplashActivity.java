package com.ziro.bullet.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

public class DeepLinkSplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private PrefConfig prefConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Components().settingStatusBarColors(this, "black");
        setContentView(R.layout.splash);
        prefConfig = new PrefConfig(this);
        AnalyticsEvents.INSTANCE.logEvent(this,
                Events.APP_OPEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            return;
        }

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d(TAG, "onSuccess: deepLink" + deepLink);
                            Log.d(TAG, "onSuccess: deepLink" + pendingDynamicLinkData.getExtensions());
                            Log.d(TAG, "onSuccess: deepLink id = " + deepLink.getQueryParameter("id"));
                            Log.d(TAG, "onSuccess: deepLink qry = " + deepLink.getQuery());
                            Log.d(TAG, "onSuccess: deepLink qry prmtr names= " + deepLink.getQueryParameterNames());
                        }

                        Intent intent;
                        if (TextUtils.isEmpty(prefConfig.getAccessToken())) {
                            if (prefConfig.getFirstTimeLaunch()) {
                                intent = new Intent(DeepLinkSplashActivity.this, LanguageActivity.class);
                            } else {
                                intent = new Intent(DeepLinkSplashActivity.this, FirstActivity.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        } else {
                            intent = new Intent(DeepLinkSplashActivity.this, MainActivityNew.class);
//                            intent.putExtra("source_id", getIntent().getExtras().getString("source_id"));
//                            intent.putExtra("source_name", getIntent().getExtras().getString("source_name"));
                            if (deepLink != null && !TextUtils.isEmpty(deepLink.getLastPathSegment()) && deepLink.getLastPathSegment().contains("articles"))
                                intent.putExtra("article_id", deepLink.getQueryParameter("id"));
                            else if (deepLink != null && !TextUtils.isEmpty(deepLink.getLastPathSegment()) && deepLink.getLastPathSegment().contains("reel"))
                                intent.putExtra("reel_context", deepLink.getQueryParameter("context"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            clearIntent();
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finishAfterTransition();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);

                        Intent intent;
                        if (TextUtils.isEmpty(prefConfig.getAccessToken())) {
                            if (prefConfig.getFirstTimeLaunch()) {
                                intent = new Intent(DeepLinkSplashActivity.this, LanguageActivity.class);
                            } else {
                                intent = new Intent(DeepLinkSplashActivity.this, FirstActivity.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        } else {
                            intent = new Intent(DeepLinkSplashActivity.this, MainActivityNew.class);
                            clearIntent();
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finishAfterTransition();
                    }
                });

        super.onWindowFocusChanged(true);
    }

    private void clearIntent() {
        getIntent().getExtras().clear();
    }
}
