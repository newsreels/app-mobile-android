package com.ziro.bullet.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.utills.Utils;

import java.util.List;

public class CardBulletsAdapter extends RecyclerView.Adapter<CardBulletsAdapter.ViewHolder> {

    private Activity mContext;
    private List<Bullet> arr;
    private PrefConfig mPrefConfig;
    private int mPosition = 0;
    private int mlayout = 0;
    private int mHeight = 0;
    private CardBulletsAdapter.ViewHolder holder;
    private int lastPosition = -1;
    private ListAdapterListener listener;
    private GestureDetector gestureDetector;
    private boolean mIsScrolling = false;
    private boolean mIsHold = false;
    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;
    private Article currentArticle;
    private boolean isPostArticle;
    private String type;
    private CommentClick mCommentClick;
    private int articlePosition = -1;
    private boolean isWhiteModeOnly;


    public CardBulletsAdapter(boolean isWhiteModeOnly, CommentClick mCommentClick, String type, boolean isPostArticle, Activity mContext, List<Bullet> arr, ListAdapterListener listener, Article article, int articlePosition) {
        this.mContext = mContext;
        this.isWhiteModeOnly = isWhiteModeOnly;
        this.type = type;
        this.listener = listener;
        this.mCommentClick = mCommentClick;
        this.isPostArticle = isPostArticle;
        this.arr = arr;
        mPrefConfig = new PrefConfig(mContext);
        currentArticle = article;
        this.articlePosition = articlePosition;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardBulletsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_bullet_item, parent, false);
        return new CardBulletsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardBulletsAdapter.ViewHolder holder, int position) {
        this.holder = holder;
        String item = arr.get(position).getData();

        if (isWhiteModeOnly) {
            holder.expand_text.setTextColor(mContext.getResources().getColor(R.color.black));
        } else {
            holder.expand_text.setTextColor(mContext.getResources().getColor(R.color.bullet_text));
        }

        holder.expand_text.setTextDirection(Utils.getLanguageDirectionForView(currentArticle.getLanguageCode()));
        if (!TextUtils.isEmpty(item)) {
            Typeface typeface = null;
            if (position == 0) {
                Typeface typeface1 = ResourcesCompat.getFont(mContext, R.font.manuale_bold);
                holder.expand_text.setTypeface(typeface1);
//                holder.expand_text.setTypeface(null, Typeface.BOLD);
//                typeface = ResourcesCompat.getFont(mContext, R.font.muli_semi_bold);
            } else {
                Typeface typeface2 = ResourcesCompat.getFont(mContext, R.font.manuale_medium);
                holder.expand_text.setTypeface(typeface2);
//                holder.expand_text.setTypeface(null, Typeface.NORMAL);
//                typeface = ResourcesCompat.getFont(mContext, R.font.muli_regular);
            }
//            holder.expand_text.setTypeface(typeface);
            holder.expand_text.setText(item);
        } else {
            holder.expand_text.setText("");
        }

        float val = -1;
        if (position == 0) {
            val = Utils.getHeadlineDimens(mPrefConfig, mContext);
        } else {
            val = Utils.getBulletDimens(mPrefConfig, mContext);
        }
        if (val != -1) {
            holder.expand_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
        }

//        holder.leftArc.setOnClickListener(v -> {
//            if (listener != null)
//                listener.prevArticle(position);
//        });
//        holder.rightArc.setOnClickListener(v -> {
//            if (listener != null)
//                listener.nextArticle(position);
//        });

        if (!isPostArticle) {
            holder.expand_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCommentClick != null && currentArticle != null)
                        mCommentClick.onDetailClick(articlePosition, currentArticle);
//                    Utils.openDetailView(mContext, currentArticle, type);
                }
            });
        }

        if (mHeight != 0) {
//            holder.articleClick.getLayoutParams().height = mHeight;
            holder.buttons.getLayoutParams().height = mHeight;
            holder.buttons.requestLayout();
        }

        gestureDetector = new GestureDetector(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                return true;
            }


            @Override
            public void onLongPress(MotionEvent e) {
                mIsHold = true;
                if (listener != null)
                    listener.verticalScrollList(false);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        holder.articleClick.setOnTouchListener((v, event) -> {
            return bulletScroller(event, position);
        });
    }


    private boolean bulletScroller(MotionEvent event, int mPosition) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.e("kjandn", "-------------------");
            Log.e("kjandn", "ACTION_UP");
            float endX = event.getX();
            float endY = event.getY();
            if (isAClick(startX, endX, startY, endY)) {
                if (listener != null)
                    listener.clickArticle(mPosition);
            } else {
                //Hold
                if (mIsHold) {
                    if (listener != null)
                        listener.verticalScrollList(true);
                    mIsHold = false;
                    Log.e("kjandn", "hold");
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e("kjandn", "ACTION_DOWN");
            startX = event.getX();
            startY = event.getY();
        }
        return false;
    }


    private boolean isAClick(float startX, float endX, float startY, float endY) {
        if (!mIsHold) {
            float differenceX = Math.abs(startX - endX);
            float differenceY = Math.abs(startY - endY);
            return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public int getCurrentPosition() {
        return mPosition;
    }

    public void setCurrentPosition(int pos) {
        if (pos >= arr.size()) {
            mPosition = arr.size() - 1;
        } else
            mPosition = pos;
    }

    public interface CardBulletCallback {
        void onItemClicked();

        void onPause();

        void onResume();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout buttons, main;
        private RelativeLayout articleClick;
        private TextView expand_text;
        private ImageView leftArc, rightArc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.main);
            expand_text = itemView.findViewById(R.id.expand_text);
            buttons = itemView.findViewById(R.id.buttons);
            leftArc = itemView.findViewById(R.id.leftArc);
            rightArc = itemView.findViewById(R.id.rightArc);
            articleClick = itemView.findViewById(R.id.articleClick);
        }
    }
}
