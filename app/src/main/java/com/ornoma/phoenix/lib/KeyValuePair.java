package com.ornoma.phoenix.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by de76 on 6/3/17.
 */

public class KeyValuePair extends SQLiteOpenHelper {
    private static final String DB_NAME = "_keyValue.pair";
    private static final int DB_VERSION = 1;

    private static KeyValuePair instance = null;
    public static KeyValuePair getInstance(Context context){
        if (instance == null)
            instance = new KeyValuePair(context);
        return instance;
    }

    public KeyValuePair(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public String getValue(String key){
        String value = null;
        if (isExists(key)){
            String query = "select _value from mainTable where _key = ?";
            String[] selectedArgs = new String[]{key};
            value = getValue(query, selectedArgs, "_value");
        }
        return value;
    }

    public void update(String key, String value){
        if (!isExists(key)){
            addPair(key, value);
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("_value", value);
        SQLiteDatabase db = getWritableDatabase();
        db.update("mainTable", contentValues, " _key = ? ", new String[]{key});
        db.close();
    }

    private void addPair(String key, String value){
        ContentValues contentValues = new ContentValues();
        contentValues.put("_key", key);
        contentValues.put("_value", value);
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow("mainTable", null, contentValues);
        db.close();
    }

    private boolean isExists(String key){
        String query = "select count(*) as count from mainTable where _key = ?";
        String[] selectedArgs = new String[]{key};
        String result = getValue(query, selectedArgs, "count");
        boolean status = false;
        try {
            status = Integer.parseInt(result) != 0;
        }
        catch (Exception e){e.printStackTrace();}
        return status;
    }

    private String getValue(String query, String[] selectedArgs, String columnName){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        String result = null;
        if (cursor.moveToFirst())
            result = cursor.getString(cursor.getColumnIndex(columnName));
        cursor.close();
        db.close();
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists mainTable(" +
                "id integer not null primary key autoincrement, " +
                "_key text," +
                "_value text" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
