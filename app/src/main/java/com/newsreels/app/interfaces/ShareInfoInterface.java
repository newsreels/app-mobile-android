package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.ShareInfo;

public interface ShareInfoInterface {
    void response(ShareInfo shareInfo);

    void error(String error);
}
