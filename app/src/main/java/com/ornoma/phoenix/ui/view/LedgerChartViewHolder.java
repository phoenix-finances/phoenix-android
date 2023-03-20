package com.ornoma.phoenix.ui.view;

import androidx.recyclerview.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Calendar;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.cache.RawTransactionCache;
import com.ornoma.phoenix.cache.TransactionCache;
import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.core.RawTransaction;
import com.ornoma.phoenix.factory.MasterCache;
import com.ornoma.phoenix.lib.ZedAsyncTask;

/**
 * Created by de76 on 5/27/17.
 */

public class LedgerChartViewHolder extends RecyclerView.ViewHolder {
    private class CustomZedAsyncTask extends ZedAsyncTask{
        @Override
        public void doInBackground() {
            int dayToCount = 30;
            long time;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            time = calendar.getTimeInMillis();

            long day_interval = 1000 * 60 * 60 * 24;

            {
                Calendar calendarEndCalender = Calendar.getInstance();
                calendarEndCalender.setTimeInMillis(time);

                time -= (day_interval * dayToCount);

                Calendar calendarStartCalender = Calendar.getInstance();
                calendarStartCalender.setTimeInMillis(time);

                String rangeText = String.format("%1$tb %1$te, %1$tY", calendarStartCalender)
                        + " - " + String.format("%1$tb %1$te, %1$tY", calendarEndCalender);
                chartView.setRangeText(rangeText);
            }

            CharPair[] cachedPair = getCacheInstance().get(ledger.getId());
            if (cachedPair != null){
                xyMap = cachedPair;
                return;
            }

            masterCache = new MasterCache(itemView.getContext());
            rawTransactionCache = RawTransactionCache.getInstance(itemView.getContext());
            transactionCache = TransactionCache.getInstance(itemView.getContext());
            CharPair[] tempCharPair = new CharPair[dayToCount];

            int[] idArray;
            RawTransaction rawTransaction;

            long startTime, endTime;
            double totalDebit, totalCredit;
            CharPair charPair, charPairChild;

            for (int i = 0; i < dayToCount; i++){
                startTime = time;
                endTime = time + day_interval;
                idArray = masterCache.getTransactionFactory().getRawTransIdArray(ledger.getId(), startTime, endTime);
                totalDebit = 0;
                totalCredit = 0;
                charPair = new CharPair();
                for (int id: idArray){
                    rawTransaction = rawTransactionCache.getTransaction(id);
                    totalDebit += rawTransaction.getDebit();
                    totalCredit += rawTransaction.getCredit();
                }

                charPairChild = getChildTotal(masterCache, rawTransactionCache, ledger.getId(), startTime, endTime);
                totalDebit += charPairChild.getY1();
                totalCredit += charPairChild.getY2();

                charPair.setY1(totalDebit);
                charPair.setY2(totalCredit);

                tempCharPair[i] = charPair;
                time = endTime;
            }
            getCacheInstance().put(ledger.getId(), tempCharPair);
            xyMap = tempCharPair;
        }

        private CharPair getChildTotal(MasterCache masterCache, RawTransactionCache rawTransactionCache,
                                       int parentId, long startTime, long endTime){
            RawTransaction rawTransaction;
            CharPair charPair = new CharPair();
            CharPair charPairChild;
            double totalDebit = 0, totalCredit = 0;
            int[] childIds = masterCache.getLedgerIds(parentId);
            int[] idArray;
            for(int ledgerId : childIds){
                idArray = masterCache.getTransactionFactory().getRawTransIdArray(ledgerId, startTime, endTime);
                for (int rawTransId : idArray){
                    rawTransaction = rawTransactionCache.getTransaction(rawTransId);
                    totalDebit += rawTransaction.getDebit();
                    totalCredit += rawTransaction.getCredit();
                }
                charPairChild = getChildTotal(masterCache, rawTransactionCache, ledgerId, startTime, endTime);
                totalDebit += charPairChild.getY1();
                totalCredit += charPairChild.getY2();
            }
            charPair.setY1(totalDebit);
            charPair.setY2(totalCredit);
            return charPair;
        }

        @Override
        public void onPostExecute() {
            chartView.setTitle(ledger.getName());
            chartView.setData(xyMap);
        }
    }

    private class Cache extends LruCache<Integer, CharPair[]>{
        Cache(int maxSize){super(maxSize);}
    }

    private static Cache cacheInstance = null;
    private static Cache getCacheInstance(){
        return cacheInstance;
    }

    private static final String TAG = "LCViewHolder";
    private Ledger ledger;
    private CustomZedAsyncTask customZedAsyncTask;
    private MasterCache masterCache;
    private RawTransactionCache rawTransactionCache;
    private TransactionCache transactionCache;
    private ChartView chartView;
    private CharPair[] xyMap = null;

    public LedgerChartViewHolder(View itemView){
        super(itemView);
        customZedAsyncTask = new CustomZedAsyncTask();
        if (cacheInstance == null){
            int maxSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxSize/ 16;
            cacheInstance = new Cache(cacheSize);
        }
    }

    public void update(Ledger ledger){
        this.ledger = ledger;

        chartView = (ChartView)itemView.findViewById(R.id.chartView);

        try {
            customZedAsyncTask.execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static View getView(LayoutInflater layoutInflater){
        return layoutInflater.inflate(R.layout.layout_ledger_chart_view, null);
    }
}
