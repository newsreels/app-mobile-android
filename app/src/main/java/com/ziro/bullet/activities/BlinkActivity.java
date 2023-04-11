package com.ziro.bullet.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.MenuViewCallback;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ziro.bullet.utills.Constants.INTENT_ACTION_ARTICLE;

public class BlinkActivity extends BaseActivity {

    PrefConfig prefConfig;
    private CardView continue_;
    private CardView gotit;
    private RelativeLayout boxMain;
    private RelativeLayout left;
    private RelativeLayout right;
    private ImageView gif;
    private ImageView right_;
    private ImageView left_;
    private ImageView center;
    private TextView box_text;
    private TextView btn_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blink);
        prefConfig = new PrefConfig(this);
        prefConfig.resetTapIntro();
        bindViews();
        step1();
    }

    private void bindViews() {
        gotit = findViewById(R.id.gotit);
        center = findViewById(R.id.center);
        continue_ = findViewById(R.id.continue_);
        right_ = findViewById(R.id.right_);
        left_ = findViewById(R.id.left_);
        boxMain = findViewById(R.id.boxMain);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        gif = findViewById(R.id.gif);
        box_text = findViewById(R.id.box_text);
        btn_text = findViewById(R.id.btn_text);
    }

    private void step1() {
        continue_.setVisibility(View.VISIBLE);
        center.setVisibility(View.VISIBLE);
        gotit.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
        left.setVisibility(View.GONE);
        left_.setVisibility(View.GONE);
        right_.setVisibility(View.GONE);
        boxMain.setVisibility(View.VISIBLE);
        box_text.setText(getString(R.string.explore_the_news_in_bullets_in_3_simple_steps));
        btn_text.setText(getString(R.string.continuee));
        Glide.with(BlinkActivity.this)
                .load(R.raw.hand)
                .into(center);
        continue_.setOnClickListener(v -> {
            continue_.setVisibility(View.GONE);
            boxMain.setVisibility(View.GONE);
            step2();
        });
    }

    private void step2() {
        gotit.setVisibility(View.GONE);
        continue_.setVisibility(View.GONE);
        center.setVisibility(View.GONE);
        boxMain.setVisibility(View.VISIBLE);
        box_text.setText(getString(R.string.tap_to_next));
        right.setVisibility(View.VISIBLE);
        right_.setVisibility(View.VISIBLE);
        Glide.with(BlinkActivity.this)
                .load(R.raw.hand)
                .into(right_);
        left.setVisibility(View.GONE);
        left_.setVisibility(View.GONE);
        box_text.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        right.setOnClickListener(v -> step3());
    }

    private void step3() {
        Utils.broadcastIntent(this, "next", INTENT_ACTION_ARTICLE);
        gotit.setVisibility(View.GONE);
        continue_.setVisibility(View.GONE);
        center.setVisibility(View.GONE);
        boxMain.setVisibility(View.VISIBLE);
        box_text.setText(getString(R.string.tap_to_previous));
        right.setVisibility(View.GONE);
        right_.setVisibility(View.GONE);
        left.setVisibility(View.VISIBLE);
        left_.setVisibility(View.VISIBLE);
        Glide.with(BlinkActivity.this)
                .load(R.raw.hand)
                .into(left_);
        box_text.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        left.setOnClickListener(v -> {
            updateConfig();
            finish();
//            step4();
        });
    }

    private void step4() {
        Utils.broadcastIntent(this, "prev", INTENT_ACTION_ARTICLE);
        gotit.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
        left.setVisibility(View.GONE);
        left_.setVisibility(View.GONE);
        right_.setVisibility(View.GONE);
        continue_.setVisibility(View.VISIBLE);
        center.setVisibility(View.VISIBLE);
        boxMain.setVisibility(View.VISIBLE);
        box_text.setText(getString(R.string.change_view_mode));
        box_text.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        btn_text.setText(getString(R.string.lear_more));
        continue_.setOnClickListener(v -> {
            boxMain.setVisibility(View.GONE);
            continue_.setVisibility(View.GONE);
            center.setVisibility(View.GONE);
            gif.setVisibility(View.VISIBLE);
            gotit.setVisibility(View.VISIBLE);
//            Glide.with(BlinkActivity.this)
//                    .asGif()
//                    .load(R.raw.list)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(gif);
        });
        gotit.setOnClickListener(v -> {
            updateConfig();
            finish();
        });
    }

    public void updateConfig() {
        if (prefConfig != null) {
            if (!InternetCheckHelper.isConnected()) {
                Log.e("RESPONSE", "no internet");
            } else {
                Call<ResponseBody> call = ApiClient
                        .getInstance(this)
                        .getApi()
                        .updateConfig("Bearer " + prefConfig.getAccessToken(), true);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response != null) {
                            Log.e("RESPONSE", "code : " + response.code());
                            if (response.code() == 200) {
                                prefConfig.setTapIntro(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (t != null)
                            Log.e("RESPONSE", "code : " + t.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}