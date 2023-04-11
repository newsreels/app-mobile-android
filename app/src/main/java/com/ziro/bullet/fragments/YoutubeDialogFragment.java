package com.ziro.bullet.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.ziro.bullet.R;
import com.ziro.bullet.interfaces.YoutubeCallback;

import org.jetbrains.annotations.NotNull;

public class YoutubeDialogFragment extends DialogFragment {

    private String videoId;
    private YouTubePlayerView youtube_view;
    private AbstractYouTubePlayerListener youtubeListener;
    private YoutubeCallback mYoutubeCallback;

    public YoutubeDialogFragment(YoutubeCallback mYoutubeCallback, String videoId) {
        this.mYoutubeCallback = mYoutubeCallback;
        this.videoId = videoId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.MyCustomTheme);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mYoutubeCallback != null) {
            mYoutubeCallback.onResume();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mYoutubeCallback != null) {
            mYoutubeCallback.onPause();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.youtube_popup, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        youtube_view = view.findViewById(R.id.youtube_view);
        view.findViewById(R.id.close).setOnClickListener(v -> dismiss());
        youtube_view.addYouTubePlayerListener(youtubeListener = new AbstractYouTubePlayerListener() {

            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                if (!TextUtils.isEmpty(videoId)) {
                    if(youTubePlayer != null) {
                        youTubePlayer.cueVideo(videoId, 0);
                        youTubePlayer.unMute();
                        youTubePlayer.play();
                    }
                }
            }
        });

    }
}