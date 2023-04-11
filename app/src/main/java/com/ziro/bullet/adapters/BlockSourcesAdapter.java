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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
import com.ziro.bullet.data.models.sources.Source;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockSourcesAdapter extends RecyclerView.Adapter<BlockSourcesAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Source> mFollowingModels;
    private FollowingCallback mCallback;
    private PrefConfig mPrefConfig;

    public BlockSourcesAdapter(Activity mContext, ArrayList<Source> mFollowingModels) {
        this.mContext = mContext;
        this.mFollowingModels = mFollowingModels;
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setCallback(FollowingCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public BlockSourcesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.relevant_subitem_authors, parent, false);
        return new BlockSourcesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockSourcesAdapter.ViewHolder holder, int position) {

        if (position != mFollowingModels.size()) {
            holder.cardView.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(mFollowingModels.get(position).getName())) {
                holder.label.setText(mFollowingModels.get(position).getName());
            }
            if (!TextUtils.isEmpty(mFollowingModels.get(position).getUsername())) {
                holder.subLabel.setVisibility(View.VISIBLE);
                holder.subLabel.setText("@"+mFollowingModels.get(position).getUsername());
            } else {
                holder.subLabel.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mFollowingModels.get(position).getIcon())) {
                Picasso.get()
                        .load(mFollowingModels.get(position).getIcon())
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .onlyScaleDown()
                        .transform(new CropCircleTransformation())
                        .error(R.drawable.img_place_holder)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.img_place_holder);
            }
            holder.name.setText(mContext.getString(R.string.unblock));
            holder.name.setTextColor(ContextCompat.getColor(mContext, R.color.toolbar_icon));
            holder.item_color.setBackgroundColor(ContextCompat.getColor(mContext, R.color.edittextHint));
//            holder.ll_more.setOnClickListener(view -> {
//                showBottomSheetDialog(position,holder.ll_more);
//            });

            holder.cardView.setOnClickListener(view -> unblock(mFollowingModels.get(position).getId(), position, view));
        } else {
            holder.cardView.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mFollowingModels.size();
    }

    private void showBottomSheetDialog(int position,View view) {
        View dialogView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.bottom_sheet_following, null);
        BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.SheetDialog);
        dialog.setContentView(dialogView);
        Button button = dialogView.findViewById(R.id.btn_remove);
        Button cancel = dialogView.findViewById(R.id.btn_cancel);
        Button buttonBlock = dialogView.findViewById(R.id.btn_block);
        RelativeLayout relativeLayout = dialogView.findViewById(R.id.rl_second);

        button.setText(mContext.getString(R.string.unblock_source));
        relativeLayout.setVisibility(View.GONE);

        TextView title = dialogView.findViewById(R.id.title);
        if (position < mFollowingModels.size()) {
            title.setText(mFollowingModels.get(position).getName());

            button.setOnClickListener(v -> {
                dialog.dismiss();
                Map<String,String> params = new HashMap<>();
                params.put(Events.KEYS.SOURCE_ID,mFollowingModels.get(position).getId());
                AnalyticsEvents.INSTANCE.logEvent(mContext,
                        params,
                        Events.UNBLOCK_SOURCE);
                unblock(mFollowingModels.get(position).getId(), position, view);
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
                .unblockSource("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Utils.showPopupMessageWithCloseButton(mContext, 3000, mContext.getString(R.string.source_unblocked), false);
//                    Utils.showSnacky(view, mContext.getString(R.string.source_unblocked));
                    if (mCallback != null)
                        mCallback.onItemChanged(mFollowingModels.get(position));
                    mFollowingModels.remove(position);
//                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    if (mCallback != null)
                        mCallback.refresh();
                } else if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Utils.showPopupMessageWithCloseButton(mContext, 2000, "" + msg, false);

//                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
//                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Utils.showPopupMessageWithCloseButton(mContext, 2000, "" + t.getLocalizedMessage(), false);

            }
        });
    }

    public interface FollowingCallback {
        void onItemChanged(Source source);

        void refresh();

        void onItemClicked(Source source);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView name;
        private LinearLayout item_color;
        private CardView cardView;
        private TextView label, subLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            item_color = itemView.findViewById(R.id.item_color);
            cardView = itemView.findViewById(R.id.bookmark);
            subLabel = itemView.findViewById(R.id.username);
        }
    }
}
