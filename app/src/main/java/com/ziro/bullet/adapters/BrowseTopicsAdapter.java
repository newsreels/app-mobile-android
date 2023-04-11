package com.ziro.bullet.adapters;


import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.search.Search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shine_joseph on 21/05/20.
 */
public class BrowseTopicsAdapter extends ListAdapter<Search, BrowseTopicsAdapter.ViewHolder> {

    private Activity context;
    private OnTopicCallback callback;
    private PrefConfig mPrefConfig;


    public BrowseTopicsAdapter(Activity context) {
        super(new DiffUtil.ItemCallback<Search>() {
            @Override
            public boolean areItemsTheSame(@NonNull Search oldItem, @NonNull Search newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Search oldItem, @NonNull Search newItem) {
                return oldItem.equals(oldItem);
            }
        });
        this.context = context;
        mPrefConfig = new PrefConfig(context);
    }

    public void setCallback(OnTopicCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse_topics, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Search model = getItem(position);
        if (!TextUtils.isEmpty(model.getIcon()))
            Picasso.get()
                    .load(model.getIcon())
                    .resize(Constants.targetWidth, Constants.targetHeight)
                    .onlyScaleDown()
                    .error(R.drawable.img_place_holder)
                    .into(holder.ivAvatar);
        else holder.ivAvatar.setImageResource(R.drawable.img_place_holder);
        holder.tvLabel.setText(model.getName());
        if (model.isFavorite()) {
            holder.ivStar.setImageResource(R.drawable.ic_bookmark_selected_without_border);
        } else {
            holder.ivStar.setImageResource(R.drawable.ic_bookmark_unselected);
        }
        holder.ivStar.setOnClickListener(view -> {
            if (callback != null)
                callback.onStarClicked(model.getId(), !model.isFavorite());

            if (model.getType() == null || model.getType().equals("topic"))
                followOrUnfollowTopic(model);
            else if (model.getType().equals("source"))
                followOrUnfollowSource(model);
        });

        holder.itemView.setOnClickListener(view -> {
            if (callback != null)
                callback.onItemClicked(model);
        });
    }

    @Override
    public void submitList(@Nullable List<Search> list) {
        super.submitList(list != null ? new ArrayList<>(list) : null);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivAvatar;
        private TextView tvLabel;
        private ImageView ivStar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            ivStar = itemView.findViewById(R.id.ivStar);
        }
    }

    private void followOrUnfollowTopic(Search model) {
        Call<ResponseBody> call;

        if (!model.isFavorite()) {
            Map<String,String> params = new HashMap<>();
            params.put(Events.KEYS.TOPIC_ID,model.getId());
            AnalyticsEvents.INSTANCE.logEvent(context,
                    params,
                    Events.FOLLOW_TOPIC);
            call = ApiClient
                    .getInstance(context)
                    .getApi()
                    .addTopic("Bearer " + mPrefConfig.getAccessToken(), model.getId());
        }
        else {
            Map<String,String> params = new HashMap<>();
            params.put(Events.KEYS.TOPIC_ID,model.getId());
            AnalyticsEvents.INSTANCE.logEvent(context,
                    params,
                    Events.UNFOLLOWED_TOPIC);
            call = ApiClient
                    .getInstance(context)
                    .getApi()
                    .unfollowTopic("Bearer " + mPrefConfig.getAccessToken(), model.getId());
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("RSPONS", "RES : "+response.body());
                if (response.code() == 200) {
                    model.setFavorite(!model.isFavorite());
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RSPONS", "RES FAIL : "+t.getMessage());
            }
        });
    }

    private void followOrUnfollowSource(Search model) {
        Call<ResponseBody> call;

        if (!model.isFavorite()) {
            Map<String,String> params = new HashMap<>();
            params.put(Events.KEYS.TOPIC_ID,model.getId());
            AnalyticsEvents.INSTANCE.logEvent(context,
                    params,
                    Events.FOLLOW_SOURCE);
            call = ApiClient
                    .getInstance(context)
                    .getApi()
                    .followSource("Bearer " + mPrefConfig.getAccessToken(), model.getId());
        }
        else {
            Map<String,String> params = new HashMap<>();
            params.put(Events.KEYS.TOPIC_ID,model.getId());
            AnalyticsEvents.INSTANCE.logEvent(context,
                    params,
                    Events.UNFOLLOWED_SOURCE);
            call = ApiClient
                    .getInstance(context)
                    .getApi()
                    .unfollowSources("Bearer " + mPrefConfig.getAccessToken(), model.getId());
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("RSPONS", "RES : "+response.body());
                if (response.code() == 200) {
                    model.setFavorite(!model.isFavorite());
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RSPONS", "RES FAIL : "+t.getMessage());
            }
        });
    }


    public interface OnTopicCallback {
        void onItemClicked(Search model);

        void onStarClicked(String id, boolean favorite);
    }


}
