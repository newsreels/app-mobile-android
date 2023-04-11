package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;

public class TitleCardViewHolder extends RecyclerView.ViewHolder {

    private final TextView text1;
    private final LinearLayout card;
    private final RelativeLayout line;
    private Context mContext;

    public TitleCardViewHolder(@NonNull View itemView, Activity context) {
        super(itemView);

        this.mContext = context;
        text1 = itemView.findViewById(R.id.text1);
        card = itemView.findViewById(R.id.card);
        line = itemView.findViewById(R.id.line);
    }

    public void bind(int position, DiscoverItem item) {
        if (mContext != null) {
            card.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            line.setBackgroundColor(mContext.getResources().getColor(R.color.discover_card_separator));
            text1.setTextColor(mContext.getResources().getColor(R.color.discover_card_title));
        }

        if (item != null) {
            if (!TextUtils.isEmpty(item.getTitle())) {
                text1.setText(item.getTitle());
            }
        }
    }
}
