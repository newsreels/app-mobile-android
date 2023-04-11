package com.ziro.bullet.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.ziro.bullet.R;

public class FullScreenVideoActivity extends BaseActivity {

    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private PlayerView playerView;
    private SimpleExoPlayer player;

    private String mediaUri = "";
    private int mPosition;
    private long duration = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        playerView = findViewById(R.id.ep_video_view);


        mediaUri = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4";
        mediaUri = getIntent().getStringExtra("url");
        mPosition = getIntent().getIntExtra("position", 0);
        duration = getIntent().getLongExtra("duration", 0);

//        findViewById(R.id.smallScreen).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(FullScreenVideoActivity.this, "Test", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void initializePlayer(String uri) {
        if (uri.isEmpty()) {
            return;
        }

//        if (player == null) {
//            // a factory to create an AdaptiveVideoTrackSelection
//            AdaptiveTrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
//
//            player = ExoPlayerFactory.newSimpleInstance(
//                    this,
//                    new DefaultRenderersFactory(this),
//                    new DefaultTrackSelector(adaptiveTrackSelectionFactory),
//                    new DefaultLoadControl());
//
//            player.addListener(new Player.EventListener() {
//                @Override
//                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//
//                }
//            });
//        }

        playerView.setPlayer(player);
//
//        player.playWhenReady = playWhenReady
//        player.seekTo(currentWindow, playbackPosition)

        player.setPlayWhenReady(true);
//
//        MediaSource mediaSource = buildMediaSource(Uri.parse(uri));
//        player.prepare(mediaSource, true, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer(mediaUri);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideSystemUi();
        if (Util.SDK_INT <= 23) {
            initializePlayer(mediaUri);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
//        playbackPosition = player.currentPosition
//        currentWindow = player.currentWindowIndex
//        playWhenReady = player.playWhenReady
//        player.removeListener(componentListener)
//        player.setVideoListener(null)
//        player.removeAnalyticsListener(componentListener)
//        player.removeAnalyticsListener(componentListener)
//        player.release();

        player.setPlayWhenReady(false);


    }


//    private MediaSource buildMediaSource(Uri uri) {
//
//        String userAgent = "exoplayer-codelab";
//
//        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
//            return new ProgressiveMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
//                    .createMediaSource(uri);
//        } else if (uri.getLastPathSegment().contains("m3u8")) {
//            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
//                    .createMediaSource(uri);
//        } else {
//            DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(
//                    new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER));
//            DefaultHttpDataSourceFactory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
//            return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri);
//        }
//    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    @Override
    public void onBackPressed() {
        duration = player.getDuration();
        Intent intent = new Intent();
        intent.putExtra("position", mPosition);
        intent.putExtra("duration", duration);
        setResult(RESULT_OK, intent);
        finish();
    }
}
