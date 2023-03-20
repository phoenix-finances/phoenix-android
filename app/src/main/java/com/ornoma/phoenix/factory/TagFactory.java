package com.ornoma.phoenix.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ornoma.phoenix.core.Transaction;
import com.ornoma.phoenix.core.TransactionTag;

/**
 * Created by de76 on 5/26/17.
 */

@SuppressWarnings("SameParameterValue")
public class TagFactory {
    private MasterCache masterCache;

    public TagFactory(MasterCache masterCache){this.masterCache = masterCache;}

    private SQLiteDatabase getReadableDatabase(){return masterCache.getReadableDatabase();}
    private SQLiteDatabase getWritableDatabase(){return masterCache.getWritableDatabase();}

    public TransactionTag getTag(int id){
        String query = "select * from tags where id = ?";
        String[] selectedArgs = new String[]{String.valueOf(id)};
        return getTag(query, selectedArgs);
    }

    public void createTag(TransactionTag transactionTag){
        ContentValues contentValues = new ContentValues();
        contentValues.put("tagName", transactionTag.getName());
        contentValues.put("tagId", transactionTag.getUid());
        SQLiteDatabase db = getWritableDatabase();
        db.insert("tags", null, contentValues);
        db.close();
    }

    public int[] getAllIdArray(){return getIdArray("select id from tags", null);}
    public String[] getIdArray(Transaction transaction){
        String query = "select tagId from tagMapping where transactionId = ?";
        String[] selectedArgs = new String[]{transaction.getTransactionId()};
        return getTagIdArray(query, selectedArgs);
    }

    public int getRawId(String tagId){
        String query = "select id from tags where tagId = ?";
        String[] selectedArgs = new String[]{tagId};
        String value = readValue(query, selectedArgs, "id");
        int valueInt = 0;
        try {valueInt = Integer.parseInt(value);}
        catch (Exception e){e.printStackTrace();}
        return valueInt;
    }

    public void addMap(String transactionId, String tagId){
        ContentValues contentValues = new ContentValues();
        contentValues.put("transactionId", transactionId);
        contentValues.put("tagId", tagId);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("tagMapping", null, contentValues);
        db.close();
    }

    private String readValue(String query, String[] selectedArgs, String columnName){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query,selectedArgs);
        String value = null;
        if (cursor.moveToFirst())
            value = cursor.getString(cursor.getColumnIndex(columnName));
        cursor.close();
        db.close();
        return value;
    }

    private TransactionTag getTag(String query, String[] selectedArgs){
        TransactionTag transactionTag = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        if (cursor.moveToFirst())
            transactionTag = getTag(cursor);
        cursor.close();
        db.close();
        return transactionTag;
    }

    private TransactionTag getTag(Cursor cursor){
        String name = cursor.getString(cursor.getColumnIndex("tagName"));
        String uid = cursor.getString(cursor.getColumnIndex("tagId"));
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        TransactionTag transactionTag = new TransactionTag(name);
        transactionTag.setId(id);
        transactionTag.setUid(uid);
        return transactionTag;
    }

    private String[] getTagIdArray(String query, String[] selectedArgs){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        String[] idArray = new String[cursor.getCount()];
        int counter = 0;
        if (cursor.moveToFirst())
            do{
                String id = cursor.getString(cursor.getColumnIndex("tagId"));
                idArray[counter] = id;
                counter++;
            }
            while (cursor.moveToNext());
        cursor.close();
        db.close();
        return idArray;
    }

    private int[] getIdArray(String query, String[] selectedArgs){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        int[] idArray = new int[cursor.getCount()];
        int counter = 0;
        if (cursor.moveToFirst())
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                idArray[counter] = id;
                counter++;
            }
            while (cursor.moveToNext());
        cursor.close();
        db.close();
        return idArray;
    }
}
