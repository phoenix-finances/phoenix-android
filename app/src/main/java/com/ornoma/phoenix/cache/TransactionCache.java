package com.ornoma.phoenix.cache;

import android.content.Context;
import android.util.LruCache;

import com.ornoma.phoenix.core.Transaction;
import com.ornoma.phoenix.factory.MasterCache;

/**
 * Created by de76 on 5/4/17.
 */

public class TransactionCache {
    private class Cache extends LruCache<Integer, Transaction>{

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public Cache(int maxSize) {
            super(maxSize);
        }
    }

    private class IdCache extends LruCache<String, Integer>{
        public IdCache(int maxSize){super(maxSize);}
    }

    private static TransactionCache instance;
    public static TransactionCache getInstance(Context context){
        if (instance == null)
            instance = new TransactionCache(context);
        return instance;
    }

    private Cache cache;
    private IdCache idCache;
    private MasterCache masterCache;

    private TransactionCache(Context context){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        cache = new Cache(cacheSize);
        idCache = new IdCache(cacheSize);
        masterCache = new MasterCache(context);
    }

    public int getRawTransactionId(String transactionId){
        Integer idInt = idCache.get(transactionId);
        if (idInt == null){
            idInt = masterCache.getRawTransId(transactionId);
            idCache.put(transactionId, idInt);
        }
        return idInt;
    }

    public Transaction getTransaction(int rawId){
        Transaction transaction = cache.get(rawId);
        if (transaction == null){
            transaction = masterCache.getTransaction(rawId);
            if (transaction != null)
                cache.put(rawId, transaction);
        }
        return transaction;
    }

    public void clear(){
        cache.evictAll();
        idCache.evictAll();
    }
}
