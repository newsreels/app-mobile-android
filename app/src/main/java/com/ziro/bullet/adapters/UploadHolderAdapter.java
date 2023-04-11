package com.ziro.bullet.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ReelActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class UploadHolderAdapter extends RecyclerView.Adapter<UploadHolderAdapter.ViewHolder> {

    private Context context;
    private View.OnClickListener onClickListener;

    public UploadHolderAdapter(Context context, View.OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_upload_holder, parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            this.onClickListener = onClickListener;
        }

        public void bind(){
            itemView.setOnClickListener(onClickListener);
        }
    }
}
