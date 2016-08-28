package com.contentprovider;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hp on 8/23/2016.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity implements Responses {

    EditText et_user_name,et_password;
    Button save_credentials;
    Gson gson;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        progress = new ProgressDialog(this);

        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_password = (EditText) findViewById(R.id.et_password);

        save_credentials = (Button) findViewById(R.id.btn_Login);
        gson = new GsonBuilder().create();

        save_credentials.setText("Save");

        save_credentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Post credentials from server and on response save token in SP
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(AppConstants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                Services service = retrofit.create(Services.class);
                Call<Token> call = service.getToken(et_user_name.getText().toString() , et_password.getText().toString());
                progress.show();
                AppConstants.getToken(call , AuthenticatorActivity.this );
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

//            startActivity(new Intent(Login.this , MainActivity.class));


//            boolean success = accountManager.addAccountExplicitly(account,"password",null);

            progress.dismiss();

            final Account account = new Account(et_user_name.getText().toString(), "com.contentprovider");
            AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);
            boolean success = accountManager.addAccountExplicitly(account,"password",null);


            finish();
        }
    }

    @Override
    public void onFailure(Call<Token> call, Throwable t) {
        System.out.println();
        Toast.makeText(AuthenticatorActivity.this, "Invalid credentials " + t, Toast.LENGTH_SHORT).show();
    }
}
