package com.ziro.bullet.notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.MainActivityNew;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.presenter.FCMPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

public class PushReceiverService extends FirebaseMessagingService {
    public static final String CHANNEL_ID = "21412";

    private PrefConfig prefConfig;
    private FCMPresenter presenter;
    private Context context;

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        context = this;
        if (!Utils.isAppIsInBackground(context)) {
            prefConfig = new PrefConfig(context);
            if (prefConfig.getLoginCount() != 0) {
                showNotification(remoteMessage);
            }
        }

//        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
//            Log.e("####", "Key : " + entry.getKey() + " Value : " + entry.getValue());
//        }
    }

    private void showNotification(RemoteMessage remoteMessage) {
        if (remoteMessage != null && remoteMessage.getNotification() != null && remoteMessage.getData() != null) {
            Log.e("showNotification", "RemoteMessage = [" + remoteMessage.getData() + "]");
            Log.e("showNotification", "RemoteMessage = [" + remoteMessage.getNotification() + "]");
            AnalyticsEvents.INSTANCE.logEvent(context,
                    Events.NOTIFICATION_RECEIVE);
            Intent intent = new Intent(this, MainActivityNew.class);

            intent.putExtra("source_name", remoteMessage.getData().get("source_name"));
            intent.putExtra("source_id", remoteMessage.getData().get("source_id"));
            intent.putExtra("type", remoteMessage.getData().get("type"));
            if (remoteMessage.getData().get("type").equalsIgnoreCase("reel.new")) {
                intent.putExtra("reel_id", remoteMessage.getData().get("article_id"));
            } else {
                intent.putExtra("article_id", remoteMessage.getData().get("article_id"));
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (remoteMessage.getNotification().getImageUrl() != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(remoteMessage.getNotification().getImageUrl())
                        .override(Constants.targetWidth, Constants.targetHeight)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                Utils.createNotification(context,
                                        remoteMessage.getNotification().getTitle(),
                                        remoteMessage.getNotification().getBody(),
                                        null,
                                        intent, null,
                                        null,
                                        getResources().getString(R.string.default_notification_channel_id),
                                        "Notifications for receiving Bullets Updates", CHANNEL_ID);
                            }

                            @Override
                            public void onResourceReady(@NonNull Bitmap bannerBitmap, @Nullable Transition<? super Bitmap> transition) {
                                Utils.createNotification(context,
                                        remoteMessage.getNotification().getTitle(),
                                        remoteMessage.getNotification().getBody(),
                                        bannerBitmap,
                                        intent, null,
                                        bannerBitmap,
                                        getResources().getString(R.string.default_notification_channel_id),
                                        "Notifications for receiving Bullets Updates", CHANNEL_ID);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            } else {
                Utils.createNotification(context,
                        remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody(),
                        null,
                        intent, null,
                        null,
                        getResources().getString(R.string.default_notification_channel_id),
                        "Notifications for receiving Bullets Updates", CHANNEL_ID);
            }
        }
    }

    @Override
    public void onNewToken(@NotNull String token) {
        super.onNewToken(token);
        context = this;
        Log.e("FCM", "FCM TOKEN : " + token);
        prefConfig = new PrefConfig(context);
        prefConfig.setFirebaseToken(token);
        presenter = new FCMPresenter(context);
        presenter.sentTokenToServer(prefConfig);
    }
}
