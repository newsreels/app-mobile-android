package com.ziro.bullet.activities;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

public class WebViewActivity extends BaseActivity {
    private RelativeLayout header_back;
    private TextView tvTitle, tvSubheader;
    private WebView newsfeed_webview;
    private String newsFeed_link;
    private PrefConfig prefConfig;
    private String type;
    private RelativeLayout mLoader;
    private boolean isLoad = false;
    private boolean isSSLError;
    private LinearProgressIndicator linearProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_webview);
        prefConfig = new PrefConfig(this);

        newsFeed_link = BuildConfig.HelpUrl;
        header_back = findViewById(R.id.header_back);
        tvTitle = findViewById(R.id.header_text);
        tvSubheader = findViewById(R.id.subheader_text);
        newsfeed_webview = findViewById(R.id.newsfeed_webview);
        mLoader = findViewById(R.id.loader);
        linearProgressIndicator = findViewById(R.id.linearPb);

        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(title)) {
            if (title.equalsIgnoreCase(getString(R.string.terms_conditions))) {
                AnalyticsEvents.INSTANCE.logEvent(this,
                        Events.TERMS_CLICK);
            } else if (title.equalsIgnoreCase(getString(R.string.privacy_policy))) {
                AnalyticsEvents.INSTANCE.logEvent(this,
                        Events.POLICY_CLICK);
            }
            tvTitle.setText(title);
        }
//        if (BuildConfig.DEBUG) {
//            if (!TextUtils.isEmpty(url)) {
//                tvSubheader.setVisibility(View.GONE);
//                tvSubheader.setText(url);
//            } else {
//                tvSubheader.setVisibility(View.GONE);
//            }
//        } else {
            tvSubheader.setVisibility(View.GONE);
//        }
        newsFeed_link = url;

        WebSettings webSettings = newsfeed_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        newsfeed_webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        webSettings.setAppCacheEnabled(false);
        if (!TextUtils.isEmpty(newsFeed_link)) {
            newsfeed_webview.loadUrl(newsFeed_link);
        }

        // HEADER BACK CLICK
        header_back.setOnClickListener(view -> {
            back();
        });

        //WEBVIEW CALLBACKS
//        newsfeed_webview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//                linearProgressIndicator.setProgressCompat(newProgress, true);
//            }
//        });
        newsfeed_webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isSSLError = false;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!isSSLError) {
                    newsfeed_webview.setVisibility(View.VISIBLE);
                    linearProgressIndicator.setVisibility(View.GONE);
                    mLoader.setVisibility(View.GONE);
                    isSSLError = false;
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                if (!isLoad) {
                    isSSLError = true;
                    String url = failingUrl;
                    if (url.startsWith("http://")) {
                        try {
                            //change protocol of url string
                            url = url.replace("http://", "https://");

                            view.loadUrl(url);
                        } catch (Exception e) {
                            Log.e("ErrorWebView", "e : " + e.getMessage());
                        }
                    } else if (url.startsWith("https://")) {
                        try {
                            //change protocol of url string
                            url = url.replace("https://", "http://");

                            view.loadUrl(url);
                        } catch (Exception e) {
                            Log.e("ErrorWebView", "e : " + e.getMessage());
                        }
                    }
                    isLoad = true;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);

                if (!isLoad) {
                    isSSLError = true;
                    String url = newsFeed_link;
                    if (url.startsWith("http://")) {
                        try {
                            //change protocol of url string
                            url = url.replace("http://", "https://");

                            view.loadUrl(url);
                        } catch (Exception e) {
                            Log.e("ErrorWebView", "e : " + e.getMessage());
                        }
                    } else if (url.startsWith("https://")) {
                        try {
                            //change protocol of url string
                            url = url.replace("https://", "http://");

                            view.loadUrl(url);
                        } catch (Exception e) {
                            Log.e("ErrorWebView", "e : " + e.getMessage());
                        }
                    }
                    isLoad = true;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
//        if (newsfeed_webview.canGoBack()) {
//            newsfeed_webview.goBack();
//        } else {
        newsfeed_webview.destroy();
        super.onBackPressed();
//            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

//        }
    }
}
