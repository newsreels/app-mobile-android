package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.utills.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageLocationAdapter extends RecyclerView.Adapter<ManageLocationAdapter.ViewHolder>  {

    private ArrayList<Location> mData;
    private LayoutInflater mInflater;
    private LocationCallback mClickListener;
    private Activity mContext;
    private PrefConfig mPrefConfig;

    public ManageLocationAdapter(Activity context, ArrayList<Location> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.mPrefConfig = new PrefConfig(context);
    }

    @Override
    @NonNull
    public ManageLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.manage_location_item, parent, false);
        return new ManageLocationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageLocationAdapter.ViewHolder holder, int position) {
        Location location = mData.get(position);
        if (location != null) {
            if (location.getId().equalsIgnoreCase("add")) {
                holder.rlContainer.setVisibility(View.GONE);
                holder.ll_data.setVisibility(View.VISIBLE);
                holder.ll_data.setOnClickListener(v -> {
                    if (mClickListener != null)
                        mClickListener.onAddTopicClicked();
                });
            } else {
                holder.rlContainer.setVisibility(View.VISIBLE);
                holder.ll_data.setVisibility(View.GONE);
                holder.ll_data.setOnClickListener(null);
            }
            if (!TextUtils.isEmpty(location.getImage())) {
                Picasso.get()
                        .load(location.getImage())
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .onlyScaleDown()
                        .error(R.drawable.img_place_holder)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.img_place_holder);
            }

            if (location.isFavorite()) {
                holder.ivFollow.setImageResource(R.drawable.ic_bookmark_selected_without_border);
            } else {
                holder.ivFollow.setImageResource(R.drawable.ic_bookmark_unselected);
            }
                holder.textView.setTextColor(ContextCompat.getColor(mContext,R.color.textSmall));

            holder.textView.setText(location.getNameToShow());
            holder.llMore.setOnClickListener(view -> {
                if (location.isFavorite()) {
                    unfollowLocation(mData.get(position), position,holder);
                } else {
                    followLocation(mData.get(position).getId(), position,holder);
                }
            });
            holder.itemView.setOnClickListener(view -> {
                if (mClickListener != null)
                    mClickListener.onItemClicked(location);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView ivFollow;
        private TextView textView;
        private RelativeLayout rlContainer;
        private FrameLayout ll_data;
        private RelativeLayout llMore;
        private ProgressBar progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.tvLabel);
            llMore = itemView.findViewById(R.id.ll_more);
            ivFollow = itemView.findViewById(R.id.ivMore);
            rlContainer = itemView.findViewById(R.id.rl_container);
            ll_data = itemView.findViewById(R.id.ll_data);
            progress = itemView.findViewById(R.id.progress);
        }
    }

    public void setClickListener(LocationCallback itemClickListener) {
        this.mClickListener = itemClickListener;
    }



    public interface LocationCallback {
        void onItemFollowed(Location location);

        void onItemUnfollowed(Location location);

        void onItemClicked(Location location);

        void onAddTopicClicked();
    }




    private void unfollowLocation(Location location, int position, ViewHolder holder) {
        holder.progress.setVisibility(View.VISIBLE);
        holder.ivFollow.setVisibility(View.GONE);
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unfollowLocation("Bearer " + mPrefConfig.getAccessToken(), location.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                holder.progress.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    if (mClickListener != null)
                        mClickListener.onItemUnfollowed(mData.get(position));
                    mData.get(position).setFavorite(false);
                    notifyDataSetChanged();
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                holder.progress.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void followLocation(String id, int position, ViewHolder holder) {
        holder.progress.setVisibility(View.VISIBLE);
        holder.ivFollow.setVisibility(View.GONE);
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .followLocation("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                holder.progress.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    if (mClickListener != null)
                        mClickListener.onItemFollowed(mData.get(position));
                    mData.get(position).setFavorite(true);
                    notifyDataSetChanged();
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                holder.progress.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
