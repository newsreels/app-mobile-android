package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.interfaces.ReportBottomSheetListener;
import com.ziro.bullet.model.Report.ReportModel;
import com.ziro.bullet.R;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private ReportBottomSheetListener listener;
    private Activity context;
    private ArrayList<ReportModel> items;
    private ViewHolder holder;

    public ReportAdapter(Activity context, ReportBottomSheetListener listener, ArrayList<ReportModel> arrayList) {
        this.context = context;
        this.items = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        this.holder = holder;
        ReportModel item = items.get(position);
        if (item != null) {
            if (!TextUtils.isEmpty(item.getName())) {
                holder.name.setText(item.getName());
            }
            holder.check.setChecked(item.isSelected());
            holder.check_main.setOnClickListener(v -> {
                item.setSelected(!item.isSelected());
                holder.check.setChecked(item.isSelected());
                if (listener != null)
                    listener.selectReport(item.isSelected(), position);
            });
        }
    }

    public ArrayList<String> getSelectedConcerns() {
        ArrayList<String> list = new ArrayList<>();
        for (ReportModel model : items) {
            if (model.isSelected()) {
                list.add(model.getName());
            }
        }
        return list;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout check_main;
        private CheckBox check;
        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            check = itemView.findViewById(R.id.check);
            check_main = itemView.findViewById(R.id.check_main);
        }
    }
}
