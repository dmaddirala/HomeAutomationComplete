package com.example.homeautomationcomplete;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button loginBtn;
    private EditText emailEt, passwordEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.btn_login);
        emailEt = findViewById(R.id.et_email);
        passwordEt = findViewById(R.id.et_password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                if(isValidCredential(email, password)){
                    signIn(email, password);
                }
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid email or password...", Toast.LENGTH_SHORT).show();
                            passwordEt.setText("");
                        }
                    }
                });

    }

    private boolean isValidCredential(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Email cannot be empty");
            emailEt.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Password cannot be empty");
            passwordEt.requestFocus();
            return false;
        }
        else {
            String emailDomain = email.split("@")[1];
            if(!emailDomain.equals("gmail.com")){
                emailEt.setError("Please enter valid Email Id");
                return false;
            }
            return true;
        }
    }
}