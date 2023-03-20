package com.ornoma.phoenix.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ornoma.phoenix.ui.activities.CalculatorActivity;
import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.core.RawTransaction;
import com.ornoma.phoenix.core.Transaction;
import com.ornoma.phoenix.cache.LedgerCache;
import com.ornoma.phoenix.factory.MasterCache;
import com.ornoma.phoenix.ui.activities.LedgerListActivity;
import com.ornoma.phoenix.ui.activities.NewTransactionActivity;

/**
 * Created by de76 on 5/4/17.
 */

public abstract class RawTransactionAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Transaction transaction;
    private LedgerCache ledgerCache;
    private int[] ledgerIdArray;
    private MasterCache masterCache;
    private boolean isEditable = false;
    private Context context;

    public abstract void onRemove(RawTransaction rawTransaction);

    public RawTransactionAdapter(Context context, Transaction transaction){
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.transaction = transaction;
        this.ledgerCache = LedgerCache.getInstance(context);
        this.masterCache = new MasterCache(context);
        this.ledgerIdArray = masterCache.getLedgerIds();
    }

    public void updateEditable(boolean isEditable){
        this.isEditable = isEditable;
    }

    private void updateView(View view, final int position){
        TextView textViewDebit = (TextView)view.findViewById(R.id.editText_debit);
        TextView textViewCredit = (TextView)view.findViewById(R.id.editText_credit);
        TextView textViewLedger = (TextView)view.findViewById(R.id.textView_ledger);
        ImageButton imageButtonRemove = (ImageButton)view.findViewById(R.id.imageButton_remove);

        final RawTransaction rawTransaction = transaction.getRawTransactionList().get(position);
        String debitStr = String.valueOf(rawTransaction.getDebit());
        String creditStr = String.valueOf(rawTransaction.getCredit());
        debitStr = debitStr.equals("0") ? "" : debitStr;
        creditStr = creditStr.equals("0") ? "" : creditStr;
        textViewDebit.setText(debitStr);
        textViewCredit.setText(creditStr);

        textViewDebit.setEnabled(isEditable);
        textViewCredit.setEnabled(isEditable);

        textViewDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextInput(true, position);
            }
        });
        textViewCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextInput(false, position);
            }
        });

        int ledgerId = rawTransaction.getLedgerId();

        String info = "Select Ledger";
        if (ledgerId != 0){
            Ledger ledger = ledgerCache.getLedger(ledgerId);
            if (ledger != null)
                info = ledger.getAncestorDiagram();
        }

        textViewLedger.setText(info);
        textViewLedger.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                openLedgerPicker(position);}});

        imageButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onRemove(rawTransaction);}});
    }

    private void openLedgerPicker(int position){
        lastPosition = position;
        Intent intentLedgerList = new Intent(context, LedgerListActivity.class);
        intentLedgerList.putExtra(LedgerListActivity.KEY_MODE, LedgerListActivity.MODE_SELECTION);
        if (context instanceof  NewTransactionActivity)
            ((NewTransactionActivity)context).startActivityForResult(intentLedgerList, NewTransactionActivity.REQUEST_LEDGER_LIST);
    }

    private boolean lastPositionOfTypeIsDebit  = false;
    private void onTextInput(boolean isDebit, int position){
        lastPositionOfTypeIsDebit = isDebit;
        lastPosition = position;
        Intent intent = new Intent(context, CalculatorActivity.class);
        intent.putExtra(CalculatorActivity.KEY_MODE, CalculatorActivity.MODE_SELECTION);
        if (context instanceof NewTransactionActivity)
            ((NewTransactionActivity)context).startActivityForResult(intent, NewTransactionActivity.REQUEST_CALCULATOR);
        else Log.d("RawAdapter","Context is not instance of NewTransactionActivity");
    }

    private int lastPosition;
    public void onLedgerSelected(int ledgerId){
        new HierarchyRetriever(ledgerId).execute();
        updateLedgerId(lastPosition, ledgerId);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private static final String TAG = "RawAdapter";
    public void onNumberSubmit(String number){
        Log.d(TAG, "onNumberSubmit->" + number);
        RawTransaction rawTransaction = getItem(lastPosition);
        double value = 0.0;
        try{
            value = Double.parseDouble(number);
            value = round(value, 2);
        }
        catch (Exception e){e.printStackTrace();}
        if (lastPositionOfTypeIsDebit)
            rawTransaction.setDebit(value);
        else
            rawTransaction.setCredit(value);
        notifyDataSetChanged();
    }

    private class HierarchyRetriever extends AsyncTask<Void, Void, Void>{
        private int ledgerId;
        HierarchyRetriever(int ledgerId){this.ledgerId = ledgerId;}
        @Override
        protected Void doInBackground(Void... params) {
            Ledger ledger = ledgerCache.getLedger(ledgerId);
            ledger.queryHierarchy(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataSetChanged();
        }
    }

    private void updateLedgerId(int position, int ledgerId){
        try{getItem(position).setLedgerId(ledgerId);}
        catch (Exception e){}
    }

    @Override
    public int getCount() {
        return transaction.getRawTransactionList().size();
    }

    @Override
    public RawTransaction getItem(int position) {
        return transaction.getRawTransactionList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.layout_raw_transaction, null);
        updateView(convertView, position);
        return convertView;
    }
}
