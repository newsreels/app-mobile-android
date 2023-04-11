package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.fragments.BlockPopup;
import com.ziro.bullet.interfaces.ReportConcernDialog;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.topics.Topics;

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

/**
 * Created by shine_joseph on 21/05/20.
 */
public class FollowingTopicsAdapter extends RecyclerView.Adapter<FollowingTopicsAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Topics> mFollowingModels;
    private FollowingCallback mCallback;
    private PrefConfig mPrefConfig;

    public FollowingTopicsAdapter(Activity mContext, ArrayList<Topics> mFollowingModels) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_following_topics, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topics topic = mFollowingModels.get(position);
        if (topic != null) {
            if (topic.getId().equalsIgnoreCase("add")) {
                holder.rlContainer.setVisibility(View.GONE);
                holder.ll_data.setVisibility(View.VISIBLE);
                holder.ll_data.setOnClickListener(v -> {
                    if (mCallback != null)
                        mCallback.onAddTopicClicked();
                });
            } else {
                holder.rlContainer.setVisibility(View.VISIBLE);
                holder.ll_data.setVisibility(View.GONE);
                holder.ll_data.setOnClickListener(null);
            }
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
            holder.llMore.setOnClickListener(view -> showBottomSheetDialog(position, holder.llMore));
            holder.itemView.setOnClickListener(view -> {
                if (topic.getId().equalsIgnoreCase("add")) {
                    return;
                }
                if (mCallback != null)
                    mCallback.onItemClicked(topic);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFollowingModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private RelativeLayout rlContainer;
        private FrameLayout ll_data;
        private LinearLayout llMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.tvLabel);
            llMore = itemView.findViewById(R.id.ll_more);
            rlContainer = itemView.findViewById(R.id.rl_container);
            ll_data = itemView.findViewById(R.id.ll_data);
        }
    }

    public interface FollowingCallback {
        void onItemChanged(Topics topic);

        void onItemClicked(Topics topic);

        void onAddTopicClicked();
    }


    private void showBottomSheetDialog(int position,View view) {

        View dialogView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.bottom_sheet_following, null);
        BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.SheetDialog);
        dialog.setContentView(dialogView);
        Button button = dialogView.findViewById(R.id.btn_remove);
        Button cancel = dialogView.findViewById(R.id.btn_cancel);
        Button buttonBlock = dialogView.findViewById(R.id.btn_block);
        RelativeLayout relativeLayout = dialogView.findViewById(R.id.rl_second);
        TextView title = dialogView.findViewById(R.id.title);
        if (mFollowingModels != null && position < mFollowingModels.size()) {

            if (mFollowingModels.get(position).isFavorite()) {
                button.setText(mContext.getString(R.string.unfollow_topic));
                buttonBlock.setText(mContext.getString(R.string.block_topic));
            } else {
                button.setText(mContext.getString(R.string.follow_topic));
                relativeLayout.setVisibility(View.GONE);
            }

            title.setText(mFollowingModels.get(position).getName());

            button.setOnClickListener(v -> {
                if (mFollowingModels.get(position).isFavorite()) {
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.TOPIC_ID,mFollowingModels.get(position).getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.UNFOLLOWED_TOPIC);
                    unfollowTopic(mFollowingModels.get(position).getId(), position);
                } else {
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.TOPIC_ID,mFollowingModels.get(position).getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.FOLLOW_TOPIC);
                    followTopic(mFollowingModels.get(position).getId(), position);
                }
                dialog.dismiss();
            });
            buttonBlock.setOnClickListener(v -> {
                BlockPopup blockPopup = new BlockPopup(mContext, new ReportConcernDialog() {
                    @Override
                    public void isPositive(boolean flag, String msg) {
                        if (flag) {
                            Map<String,String> params = new HashMap<>();
                            params.put(Events.KEYS.TOPIC_ID,mFollowingModels.get(position).getId());
                            AnalyticsEvents.INSTANCE.logEvent(mContext,
                                    params,
                                    Events.BLOCK_TOPIC);
                            blockTopic(mFollowingModels.get(position).getId(), position, view);
                            dialog.dismiss();
                        }
                    }
                });
                blockPopup.showDialog(mFollowingModels.get(position).getName());
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
                    Utils.showSnacky(view, mContext.getString(R.string.topic_blocked));
                    if (mCallback != null)
                        mCallback.onItemChanged(mFollowingModels.get(position));
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

    private void unfollowTopic(String id, int position) {
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unfollowTopic("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (mCallback != null)
                        mCallback.onItemChanged(mFollowingModels.get(position));
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

    private void followTopic(String id, int position) {
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .addTopic("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (mCallback != null)
                        mCallback.onItemChanged(mFollowingModels.get(position));
                    mFollowingModels.get(position).setFavorite(true);
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
}
