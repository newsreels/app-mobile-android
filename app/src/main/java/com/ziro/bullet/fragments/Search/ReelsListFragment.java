package com.ziro.bullet.fragments.Search;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.ProfileReelsAdapter;
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
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import im.ene.toro.widget.Container;

public class ReelsListFragment extends Fragment implements StudioCallback {
    private static final String TAG = "SaveReelsFragment";
    private Container mRecyclerView;
    private LinearLayout llCard;
    private ArrayList<ReelsItem> mReelResponseArrayList = new ArrayList<>();
    private String mPage = "";
    private ProfileReelsAdapter mAdapter;
    private StudioPresenter presenter;
    private boolean isReload = false;
    private boolean isLoading = false;
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
//                updateArticles();
            }
        }
    };

    public ReelsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new StudioPresenter(getActivity(), this);
        mAdapter = new ProfileReelsAdapter(mReelResponseArrayList, getContext(), "public", "");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateEvent, new IntentFilter(ACTION_UPDATE_EVENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_reels_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recyclerview);
        llCard = view.findViewById(R.id.ll_no_results);
        TextView from_label = view.findViewById(R.id.from_label);
//        from_label.setText(getString(R.string.no_saved_reels));

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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(3, ScreenUtils.dip2px(getContext(), 10), false));
        mAdapter.addShareListener(requireActivity(), showOptionsLoaderCallback, adapterCallback, detailsActivityInterface);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 3;
                    GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                        int currentTotalCount = layoutManager.getItemCount();
                        if (!isLoading && !TextUtils.isEmpty(mPage) && currentTotalCount <= lastItem + visibleThreshold) {
                            //show your loading view
                            // load content in background
                            presenter.getReelsHome(Constants.REELS_FOR_YOU, "", "", mPage, false, false, true, "");
//                            presenter.loadSavedReels(mPage);
                        }
                    }
                }
            }
        });
        isReload = false;
        presenter.getReelsHome(Constants.REELS_FOR_YOU, "", "", mPage, false, false, true, "");

//        presenter.loadSavedReels(mPage);
    }

    @Override
    public void onResume() {
        super.onResume();
        isReload = true;
//        mPage = "";
//        presenter.getReelsHome(Constants.REELS_FOR_YOU, "", "", mPage, false, false, true, "");

//        presenter.loadSavedReels(mPage);
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
            mRecyclerView.setVisibility(View.VISIBLE);
            llCard.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            llCard.setVisibility(View.VISIBLE);
        }

        if (body.getReels().size() <= 15) {
            presenter.getReelsHome(Constants.REELS_FOR_YOU, "", "", mPage, false, false, true, "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateEvent);
    }

    public void updateArticles() {
        if (isLoading)
            return;
        if (presenter == null) return;
        mPage = "";
        isReload = true;
//        presenter.loadSavedReels(mPage);
        presenter.getReelsHome(Constants.REELS_FOR_YOU, "", "", mPage, false, false, true, "");

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


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }
}
