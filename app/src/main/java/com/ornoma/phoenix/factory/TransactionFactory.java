package com.ornoma.phoenix.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import com.ornoma.phoenix.core.RawTransaction;
import com.ornoma.phoenix.core.Transaction;

/**
 * Created by de76 on 5/4/17.
 */

public class TransactionFactory {
    private MasterCache masterCache;

    public void setMasterCache(MasterCache masterCache){this.masterCache = masterCache;}

    public synchronized boolean isExists(Transaction transaction){
        String query = "select id from journal where transactionId = ?";
        String[] selectedArgs = new String[]{transaction.getTransactionId()};
        return getStatus(query, selectedArgs);
    }

    public synchronized boolean isExists(RawTransaction rawTransaction){
        String query = "select id from rawJournal where id = ?";
        String[] selectedArgs = new String[]{String.valueOf(rawTransaction.getRawId())};
        return getStatus(query, selectedArgs);
    }

    private synchronized boolean getStatus(String query, String[] selectedArgs){
        boolean status;
        SQLiteDatabase db = masterCache.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        status = cursor.moveToFirst();
        cursor.close();
        db.close();
        return status;
    }

    public synchronized RawTransaction getRawTransaction(int rawId){
        SQLiteDatabase db = masterCache.getReadableDatabase();
        RawTransaction rawTransaction = getRawTransaction(db, rawId);
        db.close();
        return rawTransaction;
    }

    public synchronized RawTransaction getRawTransaction(SQLiteDatabase db, int rawId){
        String query = "select * from rawJournal where id = ?";
        String[] selectedArgs = new String[]{String.valueOf(rawId)};
        Cursor cursor = db.rawQuery(query, selectedArgs);
        RawTransaction rawTransaction = null;
        if (cursor.moveToFirst())
            rawTransaction = getRawTransaction(cursor);
        cursor.close();
        return rawTransaction;
    }

    private RawTransaction getRawTransaction(Cursor cursor){
        int rawId, ledgerId, debit, credit;
        String transactionId;
        rawId = cursor.getInt(cursor.getColumnIndex("id"));
        transactionId = cursor.getString(cursor.getColumnIndex("transactionId"));
        ledgerId = cursor.getInt(cursor.getColumnIndex("ledgerId"));
        debit = cursor.getInt(cursor.getColumnIndex("debit"));
        credit = cursor.getInt(cursor.getColumnIndex("credit"));
        RawTransaction rawTransaction = new RawTransaction();
        rawTransaction.setRawId(rawId);
        rawTransaction.setTransactionId(transactionId);
        rawTransaction.setLedgerId(ledgerId);
        rawTransaction.setDebit(debit);
        rawTransaction.setCredit(credit);
        return rawTransaction;
    }

    public synchronized int[] getRawTransIdArray(int ledgerId, long startDate, long endDate){
        String query = "select id from rawJournal where transactionId in " +
                "(select transactionId from journal where date > ? and date < ? order by date desc) and ledgerId = ?";
        String[] selectedArgs = new String[]{String.valueOf(startDate), String.valueOf(endDate), String.valueOf(ledgerId)};

        int[] result =  getRawTransIdArray(query, selectedArgs);
        //Log.d("TFactory","Result Length : " + result.length);
        return result;
    }

    public synchronized int[] getRawTransIdArray(int ledgerId){
        String query = "select id from rawJournal where ledgerId = ? ";
        String[] selectedArgs = new String[]{String.valueOf(ledgerId)};
        return getRawTransIdArray(query, selectedArgs);
    }

    public synchronized int[] getRawTransIdArray(String query, String[] selectedArgs){
        SQLiteDatabase db = masterCache.getReadableDatabase();
        int[] result = getRawTransIdArray(db, query, selectedArgs);
        db.close();
        return result;
    }

    public synchronized int[] getRawTransIdArray(SQLiteDatabase db, String query, String[] selectedArgs){
        Cursor cursor = db.rawQuery(query, selectedArgs);
        int[] result = new int[cursor.getCount()];
        int counter = 0;
        if (cursor.moveToFirst())
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                result[counter] = id;
                counter++;
            }
            while (cursor.moveToNext());
        cursor.close();
        return result;
    }

    public synchronized int getRawTransId(String transactionId){
        String query = "select id from journal where transactionId = ?";
        String[] selectedArgs = new String[]{transactionId};
        String idStr = getValueFrom(query, selectedArgs, "id");
        int id = 1;
        try {id = Integer.parseInt(idStr);}
        catch (Exception e){}
        return id;
    }

    private synchronized String getValueFrom(String query, String[] selectedArgs, String columnName){
        SQLiteDatabase db = masterCache.getReadableDatabase();
        String result = "0";
        Cursor cursor = db.rawQuery(query, selectedArgs);
        if (cursor.moveToFirst())
            result = cursor.getString(cursor.getColumnIndex(columnName));
        cursor.close();
        db.close();
        return result;
    }

    public synchronized Transaction getTransactionFrom(Cursor cursor){
        int rawId = cursor.getInt(cursor.getColumnIndex("id"));
        String transactionId = cursor.getString(cursor.getColumnIndex("transactionId"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        long date = cursor.getLong(cursor.getColumnIndex("date"));
        Transaction transaction = new Transaction();
        transaction.setRawId(rawId);
        transaction.setTransactionId(transactionId);
        transaction.setDescription(description);
        transaction.setTimeInMillis(date);
        return transaction;
    }

    private synchronized void updateRawTransaction(RawTransaction rawTransaction){
        if (!isExists(rawTransaction)){
            Log.d("TF", "RawTransaction Doesn't Exist : " + rawTransaction.getRawId());
            addRawTransaction(rawTransaction);
            return;}

        ContentValues contentValues = new ContentValues();
        contentValues.put("transactionId", rawTransaction.getTransactionId());
        contentValues.put("debit", rawTransaction.getDebit());
        contentValues.put("credit", rawTransaction.getCredit());
        contentValues.put("ledgerId", rawTransaction.getLedgerId());
        SQLiteDatabase db = masterCache.getWritableDatabase();
        db.update("rawJournal", contentValues, "id = ?",
                new String[]{String.valueOf(rawTransaction.getRawId())});
        db.close();
    }

    public synchronized void updateTransaction(Transaction transaction){
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", transaction.getDescription());
        SQLiteDatabase db = masterCache.getWritableDatabase();
        db.update("journal",contentValues, "transactionId = ?",
                new String[]{transaction.getTransactionId()});
        db.close();
        for (RawTransaction rawTransaction : transaction.getRawTransactionList()){
            updateRawTransaction(rawTransaction);
        }
    }

    private synchronized void addRawTransactionsFromCursor(Transaction transaction, Cursor cursor){
        do{
            RawTransaction rawTransaction = getRawTransaction(cursor);
            transaction.addRawTransaction(rawTransaction);
        }
        while (cursor.moveToNext());
    }

    public synchronized Transaction getTransaction(String query, String[] selectedArgs){
        Transaction transaction = null;
        SQLiteDatabase db = masterCache.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        if (cursor.moveToFirst()){
            transaction = getTransactionFrom(cursor);
        }
        cursor.close();
        db.close();
        if (transaction != null){
            populateRawTransactions(transaction);
        }
        return transaction;
    }

    private synchronized void populateRawTransactions(Transaction transaction){
        String query = "select * from rawJournal where transactionId = ?";
        String[] selectedArgs = new String[]{transaction.getTransactionId()};
        SQLiteDatabase db = masterCache.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        if (cursor.moveToFirst())
            addRawTransactionsFromCursor(transaction, cursor);
        cursor.close();
        db.close();
    }

    public synchronized int[] getTransactionsIds(String query, String[] selectedArgs){
        SQLiteDatabase db = masterCache.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectedArgs);
        int[] result = new int[cursor.getCount()];
        int counter = 0;
        if (cursor.moveToFirst())
            do{
                result[counter] = cursor.getInt(cursor.getColumnIndex("id"));
                counter++;
            }
            while (cursor.moveToNext());
        cursor.close();
        db.close();
        return result;
    }

    public synchronized void addTransaction(Transaction transaction){
        ContentValues contentValues = new ContentValues();
        contentValues.put("transactionId", transaction.getTransactionId());
        contentValues.put("description", transaction.getDescription());
        contentValues.put("date", transaction.getTimeInMillis());
        SQLiteDatabase db = masterCache.getWritableDatabase();
        db.insert("journal", null, contentValues);
        addRawTransactionFromList(transaction.getRawTransactionList());
    }

    private synchronized void addRawTransactionFromList(List<RawTransaction> rawTransactionList){
        SQLiteDatabase db = masterCache.getWritableDatabase();
        for (RawTransaction rawTransaction: rawTransactionList)
            addRawTransaction(db, rawTransaction);
        db.close();
    }

    public synchronized void addRawTransaction(SQLiteDatabase db, RawTransaction rawTransaction){
        ContentValues contentValues = new ContentValues();
        contentValues.put("transactionId", rawTransaction.getTransactionId());
        contentValues.put("ledgerId", rawTransaction.getLedgerId());
        contentValues.put("debit", rawTransaction.getDebit());
        contentValues.put("credit", rawTransaction.getCredit());
        db.insert("rawJournal", null, contentValues);
    }

    private synchronized void addRawTransaction(RawTransaction rawTransaction){
        SQLiteDatabase db = masterCache.getWritableDatabase();
        addRawTransaction(db, rawTransaction);
        db.close();
    }

    private synchronized void removeRawTransaction(RawTransaction rawTransaction){
        String query = "delete from rawJournal where transactionId = ?";
        String[] selectedArgs = new String[]{rawTransaction.getTransactionId()};
        SQLiteDatabase db = masterCache.getWritableDatabase();
        db.execSQL(query, selectedArgs);
        db.close();
    }
}
