package com.ziro.bullet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziro.bullet.adapters.ManageLocationAdapter;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.ManageLocationCallback;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.presenter.ManageLocationPresenter;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.location.LocationModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

public class ManageLocationFragment extends Fragment implements ManageLocationCallback, GoHome {

    private RelativeLayout back;
    private ProgressBar mProgressBar;
    private TextView mTvProgress;
    private RelativeLayout gradient;

    private ManageLocationPresenter presenter;
    private RecyclerView list;
    private ArrayList<Location> locationArrayList = new ArrayList<>();
    private ManageLocationAdapter listAdapter;
    private String nextInternationalPage = PAGE_START;
    private boolean isLastInternationalPage = false;
    private boolean isLoadingInternational = false;

    private OnFragmentInteractionListener listener;

    public static ManageLocationFragment newInstance() {
        return new ManageLocationFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationArrayList.clear();
        listAdapter.notifyDataSetChanged();
        presenter.getLocations(PAGE_START); // for followed location
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        Log.e("LKASD", "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_manage_location, container, false);
        bindView(view);
        init();
        setlistener();
        return view;
    }

    private void setlistener() {
//        back.setOnClickListener(v -> onBackPressed());
    }

    private void init() {
        presenter = new ManageLocationPresenter(getActivity(), this);
        LinearLayoutManager internationalManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        list.setLayoutManager(internationalManager);
        list.setItemAnimator(new DefaultItemAnimator());
        listAdapter = new ManageLocationAdapter(getActivity(), locationArrayList);
        list.setAdapter(listAdapter);
        listAdapter.setClickListener(new ManageLocationAdapter.LocationCallback() {
            @Override
            public void onItemFollowed(Location location) {

            }

            @Override
            public void onItemUnfollowed(Location location) {

            }

            @Override
            public void onItemClicked(Location location) {
                if (listener != null)
                    listener.onItemClicked(TYPE.LOCATION, location.getId(), location.getNameToShow(), location.isFavorite());
            }

            @Override
            public void onAddTopicClicked() {
                startActivityForResult(new Intent(getActivity(), LocationActivity.class), MainActivityNew.RESULT_INTENT_ADD_LOCATION);
            }
        });
        setPagination(list, internationalManager);
    }

    private void setPagination(RecyclerView mRvRecyclerView, LinearLayoutManager manager) {
        mRvRecyclerView.addOnScrollListener(new PaginationListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoadingInternational = true;
                mTvProgress.setText(getString(R.string.loading));
                presenter.getLocations(nextInternationalPage);
            }

            @Override
            public boolean isLastPage() {
                return isLastInternationalPage;
            }

            @Override
            public boolean isLoading() {
                return isLoadingInternational;
            }

            @Override
            public void onScroll(int position) {

            }

            @Override
            public void onScrolling(@NonNull @NotNull RecyclerView recyclerView, int newState) {

            }
        });
        mRvRecyclerView.setLayoutManager(manager);
    }

    private void bindView(View view) {
        gradient = view.findViewById(R.id.gradient);
        list = view.findViewById(R.id.list);
        back = view.findViewById(R.id.back);
        mProgressBar = view.findViewById(R.id.progress_circular);
        mTvProgress = view.findViewById(R.id.tvProgress);
        back = view.findViewById(R.id.back);
    }

//    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//        Intent intent = new Intent();
//        intent.putExtra("change", true);
//        setResult(RESULT_OK, intent);
//        finish();
//    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            mTvProgress.setText(getString(R.string.loading));
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTvProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        mProgressBar.setVisibility(View.GONE);
        mTvProgress.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(LocationModel response) {
        Log.d("TAG", "onResponse: " + new Gson().toJson(response));
        mProgressBar.setVisibility(View.GONE);

        if (response != null) {
            if (response.getMeta() != null) {
                nextInternationalPage = response.getMeta().getNext();
            }
            if (TextUtils.isEmpty(nextInternationalPage)) {
                isLastInternationalPage = true;
            }
            if (response.getLocations() != null && response.getLocations().size() > 0) {
                locationArrayList.addAll(response.getLocations());
                locationArrayList.add(new Location("add"));
                mProgressBar.setVisibility(View.GONE);
                mTvProgress.setVisibility(View.GONE);
            } else {
                mTvProgress.setText("");
                mTvProgress.setVisibility(View.INVISIBLE);
                locationArrayList.add(new Location("add"));
            }
        } else {
            mTvProgress.setText("");
            mTvProgress.setVisibility(View.INVISIBLE);
            locationArrayList.add(new Location("add"));
        }
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
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

    public interface OnFragmentInteractionListener {
        void dataChanged(TYPE type, String id);

        void onItemClicked(TYPE type, String id, String name, boolean favorite);
    }
}