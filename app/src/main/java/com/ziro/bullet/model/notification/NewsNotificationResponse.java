package com.ziro.bullet.model.notification;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

import java.util.List;

public class NewsNotificationResponse extends BaseModel {

    @SerializedName("notifications")
    private List<NewsNotificationItem> notificationList;

    public List<NewsNotificationItem> getNotificationList() {
        return notificationList;
    }

}

