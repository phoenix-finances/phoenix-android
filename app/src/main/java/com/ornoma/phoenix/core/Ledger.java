package com.ornoma.phoenix.core;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import com.ornoma.phoenix.cache.LedgerCache;
import com.ornoma.phoenix.factory.MasterCache;
import com.ornoma.phoenix.cache.RawTransactionCache;

/**
 * Created by de76 on 5/4/17.
 */

public class Ledger {
    public static final int BaseGeneration = 0;
    public static final int NextGeneration = 1;

    private String name;
    private int id;
    private int balance;
    private int transactionCount;
    private int parent;
    private int[] ancestorHierarchy = null;
    private String ancestorDiagram = null;

    public void setName(String name){this.name = name;}
    public void setId(int id){this.id = id;}
    public void setParent(int parent){this.parent = parent;}

    public String getName(){return this.name;}
    public int getId(){return this.id;}
    public int getBalance(){return this.balance;}
    public int getTransactionCount(){return this.transactionCount;}
    public int getParent(){return this.parent;}
    public int[] getAncestorHierarchy(){return this.ancestorHierarchy;}
    public String getAncestorDiagram(){return this.ancestorDiagram;}

    public void queryBalance(Context context){
        MasterCache masterCache = new MasterCache(context);
        RawTransactionCache rawTransactionCache = RawTransactionCache.getInstance(context);
        balance = getLedgerBalance(masterCache, rawTransactionCache, id)
                + getChildBalance(masterCache, rawTransactionCache, id);
    }

    private int getChildBalance(MasterCache masterCache, RawTransactionCache rawTransactionCache, int parentId){
        int[] childIdArray = masterCache.getLedgerIds(parentId);
        int childBalance = 0, currentLedgerId;
        for (int i = 0; i < childIdArray.length; i++){
            currentLedgerId = childIdArray[i];
            childBalance += getLedgerBalance(masterCache, rawTransactionCache, currentLedgerId)
                    + getChildBalance(masterCache, rawTransactionCache, currentLedgerId);
        }
        return childBalance;
    }

    private int getLedgerBalance(MasterCache masterCache, RawTransactionCache rawTransactionCache, int ledgerId){
        int[] rawTransIds = masterCache.getRawTransIdArray(ledgerId);
        transactionCount = rawTransIds.length;
        int currentBalance = 0;
        for (int id: rawTransIds){
            RawTransaction rawTransaction = rawTransactionCache.getTransaction(id);
            currentBalance += rawTransaction.getDebit();
            currentBalance -= rawTransaction.getCredit();
        }
        return currentBalance;
    }

    public void queryHierarchy(Context context){
        List<Integer> hierarchy = new ArrayList<>();
        MasterCache masterCache = new MasterCache(context);
        LedgerCache ledgerCache = LedgerCache.getInstance(context);
        String ledgerName;
        Ledger ledger;
        ancestorDiagram = "";

        updateParentId(masterCache, hierarchy, id);
        int size = hierarchy.size();
        ancestorHierarchy = new int[size];
        int counter = 0;
        for (int i : hierarchy){
            ancestorHierarchy[counter] = i;
            counter++;

            ledger = ledgerCache.getLedger(i);
            if (ledger == null)
                ledger = this;

            ledgerName = ledger.getName();
            ancestorDiagram += ledgerName;
            if (counter < size)
                ancestorDiagram += " > ";
        }
    }

    private void updateParentId(MasterCache masterCache, List<Integer> hierarchy, int targetId){
        int parentId = masterCache.getParentLedgerId(targetId);
        hierarchy.add(parentId);
        if (parentId != 0)
            updateParentId(masterCache, hierarchy, parentId);
    }
}
