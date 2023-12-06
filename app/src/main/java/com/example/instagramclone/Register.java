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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Register extends AppCompatActivity {

    private EditText username , name , email , password;
    private Button register;
    private TextView login , progressText;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private DatabaseReference RootRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        name     = findViewById(R.id.name);
        email    = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        login    = findViewById(R.id.login);
        linearLayout = findViewById(R.id.linear_layout);
        progressBar  = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        RootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this , Login.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtUsername = username.getText().toString();
                String txtName     = name.getText().toString();
                String txtEmail    = email.getText().toString();
                String txtPassword = password.getText().toString();

                if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtName) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(Register.this, "All the credentials are required.", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length() < 8 ){
                    Toast.makeText(Register.this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser( txtUsername , txtName , txtEmail , txtPassword);
                }

            }
        });

    }

    private void registerUser(String username, String name, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
        auth.createUserWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String , Object> map = new HashMap<>();
                map.put("name" , name);
                map.put("email" , email);
                map.put("username" , username);
                map.put("id" , auth.getCurrentUser().getUid());
                map.put("bio" , "");
                map.put("imageurl" , "default");

                RootRef.child("users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "Successful, go and update your profile please.", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(Register.this , Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}