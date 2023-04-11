package com.ziro.bullet.bottomSheet;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.interfaces.BottomSheetItemCallback;

public class PostArticleBottomSheet {

    private Activity context;
    private BottomSheetDialog dialog;
    private RelativeLayout uploadMedia;
    private RelativeLayout uploadNewsReel;
    private RelativeLayout uploadYoutubeLink;
    private BottomSheetItemCallback callback;

    public PostArticleBottomSheet(Activity context, BottomSheetItemCallback callback) {
        this.context = context;
        this.callback = callback;
        dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
    }

    public void show(DialogInterface.OnDismissListener onDismissListener) {
        if (dialog != null) {
            View dialogView = context.getLayoutInflater().inflate(R.layout.bottom_sheet_post_article, null);
            uploadMedia = dialogView.findViewById(R.id.uploadMedia);
            uploadNewsReel = dialogView.findViewById(R.id.uploadNewsReel);
            uploadYoutubeLink = dialogView.findViewById(R.id.uploadYoutubeLink);

            uploadYoutubeLink.setOnClickListener(v -> {
                if (callback != null) callback.onItemClick("YOUTUBE");
                AnalyticsEvents.INSTANCE.logEvent(context, Events.UPLOAD_YOUTUBE_LINK_CLICK);
                hide();
            });
            uploadNewsReel.setOnClickListener(v -> {
                if (callback != null) callback.onItemClick("REELS");
                AnalyticsEvents.INSTANCE.logEvent(context, Events.UPLOAD_NEWSREELS_CLICK);
                hide();
            });
            uploadMedia.setOnClickListener(v -> {
                if (callback != null) callback.onItemClick("MEDIA");
                AnalyticsEvents.INSTANCE.logEvent(context, Events.UPLOAD_MEDIA_CLICK);
                hide();
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

    public void hide() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

}
