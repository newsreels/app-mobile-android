package com.ziro.bullet.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.ziro.bullet.R;
import com.ziro.bullet.utills.Utils;

public class ForceUpdateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_force_update);

        findViewById(R.id.update_app).setOnClickListener(v -> {
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
            catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(Build.VERSION.SDK_INT>=21){
            finishAndRemoveTask();
        }
    }
}