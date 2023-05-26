package com.ziro.bullet.bottomSheet;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.ziro.bullet.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.LoginPopupActivity;
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
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsBottomSheetDialog extends BottomSheetDialogFragment implements CommentInterface {
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
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private ImageView loader_gif;
    private ImageView btnClose;


    private AppCompatImageButton heart;
    private AppCompatImageButton handsUp;
    private AppCompatImageButton fire;
    private AppCompatImageButton clap;
    private AppCompatImageButton sad;
    private AppCompatImageButton inlove;
    private AppCompatImageButton shock;
    private AppCompatImageButton laugh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_comments, container, false);

//        RelativeLayout bottomSheetView = view.findViewById(R.id.comments_bottom_sheet);
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);

        getBundle();
        bindViews(view);
        init();
        listener();
        checkLoggedIn();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void checkLoggedIn() {
        if(prefConfig.isGuestUser()){
            msg.setOnClickListener(v -> LoginPopupActivity.start(requireActivity()));
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
    private void getBundle() {
        if (getArguments() != null) {
            article_id = getArguments().getString("article_id");
            position = getArguments().getInt("position", -1);
        }
    }
    private void init() {
        prefConfig = new PrefConfig(requireActivity());
        presenter = new CommentPresenter(requireActivity(), this);
        commentsAdapter = new CommentsAdapter(commentsList, requireActivity(), 0, new CommentsAdapter.CommentCallback() {
            @Override
            public void onParentReply(int position, Comment comment, String type, CommentsAdapter adapter, ArrayList<Comment> comments) {
                if (!TextUtils.isEmpty(type)) {


// Show the ReplyBottomSheetDialog and pass the commentsBottomSheetBehavior
                    ReplyBottomSheetDialog replyDialogFragment = ReplyBottomSheetDialog.newInstance(article_id, comment.getId(), type, new Gson().toJson(comment), CommentsBottomSheetDialog.this);
                    replyDialogFragment.show(getParentFragmentManager(), "reply_dialog");


                }
            }

            @Override
            public void onChildReply(int position, Comment comment, String type, RepliesAdapter adapter, ArrayList<Comment> childItems) {
                if (!TextUtils.isEmpty(type)) {
                    ReplyBottomSheetDialog replyDialogFragment = ReplyBottomSheetDialog.newInstance(article_id, comment.getId(), type, new Gson().toJson(comment), CommentsBottomSheetDialog.this);
                    replyDialogFragment.show(getParentFragmentManager(), "reply_dialog");
                }


            }

            @Override
            public void onPagination(int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                if (presenter != null)
                    presenter.comments(article_id, child.getId(), "", adapter, childItemsAdapter);
            }

            @Override
            public void updateParentId(String name, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {

            }
            // Rest of the code
        }, article_id);
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        comments.setLayoutManager(manager);
        comments.setAdapter(commentsAdapter);
        setPagination(comments, manager);

    }
    private void setPagination(RecyclerView mRvRecyclerView, LinearLayoutManager manager) {
        mRvRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Utils.hideKeyboard(requireActivity(), mRvRecyclerView);
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
                    commentPopup = new CommentPopup(requireActivity());
                } else {
                    commentPopup.showDialog(getString(R.string.before_you_can_comment_you_need_to_update_your_profile));
                }
            }
        });
        btnClose.setOnClickListener(v ->dismiss());
        comment_cancel.setOnClickListener(v ->dismiss());
    }

    //    @Override
//    public void onBackPressed() {
//        Constants.notishare = false;
//        Constants.onResumeReels = true;
//        Intent intent = new Intent();
//        intent.putExtra("id", article_id);
//        intent.putExtra("position", position);
//        requireActivity().setResult(Activity.RESULT_OK, intent);
//        dismiss();
//    }
    private void bindViews(View view) {
        heart = view.findViewById(R.id.imgHeart);
        btnClose = view.findViewById(R.id.btn_close);
        handsUp = view.findViewById(R.id.imgHands);
        fire = view.findViewById(R.id.imgFire);
        clap = view.findViewById(R.id.clapImage);
        sad = view.findViewById(R.id.imgSad);
        inlove = view.findViewById(R.id.imgSmiley);
        shock = view.findViewById(R.id.imgShock);
        laugh = view.findViewById(R.id.img_laugh);
        title = view.findViewById(R.id.title);
        comment_cancel = view.findViewById(R.id.ca_img_left_arrow);
        comments = view.findViewById(R.id.comments);
        image = view.findViewById(R.id.image);
        msg = view.findViewById(R.id.msg);
        send = view.findViewById(R.id.send);
        progress = view.findViewById(R.id.progress);
        loader_gif = view.findViewById(R.id.loader);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> Utils.hideKeyboard(requireActivity(), msg),200);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up any resources or listeners when the bottom sheet is destroyed
        // ...
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
        Utils.showPopupMessageWithCloseButton(requireActivity(), 2000, error, true);
    }

    @Override
    public void error404(String error) {
        isLoading = false;
        send.setEnabled(true);
        Utils.showPopupMessageWithCloseButton(requireActivity(), 2000, error, true);
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
                Utils.hideKeyboard(requireActivity(), msg);
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
            Utils.hideKeyboard(requireActivity(), msg);
        }
    }

    // Add any additional methods or listeners as needed

}
