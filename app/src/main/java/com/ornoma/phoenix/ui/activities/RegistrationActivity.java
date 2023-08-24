package com.ornoma.phoenix.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ornoma.phoenix.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

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


    /* public void onClick(View view) {
      switch (view.getId()){
         case R.id.regButton:
             Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
            break;
          case R.id.loginLink:
              switchOnLogin();
               }
    }*/
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
        if (userPassword.length() < 8) {
            password.requestFocus();
            password.setError("Please Enter correct password");

        }


//        Call<RegisterResponse> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .register(userName,userEmail,userPassword);
//
//        call.enqueue(new Callback<RegisterResponse>() {
//            @Override
//            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
//         RegisterResponse registerResponse=response.body();
//               if(response.isSuccessful()){
//                   Toast.makeText(RegistrationActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
//               }else {
//                   Toast.makeText(RegistrationActivity.this, registerResponse.getError(), Toast.LENGTH_SHORT).show();
//               }
//            }
//
//            @Override
//            public void onFailure(Call<RegisterResponse> call, Throwable t) {
//                Toast.makeText(RegistrationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    }
}