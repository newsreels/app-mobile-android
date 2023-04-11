package com.ziro.bullet.auth;

import com.ziro.bullet.data.models.BaseModel;
import com.ziro.bullet.model.language.LanguageResponse;
import com.ziro.bullet.model.Profile.UpdateResponse;
import com.ziro.bullet.model.Profile.UsernameCheckResponse;
import com.ziro.bullet.model.SigninResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface OAuthApiInterface {

    @FormUrlEncoded
    @PATCH("/auth/account-setpassword")
    Call<ResponseBody> register(@Field("email") String email, @Field("password") String password, @Field("code") String code, @Field("forgot") boolean forgot, @Field("termsandcondition") boolean termsandcondition);

    @FormUrlEncoded
    @POST("/auth/forgot-password")
    Call<ResponseBody> forgotPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("auth/verify")
    Call<SigninResponse> checkEmail(@Field("email") String email);

    @FormUrlEncoded
    @POST("auth/register")
    Call<ResponseBody> registerUser(@Field("email") String email, @Field("password") String password, @Field("termsandcondition") boolean termsandcondition);

    @FormUrlEncoded
    @POST("auth/resend-account-setpassword")
    Call<SigninResponse> resendCode(@Field("email") String email);

    @FormUrlEncoded
    @POST("auth/code/valid")
    Call<ResponseBody> codeValid(@Field("email") String email, @Field("code") String code, @Field("forgot") boolean forgot);

    @Multipart
    @PATCH("auth/update-profile")
    Call<UpdateResponse> updateProfile(
            @Header("Authorization") String token,
            @Part("username") RequestBody username,
            @Part("name") RequestBody firstName,
            @Part("mobile_number") RequestBody mobileNumber,
            @Part MultipartBody.Part profileImage,
            @Part MultipartBody.Part coverImage
    );

    @FormUrlEncoded
    @POST("/auth/logout")
    Call<ResponseBody> logout(@Header("Authorization") String token, @Field("token") String refreshToken);

//    @FormUrlEncoded
//    @PATCH("/auth/update-profile/language")
//    Call<LanguageResponse> updateLanguage(@Header("Authorization") String token, @Field("language") String id);

    @FormUrlEncoded
    @PATCH("news/languages")
    Call<LanguageResponse> updateLanguage(@Header("Authorization") String token, @Field("language") String id,
                                          @Field("tag") String tag);

    @FormUrlEncoded
    @PATCH("news/regions")
    Call<LanguageResponse> updateUserRegion(
            @Header("Authorization") String token,
            @Field("region") String regionId
    );

    @FormUrlEncoded
    @POST("/auth/change/password")
    Call<ResponseBody> changePassword(@Header("Authorization") String token, @Field("old_password") String oldPassword, @Field("password") String password);


    //change email
    @FormUrlEncoded
    @POST("/auth/change/email")
    Call<ResponseBody> changeEmail(@Header("Authorization") String token, @Field("email") String email, @Field("password") String password);


    @FormUrlEncoded
    @POST("auth/accounts/link")
    Call<ResponseBody> linkAccount(@Header("Authorization") String token, @Field("link_with") String linkWith);

    @FormUrlEncoded
    @POST("/auth/username")
    Call<UsernameCheckResponse> checkUsername(@Field("username") String username);

    @DELETE("auth/user")
    Call<BaseModel> deleteAccount(@Header("Authorization") String token);
}
