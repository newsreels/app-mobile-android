package com.ziro.bullet.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.SplashActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionWidget extends AppWidgetProvider {

    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";
    private static final String TAG = CollectionWidget.class.getSimpleName();
    private static final String LEFT_BUTTON_CLICK = "leftButtonClick";
    private static final String RIGH_BUTTON_CLICK = "righButtonClick";
    private static final String CONTAINER_CLICK = "container_click";
    private static final String MORE_CLICK = "more_click";
//    private static final String LOGIN_CLICK = "login_click";

    private static final String API_RESULT_OK = "api_result_ok";
    private int corner_radius = 40;

    private PrefConfig mpref;
    private String mSelectedArticleId = "";
    private RemoteViews remoteViews;


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
        Log.d(TAG, "onReceive: " + intent.getAction());

        if (LEFT_BUTTON_CLICK.equals(intent.getAction())) {

            //left click
            Log.d(TAG, "onReceive: lefttttt");
            if (mpref == null)
                mpref = new PrefConfig(context);


            ArticleResponse body = new Gson().fromJson(mpref.getWidgetArticles(), ArticleResponse.class);

            if (body != null && body.getArticles() != null && body.getArticles().size() > 0) {
                if (mpref.getWidgetCurrentIndex() <= 0)
                    mpref.setWidgetCurrentIndex(body.getArticles().size() - 1);
                else
                    mpref.setWidgetCurrentIndex(mpref.getWidgetCurrentIndex() - 1);

                int index = mpref.getWidgetCurrentIndex();
                Log.d(TAG, "onReceive: index = " + index);

                if (index > -1 && index < body.getArticles().size()) {
                    String title, imageUrl, source, time;
                    title = body.getArticles().get(index).getTitle();
                    imageUrl = body.getArticles().get(index).getImage();
                    source = body.getArticles().get(index).getSource().getName();
                    time = body.getArticles().get(index).getPublishTime();


                    // Reaches the remoteViews on widget and displays the number
//                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
                    remoteViews.setTextViewText(R.id.title, title);
                    remoteViews.setTextViewText(R.id.source, source + " \u2022 " + Utils.getTimeAgo(Utils.getDate(time), context));

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    // Get all ids
                    ComponentName thisWidget = new ComponentName(context,
                            CollectionWidget.class);
                    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                    if (allWidgetIds.length <= 0) {
                        return;
                    }
                    AppWidgetTarget awt = new AppWidgetTarget(context, R.id.image, remoteViews, allWidgetIds) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                        }
                    };

//                    RequestOptions options = new RequestOptions()
//                            .override(300, 300)
//                            .placeholder(R.drawable.error_image)
//                            .error(R.drawable.error_image)
//                            .circleCrop();

                    RequestOptions options = new RequestOptions();
                    options.override(300, 300);
                    options.placeholder(R.drawable.error_image);
                    options.error(R.drawable.error_image);
                    options.transforms(new CenterCrop(), new RoundedCorners(corner_radius));

                    Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .apply(options)
                            .into(awt);


                    mSelectedArticleId = body.getArticles().get(index).getId();

                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, mSelectedArticleId);
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.WIDGET_PREVIOUS_CLICK);
                }
            }

            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.title);
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.source);
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.image);
        }

        if (RIGH_BUTTON_CLICK.equals(intent.getAction())) {
            //right click
            if (mpref == null)
                mpref = new PrefConfig(context);


            ArticleResponse body = new Gson().fromJson(mpref.getWidgetArticles(), ArticleResponse.class);

            if (body != null && body.getArticles() != null && body.getArticles().size() > 0) {

                if (mpref.getWidgetCurrentIndex() >= body.getArticles().size() - 1)
                    mpref.setWidgetCurrentIndex(0);
                else
                    mpref.setWidgetCurrentIndex(mpref.getWidgetCurrentIndex() + 1);

                int index = mpref.getWidgetCurrentIndex();

                if (index > -1 && index < body.getArticles().size()) {

                    Log.d(TAG, "onReceive: index = " + index);

                    String title, imageUrl, source, time;
                    title = body.getArticles().get(index).getTitle();
                    imageUrl = body.getArticles().get(index).getImage();
                    source = body.getArticles().get(index).getSource().getName();
                    time = body.getArticles().get(index).getPublishTime();


                    // Reaches the remoteViews on widget and displays the number
//                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
                    remoteViews.setTextViewText(R.id.title, title);
                    remoteViews.setTextViewText(R.id.source, source + " \u2022 " + Utils.getTimeAgo(Utils.getDate(time), context));

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    // Get all ids
                    ComponentName thisWidget = new ComponentName(context,
                            CollectionWidget.class);
                    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                    if (allWidgetIds.length <= 0) {
                        return;
                    }
                    AppWidgetTarget awt = new AppWidgetTarget(context, R.id.image, remoteViews, allWidgetIds) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                        }
                    };

                    RequestOptions options = new RequestOptions();
                    options.override(300, 300);
                    options.placeholder(R.drawable.error_image);
                    options.error(R.drawable.error_image);
                    options.transforms(new CenterCrop(), new RoundedCorners(corner_radius));

                    Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .apply(options)
                            .into(awt);

                    mSelectedArticleId = body.getArticles().get(index).getId();

                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.ARTICLE_ID, mSelectedArticleId);
                    AnalyticsEvents.INSTANCE.logEvent(context,
                            params,
                            Events.WIDGET_NEXT_CLICK);
                }
            }

            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.title);
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.source);
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.image);
        }

        if (CONTAINER_CLICK.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: CONTAINER_CLICK");

//            Intent openIntent = new Intent(context, SplashActivity.class);
//
//            if (mpref == null)
//                mpref = new PrefConfig(context);
//
//            ArticleResponse body = new Gson().fromJson(mpref.getWidgetArticles(), ArticleResponse.class);
//
//            if (body != null) {
//                Log.d(TAG, "onReceive: " + body);
//                Log.d(TAG, "onReceive: " + body.getArticles());
//                Log.d(TAG, "onReceive: " + body.getArticles().get(mpref.getWidgetCurrentIndex()));
//                Log.d(TAG, "onReceive: " + body.getArticles().get(mpref.getWidgetCurrentIndex()).getId());
//                openIntent.putExtra("type", "widget_article");
//                mSelectedArticleId = body.getArticles().get(mpref.getWidgetCurrentIndex()).getId();
//                openIntent.putExtra("article_id", mSelectedArticleId);
//            }
//            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(openIntent);
            Intent openIntent = new Intent(context, SplashActivity.class);
            openIntent.putExtra("type", "widget_topic");


            if (mpref == null)
                mpref = new PrefConfig(context);

            ArticleResponse body = new Gson().fromJson(mpref.getWidgetArticles(), ArticleResponse.class);


            if (body != null &&
                    body.getArticles() != null &&
                    mpref.getWidgetCurrentIndex() < body.getArticles().size() &&
                    body.getArticles().get(mpref.getWidgetCurrentIndex()) != null &&
                    body.getArticles().get(mpref.getWidgetCurrentIndex()).getSource() != null
            ) {
                openIntent.putExtra("type", "widget_article");
                openIntent.putExtra("source_id", body.getArticles().get(mpref.getWidgetCurrentIndex()).getSource().getId());
                openIntent.putExtra("source_name", body.getArticles().get(mpref.getWidgetCurrentIndex()).getSource().getName());
                openIntent.putExtra("article_id", body.getArticles().get(mpref.getWidgetCurrentIndex()).getId());

                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID, body.getArticles().get(mpref.getWidgetCurrentIndex()).getId());
                AnalyticsEvents.INSTANCE.logEvent(context,
                        params,
                        Events.WIDGET_OPEN);
            }

            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(openIntent);
        }

//        if (LOGIN_CLICK.equals(intent.getAction())) {
//            Intent openIntent = new Intent(context, SplashActivity.class);
//            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(openIntent);
//        }

        if (MORE_CLICK.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: MORE_CLICK");
            Map<String, String> params = new HashMap<>();
            AnalyticsEvents.INSTANCE.logEvent(context,
                    params,
                    Events.WIDGET_OPEN);
            Intent openIntent = new Intent(context, SplashActivity.class);
            openIntent.putExtra("type", "widget_topic");


            if (mpref == null)
                mpref = new PrefConfig(context);

            ArticleResponse body = new Gson().fromJson(mpref.getWidgetArticles(), ArticleResponse.class);

            if (body != null &&
                    body.getArticles() != null &&
                    mpref.getWidgetCurrentIndex() < body.getArticles().size() &&
                    body.getArticles().get(mpref.getWidgetCurrentIndex()) != null &&
                    body.getArticles().get(mpref.getWidgetCurrentIndex()).getSource() != null
            ) {
                openIntent.putExtra("type", "widget_topic");
                openIntent.putExtra("source_id", body.getArticles().get(mpref.getWidgetCurrentIndex()).getSource().getId());
                openIntent.putExtra("source_name", body.getArticles().get(mpref.getWidgetCurrentIndex()).getSource().getName());
                openIntent.putExtra("article_id", body.getArticles().get(mpref.getWidgetCurrentIndex()).getId());

                if (body.getArticles().get(mpref.getWidgetCurrentIndex()).getTopics() != null &&
                        body.getArticles().get(mpref.getWidgetCurrentIndex()).getTopics().size() > 0) {
                    openIntent.putExtra("topic_id", body.getArticles().get(mpref.getWidgetCurrentIndex()).getTopics().get(0).getId());
                    openIntent.putExtra("topic_name", body.getArticles().get(mpref.getWidgetCurrentIndex()).getTopics().get(0).getName());
                }
            }

            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(openIntent);
            //            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, 0);
//            remoteViews.setOnClickPendingIntent(R.id.container, pendingIntent);
        }

        if ("update_widget".equals(intent.getAction())) {
            Log.d(TAG, "onReceive: ==================update_widget");

            getArticles(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context,
                    CollectionWidget.class)), remoteViews);
        }

        if (ACTION_AUTO_UPDATE.equals(intent.getAction())) {
            getArticles(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context,
                    CollectionWidget.class)), remoteViews);
        }

//        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd_HH-mm-ss", Locale.getDefault());
//            String currentDateandTime = sdf.format(new Date());
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
//            remoteViews.setTextViewText(R.id.btnTopcic, currentDateandTime);
//            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
//            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.btnTopcic);
//        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: ");

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
        ComponentName watchWidget = new ComponentName(context, CollectionWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.ll_left,
                getPendingSelfIntent(context, LEFT_BUTTON_CLICK));

        remoteViews.setOnClickPendingIntent(R.id.ll_right,
                getPendingSelfIntent(context, RIGH_BUTTON_CLICK));

        remoteViews.setOnClickPendingIntent(R.id.container,
                getPendingSelfIntent(context, CONTAINER_CLICK));

        remoteViews.setOnClickPendingIntent(R.id.btnTopcic,
                getPendingSelfIntent(context, MORE_CLICK));

        remoteViews.setOnClickPendingIntent(R.id.tvLogin,
                getPendingSelfIntent(context, MORE_CLICK));

//        Intent login = new Intent(context, CollectionWidget.class);
//        login.setAction(MORE_CLICK);
//        PendingIntent loginIntent = PendingIntent.getBroadcast(context, 0, login , 0);
//        remoteViews.setOnClickPendingIntent(R.id.tvLogin, loginIntent);

        appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        getArticles(context, appWidgetManager, appWidgetIds, remoteViews);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
//        PendingIntent pending;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        } else {
//            pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        }
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled: ");

        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.startAlarm();
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled: ");
        // stop alarm only if all widgets have been disabled
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(), getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);

        if (appWidgetIds.length == 0) {
            // stop alarm
            AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
            appWidgetAlarm.stopAlarm();
        }
        // Enter relevant functionality for when the last widget is disabled
    }


    private void getArticles(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, RemoteViews remoteViews) {
        if (mpref == null)
            mpref = new PrefConfig(context);


        Log.d(TAG, "getArticles: token = " + mpref.getAccessToken());

        if (TextUtils.isEmpty(mpref.getAccessToken())) {
            Log.d(TAG, "getArticles: token = " + mpref.getAccessToken());
            remoteViews.setViewVisibility(R.id.progress, View.GONE);
            remoteViews.setViewVisibility(R.id.tvLogin, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.container, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.ll_button, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.btnTopcic, View.INVISIBLE);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            return;
        }
        Call<ArticleResponse> call = ApiClient
                .getInstance()
                .getApi()
                .getPaginatedNews("Bearer " + mpref.getAccessToken(), mpref.isReaderMode(), "", "");
        call.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(@NotNull Call<ArticleResponse> call, @NotNull Response<ArticleResponse> response) {
                if (response.code() == 200) {
                    ArticleResponse body = response.body();
                    if (body != null) {
                        mpref.setWidgetArticles(new Gson().toJson(body));
                        mpref.setWidgetCurrentIndex(0);

                        updateAppWidget(context, appWidgetManager, appWidgetIds, remoteViews);
                    }
                } else {
                    Log.d(TAG, "response: code = " + response.code());
                    remoteViews.setViewVisibility(R.id.progress, View.GONE);
                    remoteViews.setViewVisibility(R.id.tvLogin, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.container, View.INVISIBLE);
                    remoteViews.setViewVisibility(R.id.ll_button, View.INVISIBLE);
                    remoteViews.setViewVisibility(R.id.btnTopcic, View.INVISIBLE);
                    appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
                }

            }

            @Override
            public void onFailure(@NotNull Call<ArticleResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "getArticles: onfailure = " + t.getLocalizedMessage());
//                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
//                remoteViews.setViewVisibility(R.id.progress, View.GONE);
//                remoteViews.setViewVisibility(R.id.tvLogin, View.VISIBLE);
//                remoteViews.setViewVisibility(R.id.container, View.INVISIBLE);
//                remoteViews.setViewVisibility(R.id.ll_button, View.INVISIBLE);
//                remoteViews.setViewVisibility(R.id.btnTopcic, View.INVISIBLE);
//                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            }
        });
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetId, RemoteViews remoteViews) {
        if (mpref == null)
            mpref = new PrefConfig(context);

        Log.d(TAG, "updateAppWidget: articles ====");

        ArticleResponse body = new Gson().fromJson(mpref.getWidgetArticles(), ArticleResponse.class);


        int index = mpref.getWidgetCurrentIndex();

        String title, imageUrl, source, time;
        if (body != null && body.getArticles().size() > 0 && index < body.getArticles().size()) {

            title = body.getArticles().get(index).getTitle();
            imageUrl = body.getArticles().get(index).getImage();
            source = body.getArticles().get(index).getSource().getName();
            time = body.getArticles().get(index).getPublishTime();
            // Reaches the remoteViews on widget and displays the number
            remoteViews.setTextViewText(R.id.title, title);

            remoteViews.setTextViewText(R.id.source, source + " \u2022 " + Utils.getTimeAgo(Utils.getDate(time), context));

            remoteViews.setViewVisibility(R.id.progress, View.GONE);
            remoteViews.setViewVisibility(R.id.tvLogin, View.GONE);
            remoteViews.setViewVisibility(R.id.container, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.ll_button, View.VISIBLE);

            if (body != null &&
                    body.getArticles() != null &&
                    mpref.getWidgetCurrentIndex() < body.getArticles().size() &&
                    body.getArticles().get(mpref.getWidgetCurrentIndex()) != null &&
                    body.getArticles().get(mpref.getWidgetCurrentIndex()).getSource() != null &&
                    body.getArticles().get(mpref.getWidgetCurrentIndex()).getTopics() != null &&
                    body.getArticles().get(mpref.getWidgetCurrentIndex()).getTopics().size() > 0
            ) {
                remoteViews.setViewVisibility(R.id.btnTopcic, View.VISIBLE);
            } else {
                remoteViews.setViewVisibility(R.id.btnTopcic, View.GONE);
            }

            remoteViews.setOnClickPendingIntent(R.id.ll_left,
                    getPendingSelfIntent(context, LEFT_BUTTON_CLICK));

            remoteViews.setOnClickPendingIntent(R.id.ll_right,
                    getPendingSelfIntent(context, RIGH_BUTTON_CLICK));

            remoteViews.setOnClickPendingIntent(R.id.container,
                    getPendingSelfIntent(context, CONTAINER_CLICK));

            remoteViews.setOnClickPendingIntent(R.id.btnTopcic,
                    getPendingSelfIntent(context, MORE_CLICK));

            remoteViews.setOnClickPendingIntent(R.id.tvLogin,
                    getPendingSelfIntent(context, MORE_CLICK));

//            Intent login = new Intent(context, CollectionWidget.class);
//            login.setAction(MORE_CLICK);
//            PendingIntent loginIntent = PendingIntent.getBroadcast(context, 0, login , 0);
//            remoteViews.setOnClickPendingIntent(R.id.tvLogin, loginIntent);

            // Get all ids
            ComponentName thisWidget = new ComponentName(context,
                    CollectionWidget.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            if (allWidgetIds.length <= 0) {
                return;
            }
            AppWidgetTarget awt = new AppWidgetTarget(context, R.id.image, remoteViews, allWidgetIds) {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    super.onResourceReady(resource, transition);
                }
            };

            RequestOptions options = new RequestOptions();
            options.override(300, 300);
            options.placeholder(R.drawable.error_image);
            options.error(R.drawable.error_image);
            options.transforms(new CenterCrop(), new RoundedCorners(corner_radius));


            Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .apply(options)
                    .into(awt);


            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}

