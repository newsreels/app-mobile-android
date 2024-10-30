package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.config.UserConfigModel;

public interface AccountLinkCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void error404(String error);

    void onLinkSuccess(boolean status);
}
