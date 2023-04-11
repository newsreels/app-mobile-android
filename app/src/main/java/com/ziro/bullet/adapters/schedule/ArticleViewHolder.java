package com.ziro.bullet.adapters.schedule;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.CardBulletsAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnScheduleCallback;
import com.ziro.bullet.interfaces.ScheduleTimerFinishListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.storyMaker.HorizontalStoriesProgressView;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ArticleViewHolder extends RecyclerView.ViewHolder {

    private static final int MIN_DISTANCE = 150;
    private final float Y_BUFFER = 10;
    public HorizontalStoriesProgressView storiesProgressView;
    private float preX = 0f;
    private float preY = 0f;
    private float x1, x2;
    private RoundedImageView ivProfile;
    private ImageView ivArticleImage, ivArticleBackImage, ivLeftArc, ivRightArc;
    private TextView tvTime, tvSourceName, tvDummyBulletForSize;
    private RecyclerView rvDescList;

    private NewsCallback categoryCallback;
    private DetailsActivityInterface detailsActivityInterface;
    private AdapterCallback adapterCallback;

    private PrefConfig mPrefConfig;
    private Activity mContext;
    private String direction = "";
    private CardBulletsAdapter bulletsAdapterMain;

    private Handler handler = new Handler();
    private boolean swipeNextEnabled = true;
    private Runnable tapRunnable = () -> swipeNextEnabled = true;
    private String mType = "";
    private TextView tvTimer;
    private LinearLayout llPost, llEdit, llDelete, llTimer;
    private OnScheduleCallback onScheduleCallback;
    private CountDownTimer timer;
    private ScheduledListAdapter.TYPE type;
    private ScheduleTimerFinishListener timerFinishListener;
    private TextView authorName;
    private boolean isCommunity;

    public ArticleViewHolder(boolean isCommunity, ScheduleTimerFinishListener timerFinishListener, @NonNull View itemView, PrefConfig config, Activity context,
                             AdapterCallback adapterCallback, NewsCallback categoryCallback, DetailsActivityInterface detailsActivityInterface,
                             OnScheduleCallback onScheduleCallback, ScheduledListAdapter.TYPE type) {
        super(itemView);

        this.timerFinishListener = timerFinishListener;
        mPrefConfig = config;
        mContext = context;

        this.type = type;
        this.isCommunity = isCommunity;
        this.adapterCallback = adapterCallback;
        this.categoryCallback = categoryCallback;
        this.detailsActivityInterface = detailsActivityInterface;
        this.onScheduleCallback = onScheduleCallback;

        tvDummyBulletForSize = itemView.findViewById(R.id.dummyBulletForSize);
        ivLeftArc = itemView.findViewById(R.id.leftArc);
        ivRightArc = itemView.findViewById(R.id.rightArc);
        ivArticleImage = itemView.findViewById(R.id.odd_image);
        ivArticleBackImage = itemView.findViewById(R.id.odd_imageBack);
        ivProfile = itemView.findViewById(R.id.profile);
        rvDescList = itemView.findViewById(R.id.desc_list);
        tvTime = itemView.findViewById(R.id.time);
        tvSourceName = itemView.findViewById(R.id.source_name);
        storiesProgressView = itemView.findViewById(R.id.stories);
        tvTimer = itemView.findViewById(R.id.tvTimer);
        llPost = itemView.findViewById(R.id.llPost);
        llEdit = itemView.findViewById(R.id.llEdit);
        llDelete = itemView.findViewById(R.id.llDelete);
        llTimer = itemView.findViewById(R.id.llTimer);
        authorName = itemView.findViewById(R.id.author_name);
    }

    public void destroy() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void bind(int position, Article article) {
        if (article != null) {
            if (article.getBullets() != null && article.getBullets().size() > 0) {
                changeImage(article.getBullets().get(0).getImage());
            } else {
                ivArticleImage.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            tvSourceName.setText(article.getSourceNameToDisplay());

            if (!TextUtils.isEmpty(article.getSourceImageToDisplay())) {
                Picasso.get()
                        .load(article.getSourceImageToDisplay())
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .transform(new CropCircleTransformation())
                        .into(ivProfile);
            } else {
                Picasso.get()
                        .load(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .transform(new CropCircleTransformation())
                        .into(ivProfile);
            }

            if (!article.getAuthorNameToDisplay().equals("")) {
                authorName.setVisibility(View.VISIBLE);
                authorName.setText(article.getAuthorNameToDisplay());
            } else {
                authorName.setVisibility(View.GONE);
            }

            if (type == ScheduledListAdapter.TYPE.SCHEDULE) {
                llTimer.setVisibility(View.VISIBLE);

                //Very important code for some problems
                if (timer != null) {
                    timer.cancel();
                }

                Date now = new Date();
                long millis = 0;
                Date publishDate = Utils.getDate(article.getPublishTime());
                if (publishDate != null)
                    millis = Objects.requireNonNull(publishDate).getTime() - now.getTime();

                long day = TimeUnit.MILLISECONDS.toDays(millis);
                if (day > 0) {
                    boolean sameYear = Utils.isSameYear(publishDate);
                    String pattern = "";
                    if (sameYear) {
                        pattern = "MMM dd, hh:mm aaa";
                    } else {
                        pattern = "yyyy MMM dd, hh:mm aaa";
                    }
                    String localizedFormattedDate = Utils.getCustomDate(article.getPublishTime(), pattern);
                    tvTimer.setText(String.format(mContext.getString(R.string.will_be_posted_on) + " %s", localizedFormattedDate));
                } else {
                    timer = new CountDownTimer(millis, 900) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.DAYS.toMillis(day);

                            long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);

                            long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);

                            long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                            Log.d("TAG", "onTick: " + day);
                            Log.d("TAG", "onTick: " + hour);
                            Log.d("TAG", "onTick: " + minute);
                            Log.d("TAG", "onTick: " + second);

                            if (day > 0) {
                                tvTimer.setText(String.format(mContext.getString(R.string.will_be_posted_in) + " %dd %dh %dm %ds", day, hour, minute, second));
                            } else if (hour > 0) {
                                tvTimer.setText(String.format(mContext.getString(R.string.will_be_posted_in) + " %dh %dm %ds", hour, minute, second));
                            } else if (minute > 0) {
                                tvTimer.setText(String.format(mContext.getString(R.string.will_be_posted_in) + " %dm %ds", minute, second));
                            } else if (second > 0) {
                                tvTimer.setText(String.format(mContext.getString(R.string.will_be_posted_in) + " %ds", second));
                            }
                        }

                        @Override
                        public void onFinish() {
                            tvTimer.setText(String.format(mContext.getString(R.string.will_be_posted_now)));
                            if (timerFinishListener != null)
                                timerFinishListener.removeArticle(position);
                        }
                    }.start();
                }
            } else {
                llTimer.setVisibility(View.GONE);
                llPost.setVisibility(View.GONE);
            }

            CardBulletsAdapter cardBulletsAdapter = new CardBulletsAdapter(isCommunity,null, mType, false, (Activity) mContext, article.getBullets(), new ListAdapterListener() {
                @Override
                public void verticalScrollList(boolean isEnable) {
                    if (isEnable) {
                        bulletResume();
                    } else {
                        bulletPause();
                    }
                }

                @Override
                public void nextArticle(int position) {
                    nextBullet(bulletsAdapterMain, true, article);
                }

                @Override
                public void prevArticle(int position) {
                    prevBullet(bulletsAdapterMain, true, article);
                }

                @Override
                public void clickArticle(int position) {
//                        mCommentClick.onDetailClick(position,article);
                }
            }, article, position);

            llPost.setOnClickListener(v -> {
                if (onScheduleCallback != null)
                    onScheduleCallback.onPost(position);
            });
            llEdit.setOnClickListener(v -> {
                if (onScheduleCallback != null)
                    onScheduleCallback.onEdit(position);
            });
            llDelete.setOnClickListener(v -> {
                article.setSelected(false);
                if (onScheduleCallback != null)
                    onScheduleCallback.onDelete(position);
            });

            if (article.getBullets() != null && article.getBullets().size() > 0) {

                LinearLayoutManager manager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
                rvDescList.setLayoutManager(manager);
                rvDescList.setAdapter(cardBulletsAdapter);
                rvDescList.setOnFlingListener(null);
                PagerSnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(rvDescList);

                rvDescList.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                rvDescList.requestLayout();

                rvDescList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                x1 = e.getX();
                                rvDescList.getParent().requestDisallowInterceptTouchEvent(true);
                                break;
                            case MotionEvent.ACTION_UP:
                                if (bulletsAdapterMain != null) {
                                    x2 = e.getX();
                                    float deltaX = x2 - x1;
                                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                                        //Utils.doHaptic(mPrefConfig);
                                        if ((!Utils.isRTL() && x2 > x1) || (Utils.isRTL() && !(x2 > x1))) {
                                            if (article.isSelected() && bulletsAdapterMain.getCurrentPosition() == 0 && position == adapterCallback.getArticlePosition()) {
                                                if (swipeNextEnabled) {
                                                    swipeNextEnabled = false;
                                                    handler.postDelayed(tapRunnable, 200);
                                                    prevArticle();
                                                }

                                            }
                                        } else {
                                            if (article.isSelected() && bulletsAdapterMain.getCurrentPosition() == (bulletsAdapterMain.getItemCount() - 1) && position == adapterCallback.getArticlePosition()) {
                                                handler.removeCallbacks(tapRunnable);

                                                if (swipeNextEnabled) {
                                                    swipeNextEnabled = false;
                                                    handler.postDelayed(tapRunnable, 200);
                                                    nextArticle();
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (Math.abs(e.getX() - preX) > Math.abs(e.getY() - preY)) {
                                    rvDescList.getParent().requestDisallowInterceptTouchEvent(true);
                                } else if (Math.abs(e.getY() - preY) > Y_BUFFER) {
                                    rvDescList.getParent().requestDisallowInterceptTouchEvent(false);
                                }
                                break;
                        }
                        preX = e.getX();
                        preY = e.getY();
                        return false;
                    }

                    @Override
                    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                });

                final int[] mPosition = {0};
                rvDescList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (mPosition[0] != -1) {
                                article.setLastPosition(mPosition[0]);
                                cardBulletsAdapter.setCurrentPosition(mPosition[0]);

                                if (mPosition[0] < article.getBullets().size()) {
                                    Bullet bullet = article.getBullets().get(mPosition[0]);
                                    if (bullet != null) {
                                        changeImage(bullet.getImage());
                                    }
                                }

                                float val = -1;
                                if (mPosition[0] == 0) {
                                    val = Utils.getHeadlineDimens(mPrefConfig, mContext);
                                } else {
                                    val = Utils.getBulletDimens(mPrefConfig, mContext);
                                }
                                if (val != -1) {
                                    tvDummyBulletForSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
                                }

                                if (article.isSelected()) {
                                    Constants.auto_scroll = false;
                                    playAudio(cardBulletsAdapter, article);
                                } else {
                                    //Utils.doHaptic(mPrefConfig);
                                    resizeBullet(cardBulletsAdapter, article);
                                }
                            }
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        mPosition[0] = snapHelper.findTargetSnapPosition(manager, dx, dy);
                        if (dx > 0) {
                            direction = "rtl";
                        } else {
                            direction = "ltr";
                        }
                    }
                });

            }

            if (article.isSelected()) {
                storiesProgressView.clearStory();
                bulletsAdapterMain = cardBulletsAdapter;

                imageCase(position, cardBulletsAdapter, article);

                if (cardBulletsAdapter.getCurrentPosition() == 0) {
                    playAudio(cardBulletsAdapter, article);
                }
            } else {
                storiesProgressView.setStoriesListener(null);
                storiesProgressView.destroy();
                storiesProgressView.setVisibility(View.INVISIBLE);
                rvDescList.scrollToPosition(article.getLastPosition());
            }
        }
    }

    public void changeImage(String img) {
        if (!TextUtils.isEmpty(img)) {
            Picasso.get()
                    .load(img)
                    .transform(new BlurTransformation(mContext, 25, 1))
                    .resize(Constants.targetWidth, Constants.targetHeight)
                    .onlyScaleDown()
                    .into(ivArticleBackImage);

            Picasso.get()
                    .load(img)
                    .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                    .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                    .fit()
//                    .centerInside()
                    .resize(Constants.targetWidth, Constants.targetHeight)
//                    .centerCrop()
                    .into(ivArticleImage);

//            Glide.with(BulletApp.getInstance())
//                    .load(img)
//                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
//                    .override(Constants.targetWidth, Constants.targetHeight)
//                    .into(ivArticleBackImage);
//
//            Glide.with(BulletApp.getInstance())
//                    .load(img)
//                    .error(R.drawable.img_place_holder)
//                    .placeholder(R.drawable.img_place_holder)
//                    .override(Constants.targetWidth, Constants.targetHeight)
//                    .into(ivArticleImage);

        } else {
            ivArticleImage.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
        }
    }


    private void nextArticle() {
        Log.e("kjandn", "nextArticle");
        Constants.auto_scroll = true;
        tvDummyBulletForSize.setText("");
        rvDescList.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        rvDescList.requestLayout();
        if (categoryCallback != null) {
            int pos = adapterCallback.getArticlePosition();
            pos++;
            categoryCallback.nextPosition(pos);
        }
    }

    private void prevArticle() {
        Constants.auto_scroll = true;
        tvDummyBulletForSize.setText("");
        rvDescList.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        rvDescList.requestLayout();
        Log.e("kjandn", "prevArticle");
        if (categoryCallback != null) {
            int pos = adapterCallback.getArticlePosition();
            pos--;
            categoryCallback.nextPosition(pos);
        }
    }


    public void bulletResume() {
        if (mContext != null) {
            if (detailsActivityInterface != null) {
                detailsActivityInterface.resume();
            }
        }
    }

    public void bulletPause() {
        if (mContext != null) {
            if (detailsActivityInterface != null) {
                detailsActivityInterface.pause();
            }
        }
    }

    public void imageCase(int position, CardBulletsAdapter bulletsAdapter, Article content) {
        if (position > -1 && bulletsAdapter != null && content != null) {

            Constants.ProgressBarDrawable = R.drawable.bullet_selector_green;
            Constants.ProgressBarBackDrawable = R.drawable.bullet_selector_green_2;

            storiesProgressView.setStoriesCount(content.getBullets().size());
            storiesProgressView.setProgressWidth(14, false);
            storiesProgressView.setStoriesListener(new HorizontalStoriesProgressView.StoriesListener() {
                @Override
                public void onFirst() {

                }

                @Override
                public void onNext() {
                    if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                        //GOTO NEXT BULLET
                        nextBullet(bulletsAdapter, false, content);
                    } else {
                        //Auto Swipe
                        if (Constants.auto_scroll) {
//                            Utils.logEvent(mContext, "list_next_auto");
//                            //GOTO NEXT ARTICLE
//                            if (categoryCallback != null) {
//                                int pos = position;
//                                pos++;
//                                categoryCallback.nextPosition(pos);
//                            }
                        } else {
                            //GOTO NEXT BULLET
                            nextBullet(bulletsAdapter, false, content);
                        }
                    }
                }

                @Override
                public void onPrev() {
                }

                @Override
                public void onComplete() {
                    Constants.auto_scroll = true;
                    if (categoryCallback != null) {
                        int pos = position;
                        pos++;
                        categoryCallback.nextPosition(pos);
                    }
                }
            });


            storiesProgressView.setVisibility(View.VISIBLE);
//            commentMain.setVisibility(View.VISIBLE);
//            likeMain.setVisibility(View.VISIBLE);
        }
    }

    public void playAudio(CardBulletsAdapter bulletsAdapter, Article content) {
        if (bulletsAdapter != null && content != null) {
            if (bulletsAdapter.getCurrentPosition() < content.getBullets().size()) {
                resizeBullet(bulletsAdapter, content);
                long duration = 0;
                if (content.getBullets() != null && content.getBullets().size() > 0) {
                    Bullet bullet = content.getBullets().get(bulletsAdapter.getCurrentPosition());
                    if (bullet != null) {
                        if (!TextUtils.isEmpty(bullet.getData()) && bullet.getDuration() == 0) {
                            duration = bullet.getData().length() * 100;
                        } else {
                            duration = (long) ((long) bullet.getDuration() / Constants.READING_SPEED_RATES.get(Constants.reading_speed));
                        }
                    }
                }
                duration = duration + 1500;
                storiesProgressView.setStoryDuration(duration);
                if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                    storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                } else {
                    storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                }

                long finalDuration1 = duration;
                detailsActivityInterface.playAudio(
                        new AudioCallback() {
                            @Override
                            public void isAudioLoaded(boolean isLoaded) {
                                if (isLoaded) {
                                    storiesProgressView.setStoryDuration(finalDuration1);
                                    if (mPrefConfig.getMenuNarration() != null && !mPrefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                                        storiesProgressView.startStories(bulletsAdapter.getCurrentPosition());
                                    } else {
                                        storiesProgressView.selectStory(bulletsAdapter.getCurrentPosition());
                                    }
                                }
                            }

                            @Override
                            public void isAudioComplete(boolean isCompleted) {

                            }
                        }, content.getFragTag(), new AudioObject(content.getId(),
                                content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData(),
                                "",
                                bulletsAdapter.getCurrentPosition(), duration));


            }
        }
    }

    public void resizeBullet(CardBulletsAdapter bulletsAdapter, Article content) {
        Log.e("resizeBullet", "------------------------------------");
        Log.e("resizeBullet", "--> " + content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData());
        Log.e("resizeBullet", "-position-> " + bulletsAdapter.getCurrentPosition());
        tvDummyBulletForSize.setText(content.getBullets().get(bulletsAdapter.getCurrentPosition()).getData());
        tvDummyBulletForSize.post(() -> {
            if (rvDescList.getMeasuredHeight() < tvDummyBulletForSize.getMeasuredHeight()) {
                setHeight(rvDescList, tvDummyBulletForSize.getMeasuredHeight());
                bulletsAdapter.setHeight(tvDummyBulletForSize.getMeasuredHeight());
                tvDummyBulletForSize.setText("");
            }
        });
    }

    public void setHeight(View view, int height) {
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), height);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = val;
            view.setLayoutParams(layoutParams);
        });
        anim.setDuration(200);
        anim.start();
    }

    private void nextBullet(CardBulletsAdapter bulletsAdapter, boolean tapEffect, Article article) {
        Log.e("kjandn", "nextBullet");
        if (tapEffect) {
            if (article.isSelected()) {
                Constants.auto_scroll = false;
            }
            if (Utils.isRTL()) {
                crossFadeAnimation(ivLeftArc, ivLeftArc, 300);
            } else {
                crossFadeAnimation(ivRightArc, ivRightArc, 300);
            }
        }
        if (bulletsAdapter != null) {
            int pos = bulletsAdapter.getCurrentPosition();
            if (pos == bulletsAdapter.getItemCount() - 1) {
                nextArticle();
            } else {
                pos++;
                rvDescList.smoothScrollToPosition(pos);
                bulletsAdapter.setCurrentPosition(pos);
            }
        }
    }

    private void prevBullet(CardBulletsAdapter bulletsAdapter, boolean tapEffect, Article article) {
        Log.e("kjandn", "prevBullet");
        if (tapEffect) {
            if (article.isSelected()) {
                Constants.auto_scroll = false;
            }
            if (Utils.isRTL()) {
                crossFadeAnimation(ivRightArc, ivRightArc, 300);
            } else {
                crossFadeAnimation(ivLeftArc, ivLeftArc, 300);
            }
        }
        if (bulletsAdapter != null) {
            if (bulletsAdapter.getCurrentPosition() == 0) {
                prevArticle();
            } else {
                int pos = bulletsAdapter.getCurrentPosition();
                rvDescList.smoothScrollToPosition(--pos);
                bulletsAdapter.setCurrentPosition(pos);
            }
        }
    }

    private void crossFadeAnimation(final View fadeInTarget, final View fadeOutTarget, long duration) {
        AnimatorSet mAnimationSet = new AnimatorSet();
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fadeOutTarget, View.ALPHA, 1f, 0f);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Utils.doHaptic(mPrefConfig);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fadeOutTarget.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        fadeOut.setInterpolator(new LinearInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(fadeInTarget, View.ALPHA, 0f, 1f);
        fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                fadeInTarget.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        fadeIn.setInterpolator(new LinearInterpolator());
        mAnimationSet.setDuration(duration);
        mAnimationSet.playTogether(fadeOut, fadeIn);
        mAnimationSet.start();
    }
}
