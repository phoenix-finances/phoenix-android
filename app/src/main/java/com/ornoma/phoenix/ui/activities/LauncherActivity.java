package com.ornoma.phoenix.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.factory.MasterCache;
import com.ornoma.phoenix.ui.adapters.TransactionAdapter;

@SuppressWarnings("FieldCanBeLocal")
public class LauncherActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private TextView textViewTitle;
    private TextView textViewProfileName;

    private ImageButton imageButtonProfile;

    private MasterCache masterCache;
    private TransactionAdapter transactionAdapter;


    //250608383673-rif494e9q2jpp5pvc03er9iu7qeitgip.apps.googleusercontent.com
    //5B:13:19:44:6A:8F:CC:74:C2:C8:58:8D:A3:17:CD:10:53:6D:5F:D1
    //https://developers.google.com/drive/v3/web/quickstart/android
    //https://github.com/googledrive/android-quickstart/blob/master/app/src/main/java/com/google/android/gms/drive/sample/quickstart/MainActivity.java
    //https://github.com/google/google-api-java-client
    //http://stackoverflow.com/questions/22865630/get-account-name-email-from-google-drive-android-api
    //http://stackoverflow.com/questions/34305046/google-drive-sqlite-db-file-upload-from-android-app
    //TODO https://github.com/Andy671/Dachshund-Tab-Layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        toolbar = (Toolbar)findViewById(R.id.toolbar);

        bindActivity();
        bindAddAction();
        bindTransactionList();
        bindProfiles();
    }

    @Override
    protected void onResume() {
        bindTransactionList();
        super.onResume();
    }

    private void bindProfiles(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, null /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            GoogleSignInAccount account = result.getSignInAccount();
            textViewProfileName.setText(account.getDisplayName());
        } else {
            Log.d("Launcher", "Signing Out");
            try{Auth.GoogleSignInApi.signOut(mGoogleApiClient);}
            catch (Exception e){e.printStackTrace();}
            //finish();
        }
        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                openProfileActivity();}});
    }

    private void openProfileActivity(){
        Intent intentProfile = new Intent(this, ProfileActivity.class);
        startActivity(intentProfile);
    }

    private void bindTransactionList(){
        int[] transactionRawIdArray = masterCache.getTransactionRawIds();
        bindTransactionList(transactionRawIdArray);
    }

    private void bindTransactionList(int[] transactionRawIdArray){
        transactionAdapter = new TransactionAdapter(this, transactionRawIdArray);
        recyclerView.setAdapter(transactionAdapter);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(animator);

        reloadColumns();
    }

    private void bindActivity(){
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab_add);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        textViewTitle = (TextView)findViewById(R.id.textView_title);

        navigationView.setNavigationItemSelectedListener(this);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab_add);
        masterCache = new MasterCache(this);
        View headerLayout = navigationView.getHeaderView(0);;
        imageButtonProfile = (ImageButton)headerLayout.findViewById(R.id.imageButton_profile);
        textViewProfileName = (TextView)headerLayout.findViewById(R.id.textView_profile_name);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open, R.string.close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        String appName = getResources().getString(R.string.app_name);
        toolbar.setTitle("");
    }

    private void onDateChanged(int year, int month, int dayOfMonth){
        Calendar calendarFrom = new GregorianCalendar(year, month, dayOfMonth);
        Calendar calendarTo = new GregorianCalendar(year, month, dayOfMonth);

        calendarTo.add(Calendar.DATE, 1);
        int[] idArray = masterCache.getTransactionRawIds(calendarFrom, calendarTo);
        bindTransactionList(idArray);
    }

    private void reloadColumns(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Resources resources = getResources();
        int pubWidth = resources.getInteger(R.integer.global_publication_item_width);
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pubWidth, resources.getDisplayMetrics());

        int max_column_width = (int)pixels;
        int column_count = width/max_column_width;

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(column_count, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void bindAddAction(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                handleAddNewTransactionEvent();}});
    }

    private void handleAddNewTransactionEvent(){
        Intent intentNewTransaction = new Intent(this, NewTransactionActivity.class);
        startActivity(intentNewTransaction);
    }

    private void openLedgerList(){
        Intent intentLedgerList = new Intent(this, LedgerListActivity.class);
        startActivity(intentLedgerList);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_ledger:
                openLedgerList();
                break;
        }
        return true;
    }
}
