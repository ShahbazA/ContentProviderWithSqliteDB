package com.contentprovider;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.Constants.AppConstants;
import com.Services.Services;
import com.getToken.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.interfaces.Responses;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hp on 8/15/2016.
 */
public class MyService extends Service implements Responses{
    Runnable runnable;
    private final int runTime = 5000;
    SqliteHelper sqliteHelper;
    Gson gson;
    JSONObject JSONArray_toSend;
    private boolean isRunning  = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
//        v.vibrate(5000);

        isRunning = true;

        Log.e("Services","ServiceStarted");
        Toast.makeText(MyService.this, "Service created", Toast.LENGTH_SHORT).show();
        sqliteHelper = new SqliteHelper(this);
        gson = new GsonBuilder().setLenient().create();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            final Handler handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {

                    if(isRunning) {
                        Log.e("Services", "Services Running");
                        Toast.makeText(MyService.this, "Services Running", Toast.LENGTH_SHORT).show();
                        JSONArray_toSend = getDataFromUpdateTable();
                        if (JSONArray_toSend != new JSONObject()) {

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(AppConstants.BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();

                            Services service = retrofit.create(Services.class);
                            Call<Token> call = service.checkToken(getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE).getString("token", null));

                        /*SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE);
                        if(getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE).getString("token",null) != null){
                            System.out.println(sharedpreferences.getString("token",null)); //TODO REMOVE LATER

                        }*/

                            AppConstants.checkToken(call, MyService.this);
                        }

                    }else{

                       /* Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/

                    }

                    handler.postDelayed(runnable, runTime);

                }
            };

            handler.post(runnable);


        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void postDataToServer(JSONObject jsonObject){
//        String base_url = "http://192.168.0.100/";
        JsonSent mJsonSent = new JsonSent();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mJsonSent.setJsonObject(jsonObject);

        Services service = retrofit.create(Services.class);
        Call<ResponseBody> call = service.postJson(mJsonSent.getJsonObject().toString());

        try {
            if (mJsonSent.getJsonObject().getJSONArray("jsonArray").length() > 0) {

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String res = response.body().string();
                            Toast.makeText(MyService.this, res , Toast.LENGTH_SHORT).show();
                            System.out.println();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        System.out.println();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public JSONObject getDataFromUpdateTable(){
        JSONObject jObjectMain = new JSONObject();
        JSONArray jArrayMain = new JSONArray();
        String table_name = "", crud_method = "";

        Cursor cursor = sqliteHelper.getAllInUpdateTable();
        if(cursor.moveToFirst()){
            do {

                String str = cursor.getString(1);
                System.out.println();

                Cursor mCursor = getContentResolver().query(Uri.parse(cursor.getString(1)), null, null, null, null);
                String str_uri = cursor.getString(1);
                if (mCursor.getCount() == 0 && str_uri.contains("deleted")) {

                    JSONObject jObj = new JSONObject();

                    Uri mUri = Uri.parse(str_uri);
                    table_name = mUri.getPathSegments().get(0);
                    crud_method = mUri.getPathSegments().get(1);


                    String  pk_from_table = mUri.getPathSegments().get(2);

                    try {
                        jObj.put("primary_key", pk_from_table);
                        jObj.put("table_name", table_name);
                        jObj.put("crud_method", crud_method);
                        jArrayMain.put(jObj);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }else{

                    if (mCursor.moveToFirst()) {
                        do {
                            Uri uri = Uri.parse(cursor.getString(1));
                            table_name = uri.getPathSegments().get(0);
                            crud_method = uri.getPathSegments().get(1);
                            String primary_key = uri.getPathSegments().get(2);

                            Cursor mCursor_values_from_table = getContentResolver().query(Uri.parse(cursor.getString(1)), null, null, null, null);
                            int count = mCursor_values_from_table.getCount();
                            if (count > 0) {
                                mCursor_values_from_table.moveToFirst();
                                String pk_from_table = mCursor_values_from_table.getString(0);
                                String col1_from_table = mCursor_values_from_table.getString(1);
                                String col2_from_table = mCursor_values_from_table.getString(2);
                                JSONObject jObj = new JSONObject();
                                try {
                                    jObj.put("table_name", table_name);
                                    jObj.put("primary_key", pk_from_table);
                                    jObj.put("crud_method", crud_method);
                                    jObj.put("col1_value", col1_from_table);
                                    jObj.put("col2_value", col2_from_table);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                jArrayMain.put(jObj);
                                System.out.println();
                            }
                            System.out.println();

                            Log.e("primary_keys_in_update", mCursor.getString(0));
                        } while (mCursor.moveToNext());
                    }

                }

            }while (cursor.moveToNext()) ;
        }

        if(sqliteHelper.getCountOfTable() > 0)
            sqliteHelper.deleteAllEntriesFromDb();
        try {
            jObjectMain.put("jsonArray",jArrayMain);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return jObjectMain;
    }

    @Override
    public void onResponse(Call<Token> call, Response<Token> response) {
        postDataToServer(JSONArray_toSend);// Token is still valid
    }

    @Override
    public void onFailure(Call<Token> call, Throwable t) {
        //delete token from SP
        SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        isRunning = false;
    }
}