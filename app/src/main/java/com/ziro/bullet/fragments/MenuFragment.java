package com.ziro.bullet.fragments;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AccountActivity;
import com.ziro.bullet.activities.AddTagActivity;
import com.ziro.bullet.activities.AudioSettingsActivity;
import com.ziro.bullet.activities.DataSaverActivity;
import com.ziro.bullet.activities.DraftsListingActivity;
import com.ziro.bullet.activities.FontSizeActivity;
import com.ziro.bullet.activities.HelpAndFeedBackActivity;
import com.ziro.bullet.activities.LanguageActivity;
import com.ziro.bullet.activities.LoginPopupActivity;
import com.ziro.bullet.activities.MainActivityNew;
import com.ziro.bullet.activities.MyChannelsActivity;
import com.ziro.bullet.activities.NotificationActivity;
import com.ziro.bullet.activities.OtherSettingsActivity;
import com.ziro.bullet.activities.PostArticleActivity;
import com.ziro.bullet.activities.ProfileActivity;
import com.ziro.bullet.activities.PushNotificationActivity;
import com.ziro.bullet.activities.SavedPostActivity;
import com.ziro.bullet.activities.ScheduleListingActivity;
import com.ziro.bullet.activities.WalletActivity;
import com.ziro.bullet.activities.WebViewActivity;
import com.ziro.bullet.activities.onboarding.OnboardingDetailsActivity;
import com.ziro.bullet.adapters.NewFeed.TopicsAdapter;
import com.ziro.bullet.adapters.following.FollowingAuthorsAdapter;
import com.ziro.bullet.adapters.following.FollowingChannelsAdapter;
import com.ziro.bullet.adapters.following.FollowingPlacesAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.background.VideoProcessorService;
import com.ziro.bullet.bottomSheet.ForYouReelSheet;
import com.ziro.bullet.bottomSheet.PostArticleBottomSheet;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.data.POST_TYPE;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.postarticle.TagItem;
import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.interfaces.ApiResponseInterface;
import com.ziro.bullet.interfaces.FollowingInterface;
import com.ziro.bullet.interfaces.MainInterface;
import com.ziro.bullet.interfaces.UserChannelCallback;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.ImageCrop;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.model.CategorizedChannelsData;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.Tabs.DataItem;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.onboarding.ui.WelcomeActivity;
import com.ziro.bullet.presenter.FollowingPresenter;
import com.ziro.bullet.presenter.MainPresenter;
import com.ziro.bullet.presenter.UserChannelPresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MenuFragment extends Fragment implements View.OnClickListener, FollowingInterface {

    public static final int PERMISSION_REQUEST_REELS = 745;
    public static final int PERMISSION_REQUEST_MEDIA = 783;
    public static final int PERMISSION_REQUEST = 12;
    public static final int PERMISSION_REQUEST_NEW_REELS = 345;
    public static final int PERMISSION_REQUEST_NEW_MEDIA = 346;


    private static final String TAG = "ProfileFragment";
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    MainInterface mainInterface = new MainInterface() {
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
    };
    private PrefConfig mPrefConfig;
    private View view;
    private ConstraintLayout viewProfile;
    //    private ConstraintLayout postArticleBtn;
    private ConstraintLayout pref_btn;
    private ConstraintLayout topic_btn;
    private ConstraintLayout channel_btn;
    private ConstraintLayout author_btn;
    private ConstraintLayout channelsBtn;
    private ConstraintLayout notificationBtn;
    private ConstraintLayout accountBtn;
    private ConstraintLayout article_size_btn;
    private ConstraintLayout scheduledPostBtn;
    private ConstraintLayout draftPostBtn;
    private ConstraintLayout languageBtn;
    private ConstraintLayout changeThemeBtn;
    private ConstraintLayout hapticBtn;
    private ConstraintLayout audioSettingsBtn;
    private ConstraintLayout logoutBtn;
    private ConstraintLayout othersBtn;
    private ConstraintLayout helpBtn;
    private ConstraintLayout walletBtn;
    private ConstraintLayout regionBtn;
    private ConstraintLayout fav_btn;
    private ConstraintLayout news_content_lang_btn;
    private ConstraintLayout bullet_autoSwitch;
    private ConstraintLayout video_autoSwitch;
    private ConstraintLayout reels_autoSwitch;
    private ConstraintLayout readerSwitch;
    private TextView on_text;
    private TextView off_text;
    private LinearLayout off;
    private LinearLayout bullet_auto_off;
    private TextView bullets_on_text;
    private TextView bullets_off_text;
    private LinearLayout video_auto_off;
    private TextView videos_on_text;
    private TextView videos_off_text;
    private LinearLayout reels_auto_off;
    private TextView reels_on_text;
    private TextView reels_off_text;
    private LinearLayout reader_off;
    private TextView reader_on_text;
    private TextView reader_off_text;
    private View reader_BgSwitch;
    private View reels_BgSwitch;
    private View videos_BgSwitch;
    private View bullets_BgSwitch;
    private View hapticBgSwitch;
    private View themeBgSwitch;
    private ConstraintLayout hapticSwitch;
    private ConstraintLayout colorThemeSwitch;
    private LinearLayout tab2;
    private TextView tab2_txt;
    private TextView tab3_txt;
    private RoundedImageView roundedImageView;
    private CircleImageView languageFlag;
    private TextView languageName;
    private LinearLayout menuLayout;
    private ConstraintLayout topBarContainer;
    private TextView title;
    private TextView username;
    private TextView viewProfileText;
    private TextView languageText;
    private TextView colorThemeText;
    private TextView hapticText;
    private TextView audioSettingsText;
    private TextView articleSizeText;
    private TextView scheduledPostText;
    private TextView draftPostText;
    private TextView accountText;
    private TextView notificationText;
    private TextView dataSaverText;
    private TextView helpText;
    private TextView othersText;
    private TextView logoutText;
    private TextView walletText;
    private TextView regionText;
    private TextView channelsText;
    private TextView topic_text;
    private TextView channel_text;
    private TextView authors_text;
    private TextView news_content_lang_text;
    private ImageView topic_arrow_account;
    private ImageView channel_arrow_account;
    private ImageView authors_arrow_account;
    private ImageView arrowChannels;
    private ImageView arrowLanguage;
    private ImageView arrowAccount;
    private ImageView arrowNotifications;
    private ImageView arrowDataSaver;
    private ImageView arrowAudioSettings;
    private ImageView arrowHelp;
    private ImageView arrowOthers;
    private ImageView news_content_lang_arrow_account;
    private ImageView arrowLogout;
    //    private ImageView walletArrowAccount;
    private ImageView regionArrowAccount;
    private ConstraintLayout dataSaverBtn;
    private View separatorLine;
    private TextView appVersion;
    private LinearLayout tiktokLink;
    private LinearLayout twitterLink;
    private LinearLayout facebookLink;
    private LinearLayout youtubeLink;
    private LinearLayout instagramLink;
    private LinearLayout menuSeparator1;
    private LinearLayout menuSeparator2;
    private LinearLayout menuSeparator3;
    private LinearLayout menuSeparator4;
    private LinearLayout menuSeparator5;
    private LinearLayout menuSeparator6;
    private LinearLayout menuSeparator7;
    private LinearLayout menuSeparator8;
    private LinearLayout menuSeparator9;
    private LinearLayout menuSeparator10;
    private LinearLayout menuSeparator11;
    private LinearLayout menuSeparator12;
    private View menuSeparatorView1;
    private View menuSeparatorView2;
    private View menuSeparatorView3;
    private View menuSeparatorView4;
    private View menuSeparatorView5;
    private View menuSeparatorView6;
    private View menuSeparatorView7;
    private View menuSeparatorView8;
    private View menuSeparatorView9;
    private View menuSeparatorView10;
    private View menuSeparatorView11;
    private View menuSeparatorView12;
    private ImageView view_notification;
    private RelativeLayout post_article;
    private RelativeLayout rlNotifications;
    private User user = null;
    private boolean isLoading = false;
    private PostArticleBottomSheet postArticleBottomSheet = null;
    private DbHandler cacheManager;
    private PictureLoadingDialog loadingDialog;
    private ArrayList<Location> regionList = new ArrayList<>();
    private RecyclerView regionRecyclerView;
    private FollowingPlacesAdapter menuRegionAdapter;
    private FollowingChannelsAdapter menuChannelAdapter;
    private FollowingChannelsAdapter followingChannelsAdapter;
    private TopicsAdapter followingTopicsAdapter;
    private FollowingAuthorsAdapter followingAuthorsAdapter;
    private ArrayList<Source> myChannelList = new ArrayList<>();
    private ArrayList<Source> channelList = new ArrayList<>();
    private ArrayList<Topics> topicList = new ArrayList<>();
    private ArrayList<Author> authorList = new ArrayList<>();
    private RecyclerView channelRecyclerView;
    private final UserChannelCallback userChannelCallback = new UserChannelCallback() {
        @Override
        public void loaderShow(boolean flag) {
        }

        @Override
        public void error(String error) {
            myChannelList.clear();
            menuChannelAdapter.notifyDataSetChanged();
        }

        @Override
        public void success(ArrayList<Source> channels) {
            myChannelList.clear();
            if (channels != null && channels.size() > 0) {
                channelRecyclerView.setVisibility(View.VISIBLE);
                myChannelList.addAll(channels);
                menuChannelAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void successData(ArrayList<CategorizedChannelsData> channels) {

        }

        @Override
        public void channelSelected() {

        }
    };
    private RecyclerView topicFollowing;
    private RecyclerView channelFollowing;
    private RecyclerView authorFollowing;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private MainPresenter presenter;
    private UserChannelPresenter userChannelPresenter;
    private FollowingPresenter followingPresenter;
    private ForYouReelSheet forYouReelSheet;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }


    @Override
    public void loaderShow(boolean flag) {

    }

    @Override
    public void error(String error, int load) {

    }

    @Override
    public void onTopicFollowSuccess(DataItem dataItem, int position) {

    }

    @Override
    public void onFollowedTopicsSuccess(TopicsModel response) {
        if (response != null && response.getTopics() != null && response.getTopics().size() > 0) {
            topicList.clear();
            topicFollowing.setVisibility(View.VISIBLE);
            topicList.addAll(response.getTopics());
            if (followingTopicsAdapter != null) followingTopicsAdapter.notifyDataSetChanged();
        } else {
            topicFollowing.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFollowedChannelsSuccess(SourceModel response) {
        if (response != null && response.getSources() != null && response.getSources().size() > 0) {
            channelList.clear();
            channelFollowing.setVisibility(View.VISIBLE);
            channelList.addAll(response.getSources());
            if (followingChannelsAdapter != null) followingChannelsAdapter.notifyDataSetChanged();
        } else {
            channelFollowing.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFollowedLocationSuccess(LocationModel response) {
        if (response != null && response.getLocations() != null && response.getLocations().size() > 0) {
            regionList.clear();
            regionRecyclerView.setVisibility(View.VISIBLE);
            regionList.addAll(response.getLocations());
            mPrefConfig.setRegions(regionList);
            if (regionList.size() > 4) {
                staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
            } else {
                staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL);
            }
            regionRecyclerView.setLayoutManager(staggeredGridLayoutManager);
            if (menuRegionAdapter != null) menuRegionAdapter.notifyDataSetChanged();
        } else {
            regionList.clear();
            mPrefConfig.setRegions(regionList);
            regionRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFollowedAuthorsSuccess(AuthorSearchResponse response) {
        if (response != null && response.getAuthors() != null && response.getAuthors().size() > 0) {
            authorList.clear();
            authorFollowing.setVisibility(View.VISIBLE);
            authorList.addAll(response.getAuthors());
            if (followingAuthorsAdapter != null) followingAuthorsAdapter.notifyDataSetChanged();
        } else {
            authorFollowing.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        new Components().settingStatusBarColors(getActivity(), "black");
        view = inflater.inflate(R.layout.fragment_menu_tab, container, false);
        mPrefConfig = new PrefConfig(getContext());
        cacheManager = new DbHandler(getContext());
        userChannelPresenter = new UserChannelPresenter(getActivity(), userChannelCallback);
        followingPresenter = new FollowingPresenter(getActivity(), this);
        presenter = new MainPresenter(getActivity(), mainInterface);

        bindViews();
//        loadLanguage();
        listeners();
        getUserDataFromShredPref();
//        loadMyChannels();
//        loadTopics();
//        loadChannels();
//        loadAuthors();
//        loadRegions();

//        setTheme();
        setHaptics(false);
        init();
//        checkChannels();
        return view;
    }

    private void loadMyChannels() {
        channelRecyclerView.setVisibility(View.GONE);
        menuChannelAdapter = new FollowingChannelsAdapter(getActivity(), myChannelList, true);
        LinearLayoutManager channelLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        channelRecyclerView.setLayoutManager(channelLayoutManager);
        channelRecyclerView.setAdapter(menuChannelAdapter);
    }

    private void loadTopics() {
        topicFollowing.setVisibility(View.GONE);
        followingTopicsAdapter = new TopicsAdapter(getActivity(), topicList);
        LinearLayoutManager channelLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        topicFollowing.setLayoutManager(channelLayoutManager);
        topicFollowing.setAdapter(followingTopicsAdapter);
    }

    private void loadChannels() {
        channelFollowing.setVisibility(View.GONE);
        followingChannelsAdapter = new FollowingChannelsAdapter(getActivity(), channelList, false);
        LinearLayoutManager channelLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        channelFollowing.setLayoutManager(channelLayoutManager);
        channelFollowing.setAdapter(followingChannelsAdapter);
    }

    private void loadAuthors() {
        authorFollowing.setVisibility(View.GONE);
        followingAuthorsAdapter = new FollowingAuthorsAdapter(getActivity(), authorList, true);
        LinearLayoutManager channelLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        authorFollowing.setLayoutManager(channelLayoutManager);
        authorFollowing.setAdapter(followingAuthorsAdapter);
    }

    public void loadRegions() {
        regionRecyclerView.setVisibility(View.GONE);
        if (mPrefConfig.getRegions() != null) {
            regionList.clear();
            regionList.addAll(mPrefConfig.getRegions());
            menuRegionAdapter = new FollowingPlacesAdapter(getActivity(), regionList, true);
//            menuRegionAdapter = new MenuRegionAdapter(regionList, getActivity());
            if (regionList != null) {
                if (regionList.size() > 0) {
                    regionRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    regionRecyclerView.setVisibility(View.GONE);
                }
                if (regionList.size() > 4) {
                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
                } else {
                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL);
                }
                regionRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                regionRecyclerView.setAdapter(menuRegionAdapter);
                menuRegionAdapter.notifyDataSetChanged();
            }
        }
    }

    private void init() {
        appVersion.setText(String.format("%s v%s", getString(R.string.app_name), BuildConfig.VERSION_NAME));
    }

    private void setHaptics(boolean first) {
        if (mPrefConfig.isHaptic()) {
            on(first);
        } else {
            off(first);
        }
    }

    private void setTheme() {
        if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.AUTO)) {
            systemMode();
        } else if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
            darkMode();
        } else {
            lightMode();
        }
    }

    private void setThemeButton() {
        if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
            dark(true);
        } else {
            light(true);
        }
    }

    private void getUserDataFromShredPref() {
        if (mPrefConfig != null) {
            user = mPrefConfig.isUserObject();
            if (user != null)
                showVerifiedIcon(username, user.isVerified());
        }
    }

    private void showVerifiedIcon(TextView view, boolean tag) {
        if (view == null) return;
        if (tag) {
            if (Utils.isRTL()) {
                view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified, 0, 0, 0);
            } else {
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0);
            }
        }
    }

    private void checkChannels() {
//        mChannels.clear();
//        myChannels.setAdapter(null);
//        if (mPrefConfig != null) {
//            ArrayList<Source> channels = mPrefConfig.userChannels();
//            if (channels != null && getContext() != null && getContext().getResources() != null) {
//                if (channels.size() > 0) {
//                    mChannels.addAll(mPrefConfig.userChannels());
//                }
//                if (channels.size() < 5) {
//                    mChannels.add(new Source(Constants.CreateChannel, getContext().getResources().getString(R.string.create_channel)));
//                } else {
//                    mChannels.add(new Source(Constants.MoreChannel, getContext().getResources().getString(R.string.create_channel)));
//                }
//            } else {
//                mChannels.add(new Source(Constants.CreateChannel, getContext().getResources().getString(R.string.create_channel)));
//            }
//        }
//        mLinearLayoutManager = new LinearLayoutManager(getContext());
//        myChannels.setLayoutManager(mLinearLayoutManager);
//        channelAdapter = new ChannelsAdapter(getActivity(), mChannels, Constants.MENU_CHANNELS);
//        myChannels.setAdapter(channelAdapter);
//        channelAdapter.setCallback(new ChannelsAdapter.ChannelCallback() {
//            @Override
//            public void onItemClicked(Source source) {
//                if (mPrefConfig != null) {
//                    if (mPrefConfig.isGuestUser()) {
//                        LoginPopupActivity.start(getActivity());
//                    } else {
//                        Intent intent = new Intent(getActivity(), ChannelDetailsActivity.class);
//                        intent.putExtra("id", source.getId());
//                        startActivity(intent);
//                    }
//                }
//            }
//
//            @Override
//            public void onCreateChannel() {
//                if (mPrefConfig != null) {
//                    if (mPrefConfig.isGuestUser()) {
//                        LoginPopupActivity.start(getActivity());
//                    } else {
//                        startActivityForResult(new Intent(getActivity(), ChannelNameActivity.class), 101);
//                    }
//                }
//            }
//
//            @Override
//            public void onContactUs() {
//                if (mPrefConfig != null) {
//                    if (mPrefConfig.isGuestUser()) {
//                        LoginPopupActivity.start(getActivity());
//                    } else {
//                        Intent intent = new Intent(getContext(), ContactUsActivity.class);
//                        intent.putExtra("msg", getString(R.string.contact_us_prefilled_create_channel));
//                        startActivity(intent);
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("@@@@@@#", "onResume");
        updateData();

        setData();
        view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                setHaptics(true);
                autoPlayBullets(mPrefConfig.isBulletsAutoPlay());
                autoPlayVideo(mPrefConfig.isVideoAutoPlay());
                autoPlayReels(mPrefConfig.isReelsAutoPlay());
                readerModeEnabled(mPrefConfig.isReaderMode());
                setThemeButton();
            }
        });
        new Handler().postDelayed(() -> {
            if (getActivity() != null) {
                Utils.hideKeyboard(getActivity(), username);
            }
        }, 500);

        // set status bar color
        if (mPrefConfig != null) {
            if (!TextUtils.isEmpty(mPrefConfig.getAppTheme())) {
                if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)) {
                    new Components().blackStatusBar(getActivity(), "black");
                }
            }
        }
    }

    public void updateData() {
//        if (userChannelPresenter != null)
//            userChannelPresenter.getUserChannels("");
//        if (followingPresenter != null) {
//            followingPresenter.getFollowingTopics("");
//            followingPresenter.getFollowingChannels("");
//            followingPresenter.getFollowingAuthors("");
//            followingPresenter.getFollowingLocations("");
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.TYPE_PROFILE_CHANGED || event.getType() == MessageEvent.TYPE_USER_LOGGED_IN) {
            setData();
        }
    }

    public void setData() {
        if (mPrefConfig != null) {
            user = mPrefConfig.isUserObject();
            if (user != null) {

                if (!TextUtils.isEmpty(user.getFirst_name())) {
                    username.setText(String.format("%s", user.getFirst_name()));

                    if (!TextUtils.isEmpty(user.getProfile_image())) {
                        Picasso.get().load(user.getProfile_image())
                                .placeholder(R.drawable.ic_placeholder_user)
                                .into(roundedImageView);
                    }
                    if (getContext() != null) {
                        viewProfileText.setText(getContext().getString(R.string.view_your_profile));
                    }
                } else {

                    if (getContext() != null) {
                        username.setText(getContext().getString(R.string.create_your_profile));
                        viewProfileText.setText(getContext().getString(R.string.set_your_profile));
                    }
                    roundedImageView.setImageResource(R.drawable.ic_placeholder_user);
                }
            } else {
                if (getContext() != null) {
                    username.setText(getContext().getString(R.string.create_your_profile));
                    viewProfileText.setText(getContext().getString(R.string.set_your_profile));
                }
                roundedImageView.setImageResource(R.drawable.ic_placeholder_user);
            }
        }
    }

    private void bindViews() {
//        myChannels = view.findViewById(R.id.myChannels);
        roundedImageView = view.findViewById(R.id.roundedImageView);
        viewProfile = view.findViewById(R.id.view_profile);
//        postArticleBtn = view.findViewById(R.id.post_article_btn);
        pref_btn = view.findViewById(R.id.pref_btn);
        topic_btn = view.findViewById(R.id.topic_btn);
        channel_btn = view.findViewById(R.id.channel_btn);
        author_btn = view.findViewById(R.id.authors_btn);
        channelsBtn = view.findViewById(R.id.channels_btn);
        notificationBtn = view.findViewById(R.id.notification_btn);
        article_size_btn = view.findViewById(R.id.article_size_btn);
        scheduledPostBtn = view.findViewById(R.id.scheduled_post_btn);
        draftPostBtn = view.findViewById(R.id.drafts_btn);
        accountBtn = view.findViewById(R.id.account_btn);
        languageBtn = view.findViewById(R.id.language_btn);
        changeThemeBtn = view.findViewById(R.id.change_theme_btn);
        hapticBtn = view.findViewById(R.id.haptic_btn);
        audioSettingsBtn = view.findViewById(R.id.audio_settings_btn);
        othersBtn = view.findViewById(R.id.others_btn);
        logoutBtn = view.findViewById(R.id.logout_btn);
        helpBtn = view.findViewById(R.id.help_btn);
        walletBtn = view.findViewById(R.id.wallet_btn);
        regionBtn = view.findViewById(R.id.region_btn);
        fav_btn = view.findViewById(R.id.fav_btn);
        news_content_lang_btn = view.findViewById(R.id.news_content_lang_btn);

        tab2 = view.findViewById(R.id.tab2);
        // tab3 = view.findViewById(R.id.tab3);
        tab2_txt = view.findViewById(R.id.tab2_text);
        tab3_txt = view.findViewById(R.id.tab3_text);

        hapticBgSwitch = view.findViewById(R.id.haptic_bg_switch);
        themeBgSwitch = view.findViewById(R.id.theme_bg_switch);

        hapticSwitch = view.findViewById(R.id.hapticSwitch);
        colorThemeSwitch = view.findViewById(R.id.color_theme_switch);

        languageFlag = view.findViewById(R.id.language_flag);
        languageName = view.findViewById(R.id.language_name);

        menuLayout = view.findViewById(R.id.menu_layout);
        topBarContainer = view.findViewById(R.id.top_bar_container);

        title = view.findViewById(R.id.title);
        username = view.findViewById(R.id.username);
        viewProfileText = view.findViewById(R.id.view_profile_text);
        languageText = view.findViewById(R.id.language_text);
        colorThemeText = view.findViewById(R.id.color_theme_text);
        audioSettingsText = view.findViewById(R.id.audio_settings_text);
        hapticText = view.findViewById(R.id.haptic_text);
        articleSizeText = view.findViewById(R.id.article_size_text);
        scheduledPostText = view.findViewById(R.id.scheduled_post_text);
        draftPostText = view.findViewById(R.id.drafts_text);
        accountText = view.findViewById(R.id.account_text);
        notificationText = view.findViewById(R.id.notification_text);
        dataSaverText = view.findViewById(R.id.data_saver_text);
        helpText = view.findViewById(R.id.help_text);
        othersText = view.findViewById(R.id.others_text);
        logoutText = view.findViewById(R.id.logout_text);
        walletText = view.findViewById(R.id.wallet_text);
        regionText = view.findViewById(R.id.region_text);
        channelsText = view.findViewById(R.id.channels_text);
        topic_text = view.findViewById(R.id.topic_text);
        channel_text = view.findViewById(R.id.channel_text);
        authors_text = view.findViewById(R.id.authors_text);
        news_content_lang_text = view.findViewById(R.id.news_content_lang_text);

        arrowChannels = view.findViewById(R.id.channels_arrow_account);
        topic_arrow_account = view.findViewById(R.id.topic_arrow_account);
        channel_arrow_account = view.findViewById(R.id.channel_arrow_account);
        authors_arrow_account = view.findViewById(R.id.authors_arrow_account);
        arrowLanguage = view.findViewById(R.id.arrow_language);
        arrowAccount = view.findViewById(R.id.arrow_account);
        arrowNotifications = view.findViewById(R.id.arrow_notifications);
        arrowDataSaver = view.findViewById(R.id.arrow_data_saver);
        arrowAudioSettings = view.findViewById(R.id.arrow_audio_settings);
        arrowHelp = view.findViewById(R.id.arrow_help);
        arrowOthers = view.findViewById(R.id.arrow_others);
        news_content_lang_arrow_account = view.findViewById(R.id.news_content_lang_arrow_account);
        arrowLogout = view.findViewById(R.id.arrow_logout);
//        walletArrowAccount = view.findViewById(R.id.wallet_arrow_account);
        regionArrowAccount = view.findViewById(R.id.region_arrow_account);
        dataSaverBtn = view.findViewById(R.id.data_saver_btn);

        separatorLine = view.findViewById(R.id.separator_line);

        off = view.findViewById(R.id.off);
        on_text = view.findViewById(R.id.on_text);
        off_text = view.findViewById(R.id.off_text);

        appVersion = view.findViewById(R.id.app_version);

        tiktokLink = view.findViewById(R.id.tiktok_link);
        twitterLink = view.findViewById(R.id.twitter_link);
        facebookLink = view.findViewById(R.id.facebook_link);
        youtubeLink = view.findViewById(R.id.youtube_link);
        instagramLink = view.findViewById(R.id.instagram_link);


        menuSeparator1 = view.findViewById(R.id.menu_separator_1);
        menuSeparator2 = view.findViewById(R.id.menu_separator_2);
        menuSeparator3 = view.findViewById(R.id.menu_separator_3);
        menuSeparator4 = view.findViewById(R.id.menu_separator_4);
        menuSeparator5 = view.findViewById(R.id.menu_separator_5);
        menuSeparator6 = view.findViewById(R.id.menu_separator_6);
        menuSeparator7 = view.findViewById(R.id.menu_separator_7);
        menuSeparator8 = view.findViewById(R.id.menu_separator_8);
        menuSeparator9 = view.findViewById(R.id.menu_separator_9);
        menuSeparator10 = view.findViewById(R.id.menu_separator_10);
        menuSeparator11 = view.findViewById(R.id.menu_separator_11);
        menuSeparator12 = view.findViewById(R.id.menu_separator_12);

        menuSeparatorView1 = view.findViewById(R.id.menu_separator_view_1);
        menuSeparatorView2 = view.findViewById(R.id.menu_separator_view_2);
        menuSeparatorView3 = view.findViewById(R.id.menu_separator_view_3);
        menuSeparatorView4 = view.findViewById(R.id.menu_separator_view_4);
        menuSeparatorView5 = view.findViewById(R.id.menu_separator_view_5);
        menuSeparatorView6 = view.findViewById(R.id.menu_separator_view_6);
        menuSeparatorView7 = view.findViewById(R.id.menu_separator_view_7);
        menuSeparatorView8 = view.findViewById(R.id.menu_separator_view_8);
        menuSeparatorView9 = view.findViewById(R.id.menu_separator_view_9);
        menuSeparatorView10 = view.findViewById(R.id.menu_separator_view_10);
        menuSeparatorView11 = view.findViewById(R.id.menu_separator_view_11);
        menuSeparatorView12 = view.findViewById(R.id.menu_separator_view_12);


        rlNotifications = view.findViewById(R.id.rlNotifications);
        view_notification = view.findViewById(R.id.view_notification);
        post_article = view.findViewById(R.id.post_article);
        channelRecyclerView = view.findViewById(R.id.channel_recycler_view);
        regionRecyclerView = view.findViewById(R.id.region_recycler_view);
        topicFollowing = view.findViewById(R.id.topicFollowing);
        channelFollowing = view.findViewById(R.id.channelFollowing);
        authorFollowing = view.findViewById(R.id.authorFollowing);

        bullet_auto_off = view.findViewById(R.id.bullet_auto_off);
        bullets_BgSwitch = view.findViewById(R.id.bullet_bg_switch);
        bullets_on_text = view.findViewById(R.id.bullet_auto_on_text);
        bullets_off_text = view.findViewById(R.id.bullet_auto_off_text);

        video_auto_off = view.findViewById(R.id.video_auto_off);
        videos_BgSwitch = view.findViewById(R.id.video_auto_bg_switch);
        videos_on_text = view.findViewById(R.id.video_auto_on_text);
        videos_off_text = view.findViewById(R.id.video_auto_off_text);

        reels_auto_off = view.findViewById(R.id.reels_auto_off);
        reels_BgSwitch = view.findViewById(R.id.reels_auto_bg_switch);
        reels_on_text = view.findViewById(R.id.reels_auto_on_text);
        reels_off_text = view.findViewById(R.id.reels_auto_off_text);

        reader_off = view.findViewById(R.id.reader_off);
        reader_BgSwitch = view.findViewById(R.id.reader_bg_switch);
        reader_on_text = view.findViewById(R.id.reader_on_text);
        reader_off_text = view.findViewById(R.id.reader_off_text);

        bullet_autoSwitch = view.findViewById(R.id.bullet_autoSwitch);
        video_autoSwitch = view.findViewById(R.id.video_autoSwitch);
        reels_autoSwitch = view.findViewById(R.id.reels_autoSwitch);
        readerSwitch = view.findViewById(R.id.readerSwitch);
    }

    private void listeners() {
        viewProfile.setOnClickListener(this);
//        postArticleBtn.setOnClickListener(this);

        news_content_lang_btn.setOnClickListener(this);
        regionBtn.setOnClickListener(this);
        fav_btn.setOnClickListener(this);
        topic_btn.setOnClickListener(this);
        pref_btn.setOnClickListener(this);
        channel_btn.setOnClickListener(this);
        author_btn.setOnClickListener(this);
        channelsBtn.setOnClickListener(this);
        notificationBtn.setOnClickListener(this);
        accountBtn.setOnClickListener(this);
        article_size_btn.setOnClickListener(this);
        scheduledPostBtn.setOnClickListener(this);
        draftPostBtn.setOnClickListener(this);
        languageBtn.setOnClickListener(this);
        changeThemeBtn.setOnClickListener(this);
        hapticBtn.setOnClickListener(this);
        audioSettingsBtn.setOnClickListener(this);
        othersBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        dataSaverBtn.setOnClickListener(this);
        helpBtn.setOnClickListener(this);
        walletBtn.setOnClickListener(this);

        tiktokLink.setOnClickListener(this);
        twitterLink.setOnClickListener(this);
        facebookLink.setOnClickListener(this);
        youtubeLink.setOnClickListener(this);
        instagramLink.setOnClickListener(this);

        rlNotifications.setOnClickListener(this);
        post_article.setOnClickListener(this);

        bullet_autoSwitch.setOnClickListener(this);
        video_autoSwitch.setOnClickListener(this);
        reels_autoSwitch.setOnClickListener(this);
        readerSwitch.setOnClickListener(this);
    }

    public void invalidateViews() {
        if (getActivity() == null)
            return;

        menuLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        viewProfile.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        topBarContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        separatorLine.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_line));

        title.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        username.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        viewProfileText.setTextColor(ContextCompat.getColor(getActivity(), R.color.textSubTitleGrey));
        languageName.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        languageText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        audioSettingsText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        hapticText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        colorThemeText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        articleSizeText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        scheduledPostText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        draftPostText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        accountText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        notificationText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        dataSaverText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        helpText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        othersText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        logoutText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        walletText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        regionText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        channelsText.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        topic_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        channel_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        authors_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));
        news_content_lang_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.title_bar_title));

        hapticSwitch.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_menu_region_item));
        colorThemeSwitch.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_menu_region_item));

        languageBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        changeThemeBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        hapticBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        article_size_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        scheduledPostBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        draftPostBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        accountBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        notificationBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        channelsBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        topic_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        channel_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        author_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        dataSaverBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        audioSettingsBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        walletBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        regionBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        news_content_lang_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        helpBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        othersBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        logoutBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));

        topic_arrow_account.setImageResource(R.drawable.ic_menu_right_arrow);
        channel_arrow_account.setImageResource(R.drawable.ic_menu_right_arrow);
        authors_arrow_account.setImageResource(R.drawable.ic_menu_right_arrow);
        arrowChannels.setImageResource(R.drawable.ic_menu_right_arrow);
        arrowLanguage.setImageResource(R.drawable.ic_menu_right_arrow);
        arrowAccount.setImageResource(R.drawable.ic_menu_right_arrow);
        arrowNotifications.setImageResource(R.drawable.ic_menu_right_arrow);
        arrowDataSaver.setImageResource(R.drawable.ic_menu_right_arrow);
        arrowAudioSettings.setImageResource(R.drawable.ic_menu_right_arrow);
        arrowHelp.setImageResource(R.drawable.ic_menu_right_arrow);
        arrowOthers.setImageResource(R.drawable.ic_menu_right_arrow);
        news_content_lang_arrow_account.setImageResource(R.drawable.ic_menu_right_arrow);
        arrowLogout.setImageResource(R.drawable.ic_menu_right_arrow);
//        walletArrowAccount.setImageResource(R.drawable.ic_menu_right_arrow);
        regionArrowAccount.setImageResource(R.drawable.ic_menu_right_arrow);

        tiktokLink.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_menu_item));
        twitterLink.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_menu_item));
        facebookLink.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_menu_item));
        youtubeLink.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_menu_item));
        instagramLink.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_menu_item));

        menuSeparator1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator3.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator4.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator5.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator6.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator7.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator8.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator9.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator10.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator11.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        menuSeparator12.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));

        menuSeparatorView1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView3.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView4.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView5.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView6.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView7.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView8.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView9.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView10.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView11.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));
        menuSeparatorView12.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.menu_separator_bg));

        view_notification.setImageResource(R.drawable.ic_bell);

        checkChannels();
        setHaptics(false);

        menuRegionAdapter.notifyDataSetChanged();
        menuChannelAdapter.notifyDataSetChanged();
    }

    private void autoPlayBullets(boolean enabled) {
        if (enabled)
            bulletsOn(true);
        else bulletsOff(true);
    }

    private void autoPlayVideo(boolean enabled) {
        if (enabled)
            videoOn(true);
        else videoOff(true);
    }

    private void autoPlayReels(boolean enabled) {
        if (enabled)
            reelsOn(true);
        else reelsOff(true);
    }

    private void readerModeEnabled(boolean enabled) {
        if (enabled)
            readerOn(true);
        else readerOff(true);
    }

    private void logoutAPI() {
        if (isLoading)
            return;
        AnalyticsEvents.INSTANCE.logEvent(getContext(), Events.LOGOUT_CLICK);
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(getContext(), "" + this.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingView(true);


        isLoading = true;
        Call<ResponseBody> call = OAuthAPIClient
                .getInstance()
                .getApi()
                .logout("Bearer " + mPrefConfig.getAccessToken(), mPrefConfig.getRefreshToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: ");
                logout();
//                isLoading = false;
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.d(TAG, "onResponse: ");
                logout();
//                isLoading = false;
            }
        });
    }

    private void logout() {
//        if (!mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK))
//            darkMode();

        Constants.saveLanguageId = mPrefConfig.isLanguagePushedToServer();
        mPrefConfig.clear();
        mPrefConfig.setLanguageForServer(Constants.saveLanguageId);
        mPrefConfig.setFirstTimeLaunch(false);
        try {
            if (getActivity() != null) {
                if (Utils.isMyServiceRunning(getActivity(), VideoProcessorService.class)) {
                    getActivity().stopService(new Intent(getActivity(), VideoProcessorService.class));
                }
            }
            if (cacheManager != null) {
                cacheManager.clearDb();
            }
            if (getActivity() != null) {
                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Constants.onResumeReels = true;
        updateWidget();
        showLoadingView(false);
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void updateWidget() {
        try {
            if (getContext() != null)
                if (AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), CollectionWidget.class)).length > 0) {
                    Intent updateWidget = new Intent(getContext(), CollectionWidget.class);
                    updateWidget.setAction("update_widget");
                    PendingIntent pending = PendingIntent.getBroadcast(getContext(), 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    pending.send();
                }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void dark(boolean first) {
        switchAnimation(themeBgSwitch, ((View) themeBgSwitch.getParent()).getWidth() - themeBgSwitch.getWidth(), first);

        //tab3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_menu_switch));
        tab3_txt.setTextColor(getResources().getColor(R.color.white));
        //tab2.setBackground(null);
        tab2_txt.setTextColor(getResources().getColor(R.color.tab_unselect));
    }

    private void light(boolean first) {
        switchAnimation(themeBgSwitch, 0, first);

        //tab2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_menu_switch));
        tab2_txt.setTextColor(getResources().getColor(R.color.white));
        //tab3.setBackground(null);
        tab3_txt.setTextColor(getResources().getColor(R.color.tab_unselect));
    }

    private void lightMode() {
        AnalyticsEvents.INSTANCE.logEvent(getContext(),
                Events.LIGHT_MODE);
//        new Components().settingStatusBarColors(getActivity(), "white");
        Utils.checkAppModeColor(getActivity(), true);
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_NO);

        light(false);
    }

    private void darkMode() {
        AnalyticsEvents.INSTANCE.logEvent(getContext(),
                Events.DARK_MODE);
//        new Components().settingStatusBarColors(getActivity(), "black");
        Utils.checkAppModeColor(getActivity(), true);
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_YES);

        dark(false);
    }

    private void systemMode() {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                new Components().settingStatusBarColors(getActivity(), "black");
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                new Components().settingStatusBarColors(getActivity(), "white");
                break;
        }

        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    private void loadLanguage() {
//        languageFlag.setVisibility(View.GONE);
//        languageName.setVisibility(View.GONE);
//        if (mPrefConfig != null && mPrefConfig.getDefaultLanguage() != null) {
//            if (!TextUtils.isEmpty(mPrefConfig.getDefaultLanguage().getImage())) {
//                Glide.with(languageFlag).load(mPrefConfig.getDefaultLanguage().getImage()).into(languageFlag);
//                languageFlag.setVisibility(View.VISIBLE);
//                languageName.setText(mPrefConfig.getDefaultLanguage().getName());
//                languageName.setVisibility(View.VISIBLE);
//            }
//        }
    }

    private void on(boolean first) {
        if (getContext() != null) {
            switchAnimation(hapticBgSwitch, ((View) hapticBgSwitch.getParent()).getWidth() - hapticBgSwitch.getWidth(), first);
            on_text.setTextColor(getResources().getColor(R.color.white));
            on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            off_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }

    private void off(boolean first) {
        if (getContext() != null) {
            switchAnimation(hapticBgSwitch, 0, first);
            off_text.setTextColor(getResources().getColor(R.color.white));
            off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            on_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }

    private void bulletsOn(boolean first) {
        if (getContext() != null) {
            switchAnimation(bullets_BgSwitch, ((View) bullets_BgSwitch.getParent()).getWidth() - bullets_BgSwitch.getWidth(), first);
            bullets_on_text.setTextColor(getResources().getColor(R.color.white));
            bullets_on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            bullets_off_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            bullets_off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }

    private void bulletsOff(boolean first) {
        if (getContext() != null) {
            switchAnimation(bullets_BgSwitch, 0, first);
            bullets_off_text.setTextColor(getResources().getColor(R.color.white));
            bullets_off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            bullets_on_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            bullets_on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }

    private void videoOn(boolean first) {
        if (getContext() != null) {
            switchAnimation(videos_BgSwitch, ((View) videos_BgSwitch.getParent()).getWidth() - videos_BgSwitch.getWidth(), first);
            videos_on_text.setTextColor(getResources().getColor(R.color.white));
            videos_on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            videos_off_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            videos_off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }

    private void videoOff(boolean first) {
        if (getContext() != null) {
            switchAnimation(videos_BgSwitch, 0, first);
            videos_off_text.setTextColor(getResources().getColor(R.color.white));
            videos_off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            videos_on_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            videos_on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }

    private void reelsOn(boolean first) {
        if (getContext() != null) {
            switchAnimation(reels_BgSwitch, ((View) reels_BgSwitch.getParent()).getWidth() - reels_BgSwitch.getWidth(), first);
            reels_on_text.setTextColor(getResources().getColor(R.color.white));
            reels_on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            reels_off_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            reels_off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }

    private void reelsOff(boolean first) {
        if (getContext() != null) {
            switchAnimation(reels_BgSwitch, 0, first);
            reels_off_text.setTextColor(getResources().getColor(R.color.white));
            reels_off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            reels_on_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            reels_on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }


    private void readerOn(boolean first) {
        if (getContext() != null) {
            switchAnimation(reader_BgSwitch, ((View) reader_BgSwitch.getParent()).getWidth() - reader_BgSwitch.getWidth(), first);
            reader_on_text.setTextColor(getResources().getColor(R.color.white));
            reader_on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            reader_off_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            reader_off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }

    private void readerOff(boolean first) {
        if (getContext() != null) {
            switchAnimation(reader_BgSwitch, 0, first);
            reader_off_text.setTextColor(getResources().getColor(R.color.white));
            reader_off_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
            reader_on_text.setTextColor(getResources().getColor(R.color.tab_unselect));
            reader_on_text.setPadding(getResources().getDimensionPixelSize(R.dimen._15sdp), 0, getResources().getDimensionPixelSize(R.dimen._15sdp), 0);
        }
    }

    private void switchAnimation(View view, int distance, boolean first) {
        if (Utils.isRTL()) {
            distance = -1 * distance;
        }
        ObjectAnimator onAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, distance);

        AnimatorSet set = new AnimatorSet();
        set.play(onAnimator);
        set.setInterpolator(new AccelerateInterpolator(1));
        set.setDuration(first ? 0 : 150);
        set.start();
    }

    public void menuOpened() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            setHaptics(true);
            autoPlayBullets(mPrefConfig.isBulletsAutoPlay());
            autoPlayVideo(mPrefConfig.isVideoAutoPlay());
            autoPlayReels(mPrefConfig.isReelsAutoPlay());
            readerModeEnabled(mPrefConfig.isReaderMode());
            setThemeButton();
        }, 50);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_NEW_REELS) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Reels();
            }
        }
        if (requestCode == PERMISSION_REQUEST_NEW_MEDIA) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Media();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (data != null) {
                    ArrayList<TagItem> tagItemArrayList = (ArrayList<TagItem>) data.getSerializableExtra(AddTagActivity.TAG_RESULT_KEY);
                    if (tagItemArrayList != null) {
                        for (TagItem tagItem : tagItemArrayList) {
                            Log.e("### TagItem", tagItem.getName() + " " + tagItem.getId());
                        }
                    }
                }
            } else if (requestCode == PERMISSION_REQUEST_REELS) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() == 0 && data != null) {
                    VideoInfo videoInfo = data.getParcelableExtra("video_info");
                    if (videoInfo != null) {
                        if (getActivity() == null || getActivity().isFinishing()) return;
                        Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                        intent.putExtra("POST_TYPE", POST_TYPE.REELS);
                        intent.putExtra("MODE", MODE.ADD);
                        intent.putExtra("file", new File(videoInfo.getPath()));
                        intent.putExtra("uri", videoInfo.getPath());
                        intent.putExtra("video_info", videoInfo);
                        getActivity().startActivity(intent);
                    }
                } else
                    loadSelectedVideo(selectList.get(0));

            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() == 0 && data != null) {
                    VideoInfo videoInfo = data.getParcelableExtra("video_info");
                    if (videoInfo != null) {
                        if (getActivity() == null || getActivity().isFinishing()) return;
                        Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                        intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
                        intent.putExtra("MODE", MODE.ADD);
                        intent.putExtra("file", new File(videoInfo.getPath()));
                        intent.putExtra("uri", videoInfo.getPath());
                        intent.putExtra("video_info", videoInfo);
                        getActivity().startActivity(intent);
                    }
                } else {
                    loadSelectedImage(selectList.get(0));
                }
            } else if (requestCode == ImageCrop.REQUEST_CROP) {
                if (getActivity() == null) return;
                if (data != null) {
                    Uri resultUri = ImageCrop.getOutput(data);
                    if (resultUri != null) {
                        String cutPath = resultUri.getPath();
                        Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                        intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
                        intent.putExtra("MODE", MODE.ADD);
                        intent.putExtra("file", new File(cutPath));
                        intent.putExtra("uri", cutPath);
                        getActivity().startActivity(intent);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.view_profile) {
//            startActivity(new Intent(getActivity(), OnBoardingActivity.class));
            if (mPrefConfig.isGuestUser()) {
                LoginPopupActivity.start(getActivity());
            } else {
                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        Events.CREATE_PROFILE_CLICK);
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        } else if (id == R.id.channels_btn) {
            MyChannelsActivity.launchActivity(getActivity());
        } else if (id == R.id.pref_btn) {
            forYouBottomSheet("menu");
        } else if (id == R.id.topic_btn) {
            openDetail("topics");
        } else if (id == R.id.channel_btn) {
            openDetail("channels");
        } else if (id == R.id.authors_btn) {
            openDetail("authors");
        } else if (id == R.id.region_btn) {
            openDetail("places");
        } else if (id == R.id.notification_btn) {
            startActivity(new Intent(getActivity(), PushNotificationActivity.class));
        } else if (id == R.id.language_btn) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LanguageActivity.class);
                intent.putExtra("flow", "setting");
                getActivity().startActivityForResult(intent, MainActivityNew.RESULT_INTENT_CHANGE_LANGUAGE);
            }
        } else if (id == R.id.change_theme_btn) {
            if (mPrefConfig != null) {
                mPrefConfig.setAppStateMainTabs("nav_home");
            }
            if (getActivity() != null) {
                if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
                    mPrefConfig.setAppTheme(Constants.LIGHT);
                    lightMode();
                } else if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)) {
                    mPrefConfig.setAppTheme(Constants.DARK);
                    darkMode();
                }

                ((MainActivityNew) getActivity()).changeTheme();

                // set status bar color
                if (mPrefConfig != null) {
                    if (!TextUtils.isEmpty(mPrefConfig.getAppTheme())) {
                        if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)) {
                            new Components().blackStatusBar(getActivity(), "black");
                        }
                    }
                }

            }
        } else if (id == R.id.haptic_btn) {
            if (mPrefConfig.isHaptic()) {
                mPrefConfig.setHaptic(false);
                off(false);
            } else if (!mPrefConfig.isHaptic()) {
                mPrefConfig.setHaptic(true);
                Utils.doHaptic(mPrefConfig);
                on(false);
            }
        } else if (id == R.id.article_size_btn) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), FontSizeActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.audio_settings_btn) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), AudioSettingsActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.account_btn) {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), AccountActivity.class));
            }
        } else if (id == R.id.others_btn) {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), OtherSettingsActivity.class));
            }
        } else if (id == R.id.logout_btn) {
            logoutAPI();
        } else if (id == R.id.data_saver_btn) {
            startActivity(new Intent(getActivity(), DataSaverActivity.class));
        } else if (id == R.id.help_btn) {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), HelpAndFeedBackActivity.class));
            }
        } else if (id == R.id.tiktok_link) {
            AnalyticsEvents.INSTANCE.logEvent(getContext(),
                    Events.MENU_TIKTOK_OPEN);
            openSocialLink(Constants.SOCIAL_LINKS.TIKTOK);
        } else if (id == R.id.twitter_link) {
            AnalyticsEvents.INSTANCE.logEvent(getContext(),
                    Events.MENU_TW_OPEN);
            openSocialLink(Constants.SOCIAL_LINKS.TWITTER);
        } else if (id == R.id.facebook_link) {
            AnalyticsEvents.INSTANCE.logEvent(getContext(),
                    Events.MENU_FB_OPEN);
            String fbUrl = Constants.SOCIAL_LINKS.FB;
            String fbAppUrl = Constants.SOCIAL_LINKS.FB_PAGE;
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fbAppUrl));
                startActivity(intent);
            } catch (Exception e) {
                openSocialLink(fbUrl);
            }
        } else if (id == R.id.youtube_link) {
            AnalyticsEvents.INSTANCE.logEvent(getContext(),
                    Events.MENU_YT_OPEN);
            openSocialLink(Constants.SOCIAL_LINKS.YOUTUBE);
        } else if (id == R.id.instagram_link) {
            AnalyticsEvents.INSTANCE.logEvent(getContext(),
                    Events.MENU_IG_OPEN);
            openSocialLink(Constants.SOCIAL_LINKS.IG);
        } else if (id == R.id.scheduled_post_btn) {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), ScheduleListingActivity.class));
            }
        } else if (id == R.id.drafts_btn) {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), DraftsListingActivity.class));
            }
        } else if (id == R.id.wallet_btn) {
            if (getActivity() != null) {
                String walletUrl = mPrefConfig.getWalletUrl();
                Intent intent = new Intent(getActivity(), WalletActivity.class);
                getActivity().startActivity(intent);
            }
        } else if (id == R.id.rlNotifications) {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), NotificationActivity.class));
            }
        } else if (id == R.id.post_article) {
            checkAccount();
        } else if (id == R.id.news_content_lang_btn) {
            OnboardingDetailsActivity.startActivity(
                    getActivity(),
                    OnboardingDetailsActivity.UPDATE_CONTENT_LANGUAGE
            );
//            Intent intent = new Intent(getActivity(), OnboardingDetailsActivity.class);
//            startActivity(intent);
        } else if (id == R.id.fav_btn) {
            startActivity(new Intent(getActivity(), SavedPostActivity.class));
        } else if (id == R.id.bullet_autoSwitch) {
            if (mPrefConfig.isBulletsAutoPlay()) {
                mPrefConfig.setBulletsAutoPlay(false);
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.STATUS, "0");
                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        params,
                        Events.DATASAVER_BULLETSAP);
                bulletsOff(false);
            } else if (!mPrefConfig.isBulletsAutoPlay()) {
                mPrefConfig.setBulletsAutoPlay(true);
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.STATUS, "1");
                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        params,
                        Events.DATASAVER_BULLETSAP);
                bulletsOn(false);
            }
            if (presenter != null) {
                presenter.updateConfig(mPrefConfig.getMenuViewMode(), mPrefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, mPrefConfig.isBulletsAutoPlay(), mPrefConfig.isReaderMode(), mPrefConfig.isVideoAutoPlay(), mPrefConfig.isReelsAutoPlay(),
                        flag -> {

                        });
            }
        } else if (id == R.id.video_autoSwitch) {
            if (mPrefConfig.isVideoAutoPlay()) {
                mPrefConfig.setVideoAutoPlay(false);
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.STATUS, "0");
                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        params,
                        Events.DATASAVER_VIDEOSAP);
                videoOff(false);
            } else if (!mPrefConfig.isVideoAutoPlay()) {
                mPrefConfig.setVideoAutoPlay(true);
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.STATUS, "1");
                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        params,
                        Events.DATASAVER_VIDEOSAP);
                videoOn(false);
            }
            if (presenter != null) {
                presenter.updateConfig(mPrefConfig.getMenuViewMode(), mPrefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, mPrefConfig.isBulletsAutoPlay(), mPrefConfig.isReaderMode(), mPrefConfig.isVideoAutoPlay(), mPrefConfig.isReelsAutoPlay(),
                        flag -> {

                        });
            }
        } else if (id == R.id.reels_autoSwitch) {
            if (mPrefConfig.isReelsAutoPlay()) {
                mPrefConfig.setReelsAutoPlay(false);
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.STATUS, "0");
                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        params,
                        Events.DATASAVER_REELSAP);
                reelsOff(false);
            } else if (!mPrefConfig.isReelsAutoPlay()) {
                mPrefConfig.setReelsAutoPlay(true);
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.STATUS, "1");
                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        params,
                        Events.DATASAVER_REELSAP);
                reelsOn(false);
            }
            if (presenter != null) {
                presenter.updateConfig(mPrefConfig.getMenuViewMode(), mPrefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, mPrefConfig.isBulletsAutoPlay(), mPrefConfig.isReaderMode(), mPrefConfig.isVideoAutoPlay(), mPrefConfig.isReelsAutoPlay(),
                        flag -> {

                        });
            }
        } else if (id == R.id.readerSwitch) {
            if (mPrefConfig.isReaderMode()) {
                mPrefConfig.setReaderMode(false);
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.STATUS, "0");
                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        params,
                        Events.DATASAVER_READERMODE);
                readerOff(false);
            } else if (!mPrefConfig.isReaderMode()) {
                mPrefConfig.setReaderMode(true);
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.STATUS, "1");
                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        params,
                        Events.DATASAVER_READERMODE);
                readerOn(false);
            }
            if (presenter != null) {
                presenter.updateConfig(mPrefConfig.getMenuViewMode(), mPrefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, mPrefConfig.isBulletsAutoPlay(), mPrefConfig.isReaderMode(), mPrefConfig.isVideoAutoPlay(), mPrefConfig.isReelsAutoPlay(),
                        flag -> {
                            clear();
                        });
            }
        }
    }

    private void clear() {
        if (cacheManager != null) {
            Constants.homeDataUpdate = true;
            Constants.menuDataUpdate = true;
            Constants.reelDataUpdate = true;
            if (mPrefConfig != null)
                mPrefConfig.setAppStateHomeTabs("");
            cacheManager.clearDb();
        }
    }

    private void checkAccount() {
        if (mPrefConfig != null) {
            if (mPrefConfig.isGuestUser()) {
                LoginPopupActivity.start(getActivity());
            } else {
                if (!mPrefConfig.getGuidelines()) {
                    GuidelinePopup popup = new GuidelinePopup(getActivity(), (flag, msg) -> {
                        if (flag) {
                            acceptGuidelines(new ApiResponseInterface() {
                                @Override
                                public void _success() {
                                    if (mPrefConfig != null)
                                        mPrefConfig.setGuidelines(true);
                                    if (postArticleBottomSheet == null) {
                                        postArticleBottomSheet = new PostArticleBottomSheet(getActivity(), MenuFragment.this::postContent);
                                    }
                                    postArticleBottomSheet.show(dialog -> {

                                    });
                                }

                                @Override
                                public void _other(int code) {

                                }
                            });
                        }
                    });
                    popup.showDialog();
                } else {
                    if (postArticleBottomSheet == null) {
                        postArticleBottomSheet = new PostArticleBottomSheet(getActivity(), this::postContent);
                    }
                    postArticleBottomSheet.show(dialog -> {

                    });
                }
            }
        }
    }

    public void acceptGuidelines(ApiResponseInterface apiResponseInterface) {
        if (!InternetCheckHelper.isConnected()) {
            return;
        }
        if (!TextUtils.isEmpty(mPrefConfig.getAccessToken())) {
            Call<ResponseBody> call = ApiClient.getInstance(getActivity()).getApi()
                    .acceptGuidelines("Bearer " + mPrefConfig.getAccessToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    Log.e("FCM", "FCM token to server onResponse : " + response.toString());
                    if (apiResponseInterface != null) {
                        if (response.code() == 200) {
                            apiResponseInterface._success();
                        } else {
                            apiResponseInterface._other(response.code());
                        }
                    } else {
                        apiResponseInterface._other(0);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    Log.e("FCM", "FCM token to server Failure : " + t.getMessage());
                    if (apiResponseInterface != null)
                        apiResponseInterface._other(0);
                }
            });
        }
    }

    private void openDetail(String type) {
        FollowingDetailActivity.launchActivity(getActivity(), type, "");
    }

    public void forYouBottomSheet(String mode) {
        forYouReelSheet = ForYouReelSheet.getInstance(mode, "", null, null, null, null);
        forYouReelSheet.show(getChildFragmentManager(), "id");
        forYouReelSheet.getViewLifecycleOwnerLiveData().observe(this, lifecycleOwner -> {
            //when lifecycleOwner is null fragment is destroyed
            if (lifecycleOwner == null) {

            }
        });
    }

    private void postContent(String option) {
        switch (option) {
            case "YOUTUBE":
                showYoutubeUploadView();
                break;
            case "REELS":
                Reels();
                break;
            case "MEDIA":
                Media();
                break;
        }
    }

    private void Media() {
        if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryWithAll(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>());
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_NEW_MEDIA);
        }
    }

    private void Reels() {
        if (getActivity() == null) return;

        if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryOnlyVideo(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PERMISSION_REQUEST_REELS);
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_NEW_REELS);
        }
    }

    private void showYoutubeUploadView() {
        if (getActivity() != null) {
            CreateYoutubePopup createYoutubePopup = new CreateYoutubePopup(getActivity());
            createYoutubePopup.showDialog(article -> {
                if (article != null) {
                    Log.e("uploadImageVideo", "-createSuccess here-> " + new Gson().toJson(article));
                    if (getActivity() == null) return;
                    Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.YOUTUBE);
                    intent.putExtra("MODE", MODE.ADD);
                    intent.putExtra("article", new Gson().toJson(article));
                    getActivity().startActivity(intent);
                }
            });
        }
    }

    private void loadSelectedVideo(LocalMedia localMedia) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), PostArticleActivity.class);
        intent.putExtra("POST_TYPE", POST_TYPE.REELS);
        intent.putExtra("MODE", MODE.ADD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
            intent.putExtra("uri", localMedia.getAndroidQToPath());
        } else {
            intent.putExtra("file", new File(String.valueOf(localMedia.getPath())));
            intent.putExtra("uri", localMedia.getPath());
        }
        intent.putExtra("localMedia", new Gson().toJson(localMedia));
        getActivity().startActivity(intent);
    }

    private void loadSelectedImage(LocalMedia localMedia) {
        if (!localMedia.getMimeType().contains("video")) {
//            showProgressDialog();
            File f = new File(getContext().getCacheDir(), localMedia.getFileName() + System.currentTimeMillis());
            try {
                f.createNewFile();
                Uri url;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    url = Uri.fromFile(new File((localMedia.getAndroidQToPath())));
                } else {
                    url = Uri.fromFile(new File((localMedia.getPath())));
                }

                ImageCrop.of(
                                url,
                                Uri.fromFile(f)
                        )
                        .start(getContext(), MenuFragment.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (getActivity() == null) return;
            Intent intent = new Intent(getActivity(), PostArticleActivity.class);
            if (localMedia.getMimeType().contains("video")) {
                intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
            } else {
                intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
            }
            intent.putExtra("MODE", MODE.ADD);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
                intent.putExtra("uri", localMedia.getAndroidQToPath());
            } else {
                intent.putExtra("file", new File(String.valueOf(localMedia.getPath())));
                intent.putExtra("uri", localMedia.getPath());
            }
            intent.putExtra("localMedia", new Gson().toJson(localMedia));
            getActivity().startActivity(intent);
        }

    }

    public void openSocialLink(String link) {
        if (getActivity() != null) {

            ArrayList<ResolveInfo> customTabsPackages = Utils.getCustomTabsPackages(getActivity());
            if (customTabsPackages.size() > 0) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder
                        .setShowTitle(true)
                        .build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(link));
            } else {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("title", "Social Media Account");
                intent.putExtra("url", link);
                getActivity().startActivity(intent);
            }
        }
    }


    public void error(String msg) {
        if (getActivity() == null) return;
        Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
    }

    private void showLoadingView(boolean isShow) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (isShow) {
            if (loadingDialog == null) {
                loadingDialog = new PictureLoadingDialog(getContext());
            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null
                    && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}
