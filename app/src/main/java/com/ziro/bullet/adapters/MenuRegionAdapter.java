package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.dataclass.Region;
import com.ziro.bullet.data.models.location.Location;

import java.util.ArrayList;

public class MenuRegionAdapter extends RecyclerView.Adapter<MenuRegionAdapter.ViewHolder> {

    private ArrayList<Location> data;
    private PrefConfig mPrefConfig;
    private Activity context;

    public MenuRegionAdapter(ArrayList<Location> data, Activity context) {
        this.data = data;
        this.context = context;
        mPrefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_region_list_item, parent, false);
        return new MenuRegionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data != null && data.size() > 0) {
            Location item = data.get(position);
            if (item != null)
                if (!TextUtils.isEmpty(item.getName()))
                    holder.regionName.setText(item.getName());

            holder.regionName.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView regionName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            regionName = itemView.findViewById(R.id.region_name);
        }
    }
}
