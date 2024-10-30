package com.newsreels.app.fragments.Reels;

import com.newsreels.app.model.Reel.ReelsItem;

import java.util.ArrayList;

public interface ReelsPageInterface {
    void onclickReel( ArrayList<ReelsItem> reelsItems,ReelsItem item );
}
