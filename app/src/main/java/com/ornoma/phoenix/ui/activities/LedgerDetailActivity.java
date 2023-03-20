package com.ornoma.phoenix.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.cache.LedgerCache;
import com.ornoma.phoenix.factory.MasterCache;
import com.ornoma.phoenix.ui.adapters.LedgerAdapter;
import com.ornoma.phoenix.ui.adapters.LedgerTrAdapter;
import com.ornoma.phoenix.ui.dialogs.NewLedgerDialogue;

@SuppressWarnings("FieldCanBeLocal")
public class LedgerDetailActivity extends AppCompatActivity {
    private class CustomNewLedgerDialogue extends NewLedgerDialogue {
        public CustomNewLedgerDialogue(@NonNull Context context) {super(context);}
        @Override public void onSubmitNewLedger(Ledger ledger) {
            ledger.setParent(ledgerId);
            masterCache.addLedger(ledger);
            bindSubList();
        }
    }

    public static final String KEY_LEDGER_ID = "_key_ledger_id";
    private static final int REQUEST_LEDGER_LIST = 188;

    private CustomNewLedgerDialogue customNewLedgerDialogue;
    private ListView listView;
    private RecyclerView recyclerView;
    private Ledger ledger;
    private LedgerTrAdapter ledgerTrAdapter;
    private LedgerAdapter ledgerAdapter;
    private int[] rawIdArray, subListLedgerIdArray;
    private int ledgerId;
    private MasterCache masterCache;
    private LedgerCache ledgerCache;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_raw_tr_list);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ledgerId = getIntent().getIntExtra(KEY_LEDGER_ID, 1);

        bindActivity();
        bindList();
        bindSubList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_LEDGER_LIST){
            if(resultCode == RESULT_OK){
                int childLedgerId = data.getIntExtra(LedgerListActivity.KEY_LEDGER_ID, 0);
                bindSubList();
            }
        }
    }

    public void launchDetailActivity(Ledger ledger){
        Intent intentDetail = new Intent(this, LedgerDetailActivity.class);
        intentDetail.putExtra(LedgerDetailActivity.KEY_LEDGER_ID, ledger.getId());
        startActivity(intentDetail);
    }

    private void bindSubList(){
        subListLedgerIdArray = masterCache.getLedgerIds(ledgerId);
        ledgerAdapter = new LedgerAdapter(this, subListLedgerIdArray);
        recyclerView.setAdapter(ledgerAdapter);
        reloadColumns();
    }

    @SuppressWarnings("unused")
    private void reloadColumns(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Resources resources = getResources();
        int pubWidth = resources.getInteger(R.integer.global_ledger_width);
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pubWidth, resources.getDisplayMetrics());

        int maxColumnWidth = (int)pixels;
        int columnCount = width/maxColumnWidth;

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void bindList(){
        rawIdArray = masterCache.getRawTransIdArray(ledgerId);
        ledgerTrAdapter = new LedgerTrAdapter(this, rawIdArray);
        listView.setAdapter(ledgerTrAdapter);
        ledgerCache = LedgerCache.getInstance(this);
        ledger = ledgerCache.getLedger(ledgerId);
        setTitle(ledgerId + " " + ledger.getName());
    }

    private void bindActivity(){
        listView = (ListView)findViewById(R.id.listView);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        fab = (FloatingActionButton)findViewById(R.id.fab_add);
        masterCache = new MasterCache(this);

        TabHost tabs = (TabHost)findViewById(R.id.tabHost);
        tabs.setup();

        TabHost.TabSpec calculatorTab = tabs.newTabSpec("Sub");
        calculatorTab.setContent(R.id.linearLayoutSubList);
        calculatorTab.setIndicator("Sub");
        tabs.addTab(calculatorTab);

        TabHost.TabSpec homeTab = tabs.newTabSpec("Transactions");
        homeTab.setContent(R.id.linearLayoutTrList);
        homeTab.setIndicator("Transactions");
        tabs.addTab(homeTab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                customNewLedgerDialogue = new CustomNewLedgerDialogue(v.getContext());
                customNewLedgerDialogue.show();
            }});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
