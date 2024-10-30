package com.newsreels.app.interfaces;

import com.newsreels.app.model.notification.GeneralNotificationResponse;

public interface NotificationPresenterCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(Object response);
}
