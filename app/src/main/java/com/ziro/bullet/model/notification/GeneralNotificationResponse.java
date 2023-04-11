package com.ziro.bullet.model.notification;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

import java.util.List;

public class GeneralNotificationResponse extends BaseModel {

    @SerializedName("notifications")
    private List<NotificationItem> notificationList;

    @SerializedName("new")
    private List<NotificationItem> newNotificationList;


    public List<NotificationItem> getNotificationList() {
        return notificationList;
    }

    public List<NotificationItem> getNewNotificationList() {
        return newNotificationList;
    }
}
