package com.ziro.bullet.adapters.CommunityFeed;

import android.content.Context;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;

public class ViewHolderHeader extends RecyclerView.ViewHolder {

    private CardView rootCard9;

    public ViewHolderHeader(View view) {
        super(view);
        rootCard9 = view.findViewById(R.id.rootCard);
    }

    public void bind(Context context) {
        if (rootCard9 != null && context != null)
            rootCard9.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
    }
}
