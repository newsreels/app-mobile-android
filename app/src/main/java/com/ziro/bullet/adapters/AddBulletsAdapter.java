package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.interfaces.AdapterItemCallback;
import com.ziro.bullet.model.articles.Bullet;

import java.util.ArrayList;

public class AddBulletsAdapter extends RecyclerView.Adapter<AddBulletsAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<Bullet> items;
    private ViewHolder holder;
    private AdapterItemCallback adapterCallback;

    public AddBulletsAdapter(Activity context, AdapterItemCallback adapterCallback, ArrayList<Bullet> arrayList) {
        this.context = context;
        this.adapterCallback = adapterCallback;
        this.items = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.create_bullet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        this.holder = holder;
        Bullet item = items.get(position);
        if (item != null) {
            int id = position;
            id++;
            if (!TextUtils.isEmpty(item.getData())) {
                holder.bullet.setVisibility(View.VISIBLE);
                holder.bullet.setText(item.getData());
            } else {
                holder.bullet.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(item.getHint())) {
                if (id > 2) {
                    holder.label.setText(item.getHint() + " " + id + " " + context.getString(R.string.optional));
                } else {
                    holder.label.setText(item.getHint() + " " + id);
                }
            }
        }
        holder.item.setOnClickListener(v -> {
            if (adapterCallback != null)
                adapterCallback.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item;
        private TextView bullet;
        private TextView label;

        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            bullet = itemView.findViewById(R.id.bullet);
            label = itemView.findViewById(R.id.label);
        }
    }
}
