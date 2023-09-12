package com.ornoma.phoenix.ui.activities;

import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;
import java.util.UUID;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.cache.TagCache;
import com.ornoma.phoenix.core.RawTransaction;
import com.ornoma.phoenix.core.Transaction;
import com.ornoma.phoenix.cache.LedgerCache;
import com.ornoma.phoenix.core.TransactionTag;
import com.ornoma.phoenix.factory.MasterCache;
import com.ornoma.phoenix.cache.RawTransactionCache;
import com.ornoma.phoenix.cache.TransactionCache;
import com.ornoma.phoenix.scripts.TagListRetriever;
import com.ornoma.phoenix.ui.adapters.RawTransactionAdapter;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

@SuppressWarnings("FieldCanBeLocal")
public class NewTransactionActivity extends AppCompatActivity {
    public static final String KEY_TRANSACTION_RAW_ID = "_key_transaction_raw_id";
    private static final String TAG = "NewTransActivity";

    public static final int REQUEST_LEDGER_LIST = 188;
    public static final int REQUEST_CALCULATOR = 189;
    private static final int REQUEST_TAG_LIST = 190;

    private MasterCache masterCache;
    private TransactionCache transactionCache;
    private RawTransactionCache rawTransactionCache;
    private TagCache tagCache;
    private Transaction transaction;
    private EditText editTextDes;
    private ImageButton imageButtonEdit;
    private ListView listViewRawTransactionList;
    private RawTransactionAdapter rawTransactionAdapter;
    private LedgerCache ledgerCache;
    private ImageButton imageButtonAddRawTransaction, imageButtonAddTags;
    private TagContainerLayout tagContainerLayout;
    private boolean isNewTransaction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindActivity();
        isNewTransaction = !isTransactionAvailable();
        if (isNewTransaction)
            createNewTransaction();
        bindTransaction();
    }

    private void updateEditActions(){
        rawTransactionAdapter.updateEditable(true);
        rawTransactionAdapter.notifyDataSetChanged();
        editTextDes.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode " + requestCode + " resultCode " + resultCode);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_LEDGER_LIST:
                    int ledgerId = data.getIntExtra(LedgerList.KEY_LEDGER_ID, 0);
                    Log.d(TAG, "onActivityResult() -> ledgerId " + ledgerId);
                    if (ledgerId != 0)
                        rawTransactionAdapter.onLedgerSelected(ledgerId);
                    break;
                case REQUEST_TAG_LIST:
                    String tagUid = data.getStringExtra(TagsActivity.KEY_TAG_ID);
                    addTag(tagUid);
                    break;
                case REQUEST_CALCULATOR:
                    String number = data.getStringExtra(CalculatorActivity.KEY_RESULT);
                    rawTransactionAdapter.onNumberSubmit(number);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void bindActivity(){
        masterCache = new MasterCache(this);
        ledgerCache = LedgerCache.getInstance(this);
        transactionCache = TransactionCache.getInstance(this);
        rawTransactionCache = RawTransactionCache.getInstance(this);
        tagCache = TagCache.getInstance(this);
        editTextDes = (EditText)findViewById(R.id.editText_des);
        listViewRawTransactionList = (ListView)findViewById(R.id.listView_rawTransactionList);
        imageButtonEdit = (ImageButton)findViewById(R.id.imageButton_edit);
        imageButtonAddRawTransaction = (ImageButton)findViewById(R.id.imageButton_add_rawTransaction);
        imageButtonAddTags = (ImageButton)findViewById(R.id.imageButton_add_tags);
        tagContainerLayout = (TagContainerLayout)findViewById(R.id.tagContainerLayout);

        int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        int colorWhite = ContextCompat.getColor(this, R.color.colorWhite);
        tagContainerLayout.setTagBackgroundColor(colorPrimary);
        tagContainerLayout.setTagTextColor(colorWhite);

        if (isNewTransaction)
            setTitle("New Transaction");
        else
            setTitle("Edit Transaction");

        imageButtonAddRawTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    transaction.addRawTransaction(getNew(transaction.getTransactionId()));
                    rawTransactionAdapter.notifyDataSetChanged();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        imageButtonAddTags.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {openTagPicker();}});
        colorTagContainer();
    }

    private void colorTagContainer(){
        int size = tagContainerLayout.getTags().size();
        int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        int colorWhite = ContextCompat.getColor(this, R.color.colorWhite);
        TagView tagView;
        for (int i = 0; i< size; i++){
            tagView = tagContainerLayout.getTagView(i);
            tagView.setBackgroundColor(colorPrimary);
            tagView.setTagTextColor(colorWhite);
            tagView.invalidate();
        }
    }

    private void openTagPicker(){
        Intent intentTagPicker = new Intent(this, TagsActivity.class);
        intentTagPicker.putExtra(TagsActivity.KEY_MODE, TagsActivity.MODE_SELECTION);
        startActivityForResult(intentTagPicker, REQUEST_TAG_LIST);
    }

    private void addTag(String tagId){
        TransactionTag tag = tagCache.getTag(tagId);
        masterCache.getTagFactory().addMap(transaction.getTransactionId(), tagId);
        tagContainerLayout.addTag(tag.getName());
    }

    private void bindTransaction(){
        editTextDes.setText(transaction.getDescription());
        editTextDes.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                transaction.setDescription(s.toString());}});
        rawTransactionAdapter = new RawTransactionAdapter(this, transaction){
            @Override public void onRemove(RawTransaction rawTransaction) {
                transaction.removeRawTransaction(rawTransaction);
                rawTransactionAdapter.notifyDataSetChanged();
            }};
        rawTransactionAdapter.updateEditable(isNewTransaction);
        listViewRawTransactionList.setAdapter(rawTransactionAdapter);
        editTextDes.setEnabled(isNewTransaction);
        imageButtonEdit.setVisibility(!isNewTransaction?View.VISIBLE:View.GONE);
        imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                isNewTransaction = true;
                imageButtonEdit.setVisibility(View.GONE);
                updateEditActions();}});
        updateEditActions();
        imageButtonEdit.setVisibility(View.GONE);
        TagListRetriever tagListRetriever;
        tagListRetriever = new TagListRetriever(this, transaction, TagListRetriever.TYPE_LIST_VALUE_STR) {
            @Override
            public void onListRetrieved(List<String> result) {
                tagContainerLayout.setTags(result);
            }
        };
    }

    @SuppressWarnings("EmptyCatchBlock")
    private boolean isTransactionAvailable(){
        try {
            int transactionId = getIntent().getIntExtra(KEY_TRANSACTION_RAW_ID, 0);
            if (transactionId != 0)
                transaction = transactionCache.getTransaction(transactionId);
            if (transaction != null)
                return true;
        }
        catch (Exception e){}
        return false;
    }

    private RawTransaction getNew(String transactionId){
        RawTransaction rawTransaction = new RawTransaction();
        rawTransaction.setTransactionId(transactionId);
        return rawTransaction;
    }

    private void createNewTransaction(){
        isNewTransaction = true;
        transaction = new Transaction();
        String uuid = UUID.randomUUID().toString();
        transaction.setTransactionId(uuid);

        RawTransaction rawTransactionFrom = getNew(transaction.getTransactionId());
        RawTransaction rawTransactionTo = getNew(transaction.getTransactionId());

        transaction.addRawTransaction(rawTransactionFrom);
        transaction.addRawTransaction(rawTransactionTo);

        transaction.setTimeInMillis(System.currentTimeMillis());
    }

    private void addTransactionToCache(){
        rawTransactionCache.clear();
        transactionCache.clear();
        ledgerCache.clear();
        masterCache.updateTransaction(transaction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_add:
                addTransactionToCache();
                finish();
                break;
        }
        return true;
    }
}
