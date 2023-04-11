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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.fragments.BlockPopup;
import com.ziro.bullet.interfaces.ReportConcernDialog;
import com.ziro.bullet.model.ShareBottomSheetResponse;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;
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

public class FollowingSourcesAdapter extends RecyclerView.Adapter<FollowingSourcesAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Source> mFollowingModels;
    private FollowingCallback mCallback;
    private PrefConfig mPrefConfig;
    private int mTotalRecord = 0;

    public FollowingSourcesAdapter(Activity mContext, ArrayList<Source> mFollowingModels) {
        this.mContext = mContext;
        this.mFollowingModels = mFollowingModels;
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setTotalRecord(int totalRecord) {
        this.mTotalRecord = totalRecord;
    }

    public void setCallback(FollowingCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public FollowingSourcesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_following_sources, parent, false);
        return new FollowingSourcesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingSourcesAdapter.ViewHolder holder, int position) {
        if (position < mFollowingModels.size()) {
            Source source = mFollowingModels.get(position);
            if (source != null) {
                if (source.getId().equalsIgnoreCase("add")) {
                    holder.cardView.setVisibility(View.GONE);
                    holder.ll_data.setVisibility(View.VISIBLE);
                    holder.rlContainer.setOnClickListener(view -> {
                        if (mCallback != null)
                            mCallback.onAddSourceClicked();
                    });
                } else {
                    holder.cardView.setVisibility(View.VISIBLE);
                    holder.ll_data.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(mFollowingModels.get(position).getName())) {
                        holder.label.setText(mFollowingModels.get(position).getName());
                    }
                    if (!TextUtils.isEmpty(mFollowingModels.get(position).getImagePortraitOrNormal())) {
                        Picasso.get()
                                .load(mFollowingModels.get(position).getImagePortraitOrNormal())
                                .resize(Constants.targetWidth, Constants.targetHeight)
                                .onlyScaleDown()
                                .error(R.drawable.img_place_holder)
                                .into(holder.imageView);
                    } else holder.imageView.setImageResource(R.drawable.img_place_holder);

//                    holder.ivMore.setColorFilter(Color.parseColor(mFollowingModels.get(position).getText_color()), android.graphics.PorterDuff.Mode.SRC_IN);
                    holder.ll_more.setOnClickListener(view -> {
                        showBottomSheetDialog(position,holder.ll_more);
                    });

                    holder.rlContainer.setOnClickListener(view -> {
//            Intent intent = new Intent(mContext, ArticlesActivity.class);
//            intent.putExtra("source_id", mFollowingModels.get(position).getId());
//            intent.putExtra("source_name", mFollowingModels.get(position).getName());
//            mContext.startActivity(intent);
                        if (mCallback != null)
                            mCallback.onItemClicked(mFollowingModels.get(position));
                    });
                }
            }
        }

//        if (position != mFollowingModels.size()) {
//            holder.cardView.setVisibility(View.VISIBLE);
//            holder.ll_data.setVisibility(View.GONE);
//            if (!TextUtils.isEmpty(mFollowingModels.get(position).getImage())) {
//                Picasso.get()
//                        .load(mFollowingModels.get(position).getImage())
//                        .into(holder.imageView);
//            }
//            holder.ivMore.setColorFilter(Color.parseColor(mFollowingModels.get(position).getText_color()), android.graphics.PorterDuff.Mode.SRC_IN);
//            holder.ll_more.setOnClickListener(view -> {
//                showBottomSheetDialog(position);
//            });
//
//            holder.rlContainer.setOnClickListener(view -> {
////            Intent intent = new Intent(mContext, ArticlesActivity.class);
////            intent.putExtra("source_id", mFollowingModels.get(position).getId());
////            intent.putExtra("source_name", mFollowingModels.get(position).getName());
////            mContext.startActivity(intent);
//                if (mCallback != null)
//                    mCallback.onItemClicked(mFollowingModels.get(position));
//            });
//        } else {
//            holder.cardView.setVisibility(View.GONE);
//            holder.ll_data.setVisibility(View.VISIBLE);
//            holder.rlContainer.setOnClickListener(view -> {
//                if (mCallback != null)
//                    mCallback.onAddSourceClicked();
//            });
//        }
    }

    @Override
    public int getItemCount() {
//        if (mTotalRecord != 0 && mTotalRecord == mFollowingModels.size())
//            return mFollowingModels.size() + 1;
//        else
        return mFollowingModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imageView;
        private ImageView ivMore;
        private RelativeLayout rlContainer;
        private LinearLayout ll_more;
        private CardView ll_data;
        private CardView cardView;
        private TextView label;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            imageView = itemView.findViewById(R.id.image);
            ivMore = itemView.findViewById(R.id.ivMore);
            rlContainer = itemView.findViewById(R.id.rl_container);
            ll_more = itemView.findViewById(R.id.ll_more);
            ll_data = itemView.findViewById(R.id.ll_data);
            cardView = itemView.findViewById(R.id.card);
        }
    }

    public interface FollowingCallback {
        void onItemChanged(Source source);

        void onItemClicked(Source source);

        void onAddSourceClicked();
    }

    private void showBottomSheetDialog(int position,View view) {
        View dialogView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.bottom_sheet_following, null);
        BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.SheetDialog);
        dialog.setContentView(dialogView);
        Button button = dialogView.findViewById(R.id.btn_remove);
        Button buttonBlock = dialogView.findViewById(R.id.btn_block);
        Button buttonCancel = dialogView.findViewById(R.id.btn_cancel);
        RelativeLayout relativeLayout = dialogView.findViewById(R.id.rl_second);
        TextView title = dialogView.findViewById(R.id.title);

        if (mFollowingModels != null && position < mFollowingModels.size()) {
            title.setText(mFollowingModels.get(position).getName());

            if (mFollowingModels.get(position).isFavorite()) {
                button.setText(mContext.getString(R.string.unfollow_source));
                buttonBlock.setText(mContext.getString(R.string.block_source));
            } else {
                button.setText(mContext.getString(R.string.follow_channel));
                relativeLayout.setVisibility(View.GONE);
            }

            button.setOnClickListener(v -> {
                if (mFollowingModels.get(position).isFavorite()) {
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.SOURCE_ID,mFollowingModels.get(position).getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.UNFOLLOWED_SOURCE);
                    unfollowSource(mFollowingModels.get(position).getId(), position);
                } else {
                    Map<String,String> params = new HashMap<>();
                    params.put(Events.KEYS.SOURCE_ID,mFollowingModels.get(position).getId());
                    AnalyticsEvents.INSTANCE.logEvent(mContext,
                            params,
                            Events.FOLLOW_SOURCE);
                    followSource(mFollowingModels.get(position).getId(), position);
                }
                dialog.dismiss();
            });

            buttonBlock.setOnClickListener(v -> {
                BlockPopup blockPopup = new BlockPopup(mContext, new ReportConcernDialog() {
                    @Override
                    public void isPositive(boolean flag, String msg) {
                        if (flag) {
                            Map<String,String> params = new HashMap<>();
                            params.put(Events.KEYS.SOURCE_ID,mFollowingModels.get(position).getId());
                            AnalyticsEvents.INSTANCE.logEvent(mContext,
                                    params,
                                    Events.BLOCK_SOURCE);
                            blockSource(mFollowingModels.get(position).getId(), position, view);
                            dialog.dismiss();
                        }
                    }
                });
                blockPopup.showDialog(mFollowingModels.get(position).getName());
                dialog.dismiss();
            });
        }
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void blockSource(String id, int position,View view)  {
        Call<ShareBottomSheetResponse> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .blockSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ShareBottomSheetResponse>() {
            @Override
            public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                if (response.code() == 200) {
                    Utils.showSnacky(view, mContext.getString(R.string.source_blocked));
                    if (mCallback != null)
                        mCallback.onItemChanged(mFollowingModels.get(position));
                    mFollowingModels.remove(position);
                    mTotalRecord--;
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
                        mCallback.onItemChanged(mFollowingModels.get(position));
                    mFollowingModels.remove(position);
                    mTotalRecord--;
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
                    if (mCallback != null)
                        mCallback.onItemChanged(mFollowingModels.get(position));
                    mFollowingModels.get(position).setFavorite(true);
                    mTotalRecord--;
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
