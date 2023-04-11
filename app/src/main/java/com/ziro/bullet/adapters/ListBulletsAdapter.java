package com.ziro.bullet.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.utills.Utils;

import java.util.List;

public class ListBulletsAdapter extends RecyclerView.Adapter<ListBulletsAdapter.ViewHolder> {
    private Activity mContext;
    private List<Bullet> arr;
    private Article currentArticle;
    private BulletCallback mCallback;
    private PrefConfig mPrefConfig;
    private int mPosition = 0;
    private int mlayout = 0;
    private int mHeight = 0;
    //    private boolean onHold = false;
    private ListBulletsAdapter.ViewHolder holder;
    private int lastPosition = -1;
    private GestureDetector gestureDetector;
    private ListAdapterListener adapterListener;
    private boolean mIsScrolling = false;
    private boolean mIsHold = false;
    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;
    private float opacity = 0.5f;
    private boolean isYouTube;
    private String type = "";
    private boolean isWhiteModeOnly;


    public ListBulletsAdapter(boolean isWhiteModeOnly, String type, ListAdapterListener adapterListener, Activity mContext, List<Bullet> arr, boolean isYouTube, Article currentArticle) {
        this.adapterListener = adapterListener;
        this.mContext = mContext;
        this.arr = arr;
        this.isWhiteModeOnly = isWhiteModeOnly;
        this.type = type;
        this.isYouTube = isYouTube;
        this.currentArticle = currentArticle;
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setCallback(BulletCallback callback) {
        this.mCallback = callback;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListBulletsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_bullet_item_collapse, parent, false);
        return new ListBulletsAdapter.ViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ListBulletsAdapter.ViewHolder holder, int position) {
        this.holder = holder;
        Bullet bullet = arr.get(position);

        String item = bullet.getData();
        holder.collapse_text.setTextDirection(Utils.getLanguageDirectionForView(currentArticle.getLanguageCode()));

        if (isWhiteModeOnly) {
            holder.collapse_text.setTextColor(mContext.getResources().getColor(R.color.black));
        } else {
            holder.collapse_text.setTextColor(mContext.getResources().getColor(R.color.bullet_text));
        }

        if (position == 0) {
//            holder.collapse_text.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            final Typeface typeface1 = ResourcesCompat.getFont(mContext, R.font.librefranklin_semibold);
//            Typeface typeface = ResourcesCompat.getFont(mContext, R.font.manuale_bold);
            holder.collapse_text.setTypeface(typeface1);
        } else {
//            holder.collapse_text.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            final Typeface typeface2 = ResourcesCompat.getFont(mContext, R.font.librefranklin_regular);
            holder.collapse_text.setTypeface(typeface2);
        }

//        if (mPrefConfig.isReaderMode()) {
//            if (!Utils.isRTL()) {
//                holder.collapse_text.setPadding(0, 0, mContext.getResources().getDimensionPixelOffset(R.dimen._10sdp), 0);
//            } else {
//                holder.collapse_text.setPadding(mContext.getResources().getDimensionPixelOffset(R.dimen._10sdp), 0, 0, 0);
//            }
//        } else {
//            if (!Utils.isRTL()) {
//                holder.collapse_text.setPadding(0, 0, mContext.getResources().getDimensionPixelOffset(R.dimen._110sdp), 0);
//            } else {
//                holder.collapse_text.setPadding(mContext.getResources().getDimensionPixelOffset(R.dimen._110sdp), 0, 0, 0);
//            }
//        }

        float val = -1;
        if (position == 0) {
            val = Utils.getHeadlineDimens(mPrefConfig, mContext);
        } else {
            val = Utils.getBulletDimens(mPrefConfig, mContext);
        }
        if (val != -1) {
            holder.collapse_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
        }

        if (!TextUtils.isEmpty(item)) {
            holder.collapse_text.setText(item);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterListener.clickArticle(position);
            }
        });
    }

    private boolean bulletScroller(MotionEvent event, int position) {
        if (gestureDetector.onTouchEvent(event)) {
            return false;
        }

        int left_swipe = (int) (ListBulletsAdapter.this.getScreenWidth(mContext) * 0.15);  // left 15%
        int _80_screen = (int) (ListBulletsAdapter.this.getScreenWidth(mContext) * 0.75);
        int right_swipe = left_swipe + _80_screen;  // right 15%

        int left = (int) (ListBulletsAdapter.this.getScreenWidth(mContext) * 0.20);
        int center = (int) (ListBulletsAdapter.this.getScreenWidth(mContext) * 0.60);
        int right = left + center;


        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();

                if (isAClick(startX, endX, startY, endY)) {
                    if (endX < left) {
                        //Left Click
                        if (mCallback != null) {
                            Utils.doHaptic(mPrefConfig);
                            mCallback.onLeft(null);
                        }
                    } else if (endX > right) {
                        //Right Click
                        if (mCallback != null) {
                            Utils.doHaptic(mPrefConfig);
                            mCallback.onRight(null);
                        }
                    } else {
                        //Center Click
                        if (mCallback != null) {
                            mCallback.onItemClicked();
                        }
                    }
                } else {
                    //Hold
                    if (mIsHold) {
                        if (mCallback != null) {
                            mCallback.onResume();
                        }
                        mIsHold = false;
                    }
                    //Scroll
                    else if (mIsScrolling) {
                        mIsScrolling = false;

                        Utils.Direction direction = Utils.getDirection(startX, startY, endX, endY);
                        if (direction != null) {
                            switch (direction.ordinal()) {
                                case 0: // up
                                    //GOTO NEXT BULLET
//                                    if (pos < content.getBullets().size()) {
//                                        pos++;
//                                        holder.desc_list.smoothScrollToPosition(pos);
//                                        bulletsAdapter.setCurrentPosition(pos);
//                                    }
                                    break;
                                case 1: // down
                                    //GOTO PREV BULLET
//                                    if (pos > 0) {
//                                        pos--;
//                                        holder.desc_list.smoothScrollToPosition(pos);
//                                        bulletsAdapter.setCurrentPosition(pos);
//                                    } else {
//                                        if (pos == 0) {
//                                            prevArticle();
//                                        }
//                                    }
                                    break;
                                case 2: // left
//                                    nextArticle();
                                    break;
                                case 3: // right
//                                    prevArticle();
                                    break;
                            }
                        }

                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mIsScrolling = true;
                if (mCallback != null) {
                    mCallback.onMove();
                }
                break;
            case MotionEvent.ACTION_DOWN:

                startX = event.getX();
                startY = event.getY();

                if (startX < left_swipe || startX > right_swipe) {
                    if (adapterListener != null) {
                        adapterListener.verticalScrollList(false);
                    }
                } else {
                    if (adapterListener != null) {
                        adapterListener.verticalScrollList(true);
                    }
                }
                break;

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

    private int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? android.R.anim.slide_in_left : android.R.anim.slide_out_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void setHighLightedText(TextView tv, String textToHighlight) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight, 0);
        Spannable wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1)
                break;
            else {
                // set color here
                wordToSpan.setSpan(new BackgroundColorSpan(0x80000000), ofe, ofe + textToHighlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
            }
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

    public void forReel() {
        if (holder != null)
            holder.collapse_text.setTextColor(mContext.getResources().getColor(R.color.black));
    }

    public interface BulletCallback {
        void onItemClicked();

        void onPause();

        void onResume();

        void onMove();

        void onBottomSheetOpen();

        void onYoutubePlay();

        void moveTolastPosition();

        void bulletPosition(int position);

        void onLeft(MotionEvent event);

        void onRight(MotionEvent event);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView collapse_text;
        private RelativeLayout main;
//        private RelativeLayout touch;
//        private RelativeLayout imageSection;
//        private ImageView imageBack;
//        private ImageView image;
//        private TextView play;
//        private ProgressBar progressAudio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            touch = itemView.findViewById(R.id.touch);
            collapse_text = itemView.findViewById(R.id.collapse_text);
            main = itemView.findViewById(R.id.main);
//            imageSection = itemView.findViewById(R.id.imageSection);
//            imageBack = itemView.findViewById(R.id.imageBack);
//            image = itemView.findViewById(R.id.image);
////            progressAudio = itemView.findViewById(R.id.progressAudio);
//            play = itemView.findViewById(R.id.play_duration);
        }
    }
}
