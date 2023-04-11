package com.ziro.bullet.adapters.feed;

import android.content.Context;
import android.view.View;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.fragments.BulletDetailFragment;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.utills.Utils;

public class DetailViewHolder extends RecyclerView.ViewHolder {

    private BulletDetailActivity context;

    private Article article;
    private BulletDetailPagerAdapter bulletDetailPagerAdapter;
    private ViewPager articlesViewpager;
    private ViewSwitcher viewSwitcher;

    private String type;
    private boolean isSelected;

    public DetailViewHolder(@NonNull View itemView, Context context1, String type) {
        super(itemView);
        this.context = (BulletDetailActivity) context1;
        this.type = type;
        articlesViewpager = itemView.findViewById(R.id.articles_viewpager);
        viewSwitcher = itemView.findViewById(R.id.viewSwitcher);
    }

    public void bind(int position, Article article, boolean isSelected) {
        this.article = article;
        this.isSelected = isSelected;
        setUpViewPager();
        Utils.loadSkeletonLoader(viewSwitcher, this.article == null);
    }

    private void setUpViewPager() {
        bulletDetailPagerAdapter = new BulletDetailPagerAdapter(context.getSupportFragmentManager(), article, type, isSelected);
        articlesViewpager.setAdapter(bulletDetailPagerAdapter);
    }

    public void pause() {
        if (bulletDetailPagerAdapter != null && bulletDetailPagerAdapter.getFragment() != null) {
            bulletDetailPagerAdapter.getFragment().pause();
        }
    }

    private static class BulletDetailPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 1;
        private BulletDetailFragment fragment;
        private Article article;
        private String type;
        private boolean isSelected;

        public BulletDetailPagerAdapter(FragmentManager fm, Article article, String type, boolean isSelected) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.article = article;
            this.type = type;
            this.isSelected = isSelected;
        }

        @Override
        public Fragment getItem(int position) {
            fragment = BulletDetailFragment.newInstance(article, type, isSelected);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        private BulletDetailFragment getFragment() {
            return fragment;
        }
    }
}
