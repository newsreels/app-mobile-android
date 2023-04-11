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
import com.ziro.bullet.adapters.BlockSourcesAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.utills.GridSpacingItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sdsmdg.harjot.vectormaster.utilities.Utils.dpToPx;

public class BlockSourcesFragment extends Fragment {
    private static final String TAG = BlockSourcesFragment.class.getSimpleName();

    private RecyclerView mRvRecyclerView;
    private PrefConfig mPrefConfig;
    private BlockSourcesAdapter mAdapter;
    private ArrayList<Source> mSources = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView mTvProgress;
    private OnBlockedSourceFragmentInteractionListener listener;

    public static BlockSourcesFragment newInstance() {
        return new BlockSourcesFragment();
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
        mRvRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mAdapter = new BlockSourcesAdapter(getActivity(), mSources);
        mRvRecyclerView.setAdapter(mAdapter);

        mAdapter.setCallback(new BlockSourcesAdapter.FollowingCallback() {
            @Override
            public void onItemChanged(Source source) {

            }

            @Override
            public void refresh() {
                if (mSources != null && mSources.size() == 0) {
                    mTvProgress.setText(getActivity().getString(R.string.no_blocked));
                    mTvProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemClicked(Source source) {
                if (listener != null)
                    listener.onItemClicked(TYPE.SOURCE, source.getId(), source.getName());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getSources();
    }

    private void bindViews(View view) {
        mRvRecyclerView = view.findViewById(R.id.recyclerview);
        mProgressBar = view.findViewById(R.id.progress_circular);
        mTvProgress = view.findViewById(R.id.tvProgress);
    }

    private void getSources() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTvProgress.setVisibility(View.GONE);
        mTvProgress.setText("");
        Call<SourceModel> call = ApiClient
                .getInstance(getActivity())
                .getApi()
                .getBlockedSources("Bearer " + mPrefConfig.getAccessToken());
        call.enqueue(new Callback<SourceModel>() {
            @Override
            public void onResponse(@NotNull Call<SourceModel> call, @NotNull Response<SourceModel> response) {
                Log.d(TAG, "onResponse: ");
                mProgressBar.setVisibility(View.GONE);
                mTvProgress.setVisibility(View.GONE);
                if (response.body() != null && response.body().getSources() != null && response.body().getSources().size() > 0) {
                    mSources.clear();
                    mSources.addAll(response.body().getSources());
                } else {
                    if (getActivity() != null)
                        mTvProgress.setText(getActivity().getString(R.string.no_blocked));
                    mTvProgress.setVisibility(View.VISIBLE);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NotNull Call<SourceModel> call, @NotNull Throwable t) {
                Log.d(TAG, "onResponse: ");
                mProgressBar.setVisibility(View.GONE);
                mTvProgress.setVisibility(View.GONE);
                mTvProgress.setVisibility(View.VISIBLE);
                mTvProgress.setText(getString(R.string.network_error));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("HOMELIFE", "onAttach");
        if (context instanceof OnBlockedSourceFragmentInteractionListener) {
            listener = (OnBlockedSourceFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnHomeFragmentInteractionListener");
        }
    }

    public interface OnBlockedSourceFragmentInteractionListener {
        void dataChanged(TYPE type, String id);

        void back();

        void onItemClicked(TYPE type, String id, String name);
    }

}
