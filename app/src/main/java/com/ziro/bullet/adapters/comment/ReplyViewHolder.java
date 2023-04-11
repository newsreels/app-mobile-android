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
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.comment.Comment;
import com.ziro.bullet.model.comment.CommentResponse;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplyViewHolder extends RecyclerView.ViewHolder {

    private RecyclerView replies;
    private CircleImageView image;
    private CircleImageView image2;
    private RelativeLayout line;
    private RelativeLayout writeReply;
    private TextView name;
    private TextView commentTv;
    private TextView time;
    private TextView reply;
    private TextView view_more;
    private Activity mContext;
    private RelativeLayout viewMoreMain;
    private PrefConfig mPrefconfig;
    private String article_id;

    public ReplyViewHolder(View itemView, Activity context, String article_id) {
        super(itemView);
        this.mContext = context;
        this.article_id = article_id;
        mPrefconfig = new PrefConfig(mContext);
        writeReply = itemView.findViewById(R.id.rl_reply);
        view_more = itemView.findViewById(R.id.view_more);
        image = itemView.findViewById(R.id.image);
        image2 = itemView.findViewById(R.id.image2);
        line = itemView.findViewById(R.id.line);
        name = itemView.findViewById(R.id.name);
        commentTv = itemView.findViewById(R.id.comment);
        time = itemView.findViewById(R.id.time);
        reply = itemView.findViewById(R.id.reply);
        replies = itemView.findViewById(R.id.replies);
        viewMoreMain = itemView.findViewById(R.id.viewMoreMain);
    }


    public void bind(int position, Comment child, int viewType, RepliesAdapter.ReplyCallback callback, int parentPosition) {

        //Default state
        line.setVisibility(View.INVISIBLE);
        replies.setVisibility(View.GONE);
        viewMoreMain.setVisibility(View.GONE);
        writeReply.setVisibility(View.GONE);

        if (child.getUser() != null) {
            if (!TextUtils.isEmpty(child.getUser().getName())) {
                name.setText(child.getUser().getName());
            }
            if (!TextUtils.isEmpty(child.getUser().getImage())) {
                Picasso.get()
                        .load(child.getUser().getImage())
                        .into(image);
                Picasso.get()
                        .load(child.getUser().getImage())
                        .into(image2);
            } else {
                image.setImageResource(R.drawable.ic_placeholder_user);
                image2.setImageResource(R.drawable.ic_placeholder_user);
            }
        }

        if (!TextUtils.isEmpty(child.getComment())) {
            commentTv.setText(child.getComment());
        }

        if (!TextUtils.isEmpty(child.getCreatedAt())) {
            String ago = Utils.getTimeAgoLikeFb(Utils.getDate(child.getCreatedAt()));
            time.setText(ago);
        }

        if (child.getChild() != null && child.getChild().size() > 0) {
            line.setVisibility(View.VISIBLE);
        } else {
            line.setVisibility(View.INVISIBLE);
        }

        if (child.getChild() == null) {
            ArrayList<Comment> childItems = new ArrayList<>();
            child.setChild(childItems);
        }
        ArrayList<Comment> childItems = child.getChild();
        RepliesAdapter repliesAdapter = new RepliesAdapter(childItems, mContext, 1, article_id, parentPosition);

        if (child.getChild() != null && child.getChild().size() > 0) {
            line.setVisibility(View.VISIBLE);
            replies.setVisibility(View.VISIBLE);

            repliesAdapter.setListener(new RepliesAdapter.ReplyCallback() {

                @Override
                public void onReply(int layer, int position, Comment childInner, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                    if (callback != null && child.getUser() != null && childInner.getUser() != null)
                        callback.onWriteReply(layer, parentPosition, child.getUser().getName(), childInner.getUser().getName(), childInner, repliesAdapter, childItems);
                }

                @Override
                public void onViewMore(int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                    if (callback != null)
                        callback.onViewMore(position, child, adapter, childItemsAdapter);
                }

                @Override
                public void onPagination(int position, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {

                }

                @Override
                public void onWriteReply(int layer, int position, String parentName, String childName, Comment childInner, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter) {
                    if (callback != null && child.getUser() != null)
                        callback.onWriteReply(layer, parentPosition, child.getUser().getName(), childName, childInner, repliesAdapter, childItems);
                }
            });
            LinearLayoutManager manager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
            replies.setLayoutManager(manager);
            replies.setAdapter(repliesAdapter);

            if (child.getMoreComment() > 0) {
                writeReply.setVisibility(View.GONE);
                line.setVisibility(View.VISIBLE);
                viewMoreMain.setVisibility(View.VISIBLE);
                view_more.setOnClickListener(v -> {
                    comments(article_id, child, repliesAdapter, childItems, callback, parentPosition);
                });

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(mContext.getString(R.string.view));
                stringBuilder.append(" ");
                stringBuilder.append(child.getMoreComment());
                stringBuilder.append(" ");
                stringBuilder.append(mContext.getString(R.string.more));
                stringBuilder.append(" ");
                if (child.getMoreComment() > 1) {
                    stringBuilder.append(mContext.getString(R.string.replies));
                } else {
                    stringBuilder.append(mContext.getString(R.string.reply));
                }
                stringBuilder.append("...");
                view_more.setText(stringBuilder);
            } else {
//                line.setVisibility(View.INVISIBLE); // check this case if it is fine
                writeReply.setVisibility(View.VISIBLE);
                viewMoreMain.setVisibility(View.GONE);
                writeReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //3rd layer click
                        if (callback != null && child.getUser() != null)
                            callback.onWriteReply(viewType, parentPosition, child.getUser().getName(), "childName", child, repliesAdapter, childItems);

                    }
                });
            }

        } else {
            line.setVisibility(View.INVISIBLE);
        }

        reply.setOnClickListener(v -> {
            if (callback != null)
                callback.onReply(viewType, position, child, repliesAdapter, childItems);
        });

    }

    public void comments(String article_id, Comment child, RepliesAdapter adapter, ArrayList<Comment> childItemsAdapter, RepliesAdapter.ReplyCallback callback, int parentPosition) {
        Log.e("CALLXSXS", "ACCESS TOKEN : " + mPrefconfig.getAccessToken());
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");
        Log.e("CALLXSXS", "article_id : " + article_id);
        Log.e("CALLXSXS", "parent_id : " + child.getId());
        Log.e("CALLXSXS", "getMoreComment : " + child.getMoreComment());
        Log.e("CALLXSXS", "page : " + child.getPage());
        Log.e("CALLXSXS", "++++++++++++++++++++++++++++++");

        try {
            if (!InternetCheckHelper.isConnected()) {
//                commentInterface.error(activity.getString(R.string.internet_error));
                return;
            } else {

                Call<CommentResponse> call = ApiClient
                        .getInstance(mContext)
                        .getApi()
                        .comments("Bearer " + mPrefconfig.getAccessToken(), article_id, child.getId(), child.getPage());
                call.enqueue(new Callback<CommentResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CommentResponse> call, @NotNull Response<CommentResponse> response) {
                        Log.e("RESPONSE", "RES: " + response.body());
                        if (response.body() != null) {
                            if (childItemsAdapter != null && adapter != null) {
                                if (TextUtils.isEmpty(child.getPage()))
                                    childItemsAdapter.clear();

                                if (response.body().getMeta() != null) {
                                    child.setPage(response.body().getMeta().getNext());
                                }
                                if (response.body().getParent() != null) {
                                    child.setMoreComment(response.body().getParent().getMoreComment());
                                    Log.e("CALLXSXS", "new getMoreComment : " + child.getMoreComment());
                                    if (callback != null)
                                        callback.onPagination(parentPosition, child, adapter, childItemsAdapter);
                                }
                                if (response.body().getmComment() != null && response.body().getmComment().size() > 0) {
                                    childItemsAdapter.addAll(response.body().getmComment());
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommentResponse> call, @NotNull Throwable t) {
                        Log.e("RESPONSE", "RES: " + t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            Log.e("RESPONSE", "RES: " + t.getMessage());
        }
    }

}
