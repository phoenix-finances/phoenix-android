package com.ornoma.phoenix.cache;

import android.content.Context;
import android.util.LruCache;

import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.factory.MasterCache;

/**
 * Created by de76 on 5/4/17.
 */

public class LedgerCache {
    private class Cache extends LruCache<Integer, Ledger>{

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public Cache(int maxSize) {
            super(maxSize);
        }
    }

    private static LedgerCache instance;
    public static LedgerCache getInstance(Context context){
        if (instance == null)
            instance = new LedgerCache(context);
        return instance;
    }

    private Cache cache;
    private MasterCache masterCache;

    private LedgerCache(Context context){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        cache = new Cache(cacheSize);
        masterCache = new MasterCache(context);
    }

    public Ledger getLedger(int ledgerId){
        Ledger ledger = cache.get(ledgerId);
        if (ledger == null){
            ledger = masterCache.getLedger(ledgerId);
            if (ledger != null){
                cache.put(ledgerId, ledger);
            }
        }
        return ledger;
    }

    public void clear(){
        cache.evictAll();
    }
}
