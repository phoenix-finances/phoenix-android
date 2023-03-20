package com.ornoma.phoenix.cache;

import android.content.Context;
import android.util.LruCache;

import com.ornoma.phoenix.core.RawTransaction;
import com.ornoma.phoenix.factory.MasterCache;

/**
 * Created by de76 on 5/4/17.
 */

public class RawTransactionCache {
    private class Cache extends LruCache<Integer, RawTransaction>{
        public Cache(int maxSize) {
            super(maxSize);
        }
    }

    private static RawTransactionCache instance;
    public static RawTransactionCache getInstance(Context context){
        if (instance == null)
            instance = new RawTransactionCache(context);
        return instance;
    }

    private Cache cache;
    private MasterCache masterCache;

    private RawTransactionCache(Context context){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        cache = new Cache(cacheSize);
        masterCache = new MasterCache(context);
    }

    public RawTransaction getTransaction(int rawId){
        RawTransaction rawTransaction = cache.get(rawId);
        if (rawTransaction == null){
            rawTransaction = masterCache.getRawTransaction(rawId);
            if (rawTransaction != null)
                cache.put(rawId, rawTransaction);
        }
        return rawTransaction;
    }

    public void clear(){
        cache.evictAll();
    }
}
