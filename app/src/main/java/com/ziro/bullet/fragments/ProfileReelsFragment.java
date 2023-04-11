package com.ziro.bullet.fragments;

import static com.ziro.bullet.utills.Constants.ACTION_UPDATE_EVENT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.ProfileReelsAdapter;
import com.ziro.bullet.adapters.UploadHolderAdapter;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.StudioCallback;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.gallery.SpacingItemDecoration;
import com.ziro.bullet.mediapicker.utils.ScreenUtils;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.presenter.StudioPresenter;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import im.ene.toro.widget.Container;

public class ProfileReelsFragment extends Fragment implements StudioCallback {
    private static final String TAG = "ProfileReelsFragment";
    private static final String BUNDLE_SOURCE = "source";
    private Container mRecyclerView;
    private RecyclerView holderRecyclerView;
    private ArrayList<ReelsItem> mReelResponseArrayList = new ArrayList<>();
    private String mPage = "";
    private ProfileReelsAdapter mAdapter;
    private StudioPresenter presenter;
    private boolean isReload = false;
    private boolean isLoading = false;
    private OnFragmentInteractionListener listener;
    private String sourceId;

    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private AdapterCallback adapterCallback;
    private DetailsActivityInterface detailsActivityInterface;
    private ShareToMainInterface shareToMainInterface;
    private ShareBottomSheet shareBottomSheet;
    private PictureLoadingDialog mLoadingDialog;
    private BroadcastReceiver updateEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("method");
            if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase("update")) {
                updateArticles();
            }
        }
    };

    public ProfileReelsFragment() {
    }

    public static ProfileReelsFragment newInstance(String sourceId) {
        ProfileReelsFragment profileReelsFragment = new ProfileReelsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_SOURCE, sourceId);
        profileReelsFragment.setArguments(bundle);
        return profileReelsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_SOURCE)) {
            sourceId = getArguments().getString(BUNDLE_SOURCE);
        }
        presenter = new StudioPresenter(getActivity(), this);
        mAdapter = new ProfileReelsAdapter(mReelResponseArrayList, getContext(), "profile", sourceId);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateEvent, new IntentFilter(ACTION_UPDATE_EVENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recyclerview);
        holderRecyclerView = view.findViewById(R.id.holder_recycler_view);
        showOptionsLoaderCallback = new ShowOptionsLoaderCallback() {
            @Override
            public void showLoader(boolean show) {
                if (show) {
                    showProgressDialog();
                } else {
                    dismissProgressDialog();
                }
            }
        };

        adapterCallback = new AdapterCallback() {
            @Override
            public int getArticlePosition() {
                return 0;
            }

            @Override
            public void showShareBottomSheet(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
                showBottomSheetDialog(shareInfo, article, onDismissListener);
            }

            @Override
            public void onItemClick(int position, boolean setCurrentView) {

            }
        };

        detailsActivityInterface = new DetailsActivityInterface() {
            @Override
            public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {

            }

            @Override
            public void pause() {

            }

            @Override
            public void resume() {

            }
        };
        shareToMainInterface = new ShareToMainInterface() {
            @Override
            public void removeItem(String id, int position) {

            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {

            }

            @Override
            public void unarchived() {

            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(2, ScreenUtils.dip2px(getContext(), 2), false));
        mAdapter.addShareListener(requireActivity(), showOptionsLoaderCallback, adapterCallback, detailsActivityInterface);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Utils.hideKeyboard(getActivity(), mRecyclerView);
                if (dy > 0) { // only when scrolling up

                    final int visibleThreshold = 2;

                    GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = layoutManager.getItemCount();
                    if (!isLoading && !TextUtils.isEmpty(mPage) && currentTotalCount <= lastItem + visibleThreshold) {
                        //show your loading view
                        // load content in background
                        presenter.loadMyReels(sourceId, mPage);
                    }
                }
            }
        });
        isReload = false;


        UploadHolderAdapter uploadHolderAdapter = new UploadHolderAdapter(getActivity(), v -> {
            if (listener != null) listener.onUploadClicked();
        });
        holderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        holderRecyclerView.setAdapter(uploadHolderAdapter);

        if (!isLoading) {
            isReload = true;
            mPage = "";
            presenter.loadMyReels(sourceId, mPage);
        }
//        presenter.loadMyReels(sourceId, mPage);
    }

    @Override
    public void onResume() {
        //dont put full refresh on onResume, it'll clear list and if paginated it's issue!!
        super.onResume();
    }

    public void refresh() {
        if (!isLoading) {
            mPage = "";
            mReelResponseArrayList.clear();
            if (presenter != null)
                presenter.loadMyReels(sourceId, mPage);
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        isLoading = flag;
    }

    @Override
    public void error(String error) {
        if (getActivity() != null && getActivity().getWindow() != null && getActivity().getWindow().getDecorView() != null) {
            Utils.showSnacky(getActivity().getWindow().getDecorView().getRootView(), error);
        }
    }

    @Override
    public void success(ReelResponse body) {
        if (isReload) {
            mReelResponseArrayList.clear();
            isReload = false;
        }
        if (body.getMeta() != null) {
            mPage = body.getMeta().getNext();
        }
        if (body.getReels() != null && body.getReels().size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mReelResponseArrayList.addAll(body.getReels());
        }
        if (mAdapter != null) {
            mAdapter.setNextPageParam(mPage);
            mAdapter.notifyDataSetChanged();
        }
        if (mReelResponseArrayList.size() > 0) {
            holderRecyclerView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holderRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateEvent);
    }

    public void updateArticles() {
        if (presenter == null) return;
        if (!isLoading) {
            isReload = true;
            mPage = "";
            presenter.loadMyReels(sourceId, mPage);
        }
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
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }


    private void showBottomSheetDialog(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet(requireActivity(), shareToMainInterface, true, "ARTICLES");
        }
        shareBottomSheet.show(article, onDismissListener, shareInfo);
    }

    public void dismissBottomSheet() {
        if (shareBottomSheet != null) {
            shareBottomSheet.hide();
        }
    }

    protected void showProgressDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new PictureLoadingDialog(getContext());
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissProgressDialog() {
        try {
            if (mLoadingDialog != null
                    && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            mLoadingDialog = null;
            e.printStackTrace();
        }
    }


    public interface OnFragmentInteractionListener {
        void onUploadClicked();
    }
}
