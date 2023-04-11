package com.ziro.bullet.utills;

import android.content.Context;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;

public class ClearGlideCacheAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private boolean result;
    private Context context;

    public ClearGlideCacheAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Glide.get(context).clearDiskCache();
            result = true;
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}