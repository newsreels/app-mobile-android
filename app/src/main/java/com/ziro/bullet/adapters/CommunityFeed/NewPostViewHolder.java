package com.ziro.bullet.adapters.CommunityFeed;

import static com.ziro.bullet.background.BroadcastEmitter.BG_ERROR;
import static com.ziro.bullet.background.BroadcastEmitter.BG_PROCESSING_COMPLETED;
import static com.ziro.bullet.background.BroadcastEmitter.BG_PROCESS_START;
import static com.ziro.bullet.background.BroadcastEmitter.BG_PUBLISHED;
import static com.ziro.bullet.background.BroadcastEmitter.BG_PUBLISHING;
import static com.ziro.bullet.background.BroadcastEmitter.BG_STOP;
import static com.ziro.bullet.background.BroadcastEmitter.BG_UPLOAD_COMPLETED;
import static com.ziro.bullet.background.BroadcastEmitter.BG_UPLOAD_PROGRESS;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.LoginPopupActivity;
import com.ziro.bullet.background.BackgroundEvent;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.fragments.GuidelinePopup;
import com.ziro.bullet.interfaces.ApiResponseInterface;
import com.ziro.bullet.interfaces.CommunityItemCallback;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPostViewHolder extends RecyclerView.ViewHolder {

    private CardView card;
    private ImageView addCancel;
    private TextView create_post;
    private LinearLayout options;
    private RelativeLayout bullets;
    private RelativeLayout addCancelMain;
    private RelativeLayout reels;
    private RelativeLayout youtube;
    private TextView tv_upload_label;
    private LinearLayout llBgProgressContainer;
    private LinearProgressIndicator progressIndicator;
    private CommunityItemCallback callback;
    private PrefConfig mPrefConfig;
    private Activity context;

    public NewPostViewHolder(View view, Activity context, CommunityItemCallback callback) {
        super(view);
        this.mPrefConfig = new PrefConfig(context);
        this.callback = callback;
        this.context = context;
        card = view.findViewById(R.id.card);
        addCancel = view.findViewById(R.id.addCancel);
        create_post = view.findViewById(R.id.create_post);
        addCancelMain = view.findViewById(R.id.addCancelMain);
        options = view.findViewById(R.id.options);
        bullets = view.findViewById(R.id.bullets);
        reels = view.findViewById(R.id.reels);
        youtube = view.findViewById(R.id.youtube);
        tv_upload_label = view.findViewById(R.id.tv_upload_label);
        llBgProgressContainer = view.findViewById(R.id.ll_bg_progress);
        progressIndicator = view.findViewById(R.id.linearPb);
        bullets.setAlpha(0);
        reels.setAlpha(0);
        youtube.setAlpha(0);
    }

    public void bind(Article article) {
        addCancel.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_red_cross));
        create_post.setTextColor(context.getResources().getColor(R.color.discover_header_text));
        card.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
        if (article != null && mPrefConfig != null) {

//            if (article.isSelected()) {
//                expandCard();
//            } else {
//                collapseCard();
//            }

            addCancelMain.setOnClickListener(v -> {
                if (!article.isSelected()) {
                    if (!mPrefConfig.isGuestUser() && mPrefConfig.getGuidelines()) {
                        article.setSelected(true);
                    }
                    checkAccount();
                } else {
                    article.setSelected(false);
                    collapseCard();
                }
            });
            card.setOnClickListener(v -> {
                if (!article.isSelected()) {
                    if (!mPrefConfig.isGuestUser() && mPrefConfig.getGuidelines()) {
                        article.setSelected(true);
                    }
                    checkAccount();
                } else {
                    article.setSelected(false);
                    collapseCard();
                }
            });
        }
        bullets.setOnClickListener(v -> {
            if (callback == null) return;
            callback.onItemClick("MEDIA", article);
        });
        reels.setOnClickListener(v -> {
            if (callback == null) return;
            callback.onItemClick("REELS", article);
        });
        youtube.setOnClickListener(v -> {
            if (callback == null) return;
            callback.onItemClick("YOUTUBE", article);
        });
    }

    public void updateBackgroundTask(BackgroundEvent event) {
        switch (event.getData()) {
            case BG_PROCESS_START:
                llBgProgressContainer.setVisibility(View.VISIBLE);
                tv_upload_label.setText(context.getString(R.string.processing));
                progressIndicator.setIndeterminate(true);
                break;
            case BG_PROCESSING_COMPLETED:
                llBgProgressContainer.setVisibility(View.GONE);
                tv_upload_label.setText("");
                break;
            case BG_UPLOAD_PROGRESS:
                llBgProgressContainer.setVisibility(View.VISIBLE);
                tv_upload_label.setText(context.getString(R.string.uploading));
                progressIndicator.setIndeterminate(false);
                progressIndicator.setProgress(event.getProgress());
                break;
            case BG_UPLOAD_COMPLETED:
                llBgProgressContainer.setVisibility(View.GONE);
                tv_upload_label.setText("");
                break;
            case BG_PUBLISHING:
                tv_upload_label.setText(context.getString(R.string.publishing));
                progressIndicator.setIndeterminate(true);
                llBgProgressContainer.setVisibility(View.VISIBLE);
                break;
            case BG_PUBLISHED:
                llBgProgressContainer.setVisibility(View.GONE);
                tv_upload_label.setText("");
                break;
            case BG_ERROR:
                llBgProgressContainer.setVisibility(View.GONE);
                break;
            case BG_STOP:
                llBgProgressContainer.setVisibility(View.GONE);
                tv_upload_label.setText("");
                break;
        }
    }

    private void collapseCard() {
        addCancel.animate().setDuration(300).rotation(45);
        create_post.animate().setDuration(500).alpha(1);
        Utils.collapseAction(options, bullets, reels, youtube, 500);
        if (callback == null) return;
        callback.onItemClick("collapse", null);
    }

    private void checkAccount() {
        if (mPrefConfig != null) {
            if (mPrefConfig.isGuestUser()) {
                LoginPopupActivity.start(context);
            } else {
                if (!mPrefConfig.getGuidelines()) {
                    GuidelinePopup popup = new GuidelinePopup(context, (flag, msg) -> {
                        if (flag) {
                            acceptGuidelines(new ApiResponseInterface() {
                                @Override
                                public void _success() {
                                    if (mPrefConfig != null)
                                        mPrefConfig.setGuidelines(true);
                                    expandCard();
                                }

                                @Override
                                public void _other(int code) {

                                }
                            });
                        }
                    });
                    popup.showDialog();
                } else {
                    expandCard();
                }
            }
        }
    }

    private void expandCard() {
        if (bullets.getAlpha() != 1) {
            addCancel.animate().setDuration(300).rotation(0);
            create_post.animate().setDuration(500).alpha(0);
            Utils.expand2(options, bullets, reels, youtube);
            addCancel.animate().setDuration(300).rotation(0);
            create_post.animate().setDuration(500).alpha(0);
            Utils.expand2(options, bullets, reels, youtube);
        }
        if (callback == null) return;
        callback.onItemClick("expand", null);
    }


    public void acceptGuidelines(ApiResponseInterface apiResponseInterface) {
        if (!InternetCheckHelper.isConnected()) {
            return;
        }
        if (!TextUtils.isEmpty(mPrefConfig.getAccessToken())) {
            Call<ResponseBody> call = ApiClient.getInstance(context).getApi()
                    .acceptGuidelines("Bearer " + mPrefConfig.getAccessToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    Log.e("FCM", "FCM token to server onResponse : " + response.toString());
                    if (apiResponseInterface != null) {
                        if (response.code() == 200) {
                            apiResponseInterface._success();
                        } else {
                            apiResponseInterface._other(response.code());
                        }
                    } else {
                        apiResponseInterface._other(0);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    Log.e("FCM", "FCM token to server Failure : " + t.getMessage());
                    if (apiResponseInterface != null)
                        apiResponseInterface._other(0);
                }
            });
        }
    }
}
