package com.ziro.bullet.mediapicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.yalantis.ucrop.BuildConfig;
import com.ziro.bullet.activities.ImageEditorActivity;

public class ImageCrop {
    public static final int REQUEST_MULTI_CROP = 609;
    public static final int REQUEST_CROP = 69;
    public static final int RESULT_ERROR = 96;
    public static final int MIN_SIZE = 10;

    private static final String EXTRA_PREFIX = BuildConfig.LIBRARY_PACKAGE_NAME;

    public static final String EXTRA_INPUT_URI = EXTRA_PREFIX + ".InputUri";
    public static final String EXTRA_OUTPUT_URI = EXTRA_PREFIX + ".OutputUri";
    public static final String EXTRA_INPUT_FRAME_ASPECT_RATIO = EXTRA_PREFIX + ".frameAspectRatio";
    public static final String EXTRA_INPUT_HIDE_CONTROLS = EXTRA_PREFIX + ".hideControls";
    public static final String EXTRA_OUTPUT_OFFSET_Y = EXTRA_PREFIX + ".OffsetY";

    public static final String EXTRA_MAX_SIZE_Y = EXTRA_PREFIX + ".MaxSizeY";


    private Intent mCropIntent;
    private Bundle mCropOptionsBundle;

    private ImageCrop(@NonNull Uri source, @NonNull Uri destination) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source);
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
    }

    private ImageCrop(@NonNull Uri source, @NonNull Uri destination, float frameAspectRatio) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source);
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
        mCropOptionsBundle.putFloat(EXTRA_INPUT_FRAME_ASPECT_RATIO, frameAspectRatio);
    }

    private ImageCrop(@NonNull Uri source, @NonNull Uri destination, boolean hideControls) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source);
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
        mCropOptionsBundle.putBoolean(EXTRA_INPUT_HIDE_CONTROLS, hideControls);
    }

    private ImageCrop(@NonNull Uri source, @NonNull Uri destination, float frameAspectRatio, boolean hideControls) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source);
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
        mCropOptionsBundle.putFloat(EXTRA_INPUT_FRAME_ASPECT_RATIO, frameAspectRatio);
        mCropOptionsBundle.putBoolean(EXTRA_INPUT_HIDE_CONTROLS, hideControls);
    }

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    public static ImageCrop of(@NonNull Uri source, @NonNull Uri destination) {
        return new ImageCrop(source, destination);
    }

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    public static ImageCrop of(@NonNull Uri source, @NonNull Uri destination, float frameAspectRatio) {
        return new ImageCrop(source, destination, frameAspectRatio);
    }

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    public static ImageCrop of(@NonNull Uri source, @NonNull Uri destination, boolean hideControls) {
        return new ImageCrop(source, destination, hideControls);
    }

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    public static ImageCrop of(@NonNull Uri source, @NonNull Uri destination, float frameAspectRatio, boolean hideControls) {
        return new ImageCrop(source, destination, frameAspectRatio, hideControls);
    }

    /**
     * Retrieve cropped image Uri from the result Intent
     *
     * @param intent crop result intent
     */
    @Nullable
    public static Uri getOutput(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_OUTPUT_URI);
    }


    /**
     * Send the crop Intent from an Activity with a custom request code or animation
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Activity activity, int requestCode, @AnimRes int activityCropEnterAnimation) {
        activity.startActivityForResult(getIntent(activity), requestCode);
        activity.overridePendingTransition(activityCropEnterAnimation, com.yalantis.ucrop.R.anim.ucrop_anim_fade_in);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    public void start(@NonNull AppCompatActivity activity) {
        start(activity, REQUEST_CROP);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull AppCompatActivity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    /**
     * Send the crop Intent from a Fragment
     *
     * @param fragment Fragment to receive result
     */
    public void start(@NonNull Context context, @NonNull Fragment fragment) {
        start(context, fragment, REQUEST_CROP);
    }


    /**
     * 多图裁剪
     */

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Context context, @NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }


    public Intent getIntent(@NonNull Context context) {
        mCropIntent.setClass(context, ImageEditorActivity.class);
        mCropIntent.putExtras(mCropOptionsBundle);
        return mCropIntent;
    }

}
