package com.ornoma.phoenix.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ornoma.phoenix.App;
import com.ornoma.phoenix.R;
import com.ornoma.phoenix.api.RetrofitClient;
import com.ornoma.phoenix.api.response.LoginRequest;
import com.ornoma.phoenix.api.response.LoginResponse;
import com.ornoma.phoenix.api.response.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    EditText email, password;
    Button logIn;
    TextView registerLink, errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        logIn = findViewById(R.id.logButton);
        registerLink = findViewById(R.id.regLink);
        errorText = findViewById(R.id.tvError);
        registerLink.setOnClickListener(this);
        logIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.logButton) {
            userLogin();
        } else if (viewId == R.id.regLink) {
            switchOnRegister();
        }
    }

    private void switchOnRegister() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void userLogin() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

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

        LoginRequest request = new LoginRequest(userEmail, userPassword);
        Call<LoginResponse> call = RetrofitClient.getInstance().getApi().login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if (response.isSuccessful()) {
                    Log.d(TAG, "jwtToken: " + loginResponse.getJwtToken());
                    Log.d(TAG, "username: " + loginResponse.getUsername());
                    App.saveToken(loginResponse.getJwtToken());
                    testMySelf();
                } else {
                    // TODO show invalid credentials
                    errorText.setText("! Invalid credentials ! Try Again");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void testMySelf() {
        String auth = String.format("Bearer %s", App.getToken());
        RetrofitClient.getInstance().getApi().getMyself(auth)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        Intent intent = new Intent(LoginActivity.this, LauncherActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {

                    }
                });
    }
}

