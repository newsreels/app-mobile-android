package com.ziro.bullet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.interfaces.CategoryCallback;
import com.ziro.bullet.model.Tabs.DataItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ArticlesCategoryAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private final Context context;
    private ArrayList<DataItem> categories;
    private CategoryCallback callback;
    private String id;

    public ArticlesCategoryAdapterNew(ArrayList<DataItem> categories, CategoryCallback callback, Context context, String id) {
        this.id = id;
        this.callback = callback;
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.articles_category_item, parent, false);
        return new ArticleCategoryViewHolder(view, context, callback);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArticleCategoryViewHolder) {
            ArticleCategoryViewHolder imageViewHolder = (ArticleCategoryViewHolder) holder;
            imageViewHolder.bind(categories.get(position), id);
            if (position == categories.size() - 1)
                ((ArticleCategoryViewHolder) holder).lineView.setVisibility(View.GONE);
            else
                ((ArticleCategoryViewHolder) holder).lineView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateList(int position, ArrayList<DataItem> updatedTabList) {
        this.categories = updatedTabList;
        notifyItemChanged(position);
    }

    public void updateList(ArrayList<DataItem> mCategoriesList) {
        this.categories = mCategoriesList;
        notifyDataSetChanged();
    }

    public static class ArticleCategoryViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout card;
        private final TextView tvLabel;
        private final ShapeableImageView ivCatImage;
        private final ImageView ivDragCat;
        private final Context context;
        private final CategoryCallback callback;
        private final View lineView;

        public ArticleCategoryViewHolder(@NonNull @NotNull View itemView, Context context, CategoryCallback callback) {
            super(itemView);
            this.context = context;
            this.callback = callback;
            card = itemView.findViewById(R.id.card);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            ivCatImage = itemView.findViewById(R.id.iv_cat_image);
            ivDragCat = itemView.findViewById(R.id.iv_drag_cat);
            lineView = itemView.findViewById(R.id.line);
        }

        public void bind(DataItem item, String id) {
            if (item != null) {
                tvLabel.setText(item.getTitle());
//                createShader(tvLabel, new int[]{ContextCompat.getColor(tvLabel.getContext(), R.color.theme_color_1), ContextCompat.getColor(tvLabel.getContext(), R.color.white)});
                if (item.isLocked()) {
                    ivCatImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_locked_cat));
                    ivDragCat.setVisibility(View.VISIBLE);
                } else {
                    if (item.isFollowed()) {
                        ivCatImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unfollow_cat));
                        ivDragCat.setVisibility(View.VISIBLE);
                    } else {
                        ivCatImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_follow_cat));
                        ivDragCat.setVisibility(View.GONE);
                    }
                }

                ivCatImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item.isFollowed()) {
                            item.setFollowed(false);
                            callback.onItemFollowClick(item, getAdapterPosition());
                        } else {
                            item.setFollowed(true);
                            callback.onItemFollowClick(item, getAdapterPosition());
                        }
                    }
                });

                card.setOnClickListener(v -> {
                    if (callback != null) {
                        callback.onTabClick(item);
                    }
                });
            }

        }
    }
}