package com.ziro.bullet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.comment.CommentsAdapter;
import com.ziro.bullet.adapters.comment.RepliesAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.fragments.CommentPopup;
import com.ziro.bullet.interfaces.CommentInterface;
import com.ziro.bullet.model.comment.Comment;
import com.ziro.bullet.model.comment.CommentResponse;
import com.ziro.bullet.presenter.CommentPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

public class CommentsActivity extends BaseActivity implements CommentInterface {

    private final int TYPE_COMMENT = 0;
    private TextView title;
    private ImageView comment_cancel;
    private ImageView send;
    private RecyclerView comments;
    private CircleImageView image;
    private EditText msg;
    private CommentPresenter presenter;
    private CommentsAdapter commentsAdapter;
    private ArrayList<Comment> commentsList = new ArrayList<>();
    private boolean isLoading = false;
    private String nextPage = PAGE_START;
    private boolean isLastPage = false;
    private String article_id = "";
    private int position = -1;
    private PrefConfig prefConfig;
    private CommentPopup commentPopup;
    private RelativeLayout progress;
    private ImageView loader_gif;

    private AppCompatImageButton heart;
    private AppCompatImageButton handsUp;
    private AppCompatImageButton fire;
    private AppCompatImageButton clap;
    private AppCompatImageButton sad;
    private AppCompatImageButton inlove;
    private AppCompatImageButton shock;
    private AppCompatImageButton laugh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_comments);
        getBundle();
        bindViews();
        init();
        listener();

        checkLoggedIn();

    }

    private void checkLoggedIn(){
        if(prefConfig.isGuestUser()){
            msg.setOnClickListener(v -> LoginPopupActivity.start(CommentsActivity.this));
            msg.setFocusable(false);
            send.setEnabled(false);
        }else{
            msg.setOnClickListener(null);
            msg.setEnabled(true);
            msg.setFocusable(true);
            msg.setFocusableInTouchMode(true);
            send.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> Utils.hideKeyboard(CommentsActivity.this, msg),200);
        nextPage = "";
        commentsList.clear();
        setData();
    }

    private void setData() {
        presenter.comments(article_id, "", nextPage, false);
        if (prefConfig != null) {
            User user = prefConfig.isUserObject();
            if (user != null) {
                if (!TextUtils.isEmpty(user.getProfile_image())) {
                    Glide.with(this).load(user.getProfile_image())
                            .placeholder(R.drawable.ic_placeholder_user)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(image);
                } else {
                    image.setImageResource(R.drawable.ic_placeholder_user);
                }
            }
        }
    }

    private void getBundle() {
        if (getIntent() != null) {
            article_id = getIntent().getStringExtra("article_id");
            position = getIntent().getIntExtra("position", -1);
        }
    }

    private void listener() {

        heart.setOnClickListener( v -> {
            msg.append("❤️");
        });
        handsUp.setOnClickListener( v -> {
            msg.append("\uD83D\uDE4C️");
        });
        fire.setOnClickListener( v -> {
            msg.append("\uD83D\uDD25️");
        });
        clap.setOnClickListener( v -> {
            msg.append("\uD83D\uDC4F️");
        });
        sad.setOnClickListener( v -> {
            msg.append("\uD83D\uDE22️");
        });
        inlove.setOnClickListener( v -> {
            msg.append("\uD83D\uDE0D️");
        });
        shock.setOnClickListener( v -> {
            msg.append("\uD83D\uDE2E️");
        });
        laugh.setOnClickListener( v -> {
            msg.append("\uD83D\uDE02");
        });

        send.setOnClickListener(v -> {
            if (prefConfig != null && prefConfig.isUserObject() != null && prefConfig.isUserObject().isSetup()) {
                String comment = msg.getText().toString();
                if (!TextUtils.isEmpty(comment)) {
                    Log.e("comment", "comment: " + comment);
                    Log.e("comment", "article id : " + article_id);
                    Log.e("comment", "parent id : " + "");
                    if (presenter != null) {
                        send.setEnabled(false);
                        presenter.createComment(article_id, "", comment);
                    }
                }
            } else {
                if (commentPopup == null) {
                    commentPopup = new CommentPopup(this);
                } else {
                    commentPopup.showDialog(getString(R.string.before_you_can_comment_you_need_to_update_your_profile));
                }
            }
        });
        comment_cancel.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        Constants.notishare = false;
        Constants.onResumeReels = true;
        Intent intent = new Intent();
        intent.putExtra("id", article_id);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void init() {
        prefConfig = new PrefConfig(this);
        presenter = new CommentPresenter(this, this);
        commentsAdapter = new CommentsAdapter(commentsList, this, 0, new CommentsAdapter.CommentCallback() {

            @Override
            public void onParentReply(int position, Comment comment, String type, CommentsAdapter adapter, ArrayList<Comment> comments) {
                if (!TextUtils.isEmpty(type)) {
                    Intent intent = new Intent(CommentsActivity.this, ReplyActivity.class);
                    intent.putExtra("article_id", article_id);
                    intent.putExtra("parent_id", comment.getId());
                    intent.putExtra("type", type);
                    if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("comment")) {
                        intent.putExtra("comment", new Gson().toJson(comment));
                    }
                    startActivity(intent);
                }
            }

            @Override
            public void onChildReply(int position, Comment comment, String type, RepliesAdapter adapter, ArrayList<Comment> childItems) {
                if (!TextUtils.isEmpty(type)) {
                    Intent intent = new Intent(CommentsActivity.this, ReplyActivity.class);
                    intent.putExtra("article_id", article_id);
                    intent.putExtra("parent_id", comment.getId());
                    intent.putExtra("type", type);
                    if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("comment")) {
                        intent.putExtra("comment", new Gson().toJson(comment));
                    }
                    startActivity(intent);
                }
            }

            @Override
            public void onPagination(int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                if (presenter != null)
                    presenter.comments(article_id, child.getId(), "", adapter, childItemsAdapter);
            }

            @Override
            public void updateParentId(String parentId, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {

            }

        }, article_id);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        comments.setLayoutManager(manager);
        comments.setAdapter(commentsAdapter);
        setPagination(comments, manager);

//        Glide.with(this)
//                .load(Utils.getLoaderForTheme(prefConfig.getAppTheme()))
//                .into(loader_gif);
    }

    private void bindViews() {

        heart = findViewById(R.id.imgHeart);
        handsUp = findViewById(R.id.imgHands);
        fire = findViewById(R.id.imgFire);
        clap = findViewById(R.id.clapImage);
        sad = findViewById(R.id.imgSad);
        inlove = findViewById(R.id.imgSmiley);
        shock = findViewById(R.id.imgShock);
        laugh = findViewById(R.id.img_laugh);

        title = findViewById(R.id.title);
        comment_cancel = findViewById(R.id.ca_img_left_arrow);
        comments = findViewById(R.id.comments);
        image = findViewById(R.id.image);
        msg = findViewById(R.id.msg);
        send = findViewById(R.id.send);
        progress = findViewById(R.id.progress);
        loader_gif = findViewById(R.id.loader);
    }

    private void setPagination(RecyclerView mRvRecyclerView, LinearLayoutManager manager) {
        mRvRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Utils.hideKeyboard(CommentsActivity.this, mRvRecyclerView);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        mRvRecyclerView.addOnScrollListener(new PaginationListener(manager) {
            @Override
            protected void loadMoreItems() {
                if (!TextUtils.isEmpty(article_id)) {
                    presenter.comments(article_id, "", nextPage, false);
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
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

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            isLoading = true;
            progress.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        isLoading = false;
        send.setEnabled(true);
        Utils.showPopupMessageWithCloseButton(this, 2000, error, true);
    }

    @Override
    public void error404(String error) {
        isLoading = false;
        send.setEnabled(true);
        Utils.showPopupMessageWithCloseButton(this, 2000, error, true);
    }

    @Override
    public void success(CommentResponse commentResponse, boolean refresh) {
        isLoading = false;
        if (commentResponse != null) {
            if (commentResponse.getMeta() != null) {
                nextPage = commentResponse.getMeta().getNext();
                if (TextUtils.isEmpty(nextPage)) {
                    isLastPage = true;
                }
            }
            if (refresh) {
                this.commentsList.clear();
                if (msg != null)
                    msg.setText("");
                Utils.hideKeyboard(CommentsActivity.this, msg);
            }
            if (commentResponse.getmComment() != null && commentResponse.getmComment().size() > 0) {
                this.commentsList.addAll(commentResponse.getmComment());
                updateTitle();
                if (commentsAdapter != null)
                    commentsAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateTitle() {
        if (commentsList.size() > 0) {
            title.setText(commentsList.size() + (commentsList.size() > 1 ? " Comments" : " Comment"));
        }
    }

    @Override
    public void successPagination(CommentResponse commentResponse) {

    }

    @Override
    public void success(Comment comment) {
        send.setEnabled(true);
        if (comment != null) {
            this.commentsList.add(0, comment);
            if (commentsAdapter != null)
                commentsAdapter.notifyDataSetChanged();
            if (msg != null)
                msg.setText("");
            updateTitle();
            Utils.hideKeyboard(this, msg);
        }
    }

    @Override
    protected void onDestroy() {
        Utils.hideKeyboard(this, msg);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == LoginPopupActivity.LOGIN_WITH_EMAIL){
            checkLoggedIn();
        }
    }
}