package com.ornoma.phoenix.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ornoma.phoenix.core.Ledger;

/**
 * Created by de76 on 5/4/17.
 */

public class LedgerFactory {
    private MasterCache masterCache;

    public void setMasterCache(MasterCache masterCache){this.masterCache = masterCache;}
    private SQLiteDatabase getReadableDatabase(){return masterCache.getReadableDatabase();}
    private SQLiteDatabase getWritableDatabase(){return masterCache.getWritableDatabase();}

    public Ledger getLedger(String query, String[] selectedArgs){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        Ledger ledger = getLedger(cursor);
        cursor.close();
        db.close();
        return ledger;
    }

    public String getValue(String query, String[] selectedArgs, String columnName){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        String result = null;
        if (cursor.moveToFirst())
            result = cursor.getString(cursor.getColumnIndex(columnName));
        cursor.close();
        db.close();
        return result;
    }

    private Ledger getLedger(Cursor cursor){
        Ledger ledger = null;
        if (cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int id = cursor.getInt(cursor.getColumnIndex("ledgerId"));
            int parent = cursor.getInt(cursor.getColumnIndex("parent"));
            ledger = new Ledger();
            ledger.setName(name);
            ledger.setId(id);
            ledger.setParent(parent);
        }
        return ledger;
    }

    public synchronized int[] getIdArray(int parent){
        String query = "select ledgerId from masterLedger where parent = ?";
        String[] selectedArgs = new String[]{String.valueOf(parent)};
        return getIdArray(query, selectedArgs);
    }

    public synchronized int[] getIdArray(String query, String[] selectedArgs, String columnName){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        int[] result = new int[cursor.getCount()];
        int counter = 0;
        if (cursor.moveToFirst())
            do{
                int id = cursor.getInt(cursor.getColumnIndex(columnName));
                result[counter] = id;
                counter++;
            }
            while (cursor.moveToNext());
        cursor.close();
        db.close();
        return result;
    }

    private synchronized int[] getIdArray(String query, String[] selectedArgs){
        return getIdArray(query, selectedArgs, "ledgerId");
    }

    public void add(Ledger ledger){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", ledger.getName());
        contentValues.put("ledgerId", ledger.getId());
        contentValues.put("parent", ledger.getParent());
        SQLiteDatabase db = getWritableDatabase();
        db.insert("masterLedger", null, contentValues);
        db.close();
    }

}
