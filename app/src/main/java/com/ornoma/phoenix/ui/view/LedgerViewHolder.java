package com.ornoma.phoenix.ui.view;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.events.EventLedgerSelected;
import com.ornoma.phoenix.lib.ZedAsyncTask;
import com.ornoma.phoenix.ui.activities.LedgerDetailActivity;
import com.ornoma.phoenix.ui.activities.LedgerListActivity;

/**
 * Created by de76 on 5/4/17.
 */

@SuppressWarnings("FieldCanBeLocal")
public class LedgerViewHolder extends RecyclerView.ViewHolder {
    private class CustomZedAsyncTask extends ZedAsyncTask {
        @Override
        public void doInBackground() {
            ledger.queryBalance(itemView.getContext());
        }

        @Override
        public void onPostExecute() {
            textViewBalance.setText(getBalanceView());
        }
    }

    public TextView textViewName;
    private TextView textViewId;
    public TextView textViewBalance;
    private LinearLayout linearLayout;
    private CustomZedAsyncTask customZedAsyncTask;

    private Ledger ledger;

    public LedgerViewHolder(View itemView) {
        super(itemView);
        customZedAsyncTask = new CustomZedAsyncTask();
    }

    public void updateView(Ledger ledger) {
        this.ledger = ledger;

        textViewName = (TextView) itemView.findViewById(R.id.textView_name);
        textViewId = (TextView) itemView.findViewById(R.id.textView_id);
        textViewBalance = (TextView) itemView.findViewById(R.id.textView_balance);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);

        textViewName.setText(ledger.getName());
        textViewId.setText(String.valueOf(ledger.getId()));
        textViewBalance.setText(getBalanceView());

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LViewHolder", "OnClick!");
                handleLedgerSelectedEvent(v.getContext(), new EventLedgerSelected(LedgerViewHolder.this.ledger));
            }
        });
        customZedAsyncTask.execute();
    }

    private Spanned getBalanceView() {
        int balance = ledger.getBalance();
        String color = (balance > 0) ? "#3f681c" : "#fb6542";
        String html = "<font color='" + color + "'>" + Math.abs(balance) + "</font>";
        return Html.fromHtml(html);
    }

    public void handleLedgerSelectedEvent(Context context, EventLedgerSelected eventLedgerSelected) {
        if (context instanceof LedgerListActivity) {
            LedgerListActivity ledgerListActivity = (LedgerListActivity) context;
            if (ledgerListActivity.isInSelectionMode)
                ledgerListActivity.openSubLedger(eventLedgerSelected.ledger.getId());
            else
                ledgerListActivity.launchDetailActivity(eventLedgerSelected.ledger);
        }
        if (context instanceof LedgerDetailActivity) {
            LedgerDetailActivity ledgerDetailActivity = (LedgerDetailActivity) context;
            ledgerDetailActivity.launchDetailActivity(eventLedgerSelected.ledger);
        }
    }
}
