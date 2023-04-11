package com.ziro.bullet.Helper;


//
//public class TempCategoryHelper {
//
//    int lastPosition = -1;
//    private UnifiedNativeAd nativeAd;
//    private onCardListener onCardListener;
//    private Activity activity;
//    private ArrayList<Article> contentArrayList = new ArrayList<>();
//    private CardViewHolder holder;
//    private int layout = R.layout.card_item;
//    private PrefConfig mPrefConfig;
//    private ShareBottomSheet shareBottomSheet;
//    private boolean isGotoFollowShow;
//    private String type;
//    private OnGotoChannelListener gotoChannelListener;
//    private Animation imageAnimationScaleUp;
//    private Animation imageAnimationScaleDown;
//    private Animation progressAnimation;
//    private ShareToMainInterface shareToMainInterface;
//    private CardBulletsAdapter bulletsAdapterMain;
//    private String direction = "";
//    private DetailsActivityInterface listener;
//    private int mCurrentPosition = 0;
//    private RecyclerView.OnScrollListener scrollListener;
//    private int origionalVolume;
//    private AudioManager mAudioManager;
//    private GestureDetector gestureDetector;
//    private boolean mIsScrolling = false;
//    private boolean mIsHold = false;
//    private int CLICK_ACTION_THRESHOLD = 200;
//    private float startX;
//    private float startY;
//    private float opacity = 0.5f;
//    private TempCategorySwipeListener swipeListener;
//    private float yPos = 0;
//    private AbstractYouTubePlayerListener youtubeListener;
//    private YouTubePlayer youTubePlayer;
//    private boolean isPlaying = false;
//
//    public TempCategoryHelper(TempCategorySwipeListener swipeListener, onCardListener onCardListener, Activity activity, ArrayList<Article> contentArrayList, View view, boolean isGotoFollowShow, String type, OnGotoChannelListener gotoChannelListener, ShareToMainInterface shareToMainInterface, DetailsActivityInterface listener) {
//        this.swipeListener = swipeListener;
//        this.onCardListener = onCardListener;
//        this.contentArrayList = contentArrayList;
//        this.activity = activity;
//        this.isGotoFollowShow = isGotoFollowShow;
//        this.type = type;
//        this.listener = listener;
//        this.gotoChannelListener = gotoChannelListener;
//        this.shareToMainInterface = shareToMainInterface;
//        this.mPrefConfig = new PrefConfig(activity);
//        holder = new CardViewHolder(view);
//        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
//        origionalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
////        refreshAd();
//
//        if (mPrefConfig != null) {
//            Static mStatic = mPrefConfig.getHomeImage();
//            if (mStatic != null && mStatic.getHomeImage() != null) {
//                opacity = mStatic.getHomeImage().getOpacity();
//                if (!TextUtils.isEmpty(mStatic.getHomeImage().getLeft())) {
//                    Glide.with(activity)
//                            .load(mStatic.getHomeImage().getLeft())
//                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                            .into(holder.leftArc);
//                } else {
//                    holder.leftArc.setImageResource(R.drawable.tap_left);
//                }
//                if (!TextUtils.isEmpty(mStatic.getHomeImage().getRight())) {
//                    Glide.with(activity)
//                            .load(mStatic.getHomeImage().getRight())
//                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                            .into(holder.rightArc);
//                } else {
//                    holder.rightArc.setImageResource(R.drawable.tap_right);
//                }
//            } else {
//                holder.rightArc.setImageResource(R.drawable.tap_right);
//                holder.leftArc.setImageResource(R.drawable.tap_left);
//            }
//        }
//
//        loadYoutube(null);
//    }
//
//    private void loadYoutube(String link) {
//        holder.youTubePlayerView.enterFullScreen();
//        holder.youTubePlayerView.addYouTubePlayerListener(youtubeListener = new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NotNull YouTubePlayer youTubePlayer1) {
//                super.onReady(youTubePlayer1);
//                youTubePlayer = youTubePlayer1;
//                if (!TextUtils.isEmpty(link)) {
//                    loadYoutubeUrl(link);
//                }
//            }
//        });
//    }
//
//    public void setData(ArrayList<Article> contentArrayList) {
//        this.contentArrayList = contentArrayList;
//        if (contentArrayList != null && contentArrayList.size() > 0) {
//            for (int i = 0; i < contentArrayList.size(); i++) {
//                if (contentArrayList.get(i) != null && contentArrayList.get(i).getBullets() != null && contentArrayList.get(i).getBullets().size() > 0)
//                    Glide.with(BulletApp.getInstance())
//                            .load(contentArrayList.get(i).getBullets().get(0).getImage())
//                            .preload();
//            }
//        }
////        refreshAd();
//    }
//
//    public void loadYoutubeUrl(String link) {
//        String url = link;
//        if (!TextUtils.isEmpty(link)) {
//            if (youTubePlayer != null) {
//                youTubePlayer.loadVideo(url, 0);
//                youTubePlayer.unMute();
//                youTubePlayer.pause();
//                isPlaying = false;
//            } else {
//                loadYoutube(url);
//            }
//        }
//    }
//
//    public void loadImage(int position) {
//        Article currentArticle = contentArrayList.get(position);
//        if (currentArticle.getBullets() != null && currentArticle.getBullets().size() > 0) {
//            if (!TextUtils.isEmpty(currentArticle.getBullets().get(0).getImage())) {
//                Glide.with(BulletApp.getInstance())
//                        .load(currentArticle.getBullets().get(0).getImage())
//                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                        .transition(DrawableTransitionOptions.withCrossFade())
//                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
//                        .override(Constants.targetWidth, Constants.targetHeight)
//                        .into(holder.odd_imageBack);
//
//                Glide.with(BulletApp.getInstance())
//                        .load(currentArticle.getBullets().get(0).getImage())
//                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                        .transition(DrawableTransitionOptions.withCrossFade())
//                        .listener(new RequestListener<Drawable>() {
//                            @Override
//                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                holder.odd_image.setImageResource(R.drawable.img_place_holder);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                return false;
//                            }
//
//                        })
//                        .error(R.drawable.img_place_holder)
//                        .override(Constants.targetWidth, Constants.targetHeight)
//                        .into(holder.odd_image);
//
//            } else {
//                holder.odd_image.setImageResource(R.drawable.img_place_holder);
//            }
//        } else {
//            holder.odd_image.setImageResource(R.drawable.img_place_holder);
//        }
//    }
//
//    public void hideArticleView() {
//        mCurrentPosition = 0;
//        holder.leftRightPanel.setVisibility(View.GONE);
////        holder.article.setVisibility(View.GONE);
//        holder.touch.setVisibility(View.GONE);
//        holder.ad_mob.setVisibility(View.GONE);
//    }
//
//    public void setCardData() {
//        if (mCurrentPosition == contentArrayList.size() - 5) {
//            if (onCardListener != null) {
//                onCardListener.callNextData();
//            }
//        }
////        refreshAd();
//        selectCardPosition(mCurrentPosition);
//        if (contentArrayList != null && contentArrayList.size() > 0 && mCurrentPosition < contentArrayList.size()) {
//            Article content = contentArrayList.get(mCurrentPosition);
//
//            if (content != null) {
//
//                Log.e("TOTILE", "=============================================");
//                Log.e("TOTILE", "orignal : " + new Gson().toJson(content));
//
//                switch (content.getTitle()) {
//                    case "G_Ad": {
//                        holder.fl_adplaceholder.removeAllViews();
//                        if (swipeListener != null)
//                            swipeListener.swipe(true);
////                        Utils.broadcastIntent(activity, "stop_destroy", INTENT_ACTION_AUDIO);
//                        holder.storiesProgressView.pause();
////                        holder.storiesProgressView.setStoriesCount(1);
//
//                        holder.ad_mob.setVisibility(View.VISIBLE);
////                        holder.article.setVisibility(View.GONE);
//                        holder.leftRightPanel.setVisibility(View.GONE);
//                        holder.touch.setVisibility(View.GONE);
//                        holder.button.setOnClickListener(v -> nextArticle());
////                        refreshAd();
//                    }
//                    break;
//                    default: {
//                        if (swipeListener != null)
//                            swipeListener.swipe(false);
//                        holder.ad_mob.setVisibility(View.GONE);
//                        holder.leftRightPanel.setVisibility(View.VISIBLE);
////                        holder.article.setVisibility(View.VISIBLE);
//                        holder.touch.setVisibility(View.VISIBLE);
////                        holder.youTubePlayerView.release();
//                        stopYoutube();
//
//                        if (content.getType() != null && content.getType().equalsIgnoreCase("YOUTUBE")) {
//
////                            Utils.broadcastIntent(activity, "stop_destroy", INTENT_ACTION_AUDIO);
//                            holder.storiesProgressView.pause();
//                            holder.storiesProgressView.setStoriesCount(1);
//                            holder.storiesProgressView.getLayoutParams().height = 0;
//                            holder.storiesProgressView.requestLayout();
////                            holder.speaker.setVisibility(View.INVISIBLE);
//                            holder.play.setVisibility(View.VISIBLE);
//                            holder.odd_imageBack.setVisibility(View.VISIBLE);
//                            holder.odd_image.setVisibility(View.VISIBLE);
//                            holder.youtubeMain.setVisibility(View.GONE);
////                            //Gesture View
////                            holder.touch.getLayoutParams().height = activity.getResources().getDimensionPixelSize(R.dimen._200sdp);
////                            holder.touch.requestLayout();
//
//                            loadImage(mCurrentPosition);
//                            loadYoutubeUrl(content.getLink());
//                        } else {
//
//                            holder.storiesProgressView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                            holder.storiesProgressView.requestLayout();
//                            yPos = 0;
//                            holder.storiesProgressView.setTranslationY(yPos);
//
//                            loadImage(mCurrentPosition);
//                            holder.play.setVisibility(View.GONE);
//                            holder.odd_imageBack.setVisibility(View.VISIBLE);
//                            holder.odd_image.setVisibility(View.VISIBLE);
//                            holder.youtubeMain.setVisibility(View.GONE);
////                            holder.speaker.setVisibility(View.VISIBLE);
//
////                            //Gesture View
////                            holder.touch.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
////                            holder.touch.requestLayout();
//                        }
//
//                        String time = Utils.getTimeAgo(Utils.getDate(content.getPublishTime()));
//                        if (!TextUtils.isEmpty(time)) {
//                            holder.time.setText(time);
//                        }
//                        if (content.getSource() != null) {
//                            holder.source_name.setText(content.getSource().getName());
//                            Glide.with(BulletApp.getInstance())
//                                    .load(content.getSource().getIcon())
//                                    .circleCrop()
//                                    .into(holder.profile);
//                        }
//
//                        holder.dots.setOnClickListener(v -> {
//                            Utils.logEvent(activity, "share_click");
//                            bulletPause();
//
//                            share_msg(content.getId(), new ShareInfoInterface() {
//                                @Override
//                                public void response(ShareInfo shareInfo) {
//                                    showBottomSheetDialog(shareInfo, mCurrentPosition, content, new DialogInterface.OnDismissListener() {
//                                        @Override
//                                        public void onDismiss(DialogInterface dialog) {
//                                            bulletResume();
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void error(String error) {
//
//                                }
//                            });
//                        });
//
//                        holder.profile.setOnClickListener(v -> {
//                            detailsPage(content);
//                        });
//                        holder.source_name.setOnClickListener(v -> {
//                            detailsPage(content);
//                        });
//                        holder.time.setOnClickListener(v -> {
//                            detailsPage(content);
//                        });
//
//                        final int[] mPosition = {0};
//                        if (content.getBullets() != null && content.getBullets().size() > 0) {
//                            ArrayList<Bullet> bulletList = new ArrayList<>();
//                            bulletList.addAll(content.getBullets());
//                            bulletList.add(new Bullet());
//                            bulletList.add(new Bullet());
//                            bulletList.add(new Bullet());
//                            bulletList.add(new Bullet());
//                            bulletList.add(new Bullet());
//                            bulletList.add(new Bullet());
//                            bulletList.add(new Bullet());
//                            bulletList.add(new Bullet());
//                            bulletList.add(new Bullet());
//                            bulletList.add(new Bullet());
//
//                            CardBulletsAdapter bulletsAdapter = new CardBulletsAdapter(new ListAdapterListener() {
//                                @Override
//                                public void verticalScrollList(boolean isEnable) {
//
//                                }
//
//                                @Override
//                                public void nextArticle(int position) {
//
//                                }
//
//                                @Override
//                                public void prevArticle(int position) {
//
//                                }
//
//                                @Override
//                                public void clickArticle(int position) {
//
//                                }
//                            }, activity, bulletList, layout);
//                            ViewTreeObserver viewTreeObserver = holder.getViewTreeObserver();
//                            if (viewTreeObserver.isAlive()) {
//                                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                    @Override
//                                    public void onGlobalLayout() {
//                                        holder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                                        int viewHeight = holder.getHeight();
//                                        mPrefConfig.setListHeight(viewHeight);
//                                    }
//                                });
//                            }
//
//
//                            CardSpeedyLinearLayoutManager manager = new CardSpeedyLinearLayoutManager(activity, RecyclerView.VERTICAL, false);
//                            holder.setLayoutManager(manager);
//                            holder.setAdapter(bulletsAdapter);
//
//                            //on bottom nav change scroll position was retained for current visible article, to tackle this manually scrolling to 1st pos
//                            holder.scrollToPosition(mPosition[0]);
//
//                            gestureDetector = new GestureDetector(activity, new GestureDetector.OnGestureListener() {
//                                @Override
//                                public boolean onDown(MotionEvent e) {
//                                    return false;
//                                }
//
//                                @Override
//                                public void onShowPress(MotionEvent e) {
//                                }
//
//                                @Override
//                                public boolean onSingleTapUp(MotionEvent e) {
//                                    return false;
//                                }
//
//                                @Override
//                                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                                    mIsScrolling = true;
//                                    return true;
//                                }
//
//
//                                @Override
//                                public void onLongPress(MotionEvent e) {
//                                    mIsHold = true;
//                                    bulletPause();
//                                }
//
//                                @Override
//                                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                                    return false;
//                                }
//                            });
//
//
//                            holder.touch.setOnTouchListener((v, event) -> {
//                                return bulletScroller(event, bulletsAdapter, content);
//                            });
//
//                            bulletsAdapterMain = bulletsAdapter;
//
//                            Log.e("PAGERS", "===========================================");
//                            Log.e("PAGERS", "IsSelected PAGE : " + content.isSelected());
//                            Log.e("PAGERS", "Bullet position : " + bulletsAdapter.getCurrentPosition());
//                            Log.e("PAGERS", "ARTICLE POSITION : " + mCurrentPosition);
//                            Log.e("PAGERS", "Frag & Page Tag : " + content.getFragTag() + " === " + "id_" + Constants.visiblePage);
//
//                            if (!TextUtils.isEmpty(content.getFragTag()) && content.isSelected() && content.getFragTag().equalsIgnoreCase("id_" + Constants.visiblePage)) {
//
//                                holder.addOnScrollListener(scrollListener = new RecyclerView.OnScrollListener() {
//                                    @Override
//                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                                        super.onScrollStateChanged(recyclerView, newState);
//                                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                                            if (mPosition[0] > -1) {
//
//                                                final int firstPosition = manager.findFirstVisibleItemPosition();
//
//                                                if (firstPosition != -1) {
//
//                                                    Rect rvRect = new Rect();
//                                                    holder.getGlobalVisibleRect(rvRect);
//
//                                                    Rect rowRect = new Rect();
//                                                    manager.findViewByPosition(firstPosition).getGlobalVisibleRect(rowRect);
//
//                                                    int percentFirst;
//                                                    if (rowRect.bottom >= rvRect.bottom) {
//                                                        int visibleHeightFirst = rvRect.bottom - rowRect.top;
//                                                        percentFirst = (visibleHeightFirst * 100) / manager.findViewByPosition(firstPosition).getHeight();
//                                                    } else {
//                                                        int visibleHeightFirst = rowRect.bottom - rvRect.top;
//                                                        percentFirst = (visibleHeightFirst * 100) / manager.findViewByPosition(firstPosition).getHeight();
//                                                    }
//
//                                                    if (percentFirst > 100)
//                                                        percentFirst = 100;
//
//                                                    //percentage is from bottom of item
//                                                    if (percentFirst < 20 && percentFirst > 0) {
//                                                        mPosition[0]++;
//                                                    }
//
//                                                    if ((percentFirst != 100 && percentFirst != 0) || firstPosition == 0 && !TextUtils.isEmpty(bulletList.get(firstPosition).getImage())) {
//                                                        lastPosition = mPosition[0];
////                                                        new Handler().postDelayed(() -> {
//                                                        if (holder.desc_list != null) {
//                                                            holder.smoothScrollToPosition(mPosition[0]);
//                                                        }
////                                                        }, 500);
//                                                    }
//                                                }
//
//                                                if (!TextUtils.isEmpty(direction)) {
//                                                    switch (direction) {
//                                                        case "rtl":
//                                                            Utils.logEvent(activity, "article_swipe_right");
//                                                            break;
//                                                        case "ltr":
//                                                            Utils.logEvent(activity, "article_swipe_left");
//                                                            break;
//                                                    }
//                                                }
//
//                                                bulletsAdapter.setCurrentPosition(mPosition[0]);
//                                                int currentPosition = bulletsAdapter.getCurrentPosition();
//                                                if (currentPosition < content.getBullets().size()) {
//
//                                                    long duration = 0;
//                                                    if (content.getBullets().get(currentPosition).getDuration() == 0)
//                                                        duration = content.getBullets().get(currentPosition).getData().length() * 100;
//                                                    else {
//                                                        duration = (long) (content.getBullets().get(currentPosition).getDuration() / Constants.READING_SPEED_RATES.get(Constants.reading_speed));
//                                                    }
//                                                    duration = duration + 1500;
//
//                                                    if (Constants.muted || content.isMute()) {
//
//                                                        holder.progressBar.setVisibility(View.GONE);
//                                                        holder.storiesProgressView.setStoryDuration(duration);
//                                                        if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
//                                                            holder.storiesProgressView.startStories(currentPosition);
//                                                        } else {
//                                                            holder.storiesProgressView.selectStory(currentPosition);
//                                                        }
//
//                                                        listener.playAudio(null, content.getFragTag(), new AudioObject(content.getId(),
//                                                                content.getBullets().get(currentPosition).getData(),
//                                                                content.getBullets().get(currentPosition).getAudio(),
//                                                                currentPosition, duration));
//
//                                                    } else {
//
//                                                        holder.progressBar.setVisibility(View.VISIBLE);
//
//                                                        long finalDuration = duration;
//                                                        listener.playAudio(new AudioCallback() {
//                                                            @Override
//                                                            public void isAudioLoaded(boolean isLoaded) {
//                                                                Log.e("isAudioLoaded", "isLoaded = " + isLoaded);
//                                                                if (isLoaded) {
//                                                                    holder.progressBar.setVisibility(View.GONE);
//                                                                    holder.storiesProgressView.setStoryDuration(finalDuration);
//                                                                    if (mPrefConfig.getMenuNarration() != null &&
//                                                                            !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
//                                                                        holder.storiesProgressView.startStories(currentPosition);
//                                                                    } else {
//                                                                        holder.storiesProgressView.selectStory(currentPosition);
//                                                                    }
//                                                                } else {
//                                                                    holder.progressBar.setVisibility(View.VISIBLE);
//                                                                }
//                                                            }
//
//                                                            @Override
//                                                            public void isAudioComplete(boolean isCompleted) {
//
//                                                            }
//                                                        }, content.getFragTag(), new AudioObject(content.getId(),
//                                                                content.getBullets().get(currentPosition).getData(),
//                                                                content.getBullets().get(currentPosition).getAudio(),
//                                                                currentPosition,
//                                                                duration));
//                                                    }
//                                                } else {
//                                                    nextArticle();
//                                                    Utils.logEvent(activity, "expand_next_scroll");
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                                        super.onScrolled(recyclerView, dx, dy);
//                                        mPosition[0] = manager.findFirstVisibleItemPosition();
//                                        if (dx > 0) {
//                                            direction = "rtl";
//                                        } else {
//                                            direction = "ltr";
//                                        }
//                                    }
//                                });
//
//
//                                if (content.getType().equalsIgnoreCase("IMAGE")) {
//
////                                    if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.AUTO)) {
////                                        switch (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
////                                            case Configuration.UI_MODE_NIGHT_YES:
////                                                Constants.ProgressBarDrawable = R.drawable.bullet_selector;
////                                                Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_2;
////                                                break;
////                                            case Configuration.UI_MODE_NIGHT_NO:
////                                                Constants.ProgressBarDrawable = R.drawable.bullet_selector_green;
////                                                Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_green_2;
////                                                break;
////                                        }
////                                    } else if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
////                                        Constants.ProgressBarDrawable = R.drawable.bullet_selector;
////                                        Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_2;
////                                    } else {
////                                        Constants.ProgressBarDrawable = R.drawable.bullet_selector_green;
////                                        Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_green_2;
////                                    }
//
//                                    Constants.ProgressBarDrawable = R.drawable.bullet_selector_green;
//                                    Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_green_2;
//
//                                    holder.storiesProgressView.pause();
//                                    holder.storiesProgressView.setStoriesCount(content.getBullets().size());
//                                    holder.storiesProgressView.setProgressHeight(Constants.mProgressCollapseSize, false);
//                                    holder.storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
//                                        @Override
//                                        public void onFirst() {
//                                            Log.e("playAudio", "onFirst : " + bulletsAdapter.getCurrentPosition());
//                                        }
//
//                                        @Override
//                                        public void onNext() {
//                                            if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
//                                                //GOTO NEXT BULLET
//                                                int pos = bulletsAdapter.getCurrentPosition();
//                                                nextBullet(pos, bulletsAdapter);
//                                            } else {
//                                                //Auto Swipe
//                                                if (Constants.auto_scroll) {
//                                                    Utils.logEvent(activity, "expand_next_auto");
//                                                    //GOTO NEXT ARTICLE
//                                                    nextArticle();
//                                                } else {
//                                                    //GOTO NEXT BULLET
//                                                    int pos = bulletsAdapter.getCurrentPosition();
//                                                    nextBullet(pos, bulletsAdapter);
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onPrev() {
//
//                                        }
//
//                                        @Override
//                                        public void onComplete() {
//                                            //Auto Swipe
//                                            if (Constants.auto_scroll) {
//                                                Utils.logEvent(activity, "expand_next_auto");
//                                            } else {
//                                                Utils.logEvent(activity, "expand_next_scroll");
//                                            }
//                                            Constants.auto_scroll = true;
//                                            nextArticle();
//                                        }
//                                    });
//
//                                    holder.odd_image.startAnimation(imageAnimationScaleUp);
//                                    progressAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in_progress);
//                                    holder.storiesProgressView.setVisibility(View.VISIBLE);
//
//                                    holder.storiesProgressView.startAnimation(progressAnimation);
//                                    progressAnimation.setAnimationListener(new Animation.AnimationListener() {
//                                        @Override
//                                        public void onAnimationStart(Animation animation) {
//
//                                        }
//
//                                        @Override
//                                        public void onAnimationEnd(Animation animation) {
//                                            scrollListener.onScrollStateChanged(holder.desc_list, RecyclerView.SCROLL_STATE_IDLE);
//                                        }
//
//                                        @Override
//                                        public void onAnimationRepeat(Animation animation) {
//
//                                        }
//                                    });
//                                    imageAnimationScaleUp.setAnimationListener(new Animation.AnimationListener() {
//                                        @Override
//                                        public void onAnimationStart(Animation animation) {
//
//                                        }
//
//                                        @Override
//                                        public void onAnimationEnd(Animation animation) {
//                                            holder.odd_image.startAnimation(imageAnimationScaleDown);
//                                        }
//
//                                        @Override
//                                        public void onAnimationRepeat(Animation animation) {
//
//                                        }
//                                    });
//                                    imageAnimationScaleDown.setAnimationListener(new Animation.AnimationListener() {
//                                        @Override
//                                        public void onAnimationStart(Animation animation) {
//
//                                        }
//
//                                        @Override
//                                        public void onAnimationEnd(Animation animation) {
//                                            holder.odd_image.startAnimation(imageAnimationScaleUp);
//                                        }
//
//                                        @Override
//                                        public void onAnimationRepeat(Animation animation) {
//
//                                        }
//                                    });
//                                } else {
//                                    holder.storiesProgressView.setStoriesListener(null);
//                                    holder.storiesProgressView.destroy();
//                                    holder.storiesProgressView.setVisibility(View.VISIBLE);
//                                    holder.scrollToPosition(0);
//                                }
//
//                                if (gotoChannelListener != null) {
//                                    gotoChannelListener.onArticleSelected(content);
//                                }
//
//                                Constants.mute_disable = content.isMute();
//                                if (Constants.mute_disable) {
//                                    Utils.broadcastIntent(activity, "mute", INTENT_ACTION_AUDIO);
//                                } else {
//                                    if (!Constants.muted) {
//                                        Utils.broadcastIntent(activity, "unmute", INTENT_ACTION_AUDIO);
//                                    } else {
//                                        Utils.broadcastIntent(activity, "mute", INTENT_ACTION_AUDIO);
//                                    }
//                                }
//                                    updateMuteButtons();
//
////                                holder.speaker.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View v) {
////                                        if (Constants.mute_disable) {
////                                            Utils.showSnacky(holder.speaker, activity.getString(R.string.speech_not_available));
////                                            return;
////                                        }
////                                        if (!Constants.muted) {
////                                            Utils.broadcastIntent(activity, "mute", INTENT_ACTION_AUDIO);
////                                            holder.speaker.setImageResource(R.drawable.ic_mute);
////                                            Utils.logEvent(activity, "mute");
////                                        } else {
////                                            if (origionalVolume == 0) {
////                                                Toast.makeText(activity, "" + activity.getString(R.string.please_turn_the_volume_up), Toast.LENGTH_SHORT).show();
////                                            }
////                                            holder.speaker.setImageResource(R.drawable.ic_unmute);
//////
////                                            Utils.broadcastIntent(activity, "unmute", INTENT_ACTION_AUDIO);
////                                            Utils.broadcastIntent(activity, "play", INTENT_ACTION_AUDIO);
////
////                                            scrollListener.onScrollStateChanged(holder.desc_list, RecyclerView.SCROLL_STATE_IDLE);
////                                            Utils.logEvent(activity, "unmute");
////                                        }
////                                        Constants.muted = !Constants.muted;
////                                    }
////                                });
//
//                            } else {
//                                holder.storiesProgressView.setStoriesListener(null);
//                                holder.storiesProgressView.destroy();
//                                holder.storiesProgressView.setVisibility(View.VISIBLE);
//                                holder.scrollToPosition(0);
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void detailsPage(Article content) {
//        if (content != null) {
//            if (isGotoFollowShow && !type.equalsIgnoreCase("ARCHIVE")) {
//                if (gotoChannelListener != null && content.getSource() != null) {
//                    if (mPrefConfig != null) {
//                        mPrefConfig.setSrcLang(content.getSource().getLanguage());
//                        mPrefConfig.setSrcLoc(content.getSource().getCategory());
//                    }
//                    gotoChannelListener.onItemClicked(TYPE.SOURCE, content.getSource().getId(), content.getSource().getName(), content.getSource().isFavorite());
//                }
//            }
//        }
//    }
//
//    private void nextBullet(int pos, CardBulletsAdapter bulletsAdapter) {
//        yPos = yPos + (Constants.mProgressCollapseSize + activity.getResources().getDimensionPixelSize(R.dimen.progress_bar_height));
//        holder.storiesProgressView.animate().translationY(-yPos);
//        pos++;
//        holder.smoothScrollToPosition(pos);
//        bulletsAdapter.setCurrentPosition(pos);
//    }
//
//    private void prevBullet(int pos, CardBulletsAdapter bulletsAdapter) {
//        yPos = yPos - (Constants.mProgressCollapseSize + activity.getResources().getDimensionPixelSize(R.dimen.progress_bar_height));
//        holder.storiesProgressView.animate().translationY(-yPos);
//        pos--;
//        holder.smoothScrollToPosition(pos);
//        bulletsAdapter.setCurrentPosition(pos);
//    }
//
//    public void bulletResume() {
//        if (holder != null && activity != null) {
//            holder.storiesProgressView.resume();
//            Utils.broadcastIntent(activity, "resume", INTENT_ACTION_AUDIO);
//        }
//    }
//
//    public void bulletPause() {
//        if (holder != null && activity != null) {
//            holder.storiesProgressView.pause();
//            Utils.broadcastIntent(activity, "pause", INTENT_ACTION_AUDIO);
//        }
//    }
//
//    private int getScreenWidth(Context context) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        return displayMetrics.widthPixels;
//    }
//
//    private int getScreenHeight(Context context) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        return displayMetrics.heightPixels;
//    }
//
//    private boolean bulletScroller(MotionEvent event, CardBulletsAdapter bulletsAdapter, Article content) {
//        if (gestureDetector.onTouchEvent(event)) {
//            return true;
//        }
//
//        int left_swipe = (int) (getScreenWidth(activity) * 0.10);  // left 10%
//        int _80_screen = (int) (getScreenWidth(activity) * 0.80);
//        int right_swipe = left_swipe + _80_screen;  // right 10%
//
//        int left = (int) (getScreenWidth(activity) * 0.35);
//        int center = (int) (getScreenWidth(activity) * 0.30);
//        int right = left + center;
//
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            float endX = event.getX();
//            float endY = event.getY();
//            if (isAClick(startX, endX, startY, endY)) {
//                if (endX < left) {
//                    //Left Click
//                    prevArticle();
//                } else if (endX > right) {
//                    //Right Click
//                    nextArticle();
//                } else {
//                    int click = (holder.touch.getHeight() / 2) + 10;
//                    Log.e("YAXIS", "-------------------");
//                    Log.e("YAXIS", "H - " + click);
//                    Log.e("ytftyfhg", "LINK: " + content.getLink());
//                    if (endY < click) {
//                        //Center Click
//                        if (content.getType().equalsIgnoreCase("YOUTUBE")) {
//                            holder.play.setVisibility(View.GONE);
//                            holder.odd_imageBack.setVisibility(View.GONE);
//                            holder.odd_image.setVisibility(View.GONE);
//                            holder.youtubeMain.setVisibility(View.VISIBLE);
//                            if (!isPlaying) {
//                                if (youTubePlayer != null) {
//                                    youTubePlayer.play();
//                                    isPlaying = true;
//                                }
//                            } else {
//                                if (youTubePlayer != null) {
//                                    youTubePlayer.pause();
//                                    isPlaying = false;
//                                }
//                            }
//                        } else if (!content.getTitle().equalsIgnoreCase("G_Ad")) {
//                            Utils.logEvent(activity, "article_open");
//                            Intent intent = new Intent(activity, WebViewActivity.class);
//                            intent.putExtra("title", content.getSource().getName());
//                            intent.putExtra("url", content.getLink());
//                            activity.startActivity(intent);
//                        }
//                    }
//                }
//            } else {
//                //Hold
//                if (mIsHold) {
//                    bulletResume();
//                    mIsHold = false;
//                }
//                //Scroll
//                else if (mIsScrolling) {
//                    mIsScrolling = false;
//                    Constants.auto_scroll = false;
//                    if (bulletsAdapter != null) {
//                        int pos = bulletsAdapter.getCurrentPosition();
//                        Utils.Direction direction = Utils.getDirection(startX, startY, endX, endY);
//                        if (direction != null) {
//                            switch (direction.ordinal()) {
//                                case 0: // up
//                                    //GOTO NEXT BULLET
//                                    if (pos < content.getBullets().size()) {
//                                        nextBullet(pos, bulletsAdapter);
//                                    }
//                                    break;
//                                case 1: // down
//                                    //GOTO PREV BULLET
//                                    if (pos > 0) {
//                                        prevBullet(pos, bulletsAdapter);
//                                    } else {
//                                        if (pos == 0) {
//                                            prevArticle();
//                                        }
//                                    }
//                                    break;
//                                case 2: // left
//                                    nextArticle();
//                                    break;
//                                case 3: // right
//                                    prevArticle();
//                                    break;
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            startX = event.getX();
//            startY = event.getY();
//
//            if (startX < left_swipe || startX > right_swipe) {
//                if (swipeListener != null) {
//                    swipeListener.swipe(false);
//                }
//            } else {
//                if (swipeListener != null) {
//                    swipeListener.swipe(true);
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean isAClick(float startX, float endX, float startY, float endY) {
//        if (!mIsHold) {
//            float differenceX = Math.abs(startX - endX);
//            float differenceY = Math.abs(startY - endY);
//            return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
//        } else {
//            return false;
//        }
//    }
//
//    private void startAudio() {
//        Utils.broadcastIntent(activity, "unmute", INTENT_ACTION_AUDIO);
//        Utils.broadcastIntent(activity, "play", INTENT_ACTION_AUDIO);
//    }
//
//    public void updateMuteButtons() {
////        if (!Constants.mute_disable) {
////            holder.speaker.setAlpha(1f);
////            if (!Constants.muted) {
////                holder.speaker.setImageResource(R.drawable.ic_unmute);
////            } else {
////                holder.speaker.setImageResource(R.drawable.ic_mute);
////            }
////        } else {
////            holder.speaker.setAlpha(0.5f);
////        }
//    }
//
//    public void stopYoutube() {
//        if (youTubePlayer != null) {
//            youTubePlayer.pause();
//            if (youtubeListener != null)
//                youTubePlayer.removeListener(youtubeListener);
//        }
//    }
//
//    public void prevArticle() {
//        yPos = 0;
//        holder.storiesProgressView.animate().translationY(-yPos);
//        crossFadeAnimation(holder.leftArc, holder.leftArc, 300);
//        Utils.doHaptic(mPrefConfig);
//        if (mCurrentPosition > 0) {
//            mCurrentPosition--;
//            setCardData();
//        } else {
//            Toast toast = Toast.makeText(activity, activity.getString(R.string.you_have_reached_the_start), Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            if (!toast.getView().isShown())
//                toast.show();
//        }
//    }
//
//    public void nextArticle() {
//        yPos = 0;
//        holder.storiesProgressView.animate().translationY(-yPos);
//        crossFadeAnimation(holder.rightArc, holder.rightArc, 300);
//        Utils.doHaptic(mPrefConfig);
//        if (mCurrentPosition < contentArrayList.size()) {
//            int temp = mCurrentPosition; // need last position for ad
//            mCurrentPosition++;
//            setCardData();
//
//            //Removing skip ad
//            if (temp < contentArrayList.size() && temp > -1) {
//                if (contentArrayList.get(temp).getTitle().equalsIgnoreCase("G_Ad")) {
//                    contentArrayList.remove(temp);
//                    mCurrentPosition--;
//                }
//            }
//
//            //Pre load Ad
//            int tempPreLoad = mCurrentPosition;
////            tempPreLoad++;
//            if (tempPreLoad < contentArrayList.size() && tempPreLoad > -1) {
//                if (contentArrayList.get(tempPreLoad).getTitle().equalsIgnoreCase("G_Ad")) {
//                    refreshAd();
//                }
//            }
//
//        } else {
//            if (TextUtils.isEmpty(onCardListener.nextPage())) {
//                Toast toast = Toast.makeText(activity, activity.getString(R.string.you_have_reached_the_end), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                if (!toast.getView().isShown())
//                    toast.show();
//            }
//        }
//    }
//
//    private void selectCardPosition(int position) {
//        if (contentArrayList.size() > 0 && position < contentArrayList.size() && position != -1) {
//            for (int i = 0; i < contentArrayList.size(); i++) {
//                contentArrayList.get(i).setSelected(false);
//            }
//            Log.e("selectCardPosition", "selectCardPosition : " + position);
//            contentArrayList.get(position).setSelected(true);
//        }
//    }
//
//    private void showBottomSheetDialog(ShareInfo shareInfo, int position, Article article, DialogInterface.OnDismissListener onDismissListener) {
//        if (shareBottomSheet == null) {
//            shareBottomSheet = new ShareBottomSheet(activity, shareToMainInterface, isGotoFollowShow, type);
//        }
//        shareBottomSheet.show(article, onDismissListener, position, shareInfo);
//    }
//
//    private void showDialog(String video) {
//        FragmentTransaction fragmentTransaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
//        Fragment previous = ((FragmentActivity) activity).getSupportFragmentManager().findFragmentByTag(YoutubeDialogFragment.class.getName());
//        if (previous != null) {
//            fragmentTransaction.remove(previous);
//        }
//        fragmentTransaction.addToBackStack(null);
//        YoutubeDialogFragment dialog = new YoutubeDialogFragment(new YoutubeCallback() {
//            @Override
//            public void onPause() {
//                bulletPause();
//            }
//
//            @Override
//            public void onResume() {
//                bulletResume();
//            }
//        }, video);
//        dialog.show(((FragmentActivity) activity).getSupportFragmentManager(), "");
//    }
//
//    public void share_msg(String id, ShareInfoInterface listener) {
//        try {
//            if (!InternetCheckHelper.isConnected()) {
//                if (listener != null) {
//                    listener.error("");
//                }
//                return;
//            } else {
//                Call<ResponseBody> call = ApiClient
//                        .getInstance(activity)
//                        .getApi()
//                        .share_msg("Bearer " + mPrefConfig.getAccessToken(), id);
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
//                        if (response != null) {
//                            if (response.code() == 200) {
//                                if (response.body() != null) {
//                                    ShareInfo shareInfo = null;
//                                    try {
//                                        shareInfo = new Gson().fromJson(response.body().string(), ShareInfo.class);
//                                        Log.e("RESP", "" + shareInfo.getShare_message());
//                                        if (listener != null) {
//                                            listener.response(shareInfo);
//                                        }
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                }
//                            } else if (response.code() == 400) {
//                                try {
//                                    if (response.errorBody() != null) {
//                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                                        JSONObject errors = jsonObject.getJSONObject("errors");
//                                        String msg = errors.getString("message");
//                                        Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
//                                        if (listener != null) {
//                                            listener.error(msg);
//                                        }
//                                    }
//                                } catch (JSONException | IOException e) {
//                                    e.printStackTrace();
//                                    if (listener != null) {
//                                        listener.error("");
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
//                        if (listener != null) {
//                            listener.error("");
//                        }
//                    }
//                });
//            }
//        } catch (Exception t) {
//            if (listener != null) {
//                listener.error("");
//            }
//        }
//    }
//
//    private void refreshAd() {
//        Log.e("refreshAd", "refreshAd : ");
//        if (holder != null) {
//            AdLoader.Builder builder = null;
//            if (BuildConfig.DEBUG)
//                builder = new AdLoader.Builder(activity, Constants.ADMOB_AD_UNIT_ID);
//            else {
//                if (mPrefConfig.getAds() != null)
//                    builder = new AdLoader.Builder(activity, mPrefConfig.getAds().getKey());
//            }
//
//            if (builder != null) {
//                builder.forUnifiedNativeAd(unifiedNativeAd -> {
//                    if (nativeAd != null) {
//                        nativeAd.destroy();
//                    }
//                    nativeAd = unifiedNativeAd;
//                    UnifiedNativeAdView adView = (UnifiedNativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_expand_card, null);
//                    populateUnifiedNativeAdView(nativeAd, adView);
//                    holder.fl_adplaceholder.removeAllViews();
//                    holder.fl_adplaceholder.addView(adView);
//                });
//
//                VideoOptions videoOptions = new VideoOptions.Builder()
//                        .setStartMuted(Constants.muted)
//                        .build();
//                NativeAdOptions adOptions = new NativeAdOptions.Builder()
//                        .setVideoOptions(videoOptions)
//                        .build();
//                builder.withNativeAdOptions(adOptions);
//                AdLoader adLoader = builder.withAdListener(new AdListener() {
//                    @Override
//                    public void onAdLoaded() {
//                        super.onAdLoaded();
//                        Log.d("TAG", "onAdLoaded() called");
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(LoadAdError loadAdError) {
//                        super.onAdFailedToLoad(loadAdError);
//                        Log.d("TAG", "onAdFailedToLoad() called with: loadAdError = [" + loadAdError + "]");
//                    }
//                }).build();
//
//                adLoader.loadAd(new AdRequest.Builder().build());
//            }
//        }
//    }
//
//    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
//
//        MediaView mediaView = adView.findViewById(R.id.ad_media);
//        adView.setMediaView(mediaView);
//
//        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//        adView.setBodyView(adView.findViewById(R.id.ad_body));
//        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
//        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//        adView.setPriceView(adView.findViewById(R.id.ad_price));
//        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//        adView.setStoreView(adView.findViewById(R.id.ad_store));
//        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
//        TextView ad_txt = adView.findViewById(R.id.ad_txt);
//        CardView cardView = adView.findViewById(R.id.ad_app_);
//
//        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
//        if (TextUtils.isEmpty(nativeAd.getBody())) {
//            adView.getBodyView().setVisibility(View.GONE);
//        } else {
//            adView.getBodyView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
//        }
//
//        if (TextUtils.isEmpty(nativeAd.getCallToAction())) {
//            adView.getCallToActionView().setVisibility(View.GONE);
//        } else {
//            adView.getCallToActionView().setVisibility(View.VISIBLE);
//            ((com.google.android.material.button.MaterialButton) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
//        }
//
//        if (nativeAd.getIcon() == null) {
//            adView.getIconView().setVisibility(View.GONE);
//            cardView.setVisibility(View.GONE);
//        } else {
//            ((ImageView) adView.getIconView()).setImageDrawable(
//                    nativeAd.getIcon().getDrawable());
//            adView.getIconView().setVisibility(View.VISIBLE);
//            cardView.setVisibility(View.VISIBLE);
//        }
//
//        if (TextUtils.isEmpty(nativeAd.getPrice())) {
//            adView.getPriceView().setVisibility(View.GONE);
//        } else {
//            adView.getPriceView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//        }
//
//        if (TextUtils.isEmpty(nativeAd.getStore())) {
//            adView.getStoreView().setVisibility(View.GONE);
//        } else {
//            adView.getStoreView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//        }
//
//        if (nativeAd.getStarRating() == null) {
//            adView.getStarRatingView().setVisibility(View.INVISIBLE);
//            ad_txt.setText("Advertisement");
//        } else {
//            ((RatingBar) adView.getStarRatingView())
//                    .setRating(nativeAd.getStarRating().floatValue());
//            adView.getStarRatingView().setVisibility(View.VISIBLE);
//            ad_txt.setText("Ad");
//        }
//
//        if (TextUtils.isEmpty(nativeAd.getAdvertiser())) {
//            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
//        } else {
//            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
//            adView.getAdvertiserView().setVisibility(View.VISIBLE);
//        }
//
//        ImageView bck = adView.findViewById(R.id.ad_imageBack);
//        if (nativeAd.getImages() != null && nativeAd.getImages().size() > 0) {
//            Glide.with(BulletApp.getInstance())
//                    .load(nativeAd.getImages().get(0).getUri())
//                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
//                    .into(bck);
//        }
//        adView.setNativeAd(nativeAd);
//        VideoController vc = nativeAd.getVideoController();
//        if (vc.hasVideoContent()) {
//            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//                @Override
//                public void onVideoEnd() {
//                    super.onVideoEnd();
//                }
//            });
//        }
//    }
//
//    private void crossFadeAnimation(final View fadeInTarget, final View fadeOutTarget, long duration) {
//        AnimatorSet mAnimationSet = new AnimatorSet();
//        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fadeOutTarget, View.ALPHA, opacity, 0f);
//        fadeOut.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                fadeOutTarget.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//        fadeOut.setInterpolator(new LinearInterpolator());
//
//        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(fadeInTarget, View.ALPHA, 0f, opacity);
//        fadeIn.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                fadeInTarget.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//        fadeIn.setInterpolator(new LinearInterpolator());
//        mAnimationSet.setDuration(duration);
//        mAnimationSet.playTogether(fadeOut, fadeIn);
//        mAnimationSet.start();
//    }
//
//    public interface onCardListener {
//        void callNextData();
//
//        String nextPage();
//    }
//
//    public class CardViewHolder extends RecyclerView.ViewHolder {
//
//        FrameLayout fl_adplaceholder;
//        RelativeLayout ad_mob;
//        RelativeLayout button;
//        RoundedImageView profile;
//        ImageView odd_imageBack;
//        ImageView odd_image;
//        TextView source_name;
//        TextView time;
//        ImageView dots, image2;
//        RelativeLayout touch;
//        RelativeLayout cardData;
//        RelativeLayout leftRightPanel;
//        RecyclerView desc_list;
//        StoriesProgressView storiesProgressView;
//        ImageView leftArc, rightArc;
//        ProgressBar progressBar;
//        RelativeLayout youtubeMain;
//        YouTubePlayerView youTubePlayerView;
//        RelativeLayout rlDummy;
//        ImageView play;
//
//        public CardViewHolder(View itemView) {
//            super(itemView);
//            leftRightPanel = itemView.findViewById(R.id.leftRightPanel);
//            play = itemView.findViewById(R.id.play);
//            youtubeMain = itemView.findViewById(R.id.youtubeMain);
//            youTubePlayerView = itemView.findViewById(R.id.youtube_view);
//            button = itemView.findViewById(R.id.button);
//            progressBar = itemView.findViewById(R.id.progressAudio);
//            leftArc = itemView.findViewById(R.id.leftArc);
//            rightArc = itemView.findViewById(R.id.rightArc);
//            touch = itemView.findViewById(R.id.touch);
//            odd_image = itemView.findViewById(R.id.odd_image);
//            odd_imageBack = itemView.findViewById(R.id.odd_imageBack);
////            article = itemView.findViewById(R.id.article);
//            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
//            ad_mob = itemView.findViewById(R.id.ad_mob);
//            profile = itemView.findViewById(R.id.profile);
//            image2 = itemView.findViewById(R.id.image2);
//            desc_list = itemView.findViewById(R.id.desc_list);
//            cardData = itemView.findViewById(R.id.cardData);
//            time = itemView.findViewById(R.id.time);
//            source_name = itemView.findViewById(R.id.source_name);
//            dots = itemView.findViewById(R.id.dots);
//            storiesProgressView = itemView.findViewById(R.id.stories);
//            rlDummy = itemView.findViewById(R.id.dummy);
//        }
//    }
//
//}
