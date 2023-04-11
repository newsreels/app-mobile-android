package com.ziro.bullet.adapters.searchhistory;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.interfaces.AdapterItemCallback;
import com.ziro.bullet.model.searchhistory.History;

import java.util.ArrayList;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<History> items = new ArrayList<>();
    private ViewHolder holder;
    private AdapterItemCallback adapterCallback;

    public SearchHistoryAdapter(Activity context, AdapterItemCallback adapterCallback) {
        this.context = context;
        this.adapterCallback = adapterCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        this.holder = holder;
        History item = items.get(position);
        if (item != null) {
            int id = position;
            id++;

            if (!TextUtils.isEmpty(item.getSearch())) {
//                if (id > 2) {
//                    holder.label.setText(item.getHint() + " " + id + " " + context.getString(R.string.optional));
//                } else {
//                    holder.label.setText(item.getHint() + " " + id);
//                }
            }
            holder.label.setText(item.getSearch());
        }
        holder.item.setOnClickListener(v -> {
            if (adapterCallback != null)
                adapterCallback.onItemClick(position, item);
        });

//        holder.sc_cancel.setOnClickListener(v -> {
//            if (adapterCallback != null)
//                adapterCallback.onItemCancelClick(position,item);
//        });

//        holder.sc_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String id = item.getId().toString();
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateDiscoverItems(ArrayList<History> arrayList) {
        this.items = arrayList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item;
        private TextView bullet;
        private ImageView sc_cancel;
        private TextView label;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
//            bullet = itemView.findViewById(R.id.bullet);
            label = itemView.findViewById(R.id.label);
            sc_cancel = itemView.findViewById(R.id.sc_cancel);
        }
    }

}
