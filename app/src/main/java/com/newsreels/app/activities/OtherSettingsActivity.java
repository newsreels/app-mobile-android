package com.newsreels.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.newsreels.app.R;
import com.newsreels.app.analytics.AnalyticsEvents;
import com.newsreels.app.analytics.Events;
import com.newsreels.app.fragments.GuidelinePopup;
import com.newsreels.app.interfaces.ReportConcernDialog;
import com.newsreels.app.utills.Constants;
import com.newsreels.app.utills.Utils;

public class OtherSettingsActivity extends BaseActivity {

    private ConstraintLayout aboutBtn;
    private ConstraintLayout termsBtn;
    private ConstraintLayout privacyBtn;
    private ConstraintLayout communityBtn;
    private RelativeLayout ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_settings);

        bindViews();
        listeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkAppModeColor(this, false);
    }

    private void bindViews() {
        communityBtn = findViewById(R.id.community_btn);
        aboutBtn = findViewById(R.id.about_btn);
        termsBtn = findViewById(R.id.terms_btn);
        privacyBtn = findViewById(R.id.privacy_btn);
        ivBack = findViewById(R.id.ivBack);
    }

    private void listeners() {
        ivBack.setOnClickListener(v -> finish());
        aboutBtn.setOnClickListener(v -> {
            AnalyticsEvents.INSTANCE.logEvent(this,
                    Events.ABOUT_CLICK);
            Intent intentAbout = new Intent(OtherSettingsActivity.this, AboutUsActivity.class);
            intentAbout.putExtra("title", getString(R.string.about));
            startActivity(intentAbout);
        });
        termsBtn.setOnClickListener(v -> Utils.openWebView(OtherSettingsActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions)));
        privacyBtn.setOnClickListener(v -> Utils.openWebView(OtherSettingsActivity.this, "https://www.newsinbullets.app/privacy?header=false", getString(R.string.privacy_policy)));
        communityBtn.setOnClickListener(v -> Utils.openWebView(OtherSettingsActivity.this, "https://www.newsinbullets.app/community-guidelines?header=false", getString(R.string.community_guideline_)));
    }
}