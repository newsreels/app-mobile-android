package com.ziro.bullet.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ziro.bullet.R;

import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.Locale;

public class SearchAuthorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_TITLE = 1;
    public static final int TYPE_AUTHOR = 2;

    private final Activity mContext;
    private ArrayList<Author> authorArrayList;
    private SearchTopicsAdapter.FollowingCallback mCallback;
    private String title;

    public SearchAuthorAdapter(Activity mContext, ArrayList<Author> authorArrayList) {
        this.mContext = mContext;
        this.authorArrayList = authorArrayList;
        title = mContext.getString(R.string.results);
    }

    public void setCallback(SearchTopicsAdapter.FollowingCallback callback) {
        this.mCallback = callback;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if(viewType == TYPE_TITLE){
            view = LayoutInflater.from(mContext).inflate(R.layout.author_item_title, parent, false);
            return new TitleViewHolder(view);
        }else{
            view = LayoutInflater.from(mContext).inflate(R.layout.author_search_item, parent, false);
            return new AuthorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AuthorViewHolder) {
            AuthorViewHolder authorViewHolder = (AuthorViewHolder) holder;
            position = position - 1;
            if (authorArrayList != null && authorArrayList.size() > 0) {
                Author author = authorArrayList.get(position);
                if (author != null) {
                    Glide.with(authorViewHolder.authorImage).load(author.getProfile_image()).into(authorViewHolder.authorImage);
                    authorViewHolder.username.setText(author.getUsername());
                    authorViewHolder.authorName.setText(String.format("%s %s", author.getFirst_name(), author.getLast_name()));
                    authorViewHolder.followCount.setText(String.format(Locale.getDefault(), "%d %s", author.getFollower_count(), mContext.getString(R.string.followers)));
                    authorViewHolder.item.setOnClickListener(v -> {
                        Utils.openAuthor(mContext, author);
                    });
                    showVerifiedIcon(authorViewHolder, author);
                }
            }
        }else if(holder instanceof TitleViewHolder) {
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            titleViewHolder.title.setText(title);
        }
    }

    private void showVerifiedIcon(AuthorViewHolder holder, Author sourceObj) {
        if (sourceObj.isVerified()) {
            if (Utils.isRTL()) {
                holder.authorName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified, 0, 0, 0);
            } else {
                holder.authorName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0);
            }
        }
    }


    @Override
    public int getItemCount() {
        return authorArrayList.size() != 0 ? authorArrayList.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_TITLE;
        }else{
            return TYPE_AUTHOR;
        }
    }

    static class AuthorViewHolder extends RecyclerView.ViewHolder {

        private final ImageView authorImage;
        private final TextView username;
        private final TextView authorName;
        private final TextView followCount;
        private final ConstraintLayout item;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            authorImage = itemView.findViewById(R.id.author_image);
            username = itemView.findViewById(R.id.username);
            authorName = itemView.findViewById(R.id.author_name);
            followCount = itemView.findViewById(R.id.follow_count);
        }
    }

    public static class TitleViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }

}
