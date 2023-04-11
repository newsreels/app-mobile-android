package com.ziro.bullet.utills;

import android.app.Application;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.util.Map;

public class AFApplication extends Application {
    private static final String AF_DEV_KEY = "ZajnjsVaGHX9SQURHUCwfV";

    @Override
    public void onCreate() {
        super.onCreate();
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {

            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
//                for (String attrName : conversionData.keySet()) {
//                    Log.d("LOG_TAG", "attribute: " + attrName + " = " + conversionData.get(attrName));
//                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d("LOG_TAG", "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
//                for (String attrName : attributionData.keySet()) {
//                    Log.d("LOG_TAG", "attribute: " + attrName + " = " + attributionData.get(attrName));
//                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d("LOG_TAG", "error onAttributionFailure : " + errorMessage);
            }
        };
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, this);
    }
}
              