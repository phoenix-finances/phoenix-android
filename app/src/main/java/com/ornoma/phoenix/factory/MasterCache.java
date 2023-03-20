package com.ornoma.phoenix.factory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.core.RawTransaction;
import com.ornoma.phoenix.core.Transaction;

/**
 * Created by de76 on 5/4/17.
 */

@SuppressWarnings("WeakerAccess")
public class MasterCache extends SQLiteOpenHelper {
    public static final String DB_NAME = "master.cache";
    public static final int DB_VERSION = 4;

    private TransactionFactory transactionFactory;
    private LedgerFactory ledgerFactory;
    private TagFactory tagFactory;
    private Context context;

    public MasterCache(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        transactionFactory = new TransactionFactory();
        transactionFactory.setMasterCache(this);
        ledgerFactory = new LedgerFactory();
        ledgerFactory.setMasterCache(this);
        tagFactory = new TagFactory(this);
        this.context = context;
    }

    public Context getContext(){
        return this.context;
    }
    public TagFactory getTagFactory(){return this.tagFactory;}
    public TransactionFactory getTransactionFactory(){return this.transactionFactory;}

    public synchronized RawTransaction getRawTransaction(int rawId){return transactionFactory.getRawTransaction(rawId);}
    public synchronized int getRawTransId(String transactionId){return transactionFactory.getRawTransId(transactionId);}
    public synchronized int[] getRawTransIdArray(int ledgerId){return transactionFactory.getRawTransIdArray(ledgerId);}

    public synchronized Ledger getLedger(int ledgerNumber){
        String query = "select * from masterLedger where ledgerId = ?";
        String[] selectedArgs = new String[]{String.valueOf(ledgerNumber)};
        return ledgerFactory.getLedger(query, selectedArgs);
    }

    public int getParentLedgerId(int ledgerId){
        String query = "select parent from masterLedger where ledgerId = ?";
        String[] selectedArgs = new String[]{String.valueOf(ledgerId)};
        String result = ledgerFactory.getValue(query, selectedArgs, "parent");
        int resultId = 0;
        try {resultId = Integer.parseInt(result);}
        catch (Exception e){}
        return resultId;
    }

    public void addLedger(Ledger ledger){ledgerFactory.add(ledger);}
    public int[] getLedgerIds(){return ledgerFactory.getIdArray(Ledger.BaseGeneration);}
    public int[] getLedgerIds(int parent){return ledgerFactory.getIdArray(parent);}

    public synchronized Transaction getTransaction(int rawId){
        String query = "select * from journal where id = ?";
        String[] selectedArgs = new String[]{String.valueOf(rawId)};
        return transactionFactory.getTransaction(query, selectedArgs);
    }

    public synchronized int[] getTransactionRawIds(Calendar from, Calendar to){
        long fromMillis = from.getTimeInMillis();
        long toMillis = to.getTimeInMillis();
        String query = "select id from journal where ((date > ?) and (date < ?)) order by id desc";
        String[] selectedArgs = new String[]{String.valueOf(fromMillis), String.valueOf(toMillis)};
        return transactionFactory.getTransactionsIds(query, selectedArgs);
    }

    public synchronized int[] getTransactionRawIds(){
        String query = "select id from journal order by id desc";
        return transactionFactory.getTransactionsIds(query, null);
    }

    public synchronized void updateTransaction(Transaction transaction){
        if (!transactionFactory.isExists(transaction))
            transactionFactory.addTransaction(transaction);
        else
            transactionFactory.updateTransaction(transaction);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createJournalQuery = "create table if not exists journal" +
                "(" +
                "id integer not null primary key autoincrement, " +
                "transactionId text, " +
                "description text, " +
                "date int" +
                ")";
        db.execSQL(createJournalQuery);

        createRawJournalTable(db);

        String createMasterLedgerQuery = "create table if not exists masterLedger" +
                "(" +
                "id integer not null primary key autoincrement, " +
                "name text, " +
                "ledgerId integer, " +
                "parent integer" +
                ")";
        db.execSQL(createMasterLedgerQuery);
        createTagTables(db);
    }

    private void createRawJournalTable(SQLiteDatabase db){
        db.execSQL("create table if not exists rawJournal" +
                "(" +
                "id integer not null primary key autoincrement, " +
                "transactionId text, " +
                "ledgerId integer, " +
                "debit real, " +
                "credit real" +
                ")");
    }

    private void createTagTables(SQLiteDatabase db){
        db.execSQL("create table if not exists tags (" +
                "id integer not null primary key autoincrement, " +
                "tagId text, " +
                "tagName text" +
                ")");
        db.execSQL("create table if not exists tagMapping (" +
                "id integer not null primary key autoincrement, " +
                "tagId text, " +
                "transactionId text" +
                ")");
    }

    private void doV4Update(SQLiteDatabase db){
        String query = "select id from rawJournal";
        int[] idArray = transactionFactory.getRawTransIdArray(db, query, null);
        RawTransaction[] rawTransactions = new RawTransaction[idArray.length];
        int counter = 0;
        for (int i : idArray){
            rawTransactions[counter] = transactionFactory.getRawTransaction(db, i);
            counter++;
        }
        db.execSQL("drop table if exists rawJournal");
        createRawJournalTable(db);
        for (RawTransaction rawTransactionNew: rawTransactions)
            transactionFactory.addRawTransaction(db, rawTransactionNew);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Its quite complicated algorithm, refer to hand written documents
        for (int version = oldVersion+1; version <= newVersion; version++)
            upgradeVersion(db, version);
    }

    private void upgradeVersion(SQLiteDatabase db, int version){
        switch (version){
            case 2:
                db.execSQL("ALTER TABLE masterLedger ADD COLUMN parent INTEGER DEFAULT 0");
                break;
            case 3:
                createTagTables(db);
                break;
            case 4:
                doV4Update(db);
                break;
        }
    }
}
