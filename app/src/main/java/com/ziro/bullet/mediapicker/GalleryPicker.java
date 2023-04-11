package com.ziro.bullet.mediapicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.Channel.ChannelImageActivity;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.config.PictureMimeType;
import com.ziro.bullet.mediapicker.engine.CacheResourcesEngine;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.engine.ImageEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.gallery.InstagramSelectionConfig;
import com.ziro.bullet.mediapicker.style.PictureCropParameterStyle;
import com.ziro.bullet.mediapicker.style.PictureParameterStyle;
import com.ziro.bullet.mediapicker.style.PictureWindowAnimationStyle;
import com.ziro.bullet.mediapicker.utils.PictureSelectionModel;
import com.ziro.bullet.mediapicker.utils.PictureSelector;

import java.util.ArrayList;
import java.util.List;

import static com.ziro.bullet.mediapicker.gallery.InstagramSelectionConfig.THEME_STYLE_DARK;
import static com.ziro.bullet.mediapicker.gallery.InstagramSelectionConfig.currentTheme;

public class GalleryPicker {
    public static void openGalleryWithAll(Fragment activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia) {
        applyInstagramOptions(activity.getContext(), PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll()))
                .imageEngine(engine)
                .loadCacheResourcesCallback(cacheResourcesEngine)
                .selectionData(selectionMedia)
//                .isEnableCrop(false)
                .withAspectRatio(16,9)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    public static void openGalleryWithAll(Activity activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia) {
        applyInstagramOptions(activity, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll()))
                .imageEngine(engine)
                .loadCacheResourcesCallback(cacheResourcesEngine)
                .withAspectRatio(16,9)
                .selectionData(selectionMedia)
//                .isEnableCrop(false)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    public static void openGalleryOnlyVideo(Fragment fragment, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, int reqCode) {
        applyInstagramOptions(fragment.getContext(), PictureSelector.create(fragment)
                .openGallery(PictureMimeType.ofVideo())
                .isReel(true))
                .videoMinSecond(2)
                .imageEngine(engine)
                .withAspectRatio(16,9)
                .loadCacheResourcesCallback(cacheResourcesEngine)
                .selectionData(selectionMedia)
                .forResult(reqCode);
    }

    public static void openGalleryOnlyVideo(Activity activity, boolean isReel, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, int reqCode) {
        applyInstagramOptions(activity, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofVideo()))
                .isReel(isReel)
                .imageEngine(engine)
                .videoMinSecond(1)
                .withAspectRatio(16,9)
                .loadCacheResourcesCallback(cacheResourcesEngine)
                .selectionData(selectionMedia)
                .forResult(reqCode);
    }

    public static void openGalleryOnlyPictures(Fragment activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, int reqCode) {
        applyInstagramOptions(activity.getContext(), PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()))
                .imageEngine(engine)
                .loadCacheResourcesCallback(cacheResourcesEngine)
                .withAspectRatio(16,9)
//                .isEnableCrop(false)
                .selectionData(selectionMedia)
                .forResult(reqCode);

    }

    public static void openGalleryOnlyPictures(Activity activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, int reqCode) {
        applyInstagramOptions(activity, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()))
                .imageEngine(engine)
                .loadCacheResourcesCallback(cacheResourcesEngine)
                .withAspectRatio(16,9)
//                .isEnableCrop(false)
                .selectionData(selectionMedia)
                .forResult(reqCode);
    }

    public static void openGalleryOnlyPicturesForProfilePic(Fragment activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, int reqCode) {
        applyInstagramOptions(activity.getContext(), PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()))
                .imageEngine(engine)
                .loadCacheResourcesCallback(cacheResourcesEngine)
                .withAspectRatio(1,1)
//                .isEnableCrop(false)
                .selectionData(selectionMedia)
                .forResult(reqCode);
    }

    public static void openGalleryOnlyPicturesForProfilePic(Activity activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, int reqCode) {
        applyInstagramOptions(activity, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage()))
                .imageEngine(engine)
                .loadCacheResourcesCallback(cacheResourcesEngine)
                .withAspectRatio(1,1)
//                .isEnableCrop(false)
                .selectionData(selectionMedia)
                .forResult(reqCode);
    }

    private static PictureSelectionModel applyInstagramOptions(Context context, PictureSelectionModel selectionModel) {
        return applyInstagramOptions(context, InstagramSelectionConfig.createConfig().setCurrentTheme(currentTheme), selectionModel);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private static PictureSelectionModel applyInstagramOptions(Context context, InstagramSelectionConfig instagramConfig, PictureSelectionModel selectionModel) {
        return selectionModel
                .setInstagramConfig(instagramConfig)
                .setPictureStyle(createInstagramStyle(context))
                .setPictureCropStyle(createInstagramCropStyle(context))
                .setPictureWindowAnimationStyle(new PictureWindowAnimationStyle())
                .isWithVideoImage(false)
                .maxSelectNum(1)
                .minSelectNum(1)
                .maxVideoSelectNum(1)
                .imageSpanCount(4)
                .isReturnEmpty(false)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .selectionMode(PictureConfig.SINGLE)
                .isSingleDirectReturn(false)
                .isPreviewImage(true)
                .isPreviewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .isZoomAnim(true)
                .isEnableCrop(false)
                .isCompress(false)
                .compressQuality(80)
                .synOrAsy(true)
                .withAspectRatio(16, 9)
                .showCropFrame(true)
                .showCropGrid(true)
                .isOpenClickSound(true)
                .videoMaxSecond(600)
                .videoMinSecond(3)
                .recordVideoSecond(60)
                .recordVideoMinSecond(3)
                .cutOutQuality(90)
                .rotateEnabled(false)
                .minimumCompressSize(100);
    }

    private static PictureParameterStyle createInstagramStyle(Context context) {
        PictureParameterStyle mPictureParameterStyle = new PictureParameterStyle();
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.isChangeStatusBarFontColor = false;
        } else {
            mPictureParameterStyle.isChangeStatusBarFontColor = true;
        }
        mPictureParameterStyle.isOpenCompletedNumStyle = false;
        mPictureParameterStyle.isOpenCheckNumStyle = true;
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#1C1C1E");
        } else {
            mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#FFFFFF");
        }
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#1C1C1E");
        } else {
            mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#FFFFFF");
        }
        mPictureParameterStyle.pictureTitleUpResId = R.drawable.ic_expand_arrow;
        mPictureParameterStyle.pictureTitleDownResId = R.drawable.ic_expand_arrow;
        mPictureParameterStyle.pictureLeftBackIcon = R.drawable.ic_back_arrow;
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(context, R.color.white);
        } else {
            mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(context, R.color.black);
        }
//        if (currentTheme == THEME_STYLE_DARK) {
//            mPictureParameterStyle.pictureRightDefaultTextColor = ContextCompat.getColor(context, R.color.picture_color_1766FF);
//        }  else {
//            mPictureParameterStyle.pictureRightDefaultTextColor = ContextCompat.getColor(context, R.color.picture_color_1766FF);
//        }
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureContainerBackgroundColor = ContextCompat.getColor(context, R.color.black);
        } else {
            mPictureParameterStyle.pictureContainerBackgroundColor = ContextCompat.getColor(context, R.color.white);
        }
//        mPictureParameterStyle.pictureBottomBgColor = ContextCompat.getColor(context, R.color.picture_color_fa);
//        mPictureParameterStyle.picturePreviewTextColor = ContextCompat.getColor(context, R.color.picture_color_fa632d);
//        mPictureParameterStyle.pictureUnPreviewTextColor = ContextCompat.getColor(context, R.color.picture_color_9b);
//        mPictureParameterStyle.pictureCompleteTextColor = ContextCompat.getColor(context, R.color.picture_color_fa632d);
//        mPictureParameterStyle.pictureUnCompleteTextColor = ContextCompat.getColor(context, R.color.picture_color_9b);
//        mPictureParameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_black_delete;
        mPictureParameterStyle.pictureExternalPreviewGonePreviewDelete = true;
        mPictureParameterStyle.pictureRightDefaultText = context.getString(R.string.next);
        return mPictureParameterStyle;
    }

    private static PictureCropParameterStyle createInstagramCropStyle(Context context) {
        return new PictureCropParameterStyle();
//        if (currentTheme == THEME_STYLE_DARK) {
//            return new PictureCropParameterStyle(
//                    Color.parseColor("#1C1C1E"),
//                    Color.parseColor("#1C1C1E"),
//                    Color.parseColor("#1C1C1E"),
//                    ContextCompat.getColor(context, R.color.picture_color_white),
//                    false);
//        }
//        return new PictureCropParameterStyle(
//                ContextCompat.getColor(context, R.color.picture_color_white),
//                ContextCompat.getColor(context, R.color.picture_color_white),
//                ContextCompat.getColor(context, R.color.picture_color_white),
//                ContextCompat.getColor(context, R.color.picture_color_black),
//                true);
    }
}
