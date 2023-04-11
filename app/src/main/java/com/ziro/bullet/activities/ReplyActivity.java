package com.ziro.bullet.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

public class ReplyActivity extends BaseActivity implements CommentInterface {

    private TextView title;
    private TextView editorReplyName;
    private TextView editorReplyCancel;
    private RelativeLayout editorReplyMain;
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
    private String type = "";
    private String parent_id = "";
    private String main_parent_id = "";
    private String article_id = "";
    private Comment comment = null;
    private boolean isParent = true;
    private RepliesAdapter repliesAdapterInner;
    private ArrayList<Comment> repliesAdapterListInner;
    private CommentPopup commentPopup;
    private PrefConfig prefConfig;
    private RelativeLayout progress;
    private ImageView loader_gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        // Utils.checkAppModeColor(this);
        setContentView(R.layout.activity_reply);
        bindViews();
        getBundle();
        init();
        listener();
        setData();
    }

    private void setData() {
        presenter.comments(article_id, parent_id, nextPage, false);
        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("comment")) {
            msg.requestFocus();
        }
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
            parent_id = getIntent().getStringExtra("parent_id");
            main_parent_id = getIntent().getStringExtra("parent_id");
            type = getIntent().getStringExtra("type");
            if (!TextUtils.isEmpty(getIntent().getStringExtra("comment"))) {
                comment = new Gson().fromJson(getIntent().getStringExtra("comment"), Comment.class);
                if (comment != null && comment.getUser() != null) {
                    editorReplyMain.setVisibility(View.VISIBLE);
                    editorReplyName.setText(comment.getUser().getName());
                    msg.requestFocus();
                    Utils.showKeyboard(ReplyActivity.this);
                }
            }
        }
    }

    private void listener() {
        send.setOnClickListener(v -> {
            if (prefConfig != null && prefConfig.isUserObject() != null && prefConfig.isUserObject().isSetup()) {
                String comment = msg.getText().toString();
                if (!TextUtils.isEmpty(comment)) {
                    Log.e("comment", "comment: " + comment);
                    Log.e("comment", "article id : " + article_id);
                    Log.e("comment", "parent id : " + parent_id);
                    if (presenter != null) {
                        send.setEnabled(false);
                        presenter.createComment(article_id, parent_id, comment);
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

        comment_cancel.setOnClickListener(v -> finish());
        editorReplyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditor();
            }
        });
    }

    private void init() {
        prefConfig = new PrefConfig(this);
        presenter = new CommentPresenter(this, this);
        commentsAdapter = new CommentsAdapter(commentsList, this, 1, new CommentsAdapter.CommentCallback() {

            @Override
            public void onParentReply(int position, Comment comment, String type, CommentsAdapter adapter, ArrayList<Comment> comments) {
                isParent = true;
                if (comment != null) {
                    if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("comment")) {
                        parent_id = comment.getId();
                        msg.requestFocus();
                        Utils.showKeyboard(ReplyActivity.this);
                    }

                    if (comment.getUser() != null) {
                        editorReplyMain.setVisibility(View.VISIBLE);
                        editorReplyName.setText(comment.getUser().getName());
                    }
                }
            }

            @Override
            public void onChildReply(int position, Comment comment, String type, RepliesAdapter adapter, ArrayList<Comment> childItems) {
                isParent = false;
                repliesAdapterInner = adapter;
                repliesAdapterListInner = childItems;
                if (comment != null) {
                    if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("comment")) {
                        parent_id = comment.getId();
                        msg.requestFocus();
                        Utils.showKeyboard(ReplyActivity.this);
                    }
                    if (comment.getUser() != null) {
                        editorReplyMain.setVisibility(View.VISIBLE);
                        editorReplyName.setText(comment.getUser().getName());
                    }
                }
            }

            @Override
            public void onPagination(int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                if (presenter != null)
                    presenter.comments(article_id, child.getId(), "", adapter, childItemsAdapter);
            }

            @Override
            public void updateParentId(String name, Comment comment, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                isParent = false;
                repliesAdapterInner = adapter;
                repliesAdapterListInner = childItemsAdapter;
                parent_id = comment.getId();
                msg.requestFocus();
                Utils.showKeyboard(ReplyActivity.this);
                if (comment != null) {
                    if (comment.getUser() != null) {
                        editorReplyMain.setVisibility(View.VISIBLE);
                        editorReplyName.setText(name);
                    }
                }
            }

        }, article_id);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        comments.setLayoutManager(manager);
        comments.setAdapter(commentsAdapter);
//        setTouchListener(comments, manager);

//        Glide.with(this)
//                .load(Utils.getLoaderForTheme(prefConfig.getAppTheme()))
//                .into(loader_gif);
    }

    private void setTouchListener(RecyclerView mRvRecyclerView, LinearLayoutManager manager) {
        mRvRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Utils.hideKeyboard(ReplyActivity.this, mRvRecyclerView);
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
                isLoading = true;
                if (!TextUtils.isEmpty(article_id) && !TextUtils.isEmpty(parent_id)) {
                    presenter.comments(article_id, parent_id, nextPage, false);
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

    private void bindViews() {
        editorReplyName = findViewById(R.id.editorReplyName);
        editorReplyCancel = findViewById(R.id.editorReplyCancel);
        editorReplyMain = findViewById(R.id.editorReplyMain);

        title = findViewById(R.id.title);
        comment_cancel = findViewById(R.id.btn_close);
        comments = findViewById(R.id.recycleViewComments);
        image = findViewById(R.id.image);
        msg = findViewById(R.id.msg);
        send = findViewById(R.id.send);
        progress = findViewById(R.id.progress);
        loader_gif = findViewById(R.id.loader);
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
        isLoading = false;
        send.setEnabled(true);
    }

    @Override
    public void error404(String error) {
        isLoading = false;
        send.setEnabled(true);
    }

    @Override
    public void success(Comment comment) {
        send.setEnabled(true);
        isLoading = false;
        if (isParent) {
            if (commentsList != null && this.commentsList.size() > 0 && commentsAdapter != null) {
                if (this.commentsList.get(0).getChild() != null) {
                    this.commentsList.get(0).getChild().add(0, comment);
                } else {
                    ArrayList<Comment> child = new ArrayList<>();
                    child.add(comment);
                    this.commentsList.get(0).setChild(child);
                }
                commentsAdapter.notifyDataSetChanged();
            }
        } else {
            if (repliesAdapterInner != null) {
                if (repliesAdapterListInner == null) {
                    repliesAdapterListInner = new ArrayList<>();
                    repliesAdapterListInner.add(comment);
                    repliesAdapterInner.notifyDataSetChanged();
                } else {
                    repliesAdapterListInner.add(comment);
                    repliesAdapterInner.notifyDataSetChanged();
                }
                if (commentsAdapter != null)
                    commentsAdapter.notifyDataSetChanged();
            }
        }
        clearEditor();
    }

    private void clearEditor() {
        if (msg != null)
            msg.setText("");
        Utils.hideKeyboard(this, msg);
        editorReplyMain.setVisibility(View.GONE);
        editorReplyName.setText("");
        parent_id = "";
        parent_id = main_parent_id;
        isParent = true;
    }

    @Override
    public void success(CommentResponse commentResponse, boolean refresh) {
        isLoading = false;
        if (commentResponse != null) {
            if (refresh) {
                this.commentsList.clear();
                clearEditor();
            }

            if (commentResponse.getmComment() != null && commentResponse.getmComment().size() > 0) {
                if (commentResponse.getParent() != null) {
                    commentResponse.getParent().setChild(commentResponse.getmComment());
                    this.commentsList.add(commentResponse.getParent());
                }
            } else {
                if (commentResponse.getParent() != null) {
                    this.commentsList.add(commentResponse.getParent());
                }
            }
            commentsAdapter.notifyDataSetChanged();

            if (commentResponse.getMeta() != null) {
                nextPage = commentResponse.getMeta().getNext();
                if (TextUtils.isEmpty(nextPage)) {
                    isLastPage = true;
                } else {
                    presenter.commentPagination(article_id, parent_id, nextPage);
                }
            }

        }
    }

    @Override
    public void successPagination(CommentResponse commentResponse) {
        if (commentResponse != null) {

            if (commentResponse.getParent() != null && commentResponse.getmComment() != null) {

                ArrayList<Comment> comments = this.commentsList.get(0).getChild();
                comments.addAll(commentResponse.getmComment());
                commentResponse.getParent().setChild(comments);
                this.commentsList.clear();
                this.commentsList.add(commentResponse.getParent());

                commentsAdapter.notifyDataSetChanged();

                if (commentResponse.getMeta() != null) {
                    nextPage = commentResponse.getMeta().getNext();
                    if (TextUtils.isEmpty(nextPage)) {
                        isLastPage = true;
                    } else {
                        presenter.commentPagination(article_id, parent_id, nextPage);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Utils.hideKeyboard(this, msg);
        super.onDestroy();
    }
}