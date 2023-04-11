package com.ziro.bullet.utills;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;


public abstract class PaginationListener extends RecyclerView.OnScrollListener {
    public static final String PAGE_START = "";
    @NonNull
    private LinearLayoutManager layoutManager;
    /**
     * Set scrolling threshold here (for now i'm assuming 10 item in one page)
     */
    private static final int PAGE_SIZE = 10;

    /**
     * Supporting only LinearLayoutManager for now.
     */
    public PaginationListener(@NonNull LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();


        // onScrolling(recyclerView, 0);

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_SIZE) {
                loadMoreItems();
            }
        }
    }

    @Override
    public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == SCROLL_STATE_IDLE) {
            onScroll(layoutManager.findFirstVisibleItemPosition());
        }

        onScrolling(recyclerView, newState);

//        else if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
//            onScrolling(recyclerView, newState);
//        }
    }

    protected abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

    public abstract void onScroll(int position);

    public abstract void onScrolling(@NonNull @NotNull RecyclerView recyclerView, int newState);
}