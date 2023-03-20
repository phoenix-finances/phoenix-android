package com.ornoma.phoenix.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.TransactionTag;
import com.ornoma.phoenix.factory.MasterCache;

/**
 * Created by de76 on 5/5/17.
 */

@SuppressWarnings("FieldCanBeLocal")
public abstract class NewTagDialogue extends Dialog{
    private Context context;
    private MasterCache masterCache;

    private EditText editTextName;
    private Button buttonAdd;
    private Button buttonCancel;

    private String tagName;

    public abstract void onSubmitNewTag(TransactionTag tag);

    public NewTagDialogue(@NonNull Context context) {
        super(context);
        masterCache = new MasterCache(context);
    }

    @Override
    public void show(){
        bindDialogue();
        bindActions();
        super.show();
    }

    private void bindActions(){
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                submitTag();}});
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                NewTagDialogue.this.dismiss();}});
    }

    private void submitTag(){
        if (!(tagName.length() > 0))
            return;
        TransactionTag transactionTag = new TransactionTag(tagName);
        dismiss();
        onSubmitNewTag(transactionTag);
    }

    private void bindDialogue(){
        setContentView(R.layout.dialogue_new_tag);
        editTextName = (EditText)findViewById(R.id.editText_name);
        buttonAdd = (Button)findViewById(R.id.button_add);
        buttonCancel = (Button)findViewById(R.id.button_cancel);

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                tagName = s.toString();}});
    }

}
