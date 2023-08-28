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
    EditText name, email, password;
    Button registerButton;
    TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
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
        String userName = name.getText().toString();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userName.isEmpty()) {
            name.requestFocus();
            name.setError("Please Enter you name");
            return;
        }
        if (userEmail.isEmpty()) {
            email.requestFocus();
            email.setError("Please Enter you email");
            return;
        }
        //Email matching
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.requestFocus();
            email.setError("Please Enter you correct email.");
            return;
        }
        if (userPassword.isEmpty()) {
            password.requestFocus();
            password.setError("Please enter your password");
            return;
        }
        if (userPassword.length() < 4) {
            password.requestFocus();
            password.setError("Please Enter correct password");
            return;
        }
        RegistrationRequest request = new RegistrationRequest(userName, userEmail, userPassword);
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
