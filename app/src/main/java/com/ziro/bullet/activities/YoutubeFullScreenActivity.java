package com.ziro.bullet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Surface;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.ziro.bullet.Helper.SimpleOrientationListener;
import com.ziro.bullet.R;
import com.ziro.bullet.model.FullScreenVideo;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.FullScreenHelper;

public class YoutubeFullScreenActivity extends BaseActivity {

    public static boolean isSowing = false;

    private YouTubePlayerView youTubePlayerView;
    private int mPosition;
    private long duration;
    private String url;
    private YouTubePlayerTracker tracker;
    private boolean backPressed;
    private boolean isManual;

    public static void start(Fragment fragment, Activity activity, String link, String mode,
                             int position, long duration, boolean isManual) {
        if(!isSowing) {
            isSowing = true;
            Intent intent = new Intent(activity, YoutubeFullScreenActivity.class);
            intent.putExtra("url", link);
            intent.putExtra("mode", mode);
            intent.putExtra("position", position);
            intent.putExtra("duration", duration);
            intent.putExtra("isManual", isManual);
            fragment.startActivityForResult(intent, Constants.VideoDurationRequestCode);
            activity.overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_full_screen);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        tracker = new YouTubePlayerTracker();
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.enterFullScreen();

        if (getIntent() != null) {
            String mode = getIntent().getStringExtra("mode");
            url = getIntent().getStringExtra("url");
            mPosition = getIntent().getIntExtra("position", -1);
            duration = getIntent().getLongExtra("duration", 0);
            isManual = getIntent().getBooleanExtra("isManual", false);
        }

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(url, duration);
                youTubePlayer.addListener(tracker);
            }
        });


        FullScreenHelper fullScreenHelper = new FullScreenHelper(this, null);
        fullScreenHelper.enterFullScreen();

        SimpleOrientationListener mOrientationListener = new SimpleOrientationListener(this) {
            @Override
            public void onSimpleOrientationChanged(int orientation) {
                if(android.provider.Settings.System.getInt(getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1 && !isManual) {
                    if (orientation == Surface.ROTATION_0 || orientation == Surface.ROTATION_180) {
                        onBackPressed();
                    }
                }
            }
        };
        mOrientationListener.enable();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(!backPressed) {
            backPressed = true;
            duration = (long) tracker.getCurrentSecond();
            youTubePlayerView.release();
            Intent intent = new Intent();
            intent.putExtra("position", mPosition);
            intent.putExtra("duration", duration);
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSowing = false;
    }
}