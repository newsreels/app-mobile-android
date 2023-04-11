package com.ziro.bullet.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

public class WalletActivity extends BaseActivity {

    private WebView walletWebView;
    private PrefConfig mPrefConfig;
    private String walletUrl;
    private RelativeLayout progress;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setBlackStatusBar(this);
        setContentView(R.layout.activity_wallet);

        mPrefConfig = new PrefConfig(this);
        walletUrl = mPrefConfig.getWalletUrl();
        walletWebView = findViewById(R.id.wallet_webview);
        loader = findViewById(R.id.loader);
        progress = findViewById(R.id.progress);

        if (walletUrl != null && !walletUrl.equals("")) {
            setUpWebView();
        }
    }

    private void setUpWebView() {
        progress.setVisibility(View.VISIBLE);
        String theme = Constants.LIGHT.toLowerCase();
        if (mPrefConfig.getAppTheme() != null && !mPrefConfig.getAppTheme().equals("")) {
            theme = mPrefConfig.getAppTheme().toLowerCase();
        }

        Log.e("URL @@@", walletUrl);
        walletUrl = walletUrl
                + "?authorization=" + mPrefConfig.getAccessToken()
                + "&theme=" + theme
                + "&language=" + mPrefConfig.isLanguagePushedToServer();
        Log.e("URL @@@", walletUrl);

        WebSettings webSettings = walletWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
//        webSettings.setAppCacheEnabled(false);
        if (!TextUtils.isEmpty(walletUrl)) {
            walletWebView.loadUrl(walletUrl);
        }

        walletWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progress.setVisibility(View.GONE);
            }
        });

        walletWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
    }

    public static class WebAppInterface {

        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void close() {
            ((Activity) mContext).finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && walletWebView.canGoBack()) {
            walletWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}