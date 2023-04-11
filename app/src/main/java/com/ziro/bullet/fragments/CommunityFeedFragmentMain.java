package com.ziro.bullet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.CommunityFeedPagerAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.HomeLoaderCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.CustomViewPager;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import static com.ziro.bullet.utills.Constants.HomeSelectedFragment;
import static com.ziro.bullet.utills.Constants.isHeader;


public class CommunityFeedFragmentMain extends Fragment implements View.OnClickListener, GoHome, TempCategorySwipeListener, HomeLoaderCallback {

    private PrefConfig mPrefConfig;
    private View view;
    private TextView errorText;
    private TabLayout tabs;
    private CustomViewPager viewpager;
    private LinearLayout noRecordFoundContainer;
    private ImageView gradient2;
    private CoordinatorLayout root;
    private CommunityFeedPagerAdapter adapter;
    private GoHome goHome;
    private OnCommunityFragmentInteractionListener listener;


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        Log.e("LKASD", "onAttach");
        if (context instanceof OnCommunityFragmentInteractionListener) {
            listener = (OnCommunityFragmentInteractionListener) context;
        }

        if (context instanceof GoHome) {
            goHome = (GoHome) context;
        }
    }

    public static CommunityFeedFragmentMain newInstance() {
        return new CommunityFeedFragmentMain();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community_feed, container, false);
        mPrefConfig = new PrefConfig(getContext());
        bindViews(view);
        init();
        tabListener();
        return view;
    }

    private void init() {
        adapter = new CommunityFeedPagerAdapter(getChildFragmentManager(), getContext(), this, "type", this, goHome, new OnGotoChannelListener() {
            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
                if (listener != null)
                    listener.onItemClicked(type, id, name, favorite);
            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite, Article article, int position) {

            }

            @Override
            public void onArticleSelected(Article article) {

            }
        }, this);
        viewpager.setAdapter(adapter);
        tabs.setupWithViewPager(viewpager, false);
    }

    private void bindViews(View view) {
        gradient2 = view.findViewById(R.id.gradient2);
        tabs = view.findViewById(R.id.tabLayout);
        viewpager = view.findViewById(R.id.viewpager);
        errorText = view.findViewById(R.id.errorText);
        noRecordFoundContainer = view.findViewById(R.id.no_record_found_container);
        root = view.findViewById(R.id.root);
    }

    private void tabListener() {
        if (tabs == null) return;
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void invalidateViews() {
        if (getActivity() == null)
            return;
        tabs.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.tabbar_text_unselected),
                ContextCompat.getColor(getActivity(), R.color.static_tabbar_text_selected_new));
        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.transparent));
        view.findViewById(R.id.root).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.discover_bg));
        gradient2.setImageResource(R.drawable.card_new_gradient_image);
        Utils.addNoDataErrorView(getContext(), noRecordFoundContainer, v -> reloadCurrent(), null);
        if (adapter != null) {
            adapter.invalidate();
            adapter.notifyDataSetChanged();
        }
    }

    private void setStatusBarColor() {
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.bottombar_bg));
        }
    }

    public void reloadCurrent() {
        setStatusBarColor();
        resetData();
    }

    private void resetData() {
        if (adapter != null && adapter.getFragment() != null) {
            adapter.getFragment().reload();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void home() {

    }

    @Override
    public void sendAudioToTempHome(AudioCallback audioCallback, String fragTag, String status, AudioObject audio) {

    }

    @Override
    public void scrollUp() {

    }

    @Override
    public void scrollDown() {

    }

    @Override
    public void sendAudioEvent(String event) {

    }

    @Override
    public void swipe(boolean enable) {

    }

    @Override
    public void muteIcon(boolean isShow) {

    }

    @Override
    public void onFavoriteChanged(boolean fav) {

    }

    @Override
    public void selectTab(String id) {

    }

    @Override
    public void selectTabOrDetailsPage(String id, String name, TYPE type, String footerType) {

    }

    @Override
    public void onProgressChanged(boolean flag) {

    }

    public void scrollToTop() {

    }

    public interface OnCommunityFragmentInteractionListener {
        void onItemClicked(TYPE type, String id, String name, boolean favorite);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (HomeSelectedFragment == Constants.BOTTOM_TAB_COMMUNITY_FEED) {
            Constants.canAudioPlay = true;
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("resume");
            }
            if (adapter != null && adapter.getFragment() != null) {
                adapter.getFragment().resetCurrentBullet();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (HomeSelectedFragment == Constants.BOTTOM_TAB_COMMUNITY_FEED || HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
            Constants.canAudioPlay = false;
            if (goHome != null)
                goHome.sendAudioEvent("pause");
            if (adapter != null && adapter.getFragment() != null) {
                adapter.getFragment().Pause();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (HomeSelectedFragment == Constants.BOTTOM_TAB_COMMUNITY_FEED) {
            Constants.canAudioPlay = false;
            Log.d("audiotest", "ondestroy home : stop_destroy");
            if (goHome != null)
                goHome.sendAudioEvent("stop_destroy");
        }
        if (HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
            HomeSelectedFragment = Constants.BOTTOM_TAB_HOME;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (adapter != null)
                adapter.resetFirstCardWithMenu();
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("stop");
            }
            if (adapter != null && adapter.getFragment() != null) {
                adapter.getFragment().Pause();
            }
        } else {
            if (tabs != null && tabs.getChildCount() > 1)
                if (isHeader) {
                    Constants.visiblePageHomeDetails = tabs.getSelectedTabPosition();
                    Constants.visiblePage = Constants.visiblePageHomeDetails;
                } else {
                    Constants.visiblePageHome = tabs.getSelectedTabPosition();
                    Constants.visiblePage = Constants.visiblePageHome;
                }

            if (HomeSelectedFragment == Constants.BOTTOM_TAB_COMMUNITY_FEED || HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
                Constants.canAudioPlay = true;
                if (!Constants.muted) {
                    if (goHome != null)
                        goHome.sendAudioEvent("resume");
                }
                if (adapter != null && adapter.getFragment() != null) {
                    adapter.getFragment().resetCurrentBullet();
                }
            }
        }
    }
}
