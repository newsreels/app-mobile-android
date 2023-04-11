package com.ziro.bullet.activities;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Constants;

import java.util.Locale;

public class BaseActivity extends LocalizationActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        super.onCreate(savedInstanceState);
    }

    //    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(LocaleManager.onAttach(base));
//    }

    @Override
    protected void onPause() {
        super.onPause();
        PrefConfig prefConfigBase = new PrefConfig(this);
        if (prefConfigBase.getBulletAudioMute() != Constants.muted) {
            prefConfigBase.setBulletAudioMute(Constants.muted);
        }
    }
}
