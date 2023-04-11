package com.ziro.bullet.adapters.feed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.utills.Constants;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class AdViewHolder extends RecyclerView.ViewHolder {

    private CardView rootCard4;
    private RelativeLayout ad_mob;
    private FrameLayout fl_adplaceholder;
    private Activity context;
    private PrefConfig mPrefConfig;
    private NativeAd mNativeAd;
    private AdFailedListener adFailedListener;
    private TextView ad_txt;
    private TextView body;
    private TextView headline;
    private boolean isWhiteOnly;


    public AdViewHolder(boolean isWhiteOnly, @NonNull View itemView, Context context, PrefConfig config, AdFailedListener adFailedListener) {
        super(itemView);
        this.adFailedListener = adFailedListener;
        rootCard4 = itemView.findViewById(R.id.rootCard);
        ad_mob = itemView.findViewById(R.id.ad_mob);
        fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
        this.context = (Activity) context;
        this.mPrefConfig = config;
        this.isWhiteOnly = isWhiteOnly;
        refreshAd();
    }

    public void bind() {
        updateView();
        //loading ads on bindview will cause lag issues during scrolling
//        refreshAd();

//        ViewGroup.LayoutParams params = itemView.getLayoutParams();
//
//        int height = (int) context.getResources().getDimension(R.dimen._130sdp);
//        if(loadingFailed) {
//            params.height = 0;
//        }else{
//            params.height = height;
//        }
//
//
//        itemView.setLayoutParams(params);
//        itemView.requestLayout();
    }

    private void refreshAd() {
        AdLoader.Builder builder = null;
        if (BuildConfig.DEBUG)
            builder = new AdLoader.Builder(context, Constants.ADMOB_AD_UNIT_ID);
        else {
            if (mPrefConfig.getAds() != null && mPrefConfig.getAds().getAdmob().getFeed() != null)
                builder = new AdLoader.Builder(context, mPrefConfig.getAds().getAdmob().getFeed());
        }

        if (builder != null) {
            builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    if (mNativeAd != null) {
                        mNativeAd.destroy();
                    }
                    mNativeAd = nativeAd;
                    NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_list_card, null);
                    populateUnifiedNativeAdView(nativeAd, adView);
                    fl_adplaceholder.removeAllViews();
                    fl_adplaceholder.addView(adView);
                }
            });

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.d("TAG", "onAdLoaded() called");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            Events.AD_CLICK);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adFailedListener.onAdFailed();
                    Log.d("TAG", "onAdFailedToLoad() called with: loadAdError = [" + loadAdError + "]");
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {


        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        body = adView.findViewById(R.id.ad_body);
        headline = adView.findViewById(R.id.ad_headline);
        // Set other ad assets.
        adView.setHeadlineView(headline);
        adView.setBodyView(body);
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        ad_txt = adView.findViewById(R.id.ad_txt);


        updateView();

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.GONE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
            ad_txt.setText("Advertisement");
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
            ad_txt.setText("AD");
        }

//            if (nativeAd.getAdvertiser() == null) {
//                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
//            } else {
//                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
//                adView.getAdvertiserView().setVisibility(View.VISIBLE);
//            }

        ImageView bck = adView.findViewById(R.id.ad_imageBack);
        if (nativeAd.getImages() != null && nativeAd.getImages().size() > 0) {
            if (nativeAd.getImages().get(0).getUri() != null) {

                Picasso.get()
                        .load(nativeAd.getImages().get(0).getUri())
                        .transform(new BlurTransformation(context, 25, 3))
                        .into(bck);
            }
        }
        adView.setNativeAd(nativeAd);
        try {
            VideoController vc = nativeAd.getMediaContent().getVideoController();
            if (vc.hasVideoContent()) {
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        if (!isWhiteOnly) {
            if (rootCard4 != null)
                rootCard4.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
            if (ad_mob != null)
                ad_mob.setBackgroundColor(context.getResources().getColor(R.color.card_bg));
            if (body != null) {
                body.setTextColor(context.getResources().getColor(R.color.bullet_text));
                Typeface typeface = ResourcesCompat.getFont(context, R.font.manuale_medium);
                body.setTypeface(typeface);

            }
            if (headline != null) {
                headline.setTextColor(context.getResources().getColor(R.color.bullet_text));
                Typeface typeface = ResourcesCompat.getFont(context, R.font.manuale_bold);
                headline.setTypeface(typeface);
            }
        } else {
            if (rootCard4 != null)
                rootCard4.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
            if (ad_mob != null)
                ad_mob.setBackgroundColor(context.getResources().getColor(R.color.card_bg));
            if (body != null) {
                body.setTextColor(context.getResources().getColor(R.color.black));
                Typeface typeface = ResourcesCompat.getFont(context, R.font.manuale_medium);
                body.setTypeface(typeface);
            }
            if (headline != null) {
                headline.setTextColor(context.getResources().getColor(R.color.black));
                Typeface typeface = ResourcesCompat.getFont(context, R.font.manuale_bold);
                headline.setTypeface(typeface);
            }
        }
    }
}
