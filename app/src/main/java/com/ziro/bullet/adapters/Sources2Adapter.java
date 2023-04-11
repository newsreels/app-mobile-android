package com.ziro.bullet.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;

public class Sources2Adapter extends RecyclerView.Adapter<Sources2Adapter.ViewHolder> {

    private ArrayList<Source> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    private PrefConfig mPrefConfig;

    public Sources2Adapter(Context context, ArrayList<Source> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.mPrefConfig = new PrefConfig(context);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.source_item_v2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!TextUtils.isEmpty(mData.get(position).getName())) {
            holder.label.setText(mData.get(position).getName());
        }
        if (!TextUtils.isEmpty(mData.get(position).getFollower())) {
            holder.subLabel.setVisibility(View.VISIBLE);
            holder.subLabel.setText(mData.get(position).getFollower());
        } else {
            holder.subLabel.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.get(position).getImagePortraitOrNormal())) {
            Picasso.get()
                    .load(mData.get(position).getImagePortraitOrNormal())
                    .resize(Constants.targetWidth, Constants.targetHeight)
                    .onlyScaleDown()
                    .error(R.drawable.img_place_holder)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.img_place_holder);
        }
        if (mData.get(position).isFavorite()) {
            holder.follow.setImageResource(R.drawable.ic_bookmark_active);
            if (mClickListener != null) {
                mClickListener.isFavorite(holder.follow, position);
            }
        } else {
            holder.follow.setImageResource(R.drawable.ic_bookmark_inactive);
        }

        holder.right.setText("" + mData.get(position).getLanguage());
        holder.left.setText("" + mData.get(position).getCategory());

        holder.item.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onItemClick(holder.follow, position, !mData.get(position).isFavorite());
            }
        });

        if (position == getItemCount() - 1) {
            if (mClickListener != null) {
                mClickListener.isLastItem(true);
            }
        } else {
            if (mClickListener != null) {
                mClickListener.isLastItem(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView label;
        private TextView subLabel;
        private ImageView follow;
        private RelativeLayout item;
        private TextView left, right;

        ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            follow = view.findViewById(R.id.follow);
            image = view.findViewById(R.id.image);
            label = view.findViewById(R.id.label);
            subLabel = view.findViewById(R.id.subLabel);
            left = view.findViewById(R.id.left);
            right = view.findViewById(R.id.right);
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position, boolean isFav);

        void isFavorite(View view, int position);

        void isLastItem(boolean flag);
    }
}