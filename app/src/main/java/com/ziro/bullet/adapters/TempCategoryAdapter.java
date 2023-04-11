package com.ziro.bullet.adapters;

import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ziro.bullet.fragments.CategoryFragment;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.HomeLoaderCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.CategoryFragmentModel;

import java.util.ArrayList;

public class TempCategoryAdapter extends FragmentStatePagerAdapter {
    private ArrayList<CategoryFragmentModel> categories;
    private CategoryFragment resto;
    private OnGotoChannelListener listener;
    private String sourceId = "";
    private boolean isHome;
    private boolean specificSourceTopic;
    private String type;
    private boolean isDark;
    private GoHome goHomeMainActivityListener;
    private GoHome goHomeTempHomeListener;
    private FragmentManager fragmentManager;
    private TempCategorySwipeListener swipeListener;
    private HomeLoaderCallback loaderCallback;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private String from;

    public TempCategoryAdapter(String from, boolean isDark, TempCategorySwipeListener swipeListener, String type,
                               boolean specificSourceTopic, GoHome goHomeTempHomeListener, GoHome goHomeMainActivityListener,
                               boolean isHome, @NonNull FragmentManager fm, int behavior, ArrayList<CategoryFragmentModel> categories, OnGotoChannelListener listener, HomeLoaderCallback loaderCallback) {
        super(fm, behavior);
        this.fragmentManager = fm;
        this.from = from;
        this.swipeListener = swipeListener;
        this.categories = categories;
        this.isHome = isHome;
        this.type = type;
        this.isDark = isDark;
        this.specificSourceTopic = specificSourceTopic;
        this.goHomeMainActivityListener = goHomeMainActivityListener;
        this.goHomeTempHomeListener = goHomeTempHomeListener;
        this.listener = listener;
        this.loaderCallback = loaderCallback;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.e("expandCard", "getItem : " + position);
        resto = CategoryFragment.newInstance(from, isDark, categories.get(position).getName(), categories.get(position).getTopicId(),
                categories.get(position).getSourceId(), categories.get(position).getHeadlineId(), categories.get(position).getArticleId(),
                categories.get(position).getLocationId(), categories.get(position).getContextId(), type, specificSourceTopic,
                isHome, goHomeMainActivityListener, goHomeTempHomeListener, loaderCallback, categories.get(position));
        resto.setMyTag("id_" + position);
        if (listener != null) {
            resto.setGotoChannelListener(swipeListener, listener, true);
        } else {
            resto.setGotoChannelListener(swipeListener, null, false);
        }
        return resto;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    public CategoryFragment getFragment() {
        return resto;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (getFragment() != object) {
            resto = (CategoryFragment) object;
        }
        super.setPrimaryItem(container, position, object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        Log.e("PAGER", "-instantiateItem-> pos : " + position);
        registeredFragments.put(position, fragment);
        if (resto != null)
            resto.setMyTag("id_" + position);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        Log.e("PAGER", "-destroyItem-> pos : " + position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public void restoreState(final Parcelable state, final ClassLoader loader) {
        try {
            super.restoreState(state, loader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
