package com.newsreels.app.adapters.relevant.callbacks;

import com.newsreels.app.data.models.sources.Source;

public interface SourceFollowingCallback {
    void onItemFollowed(Source source);

    void onItemUnfollowed(Source source);

    void onItemClicked(Source source);
}
