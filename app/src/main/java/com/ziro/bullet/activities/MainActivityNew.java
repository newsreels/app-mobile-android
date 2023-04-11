package com.ziro.bullet.activities;

import static com.ziro.bullet.background.BroadcastEmitter.BG_ERROR;
import static com.ziro.bullet.background.BroadcastEmitter.BG_PUBLISHED;
import static com.ziro.bullet.background.BroadcastEmitter.TYPE_BACKGROUND_PROCESS;
import static com.ziro.bullet.utills.Constants.BOTTOM_TAB_VIDEO;
import static com.ziro.bullet.utills.Constants.isHomeClicked;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.background.BackgroundEvent;
import com.ziro.bullet.background.UploadInfo;
import com.ziro.bullet.background.VideoProcessorService;
import com.ziro.bullet.bottomSheet.FeatureAlertBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.following.ui.FollowingFragment;
import com.ziro.bullet.fragments.CommunityFeedFragmentMain;
import com.ziro.bullet.fragments.DetailFragment;
import com.ziro.bullet.fragments.DiscoverFragmentNew;
import com.ziro.bullet.fragments.Profile.SettingsFragment;
import com.ziro.bullet.fragments.ProfileFragment;
import com.ziro.bullet.fragments.Reels.ReelFragment;
import com.ziro.bullet.fragments.SearchModifiedFragment;
import com.ziro.bullet.fragments.TempHomeFragment;
import com.ziro.bullet.fragments.searchNew.SearchResultFragment;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.MainInterface;
import com.ziro.bullet.interfaces.UserConfigCallback;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.MainPresenter;
import com.ziro.bullet.presenter.UserConfigPresenter;
import com.ziro.bullet.texttospeech.TextToAudioPlayerHelper;
import com.ziro.bullet.utills.ClearGlideCacheAsyncTask;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.NetworkUtil;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivityNew extends BaseActivity implements TempHomeFragment.OnHomeFragmentInteractionListener, DetailFragment.OnHomeFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, SearchModifiedFragment.OnFragmentInteractionListener, CommunityFeedFragmentMain.OnCommunityFragmentInteractionListener, GoHome {
    public static final int RESULT_INTENT_ADD_LOCATION = 3781;
    public static final int RESULT_INTENT_CHANGE_LANGUAGE = 3891;
    public static final int RESULT_INTENT_CHANGE_EDITION = 3111;
    public static final int RESULT_INTENT_CHANGE_THEME = 5424;
    private static final String TAG = "MainActivity_TAG";
    private static final int RESULT_INTENT_SEARCH = 3421;
    private static final int RESULT_INTENT_SETTING_CHANGES = 3211;
    private final String FRAGMENT_TAG_HOME = "1";
    private final String FRAGMENT_TAG_PROFILE = "5";
    private final String FRAGMENT_TAG_FOLLOWING = "11";
    private final String FRAGMENT_TAG_SEARCH = "4";
    private final String FRAGMENT_TAG_VIDEO = "8";
    private final String FRAGMENT_TAG_COMMUNITY = "9";
    private final long mLastClickTime = 0;
    public BottomNavigationView bottomNavigationView;
    private PrefConfig prefConfig;
    private boolean ifOffline = false;
    private View overlay;
    private Snackbar snackbar;
    private RelativeLayout mRoot;
    private TempHomeFragment homeFragment;
    private SearchModifiedFragment searchFragment;
    private DiscoverFragmentNew discoverFragment_new;
    private SearchResultFragment searchResultNew;
    private FollowingFragment followingFragment;
    private CommunityFeedFragmentMain communityFeedFragment;
    private ReelFragment reelFragment;
    private SettingsFragment menuFragment;
    private LinearLayout noRecordFoundContainer;
    private Fragment active, last;
    private FragmentManager fm;
    private MyBroadcastReceiver broadcastReceiver;
    private UserConfigPresenter userConfigPresenter;
    private MainPresenter mainPresenter;
    private TextToAudioPlayerHelper textToAudio;
    private AudioCallback audioCallback;
    private boolean isAppFirstTimeOpen = false;
    private boolean loadingReelFirst;
    //for header follow/unfollow
    private boolean isSearchNavigate = false;

    @Override
    public void sendAudioEvent(String event) {
        Log.e("ACTION-", "ACTION : " + event);
        if (textToAudio != null && !TextUtils.isEmpty(event)) {
            switch (event) {
                case "pause":
                    Log.d("audiotest", "sendAudioEvent: pause");
                    textToAudio.pause();
                    break;
                case "resume":
                    Log.d("audiotest", "sendAudioEvent: resume");
                    if (Constants.canAudioPlay) {
                        if (!Constants.muted) {
                            textToAudio.resume();
                        }
                    }
                    break;
                case "stop_destroy":
                    Log.d("audiotest", "sendAudioEvent: stop_destroy");
                    textToAudio.stop();
                    textToAudio.destroy();
                    break;
                case "stop":
                    Log.d("audiotest", "sendAudioEvent: stop");
                    textToAudio.stop();
                    break;
                case "destroy":
                    Log.d("audiotest", "sendAudioEvent: destroy");
                    textToAudio.destroy();
                    break;
                case "mute":
                    Log.d("audiotest", "sendAudioEvent: mute");
                    textToAudio.mute();
                    textToAudio.stop();
                    textToAudio.destroy();
                    break;
                case "unmute":
                    textToAudio.unmute();
                    break;
                case "isSpeaking":
                    Log.d("audiotest", "sendAudioEvent: isSpeaking");
                    if (!textToAudio.isSpeaking()) {
                        if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(Constants.speech)) {
                            sendAudioToTempHome(audioCallback, "isSpeaking", "", new AudioObject(Constants.articleId, Constants.speech, Constants.url, Constants.bullet_position, Constants.bullet_duration));
                        }
                    }
                    break;
                case "play":
                    Log.d("audiotest", "sendAudioEvent: play");
                    textToAudio.stop();
                    if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(Constants.speech)) {
                        sendAudioToTempHome(audioCallback, "play", "", new AudioObject(Constants.articleId, Constants.speech, Constants.url, Constants.bullet_position, Constants.bullet_duration));
                    }
                    break;
                case "homeTab":
                    if (bottomNavigationView != null)
                        bottomNavigationView.setSelectedItemId(R.id.action_home);
                    break;
                case "search":
                    if (bottomNavigationView != null)
                        bottomNavigationView.setSelectedItemId(R.id.action_search);
                    if (getSearchFragment() != null && getSearchFragment().isAdded()) {
//                        getSearchFragment().focus();
                    }
                    break;
            }
        }
    }

    @Override
    public void sendAudioToTempHome(AudioCallback audioCallback, String fragTag, String status, AudioObject audio) {
        this.audioCallback = audioCallback;
        if (audio != null) {
            Log.e("sendAudioToTemp", "=================HOME===================");
            Log.e("sendAudioToTemp", "fragTag : " + fragTag);
            Log.e("sendAudioToTemp", "speech : " + audio.getText());
            Log.e("sendAudioToTemp", "speech : " + audio.getId());
            Log.e("sendAudioToTemp", "bullet_position : " + audio.getIndex());
            if (Constants.canAudioPlay) {
                if (!Constants.muted) {
                    if (textToAudio != null) {
                        textToAudio.stop();
//                    textToAudio.play(articleId, bullet_position, speech);
                        textToAudio.isPlaying(audio, audioCallback);
                    }
                }
            }
        }
    }

    @Override
    public void scrollUp() {
        hideBottomBar();
    }

    @Override
    public void scrollDown() {
        showBottomBar();
    }

    private void hideBottomBar() {
        ObjectAnimator animationNav = ObjectAnimator.ofFloat(bottomNavigationView, View.TRANSLATION_Y, bottomNavigationView.getHeight());
        animationNav.setInterpolator(new AccelerateInterpolator(2));

        ObjectAnimator animationOverlay = ObjectAnimator.ofFloat(overlay, View.TRANSLATION_Y, overlay.getHeight());
        animationOverlay.setInterpolator(new AccelerateInterpolator(2));

        AnimatorSet set = new AnimatorSet();
        set.play(animationNav).with(animationOverlay);
        set.start();
    }

    private void showBottomBar() {
        ObjectAnimator animationNav = ObjectAnimator.ofFloat(bottomNavigationView, View.TRANSLATION_Y, 0);
        animationNav.setInterpolator(new DecelerateInterpolator(2));

        ObjectAnimator animationOverlay = ObjectAnimator.ofFloat(overlay, View.TRANSLATION_Y, 0);
        animationOverlay.setInterpolator(new DecelerateInterpolator(2));

        AnimatorSet set = new AnimatorSet();
        set.play(animationNav).with(animationOverlay);
        set.start();
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.TYPE_COUNT_API_CALL) {
            if (mainPresenter != null) {
                mainPresenter.articleViewCountUpdate(event.getStringData());
            }

        }
//        else if (event.getType() == MessageEvent.TYPE_SHOW_DETAIL_VIEW) {
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                return;
//            }
//            mLastClickTime = SystemClock.elapsedRealtime();
//            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
//
//                Intent intent = new Intent(MainActivityNew.this, BulletDetailActivity.class);
//                intent.putExtra("article", (Article) event.getObjectData());
//                startActivity(intent);
//            }
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BackgroundEvent event) {
        if (event.getType() == TYPE_BACKGROUND_PROCESS) {
            if (event.getData().equals(BG_PUBLISHED)) {
                Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.posted_successfully));
            } else if (event.getData().equals(BG_ERROR)) {
                Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.post_failed));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (prefConfig != null) {
            prefConfig.setBgTime(System.currentTimeMillis());
        }


        if (active instanceof ReelFragment && Constants.HomeSelectedFragment == BOTTOM_TAB_VIDEO) {
            Log.e("rvm3", "onPause:MA ");
            ((ReelFragment) active).reelpause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        showRatingsPopup();

        if (Constants.HomeSelectedFragment != BOTTOM_TAB_VIDEO) {
            Constants.onResumeReels = false;
        }
//        if (active instanceof ReelFragment && Constants.HomeSelectedFragment == BOTTOM_TAB_VIDEO) {
//            Log.e("rvm3", "onResume:MA ");
//            Constants.notishare = false;
//            if (!Constants.rvmdailogopen){
//                Constants.reelfragment=true;
//                ((ReelFragment) active).resume();
//            }

//        }
        Log.d(TAG, "onResume: ");
        if (active instanceof ReelFragment && Constants.HomeSelectedFragment == BOTTOM_TAB_VIDEO && !Constants.rvmdailogopen) {
            Constants.onResumeReels = true;
            new Components().statusBarColor(MainActivityNew.this, "black");
            staticDarkColorBottomTabs();
        } else if (active instanceof TempHomeFragment && Constants.HomeSelectedFragment == Constants.BOTTOM_TAB_HOME) {
            Constants.onResumeReels = false;
            new Components().statusBarColor(this, "white");
            dynamicColorBottomTabs();
        }/* else {
            staticDarkColorBottomTabs();
        }*/

        // refresh profile after login
//        if (prefConfig != null && prefConfig.isProfileChange()) {
//            MessageEvent messageEvent = new MessageEvent();
//            messageEvent.setType(MessageEvent.TYPE_USER_LOGGED_IN);
//
//            EventBus.getDefault().post(messageEvent);
//            prefConfig.setProfileChange(false);
//        }
        //shifa test 2 api calls take place because of this
//        if (prefConfig != null) {
//            long appBgTime = prefConfig.getBgTime();
//            if (Math.abs(System.currentTimeMillis() - appBgTime) > TimeUnit.MINUTES.toMillis(Constants.refreshTime)) {
//                prefConfig.setBgTime(System.currentTimeMillis());
//                Log.e("BGtimRE", "appBgTime is older than 2 minutes");
//                Constants.reelDataUpdate = true;
//                Constants.homeDataUpdate = true;
//                Constants.menuDataUpdate = true;
//                reloadFragments();
//            } else {
//                Log.e("BGtimRE", "appBgTime is not more than 2 minutes");
//            }
//        } else {
//            isAppFirstTimeOpen = false;
//        }

//        showBottomBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TransformationCompat.onTransformationStartContainer(this);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementsUseOverlay(false);

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: savedInstanceState = " + savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.content);

        bindView();

        textToAudio = new TextToAudioPlayerHelper(this);
        broadcastReceiver = new MyBroadcastReceiver();
        fm = getSupportFragmentManager();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        isAppFirstTimeOpen = true;
        prefConfig = new PrefConfig(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !prefConfig.hasAskedNotificationPermission()) {
            prefConfig.setHasAskedNotificationPermission(true);
            checkNotificationPermission();
        }

        userConfigPresenter = new UserConfigPresenter(this, new UserConfigCallback() {
            @Override
            public void loaderShow(boolean flag) {

            }

            @Override
            public void error(String error) {
                showNoDataErrorView(true);
            }

            @Override
            public void error404(String error) {
                showNoDataErrorView(true);
            }

            @Override
            public void onUserConfigSuccess(UserConfigModel userConfigModel) {
                showNoDataErrorView(false);
                if (userConfigModel != null) {

                    //check profile setup is complete
                    if (userConfigModel.getUser() != null && !TextUtils.isEmpty(userConfigModel.getUser().getFirst_name())) {
                        if (userConfigModel.isOnboarded()) {
                            if (userConfigModel.getAlert() != null && !TextUtils.isEmpty(userConfigModel.getAlert().getId())) {
                                FeatureAlertBottomSheet featureAlertBottomSheet = FeatureAlertBottomSheet.newInstance(userConfigModel.getAlert().getId(), userConfigModel.getAlert().getTitle(), userConfigModel.getAlert().getMessage(), userConfigModel.getAlert().getImage());
                                featureAlertBottomSheet.setCancelable(false);
                                featureAlertBottomSheet.show(fm, "dialog");
                            }

                            updateWidget();
                            if (getIntent().hasExtra("prefs")) {
                                if (reelFragment != null) {
                                    Utils.showPopupMessageWithCloseButton(MainActivityNew.this, 7000, getString(R.string.thanks_success), false);
//                                    reelFragment.pause();
//                                    ReelTutorialPopup popup = new ReelTutorialPopup(MainActivityNew.this);
//                                    popup.showDialog(view -> {
//                                        reelFragment.resume();
//                                        Utils.showPopupMessageWithCloseButton(MainActivityNew.this, 7000, getString(R.string.thanks_success), false);
//                                    });
                                }
                            }
                        } /*else {
//                            OnBoardingBottomSheet onBoardingBottomSheet = new OnBoardingBottomSheet();
//                            onBoardingBottomSheet.updateFragManager(getSupportFragmentManager());
//                            onBoardingBottomSheet.show();
//                            Intent intent = new Intent(MainActivityNew.this, OnBoardingActivity.class);
//                            finish();
//                            intent.putExtra("main", "main");
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }*/
                    } /*else {

                        //is not guest user
//                        if (userConfigModel.getUser() != null && userConfigModel.getUser().isGuestValid()) {
//                            Intent intent1 = new Intent(MainActivityNew.this, ProfileNameActivity.class);
//                            intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                            finish();
//                            startActivity(intent1);
//                            overridePendingTransition(R.anim.enter, R.anim.exit);
//                        }
                    }*/
                }
            }
        });

        mainPresenter = new MainPresenter(this, new MainInterface() {

            @Override
            public void loaderShow(boolean flag) {

            }

            @Override
            public void error(String error, int load) {

            }

            @Override
            public void error404(String error) {

            }

            @Override
            public void success(ArticleResponse response, int load, int category, String topic) {

            }

            @Override
            public void success(CategoryResponse response) {

            }

            @Override
            public void UserTopicSuccess(ArrayList<Topics> response) {

            }

            @Override
            public void UserInfoSuccess(Info info) {

            }

            @Override
            public void UserSourceSuccess(SourceModel response) {

            }
        });

        addFragments();

        userConfigPresenter.getUserConfig(false);
        Log.e("expandCard", "onCreate ");
        setlistener();

        //set theme
//        if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)) {
//            BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_NO);
//        } else if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
//            BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_YES);
//        }
//        invalidateViews(true);
        updateWidget();
    }

    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 490);
        }
    }


    private void showRatingsPopup() {
        prefConfig.setLoginCount();
        int count;
        if (!prefConfig.isRatingPopupShown()) count = prefConfig.getRatingInterval();
        else count = prefConfig.getLongRatingInterval();
        if (prefConfig.getLoginCount() % count == 0) {
//            ReviewManager manager = new FakeReviewManager(this);
            ReviewManager manager = ReviewManagerFactory.create(this);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
//                     We can get the ReviewInfo object
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(MainActivityNew.this, reviewInfo);
                    flow.addOnCompleteListener(task1 -> {
                        prefConfig.setRatingPopupShown(true);
                        Log.d(TAG, "onCreate: ");
                    });
                }
            });
        }
    }

    private void bindView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        overlay = findViewById(R.id.overlay);
        mRoot = findViewById(R.id.root);
        noRecordFoundContainer = findViewById(R.id.no_record_found_container);
    }

    private void setlistener() {

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            if (!loadingReelFirst) {
                active = getVisibleFragment();
            }
            loadingReelFirst = false;

            int itemId = item.getItemId();
            if (itemId == R.id.action_home) {
                isHomeClicked = true;
                Constants.onResumeReels = false;
                Constants.reelfragment = false;
                selectHomeFragment();
                return true;
            } else if (itemId == R.id.action_search) {
                Constants.onResumeReels = false;
                Constants.reelfragment = false;
                selectSearchFragment();
                return true;
            } else if (itemId == R.id.action_profile) {
                Constants.onResumeReels = false;
                Constants.reelfragment = false;
                selectMenuFragment();
                return true;
            } else if (itemId == R.id.action_video) {
                Constants.onResumeReels = true;
                Constants.reelfragment = true;
                selectReelsFragment();
                return true;
            } else if (itemId == R.id.following) {
                Constants.onResumeReels = false;
                Constants.reelfragment = false;
                if (!ifOffline) {
                    selectFollowingFragment();
                } else {
                    showNetworkMessage(false);
                    return false;
                }
                return true;
            }
            return false;
        });

    }

    private void selectFollowingFragment() {
        if (overlay != null) overlay.setVisibility(View.GONE);
//        popBackstackIfShown();
//        AnalyticsEvents.INSTANCE.logEvent(this, Events.MENU_PAGE_CLICK);
        if (prefConfig != null) {
            prefConfig.setAppStateMainTabs("nav_following");
        }
        Constants.canAudioPlay = false;

        if (active != null)
            fm.beginTransaction().hide(active).show(getFollowingFragment()).commitAllowingStateLoss();
        else fm.beginTransaction().show(getFollowingFragment()).commitAllowingStateLoss();

        active = getFollowingFragment();
        getFollowingFragment().reload();

        Constants.HomeSelectedFragment = Constants.BOTTOM_TAB_FOLLOWING;

        if (getHomeFragment() != null) {
            getHomeFragment().onPause();
        }

        Utils.hideKeyboard(this, bottomNavigationView);
        Constants.onResumeReels = false;

        new Components().statusBarColor(this, "white");
        dynamicColorBottomTabs();
    }

    @SuppressLint("ResourceType")
    private void staticDarkColorBottomTabs() {
        if (mRoot != null && overlay != null && bottomNavigationView != null && prefConfig != null) {
            mRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            overlay.setBackground(ContextCompat.getDrawable(this, R.drawable.nav_bar_gradient_static));
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.drawable.bottom_nav_bar_color_text_static));
            bottomNavigationView.setItemIconTintList(null);
        }
    }

    @SuppressLint("ResourceType")
    private void dynamicColorBottomTabs() {
        if (mRoot != null && overlay != null && bottomNavigationView != null && prefConfig != null) {
            mRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            overlay.setBackground(ContextCompat.getDrawable(this, R.drawable.nav_bar_gradient));
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.bottombar_bg));
            bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.drawable.bottom_nav_bar_color_text_white));
            bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.drawable.bottom_nav_bar_color));
//
//            bottomNavigationView.setItemIconTintList(null);

        }
    }

    private void clearGlideMemory() {
        Glide.get(this).clearMemory();
        new ClearGlideCacheAsyncTask(this).execute();
    }

    private void selectHomeFragment() {
        Log.e("####", "selectHomeFragment");
        if (overlay != null) overlay.setVisibility(View.VISIBLE);
        if (prefConfig != null) {
            prefConfig.setAppStateMainTabs("nav_home");
        }
        new Components().statusBarColor(this, "white");
        dynamicColorBottomTabs();

        AnalyticsEvents.INSTANCE.logEvent(this, Events.HOME_PAGE_CLICK);

        if (getHomeFragment() != null && Constants.homeDataUpdate) {
            getHomeFragment().reloadCurrent();
            Constants.homeDataUpdate = false;
        }

        boolean state = true;
        if (active instanceof TempHomeFragment && Constants.HomeSelectedFragment == Constants.BOTTOM_TAB_HOME) {
//            getHomeFragment().scrollToTop();
            if (getHomeFragment() != null) {
//                getHomeFragment().reloadCurrentBg();
                getHomeFragment().reloadCurrent();
            }

            state = false;
            // if user already in home screen then it should goto FOR YOU //
            // if user is coming from Discover, Newsreels and menu then it should goto the last selected state.
            isHomeClicked = false;
        }

        Constants.canAudioPlay = true;
        if (active != null)
            fm.beginTransaction().hide(active).show(getHomeFragment()).commitAllowingStateLoss();
        else fm.beginTransaction().show(getHomeFragment()).commitAllowingStateLoss();
        active = getHomeFragment();

        Constants.visiblePage = Constants.visiblePageHome;
        Constants.HomeSelectedFragment = Constants.BOTTOM_TAB_HOME;
        if (getHomeFragment() != null) {
            getHomeFragment().onResume();
        }
        // TODO: 2/10/21
//        mTabs.selectTab(mTabs.getTabAt(0));
        Utils.hideKeyboard(this, bottomNavigationView);
//        bottomNavigationView.setSelectedItemId(R.id.action_home);
        Constants.onResumeReels = false;
    }

    private void selectSearchFragment() {
        if (overlay != null) overlay.setVisibility(View.VISIBLE);
//        popBackstackIfShown();
        if (getSearchFragment() != null && getSearchFragment().isAdded()) {
//            getSearchFragment().clearText();
        }
        AnalyticsEvents.INSTANCE.logEvent(this, Events.DISCOVER_PAGE_CLICK);
        Constants.canAudioPlay = false;
        if (!(active instanceof DiscoverFragmentNew)) {
            if (active != null)
                fm.beginTransaction().hide(active).show(getSearchFragment()).commitAllowingStateLoss();
            else fm.beginTransaction().show(getSearchFragment()).commitAllowingStateLoss();
        }
        active = getSearchFragment();
        Constants.HomeSelectedFragment = Constants.BOTTOM_TAB_SEARCH;
        if (getSearchFragment() != null && getSearchFragment().isAdded()) {
            getSearchFragment().selectFirst();
            getSearchFragment().init();
        }
        if (getHomeFragment() != null) {
            getHomeFragment().onPause();
        }
        Utils.hideKeyboard(this, bottomNavigationView);
        Constants.onResumeReels = false;
        new Components().statusBarColor(this, "white");
        dynamicColorBottomTabs();
        if (getSearchFragment() != null && getSearchFragment().isAdded()) {
            getSearchFragment().setStatusBarColor();
        }
    }

    private void selectSearch(String name) {
        dynamicColorBottomTabs();
        if (getSearchFragment() != null) {
//            getSearchFragment().clearText();
        }
        AnalyticsEvents.INSTANCE.logEvent(this, Events.SEARCH_CLICK);
        Constants.canAudioPlay = false;
        if (active != null) {
            //old
//            fm.beginTransaction().hide(active).show(searchFragment).commitAllowingStateLoss();
            //Arslan
            fm.beginTransaction().hide(active).show(discoverFragment_new).commitAllowingStateLoss();
        } else {
            //old
//            fm.beginTransaction().show(searchFragment).commitAllowingStateLoss();
            //Arslan
            fm.beginTransaction().show(discoverFragment_new).commitAllowingStateLoss();
        }
        active = getSearchFragment();
        Constants.HomeSelectedFragment = Constants.BOTTOM_TAB_SEARCH;


        //bottomNavigationView.setCurrentActiveItem will call selectSearchFragment.
        //to avoid this a flag will be set true temporarily
        isSearchNavigate = true;
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.action_search);
        }
        new Handler().postDelayed(() -> isSearchNavigate = false, 500);

        if (getSearchFragment() != null) {
            switch (name) {
                case "discover":
//                    getSearchFragment().selectFirst();
                    break;
            }
        }
        if (getHomeFragment() != null) {
            getHomeFragment().onPause();
        }
        Utils.hideKeyboard(this, bottomNavigationView);
    }

    private void selectMenuFragment() {
        if (overlay != null) overlay.setVisibility(View.GONE);
//        popBackstackIfShown();
        AnalyticsEvents.INSTANCE.logEvent(this, Events.MENU_PAGE_CLICK);
        Constants.canAudioPlay = false;
        if (active != null)
            fm.beginTransaction().hide(active).show(getMenuFragment()).commitAllowingStateLoss();
        else fm.beginTransaction().show(getMenuFragment()).commitAllowingStateLoss();
        active = getMenuFragment();
        Constants.HomeSelectedFragment = Constants.BOTTOM_TAB_ACCOUNT;

        if (getHomeFragment() != null) {
            getHomeFragment().onPause();
        }

        if (getMenuFragment() != null && getMenuFragment().isAdded()) {
//            getMenuFragment().menuOpened();
////            getMenuFragment().loadRegions();
//            getMenuFragment().setData();
        }
        Utils.hideKeyboard(this, bottomNavigationView);
        Constants.onResumeReels = false;

        if (getMenuFragment() != null && Constants.menuDataUpdate) {
//            getMenuFragment().updateData();
            Constants.menuDataUpdate = false;
        }

        new Components().statusBarColor(this, "white");
        dynamicColorBottomTabs();
    }

    private void selectReelsFragment() {
        if (overlay != null) overlay.setVisibility(View.GONE);
        Log.e("####", "selectReelsFragment");
//        popBackstackIfShown();
        AnalyticsEvents.INSTANCE.logEvent(this, Events.NEWSREELS_PAGE_CLICK);
        Constants.onResumeReels = true;
        if (active instanceof ReelFragment && Constants.HomeSelectedFragment == BOTTOM_TAB_VIDEO) {
            if (getVideoFragment() != null)
                getVideoFragment().reload();//reloading the reels on second tap
        }

//causinf reload for nav back in 2min
//        if (prefConfig != null) {
//            long appBgTime = prefConfig.getBgTime();
//            if (Math.abs(appBgTime - System.currentTimeMillis()) > TimeUnit.MINUTES.toMillis(Constants.refreshTime)) {
////                prefConfig.setReelTime(0);
//                if (getVideoFragment() != null) {
//                    getVideoFragment().reload();
//                }
//            }
//        }

//        if (getVideoFragment() != null && Constants.reelDataUpdate) {
//            getVideoFragment().reload();
//            Constants.reelDataUpdate = false;
//        }

        Constants.canAudioPlay = false;

        if (active != null)
            fm.beginTransaction().hide(active).show(getVideoFragment()).commitAllowingStateLoss();
        else fm.beginTransaction().show(getVideoFragment()).commitAllowingStateLoss();
        active = getVideoFragment();
        Constants.HomeSelectedFragment = BOTTOM_TAB_VIDEO;
// if nav back withi 2 mins play from wat pos left.. as onhidden change onresume is called no need for this
//       getVideoFragment().reloadVideo();

        if (getHomeFragment() != null) {
            getHomeFragment().onPause();
        }

        Utils.hideKeyboard(this, bottomNavigationView);
        if (prefConfig != null) {
            prefConfig.setAppStateMainTabs("nav_reels");
        }
        new Components().statusBarColor(this, "black");
        staticDarkColorBottomTabs();
        Log.d(TAG, "selectReelsFragment: ");
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) return fragment;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (getOnBackPressedDispatcher().hasEnabledCallbacks()) {
            super.onBackPressed();
            return;
        }
        try {
            Log.d(TAG, "onBackPressed: c ===== " + fm.getBackStackEntryCount());
            if (fm.getBackStackEntryCount() > 0) {

                Fragment activeFragment = getVisibleFragment();
                if (activeFragment instanceof SettingsFragment || activeFragment instanceof TempHomeFragment || activeFragment instanceof SearchModifiedFragment) {
                    // fm.popBackStackImmediate();
                    if (getHomeFragment() != null && getHomeFragment().isHidden()) {
                        if (bottomNavigationView != null) {
                            bottomNavigationView.setSelectedItemId(R.id.action_home);
                        }
                    }
                } else {
                    fm.popBackStackImmediate();
                }

//                if (fm.getBackStackEntryCount() > 0){
//                    active = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);
//                }
//                fm.beginTransaction().show(last).commitAllowingStateLoss();


                active = last;


                if (activeFragment instanceof SettingsFragment || activeFragment instanceof TempHomeFragment || activeFragment instanceof SearchModifiedFragment || activeFragment instanceof DetailFragment) {
                    showBottomBar();
                }

                //condition when coming back from manage locations
//                if (!TextUtils.isEmpty(tag) && tag.equalsIgnoreCase("7"))
//                    active = getHomeFragment();

//                if(active instanceof ProfileFragment || active instanceof TempHomeFragment || active instanceof SearchModifiedFragment){
//                    // showBottomBar();
//                }

                if (active instanceof TempHomeFragment) {
                    Constants.visiblePage = Constants.visiblePageHome;
                    Constants.canAudioPlay = true;
                } else if (active instanceof DetailFragment) {
                    Constants.canAudioPlay = true;
                } else if (active instanceof SearchModifiedFragment) {
                    Constants.canAudioPlay = true;
                } else {
                    Constants.visiblePage = Constants.visiblePageHomeDetails;
                    Constants.canAudioPlay = false;
                }

                Constants.isHeader = false;
            } else {
                showBottomBar();
//                if (getHomeFragment() != null && getHomeFragment().isPopupVisible()) {
//                    getHomeFragment().hidePopUp();
//                } else
                if (getVideoFragment() != null && getVideoFragment().isHidden()) {
                    if (getSearchFragment() != null && getSearchFragment().isDiscoverVisible()) {
//                        getSearchFragment().selectFirst();
                    } else if (bottomNavigationView != null) {
                        bottomNavigationView.setSelectedItemId(R.id.action_video);
                    }
                } else super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private void addFragments() {
        String previousTab = prefConfig.getAppStateMainTabs();
        fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(FRAGMENT_TAG_PROFILE) == null) {
            menuFragment = SettingsFragment.newInstance();
        } else {
            menuFragment = (SettingsFragment) fm.findFragmentByTag(FRAGMENT_TAG_PROFILE);
        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_FOLLOWING) == null) {
            followingFragment = FollowingFragment.Companion.getInstance(this);
        } else {
            followingFragment = (FollowingFragment) fm.findFragmentByTag(FRAGMENT_TAG_FOLLOWING);
        }

//        if (fm.findFragmentByTag(FRAGMENT_TAG_SEARCH) == null) {
//            searchFragment = SearchModifiedFragment.newInstance(this);
//        } else {
//            searchFragment = (SearchModifiedFragment) fm.findFragmentByTag(FRAGMENT_TAG_SEARCH);
//        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_SEARCH) == null) {
            discoverFragment_new = DiscoverFragmentNew.Companion.getInstance(this);
        } else {
            discoverFragment_new = (DiscoverFragmentNew) fm.findFragmentByTag(FRAGMENT_TAG_SEARCH);
        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_COMMUNITY) == null) {
            communityFeedFragment = CommunityFeedFragmentMain.newInstance();
        } else {
            communityFeedFragment = (CommunityFeedFragmentMain) fm.findFragmentByTag(FRAGMENT_TAG_COMMUNITY);
        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_HOME) == null) {
            homeFragment = TempHomeFragment.newInstance();
        } else {
            homeFragment = (TempHomeFragment) fm.findFragmentByTag(FRAGMENT_TAG_HOME);
        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_VIDEO) == null) {
            reelFragment = ReelFragment.newInstance(this);
        } else {
            reelFragment = (ReelFragment) fm.findFragmentByTag(FRAGMENT_TAG_VIDEO);
        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_PROFILE) == null || !fm.findFragmentByTag(FRAGMENT_TAG_PROFILE).isAdded()) {
            active = menuFragment;
            fm.beginTransaction().add(R.id.frameLayout, menuFragment, FRAGMENT_TAG_PROFILE).commit();
        }

//        if (fm.findFragmentByTag(FRAGMENT_TAG_SEARCH) == null || !fm.findFragmentByTag(FRAGMENT_TAG_SEARCH).isAdded()) {
//            active = searchFragment;
//            fm.beginTransaction().hide(menuFragment).add(R.id.frameLayout, searchFragment, FRAGMENT_TAG_SEARCH).commit();
//        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_SEARCH) == null || !fm.findFragmentByTag(FRAGMENT_TAG_SEARCH).isAdded()) {
            active = discoverFragment_new;
            fm.beginTransaction().hide(menuFragment).add(R.id.frameLayout, discoverFragment_new, FRAGMENT_TAG_SEARCH).commit();
        }

//        if (fm.findFragmentByTag(FRAGMENT_TAG_COMMUNITY) == null || !fm.findFragmentByTag(FRAGMENT_TAG_COMMUNITY).isAdded()) {
//            active = communityFeedFragment;
//            fm.beginTransaction().hide(searchFragment).add(R.id.frameLayout, communityFeedFragment, FRAGMENT_TAG_COMMUNITY).commit();
//        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_COMMUNITY) == null || !fm.findFragmentByTag(FRAGMENT_TAG_COMMUNITY).isAdded()) {
            active = communityFeedFragment;
            fm.beginTransaction().hide(discoverFragment_new).add(R.id.frameLayout, communityFeedFragment, FRAGMENT_TAG_COMMUNITY).commit();
        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_HOME) == null || !fm.findFragmentByTag(FRAGMENT_TAG_HOME).isAdded()) {
            active = homeFragment;
            fm.beginTransaction().hide(communityFeedFragment).add(R.id.frameLayout, homeFragment, FRAGMENT_TAG_HOME).commit();
        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_FOLLOWING) == null || !fm.findFragmentByTag(FRAGMENT_TAG_FOLLOWING).isAdded()) {
            active = followingFragment;
            fm.beginTransaction().hide(homeFragment).add(R.id.frameLayout, followingFragment, FRAGMENT_TAG_FOLLOWING).commit();
        }

        if (fm.findFragmentByTag(FRAGMENT_TAG_VIDEO) == null || !fm.findFragmentByTag(FRAGMENT_TAG_VIDEO).isAdded()) {
            Constants.HomeSelectedFragment = BOTTOM_TAB_VIDEO;
            active = reelFragment;
            fm.beginTransaction().hide(followingFragment).add(R.id.frameLayout, reelFragment, FRAGMENT_TAG_VIDEO).commit();

            if (TextUtils.isEmpty(previousTab) || previousTab.equalsIgnoreCase("nav_reels") || (Constants.HomeSelectedFragment != Constants.BOTTOM_TAB_ACCOUNT)) {
                //prefConfig.setAppStateMainTabs("nav_reels");
                new Components().statusBarColor(this, "black");

                if (!TextUtils.isEmpty(previousTab)) {
                    switch (previousTab) {
                        case "nav_reels":
                            staticDarkColorBottomTabs();
                            Log.d(TAG, "addFragments: ");
                            break;
                        default:
                            dynamicColorBottomTabs();
                    }
                } else {
                    dynamicColorBottomTabs();
                }
            }
        }

        if (Constants.HomeSelectedFragment == Constants.BOTTOM_TAB_ACCOUNT) {
            active = getMenuFragment();
            fm.beginTransaction().show(getMenuFragment()).commitAllowingStateLoss();
        }
        if (prefConfig != null && !TextUtils.isEmpty(previousTab) && previousTab.equalsIgnoreCase("nav_home")) {
            Fragment preFragment = active;
            active = homeFragment;
            // loadingReelFirst = true;
            Constants.HomeSelectedFragment = Constants.BOTTOM_TAB_HOME;
            bottomNavigationView.setSelectedItemId(R.id.action_home);
            if (preFragment != null)
                fm.beginTransaction().hide(preFragment).show(homeFragment).commitAllowingStateLoss();
            else fm.beginTransaction().show(homeFragment).commitAllowingStateLoss();
        }
        checkIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + intent);
        if (intent != null && (intent.hasExtra("article_id") || intent.hasExtra("reel_id"))) {
            String source_name = intent.getStringExtra("source_name");
            String article_id = intent.getStringExtra("article_id");
            String source_id = intent.getStringExtra("source_id");
            String reel_id = intent.getStringExtra("reel_id");
            boolean favorite = intent.getBooleanExtra("favorite", false);
            openFromWidget(TYPE.SOURCE_PUSH, article_id, source_id, source_name, favorite, reel_id);
        }
    }

    private ReelFragment getVideoFragment() {
//        if (reelFragment != null) {
//            return reelFragment;
//        } else {
        return (ReelFragment) fm.findFragmentByTag(FRAGMENT_TAG_VIDEO);
//        }
    }

    public TempHomeFragment getHomeFragment() {
//        if (homeFragment != null) {
//            return homeFragment;
//        } else {
        return (TempHomeFragment) fm.findFragmentByTag(FRAGMENT_TAG_HOME);
//        }
    }

//    private CommunityFeedFragmentMain getCommunityFeedFragment() {
////        if (communityFeedFragment != null) {
////            return communityFeedFragment;
////        } else {
//        return (CommunityFeedFragmentMain) fm.findFragmentByTag(FRAGMENT_TAG_COMMUNITY);
////        }
//    }

    private SettingsFragment getMenuFragment() {
        return (SettingsFragment) fm.findFragmentByTag(FRAGMENT_TAG_PROFILE);
    }


    private FollowingFragment getFollowingFragment() {
        return (FollowingFragment) fm.findFragmentByTag(FRAGMENT_TAG_FOLLOWING);
    }


//    private SearchModifiedFragment getSearchFragment() {
//        return (SearchModifiedFragment) fm.findFragmentByTag(FRAGMENT_TAG_SEARCH);
//    }

    private DiscoverFragmentNew getSearchFragment() {
        return (DiscoverFragmentNew) fm.findFragmentByTag(FRAGMENT_TAG_SEARCH);
    }


    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("selected_pos", Constants.HomeSelectedFragment);
        savedInstanceState.putBoolean("onresumereels", Constants.onResumeReels);
        Log.d(TAG, "onSaveInstanceState: ");
        // etc.
    }

    @Override
    public void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
        // Restore UI state (from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        Constants.HomeSelectedFragment = savedInstanceState.getInt("selected_pos");

        if (savedInstanceState.containsKey("onresumereels"))
            Constants.onResumeReels = savedInstanceState.getBoolean("onresumereels");

        if (Constants.HomeSelectedFragment == Constants.BOTTOM_TAB_FOLLOWING) {
            active = followingFragment;
            fm.beginTransaction().hide(homeFragment).show(followingFragment).commitAllowingStateLoss();
            dynamicColorBottomTabs();
        }

        if (Constants.HomeSelectedFragment == Constants.BOTTOM_TAB_ACCOUNT) {
            active = menuFragment;
            fm.beginTransaction().hide(homeFragment).show(menuFragment).commitAllowingStateLoss();
            staticDarkColorBottomTabs();
        }
        Constants.reelDataUpdate = true;
        Constants.homeDataUpdate = true;
        Constants.menuDataUpdate = true;
        reloadFragments();
    }

    private void checkIntent() {
        if (getIntent().hasExtra("topic_id")) {
            Log.d(TAG, "checkIntent: topic_id");
            String topic_id = getIntent().getStringExtra("topic_id");
            String topic_name = getIntent().getStringExtra("topic_name");
            boolean favorite = getIntent().getBooleanExtra("favorite", false);
            onItemClicked(TYPE.TOPIC, topic_id, topic_name, favorite);
        } else if (getIntent().hasExtra("article_id")) {
            Log.d(TAG, "checkIntent: article_id");
            String source_name = getIntent().getStringExtra("source_name");
            String article_id = getIntent().getStringExtra("article_id");
            String source_id = getIntent().getStringExtra("source_id");
            String reel_id = getIntent().getStringExtra("reel_id");
            boolean favorite = getIntent().getBooleanExtra("favorite", false);
            openFromWidget(TYPE.SOURCE_PUSH, article_id, source_id, source_name, favorite, reel_id);
        } else if (getIntent().hasExtra("reel_id")) {
            Log.d(TAG, "checkIntent: ree_id");
            String source_name = getIntent().getStringExtra("source_name");
            String article_id = getIntent().getStringExtra("article_id");
            String source_id = getIntent().getStringExtra("source_id");
            String reel_id = getIntent().getStringExtra("reel_id");
            boolean favorite = getIntent().getBooleanExtra("favorite", false);
            openFromWidget(TYPE.SOURCE_PUSH, article_id, source_id, source_name, favorite, reel_id);
        } else if (getIntent().hasExtra("source_id")) {
            Log.d(TAG, "checkIntent: source_id");
            String source_id = getIntent().getStringExtra("source_id");
            String source_name = getIntent().getStringExtra("source_name");
            boolean favorite = getIntent().getBooleanExtra("favorite", false);
            onItemClicked(TYPE.SOURCE, source_id, source_name, favorite);
        } else if (getIntent().hasExtra("reel_context")) {
            Log.d(TAG, "checkIntent: " + getIntent().getStringExtra("reel_context"));
            Intent intent = new Intent(this, ReelInnerActivity.class);
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, getIntent().getStringExtra("reel_context"));
            startActivity(intent);
        }
    }

    public void showNetworkMessage(Boolean isConnected) {
        if (!isConnected) {
            Utils.showPopupMessageWithCloseButton(this, -1, getString(R.string.you_are_offline), true);
            ifOffline = true;
        } else {
            if (ifOffline) {
                ifOffline = false;
                Utils.showPopupMessageWithCloseButton(this, 3000, getString(R.string.back_online), false);
                reloadFragments();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RESULT_INTENT_SEARCH) {
            TYPE type;
            String id, name;
            boolean favorite = false;
            if (data != null && data.hasExtra("topic_id")) {
                id = data.getStringExtra("topic_id");
                name = data.getStringExtra("topic_name");
                favorite = data.getBooleanExtra("favorite", false);
                type = TYPE.TOPIC;
                onItemClicked(type, id, name, favorite);
            } else if (getIntent().hasExtra("article_id")) {
                name = getIntent().getStringExtra("source_name");
                id = getIntent().getStringExtra("article_id");
                favorite = getIntent().getBooleanExtra("favorite", false);
                type = TYPE.SOURCE_PUSH;
                onItemClicked(type, id, name, favorite);
            } else if (data != null && data.hasExtra("source_id")) {
                id = data.getStringExtra("source_id");
                name = data.getStringExtra("source_name");
                favorite = data.getBooleanExtra("favorite", false);
                type = TYPE.SOURCE;
                onItemClicked(type, id, name, favorite);
            } else {
                dataChanged(null, null);
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == RESULT_INTENT_SETTING_CHANGES) {
            if (data != null && data.hasExtra("change")) {
                boolean changes = data.getBooleanExtra("change", false);
                if (changes) {
                    reloadFragments();
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == RESULT_INTENT_CHANGE_EDITION) {
            reloadFragments();
//            getMenuFragment().loadRegions();
            //getMenuFragment().reloadEditions();
        } else if (resultCode == Activity.RESULT_OK && requestCode == RESULT_INTENT_CHANGE_THEME) {
            changeTheme();
        } /*else if (resultCode == Activity.RESULT_OK && requestCode == LoginPopupActivity.LOGIN_WITH_EMAIL) {
            Log.e("####", "LOGIN OK");
            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setType(MessageEvent.TYPE_USER_LOGGED_IN);

            EventBus.getDefault().post(messageEvent);
        }*/ else if (resultCode == Activity.RESULT_OK && requestCode == 222) {
            Log.e("####", "LOGIN OK");
            if (reelFragment != null) reelFragment.pause();
        }
    }

    public void changeTheme() {
        invalidateViews(false);
        if (getHomeFragment() != null) getHomeFragment().invalidateViews();
        if (getVideoFragment() != null) getVideoFragment().invalidateViews();
    }

    @SuppressLint("ResourceType")
    private void invalidateViews(boolean activateHome) {
//        mRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        overlay.setBackground(ContextCompat.getDrawable(this, R.drawable.nav_bar_gradient));
//        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.bottombar_bg));
//        bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.drawable.bottom_nav_bar_color_text));
//        bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.drawable.bottom_nav_bar_color));
        if (prefConfig != null && !TextUtils.isEmpty(prefConfig.getAppStateMainTabs()) && prefConfig.getAppStateMainTabs().equalsIgnoreCase("nav_reels")) {
            Log.d(TAG, "invalidateViews: ");
            staticDarkColorBottomTabs();
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//                window.setStatusBarColor(Color.BLACK);
//            } else if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO && !(prefConfig != null && !TextUtils.isEmpty(prefConfig.getAppStateMainTabs()) && prefConfig.getAppStateMainTabs().equalsIgnoreCase("nav_reels"))) {
//                window.setStatusBarColor(Color.WHITE);
//            }
//        }

        Utils.addNoDataErrorView(this, noRecordFoundContainer, v -> userConfigPresenter.getUserConfig(false), null);
    }

    private void reloadFragments() {
        if (active instanceof ReelFragment && Constants.HomeSelectedFragment == BOTTOM_TAB_VIDEO) {
            if (getVideoFragment() != null) {
                getVideoFragment().reload();
            }
        } else if (active instanceof TempHomeFragment && Constants.HomeSelectedFragment == Constants.BOTTOM_TAB_HOME) {
            if (getHomeFragment() != null) {
                getHomeFragment().reloadCurrent();
            }
        } else if (active instanceof DiscoverFragmentNew && Constants.HomeSelectedFragment == Constants.BOTTOM_TAB_SEARCH) {
            if (getSearchFragment() != null) {
                getSearchFragment().reload();
            }
        }

        try {
            DbHandler dbHandler = new DbHandler(this);
            ArrayList<UploadInfo> allTasks = dbHandler.getAllTasks();
            if (allTasks != null && allTasks.size() > 0) {
                if (!Utils.isMyServiceRunning(MainActivityNew.this, VideoProcessorService.class)) {
                    Intent intent = new Intent(this, VideoProcessorService.class);
                    startService(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void home() {
        selectHomeFragment();
        if (homeFragment != null) {
            homeFragment.home();
        }
    }

    @Override
    public void dataChanged(TYPE type, String id) {
        if (getHomeFragment() != null) {
            getHomeFragment().dataChanged();
            getHomeFragment().reloadCurrent();
        }
        switch (type) {
            case TOPIC:
                Constants.isTopicDataChange = true;
                break;
            case SOURCE:
                Constants.isSourceDataChange = true;
                break;
        }
    }

    @Override
    public void onItemClicked(TYPE type, String context, String id, String name, boolean favorite) {

        if (getVideoFragment() != null) {
            Constants.onResumeReels = false;
            getVideoFragment().onPause();
        }
        //Resetting the audio data to avoid old article speech while loading new data
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", " onitemclick : stop_destroy");
        sendAudioEvent("stop_destroy");
        Utils.hideKeyboard(MainActivityNew.this, mRoot);

        if (type != null && type.equals(TYPE.MANAGE)) {
            selectSearch(name);
        } else if (type != null && type.equals(TYPE.SOURCE)) {
            Intent intent = new Intent(MainActivityNew.this, ChannelDetailsActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
            Constants.canAudioPlay = true;
            Log.e("expandCard", "newInstance 3");
            DetailFragment detailFragment = DetailFragment.newInstance(type, id, context, name, favorite);
            last = active;
            Constants.visiblePageHomeDetails = 0;
            Constants.visiblePage = Constants.visiblePageHomeDetails;
            if (active != null) if (active != null)
                fm.beginTransaction().add(R.id.frameLayout, detailFragment, "6").hide(active).show(detailFragment).addToBackStack("6").commitAllowingStateLoss();
            else
                fm.beginTransaction().add(R.id.frameLayout, detailFragment, "6").show(detailFragment).addToBackStack("6").commitAllowingStateLoss();
            active = detailFragment;
        }
    }


    @Override
    public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
        if (getVideoFragment() != null) {
            Constants.onResumeReels = false;
            getVideoFragment().onPause();
        }
        //Resetting the audio data to avoid old article speech while loading new data
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", " onitemclick : stop_destroy");
        sendAudioEvent("stop_destroy");
        Utils.hideKeyboard(MainActivityNew.this, mRoot);

        if (type != null && type.equals(TYPE.MANAGE)) {
            selectSearch(name);
        } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
            Constants.canAudioPlay = true;
            Log.e("expandCard", "newInstance 3");
            DetailFragment detailFragment = DetailFragment.newInstance(type, id, "", name, favorite);
            last = active;
            Constants.visiblePageHomeDetails = 0;
            Constants.visiblePage = Constants.visiblePageHomeDetails;
            if (active != null)
                fm.beginTransaction().add(R.id.frameLayout, detailFragment, "6").hide(active).show(detailFragment).addToBackStack("6").commitAllowingStateLoss();
            else
                fm.beginTransaction().add(R.id.frameLayout, detailFragment, "6").show(detailFragment).addToBackStack("6").commitAllowingStateLoss();
            active = detailFragment;
        }
    }

    public void openFromWidget(TYPE type, String id, String sourceId, String name, boolean favorite, String reelId) {
//        if (homeFragment != null && homeFragment.isPopupVisible()) {
//            homeFragment.hidePopUp();
//        }
        if (getVideoFragment() != null) {
            Constants.onResumeReels = false;
            getVideoFragment().onPause();
        }
        //Resetting the audio data to avoid old article speech while loading new data
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", "widget : stop_destroy");
        sendAudioEvent("stop_destroy");

        Constants.canAudioPlay = true;
        Log.e("expandCard", "newInstance 3");
        Log.e("####", "openFromWidget");

        Intent intent;
        if (!TextUtils.isEmpty(reelId)) {
            Log.e("#####", "Reel id - " + reelId);
            intent = new Intent(MainActivityNew.this, ReelInnerActivity.class);
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, reelId);
        } else {
            intent = new Intent(MainActivityNew.this, BulletDetailActivity.class);
            intent.putExtra("articleId", id);
        }
        startActivityForResult(intent, RESULT_INTENT_SETTING_CHANGES);

    }

    public boolean isVisible() {
        return active instanceof TempHomeFragment && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
    }

    private void updateWidget() {
        try {
            if (AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, CollectionWidget.class)).length > 0) {
                Intent updateWidget = new Intent(this, CollectionWidget.class);
                updateWidget.setAction("update_widget");
                PendingIntent pending;
                pending = PendingIntent.getBroadcast(this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                } else {
//                    pending = PendingIntent.getBroadcast(this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
//                }
                pending.send();
            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void showNoDataErrorView(boolean show) {
        noRecordFoundContainer.setVisibility(View.INVISIBLE);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            int status = NetworkUtil.getConnectivityStatusString(context);
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                showNetworkMessage(status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED);
            }
        }
    }
}