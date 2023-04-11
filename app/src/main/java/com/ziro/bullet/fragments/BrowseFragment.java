package com.ziro.bullet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.adapters.BrowseTopicsAdapter;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.search.Search;
import com.ziro.bullet.data.models.suggestions.SuggestionsModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseFragment extends Fragment {
    private static final String TAG = BrowseFragment.class.getSimpleName();

    private OnBrowserFragmentInteractionListener listener;

    private RecyclerView mRvTopics, mRvFeatured;
    private TextInputEditText mEtSearch;
    private TextView mTvFeaturedLabel, mTvTopicsLabel;
    private LinearLayout mLlContainer;
    private ProgressBar mProgressBar;
    private TextView mTvProgress;

    private PrefConfig mPrefConfig;
    private ArrayList<Search> mTopicsModels = new ArrayList<>();
    private ArrayList<Search> mFeaturedModels = new ArrayList<>();
    private ArrayList<Search> mSearchModels = new ArrayList<>();
    private BrowseTopicsAdapter mFeaturedAdapter, mTopicsAdapter, mSearchAdapter;

    public static BrowseFragment newInstance() {
        return new BrowseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browser, container, false);
        bindViews(view);
        init();
        return view;
    }

    private void init() {
        mPrefConfig = new PrefConfig(getContext());

        mRvTopics.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvFeatured.setLayoutManager(new LinearLayoutManager(getContext()));
        mFeaturedAdapter = new BrowseTopicsAdapter(getActivity());
        mTopicsAdapter = new BrowseTopicsAdapter(getActivity());

        // TODO: 06/06/20  optimize
        mTopicsAdapter.setCallback(new BrowseTopicsAdapter.OnTopicCallback() {
            @Override
            public void onItemClicked(Search model) {

            }

            @Override
            public void onStarClicked(String id, boolean favorite) {
                for (int i = 0; i < mFeaturedModels.size(); i++) {
                    if (mFeaturedModels.get(i).getId().equals(id)) {
                        mFeaturedModels.get(i).setFavorite(favorite);
                    }
                }
                mFeaturedAdapter.notifyDataSetChanged();
            }
        });

        mFeaturedAdapter.setCallback(new BrowseTopicsAdapter.OnTopicCallback() {
            @Override
            public void onItemClicked(Search model) {

            }

            @Override
            public void onStarClicked(String id, boolean favorite) {
                for (int i = 0; i < mTopicsModels.size(); i++) {
                    if (mTopicsModels.get(i).getId().equals(id)) {
                        mTopicsModels.get(i).setFavorite(favorite);
                    }
                }
                mFeaturedAdapter.notifyDataSetChanged();
            }
        });

        mRvFeatured.setAdapter(mFeaturedAdapter);
        mRvTopics.setAdapter(mTopicsAdapter);

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged() called with: s = [" + s.toString() + "]");

                if (s.length() > 0) {
                    mRvFeatured.setVisibility(View.GONE);
                    mTvFeaturedLabel.setVisibility(View.GONE);
                    mTvTopicsLabel.setVisibility(View.GONE);
//                    searchTopic(s.toString());
                } else {
                    mRvFeatured.setVisibility(View.VISIBLE);
                    mTvFeaturedLabel.setVisibility(View.VISIBLE);
                    mTvTopicsLabel.setVisibility(View.VISIBLE);
                    mRvTopics.setAdapter(mTopicsAdapter);
                    mTvProgress.setVisibility(View.GONE);
                }
            }
        });

        getAllTopics();
    }


    private void bindViews(View view) {
        mRvTopics = view.findViewById(R.id.rvTopics);
        mRvFeatured = view.findViewById(R.id.rvFeatured);
        mTvFeaturedLabel = view.findViewById(R.id.tvFeaturedLabel);
        mTvTopicsLabel = view.findViewById(R.id.tvTopicsLabel);
        mEtSearch = view.findViewById(R.id.etSearchTopics);
        mLlContainer = view.findViewById(R.id.ll_featured);
        mProgressBar = view.findViewById(R.id.progress_circular);
        mTvProgress = view.findViewById(R.id.tvProgress);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBrowserFragmentInteractionListener) {
            listener = (OnBrowserFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBrowserFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface OnBrowserFragmentInteractionListener {
    }

    private void getAllTopics() {
        mFeaturedModels.clear();
        mTopicsModels.clear();
        mLlContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTvProgress.setVisibility(View.VISIBLE);
        mTvProgress.setText(getActivity().getString(R.string.loading));
        Call<SuggestionsModel> call = ApiClient
                .getInstance(getActivity())
                .getApi()
                .searchSuggestion("Bearer " + mPrefConfig.getAccessToken());
        call.enqueue(new Callback<SuggestionsModel>() {
            @Override
            public void onResponse(@NotNull Call<SuggestionsModel> call, @NotNull Response<SuggestionsModel> response) {
                Log.d(TAG, "onResponse: ");
                if (response.body() != null) {
                    SuggestionsModel suggestionsModel = response.body();
                    if (suggestionsModel.getSuggestions().size() > 0) {
                        mLlContainer.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        mTvProgress.setVisibility(View.GONE);
                        mTvFeaturedLabel.setText(suggestionsModel.getSuggestions().get(0).getTitle());
                        mFeaturedModels.addAll(suggestionsModel.getSuggestions().get(0).getTopics());

                        if (suggestionsModel.getSuggestions().get(0).getTopics().size() == 0) {
                            mTvFeaturedLabel.setVisibility(View.GONE);
                            mRvFeatured.setVisibility(View.GONE);
                        }
                        mTvTopicsLabel.setText(suggestionsModel.getSuggestions().get(1).getTitle());
                        mTopicsModels.addAll(suggestionsModel.getSuggestions().get(1).getTopics());

                        mFeaturedAdapter.notifyDataSetChanged();
                        mTopicsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<SuggestionsModel> call, @NotNull Throwable t) {
                Log.d(TAG, "onResponse: ");
                Toast.makeText(getContext(), "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                mTvProgress.setVisibility(View.VISIBLE);
                mTvProgress.setText(getActivity().getString(R.string.no_record));
            }
        });
    }

//    private void searchTopic(String keyword) {
//        mSearchAdapter = new BrowseTopicsAdapter(getContext(), mSearchModels);
//        mRvTopics.setAdapter(mSearchAdapter);
//        mTvProgress.setVisibility(View.VISIBLE);
//        mTvProgress.setText("Loading...");
//        Call<SuggestionsModel> call = ApiClient
//                .getInstance()
//                .getApi()
//                .searchTopic("Bearer " + mPrefConfig.getAccessToken(), keyword);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
//                mSearchModels.clear();
//                if (response.code() == 200) {
//                    try {
//                        if (response.body() != null) {
//                            String s = response.body().string();
//                            JSONObject jsonObject = new JSONObject(s);
//                            JSONArray resultsArray = jsonObject.getJSONArray("result");
//                            Gson gson = new Gson();
//                            for (int i = 0; i < resultsArray.length(); i++) {
//                                JSONObject item = resultsArray.getJSONObject(i);
//                                String type = item.getString("type");
//                                if (type.equals("topic")) {
//                                    JSONObject dataObject = item.getJSONObject("data");
//                                    Search topics = gson.fromJson(dataObject.toString(), Search.class);
//                                    topics.setType("topic");
//                                    mSearchModels.add(topics);
//                                } else if (type.equals("source")) {
//                                    JSONObject dataObject = item.getJSONObject("data");
//                                    Search topics = gson.fromJson(dataObject.toString(), Search.class);
//                                    topics.setType("source");
//                                    mSearchModels.add(topics);
//                                }
//                            }
//                            if (mSearchModels.size() == 0) {
//                                mTvProgress.setText("No results found.");
//                            } else {
//                                mTvProgress.setVisibility(View.GONE);
//                            }
//                            mSearchAdapter.notifyDataSetChanged();
//                        }
//                    } catch (IOException | JSONException e) {
//                        e.printStackTrace();
//                        mTvProgress.setText("No results found.");
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
//                mSearchModels.clear();
//                mTvProgress.setText("No results found.");
//
//            }
//        });
//    }

}