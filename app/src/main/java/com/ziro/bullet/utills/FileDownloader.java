package com.ziro.bullet.utills;

import android.content.Context;

import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;

public class FileDownloader {

    private static FileDownloader single_instance = null;
    private Fetch fetch = null;

    private FileDownloader(Context context) {
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(5)
                .build();
        fetch = Fetch.Impl.getInstance(fetchConfiguration);
    }

    public static FileDownloader getInstance(Context context) {
        if (single_instance == null)
            single_instance = new FileDownloader(context);
        return single_instance;
    }

    public Fetch fetch() {
        return fetch;
    }
}
