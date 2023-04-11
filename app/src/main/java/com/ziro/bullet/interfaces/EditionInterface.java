package com.ziro.bullet.interfaces;

import com.ziro.bullet.model.Edition.ResponseEdition;

public interface EditionInterface {
    void loaderShow(boolean flag);

    void error(String error);

    void success(ResponseEdition responseEdition);

    void successFollowed(ResponseEdition responseEdition);

    void onUpdateSuccess();

    void successFollowUnfollow(int position, String id, boolean isFollow);
}
