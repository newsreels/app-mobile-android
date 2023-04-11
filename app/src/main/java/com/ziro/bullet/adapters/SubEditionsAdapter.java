package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.EditionActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.Edition.EditionsItem;
import com.ziro.bullet.model.Edition.ResponseEdition;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shine_joseph on 20/05/20.
 */
public class SubEditionsAdapter extends RecyclerView.Adapter<SubEditionsAdapter.ViewHolder> {
    private ArrayList<EditionsItem> mItems = new ArrayList<>();
    private Activity mContext;
    private EditionOnclickCallback mCallback;
    private PrefConfig prefConfig;

    public SubEditionsAdapter(ArrayList<EditionsItem> mItems, Activity context) {
        this.mItems = mItems;
        this.mContext = context;
        this.prefConfig = new PrefConfig(context);
    }

    public void setCallback(EditionOnclickCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_edition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mItems != null && mItems.size() > 0) {
            EditionsItem item = mItems.get(position);
            if (item != null) {
                if (!TextUtils.isEmpty(item.getName())) {
                    holder.tvLabel.setText(item.getName());
                }
                if (!TextUtils.isEmpty(item.getLanguage())) {
                    holder.tvText.setText(item.getLanguage());
                }
                if (!TextUtils.isEmpty(item.getImage())) {
                    Picasso.get()
                            .load(item.getImage())
                            .into(holder.flag);
                }
                if (item.isSelected()) {
                    holder.radio.setImageResource(R.drawable.ic_checkbox_active);
                } else {
                    holder.radio.setImageResource(R.drawable.ic_checkbox_inactive);
                }
                holder.item.setOnClickListener(v -> {
                    if (item.getHas_child()) {
                        if (item.getExpanded()) {
                            item.setExpanded(false);
                        } else {
                            item.setExpanded(true);
                        }
                        notifyDataSetChanged();
                    }
                });
                holder.item2.setOnClickListener(v -> {
                    if (mCallback != null) {
                        mCallback.onClickMark(position, item.getId(), !item.isSelected());
                    }
                });

                holder.flag.setVisibility(View.GONE);
                if (item.getHas_child()) {
                    holder.main.setVisibility(View.VISIBLE);
                    holder.isExpandable.setVisibility(View.VISIBLE);
                    holder.isExpandableProgress.setVisibility(View.GONE);
                } else {
                    holder.main.setVisibility(View.INVISIBLE);
                    holder.isExpandable.setVisibility(View.INVISIBLE);
                    holder.isExpandableProgress.setVisibility(View.GONE);
                }

                if (item.getExpanded()) {
                    holder.subEdition.setVisibility(View.VISIBLE);
                    holder.isExpandable.setImageResource(R.drawable.ic_expand_arrow);
                    if (item.getSubEditionList() == null || item.getSubEditionList().size() == 0) {
                        getEditions(item, holder);
                    } else {
                        setAdapter(item.getSubEditionList(),item,holder);
                    }
                } else {
                    holder.subEdition.setVisibility(View.GONE);
                    holder.isExpandable.setImageResource(R.drawable.ic_collapse_arrow);
                }
            }
        }
    }

    public void setAdapter(ArrayList<EditionsItem> arrayList, EditionsItem item, ViewHolder holder) {
        if (holder != null && holder instanceof ViewHolder) {
            holder.isExpandableProgress.setVisibility(View.GONE);
            holder.isExpandable.setImageResource(R.drawable.ic_expand_arrow);
            Log.e("onClickItem", "--receive--> " + item.getName());
            item.setSubEditionList(arrayList);
            SubEditionsAdapter adapter = new SubEditionsAdapter(arrayList, mContext);
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            holder.subEdition.setLayoutManager(manager);
            adapter.setCallback(new EditionOnclickCallback() {
                @Override
                public void onClickItem(int position, String id) {

                }

                @Override
                public void onClickMarkInner(int position, String id, boolean isFollow) {
                    if (mCallback != null) {
                        mCallback.onClickMarkInner(position, id, isFollow);
                    }
                }

                @Override
                public void onClickMark(int position, String id, boolean isFollow) {
                    if (mCallback != null) {
                        mCallback.onClickMarkInner(position, id, isFollow);
                    }
                    arrayList.get(position).setSelected(isFollow);
                    adapter.notifyDataSetChanged();
                }
            });
            holder.subEdition.setAdapter(adapter);
            holder.subEdition.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface EditionOnclickCallback {
        void onClickItem(int position, String id);
        void onClickMarkInner(int position, String id, boolean isFollow);
        void onClickMark(int position, String id, boolean isFollow);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item2;
        private RelativeLayout item;
        private RoundedImageView flag;
        private TextView tvLabel;
        private TextView tvText;
        private ImageView radio;
        private RecyclerView subEdition;
        private ImageView isExpandable;
        private ProgressBar isExpandableProgress;
        private RelativeLayout main;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.main);
            isExpandableProgress = itemView.findViewById(R.id.isExpandableProgress);
            isExpandable = itemView.findViewById(R.id.isExpandable);
            subEdition = itemView.findViewById(R.id.subEdition);
            item2 = itemView.findViewById(R.id.item2);
            tvText = itemView.findViewById(R.id.tvText);
            item = itemView.findViewById(R.id.item);
            flag = itemView.findViewById(R.id.flag);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            radio = itemView.findViewById(R.id.radio);
        }
    }

    public void getEditions(EditionsItem item, ViewHolder holder) {
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(mContext, mContext.getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            holder.isExpandableProgress.setVisibility(View.GONE);
        } else {
            holder.isExpandableProgress.setVisibility(View.VISIBLE);
            Call<ResponseEdition> call = ApiClient
                    .getInstance(mContext)
                    .getApi()
                    .getEditions("Bearer " + prefConfig.getAccessToken(), item.getId());
            call.enqueue(new Callback<ResponseEdition>() {
                @Override
                public void onResponse(@NotNull Call<ResponseEdition> call, @NotNull Response<ResponseEdition> response) {
                    holder.isExpandableProgress.setVisibility(View.GONE);
                    if (response != null) {
                        if (response.code() == 200) {
                            Log.e("HOMELIFE", "userTopics : here");
                            if (response.body() != null && response.body().getEditions() != null && response.body().getEditions().size() > 0) {
                                setAdapter(response.body().getEditions(), item, holder);
                            } else {
                                if (!TextUtils.isEmpty(response.message())) {
                                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseEdition> call, @NotNull Throwable t) {
                    holder.isExpandableProgress.setVisibility(View.GONE);
                }
            });
        }
    }

}
