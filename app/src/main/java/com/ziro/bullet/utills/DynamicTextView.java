package com.ziro.bullet.utills;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.FirstActivity;
import com.ziro.bullet.data.caption.CaptionDetails;
import com.ziro.bullet.data.caption.Margin;
import com.ziro.bullet.data.caption.Padding;
import com.ziro.bullet.data.caption.Position;
import com.ziro.bullet.data.caption.WordsItem;
import com.ziro.bullet.interfaces.CaptionClickListener;

import java.util.ArrayList;
import java.util.List;

public class DynamicTextView {

    private Handler handler = new Handler();
    private int _1000 = 1000;
    private int EndTime = 0;
    private int viewId = 0;
    private View view = null;
    private int captionId = 0;
    private boolean forced = false;

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getCaptionId() {
        return captionId;
    }

    public void setCaptionId(int captionId) {
        this.captionId = captionId;
    }


    public int getEndTime() {
        return EndTime;
    }

    public void setEndTime(int endTime) {
        EndTime = endTime;
    }

    /**
     * TEXTVIEW ANIMATION
     */
    private Techniques getAnimation(String anim) {
        Techniques technique;
        switch (anim) {
            case "DropOut":
                technique = Techniques.DropOut;
                break;
            case "Landing":
                technique = Techniques.Landing;
                break;
            case "TakingOff":
                technique = Techniques.TakingOff;
                break;
            case "Flash":
                technique = Techniques.Flash;
                break;
            case "Pulse":
                technique = Techniques.Pulse;
                break;
            case "RubberBand":
                technique = Techniques.RubberBand;
                break;
            case "Shake":
                technique = Techniques.Shake;
                break;
            case "Swing":
                technique = Techniques.Swing;
                break;
            case "Wobble":
                technique = Techniques.Wobble;
                break;
            case "Bounce":
                technique = Techniques.Bounce;
                break;
            case "Tada":
                technique = Techniques.Tada;
                break;
            case "StandUp":
                technique = Techniques.StandUp;
                break;
            case "Wave":
                technique = Techniques.Wave;
                break;
            case "Hinge":
                technique = Techniques.Hinge;
                break;
            case "RollIn":
                technique = Techniques.RollIn;
                break;
            case "RollOut":
                technique = Techniques.RollOut;
                break;
            case "BounceIn":
                technique = Techniques.BounceIn;
                break;
            case "BounceInDown":
                technique = Techniques.BounceInDown;
                break;
            case "BounceInLeft":
                technique = Techniques.BounceInLeft;
                break;
            case "BounceInRight":
                technique = Techniques.BounceInRight;
                break;
            case "BounceInUp":
                technique = Techniques.BounceInUp;
                break;
            case "FadeInUp":
                technique = Techniques.FadeInUp;
                break;
            case "FadeInDown":
                technique = Techniques.FadeInDown;
                break;
            case "FadeInLeft":
                technique = Techniques.FadeInLeft;
                break;
            case "FadeInRight":
                technique = Techniques.FadeInRight;
                break;
            case "FadeOut":
                technique = Techniques.FadeOut;
                break;
            case "FadeOutDown":
                technique = Techniques.FadeOutDown;
                break;
            case "FadeOutLeft":
                technique = Techniques.FadeOutLeft;
                break;
            case "FadeOutRight":
                technique = Techniques.FadeOutRight;
                break;
            case "FadeOutUp":
                technique = Techniques.FadeOutUp;
                break;
            case "FlipInX":
                technique = Techniques.FlipInX;
                break;
            case "FlipOutX":
                technique = Techniques.FlipOutX;
                break;
            case "FlipInY":
                technique = Techniques.FlipInY;
                break;
            case "FlipOutY":
                technique = Techniques.FlipOutY;
                break;
            case "RotateIn":
                technique = Techniques.RotateIn;
                break;
            case "RotateInDownLeft":
                technique = Techniques.RotateInDownLeft;
                break;
            case "RotateInDownRight":
                technique = Techniques.RotateInDownRight;
                break;
            case "RotateInUpLeft":
                technique = Techniques.RotateInUpLeft;
                break;
            case "RotateInUpRight":
                technique = Techniques.RotateInUpRight;
                break;
            case "RotateOut":
                technique = Techniques.RotateOut;
                break;
            case "RotateOutDownLeft":
                technique = Techniques.RotateOutDownLeft;
                break;
            case "RotateOutDownRight":
                technique = Techniques.RotateOutDownRight;
                break;
            case "RotateOutUpLeft":
                technique = Techniques.RotateOutUpLeft;
                break;
            case "RotateOutUpRight":
                technique = Techniques.RotateOutUpRight;
                break;
            case "SlideInLeft":
                technique = Techniques.SlideInLeft;
                break;
            case "SlideInRight":
                technique = Techniques.SlideInRight;
                break;
            case "SlideInUp":
                technique = Techniques.SlideInUp;
                break;
            case "SlideInDown":
                technique = Techniques.SlideInDown;
                break;

            case "SlideOutLeft":
                technique = Techniques.SlideOutLeft;
                break;
            case "SlideOutRight":
                technique = Techniques.SlideOutRight;
                break;
            case "SlideOutUp":
                technique = Techniques.SlideOutUp;
                break;
            case "SlideOutDown":
                technique = Techniques.SlideOutDown;
                break;
            case "zoom_in":
            case "curveEase_In":
                technique = Techniques.ZoomIn;
                break;
            case "ZoomInDown":
                technique = Techniques.ZoomInDown;
                break;
            case "ZoomInLeft":
                technique = Techniques.ZoomInLeft;
                break;
            case "ZoomInRight":
                technique = Techniques.ZoomInRight;
                break;
            case "ZoomInUp":
                technique = Techniques.ZoomInUp;
                break;
            case "ZoomOut":
            case "curveEase_Out":
                technique = Techniques.ZoomOut;
                break;
            case "ZoomOutDown":
                technique = Techniques.ZoomOutDown;
                break;
            case "ZoomOutLeft":
                technique = Techniques.ZoomOutLeft;
                break;
            case "ZoomOutRight":
                technique = Techniques.ZoomOutRight;
                break;
            case "ZoomOutUp":
                technique = Techniques.ZoomOutUp;
                break;
            case "fade_in":
            case "cross_Dissolve":
                technique = Techniques.FadeIn;
                break;
            default: //FadeIn
                technique = Techniques.FadeIn;
        }
        return technique;
    }

    /**
     * TEXT ALIGNMENT
     */
    private int getTextAlignment(String alignment) {
        int align;
        switch (alignment) {
            case "start":
            case "left":
                align = View.TEXT_ALIGNMENT_TEXT_START;
                break;
            case "end":
            case "right":
                align = View.TEXT_ALIGNMENT_TEXT_END;
                break;
            default: // CENTER
                align = View.TEXT_ALIGNMENT_CENTER;
        }
        return align;
    }

    /**
     * TEXT FONT FAMILY/STYLE
     */
    private void setTextFontStyle(Context context, SpannableStringBuilder sentence, int finalStart, int finalEnd, String fontFamily, String fontStyle) {
        StyleSpan tas;
        if (!TextUtils.isEmpty(fontFamily)) {
            switch (fontFamily) {
                case "mulli":
                    if (!TextUtils.isEmpty(fontStyle)) {
                        switch (fontStyle) {
                            case "bold":
                                tas = new StyleSpan(R.font.muli_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "extra_bold":
                                tas = new StyleSpan(R.font.muli_extra_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "semi_bold":
                                tas = new StyleSpan(R.font.muli_semi_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "black":
                                tas = new StyleSpan(R.font.muli_black);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            default: //regular
                                tas = new StyleSpan(R.font.muli_regular);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else {
                        //regular
                        tas = new StyleSpan(R.font.muli_regular);
                        sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case "specify":
                    if (!TextUtils.isEmpty(fontStyle)) {
                        switch (fontStyle) {
                            case "black":
                                tas = new StyleSpan(R.font.specify_black);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "black_italic":
                                tas = new StyleSpan(R.font.specify_black_italic);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "bold":
                                tas = new StyleSpan(R.font.specify_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "bold_italic":
                                tas = new StyleSpan(R.font.specify_bold_italic);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "normal_black":
                                tas = new StyleSpan(R.font.specify_normal_black);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "normal_black_italic":
                                tas = new StyleSpan(R.font.specify_normal_black_italic);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "normal_bold":
                                tas = new StyleSpan(R.font.specify_normal_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "normal_bold_italic":
                                tas = new StyleSpan(R.font.specify_normal_bold_italic);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;

                            default: //regular
                                tas = new StyleSpan(R.font.muli_regular);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else {
                        //regular
                        tas = new StyleSpan(R.font.muli_regular);
                        sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case "Trueno":
                case "trueno":
                    if (!TextUtils.isEmpty(fontStyle)) {
                        switch (fontStyle) {
                            case "black":
                                tas = new StyleSpan(R.font.black);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "black_italic":
                                tas = new StyleSpan(R.font.black_italic);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "bold":
                                tas = new StyleSpan(R.font.bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "bold_italic":
                                tas = new StyleSpan(R.font.bold_italic);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "extra_bold":
                                tas = new StyleSpan(R.font.extra_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "extra_bold_italic":
                                tas = new StyleSpan(R.font.extra_bold_italic);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "semi_bold":
                                tas = new StyleSpan(R.font.semi_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "semi_bold_italic":
                                tas = new StyleSpan(R.font.semi_bold_italic);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "ultra_black":
                                tas = new StyleSpan(R.font.ultra_black);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "ultra_black_italic":
                                tas = new StyleSpan(R.font.ultra_black_italic);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            default: //regular
                                tas = new StyleSpan(R.font.muli_regular);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else {
                        //regular
                        tas = new StyleSpan(R.font.muli_regular);
                        sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case "roboto":
                    if (!TextUtils.isEmpty(fontStyle)) {
                        switch (fontStyle) {
                            case "thin":
                                tas = new StyleSpan(R.font.roboto_thin);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "extra_bold":
                                tas = new StyleSpan(R.font.roboto_black);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "bold":
                                tas = new StyleSpan(R.font.roboto_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            default: //regular
                                tas = new StyleSpan(R.font.roboto_regular);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else {
                        //regular
                        tas = new StyleSpan(R.font.roboto_regular);
                        sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case "Martel":
                case "martel":
                    if (!TextUtils.isEmpty(fontStyle)) {
                        switch (fontStyle) {
                            case "bold":
                                tas = new StyleSpan(R.font.martel_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            default: //regular
                                tas = new StyleSpan(R.font.martel_regular);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else {
                        //regular
                        tas = new StyleSpan(R.font.roboto_regular);
                        sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case "Sarala":
                case "sarala":
                    if (!TextUtils.isEmpty(fontStyle)) {
                        switch (fontStyle) {
                            case "bold":
                                tas = new StyleSpan(R.font.sarala_bold);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            default: //regular
                                tas = new StyleSpan(R.font.sarala_regular);
                                sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else {
                        //regular
                        tas = new StyleSpan(R.font.roboto_regular);
                        sentence.setSpan(tas, finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                default:
                    if (!TextUtils.isEmpty(fontStyle)) {
                        switch (fontStyle) {
                            case "bold_italic":
                                sentence.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "italic":
                                sentence.setSpan(new StyleSpan(Typeface.ITALIC), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "bold":
                                sentence.setSpan(new StyleSpan(Typeface.BOLD), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            default: //regular
                                sentence.setSpan(new StyleSpan(Typeface.NORMAL), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else {
                        //regular
                        sentence.setSpan(new StyleSpan(Typeface.NORMAL), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

            }
        } else {
            if (!TextUtils.isEmpty(fontStyle)) {
                switch (fontStyle) {
                    case "bold_italic":
                        sentence.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case "italic":
                        sentence.setSpan(new StyleSpan(Typeface.ITALIC), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case "bold":
                        sentence.setSpan(new StyleSpan(Typeface.BOLD), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    default: //regular
                        sentence.setSpan(new StyleSpan(Typeface.NORMAL), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                //regular
                sentence.setSpan(new StyleSpan(Typeface.NORMAL), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private RelativeLayout.LayoutParams addRules(RelativeLayout center, RelativeLayout top, RelativeLayout bottom, RelativeLayout start, RelativeLayout end, Position position) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return params;
    }


    /**
     * DYNAMIC TEXTVIEW
     */
    public View createTextView(RelativeLayout dynamicContainer, Context context, CaptionDetails captionDetails, CaptionClickListener mListener) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View captionView = vi.inflate(R.layout.caption, dynamicContainer, false);
        ConstraintLayout root = captionView.findViewById(R.id.root);
        RotateLayout captionBox = captionView.findViewById(R.id.captionBox);
        CardView card = captionView.findViewById(R.id.card);
        ImageView image = captionView.findViewById(R.id.image);
        TextView textview_caption = captionView.findViewById(R.id.textview_caption);

        SpannableStringBuilder sentence = new SpannableStringBuilder();
        //Box BACKGROUND
        if (!TextUtils.isEmpty(captionDetails.getImageBackground())) {
            card.setCardBackgroundColor(Color.TRANSPARENT);
            Glide.with(context).load(captionDetails.getImageBackground()).into(image);
        } else {
            if (!TextUtils.isEmpty(captionDetails.getTextBackground())) {
                card.setCardBackgroundColor(Color.parseColor(captionDetails.getTextBackground()));
            } else {
                card.setCardBackgroundColor(Color.TRANSPARENT);
            }
        }
        //Box Corner Radius
        if (captionDetails.getCorner_radius() >= 0) {
            card.setRadius(Utils.dpToPx(captionDetails.getCorner_radius(), context));
        }
        //TEXTVIEW Marquee
        if (captionDetails.isMarquee()) {
            textview_caption.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textview_caption.setSingleLine(true);
            textview_caption.setMarqueeRepeatLimit(-1);
            textview_caption.setSelected(true);
        }
        //TEXTVIEW ANIMATION
        if (!TextUtils.isEmpty(captionDetails.getAnimation())) {
            YoYo.with(getAnimation(captionDetails.getAnimation()))
                    .duration(captionDetails.getAnimation_duration())
                    .repeat(0)
                    .playOn(captionBox);
            YoYo.with(getAnimation(captionDetails.getAnimation()))
                    .duration(captionDetails.getAnimation_duration())
                    .repeat(0)
                    .playOn(card);
        }
        //TEXTVIEW ROTATION
        captionBox.setAngle(captionDetails.getRotation());

        if (captionDetails.getWords() != null && captionDetails.getWords().size() > 0) {
            int start = 0;
            int end = 0;
            for (WordsItem word : captionDetails.getWords()) {
                start = end;
                end = end + word.getWord().length();
                int finalStart = start;
                int finalEnd = end;
                if (word.getDelay() > 0) {
                    handler.postDelayed(() -> setWordAttributes(sentence, word, finalStart, finalEnd, textview_caption, context, mListener, captionDetails), word.getDelay());
                } else {
                    setWordAttributes(sentence, word, finalStart, finalEnd, textview_caption, context, mListener, captionDetails);
                }
            }
        }
        //TEXTVIEW Width Height
        if (captionDetails.getRotation() == 0) {
            if (!captionDetails.isWrapping()) {
                ViewGroup.LayoutParams layoutParams = captionBox.getLayoutParams();
//            layoutParams.height = 0;
                layoutParams.width = 0;
                captionBox.setLayoutParams(layoutParams);
            }
        }
        //TEXTVIEW PADDING
        Padding padding = captionDetails.getPadding();
        if (padding != null) {
            textview_caption.setPadding(Utils.dpToPx(padding.getLeft(), context), Utils.dpToPx(padding.getTop(), context),
                    Utils.dpToPx(padding.getRight(), context), Utils.dpToPx(padding.getBottom(), context));
        }
        //TEXTVIEW POSITION
        Position position = captionDetails.getPosition();
        if (position != null) {

            double x_ = position.getX();
            double y_ = position.getY();

            // 0.0 - 1.0
            //  0% - 100%
            double leftPercent = x_ / 100;
            double bottomPercent = y_ / 100;


            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(root);

            constraintSet.setGuidelinePercent(R.id.leftView, (float) leftPercent);
            constraintSet.setGuidelinePercent(R.id.rightView, 1.0f);

            if (captionDetails.getRotation() != 0) {
                //always wrap
                constraintSet.constrainDefaultHeight(captionBox.getId(), ConstraintSet.MATCH_CONSTRAINT_WRAP);
                constraintSet.constrainDefaultWidth(captionBox.getId(), ConstraintSet.MATCH_CONSTRAINT_WRAP);
            } else {
                if (!captionDetails.isWrapping()) {
//                constraintSet.constrainDefaultHeight(captionBox.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
                    constraintSet.constrainDefaultWidth(captionBox.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
                } else {
                    constraintSet.constrainDefaultHeight(captionBox.getId(), ConstraintSet.MATCH_CONSTRAINT_WRAP);
                    constraintSet.constrainDefaultWidth(captionBox.getId(), ConstraintSet.MATCH_CONSTRAINT_WRAP);
                }
            }


            card.setElevation(0);
            captionBox.setElevation(2);
            //TEXTVIEW MARGIN
            Margin margin = captionDetails.getMargin();
            if (margin != null) {
                constraintSet.setMargin(R.id.captionBox, ConstraintSet.TOP, Utils.dpToPx(margin.getTop(), context));
                constraintSet.setMargin(R.id.captionBox, ConstraintSet.BOTTOM, Utils.dpToPx(margin.getBottom(), context));
                constraintSet.setMargin(R.id.captionBox, ConstraintSet.START, Utils.dpToPx(margin.getLeft(), context));
                constraintSet.setMargin(R.id.captionBox, ConstraintSet.END, Utils.dpToPx(margin.getRight(), context));
            }
            //TEXT ALIGNMENT
            if (!TextUtils.isEmpty(captionDetails.getAlignment())) {
                textview_caption.setTextAlignment(getTextAlignment(captionDetails.getAlignment()));
//                if (captionDetails.isWrapping()) {
//                    if (captionDetails.getAlignment().equalsIgnoreCase("start") || captionDetails.getAlignment().equalsIgnoreCase("left"))
//                        constraintSet.setHorizontalBias(R.id.captionBox, 0.0f);
//                    else if (captionDetails.getAlignment().equalsIgnoreCase("end") || captionDetails.getAlignment().equalsIgnoreCase("right"))
//                        constraintSet.setHorizontalBias(R.id.captionBox, 1.0f);
//                    else
//                        constraintSet.setHorizontalBias(R.id.captionBox, 0.5f);
//                }
            }
//            else
//                constraintSet.setHorizontalBias(R.id.captionBox, 0.5f);


            if (captionDetails.getYDirection().equalsIgnoreCase("bottom")) {
                constraintSet.setGuidelinePercent(R.id.topView, 0);
                constraintSet.setGuidelinePercent(R.id.bottomView, (float) (1 - bottomPercent));
                constraintSet.setVerticalBias(R.id.captionBox, 1f);
            } else {
                constraintSet.setGuidelinePercent(R.id.topView, (float) bottomPercent);
                constraintSet.setGuidelinePercent(R.id.bottomView, 1f);
                constraintSet.setVerticalBias(R.id.captionBox, 0f);
            }

            constraintSet.applyTo(root);

        }
        return captionView;
    }

    private void setWordAttributes(SpannableStringBuilder sentence, WordsItem word, int finalStart, int finalEnd, TextView textView, Context context,
                                   CaptionClickListener mListener, CaptionDetails captionDetails) {
        sentence.append(word.getWord());
        if (word.getFont() != null) {
            //WORD COLOR
            if (!TextUtils.isEmpty(word.getFont().getColor())) {
                sentence.setSpan(new ForegroundColorSpan(Color.parseColor(word.getFont().getColor())), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            //WORD SIZE
            if (word.getFont().getSize() > 0) {
                sentence.setSpan(new AbsoluteSizeSpan(word.getFont().getSize(), true), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            //WORD HIGHLIGHT
            if (!TextUtils.isEmpty(word.getHighlightColor())) {
                sentence.setSpan(new BackgroundColorSpan(Color.parseColor(word.getHighlightColor())), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            //WORD SHADOW
            if (word.getShadow() != null && !TextUtils.isEmpty(word.getShadow().getShadowColor())) {
                textView.setShadowLayer(word.getShadow().getShadow_radius(), word.getShadow().getX(), word.getShadow().getY(), Color.parseColor(word.getShadow().getShadowColor()));
            }
            //WORD UNDERLINE
            if (word.isUnderline()) {
                sentence.setSpan(new UnderlineSpan(), finalStart, finalEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            //WORD STYLE
            setTextFontStyle(context, sentence, finalStart, finalEnd, word.getFont().getFamily(), word.getFont().getStyle());
        }

        textView.setText(sentence);
        //CLICK EVENT
        textView.setFocusable(true);
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (captionDetails.isIsClickable()) {
//                    String[] parts = captionDetails.getAction().split("/");
//                    String action = parts[0];
//                    String action_id = parts[1];
                    if (mListener != null) mListener.onItemClick(captionDetails.getAction(), "");
                }
            }
        });
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        textView.measure(0, 0);
    }

    private int getRelativeTop(View view) {
        final View parent = (View) view.getParent();
        int[] parentLocation = new int[2];
        int[] viewLocation = new int[2];

        view.getLocationOnScreen(viewLocation);
        parent.getLocationOnScreen(parentLocation);

        return viewLocation[1] - parentLocation[1];
    }

    public void durationCallback(int progress) {
//        hide or destroy this current view
//        end seconds of the view < progress
    }

    private int getRelativeLeft(View view) {
        final View parent = (View) view.getParent();
        int[] parentLocation = new int[2];
        int[] viewLocation = new int[2];

        view.getLocationOnScreen(viewLocation);
        parent.getLocationOnScreen(parentLocation);

        return viewLocation[0] - parentLocation[0];
    }

}
