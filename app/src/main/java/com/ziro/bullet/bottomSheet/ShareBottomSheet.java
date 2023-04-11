package com.ziro.bullet.bottomSheet;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.ziro.bullet.utills.Constants.ACTION_UPDATE_EVENT;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.PostArticleActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.data.POST_TYPE;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.ApiResponseInterface;
import com.ziro.bullet.interfaces.DismissBottomSheet;
import com.ziro.bullet.interfaces.ShareInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.SharePresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.viewHolder.ShareBottomSheetView;

import java.util.HashMap;
import java.util.Map;

public class ShareBottomSheet implements ShareInterface {

    private Activity context;
    private BottomSheetDialog dialog;
    private SharePresenter presenter;
    private ShareBottomSheetView holder;
    private ShareToMainInterface shareToMainInterface;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private boolean isGotoFollowShow;
    private ShareInfo shareInfo;
    private String type;
    private PrefConfig prefConfig;

    public ShareBottomSheet(Activity context, ShareToMainInterface shareToMainInterface, boolean isGotoFollowShow, String type) {
        this.context = context;
        this.shareToMainInterface = shareToMainInterface;
        this.isGotoFollowShow = isGotoFollowShow;
        this.type = type;
        Constants.sharePgNotVisible = false;
        if (context != null) {
            dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
            presenter = new SharePresenter(context, this, type);
            followUnfollowPresenter = new FollowUnfollowPresenter(context);
            prefConfig = new PrefConfig(context);
        }


    }

    public void show(Article article, DialogInterface.OnDismissListener onDismissListener, ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
        if (dialog != null) {
            if (!TextUtils.isEmpty(type) && (type.equals("MY_SAVE_ARTICLES") || type.equals("MY_ARTICLES") || type.equals("MY_ARTICLES_DETAILS") || type.equals("MY_REELS"))) {
                View dialogView = context.getLayoutInflater().inflate(R.layout.my_article_bottom_sheet, null);
                Log.e("dialogView", " article :" + new Gson().toJson(article));

                RelativeLayout rlSave = dialogView.findViewById(R.id.save);
                RelativeLayout rlEdit = dialogView.findViewById(R.id.edit);
                RelativeLayout rlDelete = dialogView.findViewById(R.id.delete);
                RelativeLayout rlShare = dialogView.findViewById(R.id.share);
                TextView archiveText = dialogView.findViewById(R.id.source1);
                ImageView saveIcon = dialogView.findViewById(R.id.saveIcon);
                Log.e("testshare1", "show: " + Constants.onResumeReels);
                Constants.onResumeReels = false;
                Constants.sharePgNotVisible = false;

                switch (type) {
                    case "MY_SAVE_ARTICLES":
                        rlEdit.setVisibility(View.GONE);
                        rlDelete.setVisibility(View.GONE);
                    case "MY_ARTICLES":
                    case "MY_ARTICLES_DETAILS":
                        rlSave.setVisibility(View.VISIBLE);
                        rlShare.setVisibility(View.VISIBLE);

                        if (shareInfo != null)
                            if (shareInfo.isArticle_archived()) {
                                archiveText.setText(context.getString(R.string.remove_fav));
//                                saveIcon.setImageResource(R.drawable.ic_bookmark_selected_without_border);
                            } else {
                                archiveText.setText(context.getString(R.string.save_article));
//                                saveIcon.setImageResource(R.drawable.ic_save);
                            }

                        rlEdit.setOnClickListener(v -> {
                            hide();
                            Intent intent = new Intent(context, PostArticleActivity.class);
                            switch (article.getType()) {
                                case "IMAGE":
                                    intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
                                    break;
                                case "VIDEO":
                                    intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
                                    break;
                                case "YOUTUBE":
                                    intent.putExtra("POST_TYPE", POST_TYPE.YOUTUBE);
                                    break;
                                case "REELS":
                                    intent.putExtra("POST_TYPE", POST_TYPE.REELS);
                                    break;
                            }
                            intent.putExtra("MODE", MODE.EDIT);
                            intent.putExtra("article", new Gson().toJson(article));
                            ((Activity) context).startActivityForResult(intent, 123);
                            if (type.equalsIgnoreCase("MY_ARTICLES_DETAILS"))
                                ((Activity) context).finish();
                        });
                        rlDelete.setOnClickListener(v -> {
                            presenter.unpublishArticle(article.getId(), new ApiResponseInterface() {
                                @Override
                                public void _success() {
                                    if (type.equalsIgnoreCase("MY_ARTICLES_DETAILS"))
                                        ((Activity) context).finish();
                                    Utils.broadcastIntent(context, "update", ACTION_UPDATE_EVENT);
                                    error(context.getString(R.string.article_deleted));
                                    hide();
                                }

                                @Override
                                public void _other(int code) {
                                    hide();
                                }
                            });
                        });
                        rlSave.setOnClickListener(v -> {
                            AnalyticsEvents.INSTANCE.logEvent(context,
                                    Events.ARCHIVE_CLICK);

                            if (article == null || shareInfo == null) {
                                return;
                            }
                            if (!TextUtils.isEmpty(article.getId())) {
                                presenter.archive(article.getId(), shareInfo.isArticle_archived());
                            }
                            hide();
                        });
                        rlShare.setOnClickListener(v -> {
                            MediaShare build = new MediaShare.Builder(context)
                                    .setId(article.getId())
                                    .isArticle(true)
                                    .setShareInfo(shareInfo)
                                    .setArticle(article)
                                    .setonDismissListener(dialog -> {
                                        Constants.onResumeReels = false;
                                        Constants.sharePgNotVisible = false;
                                    }).build();
                            build.show();
                        });
                        break;
                    case "MY_REELS":
                        rlSave.setVisibility(View.GONE);
                        rlShare.setVisibility(View.GONE);
                        rlEdit.setOnClickListener(v -> {
                            Intent intent = new Intent(context, PostArticleActivity.class);
                            switch (article.getType()) {
                                case "IMAGE":
                                    intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
                                    break;
                                case "VIDEO":
                                    intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
                                    break;
                                case "YOUTUBE":
                                    intent.putExtra("POST_TYPE", POST_TYPE.YOUTUBE);
                                    break;
                                case "REELS":
                                    intent.putExtra("POST_TYPE", POST_TYPE.REELS);
                                    break;
                            }
                            intent.putExtra("MODE", MODE.EDIT);
                            intent.putExtra("article", new Gson().toJson(article));
                            ((Activity) context).startActivityForResult(intent, 123);
                            hide();
                            ((Activity) context).finish();
                        });
                        rlDelete.setOnClickListener(v -> {
                            presenter.unpublishArticle(article.getId(), new ApiResponseInterface() {
                                @Override
                                public void _success() {
                                    Utils.broadcastIntent(context, "update", ACTION_UPDATE_EVENT);
                                    error(context.getString(R.string.article_deleted));
                                    hide();
                                    ((Activity) context).finish();
                                }

                                @Override
                                public void _other(int code) {
                                    hide();
                                }
                            });
                        });
                        break;
                }

                dialog.setContentView(dialogView);
                dialog.setOnDismissListener(onDismissListener);
                dialog.setDismissWithAnimation(true);
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        BottomSheetDialog d = (BottomSheetDialog) dialog;

                        // This is gotten directly from the source of BottomSheetDialog
                        // in the wrapInBottomSheet() method
                        FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);

                        // Right here!
                        if (bottomSheet != null) {
                            BottomSheetBehavior.from(bottomSheet)
                                    .setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }
                });
                dialog.show();

            } else {
                View dialogView = context.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                holder = new ShareBottomSheetView(dialogView);
                Log.e("testshare", "show: ");
                Constants.onResumeReels = false;
                if (!TextUtils.isEmpty(type) && (type.equals("AUTHOR_ARTICLES"))) {
                    holder.getBlock().setVisibility(View.GONE);
                    holder.getGotoChannel().setVisibility(View.GONE);
                    holder.getFollow().setVisibility(View.GONE);
                } else if (!TextUtils.isEmpty(type)
                        && ((type.equals("COMMUNITY"))
                        || (type.equals("COMMUNITY_DETAILS")))
                ) {
                    holder.getFollow().setVisibility(View.GONE);
                    if (article.getSource() != null || (article.getAuthor() != null && article.getAuthor().size() > 0)) {
                        holder.getBlock().setVisibility(View.VISIBLE);
                        if (article.getAuthor().get(0) != null && prefConfig.isUserObject() != null && !TextUtils.isEmpty(prefConfig.isUserObject().getId()) && !TextUtils.isEmpty(article.getAuthor().get(0).getId())) {
                            if (article.getAuthor().get(0).getId().equalsIgnoreCase(prefConfig.isUserObject().getId())) {
                                holder.getBlock().setVisibility(View.GONE);
                                holder.getFollow().setVisibility(View.GONE);
                            }
                        }
                    } else {
                        holder.getBlock().setVisibility(View.GONE);
                    }
                    holder.getGotoChannel().setVisibility(View.VISIBLE);
                    holder.getReport().setVisibility(View.GONE);
                } else if (!TextUtils.isEmpty(type) && (type.equals("AuthorArticles"))) {
                    holder.getBlock().setVisibility(View.VISIBLE);
                    holder.getGotoChannel().setVisibility(View.GONE);
                    holder.getFollow().setVisibility(View.GONE);
                    holder.getReport().setVisibility(View.VISIBLE);
                } else if (isGotoFollowShow && !TextUtils.isEmpty(type) && (type.equals("REEL_MAIN"))) {
//                    holder.getCaption().setVisibility(View.VISIBLE);
                    if (prefConfig.isReelsCaption()) {
                        holder.getCaptionText().setText(context.getString(R.string.caption_off));
                    } else {
                        holder.getCaptionText().setText(context.getString(R.string.caption_on));
                    }
                    holder.getCaption().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            prefConfig.setReelsCaption(!prefConfig.isReelsCaption());
                            if (!prefConfig.isReelsCaption()) {
                                holder.getCaptionText().setText(context.getString(R.string.caption_off));
                                Utils.showPopupMessageWithCloseButton(context, 3000, context.getString(R.string.caption_turned_off), false);
                            } else {
                                holder.getCaptionText().setText(context.getString(R.string.caption_on));
                                Utils.showPopupMessageWithCloseButton(context, 3000, context.getString(R.string.caption_turned_on), false);
                            }
                            // for updating caption
                            if (shareToMainInterface != null) shareToMainInterface.unarchived();
                            hide();
                        }
                    });


                    if (article.getSource() != null || (article.getAuthor() != null && article.getAuthor().size() > 0)) {
                        holder.getGotoChannel().setVisibility(View.VISIBLE);
//                        holder.getFollow().setVisibility(View.VISIBLE);
//                        holder.getBlock().setVisibility(View.VISIBLE);
                    } else {
                        holder.getBlock().setVisibility(View.GONE);
                        holder.getGotoChannel().setVisibility(View.VISIBLE);
                        holder.getFollow().setVisibility(View.GONE);
                    }
                } else if (isGotoFollowShow && !TextUtils.isEmpty(type) && (type.equals("REEL_INNER"))) {
                    if (article.getSource() != null || (article.getAuthor() != null && article.getAuthor().size() > 0)) {
                        holder.getGotoChannel().setVisibility(View.VISIBLE);
//                        holder.getFollow().setVisibility(View.VISIBLE);
//                        holder.getBlock().setVisibility(View.VISIBLE);
                    } else {
                        holder.getBlock().setVisibility(View.GONE);
                        holder.getGotoChannel().setVisibility(View.VISIBLE);
                        holder.getFollow().setVisibility(View.GONE);
                    }
                } else if (isGotoFollowShow && type != null && type.equalsIgnoreCase("ARTICLES")) {
                    holder.getGotoChannel().setVisibility(View.VISIBLE);
                    holder.getBlock().setVisibility(View.VISIBLE);
                } else if (isGotoFollowShow && type != null && !type.equalsIgnoreCase("ARCHIVE")) {
                    if (article.getSource() != null || (article.getAuthor() != null && article.getAuthor().size() > 0)) {
                        holder.getGotoChannel().setVisibility(View.VISIBLE);
                        holder.getFollow().setVisibility(View.GONE);
                        holder.getBlock().setVisibility(View.GONE);
                    } else {
                        holder.getBlock().setVisibility(View.GONE);
                        holder.getGotoChannel().setVisibility(View.VISIBLE);
                        holder.getFollow().setVisibility(View.GONE);
                    }
                } else {
                    holder.getBlock().setVisibility(View.GONE);
                    holder.getGotoChannel().setVisibility(View.GONE);
                    holder.getFollow().setVisibility(View.GONE);
                }

                if (shareInfo != null && !TextUtils.isEmpty(shareInfo.getShare_message())) {
                    holder.getShare().setVisibility(View.VISIBLE);
                } else {
                    holder.getShare().setVisibility(View.GONE);
                }

                holder.getCopy().setOnClickListener(view ->
                        {
                            if (shareInfo != null) {
                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Newsreels", shareInfo.getShare_message());
                                clipboard.setPrimaryClip(clip);
                                Utils.showPopupMessageWithCloseButton(context, 3000, context.getString(R.string.copied_), false);
                                hide();
                            }
                        }
                );
                holder.getCancel().setOnClickListener(view -> hide());

                holder.getMore().setOnClickListener(v -> {
                    if (article == null) {
                        return;
                    }
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.MORE_LIKE_THIS_CLICK);
                    presenter.more(article.getId());
                });
                holder.getLess().setOnClickListener(v -> {
                    if (article == null) {
                        return;
                    }
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.LESS_LIKE_THIS_CLICK);
                    presenter.less(article.getId());
                });
                if (isGotoFollowShow && !TextUtils.isEmpty(type) && ((type.equals("REEL_MAIN")) || type.equals("REEL_INNER"))) {
                    holder.getGotoChannel().setOnClickListener(v -> {
                        hide();
                        if (article != null && article.getSource() != null && !TextUtils.isEmpty(article.getSource().getId())) {
                            Intent intent = new Intent(context, ChannelDetailsActivity.class);
                            intent.putExtra("id", article.getSource().getId());
                            context.startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST);
                        }

                    });
                } else {
                    holder.getGotoChannel().setOnClickListener(v -> {
                        if (article != null && article.getSource() != null && !TextUtils.isEmpty(article.getSource().getId())) {
                            Intent intent = new Intent(context, ChannelDetailsActivity.class);
                            intent.putExtra("id", article.getSource().getId());
                            context.startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST);
                        }
                        hide();
                    });
//                    holder.getGotoChannel().setOnClickListener(v -> {
//                        if (article == null) {
//                            return;
//                        }
//                        if (article.getSource() != null) {
//                            if (shareToMainInterface != null) {
//                                shareToMainInterface.onItemClicked(TYPE.SOURCE, article.getSource().getId(), article.getSource().getName(), article.getSource().isFavorite());
//                            }
//                        } else if (article.getAuthor() != null && article.getAuthor().size() > 0) {
//                            if (article.getAuthor().get(0) != null) {
//                                Utils.openAuthor(context, article.getAuthor().get(0));
//                            }
//                        }
//                        hide();
//                    });
                }

                holder.getBlock().setOnClickListener(v -> {
                    if (article == null) {
                        return;
                    }
                    if (shareInfo == null) {
                        return;
                    }
                    if (article.getSource() != null) {
                        Map<String, String> params = new HashMap<>();
                        params.put(Events.KEYS.REEL_ID, article.getId());
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.BLOCK_SOURCE);
                        if (shareInfo.isSource_blocked()) {
                            presenter.unblock(article.getSource().getId(), article.getId());
                        } else {
                            presenter.block(article.getSource().getId(), article.getId());
                        }
                    } else if (article.getAuthor() != null && article.getAuthor().size() > 0) {
                        Map<String, String> params = new HashMap<>();
                        params.put(Events.KEYS.AUTHOR_ID, article.getId());
                        AnalyticsEvents.INSTANCE.logEvent(context,
                                params,
                                Events.BLOCK_AUTHOR);
                        if (article.getAuthor().get(0) != null) {
                            if (shareInfo.isAuthor_blocked()) {
                                presenter.unblockAuthor(article.getAuthor().get(0).getId(), article.getId());
                            } else {
                                presenter.blockAuthor(article.getAuthor().get(0).getId(), article.getId());
                            }
                        }
                    }
                });
                holder.getFollow().setOnClickListener(v -> {
                    if (article == null) {
                        return;
                    }
                    if (shareInfo == null) {
                        return;
                    }
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.FOLLOW_SOURCE);
                    if (article.getSource() != null) {
                        if (shareInfo.isSource_followed()) {
                            presenter.unfollow(article.getSource().getId());
                        } else {
                            presenter.follow(article.getSource().getId());
                        }
                    } else if (article.getAuthor() != null && article.getAuthor().size() > 0) {
                        Author author = article.getAuthor().get(0);
                        if (author != null && followUnfollowPresenter != null) {
                            holder.getFollowIcon().setVisibility(View.GONE);
                            holder.getFollowProgress().setVisibility(View.VISIBLE);
                            if (shareInfo.isSource_followed()) {
                                followUnfollowPresenter.unFollowAuthor(author.getId(), 0, (position, flag) -> {
                                    if (flag) {
                                        holder.getFollowIcon().setVisibility(View.VISIBLE);
                                        holder.getFollowProgress().setVisibility(View.GONE);
                                        hide();
                                    }
                                });
                            } else {
                                followUnfollowPresenter.followAuthor(author.getId(), 0, (position, flag) -> {
                                    if (flag) {
                                        holder.getFollowIcon().setVisibility(View.VISIBLE);
                                        holder.getFollowProgress().setVisibility(View.GONE);
                                        hide();
                                    }
                                });
                            }
                        }
                    }
                });
                holder.getReport().setOnClickListener(v -> {
                    if (article == null) {
                        return;
                    }
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            Events.REPORT_CLICK);
                    ReportBottomSheet reportBottomSheet = new ReportBottomSheet(context, new DismissBottomSheet() {
                        @Override
                        public void dismiss(boolean flag) {
                            if (flag) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                    reportBottomSheet.show(article.getId(), "articles");
                });
                holder.getShare().setOnClickListener(v -> {
                    if (shareInfo == null) {
                        return;
                    }

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    if (shareInfo != null) {
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfo.getShare_message());
                    }
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    context.startActivity(shareIntent);

//                    MediaShare build = new MediaShare.Builder(context)
//                            .setId(article.getId())
//                            .isArticle(true)
//                            .setShareInfo(shareInfo)
//                            .setArticle(article)
//                            .setonDismissListener(dialog -> {
//
//                            }).build();
//                    build.show();
                });

                if (shareInfo != null) {
                    if (article != null && article.getSource() != null) {
                        holder.getSource1().setText(context.getString(R.string.go_to_guardian) + " " + article.getSource().getName());
                        if (shareInfo.isSource_followed()) {
                            holder.getSource2().setText(context.getString(R.string.unfollow) + " " + article.getSource().getName());
                        } else {
                            holder.getSource2().setText(context.getString(R.string.follow_guardian) + " " + article.getSource().getName());
                        }
                        if (shareInfo.isSource_blocked()) {
                            holder.getSource3().setText(context.getString(R.string.unblock_channel) + " " + article.getSource().getName());
                        } else {
                            holder.getSource3().setText(context.getString(R.string.block_guardian) + " " + article.getSource().getName());
                        }
                    } else if (article != null && article.getAuthor() != null) {
                        Author author = article.getAuthor().get(0);
                        if (author != null) {
                            holder.getSource1().setText(context.getString(R.string.go_to_guardian) + " " + author.getName());
                            if (shareInfo.isSource_followed()) {
                                holder.getSource2().setText(context.getString(R.string.unfollow) + " " + author.getName());
                            } else {
                                holder.getSource2().setText(context.getString(R.string.follow_guardian) + " " + author.getName());
                            }
                            if (shareInfo.isAuthor_blocked()) {
                                holder.getSource3().setText(context.getString(R.string.unblock_channel) + " " + author.getName());
                            } else {
                                holder.getSource3().setText(context.getString(R.string.block_guardian) + " " + author.getName());
                            }
                        }
                    }

                    if (shareInfo.isArticle_archived()) {
//                        holder.getArchiveIcon().setImageResource(R.drawable.ic_bookmark_selected_without_border);
                        holder.getArchiveText().setText(context.getString(R.string.remove_save));
                    } else {
                        holder.getArchiveText().setText(context.getString(R.string.save));
                    }
                }

                holder.getArchive().setOnClickListener(v -> {
                    if (article == null || shareInfo == null) {
                        return;
                    }
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.ARCHIVE_CLICK);
                    if (!TextUtils.isEmpty(article.getId())) {
                        presenter.archive(article.getId(), shareInfo.isArticle_archived());
                    }
                });

                dialog.setContentView(dialogView);
                dialog.setOnDismissListener(onDismissListener);
                dialog.setDismissWithAnimation(true);
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        BottomSheetDialog d = (BottomSheetDialog) dialog;

                        // This is gotten directly from the source of BottomSheetDialog
                        // in the wrapInBottomSheet() method
                        FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);

                        // Right here!
                        if (bottomSheet != null) {
                            BottomSheetBehavior.from(bottomSheet)
                                    .setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }
                });
                dialog.show();
            }
        }
    }


    public void disableBtn() {
        if (holder != null) {
            holder.getShare().setEnabled(false);
            holder.getMore().setEnabled(false);
            holder.getLess().setEnabled(false);
            holder.getGotoChannel().setEnabled(false);
            holder.getBlock().setEnabled(false);
            holder.getReport().setEnabled(false);
        }
    }

    public void enableBtn() {
        if (holder != null) {
            holder.getShare().setEnabled(true);
            holder.getMore().setEnabled(true);
            holder.getLess().setEnabled(true);
            holder.getGotoChannel().setEnabled(true);
            holder.getBlock().setEnabled(true);
            holder.getReport().setEnabled(true);
        }
    }

    public void hide() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    @Override
    public void error(String string) {
        Utils.showPopupMessageWithCloseButton(context, 3000, "" + string, true);
    }

    @Override
    public void loaderShow(boolean flag, String method) {
        if (flag) {
            disableBtn();
        } else {
            enableBtn();
        }
        switch (method) {
            case "share":
                if (flag) {
                    holder.getShareIcon().setVisibility(View.GONE);
                    holder.getShareProgress().setVisibility(View.VISIBLE);
                } else {
                    holder.getShareIcon().setVisibility(View.VISIBLE);
                    holder.getShareProgress().setVisibility(View.GONE);
                }
                break;
            case "save":
                break;
            case "more":
                if (flag) {
                    holder.getMoreIcon().setVisibility(View.GONE);
                    holder.getMoreProgress().setVisibility(View.VISIBLE);
                } else {
                    holder.getMoreIcon().setVisibility(View.VISIBLE);
                    holder.getMoreProgress().setVisibility(View.GONE);
                }
                break;
            case "less":
                if (flag) {
                    holder.getLessIcon().setVisibility(View.GONE);
                    holder.getLessProgress().setVisibility(View.VISIBLE);
                } else {
                    holder.getLessIcon().setVisibility(View.VISIBLE);
                    holder.getLessProgress().setVisibility(View.GONE);
                }
                break;
            case "follow":
            case "unfollow":
                if (flag) {
                    holder.getFollowIcon().setVisibility(View.GONE);
                    holder.getFollowProgress().setVisibility(View.VISIBLE);
                } else {
                    holder.getFollowIcon().setVisibility(View.VISIBLE);
                    holder.getFollowProgress().setVisibility(View.GONE);
                }
                break;
            case "unblock":
            case "block":
                if (flag) {
                    holder.getBlockIcon().setVisibility(View.GONE);
                    holder.getBlockProgress().setVisibility(View.VISIBLE);
                } else {
                    holder.getBlockIcon().setVisibility(View.VISIBLE);
                    holder.getBlockProgress().setVisibility(View.GONE);
                }
                break;
            case "report":
                if (flag) {
                    holder.getReportIcon().setVisibility(View.GONE);
                    holder.getReportProgress().setVisibility(View.VISIBLE);
                } else {
                    holder.getReportIcon().setVisibility(View.VISIBLE);
                    holder.getReportProgress().setVisibility(View.GONE);
                }
                break;
            case "archive":
                if (holder == null || holder.getArchiveIcon() == null || holder.getArchiveProgress() == null) {
                    return;
                }
                if (flag) {
                    holder.getArchiveIcon().setVisibility(View.GONE);
                    holder.getArchiveProgress().setVisibility(View.VISIBLE);
                } else {
                    holder.getArchiveIcon().setVisibility(View.VISIBLE);
                    holder.getArchiveProgress().setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void success(String msg, String method, String id) {
        Log.d("BOTTTOMST", "=========================");
        Log.e("BOTTTOMST", "id : " + id);
        Log.e("BOTTTOMST", "msg : " + msg);
        Log.e("BOTTTOMST", "method : " + method);
//        if (!method.equalsIgnoreCase("share")) {
//            Utils.showPopupMessageWithCloseButton(context, 3000, "" + msg, false);
//
////            Snackbar snackbar = Snackbar.make(context.getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG);
////            snackbar.show();
//
////            Utils.showSnacky(context.getWindow().getDecorView().getRootView(), msg);
//        }
        switch (method) {
            case "share":
                break;
            case "more":
                break;
            case "less":
                Utils.showPopupMessageWithCloseButton(context, 3000, context.getString(R.string.you_will_less_see), false);
                break;
            case "follow":
                Constants.isSourceDataChange = true;
                break;
            case "unfollow":
                break;
            case "block":
                Utils.showPopupMessageWithCloseButton(context, 3000, context.getString(R.string.blocked_successfully), false);
                break;
            case "remove":
                if (shareToMainInterface != null) {
                    shareToMainInterface.removeItem(id, 0);
                }
                Utils.showPopupMessageWithCloseButton(context, 3000, context.getString(R.string.removed_successfully), false);
                break;
            case "unblock":
                Utils.showPopupMessageWithCloseButton(context, 3000, context.getString(R.string.unblocked_successfully), false);
                break;
            case "report":
                break;
            case "archive":
                Utils.showPopupMessageWithCloseButton(context, 3000, context.getString(R.string.added_successfully), false);
                if (shareToMainInterface != null) {
                    shareToMainInterface.unarchived();
                }
                break;
        }
        hide();
    }
}
