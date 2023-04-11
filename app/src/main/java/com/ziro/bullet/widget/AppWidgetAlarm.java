package com.ziro.bullet.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AppWidgetAlarm {
    private Context mContext;

    private int ALARM_ID = 0;
    private int INTERVAL_MILLIS = 7200000;

    public AppWidgetAlarm(Context context) {
        this.mContext = context;
    }

    public void startAlarm(){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MILLISECOND, INTERVAL_MILLIS);

        Intent alarmIntent=new Intent(mContext, CollectionWidget.class);
        alarmIntent.setAction(CollectionWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
        alarmManager.setRepeating(AlarmManager.RTC, instance.getTimeInMillis(), INTERVAL_MILLIS, pendingIntent);

    }



    public void stopAlarm() {
        Intent alarmIntent = new Intent(CollectionWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
