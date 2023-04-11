package com.ziro.bullet.adapters.feed;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView related_title;

    public ViewHolder(View view) {
        super(view);
        related_title = view.findViewById(R.id.related_title);
    }

    public void bind(String title) {
        if (!TextUtils.isEmpty(title)) {
            related_title.setText(title);
        }
    }
}
