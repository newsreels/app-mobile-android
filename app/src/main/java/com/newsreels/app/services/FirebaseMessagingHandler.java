package com.newsreels.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.newsreels.app.R;
import com.newsreels.app.activities.MainActivityNew;
import com.newsreels.app.model.notification.OneSignalNotificationModel;

import org.json.JSONObject;

public class FirebaseMessagingHandler extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessaging";
    private static final String CHANNEL_ID = "newsreels_notifications";
    private static final String CHANNEL_NAME = "Newsreels";
    private static final int NOTIFICATION_ID = 100;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Send this token to your server to associate it with the user
        Log.d(TAG, "New FCM Token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: type "+remoteMessage.getData());
        try {
            // Convert notification data to JSON
            JSONObject payload = new JSONObject(remoteMessage.getData());
            
            // Parse the notification data
            OneSignalNotificationModel model = new Gson().fromJson(payload.toString(), OneSignalNotificationModel.class);

            // Create intent for notification click
            Intent intent = new Intent(this, MainActivityNew.class);
            intent.putExtra("type", model.getType());
            Log.d(TAG, "onMessageReceived: type "+model.getType());
            if (model.getType().equalsIgnoreCase("reel")) {
                intent.putExtra("reel_id", model.getId());

            } else if (model.getType().equalsIgnoreCase("article")) {
                intent.putExtra("article_id", model.getId());
            }
            Log.d(TAG, "onMessageReceived: type "+ model.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            // Create PendingIntent
            PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
            );

            // Create and show notification
            createNotification(
                    model.getTitle(),
                    model.getMessage(),
                pendingIntent
            );

        } catch (Exception e) {
            Log.e(TAG, "Error processing notification: ", e);
        }
    }

    private void createNotification(String title, String message, PendingIntent pendingIntent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon) // Replace with your app icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent);

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}