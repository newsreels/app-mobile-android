package com.ziro.bullet.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

public class DarkLightModeActivity extends BaseActivity {

    private static final String TAG = "DarkLightModeActivity";

    private PrefConfig prefConfig;

    private TabLayout mTabs;
    private View mBackgroundView;
    private TextView mTvHeader, mTvSubheader, mTvHelp;
    private RelativeLayout mContinue;
    private ImageView ivScreen;
    private LinearLayout llGradient;
    private LinearLayout llTabContainer;
    private RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
//        }

        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_dark_light_mode);
        prefConfig = new PrefConfig(this);
        bindView();

        if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)) {
            mTabs.getTabAt(1).select();
            mBackgroundView.setTranslationX(2000);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.white, DarkLightModeActivity.this.getTheme()));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            }
            mTvHeader.setTextColor(ContextCompat.getColor(DarkLightModeActivity.this, R.color.black));
            mTvSubheader.setTextColor(ContextCompat.getColor(DarkLightModeActivity.this, R.color.black));
            mContinue.setBackgroundResource(R.drawable.btn_blue);
            mTvHelp.setTextColor(ContextCompat.getColor(DarkLightModeActivity.this, R.color.black));
            ivScreen.setImageResource(R.drawable.screen_lite_mode);
            llGradient.setBackgroundResource(R.drawable.gradient_theme_lite);
            llTabContainer.setBackgroundResource(R.drawable.theme_tab_bg_lite);
        } else if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
            mTabs.getTabAt(0).select();
        }

        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getText().toString().equalsIgnoreCase(getString(R.string.dark))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(getResources().getColor(R.color.black, DarkLightModeActivity.this.getTheme()));
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
                    }
                    ObjectAnimator translateY = ObjectAnimator.ofFloat(mBackgroundView, "translationX", 0);
                    translateY.start();

//                    mBackgroundView.animate().translationX(0);
                    new Handler().postDelayed(() -> {
                        mTvHeader.setTextColor(ContextCompat.getColor(DarkLightModeActivity.this, R.color.white));
                        mTvSubheader.setTextColor(ContextCompat.getColor(DarkLightModeActivity.this, R.color.white));

                        tab.view.setBackgroundResource(R.drawable.theme_tab_indicator_green);
//                        mTabs.setSelectedTabIndicatorColor(ContextCompat.getColor(DarkLightModeActivity.this,R.color.themeGreen));

                        mContinue.setBackgroundResource(R.drawable.btn_green);
                        mTvHelp.setTextColor(ContextCompat.getColor(DarkLightModeActivity.this, R.color.white));
                        ivScreen.setImageResource(R.drawable.screen_dark_mode);
                        llGradient.setBackgroundResource(R.drawable.gradient_theme_dark);
                        llTabContainer.setBackgroundResource(R.drawable.theme_tab_bg_dark);
                    }, 150);
                    prefConfig.setAppTheme(Constants.DARK);
                } else {
                    ObjectAnimator translateY = ObjectAnimator.ofFloat(mBackgroundView, "translationX", 2000);
                    translateY.start();
//                    mBackgroundView.animate().translationX(2000);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().setStatusBarColor(getResources().getColor(R.color.white, DarkLightModeActivity.this.getTheme()));
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
                    }
                    new Handler().postDelayed(() -> {
                        mTvHeader.setTextColor(ContextCompat.getColor(DarkLightModeActivity.this, R.color.black));
                        mTvSubheader.setTextColor(ContextCompat.getColor(DarkLightModeActivity.this, R.color.black));
//                        mTabs.setSelectedTabIndicator(R.drawable.theme_tab_indicator_blue);

                        tab.view.setBackgroundResource(R.drawable.theme_tab_indicator_blue);
//                        mTabs.setSelectedTabIndicatorColor(ContextCompat.getColor(DarkLightModeActivity.this,R.color.blue));

                        mContinue.setBackgroundResource(R.drawable.btn_blue);
                        mTvHelp.setTextColor(ContextCompat.getColor(DarkLightModeActivity.this, R.color.black));
                        ivScreen.setImageResource(R.drawable.screen_lite_mode);
                        llGradient.setBackgroundResource(R.drawable.gradient_theme_lite);
                        llTabContainer.setBackgroundResource(R.drawable.theme_tab_bg_lite);
                    }, 150);
                    prefConfig.setAppTheme(Constants.LIGHT);
                }

//                changeTheme();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.view.setBackgroundResource(0);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContinue.setEnabled(false);
                changeTheme();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Log.d(TAG, "run: theme "+prefConfig.getAppTheme());
//                        mContinue.setEnabled(true);
                        Intent intent = new Intent(DarkLightModeActivity.this, MainActivityNew.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }
                }, 300);

            }
        });

        back.setOnClickListener(v -> onBackPressed());


        mTvHelp.setOnClickListener(v -> {
//            Utils.openHelpView(this, BuildConfig.HelpUrl, "Help");
            Intent intent = new Intent(this, HelpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });


//        systemMode();
    }


    private void changeTheme() {
        if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)) {
            lightMode();
        } else if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
            darkMode();
        }
    }
//prefConfig.setAppTheme(Constants.AUTO);

    private void lightMode() {
//        new Components().settingStatusBarColors(this, "white");
//        Utils.checkAppModeColor(this, false);
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void darkMode() {
//        new Components().settingStatusBarColors(this, "black");
//        Utils.checkAppModeColor(this, false);
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_YES);
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

    private void bindView() {
        mTabs = findViewById(R.id.tabs);
        mBackgroundView = findViewById(R.id.backgroundView);
        mTvHeader = findViewById(R.id.tvStyle);
        mTvSubheader = findViewById(R.id.tvStyleSub);
        mContinue = findViewById(R.id.done);
        mTvHelp = findViewById(R.id.help);
        ivScreen = findViewById(R.id.ivScreen);
        llGradient = findViewById(R.id.gradient);
        llTabContainer = findViewById(R.id.llTabContainer);
        back = findViewById(R.id.back);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeTheme();
    }

    @Override
    public void onBackPressed() {
        setResult(101);
        finishAfterTransition();
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
