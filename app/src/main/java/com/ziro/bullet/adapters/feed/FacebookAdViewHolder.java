package com.ziro.bullet.adapters.feed;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AdFailedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class FacebookAdViewHolder extends RecyclerView.ViewHolder {

    private NativeAdLayout nativeAdLayout = null;
    private Activity context;
    private PrefConfig mPrefConfig;
    private AdFailedListener adFailedListener;
    private CardView rootCard5;
    private boolean isWhiteOnly;
    private TextView nativeAdTitle;
    private TextView nativeAdBody;

    public FacebookAdViewHolder(boolean isWhiteOnly, @NonNull View itemView, Context context, PrefConfig config, AdFailedListener adFailedListener) {
        super(itemView);
        this.adFailedListener = adFailedListener;
        this.nativeAdLayout = itemView.findViewById(R.id.native_ad_container);
        this.context = (Activity) context;
        this.mPrefConfig = config;
        this.isWhiteOnly = isWhiteOnly;
        rootCard5 = itemView.findViewById(R.id.rootCard);
        refreshAd();
    }

    private void refreshAd() {
        NativeAd nativeAd = null;
        if (mPrefConfig.getAds() != null && mPrefConfig.getAds().getFacebook() != null && !TextUtils.isEmpty(mPrefConfig.getAds().getFacebook().getReel())) {
            if (BuildConfig.DEBUG)
                nativeAd = new NativeAd(context, "PLAYABLE#" + mPrefConfig.getAds().getFacebook().getReel());
            else {
                if (mPrefConfig.getAds() != null)
                    nativeAd = new NativeAd(context, mPrefConfig.getAds().getFacebook().getReel());
            }
        }

        NativeAd finalNativeAd = nativeAd;
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                Log.e("FB_Error", "onMediaDownloaded : ");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                adFailedListener.onAdFailed();
                if (adError != null) {
                    Log.e("FB_Error", "FB Message : " + adError.getErrorMessage());
                    Log.e("FB_Error", "FB Code : " + adError.getErrorCode());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.e("FB_Error", "onAdLoaded");
                // Race condition, load() called again before last ad was displayed
                if (finalNativeAd == null || finalNativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(finalNativeAd);
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


    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        View adView = context.getLayoutInflater().inflate(R.layout.fb_ad_list_card, null);
        nativeAdLayout.removeAllViews();
        nativeAdLayout.addView(adView);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        nativeAdTitle = adView.findViewById(R.id.ad_headline);
        MediaView nativeAdMedia = adView.findViewById(R.id.ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.ad_store);
        nativeAdBody = adView.findViewById(R.id.ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.ad_advertiser);
        Button nativeAdCallToAction = adView.findViewById(R.id.ad_call_to_action);


        updateView();

        // Set the Text.
        nativeAdTitle.setVisibility(nativeAd.getAdvertiserName() != null ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setVisibility(nativeAd.getAdBodyText() != null ? View.VISIBLE : View.INVISIBLE);
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setVisibility(nativeAd.getAdSocialContext() != null ? View.VISIBLE : View.INVISIBLE);
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setVisibility(nativeAd.getSponsoredTranslation() != null ? View.VISIBLE : View.INVISIBLE);
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        ImageView bck = adView.findViewById(R.id.ad_imageBack);
        if (nativeAd.getAdCoverImage() != null && !TextUtils.isEmpty(nativeAd.getAdCoverImage().getUrl())) {
            Picasso.get()
                    .load(nativeAd.getAdCoverImage().getUrl())
                    .transform(new BlurTransformation(context, 25, 3))
                    .into(bck);
        }

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    private void updateView() {
        if (!isWhiteOnly) {
            if (rootCard5 != null)
                rootCard5.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
            if (nativeAdTitle != null) {
                nativeAdTitle.setTextColor(context.getResources().getColor(R.color.bullet_text));
            }
            if (nativeAdBody != null) {
                nativeAdBody.setTextColor(context.getResources().getColor(R.color.bullet_text));
            }
        } else {
            if (rootCard5 != null)
                rootCard5.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            if (nativeAdTitle != null) {
                nativeAdTitle.setTextColor(context.getResources().getColor(R.color.black));
            }
            if (nativeAdBody != null) {
                nativeAdBody.setTextColor(context.getResources().getColor(R.color.black));
            }
        }
    }

    public void bind() {
        updateView();
//        ViewGroup.LayoutParams params = itemView.getLayoutParams();
//
//        int height = (int) context.getResources().getDimension(R.dimen._130sdp);
//        if(loadingFailed) {
//            params.height = 0;
//        }else{
//            params.height = height;
//        }
//        itemView.setLayoutParams(params);
//        itemView.requestLayout();
    }
}
