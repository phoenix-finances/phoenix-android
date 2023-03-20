package com.ornoma.phoenix.ui.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.RawTransaction;
import com.ornoma.phoenix.core.Transaction;
import com.ornoma.phoenix.cache.RawTransactionCache;
import com.ornoma.phoenix.cache.TransactionCache;

/**
 * Created by de76 on 5/5/17.
 */

public class LedgerTrAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private TransactionCache transactionCache;
    private RawTransactionCache rawTransactionCache;

    private int[] rawTrIdArray;
    private int[] balanceArray;

    public LedgerTrAdapter(Context context, int[] rawTrIdArray){
        this.rawTrIdArray = rawTrIdArray;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.transactionCache = TransactionCache.getInstance(context);
        this.rawTransactionCache = RawTransactionCache.getInstance(context);
        bindBalanceArray();
    }

    private void bindBalanceArray(){
        this.balanceArray = new int[rawTrIdArray.length];
        int balance = 0;
        for (int i = 0; i < balanceArray.length; i++){
            RawTransaction rawTransaction = getItem(i);
            balance += (rawTransaction.getDebit() - rawTransaction.getCredit());
            balanceArray[i] = balance;
        }
    }

    private void updateView(View view, int position){
        int reversePosition = getReversePosition(position);
        RawTransaction rawTransaction = getItem(reversePosition);
        TextView textViewName, textViewTime, textViewDebit, textViewCredit, textViewBalance;

        textViewName = (TextView)view.findViewById(R.id.textView_des);
        textViewTime = (TextView)view.findViewById(R.id.textView_time);
        textViewDebit = (TextView)view.findViewById(R.id.textView_debit);
        textViewCredit = (TextView)view.findViewById(R.id.textView_credit);
        textViewBalance = (TextView)view.findViewById(R.id.textView_balance);

        int rawTransId = transactionCache.getRawTransactionId(rawTransaction.getTransactionId());
        Transaction transaction = transactionCache.getTransaction(rawTransId);

        textViewName.setText(transaction.getDescription());
        textViewTime.setText(transaction.getDifString());
        textViewDebit.setText(String.valueOf(rawTransaction.getDebit()));
        textViewCredit.setText(String.valueOf(rawTransaction.getCredit()));
        textViewBalance.setText(getBalanceView(reversePosition));
    }

    private Spanned getBalanceView(int position){
        int balance = balanceArray[position];
        String color = (balance > 0) ? "#3f681c" : "#fb6542";
        String html = "<font color='"+color+"'>" + Math.abs(balance) + "</font>";
        return Html.fromHtml(html);
    }

    @Override
    public int getCount() {
        return rawTrIdArray.length;
    }

    @Override
    public RawTransaction getItem(int position) {
        int rawId = rawTrIdArray[position];
        return rawTransactionCache.getTransaction(rawId);
    }

    private int getReversePosition(int position){
        return rawTrIdArray.length - (position +1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.layout_raw_transaction_detail, null);
        updateView(convertView, position);
        return convertView;
    }
}
