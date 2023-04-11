package com.ziro.bullet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

@SuppressLint("UseCompatLoadingForDrawables")
public class PersonalizationActivity extends BaseActivity {

    private LinearLayout tab2;
    private LinearLayout tab3;
    private TextView tab2_txt;
    private TextView tab3_txt;
    private ConstraintLayout changeTheme;
    private RelativeLayout ivBack;
    private ConstraintLayout audioSettings;

    private LinearLayout on;
    private LinearLayout off;
    private TextView on_text;
    private TextView off_text;

    private ConstraintLayout hapticBtn;

    private PrefConfig mPrefConfig;

    private LinearLayout personalizationLayout;
    private TextView headerText;
    private TextView colorThemeText;
    private TextView hapticText;
    private TextView audioSettingsText;

    private LinearLayout menu;
    private LinearLayout hapticSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalization);

        mPrefConfig = new PrefConfig(this);

        bindViews();

        setTheme();
        setHaptics();

        listeners();
    }

    private void setHaptics(){
        if (mPrefConfig.isHaptic()) {
            on();
        } else {
            off();
        }
    }

    private void bindViews(){
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);
        tab2_txt = findViewById(R.id.tab2_text);
        tab3_txt = findViewById(R.id.tab3_text);
        changeTheme = findViewById(R.id.change_theme);
        ivBack = findViewById(R.id.ivBack);
        audioSettings = findViewById(R.id.audio_settings);
        on = findViewById(R.id.on);
        off = findViewById(R.id.off);
        on_text = findViewById(R.id.on_text);
        off_text = findViewById(R.id.off_text);
        hapticBtn = findViewById(R.id.haptic_btn);
        personalizationLayout = findViewById(R.id.personalization_layout);
        headerText = findViewById(R.id.headerText);
        colorThemeText = findViewById(R.id.color_theme_text);
        hapticText = findViewById(R.id.haptic_text);
        audioSettingsText = findViewById(R.id.audio_settings_text);
        menu = findViewById(R.id.menu);
        hapticSwitch = findViewById(R.id.hapticSwitch);
    }

    private void listeners(){
        changeTheme.setOnClickListener(v -> {
            if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
                mPrefConfig.setAppTheme(Constants.LIGHT);
                lightMode();
            } else if(mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)){
                mPrefConfig.setAppTheme(Constants.DARK);
                darkMode();
            }

            invalidateView();
        });

        ivBack.setOnClickListener(v -> onBackPressed());

        audioSettings.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalizationActivity.this, AudioSettingsActivity.class);
            startActivity(intent);
        });

        hapticBtn.setOnClickListener(v -> {
            if (mPrefConfig.isHaptic()) {
                mPrefConfig.setHaptic(false);
                off();
            } else if (!mPrefConfig.isHaptic()){
                mPrefConfig.setHaptic(true);
                on();
            }
        });
    }

    private void setTheme() {
        if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.AUTO)) {
            systemMode();
        } else if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
            darkMode();
        } else {
            lightMode();
        }
    }

    private void systemMode() {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                new Components().settingStatusBarColors(this, "black");
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                new Components().settingStatusBarColors(this, "white");
                break;
        }

        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }


    private void dark() {

        tab3.setBackground(getResources().getDrawable(R.drawable.shape));
        tab3_txt.setTextColor(getResources().getColor(R.color.tab_selected));
        tab2.setBackground(null);
        tab2_txt.setTextColor(getResources().getColor(R.color.tab_unselected));
    }

    private void light() {
        tab2.setBackground(getResources().getDrawable(R.drawable.shape));
        tab2_txt.setTextColor(getResources().getColor(R.color.tab_selected));
        tab3.setBackground(null);
        tab3_txt.setTextColor(getResources().getColor(R.color.tab_unselected));
    }


    private void lightMode() {
        new Components().settingStatusBarColors(this, "white");
        Utils.checkAppModeColor(this, false);
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_NO);

        light();
    }

    private void darkMode() {
        new Components().settingStatusBarColors(this, "black");
        Utils.checkAppModeColor(this, false);
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_YES);

        dark();
    }

    private void on() {
        on.setBackground(getDrawable(R.drawable.shape));
        on_text.setTextColor(getResources().getColor(R.color.tab_selected));
        off.setBackground(null);
        off_text.setTextColor(getResources().getColor(R.color.tab_unselected));
    }

    private void off() {
        off.setBackground(getDrawable(R.drawable.shape));
        off_text.setTextColor(getResources().getColor(R.color.tab_selected));
        on.setBackground(null);
        on_text.setTextColor(getResources().getColor(R.color.tab_unselected));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void invalidateView() {
        personalizationLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        menu.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_dark));
        hapticSwitch.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_dark));

        headerText.setTextColor(ContextCompat.getColor(this, R.color.textHeader));
        colorThemeText.setTextColor(ContextCompat.getColor(this, R.color.textHeader));
        hapticText.setTextColor(ContextCompat.getColor(this, R.color.textHeader));
        audioSettingsText.setTextColor(ContextCompat.getColor(this, R.color.textHeader));

        setHaptics();

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                dark();
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                light();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}