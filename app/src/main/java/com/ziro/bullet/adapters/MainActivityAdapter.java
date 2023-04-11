package com.ziro.bullet.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.feed.FacebookAdViewHolder;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.storyMaker.StoriesProgressView;
import com.ziro.bullet.utills.BlurTransformation;
import com.ziro.bullet.utills.Constants;

import java.util.List;
import java.util.Objects;


public class
MainActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ShareToMainInterface, AdapterCallback {

    public static final String TYPE_SEARCH = "SEARCH";
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE_GOOGLE = 1;
    private static final int AD_TYPE_FACEBOOK = 2;
    private final Activity context;
    private final List<Article> items;
    private final boolean isGotoFollowShow;
    private final OnGotoChannelListener gotoChannelListener;
    private final PrefConfig mPrefConfig;
    private final NewsCallback categoryCallback;
    private final String mType;
    private final ShareToMainInterface shareToMainInterface;
    private final TempCategorySwipeListener swipeListener;
    private final GoHome goHomeMainActivity;
    private final DetailsActivityInterface detailsActivityInterface;
    private final ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private ShareBottomSheet shareBottomSheet;
    //FOR PROGRESS LISTENER END
    private NativeAd mNativeAd;
    private int currentArticlePosition = 0;
    private CommentClick mCommentClick;
    private AdFailedListener adFailedListener;

    public MainActivityAdapter(CommentClick mCommentClick, GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface,
                               TempCategorySwipeListener swipeListener, NewsCallback categoryCallback,
                               OnGotoChannelListener gotoChannelListener,
                               Activity context, List<Article> arrayList, boolean isGotoFollowShow,
                               String type, DetailsActivityInterface detailsActivityInterface,
                               ShowOptionsLoaderCallback showOptionsLoaderCallback, AdFailedListener adFailedListener) {

        this.mCommentClick = mCommentClick;
        this.swipeListener = swipeListener;
        this.goHomeMainActivity = goHomeMainActivity;
        this.shareToMainInterface = shareToMainInterface;
        this.context = context;
        this.mType = type;
        this.gotoChannelListener = gotoChannelListener;
        this.items = arrayList;
        this.isGotoFollowShow = isGotoFollowShow;
        this.categoryCallback = categoryCallback;
        this.detailsActivityInterface = detailsActivityInterface;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.adFailedListener = adFailedListener;
        this.mPrefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view;
        if (type == CONTENT_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_small_card, parent, false);
            SmallCardViewHolder smallCardViewHolder = new SmallCardViewHolder(false, mCommentClick, mType, view, context, this, mPrefConfig, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);

            ViewTreeObserver vtoSmall = smallCardViewHolder.itemView.getViewTreeObserver();
            if (vtoSmall.isAlive()) {
                vtoSmall.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        smallCardViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewHeight = smallCardViewHolder.itemView.getHeight();
                        if (categoryCallback != null)
                            categoryCallback.onItemHeightMeasured(viewHeight);
                    }
                });
            }
            return smallCardViewHolder;
        } else if (type == AD_TYPE_GOOGLE) {
            view = LayoutInflater.from(context).inflate(R.layout.admob_list_view, parent, false);
            return new ADViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_list_fb_ad, parent, false);
            return new FacebookAdViewHolder(false, view, context, mPrefConfig, adFailedListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < items.size() && !TextUtils.isEmpty(items.get(position).getType()) && items.get(position).getType().equalsIgnoreCase("G_Ad")) {
            return AD_TYPE_GOOGLE;
        } else if (position < items.size() && !TextUtils.isEmpty(items.get(position).getType()) && items.get(position).getType().equalsIgnoreCase("FB_Ad")) {
            return AD_TYPE_FACEBOOK;
        } else
            return CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {
        if (items != null && items.size() > 0) {
            return items.size();
        } else {
            return 0;
        }
    }

    public void setCurrentArticlePosition(int position) {
        this.currentArticlePosition = position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder1, final int position) {
        if (items != null && items.size() > 0) {
            Article content = items.get(position);
            if (holder1 instanceof SmallCardViewHolder) {
                ((SmallCardViewHolder) holder1).bind(position, content);
            } else {
                Log.d("audiotest", "aefac home : stop_destroy");
                if (goHomeMainActivity != null)
                    goHomeMainActivity.sendAudioEvent("stop_destroy");
            }
        }
    }

    public void dismissBottomSheet() {
        if (shareBottomSheet != null) {
            shareBottomSheet.hide();
        }
    }

    @Override
    public void removeItem(String id, int position) {
        if (items != null && items.size() > 0) {
            if (!TextUtils.isEmpty(items.get(position).getId()) && !TextUtils.isEmpty(id)) {
                if (items.get(position).getId().equalsIgnoreCase(id)) {
                    boolean isSelected = items.get(position).isSelected();
                    items.remove(position);
                    notifyDataSetChanged();
                    if (position < items.size()) {
                        if (isSelected)
                            items.get(position).setSelected(true);
                    }
                    notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void unarchived() {

    }

    @Override
    public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
        Log.e("TabSelected", "here type --> " + type);
        if (gotoChannelListener != null) {
            gotoChannelListener.onItemClicked(type, id, name, favorite);
        }
    }

    private void showBottomSheetDialog(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet(context, shareToMainInterface, isGotoFollowShow, mType);
        }
        shareBottomSheet.show(article, onDismissListener, shareInfo);
    }

    private void refreshAd(ADViewHolder holder1) {
        Log.e("refreshAd", "refreshAd : ");
        if (holder1 != null) {
            AdLoader.Builder builder = null;
            if (BuildConfig.DEBUG)
                builder = new AdLoader.Builder(context, Constants.ADMOB_AD_UNIT_ID);
            else {
                if (mPrefConfig.getAds() != null && mPrefConfig.getAds().getAdmob() != null)
                    builder = new AdLoader.Builder(context, mPrefConfig.getAds().getAdmob().getFeed());
            }

            if (builder != null) {
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        if (mNativeAd != null) {
                            nativeAd.destroy();
                        }
                        mNativeAd = nativeAd;
                        NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_list_card, null);
                        populateUnifiedNativeAdView(nativeAd, adView, holder1);
                        holder1.fl_adplaceholder.removeAllViews();
                        holder1.fl_adplaceholder.addView(adView);
                    }
                });
//                builder.forUnifiedNativeAd(unifiedNativeAd -> {
//                    // You must call destroy on old ads when you are done with them,
//                    // otherwise you will have a memory leak.
//                    if (nativeAd != null) {
//                        nativeAd.destroy();
//                    }
//                    nativeAd = unifiedNativeAd;
//                    NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_list_card, null);
//                    populateUnifiedNativeAdView(unifiedNativeAd, adView, holder1);
//                    holder1.fl_adplaceholder.removeAllViews();
//                    holder1.fl_adplaceholder.addView(adView);
//                });

                VideoOptions videoOptions = new VideoOptions.Builder()
                        .setStartMuted(Constants.muted)
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
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d("TAG", "onAdFailedToLoad() called with: loadAdError = [" + loadAdError + "]");
                    }
                }).build();

                adLoader.loadAd(new AdRequest.Builder().build());
            }
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView, ADViewHolder adHolder) {

        if (adHolder != null) {
            ADViewHolder holder1 = (ADViewHolder) adHolder;

            // Set the media view. Media content will be automatically populated in the media view once
            // adView.setNativeAd() is called.
            MediaView mediaView = adView.findViewById(R.id.ad_media);
            adView.setMediaView(mediaView);

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
            TextView ad_txt = adView.findViewById(R.id.ad_txt);

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

//            if (nativeAd.getIcon() == null) {
//                adView.getIconView().setVisibility(View.GONE);
//            } else {
//                ((ImageView) adView.getIconView()).setImageDrawable(
//                        nativeAd.getIcon().getDrawable());
//                adView.getIconView().setVisibility(View.GONE);
//            }

//            if (nativeAd.getPrice() == null) {
//                adView.getPriceView().setVisibility(View.INVISIBLE);
//            } else {
//                adView.getPriceView().setVisibility(View.VISIBLE);
//                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//            }
//
//            if (nativeAd.getStore() == null) {
//                adView.getStoreView().setVisibility(View.INVISIBLE);
//            } else {
//                adView.getStoreView().setVisibility(View.VISIBLE);
//                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//            }

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
                Glide.with(context)
                        .load(nativeAd.getImages().get(0).getUri())
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                        .into(bck);
            }
            adView.setNativeAd(nativeAd);
            try {
                VideoController vc = Objects.requireNonNull(nativeAd.getMediaContent()).getVideoController();
                if (holder1 != null) {
                    if (vc.hasVideoContent()) {
                        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                            @Override
                            public void onVideoEnd() {
                                super.onVideoEnd();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getArticlePosition() {
        return currentArticlePosition;
    }

    @Override
    public void showShareBottomSheet(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareInfo == null && !TextUtils.isEmpty(mType) && mType.equals("MY_ARTICLES")) {
            showBottomSheetDialog(shareInfo, article, onDismissListener);
        } else
            showBottomSheetDialog(shareInfo, article, onDismissListener);
    }

    @Override
    public void onItemClick(int position, boolean setCurrentView) {

    }

    public class ADViewHolder extends RecyclerView.ViewHolder {
        //AD-MOB START
        StoriesProgressView progress;
        FrameLayout fl_adplaceholder;
        //AD-MOB END

        public ADViewHolder(View itemView) {
            super(itemView);

            refreshAd(this);
            //AD-MOB START
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
            progress = itemView.findViewById(R.id.progress);
            //AD-MOB END
        }
    }
}
