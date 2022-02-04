package com.dev.localvocaladmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dev.localvocaladmin.R;
import com.dev.localvocaladmin.RegisteredEmails;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //UI Views
    private EditText emailEt, passwordEt;
    private TextView forgetPasswordTv, loginTv;
    private RelativeLayout loginRl;
    private ProgressBar loginPb;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        setContentView(R.layout.activity_login);

        //init UI Views
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        forgetPasswordTv = findViewById(R.id.forgetPasswordTv);
        loginRl = findViewById(R.id.loginRl);
        loginTv = findViewById(R.id.loginTv);
        loginPb = findViewById(R.id.loginPb);

        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //forget password click handler
        forgetPasswordTv.setOnClickListener(v -> {
            //Start ForgetPasswordActivity
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        });

        //loginBtn Click Handler
        loginRl.setOnClickListener(v -> {
            //Login User
            loginUser();
        });

    }

    private void loginUser() {
        //get Data
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !RegisteredEmails.contains(email)) {
            Toast.makeText(this, "Please Enter a valid Email Address...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required to proceed further...", Toast.LENGTH_SHORT).show();
            return;
        }

        loginPb.setVisibility(View.VISIBLE);
        loginTv.setVisibility(View.INVISIBLE);

        //creating Account
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            loginPb.setVisibility(View.INVISIBLE);
                            loginTv.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }, 4000);
                    } else {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            loginPb.setVisibility(View.INVISIBLE);
                            loginTv.setVisibility(View.VISIBLE);
                        }, 4000);
                    }
                })
                .addOnFailureListener(e -> {
                    loginPb.setVisibility(View.INVISIBLE);
                    loginTv.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}