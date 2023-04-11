package com.ziro.bullet.services;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;
import com.ziro.bullet.activities.MainActivityNew;
import com.ziro.bullet.model.notification.OneSignalNotificationModel;

import org.json.JSONObject;

public class OneSignalNotificationOpenHandler implements OneSignal.OSNotificationOpenedHandler {

    private Context context;

    public OneSignalNotificationOpenHandler(Context context) {
        this.context = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {
        JSONObject payload = osNotificationOpenedResult.getNotification().getAdditionalData();
        try {
            OneSignalNotificationModel model = new Gson().fromJson(payload.toString(), OneSignalNotificationModel.class);
            Intent intent = new Intent(context, MainActivityNew.class);
            intent.putExtra("type", model.getType());
            if (model.getType().equalsIgnoreCase("reel")) {
                intent.putExtra("reel_id", model.getContext());
            } else if (model.getType().equalsIgnoreCase("article")) {
                intent.putExtra("article_id", model.getId());
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}