package com.ziro.bullet.adapters;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.bottomSheet.MediaShare;
import com.ziro.bullet.data.models.ShareInfo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReelShareAdapter extends RecyclerView.Adapter<ReelShareAdapter.ViewHolder> {

    private Context context;
    private List<DirectShare> mApps;
    private ShareInfo shareInfo;
    private MediaShare.ShareSheet dismissBottomSheet;

    public ReelShareAdapter(Context context, List<DirectShare> mApps, ShareInfo shareInfo,
                            MediaShare.ShareSheet dismissBottomSheet) {
        this.context = context;
        this.mApps = mApps;
        this.shareInfo = shareInfo;
        this.dismissBottomSheet = dismissBottomSheet;
    }

    @NonNull
    @NotNull
    @Override
    public ReelShareAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reel_bottom_sheet, parent, false);
        return new ReelShareAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReelShareAdapter.ViewHolder holder, int position) {
        DirectShare info = mApps.get(position);
        if (info != null) {
            holder.title.setText(info.name);
            holder.image.setImageDrawable(info.image);
        } else {
            holder.title.setText(context.getString(R.string.others));
            holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_other_reel));
        }

        holder.card.setOnClickListener(v1 -> {
            if (mApps.size() == 0) {
                return;
            }
            if (shareInfo == null) return;
            if (info != null)
                dismissBottomSheet.onShare(info);
            else dismissBottomSheet.onShare(null);
        });
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title;
        private LinearLayout card;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            card = itemView.findViewById(R.id.card);
        }
    }

}
