package com.ziro.bullet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.interfaces.CategoryCallback;
import com.ziro.bullet.model.Tabs.DataItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private final Context context;
    private final ArrayList<DataItem> categories;
    private CategoryCallback callback;
    private String id;

    public CategoryAdapter(ArrayList<DataItem> categories, CategoryCallback callback, Context context, String id) {
        this.id = id;
        this.callback = callback;
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_home, parent, false);
        return new CardViewHolder(view, context, callback);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CardViewHolder) {
            CardViewHolder imageViewHolder = (CardViewHolder) holder;
            imageViewHolder.bind(categories.get(position), id);
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

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout card;
        private final TextView tvLabel;
        private final ShapeableImageView ivCatImage;
        private final Context context;
        private final CategoryCallback callback;

        public CardViewHolder(@NonNull @NotNull View itemView, Context context, CategoryCallback callback) {
            super(itemView);
            this.context = context;
            this.callback = callback;
            card = itemView.findViewById(R.id.card);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            ivCatImage = itemView.findViewById(R.id.iv_cat_image);
        }

        public void bind(DataItem item, String id) {
            if (item != null) {
                tvLabel.setText(item.getTitle());

                if (item.getId().equalsIgnoreCase(id)) {
                    card.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.category_item_selected_bg));
//                    card.setStrokeColor(ContextCompat.getColor(context, R.color.theme_color_1));
                    tvLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    card.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.category_item_bg));
//                    card.setStrokeColor(ContextCompat.getColor(context, R.color.black));
                    tvLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
                }

                Glide.with(itemView.getContext())
                        .load(item.getImage())
                        .placeholder(R.drawable.bg_btn_round_grey)
                        .error(R.drawable.bg_btn_round_grey)
                        .into(ivCatImage);

                card.setOnClickListener(v -> {
                    if (callback != null) {
                        callback.onTabClick(item);
                    }
                });
            }

        }
    }
}
