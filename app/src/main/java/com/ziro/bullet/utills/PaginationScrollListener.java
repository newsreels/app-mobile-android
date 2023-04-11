package com.ziro.bullet.utills;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by shine_joseph on 20/04/20.
 */
public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = PaginationScrollListener.class.getSimpleName();
    private LinearLayoutManager layoutManager;

    protected PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /*
     Method gets callback when user scroll the search list
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        onScroll();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                Log.i(TAG, "Loading more items");
                loadMoreItems();
            }
        }

    }

    protected abstract void loadMoreItems();

    protected abstract void onScroll();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}