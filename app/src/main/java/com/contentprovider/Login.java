package com.contentprovider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Constants.AppConstants;
import com.Services.Services;
import com.getToken.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.interfaces.Responses;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hp on 8/19/2016.
 */
public class Login extends Activity implements Responses {


    EditText et_password, et_user_name;
    Button btn_Login;
    Gson gson;
    String userName , password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        gson = new GsonBuilder().create();
        et_user_name = (EditText)findViewById(R.id.et_user_name);
        et_password = (EditText)findViewById(R.id.et_password);
        btn_Login = (Button) findViewById(R.id.btn_Login);

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = AppConstants.md5Encrypt(et_user_name.getText().toString());
                password = AppConstants.md5Encrypt(et_password.getText().toString());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(AppConstants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                Services service = retrofit.create(Services.class);
                Call<Token> call = service.getToken(userName , password);
                AppConstants.getToken(call , Login.this);

            }
        });

    }

    @Override
    public void onResponse(Call<Token> call, Response<Token> response) {
                Token body = response.body();
                SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("token", body.getToken());
                editor.commit();

                if(getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE).getString("token",null) != null){
                    System.out.println(sharedpreferences.getString("token",null)); //TODO REMOVE LATER

                    startActivity(new Intent(Login.this , MainActivity.class));
                    finish();
                }
    }

    @Override
    public void onFailure(Call<Token> call, Throwable t) {
        System.out.println();
        Toast.makeText(Login.this, "ERROR: " + t, Toast.LENGTH_SHORT).show();
    }
}
