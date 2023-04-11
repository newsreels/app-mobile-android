package com.ziro.bullet.fragments;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.ziro.bullet.R;

import org.jetbrains.annotations.NotNull;

public class YoutubePopup {
    private YouTubePlayerView youtube_view;
    private Context context;
    private Dialog dialog;
    private AbstractYouTubePlayerListener youtubeListener;
    private String videoId;


    // CONSTRUCTORS
    public YoutubePopup(Context context) {
        this.context = context;
    }


    // SHOW DIALOG METHOD
    public void showDialog(String videoId) {
        dialog = new Dialog(context, R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.youtube_popup);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        youtube_view = dialog.findViewById(R.id.youtube_view);

        youtube_view.addYouTubePlayerListener(youtubeListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                if (!TextUtils.isEmpty(videoId)) {
                    youTubePlayer.cueVideo(videoId, 0);
                    youTubePlayer.play();
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

}
