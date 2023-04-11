package com.ziro.bullet.adapters.relevant.callbacks;

import com.ziro.bullet.data.models.sources.Source;

public interface SourceFollowingCallback {
    void onItemFollowed(Source source);

    void onItemUnfollowed(Source source);

    void onItemClicked(Source source);
}
