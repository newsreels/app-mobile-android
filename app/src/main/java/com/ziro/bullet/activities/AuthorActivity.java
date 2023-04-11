package com.ziro.bullet.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.AuthorPagerAdapter;
import com.ziro.bullet.bottomSheet.ProfileBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AuthorApiCallback;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.presenter.AuthorPresenter;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AuthorActivity extends BaseActivity implements AuthorApiCallback {

    private static ArrayList<String> mTabTitleList = new ArrayList<>();
    private TabLayout tabLayout;
    private LinearLayout statusBarSpace;
    private ViewPager mPager;
    private RelativeLayout backBtn;
    private RelativeLayout progress;
    private ImageView arrowIcon;
    private CircleImageView profileImage;
    private ImageView coverImage;
    private TextView username;
    private TextView textView5;
    private TextView follow_btn;
    private TextView tvCount;
    private TextView textView10;
    private PrefConfig mPrefConfig;
    private AuthorPresenter authorPresenter;
    private String authorID = null;
    private String authorContext = null;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private Author author;
    private GestureDetectorCompat gestureDetectorCompat;
    private CoordinatorLayout htab_maincontent;
    private ProfileBottomSheet shareBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_author);
        mPrefConfig = new PrefConfig(this);
        authorPresenter = new AuthorPresenter(this, this);
        followUnfollowPresenter = new FollowUnfollowPresenter(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, new SwipeGestureListener());

        bindViews();
        setStatusBarHeight();
        listeners();

        mTabTitleList.add(getString(R.string.newsreels));
        mTabTitleList.add(getString(R.string.articles));

        if (getIntent() != null) {
            authorID = getIntent().getStringExtra("authorID");
            authorContext = getIntent().getStringExtra("authorContext");
            AuthorPagerAdapter profilePagerAdapter = new AuthorPagerAdapter(getSupportFragmentManager(), mTabTitleList, authorContext);
            mPager.setAdapter(profilePagerAdapter);
            mPager.setOffscreenPageLimit(2);
            tabLayout.setupWithViewPager(mPager);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (authorPresenter != null && !TextUtils.isEmpty(authorID))
            authorPresenter.getAuthor(authorID);
    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return gestureDetectorCompat.onTouchEvent(event);
//        // return super.onTouchEvent(event);
//    }

    private void showVerifiedIcon(@NotNull Author authorObj) {
        if (authorObj.isVerified()) {
            if (Utils.isRTL()) {
                textView5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified, 0, 0, 0);
            } else {
                textView5.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0);
            }
        }
    }

    private void bindViews() {
//        ImageView loader_gif = findViewById(R.id.loader);
//        if (mPrefConfig != null) {
//            Glide.with(this).load(Utils.getLoaderForTheme(mPrefConfig.getAppTheme())).into(loader_gif);
//        }
        arrowIcon = findViewById(R.id.arrowIcon);
        progress = findViewById(R.id.progress);
        follow_btn = findViewById(R.id.follow_btn);
        tvCount = findViewById(R.id.textView9);
        textView10 = findViewById(R.id.textView10);
        textView5 = findViewById(R.id.textView5);
        username = findViewById(R.id.username);
        tabLayout = findViewById(R.id.tabLayout);
        statusBarSpace = findViewById(R.id.status_bar_space);
        mPager = findViewById(R.id.viewpager);
        backBtn = findViewById(R.id.back_btn);
        coverImage = findViewById(R.id.cover_image);
        profileImage = findViewById(R.id.profile_image);
        htab_maincontent = findViewById(R.id.htab_maincontent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("author", author);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        overridePendingTransition(R.anim.left_to_right_500, R.anim.right_to_left_500);
    }

    private void listeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
//        tvCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(author != null && !TextUtils.isEmpty(author.getId())) {
//                    FollowersActivity.start(AuthorActivity.this, "", author.getId());
//                }
//            }
//        });

        arrowIcon.setOnClickListener(v -> {
            if (author != null) {
                showBottomSheetDialog(author.getId(), author.getNameToDisplay(), dialog -> {
                });
            }
        });

        htab_maincontent.setOnTouchListener((v, event) -> {
            return gestureDetectorCompat.onTouchEvent(event);
            // return false;
        });
    }

    private void showBottomSheetDialog(String authorId, String authorName, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ProfileBottomSheet(this, "authors", authorId, authorName);
        }
        shareBottomSheet.show(onDismissListener);
    }

    private void setStatusBarHeight() {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tabLayout.getLayoutParams();
        params.setMargins(0, getStatusBarHeight(), 0, 0);
        tabLayout.setLayoutParams(params);

        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) statusBarSpace.getLayoutParams();
        params1.setMargins(0, getStatusBarHeight(), 0, 0);
        statusBarSpace.setLayoutParams(params1);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setData(Author author) {
        if (isFinishing())
            return;
        if (author != null) {
            tvCount.setText(author.getFollower_count() + " " + getString(R.string.followers));
            textView10.setText(author.getPost_count() + " " + getString(R.string.posts));

            if (author.isFollow()) {
                follow_btn.setText(getString(R.string.following));
            } else {
                follow_btn.setText(getString(R.string.follow));
            }

            follow_btn.setOnClickListener(v -> {
                if (author.isFollow()) {
                    followUnfollowPresenter.unFollowAuthor(authorID, 0, (position, flag) -> {
                        author.setFollow(!flag);
                        author.setFollower_count(author.getFollower_count() - 1);
                        setData(author);
                    });
                } else {
                    followUnfollowPresenter.followAuthor(authorID, 0, (position, flag) -> {
                        author.setFollow(flag);
                        author.setFollower_count(author.getFollower_count() + 1);
                        setData(author);
                    });
                }
            });

            username.setText("@"+author.getUsername());

            if (!TextUtils.isEmpty(author.getNameToDisplay())) {
                textView5.setText(author.getNameToDisplay());
            }
            if (!TextUtils.isEmpty(author.getProfile_image())) {
                Glide.with(this).load(author.getProfile_image())
                        .placeholder(R.drawable.ic_placeholder_user)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.ic_placeholder_user);
            }
            if (!TextUtils.isEmpty(author.getCover_image())) {
                Glide.with(this).load(author.getCover_image())
                        .placeholder(R.drawable.cover)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(coverImage);
            } else {
                coverImage.setImageResource(R.drawable.cover);
            }

            showVerifiedIcon(author);
        } else {
            profileImage.setImageResource(R.drawable.ic_placeholder_user);
            coverImage.setImageResource(R.drawable.cover);
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        Utils.showSnacky(profileImage, error);
    }

    @Override
    public void success(Author author) {
        this.author = author;
        setData(author);
    }

    class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {

//            if(event2.getX() > event1.getX()){
//                onBackPressed();
//            }

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onBackPressed();
                            result = true;
                            //onSwipeRight();
                        } else {
                            //onSwipeLeft();
                        }
                        //result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        //onSwipeBottom();
                    } else {
                        //onSwipeTop();
                    }
                    //result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return result;
        }
    }

}