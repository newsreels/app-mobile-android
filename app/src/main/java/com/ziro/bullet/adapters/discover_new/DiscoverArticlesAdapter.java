package com.ziro.bullet.adapters.discover_new;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.List;


public class DiscoverArticlesAdapter extends RecyclerView.Adapter<DiscoverArticlesAdapter.DiscoverArticlesViewHolder> {

    private final List<Article> discoverArticles;
    private final CommentClick commentClick;
    private final boolean limitItems;

    public DiscoverArticlesAdapter(List<Article> discoverArticles, boolean limitItems, CommentClick commentClick) {
        this.discoverArticles = discoverArticles;
        this.commentClick = commentClick;
        this.limitItems = limitItems;
    }

    @NonNull
    @Override
    public DiscoverArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiscoverArticlesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_article_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverArticlesViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (discoverArticles.size() > 5 && limitItems) {
            return 5;
        } else {
            return discoverArticles.size();
        }
    }

    class DiscoverArticlesViewHolder extends RecyclerView.ViewHolder {
        TextView tvBulletRank;
        TextView tvBulletHeadline;
        ImageView ivBulletIcon;


        public DiscoverArticlesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBulletRank = itemView.findViewById(R.id.tv_bullet_rank);
            tvBulletHeadline = itemView.findViewById(R.id.tv_bullet_headline);
            ivBulletIcon = itemView.findViewById(R.id.bullet_image);
        }

        void onBind(int position) {

            tvBulletRank.setText(String.valueOf(position + 1));

            createShader(tvBulletRank, new int[]{Color.parseColor("#F73458"), Color.parseColor("#F73458"), Color.parseColor("#DDD5D7")});

            List<Bullet> bulletArrayList = discoverArticles.get(position).getBullets();
            if (bulletArrayList != null && !bulletArrayList.isEmpty()) {
                tvBulletHeadline.setText(discoverArticles.get(position).getBullets().get(0).getData());
                try {
                    float textSize = Utils.getBulletDimens(new PrefConfig(itemView.getContext()), (Activity) itemView.getContext());
                    tvBulletHeadline.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Glide.with(itemView.getContext())
                    .load(discoverArticles.get(position).getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Constants.targetWidth, Constants.targetHeight)
                    .into(ivBulletIcon);

            itemView.setOnClickListener(view -> {
                if (position + 10 < discoverArticles.size()) {
                    List<Article> copyArray = new ArrayList<>(discoverArticles.subList(position, position + 10));
                    commentClick.onNewDetailClick(position, discoverArticles.get(position), copyArray);
                } else {
                    List<Article> copyArray = new ArrayList<>(discoverArticles.subList(position, discoverArticles.size()));
                    commentClick.onNewDetailClick(position, discoverArticles.get(position), copyArray);
                }
            });
        }

        private void createShader(TextView view, int[] colorCode) {
            view.setTextColor(colorCode[0]);
            view.getPaint().clearShadowLayer();
            TextPaint paint = view.getPaint();

            Shader textShader = new LinearGradient(
                    0f,
                    0f,
                    view.getWidth(),
                    view.getTextSize(),
                    colorCode, null,
                    Shader.TileMode.CLAMP
            );
            view.getPaint().setShader(textShader);
            view.getPaint().setShader(textShader);
        }
    }
}


