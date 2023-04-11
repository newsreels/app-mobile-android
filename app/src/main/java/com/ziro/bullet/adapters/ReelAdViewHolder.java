package com.ziro.bullet.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.config.Ads;
import com.ziro.bullet.reels.ReelsRemoveAdvt;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;
import java.util.List;

public class ReelAdViewHolder extends RecyclerView.ViewHolder {

    private NativeAdLayout nativeAdLayout = null;
    private FrameLayout fl_adplaceholder = null;
    private LinearLayout llAdShimmer = null;
    private Activity context;
    private PrefConfig mPrefConfig;

    private com.google.android.gms.ads.nativead.NativeAd mNativeAd = null;
    private NativeAd finalNativeAd;
    private AdError adErrorFb;

    private ReelsRemoveAdvt reelsRemoveAdvt;
    private int position;

    public ReelAdViewHolder(@NonNull View itemView, Context context, PrefConfig config, ReelsRemoveAdvt reelsRemoveAdvt, int position) {
        super(itemView);
        this.position = position;
        this.reelsRemoveAdvt = reelsRemoveAdvt;
        this.context = (Activity) context;
        this.mPrefConfig = config;
        Ads ads = mPrefConfig.getAds();
        if (ads != null) {
            if (!TextUtils.isEmpty(ads.getType()) && ads.getType().equalsIgnoreCase("facebook")) {
                this.nativeAdLayout = itemView.findViewById(R.id.native_ad_container);
                this.nativeAdLayout.setVisibility(View.VISIBLE);
                refreshFbAd();
            } else {
                this.fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
                this.llAdShimmer = itemView.findViewById(R.id.ll_ad_shimmer);
                refreshGoogleAd();
            }
        }
    }

    private void refreshFbAd() {
        NativeAd nativeAd = null;
        if (mPrefConfig.getAds() != null && mPrefConfig.getAds().getFacebook() != null && !TextUtils.isEmpty(mPrefConfig.getAds().getFacebook().getReel())) {
            if (BuildConfig.DEBUG)
                nativeAd = new NativeAd(context, "VID_HD_16_9_46S_LINK#" + mPrefConfig.getAds().getFacebook().getReel());
            else {
                nativeAd = new NativeAd(context, mPrefConfig.getAds().getFacebook().getReel());
            }
        }

        finalNativeAd = nativeAd;
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                Log.e("FB_Error", "onMediaDownloaded");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (adError != null) {
                    adErrorFb = adError;
                    Log.e("FB_Error", "FB Message : " + adError.getErrorMessage());
                    Log.e("FB_Error", "FB Code : " + adError.getErrorCode());
                    Log.e("FB_Error", "FB position adapter : " + getBindingAdapterPosition());
                    Log.e("FB_Error", "FB position : " + position);
                    if (reelsRemoveAdvt != null) {
                        if (position != 0) {
                            reelsRemoveAdvt.removeItem(position);
                        } else {
                            reelsRemoveAdvt.removeItem(getBindingAdapterPosition());
                        }
                    }
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.e("FB_Error", "onAdLoaded");
                // Race condition, load() called again before last ad was displayed
                adErrorFb = null;
                if (finalNativeAd == null || finalNativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateFbAd(finalNativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.e("FB_Error", "onAdClicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.e("FB_Error", "onLoggingImpression");
            }
        };

        // Request an ad
        if (finalNativeAd != null) {
            finalNativeAd.loadAd(
                    finalNativeAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.NONE)
                            .build());
        }
    }

    private void inflateFbAd(NativeAd nativeAd) {
        if (nativeAdLayout != null && nativeAd != null) {
            nativeAd.unregisterView();

            View adView = context.getLayoutInflater().inflate(R.layout.item_reel_ad, null);
            nativeAdLayout.removeAllViews();
            nativeAdLayout.addView(adView);

            // Create native UI using the ad metadata.
            MediaView nativeAdMedia = adView.findViewById(R.id.facebookMedia);
            MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
            TextView nativeAdTitle = adView.findViewById(R.id.title);
            Button nativeAdCallToAction = adView.findViewById(R.id.shop);

            // Set the Text.
            nativeAdTitle.setVisibility(nativeAd.getAdBodyText() != null ? View.VISIBLE : View.INVISIBLE);
            nativeAdTitle.setText(nativeAd.getAdBodyText());
            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            nativeAdIcon.setVisibility(View.VISIBLE);
            nativeAdMedia.setVisibility(View.VISIBLE);
            // Create a list of clickable views
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);

            // Register the Title and CTA button to listen for clicks.
            nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
        }
    }

    public void bind() {
        Ads ads = mPrefConfig.getAds();
        if (ads != null) {
            if (!TextUtils.isEmpty(ads.getType()) && ads.getType().equalsIgnoreCase("facebook")) {
                if (adErrorFb == null && finalNativeAd != null) {
                    // Inflate Native Ad into Container
                    inflateFbAd(finalNativeAd);
                }

            } else {
                if (mNativeAd != null) {
                    NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.item_reel_ad_google, null);
                    populateUnifiedNativeAdView(mNativeAd, adView);
                    fl_adplaceholder.removeAllViews();
                    fl_adplaceholder.addView(adView);
                }
            }
        }
    }

    private void refreshGoogleAd() {
        AdLoader.Builder builder = null;
        if (BuildConfig.DEBUG)
            builder = new AdLoader.Builder(context, Constants.ADMOB_AD_UNIT_ID);
        else {
            if (mPrefConfig.getAds() != null && mPrefConfig.getAds().getAdmob() != null)
                builder = new AdLoader.Builder(context, mPrefConfig.getAds().getAdmob().getReel());
        }

        if (builder != null) {

            builder.forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                    if (mNativeAd != null) {
                        nativeAd.destroy();
                    }
                    mNativeAd = nativeAd;
                    NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.item_reel_ad_google, null);
                    populateUnifiedNativeAdView(nativeAd, adView);
                    fl_adplaceholder.removeAllViews();
                    fl_adplaceholder.addView(adView);
                    llAdShimmer.setVisibility(View.GONE);
                    fl_adplaceholder.setVisibility(View.VISIBLE);
                }
            });
            builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                @Override
                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                    // You must call destroy on old ads when you are done with them,
                    // otherwise you will have a memory leak.

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
                    Log.d("TAG", " google ads onAdLoaded() called");
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
                    Log.d("TAG", "onAdFailedToLoad() called with: loadAdError = [" + loadAdError + "]");
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    private void populateUnifiedNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {


        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        com.google.android.gms.ads.nativead.MediaView mediaView = adView.findViewById(R.id.googleMedia);
        mediaView.setVisibility(View.VISIBLE);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.title));
//        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.shop));
//        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        TextView ad_txt = adView.findViewById(R.id.ad_txt);

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
//        if (nativeAd.getBody() == null) {
//            adView.getBodyView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getBodyView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
//        }

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
}
