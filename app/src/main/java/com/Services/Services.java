package com.Services;

import com.getToken.Token;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by hp on 8/19/2016.
 */
public interface Services {

    @FormUrlEncoded
//    @Headers("Content-Type: application/json")
    @POST("/post_json.php")
    Call<ResponseBody> postJson(@Field("json_post") String body);

    @FormUrlEncoded
    @POST("getToken.php")
    Call<Token> getToken(@Field("username") String userName, @Field("password") String password);


    @FormUrlEncoded
    @POST("checkToken.php")
    Call<Token> checkToken(@Field("token") String token);
}
