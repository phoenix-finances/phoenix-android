package com.ornoma.phoenix.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.factory.MasterCache;
import com.ornoma.phoenix.ui.adapters.ChartAdapter;

public class LedgerGraphActivity extends AppCompatActivity {

    private ListView listView;
    private ChartAdapter chartAdapter;
    private MasterCache masterCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindActivity();
        bindLedgerGraphList();
    }

    private void bindActivity(){
        listView = (ListView)findViewById(R.id.listView);

        masterCache = new MasterCache(this);
    }

    private void bindLedgerGraphList(){
        int[] idArray = masterCache.getLedgerIds();
        chartAdapter = new ChartAdapter(this, idArray);
        listView.setAdapter(chartAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
