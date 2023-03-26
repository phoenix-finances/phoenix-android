package com.ornoma.phoenix.ui.activities;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ornoma.phoenix.R;
import com.ornoma.phoenix.factory.MasterCache;

public class ProfileActivity extends AppCompatActivity
    implements GoogleApiClient.OnConnectionFailedListener{

    private TextView textViewOutput;
    private Button buttonBackup, buttonRestore;

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    private FirebaseStorage storage;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        mAuth = FirebaseAuth.getInstance();

        bindActivity();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void bindActivity(){
        buttonBackup = (Button)findViewById(R.id.button_backup);
        buttonRestore = (Button)findViewById(R.id.button_restore);

        buttonBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewOutput.setText("");
                backup();
            }
        });

        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                restore();
            }});

        textViewOutput = (TextView)findViewById(R.id.textView_output);
        textViewOutput.setPadding(16, 16, 16, 16);
        textViewOutput.setVerticalScrollBarEnabled(true);
        textViewOutput.setMovementMethod(new ScrollingMovementMethod());

        storage = FirebaseStorage.getInstance();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
    }

    private String userId;

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "Email : " + acct.getEmail());
            Log.d(TAG, "Id : " + acct.getId());
            Log.d(TAG, "IdToken : " + acct.getIdToken());
            final String idToken = acct.getIdToken();
            final String uid = acct.getId();
            userId = acct.getId();
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential);
            /*
            mAuth.signInAnonymously()
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d(TAG, "signInAnonymously:SUCCESS");
                            Log.d(TAG, "IsAnonymous: " + authResult.getUser().isAnonymous());
                            Log.d(TAG, "Display Name: " + authResult.getUser().getDisplayName());
                            Log.d(TAG, "Uid: " + authResult.getUser().getUid());
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "signInAnonymously:FAILURE", exception);
                        }
                    });
            */
        }
        else finish();
    }

    private void linkCredentials(AuthCredential credential){
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                        }
                    }
                });

    }

    private String getRef(){
        return MasterCache.DB_NAME;
    }

    //"gs://financia-4e567.appspot.com"
    private void backup(){
        StorageReference storageReference = storage.getReference().child(userId);
        Log.d(TAG,storage.getApp().getOptions().toString());
        StorageReference childStorageReference = storageReference.child(getRef());
        File dbFile = getDatabasePath(MasterCache.DB_NAME);

        try{
            InputStream stream = new FileInputStream(dbFile);

            UploadTask uploadTask = childStorageReference.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    msg("Backup Failed");
                    exception.printStackTrace();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    //@SuppressWarnings("VisibleForTests")
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //msg("Backup Successful");
                }
            });
        }
        catch (Exception e){e.printStackTrace();}
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void>{
        private String targetUrl;
        DownloadTask(String targetUrl){this.targetUrl = targetUrl;}
        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection connection = null;
            InputStream is = null;
            ByteArrayOutputStream out = null;

            try{
                connection = (HttpURLConnection) new URL(targetUrl).openConnection();
                connection.connect();
                updateDatabase(connection.getInputStream());
            }
            catch (Exception e){}

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            msg("Database restored!");
        }
    }

    private void restore(){
        StorageReference storageReference = storage.getReference().child(userId);
        StorageReference dbStorageReference = storageReference.child(getRef());

        dbStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                new DownloadTask(uri.toString()).execute();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    void msg(String msg){
        String text = textViewOutput.getText().toString();
        text += msg + "\n";
        try {textViewOutput.setText(text);}
        catch (Exception e){}
    }

    private void updateDatabase(InputStream in){
        File outFile = getDatabasePath(MasterCache.DB_NAME);
        if (outFile.exists()) Log.d("Database", "File Delete Status : " + outFile.delete());
        try {
            OutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];int length;
            while ((length = in.read(buffer)) != -1) {out.write(buffer, 0, length);}
            in.close();
            out.flush();
            out.close();
            setDatabaseVersion();
            Log.d("Database","Successfully Copied");
        }
        catch (IOException e) {
            e.printStackTrace();}
    }

    private void setDatabaseVersion() {
        SQLiteDatabase db = null;
        File DATABASE_FILE = getDatabasePath(MasterCache.DB_NAME);
        try {
            db = SQLiteDatabase.openDatabase(DATABASE_FILE.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("PRAGMA user_version = " + MasterCache.DB_VERSION);}
        catch (SQLiteException e ) {e.printStackTrace();}
        finally {if (db != null && db.isOpen()) {db.close();}}
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