package com.ziro.bullet.adapters.followers;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private final Context context;
    private final ArrayList<Author> followersUserArrayList;
    private boolean isSearchResult;

    public FollowersAdapter(ArrayList<Author> followersUserArrayList, Context context) {
        this.followersUserArrayList = followersUserArrayList;
        this.context = context;
    }

    public void setSearchResult(boolean searchResult) {
        isSearchResult = searchResult;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followers_adapter_header_item, parent, false);
            return new FollowersHeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followers_adapter_item, parent, false);
            return new FollowersViewHolder(view, context);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FollowersViewHolder) {
            FollowersViewHolder imageViewHolder = (FollowersViewHolder) holder;
            imageViewHolder.bind(followersUserArrayList.get(position - 1));
        } else if (holder instanceof FollowersHeaderViewHolder) {
            FollowersHeaderViewHolder followersHeaderViewHolder = (FollowersHeaderViewHolder) holder;
            followersHeaderViewHolder.bind(isSearchResult);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return followersUserArrayList.size() + 1;
    }

    public static class FollowersHeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView headerTitle;


        public FollowersHeaderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.header_title);
        }

        public void bind(boolean isSearchResult) {
            if (isSearchResult) {
                headerTitle.setText(headerTitle.getContext().getString(R.string.search_results));
            } else {
                headerTitle.setText(headerTitle.getContext().getString(R.string.all_followers));
            }
        }
    }

    public static class FollowersViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userImage;
        private final TextView username;
        private final Context context;

        public FollowersViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            userImage = itemView.findViewById(R.id.user_image);
            username = itemView.findViewById(R.id.username);
        }

        public void bind(Author author) {
            if (author != null) {
                if (!TextUtils.isEmpty(author.getImage())) {
                    Picasso.get()
                            .load(author.getImage())
                            .into(userImage);
                }
                username.setText(author.getName());
            }

            itemView.setOnClickListener(v -> {
                if (author != null) {
                     Utils.openAuthor(context, author);
                }
            });
        }
    }
}
