package com.ziro.bullet.fragments;

import static com.ziro.bullet.utills.Constants.HomeSelectedFragment;
import static com.ziro.bullet.utills.Constants.firstTime;
import static com.ziro.bullet.utills.Constants.isHeader;
import static com.ziro.bullet.utills.Constants.isHomeClicked;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelPostActivity;
import com.ziro.bullet.adapters.TempCategoryAdapter;
import com.ziro.bullet.background.UploadInfo;
import com.ziro.bullet.background.VideoProcessorService;
import com.ziro.bullet.bottomSheet.ForYouReelSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.home.HomeModel;
import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.BottomSheetInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.HomeCallback;
import com.ziro.bullet.interfaces.HomeLoaderCallback;
import com.ziro.bullet.interfaces.MainInterface;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.CategoryFragmentModel;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.Tabs.DataItem;
import com.ziro.bullet.model.Tabs.SubItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.HomePresenter;
import com.ziro.bullet.presenter.MainPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.CustomViewPager;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TempHomeFragment extends Fragment implements MainInterface, GoHome, TempCategorySwipeListener, HomeLoaderCallback {

    public static final int INTENT_EXPLORE_REQUEST = 34234;
    private static final String TAG = "TempHomeFragment_TAG";
    boolean tag = false;
    private ImageView ivMore;
    private ViewGroup llHomeFeatures;
    //    private LinearLayout bgMoreBtn;
    private TextView error;
    //    private RelativeLayout progress;
//    private ImageView loader_gif;
//    private RelativeLayout tabEndGradient;
    private LinearLayout noRecordFoundContainer;
    private LinearLayout ll_no_results;
    private CustomViewPager viewpager;
    private TabLayout categoryTabs;
    private RelativeLayout no_article_found;
    private CardView lets_go;
    private ImageView gradient2;
    //    private TextView tvGreeting;
    private AppBarLayout appbar;
    private View view;
    private GoHome goHome;
    private OnHomeFragmentInteractionListener listener;
    private MainPresenter presenter;
    private TempCategoryAdapter categoriesAdapter;
    private ArrayList<CategoryFragmentModel> categoryFragmentModels = new ArrayList<>();
    private PrefConfig prefConfig;
    private HomeModel mHomeModel;
    private HomePresenter mHomePresenter;
    private MenuPopup menuPopup;
    private String state = "";
    private ViewSwitcher toolbarViewSwitcher;
    private ViewSwitcher homeViewSwitcher;
    private DbHandler cacheManager;
    private HomeCallback homeCallback;
    private int currentPosition = 0;
    private ForYouReelSheet forYouReelSheet;
    private boolean isSequenceUpdated = false;
    private boolean isTabSelected = false;
    private DataItem selectedTab = null;

    public static TempHomeFragment newInstance() {
        return new TempHomeFragment();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        Log.e("LKASD", "onAttach");
        if (context instanceof OnHomeFragmentInteractionListener) {
            listener = (OnHomeFragmentInteractionListener) context;
        }

        if (context instanceof GoHome) {
            goHome = (GoHome) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setRetainInstance(true);
        view = inflater.inflate(R.layout.main_content, container, false);
        presenter = new MainPresenter(getActivity(), this);
        prefConfig = new PrefConfig(getContext());
        cacheManager = new DbHandler(getContext());
        bindViews(view);
        Utils.addNoDataErrorView(getContext(), noRecordFoundContainer, v -> reloadCurrent(), null);
        setStatusBarColor();
        selectLastTab();
        try {
            if (InternetCheckHelper.isConnected()) {
                DbHandler dbHandler = new DbHandler(getContext());
                ArrayList<UploadInfo> allTasks = dbHandler.getAllTasks();
                if (allTasks != null && allTasks.size() > 0) {
                    if (!Utils.isMyServiceRunning(getContext(), VideoProcessorService.class)) {
                        Intent intent = new Intent(getContext(), VideoProcessorService.class);
                        getContext().startService(intent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void loadCacheData() {
        ArrayList<DataItem> dataItemArrayList = cacheManager.GetHomeTabs();
        if (dataItemArrayList != null && dataItemArrayList.size() > 0) {
            HomeModel model = new HomeModel();
            model.setData(dataItemArrayList);
            setHomeDataFromDB(model);
            Utils.loadSkeletonLoader(toolbarViewSwitcher, false);
            Utils.loadSkeletonLoader(homeViewSwitcher, false);
        } else {
            selectLastTab();
        }
    }

    private void bindViews(View view) {
//        greeting = view.findViewById(R.id.headline_greetings);
//        currentDay = view.findViewById(R.id.headline_date);
        no_article_found = view.findViewById(R.id.no_article_found);
//        lets_go = view.findViewById(R.id.lets_go);
        categoryTabs = view.findViewById(R.id.cat_tabs);
        gradient2 = view.findViewById(R.id.gradient2);
        error = view.findViewById(R.id.errorText);
        noRecordFoundContainer = view.findViewById(R.id.no_record_found_container);
        ll_no_results = view.findViewById(R.id.ll_no_results);
        viewpager = view.findViewById(R.id.viewpager);
        ivMore = view.findViewById(R.id.ivMore);
        llHomeFeatures = view.findViewById(R.id.rlHomeFeatures);
        appbar = view.findViewById(R.id.appbar);
        toolbarViewSwitcher = view.findViewById(R.id.toolbar);
        homeViewSwitcher = view.findViewById(R.id.home_view_switcher);
        //on open source setting false by default then on api response it will set accordingly
        prefConfig.setSrcFav(false);
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null && getChildFragmentManager().getFragments().size() > 0) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof CategoryFragment) {
                    getChildFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
            }
        }

        //TODO CHANGE HERE
        categoriesAdapter = new TempCategoryAdapter(Constants.FROM_HOME, false, this, "",
                false, this, goHome, true,
                getChildFragmentManager(), FragmentPagerAdapter.POSITION_NONE, categoryFragmentModels, new OnGotoChannelListener() {
            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
                if (listener != null) {
                    listener.onItemClicked(type, id, name, favorite);
                }
            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite, Article article, int position) {

            }

            @Override
            public void onArticleSelected(Article article) {
                Constants.mute_disable = article.isMute();
                if (Constants.mute_disable) {
                    if (goHome != null)
                        goHome.sendAudioEvent("mute");
                } else {
                    if (!Constants.muted) {
                        if (goHome != null)
                            goHome.sendAudioEvent("unmute");
                    } else {
                        if (goHome != null)
                            goHome.sendAudioEvent("mute");
                    }
                }
            }
        }, this);

        viewpager.setAdapter(categoriesAdapter);
        viewpager.setOffscreenPageLimit(3);
        categoryTabs.setupWithViewPager(viewpager, false);

        ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                if (categoriesAdapter != null && categoryFragmentModels.get(position).isFontSizeUpdated()) {
                    categoryFragmentModels.get(position).setFontSizeUpdated(false);
                    categoriesAdapter.getFragment().resetCurrentBullet();
                }

                if (!isTabSelected) {
                    currentPosition = position;
                    isTabSelected = false;
                }
                Log.d(TAG, "onPageSelected: " + currentPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewpager.addOnPageChangeListener(pageListener);

        ivMore.setOnClickListener(v -> {
            showPopUp();
        });

        homeCallback = new HomeCallback() {
            @Override
            public void loaderShow(boolean flag) {
                if (flag) {
                    //llHomeFeatures.setVisibility(View.GONE);
                    firstTime = false;
                    showSkeletonLoader(true);
                    //showLoader();
                } else {
                    showSkeletonLoader(false);
                    //hideLoader();
                }
            }

            @Override
            public void error(String error) {
                showNoDataErrorView(true);
            }

            @Override
            public void success(HomeModel response) {
                setHomeDataFromApi(response);
            }

            @Override
            public void searchSuccess(CategoryResponse body, boolean isPagination) {

            }
        };
        mHomePresenter = new HomePresenter(getActivity(), homeCallback);
    }

    private void setHomeDataFromApi(HomeModel response) {
        showNoDataErrorView(false);
        this.mHomeModel = response;
//        if (currentPosition == 0) {
        setHomeData(response);
//        }
//        setGreetings();
        if (mHomeModel != null) {
            updateHomeData(mHomeModel.getData());
        }
    }

    private void setGreetings() {
//        greeting.setText(mHomePresenter.getGreetings());
//        currentDay.setText(mHomePresenter.getDay());
    }

    private void setHomeDataFromDB(HomeModel response) {
        showNoDataErrorView(false);
        setHomeData(response);
        if (mHomeModel != null) {
            resetData();
            loadHomeData(mHomeModel.getData());
            if (!tag) {
                tabListener();
                tag = true;
            }
        }
    }

    public void saveState(boolean isStateNeeded) {
        if (prefConfig != null) {
            if (isStateNeeded) {
                prefConfig.setAppStateHomeTabs("" + state);
            } else {
                prefConfig.setAppStateHomeTabs("");
            }
        }
    }

    private void showSkeletonLoader(boolean show) {
        if (categoryFragmentModels != null && categoryFragmentModels.size() <= 0) {
            Utils.loadSkeletonLoader(toolbarViewSwitcher, show);
            showHomeSkeletonLoader(show);
        }
    }

    private void showHomeSkeletonLoader(boolean show) {
//        if (categoryFragmentModels != null && categoryFragmentModels.size() <= 0) {
        Utils.loadSkeletonLoader(homeViewSwitcher, show);
//        }
    }

    private void tabListener() {
        if (categoryTabs == null) return;
        categoryTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Resetting the audio data to avoid old article speech while loading new data
                Constants.articleId = "";
                Constants.speech = "";
                Constants.url = "";

                if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
                    categoriesAdapter.getFragment().unSelectAllItems();
                }

                if (isVisible()) {
                    if (goHome != null)
                        goHome.sendAudioEvent("stop_destroy");
                }

                if (isHeader) {
                    Constants.visiblePageHomeDetails = tab.getPosition();
                    Constants.visiblePage = Constants.visiblePageHomeDetails;
                } else {
                    Constants.visiblePageHome = tab.getPosition();
                    Constants.visiblePage = Constants.visiblePageHome;
                }

                if (categoryFragmentModels.size() > 0) {
                    state = categoryFragmentModels.get(tab.getPosition()).getContextId();
                    if (isHomeClicked) {
                        isHomeClicked = false;
                    } else {
                        saveState(true);
                    }
                }

                viewpager.setCurrentItem(tab.getPosition());

                if (goHome != null)
                    goHome.scrollDown();
                if (appbar != null)
                    appbar.setExpanded(true, true);
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
        view.findViewById(R.id.root).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        gradient2.setImageResource(R.drawable.card_new_gradient_image);
        // to change popup theme
        menuPopup = null;
        Utils.addNoDataErrorView(getContext(), noRecordFoundContainer, v -> reloadCurrent(), null);
        Utils.invalidateShimmerView(homeViewSwitcher, getActivity(), R.layout.skeleton_home_list);
        Utils.invalidateShimmerView(toolbarViewSwitcher, getActivity(), R.layout.skeleton_home_categories);
        if (categoriesAdapter != null) {
            categoriesAdapter.notifyDataSetChanged();
        }
    }


    public void refreshData() {
        if (presenter != null) {
            showLoader();
            resetData();
            if (categoriesAdapter != null) {
                categoriesAdapter.notifyDataSetChanged();
            }
        }
    }

    private void resetData() {
        categoryFragmentModels.clear();
        if (categoriesAdapter != null) {
            categoriesAdapter.notifyDataSetChanged();
        }
        isSequenceUpdated = false;
        isTabSelected = false;
        selectedTab = null;
        no_article_found.setVisibility(View.GONE);
        //llHomeFeatures.setVisibility(View.GONE);
        if (categoryTabs != null) {
            categoryTabs.removeAllTabs();
        }
    }

    @Override
    public void onProgressChanged(boolean flag) {
        Log.e("@@@@@@", "onProgressChanged");
        if (flag) {
            showHomeSkeletonLoader(true);
            firstTime = false;
            //showLoader();
        } else {
            showHomeSkeletonLoader(false);
            //hideLoader();
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        Log.e("@@@@@@", "loaderShow");
        if (flag) {
            showLoader();
        } else {
            hideLoader();
        }
    }

    @Override
    public void error(String error, int load) {
        Log.e("###", "API error");


        ll_no_results.setVisibility(View.VISIBLE);
    }

    @Override
    public void error404(String msg) {
        Log.e("###", "API error 404");
        if (msg == mHomePresenter.errorInternet) {
            ll_no_results.setVisibility(View.VISIBLE);
            homeViewSwitcher.setVisibility(View.GONE);
        }

        error.setText(R.string.no_record);
        error.setVisibility(View.VISIBLE);
    }

    @Override
    public void success(ArticleResponse response, int load, int category, String topic) {

    }

    @Override
    public void success(CategoryResponse response) {

    }

    @Override
    public void UserTopicSuccess(ArrayList<Topics> response) {

    }

    @Override
    public void UserInfoSuccess(Info info) {

    }

    @Override
    public void UserSourceSuccess(SourceModel response) {

    }


    private void showLoader() {
        Log.e("LKASD", "showLoader");
        firstTime = false;
    }

    private void hideLoader() {
        Log.e("LKASD", "hideLoader");
    }

    public void dataChanged() {
        refreshData();
    }

    @Override
    public void home() {
        if (viewpager != null) {
            viewpager.setCurrentItem(0, true);
        }
    }

    @Override
    public void sendAudioToTempHome(AudioCallback audioCallback, String fragTag, String
            status, AudioObject audio) {
        if (categoriesAdapter.getFragment() != null) {
            Log.e("sendAudioToTemp", "====================================");
            Constants.fragTag = fragTag;
            Constants.articleId = audio.getId();
            Constants.bullet_position = audio.getIndex();
            Constants.speech = audio.getText();
            Constants.url = audio.getUrl();
            Constants.bullet_duration = audio.getDuration();
            //TODO: CHECK THIS
//            if (!TextUtils.isEmpty(fragTag) && fragTag.equalsIgnoreCase(categoriesAdapter.getFragment().getMyTag())) {
            playTextAudio(audioCallback, audio);
//            } else {
//                Log.e("sendAudioToTemp", "ignore this");
//            }
        }
    }

    @Override
    public void scrollUp() {

    }

    @Override
    public void scrollDown() {

    }

    @Override
    public void sendAudioEvent(String event) {
        if (!TextUtils.isEmpty(event)) {
            if (event.equalsIgnoreCase("select_first_article")) {
                if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
                    categoriesAdapter.getFragment().selectFirstItemOnCardViewMode();
                }
            }
        }
    }

    private void playTextAudio(AudioCallback audioCallback, AudioObject audio) {
        if (goHome != null) {
            goHome.sendAudioToTempHome(audioCallback, "TempHome", "", audio);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (HomeSelectedFragment == Constants.BOTTOM_TAB_HOME) {
            Constants.canAudioPlay = true;
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("resume");
            }
            if (Constants.isFontSizeUpdated) {
                if (categoriesAdapter != null && categoryFragmentModels != null && !categoryFragmentModels.isEmpty()) {
                    for (int i = 0; i < categoryFragmentModels.size(); i++) {
                        categoryFragmentModels.get(i).setFontSizeUpdated(true);
                    }
                    try {
                        categoriesAdapter.getFragment().resetCurrentBullet();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Constants.isFontSizeUpdated = false;
                }
            }
        }
        if (appbar != null)
            appbar.setExpanded(true, true);

        if (Constants.articleTabItem != null && mHomeModel != null && mHomeModel.getData() != null && forYouReelSheet != null) {
            forYouReelSheet.updateSearchResults();
        }

//        if (forYouReelSheet != null && forYouReelSheet.isVisible()) {
//            forYouReelSheet.updateSearchResults();
//        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_EXPLORE_REQUEST) {
                if (Constants.homeDataUpdate) {
                    reloadCurrent();
                    Constants.homeDataUpdate = false;
                } else {
//                    if (data != null) {
//                        String id = data.getStringExtra(ExploreFeedActivity.SELECTED);
//                        selectTabMain(id, "", TYPE.HOME_TAB);
//                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (HomeSelectedFragment == Constants.BOTTOM_TAB_HOME || HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
            Constants.canAudioPlay = false;
            if (goHome != null)
                goHome.sendAudioEvent("pause");
            if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
                categoriesAdapter.getFragment().Pause();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (HomeSelectedFragment == Constants.BOTTOM_TAB_HOME) {
            Constants.canAudioPlay = false;
            currentPosition = 0;
            Log.d("audiotest", "ondestroy home : stop_destroy");
            if (goHome != null)
                goHome.sendAudioEvent("stop_destroy");
        }
        if (HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
            HomeSelectedFragment = Constants.BOTTOM_TAB_HOME;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("stop");
            }
            if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
                categoriesAdapter.getFragment().Pause();
            }
        } else {
            if (categoryTabs != null && categoryTabs.getChildCount() > 1)
                if (isHeader) {
                    Constants.visiblePageHomeDetails = categoryTabs.getSelectedTabPosition();
                    Constants.visiblePage = Constants.visiblePageHomeDetails;
                } else {
                    Constants.visiblePageHome = categoryTabs.getSelectedTabPosition();
                    Constants.visiblePage = Constants.visiblePageHome;
                }

            if (HomeSelectedFragment == Constants.BOTTOM_TAB_HOME || HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
                Constants.canAudioPlay = true;
                if (!Constants.muted) {
                    if (goHome != null)
                        goHome.sendAudioEvent("resume");
                }
//                if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
//                    categoriesAdapter.getFragment().resetCurrentBullet();
//                }
            }
        }
    }

    public void setHomeData(HomeModel response) {
//        this.mHomeModel = response;
        categoryTabs.removeAllTabs();
//        List<String> catIdsList = new ArrayList<>();
        if (mHomeModel.getData() != null && mHomeModel.getData().size() > 0) {
            for (DataItem dataItem : mHomeModel.getData()) {
                if (dataItem.isLocked() || dataItem.isFollowed()) {
                    categoryTabs.addTab(categoryTabs.newTab().setText(dataItem.getTitle()));
//                    catIdsList.add(dataItem.getRawId());
                }
            }
            Log.d(TAG, "setHomeData: " + currentPosition);
            viewpager.setCurrentItem(currentPosition);
//            if (isSequenceUpdated) {
//                mHomePresenter.updateArticleCatSequence(catIdsList, Constants.CAT_TYPE_ARTICLES);
//            }
        }
    }

    private void loadHomeData(ArrayList<DataItem> dataList) {
        if (dataList != null && dataList.size() > 0) {
//            isagani_dev - temporary commented: tabs.setVisibility(View.VISIBLE);

            categoryFragmentModels.clear();
            ArrayList<CategoryFragmentModel> tempList = new ArrayList<>();
            for (DataItem dataItem : dataList) {
                if (dataItem.isLocked() || dataItem.isFollowed()) {
                    CategoryFragmentModel model = new CategoryFragmentModel();
                    model.setName(dataItem.getTitle());
                    model.setContextId(dataItem.getId());
                    model.setGreeting(dataItem.getGreeting());
                    model.setSubItems(dataItem.getSubItems());
                    tempList.add(model);
                }
            }
            categoryFragmentModels.addAll(tempList);

            categoriesAdapter.notifyDataSetChanged();
            llHomeFeatures.setVisibility(View.VISIBLE);
            Log.d(TAG, "loadHomeData: " + currentPosition);
            if (!isTabSelected) {
                viewpager.setCurrentItem(currentPosition);
            } else
                selectTabMain(selectedTab, TYPE.HOME_TAB);
        }
//        selectLastTab();
    }

    private void updateHomeData(ArrayList<DataItem> dataList) {
        if (dataList != null && dataList.size() > 0) {
            if (cacheManager != null) {
                cacheManager.clearHomeDb();
            }

            ArrayList<CategoryFragmentModel> apiList = new ArrayList<>();
            boolean find = false;
            for (DataItem dataItem : dataList) {
                if (cacheManager != null) {

                    Type listType = new TypeToken<List<SubItem>>() {
                    }.getType();
                    Gson gson = new Gson();
                    String json = gson.toJson(dataItem.getSubItems(), listType);
                    cacheManager.insertHomeData(dataItem.getTitle(), dataItem.getId(), json);
                }
                CategoryFragmentModel model = new CategoryFragmentModel();
                model.setName(dataItem.getTitle());
                model.setContextId(dataItem.getId());
                model.setGreeting(dataItem.getGreeting());
                model.setSubItems(dataItem.getSubItems());
                apiList.add(model);

                if (categoryFragmentModels.size() > 0) {
                    for (CategoryFragmentModel categoryFragmentModel : categoryFragmentModels) {
                        if (categoryFragmentModel.getName().equalsIgnoreCase(model.getName())) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        categoryFragmentModels.add(model);
//                        categoryTabs.addTab(categoryTabs.newTab().setText(model.getName()));
                        categoriesAdapter.notifyDataSetChanged();
                    }
                }
            }

            if (categoryFragmentModels.size() == 0) {
                loadHomeData(dataList);
            }

            llHomeFeatures.setVisibility(View.VISIBLE);
        }
    }

    private void showPopUp() {
        if (mHomeModel == null) return;
        if (categoryTabs == null) return;

        String id = "";
        try {
            id = mHomeModel.getData().get(categoryTabs.getSelectedTabPosition()).getId();
        } catch (Exception ignored) {
        }
        if (forYouReelSheet != null && forYouReelSheet.isVisible())
            return;
        forYouBottomSheet(id, mHomeModel);
    }

    public void forYouBottomSheet(String id, HomeModel model) {

        forYouReelSheet = ForYouReelSheet.getInstance("articles", Constants.CAT_TYPE_ARTICLES, id, model, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }, new BottomSheetInterface() {
            @Override
            public void onForYouSelect() {

            }

            @Override
            public void onFollowingSelect() {

            }

            @Override
            public void onHomeTab(DataItem item) {
                if (id != null || !id.isEmpty()) {
                    ivMore.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fourdots));
                }

                if (!item.getTitle().matches("For\\syou")) {
                    ivMore.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_threeblackgreydot));
                } else {
                    ivMore.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fourdots));
                }
                if (isSequenceUpdated) {
                    isTabSelected = true;
                    selectedTab = item;
                    if (forYouReelSheet != null)
                        forYouReelSheet.dismiss();
                } else {
                    Log.d(TAG, "onHomeTab: " + currentPosition);
                    selectTabMain(item, TYPE.HOME_TAB);
                }
            }

            @Override
            public void onSearch() {
                if (goHome != null && forYouReelSheet != null) {
                    goHome.sendAudioEvent("search");
                    forYouReelSheet.dismiss();
                }
            }

            @Override
            public void onSequenceUpdated(ArrayList<DataItem> mCategoriesList) {
                isSequenceUpdated = true;
                List<String> catIdsList = new ArrayList<>();
                mHomeModel.setData(mCategoriesList);
                if (mHomeModel.getData() != null && mHomeModel.getData().size() > 0) {
                    for (DataItem dataItem : mHomeModel.getData()) {
                        if (dataItem.isLocked() || dataItem.isFollowed()) {
                            catIdsList.add(dataItem.getRawId());
                        }
                    }
                    if (isSequenceUpdated) {
                        mHomePresenter.updateArticleCatSequence(catIdsList, Constants.CAT_TYPE_ARTICLES);
                    }
                }
            }

            @Override
            public void updateTabs(ArrayList<DataItem> mCategoriesList) {
                isSequenceUpdated = true;
                mHomeModel.setData(mCategoriesList);
            }

            @Override
            public void dialogDismissListener() {
                Log.d(TAG, "dialogDismissListener: " + currentPosition);
                if (isSequenceUpdated) {
                    categoriesAdapter.notifyDataSetChanged();
                    setHomeData(mHomeModel);
                    loadHomeData(mHomeModel.getData());
                    isSequenceUpdated = false;
                }
            }
        });

        forYouReelSheet.show(getChildFragmentManager(), "id");

        try {
            BottomSheetBehavior behavior = BottomSheetBehavior.from(forYouReelSheet.getView());
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
            behavior.setDraggable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hidePopUp() {
        if (menuPopup != null)
            menuPopup.hideDialog();

        if (HomeSelectedFragment == Constants.BOTTOM_TAB_HOME || HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
            Constants.canAudioPlay = true;
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("resume");
            }
//            if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
//                categoriesAdapter.getFragment().resetCurrentBullet();
//            }
        }
    }

    private void selectTabMain(DataItem item, TYPE type) {
        Constants.canAudioPlay = true;
        try {
            int pos = -1;
            for (int i = 0; i < categoryFragmentModels.size(); i++) {
                if (categoryFragmentModels.get(i).getContextId().equalsIgnoreCase(item.getId())) {
                    pos = i;
                    break;
                }
            }

            if ((type == TYPE.HOME_TAB || type == TYPE.FEED) && pos != categoryTabs.getSelectedTabPosition() && categoryTabs.getTabAt(pos) != null) {
                if (goHome != null)
                    goHome.scrollDown();
                if (appbar != null)
                    appbar.setExpanded(true, true);
                viewpager.setCurrentItem(pos, true);
                if (isTabSelected) {
                    currentPosition = pos;
                    selectedTab = null;
                    isTabSelected = false;
                }
                if (forYouReelSheet != null) {
                    forYouReelSheet.dismiss();
                }
            } else if (pos == -1) {
                Intent intent = new Intent(getContext(), ChannelPostActivity.class);
                intent.putExtra("type", TYPE.HOME_TAB);
                intent.putExtra("id", item.getRawId());
                intent.putExtra("context", item.getId());
                intent.putExtra("name", item.getTitle());
                intent.putExtra("favorite", item.isFollowed());
                requireContext().startActivity(intent);
            } else {
                if (forYouReelSheet != null) {
                    forYouReelSheet.dismiss();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void selectLastTab() {
        firstTime = true;
        if (mHomePresenter != null) {
            mHomePresenter.getHome(Constants.CAT_TYPE_ARTICLES);
        }
    }

    public void reloadCurrent() {
        setStatusBarColor();
        appbar.setExpanded(true, true);
//        tvGreeting.setVisibility(View.GONE);

        firstTime = true;
        resetData();
        if (mHomePresenter != null) {
            mHomePresenter.getHome(Constants.CAT_TYPE_ARTICLES);
        }
    }

    public void reloadCurrentBg() {
        setStatusBarColor();
        categoryTabs.setScrollPosition(currentPosition, 0f, true);
        appbar.setExpanded(true, true);
        if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
            categoriesAdapter.getFragment().refreshCategory();
        }
    }

    public void apiCallBg() {
        if (categoryFragmentModels == null || categoryFragmentModels.size() <= 0) {
            selectLastTab();
        } else {
            if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
                categoriesAdapter.getFragment().categoryFirstCallAfterGettingNetwork();
            }
        }
    }

    private void setStatusBarColor() {
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }

    public void showNoDataErrorView(boolean show) {
        if (show) {
            if (categoriesAdapter != null && categoriesAdapter.getFragment() != null && categoriesAdapter.getFragment().getCategoryDataCount() > 0) {
                show = false;
            }
        }
        noRecordFoundContainer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        ll_no_results.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void swipe(boolean enable) {
//        if (viewpager != null) {
//            viewpager.disableScroll(enable);
//        }
    }

    @Override
    public void muteIcon(boolean isShow) {

    }

    @Override
    public void onFavoriteChanged(boolean fav) {

    }

    @Override
    public void selectTab(String id) {
//        selectTabMain(id, "", TYPE.HOME_TAB);
    }

    @Override
    public void selectTabOrDetailsPage(String id, String name, TYPE type, String footerType) {
        if (!TextUtils.isEmpty(footerType) && footerType.equals("LARGE_REEL")) {
            Intent intent = new Intent(getContext(), ReelInnerActivity.class);
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, name);
            startActivity(intent);
        } else {
//            selectTabMain(id, name, type);
        }
    }

    public boolean isPopupVisible() {
        return (menuPopup != null && menuPopup.isVisible());
    }

    public interface OnHomeFragmentInteractionListener {
        void onItemClicked(TYPE type, String id, String name, boolean favorite);
    }
}
