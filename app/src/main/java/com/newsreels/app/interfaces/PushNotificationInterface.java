package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.push.Push;

public interface PushNotificationInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(Push push);

    void SuccessFirst(boolean flag);

}
