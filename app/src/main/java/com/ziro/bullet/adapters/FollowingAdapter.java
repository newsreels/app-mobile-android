package com.ziro.bullet.adapters;


import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.model.ShareBottomSheetResponse;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Topics> mFollowingModels;
    private FollowingCallback mCallback;
    private PrefConfig mPrefConfig;
    private boolean isFollowedMode = true;
    private boolean isShowAll = false;

    public FollowingAdapter(Activity mContext, ArrayList<Topics> mFollowingModels) {
        this.mContext = mContext;
        this.mFollowingModels = mFollowingModels;
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setCallback(FollowingCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.topic_item_v2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mFollowingModels != null && mFollowingModels.size() > 0) {
            Topics topic = mFollowingModels.get(position);
            if (topic != null) {

                if (!TextUtils.isEmpty(topic.getImage())) {
                    Picasso.get()
                            .load(topic.getImage())
                            .resize(Constants.targetWidth, Constants.targetHeight)
                            .onlyScaleDown()
                            .error(R.drawable.img_place_holder)
                            .into(holder.imageView);
                } else {
                    holder.imageView.setImageResource(R.drawable.img_place_holder);
                }
                holder.textView.setText(topic.getName());
                if (!TextUtils.isEmpty(topic.getFollower())) {
                    holder.subLabel.setVisibility(View.VISIBLE);
                    holder.subLabel.setText(topic.getFollower());
                } else {
                    holder.subLabel.setVisibility(View.GONE);
                }
                holder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.discover_item_header_text));
                holder.subLabel.setTextColor(ContextCompat.getColor(mContext, R.color.discover_item_sub_header_text));


                if (topic.isFavorite()) {
                    holder.llMore.setVisibility(View.VISIBLE);
                    holder.ivFollow.setImageResource(R.drawable.ic_bookmark_active);
                } else {
                    holder.llMore.setVisibility(View.GONE);
                    holder.ivFollow.setImageResource(R.drawable.ic_bookmark_inactive);
                }

                holder.llFollow.setOnClickListener(view -> {
                    if (topic.isFavorite()) {
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.TOPIC_ID,topic.getId());
                        AnalyticsEvents.INSTANCE.logEvent(mContext,
                                params,
                                Events.UNFOLLOWED_TOPIC);
                        if (!TextUtils.isEmpty(topic.getType()) && topic.getType().equals("TOPICS"))
                            unfollowTopic(topic.getId(), position, holder);
                        else
                            unfollowSource(topic.getId(), position, holder);
                    } else {
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.TOPIC_ID,topic.getId());
                        AnalyticsEvents.INSTANCE.logEvent(mContext,
                                params,
                                Events.FOLLOW_TOPIC);
                        if (!TextUtils.isEmpty(topic.getType()) && topic.getType().equals("TOPICS"))
                            followTopic(topic.getId(), position, holder);
                        else
                            followSource(topic.getId(), position, holder);
                    }
                });

                holder.llMore.setOnClickListener(view -> {
                    showBottomSheetDialog(position, holder.itemView, topic.getName());
                });
                holder.itemView.setOnClickListener(view -> {
                    if (mCallback != null)
                        mCallback.onItemClicked(topic);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFollowingModels.size();
    }

    private void showBottomSheetDialog(int position, View view, String topic_name) {
        View dialogView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.bottom_sheet_following, null);
        BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.SheetDialog);
        dialog.setContentView(dialogView);
        Button button = dialogView.findViewById(R.id.btn_remove);
        Button cancel = dialogView.findViewById(R.id.btn_cancel);
        RelativeLayout relativeLayout = dialogView.findViewById(R.id.rl_second);


        if (mContext != null)
            button.setText(mContext.getString(R.string.block_topic));
        button.setText(mContext.getString(R.string.block) + " " + topic_name);
        relativeLayout.setVisibility(View.GONE);

        TextView title = dialogView.findViewById(R.id.title);
        if (position < mFollowingModels.size()) {
            title.setText(mFollowingModels.get(position).getName());

            button.setOnClickListener(v -> {
                Utils.logEvent(mContext, "block_topic");
                if (!TextUtils.isEmpty(mFollowingModels.get(position).getType()) && mFollowingModels.get(position).getType().equals("TOPICS"))
                    blockTopic(mFollowingModels.get(position).getId(), position, view);
                else
                    blockSource(mFollowingModels.get(position).getId(), position, view);

                dialog.dismiss();
            });

        }
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void blockTopic(String id, int position, View view) {
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .blockTopic("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (mContext != null)
                        Utils.showSnacky(mContext.getWindow().getDecorView().getRootView(), mContext.getString(R.string.topic_blocked));
                    mFollowingModels.remove(position);
//                    notifyItemRemoved(position);
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
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void blockSource(String id, int position, View view) {
        Call<ShareBottomSheetResponse> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .blockSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ShareBottomSheetResponse>() {
            @Override
            public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                if (response.code() == 200) {
                    if (mContext != null)
                        Utils.showSnacky(mContext.getWindow().getDecorView().getRootView(), mContext.getString(R.string.source_blocked));
                    mFollowingModels.remove(position);
//                    notifyItemRemoved(position);
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
            public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unfollowTopic(String topicId, int position, ViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.ivFollow.setVisibility(View.GONE);
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unfollowTopic("Bearer " + mPrefConfig.getAccessToken(), topicId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                holder.progressBar.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    if (mFollowingModels != null && mFollowingModels.size() > 0) {
                        if (mCallback != null)
                            mCallback.onItemUnfollowed(mFollowingModels.get(position));
                        mFollowingModels.get(position).setFavorite(false);
                        notifyDataSetChanged();
                    }
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
                holder.progressBar.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unfollowSource(String id, int position, ViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.ivFollow.setVisibility(View.GONE);
        Call<ShareBottomSheetResponse> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unfollowSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ShareBottomSheetResponse>() {
            @Override
            public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                holder.progressBar.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    if (position < mFollowingModels.size()) {
                        if (mCallback != null)
                            mCallback.onItemUnfollowed(mFollowingModels.get(position));
                        mFollowingModels.get(position).setFavorite(false);
                    }
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
            public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {
                holder.progressBar.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void followTopic(String topicId, int position, ViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.ivFollow.setVisibility(View.GONE);
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .addTopic("Bearer " + mPrefConfig.getAccessToken(), topicId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                holder.progressBar.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    if (mFollowingModels != null && mFollowingModels.size() > 0) {
                        if (mCallback != null)
                            mCallback.onItemFollowed(mFollowingModels.get(position));
                        mFollowingModels.get(position).setFavorite(true);
                        notifyDataSetChanged();
                    }
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
                holder.progressBar.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void followSource(String id, int position, ViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.ivFollow.setVisibility(View.GONE);
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .followSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                holder.progressBar.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    if (mCallback != null && position < mFollowingModels.size()) {
                        mCallback.onItemFollowed(mFollowingModels.get(position));
                        mFollowingModels.get(position).setFavorite(true);
//                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                    }
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
                holder.progressBar.setVisibility(View.GONE);
                holder.ivFollow.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void add(Topics topic) {
        this.mFollowingModels.add(0, topic);
        notifyItemInserted(mFollowingModels.size() - 1);
    }

    public void setCount(boolean showAll) {
        this.isShowAll = showAll;
        notifyDataSetChanged();
    }

    public interface FollowingCallback {
        void onItemFollowed(Topics topic);

        void onItemUnfollowed(Topics topic);

        void onItemClicked(Topics topic);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView ivMore;
        private ImageView ivFollow;
        private TextView textView;
        private TextView subLabel;
        private RelativeLayout llFollow;
        private LinearLayout llMore;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMore = itemView.findViewById(R.id.ivMore);
            imageView = itemView.findViewById(R.id.img);
            textView = itemView.findViewById(R.id.label);
            llFollow = itemView.findViewById(R.id.follow_click);
            llMore = itemView.findViewById(R.id.ll_more);
            subLabel = itemView.findViewById(R.id.subLabel);
            ivFollow = itemView.findViewById(R.id.follow);
            progressBar = itemView.findViewById(R.id.progress);
        }
    }
}
