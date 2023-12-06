package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText email , password;
    private TextView progressText;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email    = findViewById(R.id.email);
        password = findViewById(R.id.password);
        TextView register = findViewById(R.id.register);
        Button login = findViewById(R.id.login);
        linearLayout = findViewById(R.id.linear_layout);
        progressBar  = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        auth = FirebaseAuth.getInstance();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this , Register.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtEmail    = email.getText().toString();
                String txtPassword = password.getText().toString();

                if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(Login.this, "All the credentials are required.", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length() < 8 ){
                    Toast.makeText(Login.this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(txtEmail , txtPassword);
                }

            }
        });

    }

    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        auth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);

                    Toast.makeText(Login.this, "Login successful.", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(Login.this , Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

    }
}