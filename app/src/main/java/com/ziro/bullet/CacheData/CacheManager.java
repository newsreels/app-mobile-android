package com.ziro.bullet.CacheData;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ziro.bullet.model.articles.Article;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class CacheManager {

    private String PARENT_FOLDER = "NIB";
    private static CacheManager singleton = null;
    private static String file_location = "";
    private static Gson gson = new GsonBuilder().setLenient().create();
    private Context context;

    public CacheManager(Context context) {
        this.context = context;
        if (context != null) {
            File f = new File(context.getExternalFilesDir(PARENT_FOLDER).getAbsolutePath()); //Creating an internal dir;
            if (!f.exists()) {
                f.mkdirs();
            }
        }
    }

    private File makeFolder(String folderName) {
        //below android 10 may be!!
//        File mydir = context.getDir("mydir", Context.MODE_PRIVATE); //Creating an internal dir;
        //for android 10
        File f = null;
        if (context != null) {
            f = new File(context.getExternalFilesDir(PARENT_FOLDER).getAbsolutePath() + "/" + folderName); //Creating an internal dir;
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        return f;
    }

    public void storeCategory(String context) {
        File myDir = makeFolder("Categories");
        File file = new File(myDir, context);
        if (!file.exists()) {
            try {
                File directory = new File(file.getParent());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                log("Excepton Occured: " + e.toString());
            }
        }
    }

    public void storeCategoryArticle(String context, String article) {
        File myDir = makeFolder("Categories");
        File file = new File(myDir, context);
        if (file != null) {
            if (!file.exists()) {
                try {
                    File directory = new File(file.getParent());
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    file.createNewFile();

                } catch (IOException e) {
                    log("Excepton Occured: " + e.toString());
                }
            }
            try {
                // Convenience class for writing character files
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
                bufferWriter.write(article);
                bufferWriter.close();

            } catch (IOException e) {
                log("Hmm.. Got an error while saving Company data to file " + e.toString());
            }
        }
    }

    // Read From File Utility
    public ArrayList<Article> readCategoryArticles(String context) {
        ArrayList<Article> articles = null;
        File myDir = makeFolder("Categories");
        File file = new File(myDir, context);
        log("Context FILE : " + file.exists());
        if (file.exists()) {
            try {
                String articleFile = readFile(file);
                if (!TextUtils.isEmpty(articleFile)) {
                    articles = gson.fromJson(articleFile, new TypeToken<ArrayList<Article>>() {
                    }.getType());
                    log("Articles : " + gson.toJson(articles));
                }
            } catch (Exception e) {
                log("error load cache from file Articles : " + e.toString());
            }
        }
        return articles;
    }

    private static String readFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }

    private static void log(String string) {
        Log.d("CacheManager", "log() called with: string = [" + string + "]");
    }
}
