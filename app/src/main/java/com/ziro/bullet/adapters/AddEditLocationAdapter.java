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
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;

public class AddEditLocationAdapter extends RecyclerView.Adapter<AddEditLocationAdapter.ViewHolder>  {

    private ArrayList<Location> mData;
    private LayoutInflater mInflater;
    private AddEditLocationAdapter.ItemClickListener mClickListener;
    private Context mContext;
    private PrefConfig mPrefConfig;

    public AddEditLocationAdapter(Context context, ArrayList<Location> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.mPrefConfig = new PrefConfig(context);
    }

    @Override
    @NonNull
    public AddEditLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.loc_list_item, parent, false);
        return new AddEditLocationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddEditLocationAdapter.ViewHolder holder, int position) {
        if (!TextUtils.isEmpty(mData.get(position).getNameToShow())) {
            holder.label.setText(mData.get(position).getNameToShow());
        }
        if (!TextUtils.isEmpty(mData.get(position).getImage())) {
            Picasso.get()
                    .load(mData.get(position).getImage())
                    .resize(Constants.targetWidth, Constants.targetHeight)
                    .onlyScaleDown()
                    .error(R.drawable.img_place_holder)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.img_place_holder);
        }
        if (mData.get(position).isFavorite()) {
            holder.follow.setImageResource(R.drawable.ic_bookmark_selected);
            if (mClickListener != null) {
                mClickListener.isFavorite(holder.follow, position);
            }
        } else {
            holder.follow.setImageResource(R.drawable.ic_bookmark_unselected_black);
        }
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
        private ImageView follow;
        private RelativeLayout item;

        ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            follow = view.findViewById(R.id.follow);
            image = view.findViewById(R.id.img);
            label = view.findViewById(R.id.label);
        }
    }

    public void setClickListener(AddEditLocationAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }



    public interface ItemClickListener {
        void onItemClick(View view, int position, boolean isFav);

        void isFavorite(View view, int position);

        void isLastItem(boolean flag);
    }
}
