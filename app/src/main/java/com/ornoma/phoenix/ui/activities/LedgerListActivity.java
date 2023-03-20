package com.ornoma.phoenix.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.Ledger;
import com.ornoma.phoenix.cache.LedgerCache;
import com.ornoma.phoenix.factory.MasterCache;
import com.ornoma.phoenix.ui.adapters.LedgerAdapter;
import com.ornoma.phoenix.ui.dialogs.NewLedgerDialogue;

@SuppressWarnings("FieldCanBeLocal")
public class LedgerListActivity extends AppCompatActivity {
    private class CustomNewLedgerDialogue extends NewLedgerDialogue{
        CustomNewLedgerDialogue(@NonNull Context context) {super(context);}
        @Override public void onSubmitNewLedger(Ledger ledger) {
            ledger.setParent(parent);
            masterCache.addLedger(ledger);
            bindLedgerList();
        }
    }

    public static final String KEY_LEDGER_ID = "_key_ledger_id";
    public static final String KEY_MODE = "_key_mode";
    public static final String KEY_PARENT = "_key_generation";
    public static final String MODE_SELECTION = "_mode_selection";

    private static final String KEY_PARALLAX = "_key_parallax";
    private static final int REQUEST_CODE_LEDGER_LIST = 189;
    private static final String TAG = "LedgerListActivity";

    private MasterCache masterCache;
    private RecyclerView recyclerView;
    private LedgerAdapter ledgerAdapter;
    private FloatingActionButton fabAdd;
    private LinearLayout linearLayoutHeader;
    private Button buttonSelect;
    private TextView textViewTitle;
    private CustomNewLedgerDialogue customNewLedgerDialogue;
    public boolean isInSelectionMode = false;
    private int parent = 0;
    private int parallax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_list);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindActivity();
        bindLedgerList();
        bindActions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_LEDGER_LIST:
                    int selectedId = data.getIntExtra(KEY_LEDGER_ID, 0);
                    Log.d(TAG, "onActivityResult() -> ledgerId " + selectedId);
                    finishWithLedgerId(selectedId);
                    break;
            }
        }
    }

    public void openSubLedger(int ledgerId){
        Intent intentLedgerList = new Intent(this, LedgerListActivity.class);
        intentLedgerList.putExtra(LedgerListActivity.KEY_MODE, LedgerListActivity.MODE_SELECTION);
        intentLedgerList.putExtra(LedgerListActivity.KEY_PARALLAX, parallax+1);
        intentLedgerList.putExtra(LedgerListActivity.KEY_PARENT, ledgerId);
        startActivityForResult(intentLedgerList, REQUEST_CODE_LEDGER_LIST);
    }

    private void finishWithLedgerId(int selectedId){
        Intent intent = new Intent();
        intent.putExtra(KEY_LEDGER_ID, selectedId);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void launchDetailActivity(Ledger ledger){
        Intent intentDetail = new Intent(this, LedgerDetailActivity.class);
        intentDetail.putExtra(LedgerDetailActivity.KEY_LEDGER_ID, ledger.getId());
        startActivity(intentDetail);
    }

    private void bindActions(){
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                handleNewLedgerAddEvent();}});
    }

    private void handleNewLedgerAddEvent(){
        customNewLedgerDialogue = new CustomNewLedgerDialogue(this);
        customNewLedgerDialogue.show();
    }

    private void bindLedgerList(){
        new AccountCalculator().execute();
    }

    private void bindActivity(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        linearLayoutHeader = (LinearLayout)findViewById(R.id.linearLayout_header);
        textViewTitle = (TextView)findViewById(R.id.textView_title);
        buttonSelect = (Button)findViewById(R.id.button_select);
        masterCache = new MasterCache(this);

        setTitle("Ledger List");

        try {
            String mode = getIntent().getStringExtra(KEY_MODE);
            isInSelectionMode = mode.equals(MODE_SELECTION);
        }
        catch (Exception e){}

        if (getIntent().hasExtra(KEY_PARENT))
            parent = getIntent().getIntExtra(KEY_PARENT, Ledger.BaseGeneration);
        if (getIntent().hasExtra(KEY_PARALLAX))
            parallax = getIntent().getIntExtra(KEY_PARALLAX, 0);

        if (parallax == 0)
            linearLayoutHeader.setVisibility(View.GONE);
        else linearLayoutHeader.setVisibility(View.VISIBLE);

        if (parallax == 0)
            return;

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finishWithLedgerId(parent);}});

        new BackgroundWorker().execute();
    }

    private class BackgroundWorker extends AsyncTask<Void, Void, Void> {
        private Ledger ledger = null;
        @Override
        protected Void doInBackground(Void... params) {
            ledger = masterCache.getLedger(parent);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ledger != null)
                textViewTitle.setText(ledger.getName());
        }
    }

    private class AccountCalculator extends AsyncTask<Void, Void, Void>{
        private int[] ledgerIdArray;
        private LedgerCache ledgerCache;
        @Override
        protected Void doInBackground(Void... params) {
            Ledger ledger;
            ledgerCache = LedgerCache.getInstance(LedgerListActivity.this);
            ledgerIdArray = masterCache.getLedgerIds(parent);
            /*
            for (int i=0; i<ledgerIdArray.length; i++){
                ledger = ledgerCache.getLedger(ledgerIdArray[i]);
                if (ledger != null){
                    ledger.queryBalance(masterCache.getContext());
                    initChildLedger(ledger.getId());
                }
            }
            */
            return null;
        }

        private void initChildLedger(int targetLedgerId){
            int[] childIdArray = masterCache.getLedgerIds(targetLedgerId);
            Ledger ledger;
            for (int i=0; i<childIdArray.length; i++){
                ledger = ledgerCache.getLedger(childIdArray[i]);
                if (ledger != null)
                    ledger.queryBalance(masterCache.getContext());
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ledgerAdapter = new LedgerAdapter(LedgerListActivity.this, ledgerIdArray);
            recyclerView.setAdapter(ledgerAdapter);

            DefaultItemAnimator animator = new DefaultItemAnimator();
            recyclerView.setItemAnimator(animator);

            reloadColumns();
        }
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

    private void launchChartsActivity(){
        Intent intentCharts = new Intent(this, LedgerGraphActivity.class);
        startActivity(intentCharts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ledger_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_charts:
                launchChartsActivity();
                break;
        }
        return true;
    }
}
