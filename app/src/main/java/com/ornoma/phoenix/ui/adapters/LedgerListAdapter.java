package com.ornoma.phoenix.ui.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ornoma.phoenix.R;
import com.ornoma.phoenix.api.response.CreateLedgersResponse;

import java.util.List;

public class LedgerListAdapter extends RecyclerView.Adapter<LedgerListAdapter.ViewHolder> {
    private List<CreateLedgersResponse> ledgerDataList;

    public LedgerListAdapter(List<CreateLedgersResponse> ledgerDataList) {
        this.ledgerDataList = ledgerDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemview = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.each_ledger_list_item,parent,false);
       return new ViewHolder(itemview);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CreateLedgersResponse ledgerData =ledgerDataList.get(position);
       holder.nameTextView.setText(ledgerData.getName());//Name
        holder.balanceTextView.setText(String.valueOf(ledgerData.getBalance()));

    }

    @Override
    public int getItemCount() {
        return ledgerDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView nameTextView;
        public TextView balanceTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itemNameTextView);
            balanceTextView = itemView.findViewById(R.id.itemBalanceTextView);
        }
    }
}
