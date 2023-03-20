package com.ornoma.phoenix.scripts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ornoma.phoenix.cache.TagCache;
import com.ornoma.phoenix.core.Transaction;
import com.ornoma.phoenix.core.TransactionTag;
import com.ornoma.phoenix.factory.MasterCache;

/**
 * Created by de76 on 5/26/17.
 */

public abstract class TagListRetriever {
    private static final String TAG = "TagRetriever";
    public static final int TYPE_LIST_ID_STR = 0;
    public static final int TYPE_LIST_VALUE_STR = 1;
    public abstract void onListRetrieved(List<String> result);

    private MasterCache masterCache;
    private Transaction transaction;
    private TagCache tagCache;
    private int modeType;
    public TagListRetriever(Context context, Transaction transaction, int modeType){
        masterCache = new MasterCache(context);
        tagCache = TagCache.getInstance(context);
        this.transaction = transaction;
        this.modeType = modeType;

        new Retriever().execute();
    }

    private class Retriever extends AsyncTask<Void, Void, Void>{
        private List<String> result = null;
        @Override
        protected Void doInBackground(Void... voids) {
            String[] idArray = masterCache.getTagFactory().getIdArray(transaction);
            switch (modeType){
                case TYPE_LIST_ID_STR:
                    result = new ArrayList<String>(Arrays.asList(idArray));
                    break;
                case TYPE_LIST_VALUE_STR:
                    result = new ArrayList<>();
                    for (int i = 0; i < idArray.length; i++){
                        TransactionTag tag = tagCache.getTag(idArray[i]);
                        if (tag != null)
                            result.add(tag.getName());
                        else Log.d(TAG, "Tag is null > " + idArray[i]);
                    }
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onListRetrieved(result);
        }
    }
}
