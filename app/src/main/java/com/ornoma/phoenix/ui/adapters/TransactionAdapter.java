package com.ornoma.phoenix.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.Transaction;
import com.ornoma.phoenix.cache.TransactionCache;
import com.ornoma.phoenix.ui.view.TransactionViewHolder;

/**
 * Created by de76 on 5/4/17.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {
    private int[] transactionRawIdArray;
    private LayoutInflater layoutInflater;
    private TransactionCache transactionCache;

    public TransactionAdapter(Context context, int[] transactionRawIdArray){
        this.transactionRawIdArray = transactionRawIdArray;
        transactionCache = TransactionCache.getInstance(context);
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.layout_transaction, null);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        int rawId = transactionRawIdArray[position];
        Transaction transaction = transactionCache.getTransaction(rawId);
        holder.updateView(transaction);
    }

    @Override
    public int getItemCount() {
        return transactionRawIdArray.length;
    }
}
