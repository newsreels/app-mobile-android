//package com.ziro.bullet.fragments;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.content.res.TypedArray;
//import android.graphics.Rect;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.InsetDrawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.RelativeLayout;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.gson.Gson;
//import com.ziro.bullet.R;
//import com.ziro.bullet.activities.ChannelPostActivity;
//import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
//import com.ziro.bullet.adapters.feed.RelatedFeedAdapter;
//import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
//import com.ziro.bullet.adapters.feed.VideoViewHolder;
//import com.ziro.bullet.adapters.feed.YoutubeViewHolder;
//import com.ziro.bullet.data.PrefConfig;
//import com.ziro.bullet.data.TYPE;
//import com.ziro.bullet.interfaces.AudioCallback;
//import com.ziro.bullet.interfaces.BulletDetailCallback;
//import com.ziro.bullet.interfaces.DetailsActivityInterface;
//import com.ziro.bullet.interfaces.NewsCallback;
//import com.ziro.bullet.interfaces.ShareToMainInterface;
//import com.ziro.bullet.interfaces.TempCategorySwipeListener;
//import com.ziro.bullet.interfaces.goHome;
//import com.ziro.bullet.model.Articles.Article;
//import com.ziro.bullet.model.Articles.ArticleResponse;
//import com.ziro.bullet.model.Articles.ForYou;
//import com.ziro.bullet.model.AudioObject;
//import com.ziro.bullet.presenter.MainPresenter;
//import com.ziro.bullet.presenter.NewsPresenter;
//import com.ziro.bullet.texttospeech.TextToAudioPlayerHelper;
//import com.ziro.bullet.utills.Constants;
//import com.ziro.bullet.utills.MessageEvent;
//import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
//import com.ziro.bullet.utills.Utils;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//
//import im.ene.toro.PlayerSelector;
//import im.ene.toro.widget.Container;
//
//import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
//import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
//import static com.ziro.bullet.utills.Constants.isApiCalling;
//
//public class MainBulletDetailFragment extends Fragment implements NewsCallback, BulletDetailCallback {
//
//    PlayerSelector selector = PlayerSelector.DEFAULT;
//    private String _myTag;
//    private TextToAudioPlayerHelper textToAudio;
//    private AudioCallback audioCallback;
//
//    private Article article;
//
//    private NewsPresenter presenter;
//    private MainPresenter mainPresenter;
//    private PrefConfig prefConfig;
//
//    private RelativeLayout ivBack;
//    private Container mListRV;
//    private RelatedFeedAdapter mCardAdapter;
//    private SpeedyLinearLayoutManager cardLinearLayoutManager;
//
//    private ArrayList<Article> contentArrayList = new ArrayList<>();
//    private String nextPage;
//    private String articleId;
//
//    private int mArticlePosition = 0;
//    private boolean activityVisible;
//
//
//    private goHome goHome = new goHome() {
//        @Override
//        public void home() {
//
//        }
//
//        @Override
//        public void sendAudioToTempHome(AudioCallback audioCallback1, String fragTag, String status, AudioObject audio) {
//            audioCallback = audioCallback1;
//            if (audio != null) {
//                Log.e("sendAudioToTemp", "=================HOME===================");
//                Log.e("sendAudioToTemp", "fragTag : " + fragTag);
//                Log.e("sendAudioToTemp", "speech : " + audio.getText());
//                Log.e("sendAudioToTemp", "speech : " + audio.getId());
//                Log.e("sendAudioToTemp", "bullet_position : " + audio.getIndex());
//                if (Constants.canAudioPlay) {
//                    if (!Constants.muted) {
//                        if (textToAudio != null) {
//                            textToAudio.stop();
////                    textToAudio.play(articleId, bullet_position, speech);
//                            textToAudio.isPlaying(audio, audioCallback);
//                        }
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void scrollUp() {
//
//        }
//
//        @Override
//        public void scrollDown() {
//
//        }
//
//        @Override
//        public void sendAudioEvent(String event) {
//
//            Log.e("ACTION-", "ACTION : " + event);
//            if (textToAudio != null && !TextUtils.isEmpty(event)) {
//                switch (event) {
//                    case "pause":
//                        Log.d("audiotest", "sendAudioEvent: pause");
//                        textToAudio.pause();
//                        break;
//                    case "resume":
//                        Log.d("audiotest", "sendAudioEvent: resume");
//                        if (Constants.canAudioPlay) {
//                            if (!Constants.muted) {
//                                textToAudio.resume();
//                            }
//                        }
//                        break;
//                    case "stop_destroy":
//                        Log.d("audiotest", "sendAudioEvent: stop_destroy");
//                        textToAudio.stop();
//                        textToAudio.destroy();
//                        break;
//                    case "stop":
//                        Log.d("audiotest", "sendAudioEvent: stop");
//                        textToAudio.stop();
//                        break;
//                    case "destroy":
//                        Log.d("audiotest", "sendAudioEvent: destroy");
//                        textToAudio.destroy();
//                        break;
//                    case "mute":
//                        Log.d("audiotest", "sendAudioEvent: mute");
//                        textToAudio.mute();
//                        textToAudio.stop();
//                        textToAudio.destroy();
//                        break;
//                    case "unmute":
//                        textToAudio.unmute();
//                        break;
//                    case "isSpeaking":
//                        Log.d("audiotest", "sendAudioEvent: isSpeaking");
//                        if (!textToAudio.isSpeaking()) {
//                            if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(Constants.speech)) {
//                                sendAudioToTempHome(audioCallback, "isSpeaking", "", new AudioObject(Constants.articleId, Constants.speech, Constants.url, Constants.bullet_position, Constants.bullet_duration));
//                            }
//                        }
//                        break;
//                    case "play":
//                        Log.d("audiotest", "sendAudioEvent: play");
//                        textToAudio.stop();
//                        if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(Constants.speech)) {
//                            sendAudioToTempHome(audioCallback, "play", "", new AudioObject(Constants.articleId, Constants.speech, Constants.url, Constants.bullet_position, Constants.bullet_duration));
//                        }
//                        break;
//                    case "homeTab":
////                        if (bottomNavigationView != null)
////                            bottomNavigationView.setSelectedItemId(R.id.action_home);
//                        break;
//                }
//            }
//
//        }
//    };
//    public OnGotoChannelListener onGotoChannelListener = new OnGotoChannelListener() {
//        @Override
//        public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
//            Log.e("@@@", "ITEM CLICKED");
//
//            //Resetting the audio data to avoid old article speech while loading new data
//            Constants.articleId = "";
//            Constants.speech = "";
//            Constants.url = "";
//            Log.d("audiotest", " onitemclick : stop_destroy");
//            goHome.sendAudioEvent("stop_destroy");
//            // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);
//
//            if (type != null && type.equals(TYPE.MANAGE)) {
//                //selectSearch(name);
//            } else if (type != null && id != null && name != null) {
////            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
//                Constants.canAudioPlay = true;
//                Log.e("expandCard", "newInstance 3");
//
//                Intent intent = new Intent(MainBulletDetailFragment.this, ChannelPostActivity.class);
//                intent.putExtra("type", type);
//                intent.putExtra("id", id);
//                intent.putExtra("name", name);
//                intent.putExtra("favorite", favorite);
//                startActivity(intent);
//                // finish();
//            }
//        }
//
//        @Override
//        public void onArticleSelected(Article article) {
//
//        }
//    };
//    private ShareToMainInterface shareToMainInterface = new ShareToMainInterface() {
//
//        @Override
//        public void removeItem(String id, int position) {
//
//        }
//
//        @Override
//        public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
//            Log.e("@@@", "ITEM CLICKED");
//
//            //Resetting the audio data to avoid old article speech while loading new data
//            Constants.articleId = "";
//            Constants.speech = "";
//            Constants.url = "";
//            Log.d("audiotest", " onitemclick : stop_destroy");
//            goHome.sendAudioEvent("stop_destroy");
//            // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);
//
//            if (type != null && type.equals(TYPE.MANAGE)) {
//                //selectSearch(name);
//            } else if (type != null && id != null && name != null) {
////            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
//                Constants.canAudioPlay = true;
//                Log.e("expandCard", "newInstance 3");
//
//                Intent intent = new Intent(MainBulletDetailFragment.this, ChannelPostActivity.class);
//                intent.putExtra("type", type);
//                intent.putExtra("id", id);
//                intent.putExtra("name", name);
//                intent.putExtra("favorite", favorite);
//                startActivity(intent);
//            }
//        }
//
//        @Override
//        public void unarchived() {
//
//        }
//    };
//    private TempCategorySwipeListener swipeListener = new TempCategorySwipeListener() {
//        @Override
//        public void swipe(boolean enable) {
//
//        }
//
//        @Override
//        public void muteIcon(boolean isShow) {
//
//        }
//
//        @Override
//        public void onFavoriteChanged(boolean fav) {
//
//        }
//
//        @Override
//        public void selectTab(String id) {
//
//        }
//    };
//    private long mLastClickTime = 0;
//    private OnMainBulletDetailsListener listener;
//    private View view;
//
//    public static MainBulletDetailFragment newInstance() {
//        MainBulletDetailFragment mainBulletDetailFragment = new MainBulletDetailFragment();
//        return mainBulletDetailFragment;
//    }
//
//    public static MainBulletDetailFragment newInstance(Article article, String articleId) {
//        MainBulletDetailFragment mainBulletDetailFragment = new MainBulletDetailFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("article", new Gson().toJson(article));
//        bundle.putString("articleId", articleId);
//        mainBulletDetailFragment.setArguments(bundle);
//        return mainBulletDetailFragment;
//    }
//
//    @Override
//    public void onAttach(@NotNull Context context) {
//        super.onAttach(context);
//        Log.e("LKASD", "onAttach");
//        if (context instanceof MainBulletDetailFragment.OnMainBulletDetailsListener) {
//            listener = (MainBulletDetailFragment.OnMainBulletDetailsListener) context;
//        }
//
//        if (context instanceof goHome) {
//            goHome = (goHome) context;
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        Log.d("TAG", "onDetach: ");
//    }
//
//
//    public interface OnMainBulletDetailsListener {
//        void onItemClicked(TYPE type, String id, String name, boolean favorite);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.activity_bullet_detail, container, false);
//
//        if (getArguments() != null && getArguments().containsKey("article")) {
//            article = new Gson().fromJson(getArguments().getString("article"), Article.class);
//            articleId = article.getId();
//        } else if (getArguments() != null && getArguments().containsKey("articleId")) {
//            articleId = getArguments().getString("articleId");
//            presenter.loadSingleArticle(articleId);
//        }
//
//        if (articleId != null && !articleId.equals("")) {
//            presenter.loadRelatedArticle(articleId, nextPage != null ? nextPage : "");
//        }
//
//        bindViews(view);
//
//        loadRelatedPostView();
//        // addItemSeparators();
//        setCardRecyclerAdapter();
//        setRvScrollListener();
//
//        ivBack.setOnClickListener(v -> {
//
//        });
//        return view;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Constants.canAudioPlay = true;
//        prefConfig = new PrefConfig(getContext());
//        textToAudio = new TextToAudioPlayerHelper(getContext());
//        presenter = new NewsPresenter(getActivity(), this);
//        mainPresenter = new MainPresenter(getActivity(), null);
//    }
//
//    @Override
//    public void onStart() {
//        activityVisible = true;
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        activityVisible = false;
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//
//        //Added this in case of opening article from widgets, if u go to background and open another article then headline only breaking.
//        Constants.auto_scroll = true;
//        //Audio overlapping issue from widgets only,
//        Constants.articleId = "";
//        Constants.speech = "";
//        Constants.url = "";
//        Log.d("audiotest", "onStop : stop_destroy");
//        if (goHome != null)
//            goHome.sendAudioEvent("stop_destroy");
//    }
//
//    @Override
//    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        super.onDestroy();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(MessageEvent event) {
//        if (event.getType() == MessageEvent.TYPE_COUNT_API_CALL) {
//            if (mainPresenter != null) {
//                mainPresenter.articleViewCountUpdate(event.getStringData());
//            }
//        }
////        else if (event.getType() == MessageEvent.TYPE_SHOW_DETAIL_VIEW) {
////
////            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
////                return;
////            }
////            mLastClickTime = SystemClock.elapsedRealtime();
////
////            if (activityVisible) {
////
////                Intent intent = new Intent(BulletDetailActivity.this, BulletDetailActivity.class);
////                intent.putExtra("article", (Article) event.getObjectData());
////                startActivity(intent);
////                finish();
////            }
////            //}
////        }
//    }
//
//    private void bindViews(View view) {
//        mListRV = view.findViewById(R.id.recyclerview);
//        ivBack = view.findViewById(R.id.ivBack);
//    }
//
//    private void loadRelatedPostView() {
//        if (swipeListener != null) swipeListener.muteIcon(false);
//        mListRV.setAdapter(null);
//        selectCardPosition(0);
//        contentArrayList.add(new Article("", "DETAILS"));
//        mCardAdapter = new RelatedFeedAdapter(this, contentArrayList, "", true, new DetailsActivityInterface() {
//            @Override
//            public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {
//                if (goHome != null) {
//                    goHome.sendAudioToTempHome(audioCallback, fragTag, "", audio);
//                }
//            }
//
//            @Override
//            public void pause() {
//                MainBulletDetailFragment.this.pause();
//            }
//
//            @Override
//            public void resume() {
//                resetCurrentBullet();
//            }
//        }, goHome, shareToMainInterface, swipeListener, this, onGotoChannelListener);
//
//        if (article != null) {
//            mCardAdapter.setCurrentArticle(article);
//        }
//    }
//
//    public void resetCurrentBullet() {
//
//        try {
//            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//            if (holder != null) {
//                if (holder instanceof VideoViewHolder) {
//                    ((VideoViewHolder) holder).play();
//                } else {
//                    if (mArticlePosition != 0) {
//                        if (mCardAdapter != null)
//                            mCardAdapter.notifyDataSetChanged();
//
//                        nextPosition(mArticlePosition);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setCardRecyclerAdapter() {
//        cardLinearLayoutManager = new SpeedyLinearLayoutManager(this);
//        mListRV.setLayoutManager(cardLinearLayoutManager);
//        mListRV.setOnFlingListener(null);
//        mListRV.setAdapter(mCardAdapter);
//
//        mListRV.setCacheManager(mCardAdapter);
//
//        mListRV.setPlayerSelector(selector);
//    }
//
//    private void addItemSeparators() {
//        int[] ATTRS = new int[]{android.R.attr.listDivider};
//
//        TypedArray a = obtainStyledAttributes(ATTRS);
//        Drawable divider = a.getDrawable(0);
//
//        int inset = getResources().getDimensionPixelSize(R.dimen._15sdp);
//
//        InsetDrawable insetDivider = new InsetDrawable(divider, inset, 0, inset, 0);
//        a.recycle();
//
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(insetDivider);
//        mListRV.addItemDecoration(itemDecoration);
//    }
//
//    private void setRvScrollListener() {
//        mListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                //pause bullet and audio while scrolling
//                if (newState == SCROLL_STATE_DRAGGING) {
//                    if (goHome != null)
//                        goHome.sendAudioEvent("pause");
//                    pause();
//                }
//
//                if (newState == SCROLL_STATE_IDLE) {
//                    Utils.logEvent(MainBulletDetailFragment.this, "swipe_next");
//                    Constants.auto_scroll = true;
//
//                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());
//
//                    final int firstPosition = layoutManager.findFirstVisibleItemPosition();
//
//                    if (firstPosition != -1) {
//
//                        Rect rvRect = new Rect();
//                        mListRV.getGlobalVisibleRect(rvRect);
//
//                        Rect rowRect = new Rect();
//                        layoutManager.findViewByPosition(firstPosition).getGlobalVisibleRect(rowRect);
//
//                        int percentFirst;
//                        if (rowRect.bottom >= rvRect.bottom) {
//                            int visibleHeightFirst = rvRect.bottom - rowRect.top;
//                            percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
//                        } else {
//                            int visibleHeightFirst = rowRect.bottom - rvRect.top;
//                            percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
//                        }
//
//                        if (percentFirst > 100)
//                            percentFirst = 100;
//
//                        int VISIBILITY_PERCENTAGE = 10;
//
//                        int copyOfmArticlePosition = mArticlePosition;
//
//                        Log.d("slections", "onScrollStateChanged: percentFirst = " + percentFirst);
//
//                        /* based on percentage of item visibility, select current or next article
//                         *  if prev position is same as new pos then dont reset the article
//                         * */
//                        if (percentFirst >= VISIBILITY_PERCENTAGE) {
//                            Log.d("slections", "onScrollStateChanged: percentage greater");
//                            mArticlePosition = firstPosition;
//                            if (mArticlePosition == 0) {
//                                Log.d("slections", "onScrollStateChanged: mArticlePosition = 0");
//                                selectCardPosition(mArticlePosition);
//
////                                if (prefConfig.getMenuViewMode().equalsIgnoreCase(MENU_EXTENDED_VIEW)) {
//                                if (mCardAdapter != null)
//                                    mCardAdapter.notifyDataSetChanged();
////                                } else {
////                                    if (mListAdapter != null)
////                                        mListAdapter.notifyDataSetChanged();
////                                }
//                            } else if (mArticlePosition == contentArrayList.size() - 1) {
//                                Log.d("slections", "onScrollStateChanged: last = 0");
//
//                                //on fast scrolling select the last one in the last
//                                selectCardPosition(mArticlePosition);
//
////                                if (prefConfig.getMenuViewMode().equalsIgnoreCase(MENU_EXTENDED_VIEW)) {
//                                if (mCardAdapter != null)
//                                    mCardAdapter.notifyDataSetChanged();
////                                } else {
////                                    if (mListAdapter != null)
////                                        mListAdapter.notifyDataSetChanged();
////                                }
//                            } else if (copyOfmArticlePosition == mArticlePosition) {
//                                Log.d("slections", "onScrollStateChanged: copy = new pos");
//                                //scroll rested on same article so resume audio and bullet
//                                try {
//                                    RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//                                    if (holder != null) {
//                                        if (holder instanceof LargeCardViewHolder) {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("resume");
//                                            ((LargeCardViewHolder) holder).storiesProgressView.resume();
//                                        } else if (holder instanceof SmallCardViewHolder) {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("resume");
//                                            ((SmallCardViewHolder) holder).storiesProgressView.resume();
//                                        } else {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("stop_destroy");
//                                        }
//                                    } else {
//                                        Log.d("audiotest", "scroll : stop_destroy");
//                                        if (goHome != null)
//                                            goHome.sendAudioEvent("stop_destroy");
//                                    }
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                if (copyOfmArticlePosition != mArticlePosition) {
//                                    Log.d("slections", "onScrollStateChanged: select new");
//                                    //scrolled to a new pos, so select new article
//                                    selectCardPosition(mArticlePosition);
//
////                                    if (prefConfig.getMenuViewMode().equalsIgnoreCase(MENU_EXTENDED_VIEW)) {
//                                    if (mCardAdapter != null)
//                                        mCardAdapter.notifyDataSetChanged();
////                                    } else {
////                                        if (mListAdapter != null)
////                                            mListAdapter.notifyDataSetChanged();
////                                    }
//                                }
//                            }
//                        } else {
//                            Log.d("slections", "onScrollStateChanged: percentage less");
//                            mArticlePosition = firstPosition;
//                            mArticlePosition++;
//
//                            if (copyOfmArticlePosition != mArticlePosition) {
//                                Log.d("slections", "onScrollStateChanged: select new");
//                                //scrolled to a new pos, so select new article
//                                selectCardPosition(mArticlePosition);
//
////                                if (prefConfig.getMenuViewMode().equalsIgnoreCase(MENU_EXTENDED_VIEW)) {
//                                if (mCardAdapter != null)
//                                    mCardAdapter.notifyDataSetChanged();
////                                } else {
////                                    if (mListAdapter != null)
////                                        mListAdapter.notifyDataSetChanged();
////                                }
//                            } else {
//                                Log.d("slections", "onScrollStateChanged: else");
//                                //scroll rested on same article so resume audio and bullet
//                                try {
//                                    RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//                                    if (holder != null) {
//                                        if (holder instanceof LargeCardViewHolder) {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("resume");
//                                            ((LargeCardViewHolder) holder).storiesProgressView.resume();
//                                        } else if (holder instanceof SmallCardViewHolder) {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("resume");
//                                            ((SmallCardViewHolder) holder).storiesProgressView.resume();
//                                        } else {
//                                            if (goHome != null)
//                                                goHome.sendAudioEvent("stop_destroy");
//                                        }
//                                    } else {
//                                        Log.d("audiotest", "scroll : stop_destroy");
//                                        if (goHome != null)
//                                            goHome.sendAudioEvent("stop_destroy");
//                                    }
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                int visibleItemCount = cardLinearLayoutManager.getChildCount();
//                int totalItemCount = cardLinearLayoutManager.getItemCount();
//                int firstVisibleItemPosition = cardLinearLayoutManager.findFirstVisibleItemPosition();
//
//                if (!isApiCalling && nextPage != null && !nextPage.equals("")) {
//                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                            && firstVisibleItemPosition >= 0) {
//                        if (articleId != null && !articleId.equals("")) {
//                            presenter.loadRelatedArticle(articleId, nextPage);
//                        }
//                    }
//                }
//
//
//                if (goHome != null) {
//                    if (dy > 0) {
//                        goHome.scrollUp();
//                    } else {
//                        goHome.scrollDown();
//                    }
//                }
////
////                // hide bottom bar while scrolling
////                if (goHomeMainActivity != null) {
////                    if (isVisible && scrollDist > MINIMUM_SCROLL_DIST) {
////                        goHomeMainActivity.scrollUp();
////                        scrollDist = 0;
////                        isVisible = false;
////                    } else if (!isVisible && scrollDist < -MINIMUM_SCROLL_DIST) {
////                        goHomeMainActivity.scrollDown();
////                        scrollDist = 0;
////                        isVisible = true;
////                    }
////
////                    if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
////                        scrollDist += dy;
////                    }
////                }
//
//            }
//        });
//    }
//
//    public void selectCardPosition(int position) {
//        if (position > -1) {
//            mArticlePosition = position;
//            if (mCardAdapter != null)
//                mCardAdapter.setCurrentArticlePosition(mArticlePosition);
//            if (contentArrayList.size() > 0 && position < contentArrayList.size()) {
//                for (int i = 0; i < contentArrayList.size(); i++) {
//                    contentArrayList.get(i).setSelected(false);
//                }
//                //Log.e("isAudioLoaded", "selectCardPosition : " + position + " TAG : " + this.getMyTag());
//                contentArrayList.get(position).setSelected(true);
//            }
//        }
//    }
//
//    public void unSelectAllItems() {
//        if (contentArrayList.size() > 0) {
//            for (int i = 0; i < contentArrayList.size(); i++) {
//                contentArrayList.get(i).setSelected(false);
//            }
//            if (mCardAdapter != null)
//                mCardAdapter.notifyDataSetChanged();
//        }
//    }
//
//    private void playTextAudio(AudioCallback audioCallback, AudioObject audio) {
//        if (goHome != null) {
//            goHome.sendAudioToTempHome(audioCallback, "TempHome", "", audio);
//        }
//    }
//
//    public String getMyTag() {
//        return _myTag;
//    }
//
//    public void setMyTag(String value) {
//        if ("".equals(value))
//            return;
//        _myTag = value;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Utils.checkAppModeColor(this, false);
//
//        Constants.canAudioPlay = true;
//        if (!Constants.muted) {
//            if (goHome != null)
//                goHome.sendAudioEvent("resume");
//        }
//        resetCurrentBullet();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        Constants.canAudioPlay = false;
//        if (goHome != null)
//            goHome.sendAudioEvent("pause");
//        pause();
//    }
//
//    private void pause() {
//        try {
//            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//            if (holder != null) {
//                if (holder instanceof LargeCardViewHolder) {
//                    ((LargeCardViewHolder) holder).storiesProgressView.pause();
//                } else if (holder instanceof SmallCardViewHolder) {
//                    ((SmallCardViewHolder) holder).storiesProgressView.pause();
//                } else if (holder instanceof VideoViewHolder) {
//                    ((VideoViewHolder) holder).pause();
//                } else if (holder instanceof YoutubeViewHolder) {
////                    ((YoutubeViewHolder) holder).bulletPause();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void loaderShow(boolean flag) {
//
//    }
//
//    @Override
//    public void error(String error) {
//
//    }
//
//    @Override
//    public void error404(String error) {
//
//    }
//
//    @Override
//    public void success(ArticleResponse response) {
//        Log.e("@@@@", response.toString());
//
//        if (nextPage == null) {
//            contentArrayList.clear();
//            contentArrayList.add(new Article("", "DETAILS"));
//        }
//        int preCount = contentArrayList.size();
//        contentArrayList.addAll(response.getArticles());
//        mCardAdapter.notifyDataSetChanged();
//        // mCardAdapter.notifyItemRangeChanged(preCount - 1, contentArrayList.size() - preCount);
//
//        nextPage = response.getMeta().getNext();
//    }
//
//    @Override
//    public void successArticle(Article article) {
//        if (mCardAdapter != null && article != null) {
//            mCardAdapter.setCurrentArticle(article);
//        } else {
//            finish();
//        }
//    }
//
//    @Override
//    public void successForYou(ForYou forYou) {
//
//    }
//
//    @Override
//    public void nextPosition(int position) {
//        if (contentArrayList.size() > 0 && position < contentArrayList.size()) {
//            mArticlePosition = position;
//            selectCardPosition(mArticlePosition);
//            if (mArticlePosition == 0) {
//                goHome.sendAudioEvent("stop_destroy");
//            }
//            if (cardLinearLayoutManager != null)
//                cardLinearLayoutManager.scrollToPositionWithOffset(mArticlePosition, 0);
//            if (mCardAdapter != null)
//                mCardAdapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void onItemHeightMeasured(int height) {
//        ViewTreeObserver viewTreeObserver = mListRV.getViewTreeObserver();
//        if (viewTreeObserver.isAlive()) {
//            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    if (mListRV != null) {
//                        mListRV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        int viewHeight = mListRV.getHeight();
//
//                        mListRV.setPadding(0, 0, 0, viewHeight);
//                    }
//                }
//            });
//        }
//    }
//
//    private int statusBarHeight() {
//        Rect rectangle = new Rect();
//        Window window = getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
//        return rectangle.top;
//    }
//
//    @Override
//    public void onChannelItemClicked(TYPE type, String id, String name, boolean favorite) {
//        //Resetting the audio data to avoid old article speech while loading new data
//        Constants.articleId = "";
//        Constants.speech = "";
//        Constants.url = "";
//        Log.d("audiotest", " onitemclick : stop_destroy");
//        goHome.sendAudioEvent("stop_destroy");
//        // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);
//
//        if (type != null && type.equals(TYPE.MANAGE)) {
//            //selectSearch(name);
//        } else if (type != null && id != null && name != null) {
////            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
//            Constants.canAudioPlay = true;
//            Log.e("expandCard", "newInstance 3");
//
//            Intent intent = new Intent(MainBulletDetailFragment.this, ChannelPostActivity.class);
//            intent.putExtra("type", type);
//            intent.putExtra("id", id);
//            intent.putExtra("name", name);
//            intent.putExtra("favorite", favorite);
//            startActivity(intent);
//            // finish();
//        }
//    }
//}