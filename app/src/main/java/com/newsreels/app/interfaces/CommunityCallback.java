package com.newsreels.app.interfaces;

import com.newsreels.app.data.models.AuthorListResponse;
import com.newsreels.app.model.Reel.ReelResponse;

public interface CommunityCallback extends NewsCallback{

    void authors(AuthorListResponse response);

    void reels(ReelResponse response);
}
