package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.push.Push;

public interface PushNotificationInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void success(Push push);

    void SuccessFirst(boolean flag);

}
