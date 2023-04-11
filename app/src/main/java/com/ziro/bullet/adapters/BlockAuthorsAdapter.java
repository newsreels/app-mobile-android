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
import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.articles.Author;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockAuthorsAdapter extends RecyclerView.Adapter<BlockAuthorsAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Author> mFollowingModels;
    private FollowingCallback mCallback;
    private PrefConfig mPrefConfig;

    public BlockAuthorsAdapter(Activity mContext, ArrayList<Author> mFollowingModels) {
        this.mContext = mContext;
        this.mFollowingModels = mFollowingModels;
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setCallback(FollowingCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public BlockAuthorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_community_author_block, parent, false);
        return new BlockAuthorsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockAuthorsAdapter.ViewHolder holder, int position) {

        if (position != mFollowingModels.size()) {
            holder.card.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(mFollowingModels.get(position).getNameToDisplay())) {
                holder.title.setText(mFollowingModels.get(position).getNameToDisplay());
            }else {
                holder.title.setText(mFollowingModels.get(position).getUsername());
            }

            if (!TextUtils.isEmpty(mFollowingModels.get(position).getProfile_image())) {
                Picasso.get()
                        .load(mFollowingModels.get(position).getProfile_image())
                        .resize(Constants.targetWidth, Constants.targetHeight)
                        .onlyScaleDown()
                        .error(R.drawable.img_place_holder)
                        .into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.img_place_holder);
            }

            holder.ll_more.setOnClickListener(view -> {
                showBottomSheetDialog(position,holder.ll_more);
            });

        } else {
            holder.card.setVisibility(View.GONE);
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

        button.setText(mContext.getString(R.string.unblock));
        relativeLayout.setVisibility(View.GONE);

        TextView title = dialogView.findViewById(R.id.title);
        if (position < mFollowingModels.size()) {
            title.setText(mFollowingModels.get(position).getName());

            button.setOnClickListener(v -> {
                dialog.dismiss();
                Map<String,String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID,mFollowingModels.get(position).getId());
                AnalyticsEvents.INSTANCE.logEvent(mContext,
                        params,
                        Events.UNBLOCK_AUTHOR);
                unblock(mFollowingModels.get(position).getId(), position, view);
            });

        }
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void unblock(String id, int position, View view) {
        Call<ShareBottomSheetResponse> call = ApiClient
                .getInstance(mContext)
                .getApi()
                .unblockAuthor("Bearer " + mPrefConfig.getAccessToken(), id);
        call.enqueue(new Callback<ShareBottomSheetResponse>() {
            @Override
            public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {
                if (response.code() == 200) {
                    Utils.showSnacky(view, mContext.getString(R.string.author_unblocked));
                    if (mCallback != null)
                        mCallback.onItemChanged(mFollowingModels.get(position));
                    mFollowingModels.remove(position);
                    notifyDataSetChanged();
                    if (mCallback != null)
                        mCallback.refresh();
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

    public interface FollowingCallback {
        void onItemChanged(Author author);

        void refresh();

        void onItemClicked(Author author);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private LinearLayout ll_more;
        private CardView card;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_more = itemView.findViewById(R.id.ll_more);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            card = itemView.findViewById(R.id.card);
        }
    }
}
