package com.ziro.bullet.bottomSheet;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.ShareInterface;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.SharePresenter;
import com.ziro.bullet.utills.Utils;

public class ProfileBottomSheet implements ShareInterface {

    private Activity context;
    private BottomSheetDialog dialog;
    private SharePresenter presenter;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private String type;
    private PrefConfig prefConfig;

    //BINDVIEWS
    private RelativeLayout block;
    private TextView blockTxt;
    private ImageView blockIcon;
    private ProgressBar blockProgress;
    private RelativeLayout report;
    private TextView reportTxt;
    private ImageView reportIcon;
    private ProgressBar reportProgress;
    private String Id;
    private String authorName;

    public ProfileBottomSheet(Activity context, String type, String Id, String authorName) {
        this.context = context;
        this.type = type;
        this.Id = Id;
        this.authorName = authorName;
        dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        presenter = new SharePresenter(context, this, type);
        followUnfollowPresenter = new FollowUnfollowPresenter(context);
        prefConfig = new PrefConfig(context);
    }

    public void show(DialogInterface.OnDismissListener onDismissListener) {

        if (dialog != null) {
            if (!TextUtils.isEmpty(type)) {
                View dialogView = context.getLayoutInflater().inflate(R.layout.profile_bottom_sheet, null);

                block = dialogView.findViewById(R.id.block);
                blockTxt = dialogView.findViewById(R.id.blockTxt);
                blockIcon = dialogView.findViewById(R.id.blockIcon);
                blockProgress = dialogView.findViewById(R.id.blockProgress);

                report = dialogView.findViewById(R.id.report);
                reportTxt = dialogView.findViewById(R.id.reportTxt);
                reportIcon = dialogView.findViewById(R.id.reportIcon);
                reportProgress = dialogView.findViewById(R.id.reportProgress);

                if (!TextUtils.isEmpty(authorName)) {
                    blockTxt.setText(context.getResources().getString(R.string.block) + " " + authorName);
                    reportTxt.setText(context.getResources().getString(R.string.report) + " " + authorName);
                }

                block.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(Id) && presenter != null) {
                        switch (type) {
                            case "authors":
                                presenter.blockAuthor(Id);
                                break;
                            case "sources":
                                presenter.block(Id, "");
                                break;
                        }

                    }
                });

                report.setOnClickListener(v -> {
                    ReportBottomSheet reportBottomSheet = new ReportBottomSheet(context, flag -> {
                        if (flag) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    reportBottomSheet.show(Id, type);
                });

                dialog.setContentView(dialogView);
                dialog.setOnDismissListener(onDismissListener);
                dialog.setDismissWithAnimation(true);
                dialog.setOnShowListener(dialogInterface -> {
                    BottomSheetDialog d = (BottomSheetDialog) dialog;
                    FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    if (bottomSheet != null) {
                        BottomSheetBehavior.from(bottomSheet)
                                .setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });
                dialog.show();
            }
        }
    }

    public void disableBtn() {
        if (block != null) {
            block.setEnabled(false);
        }
        if (report != null) {
            report.setEnabled(false);
        }
    }

    public void enableBtn() {
        if (block != null) {
            block.setEnabled(true);
        }
        if (report != null) {
            report.setEnabled(true);
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
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loaderShow(boolean flag, String method) {
        if (flag) {
            disableBtn();
        } else {
            enableBtn();
        }
        switch (method) {
            case "block":
                if (flag) {
                    blockIcon.setVisibility(View.GONE);
                    blockProgress.setVisibility(View.VISIBLE);
                } else {
                    blockIcon.setVisibility(View.VISIBLE);
                    blockProgress.setVisibility(View.GONE);
                }
                break;
            case "report":
                if (flag) {
                    reportIcon.setVisibility(View.GONE);
                    reportProgress.setVisibility(View.VISIBLE);
                } else {
                    reportIcon.setVisibility(View.VISIBLE);
                    reportProgress.setVisibility(View.GONE);
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

        switch (method) {
            case "block":
                if (context != null) {
                    Utils.showSnacky(context.getWindow().getDecorView().getRootView(), msg);
                    context.finish();
                }
                break;
        }
        hide();
    }
}
