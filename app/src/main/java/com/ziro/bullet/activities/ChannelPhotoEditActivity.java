package com.ziro.bullet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.data.models.channels.UpdateChannelResponse;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.interfaces.ChannelApiCallback;
import com.ziro.bullet.interfaces.CreateChannelCallback;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.thread.PictureThreadUtils;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.presenter.ChannelEditPresenter;
import com.ziro.bullet.presenter.CreateChannelPresenter;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelPhotoEditActivity extends BaseActivity {

    public static final String EXTRA_CHANNEL = "EXTRA_CHANNEL";
    public static final int PROFILE_IMAGE_REQUEST = 10;
    public static final int COVER_IMAGE_REQUEST = 11;
    public static final int COVER_IMAGE_PORTRAIT_REQUEST = 12;
    public static final int PERMISSION_REQUEST = 745;
    private ImageView coverImage;
    private ImageView cover_image_port;
    private CircleImageView profileImage;
    private RelativeLayout backBtn;
    private ImageView loader;
    private EditText channelNameEdit;
    private Source channelObj;
    private String channelId;
    private String whichImage = "";
    private ChannelEditPresenter mChannelEditPresenter;
    private CreateChannelPresenter mChannelPresenter;
    private ChannelApiCallback channelApiCallback = new ChannelApiCallback() {
        @Override
        public void loaderShow(boolean flag) {
            if (flag)
                showProgressDialog();
            else
                dismissProgressDialog();
        }

        @Override
        public void error(String error, String img) {
            Toast.makeText(ChannelPhotoEditActivity.this, error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void success(Object data) {
            if (data instanceof UpdateChannelResponse) {
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.TYPE_CHANNEL_PROFILE_CHANGED);
                messageEvent.setObjectData(((UpdateChannelResponse) data).getChannel());

                EventBus.getDefault().post(messageEvent);

                channelObj = ((UpdateChannelResponse) data).getChannel();
                Log.e("channelObj", "Data = [" + new Gson().toJson(channelObj) + "]");
                setData();
            }
        }
    };
    private CreateChannelCallback createChannelCallback = new CreateChannelCallback() {
        @Override
        public void loaderShow(boolean flag) {
            loader.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void error(String error) {
            Toast.makeText(ChannelPhotoEditActivity.this, error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void mediaUploaded(String url, String type, int request) {
            if (!TextUtils.isEmpty(whichImage)) {
                switch (whichImage) {
                    case "profile":
                        mChannelEditPresenter.updateChannelProfile(channelId, "", url, null, null, false);
                        Glide.with(profileImage)
                                .load(url).into(profileImage);
                        if (channelObj != null) {
                            channelObj.setIcon(url);
                        }
                        break;
                    case "cover_horizontal":
                        mChannelEditPresenter.updateChannelProfile(channelId, "", null, url, null, false);
                        Glide.with(coverImage)
                                .load(url).into(coverImage);
                        if (channelObj != null) {
                            channelObj.setImage(url);
                        }
                        break;
                    case "cover_portrait":
                        mChannelEditPresenter.updateChannelProfile(channelId, "", null, null, url, false);
                        Glide.with(cover_image_port)
                                .load(url).into(cover_image_port);
                        if (channelObj != null) {
                            channelObj.setPortrait_image(url);
                        }
                        break;
                }
            }
        }

        @Override
        public void validName(boolean isValid) {

        }
    };
    private PictureLoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_channel_photo_edit);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_CHANNEL)) {
            channelObj = (Source) getIntent().getParcelableExtra(EXTRA_CHANNEL);
        }
        mChannelEditPresenter = new ChannelEditPresenter(this, channelApiCallback);
        mChannelPresenter = new CreateChannelPresenter(this, createChannelCallback);

        if (channelObj != null) {
            channelId = channelObj.getId();
        }
        bindViews();
        listeners();
        Utils.setStatusBarColor(this);
        setData();
    }

    private void setData() {
        if (channelObj != null) {
            channelNameEdit.setText(channelObj.getName());
            Glide.with(profileImage).load(channelObj.getIcon()).into(profileImage);
            Glide.with(coverImage).load(channelObj.getImage()).into(coverImage);
            Glide.with(cover_image_port).load(channelObj.getPortrait_image()).into(cover_image_port);
        } else {
            profileImage.setImageResource(R.drawable.ic_placeholder_user);
            coverImage.setImageResource(R.drawable.cover);
            cover_image_port.setImageResource(R.drawable.cover);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!TextUtils.isEmpty(whichImage)) {
                    switch (whichImage) {
                        case "profile":
                            fromGallery(PROFILE_IMAGE_REQUEST);
                            break;
                        case "cover_horizontal":
                            fromGallery(COVER_IMAGE_REQUEST);
                            break;
                        case "cover_portrait":
                            fromGallery(COVER_IMAGE_PORTRAIT_REQUEST);
                            break;
                    }
                }
            }
        }
    }

    private void bindViews() {
        coverImage = findViewById(R.id.cover_image);
        cover_image_port = findViewById(R.id.cover_image_port);
        backBtn = findViewById(R.id.back_btn);
        profileImage = findViewById(R.id.profile_image);
        loader = findViewById(R.id.loader);
        channelNameEdit = findViewById(R.id.channel_name_edit);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ChannelPhotoEditActivity.EXTRA_CHANNEL, channelObj);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void listeners() {
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        profileImage.setOnClickListener(v -> {
            whichImage = "profile";
            fromGallery(PROFILE_IMAGE_REQUEST);
        });

        coverImage.setOnClickListener(v -> {
            whichImage = "cover_horizontal";
            fromGallery(COVER_IMAGE_REQUEST);
        });

        cover_image_port.setOnClickListener(v -> {
            whichImage = "cover_portrait";
            fromGallery(COVER_IMAGE_PORTRAIT_REQUEST);
        });
    }

    public void fromGallery(int type) {
        if (PermissionChecker
                .checkSelfPermission(ChannelPhotoEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
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
        if (resultCode == RESULT_OK) {
            showProgressDialog();
            LocalMedia localMedia = PictureSelector.obtainMultipleResult(data).get(0);

            File file;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                file = new File(localMedia.getAndroidQToPath());
            } else {
                file = new File(localMedia.getPath());
            }


            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<File>>() {

                @Override
                public List<File> doInBackground() {
                    File modifiedFile;
                    try {
                        modifiedFile = Utils.getFileFromBitmap(ChannelPhotoEditActivity.this, "imageProfile", Utils.modifyOrientation(file.getAbsolutePath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        modifiedFile = file;
                    }

                    return Collections.singletonList(modifiedFile);
                }

                @Override
                public void onSuccess(List<File> files) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        loadSelectedImage(requestCode, new File(files.get(0).getPath()));
                    } else {
                        loadSelectedImage(requestCode, new File(files.get(0).getPath()));
                    }
                }
            });
        }
    }

    private void loadSelectedImage(int type, File file) {
        if (mChannelPresenter != null) {
            mChannelPresenter.uploadImageVideo(file, "images", 0);
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
}