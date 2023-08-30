package com.ornoma.phoenix.ui.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.api.response.CreateLedgersResponse;
import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.cache.LedgerCache;
import com.ornoma.phoenix.ui.view.LedgerViewHolder;

import java.util.List;

/**
 * Created by de76 on 5/4/17.
 */

public class LedgerAdapter extends RecyclerView.Adapter<LedgerViewHolder> {
  /*  private int[] ledgerIdArray;
    private LayoutInflater layoutInflater;
    private LedgerCache ledgerCache;
    private int[] sortedIdArray;
*/

    List<CreateLedgersResponse> list;
    private LayoutInflater layoutInflater;
   /* public LedgerAdapter(Context context, int[] ledgerIdArray){
        this.ledgerIdArray = ledgerIdArray;
        this.ledgerCache = LedgerCache.getInstance(context);
    }*/

    public LedgerAdapter(List<CreateLedgersResponse> list) {
        this.list= list;
    }

    @Override
    public LedgerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.layout_ledger, parent, false);
        return new LedgerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LedgerViewHolder holder, int position) {
//        int ledgerId = ledgerIdArray[position];
//        Ledger ledger = ledgerCache.getLedger(ledgerId);
//        holder.updateView(ledger);
        CreateLedgersResponse response = list.get(position);
//        holder.updateView(ledger);
        holder.textViewName.setText(list.get(position).getName());
        holder.textViewBalance.setText(list.get(position).getBalance().toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
