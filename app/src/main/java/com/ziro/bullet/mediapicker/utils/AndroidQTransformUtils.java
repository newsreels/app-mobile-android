package com.ziro.bullet.mediapicker.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.ziro.bullet.mediapicker.config.PictureSelectionConfig;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

import okio.BufferedSource;
import okio.Okio;

public class AndroidQTransformUtils {


    public static String copyPathToAndroidQ(Context ctx, String url, int width, int height, String mineType, String customFileName) {
        if (PictureSelectionConfig.cacheResourcesEngine != null) {
            String cachePath = PictureSelectionConfig.cacheResourcesEngine.onCachePath(ctx, url);
            if (!TextUtils.isEmpty(cachePath)) {
                return cachePath;
            }
        }

        BufferedSource inBuffer = null;
        try {
            Uri uri = Uri.parse(url);
            String encryptionValue = StringUtils.getEncryptionValue(url, width, height);
            String newPath = PictureFileUtils.createFilePath(ctx, encryptionValue, mineType, customFileName);
            File outFile = new File(newPath);
            if (outFile.exists()) {
                return newPath;
            }
            inBuffer = Okio.buffer(Okio.source(Objects.requireNonNull(ctx.getContentResolver().openInputStream(uri))));
            boolean copyFileSuccess = PictureFileUtils.bufferCopy(inBuffer, outFile);
            if (copyFileSuccess) {
                return newPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inBuffer != null && inBuffer.isOpen()) {
                PictureFileUtils.close(inBuffer);
            }
        }
        return null;
    }

    public static boolean copyPathToDCIM(Context context, File inFile, Uri outUri) {
        try {
            OutputStream fileOutputStream = context.getContentResolver().openOutputStream(outUri);
            return PictureFileUtils.bufferCopy(inFile, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
