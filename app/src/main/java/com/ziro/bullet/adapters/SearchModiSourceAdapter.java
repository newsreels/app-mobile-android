package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;

import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SearchModiSourceAdapter extends RecyclerView.Adapter<SearchModiSourceAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Source> mFollowingModels;
    private SearchModiSourceAdapter.FollowingCallback mCallback;
    private PrefConfig mPrefConfig;
    private boolean isFollowedMode = true;
    private boolean isShowAll = false;
    private String locc = null;

    public SearchModiSourceAdapter(Activity mContext, ArrayList<Source> mFollowingModels, boolean isFollowedMode) {
        this.mContext = mContext;
        this.mFollowingModels = mFollowingModels;
        this.isFollowedMode = isFollowedMode;
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setCallback(SearchModiSourceAdapter.FollowingCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public SearchModiSourceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.source_item_v2, parent, false);
        return new SearchModiSourceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchModiSourceAdapter.ViewHolder holder, int position) {
        Source source = mFollowingModels.get(position);
        if (source != null) {
            if (!TextUtils.isEmpty(source.getIcon())) {
                Picasso.get()
                        .load(source.getIcon())
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .onlyScaleDown()
                        .transform(new CropCircleTransformation())
                        .error(R.drawable.img_place_holder)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.img_place_holder);
            }
            holder.textView.setText(source.getName());
//            if (!isFollowedMode)
//                holder.ivMore.setVisibility(View.GONE);

            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.source_card));
            holder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.discover_header_text));

            if (!TextUtils.isEmpty(source.getFollower())) {
                holder.subLabel.setText(source.getFollower());
                holder.subLabel.setVisibility(View.VISIBLE);
            } else {
                holder.subLabel.setVisibility(View.VISIBLE);
            }

            locc = source.getCategory();

            holder.itemView.setOnClickListener(view -> {
                if (mPrefConfig != null) {
                    mPrefConfig.setSrcLang(mFollowingModels.get(position).getLanguage());
                    mPrefConfig.setSrcLoc(locc);
                }
                if (mCallback != null)
                    mCallback.onItemClicked(source);
            });
            showVerifiedIcon(holder, source);
        }
    }

    private void showVerifiedIcon(SearchModiSourceAdapter.ViewHolder holder, Source sourceObj) {
        if (sourceObj.isVerified()) {
            holder.verifyIcon.setVisibility(View.VISIBLE);
        } else {
            holder.verifyIcon.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mFollowingModels.size();
    }

    public void add(Source source) {
        this.mFollowingModels.add(0, source);
        notifyItemInserted(mFollowingModels.size() - 1);
    }

    public void setCount(boolean showAll) {
        this.isShowAll = showAll;
        notifyDataSetChanged();
    }

    public interface FollowingCallback {
        void onItemFollowed(Source source);

        void onItemUnfollowed(Source source);

        void onItemClicked(Source source);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView verifyIcon;
        private TextView textView, subLabel;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            verifyIcon = itemView.findViewById(R.id.verifyIcon);
            textView = itemView.findViewById(R.id.label);
            subLabel = itemView.findViewById(R.id.subLabel);
            cardView = itemView.findViewById(R.id.card);
        }
    }
}
