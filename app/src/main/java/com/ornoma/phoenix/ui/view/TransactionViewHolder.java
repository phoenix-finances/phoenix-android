package com.ornoma.phoenix.ui.view;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.core.RawTransaction;
import com.ornoma.phoenix.core.Transaction;
import com.ornoma.phoenix.cache.LedgerCache;
import com.ornoma.phoenix.ui.activities.NewTransactionActivity;

/**
 * Created by de76 on 5/4/17.
 */

@SuppressWarnings("FieldCanBeLocal")
public class TransactionViewHolder extends RecyclerView.ViewHolder {

    private LedgerCache ledgerCache;

    private TextView textViewDes;
    private TextView textViewTime;
    private TextView textViewRawInfo;
    private TextView textViewDrCr;
    private TextView textViewNote;
    private LinearLayout linearLayout;

    private Transaction transaction;

    public TransactionViewHolder(View itemView) {
        super(itemView);
        ledgerCache = LedgerCache.getInstance(itemView.getContext());
    }

    public void updateView(Transaction transaction){
        this.transaction = transaction;
        textViewDes = (TextView)itemView.findViewById(R.id.textView_des);
        textViewTime = (TextView)itemView.findViewById(R.id.textView_time);
        textViewRawInfo = (TextView)itemView.findViewById(R.id.textView_rawTransactionInfo);
        textViewDrCr = (TextView)itemView.findViewById(R.id.textView_drCr);
        linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);
        textViewNote = (TextView)itemView.findViewById(R.id.textView_note);

        textViewDes.setText(String.valueOf(transaction.getRawId()));
        textViewRawInfo.setText(getRawTransactionView());
        textViewTime.setText(transaction.getDifString());
        textViewDrCr.setText(getDebitCreditView());
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransaction(v.getContext());
            }
        });

        String note = transaction.getDescription();
        if (!TextUtils.isEmpty(note)){
            textViewNote.setText(note);
            textViewNote.setVisibility(View.VISIBLE);
        }
        else
            textViewNote.setVisibility(View.GONE);
    }

    private void openTransaction(Context context){
        Intent intentTransaction = new Intent(context, NewTransactionActivity.class);
        intentTransaction.putExtra(NewTransactionActivity.KEY_TRANSACTION_RAW_ID, transaction.getRawId());
        context.startActivity(intentTransaction);
    }

    private Spanned getDebitCreditView(){
        String html = "", row, name;
        double debit, credit;
        Ledger ledger;

        for (RawTransaction rawTransaction : transaction.getRawTransactionList()){
            ledger = ledgerCache.getLedger(rawTransaction.getLedgerId());

            if (ledger != null) {
                if (ledger.getAncestorDiagram() == null)
                    ledger.queryHierarchy(itemView.getContext());
                name = ledger.getAncestorDiagram();
            }
            else name = String.valueOf(rawTransaction.getLedgerId());

            String colorGreenStr = "#3f681c", colorRedStr = "#fb6542";
            debit = rawTransaction.getDebit();
            credit = rawTransaction.getCredit();

            String color = (debit>credit)? colorGreenStr : colorRedStr;

            row =   "<font color='"  + color + "'><u>" + name + "</u></font> ";
            html += row;
        }
        return Html.fromHtml(html);
    }

    private Spanned getRawTransactionView(){
        String html = "", row, name;
        int debit = 0, credit;
        Ledger ledger;

        for (RawTransaction rawTransaction : transaction.getRawTransactionList()){
            debit += rawTransaction.getDebit();
        }
        html = "<font color='#3f681c'>" + debit +  "</font>";
        return Html.fromHtml(html);
    }
}
