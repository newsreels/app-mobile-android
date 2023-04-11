package com.ziro.bullet.services;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.ziro.bullet.R;

import org.json.JSONObject;

public class OneSignalNotificationServiceExtension {
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent osNotificationReceivedEvent) {
        OSNotification notification = osNotificationReceivedEvent.getNotification();

        // Example of modifying the notification's accent colo
        OSMutableNotification mutableNotification = notification.mutableCopy();
        mutableNotification.setExtender(builder -> {
            // Sets the accent color to Green on Android 5+ devices.
            // Accent color controls icon and action buttons on Android 5+. Accent color does not change app title on Android 10+
//            builder.setColor(new BigInteger("FF00FF00", 16).intValue());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                builder.setSmallIcon(R.mipmap.one_signal_adaptive_icon);
            } else {
                builder.setSmallIcon(R.drawable.ic_stat_onesignal_default);
            }
            // Sets the notification Title to Red
            Spannable spannableTitle = new SpannableString(notification.getTitle());
            spannableTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, notification.getTitle().length(), 0);
            builder.setContentTitle(spannableTitle);
            // Sets the notification Body to Blue
            Spannable spannableBody = new SpannableString(notification.getBody());
            spannableBody.setSpan(new ForegroundColorSpan(Color.BLUE), 0, notification.getBody().length(), 0);
            builder.setContentText(spannableBody);
            //Force remove push from Notification Center after 30 seconds
            return builder;
        });
        JSONObject data = notification.getAdditionalData();
        // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
        // To omit displaying a notification, pass `null` to complete()
        osNotificationReceivedEvent.complete(mutableNotification);
    }
}
