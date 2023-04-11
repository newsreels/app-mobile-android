package com.ziro.bullet.CacheData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ziro.bullet.background.UploadInfo;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.background.VideoStatus;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.Tabs.DataItem;
import com.ziro.bullet.model.Tabs.SubItem;
import com.ziro.bullet.model.articles.ForYou;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "nib_db_v1";

    //HOME TABS
    private static final String TABLE_HOME = "home";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUB_ITEMS = "subItems";
    private static final String KEY_CONTEXT = "context";

    //HOME ARTICLES
    private static final String TABLE_HOME_ARTICLES = "home_articles";
    private static final String KEY_HOME_ARTICLES_ID = "id";
    private static final String KEY_HOME_ARTICLES_TITLE = "title";
    private static final String KEY_HOME_ARTICLES_CONTEXT = "context";
    private static final String KEY_HOME_ARTICLES_JSON = "json";
    private static final String KEY_HOME_ARTICLES_LAST_MODIFIED = "last";

    //REELS
    private static final String TABLE_REELS = "reels";
    private static final String KEY_REEL_ID = "id";
    private static final String KEY_REEL_TYPE = "type";
    private static final String KEY_REEL_JSON = "json";

//    //Community
//    private static final String TABLE_COMMUNITY = "community";
//    private static final String KEY_COMMUNITY_ID = "id";
//    private static final String KEY_COMMUNITY_TYPE = "type";
//    private static final String KEY_COMMUNITY_JSON = "json";

    //DISCOVER
    private static final String TABLE_DISCOVER = "discover";
    private static final String KEY_DISCOVER_ID = "id";
    private static final String KEY_DISCOVER_TYPE = "type";
    private static final String KEY_DISCOVER_JSON = "json";
    private static final String KEY_DISCOVER_LAST_MODIFIED = "last";

    //PUBLISH
    private static final String TABLE_TASKS = "tasks";
    private static final String KEY_TASKS_ID = "id";
    private static final String KEY_TASKS_TYPE = "type";
    private static final String KEY_TASKS_SOURCE = "source";
    private static final String KEY_TASKS_STATUS = "status";
    private static final String KEY_TASKS_VIDEO_INFO = "video_info";
    private static final String KEY_TASKS_UPLOAD_KEY = "upload_key";
    private static final String KEY_TASKS_READY_TO_PUBLISH = "ready_to_publish";
    private static final String KEY_TASKS_ERROR = "error";

    private static final String TAG = "CacheManager";


    public DbHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_TABLE = "CREATE TABLE " + TABLE_HOME + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_SUB_ITEMS + " TEXT,"
                    + KEY_CONTEXT + " TEXT" +
                    ")";
            db.execSQL(CREATE_TABLE);

            String CREATE_TABLE_ARTICLES = "CREATE TABLE " + TABLE_HOME_ARTICLES + "("
                    + KEY_HOME_ARTICLES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_HOME_ARTICLES_TITLE + " TEXT,"
                    + KEY_HOME_ARTICLES_CONTEXT + " TEXT,"
                    + KEY_HOME_ARTICLES_LAST_MODIFIED + " TEXT,"
                    + KEY_HOME_ARTICLES_JSON + " TEXT" + ")";
            db.execSQL(CREATE_TABLE_ARTICLES);

            String CREATE_TABLE_REEL = "CREATE TABLE " + TABLE_REELS + "("
                    + KEY_REEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_REEL_TYPE + " TEXT,"
                    + KEY_REEL_JSON + " TEXT" + ")";
            db.execSQL(CREATE_TABLE_REEL);

//            String CREATE_TABLE_COMMUNITY = "CREATE TABLE " + TABLE_COMMUNITY + "("
//                    + KEY_COMMUNITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + KEY_COMMUNITY_TYPE + " TEXT,"
//                    + KEY_COMMUNITY_JSON + " TEXT" + ")";
//            db.execSQL(CREATE_TABLE_COMMUNITY);

            String CREATE_TABLE_DISCOVER = "CREATE TABLE " + TABLE_DISCOVER + "("
                    + KEY_DISCOVER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_DISCOVER_TYPE + " TEXT,"
                    + KEY_DISCOVER_LAST_MODIFIED + " TEXT,"
                    + KEY_DISCOVER_JSON + " TEXT" + ")";
            db.execSQL(CREATE_TABLE_DISCOVER);

            //tasks table
            String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + "("
                    + KEY_TASKS_ID + " TEXT PRIMARY KEY,"
                    + KEY_TASKS_TYPE + " TEXT,"
                    + KEY_TASKS_STATUS + " TEXT,"
                    + KEY_TASKS_SOURCE + " TEXT,"
                    + KEY_TASKS_UPLOAD_KEY + " TEXT,"
                    + KEY_TASKS_READY_TO_PUBLISH + " TEXT,"
                    + KEY_TASKS_ERROR + " TEXT,"
                    + KEY_TASKS_VIDEO_INFO + " TEXT" + ")";
            db.execSQL(CREATE_TABLE_TASKS);
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // Drop older table if exist
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOME_ARTICLES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REELS);
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMUNITY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISCOVER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            // Create tables again
            onCreate(db);
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }
    // **** CRUD (Create, Read, Update, Delete) Operations ***** //

    public void clearTasksDb() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_TASKS, null, null);
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    public void clearHomeDb() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_HOME, null, null);
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    public void clearReelsDb() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_REELS, null, null);
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    public void clearDb() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_HOME, null, null);
            db.delete(TABLE_HOME_ARTICLES, null, null);
            db.delete(TABLE_REELS, null, null);
//            db.delete(TABLE_COMMUNITY, null, null);
            db.delete(TABLE_DISCOVER, null, null);
            db.delete(TABLE_TASKS, null, null);
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    // Adding new record
    public void insertHomeData(String title, String context, String json) {
        try {
            //Get the Data Repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_TITLE, title);
            cValues.put(KEY_CONTEXT, context);
            cValues.put(KEY_SUB_ITEMS, json);

            if (!checkIfContextExist(context, TABLE_HOME)) {
                db.insert(TABLE_HOME, null, cValues);
            } else {
                db.update(TABLE_HOME, cValues, KEY_CONTEXT + " = ?", new String[]{context});
            }

        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    // Adding new reel record
    public void insertReelData(String type, String json) {
        try {
            //Get the Data Repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_REEL_JSON, json);
            cValues.put(KEY_REEL_TYPE, type);
            if (!checkIfReelExist(type)) {
                db.insert(TABLE_REELS, null, cValues);
            } else {
                db.update(TABLE_REELS, cValues, KEY_REEL_TYPE + " = ?", new String[]{type});
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }


    // Adding new reel record
    public int insertReelList(String type, String json) {
        try {
            //Get the Data Repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_REEL_JSON, json);
            cValues.put(KEY_REEL_TYPE, type);
            if (!checkIfReelExist(type)) {
                return (int) db.insert(TABLE_REELS, null, cValues);
            } else {
                return db.update(TABLE_REELS, cValues, KEY_REEL_TYPE + " = ?", new String[]{type});
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
            return 123;
        }
    }

    // Adding new community
    public void insertCommunityData(String type, String json) {
//        try {
//            //Get the Data Repository in write mode
//            SQLiteDatabase db = this.getWritableDatabase();
//            ContentValues cValues = new ContentValues();
//            cValues.put(KEY_COMMUNITY_JSON, json);
//            cValues.put(KEY_COMMUNITY_TYPE, type);
//            if (!checkIfCommunityExist(type)) {
//                Log.d(TAG, "not exist = " + type + "");
//                db.insert(TABLE_COMMUNITY, null, cValues);
//            } else {
//                Log.d(TAG, " exist = " + type + "");
//                db.update(TABLE_COMMUNITY, cValues, KEY_COMMUNITY_TYPE + " = ?", new String[]{type});
//            }
//        } catch (Exception e) {
//            Log.d(TAG, " db = [" + e.getMessage() + "]");
//        }
    }


    // Adding new community
    public void insertDiscoverData(String type, String json, String lastModified) {
        try {
            //Get the Data Repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_DISCOVER_JSON, json);
            cValues.put(KEY_DISCOVER_LAST_MODIFIED, lastModified);
            cValues.put(KEY_DISCOVER_TYPE, type);
            if (!checkIfDiscoverExist(type)) {
                Log.d(TAG, "not exist = " + type + "");
                db.insert(TABLE_DISCOVER, null, cValues);
            } else {
                Log.d(TAG, " exist = " + type + "");
                db.update(TABLE_DISCOVER, cValues, KEY_DISCOVER_TYPE + " = ?", new String[]{type});
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    // Get all record
    public ArrayList<DataItem> GetHomeTabs() {
        ArrayList<DataItem> dataItems = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_HOME, new String[]{KEY_TITLE, KEY_SUB_ITEMS, KEY_CONTEXT}, null, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DataItem dataItem = new DataItem();
                    dataItem.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                    dataItem.setId(cursor.getString(cursor.getColumnIndex(KEY_CONTEXT)));
                    String json = cursor.getString(cursor.getColumnIndex(KEY_SUB_ITEMS));
                    if (!TextUtils.isEmpty(json)) {
                        Type listType = new TypeToken<List<SubItem>>() {
                        }.getType();
                        Gson gson = new Gson();
                        List<SubItem> subItemList = gson.fromJson(json, listType);
                        dataItem.setSubItems(subItemList);
                    }
                    dataItems.add(dataItem);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return dataItems;
    }

    // Get Record based on context
    public ArrayList<DiscoverItem> getDiscoverRecord() {
        String Json = "";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_DISCOVER, new String[]{KEY_DISCOVER_TYPE, KEY_DISCOVER_JSON}, KEY_DISCOVER_TYPE + "=?", new String[]{"discover"}, null, null, null, null);
            if (cursor.moveToNext()) {
                Json = cursor.getString(cursor.getColumnIndex(KEY_DISCOVER_JSON));
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return new Gson().fromJson(Json, new TypeToken<ArrayList<DiscoverItem>>() {
        }.getType());
    }

    // Get Record based on context
    public HomeResponse GetHomeRecordById(String context) {
//        return null;
        String Json = "";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_HOME_ARTICLES, new String[]{KEY_HOME_ARTICLES_TITLE, KEY_HOME_ARTICLES_CONTEXT,
                    KEY_HOME_ARTICLES_JSON, KEY_HOME_ARTICLES_LAST_MODIFIED}, KEY_HOME_ARTICLES_CONTEXT + "=?", new String[]{context}, null, null, null, null);
            if (cursor.moveToNext()) {
                Json = cursor.getString(cursor.getColumnIndex(KEY_HOME_ARTICLES_JSON));
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return new Gson().fromJson(Json, HomeResponse.class);
    }

    public String getHomeLastModifiedTimeById(String context) {
        String last = "";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_HOME_ARTICLES, new String[]{KEY_HOME_ARTICLES_TITLE, KEY_HOME_ARTICLES_CONTEXT, KEY_HOME_ARTICLES_JSON, KEY_HOME_ARTICLES_LAST_MODIFIED}, KEY_HOME_ARTICLES_CONTEXT + "=?", new String[]{context}, null, null, null, null);
            if (cursor.moveToNext()) {
                last = cursor.getString(cursor.getColumnIndex(KEY_HOME_ARTICLES_LAST_MODIFIED));
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return last;
    }

    public String getDiscoverLastModifiedTimeById() {
        String last = "";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_DISCOVER, new String[]{KEY_DISCOVER_LAST_MODIFIED}, KEY_DISCOVER_TYPE + "=?", new String[]{"discover"}, null, null, null, null);
            if (cursor.moveToNext()) {
                last = cursor.getString(cursor.getColumnIndex(KEY_DISCOVER_LAST_MODIFIED));
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return last;
    }


    // Get Record based on context
    public ForYou GetHomeRecordByForYou(String context) {
        String Json = "";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_HOME_ARTICLES, new String[]{KEY_HOME_ARTICLES_TITLE, KEY_HOME_ARTICLES_CONTEXT, KEY_HOME_ARTICLES_JSON, KEY_HOME_ARTICLES_LAST_MODIFIED}, KEY_HOME_ARTICLES_CONTEXT + "=?", new String[]{context}, null, null, null, null);
            if (cursor.moveToNext()) {
                Json = cursor.getString(cursor.getColumnIndex(KEY_HOME_ARTICLES_JSON));
            }
            cursor.close();
            return new Gson().fromJson(Json, ForYou.class);
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
            return new ForYou();
        }
    }

    // Get Record based on type
    public String GetCommunityRecord(String type) {
//        SQLiteDatabase db = this.getWritableDatabase();
        String Json = "";
//        try {
//            Cursor cursor = db.query(TABLE_COMMUNITY, new String[]{KEY_COMMUNITY_TYPE, KEY_COMMUNITY_JSON}, KEY_COMMUNITY_TYPE + "=?", new String[]{type}, null, null, null, null);
//            if (cursor.moveToNext()) {
//                Json = cursor.getString(cursor.getColumnIndex(KEY_COMMUNITY_JSON));
//            }
//            cursor.close();
//        } catch (Exception e) {
//            Log.d(TAG, " db = [" + e.getMessage() + "]");
//        }
        return Json;
    }

    // Get Record based on type
    public ReelsItem GetReelRecord(String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Json = "";
        try {
            Cursor cursor = db.query(TABLE_REELS, new String[]{KEY_REEL_TYPE, KEY_REEL_JSON}, KEY_REEL_TYPE + "=?", new String[]{type}, null, null, null, null);
            if (cursor.moveToNext()) {
                Json = cursor.getString(cursor.getColumnIndex(KEY_REEL_JSON));
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return new Gson().fromJson(Json, new TypeToken<ReelsItem>() {
        }.getType());
    }

    public ReelResponse getReelList(String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Json = "";
        try {
            Cursor cursor = db.query(TABLE_REELS, new String[]{KEY_REEL_TYPE, KEY_REEL_JSON}, KEY_REEL_TYPE + "=?", new String[]{type}, null, null, null, null);
            if (cursor.moveToNext()) {
                Json = cursor.getString(cursor.getColumnIndex(KEY_REEL_JSON));
            cursor.close();
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return new Gson().fromJson(Json, new TypeToken<ReelResponse>() {
        }.getType());
    }

    // Get Record based on context
    public boolean checkIfContextExist(String context, String table) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ArrayList<HashMap<String, String>> userList = new ArrayList<>();
            Cursor cursor = db.query(table, new String[]{KEY_TITLE, KEY_CONTEXT}, KEY_CONTEXT + "=?", new String[]{context}, null, null, null, null);
            if (cursor.getCount() <= 0) {
                return false;
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return true;
    }

    // Get Record based on type
    public boolean checkIfReelExist(String type) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ArrayList<HashMap<String, String>> userList = new ArrayList<>();
            Cursor cursor = db.query(TABLE_REELS, new String[]{KEY_REEL_TYPE, KEY_REEL_JSON}, KEY_REEL_TYPE + "=?", new String[]{type}, null, null, null, null);
            if (cursor.getCount() <= 0) {
                return false;
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return true;
    }

    // Get Record based on context
    public boolean checkIfCommunityExist(String type) {
//        try {
//            SQLiteDatabase db = this.getWritableDatabase();
//            Cursor cursor = db.query(TABLE_COMMUNITY, new String[]{KEY_COMMUNITY_TYPE, KEY_COMMUNITY_JSON}, KEY_COMMUNITY_TYPE + "=?", new String[]{type}, null, null, null, null);
//            if (cursor.getCount() <= 0) {
//                return false;
//            }
//            cursor.close();
//        } catch (Exception e) {
//            Log.d(TAG, " db = [" + e.getMessage() + "]");
//        }
        return true;
    }

    // Get Record based on context
    public boolean checkIfDiscoverExist(String type) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_DISCOVER, new String[]{KEY_DISCOVER_TYPE, KEY_DISCOVER_JSON}, KEY_DISCOVER_TYPE + "=?", new String[]{type}, null, null, null, null);
            if (cursor.getCount() <= 0) {
                return false;
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return true;
    }

    // Delete Home Record
    public void DeleteHome(String context) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_HOME, KEY_CONTEXT + " = ?", new String[]{context});
            db.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    // Update Home Record
    public void UpdateHomeRecord(String context, String json, String lastModified) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_HOME_ARTICLES_CONTEXT, context);
            cValues.put(KEY_HOME_ARTICLES_JSON, json);
            cValues.put(KEY_HOME_ARTICLES_LAST_MODIFIED, lastModified);
            if (!checkIfContextExist(context, TABLE_HOME_ARTICLES)) {
                db.insert(TABLE_HOME_ARTICLES, null, cValues);
            } else {
                db.update(TABLE_HOME_ARTICLES, cValues, KEY_CONTEXT + " = ?", new String[]{context});
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    // Update Home Record
    public void UpdateForYouRecord(String json) {
        try {
            String context = "for_you";
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_HOME_ARTICLES_CONTEXT, context);
            cValues.put(KEY_HOME_ARTICLES_JSON, json);
            if (!checkIfContextExist(context, TABLE_HOME_ARTICLES)) {
                db.insert(TABLE_HOME_ARTICLES, null, cValues);
            } else {
//                db.delete(TABLE_HOME_ARTICLES, KEY_CONTEXT + " = ?", new String[]{context});
//                db.insert(TABLE_HOME_ARTICLES, null, cValues);
                db.update(TABLE_HOME_ARTICLES, cValues, KEY_CONTEXT + " = ?", new String[]{context});
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }


    // Adding new task
    public void insertTask(UploadInfo uploadInfo) {
        try {
            //Get the Data Repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_TASKS_ID, uploadInfo.getId());
            cValues.put(KEY_TASKS_TYPE, uploadInfo.getType());
            cValues.put(KEY_TASKS_SOURCE, uploadInfo.getSource());
            cValues.put(KEY_TASKS_STATUS, uploadInfo.getVideoStatus());
            cValues.put(KEY_TASKS_UPLOAD_KEY, uploadInfo.getKey());
            cValues.put(KEY_TASKS_VIDEO_INFO, new Gson().toJson(uploadInfo.getVideoInfo()));
            cValues.put(KEY_TASKS_READY_TO_PUBLISH, VideoStatus.READY_TO_PUBLISH_NO);
            cValues.put(KEY_TASKS_ERROR, uploadInfo.getError());
            if (!checkIfTaskExist(uploadInfo.getId())) {
                db.insert(TABLE_TASKS, null, cValues);
            } else {
                db.update(TABLE_TASKS, cValues, KEY_TASKS_ID + " = ?", new String[]{uploadInfo.getId()});
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    public boolean checkIfTaskExist(String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_TASKS, new String[]{}, KEY_TASKS_ID + "=?", new String[]{id}, null, null, null, null);
            if (cursor.getCount() <= 0) {
                return false;
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return true;
    }


    // Get first
    public UploadInfo getTask() {
        ArrayList<UploadInfo> uploadInfos = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_TASKS, new String[]{"*"},
                    null,
                    null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0)
                while (cursor.moveToNext()) {
                    UploadInfo dataItem = new UploadInfo(
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_TYPE)),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_SOURCE)),
                            new Gson().fromJson(cursor.getString(cursor.getColumnIndex(KEY_TASKS_VIDEO_INFO)), VideoInfo.class),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_STATUS)),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_UPLOAD_KEY)),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_ERROR))
                    );
                    uploadInfos.add(dataItem);
                }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        if (uploadInfos.size() > 0)
            return uploadInfos.get(0);
        else
            return null;
    }

    // Get first
    public UploadInfo getTaskWithId(String id) {
        UploadInfo dataItem = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_HOME_ARTICLES, new String[]{"*"}, KEY_TASKS_ID + "=?", new String[]{id}, null, null, null, null);
            if (cursor.moveToNext()) {
                dataItem = new UploadInfo(
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_SOURCE)),
                        new Gson().fromJson(cursor.getString(cursor.getColumnIndex(KEY_TASKS_VIDEO_INFO)), VideoInfo.class),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_STATUS)),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_UPLOAD_KEY)),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_ERROR))
                );
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return dataItem;
    }

    // Get all record
    public ArrayList<UploadInfo> getAllTasks() {
        ArrayList<UploadInfo> uploadInfos = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_TASKS, new String[]{"*"},
                    null,
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                UploadInfo dataItem = new UploadInfo(
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_SOURCE)),
                        new Gson().fromJson(cursor.getString(cursor.getColumnIndex(KEY_TASKS_VIDEO_INFO)), VideoInfo.class),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_STATUS)),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_UPLOAD_KEY)),
                        cursor.getString(cursor.getColumnIndex(KEY_TASKS_ERROR))
                );
                uploadInfos.add(dataItem);
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return uploadInfos;
    }

    // Get all record which are ready to publis
    public ArrayList<UploadInfo> getTasksReadyToPublish() {
        ArrayList<UploadInfo> uploadInfos = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_TASKS, new String[]{"*"},
                    null,
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex(KEY_TASKS_READY_TO_PUBLISH)).equals(VideoStatus.READY_TO_PUBLISH_YES)) {
                    UploadInfo dataItem = new UploadInfo(
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_TYPE)),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_SOURCE)),
                            new Gson().fromJson(cursor.getString(cursor.getColumnIndex(KEY_TASKS_VIDEO_INFO)), VideoInfo.class),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_STATUS)),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_UPLOAD_KEY)),
                            cursor.getString(cursor.getColumnIndex(KEY_TASKS_ERROR))
                    );
                    uploadInfos.add(dataItem);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return uploadInfos;
    }

    public void addTaskKey(@NotNull String id, @NotNull String key) {
        try {
            //Get the Data Repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_TASKS_UPLOAD_KEY, key);
            if (checkIfTaskExist(id)) {
                int update = db.update(TABLE_TASKS, cValues, KEY_TASKS_ID + " = ?", new String[]{id});
                Log.d(TAG, "update = " + update);
            } else {
                Log.d(TAG, "does not exist");
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    public void setTaskReadyToPublish(@NotNull String id, @NotNull String ready) {
        try {
            //Get the Data Repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_TASKS_READY_TO_PUBLISH, ready);
            if (checkIfTaskExist(id)) {
                db.update(TABLE_TASKS, cValues, KEY_TASKS_ID + " = ?", new String[]{id});
            } else {
                Log.d(TAG, "does not exist");
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    public void setTaskStatus(@NotNull String id, @NotNull String status) {
        try {
            //Get the Data Repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_TASKS_STATUS, status);
            if (checkIfTaskExist(id)) {
                db.update(TABLE_TASKS, cValues, KEY_TASKS_ID + " = ?", new String[]{id});
            } else {
                Log.d(TAG, "does not exist");
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    public void setTaskError(@NotNull String id, @NotNull String error) {
        try {
            //Get the Data Repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cValues = new ContentValues();
            cValues.put(KEY_TASKS_ERROR, error);
            if (checkIfTaskExist(id)) {
                db.update(TABLE_TASKS, cValues, KEY_TASKS_ID + " = ?", new String[]{id});
            } else {
                Log.d(TAG, "does not exist");
            }
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
    }

    public boolean isTaskReadyToPublish(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isReady = false;
        try {
            Cursor cursor = db.query(TABLE_TASKS, new String[]{KEY_TASKS_READY_TO_PUBLISH}, KEY_TASKS_ID + "=?", new String[]{id}, null, null, null, null);
            if (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex(KEY_TASKS_READY_TO_PUBLISH)).equals(VideoStatus.READY_TO_PUBLISH_YES)) {
                    isReady = true;
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, " db = [" + e.getMessage() + "]");
        }
        return isReady;
    }

    public void deleteTask(String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_TASKS, KEY_TASKS_ID + "='" + id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAllTasks() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_TASKS, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}