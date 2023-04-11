package com.ziro.bullet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.BlockTopicsAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.data.models.topics.TopicsModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockedTopicsFragment extends Fragment {
    private static final String TAG = BlockedTopicsFragment.class.getSimpleName();
    private RecyclerView mRvRecyclerView;
    private PrefConfig mPrefConfig;
    private BlockTopicsAdapter mAdapter;
    private ArrayList<Topics> mTopics = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView mTvProgress;
    private OnBlockedFragmentInteractionListener listener;


    public static BlockedTopicsFragment newInstance() {
        return new BlockedTopicsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following_topics, container, false);
        bindViews(view);
        init();
        setListener();
        return view;
    }

    private void setListener() {

    }

    private void init() {
        mPrefConfig = new PrefConfig(getContext());

        mRvRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mAdapter = new BlockTopicsAdapter(getActivity(), mTopics);
        mRvRecyclerView.setAdapter(mAdapter);

        mAdapter.setCallback(new BlockTopicsAdapter.FollowingCallback() {
            @Override
            public void onItemChanged(Topics topic) {

            }

            @Override
            public void refresh() {
                if (mTopics != null && mTopics.size() == 0) {
                    mTvProgress.setText(getActivity().getString(R.string.no_blocked));
                    mTvProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemClicked(Topics topic) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getTopics();
    }

    private void bindViews(View view) {
        mRvRecyclerView = view.findViewById(R.id.recyclerview);
        mProgressBar = view.findViewById(R.id.progress_circular);
        mTvProgress = view.findViewById(R.id.tvProgress);
    }

    private void getTopics() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTvProgress.setVisibility(View.VISIBLE);
        mTvProgress.setText(getActivity().getString(R.string.loading));
        Call<TopicsModel> call = ApiClient
                .getInstance(getActivity())
                .getApi()
                .getBlockedTopics("Bearer " + mPrefConfig.getAccessToken());
        call.enqueue(new Callback<TopicsModel>() {
            @Override
            public void onResponse(@NotNull Call<TopicsModel> call, @NotNull Response<TopicsModel> response) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    Log.d(TAG, "onResponse: ");
                    mProgressBar.setVisibility(View.GONE);
                    mTvProgress.setVisibility(View.GONE);
                    if (response.body() != null && response.body().getTopics() != null && response.body().getTopics().size() > 0) {
                        mTopics.clear();
                        mTopics.addAll(response.body().getTopics());
                    } else {
                        mTvProgress.setText(getActivity().getString(R.string.no_blocked));
                        mTvProgress.setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NotNull Call<TopicsModel> call, @NotNull Throwable t) {
                Log.d(TAG, "onResponse: ");
                mProgressBar.setVisibility(View.GONE);
                mTvProgress.setVisibility(View.GONE);
                mTvProgress.setText(getString(R.string.network_error));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("HOMELIFE", "onAttach");
        if (context instanceof OnBlockedFragmentInteractionListener) {
            listener = (OnBlockedFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnHomeFragmentInteractionListener");
        }
    }

    public interface OnBlockedFragmentInteractionListener {
        void dataChanged(TYPE type, String id);

        void back();

        void onItemClicked(TYPE type, String id, String name);
    }

}
