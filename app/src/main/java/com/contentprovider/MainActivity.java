package com.contentprovider;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    SqliteHelper sqliteHelper;
    MyProvider mMyProvider;
    EditText col1_value, col2_value, col1_value_update, col2_value_update, where_update, et_delete_id;
    Button insert_in_db, update_in_db, btn_delete_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        col1_value = (EditText) findViewById(R.id.col1_value);
        col2_value = (EditText) findViewById(R.id.col2_value);
        col1_value_update = (EditText) findViewById(R.id.col1_value_update);
        col2_value_update = (EditText) findViewById(R.id.col2_value_update);
        et_delete_id  = (EditText) findViewById(R.id.et_delete_id);
        where_update = (EditText) findViewById(R.id.where_update);
        insert_in_db = (Button) findViewById(R.id.insert_in_db);
        update_in_db = (Button) findViewById(R.id.update_in_db);
        btn_delete_id = (Button) findViewById(R.id.btn_delete_id);

        startService(new Intent(MainActivity.this, MyService.class));

        System.out.println();

        sqliteHelper = new SqliteHelper(this);

        mMyProvider = new MyProvider();
        final String url = mMyProvider.CONTENT_URI_TABLE1.toString();

        insert_in_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("column1",col1_value.getText().toString());
                mContentValues.put("column2",col2_value.getText().toString());
                Uri mUri = getContentResolver().insert(Uri.parse(url),mContentValues);

                Cursor c = getContentResolver().query(mUri,null,null,null,null);

                if (c.moveToFirst()) {
                    do{
                        Toast.makeText(getApplicationContext(), c.getString(0) + ", " +  c.getString(1) + ", " + c.getString(2), Toast.LENGTH_SHORT).show();
                        et_delete_id.setText(c.getString(0));
                    } while (c.moveToNext());
                }
            }
        });

        update_in_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues mContentValues = new ContentValues();
                mContentValues.put("column1",col1_value_update.getText().toString());
                mContentValues.put("column2",col2_value_update.getText().toString());
                try {
                    int updated_id = getContentResolver().update(Uri.parse(url + "/updated/"), mContentValues, where_update.getText().toString(), null);
                }catch (Exception ex){
                    Toast.makeText(MainActivity.this, "Could not update", Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
                CheckUpdateTableValues();
                System.out.println();
            }
        });

        btn_delete_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentResolver().delete(Uri.parse(url+"/deleted/"), et_delete_id.getText().toString(), null);
                System.out.println();
            }
        });

        Cursor cursor = sqliteHelper.getAllInUpdateTable();
        if(cursor.moveToFirst()){
            do {

                String str = cursor.getString(1);
                System.out.println();

                Cursor mCursor = getContentResolver().query(Uri.parse(cursor.getString(1)),null,null,null,null);
                if(mCursor.moveToFirst()){
                    do {
                        Uri uri = Uri.parse(cursor.getString(1));
                        String table_name = uri.getPathSegments().get(0);
                        String crud_method = uri.getPathSegments().get(1);
                        String primary_key = uri.getPathSegments().get(2);


                        Log.e("primary_keys_in_update",mCursor.getString(0));
                    }while (mCursor.moveToNext());
                }
            }while (cursor.moveToNext());
        }

    }

    public void CheckUpdateTableValues(){
        Cursor cursor = sqliteHelper.getAllInUpdateTable();
        if(cursor.moveToFirst()){
            do {

                String str = cursor.getString(1);
                System.out.println();

                Cursor mCursor = getContentResolver().query(Uri.parse(cursor.getString(1)),null,null,null,null);
                if(mCursor.moveToFirst()){
                    do {
                        Uri uri = Uri.parse(cursor.getString(1));
                        String table_name = uri.getPathSegments().get(0);
                        String crud_method = uri.getPathSegments().get(1);
                        String primary_key = uri.getPathSegments().get(2);


                        Log.e("primary_keys_in_update",mCursor.getString(0));
                        Log.e("col1_value",mCursor.getString(1));
                        Log.e("col2_value",mCursor.getString(2));
                    }while (mCursor.moveToNext());
                }
            }while (cursor.moveToNext());
        }

        /*final String ACCOUNT_TYPE = "com.eutechpro.syncadapterexample";
        final String ACCOUNT      = "dummyaccount";


        AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);

        accountManager.addAccountExplicitly(newAccount, null, null);*/

    }
}
