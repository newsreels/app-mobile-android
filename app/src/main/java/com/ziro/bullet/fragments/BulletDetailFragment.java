package com.ziro.bullet.fragments;

import static android.app.Activity.RESULT_OK;
import static androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.ziro.bullet.Helper.PopUpClass;
import com.ziro.bullet.Helper.SimpleOrientationListener;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.VideoFullScreenActivity;
import com.ziro.bullet.activities.WebViewActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.BulletDetailCallback;
import com.ziro.bullet.interfaces.LikeInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.model.articles.MediaMeta;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulletDetailFragment extends Fragment implements NewsCallback, ShareToMainInterface {

    private static final String ARTICLE = "article";
    private static final String IS_SELECTED = "is_selected";

    private static String mType;
    private SimpleExoPlayer simpleExoPlayer;
    private Article article;
    private String articleId = null;
    private NewsPresenter presenter;
    private PrefConfig prefConfig;
    private ImageView postImage;
    private TextView articleTitle;
    private LinearLayout bulletContainer;
    private ConstraintLayout sourceContainer;
    private TextView sourceName;
    private TextView authorName;
    private ImageView sourceImage;
    private TextView postTime;
    private TextView viewCount;
    private TextView viewFullArticle;
    private PlayerView videoPlayer;
    private YouTubePlayerView youtubeView;
    private YouTubePlayer mYouTubePlayer;
    private ImageView playImage;
    private ImageView speaker;
    private ImageView back;
    private ImageView fullscreen;
    private RelativeLayout buttonPanel;
    private LikePresenter likePresenter;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private boolean isSelected;

    private ConstraintLayout playerContainer;
    private PictureLoadingDialog mLoadingDialog;
    private long mDuration = 0;
    private PopUpClass popUpClass;

    private long startTime = 0;

    private PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            if (videoPlayer != null && videoPlayer.getPlayer() != null && videoPlayer.getPlayer().getPlaybackState() == Player.STATE_READY) {
                playImage.setVisibility(View.GONE);
                videoPlayer.setVisibility(View.VISIBLE);
            }
        }
    };
    private FollowUnfollowPresenter followUnfollowPresenter;
    private LinearLayout share;
    private LinearLayout comment;
    private LinearLayout llFavIcon;
    private ImageView favIcon;
    private TextView favCount;
    private TextView commentCount;
    private ConstraintLayout follow;
    private TextView follow_txt;
    private View followBottomBar;
    private ProgressBar follow_progress;

    public BulletDetailFragment() {
        // Required empty public constructor
    }

    public static BulletDetailFragment newInstance(Article article, String type, boolean isSelected) {

        if (article != null) {
            if (article.getType().equalsIgnoreCase("first_youtube")) {
                article.setType("youtube");
            } else if (article.getType().equalsIgnoreCase("first_video")) {
                article.setType("video");
            }
        }
        mType = type;
        BulletDetailFragment fragment = new BulletDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARTICLE, article);
        args.putBoolean(IS_SELECTED, isSelected);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Returns a list of packages that support Custom Tabs.
     */
    public static ArrayList<ResolveInfo> getCustomTabsPackages(Context context) {
        PackageManager pm = context.getPackageManager();
        // Get default VIEW intent handler.
        Intent activityIntent = new Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.fromParts("http", "", null));

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        ArrayList<ResolveInfo> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
//            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info);
            }
        }
        return packagesSupportingCustomTabs;
    }

    public static boolean isCustomTabSupported(Context context, Uri url) {
        return getCustomTabsPackages(context, url).size() > 0;
    }

    /**
     * Returns a list of packages that support Custom Tabs.
     */
    public static ArrayList getCustomTabsPackages(Context context, Uri url) {
        PackageManager pm = context.getPackageManager();
        // Get default VIEW intent handler.
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, url);

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        ArrayList packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info);
            }
        }
        return packagesSupportingCustomTabs;
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        if (articleId != null) {
            AnalyticsEvents.INSTANCE.articleViewEvent(requireContext(), articleId);
            Map<String, String> params = new HashMap<>();
            params.put(Events.KEYS.ARTICLE_ID, articleId);
            AnalyticsEvents.INSTANCE.logEventWithAPI(requireContext(),
                    params,
                    Events.ARTICLE_VIEW);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        long duration = System.currentTimeMillis() - startTime;

        if (duration > 1000) {
            if (articleId != null) {
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID, articleId);
                params.put(Events.KEYS.DURATION, String.valueOf(duration));
                AnalyticsEvents.INSTANCE.logEventWithAPI(getContext(),
                        params,
                        Events.ARTICLE_DURATION);
                AnalyticsEvents.INSTANCE.logEventWithAPI(getContext(),
                        params,
                        Events.ARTICLE_DURATION);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            article = getArguments().getParcelable(ARTICLE);
//            article = new Gson().fromJson(getArguments().getString(ARTICLE), Article.class);
//            article = new Gson().fromJson(getArguments().getString(ARTICLE), Article.class);
            isSelected = getArguments().getBoolean(IS_SELECTED);
            // Log.e("ARTICLE TYPE @@@", article.getType()+"");
        }
        shareBottomSheetPresenter = new ShareBottomSheetPresenter(getActivity());
        prefConfig = new PrefConfig(getContext());
        presenter = new NewsPresenter(getActivity(), this);
        likePresenter = new LikePresenter(getActivity());
        followUnfollowPresenter = new FollowUnfollowPresenter(getActivity());
        popUpClass = new PopUpClass();
        popUpClass.setOnDismissListener(onDismissListener);
    }

    private void resizeVideoView(ViewGroup view, double mVideoHeight, double mVideoWidth) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int cardWidth = displayMetrics.widthPixels - (2 * getResources().getDimensionPixelSize(R.dimen._15sdp));

        int screenHeight;
//        if (TextUtils.isEmpty(type))
        screenHeight = displayMetrics.heightPixels - (getResources().getDimensionPixelSize(R.dimen._150sdp));
//        else
//            screenHeight = displayMetrics.heightPixels - (getResources().getDimensionPixelSize(R.dimen._220sdp));
        int cardHeight = (int) ((mVideoHeight * cardWidth) / mVideoWidth);
        if (cardHeight > screenHeight) {
            cardHeight = screenHeight;
        }
        view.getLayoutParams().height = cardHeight;
        view.requestLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bullet_detail, container, false);

        bind(view);
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        comment.setOnClickListener(v -> {
            if (article == null) return;
            Intent intent = new Intent(getContext(), CommentsActivity.class);
            intent.putExtra("article_id", article.getId());
            startActivityForResult(intent, Constants.CommentsRequestCode);
        });
        follow.setOnClickListener(view1 -> {
                    if (getActivity() == null) return;
                    follow.setEnabled(false);
                    follow_progress.setVisibility(View.VISIBLE);
                    follow_txt.setVisibility(View.INVISIBLE);
                    if (article.getSource().isFavorite()) {
                        followUnfollowPresenter.unFollowSource(article.getSource().getId(), 0,
                                (position1, flag) -> {
                                    follow.setEnabled(true);
                                    follow_progress.setVisibility(View.GONE);
                                    follow_txt.setVisibility(View.VISIBLE);
                                    if (flag) {
                                        article.getSource().setFavorite(false);
                                        try {
                                            updateFollowColor(article.getSource().isFavorite());
                                        } catch (IllegalStateException e) {
                                            e.printStackTrace();
                                        }
//                                        follow_txt.setText(getActivity().getString(R.string.follow));
                                    }
                                }
                        );
                    } else {
                        followUnfollowPresenter.followSource(article.getSource().getId(), 0,
                                (position1, flag) -> {
                                    follow.setEnabled(true);
                                    follow_progress.setVisibility(View.GONE);
                                    follow_txt.setVisibility(View.VISIBLE);
                                    if (flag) {
                                        article.getSource().setFavorite(true);
                                        try {
                                            updateFollowColor(article.getSource().isFavorite());
                                        } catch (IllegalStateException e) {
                                            e.printStackTrace();
                                        }
//                                        follow_txt.setText(getActivity().getString(R.string.following));
                                    }
                                }
                        );
                    }
                }
        );
        llFavIcon.setOnClickListener(v -> {
            llFavIcon.setEnabled(false);
            if (article != null)
                likePresenter.like(article.getId(), new LikeInterface() {
                    @Override
                    public void success(boolean like) {
                        //if (getContext() == null) return;
                        llFavIcon.setEnabled(true);
                        article.getInfo().setLiked(like);
                        int counter = article.getInfo().getLike_count();
                        if (like) {
                            counter++;
                        } else {
                            if (counter > 0) {
                                counter--;
                            } else {
                                counter = 0;
                            }
                        }
                        article.getInfo().setLike_count(counter);
                        favCount.setText("" + counter);

                        if (getActivity() == null) return;
                        if (article.getInfo().isLiked()) {
                            favIcon.setImageResource(R.drawable.ic_reel_like_active);
                            favCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.theme_color_1));
                            DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.theme_color_1));
                        } else {
                            favIcon.setImageResource(R.drawable.ic_reel_like_inactive);
                            favCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.greyad));
                            DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.greyad));
                        }
                    }

                    @Override
                    public void failure() {
                        llFavIcon.setEnabled(true);
                    }
                }, !article.getInfo().isLiked());
        });

        share.setOnClickListener(view1 -> share());

        viewFullArticle.setOnClickListener(v -> {
            if (getActivity() != null
                    && article != null
                    && !TextUtils.isEmpty(article.getOriginalLink())
            ) {

                ArrayList<ResolveInfo> customTabsPackages = getCustomTabsPackages(getActivity());
                String link = "";
//                if (article.getType().toLowerCase().contains("youtube")) {
//                    link = "https://www.youtube.com/watch?v=" + article.getOriginalLink();
//                } else {
                link = article.getOriginalLink();
//                }
                if (isCustomTabSupported(getActivity(), Uri.parse(link))
                        && customTabsPackages.size() > 0) {
                    try {
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        CustomTabsIntent customTabsIntent = builder
                                .setShowTitle(true)
                                .build();
                        if (customTabsIntent != null) {
                            Log.d("TAGlink", "link = " + link);
                            Log.d("TAGlink", "Type = " + article.getType());
                            customTabsIntent.launchUrl(getActivity(), Uri.parse(link));
                        }
                    } catch (ActivityNotFoundException exception) {
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        if (article.getSource() != null && !TextUtils.isEmpty(article.getSource().getName()))
                            intent.putExtra("title", article.getSource().getName());
                        intent.putExtra("url", link);
                        getActivity().startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    if (article.getSource() != null && !TextUtils.isEmpty(article.getSource().getName()))
                        intent.putExtra("title", article.getSource().getName());
                    intent.putExtra("url", link);
                    getActivity().startActivity(intent);
                }
            }
        });

        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (article != null) {
                    if (article.getType().equalsIgnoreCase("YOUTUBE")) {
//                        if (mYouTubePlayer != null) {
//                            mYouTubePlayer.play();
//                            playImage.setVisibility(View.GONE);
//                            youtubeView.setVisibility(View.VISIBLE);
//                        }
                    } /*else if (article.getType().equalsIgnoreCase("VIDEO")) {
                        if (simpleExoPlayer != null) {
                            playImage.setVisibility(View.GONE);
                            videoPlayer.setVisibility(View.VISIBLE);
                            playPlayer(true);
                        }
                    }*/
                }
            }
        });

        sourceContainer.setOnClickListener(v -> sourceClicked());

        speaker.setOnClickListener(v -> {
            try {
                if (!Constants.muted) {
                    Constants.muted = true;
                    //Constants.ReelMute = true;
                    if (simpleExoPlayer != null) {
                        simpleExoPlayer.setVolume(0f);
                    } else if (mYouTubePlayer != null) {
                        mYouTubePlayer.mute();
                        //mYouTubePlayer.setVolume(0);
                    }
                    if (articleId != null) {
                        Map<String, String> params = new HashMap<>();
                        params.put(Events.KEYS.ARTICLE_ID, articleId);
                        AnalyticsEvents.INSTANCE.logEvent(getContext(),
                                params,
                                Events.MUTE_VIDEO_FEED);
                    }

                } else {
                    Constants.muted = false;
                    //Constants.ReelMute = false;
                    if (simpleExoPlayer != null) {
                        simpleExoPlayer.setVolume(1f);
                    } else if (mYouTubePlayer != null) {
                        mYouTubePlayer.unMute();
                        //mYouTubePlayer.setVolume(1f);
                    }
                    if (articleId != null) {
                        Map<String, String> params = new HashMap<>();
                        params.put(Events.KEYS.ARTICLE_ID, articleId);
                        AnalyticsEvents.INSTANCE.logEvent(getContext(),
                                params,
                                Events.UNMUTE);
                    }
                }
                updateMuteButtons();
            } catch (Exception ignore) {
            }
        });

        back.setOnClickListener(view12 -> {
            if (getActivity() == null) return;
//            Intent intent = new Intent();
//            intent.putExtra("id", article.getId());
//            intent.putExtra("position", "");
//            getActivity().setResult(RESULT_OK, intent);
            getActivity().onBackPressed();
        });

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (article != null && !TextUtils.isEmpty(article.getType()) && article.getType().equalsIgnoreCase("VIDEO")) {
                    showFullScreenVideo(0);
                } else*/
                if (article != null && !TextUtils.isEmpty(article.getType())) {
                    Intent intent = new Intent(getActivity(), VideoFullScreenActivity.class);
                    intent.putExtra("url", article.getLink());
                    intent.putExtra("position", 0);
                    /*if (article.getType().equalsIgnoreCase("VIDEO")) {
                        mDuration = simpleExoPlayer.getContentPosition();
                        intent.putExtra("mode", "video");
                    } else */
                    if (article.getType().equalsIgnoreCase("YOUTUBE")) {
                        intent.putExtra("mode", "youtube");
                    }
                    intent.putExtra("duration", mDuration);
                    startActivityForResult(intent, Constants.VideoDurationRequestCode);
                }
            }
        });

        authorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && article != null && article.getAuthor() != null && article.getAuthor().size() > 0) {
                    Utils.openAuthor(getActivity(), article.getAuthor().get(0));
                }
            }
        });


        SimpleOrientationListener mOrientationListener = new SimpleOrientationListener(getContext()) {

            @Override
            public void onSimpleOrientationChanged(int orientation) {

//                if(orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270){
//                    showFullScreenVideo(orientation);
//                }else{
//                    if(popUpClass != null && popUpClass.isShowing()){
//                        popUpClass.dismiss();
//                    }
//                }
            }
        };
        mOrientationListener.enable();


        return view;
    }

    private void showBottomSheetDialog(ShareInfo shareInfo, Article
            article, DialogInterface.OnDismissListener onDismissListener) {

        ShareBottomSheet shareBottomSheet = new ShareBottomSheet(getActivity(), new ShareToMainInterface() {
            @Override
            public void removeItem(String id, int position) {

            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {

            }

            @Override
            public void unarchived() {

            }
        }, true, "ARTICLES");
        shareBottomSheet.show(article, onDismissListener, shareInfo);

    }

    private void share() {
        if (!InternetCheckHelper.isConnected()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (getContext() == null) return;
        if (article == null) return;

        loaderShow(true);
        if (shareBottomSheetPresenter != null) {
            shareBottomSheetPresenter.share_msg(article.getId(), new ShareInfoInterface() {
                @Override
                public void response(ShareInfo shareInfo) {
                    loaderShow(false);
                    if (shareInfo == null) {
                        return;
                    }

                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
                            Events.REEL_SHARE_CLICK);

                    Article article = new Article();
                    if (article != null) {
                        MediaMeta mediaMeta = article.getMediaMeta();
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

                }

                @Override
                public void error(String error) {

                }
            });
        }
    }

    public void showFullScreenVideo(int mOrientation) {

        if (!popUpClass.isShowing() && videoPlayer != null) {
            popUpClass.showPopupWindow(fullscreen, videoPlayer, getActivity(), mOrientation);
        }

    }

    public void updateMuteButtons() {
        if (!Constants.muted) {
            speaker.setImageResource(R.drawable.ic_speaker_unmute);
        } else {
            speaker.setImageResource(R.drawable.ic_speaker_mute);
        }
    }

    private void showLoaderInActivity(boolean show) {
//        if (getActivity() != null) {
//            ShowOptionsLoaderCallback showOptionsLoaderCallback = (ShowOptionsLoaderCallback) getActivity();
//            if (showOptionsLoaderCallback != null) {
//                showOptionsLoaderCallback.showLoader(show);
//            }
//        }
    }

    /**
     * loading dialog
     */
    protected void showProgressDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new PictureLoadingDialog(getContext());
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissProgressDialog() {
        try {
            if (mLoadingDialog != null
                    && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            mLoadingDialog = null;
            e.printStackTrace();
        }
    }

    private void sourceClicked() {
        if (getActivity() != null) {

            if (article.getSource() != null) {
                ((BulletDetailCallback) getActivity()).onChannelItemClicked(
                        TYPE.SOURCE,
                        article.getSource().getId(),
                        article.getSource().getName(),
                        article.getSource().isFavorite()
                );
            } else if (article.getAuthor() != null && article.getAuthor().size() > 0) {
                if (article.getAuthor().get(0) != null) {
                    Utils.openAuthor(getActivity(), article.getAuthor().get(0));
                }
            }
        }
    }

    private void initializePlayer(String videoLink) {

//        if (videoLink.endsWith(".m3u8")) {
//            //for HLS
//            TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory();
//            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(),
//                    new DefaultTrackSelector(adaptiveTrackSelection));
//
//            DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
//            com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
//                    Util.getUserAgent(getActivity(), "Exo2"), defaultBandwidthMeter);
//
////            String hls_url = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8";
//            Uri uri = Uri.parse(videoLink);
//            Handler mainHandler = new Handler();
//            MediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
//
//            simpleExoPlayer.prepare(mediaSource);
//        } else {
//            TrackSelector trackSelectorDef = new DefaultTrackSelector();
//            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelectorDef);
//
//            String userAgent = getString(R.string.app_name);
//            DefaultDataSourceFactory defDataSourceFactory = new DefaultDataSourceFactory(getContext(), userAgent);
//            Uri uriOfContentUrl = Uri.parse(videoLink);
//            MediaSource mediaSource = new ProgressiveMediaSource.Factory(defDataSourceFactory).createMediaSource(uriOfContentUrl);
//
//            simpleExoPlayer.prepare(mediaSource);
//        }
//        videoPlayer.setPlayer(simpleExoPlayer);
//        simpleExoPlayer.addListener(new Player.EventListener() {
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                if (playbackState == Player.STATE_ENDED) {
//                    if (videoPlayer != null && videoPlayer.getPlayer() != null) {
//                        videoPlayer.getPlayer().seekTo(0);
//                        videoPlayer.getPlayer().setPlayWhenReady(false);
//                    }
//                    playImage.setVisibility(View.VISIBLE);
//                    videoPlayer.setVisibility(View.GONE);
//                }
//            }
//        });
//        playPlayer(prefConfig.isVideoAutoPlay() && isSelected);
    }

    private void initYouTube() {
        getLifecycle().addObserver(youtubeView);

        youtubeView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float duration) {
                super.onVideoDuration(youTubePlayer, duration);
                mDuration = (long) duration;
            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float second) {
                super.onCurrentSecond(youTubePlayer, second);
                mDuration = (long) second;
            }

            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                mYouTubePlayer = youTubePlayer;
                try {
                    if (Constants.muted) {
                        mYouTubePlayer.mute();
                    } else {
                        mYouTubePlayer.unMute();
                    }
                    updateMuteButtons();
                } catch (Exception ignore) {
                }
                youTubePlayer.loadVideo(article.getLink(), 0);
                if (!prefConfig.isVideoAutoPlay() || !isSelected) {
                    youTubePlayer.pause();
                }
            }
        });
    }

    public void pause() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }

        if (mYouTubePlayer != null) {
            mYouTubePlayer.pause();
        }
    }

    private void pausePlayer(SimpleExoPlayer player) {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    private void playPlayer(boolean flag) {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(flag);

            if (Constants.muted) {
                simpleExoPlayer.setVolume(0f);
                if (articleId != null) {
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, articleId);
                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
                            params,
                            Events.MUTE_VIDEO_FEED);
                }
            } else {
                simpleExoPlayer.setVolume(1f);
                if (articleId != null) {
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, articleId);
                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
                            params,
                            Events.UNMUTE);
                }
            }
            updateMuteButtons();
        }
    }

    private void stopPlayer() {
        videoPlayer.setPlayer(null);
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    private void seekTo(SimpleExoPlayer player, long positionInMS) {
        if (player != null) {
            player.seekTo(positionInMS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayer();
    }

    private void bind(View view) {
        share = view.findViewById(R.id.share);
        comment = view.findViewById(R.id.comment);
        commentCount = view.findViewById(R.id.commentCount);
        follow = view.findViewById(R.id.cl_follow);
        follow_txt = view.findViewById(R.id.follow_txt);
        followBottomBar = view.findViewById(R.id.follow_bottom_bar);
        follow_progress = view.findViewById(R.id.follow_progress);
        favIcon = view.findViewById(R.id.favIcon);
        llFavIcon = view.findViewById(R.id.ll_favorite);
        favCount = view.findViewById(R.id.favCount);
        postImage = view.findViewById(R.id.post_image);
        articleTitle = view.findViewById(R.id.article_title);
        bulletContainer = view.findViewById(R.id.bullet_container);
        sourceContainer = view.findViewById(R.id.source_container);
        sourceName = view.findViewById(R.id.source_name);
        sourceImage = view.findViewById(R.id.source_image);
        postTime = view.findViewById(R.id.post_time);
        viewCount = view.findViewById(R.id.view_count);
        viewFullArticle = view.findViewById(R.id.view_full_article);

        playImage = view.findViewById(R.id.play_image);
        videoPlayer = view.findViewById(R.id.video_player);
        youtubeView = view.findViewById(R.id.youtube_view);
        playerContainer = view.findViewById(R.id.post_display_container);
        speaker = view.findViewById(R.id.speaker);
        back = view.findViewById(R.id.bullet_detail_img_left_arrow);
        fullscreen = view.findViewById(R.id.fullscreen);
        buttonPanel = view.findViewById(R.id.buttonPanel);
        authorName = view.findViewById(R.id.author_name);
    }

    private void loadData() throws Exception {
        if (article != null) {
            showLoaderInActivity(false);
            articleId = article.getId();
//            if (!TextUtils.isEmpty(article.getType()) && article.getType().equalsIgnoreCase("VIDEO")) {
//                String link = article.getImage() != null ? article.getImage() : article.getLink();
//                Glide.with(postImage)
//                        .load(link)
//                        .into(postImage);
//            } else {
//                Glide.with(postImage)
//                        .load(article.getImage())
//                        .into(postImage);
//            }
            Glide.with(postImage)
                    .load(article.getImage())
                    .into(postImage);

            try {
                updateFollowColor(article.getSource().isFavorite());
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (article.getSource().isFavorite()) {
//                follow_txt.setText(getActivity().getString(R.string.following));
//                follow_txt.setTextColor(ContextCompat.getColor(requireContext(), R.color.following_text_color));
//                follow_txt.setCompoundDrawables(null, null, null, null);
//            } else {
//                follow_txt.setText(getActivity().getString(R.string.follow));
//                follow_txt.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryRed));
//                follow_txt.setCompoundDrawables(ContextCompat.getDrawable(requireContext(), R.drawable.ic_plus), null, null, null);
//                DrawableCompat.setTint(follow_txt.getCompoundDrawables()[0], ContextCompat.getColor(requireContext(), R.color.primaryRed));
//            }

            if (article.getInfo().isLiked()) {
                favIcon.setImageResource(R.drawable.ic_reel_like_active);
                favCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.theme_color_1));
                DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.theme_color_1));
            } else {
                favCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.greyad));
                favIcon.setImageResource(R.drawable.ic_reel_like_inactive);
                DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.greyad));
            }
            if (article.getInfo() != null)
                favCount.setText("" + article.getInfo().getLike_count());
            youtubeView.getPlayerUiController().enableLiveVideoUi(article.getBullets() != null && article.getBullets().size() > 0 && article.getBullets().get(0).getDuration() == 0);

            Glide.with(sourceImage)
                    .load(article.getSourceImageToDisplay())
                    .into(sourceImage);
//            authorName.setText(article.getSourceNameToDisplay());
//            if (!article.getAuthorNameToDisplay().equals("")) {
//                authorName.setVisibility(View.VISIBLE);
//                // separatorDot.setVisibility(View.VISIBLE);
//                authorName.setText(article.getAuthorNameToDisplay());
//            } else {
//                authorName.setVisibility(View.GONE);
//                // separatorDot.setVisibility(View.GONE);
//            }
            sourceName.setText(article.getSourceNameToDisplay());

            if (Utils.getLanguageDirectionForView(article.getLanguageCode()) == View.TEXT_DIRECTION_LTR) {
                articleTitle.setPadding(
                        0,
                        getResources().getDimensionPixelOffset(R.dimen._10sdp),
                        getResources().getDimensionPixelOffset(R.dimen._35sdp),
                        0
                );
            } else {
                articleTitle.setPadding(
                        getResources().getDimensionPixelOffset(R.dimen._35sdp),
                        getResources().getDimensionPixelOffset(R.dimen._10sdp),
                        0,
                        0
                );
            }
            articleTitle.setTextDirection(Utils.getLanguageDirectionForView(article.getLanguageCode()));

            articleTitle.setText(article.getTitle());
            authorName.setText("By " + article.getSourceNameToDisplay());
            float val = Utils.getHeadlineDimens(prefConfig, getActivity());
            if (val != -1) {
                articleTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
            }


            String time = Utils.getTimeAgo(Utils.getDate(article.getPublishTime()), getContext());
            postTime.setText(time);
            if (article.getInfo() != null) {
                //viewCount.setText(article.getInfo().getViewCount());
                if (!article.getInfo().getViewCount().equals("1")) {
                    viewCount.setText(getString(R.string.view_counts, article.getInfo().getViewCount()));
                } else {
                    viewCount.setText(getString(R.string.view_count, article.getInfo().getViewCount()));
                }
            }

            if (article.getType().equalsIgnoreCase("YOUTUBE")) {
                loadYoutubeVideo();
                initYouTube();
                buttonPanel.setVisibility(View.VISIBLE);
            } /*else if (article.getType().equalsIgnoreCase("VIDEO")) {
                loadVideo();
                initializePlayer(article.getLink());
                buttonPanel.setVisibility(View.VISIBLE);
            } */ else {
                loadImagePost();
                addBullets();
                buttonPanel.setVisibility(View.GONE);
            }

        }
    }

    private void updateFollowColor(boolean isFavorite) throws IllegalStateException {
        if (isFavorite) {
//            if(getActivity() !=null){
//                follow_txt.setText(getActivity().getString(R.string.following));
//            }
//            follow_txt.setText(R.string.following);
            follow_txt.setText(requireContext().getResources().getString(R.string.following));
            follow_txt.setTextColor(ContextCompat.getColor(requireContext(), R.color.following_text_color));
            follow_txt.setCompoundDrawables(null, null, null, null);
            DrawableCompat.setTint(followBottomBar.getBackground(), ContextCompat.getColor(requireContext(), R.color.following_text_color));
//            followBottomBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.following_text_color));
        } else {
//            follow_txt.setText(R.string.follow);
            follow_txt.setText(requireContext().getResources().getString(R.string.follow));
//            follow_txt.setText(getActivity().getString(R.string.follow));
            follow_txt.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryRed));
            DrawableCompat.setTint(followBottomBar.getBackground(), ContextCompat.getColor(requireContext(), R.color.theme_color_1));
//            followBottomBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryRed));
            follow_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus, 0, 0, 0);
            for (Drawable drawable : follow_txt.getCompoundDrawables()) {
                if (drawable != null) {
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(requireContext(), R.color.primaryRed));
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CommentsRequestCode) {
            if (data != null) {
                if (resultCode == RESULT_OK) {
                    if (presenter != null && data.hasExtra("id")) {
                        String id = data.getStringExtra("id");
                        if (!TextUtils.isEmpty(id)) {
                            presenter.counters(id, info -> {
                                Utils.checkLikeView(getContext(),
                                        info.getLike_count(),
                                        info.getComment_count(),
                                        commentCount,
                                        favCount,
                                        favIcon,
                                        info.isLiked()
                                );
                            });
                        }
                    }

                }
            }
        } else if (data != null) {
            if (resultCode == RESULT_OK) {
                if (requestCode == Constants.VideoDurationRequestCode) {
                    int position = data.getIntExtra("position", -1);
                    long duration = data.getLongExtra("duration", 0);
                    if (position != -1 && duration > 0) {
                        try {
                            mDuration = duration;
                            if (mYouTubePlayer != null) {
                                mYouTubePlayer.seekTo(duration);
                                mYouTubePlayer.play();
                            }
                            if (simpleExoPlayer != null) {
                                simpleExoPlayer.seekTo(duration);
                                simpleExoPlayer.setPlayWhenReady(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void loadVideo() {
        if (prefConfig.isVideoAutoPlay()) {
            playImage.setVisibility(View.GONE);
            videoPlayer.setVisibility(View.VISIBLE);
        } else {
            playImage.setVisibility(View.VISIBLE);
            videoPlayer.setVisibility(View.GONE);
        }
        youtubeView.setVisibility(View.GONE);
        if (article != null && !TextUtils.isEmpty(article.getOriginalLink()))
            viewFullArticle.setVisibility(View.VISIBLE);
        else
            viewFullArticle.setVisibility(View.GONE);
        if (article.getMediaMeta() != null
                && article.getMediaMeta().getHeight() > 0 &&
                article.getMediaMeta().getWidth() > 0
        ) {
            resizeVideoView(playerContainer, article.getMediaMeta().getHeight(), article.getMediaMeta().getWidth());
        }
    }

    private void loadYoutubeVideo() {
        playImage.setVisibility(View.GONE);
        videoPlayer.setVisibility(View.GONE);
        youtubeView.setVisibility(View.VISIBLE);
        if (article != null && !TextUtils.isEmpty(article.getOriginalLink()))
            viewFullArticle.setVisibility(View.VISIBLE);
        else
            viewFullArticle.setVisibility(View.GONE);
    }

    private void loadImagePost() {
        playImage.setVisibility(View.GONE);
        videoPlayer.setVisibility(View.GONE);
        youtubeView.setVisibility(View.GONE);
        if (article != null && !TextUtils.isEmpty(article.getOriginalLink()))
            viewFullArticle.setVisibility(View.VISIBLE);
        else
            viewFullArticle.setVisibility(View.GONE);
    }

    private void addBullets() {
        bulletContainer.removeAllViews();
        int i = 0;
        for (Bullet bullet : article.getBullets()) {
            // checking first bullet same as title
            if (i != 0 || !(bullet.getData().trim().equals(article.getTitle().trim()) || bullet.getData().trim().equals(article.getTitle().trim() + "."))) {
//                if (article.getSource() != null) {
                bulletContainer.addView(createBullet(bullet, article.getLanguageCode()));
//                } else {
//                    bulletContainer.addView(createBullet(bullet, ""));
//                }
            }
            i++;
        }
    }

    private View createBullet(Bullet bullet, String langCode) {
        LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if (Utils.getLanguageDirectionForView(langCode) == View.TEXT_DIRECTION_LTR) {
            v = vi.inflate(R.layout.detail_bullet_item, null);
        } else {
            v = vi.inflate(R.layout.detail_bullet_item_rtl, null);
        }
        TextView bulletText = v.findViewById(R.id.bullet_text);
        bulletText.setText(bullet.getData());
        bulletText.setTextDirection(Utils.getLanguageDirectionForView(article.getLanguageCode()));
        float val = Utils.getBulletDimens(prefConfig, getActivity());
        if (val != -1) {
            bulletText.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
        }
        return v;
    }

    @Override
    public void loaderShow(boolean flag) {
        showLoaderInActivity(flag);
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void error404(String error) {

    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {

    }

    @Override
    public void successArticle(Article articleLoaded) {
        article = articleLoaded;
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void homeSuccess(HomeResponse homeResponse, String currentPage) {

    }

    @Override
    public void nextPosition(int position) {

    }

    @Override
    public void nextPositionNoScroll(int position, boolean shouldNotify) {

    }

    @Override
    public void onItemHeightMeasured(int height) {

    }

    @Override
    public void removeItem(String id, int position) {

    }

    @Override
    public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
        if (getActivity() != null && getActivity() instanceof BulletDetailActivity) {
            ((BulletDetailActivity) getActivity()).onGotoChannelListener.onItemClicked(type, id, name, favorite);
        }
    }

    @Override
    public void unarchived() {

    }


}