package com.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Shahbaz on 8/14/2016.
 */
public class MyProvider extends ContentProvider {

    SqliteHelper sqliteHelper;
    ContentObserver mContentObserver;

    private static final String PROVIDER_NAME = "content.provider.testing";
    public static final Uri CONTENT_URI_TABLE1 = Uri.parse("content://" + PROVIDER_NAME + "/table1");


    private static final int TABLE1_URI_IDENTIFIER = 1;
    private static final int ALL_RECORDS = 2;
    private static final int TABLE1_INSERTED_URI_IDENTIFIER = 3;
    private static final int TABLE1_UPDATED_URI_IDENTIFIER = 4;
    private static final int TABLE1_UPDATED_VALUE_URI_IDENTIFIER = 5;
    private static final int TABLE1_DELETED_URI_IDENTIFIER = 6;
    private static final int TABLE1_DELETED_VALUE_URI_IDENTIFIER = 7;


    private static final UriMatcher uriMatcher = getUriMatcher();

    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, SqliteHelper.TABLE_NAME, TABLE1_URI_IDENTIFIER);
        uriMatcher.addURI(PROVIDER_NAME, SqliteHelper.TABLE_NAME+"/inserted/#", TABLE1_INSERTED_URI_IDENTIFIER);
        uriMatcher.addURI(PROVIDER_NAME, SqliteHelper.TABLE_NAME+"/updated/", TABLE1_UPDATED_URI_IDENTIFIER);
        uriMatcher.addURI(PROVIDER_NAME, SqliteHelper.TABLE_NAME+"/updated/#", TABLE1_UPDATED_VALUE_URI_IDENTIFIER);
        uriMatcher.addURI(PROVIDER_NAME, SqliteHelper.TABLE_NAME+"/deleted/", TABLE1_DELETED_URI_IDENTIFIER);
        uriMatcher.addURI(PROVIDER_NAME, SqliteHelper.TABLE_NAME+"/deleted/#", TABLE1_DELETED_VALUE_URI_IDENTIFIER);
        uriMatcher.addURI(PROVIDER_NAME, "images/#", ALL_RECORDS);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        sqliteHelper = new SqliteHelper(getContext());
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();

        // creates the handler using the passed looper
        Handler handler = new Handler(thread.getLooper());

        mContentObserver = new MyContentObserver(handler,getContext());
        return true;
    }


    public void Insert_in_update_table(ContentValues values){
        Cursor cursor = null;
        sqliteHelper.Insert_in_update_table(values);
        cursor = sqliteHelper.getWritableDatabase().rawQuery("select * from table1",null);
        cursor.moveToFirst();
        do {
            Log.e("values in table1", cursor.getString(0));
        }while (cursor.moveToNext());


        cursor = sqliteHelper.getWritableDatabase().rawQuery("select * from table_update",null);
        cursor.moveToFirst();
        do {
            Log.e("values in table_update", cursor.getString(1));
        }while (cursor.moveToNext());
    }


    public void DeleteFromUpdateTable(String uri){
        Uri mUri = Uri.parse(uri);
        String id = mUri.getPathSegments().get(2);
        sqliteHelper.deleteFromUpdateTable(id);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String id = null;
        switch (uriMatcher.match(uri)){
            case TABLE1_INSERTED_URI_IDENTIFIER:
                id = uri.getPathSegments().get(2);
                break;
            case TABLE1_UPDATED_VALUE_URI_IDENTIFIER:
                id = uri.getPathSegments().get(2);
                break;
            case TABLE1_DELETED_VALUE_URI_IDENTIFIER:
                id = uri.getPathSegments().get(2);
                break;
        }

        return sqliteHelper.getAllInDB(id, projection, selection, selectionArgs, sortOrder);

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case ALL_RECORDS:
                return "get_all";
            case TABLE1_URI_IDENTIFIER:
                return "get_one";
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = sqliteHelper.getWritableDatabase();
        int uriType = uriMatcher.match(uri);

        switch (uriType){
            case TABLE1_URI_IDENTIFIER:
                long rowID = sqlDB.insert(SqliteHelper.TABLE_NAME, "", values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(Uri.parse(uri.toString()+ "/inserted"), rowID);

                    getContext().getContentResolver().registerContentObserver(_uri, true, mContentObserver);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
        }

        throw new IllegalArgumentException("Unknown URI: " + uri);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        long deleted = 0;

        SQLiteDatabase sqlDB = sqliteHelper.getWritableDatabase();
        int uriType = uriMatcher.match(uri);

        switch (uriType){
            case TABLE1_DELETED_URI_IDENTIFIER:
                String where = "id=?";
                String[] whereArgs = {Integer.toString(Integer.valueOf(selection))};

                deleted = sqlDB.delete("table1", where , whereArgs);


//                Cursor cursor = sqlDB.rawQuery("update table1 set column1=?, column2=? where id=?",new String[]{values.get("column1").toString(),values.get("column2").toString(),selection});
//                int count = cursor.getCount();
                if (deleted == 1) {
                    Uri _uri = ContentUris.withAppendedId(Uri.parse(uri.toString()), Integer.valueOf(selection));

                    getContext().getContentResolver().registerContentObserver(_uri, true, mContentObserver);
                    getContext().getContentResolver().notifyChange(_uri, null);

                }else{
                    throw new IllegalArgumentException("Unknown URI: " + uri);
                }
                break;
        }

        return Integer.valueOf(selection); //returns the id of the record updated
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        long updated = 0;

        SQLiteDatabase sqlDB = sqliteHelper.getWritableDatabase();
        int uriType = uriMatcher.match(uri);

        switch (uriType){
            case TABLE1_UPDATED_URI_IDENTIFIER:
                String where = "id=?";
                String[] whereArgs = {Integer.toString(Integer.valueOf(selection))};

                updated = sqlDB.update("table1", values, where , whereArgs);


//                Cursor cursor = sqlDB.rawQuery("update table1 set column1=?, column2=? where id=?",new String[]{values.get("column1").toString(),values.get("column2").toString(),selection});
//                int count = cursor.getCount();
                if (updated == 1) {
                    Uri _uri = ContentUris.withAppendedId(Uri.parse(uri.toString()), Integer.valueOf(selection));

                    getContext().getContentResolver().registerContentObserver(_uri, true, mContentObserver);
                    getContext().getContentResolver().notifyChange(_uri, null);

                }else{
                    throw new IllegalArgumentException("Unknown URI: " + uri);
                }
                break;
        }

        return Integer.valueOf(selection); //returns the id of the record updated
    }
}
