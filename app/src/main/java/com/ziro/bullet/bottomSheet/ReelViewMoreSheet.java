package com.ziro.bullet.bottomSheet;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AuthorActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.ChannelPostActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.ProfileActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.AuthorListResponse;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.fragments.Reels.ReelsPageInterface;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.CommunityCallback;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.LikeInterface;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.model.articles.MediaMeta;
import com.ziro.bullet.presenter.CommunityPresenter;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.texttospeech.TextToAudioPlayerHelper;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import im.ene.toro.PlayerSelector;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class ReelViewMoreSheet extends BottomSheetDialogFragment implements CommunityCallback, ReelsPageInterface {

    private final static String KEY_REEL_ITEM = "KEY_REEL_ITEM";
    private final static String REEL_POSITION = "REEL_POSITION";
    private static int position;
    private static OnShareListener listener;
    private AppBarLayout appBarLayout;
    //    private RecyclerView mListRV;
//    private HomeAdapter mCardAdapter;
//    private ProgressBar progress;
    private ArrayList<Article> contentArrayList = new ArrayList<>();
    private ArrayList<ReelsItem> reels = new ArrayList<>();
    private CommunityPresenter presenter;
    private LikePresenter likePresenter;
    private int mArticlePosition = 0;
    private RelativeLayout rlChannelDetail;
    private TextView tvTime;
    private TempCategorySwipeListener swipeListener = new TempCategorySwipeListener() {
        @Override
        public void swipe(boolean enable) {

        }

        @Override
        public void muteIcon(boolean isShow) {

        }

        @Override
        public void onFavoriteChanged(boolean fav) {

        }

        @Override
        public void selectTab(String id) {

        }

        @Override
        public void selectTabOrDetailsPage(String id, String name, TYPE type, String footerType) {

        }
    };
    //    private CardView gotoTop;
    private LinearLayout viewArticle;
    private CardView tag;
    private CardView cvVideoThumbnail;
    private CircleImageView image;
    private ConstraintLayout menu;
    private ImageView back;
    private ImageView setting2;
    private LinearLayout llFavIcon;
    private ImageView favIcon;
    private LinearLayout comment;
    private LinearLayout share;
    private ConstraintLayout follow;
    private TextView follow_txt;
    private View followBottomBar;
    private ProgressBar follow_progress;
    private TextView favCount;
    private TextView commentCount;
    private TextView name;
    private TextView desc;
    private TextView username;
    private CardView channel_btn;
    private TextView noData;
    private ImageView image_bg;
    private ImageView cover;
    //    private RelativeLayout ivBack;
    private AudioCallback audioCallback;
    private PlayerSelector selector = PlayerSelector.DEFAULT;
    private String _myTag;
    private TextToAudioPlayerHelper textToAudio;
    private GoHome goHome = new GoHome() {
        @Override
        public void home() {

        }

        @Override
        public void sendAudioToTempHome(AudioCallback audioCallback1, String fragTag, String status, AudioObject audio) {
            audioCallback = audioCallback1;
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

        }

        @Override
        public void scrollDown() {

        }

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
//                        if (bottomNavigationView != null)
//                            bottomNavigationView.setSelectedItemId(R.id.action_home);
                        break;
                }
            }

        }
    };
    private ShareToMainInterface shareToMainInterface = new ShareToMainInterface() {

        @Override
        public void removeItem(String id, int position) {

        }

        @Override
        public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
            Log.e("@@@", "ITEM CLICKED");

            //Resetting the audio data to avoid old article speech while loading new data
            Constants.articleId = "";
            Constants.speech = "";
            Constants.url = "";
            Log.d("audiotest", " onitemclick : stop_destroy");
            goHome.sendAudioEvent("stop_destroy");
            // Utils.hideKeyboard(getContext(), mRoot);

            if (type != null && type.equals(TYPE.MANAGE)) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
                Constants.canAudioPlay = true;
                Log.e("expandCard", "newInstance 3");

                Intent intent = null;
                if (type.equals(TYPE.SOURCE)) {
                    intent = new Intent(getContext(), ChannelDetailsActivity.class);
                } else {
                    intent = new Intent(getContext(), ChannelPostActivity.class);
                }
                intent.putExtra("type", type);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("favorite", favorite);
                startActivity(intent);
            }
        }

        @Override
        public void unarchived() {

        }
    };
    public OnGotoChannelListener onGotoChannelListener = new OnGotoChannelListener() {
        @Override
        public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
            Log.e("@@@", "ITEM CLICKED");

            //Resetting the audio data to avoid old article speech while loading new data
            Constants.articleId = "";
            Constants.speech = "";
            Constants.url = "";
            Log.d("audiotest", " onitemclick : stop_destroy");
            goHome.sendAudioEvent("stop_destroy");
            // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);

            if (type != null && type.equals(TYPE.MANAGE)) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
                Constants.canAudioPlay = true;
                Log.e("expandCard", "newInstance 3");

                Intent intent = null;
                if (type.equals(TYPE.SOURCE)) {
                    intent = new Intent(getContext(), ChannelDetailsActivity.class);
                } else {
                    intent = new Intent(getContext(), ChannelPostActivity.class);
                }
                intent.putExtra("type", type);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("favorite", favorite);
                startActivity(intent);
                // finish();
            }
        }

        @Override
        public void onItemClicked(TYPE type, String id, String name, boolean favorite, Article article, int position) {

        }

        @Override
        public void onArticleSelected(Article article) {

        }
    };
    private SpeedyLinearLayoutManager cardLinearLayoutManager;
    private String mNextPage = "";
    private ReelsItem mReelsItem;
    private PrefConfig prefConfig;
    private FrameLayout bottomSheet;
    private BottomSheetBehavior behavior;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private ShareBottomSheet shareBottomSheet;

    public static ReelViewMoreSheet getInstance(ReelsItem reelsItem, int position1, OnShareListener listener1) {
        ReelViewMoreSheet reelViewMoreSheet = new ReelViewMoreSheet();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_REEL_ITEM, reelsItem);
        bundle.putInt(REEL_POSITION, position1);
        reelViewMoreSheet.setArguments(bundle);
        listener = listener1;
        return reelViewMoreSheet;
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
//        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<ResolveInfo> resolvedActivityList;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resolvedActivityList =
                    pm.queryIntentActivities(activityIntent, PackageManager.MATCH_ALL);
        } else {
            resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        }

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

    @Override
    public void onResume() {
        super.onResume();
        Log.e("rvm", "onResume: rvm");
//        Constants.onResumeReels = true;
        Constants.rvmdailogopen = true;
//        onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog d = (BottomSheetDialog) getDialog();
        if (d != null) {
            setupFullHeight(d);
            bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
//            d.setOnDismissListener(dismissListener);
            if (bottomSheet != null && getContext() != null) {
                behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                bottomSheet.setBackground(getContext().getResources().getDrawable(R.drawable.bottom_sheet_white_shape));
            }
        }
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.e("rvm", "onDismiss: ");
        Constants.onResumeReels = true;
        Constants.rvmdailogopen = false;
        Constants.rvm = (Constants.rvm) - 1;
        listener.viewMoreDismissed();
    }


    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_more_reel_new, container, false);
        if (getArguments() != null && getArguments().containsKey(KEY_REEL_ITEM)) {
            mReelsItem = getArguments().getParcelable(KEY_REEL_ITEM);
        }

        if (getArguments() != null && getArguments().containsKey(REEL_POSITION)) {
            position = getArguments().getInt(REEL_POSITION);
        }
        Constants.rvmdailogopen = true;
        presenter = new CommunityPresenter(getActivity(), this);
        likePresenter = new LikePresenter(getActivity());
        shareBottomSheetPresenter = new ShareBottomSheetPresenter(getActivity());
        followUnfollowPresenter = new FollowUnfollowPresenter(getActivity());
        prefConfig = new PrefConfig(getContext());

        initView(view);
        setData();
        setRvScrollListener();
        setListener();

        AnalyticsEvents.INSTANCE.logEvent(getContext(),
                Events.VIEW_MORE_CLICK);

//        presenter.loadRelatedArticle(mNextPage, mReelsItem.getId(), prefConfig.isReaderMode());
        return view;
    }

    private void updateFollowColor(boolean isFavorite) {
        try {
            if (isFavorite) {
                follow_txt.setText(getString(R.string.following));
                follow_txt.setText(getActivity().getString(R.string.following));
                follow_txt.setTextColor(getResources().getColor(R.color.following_text_color));
                follow_txt.setTextColor(ContextCompat.getColor(requireContext(), R.color.following_text_color));
                follow_txt.setCompoundDrawables(null, null, null, null);
                DrawableCompat.setTint(followBottomBar.getBackground(), ContextCompat.getColor(requireContext(), R.color.following_text_color));
            } else {
                follow_txt.setText(getString(R.string.follow));
                //            follow_txt.setTextColor(getResources().getColor(R.color.primaryRed));
                //            follow_txt.setText(getActivity().getString(R.string.follow));
                follow_txt.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryRed));
                DrawableCompat.setTint(followBottomBar.getBackground(), ContextCompat.getColor(requireContext(), R.color.theme_color_1));
                follow_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus, 0, 0, 0);
                for (Drawable drawable : follow_txt.getCompoundDrawables()) {
                    if (drawable != null) {
                        DrawableCompat.setTint(drawable, ContextCompat.getColor(requireContext(), R.color.primaryRed));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
//        ivBack.setOnClickListener(v -> {
//            dismiss();
//        });
        ;
        rlChannelDetail.setOnClickListener(v -> {
            if (!InternetCheckHelper.isConnected()) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            Intent intent;
            if (mReelsItem.getSource() != null) {
                intent = new Intent(getContext(), ChannelDetailsActivity.class);
                intent.putExtra("id", mReelsItem.getSource().getId());
            } else {
                User user = new PrefConfig(getContext()).isUserObject();
                if (user != null && !TextUtils.isEmpty(user.getId()) && user.getId().equalsIgnoreCase(mReelsItem.getAuthor().get(0).getId())) {
                    intent = new Intent(requireActivity(), ProfileActivity.class);
                } else {
                    intent = new Intent(requireActivity(), AuthorActivity.class);
                }
                intent.putExtra("authorID", mReelsItem.getAuthor().get(0).getId());
                intent.putExtra("authorContext", mReelsItem.getAuthor().get(0).getContext());
            }
            startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST);
        });

        viewArticle.setOnClickListener(v -> {
            if (getContext() == null) return;
            AnalyticsEvents.INSTANCE.logEvent(getContext(),
                    Events.VIEW_ORIGINAL_ARTICLE_CLICK);


//            ArrayList<ResolveInfo> customTabsPackages = getCustomTabsPackages(getContext());
            if (mReelsItem.getLink() != null) {
                Constants.viewsource = true;
                Uri url = Uri.parse(mReelsItem.getLink());
                startActivity(new Intent(Intent.ACTION_VIEW, url));
            }

//            if (customTabsPackages.size() > 0) {
//                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                CustomTabsIntent customTabsIntent = builder
//                        .setShowTitle(true)
//                        .build();
//                customTabsIntent.launchUrl(getContext(), Uri.parse(mReelsItem.getLink()));
//            } else {
//                Intent intent = new Intent(getContext(), WebViewActivity.class);
//                intent.putExtra("title", getContext().getResources().getString(R.string.view_original_article));
//                intent.putExtra("url", mReelsItem.getLink());
//                getContext().startActivity(intent);
//            }
            dismiss();
        });

//        gotoTop.setOnClickListener(v -> {
//            gotoTop.setVisibility(View.GONE);
//            scrollToTop();
//            if (bottomSheet != null)
//                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
//        });

        back.setOnClickListener(view -> dismiss());

        cvVideoThumbnail.setOnClickListener(view -> dismiss());

        share.setOnClickListener(view -> {
            if (listener != null) listener.onShareClicked();
        });
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!InternetCheckHelper.isConnected()) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                follow_txt.setVisibility(View.INVISIBLE);
                follow_progress.setVisibility(View.VISIBLE);
                follow.setEnabled(false);
                if (mReelsItem.getSource() != null) {
                    if (!mReelsItem.getSource().isFavorite()) {
                        followUnfollowPresenter.followSource(mReelsItem.getSource().getId(), 0, new FollowUnfollowPresenter.FollowUnFollowApiCallback() {
                            @Override
                            public void onResponse(int position, boolean flag) {
                                follow.setEnabled(true);
                                if (flag) {
                                    mReelsItem.getSource().setFavorite(true);
                                    if (listener != null) listener.onFollow(mReelsItem);
                                    follow_txt.setVisibility(View.VISIBLE);
                                    follow_progress.setVisibility(View.INVISIBLE);
                                    updateFollowColor(mReelsItem.getSource().isFavorite());
//                                    follow_txt.setText(getString(R.string.unfollow));
//                                    follow.setCardBackgroundColor(getResources().getColor(R.color.edittextHint));
                                }
                            }
                        });

                    } else {
                        followUnfollowPresenter.unFollowSource(mReelsItem.getSource().getId(), 0, new FollowUnfollowPresenter.FollowUnFollowApiCallback() {
                            @Override
                            public void onResponse(int position, boolean flag) {
                                follow.setEnabled(true);
                                if (flag) {
                                    mReelsItem.getSource().setFavorite(false);
                                    follow_txt.setVisibility(View.VISIBLE);
                                    if (listener != null) listener.onFollow(mReelsItem);
                                    follow_progress.setVisibility(View.INVISIBLE);
                                    updateFollowColor(mReelsItem.getSource().isFavorite());
//                                    follow_txt.setText(getString(R.string.follow));
//                                    follow.setCardBackgroundColor(getResources().getColor(R.color.theme_color_1));
                                }
                            }
                        });
                    }
                } else {
                    follow.setEnabled(true);
                }
            }
        });

        setting2.setOnClickListener(v -> {
            more();
        });

    }

    private void more() {
        if (shareBottomSheetPresenter != null) {
            shareBottomSheetPresenter.share_msg(mReelsItem.getId(), new ShareInfoInterface() {
                @Override
                public void response(ShareInfo shareInfo) {

                    if (shareBottomSheet == null) {
                        shareBottomSheet = new ShareBottomSheet(getActivity(), null, true, "REEL_INNER");
                    }
                    shareBottomSheet.show(getArticleFromReels(mReelsItem), new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {

                        }
                    }, shareInfo);
                }

                @Override
                public void error(String error) {

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

    private void setData() {

        if (mReelsItem != null) {
            name.setText(mReelsItem.getSourceNameToDisplay());

            showVerifiedIcon(name, mReelsItem);

            desc.setText(mReelsItem.getDescription());
            username.setText("");
            if (!TextUtils.isEmpty(mReelsItem.getSourceImageToDisplay())) {
                Glide.with(this).load(mReelsItem.getSourceImageToDisplay())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(image);
            }
            if (!TextUtils.isEmpty(mReelsItem.getImage())) {
                Glide.with(this).load(mReelsItem.getImage())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(image_bg);
                Picasso.get()
                        .load(mReelsItem.getImage())
                        .transform(new BlurTransformation(getContext(), 25, 3))
                        .into(cover);
            }
            if (mReelsItem.getInfo() != null) {
                commentCount.setText("" + mReelsItem.getInfo().getComment_count());
                favCount.setText("" + mReelsItem.getInfo().getLike_count());
                if (mReelsItem.getInfo().isLiked()) {
                    favIcon.setImageResource(R.drawable.ic_reel_like_active);
                    favCount.setTextColor(ContextCompat.getColor(getContext(), R.color.theme_color_1));
                    DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.theme_color_1));

                } else {
                    favIcon.setImageResource(R.drawable.ic_reel_like_inactive);
                    favCount.setTextColor(ContextCompat.getColor(getContext(), R.color.greyad));
                    DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.greyad));
                }
                comment.setOnClickListener(v -> {
                    if (!InternetCheckHelper.isConnected()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    if (getActivity() != null) {
                        Map<String, String> params = new HashMap<>();
                        params.put(Events.KEYS.REEL_ID, mReelsItem.getId());
                        AnalyticsEvents.INSTANCE.logEvent(getContext(),
                                params,
                                Events.REELS_COMMENT);
                        Intent intent = new Intent(getActivity(), CommentsActivity.class);
                        intent.putExtra("article_id", mReelsItem.getId());
                        intent.putExtra("position", position);
                        startActivityForResult(intent, Constants.CommentsRequestCode);
                    }
                });
                llFavIcon.setOnClickListener(v -> {
                    if (!InternetCheckHelper.isConnected()) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    llFavIcon.setEnabled(false);
                    boolean flag = mReelsItem.getInfo().isLiked();
                    if (!flag) {
                        favCount.setTextColor(ContextCompat.getColor(getContext(), R.color.theme_color_1));
                        favIcon.setImageResource(R.drawable.ic_reel_like_active);
                        DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.theme_color_1));

                        favCount.setText("" + (mReelsItem.getInfo().getLike_count() + 1));
                    } else {
                        favCount.setTextColor(ContextCompat.getColor(getContext(), R.color.greyad));
                        favIcon.setImageResource(R.drawable.ic_reel_like_inactive);
                        DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.greyad));
                        favCount.setText("" + (mReelsItem.getInfo().getLike_count() - 1));
                    }


                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID, mReelsItem.getId());
                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
                            params,
                            Events.REELS_LIKE);
                    likePresenter.like(mReelsItem.getId(), new LikeInterface() {
                        @Override
                        public void success(boolean like) {
                            if (getContext() == null) return;
                            llFavIcon.setEnabled(true);
                            mReelsItem.getInfo().setLiked(like);
                            int counter = mReelsItem.getInfo().getLike_count();
                            if (like) {
                                counter++;
                            } else {
                                if (counter > 0) {
                                    counter--;
                                } else {
                                    counter = 0;
                                }
                            }

                            mReelsItem.getInfo().setLike_count(counter);
                            favCount.setText("" + counter);

                            if (mReelsItem.getInfo().isLiked()) {
                                favCount.setTextColor(ContextCompat.getColor(getContext(), R.color.like_heart_filled));
                                favIcon.setImageResource(R.drawable.ic_reel_like_active);
                                DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.theme_color_1));

                            } else {
                                favCount.setTextColor(ContextCompat.getColor(getContext(), R.color.greyad));
                                favIcon.setImageResource(R.drawable.ic_reel_like_inactive);
                                DrawableCompat.setTint(favIcon.getDrawable(), getContext().getResources().getColor(R.color.greyad));

                            }
                        }

                        @Override
                        public void failure() {
                            llFavIcon.setEnabled(true);
                        }
                    }, !mReelsItem.getInfo().isLiked());
                });
            }

            if (mReelsItem.getSource() != null) {
                if (mReelsItem.getSource().isFavorite()) {
                    follow_txt.setVisibility(View.VISIBLE);
                    follow_progress.setVisibility(View.INVISIBLE);
                    updateFollowColor(mReelsItem.getSource().isFavorite());
//                    follow_txt.setText(getString(R.string.unfollow));
//                    follow.setCardBackgroundColor(getResources().getColor(R.color.edittextHint));
                } else {
                    follow_txt.setVisibility(View.VISIBLE);
                    follow_progress.setVisibility(View.INVISIBLE);
                    updateFollowColor(mReelsItem.getSource().isFavorite());
//                    follow_txt.setText(getString(R.string.follow));
//                    follow.setCardBackgroundColor(getResources().getColor(R.color.theme_color_1));
                }
            }
        }

        //SET DATA
        textToAudio = new TextToAudioPlayerHelper(getContext());
//        mCardAdapter = new HomeAdapter(false, new CommentClick() {
//
//            @Override
//            public void onDetailClick(int position, Article article) {
//                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
//                intent.putExtra("article", new Gson().toJson(article));
//                intent.putExtra("type", "");
//                intent.putExtra("position", position);
//                startActivityForResult(intent, Constants.CommentsRequestCode);
//            }
//
//            @Override
//            public void fullscreen(int position, Article article, long duration, String mode,
//                                   boolean isManual) {
//                Intent intent = new Intent(getContext(), VideoFullScreenActivity.class);
//                intent.putExtra("url", article.getLink());
//                intent.putExtra("mode", mode);
//                intent.putExtra("position", position);
//                intent.putExtra("duration", duration);
//                startActivityForResult(intent, Constants.VideoDurationRequestCode);
//            }
//
//            @Override
//            public void commentClick(int position, String id) {
//                Intent intent = new Intent(getContext(), CommentsActivity.class);
//                intent.putExtra("article_id", id);
//                intent.putExtra("position", position);
//                startActivityForResult(intent, Constants.CommentsRequestCode);
//            }
//        }, false, (AppCompatActivity)
//
//                getActivity(), contentArrayList, "", true, new
//
//                DetailsActivityInterface() {
//                    @Override
//                    public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {
//                        if (goHome != null) {
//                            goHome.sendAudioToTempHome(audioCallback, fragTag, "", audio);
//                        }
//                    }
//
//                    @Override
//                    public void pause() {
//                        Pause();
//                    }
//
//                    @Override
//                    public void resume() {
////                        resumeCurrentBullet();
//                    }
//                }, goHome, shareToMainInterface, swipeListener, this, onGotoChannelListener,
//                new ShowOptionsLoaderCallback() {
//                    @Override
//                    public void showLoader(boolean show) {
//
//                    }
//                }
//                , new AdFailedListener() {
//            @Override
//            public void onAdFailed() {
//
//            }
//        }, getLifecycle(), new CommunityItemCallback() {
//            @Override
//            public void onItemClick(String option, Article article) {
//
//            }
//        });

//        mCardAdapter.addChildListener(this);
//
//        cardLinearLayoutManager = new
//
//                SpeedyLinearLayoutManager(getContext());
//        mListRV.setLayoutManager(cardLinearLayoutManager);
//        mListRV.setOnFlingListener(null);
//        mListRV.setAdapter(mCardAdapter);
//        mListRV.setCacheManager(mCardAdapter);
//        mListRV.setPlayerSelector(selector);

//        if (bottomSheet != null) {
//            BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                @Override
//                public void onStateChanged(@NonNull View bottomSheet, int newState) {
//
//                }
//
//                @Override
//                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                    int bottomSheetVisibleHeight = bottomSheet.getHeight();
//                    pinned_bottom.setTranslationY((bottomSheetVisibleHeight - pinned_bottom.getHeight()));
//                }
//            });
//        }
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

//    public void resumeCurrentBullet() {
//        try {
//            mListRV.setPlayerSelector(selector);
//            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//            if (holder != null) {
//                if (holder instanceof LargeCardViewHolder) {
//                    ((LargeCardViewHolder) holder).storiesProgressView.resume();
//                } else if (holder instanceof SmallCardViewHolder) {
//                    ((SmallCardViewHolder) holder).storiesProgressView.resume();
//                } else if (holder instanceof YoutubeViewHolder) {
//                    ((YoutubeViewHolder) holder).youtubeResume();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * /**
     * Pause current bullets, youtube videos
     * Normal videos are by setting selector NONE, so dont forget to set DEFAULT selector on resume otherwise videos wont play even if resumed
     */
//    public void Pause() {
//        Log.d("youtubePlayer", "Pause = " + mArticlePosition);
//        try {
//            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//            Log.d("youtubePlayer", "Pauseholder = " + holder);
//            if (holder != null) {
//                if (holder instanceof LargeCardViewHolder) {
//                    ((LargeCardViewHolder) holder).storiesProgressView.pause();
//                } else if (holder instanceof SmallCardViewHolder) {
//                    ((SmallCardViewHolder) holder).storiesProgressView.pause();
//                } else if (holder instanceof VideoViewHolder) {
//
////                    ((VideoViewHolder) holder).pause();
//                } else if (holder instanceof YoutubeViewHolder) {
//                    Log.d("youtubePlayer", "Pause utube");
//                    ((YoutubeViewHolder) holder).bulletPause();
//                }
//            }
//
//            PlayerSelector playerSelector = PlayerSelector.NONE;
////            mListRV.setPlayerSelector(playerSelector);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private void initView(View dialogView) {
        setting2 = dialogView.findViewById(R.id.setting2);
        tag = dialogView.findViewById(R.id.tag);
        back = dialogView.findViewById(R.id.iv_back);
        menu = dialogView.findViewById(R.id.menu);
        favCount = dialogView.findViewById(R.id.favCount);
        follow_txt = dialogView.findViewById(R.id.follow_txt);
        followBottomBar = dialogView.findViewById(R.id.follow_bottom_bar);
        follow_progress = dialogView.findViewById(R.id.follow_progress);
        follow = dialogView.findViewById(R.id.cl_follow);
        favIcon = dialogView.findViewById(R.id.favIcon);
        llFavIcon = dialogView.findViewById(R.id.ll_favorite);
        commentCount = dialogView.findViewById(R.id.commentCount);
        share = dialogView.findViewById(R.id.share);
        comment = dialogView.findViewById(R.id.comment);
        image_bg = dialogView.findViewById(R.id.image_bg);
        cover = dialogView.findViewById(R.id.cover);
        image = dialogView.findViewById(R.id.image);
        name = dialogView.findViewById(R.id.name);
        desc = dialogView.findViewById(R.id.desc);
        username = dialogView.findViewById(R.id.username);
        channel_btn = dialogView.findViewById(R.id.channel_btn);
        viewArticle = dialogView.findViewById(R.id.viewArticle);
        noData = dialogView.findViewById(R.id.noData);
//        mListRV = dialogView.findViewById(R.id.viewMoreList);
//        gotoTop = dialogView.findViewById(R.id.gotoTop);
        appBarLayout = dialogView.findViewById(R.id.appBarLayout);
//        progress = dialogView.findViewById(R.id.progress);
        cvVideoThumbnail = dialogView.findViewById(R.id.cardViewImg);
        rlChannelDetail = dialogView.findViewById(R.id.rl_channel_detail);
        tvTime = dialogView.findViewById(R.id.tv_time);
        Constants.rvm = (Constants.rvm) + 1;

        String time = Utils.getTimeAgo(Utils.getDate(mReelsItem.getPublishTime()), getContext());

        if (!TextUtils.isEmpty(time)) {
            tvTime.setText(time);
        }

        if (!TextUtils.isEmpty(mReelsItem.getLink())) {
            viewArticle.setVisibility(View.VISIBLE);
        } else {
            viewArticle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void authors(AuthorListResponse response) {

    }

    @Override
    public void reels(ReelResponse response) {

    }

    @Override
    public void loaderShow(boolean flag) {
//        if (flag) {
//            progress.setVisibility(View.VISIBLE);
//        } else {
//            progress.setVisibility(View.GONE);
//        }
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void error404(String error) {

    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {
        if (getActivity() == null) return;
        if (response != null && response.getArticles() != null && response.getArticles().size() > 0) {
            noData.setVisibility(View.GONE);

            if (TextUtils.isEmpty(mNextPage)) {

                if (response.getReels() != null && response.getReels().size() > 0) {

                    Article article2 = new Article();
                    article2.setTitle(getActivity().getString(R.string.suggested_reels));
                    article2.setTabTitle(getActivity().getString(R.string.suggested_reels));
                    article2.setType("TITLE");
                    contentArrayList.add(article2);

                    reels.clear();
                    reels.addAll(response.getReels());

                    Article adArticle2 = new Article();
//                    adArticle2.setTitle(getActivity().getString(R.string.suggested_reels));
                    adArticle2.setType("REELS");
                    adArticle2.setReels(reels);
                    contentArrayList.add(adArticle2);
                }

                Article article1 = new Article();
                article1.setTitle(getActivity().getString(R.string.suggested_articles));
                article1.setTabTitle(getActivity().getString(R.string.suggested_articles));
                article1.setType("TITLE");
                contentArrayList.add(article1);
            }

            if (response.getMeta() != null) {
                mNextPage = response.getMeta().getNext();
            }

            for (int position = 0; position < response.getArticles().size(); position++) {
                Article article = response.getArticles().get(position);
                if (prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                    int interval = 10;
                    if (prefConfig.getAds().getInterval() != 0) {
                        interval = prefConfig.getAds().getInterval();
                    }

                    if (contentArrayList.size() != 0 && contentArrayList.size() % interval == 0) {
                        Log.e("ADS", "AD Added");
                        Article adArticle1 = new Article();
                        if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                            adArticle1.setType("FB_Ad");
                        } else {
                            adArticle1.setType("G_Ad");
                        }
                        contentArrayList.add(adArticle1);
                    }
                }
                contentArrayList.add(article);
            }

//            if (mCardAdapter != null)
//                mCardAdapter.notifyDataSetChanged();
//
//            selectCardPosition(mArticlePosition);

            if (swipeListener != null) {
                swipeListener.muteIcon(true);
            }
        } else {

            if (TextUtils.isEmpty(mNextPage))
                noData.setVisibility(View.VISIBLE);

            if (contentArrayList.size() == 0) {
                if (swipeListener != null) {
                    swipeListener.muteIcon(false);
                }
                contentArrayList.clear();
//                if (mCardAdapter != null)
//                    mCardAdapter.notifyDataSetChanged();
            }
        }
    }

    public void scrollToTop() {
//        if (mListRV != null)
//            mListRV.scrollToPosition(0);
        if (cardLinearLayoutManager != null)
            cardLinearLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    private void callNextPage() {
        if (presenter != null)
            presenter.loadRelatedArticle(mNextPage, mReelsItem.getId(), prefConfig.isReaderMode());
    }

    public void selectCardPosition(int position) {
//        if (position > -1) {
//            mArticlePosition = position;
//            if (mCardAdapter != null)
//                mCardAdapter.setCurrentArticlePosition(mArticlePosition);
//            if (contentArrayList.size() > 0 && position < contentArrayList.size()) {
//                for (int i = 0; i < contentArrayList.size(); i++) {
//                    contentArrayList.get(i).setSelected(false);
//                }
//                contentArrayList.get(position).setSelected(true);
//            }
//        }
    }

    public void pauseOnlyBullets() {
//        try {
//            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//            if (holder != null) {
//                if (holder instanceof LargeCardViewHolder) {
//                    ((LargeCardViewHolder) holder).storiesProgressView.pause();
//                } else if (holder instanceof SmallCardViewHolder) {
//                    ((SmallCardViewHolder) holder).storiesProgressView.pause();
//                } else if (holder instanceof VideoViewHolder) {
//
////                    ((VideoViewHolder) holder).pause();
//                } else if (holder instanceof YoutubeViewHolder) {
//                    ((YoutubeViewHolder) holder).bulletPause();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void setRvScrollListener() {
//        mListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());
//                if (layoutManager != null && behavior != null) {
//                    final int firstPosition = layoutManager.findFirstVisibleItemPosition();
//                    if (firstPosition > 2) {
//                        if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                            if (bottomSheet != null)
//                                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
//                        }
//                        gotoTop.setVisibility(View.VISIBLE);
//                    } else {
//                        gotoTop.setVisibility(View.GONE);
//                    }
//                }
//
//                //pause bullet and audio while scrolling
//                if (newState == SCROLL_STATE_DRAGGING) {
//                    if (goHome != null)
//                        goHome.sendAudioEvent("pause");
//                    pauseOnlyBullets();
//                }
//
//                if (newState == SCROLL_STATE_IDLE) {
//                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
//                            Events.ARTICLE_SWIPE);
//                    Constants.auto_scroll = true;
//
//                    final int firstPosition = layoutManager.findFirstVisibleItemPosition();
//
//                    if (firstPosition != -1) {
//
//                        Rect rvRect = new Rect();
//                        mListRV.getGlobalVisibleRect(rvRect);
//
//                        Rect rowRect = new Rect();
//                        layoutManager.findViewByPosition(firstPosition).getGlobalVisibleRect(rowRect);
//
//                        int percentFirst;
//                        if (rowRect.bottom >= rvRect.bottom) {
//                            int visibleHeightFirst = rvRect.bottom - rowRect.top;
//                            percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
//                        } else {
//                            int visibleHeightFirst = rowRect.bottom - rvRect.top;
//                            percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
//                        }
//
//                        if (percentFirst > 100)
//                            percentFirst = 100;
//
//                        int VISIBILITY_PERCENTAGE = 90;
//
//                        int copyOfmArticlePosition = mArticlePosition;
//
//                        Log.d("slections", "onScrollStateChanged: percentFirst = " + percentFirst);
//
//                        /* based on percentage of item visibility, select current or next article
//                         *  if prev position is same as new pos then dont reset the article
//                         * */
//                        if (percentFirst >= VISIBILITY_PERCENTAGE) {
//                            Log.d("slections", "onScrollStateChanged: percentage greater");
//                            mArticlePosition = firstPosition;
//                            if (mArticlePosition == 0) {
////                                mArticlePosition++;
////                                Log.d("slections", "onScrollStateChanged: mArticlePosition = 0");
////                                selectCardPosition(mArticlePosition);
////
////                                if (mCardAdapter != null)
////                                    mCardAdapter.notifyDataSetChanged();
//////                                }
//                            } else if (mArticlePosition == contentArrayList.size() - 1) {
//                                Log.d("slections", "onScrollStateChanged: last = 0");
//
//                                //on fast scrolling select the last one in the last
//                                selectCardPosition(mArticlePosition);
//
//                                if (mCardAdapter != null)
//                                    mCardAdapter.notifyDataSetChanged();
//                            } else if (copyOfmArticlePosition == mArticlePosition) {
//                                Log.d("slections", "onScrollStateChanged: copy = new pos");
//                                //scroll rested on same article so resume audio and bullet
//                                try {
//                                    RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//                                    if (holder != null) {
//                                        if (holder instanceof LargeCardViewHolder) {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("resume");
//                                            ((LargeCardViewHolder) holder).storiesProgressView.resume();
//                                        } else if (holder instanceof SmallCardViewHolder) {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("resume");
//                                            ((SmallCardViewHolder) holder).storiesProgressView.resume();
//                                        } else if (holder instanceof YoutubeViewHolder) {
//                                            ((YoutubeViewHolder) holder).youtubeResume();
//                                        } else {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("stop_destroy");
//                                        }
//                                    } else {
//                                        Log.d("audiotest", "scroll : stop_destroy");
//                                        if (goHome != null)
//                                            goHome.sendAudioEvent("stop_destroy");
//                                    }
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                if (copyOfmArticlePosition != mArticlePosition) {
//                                    Log.d("slections", "onScrollStateChanged: select new");
//                                    //scrolled to a new pos, so select new article
//                                    selectCardPosition(mArticlePosition);
//
//                                    if (mCardAdapter != null)
//                                        mCardAdapter.notifyDataSetChanged();
//                                }
//                            }
//                        } else {
//                            Log.d("slections", "onScrollStateChanged: percentage less");
//                            mArticlePosition = firstPosition;
//                            mArticlePosition++;
//
//                            if (copyOfmArticlePosition != mArticlePosition) {
//                                Log.d("slections", "onScrollStateChanged: select new");
//                                //scrolled to a new pos, so select new article
//                                selectCardPosition(mArticlePosition);
//
//                                if (mCardAdapter != null)
//                                    mCardAdapter.notifyDataSetChanged();
//                            } else {
//                                Log.d("slections", "onScrollStateChanged: else");
//                                //scroll rested on same article so resume audio and bullet
//                                try {
//                                    RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//                                    if (holder != null) {
//                                        if (holder instanceof LargeCardViewHolder) {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("resume");
//                                            ((LargeCardViewHolder) holder).storiesProgressView.resume();
//                                        } else if (holder instanceof SmallCardViewHolder) {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("resume");
//                                            ((SmallCardViewHolder) holder).storiesProgressView.resume();
//                                        } else if (holder instanceof YoutubeViewHolder) {
//                                            ((YoutubeViewHolder) holder).youtubeResume();
//                                        } else {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("stop_destroy");
//                                        }
//                                    } else {
//                                        Log.d("audiotest", "scroll : stop_destroy");
//                                        if (goHome != null)
//                                            goHome.sendAudioEvent("stop_destroy");
//                                    }
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (contentArrayList.size() - 3 <= cardLinearLayoutManager.findLastVisibleItemPosition() && !isLast()) {
//                    if (!isApiCalling) {
//                        isApiCalling = true;
//                        callNextPage();
//                    }
//                }
//
//            }
//        });
    }

    private boolean isLast() {
        return mNextPage.equalsIgnoreCase("");
    }

    @Override
    public void successArticle(Article article) {

    }

    @Override
    public void homeSuccess(HomeResponse homeResponse, String currentPage) {

    }

    @Override
    public void nextPosition(int position) {
        if (goHome != null)
            goHome.sendAudioEvent("pause");
        if (contentArrayList.size() > 0 && position < contentArrayList.size() && position > -1) {
            mArticlePosition = position;
            String type = contentArrayList.get(mArticlePosition).getType();
            if (!TextUtils.isEmpty(type) && (type.equalsIgnoreCase("suggested_reels") || type.equalsIgnoreCase("suggested_authors"))) {
                mArticlePosition++;
            }
            selectCardPosition(mArticlePosition);
            if (cardLinearLayoutManager != null)
                cardLinearLayoutManager.scrollToPositionWithOffset(mArticlePosition, 0);
//            if (mCardAdapter != null)
//                mCardAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void nextPositionNoScroll(int position, boolean shouldNotify) {
//        if (goHome != null)
//            goHome.sendAudioEvent("pause");
//        RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);
//        int oldPos = mArticlePosition;
//        if (holder instanceof LargeCardViewHolder)
//            ((LargeCardViewHolder) holder).selectUnselectedItem(position, contentArrayList.get(position));
//        else if (holder instanceof SmallCardViewHolder)
//            ((SmallCardViewHolder) holder).selectUnselectedItem(position, contentArrayList.get(position));
//
//        selectCardPosition(position);
//
//        RecyclerView.ViewHolder holderOld = mListRV.findViewHolderForAdapterPosition(oldPos);
//        if (holderOld instanceof SmallCardViewHolder)
//            ((SmallCardViewHolder) holderOld).unselect(contentArrayList.get(oldPos));
//        else if (holderOld instanceof LargeCardViewHolder)
//            ((LargeCardViewHolder) holderOld).unselect(contentArrayList.get(oldPos));
//        else if (holderOld instanceof VideoViewHolder)
//            mCardAdapter.notifyItemChanged(oldPos);
////            ((VideoViewHolder) holderOld).pause();
//        else if (holderOld instanceof YoutubeViewHolder)
//            mCardAdapter.notifyItemChanged(oldPos);
    }

    @Override
    public void onItemHeightMeasured(int height) {

    }

    @Override
    public void onclickReel(ArrayList<ReelsItem> reelsItems, ReelsItem item) {
        Log.e("onclickReel", "onclickReel: ");
        //add the new reels page
//
//        Fragment reelInnerFragment = new ReelInnerActivity();
//        FragmentManager fragmentManager = getChildFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        Bundle bundle = new Bundle();
//        bundle.putString(ReelInnerActivity.REEL_F_AUTHOR_ID,item.getId());
//        bundle.putString(ReelInnerActivity.REEL_F_CONTEXT,item.getContext());
//        bundle.putString(ReelInnerActivity.REEL_F_MODE,"public");
//        bundle.putInt(ReelInnerActivity.REEL_POSITION,0);
//        bundle.putParcelableArrayList(ReelInnerActivity.REEL_F_DATALIST,reelsItems);
//
//        reelInnerFragment.setArguments(bundle);
//
//        fragmentTransaction.add(R.id.framereel, reelInnerFragment);
//        fragmentTransaction.commit();


        Intent intent = new Intent(requireContext(), ReelInnerActivity.class);
        intent.putExtra(ReelInnerActivity.REEL_F_AUTHOR_ID, item.getId());
        intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, item.getContext());
        intent.putExtra(ReelInnerActivity.REEL_F_MODE, "public");
        intent.putExtra(ReelInnerActivity.REEL_POSITION, 0);
        intent.putParcelableArrayListExtra(ReelInnerActivity.REEL_F_DATALIST, reelsItems);
//                intent.putParcelableArrayListExtra("datalist", data);
        requireContext().startActivity(intent);
    }

    public interface OnShareListener {
        void onShareClicked();

        void onFollow(ReelsItem mReelsItem);

        void viewMoreDismissed();
    }
}
