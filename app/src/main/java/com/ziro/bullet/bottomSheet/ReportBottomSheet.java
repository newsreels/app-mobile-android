package com.ziro.bullet.bottomSheet;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.MainActivityNew;
import com.ziro.bullet.adapters.ReportAdapter;
import com.ziro.bullet.interfaces.DismissBottomSheet;
import com.ziro.bullet.interfaces.ReportBottomSheetListener;
import com.ziro.bullet.model.Report.ReportModel;
import com.ziro.bullet.presenter.ReportPresenter;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class ReportBottomSheet implements ReportBottomSheetListener {

    private ArrayList<ReportModel> arrayList = new ArrayList<>();
    private Activity context;
    private BottomSheetDialog dialog;
    private RecyclerView report_list;
    private TextView cancel;
    private TextView submit;
    private ProgressBar progress;
    private ReportAdapter adapter;
    private ReportPresenter presenter;
    private String article_id;
    private String type;
    private DismissBottomSheet dismissBottomSheet;

    public ReportBottomSheet(Activity context, DismissBottomSheet dismissBottomSheet) {
        this.context = context;
        this.dismissBottomSheet = dismissBottomSheet;
        dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        presenter = new ReportPresenter(context, this);
    }

    public void show(String article_id, String type) {
        this.article_id = article_id;
        this.type = type;
        if (dialog != null) {
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.report_bottom_sheet, null);
            initView(dialogView);
            setData();
            dialog.setContentView(dialogView);
            dialog.setDismissWithAnimation(true);
            if (!dialog.isShowing()) {
                dialog.show();
                if (dismissBottomSheet != null) {
                    dismissBottomSheet.dismiss(false);
                }
            }
            if (presenter != null)
                presenter.getConcerns(article_id, type);
        }
    }

    private void setData() {
        //SET DATA
        adapter = new ReportAdapter(context, this, arrayList);
        LinearLayoutManager manager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        report_list.setLayoutManager(manager);
        report_list.setAdapter(adapter);
        submit.setEnabled(false);
    }

    private void initView(View dialogView) {
        report_list = dialogView.findViewById(R.id.report_list);
        submit = dialogView.findViewById(R.id.submit);
        cancel = dialogView.findViewById(R.id.cancel);
        progress = dialogView.findViewById(R.id.progress);

        cancel.setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (dismissBottomSheet != null) {
                dismissBottomSheet.dismiss(true);
            }
        });
        submit.setOnClickListener(v -> {
            if (presenter != null && adapter != null && !TextUtils.isEmpty(article_id)) {
                if (adapter.getSelectedConcerns().size() > 0) {
                    presenter.sendReport(adapter.getSelectedConcerns(), article_id, type);
                } else {
                    error(context.getString(R.string.select_at_least_one_option_to_continue));
                }
            }
        });
    }

    @Override
    public void selectReport(boolean isSelect, int position) {
        if (adapter != null && adapter.getSelectedConcerns() != null) {
            if (adapter.getSelectedConcerns().size() > 0) {
                submit.setEnabled(true);
                submit.setTextColor(context.getResources().getColor(R.color.theme_color_1));
            } else {
                submit.setTextColor(context.getResources().getColor(R.color.grey));
                submit.setEnabled(false);
            }
        }
    }

    @Override
    public void error(String string) {
        Utils.showPopupMessageWithCloseButton(context, 3000, "" + string, true);
    }

    @Override
    public void loaderShow(boolean b) {
        if (b) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void success(ArrayList<String> list) {
        arrayList.clear();
        for (String item : list) {
            arrayList.add(new ReportModel(item, false));
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void success() {
        Utils.showPopupMessageWithCloseButton(context, 3000, context.getString(R.string.report_submitted), false);
        if (dialog != null) {
            dialog.dismiss();
        }
        if (dismissBottomSheet != null) {
            dismissBottomSheet.dismiss(true);
        }
    }
}
