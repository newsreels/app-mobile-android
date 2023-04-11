package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
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
public class BlockTopicsAdapter extends RecyclerView.Adapter<BlockTopicsAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Topics> mFollowingModels;
    private FollowingCallback mCallback;
    private PrefConfig mPrefConfig;

    public BlockTopicsAdapter(Activity mContext, ArrayList<Topics> mFollowingModels) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.block_item_v2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!TextUtils.isEmpty(mFollowingModels.get(position).getImage()))
            Picasso.get()
                    .load(mFollowingModels.get(position).getImage())
                    .resize(Constants.targetWidth, Constants.targetHeight)
                    .onlyScaleDown()
                    .error(R.drawable.img_place_holder)
                    .into(holder.imageView);
        else holder.imageView.setImageResource(R.drawable.img_place_holder);

        holder.textView.setText(mFollowingModels.get(position).getName());
        if (!TextUtils.isEmpty(mFollowingModels.get(position).getFollower())) {
            holder.subLabel.setVisibility(View.VISIBLE);
            holder.subLabel.setText(mFollowingModels.get(position).getFollower());
        } else {
            holder.subLabel.setVisibility(View.GONE);
        }
        holder.llMore.setOnClickListener(view -> {
//            Utils.showDialog(mContext, "Are you sure, you want to unblock " + mFollowingModels.get(position).getName() + "?", "yes", new dialogClick() {
//                @Override
//                public void isPositive(boolean flag) {
//                    if (flag) {
//                        Utils.logEvent(mContext,"unblock_topic");
//                        unblock(mFollowingModels.get(position).getId(), position);
//                    }
//                }
//            });
            showBottomSheetDialog(position, holder.llMore);
//            unblock(mFollowingModels.get(position).getId(), position, holder.llMore);
        });

//        holder.itemView.setOnClickListener(v -> {
//            showBottomSheetDialog(position);
//        });
    }

    @Override
    public int getItemCount() {
        return mFollowingModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private TextView subLabel;
        private RelativeLayout rlContainer;
        private LinearLayout llMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subLabel = itemView.findViewById(R.id.subLabel);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.tvLabel);
            llMore = itemView.findViewById(R.id.ll_more);
            rlContainer = itemView.findViewById(R.id.rl_container);
        }
    }

    public interface FollowingCallback {
        void onItemChanged(Topics topic);

        void refresh();

        void onItemClicked(Topics topic);
    }

    private void showBottomSheetDialog(int position, View view) {
        View dialogView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.bottom_sheet_following, null);
        BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.SheetDialog);
        dialog.setContentView(dialogView);
        Button button = dialogView.findViewById(R.id.btn_remove);
        Button cancel = dialogView.findViewById(R.id.btn_cancel);
        Button buttonBlock = dialogView.findViewById(R.id.btn_block);
        RelativeLayout relativeLayout = dialogView.findViewById(R.id.rl_second);

        button.setText(mContext.getString(R.string.unblock_topic));
        relativeLayout.setVisibility(View.GONE);

        TextView title = dialogView.findViewById(R.id.title);
        if (position < mFollowingModels.size()) {
            title.setText(mFollowingModels.get(position).getName());

            button.setOnClickListener(v -> {
                Map<String,String> params = new HashMap<>();
                params.put(Events.KEYS.TOPIC_ID,mFollowingModels.get(position).getId());
                AnalyticsEvents.INSTANCE.logEvent(mContext,
                        params,
                        Events.UNBLOCK_TOPIC);
                unblock(mFollowingModels.get(position).getId(), position, view);
                dialog.dismiss();
            });

        }
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void unblock(String id, int position, View view) {
        Call<ResponseBody> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unblockTopic("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Utils.showSnacky(view, mContext.getString(R.string.topic_unblocked));
                    if (mCallback != null) {
                        mCallback.onItemChanged(mFollowingModels.get(position));
                    }
                    mFollowingModels.remove(position);
//                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    if (mCallback != null) {
                        mCallback.refresh();
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
}
