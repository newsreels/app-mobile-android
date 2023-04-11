package com.ziro.bullet.adapters.postarticle;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.data.models.postarticle.TagItem;

import java.util.ArrayList;

public class SelectTagAdapter extends RecyclerView.Adapter<SelectTagAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<TagItem> tagItemArrayList;
    private TagSelectCallback tagSelectCallback;

    public SelectTagAdapter(Context mContext, ArrayList<TagItem> tagItemArrayList, TagSelectCallback tagSelectCallback) {
        this.mContext = mContext;
        this.tagItemArrayList = tagItemArrayList;
        this.tagSelectCallback = tagSelectCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_select_item, parent, false);
        return new ViewHolder(view, tagSelectCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TagItem item = tagItemArrayList.get(position);
        holder.tagName.setText(item.getName());
        if(!TextUtils.isEmpty(item.getIcon())) {
            Picasso.get().load(item.getIcon()).into(holder.tagIcon);
        }

        if(item.isSelected()){
            holder.selectionIcon.setImageResource(R.drawable.ic_radio_btn_active);
        } else {
            holder.selectionIcon.setImageResource(R.drawable.ic_radio_btn_inactive);
        }
    }

    @Override
    public int getItemCount() {
        return tagItemArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tagName;
        private ImageView tagIcon;
        private ImageView selectionIcon;

        private TagSelectCallback tagSelectCallback;

        public ViewHolder(@NonNull View itemView, TagSelectCallback tagSelectCallback) {
            super(itemView);
            this.tagSelectCallback = tagSelectCallback;

            tagName = itemView.findViewById(R.id.tag_name);
            tagIcon = itemView.findViewById(R.id.tag_icon);
            selectionIcon = itemView.findViewById(R.id.selection_icon);

            itemView.setOnClickListener(v -> tagSelectCallback.onItemSelected(getBindingAdapterPosition()));
        }
    }

    public interface TagSelectCallback {
        void onItemSelected(int position);
    }
}
