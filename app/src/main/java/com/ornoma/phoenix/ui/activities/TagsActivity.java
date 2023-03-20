package com.ornoma.phoenix.ui.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.core.TransactionTag;
import com.ornoma.phoenix.factory.MasterCache;
import com.ornoma.phoenix.ui.adapters.TagAdapter;
import com.ornoma.phoenix.ui.dialogs.NewTagDialogue;

public class TagsActivity extends AppCompatActivity {
    private class CustomNewTagDialogue extends NewTagDialogue {
        CustomNewTagDialogue(Context context) {super(context);}
        @Override public void onSubmitNewTag(TransactionTag tag) {
            masterCache.getTagFactory().createTag(tag);
            bindTagList();
        }
    }

    public static final String KEY_TAG_ID = "_key_tag_id";
    public static final String KEY_MODE = "_key_mode";
    public static final String MODE_SELECTION = "_mode_selection";

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private TextView textViewTitle;

    private TagAdapter tagAdapter;
    private MasterCache masterCache;
    public boolean isInSelectionMode = false;
    private CustomNewTagDialogue customNewTagDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        bindActivity();
        bindTagList();
    }

    private void bindActivity(){
        masterCache = new MasterCache(this);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        textViewTitle = (TextView)findViewById(R.id.textView_title);
        setTitle("");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {launchNewTagDialogue();}});

        if (getIntent().hasExtra(KEY_MODE))
            isInSelectionMode = getIntent().getStringExtra(KEY_MODE).equals(MODE_SELECTION);

        if (isInSelectionMode)
            textViewTitle.setText("Pick A Tag");
        else
            textViewTitle.setText("My Tags");
    }

    private void launchNewTagDialogue(){
        customNewTagDialogue = new CustomNewTagDialogue(this);
        customNewTagDialogue.show();
    }

    private void bindTagList(){
        int[] idArray;
        idArray = masterCache.getTagFactory().getAllIdArray();
        tagAdapter = new TagAdapter(this, idArray);
        recyclerView.setAdapter(tagAdapter);
        bindTagColumns();
    }

    @SuppressWarnings("unused")
    private void bindTagColumns(){
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

}
