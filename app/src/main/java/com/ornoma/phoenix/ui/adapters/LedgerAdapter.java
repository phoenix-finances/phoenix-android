package com.ornoma.phoenix.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.cache.LedgerCache;
import com.ornoma.phoenix.ui.view.LedgerViewHolder;

/**
 * Created by de76 on 5/4/17.
 */

public class LedgerAdapter extends RecyclerView.Adapter<LedgerViewHolder> {
    private int[] ledgerIdArray;
    private LayoutInflater layoutInflater;
    private LedgerCache ledgerCache;
    private int[] sortedIdArray;

    public LedgerAdapter(Context context, int[] ledgerIdArray){
        this.ledgerIdArray = ledgerIdArray;
        this.ledgerCache = LedgerCache.getInstance(context);
    }

    @Override
    public LedgerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.layout_ledger, null);
        return new LedgerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LedgerViewHolder holder, int position) {
        int ledgerId = ledgerIdArray[position];
        Ledger ledger = ledgerCache.getLedger(ledgerId);
        holder.updateView(ledger);
    }

    @Override
    public int getItemCount() {
        return ledgerIdArray.length;
    }
}
