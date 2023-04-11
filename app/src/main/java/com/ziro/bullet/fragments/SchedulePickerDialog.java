package com.ziro.bullet.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ziro.bullet.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SchedulePickerDialog {


    private final static String DATE_FORMAT = "EEE, MMM dd";
    private final static String TIME_FORMAT = "hh:mm aaa";
    private Context context;
    private Dialog dialog;

    public SchedulePickerDialog(Context context) {
        this.context = context;
    }

    public void showDialog(ScheduleSelectionCallback callback, boolean showClearBtn) {
        dialog = new Dialog(context, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_schedule_time_picker);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlDate = dialog.findViewById(R.id.rlDate);
        TextView tvDate = dialog.findViewById(R.id.tvDate);
        RelativeLayout rlTime = dialog.findViewById(R.id.rlTime);
        TextView tvTime = dialog.findViewById(R.id.tvTime);
        ConstraintLayout rlContinue = dialog.findViewById(R.id.continuee);
        RelativeLayout root = dialog.findViewById(R.id.root);
        ConstraintLayout clear = dialog.findViewById(R.id.clear);
        TextView okBtn = dialog.findViewById(R.id.ok_btn);

        clear.setVisibility(showClearBtn ? View.VISIBLE : View.GONE);
        if(showClearBtn){
            okBtn.setText(context.getString(R.string.ok));
        }else{
            okBtn.setText(context.getString(R.string.continuee));
        }

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

        SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat(TIME_FORMAT, Locale.US);

        tvDate.setText(sdfDate.format(calendar.getTime()));
        tvTime.setText(sdfTime.format(calendar.getTime()));
        rlDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    context,
                    R.style.DialogTheme,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvDate.setText(sdfDate.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        });
        rlTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    context,
                    R.style.DialogTheme,
                    (view, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        tvTime.setText(sdfTime.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            );
            timePickerDialog.show();
        });
        rlContinue.setOnClickListener(v -> {
            Calendar calendarNow = Calendar.getInstance();
            if (calendar.after(calendarNow)) {
                if (callback != null)
                    callback.onScheduleSelected(calendar);
            } else {
                Toast.makeText(context, "" + context.getString(R.string.schedule_invalid_time), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onCleared();

                dialog.dismiss();
            }
        });

        root.setOnClickListener(v -> dialog.dismiss());

        if (!((Activity) context).isFinishing())
            if (!dialog.isShowing()) {
                dialog.show();
            }
    }

    public interface ScheduleSelectionCallback {
        void onScheduleSelected(Calendar selectedCalendar);
        void onCleared();
    }

}
