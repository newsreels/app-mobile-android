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
import com.ziro.bullet.model.ShareBottomSheetResponse;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.Source;

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

public class SearchSourcesAdapter extends RecyclerView.Adapter<SearchSourcesAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Source> mFollowingModels;
    private SearchSourcesAdapter.FollowingCallback mCallback;
    private PrefConfig mPrefConfig;
    private boolean isFollowedMode = true;
    private boolean isShowAll = false;

    public SearchSourcesAdapter(Activity mContext, ArrayList<Source> mFollowingModels, boolean isFollowedMode) {
        this.mContext = mContext;
        this.mFollowingModels = mFollowingModels;
        this.isFollowedMode = isFollowedMode;
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setCallback(SearchSourcesAdapter.FollowingCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public SearchSourcesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_following_topics, parent, false);
        return new SearchSourcesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchSourcesAdapter.ViewHolder holder, int position) {
        Source source = mFollowingModels.get(position);
        if (source != null) {
            holder.rlContainer.setVisibility(View.VISIBLE);
            holder.ll_data.setVisibility(View.GONE);
            holder.ll_data.setOnClickListener(null);

            if (!TextUtils.isEmpty(source.getImagePortraitOrNormal())) {
                Picasso.get()
                        .load(source.getImagePortraitOrNormal())
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .onlyScaleDown()
                        .error(R.drawable.img_place_holder)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.img_place_holder);
            }
            holder.textView.setText(source.getName());
            if (!isFollowedMode) {
                if (source.isFavorite()) {
                    holder.ivMore.setImageResource(R.drawable.ic_bookmark_selected_without_border);
                } else {
                    holder.ivMore.setImageResource(R.drawable.ic_bookmark_unselected);
                }
//                holder.ivMore.setImageResource(R.drawable.ic_bookmark_unselected);
            } else {
                holder.ivMore.setImageResource(R.drawable.ic_3_dots);
            }
            holder.llMore.setOnClickListener(view -> {
                if (isFollowedMode)
                    showBottomSheetDialog(position);
                else {
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.SOURCE_ID,mFollowingModels.get(position).getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.FOLLOW_SOURCE);
                    followSource(source.getId(), position);
                }
            });
            holder.itemView.setOnClickListener(view -> {
                if (mCallback != null)
                    mCallback.onItemClicked(source);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (isShowAll)
            return mFollowingModels.size();
        else {
            if (mFollowingModels.size() < 5)
                return mFollowingModels.size();
            else
                return 5;
        }
    }

    private void showBottomSheetDialog(int position) {
        View dialogView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.bottom_sheet_following, null);
        BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.SheetDialog);
        dialog.setContentView(dialogView);
        Button button = dialogView.findViewById(R.id.btn_remove);
        Button cancel = dialogView.findViewById(R.id.btn_cancel);
        Button buttonBlock = dialogView.findViewById(R.id.btn_block);
        RelativeLayout relativeLayout = dialogView.findViewById(R.id.rl_second);


//        if (mFollowingModels.get(position).isFavorite()) {
        button.setText(mContext.getString(R.string.unfollow_source));
        buttonBlock.setText(mContext.getString(R.string.block_channel));
//        } else {
//            button.setText("Follow Topic");
//            relativeLayout.setVisibility(View.GONE);
//        }


        TextView title = dialogView.findViewById(R.id.title);
        if (position < mFollowingModels.size()) {
            title.setText(mFollowingModels.get(position).getName());

            button.setOnClickListener(v -> {
//                if (mFollowingModels.get(position).isFavorite()) {
//                    unfollowTopic(mFollowingModels.get(position).getId(), position);
//                } else {
                Map<String,String> params = new HashMap<>();
                params.put(Events.KEYS.SOURCE_ID,mFollowingModels.get(position).getId());
                AnalyticsEvents.INSTANCE.logEvent(mContext,
                        params,
                        Events.UNFOLLOWED_SOURCE);
                unfollowSource(mFollowingModels.get(position).getId(), position);
//                }
                dialog.dismiss();
            });
            buttonBlock.setOnClickListener(v -> {
                Map<String,String> params = new HashMap<>();
                params.put(Events.KEYS.SOURCE_ID,mFollowingModels.get(position).getId());
                AnalyticsEvents.INSTANCE.logEvent(mContext,
                        params,
                        Events.BLOCK_SOURCE);
                blockSource(mFollowingModels.get(position).getId(), position);
                dialog.dismiss();
            });
        }
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void blockSource(String id, int position) {
        Call<ShareBottomSheetResponse> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .blockSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ShareBottomSheetResponse>() {
            @Override
            public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                if (response.code() == 200) {

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

    private void unfollowSource(String id, int position) {
        Call<ShareBottomSheetResponse> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unfollowSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ShareBottomSheetResponse>() {
            @Override
            public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                if (response.code() == 200) {
                    if (mCallback != null)
                        mCallback.onItemRemoved(mFollowingModels.get(position));
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

    private void followSource(String id, int position) {
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .followSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (mCallback != null && position < mFollowingModels.size()) {
                        mCallback.onItemRemoved(mFollowingModels.get(position));
                        mFollowingModels.remove(position);
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
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void add(Source source) {
        this.mFollowingModels.add(0, source);
        notifyItemInserted(mFollowingModels.size() - 1);
    }

    public void setCount(boolean showAll) {
        this.isShowAll = showAll;
        notifyDataSetChanged();
    }

    public interface FollowingCallback {
        void onItemRemoved(Source source);

        void onItemClicked(Source source);

        void onAddTopicClicked();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView ivMore;
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
            ivMore = itemView.findViewById(R.id.ivMore);
        }
    }
}
