package com.ziro.bullet.activities;

import static com.ziro.bullet.background.BroadcastEmitter.BG_ERROR;
import static com.ziro.bullet.background.BroadcastEmitter.BG_PUBLISHED;
import static com.ziro.bullet.background.BroadcastEmitter.TYPE_BACKGROUND_PROCESS;
import static com.ziro.bullet.utills.Constants.ACTION_UPDATE_EVENT;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.background.BackgroundEvent;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.bottomSheet.PostArticleBottomSheet;
import com.ziro.bullet.bottomSheet.ProfileBottomSheet;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.data.POST_TYPE;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.fragments.AuthorArticleFragment;
import com.ziro.bullet.fragments.AuthorReelsFragment;
import com.ziro.bullet.fragments.CommentPopup;
import com.ziro.bullet.fragments.CreateYoutubePopup;
import com.ziro.bullet.fragments.GuidelinePopup;
import com.ziro.bullet.fragments.ProfileArticleFragment;
import com.ziro.bullet.fragments.ProfileReelsFragment;
import com.ziro.bullet.interfaces.ApiResponseInterface;
import com.ziro.bullet.interfaces.ChannelDetailsInterface;
import com.ziro.bullet.interfaces.ProfileApiCallback;
import com.ziro.bullet.interfaces.UpdateCallback;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.ImageCrop;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.model.SelectedChannel;
import com.ziro.bullet.presenter.ChannelDetailsPresenter;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.MenuFragmentPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class ChannelDetailsActivity extends BaseActivity
        implements ChannelDetailsInterface, ProfileApiCallback, ProfileArticleFragment.OnFragmentInteractionListener,
        ProfileReelsFragment.OnFragmentInteractionListener {

    public static final int PROFILE_IMAGE_REQUEST = 10;
    public static final int COVER_IMAGE_REQUEST = 11;
    public static final int PERMISSION_REQUEST = 745;
    public static final int PERMISSION_REQUEST_REELS = 725;

    public static final int PROFILE_CROP_IMAGE_REQUEST = 231;
    public static final int PROFILE_CROP_COVER_REQUEST = 232;

    public static final int FOLLOW_REQUEST = 300;

    //TABS
    private static ArrayList<String> mTabTitleList = new ArrayList<>();
    //IMAGE CHANGE SETUP
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private ChannelDetailsPresenter presenter;
    private MenuFragmentPresenter menuFragmentPresenter;

    private ImageView back;
    private CardView mod_tools;
    private TextView channel_btn_txt;
    private ProgressBar channel_btn_progress;
    private CardView channel_btn;
    private CardView arrow_btn;
    //    private TextView title2;
    //    private View text_bottom_4, text_bottom_5;
    private TextView name;
    private ImageView cover;
    //    private ImageView dot_;
    private ImageView edit_cover_image;
    private CircleImageView image;
    private ImageView edit_profile_image;
    private ImageView coverBack;
    private TextView followers;
    private ImageView setting;
    private TextView posts;
    private TextView desc;
    private TextView username;
    private ImageView mLoader;
    private RelativeLayout mRlprogress;
    private ViewSwitcher topViewSwitcher;
    private PrefConfig mPrefConfig;
    private Source mSource;
    private File profileImageFile;
    private File coverImageFile;
    private ChannelPagerAdapter channelPagerAdapter;
    private ViewPager viewpager;
    private TabLayout tabLayout;
    private RelativeLayout progress;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private PostArticleBottomSheet postArticleBottomSheet = null;
    private CommentPopup commentPopup;
    private boolean isArticle = false;
    private String id;
    private final BroadcastReceiver updateEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (presenter != null && !TextUtils.isEmpty(id)) {
                presenter.getDetails(id);
            }
        }
    };
    private String article_id;
    private int position;
    private TYPE type;
    private boolean firstTimeLoading = true;
    private PictureLoadingDialog mLoadingDialog;
    private GestureDetectorCompat gestureDetectorCompat;
    private RelativeLayout main_layout;
    private CoordinatorLayout htab_maincontent;
    private ProfileBottomSheet shareBottomSheet;

    private TextView toolbarTextTitle;
    private AppBarLayout appBarLayout;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Utils.checkAppModeColor(this);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_channel_details_new);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        gestureDetectorCompat = new GestureDetectorCompat(this, new SwipeGestureListener());

        bindViews();
        init();
        setData();
        setListener();


        if (presenter != null && !TextUtils.isEmpty(id)) {
            presenter.getDetails(id);
            if (mTabTitleList != null && channelPagerAdapter != null) {
                mTabTitleList.clear();
                viewpager.setAdapter(null);
            }
        }

        EventBus.getDefault().register(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(updateEvent, new IntentFilter(ACTION_UPDATE_EVENT));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Constants.notishare = false;
        Constants.onResumeReels = true;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("source", mSource);
        returnIntent.putExtra("id", article_id);
        returnIntent.putExtra("position", position);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.left_to_right_500, R.anim.right_to_left_500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int range = appBarLayout.getTotalScrollRange();
                float alpha = Math.abs(verticalOffset) / (float) range;
                Log.d("onOffsetChanged", "onOffsetChanged: " + alpha);
                toolbarTextTitle.setAlpha(alpha);
            }
        });
        desc.setOnClickListener(v -> {
            if (desc.getMaxLines() == 3) {
                desc.setMaxLines(Integer.MAX_VALUE);
            } else {
                desc.setMaxLines(3);
            }
            desc.requestLayout();
        });
        back.setOnClickListener(v -> {
            onBackPressed();
        });
        edit_profile_image.setOnClickListener(v -> {
            fromGallery(PROFILE_IMAGE_REQUEST);
        });
        edit_cover_image.setOnClickListener(v -> {
            fromGallery(COVER_IMAGE_REQUEST);
        });
        channel_btn.setOnClickListener(v -> {

            channel_btn_txt.setVisibility(View.INVISIBLE);
            channel_btn_progress.setVisibility(View.VISIBLE);

            if (mSource != null && followUnfollowPresenter != null) {
                if (mSource.isFavorite()) {
                    followUnfollowPresenter.unFollowSource(mSource.getId(), position, (position, flag) -> {
                        if (flag) {
                            Constants.sourceStatusChanged = mSource.getId();
                            Constants.isSourceDataChange = true;
                            Constants.followStatus = String.valueOf(false);
                            Constants.itemPosition = position;
                            channel_btn_txt.setText(getResources().getString(R.string.follow));
                            channel_btn.setCardBackgroundColor(getResources().getColor(R.color.theme_color_1));
                            mSource.setFavorite(!mSource.isFavorite());
                            channel_btn_txt.setVisibility(View.VISIBLE);
                            channel_btn_progress.setVisibility(View.GONE);
                        }
                    });
                } else {
                    followUnfollowPresenter.followSource(mSource.getId(), position, (position, flag) -> {
                        if (flag) {
                            Constants.sourceStatusChanged = mSource.getId();
                            Constants.isSourceDataChange = true;
                            Constants.followStatus = String.valueOf(true);
                            Constants.itemPosition = position;
                            channel_btn_txt.setText(getResources().getString(R.string.unfollow));
                            channel_btn.setCardBackgroundColor(getResources().getColor(R.color.following_text_color));
                            mSource.setFavorite(!mSource.isFavorite());
                            channel_btn_txt.setVisibility(View.VISIBLE);
                            channel_btn_progress.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        mod_tools.setOnClickListener(v -> {
            Intent intent = new Intent(ChannelDetailsActivity.this, ModeratorToolsActivity.class);
            intent.putExtra("SOURCE", new Gson().toJson(mSource));
            mPrefConfig.setChannel(new SelectedChannel(mSource.getId(), mSource.getIcon(), mSource.getName()));
            startActivity(intent);
        });
        followers.setOnClickListener(v -> {
            if (mSource != null && !TextUtils.isEmpty(mSource.getId()) && mSource.isOwn()) {
                FollowersActivity.start(ChannelDetailsActivity.this, mSource.getId());
            }
        });
        setting.setOnClickListener(v -> {
            if (mSource != null) {
                showBottomSheetDialog(mSource.getId(), mSource.getName(), dialog -> {
                });
            }
        });
        main_layout.setOnTouchListener((v, event) -> {
            return gestureDetectorCompat.onTouchEvent(event);
            // return false;
        });
        htab_maincontent.setOnTouchListener((v, event) -> {
            return gestureDetectorCompat.onTouchEvent(event);
            // return false;
        });
    }

    private void showBottomSheetDialog(String authorId, String authorName, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ProfileBottomSheet(this, "sources", authorId, authorName);
        }
        shareBottomSheet.show(onDismissListener);
    }

    private void initTabs(Source source) {
        mTabTitleList.clear();
        if (!source.isOwn()) {
            if (source.isHas_reel()) {
                tabLayout.setVisibility(View.VISIBLE);
                mTabTitleList.add(getString(R.string.newsreels));
            } else {
                tabLayout.setVisibility(View.GONE);
            }
        } else {
            tabLayout.setVisibility(View.VISIBLE);
            mTabTitleList.add(getString(R.string.newsreels));
        }
        mTabTitleList.add(getString(R.string.articles));
//        mTabTitleList.add(getString(R.string.favorite));
        channelPagerAdapter = new ChannelPagerAdapter(getSupportFragmentManager(), source, mTabTitleList.size(), type, new UpdateCallback() {
            @Override
            public void onUpdate() {
                if (presenter != null && !TextUtils.isEmpty(id)) {
                    presenter.getDetails(id);
                }
            }
        });
        viewpager.setAdapter(channelPagerAdapter);
        viewpager.setOffscreenPageLimit(mTabTitleList.size());
        tabLayout.setupWithViewPager(viewpager);
        setupTabIcons();
    }

    private void setData() {
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            type = (TYPE) getIntent().getSerializableExtra("type");
            article_id = getIntent().getStringExtra("article_id");
            position = getIntent().getIntExtra("position", -1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstTimeLoading) {
            if (presenter != null && !TextUtils.isEmpty(id)) {
                presenter.getDetails(id);
            }
        }

    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        progress = findViewById(R.id.progress);
        tabLayout = findViewById(R.id.tabLayout);
        coverBack = findViewById(R.id.coverBack);
        viewpager = findViewById(R.id.viewpager);
        desc = findViewById(R.id.desc);
        username = findViewById(R.id.username);
        mod_tools = findViewById(R.id.mod_tools);
        channel_btn = findViewById(R.id.channel_btn);
        channel_btn_progress = findViewById(R.id.channel_btn_progress);
//        dot_ = findViewById(R.id.dot_);
        channel_btn_txt = findViewById(R.id.channel_btn_txt);
        arrow_btn = findViewById(R.id.arrow_btn);
        cover = findViewById(R.id.cover);
        image = findViewById(R.id.image);
        back = findViewById(R.id.img_left_arrow);
        toolbarTextTitle = findViewById(R.id.toolbarName);
        edit_cover_image = findViewById(R.id.edit_cover_image);
        edit_profile_image = findViewById(R.id.edit_profile_image);
//        title2 = findViewById(R.id.title2);
        name = findViewById(R.id.name);
        followers = findViewById(R.id.followers);
        setting = findViewById(R.id.setting);
        posts = findViewById(R.id.posts);
        topViewSwitcher = findViewById(R.id.top_view_switcher);
        main_layout = findViewById(R.id.main_layout);
        htab_maincontent = findViewById(R.id.htab_maincontent);
        // mLoader = findViewById(R.id.loader);
        // mRlprogress = findViewById(R.id.progress);
        channel_btn.setCardBackgroundColor(getResources().getColor(R.color.theme_color_1));

    }

    private void init() {
        mPrefConfig = new PrefConfig(this);
        followUnfollowPresenter = new FollowUnfollowPresenter(this);
        presenter = new ChannelDetailsPresenter(this, this);
        menuFragmentPresenter = new MenuFragmentPresenter(this);
    }

    private void showVerifiedIcon(@NotNull Source sourceObj) {
        if (sourceObj.isVerified()) {
            if (Utils.isRTL()) {
                name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified, 0, 0, 0);
            } else {
                name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0);
            }
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        if (firstTimeLoading) {
//            Utils.loadSkeletonLoader(topViewSwitcher, flag);
            progress.setVisibility(flag ? View.VISIBLE : View.GONE);
        }

        if (!flag)
            dismissProgressDialog();
    }

    @Override
    public void error(String error, String img) {
        Utils.showSnacky(followers, error);
    }

    @Override
    public void success() {
        Utils.showSnacky(followers, getResources().getString(R.string.updated_successfully));
    }

    @Override
    public void error(String error) {
        Utils.showSnacky(followers, error);
    }

    @Override
    public void success(Source source) {

        if (source != null) {
            this.mSource = source;
            if (!TextUtils.isEmpty(source.getImage())) {
                Picasso.get()
                        .load(source.getImage())
                        .into(cover);
                Picasso.get()
                        .load(source.getImage())
                        .transform(new BlurTransformation(this, 25, 3))
                        .into(coverBack);
            }
            if (!TextUtils.isEmpty(source.getIcon()))
                Picasso.get()
                        .load(source.getIcon())
                        .into(image);
            if (!TextUtils.isEmpty(source.getName())) {
                name.setText(source.getName());
                toolbarTextTitle.setText(source.getName());
            }
            if (!TextUtils.isEmpty(source.getUsername())) {
                username.setText("@" + source.getUsername());
                username.setVisibility(View.VISIBLE);
            } else {
                username.setVisibility(View.GONE);
            }
            followers.setText(source.getFollower_count() + " ");
            posts.setText(source.getPost_count() + " ");
            if (!TextUtils.isEmpty(source.getDescription())) {
                desc.setText(source.getDescription());
                desc.setVisibility(View.VISIBLE);
            } else {
                desc.setVisibility(View.GONE);
            }

//            dot_.setVisibility(View.VISIBLE);

            if (source.isFavorite()) {
                channel_btn_txt.setText(getResources().getString(R.string.following));
                channel_btn.setCardBackgroundColor(getResources().getColor(R.color.following_text_color));
            } else {
                channel_btn_txt.setText(getResources().getString(R.string.follow));
                channel_btn.setCardBackgroundColor(getResources().getColor(R.color.theme_color_1));
            }
            if (source.isOwn()) {
                edit_cover_image.setVisibility(View.VISIBLE);
                edit_profile_image.setVisibility(View.VISIBLE);
                mod_tools.setVisibility(View.VISIBLE);
                channel_btn.setVisibility(View.GONE);
            } else {
                edit_cover_image.setVisibility(View.GONE);
                edit_profile_image.setVisibility(View.GONE);
                mod_tools.setVisibility(View.GONE);
                channel_btn.setVisibility(View.VISIBLE);
            }

            showVerifiedIcon(source);
            if (firstTimeLoading) {
                initTabs(source);
            }
        }
        firstTimeLoading = false;
    }

    @Override
    public void update(String id, String imageUrl, String coverUrl) {
        if (mSource != null) {
            if (!TextUtils.isEmpty(coverUrl)) {
                mSource.setImage(coverUrl);
                Picasso.get()
                        .load(mSource.getImage())
                        .into(cover);
                Picasso.get()
                        .load(mSource.getImage())
                        .transform(new BlurTransformation(this, 25, 3))
                        .into(coverBack);
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                mSource.setIcon(imageUrl);
                Picasso.get()
                        .load(mSource.getIcon())
                        .into(image);
            }
        }
    }

    public void fromGallery(int type) {
        if (PermissionChecker
                .checkSelfPermission(ChannelDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (type == PROFILE_IMAGE_REQUEST)
                GalleryPicker.openGalleryOnlyPicturesForProfilePic(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), type);
            else
                GalleryPicker.openGalleryOnlyPictures(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), type);
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PROFILE_IMAGE_REQUEST || requestCode == COVER_IMAGE_REQUEST) && resultCode == RESULT_OK && null != data) {
            LocalMedia localMedia = PictureSelector.obtainMultipleResult(data).get(0);

            File file;
            Uri uri;
            String fileName = localMedia.getFileName();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                file = new File(localMedia.getAndroidQToPath());
                uri = Uri.fromFile(file);
            } else {
                file = new File(localMedia.getPath());
                uri = Uri.fromFile(file);
            }

            loadSelectedImage(requestCode, fileName, uri);

            //            showProgressDialog();
//            LocalMedia localMedia = PictureSelector.obtainMultipleResult(data).get(0);
//
//            File file;
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                file = new File(localMedia.getAndroidQToPath());
//            } else {
//                file = new File(localMedia.getPath());
//            }
//
//
//            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<File>>() {
//
//                @Override
//                public List<File> doInBackground() {
//                    File modifiedFile;
//                    try {
//                        modifiedFile = Utils.getFileFromBitmap(ChannelDetailsActivity.this, "imageProfile", Utils.modifyOrientation(file.getAbsolutePath()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        modifiedFile = file;
//                    }
//
//                    return Collections.singletonList(modifiedFile);
//                }
//
//                @Override
//                public void onSuccess(List<File> files) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        loadSelectedImage(requestCode, new File(files.get(0).getPath()), Uri.parse(files.get(0).getPath()));
//                    } else {
//                        loadSelectedImage(requestCode, new File(files.get(0).getPath()), Uri.parse(files.get(0).getPath()));
//                    }
//                }
//            });
        } else if (resultCode == RESULT_OK) {
            if (requestCode == PERMISSION_REQUEST_REELS) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() == 0) {
                    VideoInfo videoInfo = data.getParcelableExtra("video_info");

                    if (isFinishing()) return;
                    Intent intent = new Intent(this, PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.REELS);
                    intent.putExtra("MODE", MODE.ADD);

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
//                        intent.putExtra("uri", localMedia.getAndroidQToPath());
//                    } else {
                    intent.putExtra("file", new File(videoInfo.getPath()));
                    intent.putExtra("uri", videoInfo.getPath());
//                    }
                    intent.putExtra("video_info", videoInfo);
                    startActivity(intent);
                } else
                    loadSelectedVideo(selectList.get(0));
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() == 0) {
                    VideoInfo videoInfo = data.getParcelableExtra("video_info");

                    if (isFinishing()) return;
                    Intent intent = new Intent(this, PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
                    intent.putExtra("MODE", MODE.ADD);

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
//                        intent.putExtra("uri", localMedia.getAndroidQToPath());
//                    } else {
                    intent.putExtra("file", new File(videoInfo.getPath()));
                    intent.putExtra("uri", videoInfo.getPath());
//                    }
                    intent.putExtra("video_info", videoInfo);
                    startActivity(intent);
                } else {
                    loadImageArticle(selectList.get(0));
                }

            } else if (requestCode == ImageCrop.REQUEST_CROP) {
                Uri resultUri = ImageCrop.getOutput(data);
                String cutPath = resultUri.getPath();

                Intent intent = new Intent(ChannelDetailsActivity.this, PostArticleActivity.class);

                intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
                intent.putExtra("MODE", MODE.ADD);
                intent.putExtra("file", new File(cutPath));
                intent.putExtra("uri", cutPath);
                startActivity(intent);
            } else if (requestCode == PROFILE_CROP_IMAGE_REQUEST) {
                Uri cropProfileUri = ImageCrop.getOutput(data);
                String cropProfileCutPath = cropProfileUri.getPath();

                profileImageFile = new File(cropProfileCutPath);
                if (mSource != null) {
                    presenter.uploadMedia(mSource.getId(), profileImageFile, "icon");
                }
            } else if (requestCode == PROFILE_CROP_COVER_REQUEST) {
                Uri cropProfileUri = ImageCrop.getOutput(data);
                String cropProfileCutPath = cropProfileUri.getPath();

                if (mSource != null) {
                    coverImageFile = new File(cropProfileCutPath);
                    presenter.uploadMedia(mSource.getId(), coverImageFile, "cover");
                }
            }
        }
    }

    private void loadSelectedImage(int type, String fileName, Uri uri) {
        File f = new File(getCacheDir(), fileName + System.currentTimeMillis());
        try {
            f.createNewFile();
            int cropRequest;
            float frameAspectRatio;
            if (type == PROFILE_IMAGE_REQUEST) {
                cropRequest = PROFILE_CROP_IMAGE_REQUEST;
                frameAspectRatio = 1;
            } else {
                cropRequest = PROFILE_CROP_COVER_REQUEST;
                frameAspectRatio = 1.77f;
            }
            ImageCrop.of(
                            uri,
                            Uri.fromFile(f)
                            , frameAspectRatio, true
                    )
                    .start(this, cropRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
        return "";
    }

    @Override
    public void onUploadClicked() {
        if (mPrefConfig != null && mPrefConfig.isUserObject() != null && mPrefConfig.isUserObject().isSetup()) {
            if (!mPrefConfig.getGuidelines()) {
                GuidelinePopup popup = new GuidelinePopup(ChannelDetailsActivity.this, (flag, msg) -> {
                    if (flag) {
                        if (menuFragmentPresenter != null) {
                            menuFragmentPresenter.acceptGuidelines(new ApiResponseInterface() {
                                @Override
                                public void _success() {
                                    if (mPrefConfig != null) {
                                        mPrefConfig.setGuidelines(true);
                                        if (mSource != null) {
                                            mPrefConfig.setChannel(new SelectedChannel(mSource.getId(), mSource.getIcon(), mSource.getName()));
                                        }
                                    }
                                    if (postArticleBottomSheet == null) {
                                        postArticleBottomSheet = new PostArticleBottomSheet(ChannelDetailsActivity.this, option -> postContent(option));
                                    }
                                    postArticleBottomSheet.show(dialog -> {

                                    });
                                }

                                @Override
                                public void _other(int code) {

                                }
                            });
                        }
                    }
                });
                popup.showDialog();
            } else {
                if (mPrefConfig != null) {
                    if (mSource != null) {
                        mPrefConfig.setChannel(new SelectedChannel(mSource.getId(), mSource.getIcon(), mSource.getName()));
                    }
                }
                if (postArticleBottomSheet == null) {
                    postArticleBottomSheet = new PostArticleBottomSheet(ChannelDetailsActivity.this, this::postContent);
                }
                postArticleBottomSheet.show(dialog -> {

                });
            }
        } else {
            if (commentPopup == null) {
                commentPopup = new CommentPopup(ChannelDetailsActivity.this);
            } else {
                commentPopup.showDialog(getString(R.string.before_you_can_post_you_need_to_update_your_profile));
            }
        }
    }

    private void postContent(String option) {
        switch (option) {
            case "YOUTUBE":
                showYoutubeUploadView();
                break;
            case "REELS":
                postReels();
                break;
            case "MEDIA":
                postArticle();
                break;
        }
    }

    private void showYoutubeUploadView() {
        CreateYoutubePopup createYoutubePopup = new CreateYoutubePopup(ChannelDetailsActivity.this);
        createYoutubePopup.showDialog(article -> {
            if (article != null) {
                Log.e("uploadImageVideo", "-createSuccess here-> " + new Gson().toJson(article));
                if (isFinishing()) return;
                Intent intent = new Intent(ChannelDetailsActivity.this, PostArticleActivity.class);
                intent.putExtra("POST_TYPE", POST_TYPE.YOUTUBE);
                intent.putExtra("MODE", MODE.ADD);
                intent.putExtra("article", new Gson().toJson(article));
                startActivity(intent);
            }
        });
    }

    private void postReels() {
        if (isFinishing()) return;
        if (PermissionChecker
                .checkSelfPermission(ChannelDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryOnlyVideo(this, true, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PERMISSION_REQUEST_REELS);
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    private void postArticle() {
        isArticle = true;

        if (PermissionChecker
                .checkSelfPermission(ChannelDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(ChannelDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryWithAll(ChannelDetailsActivity.this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>());
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
//        fromGallery(isVideoArticle);
    }

    private void loadSelectedVideo(LocalMedia localMedia) {
        if (isFinishing()) return;

        Intent intent = new Intent(this, PostArticleActivity.class);
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
        startActivity(intent);
    }

    private void loadImageArticle(LocalMedia localMedia) {

        if (!localMedia.getMimeType().contains("video")) {
            File f = new File(getCacheDir(), localMedia.getFileName() + System.currentTimeMillis());
            try {
                f.createNewFile();
                Uri url;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    url = Uri.fromFile(new File((localMedia.getAndroidQToPath())));
                } else {
                    url = Uri.fromFile(new File((localMedia.getPath())));
                }
                ImageCrop.of(url, Uri.parse(f.getPath()))
                        .start(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (isFinishing()) return;
            Intent intent = new Intent(ChannelDetailsActivity.this, PostArticleActivity.class);
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
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isArticle)
                        postArticle();
                    else postReels();
                } else {
                    Utils.showSnacky(getWindow().getDecorView().getRootView(), "" + getString(R.string.permision));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateEvent);
//        if (mPrefConfig != null) {
//            mPrefConfig.setChannel(null);
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

    /**
     * loading dialog
     */
    protected void showProgressDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new PictureLoadingDialog(this);
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

    private final int[] tabTextActive = {
            R.string.reels,
            R.string.articles
    };

    private final int[] tabTextInActive = {
            R.string.reels,
            R.string.articles
    };

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setText(tabTextActive[0]);
        if (mTabTitleList.size() > 1) {
            tabLayout.getTabAt(1).setText(tabTextInActive[1]);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setText(tabTextActive[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setText(tabTextInActive[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private static class ChannelPagerAdapter extends FragmentStatePagerAdapter {

        private int NUM_PAGES = 2;
        private Source source;
        private TYPE type;
        private ProfileArticleFragment fragment;
        private AuthorArticleFragment fragment2;
        private UpdateCallback updateCallback;

        public ChannelPagerAdapter(FragmentManager fm, Source source, int size, TYPE type, UpdateCallback updateCallback) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.source = source;
            this.NUM_PAGES = size;
            this.type = type;
            this.updateCallback = updateCallback;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            if (source.isOwn()) {
                if (position == 0 && NUM_PAGES == 2) {
                    return ProfileReelsFragment.newInstance(source.getId());
                } else {
                    fragment = ProfileArticleFragment.newInstance(source.getId());
                    fragment.setCallbackListener(updateCallback);
                    return fragment;
                }
            } else {
                if (position == 0 && NUM_PAGES == 2) {
                    return AuthorReelsFragment.newInstance(source.getContext());
                } else {
                    fragment2 = AuthorArticleFragment.newInstance(source.getContext());
                    fragment2.setCallbackListener(updateCallback);
                    return fragment2;
                }
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

    class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {

//            if(event2.getX() > event1.getX()){
//                onBackPressed();
//            }

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onBackPressed();
                            result = true;
                            //onSwipeRight();
                        } else {
                            //onSwipeLeft();
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