package com.ziro.bullet.Helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AuthorActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.ChannelPostActivity;
import com.ziro.bullet.activities.ProfileActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.caption.CaptionDetails;
import com.ziro.bullet.data.caption.Duration;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.interfaces.CaptionClickListener;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.DynamicTextView;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.ProgressTracker;
import com.ziro.bullet.utills.ViewIdGenerator;

import java.util.ArrayList;
import java.util.Iterator;

public class PopUpClass implements ProgressTracker.PositionListener {

    private PlayerView playerView;
    private RotateLayout rotateLayout;
    private RelativeLayout dynamicContainer;

    private PopupWindow popupWindow;
    private Activity activity;

    private ImageView smallScreen;
    private ImageView speaker;
    private ImageView caption;

    private PrefConfig config;
    private PopupWindow.OnDismissListener onDismissListener;
    private ProgressTracker tracker;
    private SimpleExoPlayer player;
    private ReelsItem reelsItem;
    private ArrayList<DynamicTextView> viewArray = new ArrayList<>();

    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void showPopupWindow(final View view, PlayerView playerViewOld, Activity activity, int mOrientation) {
        this.activity = activity;
        config = new PrefConfig(activity);

        Player player = playerViewOld.getPlayer();

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_video_layout, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        popupWindow = new PopupWindow(popupView, width, height, true);
        playerView = popupView.findViewById(R.id.ep_video_view);
        rotateLayout = popupView.findViewById(R.id.rotateLayout);
//        smallScreen = popupView.findViewById(R.id.smallScreen);
//        speaker = popupView.findViewById(R.id.speaker);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                disableFullScreen();
                PlayerView.switchTargetView(player, playerView, playerViewOld);
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }
        });

//        smallScreen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//            }
//        });


//        player.addListener(new Player.EventListener() {
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                Log.e("@@@@@", "" + playbackState);
//                if (playbackState == ExoPlayer.STATE_ENDED) {
//                    popupWindow.dismiss();
//                }
//            }
//        });

//        speaker.setOnClickListener(v -> {
//            try {
//                if (!Constants.muted) {
//                    Constants.muted = true;
////                    Constants.ReelMute = true;
//                    player.getAudioComponent().setVolume(0f);
//                    AnalyticsEvents.INSTANCE.logEvent(activity,
//                            Events.MUTE_REELS);
//                } else {
//                    Constants.muted = false;
////                    Constants.ReelMute = false;
//                    player.getAudioComponent().setVolume(1f);
//                    AnalyticsEvents.INSTANCE.logEvent(activity,
//                            Events.UNMUTE);
//                }
//                updateMuteButtons();
//            } catch (Exception ignore) {
//            }
//        });


//        player.setPlayWhenReady(true);
        PlayerView.switchTargetView(player, playerViewOld, playerView);

        updateMuteButtons();
        enableFullScreen();

        if (mOrientation == Surface.ROTATION_270 || mOrientation == Surface.ROTATION_0) {
            rotateLayout.setAngle(270);
        } else {
            rotateLayout.setAngle(90);
        }


//        SimpleOrientationListener mOrientationListener = new SimpleOrientationListener(view.getContext()) {
//
//            @Override
//            public void onSimpleOrientationChanged(int orientation) {
//                if(orientation == Configuration.ORIENTATION_LANDSCAPE){
//                    rotateLayout.setAngle(270);
//                }else if(orientation == Configuration.ORIENTATION_PORTRAIT){
//                    rotateLayout.setAngle(0);
//                }
//            }
//        };
//        mOrientationListener.enable();
    }

    public void showPopupWindowReels(final View view, ReelsItem reelsItem, Activity activity, long playingPos, OnPopupDismissListener onPopupDismissListener, int mOrientation) {
        this.activity = activity;
        this.reelsItem = reelsItem;
        config = new PrefConfig(activity);

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_video_layout_caption, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        popupWindow = new PopupWindow(popupView, width, height, true);
//        caption = popupView.findViewById(R.id.caption);
        dynamicContainer = popupView.findViewById(R.id.dynamicContainer);
        playerView = popupView.findViewById(R.id.ep_video_view);
        rotateLayout = popupView.findViewById(R.id.rotateLayout);
//        smallScreen = popupView.findViewById(R.id.smallScreen);
//        speaker = popupView.findViewById(R.id.speaker);

//        if (reelsItem.getCaptions() != null && reelsItem.getCaptions().size() > 0) {
//            caption.setVisibility(View.VISIBLE);
//        } else {
//            caption.setVisibility(View.GONE);
//        }

//        if (config.isReelsCaption()) {
//            caption.setImageResource(R.drawable.ic_caption_on);
//        } else {
//            caption.setImageResource(R.drawable.ic_caption_off);
//        }

//        if (reelsItem != null && !TextUtils.isEmpty(reelsItem.getMediaLandscape())) {
//
//            if (reelsItem.getMediaLandscape().endsWith(".m3u8")) {
//                //for HLS
//                AdaptiveTrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory();
//                player = ExoPlayerFactory.newSimpleInstance(activity, new DefaultTrackSelector(adaptiveTrackSelection));
//                DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
//                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(activity,
//                        Util.getUserAgent(activity, "nib"), defaultBandwidthMeter);
//
//                Uri uri = Uri.parse(reelsItem.getMediaLandscape());
//                HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
////                        .setAllowChunklessPreparation(true)
//                        .createMediaSource(uri);
//                player.prepare(mediaSource);
//            } else {
//
//                //for mp4
//                DefaultTrackSelector trackSelector = new DefaultTrackSelector();
//                trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());
//                player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);
//
//                ProgressiveMediaSource sample = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(activity, "nib")).createMediaSource(Uri.parse(reelsItem.getMediaLandscape()));
//                player.prepare(sample);
//            }
//
//            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//
//            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//                    long playingPos = 0;
//                    if (player != null) {
//                        playingPos = player.getCurrentPosition();
//                        player.setPlayWhenReady(false);
//                        player.release();
//                    }
//                    if (tracker != null) {
//                        tracker.purgeHandler();
//                        tracker = null;
//                    }
//                    disableFullScreen();
//                    onPopupDismissListener.onDismiss(playingPos);
//                }
//            });
//
//            smallScreen.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    popupWindow.dismiss();
//                }
//            });
//
//            player.addListener(new Player.EventListener() {
//                @Override
//                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                    if (playbackState == ExoPlayer.STATE_ENDED) {
//                        popupWindow.dismiss();
//                    }
//                    if (playbackState == ExoPlayer.STATE_READY && isPlaying()) {
//                        // start/resume timer
//                        trackVideoDuration();
//                    } else {
//                        // pause timer
//                        if (tracker != null) {
//                            tracker.purgeHandler();
//                            tracker = null;
//                        }
//                    }
//                }
//            });
//
//            caption.setOnClickListener(v -> {
//                if (!config.isReelsCaption()) {
//                    config.setReelsCaption(true);
//                    caption.setImageResource(R.drawable.ic_caption_on);
//                    if (player != null) {
//                        getCaption(player.getCurrentPosition());
//                    }
//                } else {
//                    config.setReelsCaption(false);
//                    caption.setImageResource(R.drawable.ic_caption_off);
//                    removeCaptions();
//                }
//            });
//
//            speaker.setOnClickListener(v -> {
//                try {
//                    if (!Constants.ReelMute) {
////                    Constants.muted = true;
//                        Constants.ReelMute = true;
//                        player.getAudioComponent().setVolume(0f);
//                        AnalyticsEvents.INSTANCE.logEvent(activity,
//                                Events.MUTE_REELS);
//                    } else {
////                    Constants.muted = false;
//                        Constants.ReelMute = false;
//                        player.getAudioComponent().setVolume(1f);
//                        AnalyticsEvents.INSTANCE.logEvent(activity,
//                                Events.UNMUTE);
//                    }
//                    updateReelsMuteButtons();
//                } catch (Exception ignore) {
//                }
//            });
//
//            playerView.setPlayer(player);
//            player.seekTo(playingPos);
//            player.setPlayWhenReady(config.isReelsAutoPlay() || playingPos != 0);
//
//            if (player.getAudioComponent() != null) {
//                if (Constants.ReelMute) {
//                    player.getAudioComponent().setVolume(0f);
//                } else {
//                    player.getAudioComponent().setVolume(1f);
//                }
//            }
//
//            updateReelsMuteButtons();
//            enableFullScreen();
//
//            if (mOrientation == Surface.ROTATION_270 || mOrientation == Surface.ROTATION_0) {
//                rotateLayout.setAngle(270);
//            } else {
//                rotateLayout.setAngle(90);
//            }
//
//
////        SimpleOrientationListener mOrientationListener = new SimpleOrientationListener(view.getContext()) {
////
////            @Override
////            public void onSimpleOrientationChanged(int orientation) {
////                if(orientation == Configuration.ORIENTATION_LANDSCAPE){
////                    rotateLayout.setAngle(270);
////                }else if(orientation == Configuration.ORIENTATION_PORTRAIT){
////                    rotateLayout.setAngle(0);
////                }
////            }
////        };
////        mOrientationListener.enable();
//        }
    }

    public boolean isPlaying() {
        if (player == null) return false;
        return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
    }

    private void trackVideoDuration() {
        if (tracker == null)
            tracker = new ProgressTracker(player, this);
    }

    public void updateMuteButtons() {
        if (!Constants.muted) {
            speaker.setImageResource(R.drawable.ic_sound_max);
        } else {
            speaker.setImageResource(R.drawable.ic_sound_min);
        }
    }

    public void updateReelsMuteButtons() {
        if (!Constants.ReelMute) {
            speaker.setImageResource(R.drawable.ic_sound_max);
        } else {
            speaker.setImageResource(R.drawable.ic_sound_min);
        }
    }

    private void enableFullScreen() {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        activity.findViewById(android.R.id.content).requestLayout();
    }

    private void disableFullScreen() {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.findViewById(android.R.id.content).requestLayout();
    }

    public boolean isShowing() {
        if (popupWindow != null) {
            return popupWindow.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void progress(long position) {
        getCaption(position);
    }

    private void getCaption(long progress) {
        if (reelsItem != null && reelsItem.getCaptions() != null && reelsItem.getCaptions().size() > 0) {
            caption.setVisibility(View.VISIBLE);

            try {
                int forcedCount = 0;
                for (int i = 0; i < reelsItem.getCaptions().size(); i++) {
                    CaptionDetails details = reelsItem.getCaptions().get(i);
                    details.setId(i);
                    if (details.isForced()) {
                        forcedCount++;
                    }
                    Duration duration = details.getDuration();
                    if (progress >= duration.getStart() && progress <= duration.getEnd()) {
                        if (!exist(details.getId()) && details.isLandscape()) {
                            if (details.isForced()) {
                                TextView(i, details, duration);
                            } else {
                                if (config.isReelsCaption()) {
                                    TextView(i, details, duration);
                                }
                            }
                        }
                    }
                }

                if (reelsItem.getCaptions().size() == forcedCount) {
                    caption.setVisibility(View.GONE);
                }

                for (DynamicTextView view : viewArray) {
                    if (view.getEndTime() < progress) {
                        viewArray.remove(view);
                        dynamicContainer.removeView(view.getView());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            caption.setVisibility(View.GONE);
        }
    }

    private void removeCaptions() {
        if (viewArray != null && viewArray.size() > 0 && dynamicContainer != null) {
            Iterator i = viewArray.iterator();
            DynamicTextView view = null;
            while (i.hasNext()) {
                view = (DynamicTextView) i.next();
                if (!view.isForced()) {
                    dynamicContainer.removeView(view.getView());
                    i.remove();
                }
            }
        }
    }

    private void TextView(int i, CaptionDetails details, Duration duration) {
        int viewId = ViewIdGenerator.generateViewId();
        DynamicTextView object = new DynamicTextView();
        View view2 = object.createTextView(dynamicContainer, activity, details, new CaptionClickListener() {
            @Override
            public void onItemClick(String action, String id) {
                if (activity != null) {
                    if (!InternetCheckHelper.isConnected()) {
                        Toast.makeText(activity, activity.getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!TextUtils.isEmpty(action) && !TextUtils.isEmpty(id)) {
                        Intent intent = null;
                        switch (action) {
                            case "source":
                                intent = new Intent(activity, ChannelDetailsActivity.class);
                                if (details.getAction_data() != null && details.getAction_data().getSource() != null) {
                                    intent.putExtra("id", details.getAction_data().getSource().getId());
                                }
                                break;
                            case "topic":
                                intent = new Intent(activity, ChannelPostActivity.class);
                                if (details.getAction_data() != null && details.getAction_data().getTopics() != null) {
                                    intent.putExtra("id", details.getAction_data().getTopics().getId());
                                    intent.putExtra("name", details.getAction_data().getTopics().getName());
                                    intent.putExtra("favorite", details.getAction_data().getTopics().isFavorite());
                                    intent.putExtra("type", TYPE.TOPIC);
                                }
                                break;
                            case "author":
                                User user = new PrefConfig(activity).isUserObject();
                                if (user != null && !TextUtils.isEmpty(user.getId()) && user.getId().equalsIgnoreCase(id)) {
                                    intent = new Intent(activity, ProfileActivity.class);
                                } else {
                                    intent = new Intent(activity, AuthorActivity.class);
                                }
                                if (details.getAction_data() != null && details.getAction_data().getAuthor() != null) {
                                    intent.putExtra("authorID", details.getAction_data().getAuthor().getId());
                                    intent.putExtra("authorContext", details.getAction_data().getAuthor().getContext());
                                }
                                break;
                        }
                        if (intent == null) return;
                        activity.startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST);
                    }
                }
            }
        });
        object.setEndTime(duration.getEnd());
        object.setView(view2);
        object.setViewId(viewId);
        object.setCaptionId(details.getId());
        object.setForced(details.isForced());
        view2.setId(viewId);
        view2.setTag(i);
        dynamicContainer.addView(view2);
        details.setShown(true);
        viewArray.add(object);
    }

    private boolean exist(int captionId) {
        for (int i = 0; i < viewArray.size(); i++) {
            if (viewArray.get(i).getCaptionId() == captionId) {
                return true;
            }
        }
        return false;
    }

    public interface OnPopupDismissListener {
        void onDismiss(long pos);
    }
}
