package com.ziro.bullet.adapters.relevant;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;

public class TitleViewHolder extends RecyclerView.ViewHolder {

    private final TextView title;

    public TitleViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
    }

    public void bind(String titleStr) {
        title.setText(titleStr);

//        if(titleStr.equalsIgnoreCase(title.getContext().getString(R.string.articles))){
//            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.title_bar_title));
//            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.discover_bg));
//        }else{
//            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.discover_title_night));
//            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.discover_bg_night));
//        }
    }
}
