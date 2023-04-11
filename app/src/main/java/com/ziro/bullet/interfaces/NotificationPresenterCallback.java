package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.notification.GeneralNotificationResponse;

public interface NotificationPresenterCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(Object response);
}
