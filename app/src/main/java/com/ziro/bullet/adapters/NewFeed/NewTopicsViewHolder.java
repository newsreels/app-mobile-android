package com.ziro.bullet.adapters.NewFeed;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.HashTagDetailsActivity;
import com.ziro.bullet.activities.changereels.ReelTopicsAdapter;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.flowlayout.FlowLayout;
import com.ziro.bullet.flowlayout.TagAdapter;
import com.ziro.bullet.flowlayout.TagFlowLayout;
import com.ziro.bullet.fragments.FollowingDetailActivity;

import java.util.ArrayList;

public class NewTopicsViewHolder extends RecyclerView.ViewHolder {

    private Activity context;
    public GridLayoutManager layoutManager;
    private TextView title;
    private LinearLayout rootCard;
    private TagFlowLayout listView;
    private TextView see_all;
    private TagAdapter<Topics> mTopicsAdapter;


    public NewTopicsViewHolder(View view, Activity context) {
        super(view);
        this.context = context;
        rootCard = view.findViewById(R.id.rootCard);
        title = view.findViewById(R.id.title);
        listView = view.findViewById(R.id.items);
        see_all = view.findViewById(R.id.see_all);
        see_all.setVisibility(View.VISIBLE);
    }

    private void openDetail(String type) {
        FollowingDetailActivity.launchActivityWithResult(context, type, "", 222);
    }

    public void bind(String mTitle, ArrayList<Topics> data) {

        see_all.setOnClickListener(view -> openDetail("topics"));
        title.setTextColor(context.getResources().getColor(R.color.bullet_text));
        rootCard.setBackgroundColor(context.getResources().getColor(R.color.card_bg));
        if (!TextUtils.isEmpty(mTitle)) {
            title.setVisibility(View.VISIBLE);
            title.setText(mTitle);
        } else {
            title.setVisibility(View.GONE);
        }
        if (data != null && data.size() > 0 && context != null) {
            mTopicsAdapter = new TagAdapter<Topics>(data) {
                @Override
                public View getView(FlowLayout parent, int position, Topics topic) {
                    View view = context.getLayoutInflater().inflate(R.layout.item_topics_feed, parent, false);

                    TextView name = view.findViewById(R.id.title);
                    LottieAnimationView selected = view.findViewById(R.id.selected);
                    CardView cross = view.findViewById(R.id.container);
                    name.setText("#" + topic.getName());

                    if (topic.isFavorite()) {
                        selected.setProgress(1f);
                    } else {
                        selected.setProgress(0f);
                    }

                    cross.setOnClickListener(view1 -> {
                        Intent intent = new Intent(context, HashTagDetailsActivity.class);
                        intent.putExtra("type", TYPE.TOPIC);
                        intent.putExtra("id", topic.getId());
                        intent.putExtra("mContext", topic.getContext());
                        intent.putExtra("name", topic.getName());
                        intent.putExtra("favorite", topic.isFavorite());
                        context.startActivity(intent);
                    });

                    return view;
                }
            };
            listView.setAdapter(mTopicsAdapter);
        }
    }
}
