package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.config.UserConfigModel;

public interface UserConfigCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void onUserConfigSuccess(UserConfigModel userConfigModel);
}
