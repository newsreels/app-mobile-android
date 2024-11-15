package com.newsreels.app.fragments;

import static com.newsreels.app.utills.Constants.ACTION_UPDATE_EVENT;

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
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsreels.app.R;
import com.newsreels.app.adapters.ProfileReelsAdapter;
import com.newsreels.app.bottomSheet.ShareBottomSheet;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.data.TYPE;
import com.newsreels.app.data.models.ShareInfo;
import com.newsreels.app.interfaces.AdapterCallback;
import com.newsreels.app.interfaces.AudioCallback;
import com.newsreels.app.interfaces.DetailsActivityInterface;
import com.newsreels.app.interfaces.ShareToMainInterface;
import com.newsreels.app.interfaces.ShowOptionsLoaderCallback;
import com.newsreels.app.interfaces.StudioCallback;
import com.newsreels.app.interfaces.VideoInterface;
import com.newsreels.app.mediapicker.dialog.PictureLoadingDialog;
import com.newsreels.app.mediapicker.gallery.SpacingItemDecoration;
import com.newsreels.app.mediapicker.utils.ScreenUtils;
import com.newsreels.app.model.AudioObject;
import com.newsreels.app.model.Reel.ReelResponse;
import com.newsreels.app.model.Reel.ReelsItem;
import com.newsreels.app.model.articles.Article;
import com.newsreels.app.presenter.ReelPresenter;
import com.newsreels.app.presenter.StudioPresenter;
import com.newsreels.app.utills.Utils;

import java.util.ArrayList;

public class AuthorReelsFragment extends Fragment implements StudioCallback, VideoInterface {
    private RecyclerView mRecyclerView;
    private ArrayList<ReelsItem> mReelResponseArrayList = new ArrayList<>();
    private String mPage = "";
    private ProfileReelsAdapter mAdapter;
    private LinearLayout llCard;

    private PrefConfig prefConfig;
    private ProgressBar progress;
    private StudioPresenter presenter;
    private ReelPresenter reelPresenter;
    private boolean isReload = false;
    private boolean isLoading = false;
    private String contextOrAuthorId;

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

    private static final String BUNDLE_CONTEXT_ID = "bundle_context_id";

    public static AuthorReelsFragment newInstance(String sourceId) {
        AuthorReelsFragment authorReelsFragment = new AuthorReelsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CONTEXT_ID, sourceId);
        authorReelsFragment.setArguments(bundle);
        return authorReelsFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        //dont put full refresh on onResume, it'll clear list and if paginated it's issue!!
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new StudioPresenter(getActivity(), this);
        reelPresenter = new ReelPresenter(getActivity(), this);
        prefConfig = new PrefConfig(getContext());
        if (getArguments() != null && getArguments().containsKey(BUNDLE_CONTEXT_ID)) {
            contextOrAuthorId = getArguments().getString(BUNDLE_CONTEXT_ID);
        }
        mAdapter = new ProfileReelsAdapter(mReelResponseArrayList, getContext(), "public", contextOrAuthorId);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateEvent, new IntentFilter(ACTION_UPDATE_EVENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_author_reels, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recyclerview);
        llCard = view.findViewById(R.id.llCard);
        progress = view.findViewById(R.id.progress);


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
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(2,
                ScreenUtils.dip2px(getContext(), 5), true));
//        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter.addShareListener(requireActivity(), showOptionsLoaderCallback, adapterCallback, detailsActivityInterface);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Utils.hideKeyboard(getActivity(), mRecyclerView);
                if (dy > 0) { // only when scrolling up
                    final int visibleThreshold = 3;
                    GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = layoutManager.getItemCount();
                    if (!isLoading && !TextUtils.isEmpty(mPage) && currentTotalCount <= lastItem + visibleThreshold) {
                        //show your loading view
                        // load content in background
                        Log.d("SHAHZAIB", "TAG 1: "+contextOrAuthorId);
                        if (!TextUtils.isEmpty(contextOrAuthorId))//;;;;/////////////////////////////////
                            reelPresenter.getVideos("", contextOrAuthorId, mPage, true, false, "");
                    }
                }
            }
        });

        progress.setVisibility(View.VISIBLE);
        updateArticles();
        isReload = false;
    }

    @Override
    public void loaderShow(boolean flag) {
        isLoading = flag;
        progress.setVisibility(View.GONE);
    }

    @Override
    public void error(String error) {
//        if (getActivity() != null && getActivity().getWindow() != null && getActivity().getWindow().getDecorView() != null) {
////            Utils.showSnacky(getActivity().getWindow().getDecorView().getRootView(), error);
//            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void error404(String error) {

    }

    @Override
    public void success(ReelResponse reelResponse, boolean reload) {
        if (isReload) {
            isReload = false;
            mReelResponseArrayList.clear();
        }
        if (reelResponse.getMeta() != null)
            mPage = reelResponse.getMeta().getNext();

        if (reelResponse.getReels() != null && reelResponse.getReels().size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mReelResponseArrayList.addAll(reelResponse.getReels());
        }
        if (mAdapter != null) {
            mAdapter.setNextPageParam(mPage);
            mAdapter.notifyDataSetChanged();
        }

        if (mReelResponseArrayList.size() > 0) {
            llCard.setVisibility(View.GONE);
        } else {
            llCard.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void nextVideo(int postion) {

    }

    @Override
    public void success(ReelResponse body) {
        if (isReload) mReelResponseArrayList.clear();
        if (body.getMeta() != null)
            mPage = body.getMeta().getNext();
        if (body.getReels() != null && body.getReels().size() > 0) {
            mReelResponseArrayList.addAll(body.getReels());
        }
        if (mAdapter != null) {
            mAdapter.setNextPageParam(mPage);
            mAdapter.notifyDataSetChanged();
        }

        if (mReelResponseArrayList.size() > 0) {
            llCard.setVisibility(View.GONE);
        } else {
            llCard.setVisibility(View.VISIBLE);
//            mRecyclerView.setVisibility(View.GONE);
        }
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
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateEvent);
    }

    public void updateArticles() {
        if (!isLoading) {
            isReload = true;
            if (presenter == null) return;
            mPage = "";
            Log.d("SHAHZAIB", "TAG 2: "+contextOrAuthorId);
            if (!TextUtils.isEmpty(contextOrAuthorId))
                reelPresenter.getVideos("", contextOrAuthorId, mPage, true, false, "");
        }
    }
}
