package com.ziro.bullet.presenter;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.ziro.bullet.R;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.SignInInterface;
import com.ziro.bullet.model.SigninResponse;
import com.ziro.bullet.utills.InternetCheckHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerificationPresenter {

    private Activity activity;
    private SignInInterface signInInterface;
    private PrefConfig mPrefconfig;

    public OTPVerificationPresenter(Activity activity, SignInInterface signInInterface) {
        this.activity = activity;
        this.signInInterface = signInInterface;
        this.mPrefconfig = new PrefConfig(activity);
    }

    public void resendCode(String email) {
        try {
            signInInterface.loaderShow(true);

            if (!InternetCheckHelper.isConnected()) {
                signInInterface.error(activity.getString(R.string.internet_error));
                signInInterface.loaderShow(false);
            } else {
                Call<SigninResponse> signinResponseCall = OAuthAPIClient
                        .getInstance()
                        .getApi()
                        .resendCode(email);
                signinResponseCall.enqueue(new Callback<SigninResponse>() {
                    @Override
                    public void onResponse(Call<SigninResponse> call, Response<SigninResponse> response) {
                        signInInterface.loaderShow(false);
                        Log.e("CALLXSXS", "code : " + response.code());
                        switch (response.code()) {
                            case 200:
                                signInInterface.success(false);
                                break;
                            case 404:
                                try {
                                    if (response.errorBody() != null) {
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                        String msg = jsonObject.getString("message");
                                        signInInterface.error404(msg);
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    if (response.errorBody() != null) {
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                        String msg = jsonObject.getString("message");
                                        signInInterface.error(msg);
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }

                        }
                    }

                    @Override
                    public void onFailure(Call<SigninResponse> call, Throwable t) {
                        signInInterface.loaderShow(false);
                        signInInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            signInInterface.error(t.getMessage());
        }
    }

//    public void checkCode(String code) {
//        try {
//            signInInterface.loaderShow(true);
//
//            if (!InternetCheckHelper.isConnected()) {
//                signInInterface.error(activity.getString(R.string.internet_error));
//                signInInterface.loaderShow(false);
//            } else {
//                Call<ResponseBody> signinResponseCall = OAuthAPIClient
//                        .getInstance()
//                        .getApi()
//                        .checkCode(code);
//                signinResponseCall.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        signInInterface.loaderShow(false);
//                        Log.e("CALLXSXS", "code : " + response.code());
//                        switch (response.code()) {
//                            case 200:
//                                signInInterface.success(true);
//                                try {
//                                    if (response.errorBody() != null) {
//                                        JSONObject jsonObject = new JSONObject(response.body().string());
//                                        String msg = jsonObject.getString("message");
//                                        Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
//                                    }
//                                } catch (JSONException | IOException e) {
//                                    e.printStackTrace();
//                                }
//                                break;
//                            case 404:
//                                try {
//                                    if (response.errorBody() != null) {
//                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                                        JSONObject errors = jsonObject.getJSONObject("errors");
//                                        String msg = errors.getString("message");
//                                        signInInterface.error404(msg);
//                                    }
//                                } catch (JSONException | IOException e) {
//                                    e.printStackTrace();
//                                }
//                                break;
//                            default:
//                                try {
//                                    if (response.errorBody() != null) {
//                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                                        JSONObject errors = jsonObject.getJSONObject("errors");
//                                        String msg = errors.getString("message");
//                                        signInInterface.error(msg);
//                                    }
//                                } catch (JSONException | IOException e) {
//                                    e.printStackTrace();
//                                }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        signInInterface.loaderShow(false);
//                        signInInterface.error(t.getMessage());
//                    }
//                });
//            }
//        } catch (Exception t) {
//            signInInterface.error(t.getMessage());
//        }
//    }

    public void codeValid(String email, String code, boolean forgot) {
        try {
            signInInterface.loaderShow(true);

            if (!InternetCheckHelper.isConnected()) {
                signInInterface.error(activity.getString(R.string.internet_error));
                signInInterface.loaderShow(false);
            } else {
                Call<ResponseBody> signinResponseCall = OAuthAPIClient
                        .getInstance()
                        .getApi()
                        .codeValid(email, code, forgot);
                signinResponseCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        signInInterface.loaderShow(false);
                        Log.e("CALLXSXS", "code : " + response.code());
                        switch (response.code()) {
                            case 200:
                                try {
                                    if (response.body() != null) {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        boolean valid = jsonObject.getBoolean("valid");
                                        if (!valid) {
                                            signInInterface.error(activity.getResources().getString(R.string.code_not_valid));
                                        }
                                        signInInterface.success(valid);
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 404:
                                try {
                                    if (response.errorBody() != null) {
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                        String msg = jsonObject.getString("message");
                                        signInInterface.error404(msg);
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    if (response.errorBody() != null) {
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                        String msg = jsonObject.getString("message");
                                        signInInterface.error(msg);
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        signInInterface.loaderShow(false);
                        signInInterface.error(t.getMessage());
                    }
                });
            }
        } catch (Exception t) {
            signInInterface.error(t.getMessage());
        }
    }
}
