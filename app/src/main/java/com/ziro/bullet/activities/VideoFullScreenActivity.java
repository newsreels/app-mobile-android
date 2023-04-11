package com.ziro.bullet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.Helper.SimpleOrientationListener;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.FullScreenVideo.FullScreenAdapter;
import com.ziro.bullet.adapters.FullScreenVideo.VideoViewHolder;
import com.ziro.bullet.adapters.FullScreenVideo.YoutubeViewHolder;
import com.ziro.bullet.model.FullScreenVideo;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.FullScreenHelper;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;

import java.util.ArrayList;

import im.ene.toro.PlayerSelector;
import im.ene.toro.widget.Container;

public class VideoFullScreenActivity extends BaseActivity {

    private PlayerSelector selector = PlayerSelector.DEFAULT;
    private Container container;
    private FullScreenAdapter adapter;
    private ArrayList<FullScreenVideo> arrayList;
    private SpeedyLinearLayoutManager cardLinearLayoutManager;
    private long mDuration = 0;
    private int mPosition = -1;

    public static void start(Activity activity, String link, String mode, int position, long duration) {

        Intent intent = new Intent(activity, VideoFullScreenActivity.class);
        intent.putExtra("url", link);
        intent.putExtra("mode", mode);
        intent.putExtra("position", position);
        intent.putExtra("duration", duration);
        activity.startActivityForResult(intent, Constants.VideoDurationRequestCode);
        activity.overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
    }

//    @Override
//    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        container = findViewById(R.id.container);
        arrayList = new ArrayList<>();
        if (getIntent() != null) {
            String mode = getIntent().getStringExtra("mode");
            String url = getIntent().getStringExtra("url");
            mPosition = getIntent().getIntExtra("position", -1);
            long duration = getIntent().getLongExtra("duration", 0);
            if (!TextUtils.isEmpty(mode)) {
                switch (mode) {
                    case "youtube":
                        arrayList.add(new FullScreenVideo(url, "YOUTUBE", duration));
                        break;
                    case "video":
                        arrayList.add(new FullScreenVideo(url, "VIDEO", duration));
//                        Intent intent = new Intent(this, FullScreenVideoActivity.class);
//                        intent.putExtra("url", url);
//                        intent.putExtra("position", mPosition);
//                        // startActivity(intent);
//                        startActivityForResult(intent, Constants.VideoDurationRequestCode);
//                        return;
                         break;
                    default:
                }
            }
        }
        adapter = new FullScreenAdapter(this, arrayList, duration -> {
            mDuration = duration;
            onBackPressed();
        });
        cardLinearLayoutManager = new SpeedyLinearLayoutManager(this);
        container.setLayoutManager(cardLinearLayoutManager);
        container.setOnFlingListener(null);
        container.setAdapter(adapter);
        container.setCacheManager(adapter);
        container.setPlayerSelector(selector);

        FullScreenHelper fullScreenHelper = new FullScreenHelper(this, null);
        fullScreenHelper.enterFullScreen();

        SimpleOrientationListener mOrientationListener = new SimpleOrientationListener(this) {
            @Override
            public void onSimpleOrientationChanged(int orientation) {
//                if (orientation == Surface.ROTATION_0 || orientation == Surface.ROTATION_180) {
//                    onBackPressed();
//                }
            }
        };
        mOrientationListener.enable();

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        overridePendingTransition(0, 0);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.VideoDurationRequestCode) {
            int position = data.getIntExtra("position", -1);
            long duration = data.getLongExtra("duration", 0);

            Intent intent = new Intent();
            intent.putExtra("position", position);
            intent.putExtra("duration", duration);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (container != null) {
            RecyclerView.ViewHolder holder = container.findViewHolderForAdapterPosition(0);
            if (holder != null) {
                if (holder instanceof VideoViewHolder) {
                    mDuration = ((VideoViewHolder) holder).getDuration();
                } else if (holder instanceof YoutubeViewHolder) {
                    mDuration = ((YoutubeViewHolder) holder).getDuration();
                }
            }
        }
        Intent intent = new Intent();
        intent.putExtra("position", mPosition);
        intent.putExtra("duration", mDuration);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
    }
}