package com.ziro.bullet.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;

/**
 * Created by shine_joseph on 20/05/20.
 */
public class SettingsMenuAdapter extends RecyclerView.Adapter<SettingsMenuAdapter.ViewHolder> {
    private ArrayList<String> mItems = new ArrayList<>();
    private Context mContext;
    private SettingsCallback mCallback;
    private PrefConfig prefConfig;

    private String BLOCKLIST = null;
    private String HAPTIC = null;
    private String THEME = null;
    private String PUSH_NOTIFICATIONS = null;
    private String FEEDBACK = null;
    private String MY_ACCOUNT = null;
    private String LANGUAGE = null;
    private String EDITION_REGION = null;

    public SettingsMenuAdapter(ArrayList<String> mItems, Context context) {
        this.mItems = mItems;
        this.mContext = context;
        prefConfig = new PrefConfig(context);

        HAPTIC = context.getString(R.string.haptics);
        MY_ACCOUNT = context.getString(R.string.ny_account);
        PUSH_NOTIFICATIONS = context.getString(R.string.push_notifications);
        THEME = context.getString(R.string.reader_theme);
        BLOCKLIST = context.getString(R.string.block_list);
        FEEDBACK = context.getString(R.string.feedback);
        LANGUAGE = context.getString(R.string.language);
        EDITION_REGION = context.getString(R.string.edition_region);
    }

    public void setCallback(SettingsCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_settings_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mItems != null && mItems.size() > 0) {
            String item = mItems.get(position);
            if (!TextUtils.isEmpty(item)) {
//                if (item.equalsIgnoreCase(BLOCKLIST)) {
//                    holder.icon.setVisibility(View.GONE);
//                    holder.arrow.setVisibility(View.GONE);
//                    holder.menu.setVisibility(View.GONE);
//                    holder.tvLabel.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
//                    holder.tvLabel.setPadding(20, 20, 20, 20);
//                    holder.itemView.setOnClickListener(view -> {
//                        if (mCallback != null)
//                            mCallback.onClick(item);
//                    });
//                } else

                holder.flag.setVisibility(View.GONE);
                if (item.equalsIgnoreCase(MY_ACCOUNT)) {
                    holder.tvHeading.setVisibility(View.VISIBLE);
                    holder.tvHeading.setText(mContext.getString(R.string.account));
                } else if (item.equalsIgnoreCase(PUSH_NOTIFICATIONS)) {
                    holder.tvHeading.setVisibility(View.VISIBLE);
                    holder.tvHeading.setText(mContext.getString(R.string.notifications));
                } else if (item.equalsIgnoreCase(THEME)) {
                    holder.tvHeading.setVisibility(View.VISIBLE);
                    holder.tvHeading.setText(mContext.getString(R.string.personalization));
                } else if (item.equalsIgnoreCase(FEEDBACK)) {
                    holder.tvHeading.setVisibility(View.VISIBLE);
                    holder.tvHeading.setText(mContext.getString(R.string.others));
                } else if (item.equalsIgnoreCase(LANGUAGE)) {
                    holder.flag.setVisibility(View.VISIBLE);
                    if (prefConfig != null && prefConfig.getDefaultLanguage() != null) {
                        if (!TextUtils.isEmpty(prefConfig.getDefaultLanguage().getImage())) {

                            Picasso.get().load(prefConfig.getDefaultLanguage().getImage()).into(holder.flag);
//                            Utils.fetchSvg(mContext, prefConfig.getDefaultLanguage().getImage(), holder.flag);
                        }
                    }
                } else if (item.equalsIgnoreCase(EDITION_REGION)) {
                    if (prefConfig != null && prefConfig.getEditions() != null && prefConfig.getEditions().size() > 0) {

                        for (int i = 0; i < prefConfig.getEditions().size(); i++) {
                            if (i == 0) {
                                holder.ivFlag1.setVisibility(View.VISIBLE);
                                if (!TextUtils.isEmpty(prefConfig.getEditions().get(i).getImage())) {
                                    Picasso.get().load(prefConfig.getEditions().get(i).getImage()).into(holder.ivFlag1);
                                }
//                                GlideToVectorYou.init().with(mContext).load(Uri.parse(prefConfig.getEditions().get(i).getImage()), holder.ivFlag1);
//                                Utils.fetchSvg(mContext, prefConfig.getEditions().get(i).getImage(), holder.ivFlag1);
                            } else if (i == 1) {
                                holder.ivFlag2.setVisibility(View.VISIBLE);
                                if (!TextUtils.isEmpty(prefConfig.getEditions().get(i).getImage())) {
                                    Picasso.get().load(prefConfig.getEditions().get(i).getImage()).into(holder.ivFlag2);
                                }
//                                GlideToVectorYou.init().with(mContext).load(Uri.parse(prefConfig.getEditions().get(i).getImage()), holder.ivFlag2);
//                                Utils.fetchSvg(mContext, prefConfig.getEditions().get(i).getImage(), holder.ivFlag2);
                            } else if (i == 2) {
                                holder.ivFlag3.setVisibility(View.VISIBLE);
                                if (!TextUtils.isEmpty(prefConfig.getEditions().get(i).getImage())) {
                                    Picasso.get().load(prefConfig.getEditions().get(i).getImage()).into(holder.ivFlag3);
                                }
//                                GlideToVectorYou.init().with(mContext).load(Uri.parse(prefConfig.getEditions().get(i).getImage()), holder.ivFlag3);
//                                Utils.fetchSvg(mContext, prefConfig.getEditions().get(i).getImage(), holder.ivFlag3);
                            }
                        }
                    }
                } else {
                    holder.tvHeading.setVisibility(View.GONE);
                }

                if (item.equalsIgnoreCase(THEME)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        holder.tab1.setVisibility(View.VISIBLE);
//                        holder.rline.setVisibility(View.VISIBLE);
//                        holder.lline.setVisibility(View.VISIBLE);
//                    } else {
                    holder.tab1.setVisibility(View.GONE);
                    holder.rline.setVisibility(View.GONE);
                    holder.lline.setVisibility(View.GONE);
//                    }
                    holder.icon.setVisibility(View.VISIBLE);
                    holder.arrow.setVisibility(View.GONE);
                    holder.hapticSwitch.setVisibility(View.GONE);
                    holder.menu.setVisibility(View.VISIBLE);
                    holder.tvLabel.setBackground(null);
                    holder.tvLabel.setPadding(0, 0, 0, 0);

                    holder.tab1.setOnClickListener(view -> {
                        prefConfig.setAppTheme(Constants.AUTO);
//                        auto(holder);
                        if (mCallback != null)
                            mCallback.onClick(Constants.AUTO);
                    });

                    holder.tab2.setOnClickListener(view -> {
                        prefConfig.setAppTheme(Constants.LIGHT);
//                        light(holder);
                        if (mCallback != null)
                            mCallback.onClick(Constants.LIGHT);
                    });

                    holder.tab3.setOnClickListener(view -> {
                        prefConfig.setAppTheme(Constants.DARK);
//                        dark(holder);
                        if (mCallback != null)
                            mCallback.onClick(Constants.DARK);
                    });

                } else if (item.equalsIgnoreCase(HAPTIC)) {
                    holder.icon.setVisibility(View.GONE);
                    holder.arrow.setVisibility(View.GONE);
                    holder.hapticSwitch.setVisibility(View.VISIBLE);
                    holder.menu.setVisibility(View.GONE);

                    if (prefConfig.isHaptic()) {
                        on(holder, mContext);
                    } else {
                        off(holder, mContext);
                    }

                    holder.off.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            off(holder, mContext);
                            prefConfig.setHaptic(false);
                        }
                    });

                    holder.on.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            on(holder, mContext);
                            prefConfig.setHaptic(true);
                        }
                    });

                } else {
                    holder.icon.setVisibility(View.VISIBLE);
                    holder.arrow.setVisibility(View.VISIBLE);
                    holder.menu.setVisibility(View.GONE);
                    holder.hapticSwitch.setVisibility(View.GONE);
                    holder.tvLabel.setBackground(null);
                    holder.tvLabel.setPadding(0, 0, 0, 0);
                    holder.itemView.setOnClickListener(view -> {
                        if (mCallback != null)
                            mCallback.onClick(item);
                    });
                }
                if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.AUTO)) {
//                    switch (mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
//                        case Configuration.UI_MODE_NIGHT_YES:
////                            holder.tvLabel.setTextColor(mContext.getResources().getColor(R.color.darkText));
//                            holder.menu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_dark));
//                            break;
//                        case Configuration.UI_MODE_NIGHT_NO:
//                            holder.tvLabel.setTextColor(mContext.getResources().getColor(R.color.lightText));
//                            holder.menu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_light_back));
//                            break;~
//                    }
                    auto(holder);
                } else if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
                    dark(holder);
//                    holder.tvLabel.setTextColor(mContext.getResources().getColor(R.color.darkText));
//                    holder.menu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_dark));
                } else {
                    light(holder);
//                    holder.tvLabel.setTextColor(mContext.getResources().getColor(R.color.lightText));
//                    holder.menu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_light_back));
                }
                holder.tvLabel.setText(item);
                holder.tvLabel.setTextColor(ContextCompat.getColor(mContext, R.color.textHeader));
                holder.tvHeading.setTextColor(ContextCompat.getColor(mContext, R.color.textSubTitleGrey));
                holder.arrow.setImageResource(R.drawable.ic_next_arrow);
                holder.divider.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme_tab_bg));
            }
        }
    }

    private void on(ViewHolder holder, Context context) {
        holder.on.setBackground(context.getDrawable(R.drawable.shape));
        holder.on_text.setTextColor(context.getResources().getColor(R.color.tab_selected));
        holder.off.setBackground(null);
        holder.off_text.setTextColor(context.getResources().getColor(R.color.tab_unselected));
    }

    private void off(ViewHolder holder, Context context) {
        holder.off.setBackground(context.getDrawable(R.drawable.shape));
        holder.off_text.setTextColor(context.getResources().getColor(R.color.tab_selected));
        holder.on.setBackground(null);
        holder.on_text.setTextColor(context.getResources().getColor(R.color.tab_unselected));
    }


    private void dark(ViewHolder holder) {
        holder.rline.setVisibility(View.GONE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            holder.lline.setVisibility(View.VISIBLE);
//        } else {
        holder.lline.setVisibility(View.GONE);
//        }

        holder.tab3.setBackground(mContext.getResources().getDrawable(R.drawable.shape));
        holder.tab3_txt.setTextColor(mContext.getResources().getColor(R.color.tab_selected));
        holder.menu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_dark));
        holder.tab1.setBackground(null);
        holder.tab1_txt.setTextColor(mContext.getResources().getColor(R.color.tab_unselected));
        holder.tab2.setBackground(null);
        holder.tab2_txt.setTextColor(mContext.getResources().getColor(R.color.tab_unselected));
    }

    private void light(ViewHolder holder) {
        holder.rline.setVisibility(View.GONE);
        holder.lline.setVisibility(View.GONE);
        holder.tab2.setBackground(mContext.getResources().getDrawable(R.drawable.shape));
        holder.tab2_txt.setTextColor(mContext.getResources().getColor(R.color.tab_selected));
        holder.menu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_dark));
        holder.tab1.setBackground(null);
        holder.tab1_txt.setTextColor(mContext.getResources().getColor(R.color.tab_unselected));
        holder.tab3.setBackground(null);
        holder.tab3_txt.setTextColor(mContext.getResources().getColor(R.color.tab_unselected));
    }


    private void auto(@NonNull ViewHolder holder) {
        holder.rline.setVisibility(View.VISIBLE);
        holder.lline.setVisibility(View.GONE);
        holder.tab1.setBackground(mContext.getResources().getDrawable(R.drawable.shape));
        holder.tab1_txt.setTextColor(mContext.getResources().getColor(R.color.tab_selected));

        switch (mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                holder.menu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_dark));
                holder.tab2.setBackground(null);
                holder.tab2_txt.setTextColor(mContext.getResources().getColor(R.color.tab_unselected));
                holder.tab3.setBackground(null);
                holder.tab3_txt.setTextColor(mContext.getResources().getColor(R.color.tab_unselected));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                holder.menu.setBackground(mContext.getResources().getDrawable(R.drawable.shape_dark));
                holder.tab2.setBackground(null);
                holder.tab2_txt.setTextColor(mContext.getResources().getColor(R.color.tab_unselected));
                holder.tab3.setBackground(null);
                holder.tab3_txt.setTextColor(mContext.getResources().getColor(R.color.tab_unselected));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface SettingsCallback {
        void onClick(String item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLabel, tab1_txt, tab2_txt, tab3_txt;
        private RelativeLayout icon, lline, rline;
        private ImageView arrow;
        private LinearLayout menu, tab1, tab2, tab3;
        private LinearLayout hapticSwitch;
        private LinearLayout on;
        private TextView on_text;
        private LinearLayout off;
        private TextView off_text;
        private TextView tvHeading;
        private RelativeLayout divider;
        private RoundedImageView flag;
        private RoundedImageView ivFlag1, ivFlag2, ivFlag3;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            flag = itemView.findViewById(R.id.flag);
            hapticSwitch = itemView.findViewById(R.id.hapticSwitch);
            tvHeading = itemView.findViewById(R.id.tvHeading);
            on = itemView.findViewById(R.id.on);
            on_text = itemView.findViewById(R.id.on_text);
            off = itemView.findViewById(R.id.off);
            off_text = itemView.findViewById(R.id.off_text);
            rline = itemView.findViewById(R.id.rline);
            lline = itemView.findViewById(R.id.lline);
            tab1 = itemView.findViewById(R.id.tab1);
            tab2 = itemView.findViewById(R.id.tab2);
            tab3 = itemView.findViewById(R.id.tab3);
            tab1_txt = itemView.findViewById(R.id.tab1_text);
            tab2_txt = itemView.findViewById(R.id.tab2_text);
            tab3_txt = itemView.findViewById(R.id.tab3_text);
            menu = itemView.findViewById(R.id.menu);
            arrow = itemView.findViewById(R.id.arrow);
            icon = itemView.findViewById(R.id.icon);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            ivFlag1 = itemView.findViewById(R.id.ivFlag1);
            ivFlag2 = itemView.findViewById(R.id.ivFlag2);
            ivFlag3 = itemView.findViewById(R.id.ivFlag3);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
