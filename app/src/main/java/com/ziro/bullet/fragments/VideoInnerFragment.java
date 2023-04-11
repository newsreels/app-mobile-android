package com.ziro.bullet.fragments;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_READY;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.Helper.PopUpClass;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AuthorActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.ChannelPostActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.ProfileActivity;
import com.ziro.bullet.activities.ReelActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.bottomSheet.ForYouReelSheet;
import com.ziro.bullet.bottomSheet.MediaShare;
import com.ziro.bullet.bottomSheet.ReelViewMoreSheet;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.caption.CaptionDetails;
import com.ziro.bullet.data.caption.CaptionResponse;
import com.ziro.bullet.data.caption.Duration;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.interfaces.CaptionApiCallback;
import com.ziro.bullet.interfaces.LikeInterface;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.VideoInterface;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.model.articles.MediaMeta;
import com.ziro.bullet.presenter.CaptionPresenter;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.presenter.SharePresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.DoubleClickHandler.DoubleClick;
import com.ziro.bullet.utills.DoubleClickHandler.DoubleClickListener;
import com.ziro.bullet.utills.DynamicTextView;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.ProgressTracker;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.utills.ViewIdGenerator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class VideoInnerFragment extends Fragment implements CaptionApiCallback, ShareInterface, Player.EventListener, ProgressTracker.PositionListener {
    private static final int MIN_LINES = 2;
    private static final String TWO_SPACES = "  ";
    private static final String TAG = "VideoInnerFragment";
    private static final String ARG_REELS_ITEM = "reels_item";
    private static final String ARG_REELS_POSITION = "reels_position";
    private static final String ARG_REELS_MODE = "reels_mode";
    private static final String ARG_REELS_CHECKREELTAB = "reels_checkreeltab";
    private static final String ARG_REELS_isReelActivity = "reels_isReelActivity";
    private static final String ARG_REELS_DISABLESWIPINGRIGHT = "reels_disableswipingright";
    Runnable volumeRunnable;
    AudioManager audioManager;
    int minVolume;
    int curVolume;
    int updatedVolume;
    int maxVolume;
    private ReelViewMoreSheet reelViewMore;
    private ForYouReelSheet forYouReelSheet;
    private int MAX_LINES = MIN_LINES;
    private View view = null;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private ReelsItem reelsItem = null;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private DefaultTrackSelector trackSelector;
    private LinearLayout userFollowBtn;
    private LinearLayout llRight;
    private ImageView userFollowIcon;
    private ImageView icon;
    private TextView source_name;
    private TextView author_name;
    private TextView description;
    private TextView description_fastreel;
    private TextView dummy_text;
    private Handler volumeHandler;
    //    private TextView speaker_text;
    private ConstraintLayout layoutView;
    private ImageView speaker;
    //    private LinearLayout soundMain;
    private ImageView like_icon;
    private TextView like_text;
    private ImageView comment_icon;
    private ImageView share_icon;
    private ImageView dots;
    private TextView comment_text;
    private RelativeLayout gradient;
    private RelativeLayout edit;
    private ImageButton playPauseBtn;
    private VideoInterface listener;
    private ImageView fullScreen;
    private RelativeLayout play_image;
    private int position = 0;
    private ProgressiveMediaSource sample;
    private LikePresenter presenter;
    private SharePresenter sharePresenter;
    private String mode;
    private ShareBottomSheet shareBottomSheet;
    private boolean isVideoReady = false;
    private PrefConfig prefConfig;
    private NewsPresenter newsPresenter;
    private ImageView heart;
    private ImageView iv_add_to_archive;
    private ImageView thumbnail;
    private ProgressBar progress_bar_video;
    private LinearLayout linearLayout5;
    private RelativeLayout extraSpace;
    private SeekBar volumeseekbar;
    private TextView text2;
    private TextView clickLayout;
    private ConstraintLayout playerlayout;
    private ConstraintLayout constraintL2;
    private DefaultTimeBar exo_progress;
    private TextView debuglogs;
    private CardView debugcardview;
    private ProgressBar progress;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private AnimatedVectorDrawableCompat avd;
    private AnimatedVectorDrawable avd2;
    private boolean checkReelTab = false;
    private boolean isReelActivity = false;
    private MediaShare reelsBottomSheet;
    private DefaultBandwidthMeter defaultBandwidthMeter;
    private DataSource.Factory dataSourceFactory;
    private HlsMediaSource mediaSource;
    private GestureDetectorCompat gestureDetectorCompat;
    private ReelViewMoreSheet blankFragment;
    private boolean disableSwipingRight;
    private PictureLoadingDialog loadingDialog;
    private boolean mMenuVisible;
    private boolean mFragmentHidden;
    private PopUpClass popUpClass;
    private boolean fragmentResumed;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private ProgressTracker tracker;
    private RelativeLayout dynamicContainer;
    private ConstraintLayout containerParent;
    private ImageView caption;
    private Context mContext;
    private CaptionPresenter captionPresenter;
    private Handler controllerHandler = new Handler(Looper.getMainLooper());
    private Runnable controllerRunnable = new Runnable() {
        @Override
        public void run() {
            constraintL2.setVisibility(View.INVISIBLE);
        }
    };
    private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            blankFragment.dismiss();
            onResume();
        }
    };
    private ArrayList<DynamicTextView> viewArray = new ArrayList<>();

    public VideoInnerFragment() {
    }

    public static VideoInnerFragment getInstance(ReelsItem reelsItem, int position, String mode, boolean checkReelTab, boolean disableSwipingRight, boolean isReelActivity) {
        VideoInnerFragment fragment = new VideoInnerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_REELS_ITEM, reelsItem);
        bundle.putInt(ARG_REELS_POSITION, position);
        bundle.putString(ARG_REELS_MODE, mode);
        bundle.putBoolean(ARG_REELS_CHECKREELTAB, checkReelTab);
        bundle.putBoolean(ARG_REELS_isReelActivity, isReelActivity);
        bundle.putBoolean(ARG_REELS_DISABLESWIPINGRIGHT, disableSwipingRight);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void trackVideoDuration() {
        if (tracker == null) tracker = new ProgressTracker(player, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void progress(long position) {
//        Log.i(TAG, "VideoViewActivity/progress: position=" + position);
        getCaption(position);
    }

    private void getCaption(long progress) {
        if (reelsItem != null && reelsItem.getCaptions() != null && reelsItem.getCaptions().size() > 0) {
//            caption.setVisibility(View.VISIBLE);

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
                        if (!exist(details.getId())) {
                            if (details.isForced()) {
                                TextView(i, details, duration);
                            } else {
                                if (prefConfig.isReelsCaption()) {
                                    TextView(i, details, duration);
                                }
                            }
                        }
                    }
                }

                if (reelsItem.getCaptions().size() == forcedCount) {
                    caption.setVisibility(View.GONE);
                }

//                for (DynamicTextView view : viewArray) {
//                    if (view.getEndTime() < progress) {
//                        viewArray.remove(view);
//                        dynamicContainer.removeView(view.getView());
//                    }
//                }
                checkCaptions(progress);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            caption.setVisibility(View.GONE);
        }
    }

    private void checkCaptions(long progress) {
        if (viewArray != null && viewArray.size() > 0 && dynamicContainer != null) {
            Iterator i = viewArray.iterator();
            DynamicTextView view = null;
            while (i.hasNext()) {
                view = (DynamicTextView) i.next();
                if (view.getEndTime() < progress) {
                    viewArray.remove(view);
                    dynamicContainer.removeView(view.getView());
                    i.remove();
                }
            }
        }
    }

    private void removeCaptions(boolean checkForce) {
        if (viewArray != null && viewArray.size() > 0 && dynamicContainer != null) {
            Iterator i = viewArray.iterator();
            DynamicTextView view = null;
            if (checkForce) {
                while (i.hasNext()) {
                    view = (DynamicTextView) i.next();
                    if (!view.isForced()) {
                        dynamicContainer.removeView(view.getView());
                        i.remove();
                    }
                }
            } else {
                while (i.hasNext()) {
                    view = (DynamicTextView) i.next();
                    dynamicContainer.removeView(view.getView());
                    i.remove();
                }
            }

        }
    }

    private void TextView(int i, CaptionDetails details, Duration duration) {
        int viewId = ViewIdGenerator.generateViewId();
        DynamicTextView object = new DynamicTextView();
        View view2 = object.createTextView(dynamicContainer, getContext(), details, (action, id) -> {

            if (!InternetCheckHelper.isConnected()) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            if (!TextUtils.isEmpty(action) && !TextUtils.isEmpty(id)) {
                Intent intent = null;
                switch (action) {
                    case "source":
                        intent = new Intent(getContext(), ChannelDetailsActivity.class);
                        if (details.getAction_data() != null && details.getAction_data().getSource() != null) {
                            intent.putExtra("id", details.getAction_data().getSource().getId());
                        }
                        break;
                    case "topic":
                        intent = new Intent(getContext(), ChannelPostActivity.class);
                        if (details.getAction_data() != null && details.getAction_data().getTopics() != null) {
                            intent.putExtra("id", details.getAction_data().getTopics().getId());
                            intent.putExtra("name", details.getAction_data().getTopics().getName());
                            intent.putExtra("favorite", details.getAction_data().getTopics().isFavorite());
                            intent.putExtra("type", TYPE.TOPIC);
                        }
                        break;
                    case "author":
                        User user = new PrefConfig(getContext()).isUserObject();
                        if (user != null && !TextUtils.isEmpty(user.getId()) && user.getId().equalsIgnoreCase(id)) {
                            intent = new Intent(getContext(), ProfileActivity.class);
                        } else {
                            intent = new Intent(getContext(), AuthorActivity.class);
                        }
                        if (details.getAction_data() != null && details.getAction_data().getAuthor() != null) {
                            intent.putExtra("authorID", details.getAction_data().getAuthor().getId());
                            intent.putExtra("authorContext", details.getAction_data().getAuthor().getContext());
                        }
                        break;
                }
                if (intent == null) return;
                startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST);
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

    public void setVideoCallback(VideoInterface listener) {
        this.listener = listener;
    }

    public void setOrientation(int mOrientation) {
        if ((mOrientation == Surface.ROTATION_90 || mOrientation == Surface.ROTATION_270) && getContext() != null && android.provider.Settings.System.getInt(getContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            if (reelsItem != null && !TextUtils.isEmpty(reelsItem.getMediaLandscape())) {
                fullScreen(mOrientation);
            }
        } else if (mOrientation == Surface.ROTATION_0 || mOrientation == Surface.ROTATION_180) {
            if (popUpClass != null && popUpClass.isShowing()) {
                popUpClass.dismiss();
            }
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == STATE_ENDED) {
            if (listener != null && isVideoReady) {
//                player.setPlayWhenReady(true);
                progress_bar_video.setVisibility(View.GONE);
                thumbnail.setVisibility(View.VISIBLE);
                player.seekTo(0);
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID, reelsItem.getId());
                AnalyticsEvents.INSTANCE.logEventWithAPI(getContext(), params, Events.REEL_COMPLETE);
                listener.nextVideo(position);
            }
            isVideoReady = false;
        }

        if (playbackState == STATE_READY) {
            playerView.setVisibility(View.VISIBLE);
            progress_bar_video.setVisibility(View.GONE);
            thumbnail.setVisibility(View.INVISIBLE);
            AnalyticsEvents.INSTANCE.reelViewEvent(requireContext(), reelsItem.getId());
            isVideoReady = true;
        }

        if (playbackState == STATE_BUFFERING) {
            progress_bar_video.setVisibility(View.VISIBLE);
            isVideoReady = false;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            reelsItem = arguments.getParcelable(ARG_REELS_ITEM);
            position = arguments.getInt(ARG_REELS_POSITION);
            mode = arguments.getString(ARG_REELS_MODE);
            checkReelTab = arguments.getBoolean(ARG_REELS_CHECKREELTAB);
            isReelActivity = arguments.getBoolean(ARG_REELS_isReelActivity);
            disableSwipingRight = arguments.getBoolean(ARG_REELS_DISABLESWIPINGRIGHT);
        }
//        if (reelsItem != null && reelsItem.getCaptions() == null) {
//            captionPresenter = new CaptionPresenter(getActivity(), this);
//            captionPresenter.getCaptions(reelsItem.getId());
//        }
        volumeHandler = new Handler();
        volumeRunnable = new Runnable() {
            public void run() {
                volumeseekbar.setVisibility(View.GONE);
            }
        };

//        nHandler = new Handler();
//        r2 = new Runnable() {
//            public void run() {
//                exo_progress.setVisibility(View.GONE);
//                playPauseBtn.setVisibility(View.GONE);
//                nHandler.postDelayed(this, 5000);
//            }
//        };


        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);


    }

    public void onMyKeyDown(int key, KeyEvent event) {
        volumeseekbar.setVisibility(View.VISIBLE);
        int index = volumeseekbar.getProgress();
        volumeseekbar.setProgress(index - 1);
        if (volumeHandler != null) {
            volumeHandler.removeCallbacks(volumeRunnable);
            volumeHandler.postDelayed(volumeRunnable, 2000);
        }
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updatedVolume = curVolume;
        Constants.updatedvol = curVolume;
    }

    public void onMyKeyUp(int key, KeyEvent event) {
        volumeseekbar.setVisibility(View.VISIBLE);
        if (volumeHandler != null) {
            volumeHandler.removeCallbacks(volumeRunnable);
            volumeHandler.postDelayed(volumeRunnable, 2000);
        }

        if (Constants.ReelMute) {
            Constants.ReelMute = false;
            prefConfig.setMute(false);
        }
        if (curVolume == minVolume) {
            volumeseekbar.setProgress(curVolume);
        }
        if (curVolume < maxVolume) {
            int index = volumeseekbar.getProgress();
            volumeseekbar.setProgress(index + 1);
            if (player != null) {
                player.setVolume(1);
            }
        }

        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updatedVolume = curVolume;
        Constants.updatedvol = curVolume;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.item_videos_container, container, false);
        prefConfig = new PrefConfig(getActivity());
        presenter = new LikePresenter(getActivity());
        newsPresenter = new NewsPresenter(getActivity(), null);

        followUnfollowPresenter = new FollowUnfollowPresenter(getActivity());
        init(view);
        setData();
        gestureDetectorCompat = new GestureDetectorCompat(getContext(), new SwipeGestureListener(reelsItem, disableSwipingRight));
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.TYPE_MUTE_REELS_SCROLLING) {
            if (player != null) {
                if (!Constants.ReelMute) {
                    player.setVolume(event.getIntData());
                }
            }
        }
    }

    private void showVerifiedIcon(TextView view, ReelsItem reelsItem) {
        if (reelsItem.isVerified()) {
            if (Utils.isRTL()) {
                view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified, 0, 0, 0);
            } else {
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData() {
        if (BuildConfig.DEBUG) {
            debugcardview.setVisibility(View.VISIBLE);
            debuglogs.setText(!TextUtils.isEmpty(reelsItem.getDebug()) ? reelsItem.getDebug() : "");
        } else {
            debugcardview.setVisibility(View.GONE);
        }
//        if (Constants.ReelMute) {
//            speaker.setImageResource(R.drawable.ic_sound_min);
//        } else {
//            speaker.setImageResource(R.drawable.ic_sound_max);
//        }
        Log.e("mutetest1", "setDa+ a: " + prefConfig.isMute());
        if (prefConfig.isMute()) {
            speaker.setImageResource(R.drawable.ic_sound_min);
            Constants.ReelMute = true;
            if (player != null) {
                player.setVolume(0);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//            curVolume = audioManager.setStreamVolume(player.getAudioStreamType(),0,0);
            }
        } else {
            Constants.ReelMute = false;
            speaker.setImageResource(R.drawable.ic_sound_max);
            if (player != null)
                player.setVolume(1);
        }

        if (isReelActivity) {
            // status bar height
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(containerParent);
                constraintSet.setMargin(R.id.dynamicContainer, ConstraintSet.TOP, statusBarHeight);
                constraintSet.applyTo(containerParent);
            }
            extraSpace.setVisibility(View.VISIBLE);
        } else {
            extraSpace.setVisibility(View.GONE);
        }

        shareBottomSheetPresenter = new ShareBottomSheetPresenter(getActivity());
        sharePresenter = new SharePresenter(getActivity(), this, null);
        if (reelsItem != null) {

            final Drawable drawable = heart.getDrawable();
            if (!TextUtils.isEmpty(mode)) {
                if (mode.equalsIgnoreCase("profile")) {
                    edit.setVisibility(View.VISIBLE);
                    edit.setOnClickListener(v -> {
                        showBottomSheetDialog(getArticleFromReels(reelsItem), dialog -> {
//                            getActivity().finish();
                        });
                    });
                } else {
                    edit.setVisibility(View.GONE);
                }
            } else {
                edit.setVisibility(View.GONE);
            }
//            if (!TextUtils.isEmpty(reelsItem.getMediaLandscape())) {
//                fullScreen.setVisibility(View.VISIBLE);
//            } else {
//                fullScreen.setVisibility(View.GONE);
//            }

            if (!reelsItem.getAuthorNameToDisplay().equals("")) {
                source_name.setText(reelsItem.getAuthorNameToDisplay());
            } else {
                source_name.setText(reelsItem.getSourceNameToDisplay());
            }

            if (reelsItem.getDescription() != null) {
                description.setText(reelsItem.getDescription());
                description_fastreel.setText(reelsItem.getDescription());
            }

            if (reelsItem.getMediaMeta().getType() != null && view != null) {
                if (reelsItem.getMediaMeta().getType() != null) {
                    if (reelsItem.getMediaMeta().getType().equals("ai")) {
                        description.setVisibility(View.GONE);
                        description_fastreel.setVisibility(View.GONE);
                    } else if (reelsItem.getMediaMeta().getType().equals("reel")) {
                        description.setVisibility(View.VISIBLE);
                        description_fastreel.setVisibility(View.GONE);
                    } else if (reelsItem.getMediaMeta().getType().equals("fastreel")) {
                        description.setVisibility(View.GONE);
                        description_fastreel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (reelsItem.isNative_title()) {
                        description.setVisibility(View.VISIBLE);
                        description_fastreel.setVisibility(View.GONE);
                    } else {
                        description.setVisibility(View.GONE);
                        description_fastreel.setVisibility(View.GONE);
                    }
                }
            }

            if (reelsItem.getInfo() != null) {
                comment_text.setText("" + reelsItem.getInfo().getComment_count());
                like_text.setText("" + reelsItem.getInfo().getLike_count());
                comment_text.setVisibility(reelsItem.getInfo().getComment_count() > 0 ? View.VISIBLE : View.VISIBLE);
                like_text.setVisibility(reelsItem.getInfo().getLike_count() > 0 ? View.VISIBLE : View.VISIBLE);
                if (reelsItem.getInfo().isLiked()) {
                    DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(requireContext(), R.color.theme_color_1));
                } else {
                    DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(requireContext(), R.color.white));
                }
                like_icon.setOnClickListener(v -> {
                    if (!InternetCheckHelper.isConnected()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    like_icon.setEnabled(false);
                    boolean flag = reelsItem.getInfo().isLiked();
                    if (!flag) {
                        DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(requireContext(), R.color.theme_color_1));
//                        like_text.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                        like_icon.setImageResource(R.drawable.ic_reel_like_active);

                        like_text.setText("" + (reelsItem.getInfo().getLike_count() + 1));
                        like_text.setVisibility(reelsItem.getInfo().getLike_count() + 1 > 0 ? View.VISIBLE : View.VISIBLE);
                    } else {
                        DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(requireContext(), R.color.white));
//                        like_text.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                        like_icon.setImageResource(R.drawable.ic_reel_like_inactive);

                        like_text.setText("" + (reelsItem.getInfo().getLike_count() - 1));
                        like_text.setVisibility(reelsItem.getInfo().getLike_count() - 1 > 0 ? View.VISIBLE : View.VISIBLE);
                    }


                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID, reelsItem.getId());
                    AnalyticsEvents.INSTANCE.logEvent(getContext(), params, Events.REELS_LIKE);
                    presenter.like(reelsItem.getId(), new LikeInterface() {
                        @Override
                        public void success(boolean like) {
                            if (getContext() == null) return;
                            like_icon.setEnabled(true);
                            reelsItem.getInfo().setLiked(like);
                            int counter = reelsItem.getInfo().getLike_count();
                            if (like) {
                                counter++;
                            } else {
                                if (counter > 0) {
                                    counter--;
                                } else {
                                    counter = 0;
                                }
                            }

                            reelsItem.getInfo().setLike_count(counter);
                            like_text.setText("" + counter);

                            if (reelsItem.getInfo().isLiked()) {
                                DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(requireContext(), R.color.theme_color_1));
//                                like_text.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                                like_icon.setImageResource(R.drawable.ic_reel_like_active);
                            } else {
                                DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(requireContext(), R.color.white));
//                                like_text.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                                like_icon.setImageResource(R.drawable.ic_reel_like_inactive);
                            }
                        }

                        @Override
                        public void failure() {
                            like_icon.setEnabled(true);
                        }
                    }, !reelsItem.getInfo().isLiked());
                });
            }

            if (!TextUtils.isEmpty(reelsItem.getImage())) {
                thumbnail.setVisibility(View.VISIBLE);
                Picasso.get().load(reelsItem.getImage()).into(thumbnail);
//                Glide.with(this).load(reelsItem.getImage()).into(thumbnail);
            } else {
                thumbnail.setVisibility(View.INVISIBLE);
            }

            if (reelsItem.getSourceImageToDisplay() != null && !reelsItem.getSourceImageToDisplay().equals("")) {
                Picasso.get().load(reelsItem.getSourceImageToDisplay()).placeholder(R.drawable.img_place_holder).transform(new CropCircleTransformation()).into(icon);
            } else {
                Picasso.get().load(R.drawable.img_place_holder).transform(new CropCircleTransformation()).into(icon);
            }


            if (reelsItem.getSource() != null) {
                if (reelsItem.getSource().isFavorite()) {
                    userFollowBtn.setEnabled(false);
                    userFollowIcon.setVisibility(View.INVISIBLE);
                } else {
                    userFollowBtn.setEnabled(true);
                    userFollowIcon.setVisibility(View.VISIBLE);
                }
            } else {

                User user = new PrefConfig(getContext()).isUserObject();
                if (user != null && !TextUtils.isEmpty(user.getId()) && user.getId().equalsIgnoreCase(reelsItem.getAuthor().get(0).getId())) {
                    userFollowBtn.setEnabled(false);
                    userFollowIcon.setVisibility(View.INVISIBLE);
                } else if (reelsItem.getAuthor().get(0).isFollow()) {
                    userFollowBtn.setEnabled(false);
                    userFollowIcon.setVisibility(View.INVISIBLE);
                } else {
                    userFollowBtn.setEnabled(true);
                    userFollowIcon.setVisibility(View.VISIBLE);
                }
            }

            comment_icon.setOnClickListener(v -> {
                if (!InternetCheckHelper.isConnected()) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (getActivity() != null) {
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID, reelsItem.getId());
                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
                            params,
                            Events.REELS_COMMENT);
                    Intent intent = new Intent(getActivity(), CommentsActivity.class);
                    intent.putExtra("article_id", reelsItem.getId());
                    intent.putExtra("position", position);
                    startActivityForResult(intent, Constants.CommentsRequestCode);
                }
            });

            speaker.setOnClickListener(v -> {
                if (!InternetCheckHelper.isConnected()) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (player != null) {
                    if (!Constants.ReelMute) {
                        Constants.ReelMute = true;
                        prefConfig.setMute(true);
                        Log.e("mutetest", "setDa+ a: " + prefConfig.isMute());
//                        speaker.getDrawable();
                        speaker.setImageResource(R.drawable.ic_sound_min);
                        player.setVolume(0);
                    } else {
                        //if the volume is 0 then set to default 8 else just unmute
                        if (curVolume == minVolume) {
                            curVolume = 6;
                            updatedVolume = curVolume;
                            Constants.updatedvol = curVolume;
                            //added1
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, updatedVolume, 0);
                            volumeseekbar.setProgress(curVolume);
                        }
                        speaker.setImageResource(R.drawable.ic_sound_max);
                        Constants.ReelMute = false;
                        prefConfig.setMute(false);
                        player.setVolume(1);

                    }
                }
            });

            share_icon.setOnClickListener(v -> {
                share();
            });
            dots.setOnClickListener(v -> {
                if (shareBottomSheetPresenter != null) {
                    shareBottomSheetPresenter.share_msg(getArticleFromReels(reelsItem).getId(), new ShareInfoInterface() {
                        @Override
                        public void response(ShareInfo shareInfo) {
                            onPause();
                            if (shareBottomSheet == null) {
                                shareBottomSheet = new ShareBottomSheet(getActivity(), new ShareToMainInterface() {
                                    @Override
                                    public void removeItem(String id, int position) {

                                    }

                                    @Override
                                    public void onItemClicked(TYPE type, String id, String name, boolean favorite) {

                                    }

                                    @Override
                                    public void unarchived() {
                                        // for updating caption
                                        updateCaption();
                                    }
                                }, true, "REEL_MAIN");
                            }
                            shareBottomSheet.show(getArticleFromReels(reelsItem), new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    onResume();
                                }
                            }, shareInfo);
                        }

                        @Override
                        public void error(String error) {

                        }
                    });
                }
            });

//            text2.setOnTouchListener((view, motionEvent) -> {
//                constraintL2.setVisibility(View.GONE);
//                return gestureDetectorCompat.onTouchEvent(motionEvent);
//                // return false;
//            });
//            text2.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    return false;
//                }
//            });

            clickLayout.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    if (!InternetCheckHelper.isConnected()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    constraintL2.setVisibility(View.GONE);
                    onPause();
                    showBottomSheet();
                }

                @Override
                public void onDoubleClick(View view) {
                    if (!InternetCheckHelper.isConnected()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    if (getContext() != null)
                        DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(getContext(), R.color.theme_color_1));
                    heart.setAlpha(1f);
                    if (drawable instanceof AnimatedVectorDrawableCompat) {
                        avd = (AnimatedVectorDrawableCompat) drawable;
                        avd.start();
                    } else if (drawable instanceof AnimatedVectorDrawable) {
                        avd2 = (AnimatedVectorDrawable) drawable;
                        avd2.start();
                    }

                    final int[] counter = {-1};
                    if (!reelsItem.getInfo().isLiked()) {

                        counter[0] = reelsItem.getInfo().getLike_count();
                        counter[0]++;
                        like_text.setText("" + counter[0]);
                        reelsItem.getInfo().setLiked(true);
                        like_text.setVisibility(reelsItem.getInfo().getLike_count() + 1 > 0 ? View.VISIBLE : View.VISIBLE);

                        presenter.like(reelsItem.getId(), new LikeInterface() {
                            @Override
                            public void success(boolean like) {
                                if (getActivity() == null || getActivity().isFinishing()) {
                                    return;
                                }
                                like_icon.setEnabled(true);
                                reelsItem.getInfo().setLiked(like);
                                reelsItem.getInfo().setLike_count(counter[0]);
                                like_text.setVisibility(reelsItem.getInfo().getLike_count() > 0 ? View.VISIBLE : View.VISIBLE);
                                if (reelsItem.getInfo().isLiked()) {
                                    DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(requireContext(), R.color.theme_color_1));
//                                    like_text.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                                    like_icon.setImageResource(R.drawable.ic_reel_like_active);
                                } else {
                                    DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(requireContext(), R.color.white));
//                                    like_text.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                                    like_icon.setImageResource(R.drawable.ic_reel_like_inactive);
                                }
                            }

                            @Override
                            public void failure() {
                                if (getActivity() == null || getActivity().isFinishing()) {
                                    return;
                                }
                                like_icon.setEnabled(true);
                                if (counter[0] > 0) {
                                    counter[0]--;
                                } else {
                                    counter[0] = 0;
                                }
                                DrawableCompat.setTint(like_icon.getDrawable(), ContextCompat.getColor(requireContext(), R.color.white));
//                                like_text.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
//                                like_icon.setImageResource(R.drawable.ic_reel_like_inactive);
                                reelsItem.getInfo().setLiked(false);
                            }
                        }, true);
                    }
                }
            }));

            fullScreen.setOnClickListener(view -> fullScreen(0));

            play_image.setOnClickListener(v -> {
                if (!InternetCheckHelper.isConnected()) {
                    Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (player != null) {
                    if (!player.getPlayWhenReady()) {
                        Log.e(TAG, "play:5 ");
                        play_image.setVisibility(View.GONE);
                        player.setPlayWhenReady(true);
                        trackVideoDuration();
//                        if (getContext() != null)
//                            Glide.with(getContext()).load(R.raw.equilizer_white).into(equilizer);
                    }
                }
            });

            userFollowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!InternetCheckHelper.isConnected()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    if (reelsItem.getSource() != null) {
                        followUnfollowPresenter.followSource(reelsItem.getSource().getId(), 0, null);
                        reelsItem.getSource().setFavorite(true);
                    } else {
                        followUnfollowPresenter.followAuthor(reelsItem.getAuthor().get(0).getId(), 0, null);
                        reelsItem.getAuthor().get(0).setFollow(true);
                    }
                    userFollowIcon.setVisibility(View.INVISIBLE);
                    userFollowBtn.setEnabled(false);
                }
            });
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!InternetCheckHelper.isConnected()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    Intent intent;
                    if (reelsItem.getSource() != null) {
                        intent = new Intent(getContext(), ChannelDetailsActivity.class);
                        intent.putExtra("id", reelsItem.getSource().getId());
                    } else {
                        User user = new PrefConfig(getContext()).isUserObject();
                        if (user != null && !TextUtils.isEmpty(user.getId()) && user.getId().equalsIgnoreCase(reelsItem.getAuthor().get(0).getId())) {
                            intent = new Intent(getContext(), ProfileActivity.class);
                        } else {
                            intent = new Intent(getContext(), AuthorActivity.class);
                        }
                        intent.putExtra("authorID", reelsItem.getAuthor().get(0).getId());
                        intent.putExtra("authorContext", reelsItem.getAuthor().get(0).getContext());
                    }
                    startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST);
                    // getActivity().startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.enter_500, R.anim.exit_500);
                }
            });

//            caption.setOnClickListener(v -> {
//                if (!prefConfig.isReelsCaption()) {
//                    prefConfig.setReelsCaption(true);
//                    caption.setImageResource(R.drawable.ic_caption_on);
//                    if (player != null) {
//                        getCaption(player.getCurrentPosition());
//                    }
//                } else {
//                    prefConfig.setReelsCaption(false);
//                    caption.setImageResource(R.drawable.ic_caption_off);
//                    removeCaptions(true);
//                }
//            });

            iv_add_to_archive.setOnClickListener(v -> {
                if (sharePresenter != null) {

                }
            });
        }
    }

    private void updateCaption() {
        if (prefConfig.isReelsCaption()) {
            if (player != null) {
                getCaption(player.getCurrentPosition());
            }
        } else {
            removeCaptions(true);
        }
    }

    private void share() {
        if (!InternetCheckHelper.isConnected()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (getContext() == null) return;
        if (shareBottomSheetPresenter != null) {
            final boolean[] isIgnoreDismiss = {false};
            showLoadingView(true);
            onPause();
            shareBottomSheetPresenter.share_msg(reelsItem.getId(), new ShareInfoInterface() {
                @Override
                public void response(ShareInfo shareInfo) {
                    showLoadingView(false);
                    onPause();
                    share_icon.setVisibility(View.VISIBLE);
                    if (shareInfo == null) {
                        return;
                    }

                    AnalyticsEvents.INSTANCE.logEvent(getContext(), Events.REEL_SHARE_CLICK);

                    Article article = new Article();
                    if (reelsItem != null) {
                        MediaMeta mediaMeta = reelsItem.getMediaMeta();
                        article.setMediaMeta(mediaMeta);
                    }
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    if (shareInfo != null) {
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfo.getShare_message());
                    }
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    getActivity().startActivity(shareIntent);

//                    reelsBottomSheet = new MediaShare.Builder(getActivity())
//                            .setId(reelsItem.getId())
//                            .isArticle(false)
//                            .setShareInfo(shareInfo)
//                            .setFragmentContextVal(VideoInnerFragment.this)
//                            .setArticle(article)
//                            .setonDismissListener(dialog -> {
//                                if (!isIgnoreDismiss[0])
//                                    onResume();
//                            })
//                            .setReelBottomSheetCallback(new ReelBottomSheetCallback() {
//                                @Override
//                                public void onReport() {
//                                    isIgnoreDismiss[0] = true;
//                                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
//                                            Events.REPORT_CLICK);
//                                    if (reelsItem == null) {
//                                        return;
//                                    }
//                                    ReportBottomSheet reportBottomSheet = new ReportBottomSheet(getActivity(), new DismissBottomSheet() {
//                                        @Override
//                                        public void dismiss(boolean flag) {
//                                            if (flag) {
//                                                // on hide Bottom sheet
//                                                onResume();
//                                            }
//                                        }
//                                    });
//                                    reportBottomSheet.show(reelsItem.getId(), "articles");
//                                }
//
//                                @Override
//                                public void onSave() {
//                                    if (!TextUtils.isEmpty(reelsItem.getId())) {
//                                        if (sharePresenter != null)
//                                            sharePresenter.archive(reelsItem.getId(), shareInfo.isArticle_archived());
//                                    }
//                                    onResume();
//                                }
//
//                                @Override
//                                public void onNotInterested() {
//                                    if (!TextUtils.isEmpty(reelsItem.getId())) {
//                                        if (sharePresenter != null)
//                                            sharePresenter.less(reelsItem.getId());
//                                    }
//                                    onResume();
//                                }
//
//                                @Override
//                                public void onIgnore() {
//                                    isIgnoreDismiss[0] = true;
//                                }
//                            })
//                            .build();
//                    reelsBottomSheet.show();

                }

                @Override
                public void error(String error) {
                    showLoadingView(false);
                    onResume();
                }
            });
        }
    }

    private void setCaptions() {
        if (reelsItem.getCaptions() != null && reelsItem.getCaptions().size() > 0) {
//            caption.setVisibility(View.VISIBLE);
            description.setVisibility(View.GONE);
            if (player != null) {
                getCaption(player.getCurrentPosition());
            }
        } else {
            caption.setVisibility(View.GONE);
            linearLayout5.setVisibility(View.VISIBLE);

//            showVerifiedIcon(source_name, reelsItem);
            if (!reelsItem.getAuthorNameToDisplay().equals("")) {
                source_name.setText(reelsItem.getAuthorNameToDisplay());
            } else {
                source_name.setText(reelsItem.getSourceNameToDisplay());
            }

            if (reelsItem.isNativetitle()) {
//                dummy_text.setText(!TextUtils.isEmpty(reelsItem.getDescription()) ? reelsItem.getDescription() : "");
                description.setText(!TextUtils.isEmpty(reelsItem.getDescription()) ? reelsItem.getDescription() : "");
                ViewGroup.LayoutParams params1 = (ViewGroup.LayoutParams) description.getLayoutParams();
                params1.height = ViewGroup.LayoutParams.MATCH_PARENT;
                description.setLayoutParams(params1);

                if (!TextUtils.isEmpty(reelsItem.getDescription())) {
                    if (dummy_text.getLayoutParams().height < description.getLayoutParams().height) {
                        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) description.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        description.setLayoutParams(params);
                    } else {
                        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) description.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        description.setLayoutParams(params);
                    }

                    description.setVisibility(View.VISIBLE);
                    String descriptionWithTags = reelsItem.getDescription().trim();
                    description.setText(setClickForHashTag(new SpannableString(descriptionWithTags), descriptionWithTags));
                    description.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    description.setVisibility(View.GONE);
                }
            } else {
                description.setVisibility(View.GONE);
            }
        }
    }

    private void fullScreen(int mOrientation) {
        if (!InternetCheckHelper.isConnected()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if ((popUpClass == null || !popUpClass.isShowing()) && mMenuVisible && fragmentResumed && !mFragmentHidden) {
            onPause();
            long playingPos = 0;
            if (player != null) {
                playingPos = player.getCurrentPosition();
            }
            popUpClass = new PopUpClass();
//            popUpClass.showPopupWindowReels(view, reelsItem.getMediaLandscape(), getActivity(), playingPos, new PopUpClass.OnPopupDismissListener() {
            popUpClass.showPopupWindowReels(view, reelsItem, getActivity(), playingPos, new PopUpClass.OnPopupDismissListener() {
                @Override
                public void onDismiss(long pos) {
                    if (Constants.onResumeReels) {
                        if (reelViewMore == null || reelViewMore.getDialog() == null)
                            startVideo(pos);
                    }
                    fragmentResumed = true;
                    popUpClass = null;
                }
            }, mOrientation);
        }
    }

    private void showBottomSheet() {
        if (reelsItem != null && isAdded()) {
            reelViewMore = ReelViewMoreSheet.getInstance(reelsItem, position, new ReelViewMoreSheet.OnShareListener() {
                @Override
                public void onShareClicked() {
                    share();
                }

                @Override
                public void onFollow(ReelsItem mReelsItem) {
                    if (mReelsItem == null) return;
                    com.ziro.bullet.data.models.sources.Source source = mReelsItem.getSource();
                    if (source != null) {
                        reelsItem.getSource().setFavorite(source.isFavorite());
                        if (reelsItem.getSource().isFavorite()) {
                            userFollowIcon.setVisibility(View.INVISIBLE);
                            userFollowBtn.setEnabled(false);
                        } else {
                            userFollowIcon.setVisibility(View.VISIBLE);
                            userFollowBtn.setEnabled(true);
                        }
                    }
                }

                @Override
                public void viewMoreDismissed() {

                }
            });
            reelViewMore.show(getChildFragmentManager(), reelsItem.getId());
            reelViewMore.getViewLifecycleOwnerLiveData().observe(this, new Observer<LifecycleOwner>() {
                @Override
                public void onChanged(LifecycleOwner lifecycleOwner) {
                    //when lifecycleOwner is null fragment is destroyed
                    if (lifecycleOwner == null) {
                        onResume();
                    }
                }
            });
        }
    }

    private Article getArticleFromReels(ReelsItem reels) {
        Article article = new Article();
        if (reels != null) {
            article.setId(reels.getId());
            article.setTitle(reels.getDescription());
            article.setLink(reels.getMedia());
            article.setPublishTime(reels.getPublishTime());
            article.setType("REELS");
            article.setSource(reels.getSource());
            article.setAuthor(reels.getAuthor());
            MediaMeta mediaMeta = new MediaMeta();
            if (reels.getMediaMeta() != null) {
                mediaMeta.setDuration(reels.getMediaMeta().getDuration());
                mediaMeta.setHeight(reels.getMediaMeta().getHeight());
                mediaMeta.setWidth(reels.getMediaMeta().getWidth());
            }
            article.setMediaMeta(mediaMeta);
            ArrayList<Bullet> bullets = new ArrayList<>();
            Bullet bullet = new Bullet();
            bullet.setImage(reels.getImage());
            bullet.setData(reels.getDescription());
            bullets.add(bullet);
            article.setBullets(bullets);
            article.setSelected(true);
        }
        return article;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(View view) {
        Log.e("VideoInner", "===================================");
        Log.e("VideoInner", "init - " + this.getTag() + " =player= " + player);

        extraSpace = view.findViewById(R.id.extraSpace);
        volumeseekbar = view.findViewById(R.id.volumeseekbar);
        playerlayout = view.findViewById(R.id.constraintLayoutPlay);
        constraintL2 = view.findViewById(R.id.constraintL2);
        text2 = view.findViewById(R.id.text2);
        clickLayout = view.findViewById(R.id.tv_click);
        exo_progress = view.findViewById(R.id.exo_progress);
        debuglogs = view.findViewById(R.id.debuglogs);
        debugcardview = view.findViewById(R.id.debugcardview);
        linearLayout5 = view.findViewById(R.id.linearLayout5);
        thumbnail = view.findViewById(R.id.thumbnail);
        progress_bar_video = view.findViewById(R.id.progress_bar_video);
        progress = view.findViewById(R.id.progress);
        heart = view.findViewById(R.id.imgHeart);
        iv_add_to_archive = view.findViewById(R.id.iv_add_to_archive);
        dynamicContainer = view.findViewById(R.id.dynamicContainer);
        containerParent = view.findViewById(R.id.container_parent);
        caption = view.findViewById(R.id.caption);
        edit = view.findViewById(R.id.edit);
        playPauseBtn = view.findViewById(R.id.img_play);
        share_icon = view.findViewById(R.id.img_share);
        dots = view.findViewById(R.id.imgSelect);
        comment_icon = view.findViewById(R.id.btn_comment);
        like_icon = view.findViewById(R.id.imgLike);
        comment_text = view.findViewById(R.id.comment_text);
        like_text = view.findViewById(R.id.like_text);
        gradient = view.findViewById(R.id.gradient);
        playerView = view.findViewById(R.id.video_view);
        layoutView = view.findViewById(R.id.constraintLayout);
        speaker = view.findViewById(R.id.speaker);
        userFollowBtn = view.findViewById(R.id.ll_follow);
        userFollowIcon = view.findViewById(R.id.user_follow_icon);
        icon = view.findViewById(R.id.user_pic);
        source_name = view.findViewById(R.id.source_name);
        author_name = view.findViewById(R.id.author_name);
        description = view.findViewById(R.id.description);
        dummy_text = view.findViewById(R.id.dummy_text);
//        textArea = view.findViewById(R.id.textArea);
        fullScreen = view.findViewById(R.id.full_screen);
        play_image = view.findViewById(R.id.play_image);
        llRight = view.findViewById(R.id.right);
        description_fastreel = view.findViewById(R.id.description_fastreel);

        initializePlayer();

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
        }
        volumeseekbar.setMax(maxVolume);

        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updatedVolume = curVolume;
        Constants.updatedvol = curVolume;
        //to disable seekbar movement in touch
        volumeseekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        volumeseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

//                audioManager.setStreamVolume(player.getAudioStreasetmType(), i, 0);
                curVolume = i;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, 0);
                if (curVolume == minVolume) {
                    Constants.ReelMute = true;
                    prefConfig.setMute(true);
                    speaker.setImageResource(R.drawable.ic_sound_min);
                } else if (curVolume > minVolume && !Constants.ReelMute) {
//                    Constants.ReelMute = false;
                    speaker.setImageResource(R.drawable.ic_sound_max);
                }
                updatedVolume = curVolume;
                volumeseekbar.setProgress(updatedVolume);
                Constants.updatedvol = curVolume;
                //if i == max set to max vol if i == min set to min volume
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//check this
//        if (curVolume == minVolume) {
//            Constants.ReelMute = true;
//            speaker.setImageResource(R.drawable.ic_sound_min);
//        } else if (curVolume > minVolume) {
//            Constants.ReelMute = false;
//            speaker.setImageResource(R.drawable.ic_sound_max);
//        }

        playerlayout.setOnClickListener(v -> {
            controllerHandler.removeCallbacks(controllerRunnable);
            if (constraintL2.getVisibility() == View.VISIBLE) {
                constraintL2.setVisibility(View.INVISIBLE);
            } else {
                constraintL2.setVisibility(View.VISIBLE);
                controllerHandler.postDelayed(controllerRunnable, 2000);
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.setPlayWhenReady(!player.getPlayWhenReady());
                if (player.getPlayWhenReady()) {
//                    R.drawable.icon_video_pause
                    playPauseBtn.setImageResource(R.drawable.ic_pausebg);
                } else {
                    playPauseBtn.setImageResource(R.drawable.ic_playbg);
                }
            }
        });

    }

    private void playVideo() {
        playPauseBtn.setImageResource(R.drawable.exo_controls_pause);
//        player.pl
    }


    private boolean checkHashTag(String str) {
        Pattern pattern = Pattern.compile("#(\\S+)");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private SpannableString setClickForHashTag(SpannableString spannableString, String descriptionStr) {
        String[] strings = descriptionStr.split("\\s+");

//        String btnString = "";
//        if (descriptionStr.endsWith(getString(R.string.see_less)) || descriptionStr.endsWith(getString(R.string.see_more))) {
//            btnString = getString(R.string.see_less);
//            if (!isMore) {
//                btnString = "..." + TWO_SPACES + getString(R.string.see_more);
//            }
//        }

        StringBuilder dummyString = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            String tag = strings[i];

            int start = dummyString.length();
            int end = dummyString.length() + tag.length();

            dummyString.append(" ").append(tag);

            if (checkHashTag(tag)) {
                if (spannableString.length() >= start && spannableString.length() >= end) {
                    spannableString.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            Intent intent = new Intent(getContext(), ReelActivity.class);
                            intent.putExtra(ReelActivity.REEL_HASHTAG, tag.trim());
                            startActivity(intent);
                            if (!checkReelTab && getActivity() != null) {
                                getActivity().finish();
                            }
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                        }
                    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    spannableString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.white)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannableString;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ChannelDetailsActivity.FOLLOW_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                com.ziro.bullet.data.models.sources.Source source = data.getParcelableExtra("source");
                Author author = data.getParcelableExtra("author");
                if (source != null) {
                    reelsItem.getSource().setFavorite(source.isFavorite());
                    if (reelsItem.getSource().isFavorite()) {
                        userFollowIcon.setVisibility(View.INVISIBLE);
                        userFollowBtn.setEnabled(false);
                    } else {
                        userFollowIcon.setVisibility(View.VISIBLE);
                        userFollowBtn.setEnabled(true);
                    }
                }
                if (author != null) {
                    reelsItem.getAuthor().get(0).setFollow(author.isFollow());
                    if (reelsItem.getAuthor().get(0).isFollow()) {
                        userFollowIcon.setVisibility(View.INVISIBLE);
                        userFollowBtn.setEnabled(false);
                    } else {
                        userFollowIcon.setVisibility(View.VISIBLE);
                        userFollowBtn.setEnabled(true);
                    }
                }
            }
        } else if (newsPresenter != null && data != null && data.hasExtra("id")) {
            String id = data.getStringExtra("id");
            if (!TextUtils.isEmpty(id)) {
                newsPresenter.counters(id, info -> {
                    Utils.checkLikeViewReels(getContext(), info.getLike_count(), info.getComment_count(), comment_text, like_text, like_icon, info.isLiked());
                });
            }
        }
    }

    public void inAnimation(View view) {
        //In transition: (alpha from 0 to 1)
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
    }

    private void updateVolume() {
        volumeseekbar.setProgress(curVolume);
    }

    @Override
    public void onResume() {
//        if (!isVisible() || Constants.HomeSelectedFragment != BOTTOM_TAB_VIDEO) {
//            super.onResume();
//            return;
//        }
        Log.e("VideoFragment_DEBUG", "onResume - " + this.getTag() + " =player= " + player + " " + (reelsItem != null ? (reelsItem.getId() + "  >> " + reelsItem.getDescription()) : ""));
        if (prefConfig.isVideoAutoPlay()) {
            play_image.setVisibility(View.GONE);
        } else {
            play_image.setVisibility(View.VISIBLE);
        }
//avoiding initilizing previous player avoiding player selay

//        initializePlayer();
        if (player == null) {
            Log.e("VideoFragment_DEBUG", "onResume: 1");
            initializePlayer();
        } else {
            Log.e("VideoFragment_DEBUG", "onResume: 2");
            player.setPlayWhenReady(prefConfig.isVideoAutoPlay());
        }

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
        }
        volumeseekbar.setMax(maxVolume);

        curVolume = Constants.updatedvol;
        volumeseekbar.setProgress(curVolume);

        if (prefConfig.isMute()) {
            Constants.ReelMute = true;
            player.setVolume(0);
            speaker.setImageResource(R.drawable.ic_sound_min);
        } else {
            Constants.ReelMute = false;
            player.setVolume(1);
            speaker.setImageResource(R.drawable.ic_sound_max);
        }

        fragmentResumed = true;

        if (reelViewMore != null && !reelViewMore.isVisible() && Constants.HomeSelectedFragment == Constants.BOTTOM_TAB_VIDEO) {
            player.setPlayWhenReady(prefConfig.isVideoAutoPlay());
        }

        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.e("VideoInner", "onStart - " + this.getTag() + " =player= " + player + " =reelsItem= " + reelsItem);
//        initializePlayer();
    }

    @Override
    public void onPause() {
        removeCaptions(true);
        if (popUpClass != null && popUpClass.isShowing()) {
            popUpClass.dismiss();
        }
        fragmentResumed = false;


        Log.e("VideoInner", "onPause - " + this.getTag() + " =player= " + player);
        if (player != null) {
            if (isVisible() && !player.getPlayWhenReady() && player.getCurrentPosition() > 500) {

                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.REEL_ID, reelsItem.getId());
                params.put(Events.KEYS.DURATION, String.valueOf(player.getCurrentPosition()));
                AnalyticsEvents.INSTANCE.logEventWithAPI(getContext(), params, Events.REEL_DURATION);
                Log.e(TAG, "onPause:ssss " + params);

            }
            player.setPlayWhenReady(false);
            if (prefConfig != null && !prefConfig.isVideoAutoPlay()) {
                play_image.setVisibility(View.VISIBLE);
            }
        }
        super.onPause();
    }

    public void clickEnabling(boolean enable) {
        if (layoutView != null) layoutView.setEnabled(enable);
    }

    public void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.setPlayWhenReady(false);
            if (prefConfig != null && !prefConfig.isVideoAutoPlay()) {
                play_image.setVisibility(View.VISIBLE);
            }
            if (tracker != null) {
                tracker.purgeHandler();
                tracker = null;
            }
            player.removeListener(this);
            player.release();
            player = null;
            if (playerView != null) playerView.setPlayer(null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("video-test", "onStop: ");
        Log.e(TAG, "onStop - " + this.getTag() + " =player= " + player);
        EventBus.getDefault().unregister(this);
//        releasePlayer();
        if (tracker != null) {
            tracker.purgeHandler();
            tracker = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy - " + this.getTag() + " =player= " + player);
        releasePlayer();
        if (tracker != null) {
            tracker.purgeHandler();
            tracker = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach - " + this.getTag() + " =player= " + player);
        releasePlayer();
        if (tracker != null) {
            tracker.purgeHandler();
            tracker = null;
        }
    }

    public void startVideo(long seekPos) {
        Log.e(TAG, "-Start - " + this.getTag() + " =player= " + player);
        if (checkReelTab) {
            if (!Constants.onResumeReels) {
                return;
            }
        }
        if (prefConfig != null) {
//            if (prefConfig.isReelsCaption()) {
//                caption.setImageResource(R.drawable.ic_caption_on);
//            } else {
//                caption.setImageResource(R.drawable.ic_caption_off);
//            }
            if (prefConfig.isVideoAutoPlay()) {
                play_image.setVisibility(View.GONE);
            } else {
                play_image.setVisibility(View.VISIBLE);
            }
            if (player != null && getContext() != null) {
                if (!player.getPlayWhenReady()) {
                    if (Constants.ReelMute) {
                        player.setVolume(0);
//                        if (getContext() != null && prefConfig.isVideoAutoPlay()) {
//                            Glide.with(getContext()).load(R.raw.equilizer).into(equilizer);
//                        } else {
//                            equilizer.setImageResource(R.drawable.static_equilizer);
//                        }

                        speaker.setImageResource(R.drawable.ic_sound_min);
                    } else {
                        player.setVolume(1);
//                        if (getContext() != null && prefConfig.isVideoAutoPlay()) {
//                            Glide.with(getContext()).load(R.raw.equilizer_white).into(equilizer);
//                        } else {
//                            equilizer.setImageResource(R.drawable.static_equilizer);
//                        }
                        speaker.setImageResource(R.drawable.ic_sound_max);
                    }
                    player.seekTo(seekPos);
                    player.setPlayWhenReady(prefConfig.isVideoAutoPlay());
                    if (prefConfig.isVideoAutoPlay()) trackVideoDuration();
                }
            }
        }
    }

    public void mute() {
        if (player != null) {
            player.setVolume(0);
        }
    }

    public void unmute() {
        if (player != null) player.setVolume(1);
    }

    private void initializePlayer() {
        LoadControl loadControl = new DefaultLoadControl.Builder().setAllocator(new DefaultAllocator(true, 16))
                .setBufferDurationsMs(VideoPlayerConfig.MIN_BUFFER_DURATION, VideoPlayerConfig.MAX_BUFFER_DURATION, VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER, VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER)
                .setTargetBufferBytes(-1).setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl();

        if (reelsItem != null && !TextUtils.isEmpty(reelsItem.getMedia())) {

            String streamUrl = reelsItem.getMedia();
//            if (streamUrl.endsWith(".m3u8")) {
//                //for HLS
//                AdaptiveTrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory();
//                player = ExoPlayerFactory.newSimpleInstance(getActivity(), new DefaultTrackSelector(adaptiveTrackSelection), loadControl);
//                defaultBandwidthMeter = new DefaultBandwidthMeter();
//                dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "nib"), defaultBandwidthMeter);
//
//                Uri uri = Uri.parse(streamUrl);
//                mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
//                        .setAllowChunklessPreparation(true)
//                        .createMediaSource(uri);
//                player.prepare(mediaSource);
//            } else {
//
//                //for mp4
//                trackSelector = new DefaultTrackSelector();
//                trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());
//                player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
//
//                sample = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(getActivity(), "nib")).createMediaSource(Uri.parse(streamUrl));
//                player.prepare(sample);
//            }

            player.setRepeatMode(Player.REPEAT_MODE_OFF);

            if (prefConfig.isVideoAutoPlay()) {
                play_image.setVisibility(View.GONE);
            } else {
                play_image.setVisibility(View.VISIBLE);
            }

//            if (reelsItem.getMediaMeta() != null) {
//                Log.d("DIMEN_TAG", "initializePlayer: Width: " + reelsItem.getMediaMeta().getWidth());
//                Log.d("DIMEN_TAG", "initializePlayer: Height: " + reelsItem.getMediaMeta().getHeight());
//                if (reelsItem.getMediaMeta().getWidth() > reelsItem.getMediaMeta().getHeight()) {
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
//                } else {
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
//                }
//            } else {
//                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//            }
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
            player.addListener(this);
            playerView.setPlayer(player);

            exo_progress.addListener(new TimeBar.OnScrubListener() {
                @Override
                public void onScrubStart(TimeBar timeBar, long position) {
                    if (controllerHandler != null && controllerRunnable != null) {
                        controllerHandler.removeCallbacks(controllerRunnable);
                    }
                }

                @Override
                public void onScrubMove(TimeBar timeBar, long position) {
                    player.seekTo(position);
                }

                @Override
                public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                    if (constraintL2.getVisibility() == View.VISIBLE) {
                        if (controllerHandler != null && controllerRunnable != null) {
                            controllerHandler.postDelayed(controllerRunnable, 2000);
                        }
                    }
                }
            });
//            player.setPlayWhenReady(prefConfig.isVideoAutoPlay());

        } else {
            Log.d("VideoInner", "initializePlayer: empty url");
        }
    }

    private void manageControllers(boolean show) {
        if (playerView != null) {
            playerView.setUseController(show);
            playerView.setControllerAutoShow(show);
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        mMenuVisible = menuVisible;
        if (!mMenuVisible && popUpClass != null && popUpClass.isShowing()) {
            popUpClass.dismiss();

        }
        super.setMenuVisibility(menuVisible);
        /*//Causing reels to play when are paused after video gets downloaded
        if (menuVisible) {
            if (getActivity() instanceof ReelActivity) startVideo(0);
        } else {
            onPause();
        }*/
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mFragmentHidden = hidden;
        if (hidden && popUpClass != null && popUpClass.isShowing()) {
            popUpClass.dismiss();
        }
    }

    private void showBottomSheetDialog(Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet(getActivity(), null, false, "MY_REELS");
        }
        shareBottomSheet.show(article, onDismissListener, null);
    }

    private void hideSystemUi() {
        if (playerView == null) return;
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
//        player.stop();
//        Start(0);
        Log.d(TAG, "onPlayerError: " + error);
        if (listener != null) {
            listener.nextVideo(position);
        }

//        player.release();
//        initializePlayer();
        playPauseBtn.setImageResource(R.drawable.ic_playbg);
    }

    @Override
    public void loaderShow(boolean flag) {

    }

    @Override
    public void error(String string) {

    }

    @Override
    public void success(CaptionResponse response) {
        if (reelsItem != null && response != null && response.getCaptions() != null) {
            Log.d(TAG, "success() called with: response = [" + response.getCaptions().size() + "]");
            reelsItem.setCaptions(response.getCaptions());
        }
//        setCaptions();
    }

    @Override
    public void loaderShow(boolean b, String method) {

    }

    @Override
    public void success(String message, String method, String id) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MediaShare.PERMISSION_REQUEST_STORAGE) {
            if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (reelsBottomSheet != null) reelsBottomSheet.saveToDevice();
            }
        }
    }

    private void showLoadingView(boolean isShow) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (isShow) {
            if (loadingDialog == null) {
                loadingDialog = new PictureLoadingDialog(getActivity());
            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    public class VideoPlayerConfig {
        //Minimum Video you want to buffer while Playing
        public static final int MIN_BUFFER_DURATION = 5000;
        //Max Video you want to buffer during PlayBack
        public static final int MAX_BUFFER_DURATION = 8000;
        //Min Video you want to buffer before start Playing it
        public static final int MIN_PLAYBACK_START_BUFFER = 0;
        //Min video You want to buffer when user resumes video
        public static final int MIN_PLAYBACK_RESUME_BUFFER = 0;
    }

    class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        private ReelsItem reelsItem;
        private boolean disableSwipingRight;

        public SwipeGestureListener(ReelsItem reelsItem, boolean disableSwipingRight) {
            this.reelsItem = reelsItem;
            this.disableSwipingRight = disableSwipingRight;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            //onSwipeRight();
                        } else {
                            if (!disableSwipingRight) {
                                result = true;

                                Intent intent;
                                if (reelsItem.getSource() != null) {
                                    intent = new Intent(getContext(), ChannelDetailsActivity.class);
                                    intent.putExtra("id", reelsItem.getSource().getId());
                                    Map<String, String> params = new HashMap<>();
                                    params.put(Events.KEYS.REEL_ID, reelsItem.getSource().getId());
                                    AnalyticsEvents.INSTANCE.logEvent(getContext(), params, Events.REELS_SOURCE_OPEN);
                                } else {
                                    User user = new PrefConfig(getContext()).isUserObject();
                                    if (user != null && !TextUtils.isEmpty(user.getId()) && user.getId().equalsIgnoreCase(reelsItem.getAuthor().get(0).getId())) {
                                        intent = new Intent(getContext(), ProfileActivity.class);
                                    } else {
                                        intent = new Intent(getContext(), AuthorActivity.class);
                                    }
                                    intent.putExtra("authorID", reelsItem.getAuthor().get(0).getId());
                                    intent.putExtra("authorContext", reelsItem.getAuthor().get(0).getContext());

                                    Map<String, String> params = new HashMap<>();
                                    params.put(Events.KEYS.REEL_ID, reelsItem.getAuthor().get(0).getId());
                                    AnalyticsEvents.INSTANCE.logEvent(getContext(), params, Events.REELS_SOURCE_OPEN);
                                }
                                startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST);
//                                getActivity().startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.enter_500, R.anim.exit_500);
                                //onSwipeLeft();
                            }
                        }
                        //result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        //onSwipeBottom();
                    } else {
                        //onSwipeTop();
                    }
                    //result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return result;
        }
    }
}