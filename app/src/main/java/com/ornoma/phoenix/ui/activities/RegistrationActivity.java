package com.ornoma.phoenix.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ornoma.phoenix.App;
import com.ornoma.phoenix.R;
import com.ornoma.phoenix.api.RetrofitClient;
import com.ornoma.phoenix.api.response.RegistrationRequest;
import com.ornoma.phoenix.api.response.RegistrationResponse;
import com.ornoma.phoenix.api.response.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RegistrationActivity.class.getSimpleName();
    EditText userName, userEmail, userPassword;
    Button registerButton;
    TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        userName = findViewById(R.id.etName);
        userEmail = findViewById(R.id.etEmail);
        userPassword = findViewById(R.id.etPassword);
        registerButton = findViewById(R.id.regButton);
        loginLink = findViewById(R.id.loginLink);

        loginLink.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.regButton) {
            registerUser();
        } else if (viewId == R.id.loginLink) {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void registerUser() {
        String name = userName.getText().toString();
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if (name.isEmpty()) {
            userName.requestFocus();
            userName.setError("Please Enter you name");
            return;
        }
        if (email.isEmpty()) {
            userEmail.requestFocus();
            userEmail.setError("Please Enter you email");
            return;
        }
        //Email matching
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.requestFocus();
            userEmail.setError("Please Enter you correct email.");
            return;
        }
        if (password.isEmpty()) {
            userPassword.requestFocus();
            userPassword.setError("Please enter your password");
            return;
        }
        if (password.length() < 4) {
            userPassword.requestFocus();
            userPassword.setError("Please Enter correct password");
            return;
        }
        RegistrationRequest request = new RegistrationRequest(name, email, password);
       Call<RegistrationResponse> call = RetrofitClient.getInstance()
               .getApi().register(request);
       call.enqueue(new Callback<RegistrationResponse>() {
           @Override
           public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
               if (response.isSuccessful()) {
                   RegistrationResponse registrationResponse = response.body();
                   if (registrationResponse != null) {
                       Log.d(TAG, "onResponse: " + registrationResponse.getId());
                       Log.d(TAG, "onResponse: " + registrationResponse.getName());
                       Log.d(TAG, "onResponse: " + registrationResponse.getEmail());
                       Log.d(TAG, "onResponse: " + registrationResponse.getPassword());
                   }
               }
           }
           @Override
           public void onFailure(Call<RegistrationResponse> call, Throwable t) {

           }
       });
    }
}
