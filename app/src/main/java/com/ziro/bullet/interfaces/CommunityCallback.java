package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.models.AuthorListResponse;
import com.ziro.bullet.model.Reel.ReelResponse;

public interface CommunityCallback extends NewsCallback{

    void authors(AuthorListResponse response);

    void reels(ReelResponse response);
}
