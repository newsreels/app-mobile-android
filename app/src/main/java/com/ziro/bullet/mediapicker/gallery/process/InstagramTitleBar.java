package com.ziro.bullet.mediapicker.gallery.process;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.ziro.bullet.R;
import com.ziro.bullet.mediapicker.config.PictureSelectionConfig;
import com.ziro.bullet.mediapicker.utils.ScreenUtils;

import static com.ziro.bullet.mediapicker.gallery.InstagramSelectionConfig.THEME_STYLE_DARK;
import static com.ziro.bullet.mediapicker.gallery.InstagramSelectionConfig.THEME_STYLE_DEFAULT;

/**
 * ================================================
 * Created by JessYan on 2020/5/29 11:51
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class InstagramTitleBar extends FrameLayout {
    private TextView mLeftView;
    private TextView mRightView;
    private OnTitleBarItemOnClickListener mClickListener;

    public InstagramTitleBar(@NonNull Context context, PictureSelectionConfig config, InstagramMediaProcessActivity.MediaType mediaType) {
        super(context);
        mLeftView = new TextView(context);
        mLeftView.setPadding(ScreenUtils.dip2px(context, 15), 0, ScreenUtils.dip2px(context, 15), 0);
        mLeftView.setText(context.getString(R.string.cancel));
        mLeftView.setTextSize(14);
        mLeftView.setTypeface(ResourcesCompat.getFont(context,R.font.muli_bold));
        mLeftView.setTextColor(ContextCompat.getColor(context,R.color.title_bar_title));
        mLeftView.setGravity(Gravity.CENTER);
        mLeftView.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onLeftViewClick();
            }
        });
        addView(mLeftView);

        mRightView = new TextView(context);
        mRightView.setPadding(ScreenUtils.dip2px(context, 10), 0, ScreenUtils.dip2px(context, 10), 0);
//        int textColor;
//        if (config.style.pictureRightDefaultTextColor != 0) {
//            textColor = config.style.pictureRightDefaultTextColor;
//        } else {
//            if (config.instagramSelectionConfig.getCurrentTheme() == THEME_STYLE_DARK) {
//                textColor = ContextCompat.getColor(context, R.color.picture_color_1766FF);
//            }  else {
//                textColor = ContextCompat.getColor(context, R.color.picture_color_1766FF);
//            }
//        }
//        mRightView.setTextColor(textColor);
        mRightView.setTextSize(14);
        mRightView.setText(context.getString(R.string.add));
        mRightView.setTypeface(ResourcesCompat.getFont(context,R.font.muli_bold));
        mRightView.setTextColor(ContextCompat.getColor(context,R.color.title_bar_title));
        mRightView.setGravity(Gravity.CENTER);
        mRightView.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onRightViewClick();
            }
        });
        addView(mRightView);
    }

    public void setRightViewText(String text) {
        mRightView.setText(text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = ScreenUtils.dip2px(getContext(), 48);
        mLeftView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));


        mRightView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int viewTop = (getMeasuredHeight() - mLeftView.getMeasuredHeight()) / 2;
        int viewLeft = 0;
        mLeftView.layout(viewLeft, viewTop, viewLeft + mLeftView.getMeasuredWidth(), viewTop + mLeftView.getMeasuredHeight());

        viewTop = (getMeasuredHeight() - mRightView.getMeasuredHeight()) / 2;
        viewLeft = getMeasuredWidth() - mRightView.getMeasuredWidth();
        mRightView.layout(viewLeft, viewTop, viewLeft + mRightView.getMeasuredWidth(), viewTop + mRightView.getMeasuredHeight());

    }

    public void setClickListener(OnTitleBarItemOnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface OnTitleBarItemOnClickListener {
        void onLeftViewClick();
        void onCenterViewClick(ImageView view);
        void onRightViewClick();
    }
}
