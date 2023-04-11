//package com.ziro.bullet.Utills;
//
//import android.app.Activity;
//import android.content.Intent;
//
//import com.ziro.bullet.auth.TokenGenerator;
//import com.ziro.bullet.Activities.SplashActivity;
//import com.ziro.bullet.data.PrefConfig;
//
//import java.io.IOException;
//
//import ca.mimic.oauth2library.OAuthResponse;
//import ca.mimic.oauth2library.OAuthResponseCallback;
//import retrofit2.Call;
//
//
//public class TokenAuthentication {
//
//    private TokenGenerator mTokenGenerator;
//    private PrefConfig mPrefConfig;
//    private Activity activity;
//
//    public TokenAuthentication(Activity activity) {
//        this.activity = activity;
//        mTokenGenerator = new TokenGenerator();
//        mPrefConfig = new PrefConfig(activity);
//    }
//
//    public <T> void authenticate(Call<T> call) {
//        android.util.Log.e("CALLXSXS", "reached here " + mTokenGenerator);
//        android.util.Log.e("CALLXSXS", "reached here " + mPrefConfig);
//        if (mTokenGenerator != null && mPrefConfig != null) {
//            android.util.Log.e("CALLXSXS", "reached here " + mPrefConfig.getRefreshToken());
//            mTokenGenerator.refreshToken(mPrefConfig.getRefreshToken(), new OAuthResponseCallback() {
//                @Override
//                public void onResponse(OAuthResponse responseAuth) {
//                    android.util.Log.e("CALLXSXS", "reached here finally : " + responseAuth.isSuccessful());
//                    if (responseAuth.isSuccessful()) {
//                        android.util.Log.e("CALLXSXS", "reached here SUCCESS");
//                        android.util.Log.e("CALLXSXS", "new access token : " + responseAuth.getAccessToken());
//                        android.util.Log.e("CALLXSXS", "new refresh token : " + responseAuth.getRefreshToken());
//                        mPrefConfig.setAccessToken(responseAuth.getAccessToken());
////                        mPrefConfig.setRefreshToken(responseAuth.getRefreshToken()); refresh token is null
////                        android.util.Log.e("CALLXSXS", "NEW ACCESS TOKEN : " + mPrefConfig.getAccessToken());
//                        call.request().newBuilder()
//                                .header("Authorization", "Bearer " + responseAuth.getAccessToken())
//                                .build();
//                        call.clone();
//                        try {
//                            call.execute();
//                        } catch (IOException e) {
//                            android.util.Log.e("CALLXSXS", " EXP : " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    } else {
//                        android.util.Log.e("CALLXSXS", "reached here FAIL");
//                        logout();
//                    }
//                }
//            });
//        } else {
//            android.util.Log.e("CALLXSXS", "reached here FAILURE BC");
//        }
//    }
//
//    private void logout() {
//        if (activity == null) {
//            return;
//        }
//        mPrefConfig.clear();
//        Intent intent = new Intent(activity, SplashActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        activity.startActivity(intent);
//    }
//}
