package com.ziro.bullet.activities;

import static com.ziro.bullet.background.BroadcastEmitter.BG_ERROR;
import static com.ziro.bullet.background.BroadcastEmitter.BG_PUBLISHED;
import static com.ziro.bullet.background.BroadcastEmitter.TYPE_BACKGROUND_PROCESS;
import static com.ziro.bullet.utills.Constants.ACTION_UPDATE_EVENT;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.background.BackgroundEvent;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.bottomSheet.PostArticleBottomSheet;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.data.POST_TYPE;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.fragments.CommentPopup;
import com.ziro.bullet.fragments.CreateYoutubePopup;
import com.ziro.bullet.fragments.GuidelinePopup;
import com.ziro.bullet.fragments.ProfileArticleFragment;
import com.ziro.bullet.fragments.ProfileReelsFragment;
import com.ziro.bullet.interfaces.ApiResponseInterface;
import com.ziro.bullet.interfaces.AuthorApiCallback;
import com.ziro.bullet.interfaces.ProfileApiCallback;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.ImageCrop;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.presenter.AuthorPresenter;
import com.ziro.bullet.presenter.MenuFragmentPresenter;
import com.ziro.bullet.presenter.ProfilePresenter;
import com.ziro.bullet.utills.MessageEvent;
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

public class ProfileActivity extends BaseActivity implements
        ProfileApiCallback,
        AuthorApiCallback,
        ProfileArticleFragment.OnFragmentInteractionListener,
        ProfileReelsFragment.OnFragmentInteractionListener {

    public static final int PERMISSION_REQUEST_REELS = 745;
    public static final int EDIT_PROFILE_REQUEST = 3244;
    public static final int PROFILE_IMAGE_REQUEST = 10;
    public static final int PROFILE_CROP_IMAGE_REQUEST = 231;
    public static final int PROFILE_CROP_COVER_REQUEST = 232;
    public static final int COVER_IMAGE_REQUEST = 11;
    public static final int PERMISSION_REQUEST = 745;
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private ArrayList<String> mTabTitleList = new ArrayList<>();
    private TabLayout tabLayout;
    private LinearLayout statusBarSpace;
    private ViewPager mPager;
    private RelativeLayout backBtn;
    private TextView editProfileBtn;
    private ImageView editCoverPic;
    private ImageView editProfileImage;
    private TextView usernameEditText;
    private String firstName;
//    private String lastName;
    private File profileImageFile;
    private File coverImageFile;
    private CircleImageView profileImage;
    private ImageView coverImage;
    private TextView textView5;
    private PrefConfig mPrefConfig;
    private TextView textView10;
    private TextView followersBtn;
    private User user;
    private ProfilePresenter profilePresenter;
    private boolean isProfilePicBtnClicked;
    private AuthorPresenter authorPresenter;
    private final BroadcastReceiver updateEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getProfile();
        }
    };
    private Author author = null;
    private CommentPopup commentPopup;
    private MenuFragmentPresenter presenter;
    private PostArticleBottomSheet postArticleBottomSheet = null;
    private boolean isArticle = false;
    private boolean isPosting = false;
    private ProfilePagerAdapter profilePagerAdapter;
    private PictureLoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_profile);
        mPrefConfig = new PrefConfig(this);
        authorPresenter = new AuthorPresenter(this, this);
        profilePresenter = new ProfilePresenter(this, this);
        presenter = new MenuFragmentPresenter(this);

        bindViews();
        setStatusBarHeight();

        mTabTitleList.add(getString(R.string.newsreels));
        mTabTitleList.add(getString(R.string.articles));
//        mTabTitleList.add(getString(R.string.following));

        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), mTabTitleList);
        mPager.setAdapter(profilePagerAdapter);
        mPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(mPager);

        listeners();
        getUserDataFromShredPref();
        getProfile();

        LocalBroadcastManager.getInstance(this).registerReceiver(updateEvent, new IntentFilter(ACTION_UPDATE_EVENT));
    }

    private void getUserDataFromShredPref() {
        if (mPrefConfig != null) {
            user = mPrefConfig.isUserObject();

            if (user != null) {
                if (user.isSetup()) {
                    usernameEditText.setText("@"+user.getUsername());
                    if (!TextUtils.isEmpty(user.getNameToDisplay())) {
                        firstName = user.getNameToDisplay();
                        textView5.setText(user.getNameToDisplay());
                    }
                    if (!TextUtils.isEmpty(user.getProfile_image())) {
                        Glide.with(this).load(user.getProfile_image())
                                .placeholder(R.drawable.ic_placeholder_user)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.ic_placeholder_user);
                    }
                    if (!TextUtils.isEmpty(user.getCover_image())) {
                        Glide.with(this).load(user.getCover_image())
                                .placeholder(R.drawable.cover)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(coverImage);
                    } else {
                        coverImage.setImageResource(R.drawable.cover);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.TYPE_PROFILE_CHANGED) {
            getProfile();
        }
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

    private void getProfile() {
        if (authorPresenter != null && mPrefConfig != null && mPrefConfig.isUserObject() != null && !TextUtils.isEmpty(mPrefConfig.isUserObject().getId())) {
            authorPresenter.getAuthor(mPrefConfig.isUserObject().getId());
        }
    }

    private void setData(Author author) {
        if (isFinishing()) {
            return;
        }
        if (author != null) {

            followersBtn.setText(author.getFollower_count() + " " + getString(R.string.followers));
            textView10.setText(author.getPost_count() + " " + getString(R.string.posts));

            usernameEditText.setText("@"+author.getUsername());
            if (!TextUtils.isEmpty(author.getNameToDisplay())) {
                firstName = author.getNameToDisplay();
                textView5.setText(author.getNameToDisplay());
            }
            if (!TextUtils.isEmpty(author.getProfile_image())) {
                Glide.with(this).load(author.getProfile_image())
                        .placeholder(R.drawable.ic_placeholder_user)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .dontAnimate()
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_placeholder_user);
            }
            if (!TextUtils.isEmpty(author.getCover_image())) {
                Glide.with(this).load(author.getCover_image())
                        .placeholder(R.drawable.cover)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(coverImage);
            } else {
                coverImage.setImageResource(R.drawable.cover);
            }

            showVerifiedIcon(author);

        } else {
            profileImage.setImageResource(R.drawable.ic_placeholder_user);
            coverImage.setImageResource(R.drawable.cover);
        }
    }

    private void showVerifiedIcon(@NotNull Author authorObj) {
        if (authorObj.isVerified()) {
            if (Utils.isRTL()) {
                textView5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified, 0, 0, 0);
            } else {
                textView5.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0);
            }
        }
    }

    private void bindViews() {
        textView5 = findViewById(R.id.textView5);
        usernameEditText = findViewById(R.id.username);
        tabLayout = findViewById(R.id.tabLayout);
        statusBarSpace = findViewById(R.id.status_bar_space);
        mPager = findViewById(R.id.viewpager);
        backBtn = findViewById(R.id.back_btn);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        coverImage = findViewById(R.id.cover_image);
        profileImage = findViewById(R.id.profile_image);
        editCoverPic = findViewById(R.id.edit_cover_pic);
        editProfileImage = findViewById(R.id.edit_profile_image);
        followersBtn = findViewById(R.id.followers_btn);
        textView10 = findViewById(R.id.textView10);
    }

    private void listeners() {
        backBtn.setOnClickListener(v -> finish());

        editProfileBtn.setOnClickListener(v -> startActivityForResult(new Intent(ProfileActivity.this, EditProfileActivity.class), EDIT_PROFILE_REQUEST));

        editProfileImage.setOnClickListener(v -> {
            isProfilePicBtnClicked = true;

            if (PermissionChecker
                    .checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    PermissionChecker
                            .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                GalleryPicker.openGalleryOnlyPicturesForProfilePic(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PROFILE_IMAGE_REQUEST);
            } else {
                PermissionChecker.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        });

        editCoverPic.setOnClickListener(v -> {
            isProfilePicBtnClicked = false;

            if (PermissionChecker
                    .checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    PermissionChecker
                            .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                GalleryPicker.openGalleryOnlyPictures(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), COVER_IMAGE_REQUEST);
            } else {
                PermissionChecker.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        });

        followersBtn.setOnClickListener(v -> {
            if (user != null && !TextUtils.isEmpty(user.getId())) {
                FollowersActivity.start(ProfileActivity.this, "");
            }
        });
    }

    private void setStatusBarHeight() {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tabLayout.getLayoutParams();
        params.setMargins(0, getStatusBarHeight(), 0, 0);
        tabLayout.setLayoutParams(params);

        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) statusBarSpace.getLayoutParams();
        params1.setMargins(0, getStatusBarHeight(), 0, 0);
        statusBarSpace.setLayoutParams(params1);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void fromGallery(int type) {

        if (PermissionChecker
                .checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isProfilePicBtnClicked) {
                    fromGallery(PROFILE_IMAGE_REQUEST);
                } else {
                    fromGallery(COVER_IMAGE_REQUEST);
                }
            } else if (requestCode == PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PROFILE_IMAGE_REQUEST:
                case COVER_IMAGE_REQUEST:


//                    showProgressDialog();
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

//
//
//                    PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<File>>() {
//
//                        @Override
//                        public List<File> doInBackground() {
//                            File modifiedFile;
//                            try {
//                                modifiedFile = Utils.getFileFromBitmap(ProfileActivity.this, "imageProfile", Utils.modifyOrientation(file.getAbsolutePath()));
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                modifiedFile = file;
//                            }
//
//                            return Collections.singletonList(modifiedFile);
//                        }
//
//                        @Override
//                        public void onSuccess(List<File> files) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                loadSelectedImage(requestCode, file, Uri.parse(file)));
//                            } else {
//                                loadSelectedImage(requestCode, file, Uri.parse(files.get(0).getPath()));
//                            }
//                        }
//                    });


                    loadSelectedImage(requestCode, fileName, uri);

                    break;

                case PROFILE_CROP_IMAGE_REQUEST:

                    Uri cropProfileUri = ImageCrop.getOutput(data);
                    String cropProfileCutPath = cropProfileUri.getPath();

                    profileImageFile = new File(cropProfileCutPath);
                    profilePresenter.updateProfile(firstName, profileImageFile, null, false, null);
                    break;

                case PROFILE_CROP_COVER_REQUEST:
                    Uri cropCoverUri = ImageCrop.getOutput(data);
                    String cropCoverCutPath = cropCoverUri.getPath();

                    coverImageFile = new File(cropCoverCutPath);
                    profilePresenter.updateProfile(firstName, null, coverImageFile, false, null);

                    break;
                case EDIT_PROFILE_REQUEST:
                    getProfile();
                    break;


                case PERMISSION_REQUEST_REELS: {
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
                }
                break;
                case PictureConfig.CHOOSE_REQUEST:
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

                    break;
                case ImageCrop.REQUEST_CROP:


                    Uri resultUri = ImageCrop.getOutput(data);
                    String cutPath = resultUri.getPath();

                    Intent intent = new Intent(this, PostArticleActivity.class);
//                if (localMedia.getMimeType().contains("video")) {
//                    intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
//                } else {
                    intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
//                }
                    intent.putExtra("MODE", MODE.ADD);
                    intent.putExtra("file", new File(cutPath));
                    intent.putExtra("uri", cutPath);
//                intent.putExtra("localMedia", new Gson().toJson(localMedia));
                    startActivity(intent);
                    break;
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
                    , frameAspectRatio,true
            )
                    .start(this, cropRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        if (!flag)
            dismissProgressDialog();
    }

    @Override
    public void error(String error) {
        Utils.showSnacky(getWindow().getDecorView().getRootView(), "" + error);
    }

    @Override
    public void success(Author author) {
        this.author = author;
        setData(author);
    }

    @Override
    public void error(String error, String img) {
        if (!TextUtils.isEmpty(img)) {
            if (img.equalsIgnoreCase("profile_image")) {
                profileImage.setImageResource(R.drawable.ic_placeholder_user);
            } else if (img.equalsIgnoreCase("cover_image")) {
                coverImage.setImageResource(R.drawable.cover);
            }
        }
        Utils.showSnacky(profileImage, error);
    }

    @Override
    public void success() {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setType(MessageEvent.TYPE_PROFILE_CHANGED);

        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void onUploadClicked() {
        if (mPrefConfig != null && mPrefConfig.isUserObject() != null && mPrefConfig.isUserObject().isSetup()) {
            if (!mPrefConfig.getGuidelines()) {
                GuidelinePopup popup = new GuidelinePopup(ProfileActivity.this, (flag, msg) -> {
                    if (flag) {
                        if (presenter != null) {
                            presenter.acceptGuidelines(new ApiResponseInterface() {
                                @Override
                                public void _success() {
                                    if (mPrefConfig != null)
                                        mPrefConfig.setGuidelines(true);
                                    if (postArticleBottomSheet == null) {
                                        postArticleBottomSheet = new PostArticleBottomSheet(ProfileActivity.this, option -> postContent(option));
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
                if (postArticleBottomSheet == null) {
                    postArticleBottomSheet = new PostArticleBottomSheet(ProfileActivity.this, this::postContent);
                }
                postArticleBottomSheet.show(dialog -> {

                });
            }
        } else {
            if (commentPopup == null) {
                commentPopup = new CommentPopup(ProfileActivity.this);
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
        CreateYoutubePopup createYoutubePopup = new CreateYoutubePopup(ProfileActivity.this);
        createYoutubePopup.showDialog(article -> {
            if (article != null) {
                Log.e("uploadImageVideo", "-createSuccess here-> " + new Gson().toJson(article));
                if (isFinishing()) return;
                Intent intent = new Intent(ProfileActivity.this, PostArticleActivity.class);
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
                .checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
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
                .checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryWithAll(ProfileActivity.this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>());
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
//        fromGallery(isVideoArticle);
    }

    private void loadSelectedVideo(LocalMedia localMedia) {
        if (isFinishing()) return;

        isPosting = true;
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
        if (isFinishing()) return;

        isPosting = true;
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

    private static class ProfilePagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> mTabTitleList = new ArrayList<>();

        public ProfilePagerAdapter(FragmentManager fm, ArrayList<String> mTabTitleList) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mTabTitleList = mTabTitleList;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return ProfileReelsFragment.newInstance("");
            } else {
                return ProfileArticleFragment.newInstance("");
            }
        }

        @Override
        public int getCount() {
            return mTabTitleList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitleList.get(position);
        }
    }
}