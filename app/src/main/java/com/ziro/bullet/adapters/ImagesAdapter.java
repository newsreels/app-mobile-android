package com.ziro.bullet.adapters;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.interfaces.ReportBottomSheetListener;
import com.ziro.bullet.R;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private ReportBottomSheetListener listener;
    private Activity context;
    private ArrayList<Uri> items;
    private ViewHolder holder;

    public ImagesAdapter(Activity context, ReportBottomSheetListener listener, ArrayList<Uri> arrayList) {
        this.context = context;
        this.items = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        this.holder = holder;
        Uri item = items.get(position);
        if (item != null) {
            holder.image.setImageURI(item);
        }
        holder.remove.setOnClickListener(v -> {
            if (listener != null)
                listener.selectReport(false, position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageView remove;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            remove = itemView.findViewById(R.id.remove);
        }
    }
}
