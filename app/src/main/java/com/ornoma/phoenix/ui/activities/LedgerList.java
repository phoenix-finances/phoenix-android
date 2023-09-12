package com.ornoma.phoenix.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ornoma.phoenix.App;
import com.ornoma.phoenix.R;
import com.ornoma.phoenix.api.Api;
import com.ornoma.phoenix.api.RetrofitClient;
import com.ornoma.phoenix.api.response.CreateLedgersRequest;
import com.ornoma.phoenix.api.response.CreateLedgersResponse;
import com.ornoma.phoenix.ui.adapters.LedgerListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LedgerList extends AppCompatActivity {
    public static final String KEY_LEDGER_ID = "_key_ledger_id";
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private LedgerListAdapter adapter;
    private List<CreateLedgersResponse> ledgerDataList;
    private String auth = String.format("Bearer %s", App.getToken());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_list2);
        floatingActionButton = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        ledgerDataList = new ArrayList<>();
        adapter = new LedgerListAdapter(ledgerDataList);

        int numberOfColumns = 2; // Change this to the desired number of columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
        getAllLedgers();
        clickFloatingBtn();
    }

    private void getAllLedgers() {
        RetrofitClient.getInstance().getApi().getLedgers(auth)
                .enqueue(new Callback<List<CreateLedgersResponse>>() {
                    @Override
                    public void onResponse(Call<List<CreateLedgersResponse>> call, Response<List<CreateLedgersResponse>> response) {
                        if (response.isSuccessful()) {
                            List<CreateLedgersResponse> responses = response.body();
                            ledgerDataList.addAll(responses);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CreateLedgersResponse>> call, Throwable t) {

                    }
                });
    }

    private void clickFloatingBtn() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LedgerList.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.ledger_dialogue, null);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                EditText etName = dialogView.findViewById(R.id.editTextName);
                Button btnAdd = dialogView.findViewById(R.id.buttonAdd);
                Button btnCancel = dialogView.findViewById(R.id.buttonCancel);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = etName.getText().toString();
                        CreateLedgersRequest request = new CreateLedgersRequest(name);
                        RetrofitClient.getInstance().getApi().createLedger(auth, request)
                                .enqueue(new Callback<CreateLedgersResponse>() {
                                    @Override
                                    public void onResponse(Call<CreateLedgersResponse> call, Response<CreateLedgersResponse> response) {
                                        if (response.isSuccessful()) {
                                            CreateLedgersResponse responseData = response.body();
                                            ledgerDataList.add(0, responseData);
                                            adapter.notifyItemInserted(0);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CreateLedgersResponse> call, Throwable t) {

                                    }
                                });
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
}
