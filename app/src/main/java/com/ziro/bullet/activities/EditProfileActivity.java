package com.ziro.bullet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ziro.bullet.R;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.interfaces.ApiCallbacks;
import com.ziro.bullet.interfaces.ProfileApiCallback;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.ImageCrop;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.presenter.ProfilePresenter;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends BaseActivity implements ProfileApiCallback {

    public static final int PROFILE_IMAGE_REQUEST = 10;
    public static final int COVER_IMAGE_REQUEST = 11;
    public static final int PERMISSION_REQUEST = 745;

    public static final int PROFILE_CROP_IMAGE_REQUEST = 231;
    public static final int PROFILE_CROP_COVER_REQUEST = 232;
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private ProfilePresenter profilePresenter;
    private TextView counterLn;
    private TextView counterFn;
    private TextView counterUsername;
    private ImageView coverImage;
    private CircleImageView profileImage;
    private RelativeLayout backBtn;
    private PrefConfig mPrefConfig;
    private User user;
    private EditText usernameEdit;
    private EditText firstNameEdit;
//    private EditText lastNameEdit;
    private ImageView icon;
    private ProgressBar progress;
    private File profileImageFile;
    private File coverImageFile;

    private String username;
    private String firstName;

    private boolean isUsernameValid;

    private boolean isProfilePicBtnClicked;
    private boolean nameUpdated;

    private PictureLoadingDialog mLoadingDialog;
    private String validUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_edit_profile);

        mPrefConfig = new PrefConfig(this);
        profilePresenter = new ProfilePresenter(this, this);

        bindViews();

        coverImage.setClipToOutline(true);

        listeners();
        getUserDataFromShredPref();

        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    counterUsername.setText(s.length() + "/25");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (TextUtils.isEmpty(usernameEdit.getText())) {
//                    User userTemp = mPrefConfig.isUserObject();
//                    if (userTemp != null) {
//                        userTemp.setUsername(usernameEdit.getText().toString());
//                        mPrefConfig.setUserProfile(userTemp);
//                    }
//                }

                if (mPrefConfig.getUsername().equals(s.toString())) {
                    validUsername = s.toString();
                    isUsernameValid = true;
                    setUsernameValidIcon();
                } else {
                    OAuthAPIClient.cancelAll();
                    isUsernameValid = false;
                    String usernameCopy = s.toString();
                    profilePresenter.checkUsername(usernameCopy, new ApiCallbacks() {
                        @Override
                        public void loaderShow(boolean flag) {
                            if (flag) {
                                setUsernameLoading();
                            }
                        }

                        @Override
                        public void error(String error) {
                            Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void error404(String error) {

                        }

                        @Override
                        public void success(Object response) {
                            if (response instanceof Boolean) {
                                if ((Boolean) response) {
                                    validUsername = usernameCopy;
                                    isUsernameValid = true;
                                    setUsernameValidIcon();
                                } else {
                                    isUsernameValid = false;
                                    setUsernameInvalidIcon();
                                }
                            } else {
                                isUsernameValid = false;
                                setUsernameInvalidIcon();
                            }
                        }
                    });
                }


            }
        });

        firstNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    counterFn.setText(s.length() + "/25");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(firstNameEdit.getText())) {
                    User userTemp = mPrefConfig.isUserObject();
                    if (userTemp != null) {
                        userTemp.setFirst_name(firstNameEdit.getText().toString());
                        mPrefConfig.setUserProfile(userTemp);
                    }
                }
            }
        });

//        lastNameEdit.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s != null) {
//                    counterLn.setText(s.length() + "/25");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (TextUtils.isEmpty(lastNameEdit.getText())) {
//                    User userTemp = mPrefConfig.isUserObject();
//                    if (userTemp != null) {
//                        userTemp.setLast_name(lastNameEdit.getText().toString());
//                        mPrefConfig.setUserProfile(userTemp);
//                    }
//                }
//            }
//        });

        Utils.setStatusBarColor(this);
    }

    private void setUsernameValidIcon() {
        progress.setVisibility(View.INVISIBLE);
        icon.setImageResource(R.drawable.ic_username_check);
        icon.setVisibility(View.VISIBLE);
    }

    private void setUsernameInvalidIcon() {
        progress.setVisibility(View.INVISIBLE);
        icon.setImageResource(R.drawable.ic_username_cross);
        icon.setVisibility(View.VISIBLE);
    }

    private void setUsernameLoading() {
        progress.setVisibility(View.VISIBLE);
        icon.setImageResource(R.drawable.ic_username_cross);
        icon.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    private void getUserDataFromShredPref() {
        if (mPrefConfig != null) {
            user = mPrefConfig.isUserObject();
        }
    }

    private void setData() {
        if (mPrefConfig != null) {
            user = mPrefConfig.isUserObject();
            if (user != null) {
                if (!nameUpdated) {
                    if (!TextUtils.isEmpty(user.getFirst_name())) {
                        firstNameEdit.setText(user.getFirst_name());
                        counterFn.setText(user.getFirst_name().length() + "/25");
                    } else {
                        counterFn.setText("0/25");
                    }
//                    if (!TextUtils.isEmpty(user.getLast_name())) {
//                        lastNameEdit.setText(user.getLast_name());
//                        counterLn.setText(user.getLast_name().length() + "/25");
//                    } else {
//                        counterLn.setText("0/25");
//                    }
                    if (!TextUtils.isEmpty(user.getUsername())) {
                        usernameEdit.setText(user.getUsername());
                        counterUsername.setText(user.getUsername().length() + "/25");
                    } else {
                        counterUsername.setText("0/25");
                    }
                    nameUpdated = true;
                }
                if (!TextUtils.isEmpty(user.getProfile_image())) {
                    Glide.with(this).load(user.getProfile_image())
                            .placeholder(R.drawable.ic_placeholder_user)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(profileImage);
                }
                if (!TextUtils.isEmpty(user.getCover_image())) {
                    Glide.with(this).load(user.getCover_image())
                            .placeholder(R.drawable.cover)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(coverImage);
                }
            } else {
                profileImage.setImageResource(R.drawable.ic_placeholder_user);
                coverImage.setImageResource(R.drawable.cover);
            }
        }
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
        if (event.getType() == MessageEvent.TYPE_PROFILE_CHANGED) {
            setData();
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
            }
        }
    }

    private void bindViews() {
        counterUsername = findViewById(R.id.counter_username);
        counterLn = findViewById(R.id.counterLn);
        counterFn = findViewById(R.id.counterFn);
        coverImage = findViewById(R.id.cover_image);
        backBtn = findViewById(R.id.back_btn);
        profileImage = findViewById(R.id.profile_image);
        usernameEdit = findViewById(R.id.username_edit);
        firstNameEdit = findViewById(R.id.first_name_edit);
//        lastNameEdit = findViewById(R.id.last_name_edit);
        icon = findViewById(R.id.icon);
        progress = findViewById(R.id.progress);
    }

    private void listeners() {

        backBtn.setOnClickListener(v -> finish());
        profileImage.setOnClickListener(v -> {
            isProfilePicBtnClicked = true;
            fromGallery(PROFILE_IMAGE_REQUEST);
        });

        coverImage.setOnClickListener(v -> {
            isProfilePicBtnClicked = false;
            fromGallery(COVER_IMAGE_REQUEST);
        });
    }

    private void updateNames() {
        username = usernameEdit.getText().toString();
        if (!isUsernameValid || username.equals(mPrefConfig.getUsername())) {
            username = null;
        }
        if (username != null && username.matches("[0-9]+")) {
            username = null;
        }

        firstName = firstNameEdit.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            return;
        }

        profilePresenter.updateProfile(firstName, null, null, false, username);
    }

    public void fromGallery(int type) {
        if (PermissionChecker
                .checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
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
    protected void onPause() {
        super.onPause();
        updateNames();
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
//                        modifiedFile = Utils.getFileFromBitmap(EditProfileActivity.this, "imageProfile", Utils.modifyOrientation(file.getAbsolutePath()));
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
        } else if (requestCode == PROFILE_CROP_IMAGE_REQUEST) {
            if(data != null) {
                Uri cropProfileUri = ImageCrop.getOutput(data);
                if(cropProfileUri !=null) {
                    String cropProfileCutPath = cropProfileUri.getPath();

                    profileImageFile = new File(cropProfileCutPath);
                    profilePresenter.updateProfile(firstName, profileImageFile, null, false, username);
                }
            }
        } else if (requestCode == PROFILE_CROP_COVER_REQUEST) {
            if(data != null) {
                Uri cropProfileUri = ImageCrop.getOutput(data);
                if(cropProfileUri != null) {
                    String cropProfileCutPath = cropProfileUri.getPath();

                    coverImageFile = new File(cropProfileCutPath);
                    profilePresenter.updateProfile(firstName, null, coverImageFile, false, username);
                }
            }
        }
    }

    private void loadSelectedImage(int type, String fileName, Uri uri) {
        username = usernameEdit.getText().toString();
        if (!isUsernameValid || username.equals(mPrefConfig.getUsername())) {
            username = null;
        }
        if (username != null && username.matches("[0-9]+")) {
            username = null;
        }

        firstName = firstNameEdit.getText().toString();
//        lastName = lastNameEdit.getText().toString();

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

    @Override
    public void loaderShow(boolean flag) {
        if (!flag)
            dismissProgressDialog();
    }

    @Override
    public void error(String error, String img) {

    }

    @Override
    public void success() {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setType(MessageEvent.TYPE_PROFILE_CHANGED);

        EventBus.getDefault().post(messageEvent);
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
}