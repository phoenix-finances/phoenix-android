package com.ornoma.phoenix.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ornoma.phoenix.cache.LedgerCache;
import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.ui.view.LedgerChartViewHolder;

/**
 * Created by de76 on 5/28/17.
 */

public class ChartAdapter extends BaseAdapter {
    private static final String TAG = "ChartAdapter";
    private static final int VIEW_TAG_KEY = 19;

    private LedgerCache ledgerCache;
    private LayoutInflater layoutInflater;
    private int[] ledgerIdArray;

    public ChartAdapter(Context context, int[] ledgerIdArray){
        this.ledgerIdArray = ledgerIdArray;
        this.layoutInflater = LayoutInflater.from(context);
        this.ledgerCache = LedgerCache.getInstance(context);
    }

    @Override
    public int getCount() {
        return ledgerIdArray.length;
    }

    @Override
    public Ledger getItem(int position) {
        int id = ledgerIdArray[position];
        return ledgerCache.getLedger(id);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LedgerChartViewHolder.getView(layoutInflater);
            LedgerChartViewHolder holder = new LedgerChartViewHolder(convertView);
            convertView.setTag(holder);
        }
        LedgerChartViewHolder viewHolder = (LedgerChartViewHolder) convertView.getTag();
        viewHolder.update(getItem(position));
        return convertView;
    }
}
