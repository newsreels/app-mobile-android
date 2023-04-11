package com.ziro.bullet.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.NewsNotificationsAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.NotificationPresenterCallback;
import com.ziro.bullet.model.notification.NewsNotificationItem;
import com.ziro.bullet.model.notification.NewsNotificationResponse;
import com.ziro.bullet.presenter.NotificationPresenter;
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ArticleNotificationsFragment extends Fragment implements NotificationPresenterCallback {

    private static final String TAG = "ArticleNotificationsFragment";

    private RecyclerView mRecyclerView;
    private TextView mTvEmpty;

    private PrefConfig prefConfig;
    private NotificationPresenter mPresenter;

    private String mPage = "";
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private ArrayList<NewsNotificationItem> notificationItems = new ArrayList<>();
    private NewsNotificationsAdapter mAdapter;

    private ProgressBar progressBar;

    public static ArticleNotificationsFragment createInstance() {
        return new ArticleNotificationsFragment();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        mRecyclerView = view.findViewById(R.id.epoxyRecyclerView);
        mTvEmpty = view.findViewById(R.id.tvEmpty);

        prefConfig = new PrefConfig(getContext());
        mPresenter = new NotificationPresenter(getActivity(), this);
        mAdapter = new NewsNotificationsAdapter(getActivity(), notificationItems);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new PaginationListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                mPresenter.getArticleNotifications(mPage);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public void onScroll(int position) {
//                Utils.hideKeyboard(EditionActivity.this, mRvRecyclerView);
            }

            @Override
            public void onScrolling(@NonNull @NotNull RecyclerView recyclerView, int newState) {

            }
        });

        mPresenter.getArticleNotifications(mPage);

        return view;
    }

    @Override
    public void loaderShow(boolean flag) {
        isLoading = flag;
        progressBar.setVisibility(flag?View.VISIBLE:View.GONE);
    }

    @Override
    public void error(String error) {
        if (getActivity() != null && !getActivity().isFinishing())
            Utils.showSnacky(getActivity().getWindow().getDecorView().getRootView(), "" + error);
        mTvEmpty.setVisibility(notificationItems.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void success(Object response) {
        if (getActivity()==null ||getActivity().isFinishing()){
            return;
        }
        if (response instanceof NewsNotificationResponse) {
            if (((NewsNotificationResponse) response).meta != null) {
                mPage = ((NewsNotificationResponse) response).meta.getNext();
            } else {
                mPage = "";
            }
            if (TextUtils.isEmpty(mPage)) {
                isLastPage = true;
            }

            if (((NewsNotificationResponse) response).getNotificationList() != null && ((NewsNotificationResponse) response).getNotificationList().size() > 0) {
                notificationItems.addAll(((NewsNotificationResponse) response).getNotificationList());
            }

            mTvEmpty.setVisibility(notificationItems.size() > 0 ? View.GONE : View.VISIBLE);

            mAdapter.notifyDataSetChanged();
        }
    }
}
