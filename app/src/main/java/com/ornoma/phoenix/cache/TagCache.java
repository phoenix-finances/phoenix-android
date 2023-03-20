package com.ornoma.phoenix.cache;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;

import com.ornoma.phoenix.core.TransactionTag;
import com.ornoma.phoenix.factory.MasterCache;

/**
 * Created by de76 on 5/4/17.
 */

public class TagCache {
    private class Cache extends LruCache<Integer, TransactionTag>{

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
        IdCache(int maxSize){super(maxSize);}
    }

    private static TagCache instance;
    public static TagCache getInstance(Context context){
        if (instance == null)
            instance = new TagCache(context);
        return instance;
    }

    private Cache cache;
    private IdCache idCache;
    private MasterCache masterCache;
    private static final String TAG = "TagCache";

    private TagCache(Context context){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        final int idCacheSize = cacheSize / 2;
        cache = new Cache(cacheSize);
        idCache = new IdCache(idCacheSize);
        masterCache = new MasterCache(context);
    }

    public TransactionTag getTag(String tagId){
        Log.d(TAG, "tagId : " + tagId);
        Integer result = idCache.get(tagId);
        if (result == null){
            result = masterCache.getTagFactory().getRawId(tagId);
            Log.d(TAG, "tagRawId : " + result);
            if (result != 0){
                idCache.put(tagId, result);
                return getTag(result);
            }
        }
        else
            return cache.get(result);
        return null;
    }

    public TransactionTag getTag(int tagRawId){
        TransactionTag ledger = cache.get(tagRawId);
        if (ledger == null){
            ledger = masterCache.getTagFactory().getTag(tagRawId);
            if (ledger != null){
                cache.put(tagRawId, ledger);
            }
        }
        return ledger;
    }

    public void clear(){
        cache.evictAll();
    }
}
