package com.ziro.bullet.bottomSheet;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
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
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyBottomSheetDialog extends BottomSheetDialogFragment implements CommentInterface {
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
    private CommentsBottomSheetDialog commentsBottomSheetBehavior;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    public static ReplyBottomSheetDialog newInstance(String articleId, String parentId, String type, String commentJson, CommentsBottomSheetDialog commentsBottomSheetBehavior) {
        ReplyBottomSheetDialog fragment = new ReplyBottomSheetDialog();

        Bundle args = new Bundle();
        args.putString("article_id", articleId);
        args.putString("parent_id", parentId);
        args.putString("type", type);
        args.putString("comment", commentJson);
        fragment.setArguments(args);
        fragment.commentsBottomSheetBehavior = commentsBottomSheetBehavior;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reply, container, false);
        bindViews(view);
        getBundle();
        init();
        listener();
        setData();
        RelativeLayout bottomSheetView = view.findViewById(R.id.reply_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (commentsBottomSheetBehavior != null) {
                        commentsBottomSheetBehavior.dismiss();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (commentsBottomSheetBehavior != null) {
//                    View commentsView = commentsBottomSheetBehavior.getView();
//                    if (commentsView != null) {
//                        int translationY = (int) (slideOffset * bottomSheet.getHeight());
//                        commentsView.setTranslationY(translationY);

                    commentsBottomSheetBehavior.dismiss();
                }
            }
        });
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assuming replyBottomSheetBehavior is a ReplyBottomSheetBehavior instance
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    private void getBundle() {
        if (getArguments() != null) {

            article_id = getArguments().getString("article_id");
            parent_id = getArguments().getString("parent_id");
            main_parent_id = getArguments().getString("parent_id");
            type = getArguments().getString("type");
            if (!TextUtils.isEmpty(getArguments().getString("comment"))) {
                comment = new Gson().fromJson(getArguments().getString("comment"), Comment.class);
                if (comment != null && comment.getUser() != null) {
                    editorReplyMain.setVisibility(View.VISIBLE);
                    editorReplyName.setText(comment.getUser().getName());
                    msg.requestFocus();
                    Utils.showKeyboard(requireActivity());
                }
            }
        }
    }

    private void init() {
        prefConfig = new PrefConfig(requireActivity());
        presenter = new CommentPresenter(requireActivity(), this);
        commentsAdapter = new CommentsAdapter(commentsList, requireActivity(), 1, new CommentsAdapter.CommentCallback() {

            @Override
            public void onParentReply(int position, Comment comment, String type, CommentsAdapter adapter, ArrayList<Comment> comments) {
                isParent = true;
                if (comment != null) {
                    if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("comment")) {
                        parent_id = comment.getId();
                        msg.requestFocus();
                        Utils.showKeyboard(requireActivity());
                    }

                    if (comment.getUser() != null) {
                        editorReplyMain.setVisibility(View.VISIBLE);
                        editorReplyName.setText(comment.getUser().getName());
                    }
                }
            }

            @Override
            public void onChildReply(int position, Comment comment, String type, RepliesAdapter adapter, ArrayList<Comment> childItems) {
//                isParent = false;
//                repliesAdapterInner = adapter;
//                repliesAdapterListInner = childItems;
//                if (comment != null) {
//                    if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("comment")) {
//                        parent_id = comment.getId();
//                        msg.requestFocus();
//                        Utils.showKeyboard(requireActivity());
//                    }
//                    if (comment.getUser() != null) {
//                        editorReplyMain.setVisibility(View.VISIBLE);
//                        editorReplyName.setText(comment.getUser().getName());
//                    }
//                }
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
                Utils.showKeyboard(requireActivity());
                if (comment != null) {
                    if (comment.getUser() != null) {
                        editorReplyMain.setVisibility(View.VISIBLE);
                        editorReplyName.setText(name);
                    }
                }
            }

        }, article_id);
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        comments.setLayoutManager(manager);
        comments.setAdapter(commentsAdapter);

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
                    commentPopup = new CommentPopup(requireActivity());
                } else {
                    commentPopup.showDialog(getString(R.string.before_you_can_comment_you_need_to_update_your_profile));
                }
            }
        });
        comment_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentsBottomSheetBehavior != null) {
                    commentsBottomSheetBehavior.dismiss();
                }
                dismiss();
            }
        });


        editorReplyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditor();
            }
        });
    }

    private void clearEditor() {
        if (msg != null)
            msg.setText("");
        Utils.hideKeyboard(requireActivity(), msg);
        editorReplyMain.setVisibility(View.GONE);
        editorReplyName.setText("");
        parent_id = "";
        parent_id = main_parent_id;
        isParent = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bottomSheetBehavior!=null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

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
        editorReplyName = view.findViewById(R.id.editorReplyName);
        editorReplyCancel = view.findViewById(R.id.editorReplyCancel);
        editorReplyMain = view.findViewById(R.id.editorReplyMain);
        title = view.findViewById(R.id.title);
        comment_cancel = view.findViewById(R.id.btn_close);
        comments = view.findViewById(R.id.recycleViewComments);
        image = view.findViewById(R.id.image);
        msg = view.findViewById(R.id.msg);
        send = view.findViewById(R.id.send);
        progress = view.findViewById(R.id.progress);
        loader_gif = view.findViewById(R.id.loader);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up any resources or listeners when the bottom sheet is destroyed
        // ...
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

    private void updateTitle() {
        if (commentsList.size() > 0) {
            title.setText(commentsList.size() + (commentsList.size() > 1 ? " Comments" : " Comment"));
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

    // Add any additional methods or listeners as needed

}
