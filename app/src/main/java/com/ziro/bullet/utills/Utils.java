package com.ziro.bullet.utills;

import static android.graphics.Bitmap.createScaledBitmap;
import static androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.pixplicity.sharp.Sharp;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AuthorActivity;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.HelpActivity;
import com.ziro.bullet.activities.ProfileActivity;
import com.ziro.bullet.activities.VideoFullScreenActivity;
import com.ziro.bullet.activities.WebViewActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.NewFeed.SectionsItem;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.interfaces.dialogClick;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Author;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Utils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static Context mContext;
    public static PrefConfig prefConfig;
    private static OkHttpClient httpClient;
    private static Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    private static AlertDialog dialog;

    /**
     * Given two points in the plane p1=(x1, x2) and p2=(y1, y1), this method
     * returns the direction that an arrow pointing from p1 to p2 would have.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the direction
     */
    public static Direction getDirection(float x1, float y1, float x2, float y2) {
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.fromAngle(angle);
    }

    /**
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    public static double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }

    public static float getBulletDimens(PrefConfig prefConfig, Activity activity) {
        if (prefConfig == null) return -1;
        if (prefConfig.getTextSize() == null) return -1;
        //Bullet Size
        switch (prefConfig.getTextSize().getId()) {
            case 0:
                return activity.getResources().getDimension(R.dimen._9sdp);
            case 1:
                return activity.getResources().getDimension(R.dimen._13sdp);
            case 2:
                return activity.getResources().getDimension(R.dimen._15sdp);
            case 3:
                return activity.getResources().getDimension(R.dimen._17sdp);
        }
        return -1;
    }

    public static String formatViews(long number) {
        String numberString = "";
        if (number / 1000000f > 1f) {
            numberString = new DecimalFormat("#.#").format(number / 1000000f) + "m";
        } else if (number / 1000f > 1f) {
            numberString = new DecimalFormat("#.#").format(number / 1000f) + "k";
        } else {
            numberString = String.valueOf(number);
        }
        return numberString;
    }

    public static float getHeadlineDimens(PrefConfig prefConfig, Activity activity) {
        if (prefConfig == null) return -1;
        if (prefConfig.getTextSize() == null) return -1;
        //Headline Size
        switch (prefConfig.getTextSize().getId()) {
            case 0:
                return activity.getResources().getDimension(R.dimen._12sdp);
            case 1:
                return activity.getResources().getDimension(R.dimen._14sdp);
            case 2:
                return activity.getResources().getDimension(R.dimen._16sdp);
            case 3:
                return activity.getResources().getDimension(R.dimen._18sdp);
        }
        return -1;
    }

//    public static void unFollowAnimation(LottieAnimationView avLike, int duration) {
//        Log.d("animation", "UnFollow progress = " + avLike.getProgress());
////        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f).setDuration(duration);
//        ValueAnimator animator = ValueAnimator.ofFloat(avLike.getProgress(), ((avLike.getProgress() == 0) ? 1f : 0f)).setDuration(duration);
//        animator.addUpdateListener(animation -> avLike.setProgress((float) animation.getAnimatedValue()));
//        animator.start();
//    }

    public static void followAnimation(LottieAnimationView avLike, int duration) {
        Log.d("animation", "Follow progress = " + avLike.getProgress());
        ValueAnimator animator = ValueAnimator.ofFloat(avLike.getProgress(), ((avLike.getProgress() == 0) ? 1f : 0f)).setDuration(duration);
        animator.addUpdateListener(animation -> avLike.setProgress((float) animation.getAnimatedValue()));
        animator.start();
    }

    public static void doHaptic(PrefConfig mPrefConfig) {
        if (mPrefConfig.isHaptic()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE);
                v.vibrate(vibrationEffect);
            } else {
                v.vibrate(10);
            }
        }
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static void createKeyHash(Activity activity, String yourPackage) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(yourPackage, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String code = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", code);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // COMMAND : keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
    }

    public static float getResolution(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;
        return scaleFactor;
    }

    public static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }

    public static void showSnacky(View view, String msg) {
//        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
//        snackbar.show();

        make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnacky(View view, String msg, Snackbar.Callback callback) {
//        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
//        snackbar.show();

        make(view, msg, Snackbar.LENGTH_LONG).addCallback(callback).show();
    }

    public static Snackbar make(View view, CharSequence text, int duration) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.white));
        snackbar.setTextColor(ContextCompat.getColor(view.getContext(), R.color.primaryRed));
        return snackbar;
    }

    public static Snackbar make(View view, int resId, int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    private static int getAttribute(Context context, int resId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, typedValue, true);
        return typedValue.data;
    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (image != null) {
            if (maxHeight > 0 && maxWidth > 0) {
                int width = image.getWidth();
                int height = image.getHeight();
                float ratioBitmap = (float) width / (float) height;
                float ratioMax = (float) maxWidth / (float) maxHeight;

                int finalWidth = maxWidth;
                int finalHeight = maxHeight;
                if (ratioMax > ratioBitmap) {
                    finalWidth = (int) ((float) maxHeight * ratioBitmap);
                } else {
                    finalHeight = (int) ((float) maxWidth / ratioBitmap);
                }
                image = createScaledBitmap(image, finalWidth, finalHeight, true);
                return image;
            } else {
                return image;
            }
        } else {
            return image;
        }
    }

    public static void scaleView(boolean shouldShow, boolean isGone, View view) {

        float scaleX = 1.0f;
        float scaleY = 1.0f;
        int duration = 200;

        // IF HIDE CURRENT LOCATION ICON
        if (!shouldShow) {
            scaleX = 0.0f;
            scaleY = 0.0f;
        }

        // ANIMATE ICON
        view.animate().scaleX(scaleX).scaleY(scaleY).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // if showing icon
                if (shouldShow) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // if hiding icon
                if (!shouldShow) {
                    if (isGone) {
                        view.setVisibility(View.GONE);
                    } else {
                        view.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public static void fetchSvg(final Context context, String url, final ImageView target) {
        if (!TextUtils.isEmpty(url) && target != null && context != null) {
            if (httpClient == null) {
                // Use cache for performance and basic offline capability
                httpClient = new OkHttpClient.Builder().cache(new Cache(context.getCacheDir(), 5 * 1024 * 1014)).build();
            }

            Request request = new Request.Builder().url(url).build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    try {
                        target.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.error_image));
                    } catch (Exception ignore) {

                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try {
                        InputStream stream = response.body().byteStream();
                        Sharp.loadInputStream(stream).into(target);
                        stream.close();
                    } catch (Exception ignore) {

                    }
                }
            });
        }
    }

    public static void broadcastIntent(Context context, String action, String type) {
        Intent intent = new Intent(type);
        intent.putExtra("method", action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    /**
     * CREATE NOTIFICATION CHANNEL METHOD
     */
    public static void createNotification(Context context, String title, String message, Bitmap icon, Intent intent, Bundle bundle, Bitmap banner, CharSequence cName, String cDesc, String channelID) {
        if (bundle != null) intent.putExtras(bundle);
        PendingIntent mainPendingIntent;
        mainPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        } else {
//            mainPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }
        //create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, cName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(cDesc);
            channel.setLightColor(context.getColor(R.color.colorPrimary));
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder;
        if (icon != null) {
            if (banner != null) {
                builder = new NotificationCompat.Builder(context, channelID)
                        .setContentTitle(title).setContentText(message).setAutoCancel(true)
                        .setGroupSummary(true).setLargeIcon(icon).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setSmallIcon(R.drawable.ic_notification_icon)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(mainPendingIntent);
            } else {
                builder = new NotificationCompat.Builder(context, channelID)
                        .setContentTitle(title).setAutoCancel(true).setGroupSummary(true)
                        .setLargeIcon(icon).setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setSmallIcon(R.drawable.ic_notification_icon).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(mainPendingIntent);
            }
        } else {
            builder = new NotificationCompat.Builder(context, channelID)
                    .setContentTitle(title).setContentText(message).setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(mainPendingIntent);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setColor(Color.BLACK);
            //builder.setColor(context.getResources().getColor(R.color.colorPrimary));
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void checkAppModeColor(Activity activity, boolean isDarkModeOnly) {

        new Components().settingStatusBarColors(activity, "white");

//        if (isDarkModeOnly) {
//            new Components().settingStatusBarColors(activity, "black");
//        } else {
//            PrefConfig prefConfig = new PrefConfig(activity);
//            if (prefConfig.getAppTheme() != null && prefConfig.getAppTheme().equalsIgnoreCase(Constants.AUTO)) {
//                switch (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
//                    case Configuration.UI_MODE_NIGHT_YES:
//                        new Components().settingStatusBarColors(activity, "black");
//                        break;
//                    case Configuration.UI_MODE_NIGHT_NO:
//                        new Components().settingStatusBarColors(activity, "white");
//                        break;
//                }
//            } else if (prefConfig.getAppTheme() != null && prefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
//                new Components().settingStatusBarColors(activity, "black");
//            } else {
//                new Components().settingStatusBarColors(activity, "white");
//            }
//        }
//        delegate.applyDayNight();
    }

    public static void checkAppModeColor(Activity activity) {
        PrefConfig prefConfig = new PrefConfig(activity);
        if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.AUTO)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            switch (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    new Components().settingStatusBarColors(activity, "black");
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    new Components().settingStatusBarColors(activity, "white");
                    break;
            }
        } else if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
            new Components().settingStatusBarColors(activity, "black");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            new Components().settingStatusBarColors(activity, "white");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void setDrawableColor(Drawable drawable, String color) {
//        if (drawable != null && !TextUtils.isEmpty(color)) {
//            if (drawable instanceof ShapeDrawable) {
//                ((ShapeDrawable) drawable).getPaint().setColor(Color.parseColor(color));
//            } else if (drawable instanceof GradientDrawable) {
//                ((GradientDrawable) drawable).setColor(Color.parseColor(color));
//            } else if (drawable instanceof ColorDrawable) {
//                ((ColorDrawable) drawable).setColor(Color.parseColor(color));
//            }
//        }
    }

    public static void setDrawableColor(Drawable drawable, int color) {
        if (drawable != null && color != 0) {
            if (drawable instanceof ShapeDrawable) {
                ((ShapeDrawable) drawable).getPaint().setColor(color);
            } else if (drawable instanceof GradientDrawable) {
                ((GradientDrawable) drawable).setColor(color);
            } else if (drawable instanceof ColorDrawable) {
                ((ColorDrawable) drawable).setColor(color);
            }
        }
    }

    public static String getLocalizedFormattedDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

//        Date dateObj1 = null;
//        try {
//            dateObj1 = inputFormat.parse(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeZone(TimeZone.getDefault());
//        cal.setTime(dateObj1);
//        int day = cal.get(Calendar.DATE);

        Date dateObj1 = null;
        try {
            dateObj1 = inputFormat.parse(date);
            inputFormat.setTimeZone(TimeZone.getDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        if (DateUtils.isToday(dateObj1.getTime()))
//            return "Today";
//        if (!((day > 10) && (day < 19)))
//            switch (day % 10) {
//                case 1:
//                    return new SimpleDateFormat("d'st' MMMM yyyy", Locale.getDefault()).format(dateObj1);
//                case 2:
//                    return new SimpleDateFormat("d'nd' MMMM yyyy", Locale.getDefault()).format(dateObj1);
//                case 3:
//                    return new SimpleDateFormat("d'rd' MMMM yyyy", Locale.getDefault()).format(dateObj1);
//                default:
//                    return new SimpleDateFormat("d'th' MMMM yyyy", Locale.getDefault()).format(dateObj1);
//            }
        // return new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(dateObj1);
        return new SimpleDateFormat("MMM dd", Locale.US).format(dateObj1);
    }

    public static String getCustomDateIfValid(String date, String patternYouWant) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateObj1 = null;
        try {
            dateObj1 = inputFormat.parse(date);
            Date today = new Date();
            if (dateObj1 != null) {
                if (dateObj1.compareTo(today) > 0) {
                    SimpleDateFormat pattern = new SimpleDateFormat(patternYouWant, Locale.US);
                    pattern.setTimeZone(TimeZone.getDefault());
                    return pattern.format(dateObj1);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCustomDate(String date, String patternYouWant) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateObj1 = null;
        try {
            dateObj1 = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat pattern = new SimpleDateFormat(patternYouWant, Locale.US);
        pattern.setTimeZone(TimeZone.getDefault());
        return pattern.format(dateObj1);
    }

    public static long getTime(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date dateObj1 = null;
        try {
            dateObj1 = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dateObj1 != null) {
            return dateObj1.getTime();
        } else {
            return 0;
        }
    }

    public static Date getDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return null;
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date dateObj1 = null;
        try {
            dateObj1 = inputFormat.parse(date);
            inputFormat.setTimeZone(TimeZone.getDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateObj1;
    }

    public static String convertDateToString(Date dt) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return simpleDate.format(dt);
    }

    private static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private static Date getStringDate(String dtStart) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            return inputFormat.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTimeAgo(Date date, Context mContext) {
        if (date == null) {
            return null;
        }
        long time = date.getTime();
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate().getTime();
//        if (time > now || time <= 0) {
//            return "in the future";
//        }

        final long diff = now - time;
        if (0 < diff && diff < MINUTE_MILLIS) {
            return mContext.getString(R.string.moments_ago);
        } else if (0 < diff && diff < 2 * MINUTE_MILLIS) {
            return mContext.getString(R.string.a_moment_ago);
        } else if (0 < diff && diff < 60 * MINUTE_MILLIS) {
            return String.format(Locale.getDefault(), "%d%s", diff / MINUTE_MILLIS, mContext.getString(R.string.minutes_ago));
        } else if (0 < diff && diff < 2 * HOUR_MILLIS) {
            return mContext.getString(R.string.an_hour_ago);
        } else if (0 < diff && diff < 48 * HOUR_MILLIS) {
            if (isSameDay(date)) {
                return String.format(Locale.getDefault(), "%d%s", diff / HOUR_MILLIS, mContext.getString(R.string.hours_ago));
            } else if (isYesterday(date)) {
                return mContext.getString(R.string.yesterday);
            } else {
                return String.format(Locale.getDefault(), "%d%s", diff / DAY_MILLIS, mContext.getString(R.string.day_ago));
            }
        } else if (0 < diff && diff < 7 * 24 * HOUR_MILLIS) {
            return String.format(Locale.getDefault(), "%d%s", diff / DAY_MILLIS, mContext.getString(R.string.days_ago));
        } else {
            return getLocalizedFormattedDate(convertDateToString(date));
        }
    }

    public static String getTimeAgoLikeFb(Date date) {
        if (date == null) {
            return null;
        }
        long time = date.getTime();
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate().getTime();

        final long diff = now - time;
        if (0 < diff && diff < MINUTE_MILLIS) {
            return "just now";
        } else if (0 < diff && diff < 2 * MINUTE_MILLIS) {
            return "1m";
        } else if (0 < diff && diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "m";
        } else if (0 < diff && diff < 2 * HOUR_MILLIS) {
            return "1h";
        } else if (0 < diff && diff < 48 * HOUR_MILLIS) {
            if (isSameDay(date)) {
                return diff / HOUR_MILLIS + "h";
            } else {
                return "1d";
            }
        } else if (0 < diff && diff < 7 * 24 * HOUR_MILLIS) {
            return diff / DAY_MILLIS + "d";
        } else {
            return getLocalizedFormattedDate(convertDateToString(date));
        }
    }

    private static boolean isSameDay(Date date) {
        Calendar thisCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        Date date1 = new Date();
        thisCalendar.setTime(date1);
        otherCalendar.setTime(date);
        return thisCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR) && thisCalendar.get(Calendar.DAY_OF_YEAR) == otherCalendar.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isYesterday(Date date) {
        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date); // your date

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isSameYear(Date date) {
        Calendar thisCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        Date date1 = new Date();
        thisCalendar.setTime(date1);
        otherCalendar.setTime(date);
        return thisCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
    }

    public static void animateIcon(final boolean shouldShow, final View view) {

        float scaleX = 1.0f;
        float scaleY = 1.0f;
        int duration = 200;

        // IF HIDE CURRENT LOCATION ICON
        if (!shouldShow) {
            scaleX = 0.0f;
            scaleY = 0.0f;
        }

        // ANIMATE ICON
        view.animate().scaleX(scaleX).scaleY(scaleY).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // if showing icon
                if (shouldShow) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // if hiding icon
                if (!shouldShow) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static void showKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception ignore) {

        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignore) {
        }
    }

    public static boolean isPictureLight(android.graphics.Bitmap bitmap, int skipPixel) {
        int R = 0;
        int G = 0;
        int B = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += skipPixel) {
            int color = pixels[i];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            n++;
        }

        int i = (R + B + G) / (n * 3);
        Log.d("TAG", "isPictureLight: == " + i);

        return i > 160;
    }

    public static void expand(final View view) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Set initial height to 0 and show the view
        view.getLayoutParams().height = R.dimen._45sdp;
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), targetHeight);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(1000);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = (int) (targetHeight * animation.getAnimatedFraction());
                view.setLayoutParams(layoutParams);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // At the end of animation, set the height to wrap content
                // This fix is for long views that are not shown on screen
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });
        anim.start();
    }

    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    public static String generatePageNumber(int currentPage, int nextPage) {
        return currentPage + "__" + nextPage;
    }

    public static List<String> getPageNumbers(String data) {
        return Arrays.asList(data.split("__"));
    }

    public static void viewAnimation(TextView textView, boolean shouldShow) {
        if (shouldShow) {
            textView.setScaleY(0);
            textView.setScaleX(0);
            textView.animate().scaleY(1).scaleX(1).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
        } else {
            textView.setScaleY(1);
            textView.setScaleX(1);
            textView.animate().scaleY(0).scaleX(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    public static String checkString(String str) {
        char ch;
        String status = null;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        boolean specialCh = false;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (Character.isDigit(ch)) {
                numberFlag = true;
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }

            if (i == str.length() - 1) {
                specialCh = hasSpecialCh(str);
            }

            if (numberFlag && capitalFlag && lowerCaseFlag && specialCh && str.length() >= 8) {
                status = "Strong";
            } else if (str.length() >= 8) {
                status = "Medium";
            } else {
                status = "Weak";
            }
        }
        return status;
    }

    public static boolean hasSpecialCh(String password) {
        Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
        Matcher hasSpecial = special.matcher(password);
        return hasSpecial.find();
    }

    public static String getMessage(String body) {
        try {
            if (!TextUtils.isEmpty(body)) {
                JSONObject obj = new JSONObject(body);
                if (obj != null) {
                    return obj.getString("message");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a list of packages that support Custom Tabs.
     */
    public static ArrayList<ResolveInfo> getCustomTabsPackages(Context context) {
        PackageManager pm = context.getPackageManager();
        // Get default VIEW intent handler.
        Intent activityIntent = new Intent().setAction(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_BROWSABLE).setData(Uri.fromParts("http", "", null));

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        ArrayList<ResolveInfo> packagesSupportingCustomTabs = new ArrayList<>();
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

    public static void showDialog(Context context, String msg, String positiveBtn, dialogClick listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setMessage(msg)
//                .setTitle(title)
//                .setCancelable(false)
                .setPositiveButton(positiveBtn, (dialog, id) -> {
                    if (listener != null) {
                        listener.isPositive(true);
                    }
                    dialog.dismiss();
                }).setNegativeButton("Cancel", (dialog, id) -> {
                    if (listener != null) {
                        listener.isPositive(false);
                    }
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.setOnShowListener(arg0 -> {
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.primaryRed));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.primaryRed));
        });
        alert.show();
    }

    public static void showPopupMessageWithCloseButton(Activity activity, int delay, String msg, boolean isError) {
        if (!activity.isFinishing()) {
            try {
                activity.runOnUiThread(() -> {

                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.message_popup_view, null);
                    builder.setView(dialogView);
                    RelativeLayout alert_box = dialogView.findViewById(R.id.alert_box);
                    TextView close = dialogView.findViewById(R.id.cancel_alert);
                    TextView alert_box_text = dialogView.findViewById(R.id.alert_box_text);
                    alert_box_text.setText(msg);
                    if (isError) {
                        alert_box.setBackgroundColor(activity.getResources().getColor(R.color.theme_color_1));
                    }
                    dialog = builder.create();
                    dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.TOP;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    wlp.dimAmount = 0.0f;
                    window.setAttributes(wlp);

                    if (delay > 0) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(dialog::dismiss, delay);
                    } else {
                        dialog.setCancelable(false);
                    }

                    dialog.show();
                    close.setOnClickListener(view -> {
                        dialog.dismiss();
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openHelpView(Context context, String url, String title) {
        AnalyticsEvents.INSTANCE.logEvent(context, Events.HELP_CENTER);
        Intent intent = new Intent(context, HelpActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    public static void openWebView(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static int getHeight(int val) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        Log.e("CehckHeigt", "=========================================");
        Log.e("CehckHeigt", "val : " + val);
        Log.e("CehckHeigt", "heightPixels : " + metrics.heightPixels);
        Log.e("CehckHeigt", "standard_device_height : " + Constants.standard_device_height);
        double div = metrics.heightPixels / Constants.standard_device_height;
        Log.e("CehckHeigt", "divider : " + div);
        double calculated = (div * val);
        Log.e("CehckHeigt", "calculated : " + calculated);
        return (int) calculated;
    }

    public static String getDeviceDensity(Context context) {
        int density = context.getResources().getDisplayMetrics().densityDpi;
        Log.e("DPNEDDe", "value : " + density);
        switch (density) {
            case DisplayMetrics.DENSITY_MEDIUM:
                return "MDPI";
            case DisplayMetrics.DENSITY_HIGH:
                return "HDPI";
            case DisplayMetrics.DENSITY_LOW:
                return "LDPI";
            case DisplayMetrics.DENSITY_XHIGH:
                return "XHDPI";
            case DisplayMetrics.DENSITY_TV:
                return "TV";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "XXHDPI";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "XXXHDPI";
            default:
                return "Unknown";
        }
    }

    public static int toPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int dpToSp(float dp, Context context) {
        return (int) (dpToPx(dp, context) / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int getActualDuration(String txt) {
        if (containsNumber(txt)) {
            return 0;
        } else {
            return txt.length() * 100;
        }
    }

    public static boolean containsNumber(String str) {
        return str.matches(".*\\d.*");
    }

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName() + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm", Locale.ENGLISH).format(new Date());
        File mediaFile;
        String mImageName = "DOC_" + timeStamp + ".jpeg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static void logEvent(Context context, String Event) {
//        if (BuildConfig.DEBUG) {
//            return;
//        }

        if (mContext == null || prefConfig == null) {
            mContext = context;
            prefConfig = new PrefConfig(mContext);
        }
        Log.e("Analytics", "Event : " + Event);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle params = new Bundle();
        try {
            firebaseAnalytics.setUserProperty("user", prefConfig.isUserObject().getId());
        } catch (Exception ignore) {
        }
        firebaseAnalytics.logEvent(Event, params);

        //appsflyer
//        if (mContext == null || prefConfig == null) {
//            mContext = context;
//            prefConfig = new PrefConfig(mContext);
//        }
//        Map<String, Object> eventValue = new HashMap<>();
//        if (prefConfig != null && !TextUtils.isEmpty(prefConfig.getUserEmail()))
//            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID, prefConfig.getUserEmail());
//        else
//            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID, prefConfig.getUserEmail());
//        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), Event, eventValue);
    }

    public static void logEvent(Context context, String articleId, String event) {
//        if (BuildConfig.DEBUG) {
//            return;
//        }

        if (mContext == null || prefConfig == null) {
            mContext = context;
            prefConfig = new PrefConfig(mContext);
        }
        Log.e("Analytics", "Event : " + event + "  articleId : " + articleId);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle params = new Bundle();
//        params.putString("user", prefConfig.getUsername());
        params.putString("article_id", articleId);
        try {
            firebaseAnalytics.setUserProperty("user", prefConfig.isUserObject().getId());
        } catch (Exception ignore) {
        }
        firebaseAnalytics.logEvent(event, params);


        retrofit2.Call<ResponseBody> call = ApiClient.getInstance().getApi().event("Bearer " + prefConfig.getAccessToken(), articleId, event, "0");
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });


        //appsflyer
//        if (mContext == null || prefConfig == null) {
//            mContext = context;
//            prefConfig = new PrefConfig(mContext);
//        }
//        Map<String, Object> eventValue = new HashMap<>();
//        if (prefConfig != null && !TextUtils.isEmpty(prefConfig.getUserEmail()))
//            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID, prefConfig.getUserEmail());
//        else
//            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID, prefConfig.getUserEmail());
//
//        eventValue.put("articleId", articleId);
//        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), Event, eventValue);
    }

    public static void logDurationEvent(Context context, String articleId, long duration, String event) {
//        if (BuildConfig.DEBUG) {
//            return;
//        }

        if (mContext == null || prefConfig == null) {
            mContext = context;
            prefConfig = new PrefConfig(mContext);
        }
        Log.e("Analytics", "Event : " + event + "  articleId : " + articleId);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle params = new Bundle();
//        params.putString("user", prefConfig.getUsername());
        params.putString("article_id", articleId);
        params.putLong("duration", duration);
        try {
            firebaseAnalytics.setUserProperty("user", prefConfig.isUserObject().getId());
        } catch (Exception ignore) {
        }
        firebaseAnalytics.logEvent(event, params);

        retrofit2.Call<ResponseBody> call = ApiClient.getInstance().getApi().event("Bearer " + prefConfig.getAccessToken(), articleId, event, String.valueOf(duration));
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });


        //appsflyer
//        if (mContext == null || prefConfig == null) {
//            mContext = context;
//            prefConfig = new PrefConfig(mContext);
//        }
//        Map<String, Object> eventValue = new HashMap<>();
//        if (prefConfig != null && !TextUtils.isEmpty(prefConfig.getUserEmail()))
//            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID, prefConfig.getUserEmail());
//        else
//            eventValue.put(AFInAppEventParameterName.CUSTOMER_USER_ID, prefConfig.getUserEmail());
//
//        eventValue.put("articleId", articleId);
//        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), Event, eventValue);
    }

    public static void displaySnackBarWithBottomMargin(Snackbar snackbar, int sideMargin, int marginBottom) {
        final View snackBarView = snackbar.getView();
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams();

        params.setMargins(params.leftMargin + sideMargin, params.topMargin, params.rightMargin + sideMargin, params.bottomMargin + marginBottom);

        snackBarView.setLayoutParams(params);
        snackbar.show();
    }

    public static boolean isTimeToShowReview(long loginTime) {
        long days_10 = 1000 * 60 * 60 * 24 * 10L;

        Log.d("TAG", "isTimeToShowReview: " + getDate(loginTime, "dd/MM/yyyy hh:mm:ss.SSS"));
        Log.d("TAG", "isTimeToShowReview: " + getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss.SSS"));
        return System.currentTimeMillis() - loginTime > days_10;
    }

    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param dateFormat   Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static void enableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                enableView(child, enabled);
            }
        }
    }

    public static boolean isTimeInFuture(String datrS) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = sdf.parse(datrS);
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            Calendar yourDate = Calendar.getInstance();
            yourDate.setTimeInMillis(date.getTime());
            return now.after(yourDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static void saveSystemThemeAsDefault(Context context, PrefConfig preference) {
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            preference.setAppTheme(Constants.DARK);
        } else {
            preference.setAppTheme(Constants.LIGHT);
        }
    }

    public static Integer getLoaderForTheme(String theme) {
//        if (theme != null && theme.equals(Constants.DARK)) {
//            return R.raw.loader_night;
//        }
//        return R.raw.loader;
        return R.raw.loader_night;
    }

    public static Integer getPlaceholderForTheme(String theme) {
        if (theme != null && theme.equals(Constants.DARK)) {
            return R.drawable.ad_shape_with_stroke;
        }
        return R.drawable.ad_shape_with_stroke;
    }

    public static Integer getProcessingForTheme(String theme) {
        if (theme != null && theme.equals(Constants.DARK)) {
            return R.raw.processing;
        }
        return R.raw.processing_black;
    }

    public static Integer getAboutUsGifTheme(String theme) {
//        if (theme != null && theme.equals(Constants.DARK)) {
//            return R.raw.about_us;
//        }
//        return R.raw.about_us_night;
        return R.raw.about_us;
    }

    public static Integer getLanguageDirectionForView(String langCode) {
        if (langCode != null && (langCode.equals("fa") || langCode.equals("ar") || langCode.equals("iw") || langCode.equals("ur"))) {
            return View.TEXT_DIRECTION_RTL;
        } else {
            return View.TEXT_DIRECTION_LTR;
        }
    }

    public static void openDetailView(Activity context, Article article, String type) {
        Log.e("openDetailView @@@", "called");
//        MessageEvent messageEvent = new MessageEvent();
//        messageEvent.setType(MessageEvent.TYPE_SHOW_DETAIL_VIEW);
//        messageEvent.setObjectData(article);
//
//        EventBus.getDefault().post(messageEvent);

        Intent intent = new Intent(context, BulletDetailActivity.class);
        intent.putExtra("article", new Gson().toJson(article));
        intent.putExtra("type", type);
        context.startActivity(intent);
        if (context instanceof BulletDetailActivity) {
            context.finish();
        }
    }

    public static void openAuthor(Context context, Author author) {
        Log.e("openDetailView @@@", "called");
        Intent intent = null;
        User user = new PrefConfig(context).isUserObject();
        if (author != null && !TextUtils.isEmpty(author.getId())) {
            if (user != null && !TextUtils.isEmpty(user.getId()) && user.getId().equalsIgnoreCase(author.getId())) {
                intent = new Intent(context, ProfileActivity.class);
            } else {
                intent = new Intent(context, AuthorActivity.class);
                intent.putExtra("authorID", author.getId());
                intent.putExtra("authorContext", author.getContext());
            }
        }
        if (intent != null) context.startActivity(intent);
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws IOException {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixelSize) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = roundPixelSize;
        paint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static ArrayList<SectionsItem> checkArticleDataIsSame(ArrayList<SectionsItem> cacheRecord, ArrayList<SectionsItem> apiRecord) {
        ArrayList<SectionsItem> newData = new ArrayList<>();
        if (cacheRecord != null && cacheRecord.size() > 0 && apiRecord != null && apiRecord.size() > 0) {
            for (SectionsItem api : apiRecord) {
                boolean found = false;
                for (SectionsItem cache : cacheRecord) {
                    if (!TextUtils.isEmpty(cache.getId()) && !TextUtils.isEmpty(api.getId())) {
                        if (cache.getId().equalsIgnoreCase(api.getId())) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    newData.add(api);
                }
            }
        }
        return newData;
    }

    public static ArrayList<ReelsItem> checkReelDataIsSame(List<ReelsItem> cacheRecord, List<ReelsItem> apiRecord) {
        ArrayList<ReelsItem> newData = new ArrayList<>();
        if (cacheRecord != null && cacheRecord.size() > 0 && apiRecord != null && apiRecord.size() > 0) {
            for (ReelsItem api : apiRecord) {
                boolean found = false;
                for (ReelsItem cache : cacheRecord) {
                    if (cache.getId().equalsIgnoreCase(api.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newData.add(api);
                }
            }
        }
        return newData;
    }

    public static ArrayList<DiscoverItem> checkDiscoverDataIsSame(List<DiscoverItem> cacheRecord, List<DiscoverItem> apiRecord) {
        ArrayList<DiscoverItem> newData = new ArrayList<>();
        if (cacheRecord != null && cacheRecord.size() > 0 && apiRecord != null && apiRecord.size() > 0) {
            for (DiscoverItem api : apiRecord) {
                boolean found = false;
                for (DiscoverItem cache : cacheRecord) {
                    if (cache.getData() != null && api.getData() != null && !TextUtils.isEmpty(cache.getData().getContext()) && !TextUtils.isEmpty(api.getData().getContext())) {
                        if (cache.getData().getContext().equalsIgnoreCase(api.getData().getContext())) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    newData.add(api);
                }
            }
        }
        return newData;
    }


    public static void addNoDataErrorView(Context context, ViewGroup mainViewGroup, View.OnClickListener refreshClickListener, String theme) {
        if (context != null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.no_data_error, null);

            RelativeLayout refreshButton = v.findViewById(R.id.refresh);
            ImageView noDataImage = v.findViewById(R.id.no_data_image);
            TextView sorryText = v.findViewById(R.id.sorry_text);
            TextView buttonText = v.findViewById(R.id.button_text);

            refreshButton.setOnClickListener(refreshClickListener);

            mainViewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            Glide.with(noDataImage).load(R.drawable.no_data_error_icon_light).into(noDataImage);
            sorryText.setTextColor(ContextCompat.getColor(context, R.color.black));
            buttonText.setTextColor(ContextCompat.getColor(context, R.color.black));
//                }

            //theme.equals(Constants.DARK)
//            }

            mainViewGroup.removeAllViews();
            mainViewGroup.addView(v);
        }
    }

    public static void addNoDataView(Context context, ViewGroup mainViewGroup, String theme) {
        if (context != null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.no_data_error, null);

            RelativeLayout refreshButton = v.findViewById(R.id.refresh);
            ImageView noDataImage = v.findViewById(R.id.no_data_image);
            TextView sorryText = v.findViewById(R.id.sorry_text);
            TextView tvContent = v.findViewById(R.id.textView7);
            TextView buttonText = v.findViewById(R.id.button_text);

            refreshButton.setVisibility(View.GONE);
            sorryText.setText("");

            tvContent.setText(context.getString(R.string.no_reels_yet));

//            if (theme == null) {
//                int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//                    Glide.with(noDataImage).load(R.drawable.no_data_error_icon_dark).into(noDataImage);
//                } else {
//                    Glide.with(noDataImage).load(R.drawable.no_data_error_icon_light).into(noDataImage);
//                }
//                mainViewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//                v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//                sorryText.setTextColor(ContextCompat.getColor(context, R.color.textHeader));
//                buttonText.setTextColor(ContextCompat.getColor(context, R.color.textHeader));
//            } else {
//                if (theme.equals(Constants.DARK)) {
            mainViewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            Glide.with(noDataImage).load(R.drawable.no_data_error_icon_dark).into(noDataImage);
            sorryText.setTextColor(ContextCompat.getColor(context, R.color.white));
            buttonText.setTextColor(ContextCompat.getColor(context, R.color.white));
//                } else {
//            mainViewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//            v.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//            Glide.with(noDataImage).load(R.drawable.no_data_error_icon_light).into(noDataImage);
//            sorryText.setTextColor(ContextCompat.getColor(context, R.color.black));
//            buttonText.setTextColor(ContextCompat.getColor(context, R.color.black));
//                }

            //theme.equals(Constants.DARK)
//            }

            mainViewGroup.removeAllViews();
            mainViewGroup.addView(v);
        }
    }

    public static void addNoFollowingView(Context context, ViewGroup mainViewGroup, View.OnClickListener refreshClickListener, String theme) {
        if (context != null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.no_following_reels, null);

            CardView refreshButton = v.findViewById(R.id.button);
            ImageView noDataImage = v.findViewById(R.id.no_data_image);
            TextView sorryText = v.findViewById(R.id.sorry_text);
            TextView buttonText = v.findViewById(R.id.button_text);

            refreshButton.setOnClickListener(refreshClickListener);

//            if (theme == null) {
//                int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//                    Glide.with(noDataImage).load(R.drawable.no_data_error_icon_dark).into(noDataImage);
//                } else {
//                    Glide.with(noDataImage).load(R.drawable.no_data_error_icon_light).into(noDataImage);
//                }
//                mainViewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//                v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//                sorryText.setTextColor(ContextCompat.getColor(context, R.color.textHeader));
//                buttonText.setTextColor(ContextCompat.getColor(context, R.color.textHeader));
//            } else {
            if (theme.equals(Constants.DARK)) {
                mainViewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                Glide.with(noDataImage).load(R.drawable.no_result_art).into(noDataImage);
                sorryText.setTextColor(ContextCompat.getColor(context, R.color.white));
                buttonText.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                mainViewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                Glide.with(noDataImage).load(R.drawable.no_result_art).into(noDataImage);
                sorryText.setTextColor(ContextCompat.getColor(context, R.color.black));
                buttonText.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
//
//                //theme.equals(Constants.DARK)
//            }

            mainViewGroup.removeAllViews();
            mainViewGroup.addView(v);
        }
    }

    public static void setStatusBarColor(Activity activity) {
        int nightModeFlags = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        Window window = activity.getWindow();

//        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.black));
//            View decorView = window.getDecorView();
//            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //set status text  light
//        } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.white));
//        }
    }

    public static void setBlackStatusBar(Activity activity) {
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.black));
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static void setBlackStatusBarColor(Activity activity) {
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.white));
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static void setWhiteStatusBarColor(Activity activity) {
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.white));
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static String getErrorFromErrorBody(ResponseBody errorBody) {
        try {
            if (errorBody != null) {
                JSONObject jsonObject = new JSONObject(errorBody.string());
                return jsonObject.getString("message");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static boolean isFileLessThanMB(int max, File file) {
        int maxFileSize = max * 1024 * 1024;
        long l = file.length();
        String fileSize = Long.toString(l);
        int finalFileSize = Integer.parseInt(fileSize);
        Log.d("TAG", "isFileLessThanMB: " + finalFileSize);
        Log.d("TAG", "isFileLessThanMB: " + maxFileSize);
        return finalFileSize <= maxFileSize;
    }

    public static String getURIExtension(String url) {
        return url.substring(url.lastIndexOf("."));
    }

    public static String titleCaseConversion(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }

    /**
     * Images from the gallery have issues some time in the orientation, to fix it use this method
     *
     * @param image_absolute_path
     * @return
     * @throws IOException
     */
    public static Bitmap modifyOrientation(String image_absolute_path) throws IOException {
        Log.e("doInBackground", "FILE :  " + image_absolute_path);
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        Log.d("TAG", "modifyOrientation: " + orientation);


        Bitmap bitmap = BitmapFactory.decodeFile(image_absolute_path);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static void checkLikeView(Context context, int likeCounter, int commentCounter, TextView tv_CommentCount, TextView tv_likeCount, ImageView likeIcon, boolean isLiked) {
        try {
            tv_likeCount.setText("" + likeCounter);
            tv_CommentCount.setText("" + commentCounter);
            if (isLiked) {
                tv_likeCount.setTextColor(ContextCompat.getColor(context, R.color.theme_color_1));
                likeIcon.setImageResource(R.drawable.ic_reel_like_active);
                DrawableCompat.setTint(likeIcon.getDrawable(), context.getResources().getColor(R.color.theme_color_1));

            } else {
                likeIcon.setImageResource(R.drawable.ic_reel_like_inactive);
                tv_likeCount.setTextColor(ContextCompat.getColor(context, R.color.greyad));
                DrawableCompat.setTint(likeIcon.getDrawable(), context.getResources().getColor(R.color.greyad));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkLikeViewReels(Context context, int likeCounter, int commentCounter, TextView tv_CommentCount, TextView tv_likeCount, ImageView likeIcon, boolean isLiked) {
        if (context == null) return;
        tv_likeCount.setText("" + likeCounter);
        tv_CommentCount.setText("" + commentCounter);
        if (isLiked) {
            tv_likeCount.setTextColor(ContextCompat.getColor(context, R.color.like_heart_filled));
//            likeIcon.setImageResource(R.drawable.ic_reel_like_active);
        } else {
            tv_likeCount.setTextColor(ContextCompat.getColor(context, R.color.white));
//            likeIcon.setImageResource(R.drawable.ic_reel_like_inactive);
        }
    }

    public static File getFileFromBitmap(Context context, String filename, Bitmap bitmapOrg) {
        //create a file to write bitmap data
        File f = new File(context.getCacheDir(), filename + System.currentTimeMillis());
        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.e("doInBackground", "FILE 1 :  " + e.getMessage());
            e.printStackTrace();
        }

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (bitmapOrg != null) {

            //change resolution of image only if its very large resolution
            int width = bitmapOrg.getWidth();
            int height = bitmapOrg.getHeight();
            if (width >= 4000 || height >= 4000)
                bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, width / 3, height / 3, false);
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 70 /*ignored for PNG*/, bos);
        }
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("doInBackground", "FILE 2 :  " + e.getMessage());
        }
        try {
            if (fos != null) {
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("doInBackground", "FILE 3 :  " + e.getMessage());
        }
        return f;
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    public static boolean isRTL(Locale locale) {
        if (locale != null && !TextUtils.isEmpty(locale.getDisplayName()) && locale.getDisplayName().length() > 0) {
            final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
            return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
        }
        return false;
    }

    public static String convertLocalCalendarToUTC(Calendar calendar) {
        Date calendarTime = calendar.getTime();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return inputFormat.format(calendarTime);
    }

    public static String convertCalendarToLocalString(Calendar calendar) {
        Date calendarTime = calendar.getTime();
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, hh:mm aaa", Locale.US);
        return inputFormat.format(calendarTime);
    }

    public static void loadSkeletonLoader(ViewSwitcher viewSwitcher, boolean show) {
        if (show) {
            if (!(viewSwitcher.getCurrentView() instanceof ShimmerFrameLayout)) {
                viewSwitcher.showNext();
            }
        } else {
            if (viewSwitcher.getCurrentView() instanceof ShimmerFrameLayout) {
                viewSwitcher.showNext();
            }
        }
    }

    public static void loadSkeletonLoaderReels(ViewSwitcher viewSwitcher, boolean show) {
        if (show) {
            if (!(viewSwitcher.getCurrentView() instanceof FrameLayout)) {
                viewSwitcher.showNext();
            }
        } else {
            if (viewSwitcher.getCurrentView() instanceof FrameLayout) {
                viewSwitcher.showNext();
            }
        }
    }

    public static void invalidateShimmerView(ViewSwitcher viewSwitcher, Activity activity, int resId) {
        ShimmerFrameLayout shimmerFrameLayout = null;
        if (viewSwitcher.getCurrentView() instanceof ShimmerFrameLayout) {
            shimmerFrameLayout = (ShimmerFrameLayout) viewSwitcher.getCurrentView();
        } else if (viewSwitcher.getNextView() instanceof ShimmerFrameLayout) {
            shimmerFrameLayout = (ShimmerFrameLayout) viewSwitcher.getNextView();
        }
        if (shimmerFrameLayout != null) {
            ViewGroup parent = (ViewGroup) shimmerFrameLayout.getParent();
            int index = parent.indexOfChild(shimmerFrameLayout);
            parent.removeView(shimmerFrameLayout);
            shimmerFrameLayout = (ShimmerFrameLayout) activity.getLayoutInflater().inflate(resId, parent, false);
            parent.addView(shimmerFrameLayout, index);
        }
    }

    public static void expand2(View view, View one, View two, View three) {
        Animation animation = expandAction(view, one, two, three);
        view.startAnimation(animation);
    }

    private static Animation expandAction(final View view, View one, View two, View three) {

        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int actualheight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                view.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (actualheight * interpolatedTime);
                view.requestLayout();

                one.animate().setDuration(300).alpha(1);
                two.animate().setDuration(300).alpha(1);
                three.animate().setDuration(300).alpha(1);
            }
        };


//        animation.setDuration((long) (actualheight / view.getContext().getResources().getDisplayMetrics().density));
        animation.setDuration(500);

        view.startAnimation(animation);

        return animation;


    }

    public static void collapseAction(View view, View one, View two, View three, int duration) {

        final int actualHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                if (interpolatedTime == 1) {
                    one.animate().setDuration(duration).alpha(0);
                    two.animate().setDuration(duration).alpha(0);
                    three.animate().setDuration(duration).alpha(0);
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = actualHeight - (int) (actualHeight * interpolatedTime);
                    view.requestLayout();

                }
            }
        };

//        animation.setDuration((long) (actualHeight/ view.getContext().getResources().getDisplayMetrics().density));
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    public static void openFullScreen(Context context, Article article, String mode, long duration) {
        Intent intent = new Intent(context, VideoFullScreenActivity.class);
        intent.putExtra("url", article.getLink());
        intent.putExtra("mode", mode);
        intent.putExtra("position", article.getLastPosition());
        intent.putExtra("duration", duration);
        context.startActivity(intent);
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public static Animation outToLeftAnimation(View view) {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(1000);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    public static Animation inFromLeftAnimation(View view) {
        Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(1000);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        inFromLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        outToLeftAnimation(view);
                    }
                }, 3000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return inFromLeft;
    }

    public static Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

    public static Bitmap getBitmapFromView(Context ctx, View view) {
        view.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        view.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(canvas);
        return bitmap;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static void copyTextToClipboard(Context context, CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData label = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(label);
    }

    public static int calculateBrightness(Bitmap bitmap, int skipPixel) {
        int R = 0;
        int G = 0;
        int B = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += skipPixel) {
            int color = pixels[i];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            n++;
        }
        return (R + B + G) / (n * 3);
    }

    public static int getVideoDurationSeconds(long timeMs) {
        return (int) timeMs / 1000;
    }

    public static String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public enum Direction {
        up, down, left, right;

        /**
         * Returns a direction given an angle.
         * Directions are defined as follows:
         * <p>
         * Up: [45, 135]
         * Right: [0,45] and [315, 360]
         * Down: [225, 315]
         * Left: [135, 225]
         *
         * @param angle an angle from 0 to 360 - e
         * @return the direction of an angle
         */
        public static Direction fromAngle(double angle) {
            if (inRange(angle, 45, 135)) {
                return Direction.up;
            } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
                return Direction.right;
            } else if (inRange(angle, 225, 315)) {
                return Direction.down;
            } else {
                return Direction.left;
            }

        }

        /**
         * @param angle an angle
         * @param init  the initial bound
         * @param end   the final bound
         * @return returns true if the given angle is in the interval [init, end).
         */
        private static boolean inRange(double angle, float init, float end) {
            return (angle >= init) && (angle < end);
        }
    }
}