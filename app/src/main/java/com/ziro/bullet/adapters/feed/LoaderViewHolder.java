package com.ziro.bullet.adapters.feed;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.data.PrefConfig;

public class LoaderViewHolder extends RecyclerView.ViewHolder {


    private final Activity context;
    private final PrefConfig prefConfig;

    public LoaderViewHolder(@NonNull View itemView, Context context, PrefConfig config) {
        super(itemView);

        this.context = (Activity) context;
        this.prefConfig = config;
    }

    public void bind() {

    }
}
