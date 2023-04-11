package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.ShareInfo;

public interface ShareInfoInterface {
    void response(ShareInfo shareInfo);

    void error(String error);
}
