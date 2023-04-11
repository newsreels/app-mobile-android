package com.ziro.bullet.adapters.comment;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.model.comment.Comment;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private RecyclerView replies;
    private CircleImageView image;
    private RelativeLayout line;
    private TextView name;
    private TextView commentTv;
    private TextView time;
    private TextView reply;
    private RelativeLayout replyMain;
    private RelativeLayout line2;
    private CircleImageView reply_image;
    private TextView reply_name;
    private TextView reply_comment;
    private RelativeLayout viewMoreMain;
    private TextView view_more;
    private Activity mContext;
    private String article_id;
    private ArrayList<Comment> commentArrayList;
    private CommentsAdapter adapter;

    public CommentViewHolder(View itemView, Activity context, String article_id, ArrayList<Comment> commentArrayList, CommentsAdapter adapter) {
        super(itemView);
        this.commentArrayList = commentArrayList;
        this.adapter = adapter;
        this.mContext = context;
        this.article_id = article_id;
        image = itemView.findViewById(R.id.image);
        line = itemView.findViewById(R.id.line);
        name = itemView.findViewById(R.id.name);
        commentTv = itemView.findViewById(R.id.comment);
        time = itemView.findViewById(R.id.time);
        reply = itemView.findViewById(R.id.reply);
        replyMain = itemView.findViewById(R.id.rl_comment);
        line2 = itemView.findViewById(R.id.line2);
        reply_image = itemView.findViewById(R.id.reply_image);
        reply_name = itemView.findViewById(R.id.reply_name);
        reply_comment = itemView.findViewById(R.id.reply_comment);
        viewMoreMain = itemView.findViewById(R.id.viewMoreMain);
        view_more = itemView.findViewById(R.id.view_more);
        replies = itemView.findViewById(R.id.replies);
    }

    public void bind(int position, Comment comment, int viewType, CommentsAdapter.CommentCallback callback) {

        //Default state
        line.setVisibility(View.INVISIBLE);
        replyMain.setVisibility(View.GONE);
        replies.setVisibility(View.GONE);
        line2.setVisibility(View.GONE);
        viewMoreMain.setVisibility(View.GONE);

        if (comment.getUser() != null) {
            if (!TextUtils.isEmpty(comment.getUser().getName())) {
                name.setText(comment.getUser().getName());
            }
            if (!TextUtils.isEmpty(comment.getUser().getImage())) {
                Picasso.get()
                        .load(comment.getUser().getImage())
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .into(image);
            } else {
                image.setImageResource(R.drawable.ic_placeholder_user);
            }
        }

        if (!TextUtils.isEmpty(comment.getComment())) {
            commentTv.setText(comment.getComment());
        }

        if (!TextUtils.isEmpty(comment.getCreatedAt())) {
            String ago = Utils.getTimeAgoLikeFb(Utils.getDate(comment.getCreatedAt()));
            time.setText(ago);
        }

        reply.setOnClickListener(v -> {
            if (callback != null)
                callback.onParentReply(position, comment, "comment", adapter, commentArrayList);
        });

        if (comment.getChild() != null && comment.getChild().size() > 0) {
            line.setVisibility(View.VISIBLE);

            switch (viewType) {
                case 0:
                    //Comment
                    replyMain.setVisibility(View.VISIBLE);
                    replyMain.setOnClickListener(v -> {
                        if (callback != null)
                            callback.onParentReply(position, comment, "view_more", adapter, commentArrayList);
                    });
                    replies.setVisibility(View.GONE);
                    Comment child = comment.getChild().get(0);
                    if (child != null) {
                        if (child.getUser() != null) {
                            if (!TextUtils.isEmpty(child.getUser().getName())) {
                                reply_name.setText(child.getUser().getName());
                            }
                            if (!TextUtils.isEmpty(child.getUser().getImage())) {
                                Picasso.get()
                                        .load(child.getUser().getImage())
                                        .resize(Constants.targetWidth, Constants.targetHeight)
                                        .into(reply_image);
                            } else {
                                reply_image.setImageResource(R.drawable.ic_placeholder_user);
                            }
                        }

                        if (!TextUtils.isEmpty(child.getComment())) {
                            reply_comment.setText(child.getComment());
                        }
                    }
                    break;
                case 1:
                    //Replies
                    replyMain.setVisibility(View.GONE);
                    replies.setVisibility(View.VISIBLE);
                    ArrayList<Comment> childItems = comment.getChild();
                    RepliesAdapter repliesAdapter = new RepliesAdapter(childItems, mContext, 0, article_id, position);
                    repliesAdapter.setListener(new RepliesAdapter.ReplyCallback() {
                        @Override
                        public void onReply(int layer, int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                            if (callback != null)
                                callback.onChildReply(position, child, "comment", adapter, childItemsAdapter);
                        }

                        @Override
                        public void onViewMore(int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {

                        }

                        @Override
                        public void onPagination(int positionChild, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                            if (repliesAdapter != null && comment != null && comment.getChild() != null && positionChild < comment.getChild().size()) {
                                comment.getChild().get(positionChild).setMoreComment(child.getMoreComment());
                                repliesAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onWriteReply(int layer, int positionChild, String parentName, String childName, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                            Log.e("onWriteReply", "=========================");
                            Log.e("onWriteReply", "===parentName=== : " + parentName);
                            Log.e("onWriteReply", "===childName=== : " + childName);
                            Log.e("onWriteReply", "===layer=== : " + layer);
                            if (callback != null && comment != null && comment.getChild() != null && positionChild < comment.getChild().size()) {
                                Comment child3rd = comment.getChild().get(positionChild);
                                if (child3rd != null && child3rd.getUser() != null) {
                                    String name = parentName;
                                    if (layer == 1) {
                                        name = childName;
                                    }
                                    callback.updateParentId(name, child3rd, adapter, childItemsAdapter);
                                }
                            }
                        }
                    });
                    LinearLayoutManager manager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                    replies.setLayoutManager(manager);
                    replies.setAdapter(repliesAdapter);
                    break;
            }

            if (comment.getMoreComment() > 0) {
                line2.setVisibility(View.VISIBLE);
                viewMoreMain.setVisibility(View.VISIBLE);
                view_more.setOnClickListener(v -> {
                    if (callback != null)
                        callback.onParentReply(position, comment, "view_more", adapter, commentArrayList);
                });

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(mContext.getString(R.string.view));
                stringBuilder.append(" ");
                stringBuilder.append(comment.getMoreComment());
                stringBuilder.append(" ");
                stringBuilder.append(mContext.getString(R.string.more));
                stringBuilder.append(" ");
                if (comment.getMoreComment() > 1) {
                    stringBuilder.append(mContext.getString(R.string.replies));
                } else {
                    stringBuilder.append(mContext.getString(R.string.reply));
                }
                stringBuilder.append("...");
                view_more.setText(stringBuilder);
            } else {
                view_more.setText("");
                line2.setVisibility(View.GONE);
                viewMoreMain.setVisibility(View.GONE);
            }

        } else {
            line.setVisibility(View.INVISIBLE);
            replyMain.setVisibility(View.GONE);
        }
    }

}
