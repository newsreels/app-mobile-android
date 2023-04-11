package com.ziro.bullet.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.LanguageInterface;
import com.ziro.bullet.model.language.LanguagesItem;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shine_joseph on 20/05/20.
 */
public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> implements Filterable {
    private ArrayList<LanguagesItem> mItems = new ArrayList<>();
    private ArrayList<LanguagesItem> mResult = new ArrayList<>();
    private Context mContext;
    private LanguageClickCallback mCallback;
    private PrefConfig prefConfig;
    private LanguageInterface languageInterface;
    private final boolean isPostLanguageSelection;

    public LanguageAdapter(ArrayList<LanguagesItem> mItems, Context context, LanguageInterface languageInterface, boolean isPostLanguageSelection) {
        this.mItems = mItems;
        this.mResult = mItems;
        this.mContext = context;
        this.languageInterface = languageInterface;
        this.isPostLanguageSelection = isPostLanguageSelection;
        prefConfig = new PrefConfig(context);
    }

    public void setCallback(LanguageClickCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_language, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItems = (ArrayList<LanguagesItem>) results.values;
                if (languageInterface != null) languageInterface.languageResult(mItems.size());
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<LanguagesItem> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = mResult;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }
        };
    }

    protected ArrayList<LanguagesItem> getFilteredResults(String constraint) {
        ArrayList<LanguagesItem> results = new ArrayList<>();
        for (LanguagesItem item : mResult) {
            if (item.getName().toLowerCase().startsWith(constraint)) {
                results.add(item);
            }
        }
        return results;
    }


    public void setSelected(int position) {
        mItems.get(position).setSelected(true);
        notifyDataSetChanged();
    }

    public void updateLanguageList(ArrayList<LanguagesItem> languagesItemList) {
        this.mItems = languagesItemList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mItems != null && mItems.size() > 0) {
            LanguagesItem item = mItems.get(position);
            if (item != null) {
                if (!TextUtils.isEmpty(item.getName())) {
                    holder.tvLabel.setText(item.getName());
                }
                if (!TextUtils.isEmpty(item.getLabel())) {
                    holder.tvText.setText(item.getLabel());
                }

                if (isPostLanguageSelection) {
                    if (item.getTag().equals(Constants.SECONDARY_LANGUAGE)) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_lang_bg));
                        holder.ivSelectedTick.setVisibility(View.VISIBLE);
                    } else {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                        holder.ivSelectedTick.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (item.getTag().equals(Constants.PRIMARY_LANGUAGE)) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_lang_bg));
                        holder.ivSelectedTick.setVisibility(View.VISIBLE);
                    } else {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                        holder.ivSelectedTick.setVisibility(View.INVISIBLE);
                    }
                }
                holder.item.setOnClickListener(v -> {
//                    if (!isPostLanguageSelection && prefConfig.getDefaultLanguage() != null && prefConfig.getDefaultLanguage().getCode().equalsIgnoreCase(item.getCode())) {
//                        return;
//                    }
                    if (mCallback != null) {
                        mCallback.onClick(position, item.getId(), item.getCode());
                    }
                });

                if (position == getItemCount() - 1) {
                    if (mCallback != null) {
                        mCallback.isLastItem(true);
                    }
                } else {
                    if (mCallback != null) {
                        mCallback.isLastItem(false);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface LanguageClickCallback {
        void onClick(int position, String id, String code);

        void isLastItem(boolean flag);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout item;
        private TextView tvLabel;
        private TextView tvText;
        private ImageView ivSelectedTick;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_sub_label);
            item = itemView.findViewById(R.id.item);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            ivSelectedTick = itemView.findViewById(R.id.iv_selected_tick);
        }
    }
}
