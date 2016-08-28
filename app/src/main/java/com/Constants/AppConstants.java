package com.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.Services.Services;
import com.contentprovider.MainActivity;
import com.getToken.Token;
import com.google.gson.Gson;
import com.interfaces.Responses;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hp on 8/19/2016.
 */
public class AppConstants {
    public static String BASE_URL = "http://192.168.0.100/Auth/";
    public static String MyPREFERENCES = "hr_tih_preferences";
    public static int SPLASH_TIME_OUT = 3000;

    public static void checkToken(Call call, final Responses responses) { //every time hit to server

        call.enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                responses.onResponse(call,  response);
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                responses.onFailure(call,t);
            }
        });
    }


    public static void getToken(Call call, final Responses responses) { //every time hit to server

        call.enqueue(new Callback<Token>() {


            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                responses.onResponse(call,  response);
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                responses.onFailure(call,t);
            }
        });
    }


    public static String md5Encrypt(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
