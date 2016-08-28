package com.contentprovider;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.Constants.AppConstants;
import com.Services.Services;
import com.Threads.ThreadA;
import com.Threads.ThreadB;
import com.getToken.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.interfaces.Responses;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hp on 8/19/2016.
 */

public class SplashScreen extends Activity implements Responses{

    Gson gson;
    private static final String ACCOUNT_TYPE = "com.contentprovider";
    public static final String ACCOUNT_NAME = "sync";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

//        (new TestClassThread()).start();
//        (new ThreadA()).start();
//        (new ThreadB()).start();


        startService(new Intent(this,AuthService.class));

        gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Services service = retrofit.create(Services.class);
        Call<Token> call = service.checkToken(getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE).getString("token",null));

        if(getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE).getString("token",null) == null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, Login.class));
                    finish();
                }
            }, AppConstants.SPLASH_TIME_OUT);

        }else{
            AppConstants.checkToken(call, SplashScreen.this);
        }

        /*if (getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE).getString("token",null) == null ) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, Login.class));
                    finish();
                }
            }, AppConstants.SPLASH_TIME_OUT);


        }else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }, AppConstants.SPLASH_TIME_OUT);

        }*/
    }

    @Override
    public void onResponse(Call<Token> call, Response<Token> response) {
        Token body = response.body();
        SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("token", body.getToken());
        editor.commit();

        if(getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE).getString("token",null) != null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }, AppConstants.SPLASH_TIME_OUT);
        }
    }

    @Override
    public void onFailure(Call<Token> call, Throwable t) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SplashScreen.this, "Your token has expired. \nLogin again to continue", Toast.LENGTH_LONG).show();
                startActivity(new Intent(SplashScreen.this, Login.class));
                finish();
            }
        }, AppConstants.SPLASH_TIME_OUT);
    }

}
