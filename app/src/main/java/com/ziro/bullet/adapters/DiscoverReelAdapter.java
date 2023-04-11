package com.ziro.bullet.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.discover.ReelCardViewHolder;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;

import java.util.ArrayList;

public class DiscoverReelAdapter extends RecyclerView.Adapter<ReelCardViewHolder> {
    private Activity mContext;
    private ArrayList<ReelsItem> mFollowingModels;
    private FollowingCallback mCallback;
    private PrefConfig mPrefConfig;
    private String mType;
    private FollowUnfollowPresenter presenter;
    private String locc = null;

    public DiscoverReelAdapter(Activity mContext, ArrayList<ReelsItem> mFollowingModels, String mType) {
        this.mContext = mContext;
        this.mType = mType;
        this.mFollowingModels = mFollowingModels;
        this.presenter = new FollowUnfollowPresenter(mContext);
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setCallback(FollowingCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public ReelCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.discover_reel_card, parent, false);
        return new ReelCardViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelCardViewHolder holder, int position) {
        holder.bind(position, mFollowingModels);
    }

    @Override
    public int getItemCount() {
        if (mFollowingModels != null) {
            return mFollowingModels.size();
        } else return 0;
    }

    public void selectCard(int position) {
        if (mFollowingModels != null && mFollowingModels.size() > 0 && position < mFollowingModels.size()) {
            if (!mFollowingModels.get(position).isSelected()) {
                for (int i = 0; i < mFollowingModels.size(); i++) {
                    mFollowingModels.get(i).setSelected(false);
                }
                mFollowingModels.get(position).setSelected(true);
                notifyDataSetChanged();
            }
        }
    }

    public interface FollowingCallback {
        void onItemChanged(Source source);

        void onItemClicked(Source source, String type);
    }
}
