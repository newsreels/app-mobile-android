package com.ziro.bullet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.BlockAuthorsAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.AuthorListResponse;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.utills.GridSpacingItemDecoration;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sdsmdg.harjot.vectormaster.utilities.Utils.dpToPx;

public class BlockAuthorsFragment extends Fragment {
    private static final String TAG = BlockAuthorsFragment.class.getSimpleName();

    private RecyclerView mRvRecyclerView;
    private PrefConfig mPrefConfig;
    private BlockAuthorsAdapter mAdapter;
    private ArrayList<Author> mSources = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView mTvProgress;


    public static BlockAuthorsFragment newInstance() {
        return new BlockAuthorsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blocked_author, container, false);
        bindViews(view);
        init();
        return view;
    }



    private void init() {
        mPrefConfig = new PrefConfig(getContext());
        mRvRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRvRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mAdapter = new BlockAuthorsAdapter(getActivity(), mSources);
        mRvRecyclerView.setAdapter(mAdapter);

        mAdapter.setCallback(new BlockAuthorsAdapter.FollowingCallback() {
            @Override
            public void onItemChanged(Author author) {

            }

            @Override
            public void refresh() {
                if (mSources != null && mSources.size() == 0) {
                    mTvProgress.setText(getActivity().getString(R.string.no_blocked));
                    mTvProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemClicked(Author author) {
                Utils.openAuthor(getContext(), author);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getAuthors();
    }

    private void bindViews(View view) {
        mRvRecyclerView = view.findViewById(R.id.recyclerview);
        mProgressBar = view.findViewById(R.id.progress_circular);
        mTvProgress = view.findViewById(R.id.tvProgress);
    }

    private void getAuthors() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTvProgress.setVisibility(View.VISIBLE);
        mTvProgress.setText(getActivity().getString(R.string.loading));
        Call<AuthorListResponse> call = ApiClient
                .getInstance(getActivity())
                .getApi()
                .getBlockedAuthors("Bearer " + mPrefConfig.getAccessToken());
        call.enqueue(new Callback<AuthorListResponse>() {
            @Override
            public void onResponse(@NotNull Call<AuthorListResponse> call, @NotNull Response<AuthorListResponse> response) {
                Log.d(TAG, "onResponse: ");
                mProgressBar.setVisibility(View.GONE);
                mTvProgress.setVisibility(View.GONE);
                if (response.body() != null && response.body().getAuthors() != null && response.body().getAuthors().size() > 0) {
                    mSources.clear();
                    mSources.addAll(response.body().getAuthors());
                } else {
                    if (getActivity() != null)
                        mTvProgress.setText(getActivity().getString(R.string.no_blocked));
                    mTvProgress.setVisibility(View.VISIBLE);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NotNull Call<AuthorListResponse> call, @NotNull Throwable t) {
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

    }
}
