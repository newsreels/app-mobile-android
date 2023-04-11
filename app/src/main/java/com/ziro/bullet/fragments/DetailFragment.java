package com.ziro.bullet.fragments;

import static com.ziro.bullet.utills.Constants.HomeSelectedFragment;
import static com.ziro.bullet.utills.Constants.firstTime;
import static com.ziro.bullet.utills.Constants.isHeader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.TempCategoryAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.home.HomeModel;
import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.interfaces.AudioCallback;
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
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.HeaderPresenter;
import com.ziro.bullet.presenter.MainPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.CustomViewPager;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DetailFragment extends Fragment implements MainInterface, GoHome, TempCategorySwipeListener, HomeLoaderCallback {

    private static final String TAG = DetailFragment.class.getSimpleName();
    private static boolean isHome = true;
    private static boolean push = false;
    boolean tag = false;
    private GoHome goHome;
    private DetailFragment.OnHomeFragmentInteractionListener listener;
    private TextView errorText;
    private RelativeLayout progress;
    private RelativeLayout detaillayout;
    private LinearLayout ll_no_results;
    private RelativeLayout tabEndGradient;
    private CustomViewPager viewpager;
    private MainPresenter presenter;
    private TempCategoryAdapter categoriesAdapter;
    private TabLayout tabs;
    private RelativeLayout no_article_found;
    private CardView lets_go;
    private ArrayList<Topics> topics = new ArrayList<>();
    private ArrayList<CategoryFragmentModel> categoryFragmentModels = new ArrayList<>();
    private String sourceId = "", sourceName = "", topicId = "", topicContext = "", topicName = "", articleId = "", mLocationId = "", mLocationUpdateId = "", mCityName = "";
    private String mCurrentId = "";
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private View view;
    private boolean specificSourceTopic = false;
    private HeaderPresenter headerPresenter;
    private String currentType = "";
    private String mName = "";
    private String lastViewModel = "";
    private PrefConfig prefConfig;
    private boolean mFav = false;
    private ImageView ivFollow;
    private ProgressBar followProgress;
    private ImageView setting;
    private TextView title;
    private ImageView backImg;
    private RelativeLayout toolbar;
    private DetailsMenuDialogFragment detailsMenu;
    private ViewSwitcher tabViewSwitcher;
    private ViewSwitcher mainViewSwitcher;
    private LinearLayout menuLayout;
    private String trendingContext;

    public static DetailFragment newInstance() {
        DetailFragment homeFragment = new DetailFragment();
        isHome = true;
        return homeFragment;
    }

    /**
     * If user tap on any specific source/topic pass the parameters to the fragment
     *
     * @param type source/topic
     * @param id   id
     * @param name name
     * @return fragment object
     */
    public static DetailFragment newInstance(TYPE type, String id, String sourceId, String name, boolean fav) {
        DetailFragment homeFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        if (type == TYPE.TOPIC) {
            bundle.putString("currentType", "TOPIC");
            bundle.putString("topic_id", id);
            bundle.putString("topic_name", name);
            bundle.putString("topic_context", sourceId);
            push = false;
        } else if (type == TYPE.HOME_TAB) {
            bundle.putString("currentType", "HOME_TAB");
            bundle.putString("topic_id", id);
            bundle.putString("topic_name", name);
            bundle.putString("topic_context", sourceId);
            push = false;
        } else if (type == TYPE.SOURCE) {
            bundle.putString("currentType", "SOURCE");
            bundle.putString("source_id", id);
            bundle.putString("source_name", name);
            push = false;
        } else if (type == TYPE.SOURCE_PUSH) {
            bundle.putString("article_id", id);
            bundle.putString("source_id", sourceId);
            bundle.putString("source_name", name);
            bundle.putString("currentType", "SOURCE_PUSH");
            push = true;
        } else if (type == TYPE.ARCHIVE) {
            bundle.putString("currentType", "ARCHIVE");
            bundle.putString("name", name);
            push = false;
        } else if (type == TYPE.LOCATION) {
            bundle.putString("currentType", "LOCATION");
            bundle.putString("location_context", sourceId);
            bundle.putString("city_name", name);
            bundle.putString("location_id", id);
            push = false;
        } else if (type == TYPE.TRENDING) {
            bundle.putString("currentType", "TRENDING");
            bundle.putString("trending_context", sourceId);
            bundle.putString("trending_title", name);
            push = false;
        } else if (type == TYPE.FEED) {
            bundle.putString("type", "FEED");
            bundle.putString("feed_context", id);
            bundle.putString("feed_title", name);
            push = false;
        }
        bundle.putBoolean("favorite", fav);
        homeFragment.setArguments(bundle);
        isHome = false;
//        goHome = goHome1;
        return homeFragment;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        Log.e("LKASD", "onAttach");
        if (context instanceof DetailFragment.OnHomeFragmentInteractionListener) {
            listener = (DetailFragment.OnHomeFragmentInteractionListener) context;
        }

        if (context instanceof GoHome) {
            goHome = (GoHome) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        presenter = new MainPresenter(getActivity(), this);
        prefConfig = new PrefConfig(getContext());
        if (getArguments() != null && getArguments().containsKey("trending_context")) {
            trendingContext = getArguments().getString("trending_context");
            currentType = getArguments().getString("currentType");
            mName = getArguments().getString("trending_title");
            //mCurrentId = sourceId;
            //mName = sourceName;
        }
        if (getArguments() != null && getArguments().containsKey("source_id")) {
            sourceId = getArguments().getString("source_id");
            sourceName = getArguments().getString("source_name");
            currentType = getArguments().getString("currentType");
            mCurrentId = sourceId;
            mName = sourceName;
            AnalyticsEvents.INSTANCE.logEvent(getContext(), Events.FEED_SOURCE_OPEN);
        }
        if (getArguments() != null && getArguments().containsKey("article_id")) {
            articleId = getArguments().getString("article_id");
            sourceName = getArguments().getString("source_name");
            sourceId = getArguments().getString("source_id");
            currentType = getArguments().getString("currentType");
            mCurrentId = articleId;
            mName = sourceName;
        }
        if (getArguments() != null && getArguments().containsKey("topic_id")) {
            topicId = getArguments().getString("topic_id");
            topicName = getArguments().getString("topic_name");
            topicContext = getArguments().getString("topic_context");
            currentType = getArguments().getString("currentType");
            mCurrentId = topicId;
            mName = topicName;
            AnalyticsEvents.INSTANCE.logEvent(getContext(), Events.TOPIC_OPEN);
        }
        if (getArguments() != null && getArguments().containsKey("feed_context")) {
            mCurrentId = getArguments().getString("feed_context");
            mName = getArguments().getString("feed_title");
            currentType = getArguments().getString("type");
            AnalyticsEvents.INSTANCE.logEvent(getContext(), Events.ARTICLE_DETAIL_PAGE);
        }
        if (getArguments() != null && getArguments().containsKey("location_id") && getArguments().containsKey("location_context")) {
            mLocationId = getArguments().getString("location_context");
            mLocationUpdateId = getArguments().getString("location_id");
            mCityName = getArguments().getString("city_name");
            currentType = getArguments().getString("currentType");
            mCurrentId = mLocationId;
            mName = mCityName;
        }
        if (getArguments() != null && getArguments().containsKey("currentType")) {
            currentType = getArguments().getString("currentType");
            AnalyticsEvents.INSTANCE.logEvent(getContext(), Events.ARCHIVE_CLICK);
        }

        if (getArguments() != null && getArguments().containsKey("favorite")) {
            mFav = getArguments().getBoolean("favorite");
        }
        if (getArguments() != null && getArguments().containsKey("name")) {
            mName = getArguments().getString("name");
        }

        specificSourceTopic = !TextUtils.isEmpty(topicId) || !TextUtils.isEmpty(sourceId) || !TextUtils.isEmpty(articleId) || !TextUtils.isEmpty(mLocationId);

        bindViews(view);

        if (presenter != null) {
            showLoader();
            resetData();
            if (categoriesAdapter != null) {
                tabs.removeAllTabs();
//                categoriesAdapter.notifyDataSetChanged();
            }

            if (currentType.equalsIgnoreCase("TRENDING")) {

                CategoryFragmentModel topic = new CategoryFragmentModel();
                topic.setArticleId("");
                topic.setName("");
                topic.setContextId(trendingContext);
                categoryFragmentModels.add(topic);
                categoriesAdapter.notifyDataSetChanged();

                hideLoader();
                tabs.setVisibility(View.GONE);
                tabEndGradient.setVisibility(View.GONE);
                tabViewSwitcher.setVisibility(View.GONE);

                //setTitle(mName, true);
                //setBackEnabled(true);
                menuLayout.setVisibility(View.GONE);

            } else if (!currentType.equalsIgnoreCase("ARCHIVE")) {


                if (currentType.equalsIgnoreCase("FEED")) {

                    CategoryFragmentModel topic = new CategoryFragmentModel();
                    topic.setContextId(mCurrentId);
                    topic.setName(mName);
                    categoryFragmentModels.add(topic);
                    categoriesAdapter.notifyDataSetChanged();

                    hideLoader();
                    tabs.setVisibility(View.GONE);
                    tabEndGradient.setVisibility(View.GONE);
                    tabViewSwitcher.setVisibility(View.GONE);

                } else {
                    //adding data to topics list and notifying adapter will make the fragments for that specific type
                    //this condition is valid only when user taps on specific type, if its topic then news for that topic will load in 1st tab
                    //and then related topic will be called

                    ArrayList<CategoryFragmentModel> tempList = new ArrayList<>();
                    if (!TextUtils.isEmpty(topicId)) {
                        CategoryFragmentModel topic = new CategoryFragmentModel();
                        topic.setTopicId(topicId);
                        topic.setName(topicName);
                        topic.setContextId(topicContext);
                        tempList.add(topic);
                        Map<String, String> params = new HashMap<>();
                        params.put(Events.KEYS.TOPIC_ID, topicId);
                        AnalyticsEvents.INSTANCE.logEvent(getContext(),
                                params,
                                Events.TOPIC_OPEN);
//                    categoriesAdapter.notifyDataSetChanged();
                    } else if (!TextUtils.isEmpty(articleId)) {
                        CategoryFragmentModel topic = new CategoryFragmentModel();
                        topic.setArticleId(articleId);
                        topic.setName(sourceName);
                        tempList.add(topic);
                        categoryFragmentModels.add(topic);
                        categoriesAdapter.notifyDataSetChanged();
                    } else if (!TextUtils.isEmpty(mLocationId)) {
                        CategoryFragmentModel topic = new CategoryFragmentModel();
                        topic.setLocationId(mLocationId);
                        topic.setName("All");
                        tempList.add(topic);
                        tabs.setVisibility(View.GONE);
                        tabViewSwitcher.setVisibility(View.GONE);
                        categoryFragmentModels.add(topic);
                        categoriesAdapter.notifyDataSetChanged();
                        tabs.addTab(tabs.newTab().setText(topic.getName()));
                    }

                    //this condition is valid only when user taps on specific source/topic
                    if (specificSourceTopic) {
                        Constants.HomeSelectedFragment = Constants.BOTTOM_TAB_OTHER;
                        if (!push) {
                            if (tempList.size() > 0) {
                                presenter.relatedToTopics(tempList.get(0).getTopicId());
                            } else {
                                presenter.relatedToSources(sourceId);
                            }
                        } else {
                            hideLoader();
                            tabs.setVisibility(View.GONE);
                            tabViewSwitcher.setVisibility(View.GONE);
                        }
                    }

                }
            } else {
                CategoryFragmentModel topic = new CategoryFragmentModel();
                topic.setArticleId("");
                topic.setName("");
                categoryFragmentModels.add(topic);
                categoriesAdapter.notifyDataSetChanged();

                hideLoader();
                tabs.setVisibility(View.GONE);
                tabViewSwitcher.setVisibility(View.GONE);
                tabEndGradient.setVisibility(View.GONE);
            }
        }
//        setViewObserver();
        return view;
    }

    private void bindViews(View view) {
        no_article_found = view.findViewById(R.id.no_article_found);
        lets_go = view.findViewById(R.id.lets_go);
        tabs = view.findViewById(R.id.tabs);
        errorText = view.findViewById(R.id.errorText);
        progress = view.findViewById(R.id.progress);
        detaillayout = view.findViewById(R.id.detaillayout);
        tabEndGradient = view.findViewById(R.id.tabEndGradient);
        viewpager = view.findViewById(R.id.viewpager);
        ll_no_results = view.findViewById(R.id.ll_no_results);

        followProgress = view.findViewById(R.id.followProgress);
        ivFollow = view.findViewById(R.id.imgFollow);
        setting = view.findViewById(R.id.setting);
        toolbar = view.findViewById(R.id.toolbar);
        backImg = view.findViewById(R.id.leftArrow);
        title = view.findViewById(R.id.title);
        tabViewSwitcher = view.findViewById(R.id.tab_view_switcher);
        mainViewSwitcher = view.findViewById(R.id.main_view_switcher);
        menuLayout = view.findViewById(R.id.menu);

//        loader_gif = view.findViewById(R.id.loader);
//        Glide.with(getContext())
//                .load(Utils.getLoaderForTheme(prefConfig.getAppTheme()))
//                .into(loader_gif);

        viewpager.disableScroll(false);

        if (currentType.equals("FEED")) {
            ivFollow.setVisibility(View.GONE);
            setTitle(mName, true);
            setBackEnabled(true);
        } else {
            ivFollow.setVisibility(View.VISIBLE);
            if (!currentType.equals("SOURCE")) {
                if (mFav) {
                    ivFollow.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_unfollow_cat));
                } else {
                    ivFollow.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_follow_item));
                }
            } else {
                //on open source setting false by default then on api response it will set accordingly
                prefConfig.setSrcFav(false);
                ivFollow.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_follow_item));
            }

            if (currentType.equals("SOURCE")) {
                setting.setVisibility(View.VISIBLE);
            } else {
                setting.setVisibility(View.GONE);
            }

            if (specificSourceTopic || (!TextUtils.isEmpty(mName) && mName.equalsIgnoreCase(getString(R.string.saved_articles)))) {
                setTitle(mName, true);
                setBackEnabled(true);
            }
        }

        Log.e("expandCard", "DetailFragment : ");

        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null && getChildFragmentManager().getFragments().size() > 0) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof CategoryFragment) {
                    getChildFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
            }
//            for (int i = 0; i < getChildFragmentManager().getFragments().size(); i++) {
//                if (getChildFragmentManager().getFragments().get(i) instanceof CategoryFragment) {
//                    getChildFragmentManager().getFragments().clear();
//                    ((CategoryFragment) getChildFragmentManager().getFragments().get(i)).setMyTag("id_" + i);
//                }
//            }
        }

        //TODO CHANGE HERE
        if (specificSourceTopic) {

            if (!TextUtils.isEmpty(topicId) || !TextUtils.isEmpty(mLocationId)) {
                // From Topic Details User can goto channel details
                categoriesAdapter = new TempCategoryAdapter("", true, this, currentType, specificSourceTopic, this,
                        goHome, isHome, getChildFragmentManager(), FragmentPagerAdapter.POSITION_NONE, categoryFragmentModels, new OnGotoChannelListener() {
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
            } else {
                // For channel details only
                categoriesAdapter = new TempCategoryAdapter("", true, this, currentType, specificSourceTopic, this, goHome, isHome, getChildFragmentManager(), FragmentPagerAdapter.POSITION_NONE, categoryFragmentModels, new OnGotoChannelListener() {
                    @Override
                    public void onItemClicked(TYPE type, String id, String name, boolean favorite) {

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
            }

        } else {
            categoriesAdapter = new TempCategoryAdapter("", true, this, currentType, specificSourceTopic, this, goHome, isHome, getChildFragmentManager(), FragmentPagerAdapter.POSITION_NONE, categoryFragmentModels, new OnGotoChannelListener() {
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
        }

        viewpager.setAdapter(categoriesAdapter);
        tabs.setupWithViewPager(viewpager, false);

        ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                if (categoriesAdapter != null && categoriesAdapter.getRegisteredFragment(position) != null) {
                    if (categoriesAdapter.getRegisteredFragment(position) instanceof CategoryFragment) {
                        ((CategoryFragment) categoriesAdapter.getRegisteredFragment(position)).selectFirstItemOnCardViewMode();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        viewpager.addOnPageChangeListener(pageListener);

        headerPresenter = new HeaderPresenter(getActivity(), new HomeCallback() {
            @Override
            public void loaderShow(boolean flag) {
                if (flag) {
                    followProgress.setVisibility(View.VISIBLE);
                    ivFollow.setVisibility(View.GONE);
                } else {
                    followProgress.setVisibility(View.GONE);
                    ivFollow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void error(String error) {

            }

            @Override
            public void success(HomeModel response) {

                mFav = !mFav;
                if (mFav && getContext() != null) {
                    ivFollow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_unfollow_cat));
                } else if (getContext() != null) {
                    ivFollow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_follow_item));
                }
                switch (currentType) {
                    case "SOURCE":
                        Constants.isSourceDataChange = true;
                        break;
                    case "TOPIC": {
                        if (mFav)
                            Utils.showSnacky(getView(), requireContext().getResources().getString(R.string.added_to_fav));
                        else
                            Utils.showSnacky(getView(), requireContext().getResources().getString(R.string.removed_from_fav));
                        Constants.topicsStatusChanged = topicId;
                        Constants.followStatus = String.valueOf(mFav);
                        Constants.isTopicDataChange = true;
                    }
                    break;
                    case "HOME_TAB": {
                        if (mFav)
                            Utils.showSnacky(getView(), requireContext().getResources().getString(R.string.added_to_fav));
                        else
                            Utils.showSnacky(getView(), requireContext().getResources().getString(R.string.removed_from_fav));
                        DataItem dataItem = new DataItem();
                        dataItem.setTitle(topicName);
                        dataItem.setRawId(topicId);
                        dataItem.setId(topicContext);
                        dataItem.setFollowed(mFav);
                        Constants.articleTabItem = dataItem;
                    }
                    break;
                    case "LOCATION":
                        Constants.isTopicDataChange = true;
                        break;
                    case "SOURCE_PUSH":
                        Constants.isSourceDataChange = true;
                        break;
                }
            }

            @Override
            public void searchSuccess(CategoryResponse body, boolean isPagination) {

            }
        });

        ivFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headerPresenter != null && !TextUtils.isEmpty(mCurrentId)) {
                    if (currentType.equals("SOURCE")) {
                        if (mFav) {
                            headerPresenter.unfollowSource(mCurrentId);
                        } else {
                            headerPresenter.followSource(mCurrentId);
                        }
                    } else if (currentType.equals("TOPIC") || currentType.equals("HOME_TAB")) {
                        if (mFav) {
                            headerPresenter.unfollowTopic(mCurrentId);
                        } else {
                            headerPresenter.followTopic(mCurrentId);
                        }
                    } else if (currentType.equals("LOCATION")) {
                        if (!TextUtils.isEmpty(mLocationUpdateId)) {
                            if (mFav) {
                                headerPresenter.unfollowLocation(mLocationUpdateId);
                            } else {
                                headerPresenter.followLocation(mLocationUpdateId);
                            }
                        }
                    } else if (currentType.equals("SOURCE_PUSH")) {
                        if (mFav) {
                            headerPresenter.unfollowSource(sourceId);
                        } else {
                            headerPresenter.followSource(sourceId);
                        }
                    }
                }
            }
        });

        backImg.setOnClickListener(v -> getActivity().onBackPressed());
        setting.setOnClickListener(v -> showDetailsMenu());

        lets_go.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClicked(TYPE.MANAGE, "", "discover", true);
            }
        });
    }

    public void invalidateViews() {
        if (getActivity() == null)
            return;

        tabs.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.tabbar_text_unselected),
                ContextCompat.getColor(getActivity(), R.color.static_tabbar_text_selected_new));

        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.static_tabbar_text_selected));

        tabEndGradient.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.menu_gradient_2));
        view.findViewById(R.id.root).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
//        view.findViewById(R.id.status_bar).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bottombar_bg));
//        toolbarContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bottombar_bg));

//        gradient2.setColorFilter(ContextCompat.getColor(getContext(), R.color.gradient), android.graphics.PorterDuff.Mode.MULTIPLY);

        title.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_category_text_color));

//        mHeaderTabs.setSelectedTabIndicator(R.drawable.round_main_tab_indicator);
    }

    private void startAudio() {
        Log.d("audiotest", "startAudio: ");
        if (goHome != null) {
            goHome.sendAudioEvent("unmute");
            goHome.sendAudioEvent("isSpeaking");
        }
    }


    private void showDetailsMenu() {
        FragmentManager fm = getChildFragmentManager();
        if (detailsMenu == null) {
            detailsMenu = new DetailsMenuDialogFragment(getContext());
        }
        detailsMenu.show(fm, "fragment_menu_details");
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
        ll_no_results.setVisibility(View.GONE);
        isLastPage = false;
        if (tabs != null) {
            tabs.removeAllTabs();
        }
    }

    @Override
    public void onProgressChanged(boolean flag) {
        progress.setVisibility(View.GONE);
        Utils.loadSkeletonLoader(mainViewSwitcher, flag);

        if (flag) {
            isLoading = true;
            firstTime = false;

            // showLoader();
        } else {
            isLoading = false;

            // hideLoader();
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        Utils.loadSkeletonLoader(tabViewSwitcher, flag);

        if (flag) {
            isLoading = true;
            Log.e("LKASD", "showLoader");
            firstTime = false;

            // showLoader();
        } else {
            isLoading = false;

            // hideLoader();
        }
    }

    @Override
    public void error(String error, int load) {
        mainViewSwitcher.setVisibility(View.GONE);
        detaillayout.setVisibility(View.GONE);
        ll_no_results.setVisibility(View.VISIBLE);
//        errorText.setText(R.string.no_record);
//        errorText.setVisibility(View.VISIBLE);

        Log.e("Topic", "error: ");
    }

    @Override
    public void error404(String msg) {
        Log.e("Topic", "error404: ");
        mainViewSwitcher.setVisibility(View.GONE);
        detaillayout.setVisibility(View.GONE);
        ll_no_results.setVisibility(View.VISIBLE);
//        errorText.setText(R.string.no_record);
//        errorText.setVisibility(View.VISIBLE);
    }

    @Override
    public void success(ArticleResponse response, int load, int category, String topic) {

    }

    @Override
    public void success(CategoryResponse response) {

    }

    @Override
    public void UserTopicSuccess(ArrayList<Topics> response) {
        if (response != null) {

            categoryFragmentModels.clear();
            if (categoriesAdapter != null)
                categoriesAdapter.notifyDataSetChanged();

            topics.addAll(response);
            if (topics != null && topics.size() > 1) {
                tabs.setVisibility(View.VISIBLE);
                tabViewSwitcher.setVisibility(View.VISIBLE);
            } else {
                tabs.setVisibility(View.GONE);
                tabViewSwitcher.setVisibility(View.GONE);
            }
            for (int i = 0; i < response.size(); i++) {
                CategoryFragmentModel model = new CategoryFragmentModel();
                model.setName(response.get(i).getName());
                model.setTopicId(response.get(i).getId());
                model.setContextId(topicContext);
                categoryFragmentModels.add(model);
            }
            categoriesAdapter.notifyDataSetChanged();
            if (categoryFragmentModels != null && categoryFragmentModels.size() > 0) {
                // category_menu.setVisibility(View.VISIBLE);
                for (int i = 0; i < categoryFragmentModels.size(); i++) {
                    tabs.addTab(tabs.newTab().setText(categoryFragmentModels.get(i).getName()));
//                    if (i != 0)
//                        tabs.getTabAt(i).view.setBackgroundResource(R.drawable.round_tab_indicator_unselected);
                }

                for (int i = 0; i < ((ViewGroup) tabs.getChildAt(0)).getChildCount(); i++) {
                    View tab = ((ViewGroup) tabs.getChildAt(0)).getChildAt(i);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();

                    if (tabs.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                        if (i == (((ViewGroup) tabs.getChildAt(0)).getChildCount() - 1)) {
                            //last item
                            params.leftMargin = 100;
                        } else {
                            params.rightMargin = 10;
                            params.leftMargin = 10;
                        }
                    } else {
                        if (i == (((ViewGroup) tabs.getChildAt(0)).getChildCount() - 1)) {
                            //last item
                            params.rightMargin = 100;
                        } else {
                            params.leftMargin = 10;
                            params.rightMargin = 10;
                        }
                    }
                }

                if (!tag) {
                    tabListener();
                    tag = true;
                }

            } else {
                // category_menu.setVisibility(View.GONE);
                tabViewSwitcher.setVisibility(View.GONE);
            }
        }
    }

    private void tabListener() {
        if (tabs == null) return;
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

                viewpager.setCurrentItem(tab.getPosition());
//                tab.view.setBackgroundResource(R.drawable.round_tab_indicator);
//                categoriesAdapter.notifyDataSetChanged();
//                if (categoriesAdapter != null) {
//                    if (!prefConfig.getMenuViewMode().equalsIgnoreCase(MENU_EXTENDED_VIEW)) {
//                        gradient2.setVisibility(View.VISIBLE);
////                        categoriesAdapter.selectFirstItemOnListViewMode();
//                    } else {
//                        gradient2.setVisibility(View.GONE);
//                        categoriesAdapter.reloadCard();
//                    }
//                }

//                if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
//                    categoriesAdapter.getFragment().selectFirstItemOnCardViewMode();
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (getActivity() != null && Objects.equals(tab.getText(), getActivity().getString(R.string.edit_location))) {
                    return;
                }
////                tab.view.setBackgroundResource(0);
//                tab.view.setBackgroundResource(R.drawable.round_tab_indicator_unselected);
//                if (tabs != null && categoryFragmentModels != null && categoryFragmentModels.size() > 0) {
//                    for (int i = 0; i < categoryFragmentModels.size(); i++) {
//                        if (tabs.getTabAt(i) != null) {
//                            tabs.getTabAt(i).view.setBackgroundResource(R.drawable.round_tab_indicator_unselected);
//                        }
//                    }
//                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                if (!tab.getText().toString().equalsIgnoreCase("ALL") && activeTab.equalsIgnoreCase("CHANNELS")) {
//                    new Handler().postDelayed(() -> tabs.selectTab(tabs.getTabAt(0)), 500);
//                    return;
//                }
            }
        });
    }

    @Override
    public void UserInfoSuccess(Info info) {
        if (info != null) {

            if (prefConfig != null) {
                prefConfig.setSrcLoc(info.getCategory());
                prefConfig.setSrcLang(info.getLanguage());
                prefConfig.setSrcFav(info.isFavorite());
            }

            TopicsModel topicsModel = new TopicsModel();
            ArrayList<Topics> topics = new ArrayList<>();
            if (info.getCategories() != null) {
                for (int i = 0; i < info.getCategories().size(); i++) {
                    Topics topic = new Topics();
                    topic.setId(info.getCategories().get(i).getId());
                    topic.setContext(info.getCategories().get(i).getContext());
                    topic.setName(info.getCategories().get(i).getName());
                    topics.add(topic);
                }
            }
            topicsModel.setTopics(topics);

            //updating categories
            UserTopicSuccess(topicsModel.getTopics());

            //updating fav icon
            setFavIcon();
        }
    }

    @Override
    public void UserSourceSuccess(SourceModel response) {
        if (categoriesAdapter != null) {
            categoriesAdapter.notifyDataSetChanged();
        }
    }

    public void setFavIcon() {
        if (prefConfig != null) {
            Log.e("FAVSS", "--> " + prefConfig.getSrcFav());
            if (prefConfig.getSrcFav() && getContext() != null) {
                ivFollow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_unfollow_cat));
            } else if (getContext() != null) {
                ivFollow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_follow_item));
            }
        }
    }

    private void showLoader() {
        isLoading = true;
//        if (firstTime) {
        Log.e("LKASD", "showLoader");
        //progress.setVisibility(View.VISIBLE);
        firstTime = false;
//        }
    }

    private void hideLoader() {
        Log.e("LKASD", "hideLoader");
        isLoading = false;
        //progress.setVisibility(View.GONE);
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

    public void scrollToTop() {
        if (viewpager != null) {
            viewpager.setCurrentItem(0, true);
        }

        if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
            categoriesAdapter.getFragment().scrollToTop();
        }
    }

    @Override
    public void sendAudioToTempHome(AudioCallback audioCallback, String fragTag, String status, AudioObject audio) {
        if (categoriesAdapter.getFragment() != null) {
            Log.e("sendAudioToTemp", "====================================");
            Constants.fragTag = fragTag;
            Constants.articleId = audio.getId();
            Constants.bullet_position = audio.getIndex();
            Constants.speech = audio.getText();
            Constants.url = audio.getUrl();
            Constants.bullet_duration = audio.getDuration();
//            if (fragTag.equalsIgnoreCase(categoriesAdapter.getFragment().getMyTag())) {
            playTextAudio(audioCallback, audio);
//            } else {
//                Log.e("sendAudioToTemp", "ignore this");
//            }
        }
    }

    @Override
    public void scrollUp() {
//        category_menu.setVisibility(View.GONE);
    }

    @Override
    public void scrollDown() {
//        category_menu.setVisibility(View.VISIBLE);
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

        if (tabs != null && tabs.getChildCount() > 1)
            if (isHeader) {
                Constants.visiblePageHomeDetails = tabs.getSelectedTabPosition();
                Constants.visiblePage = Constants.visiblePageHomeDetails;
            } else {
                Constants.visiblePageHome = tabs.getSelectedTabPosition();
                Constants.visiblePage = Constants.visiblePageHome;
            }

        if ((HomeSelectedFragment == Constants.BOTTOM_TAB_HOME || HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) || currentType.equalsIgnoreCase("ARCHIVE")) {
            Constants.canAudioPlay = true;
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("resume");
            }
            if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
                categoriesAdapter.getFragment().resetCurrentBullet();
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            Log.d("audiotest", "ondestroy home : stop_destroy");
            if (goHome != null)
                goHome.sendAudioEvent("stop_destroy");
        }
//        if (HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
//            HomeSelectedFragment = Constants.BOTTOM_TAB_HOME;
//        }
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
//            if (!lastViewModel.equals(prefConfig.getMenuViewMode()))
//                categoriesAdapter.reload();
            if (tabs != null && tabs.getChildCount() > 1)
                if (isHeader) {
                    Constants.visiblePageHomeDetails = tabs.getSelectedTabPosition();
                    Constants.visiblePage = Constants.visiblePageHomeDetails;
                } else {
                    Constants.visiblePageHome = tabs.getSelectedTabPosition();
                    Constants.visiblePage = Constants.visiblePageHome;
                }


            if (HomeSelectedFragment == Constants.BOTTOM_TAB_HOME || HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
                Constants.canAudioPlay = true;
                if (!Constants.muted) {
                    if (goHome != null)
                        goHome.sendAudioEvent("resume");
                }
                if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
                    categoriesAdapter.getFragment().resetCurrentBullet();
                }
            }

        }
    }

    private void setTitle(String txt, boolean wanaShow) {
        if (wanaShow) {
            toolbar.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            Constants.isHeader = true;
            if (!TextUtils.isEmpty(txt) && !txt.equalsIgnoreCase(getString(R.string.saved_articles))) {
                if (!TextUtils.isEmpty(currentType) && currentType.equalsIgnoreCase("FEED")) {
                    ivFollow.setVisibility(View.GONE);
                } else {
                    ivFollow.setVisibility(View.VISIBLE);
                }
            } else {
                ivFollow.setVisibility(View.GONE);
            }
        } else {
            toolbar.setVisibility(View.GONE);
            title.setVisibility(View.INVISIBLE);
            backImg.setVisibility(View.INVISIBLE);
            Constants.isHeader = false;
        }
        title.setText(txt);
    }

    private void setBackEnabled(boolean enabled) {
        if (enabled) {
            backImg.setVisibility(View.VISIBLE);
        } else {
            backImg.setVisibility(View.GONE);
        }
    }

    public void hidePopUp() {
//        if (menuPopup != null)
//            menuPopup.hideDialog();

        if (HomeSelectedFragment == Constants.BOTTOM_TAB_HOME || HomeSelectedFragment == Constants.BOTTOM_TAB_OTHER) {
            Constants.canAudioPlay = true;
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("resume");
            }
            if (categoriesAdapter != null && categoriesAdapter.getFragment() != null) {
                categoriesAdapter.getFragment().resetCurrentBullet();
            }
        }
    }

    @Override
    public void swipe(boolean enable) {
        if (viewpager != null) {
            viewpager.disableScroll(enable);
        }
    }

    @Override
    public void muteIcon(boolean isShow) {
//        if (prefConfig != null && ivRead != null) {
//            if (!prefConfig.getMenuViewMode().equalsIgnoreCase(MENU_EXTENDED_VIEW)) {
//                if (isShow) {
//                    gradient2.setVisibility(View.VISIBLE);
//                } else {
//                    gradient2.setVisibility(View.GONE);
//                }
//            }
//        }
    }

    @Override
    public void onFavoriteChanged(boolean fav) {
        if (fav) {
            ivFollow.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_unfollow_cat));
        } else {
            ivFollow.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_follow_item));
        }
    }

    @Override
    public void selectTab(String id) {

    }

    @Override
    public void selectTabOrDetailsPage(String id, String name, TYPE type, String footerType) {
        if (!TextUtils.isEmpty(footerType) && footerType.equals("LARGE_REEL")) {
            Intent intent = new Intent(getContext(), ReelInnerActivity.class);
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, name);
            startActivity(intent);
        } else {
            selectTabMain(id, name, type);
        }
    }

    private void selectTabMain(String id, String name, TYPE type) {
        Constants.canAudioPlay = true;
        try {
            int pos = 0;
            for (int i = 0; i < categoryFragmentModels.size(); i++) {
                if (categoryFragmentModels.get(i).getContextId().equalsIgnoreCase(id)) {
                    pos = i;
                }
            }

            if ((type == TYPE.HOME_TAB || type == TYPE.FEED) && pos != tabs.getSelectedTabPosition() && tabs.getTabAt(pos) != null) {
                if (goHome != null)
                    goHome.scrollDown();
//                if (appbar != null)
//                    appbar.setExpanded(true, true);
                tabs.getTabAt(pos).select();
            } else {
                hidePopUp();
                if (listener != null && !TextUtils.isEmpty(name)) {
                    listener.onItemClicked(type, id, name, false);
                }
            }
        } catch (Exception ignore) {
        }
    }

    public interface OnHomeFragmentInteractionListener {
        void onItemClicked(TYPE type, String id, String name, boolean favorite);
    }
}
