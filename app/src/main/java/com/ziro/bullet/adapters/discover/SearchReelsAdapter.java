package com.ziro.bullet.adapters.discover;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.interfaces.SearchTabsInterface;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class SearchReelsAdapter extends RecyclerView.Adapter<SearchReelsAdapter.ViewHolder> {
    private ArrayList<ReelsItem> arrayList;
    private Context context;
    private String mMode = "public";
    private PrefConfig prefConfig;
    private String page;
    private String userContext;
    private String searchquery;
    private Boolean reelsapi;
    private SearchTabsInterface searchTabsInterface;

    public SearchReelsAdapter(ArrayList<ReelsItem> arrayList, Context context, String userContext) {
        this.arrayList = arrayList;
        this.context = context;
        this.userContext = userContext;
        this.mMode = "public";
        prefConfig = new PrefConfig(context);
    }

    public void updateQuery(String searchquery, Boolean reelsapi, SearchTabsInterface searchTabsInterface) {
        this.searchquery = searchquery;
        this.reelsapi = reelsapi;
        this.searchTabsInterface = searchTabsInterface;
    }

    public void updateRecords(ArrayList<ReelsItem> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_reels, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(arrayList.get(position), position, arrayList);
    }

    public void setNextPageParam(String page) {
        this.page = page;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView tvViewCount;
        private final TextView tvReelDescription;
        private final ImageView ivReelOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            tvViewCount = itemView.findViewById(R.id.tv_views_total);
            tvReelDescription = itemView.findViewById(R.id.tv_reel_title);
            ivReelOptions = itemView.findViewById(R.id.iv_reel_option);
        }

        private void bind(ReelsItem reelsItem, int position, ArrayList<ReelsItem> arrayList) {

            if (reelsItem != null) {
                if (!TextUtils.isEmpty(reelsItem.getImage())) {
                    Glide.with(context)
                            .load(reelsItem.getImage())
                            .placeholder(R.drawable.ad_card_button_shape)
                            .override(Constants.targetWidth, Constants.targetHeight)
                            .into(imageView);
                }

                ivReelOptions.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(reelsItem.getInfo().getViewCount())) {
                    tvViewCount.setText(Utils.formatViews(Long.valueOf(reelsItem.getInfo().getViewCount())) + " views");
                }

                if (reelsItem.getDescription() != null) {
                    tvReelDescription.setText(reelsItem.getDescription());
                }

                itemView.setOnClickListener(v -> {
                    int pos;
                    if (arrayList.size() > position + 12) {
                        pos = position + 12;
                    } else {
                        pos = arrayList.size();
                    }
                    ArrayList<ReelsItem> reelsItems = new ArrayList<ReelsItem>(arrayList.subList(position, pos));

                    Intent intent = new Intent(context, ReelInnerActivity.class);
                    if (reelsItem.getAuthor() != null && reelsItem.getAuthor().size() > 0) {
                        intent.putExtra(ReelInnerActivity.REEL_F_AUTHOR_ID, reelsItem.getAuthor().get(0).getId());
                        intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, userContext);
                    }
                    if (reelsItem.getSource() != null) {
                        intent.putExtra(ReelInnerActivity.REEL_F_SOURCE_ID, reelsItem.getSource().getId());
                        intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, userContext);
                    }
//                    intent.putExtra(ReelActivity.REEL_MODE, "public");
                    intent.putExtra(ReelInnerActivity.REEL_F_MODE, mMode);
                    //sending sublist of reels to reel activity with start position as 0 ie selected position
                    intent.putExtra(ReelInnerActivity.REEL_POSITION, 0);
                    intent.putExtra(ReelInnerActivity.REEL_F_PAGE, page);
                    intent.putParcelableArrayListExtra(ReelInnerActivity.REEL_F_DATALIST, reelsItems);
                    context.startActivity(intent);
                });
            }
        }
    }
}
