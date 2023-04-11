package com.ziro.bullet.interfaces;

/**
 * This callback is made to to remove the conflict in loaders in @{@link com.ziro.bullet.fragments.TempHomeFragment} and @{@link com.ziro.bullet.fragments.TempCategoryFragment}
 * There is an issue showing 2 loaders at the same time because of the api calls in both fragments.
 */
public interface HomeLoaderCallback {
    void onProgressChanged(boolean flag);
}
