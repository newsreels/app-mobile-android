package com.newsreels.app.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.newsreels.app.BuildConfig;
import com.newsreels.app.R;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.utills.Utils;

public class AboutUsActivity extends BaseActivity {
    private ImageView back_img;
    private TextView version;
    private ImageView gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_about_us);

        back_img = findViewById(R.id.back_img);
        version = findViewById(R.id.version);
        gif = findViewById(R.id.gif);
        PrefConfig prefConfig = new PrefConfig(this);

//        Glide.with(this).load(Utils.getAboutUsGifTheme(prefConfig.getAppTheme())).into(gif);

        version.setText(getString(R.string.app_name) + " "+ BuildConfig.VERSION_NAME);
        // HEADER BACK CLICK
        back_img.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
