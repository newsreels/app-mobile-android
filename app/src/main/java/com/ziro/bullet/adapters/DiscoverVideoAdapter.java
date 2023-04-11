package com.ziro.bullet.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.discover.VideoCardsViewHolder;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;

import java.util.ArrayList;

public class DiscoverVideoAdapter extends RecyclerView.Adapter<VideoCardsViewHolder> {
    private Activity mContext;
    private ArrayList<Article> mFollowingModels;
    private FollowingCallback mCallback;
    private PrefConfig mPrefConfig;
    private String mType;
    private FollowUnfollowPresenter presenter;
    private String locc = null;
    private int baseAdapterPosition = -1;

    public DiscoverVideoAdapter(Activity mContext, ArrayList<Article> mFollowingModels, String mType) {
        this.mContext = mContext;
        this.mType = mType;
        this.mFollowingModels = mFollowingModels;
        this.presenter = new FollowUnfollowPresenter(mContext);
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setCallback(FollowingCallback callback) {
        this.mCallback = callback;
    }

    public void setBaseAdapterPosition(int baseAdapterPosition) {
        this.baseAdapterPosition = baseAdapterPosition;
    }

    @NonNull
    @Override
    public VideoCardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.discover_video_card_list, parent, false);
        return new VideoCardsViewHolder(view, mContext,baseAdapterPosition);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoCardsViewHolder holder, int position) {
        holder.bind(position, mFollowingModels.get(position));
    }

    public void selectCard(int position) {
        if (mFollowingModels != null && mFollowingModels.size() > 0 && position < mFollowingModels.size()) {
            if(!mFollowingModels.get(position).isSelected()) {
                for (int i = 0; i < mFollowingModels.size(); i++) {
                    mFollowingModels.get(i).setSelected(false);
                }
                mFollowingModels.get(position).setSelected(true);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mFollowingModels != null) {
            return mFollowingModels.size();
        } else {
            return 0;
        }
    }

    public interface FollowingCallback {
        void onItemChanged(Source source);

        void onItemClicked(Source source, String type);
    }
}
